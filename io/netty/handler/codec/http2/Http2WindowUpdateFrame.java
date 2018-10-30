/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec.http2;

import io.netty.handler.codec.http2.Http2StreamFrame;

public interface Http2WindowUpdateFrame
extends Http2StreamFrame {
    public int windowSizeIncrement();
}

