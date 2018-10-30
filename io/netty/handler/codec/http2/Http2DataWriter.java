/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec.http2;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;

public interface Http2DataWriter {
    public ChannelFuture writeData(ChannelHandlerContext var1, int var2, ByteBuf var3, int var4, boolean var5, ChannelPromise var6);
}

