/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec.http2;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufHolder;
import io.netty.handler.codec.http2.Http2Flags;
import io.netty.handler.codec.http2.Http2Frame;
import io.netty.handler.codec.http2.Http2FrameStream;

public interface Http2UnknownFrame
extends Http2Frame,
ByteBufHolder {
    public Http2FrameStream stream();

    public Http2UnknownFrame stream(Http2FrameStream var1);

    public byte frameType();

    public Http2Flags flags();

    @Override
    public Http2UnknownFrame copy();

    @Override
    public Http2UnknownFrame duplicate();

    @Override
    public Http2UnknownFrame retainedDuplicate();

    @Override
    public Http2UnknownFrame replace(ByteBuf var1);

    @Override
    public Http2UnknownFrame retain();

    @Override
    public Http2UnknownFrame retain(int var1);

    @Override
    public Http2UnknownFrame touch();

    @Override
    public Http2UnknownFrame touch(Object var1);
}

