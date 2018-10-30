/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec.spdy;

import io.netty.handler.codec.spdy.SpdyHeadersFrame;

public interface SpdySynStreamFrame
extends SpdyHeadersFrame {
    public int associatedStreamId();

    public SpdySynStreamFrame setAssociatedStreamId(int var1);

    public byte priority();

    public SpdySynStreamFrame setPriority(byte var1);

    public boolean isUnidirectional();

    public SpdySynStreamFrame setUnidirectional(boolean var1);

    @Override
    public SpdySynStreamFrame setStreamId(int var1);

    @Override
    public SpdySynStreamFrame setLast(boolean var1);

    @Override
    public SpdySynStreamFrame setInvalid();
}

