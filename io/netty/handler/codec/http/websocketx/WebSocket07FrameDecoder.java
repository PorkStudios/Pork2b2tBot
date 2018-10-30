/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec.http.websocketx;

import io.netty.handler.codec.http.websocketx.WebSocket08FrameDecoder;

public class WebSocket07FrameDecoder
extends WebSocket08FrameDecoder {
    public WebSocket07FrameDecoder(boolean expectMaskedFrames, boolean allowExtensions, int maxFramePayloadLength) {
        this(expectMaskedFrames, allowExtensions, maxFramePayloadLength, false);
    }

    public WebSocket07FrameDecoder(boolean expectMaskedFrames, boolean allowExtensions, int maxFramePayloadLength, boolean allowMaskMismatch) {
        super(expectMaskedFrames, allowExtensions, maxFramePayloadLength, allowMaskMismatch);
    }
}

