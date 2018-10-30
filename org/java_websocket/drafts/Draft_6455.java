/*
 * Decompiled with CFR 0_132.
 */
package org.java_websocket.drafts;

import java.io.PrintStream;
import java.math.BigInteger;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.TimeZone;
import org.java_websocket.WebSocket;
import org.java_websocket.WebSocketImpl;
import org.java_websocket.WebSocketListener;
import org.java_websocket.drafts.Draft;
import org.java_websocket.exceptions.IncompleteException;
import org.java_websocket.exceptions.InvalidDataException;
import org.java_websocket.exceptions.InvalidFrameException;
import org.java_websocket.exceptions.InvalidHandshakeException;
import org.java_websocket.exceptions.LimitExedeedException;
import org.java_websocket.exceptions.NotSendableException;
import org.java_websocket.extensions.DefaultExtension;
import org.java_websocket.extensions.IExtension;
import org.java_websocket.framing.BinaryFrame;
import org.java_websocket.framing.CloseFrame;
import org.java_websocket.framing.Framedata;
import org.java_websocket.framing.FramedataImpl1;
import org.java_websocket.framing.TextFrame;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.handshake.ClientHandshakeBuilder;
import org.java_websocket.handshake.HandshakeBuilder;
import org.java_websocket.handshake.Handshakedata;
import org.java_websocket.handshake.ServerHandshake;
import org.java_websocket.handshake.ServerHandshakeBuilder;
import org.java_websocket.protocols.IProtocol;
import org.java_websocket.protocols.Protocol;
import org.java_websocket.util.Base64;
import org.java_websocket.util.Charsetfunctions;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class Draft_6455
extends Draft {
    private IExtension extension = new DefaultExtension();
    private List<IExtension> knownExtensions;
    private IProtocol protocol;
    private List<IProtocol> knownProtocols;
    private Framedata current_continuous_frame;
    private List<ByteBuffer> byteBufferList;
    private ByteBuffer incompleteframe;
    private final Random reuseableRandom = new Random();

    public Draft_6455() {
        this(Collections.emptyList());
    }

    public Draft_6455(IExtension inputExtension) {
        this(Collections.singletonList(inputExtension));
    }

    public Draft_6455(List<IExtension> inputExtensions) {
        this(inputExtensions, Collections.singletonList(new Protocol("")));
    }

    public Draft_6455(List<IExtension> inputExtensions, List<IProtocol> inputProtocols) {
        if (inputExtensions == null || inputProtocols == null) {
            throw new IllegalArgumentException();
        }
        this.knownExtensions = new ArrayList<IExtension>(inputExtensions.size());
        this.knownProtocols = new ArrayList<IProtocol>(inputProtocols.size());
        boolean hasDefault = false;
        this.byteBufferList = new ArrayList<ByteBuffer>();
        for (IExtension inputExtension : inputExtensions) {
            if (!inputExtension.getClass().equals(DefaultExtension.class)) continue;
            hasDefault = true;
        }
        this.knownExtensions.addAll(inputExtensions);
        if (!hasDefault) {
            this.knownExtensions.add(this.knownExtensions.size(), this.extension);
        }
        this.knownProtocols.addAll(inputProtocols);
    }

    @Override
    public Draft.HandshakeState acceptHandshakeAsServer(ClientHandshake handshakedata) throws InvalidHandshakeException {
        int v = this.readVersion(handshakedata);
        if (v != 13) {
            return Draft.HandshakeState.NOT_MATCHED;
        }
        Draft.HandshakeState extensionState = Draft.HandshakeState.NOT_MATCHED;
        String requestedExtension = handshakedata.getFieldValue("Sec-WebSocket-Extensions");
        for (IExtension knownExtension : this.knownExtensions) {
            if (!knownExtension.acceptProvidedExtensionAsServer(requestedExtension)) continue;
            this.extension = knownExtension;
            extensionState = Draft.HandshakeState.MATCHED;
            break;
        }
        Draft.HandshakeState protocolState = Draft.HandshakeState.NOT_MATCHED;
        String requestedProtocol = handshakedata.getFieldValue("Sec-WebSocket-Protocol");
        for (IProtocol knownProtocol : this.knownProtocols) {
            if (!knownProtocol.acceptProvidedProtocol(requestedProtocol)) continue;
            this.protocol = knownProtocol;
            protocolState = Draft.HandshakeState.MATCHED;
            break;
        }
        if (protocolState == Draft.HandshakeState.MATCHED && extensionState == Draft.HandshakeState.MATCHED) {
            return Draft.HandshakeState.MATCHED;
        }
        return Draft.HandshakeState.NOT_MATCHED;
    }

    @Override
    public Draft.HandshakeState acceptHandshakeAsClient(ClientHandshake request, ServerHandshake response) throws InvalidHandshakeException {
        if (!this.basicAccept(response)) {
            return Draft.HandshakeState.NOT_MATCHED;
        }
        if (!request.hasFieldValue("Sec-WebSocket-Key") || !response.hasFieldValue("Sec-WebSocket-Accept")) {
            return Draft.HandshakeState.NOT_MATCHED;
        }
        String seckey_answere = response.getFieldValue("Sec-WebSocket-Accept");
        String seckey_challenge = request.getFieldValue("Sec-WebSocket-Key");
        if (!(seckey_challenge = this.generateFinalKey(seckey_challenge)).equals(seckey_answere)) {
            return Draft.HandshakeState.NOT_MATCHED;
        }
        Draft.HandshakeState extensionState = Draft.HandshakeState.NOT_MATCHED;
        String requestedExtension = response.getFieldValue("Sec-WebSocket-Extensions");
        for (IExtension knownExtension : this.knownExtensions) {
            if (!knownExtension.acceptProvidedExtensionAsClient(requestedExtension)) continue;
            this.extension = knownExtension;
            extensionState = Draft.HandshakeState.MATCHED;
            break;
        }
        Draft.HandshakeState protocolState = Draft.HandshakeState.NOT_MATCHED;
        String requestedProtocol = response.getFieldValue("Sec-WebSocket-Protocol");
        for (IProtocol knownProtocol : this.knownProtocols) {
            if (!knownProtocol.acceptProvidedProtocol(requestedProtocol)) continue;
            this.protocol = knownProtocol;
            protocolState = Draft.HandshakeState.MATCHED;
            break;
        }
        if (protocolState == Draft.HandshakeState.MATCHED && extensionState == Draft.HandshakeState.MATCHED) {
            return Draft.HandshakeState.MATCHED;
        }
        return Draft.HandshakeState.NOT_MATCHED;
    }

    public IExtension getExtension() {
        return this.extension;
    }

    public List<IExtension> getKnownExtensions() {
        return this.knownExtensions;
    }

    public IProtocol getProtocol() {
        return this.protocol;
    }

    public List<IProtocol> getKnownProtocols() {
        return this.knownProtocols;
    }

    @Override
    public ClientHandshakeBuilder postProcessHandshakeRequestAsClient(ClientHandshakeBuilder request) {
        request.put("Upgrade", "websocket");
        request.put("Connection", "Upgrade");
        byte[] random = new byte[16];
        this.reuseableRandom.nextBytes(random);
        request.put("Sec-WebSocket-Key", Base64.encodeBytes(random));
        request.put("Sec-WebSocket-Version", "13");
        StringBuilder requestedExtensions = new StringBuilder();
        for (IExtension knownExtension : this.knownExtensions) {
            if (knownExtension.getProvidedExtensionAsClient() == null || knownExtension.getProvidedExtensionAsClient().length() == 0) continue;
            if (requestedExtensions.length() > 0) {
                requestedExtensions.append(", ");
            }
            requestedExtensions.append(knownExtension.getProvidedExtensionAsClient());
        }
        if (requestedExtensions.length() != 0) {
            request.put("Sec-WebSocket-Extensions", requestedExtensions.toString());
        }
        StringBuilder requestedProtocols = new StringBuilder();
        for (IProtocol knownProtocol : this.knownProtocols) {
            if (knownProtocol.getProvidedProtocol().length() == 0) continue;
            if (requestedProtocols.length() > 0) {
                requestedProtocols.append(", ");
            }
            requestedProtocols.append(knownProtocol.getProvidedProtocol());
        }
        if (requestedProtocols.length() != 0) {
            request.put("Sec-WebSocket-Protocol", requestedProtocols.toString());
        }
        return request;
    }

    @Override
    public HandshakeBuilder postProcessHandshakeResponseAsServer(ClientHandshake request, ServerHandshakeBuilder response) throws InvalidHandshakeException {
        response.put("Upgrade", "websocket");
        response.put("Connection", request.getFieldValue("Connection"));
        String seckey = request.getFieldValue("Sec-WebSocket-Key");
        if (seckey == null) {
            throw new InvalidHandshakeException("missing Sec-WebSocket-Key");
        }
        response.put("Sec-WebSocket-Accept", this.generateFinalKey(seckey));
        if (this.getExtension().getProvidedExtensionAsServer().length() != 0) {
            response.put("Sec-WebSocket-Extensions", this.getExtension().getProvidedExtensionAsServer());
        }
        if (this.getProtocol() != null && this.getProtocol().getProvidedProtocol().length() != 0) {
            response.put("Sec-WebSocket-Protocol", this.getProtocol().getProvidedProtocol());
        }
        response.setHttpStatusMessage("Web Socket Protocol Handshake");
        response.put("Server", "TooTallNate Java-WebSocket");
        response.put("Date", this.getServerTime());
        return response;
    }

    @Override
    public Draft copyInstance() {
        ArrayList<IExtension> newExtensions = new ArrayList<IExtension>();
        for (IExtension extension : this.getKnownExtensions()) {
            newExtensions.add(extension.copyInstance());
        }
        ArrayList<IProtocol> newProtocols = new ArrayList<IProtocol>();
        for (IProtocol protocol : this.getKnownProtocols()) {
            newProtocols.add(protocol.copyInstance());
        }
        return new Draft_6455(newExtensions, newProtocols);
    }

    @Override
    public ByteBuffer createBinaryFrame(Framedata framedata) {
        this.getExtension().encodeFrame(framedata);
        if (WebSocketImpl.DEBUG) {
            System.out.println("afterEnconding(" + framedata.getPayloadData().remaining() + "): {" + (framedata.getPayloadData().remaining() > 1000 ? "too big to display" : new String(framedata.getPayloadData().array())) + '}');
        }
        return this.createByteBufferFromFramedata(framedata);
    }

    private ByteBuffer createByteBufferFromFramedata(Framedata framedata) {
        boolean mask;
        ByteBuffer mes = framedata.getPayloadData();
        boolean bl = mask = this.role == WebSocket.Role.CLIENT;
        int sizebytes = mes.remaining() <= 125 ? 1 : (mes.remaining() <= 65535 ? 2 : 8);
        ByteBuffer buf = ByteBuffer.allocate(1 + (sizebytes > 1 ? sizebytes + 1 : sizebytes) + (mask ? 4 : 0) + mes.remaining());
        byte optcode = this.fromOpcode(framedata.getOpcode());
        byte one = (byte)(framedata.isFin() ? -128 : 0);
        one = (byte)(one | optcode);
        buf.put(one);
        byte[] payloadlengthbytes = this.toByteArray(mes.remaining(), sizebytes);
        assert (payloadlengthbytes.length == sizebytes);
        if (sizebytes == 1) {
            buf.put((byte)(payloadlengthbytes[0] | (mask ? -128 : 0)));
        } else if (sizebytes == 2) {
            buf.put((byte)(126 | (mask ? -128 : 0)));
            buf.put(payloadlengthbytes);
        } else if (sizebytes == 8) {
            buf.put((byte)(127 | (mask ? -128 : 0)));
            buf.put(payloadlengthbytes);
        } else {
            throw new RuntimeException("Size representation not supported/specified");
        }
        if (mask) {
            ByteBuffer maskkey = ByteBuffer.allocate(4);
            maskkey.putInt(this.reuseableRandom.nextInt());
            buf.put(maskkey.array());
            int i = 0;
            while (mes.hasRemaining()) {
                buf.put((byte)(mes.get() ^ maskkey.get(i % 4)));
                ++i;
            }
        } else {
            buf.put(mes);
            mes.flip();
        }
        assert (buf.remaining() == 0);
        buf.flip();
        return buf;
    }

    public Framedata translateSingleFrame(ByteBuffer buffer) throws IncompleteException, InvalidDataException {
        byte b2;
        int realpacketsize;
        int maxpacketsize = buffer.remaining();
        if (maxpacketsize < (realpacketsize = 2)) {
            throw new IncompleteException(realpacketsize);
        }
        byte b1 = buffer.get();
        boolean FIN = b1 >> 8 != 0;
        boolean rsv1 = false;
        boolean rsv2 = false;
        boolean rsv3 = false;
        if ((b1 & 64) != 0) {
            rsv1 = true;
        }
        if ((b1 & 32) != 0) {
            rsv2 = true;
        }
        if ((b1 & 16) != 0) {
            rsv3 = true;
        }
        boolean MASK = ((b2 = buffer.get()) & -128) != 0;
        int payloadlength = b2 & 127;
        Framedata.Opcode optcode = this.toOpcode((byte)(b1 & 15));
        if (payloadlength < 0 || payloadlength > 125) {
            if (optcode == Framedata.Opcode.PING || optcode == Framedata.Opcode.PONG || optcode == Framedata.Opcode.CLOSING) {
                throw new InvalidFrameException("more than 125 octets");
            }
            if (payloadlength == 126) {
                if (maxpacketsize < (realpacketsize += 2)) {
                    throw new IncompleteException(realpacketsize);
                }
                byte[] sizebytes = new byte[3];
                sizebytes[1] = buffer.get();
                sizebytes[2] = buffer.get();
                payloadlength = new BigInteger(sizebytes).intValue();
            } else {
                if (maxpacketsize < (realpacketsize += 8)) {
                    throw new IncompleteException(realpacketsize);
                }
                byte[] bytes = new byte[8];
                for (int i = 0; i < 8; ++i) {
                    bytes[i] = buffer.get();
                }
                long length = new BigInteger(bytes).longValue();
                if (length > Integer.MAX_VALUE) {
                    throw new LimitExedeedException("Payloadsize is to big...");
                }
                payloadlength = (int)length;
            }
        }
        realpacketsize += MASK ? 4 : 0;
        if (maxpacketsize < (realpacketsize += payloadlength)) {
            throw new IncompleteException(realpacketsize);
        }
        ByteBuffer payload = ByteBuffer.allocate(this.checkAlloc(payloadlength));
        if (MASK) {
            byte[] maskskey = new byte[4];
            buffer.get(maskskey);
            for (int i = 0; i < payloadlength; ++i) {
                payload.put((byte)(buffer.get() ^ maskskey[i % 4]));
            }
        } else {
            payload.put(buffer.array(), buffer.position(), payload.limit());
            buffer.position(buffer.position() + payload.limit());
        }
        FramedataImpl1 frame = FramedataImpl1.get(optcode);
        frame.setFin(FIN);
        frame.setRSV1(rsv1);
        frame.setRSV2(rsv2);
        frame.setRSV3(rsv3);
        payload.flip();
        frame.setPayload(payload);
        this.getExtension().isFrameValid(frame);
        this.getExtension().decodeFrame(frame);
        if (WebSocketImpl.DEBUG) {
            System.out.println("afterDecoding(" + frame.getPayloadData().remaining() + "): {" + (frame.getPayloadData().remaining() > 1000 ? "too big to display" : new String(frame.getPayloadData().array())) + '}');
        }
        frame.isValid();
        return frame;
    }

    @Override
    public List<Framedata> translateFrame(ByteBuffer buffer) throws InvalidDataException {
        Framedata cur;
        LinkedList<Framedata> frames;
        do {
            frames = new LinkedList<Framedata>();
            if (this.incompleteframe == null) break;
            try {
                buffer.mark();
                int available_next_byte_count = buffer.remaining();
                int expected_next_byte_count = this.incompleteframe.remaining();
                if (expected_next_byte_count > available_next_byte_count) {
                    this.incompleteframe.put(buffer.array(), buffer.position(), available_next_byte_count);
                    buffer.position(buffer.position() + available_next_byte_count);
                    return Collections.emptyList();
                }
                this.incompleteframe.put(buffer.array(), buffer.position(), expected_next_byte_count);
                buffer.position(buffer.position() + expected_next_byte_count);
                cur = this.translateSingleFrame((ByteBuffer)this.incompleteframe.duplicate().position(0));
                frames.add(cur);
                this.incompleteframe = null;
            }
            catch (IncompleteException e) {
                ByteBuffer extendedframe = ByteBuffer.allocate(this.checkAlloc(e.getPreferredSize()));
                assert (extendedframe.limit() > this.incompleteframe.limit());
                this.incompleteframe.rewind();
                extendedframe.put(this.incompleteframe);
                this.incompleteframe = extendedframe;
                continue;
            }
            break;
        } while (true);
        while (buffer.hasRemaining()) {
            buffer.mark();
            try {
                cur = this.translateSingleFrame(buffer);
                frames.add(cur);
            }
            catch (IncompleteException e) {
                buffer.reset();
                int pref = e.getPreferredSize();
                this.incompleteframe = ByteBuffer.allocate(this.checkAlloc(pref));
                this.incompleteframe.put(buffer);
                break;
            }
        }
        return frames;
    }

    @Override
    public List<Framedata> createFrames(ByteBuffer binary, boolean mask) {
        BinaryFrame curframe = new BinaryFrame();
        curframe.setPayload(binary);
        curframe.setTransferemasked(mask);
        try {
            curframe.isValid();
        }
        catch (InvalidDataException e) {
            throw new NotSendableException(e);
        }
        return Collections.singletonList(curframe);
    }

    @Override
    public List<Framedata> createFrames(String text, boolean mask) {
        TextFrame curframe = new TextFrame();
        curframe.setPayload(ByteBuffer.wrap(Charsetfunctions.utf8Bytes(text)));
        curframe.setTransferemasked(mask);
        try {
            curframe.isValid();
        }
        catch (InvalidDataException e) {
            throw new NotSendableException(e);
        }
        return Collections.singletonList(curframe);
    }

    @Override
    public void reset() {
        this.incompleteframe = null;
        if (this.extension != null) {
            this.extension.reset();
        }
        this.extension = new DefaultExtension();
        this.protocol = null;
    }

    private String getServerTime() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        return dateFormat.format(calendar.getTime());
    }

    private String generateFinalKey(String in) {
        MessageDigest sh1;
        String seckey = in.trim();
        String acc = seckey + "258EAFA5-E914-47DA-95CA-C5AB0DC85B11";
        try {
            sh1 = MessageDigest.getInstance("SHA1");
        }
        catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
        return Base64.encodeBytes(sh1.digest(acc.getBytes()));
    }

    private byte[] toByteArray(long val, int bytecount) {
        byte[] buffer = new byte[bytecount];
        int highest = 8 * bytecount - 8;
        for (int i = 0; i < bytecount; ++i) {
            buffer[i] = (byte)(val >>> highest - 8 * i);
        }
        return buffer;
    }

    private byte fromOpcode(Framedata.Opcode opcode) {
        if (opcode == Framedata.Opcode.CONTINUOUS) {
            return 0;
        }
        if (opcode == Framedata.Opcode.TEXT) {
            return 1;
        }
        if (opcode == Framedata.Opcode.BINARY) {
            return 2;
        }
        if (opcode == Framedata.Opcode.CLOSING) {
            return 8;
        }
        if (opcode == Framedata.Opcode.PING) {
            return 9;
        }
        if (opcode == Framedata.Opcode.PONG) {
            return 10;
        }
        throw new IllegalArgumentException("Don't know how to handle " + opcode.toString());
    }

    private Framedata.Opcode toOpcode(byte opcode) throws InvalidFrameException {
        switch (opcode) {
            case 0: {
                return Framedata.Opcode.CONTINUOUS;
            }
            case 1: {
                return Framedata.Opcode.TEXT;
            }
            case 2: {
                return Framedata.Opcode.BINARY;
            }
            case 8: {
                return Framedata.Opcode.CLOSING;
            }
            case 9: {
                return Framedata.Opcode.PING;
            }
            case 10: {
                return Framedata.Opcode.PONG;
            }
        }
        throw new InvalidFrameException("Unknown opcode " + (short)opcode);
    }

    @Override
    public void processFrame(WebSocketImpl webSocketImpl, Framedata frame) throws InvalidDataException {
        Framedata.Opcode curop = frame.getOpcode();
        if (curop == Framedata.Opcode.CLOSING) {
            int code = 1005;
            String reason = "";
            if (frame instanceof CloseFrame) {
                CloseFrame cf = (CloseFrame)frame;
                code = cf.getCloseCode();
                reason = cf.getMessage();
            }
            if (webSocketImpl.getReadyState() == WebSocket.READYSTATE.CLOSING) {
                webSocketImpl.closeConnection(code, reason, true);
            } else if (this.getCloseHandshakeType() == Draft.CloseHandshakeType.TWOWAY) {
                webSocketImpl.close(code, reason, true);
            } else {
                webSocketImpl.flushAndClose(code, reason, false);
            }
        } else if (curop == Framedata.Opcode.PING) {
            webSocketImpl.getWebSocketListener().onWebsocketPing(webSocketImpl, frame);
        } else if (curop == Framedata.Opcode.PONG) {
            webSocketImpl.updateLastPong();
            webSocketImpl.getWebSocketListener().onWebsocketPong(webSocketImpl, frame);
        } else if (!frame.isFin() || curop == Framedata.Opcode.CONTINUOUS) {
            if (curop != Framedata.Opcode.CONTINUOUS) {
                if (this.current_continuous_frame != null) {
                    throw new InvalidDataException(1002, "Previous continuous frame sequence not completed.");
                }
                this.current_continuous_frame = frame;
                this.byteBufferList.add(frame.getPayloadData());
            } else if (frame.isFin()) {
                if (this.current_continuous_frame == null) {
                    throw new InvalidDataException(1002, "Continuous frame sequence was not started.");
                }
                this.byteBufferList.add(frame.getPayloadData());
                if (this.current_continuous_frame.getOpcode() == Framedata.Opcode.TEXT) {
                    ((FramedataImpl1)this.current_continuous_frame).setPayload(this.getPayloadFromByteBufferList());
                    ((FramedataImpl1)this.current_continuous_frame).isValid();
                    try {
                        webSocketImpl.getWebSocketListener().onWebsocketMessage((WebSocket)webSocketImpl, Charsetfunctions.stringUtf8(this.current_continuous_frame.getPayloadData()));
                    }
                    catch (RuntimeException e) {
                        webSocketImpl.getWebSocketListener().onWebsocketError(webSocketImpl, e);
                    }
                } else if (this.current_continuous_frame.getOpcode() == Framedata.Opcode.BINARY) {
                    ((FramedataImpl1)this.current_continuous_frame).setPayload(this.getPayloadFromByteBufferList());
                    ((FramedataImpl1)this.current_continuous_frame).isValid();
                    try {
                        webSocketImpl.getWebSocketListener().onWebsocketMessage((WebSocket)webSocketImpl, this.current_continuous_frame.getPayloadData());
                    }
                    catch (RuntimeException e) {
                        webSocketImpl.getWebSocketListener().onWebsocketError(webSocketImpl, e);
                    }
                }
                this.current_continuous_frame = null;
                this.byteBufferList.clear();
            } else if (this.current_continuous_frame == null) {
                throw new InvalidDataException(1002, "Continuous frame sequence was not started.");
            }
            if (curop == Framedata.Opcode.TEXT && !Charsetfunctions.isValidUTF8(frame.getPayloadData())) {
                throw new InvalidDataException(1007);
            }
            if (curop == Framedata.Opcode.CONTINUOUS && this.current_continuous_frame != null) {
                this.byteBufferList.add(frame.getPayloadData());
            }
        } else {
            if (this.current_continuous_frame != null) {
                throw new InvalidDataException(1002, "Continuous frame sequence not completed.");
            }
            if (curop == Framedata.Opcode.TEXT) {
                try {
                    webSocketImpl.getWebSocketListener().onWebsocketMessage((WebSocket)webSocketImpl, Charsetfunctions.stringUtf8(frame.getPayloadData()));
                }
                catch (RuntimeException e) {
                    webSocketImpl.getWebSocketListener().onWebsocketError(webSocketImpl, e);
                }
            } else if (curop == Framedata.Opcode.BINARY) {
                try {
                    webSocketImpl.getWebSocketListener().onWebsocketMessage((WebSocket)webSocketImpl, frame.getPayloadData());
                }
                catch (RuntimeException e) {
                    webSocketImpl.getWebSocketListener().onWebsocketError(webSocketImpl, e);
                }
            } else {
                throw new InvalidDataException(1002, "non control or continious frame expected");
            }
        }
    }

    @Override
    public Draft.CloseHandshakeType getCloseHandshakeType() {
        return Draft.CloseHandshakeType.TWOWAY;
    }

    @Override
    public String toString() {
        String result = super.toString();
        if (this.getExtension() != null) {
            result = result + " extension: " + this.getExtension().toString();
        }
        if (this.getProtocol() != null) {
            result = result + " protocol: " + this.getProtocol().toString();
        }
        return result;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        Draft_6455 that = (Draft_6455)o;
        if (this.extension != null ? !this.extension.equals(that.extension) : that.extension != null) {
            return false;
        }
        return this.protocol != null ? this.protocol.equals(that.protocol) : that.protocol == null;
    }

    public int hashCode() {
        int result = this.extension != null ? this.extension.hashCode() : 0;
        result = 31 * result + (this.protocol != null ? this.protocol.hashCode() : 0);
        return result;
    }

    private ByteBuffer getPayloadFromByteBufferList() throws LimitExedeedException {
        long totalSize = 0L;
        for (ByteBuffer buffer : this.byteBufferList) {
            totalSize += (long)buffer.limit();
        }
        if (totalSize > Integer.MAX_VALUE) {
            throw new LimitExedeedException("Payloadsize is to big...");
        }
        ByteBuffer resultingByteBuffer = ByteBuffer.allocate((int)totalSize);
        for (ByteBuffer buffer : this.byteBufferList) {
            resultingByteBuffer.put(buffer);
        }
        resultingByteBuffer.flip();
        return resultingByteBuffer;
    }
}

