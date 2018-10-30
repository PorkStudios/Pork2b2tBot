/*
 * Decompiled with CFR 0_132.
 */
package org.java_websocket.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.channels.NotYetConnectedException;
import java.security.SecureRandom;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import org.java_websocket.AbstractWebSocket;
import org.java_websocket.WebSocket;
import org.java_websocket.WebSocketImpl;
import org.java_websocket.WebSocketListener;
import org.java_websocket.drafts.Draft;
import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.exceptions.InvalidHandshakeException;
import org.java_websocket.framing.Framedata;
import org.java_websocket.handshake.ClientHandshakeBuilder;
import org.java_websocket.handshake.HandshakeImpl1Client;
import org.java_websocket.handshake.Handshakedata;
import org.java_websocket.handshake.ServerHandshake;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public abstract class WebSocketClient
extends AbstractWebSocket
implements Runnable,
WebSocket {
    protected URI uri = null;
    private WebSocketImpl engine = null;
    private Socket socket = null;
    private OutputStream ostream;
    private Proxy proxy = Proxy.NO_PROXY;
    private Thread writeThread;
    private Draft draft;
    private Map<String, String> headers;
    private CountDownLatch connectLatch = new CountDownLatch(1);
    private CountDownLatch closeLatch = new CountDownLatch(1);
    private int connectTimeout = 0;

    public WebSocketClient(URI serverUri) {
        this(serverUri, new Draft_6455());
    }

    public WebSocketClient(URI serverUri, Draft protocolDraft) {
        this(serverUri, protocolDraft, null, 0);
    }

    public WebSocketClient(URI serverUri, Draft protocolDraft, Map<String, String> httpHeaders, int connectTimeout) {
        if (serverUri == null) {
            throw new IllegalArgumentException();
        }
        if (protocolDraft == null) {
            throw new IllegalArgumentException("null as draft is permitted for `WebSocketServer` only!");
        }
        this.uri = serverUri;
        this.draft = protocolDraft;
        this.headers = httpHeaders;
        this.connectTimeout = connectTimeout;
        this.setTcpNoDelay(false);
        this.setReuseAddr(false);
        this.engine = new WebSocketImpl((WebSocketListener)this, protocolDraft);
    }

    public URI getURI() {
        return this.uri;
    }

    @Override
    public Draft getDraft() {
        return this.draft;
    }

    public Socket getSocket() {
        return this.socket;
    }

    public void connect() {
        if (this.writeThread != null) {
            throw new IllegalStateException("WebSocketClient objects are not reuseable");
        }
        this.writeThread = new Thread(this);
        this.writeThread.start();
    }

    public boolean connectBlocking() throws InterruptedException {
        this.connect();
        this.connectLatch.await();
        return this.engine.isOpen();
    }

    @Override
    public void close() {
        if (this.writeThread != null) {
            this.engine.close(1000);
        }
    }

    public void closeBlocking() throws InterruptedException {
        this.close();
        this.closeLatch.await();
    }

    @Override
    public void send(String text) throws NotYetConnectedException {
        this.engine.send(text);
    }

    @Override
    public void send(byte[] data) throws NotYetConnectedException {
        this.engine.send(data);
    }

    @Override
    public <T> T getAttachment() {
        return this.engine.getAttachment();
    }

    @Override
    public <T> void setAttachment(T attachment) {
        this.engine.setAttachment(attachment);
    }

    @Override
    protected Collection<WebSocket> connections() {
        return Collections.singletonList(this.engine);
    }

    @Override
    public void sendPing() throws NotYetConnectedException {
        this.engine.sendPing();
    }

    @Override
    public void run() {
        InputStream istream;
        try {
            boolean isNewSocket = false;
            if (this.socket == null) {
                this.socket = new Socket(this.proxy);
                isNewSocket = true;
            } else if (this.socket.isClosed()) {
                throw new IOException();
            }
            this.socket.setTcpNoDelay(this.isTcpNoDelay());
            this.socket.setReuseAddress(this.isReuseAddr());
            if (!this.socket.isBound()) {
                this.socket.connect(new InetSocketAddress(this.uri.getHost(), this.getPort()), this.connectTimeout);
            }
            if (isNewSocket && "wss".equals(this.uri.getScheme())) {
                SSLContext sslContext = SSLContext.getInstance("TLS");
                sslContext.init(null, null, null);
                SSLSocketFactory factory = sslContext.getSocketFactory();
                this.socket = factory.createSocket(this.socket, this.uri.getHost(), this.getPort(), true);
            }
            istream = this.socket.getInputStream();
            this.ostream = this.socket.getOutputStream();
            this.sendHandshake();
        }
        catch (Exception e) {
            this.onWebsocketError(this.engine, e);
            this.engine.closeConnection(-1, e.getMessage());
            return;
        }
        this.writeThread = new Thread(new WebsocketWriteThread());
        this.writeThread.start();
        byte[] rawbuffer = new byte[WebSocketImpl.RCVBUF];
        try {
            int readBytes;
            while (!this.isClosing() && !this.isClosed() && (readBytes = istream.read(rawbuffer)) != -1) {
                this.engine.decode(ByteBuffer.wrap(rawbuffer, 0, readBytes));
            }
            this.engine.eot();
        }
        catch (IOException e) {
            this.handleIOException(e);
        }
        catch (RuntimeException e) {
            this.onError(e);
            this.engine.closeConnection(1006, e.getMessage());
        }
    }

    private int getPort() {
        int port = this.uri.getPort();
        if (port == -1) {
            String scheme = this.uri.getScheme();
            if ("wss".equals(scheme)) {
                return 443;
            }
            if ("ws".equals(scheme)) {
                return 80;
            }
            throw new IllegalArgumentException("unknown scheme: " + scheme);
        }
        return port;
    }

    private void sendHandshake() throws InvalidHandshakeException {
        int port;
        String part1 = this.uri.getRawPath();
        String part2 = this.uri.getRawQuery();
        String path = part1 == null || part1.length() == 0 ? "/" : part1;
        if (part2 != null) {
            path = path + '?' + part2;
        }
        String host = this.uri.getHost() + ((port = this.getPort()) != 80 ? new StringBuilder().append(":").append(port).toString() : "");
        HandshakeImpl1Client handshake = new HandshakeImpl1Client();
        handshake.setResourceDescriptor(path);
        handshake.put("Host", host);
        if (this.headers != null) {
            for (Map.Entry<String, String> kv : this.headers.entrySet()) {
                handshake.put(kv.getKey(), kv.getValue());
            }
        }
        this.engine.startHandshake(handshake);
    }

    @Override
    public WebSocket.READYSTATE getReadyState() {
        return this.engine.getReadyState();
    }

    @Override
    public final void onWebsocketMessage(WebSocket conn, String message) {
        this.onMessage(message);
    }

    @Override
    public final void onWebsocketMessage(WebSocket conn, ByteBuffer blob) {
        this.onMessage(blob);
    }

    @Override
    public void onWebsocketMessageFragment(WebSocket conn, Framedata frame) {
        this.onFragment(frame);
    }

    @Override
    public final void onWebsocketOpen(WebSocket conn, Handshakedata handshake) {
        this.startConnectionLostTimer();
        this.onOpen((ServerHandshake)handshake);
        this.connectLatch.countDown();
    }

    @Override
    public final void onWebsocketClose(WebSocket conn, int code, String reason, boolean remote) {
        this.stopConnectionLostTimer();
        if (this.writeThread != null) {
            this.writeThread.interrupt();
        }
        this.onClose(code, reason, remote);
        this.connectLatch.countDown();
        this.closeLatch.countDown();
    }

    @Override
    public final void onWebsocketError(WebSocket conn, Exception ex) {
        this.onError(ex);
    }

    @Override
    public final void onWriteDemand(WebSocket conn) {
    }

    @Override
    public void onWebsocketCloseInitiated(WebSocket conn, int code, String reason) {
        this.onCloseInitiated(code, reason);
    }

    @Override
    public void onWebsocketClosing(WebSocket conn, int code, String reason, boolean remote) {
        this.onClosing(code, reason, remote);
    }

    public void onCloseInitiated(int code, String reason) {
    }

    public void onClosing(int code, String reason, boolean remote) {
    }

    public WebSocket getConnection() {
        return this.engine;
    }

    @Override
    public InetSocketAddress getLocalSocketAddress(WebSocket conn) {
        if (this.socket != null) {
            return (InetSocketAddress)this.socket.getLocalSocketAddress();
        }
        return null;
    }

    @Override
    public InetSocketAddress getRemoteSocketAddress(WebSocket conn) {
        if (this.socket != null) {
            return (InetSocketAddress)this.socket.getRemoteSocketAddress();
        }
        return null;
    }

    public abstract void onOpen(ServerHandshake var1);

    public abstract void onMessage(String var1);

    public abstract void onClose(int var1, String var2, boolean var3);

    public abstract void onError(Exception var1);

    public void onMessage(ByteBuffer bytes) {
    }

    @Deprecated
    public void onFragment(Framedata frame) {
    }

    private void closeSocket() {
        try {
            if (this.socket != null) {
                this.socket.close();
            }
        }
        catch (IOException ex) {
            this.onWebsocketError(this, ex);
        }
    }

    public void setProxy(Proxy proxy) {
        if (proxy == null) {
            throw new IllegalArgumentException();
        }
        this.proxy = proxy;
    }

    public void setSocket(Socket socket) {
        if (this.socket != null) {
            throw new IllegalStateException("socket has already been set");
        }
        this.socket = socket;
    }

    @Override
    public void sendFragmentedFrame(Framedata.Opcode op, ByteBuffer buffer, boolean fin) {
        this.engine.sendFragmentedFrame(op, buffer, fin);
    }

    @Override
    public boolean isOpen() {
        return this.engine.isOpen();
    }

    @Override
    public boolean isFlushAndClose() {
        return this.engine.isFlushAndClose();
    }

    @Override
    public boolean isClosed() {
        return this.engine.isClosed();
    }

    @Override
    public boolean isClosing() {
        return this.engine.isClosing();
    }

    @Override
    public boolean isConnecting() {
        return this.engine.isConnecting();
    }

    @Override
    public boolean hasBufferedData() {
        return this.engine.hasBufferedData();
    }

    @Override
    public void close(int code) {
        this.engine.close();
    }

    @Override
    public void close(int code, String message) {
        this.engine.close(code, message);
    }

    @Override
    public void closeConnection(int code, String message) {
        this.engine.closeConnection(code, message);
    }

    @Override
    public void send(ByteBuffer bytes) throws IllegalArgumentException, NotYetConnectedException {
        this.engine.send(bytes);
    }

    @Override
    public void sendFrame(Framedata framedata) {
        this.engine.sendFrame(framedata);
    }

    @Override
    public void sendFrame(Collection<Framedata> frames) {
        this.engine.sendFrame(frames);
    }

    @Override
    public InetSocketAddress getLocalSocketAddress() {
        return this.engine.getLocalSocketAddress();
    }

    @Override
    public InetSocketAddress getRemoteSocketAddress() {
        return this.engine.getRemoteSocketAddress();
    }

    @Override
    public String getResourceDescriptor() {
        return this.uri.getPath();
    }

    private void handleIOException(IOException e) {
        if (e instanceof SSLException) {
            this.onError(e);
        }
        this.engine.eot();
    }

    static /* synthetic */ WebSocketImpl access$100(WebSocketClient x0) {
        return x0.engine;
    }

    private class WebsocketWriteThread
    implements Runnable {
        private WebsocketWriteThread() {
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         * Enabled aggressive block sorting
         * Enabled unnecessary exception pruning
         * Enabled aggressive exception aggregation
         */
        public void run() {
            Thread.currentThread().setName("WebsocketWriteThread");
            try {
                try {
                    try {
                        while (!Thread.interrupted()) {
                            ByteBuffer buffer = WebSocketClient.access$100((WebSocketClient)WebSocketClient.this).outQueue.take();
                            WebSocketClient.this.ostream.write(buffer.array(), 0, buffer.limit());
                            WebSocketClient.this.ostream.flush();
                        }
                    }
                    catch (InterruptedException e) {
                        for (ByteBuffer buffer : WebSocketClient.access$100((WebSocketClient)WebSocketClient.this).outQueue) {
                            WebSocketClient.this.ostream.write(buffer.array(), 0, buffer.limit());
                            WebSocketClient.this.ostream.flush();
                        }
                    }
                    Object var5_6 = null;
                }
                catch (IOException e) {
                    WebSocketClient.this.handleIOException(e);
                    Object var5_7 = null;
                    WebSocketClient.this.closeSocket();
                    return;
                }
                WebSocketClient.this.closeSocket();
                return;
            }
            catch (Throwable throwable) {
                Object var5_8 = null;
                WebSocketClient.this.closeSocket();
                throw throwable;
            }
        }
    }

}

