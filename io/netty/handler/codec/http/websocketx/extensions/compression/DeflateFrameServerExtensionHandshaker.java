/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec.http.websocketx.extensions.compression;

import io.netty.handler.codec.http.websocketx.extensions.WebSocketExtensionData;
import io.netty.handler.codec.http.websocketx.extensions.WebSocketExtensionDecoder;
import io.netty.handler.codec.http.websocketx.extensions.WebSocketExtensionEncoder;
import io.netty.handler.codec.http.websocketx.extensions.WebSocketServerExtension;
import io.netty.handler.codec.http.websocketx.extensions.WebSocketServerExtensionHandshaker;
import io.netty.handler.codec.http.websocketx.extensions.compression.PerFrameDeflateDecoder;
import io.netty.handler.codec.http.websocketx.extensions.compression.PerFrameDeflateEncoder;
import java.util.Collections;
import java.util.Map;

public final class DeflateFrameServerExtensionHandshaker
implements WebSocketServerExtensionHandshaker {
    static final String X_WEBKIT_DEFLATE_FRAME_EXTENSION = "x-webkit-deflate-frame";
    static final String DEFLATE_FRAME_EXTENSION = "deflate-frame";
    private final int compressionLevel;

    public DeflateFrameServerExtensionHandshaker() {
        this(6);
    }

    public DeflateFrameServerExtensionHandshaker(int compressionLevel) {
        if (compressionLevel < 0 || compressionLevel > 9) {
            throw new IllegalArgumentException("compressionLevel: " + compressionLevel + " (expected: 0-9)");
        }
        this.compressionLevel = compressionLevel;
    }

    @Override
    public WebSocketServerExtension handshakeExtension(WebSocketExtensionData extensionData) {
        if (!X_WEBKIT_DEFLATE_FRAME_EXTENSION.equals(extensionData.name()) && !DEFLATE_FRAME_EXTENSION.equals(extensionData.name())) {
            return null;
        }
        if (extensionData.parameters().isEmpty()) {
            return new DeflateFrameServerExtension(this.compressionLevel, extensionData.name());
        }
        return null;
    }

    private static class DeflateFrameServerExtension
    implements WebSocketServerExtension {
        private final String extensionName;
        private final int compressionLevel;

        public DeflateFrameServerExtension(int compressionLevel, String extensionName) {
            this.extensionName = extensionName;
            this.compressionLevel = compressionLevel;
        }

        @Override
        public int rsv() {
            return 4;
        }

        @Override
        public WebSocketExtensionEncoder newExtensionEncoder() {
            return new PerFrameDeflateEncoder(this.compressionLevel, 15, false);
        }

        @Override
        public WebSocketExtensionDecoder newExtensionDecoder() {
            return new PerFrameDeflateDecoder(false);
        }

        @Override
        public WebSocketExtensionData newReponseData() {
            return new WebSocketExtensionData(this.extensionName, Collections.<String, String>emptyMap());
        }
    }

}

