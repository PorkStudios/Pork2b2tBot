/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec.http2;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufHolder;
import io.netty.handler.codec.http2.Http2StreamFrame;

public interface Http2DataFrame
extends Http2StreamFrame,
ByteBufHolder {
    public int padding();

    @Override
    public ByteBuf content();

    public int initialFlowControlledBytes();

    public boolean isEndStream();

    @Override
    public Http2DataFrame copy();

    @Override
    public Http2DataFrame duplicate();

    @Override
    public Http2DataFrame retainedDuplicate();

    @Override
    public Http2DataFrame replace(ByteBuf var1);

    @Override
    public Http2DataFrame retain();

    @Override
    public Http2DataFrame retain(int var1);

    @Override
    public Http2DataFrame touch();

    @Override
    public Http2DataFrame touch(Object var1);
}

