/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec.http.websocketx.extensions;

import io.netty.handler.codec.http.websocketx.extensions.WebSocketExtension;
import io.netty.handler.codec.http.websocketx.extensions.WebSocketExtensionData;

public interface WebSocketServerExtension
extends WebSocketExtension {
    public WebSocketExtensionData newReponseData();
}

