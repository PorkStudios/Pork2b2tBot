/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec.http.websocketx;

import io.netty.handler.codec.http.websocketx.WebSocket08FrameDecoder;

public class WebSocket13FrameDecoder
extends WebSocket08FrameDecoder {
    public WebSocket13FrameDecoder(boolean expectMaskedFrames, boolean allowExtensions, int maxFramePayloadLength) {
        this(expectMaskedFrames, allowExtensions, maxFramePayloadLength, false);
    }

    public WebSocket13FrameDecoder(boolean expectMaskedFrames, boolean allowExtensions, int maxFramePayloadLength, boolean allowMaskMismatch) {
        super(expectMaskedFrames, allowExtensions, maxFramePayloadLength, allowMaskMismatch);
    }
}

