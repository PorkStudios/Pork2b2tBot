/*
 * Decompiled with CFR 0_132.
 */
package org.java_websocket.drafts;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import org.java_websocket.WebSocket;
import org.java_websocket.WebSocketImpl;
import org.java_websocket.exceptions.IncompleteHandshakeException;
import org.java_websocket.exceptions.InvalidDataException;
import org.java_websocket.exceptions.InvalidHandshakeException;
import org.java_websocket.exceptions.LimitExedeedException;
import org.java_websocket.framing.BinaryFrame;
import org.java_websocket.framing.ContinuousFrame;
import org.java_websocket.framing.DataFrame;
import org.java_websocket.framing.Framedata;
import org.java_websocket.framing.TextFrame;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.handshake.ClientHandshakeBuilder;
import org.java_websocket.handshake.HandshakeBuilder;
import org.java_websocket.handshake.HandshakeImpl1Client;
import org.java_websocket.handshake.HandshakeImpl1Server;
import org.java_websocket.handshake.Handshakedata;
import org.java_websocket.handshake.HandshakedataImpl1;
import org.java_websocket.handshake.ServerHandshake;
import org.java_websocket.handshake.ServerHandshakeBuilder;
import org.java_websocket.util.Charsetfunctions;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public abstract class Draft {
    public static int MAX_FAME_SIZE = 1000;
    public static int INITIAL_FAMESIZE = 64;
    protected WebSocket.Role role = null;
    protected Framedata.Opcode continuousFrameType = null;

    public static ByteBuffer readLine(ByteBuffer buf) {
        ByteBuffer sbuf = ByteBuffer.allocate(buf.remaining());
        byte cur = 48;
        while (buf.hasRemaining()) {
            byte prev = cur;
            cur = buf.get();
            sbuf.put(cur);
            if (prev != 13 || cur != 10) continue;
            sbuf.limit(sbuf.position() - 2);
            sbuf.position(0);
            return sbuf;
        }
        buf.position(buf.position() - sbuf.position());
        return null;
    }

    public static String readStringLine(ByteBuffer buf) {
        ByteBuffer b = Draft.readLine(buf);
        return b == null ? null : Charsetfunctions.stringAscii(b.array(), 0, b.limit());
    }

    public static HandshakeBuilder translateHandshakeHttp(ByteBuffer buf, WebSocket.Role role) throws InvalidHandshakeException, IncompleteHandshakeException {
        HandshakedataImpl1 handshake;
        String line = Draft.readStringLine(buf);
        if (line == null) {
            throw new IncompleteHandshakeException(buf.capacity() + 128);
        }
        String[] firstLineTokens = line.split(" ", 3);
        if (firstLineTokens.length != 3) {
            throw new InvalidHandshakeException();
        }
        if (role == WebSocket.Role.CLIENT) {
            if (!"101".equals(firstLineTokens[1])) {
                throw new InvalidHandshakeException("Invalid status code received: " + firstLineTokens[1] + " Status line: " + line);
            }
            if (!"HTTP/1.1".equalsIgnoreCase(firstLineTokens[0])) {
                throw new InvalidHandshakeException("Invalid status line received: " + firstLineTokens[0] + " Status line: " + line);
            }
            handshake = new HandshakeImpl1Server();
            ServerHandshakeBuilder serverhandshake = (ServerHandshakeBuilder)((Object)handshake);
            serverhandshake.setHttpStatus(Short.parseShort(firstLineTokens[1]));
            serverhandshake.setHttpStatusMessage(firstLineTokens[2]);
        } else {
            if (!"GET".equalsIgnoreCase(firstLineTokens[0])) {
                throw new InvalidHandshakeException("Invalid request method received: " + firstLineTokens[0] + " Status line: " + line);
            }
            if (!"HTTP/1.1".equalsIgnoreCase(firstLineTokens[2])) {
                throw new InvalidHandshakeException("Invalid status line received: " + firstLineTokens[2] + " Status line: " + line);
            }
            HandshakeImpl1Client clienthandshake = new HandshakeImpl1Client();
            clienthandshake.setResourceDescriptor(firstLineTokens[1]);
            handshake = clienthandshake;
        }
        line = Draft.readStringLine(buf);
        while (line != null && line.length() > 0) {
            String[] pair = line.split(":", 2);
            if (pair.length != 2) {
                throw new InvalidHandshakeException("not an http header");
            }
            if (handshake.hasFieldValue(pair[0])) {
                handshake.put(pair[0], handshake.getFieldValue(pair[0]) + "; " + pair[1].replaceFirst("^ +", ""));
            } else {
                handshake.put(pair[0], pair[1].replaceFirst("^ +", ""));
            }
            line = Draft.readStringLine(buf);
        }
        if (line == null) {
            throw new IncompleteHandshakeException();
        }
        return handshake;
    }

    public abstract HandshakeState acceptHandshakeAsClient(ClientHandshake var1, ServerHandshake var2) throws InvalidHandshakeException;

    public abstract HandshakeState acceptHandshakeAsServer(ClientHandshake var1) throws InvalidHandshakeException;

    protected boolean basicAccept(Handshakedata handshakedata) {
        return handshakedata.getFieldValue("Upgrade").equalsIgnoreCase("websocket") && handshakedata.getFieldValue("Connection").toLowerCase(Locale.ENGLISH).contains("upgrade");
    }

    public abstract ByteBuffer createBinaryFrame(Framedata var1);

    public abstract List<Framedata> createFrames(ByteBuffer var1, boolean var2);

    public abstract List<Framedata> createFrames(String var1, boolean var2);

    public abstract void processFrame(WebSocketImpl var1, Framedata var2) throws InvalidDataException;

    public List<Framedata> continuousFrame(Framedata.Opcode op, ByteBuffer buffer, boolean fin) {
        if (op != Framedata.Opcode.BINARY && op != Framedata.Opcode.TEXT) {
            throw new IllegalArgumentException("Only Opcode.BINARY or  Opcode.TEXT are allowed");
        }
        DataFrame bui = null;
        if (this.continuousFrameType != null) {
            bui = new ContinuousFrame();
        } else {
            this.continuousFrameType = op;
            if (op == Framedata.Opcode.BINARY) {
                bui = new BinaryFrame();
            } else if (op == Framedata.Opcode.TEXT) {
                bui = new TextFrame();
            }
        }
        bui.setPayload(buffer);
        bui.setFin(fin);
        try {
            bui.isValid();
        }
        catch (InvalidDataException e) {
            throw new IllegalArgumentException(e);
        }
        this.continuousFrameType = fin ? null : op;
        return Collections.singletonList(bui);
    }

    public abstract void reset();

    public List<ByteBuffer> createHandshake(Handshakedata handshakedata, WebSocket.Role ownrole) {
        return this.createHandshake(handshakedata, ownrole, true);
    }

    public List<ByteBuffer> createHandshake(Handshakedata handshakedata, WebSocket.Role ownrole, boolean withcontent) {
        StringBuilder bui = new StringBuilder(100);
        if (handshakedata instanceof ClientHandshake) {
            bui.append("GET ");
            bui.append(((ClientHandshake)handshakedata).getResourceDescriptor());
            bui.append(" HTTP/1.1");
        } else if (handshakedata instanceof ServerHandshake) {
            bui.append("HTTP/1.1 101 ").append(((ServerHandshake)handshakedata).getHttpStatusMessage());
        } else {
            throw new IllegalArgumentException("unknown role");
        }
        bui.append("\r\n");
        Iterator<String> it = handshakedata.iterateHttpFields();
        while (it.hasNext()) {
            String fieldname = it.next();
            String fieldvalue = handshakedata.getFieldValue(fieldname);
            bui.append(fieldname);
            bui.append(": ");
            bui.append(fieldvalue);
            bui.append("\r\n");
        }
        bui.append("\r\n");
        byte[] httpheader = Charsetfunctions.asciiBytes(bui.toString());
        byte[] content = withcontent ? handshakedata.getContent() : null;
        ByteBuffer bytebuffer = ByteBuffer.allocate((content == null ? 0 : content.length) + httpheader.length);
        bytebuffer.put(httpheader);
        if (content != null) {
            bytebuffer.put(content);
        }
        bytebuffer.flip();
        return Collections.singletonList(bytebuffer);
    }

    public abstract ClientHandshakeBuilder postProcessHandshakeRequestAsClient(ClientHandshakeBuilder var1) throws InvalidHandshakeException;

    public abstract HandshakeBuilder postProcessHandshakeResponseAsServer(ClientHandshake var1, ServerHandshakeBuilder var2) throws InvalidHandshakeException;

    public abstract List<Framedata> translateFrame(ByteBuffer var1) throws InvalidDataException;

    public abstract CloseHandshakeType getCloseHandshakeType();

    public abstract Draft copyInstance();

    public Handshakedata translateHandshake(ByteBuffer buf) throws InvalidHandshakeException {
        return Draft.translateHandshakeHttp(buf, this.role);
    }

    public int checkAlloc(int bytecount) throws LimitExedeedException, InvalidDataException {
        if (bytecount < 0) {
            throw new InvalidDataException(1002, "Negative count");
        }
        return bytecount;
    }

    int readVersion(Handshakedata handshakedata) {
        String vers = handshakedata.getFieldValue("Sec-WebSocket-Version");
        if (vers.length() > 0) {
            try {
                int v = new Integer(vers.trim());
                return v;
            }
            catch (NumberFormatException e) {
                return -1;
            }
        }
        return -1;
    }

    public void setParseMode(WebSocket.Role role) {
        this.role = role;
    }

    public WebSocket.Role getRole() {
        return this.role;
    }

    public String toString() {
        return this.getClass().getSimpleName();
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum CloseHandshakeType {
        NONE,
        ONEWAY,
        TWOWAY;
        

        private CloseHandshakeType() {
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum HandshakeState {
        MATCHED,
        NOT_MATCHED;
        

        private HandshakeState() {
        }
    }

}

