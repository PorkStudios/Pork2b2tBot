/*
 * Decompiled with CFR 0_132.
 */
package org.java_websocket.server;

import java.io.IOException;
import java.io.PrintStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;
import java.nio.channels.CancelledKeyException;
import java.nio.channels.ClosedByInterruptException;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import org.java_websocket.AbstractWebSocket;
import org.java_websocket.SocketChannelIOHelper;
import org.java_websocket.WebSocket;
import org.java_websocket.WebSocketAdapter;
import org.java_websocket.WebSocketFactory;
import org.java_websocket.WebSocketImpl;
import org.java_websocket.WebSocketServerFactory;
import org.java_websocket.WrappedByteChannel;
import org.java_websocket.drafts.Draft;
import org.java_websocket.exceptions.InvalidDataException;
import org.java_websocket.exceptions.WebsocketNotConnectedException;
import org.java_websocket.framing.Framedata;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.handshake.Handshakedata;
import org.java_websocket.handshake.ServerHandshakeBuilder;
import org.java_websocket.server.DefaultWebSocketServerFactory;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public abstract class WebSocketServer
extends AbstractWebSocket
implements Runnable {
    public static int DECODERS = Runtime.getRuntime().availableProcessors();
    private final Collection<WebSocket> connections;
    private final InetSocketAddress address;
    private ServerSocketChannel server;
    private Selector selector;
    private List<Draft> drafts;
    private Thread selectorthread;
    private final AtomicBoolean isclosed = new AtomicBoolean(false);
    protected List<WebSocketWorker> decoders;
    private List<WebSocketImpl> iqueue;
    private BlockingQueue<ByteBuffer> buffers;
    private int queueinvokes = 0;
    private final AtomicInteger queuesize = new AtomicInteger(0);
    private WebSocketServerFactory wsf = new DefaultWebSocketServerFactory();

    public WebSocketServer() {
        this(new InetSocketAddress(80), DECODERS, null);
    }

    public WebSocketServer(InetSocketAddress address) {
        this(address, DECODERS, null);
    }

    public WebSocketServer(InetSocketAddress address, int decodercount) {
        this(address, decodercount, null);
    }

    public WebSocketServer(InetSocketAddress address, List<Draft> drafts) {
        this(address, DECODERS, drafts);
    }

    public WebSocketServer(InetSocketAddress address, int decodercount, List<Draft> drafts) {
        this(address, decodercount, drafts, new HashSet<WebSocket>());
    }

    public WebSocketServer(InetSocketAddress address, int decodercount, List<Draft> drafts, Collection<WebSocket> connectionscontainer) {
        if (address == null || decodercount < 1 || connectionscontainer == null) {
            throw new IllegalArgumentException("address and connectionscontainer must not be null and you need at least 1 decoder");
        }
        this.drafts = drafts == null ? Collections.emptyList() : drafts;
        this.address = address;
        this.connections = connectionscontainer;
        this.setTcpNoDelay(false);
        this.setReuseAddr(false);
        this.iqueue = new LinkedList<WebSocketImpl>();
        this.decoders = new ArrayList<WebSocketWorker>(decodercount);
        this.buffers = new LinkedBlockingQueue<ByteBuffer>();
        for (int i = 0; i < decodercount; ++i) {
            WebSocketWorker ex = new WebSocketWorker();
            this.decoders.add(ex);
            ex.start();
        }
    }

    public void start() {
        if (this.selectorthread != null) {
            throw new IllegalStateException(this.getClass().getName() + " can only be started once.");
        }
        new Thread(this).start();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void stop(int timeout) throws InterruptedException {
        ArrayList<WebSocket> socketsToClose;
        if (!this.isclosed.compareAndSet(false, true)) {
            return;
        }
        Object object = this.connections;
        synchronized (object) {
            socketsToClose = new ArrayList<WebSocket>(this.connections);
        }
        for (WebSocket ws : socketsToClose) {
            ws.close(1001);
        }
        this.wsf.close();
        object = this;
        synchronized (object) {
            if (this.selectorthread != null) {
                this.selector.wakeup();
                this.selectorthread.join(timeout);
            }
        }
    }

    public void stop() throws IOException, InterruptedException {
        this.stop(0);
    }

    @Override
    public Collection<WebSocket> connections() {
        return this.connections;
    }

    public InetSocketAddress getAddress() {
        return this.address;
    }

    public int getPort() {
        int port = this.getAddress().getPort();
        if (port == 0 && this.server != null) {
            port = this.server.socket().getLocalPort();
        }
        return port;
    }

    public List<Draft> getDraft() {
        return Collections.unmodifiableList(this.drafts);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Loose catch block
     * Enabled aggressive exception aggregation
     */
    @Override
    public void run() {
        block81 : {
            WebSocketServer webSocketServer = this;
            synchronized (webSocketServer) {
                if (this.selectorthread != null) {
                    throw new IllegalStateException(this.getClass().getName() + " can only be started once.");
                }
                this.selectorthread = Thread.currentThread();
                if (this.isclosed.get()) {
                    return;
                }
            }
            this.selectorthread.setName("WebsocketSelector" + this.selectorthread.getId());
            try {
                this.server = ServerSocketChannel.open();
                this.server.configureBlocking(false);
                ServerSocket socket = this.server.socket();
                socket.setReceiveBufferSize(WebSocketImpl.RCVBUF);
                socket.setReuseAddress(this.isReuseAddr());
                socket.bind(this.address);
                this.selector = Selector.open();
                this.server.register(this.selector, this.server.validOps());
                this.startConnectionLostTimer();
                this.onStart();
            }
            catch (IOException ex) {
                this.handleFatal(null, ex);
                if (this.decoders != null) {
                    for (WebSocketWorker w : this.decoders) {
                        w.interrupt();
                    }
                }
                return;
            }
            try {
                int iShutdownCount = 5;
                int selectTimeout = 0;
                while (!this.selectorthread.isInterrupted() && iShutdownCount != 0) {
                    SelectionKey key = null;
                    WebSocketImpl conn = null;
                    try {
                        int keyCount;
                        if (this.isclosed.get()) {
                            selectTimeout = 5;
                        }
                        if ((keyCount = this.selector.select(selectTimeout)) == 0 && this.isclosed.get()) {
                            --iShutdownCount;
                        }
                        Set<SelectionKey> keys = this.selector.selectedKeys();
                        Iterator<SelectionKey> i = keys.iterator();
                        while (i.hasNext()) {
                            key = i.next();
                            conn = null;
                            if (!key.isValid()) continue;
                            if (key.isAcceptable()) {
                                if (!this.onConnect(key)) {
                                    key.cancel();
                                    continue;
                                }
                                SocketChannel channel = this.server.accept();
                                if (channel == null) continue;
                                channel.configureBlocking(false);
                                Socket socket = channel.socket();
                                socket.setTcpNoDelay(this.isTcpNoDelay());
                                socket.setKeepAlive(true);
                                WebSocketImpl w = this.wsf.createWebSocket((WebSocketAdapter)this, this.drafts);
                                w.key = channel.register(this.selector, 1, w);
                                try {
                                    w.channel = this.wsf.wrapChannel(channel, w.key);
                                    i.remove();
                                    this.allocateBuffers(w);
                                }
                                catch (IOException ex) {
                                    if (w.key != null) {
                                        w.key.cancel();
                                    }
                                    this.handleIOException(w.key, null, ex);
                                }
                                continue;
                            }
                            if (key.isReadable()) {
                                conn = (WebSocketImpl)key.attachment();
                                ByteBuffer buf = this.takeBuffer();
                                if (conn.channel == null) {
                                    if (key != null) {
                                        key.cancel();
                                    }
                                    this.handleIOException(key, conn, new IOException());
                                    continue;
                                }
                                try {
                                    if (SocketChannelIOHelper.read(buf, conn, conn.channel)) {
                                        if (buf.hasRemaining()) {
                                            conn.inQueue.put(buf);
                                            this.queue(conn);
                                            i.remove();
                                            if (conn.channel instanceof WrappedByteChannel && ((WrappedByteChannel)conn.channel).isNeedRead()) {
                                                this.iqueue.add(conn);
                                            }
                                        } else {
                                            this.pushBuffer(buf);
                                        }
                                    } else {
                                        this.pushBuffer(buf);
                                    }
                                }
                                catch (IOException e2) {
                                    this.pushBuffer(buf);
                                    throw e2;
                                }
                            }
                            if (!key.isWritable() || !SocketChannelIOHelper.batch(conn = (WebSocketImpl)key.attachment(), conn.channel) || !key.isValid()) continue;
                            key.interestOps(1);
                        }
                        while (!this.iqueue.isEmpty()) {
                            conn = this.iqueue.remove(0);
                            WrappedByteChannel c = (WrappedByteChannel)conn.channel;
                            ByteBuffer buf = this.takeBuffer();
                            try {
                                if (SocketChannelIOHelper.readMore(buf, conn, c)) {
                                    this.iqueue.add(conn);
                                }
                                if (buf.hasRemaining()) {
                                    conn.inQueue.put(buf);
                                    this.queue(conn);
                                    continue;
                                }
                                this.pushBuffer(buf);
                            }
                            catch (IOException e3) {
                                this.pushBuffer(buf);
                                throw e3;
                            }
                        }
                    }
                    catch (CancelledKeyException keyCount) {
                    }
                    catch (ClosedByInterruptException e4) {
                        Object var13_24 = null;
                        this.stopConnectionLostTimer();
                        if (this.decoders != null) {
                            for (WebSocketWorker w : this.decoders) {
                                w.interrupt();
                            }
                        }
                        if (this.selector != null) {
                            try {
                                this.selector.close();
                            }
                            catch (IOException e52222) {
                                this.onError(null, e52222);
                            }
                        }
                        if (this.server != null) {
                            try {
                                this.server.close();
                            }
                            catch (IOException e) {
                                this.onError(null, e);
                            }
                        }
                        return;
                    }
                    catch (IOException ex) {
                        if (key != null) {
                            key.cancel();
                        }
                        this.handleIOException(key, conn, ex);
                    }
                    catch (InterruptedException e6) {
                        Object var13_25 = null;
                        this.stopConnectionLostTimer();
                        if (this.decoders != null) {
                            for (WebSocketWorker w : this.decoders) {
                                w.interrupt();
                            }
                        }
                        if (this.selector != null) {
                            try {
                                this.selector.close();
                            }
                            catch (IOException e52222) {
                                this.onError(null, e52222);
                            }
                        }
                        if (this.server != null) {
                            try {
                                this.server.close();
                            }
                            catch (IOException e) {
                                this.onError(null, e);
                            }
                        }
                        return;
                    }
                }
                Object var13_26 = null;
            }
            catch (Throwable throwable) {
                Object var13_28 = null;
                this.stopConnectionLostTimer();
                if (this.decoders != null) {
                    for (WebSocketWorker w : this.decoders) {
                        w.interrupt();
                    }
                }
                if (this.selector != null) {
                    try {
                        this.selector.close();
                    }
                    catch (IOException e52222) {
                        this.onError(null, e52222);
                    }
                }
                if (this.server != null) {
                    try {
                        this.server.close();
                    }
                    catch (IOException e) {
                        this.onError(null, e);
                    }
                }
                throw throwable;
            }
            this.stopConnectionLostTimer();
            if (this.decoders != null) {
                for (WebSocketWorker w : this.decoders) {
                    w.interrupt();
                }
            }
            if (this.selector != null) {
                try {
                    this.selector.close();
                }
                catch (IOException e52222) {
                    this.onError(null, e52222);
                }
            }
            if (this.server != null) {
                try {
                    this.server.close();
                }
                catch (IOException e) {
                    this.onError(null, e);
                }
            }
            break block81;
            {
                catch (RuntimeException e7) {
                    this.handleFatal(null, e7);
                    Object var13_27 = null;
                    this.stopConnectionLostTimer();
                    if (this.decoders != null) {
                        for (WebSocketWorker w : this.decoders) {
                            w.interrupt();
                        }
                    }
                    if (this.selector != null) {
                        try {
                            this.selector.close();
                        }
                        catch (IOException e52222) {
                            this.onError(null, e52222);
                        }
                    }
                    if (this.server != null) {
                        try {
                            this.server.close();
                        }
                        catch (IOException e) {
                            this.onError(null, e);
                        }
                    }
                }
            }
        }
    }

    protected void allocateBuffers(WebSocket c) throws InterruptedException {
        if (this.queuesize.get() >= 2 * this.decoders.size() + 1) {
            return;
        }
        this.queuesize.incrementAndGet();
        this.buffers.put(this.createBuffer());
    }

    protected void releaseBuffers(WebSocket c) throws InterruptedException {
    }

    public ByteBuffer createBuffer() {
        return ByteBuffer.allocate(WebSocketImpl.RCVBUF);
    }

    protected void queue(WebSocketImpl ws) throws InterruptedException {
        if (ws.workerThread == null) {
            ws.workerThread = this.decoders.get(this.queueinvokes % this.decoders.size());
            ++this.queueinvokes;
        }
        ws.workerThread.put(ws);
    }

    private ByteBuffer takeBuffer() throws InterruptedException {
        return this.buffers.take();
    }

    private void pushBuffer(ByteBuffer buf) throws InterruptedException {
        if (this.buffers.size() > this.queuesize.intValue()) {
            return;
        }
        this.buffers.put(buf);
    }

    private void handleIOException(SelectionKey key, WebSocket conn, IOException ex) {
        SelectableChannel channel;
        if (conn != null) {
            conn.closeConnection(1006, ex.getMessage());
        } else if (key != null && (channel = key.channel()) != null && channel.isOpen()) {
            try {
                channel.close();
            }
            catch (IOException iOException) {
                // empty catch block
            }
            if (WebSocketImpl.DEBUG) {
                System.out.println("Connection closed because of " + ex);
            }
        }
    }

    private void handleFatal(WebSocket conn, Exception e) {
        this.onError(conn, e);
        try {
            this.stop();
        }
        catch (IOException e1) {
            this.onError(null, e1);
        }
        catch (InterruptedException e1) {
            Thread.currentThread().interrupt();
            this.onError(null, e1);
        }
    }

    @Override
    public final void onWebsocketMessage(WebSocket conn, String message) {
        this.onMessage(conn, message);
    }

    @Deprecated
    @Override
    public void onWebsocketMessageFragment(WebSocket conn, Framedata frame) {
        this.onFragment(conn, frame);
    }

    @Override
    public final void onWebsocketMessage(WebSocket conn, ByteBuffer blob) {
        this.onMessage(conn, blob);
    }

    @Override
    public final void onWebsocketOpen(WebSocket conn, Handshakedata handshake) {
        if (this.addConnection(conn)) {
            this.onOpen(conn, (ClientHandshake)handshake);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public final void onWebsocketClose(WebSocket conn, int code, String reason, boolean remote) {
        this.selector.wakeup();
        try {
            if (this.removeConnection(conn)) {
                this.onClose(conn, code, reason, remote);
            }
            Object var6_5 = null;
        }
        catch (Throwable throwable) {
            Object var6_6 = null;
            try {
                this.releaseBuffers(conn);
            }
            catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            throw throwable;
        }
        try {
            this.releaseBuffers(conn);
        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected boolean removeConnection(WebSocket ws) {
        boolean removed = false;
        Collection<WebSocket> collection = this.connections;
        synchronized (collection) {
            if (this.connections.contains(ws)) {
                removed = this.connections.remove(ws);
            } else if (WebSocketImpl.DEBUG) {
                System.out.println("Removing connection which is not in the connections collection! Possible no handshake recieved! " + ws);
            }
        }
        if (this.isclosed.get() && this.connections.size() == 0) {
            this.selectorthread.interrupt();
        }
        return removed;
    }

    @Override
    public ServerHandshakeBuilder onWebsocketHandshakeReceivedAsServer(WebSocket conn, Draft draft, ClientHandshake request) throws InvalidDataException {
        return super.onWebsocketHandshakeReceivedAsServer(conn, draft, request);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected boolean addConnection(WebSocket ws) {
        if (!this.isclosed.get()) {
            Collection<WebSocket> collection = this.connections;
            synchronized (collection) {
                boolean succ = this.connections.add(ws);
                assert (succ);
                return succ;
            }
        }
        ws.close(1001);
        return true;
    }

    @Override
    public final void onWebsocketError(WebSocket conn, Exception ex) {
        this.onError(conn, ex);
    }

    @Override
    public final void onWriteDemand(WebSocket w) {
        WebSocketImpl conn = (WebSocketImpl)w;
        try {
            conn.key.interestOps(5);
        }
        catch (CancelledKeyException e) {
            conn.outQueue.clear();
        }
        this.selector.wakeup();
    }

    @Override
    public void onWebsocketCloseInitiated(WebSocket conn, int code, String reason) {
        this.onCloseInitiated(conn, code, reason);
    }

    @Override
    public void onWebsocketClosing(WebSocket conn, int code, String reason, boolean remote) {
        this.onClosing(conn, code, reason, remote);
    }

    public void onCloseInitiated(WebSocket conn, int code, String reason) {
    }

    public void onClosing(WebSocket conn, int code, String reason, boolean remote) {
    }

    public final void setWebSocketFactory(WebSocketServerFactory wsf) {
        this.wsf = wsf;
    }

    public final WebSocketFactory getWebSocketFactory() {
        return this.wsf;
    }

    protected boolean onConnect(SelectionKey key) {
        return true;
    }

    private Socket getSocket(WebSocket conn) {
        WebSocketImpl impl = (WebSocketImpl)conn;
        return ((SocketChannel)impl.key.channel()).socket();
    }

    @Override
    public InetSocketAddress getLocalSocketAddress(WebSocket conn) {
        return (InetSocketAddress)this.getSocket(conn).getLocalSocketAddress();
    }

    @Override
    public InetSocketAddress getRemoteSocketAddress(WebSocket conn) {
        return (InetSocketAddress)this.getSocket(conn).getRemoteSocketAddress();
    }

    public abstract void onOpen(WebSocket var1, ClientHandshake var2);

    public abstract void onClose(WebSocket var1, int var2, String var3, boolean var4);

    public abstract void onMessage(WebSocket var1, String var2);

    public abstract void onError(WebSocket var1, Exception var2);

    public abstract void onStart();

    public void onMessage(WebSocket conn, ByteBuffer message) {
    }

    @Deprecated
    public void onFragment(WebSocket conn, Framedata fragment) {
    }

    public void broadcast(String text) {
        this.broadcast(text, this.connections);
    }

    public void broadcast(byte[] data) {
        this.broadcast(data, this.connections);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void broadcast(byte[] data, Collection<WebSocket> clients) {
        if (data == null || clients == null) {
            throw new IllegalArgumentException();
        }
        HashMap<Draft, List<Framedata>> draftFrames = new HashMap<Draft, List<Framedata>>();
        ByteBuffer byteBufferData = ByteBuffer.wrap(data);
        Collection<WebSocket> collection = clients;
        synchronized (collection) {
            for (WebSocket client : clients) {
                if (client == null) continue;
                Draft draft = client.getDraft();
                if (!draftFrames.containsKey(draft)) {
                    List<Framedata> frames = draft.createFrames(byteBufferData, false);
                    draftFrames.put(draft, frames);
                }
                try {
                    client.sendFrame((Collection)draftFrames.get(draft));
                }
                catch (WebsocketNotConnectedException frames) {}
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void broadcast(String text, Collection<WebSocket> clients) {
        if (text == null || clients == null) {
            throw new IllegalArgumentException();
        }
        HashMap<Draft, List<Framedata>> draftFrames = new HashMap<Draft, List<Framedata>>();
        Collection<WebSocket> collection = clients;
        synchronized (collection) {
            for (WebSocket client : clients) {
                if (client == null) continue;
                Draft draft = client.getDraft();
                if (!draftFrames.containsKey(draft)) {
                    List<Framedata> frames = draft.createFrames(text, false);
                    draftFrames.put(draft, frames);
                }
                try {
                    client.sendFrame((Collection)draftFrames.get(draft));
                }
                catch (WebsocketNotConnectedException frames) {}
            }
        }
    }

    static /* synthetic */ void access$000(WebSocketServer x0, ByteBuffer x1) throws InterruptedException {
        x0.pushBuffer(x1);
    }

    static /* synthetic */ void access$100(WebSocketServer x0, WebSocket x1, Exception x2) {
        x0.handleFatal(x1, x2);
    }

    public class WebSocketWorker
    extends Thread {
        private BlockingQueue<WebSocketImpl> iqueue = new LinkedBlockingQueue<WebSocketImpl>();

        public WebSocketWorker() {
            this.setName("WebSocketWorker-" + this.getId());
            this.setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler(WebSocketServer.this){
                final /* synthetic */ WebSocketServer val$this$0;
                {
                    this.val$this$0 = webSocketServer;
                }

                public void uncaughtException(Thread t, Throwable e) {
                    System.err.print("Uncaught exception in thread \"" + t.getName() + "\":");
                    e.printStackTrace(System.err);
                }
            });
        }

        public void put(WebSocketImpl ws) throws InterruptedException {
            this.iqueue.put(ws);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         * Unable to fully structure code
         * Enabled aggressive block sorting
         * Enabled unnecessary exception pruning
         * Enabled aggressive exception aggregation
         * Lifted jumps to return sites
         */
        public void run() {
            ws = null;
            try {
                do lbl-1000: // 4 sources:
                {
                    ws = this.iqueue.take();
                    buf = ws.inQueue.poll();
                    if (!WebSocketWorker.$assertionsDisabled && buf == null) {
                        throw new AssertionError();
                    }
                    try {
                        ws.decode(buf);
                    }
                    catch (Exception e) {
                        System.err.println("Error while reading from remote connection: " + e);
                        e.printStackTrace();
                    }
                    finally {
                        WebSocketServer.access$000(WebSocketServer.this, buf);
                        continue;
                    }
                    break;
                } while (true);
            }
            catch (InterruptedException buf) {
                return;
            }
            catch (RuntimeException e) {
                WebSocketServer.access$100(WebSocketServer.this, ws, e);
            }
            ** GOTO lbl-1000
        }

    }

}

