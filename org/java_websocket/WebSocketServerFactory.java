/*
 * Decompiled with CFR 0_132.
 */
package org.java_websocket;

import java.io.IOException;
import java.nio.channels.ByteChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.List;
import org.java_websocket.WebSocketAdapter;
import org.java_websocket.WebSocketFactory;
import org.java_websocket.WebSocketImpl;
import org.java_websocket.drafts.Draft;

public interface WebSocketServerFactory
extends WebSocketFactory {
    @Override
    public WebSocketImpl createWebSocket(WebSocketAdapter var1, Draft var2);

    @Override
    public WebSocketImpl createWebSocket(WebSocketAdapter var1, List<Draft> var2);

    public ByteChannel wrapChannel(SocketChannel var1, SelectionKey var2) throws IOException;

    public void close();
}

