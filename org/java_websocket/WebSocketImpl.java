/*
 * Decompiled with CFR 0_132.
 */
package org.java_websocket;

import java.io.IOException;
import java.io.PrintStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;
import java.nio.channels.NotYetConnectedException;
import java.nio.channels.SelectionKey;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import org.java_websocket.WebSocket;
import org.java_websocket.WebSocketListener;
import org.java_websocket.drafts.Draft;
import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.exceptions.IncompleteHandshakeException;
import org.java_websocket.exceptions.InvalidDataException;
import org.java_websocket.exceptions.InvalidHandshakeException;
import org.java_websocket.exceptions.WebsocketNotConnectedException;
import org.java_websocket.framing.CloseFrame;
import org.java_websocket.framing.Framedata;
import org.java_websocket.framing.PingFrame;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.handshake.ClientHandshakeBuilder;
import org.java_websocket.handshake.HandshakeBuilder;
import org.java_websocket.handshake.Handshakedata;
import org.java_websocket.handshake.ServerHandshake;
import org.java_websocket.handshake.ServerHandshakeBuilder;
import org.java_websocket.server.WebSocketServer;
import org.java_websocket.util.Charsetfunctions;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class WebSocketImpl
implements WebSocket {
    public static int RCVBUF = 16384;
    public static boolean DEBUG = false;
    public final BlockingQueue<ByteBuffer> outQueue;
    public final BlockingQueue<ByteBuffer> inQueue;
    private final WebSocketListener wsl;
    public SelectionKey key;
    public ByteChannel channel;
    public volatile WebSocketServer.WebSocketWorker workerThread;
    private volatile boolean flushandclosestate = false;
    private WebSocket.READYSTATE readystate = WebSocket.READYSTATE.NOT_YET_CONNECTED;
    private List<Draft> knownDrafts;
    private Draft draft = null;
    private WebSocket.Role role;
    private ByteBuffer tmpHandshakeBytes = ByteBuffer.allocate(0);
    private ClientHandshake handshakerequest = null;
    private String closemessage = null;
    private Integer closecode = null;
    private Boolean closedremotely = null;
    private String resourceDescriptor = null;
    private long lastPong = System.currentTimeMillis();
    private static final Object synchronizeWriteObject = new Object();
    private PingFrame pingFrame;
    private Object attachment;

    public WebSocketImpl(WebSocketListener listener, List<Draft> drafts) {
        this(listener, (Draft)null);
        this.role = WebSocket.Role.SERVER;
        if (drafts == null || drafts.isEmpty()) {
            this.knownDrafts = new ArrayList<Draft>();
            this.knownDrafts.add(new Draft_6455());
        } else {
            this.knownDrafts = drafts;
        }
    }

    public WebSocketImpl(WebSocketListener listener, Draft draft) {
        if (listener == null || draft == null && this.role == WebSocket.Role.SERVER) {
            throw new IllegalArgumentException("parameters must not be null");
        }
        this.outQueue = new LinkedBlockingQueue<ByteBuffer>();
        this.inQueue = new LinkedBlockingQueue<ByteBuffer>();
        this.wsl = listener;
        this.role = WebSocket.Role.CLIENT;
        if (draft != null) {
            this.draft = draft.copyInstance();
        }
    }

    @Deprecated
    public WebSocketImpl(WebSocketListener listener, Draft draft, Socket socket) {
        this(listener, draft);
    }

    @Deprecated
    public WebSocketImpl(WebSocketListener listener, List<Draft> drafts, Socket socket) {
        this(listener, drafts);
    }

    public void decode(ByteBuffer socketBuffer) {
        assert (socketBuffer.hasRemaining());
        if (DEBUG) {
            System.out.println("process(" + socketBuffer.remaining() + "): {" + (socketBuffer.remaining() > 1000 ? "too big to display" : new String(socketBuffer.array(), socketBuffer.position(), socketBuffer.remaining())) + '}');
        }
        if (this.getReadyState() != WebSocket.READYSTATE.NOT_YET_CONNECTED) {
            if (this.getReadyState() == WebSocket.READYSTATE.OPEN) {
                this.decodeFrames(socketBuffer);
            }
        } else if (this.decodeHandshake(socketBuffer) && !this.isClosing() && !this.isClosed()) {
            assert (this.tmpHandshakeBytes.hasRemaining() != socketBuffer.hasRemaining() || !socketBuffer.hasRemaining());
            if (socketBuffer.hasRemaining()) {
                this.decodeFrames(socketBuffer);
            } else if (this.tmpHandshakeBytes.hasRemaining()) {
                this.decodeFrames(this.tmpHandshakeBytes);
            }
        }
        assert (this.isClosing() || this.isFlushAndClose() || !socketBuffer.hasRemaining());
    }

    private boolean decodeHandshake(ByteBuffer socketBufferNew) {
        block28 : {
            ByteBuffer socketBuffer;
            if (this.tmpHandshakeBytes.capacity() == 0) {
                socketBuffer = socketBufferNew;
            } else {
                if (this.tmpHandshakeBytes.remaining() < socketBufferNew.remaining()) {
                    ByteBuffer buf = ByteBuffer.allocate(this.tmpHandshakeBytes.capacity() + socketBufferNew.remaining());
                    this.tmpHandshakeBytes.flip();
                    buf.put(this.tmpHandshakeBytes);
                    this.tmpHandshakeBytes = buf;
                }
                this.tmpHandshakeBytes.put(socketBufferNew);
                this.tmpHandshakeBytes.flip();
                socketBuffer = this.tmpHandshakeBytes;
            }
            socketBuffer.mark();
            try {
                try {
                    Draft.HandshakeState handshakestate;
                    if (this.role == WebSocket.Role.SERVER) {
                        if (this.draft == null) {
                            for (Draft d : this.knownDrafts) {
                                d = d.copyInstance();
                                try {
                                    ServerHandshakeBuilder response;
                                    d.setParseMode(this.role);
                                    socketBuffer.reset();
                                    Handshakedata tmphandshake = d.translateHandshake(socketBuffer);
                                    if (!(tmphandshake instanceof ClientHandshake)) {
                                        this.closeConnectionDueToWrongHandshake(new InvalidDataException(1002, "wrong http function"));
                                        return false;
                                    }
                                    ClientHandshake handshake = (ClientHandshake)tmphandshake;
                                    handshakestate = d.acceptHandshakeAsServer(handshake);
                                    if (handshakestate != Draft.HandshakeState.MATCHED) continue;
                                    this.resourceDescriptor = handshake.getResourceDescriptor();
                                    try {
                                        response = this.wsl.onWebsocketHandshakeReceivedAsServer(this, d, handshake);
                                    }
                                    catch (InvalidDataException e) {
                                        this.closeConnectionDueToWrongHandshake(e);
                                        return false;
                                    }
                                    catch (RuntimeException e) {
                                        this.wsl.onWebsocketError(this, e);
                                        this.closeConnectionDueToInternalServerError(e);
                                        return false;
                                    }
                                    this.write(d.createHandshake(d.postProcessHandshakeResponseAsServer(handshake, response), this.role));
                                    this.draft = d;
                                    this.open(handshake);
                                    return true;
                                }
                                catch (InvalidHandshakeException tmphandshake) {
                                }
                            }
                            if (this.draft == null) {
                                this.closeConnectionDueToWrongHandshake(new InvalidDataException(1002, "no draft matches"));
                            }
                            return false;
                        }
                        Handshakedata tmphandshake = this.draft.translateHandshake(socketBuffer);
                        if (!(tmphandshake instanceof ClientHandshake)) {
                            this.flushAndClose(1002, "wrong http function", false);
                            return false;
                        }
                        ClientHandshake handshake = (ClientHandshake)tmphandshake;
                        handshakestate = this.draft.acceptHandshakeAsServer(handshake);
                        if (handshakestate == Draft.HandshakeState.MATCHED) {
                            this.open(handshake);
                            return true;
                        }
                        this.close(1002, "the handshake did finaly not match");
                        return false;
                    }
                    if (this.role != WebSocket.Role.CLIENT) break block28;
                    this.draft.setParseMode(this.role);
                    Handshakedata tmphandshake = this.draft.translateHandshake(socketBuffer);
                    if (!(tmphandshake instanceof ServerHandshake)) {
                        this.flushAndClose(1002, "wrong http function", false);
                        return false;
                    }
                    ServerHandshake handshake = (ServerHandshake)tmphandshake;
                    handshakestate = this.draft.acceptHandshakeAsClient(this.handshakerequest, handshake);
                    if (handshakestate == Draft.HandshakeState.MATCHED) {
                        try {
                            this.wsl.onWebsocketHandshakeReceivedAsClient(this, this.handshakerequest, handshake);
                        }
                        catch (InvalidDataException e) {
                            this.flushAndClose(e.getCloseCode(), e.getMessage(), false);
                            return false;
                        }
                        catch (RuntimeException e) {
                            this.wsl.onWebsocketError(this, e);
                            this.flushAndClose(-1, e.getMessage(), false);
                            return false;
                        }
                        this.open(handshake);
                        return true;
                    }
                    this.close(1002, "draft " + this.draft + " refuses handshake");
                }
                catch (InvalidHandshakeException e) {
                    this.close(e);
                }
            }
            catch (IncompleteHandshakeException e) {
                if (this.tmpHandshakeBytes.capacity() == 0) {
                    socketBuffer.reset();
                    int newsize = e.getPreferedSize();
                    if (newsize == 0) {
                        newsize = socketBuffer.capacity() + 16;
                    } else assert (e.getPreferedSize() >= socketBuffer.remaining());
                    this.tmpHandshakeBytes = ByteBuffer.allocate(newsize);
                    this.tmpHandshakeBytes.put(socketBufferNew);
                }
                this.tmpHandshakeBytes.position(this.tmpHandshakeBytes.limit());
                this.tmpHandshakeBytes.limit(this.tmpHandshakeBytes.capacity());
            }
        }
        return false;
    }

    private void decodeFrames(ByteBuffer socketBuffer) {
        try {
            List<Framedata> frames = this.draft.translateFrame(socketBuffer);
            for (Framedata f : frames) {
                if (DEBUG) {
                    System.out.println("matched frame: " + f);
                }
                this.draft.processFrame(this, f);
            }
        }
        catch (InvalidDataException e1) {
            this.wsl.onWebsocketError(this, e1);
            this.close(e1);
            return;
        }
    }

    private void closeConnectionDueToWrongHandshake(InvalidDataException exception) {
        this.write(this.generateHttpResponseDueToError(404));
        this.flushAndClose(exception.getCloseCode(), exception.getMessage(), false);
    }

    private void closeConnectionDueToInternalServerError(RuntimeException exception) {
        this.write(this.generateHttpResponseDueToError(500));
        this.flushAndClose(-1, exception.getMessage(), false);
    }

    private ByteBuffer generateHttpResponseDueToError(int errorCode) {
        String errorCodeDescription;
        switch (errorCode) {
            case 404: {
                errorCodeDescription = "404 WebSocket Upgrade Failure";
                break;
            }
            default: {
                errorCodeDescription = "500 Internal Server Error";
            }
        }
        return ByteBuffer.wrap(Charsetfunctions.asciiBytes("HTTP/1.1 " + errorCodeDescription + "\r\nContent-Type: text/html\nServer: TooTallNate Java-WebSocket\r\nContent-Length: " + (48 + errorCodeDescription.length()) + "\r\n\r\n<html><head></head><body><h1>" + errorCodeDescription + "</h1></body></html>"));
    }

    public synchronized void close(int code, String message, boolean remote) {
        if (this.getReadyState() != WebSocket.READYSTATE.CLOSING && this.readystate != WebSocket.READYSTATE.CLOSED) {
            if (this.getReadyState() == WebSocket.READYSTATE.OPEN) {
                if (code == 1006) {
                    assert (!remote);
                    this.setReadyState(WebSocket.READYSTATE.CLOSING);
                    this.flushAndClose(code, message, false);
                    return;
                }
                if (this.draft.getCloseHandshakeType() != Draft.CloseHandshakeType.NONE) {
                    try {
                        if (!remote) {
                            try {
                                this.wsl.onWebsocketCloseInitiated(this, code, message);
                            }
                            catch (RuntimeException e) {
                                this.wsl.onWebsocketError(this, e);
                            }
                        }
                        if (this.isOpen()) {
                            CloseFrame closeFrame = new CloseFrame();
                            closeFrame.setReason(message);
                            closeFrame.setCode(code);
                            closeFrame.isValid();
                            this.sendFrame(closeFrame);
                        }
                    }
                    catch (InvalidDataException e) {
                        this.wsl.onWebsocketError(this, e);
                        this.flushAndClose(1006, "generated frame is invalid", false);
                    }
                }
                this.flushAndClose(code, message, remote);
            } else if (code == -3) {
                assert (remote);
                this.flushAndClose(-3, message, true);
            } else if (code == 1002) {
                this.flushAndClose(code, message, remote);
            } else {
                this.flushAndClose(-1, message, false);
            }
            this.setReadyState(WebSocket.READYSTATE.CLOSING);
            this.tmpHandshakeBytes = null;
            return;
        }
    }

    @Override
    public void close(int code, String message) {
        this.close(code, message, false);
    }

    public synchronized void closeConnection(int code, String message, boolean remote) {
        if (this.getReadyState() == WebSocket.READYSTATE.CLOSED) {
            return;
        }
        if (this.getReadyState() == WebSocket.READYSTATE.OPEN && code == 1006) {
            this.setReadyState(WebSocket.READYSTATE.CLOSING);
        }
        if (this.key != null) {
            this.key.cancel();
        }
        if (this.channel != null) {
            try {
                this.channel.close();
            }
            catch (IOException e) {
                if (e.getMessage().equals("Broken pipe")) {
                    if (DEBUG) {
                        System.out.println("Caught IOException: Broken pipe during closeConnection()");
                    }
                }
                this.wsl.onWebsocketError(this, e);
            }
        }
        try {
            this.wsl.onWebsocketClose(this, code, message, remote);
        }
        catch (RuntimeException e) {
            this.wsl.onWebsocketError(this, e);
        }
        if (this.draft != null) {
            this.draft.reset();
        }
        this.handshakerequest = null;
        this.setReadyState(WebSocket.READYSTATE.CLOSED);
    }

    protected void closeConnection(int code, boolean remote) {
        this.closeConnection(code, "", remote);
    }

    public void closeConnection() {
        if (this.closedremotely == null) {
            throw new IllegalStateException("this method must be used in conjuction with flushAndClose");
        }
        this.closeConnection(this.closecode, this.closemessage, this.closedremotely);
    }

    @Override
    public void closeConnection(int code, String message) {
        this.closeConnection(code, message, false);
    }

    public synchronized void flushAndClose(int code, String message, boolean remote) {
        if (this.flushandclosestate) {
            return;
        }
        this.closecode = code;
        this.closemessage = message;
        this.closedremotely = remote;
        this.flushandclosestate = true;
        this.wsl.onWriteDemand(this);
        try {
            this.wsl.onWebsocketClosing(this, code, message, remote);
        }
        catch (RuntimeException e) {
            this.wsl.onWebsocketError(this, e);
        }
        if (this.draft != null) {
            this.draft.reset();
        }
        this.handshakerequest = null;
    }

    public void eot() {
        if (this.getReadyState() == WebSocket.READYSTATE.NOT_YET_CONNECTED) {
            this.closeConnection(-1, true);
        } else if (this.flushandclosestate) {
            this.closeConnection(this.closecode, this.closemessage, this.closedremotely);
        } else if (this.draft.getCloseHandshakeType() == Draft.CloseHandshakeType.NONE) {
            this.closeConnection(1000, true);
        } else if (this.draft.getCloseHandshakeType() == Draft.CloseHandshakeType.ONEWAY) {
            if (this.role == WebSocket.Role.SERVER) {
                this.closeConnection(1006, true);
            } else {
                this.closeConnection(1000, true);
            }
        } else {
            this.closeConnection(1006, true);
        }
    }

    @Override
    public void close(int code) {
        this.close(code, "", false);
    }

    public void close(InvalidDataException e) {
        this.close(e.getCloseCode(), e.getMessage(), false);
    }

    @Override
    public void send(String text) throws WebsocketNotConnectedException {
        if (text == null) {
            throw new IllegalArgumentException("Cannot send 'null' data to a WebSocketImpl.");
        }
        this.send(this.draft.createFrames(text, this.role == WebSocket.Role.CLIENT));
    }

    @Override
    public void send(ByteBuffer bytes) throws IllegalArgumentException, WebsocketNotConnectedException {
        if (bytes == null) {
            throw new IllegalArgumentException("Cannot send 'null' data to a WebSocketImpl.");
        }
        this.send(this.draft.createFrames(bytes, this.role == WebSocket.Role.CLIENT));
    }

    @Override
    public void send(byte[] bytes) throws IllegalArgumentException, WebsocketNotConnectedException {
        this.send(ByteBuffer.wrap(bytes));
    }

    private void send(Collection<Framedata> frames) {
        if (!this.isOpen()) {
            throw new WebsocketNotConnectedException();
        }
        if (frames == null) {
            throw new IllegalArgumentException();
        }
        ArrayList<ByteBuffer> outgoingFrames = new ArrayList<ByteBuffer>();
        for (Framedata f : frames) {
            if (DEBUG) {
                System.out.println("send frame: " + f);
            }
            outgoingFrames.add(this.draft.createBinaryFrame(f));
        }
        this.write(outgoingFrames);
    }

    @Override
    public void sendFragmentedFrame(Framedata.Opcode op, ByteBuffer buffer, boolean fin) {
        this.send(this.draft.continuousFrame(op, buffer, fin));
    }

    @Override
    public void sendFrame(Collection<Framedata> frames) {
        this.send(frames);
    }

    @Override
    public void sendFrame(Framedata framedata) {
        this.send(Collections.singletonList(framedata));
    }

    @Override
    public void sendPing() throws NotYetConnectedException {
        if (this.pingFrame == null) {
            this.pingFrame = new PingFrame();
        }
        this.sendFrame(this.pingFrame);
    }

    @Override
    public boolean hasBufferedData() {
        return !this.outQueue.isEmpty();
    }

    public void startHandshake(ClientHandshakeBuilder handshakedata) throws InvalidHandshakeException {
        assert (this.getReadyState() != WebSocket.READYSTATE.CONNECTING);
        this.handshakerequest = this.draft.postProcessHandshakeRequestAsClient(handshakedata);
        this.resourceDescriptor = handshakedata.getResourceDescriptor();
        assert (this.resourceDescriptor != null);
        try {
            this.wsl.onWebsocketHandshakeSentAsClient(this, this.handshakerequest);
        }
        catch (InvalidDataException e) {
            throw new InvalidHandshakeException("Handshake data rejected by client.");
        }
        catch (RuntimeException e) {
            this.wsl.onWebsocketError(this, e);
            throw new InvalidHandshakeException("rejected because of" + e);
        }
        this.write(this.draft.createHandshake(this.handshakerequest, this.role));
    }

    private void write(ByteBuffer buf) {
        if (DEBUG) {
            System.out.println("write(" + buf.remaining() + "): {" + (buf.remaining() > 1000 ? "too big to display" : new String(buf.array())) + '}');
        }
        this.outQueue.add(buf);
        this.wsl.onWriteDemand(this);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void write(List<ByteBuffer> bufs) {
        Object object = synchronizeWriteObject;
        synchronized (object) {
            for (ByteBuffer b : bufs) {
                this.write(b);
            }
        }
    }

    private void open(Handshakedata d) {
        if (DEBUG) {
            System.out.println("open using draft: " + this.draft);
        }
        this.setReadyState(WebSocket.READYSTATE.OPEN);
        try {
            this.wsl.onWebsocketOpen(this, d);
        }
        catch (RuntimeException e) {
            this.wsl.onWebsocketError(this, e);
        }
    }

    @Override
    public boolean isConnecting() {
        assert (!this.flushandclosestate || this.getReadyState() == WebSocket.READYSTATE.CONNECTING);
        return this.getReadyState() == WebSocket.READYSTATE.CONNECTING;
    }

    @Override
    public boolean isOpen() {
        assert (this.getReadyState() != WebSocket.READYSTATE.OPEN || !this.flushandclosestate);
        return this.getReadyState() == WebSocket.READYSTATE.OPEN;
    }

    @Override
    public boolean isClosing() {
        return this.getReadyState() == WebSocket.READYSTATE.CLOSING;
    }

    @Override
    public boolean isFlushAndClose() {
        return this.flushandclosestate;
    }

    @Override
    public boolean isClosed() {
        return this.getReadyState() == WebSocket.READYSTATE.CLOSED;
    }

    @Override
    public WebSocket.READYSTATE getReadyState() {
        return this.readystate;
    }

    private void setReadyState(WebSocket.READYSTATE readystate) {
        this.readystate = readystate;
    }

    public int hashCode() {
        return super.hashCode();
    }

    public String toString() {
        return super.toString();
    }

    @Override
    public InetSocketAddress getRemoteSocketAddress() {
        return this.wsl.getRemoteSocketAddress(this);
    }

    @Override
    public InetSocketAddress getLocalSocketAddress() {
        return this.wsl.getLocalSocketAddress(this);
    }

    @Override
    public Draft getDraft() {
        return this.draft;
    }

    @Override
    public void close() {
        this.close(1000);
    }

    @Override
    public String getResourceDescriptor() {
        return this.resourceDescriptor;
    }

    long getLastPong() {
        return this.lastPong;
    }

    public void updateLastPong() {
        this.lastPong = System.currentTimeMillis();
    }

    public WebSocketListener getWebSocketListener() {
        return this.wsl;
    }

    @Override
    public <T> T getAttachment() {
        return (T)this.attachment;
    }

    @Override
    public <T> void setAttachment(T attachment) {
        this.attachment = attachment;
    }
}

