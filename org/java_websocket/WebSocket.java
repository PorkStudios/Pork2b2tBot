/*
 * Decompiled with CFR 0_132.
 */
package org.java_websocket;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.NotYetConnectedException;
import java.util.Collection;
import org.java_websocket.drafts.Draft;
import org.java_websocket.framing.Framedata;

public interface WebSocket {
    public static final int DEFAULT_PORT = 80;
    public static final int DEFAULT_WSS_PORT = 443;

    public void close(int var1, String var2);

    public void close(int var1);

    public void close();

    public void closeConnection(int var1, String var2);

    public void send(String var1) throws NotYetConnectedException;

    public void send(ByteBuffer var1) throws IllegalArgumentException, NotYetConnectedException;

    public void send(byte[] var1) throws IllegalArgumentException, NotYetConnectedException;

    public void sendFrame(Framedata var1);

    public void sendFrame(Collection<Framedata> var1);

    public void sendPing() throws NotYetConnectedException;

    public void sendFragmentedFrame(Framedata.Opcode var1, ByteBuffer var2, boolean var3);

    public boolean hasBufferedData();

    public InetSocketAddress getRemoteSocketAddress();

    public InetSocketAddress getLocalSocketAddress();

    public boolean isConnecting();

    public boolean isOpen();

    public boolean isClosing();

    public boolean isFlushAndClose();

    public boolean isClosed();

    public Draft getDraft();

    public READYSTATE getReadyState();

    public String getResourceDescriptor();

    public <T> void setAttachment(T var1);

    public <T> T getAttachment();

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum READYSTATE {
        NOT_YET_CONNECTED,
        CONNECTING,
        OPEN,
        CLOSING,
        CLOSED;
        

        private READYSTATE() {
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum Role {
        CLIENT,
        SERVER;
        

        private Role() {
        }
    }

}

