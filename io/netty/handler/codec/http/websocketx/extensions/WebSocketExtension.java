/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec.http.websocketx.extensions;

import io.netty.handler.codec.http.websocketx.extensions.WebSocketExtensionDecoder;
import io.netty.handler.codec.http.websocketx.extensions.WebSocketExtensionEncoder;

public interface WebSocketExtension {
    public static final int RSV1 = 4;
    public static final int RSV2 = 2;
    public static final int RSV3 = 1;

    public int rsv();

    public WebSocketExtensionEncoder newExtensionEncoder();

    public WebSocketExtensionDecoder newExtensionDecoder();
}

