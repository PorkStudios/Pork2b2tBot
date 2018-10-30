/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec.spdy;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufHolder;
import io.netty.handler.codec.spdy.SpdyStreamFrame;

public interface SpdyDataFrame
extends ByteBufHolder,
SpdyStreamFrame {
    @Override
    public SpdyDataFrame setStreamId(int var1);

    @Override
    public SpdyDataFrame setLast(boolean var1);

    @Override
    public ByteBuf content();

    @Override
    public SpdyDataFrame copy();

    @Override
    public SpdyDataFrame duplicate();

    @Override
    public SpdyDataFrame retainedDuplicate();

    @Override
    public SpdyDataFrame replace(ByteBuf var1);

    @Override
    public SpdyDataFrame retain();

    @Override
    public SpdyDataFrame retain(int var1);

    @Override
    public SpdyDataFrame touch();

    @Override
    public SpdyDataFrame touch(Object var1);
}

