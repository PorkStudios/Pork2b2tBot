/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec.http2;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufHolder;
import io.netty.handler.codec.http2.Http2Frame;

public interface Http2PingFrame
extends Http2Frame,
ByteBufHolder {
    public boolean ack();

    @Override
    public ByteBuf content();
}

