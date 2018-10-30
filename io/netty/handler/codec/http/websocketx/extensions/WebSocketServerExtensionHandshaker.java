/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec.http.websocketx.extensions;

import io.netty.handler.codec.http.websocketx.extensions.WebSocketExtensionData;
import io.netty.handler.codec.http.websocketx.extensions.WebSocketServerExtension;

public interface WebSocketServerExtensionHandshaker {
    public WebSocketServerExtension handshakeExtension(WebSocketExtensionData var1);
}

