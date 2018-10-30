/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec.spdy;

import io.netty.handler.codec.spdy.SpdyFrame;
import io.netty.handler.codec.spdy.SpdySessionStatus;

public interface SpdyGoAwayFrame
extends SpdyFrame {
    public int lastGoodStreamId();

    public SpdyGoAwayFrame setLastGoodStreamId(int var1);

    public SpdySessionStatus status();

    public SpdyGoAwayFrame setStatus(SpdySessionStatus var1);
}

