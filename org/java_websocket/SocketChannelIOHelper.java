/*
 * Decompiled with CFR 0_132.
 */
package org.java_websocket;

import java.io.IOException;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;
import java.util.concurrent.BlockingQueue;
import org.java_websocket.WebSocket;
import org.java_websocket.WebSocketImpl;
import org.java_websocket.WrappedByteChannel;
import org.java_websocket.drafts.Draft;

public class SocketChannelIOHelper {
    public static boolean read(ByteBuffer buf, WebSocketImpl ws, ByteChannel channel) throws IOException {
        buf.clear();
        int read = channel.read(buf);
        buf.flip();
        if (read == -1) {
            ws.eot();
            return false;
        }
        return read != 0;
    }

    public static boolean readMore(ByteBuffer buf, WebSocketImpl ws, WrappedByteChannel channel) throws IOException {
        buf.clear();
        int read = channel.readMore(buf);
        buf.flip();
        if (read == -1) {
            ws.eot();
            return false;
        }
        return channel.isNeedRead();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static boolean batch(WebSocketImpl ws, ByteChannel sockchannel) throws IOException {
        ByteBuffer buffer = ws.outQueue.peek();
        WrappedByteChannel c = null;
        if (buffer == null) {
            if (sockchannel instanceof WrappedByteChannel && (c = (WrappedByteChannel)sockchannel).isNeedWrite()) {
                c.writeMore();
            }
        } else {
            do {
                sockchannel.write(buffer);
                if (buffer.remaining() > 0) {
                    return false;
                }
                ws.outQueue.poll();
            } while ((buffer = ws.outQueue.peek()) != null);
        }
        if (ws != null && ws.outQueue.isEmpty() && ws.isFlushAndClose() && ws.getDraft() != null && ws.getDraft().getRole() != null && ws.getDraft().getRole() == WebSocket.Role.SERVER) {
            WebSocketImpl webSocketImpl = ws;
            synchronized (webSocketImpl) {
                ws.closeConnection();
            }
        }
        return c == null || !((WrappedByteChannel)sockchannel).isNeedWrite();
    }
}

