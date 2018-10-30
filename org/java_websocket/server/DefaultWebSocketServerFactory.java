/*
 * Decompiled with CFR 0_132.
 */
package org.java_websocket.server;

import java.io.IOException;
import java.nio.channels.ByteChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.List;
import org.java_websocket.WebSocket;
import org.java_websocket.WebSocketAdapter;
import org.java_websocket.WebSocketImpl;
import org.java_websocket.WebSocketListener;
import org.java_websocket.WebSocketServerFactory;
import org.java_websocket.drafts.Draft;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class DefaultWebSocketServerFactory
implements WebSocketServerFactory {
    @Override
    public WebSocketImpl createWebSocket(WebSocketAdapter a, Draft d) {
        return new WebSocketImpl((WebSocketListener)a, d);
    }

    @Override
    public WebSocketImpl createWebSocket(WebSocketAdapter a, List<Draft> d) {
        return new WebSocketImpl((WebSocketListener)a, d);
    }

    @Override
    public SocketChannel wrapChannel(SocketChannel channel, SelectionKey key) {
        return channel;
    }

    @Override
    public void close() {
    }
}

