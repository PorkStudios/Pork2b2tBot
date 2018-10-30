/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec.http2;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.http2.Http2Stream;

public interface Http2LifecycleManager {
    public void closeStreamLocal(Http2Stream var1, ChannelFuture var2);

    public void closeStreamRemote(Http2Stream var1, ChannelFuture var2);

    public void closeStream(Http2Stream var1, ChannelFuture var2);

    public ChannelFuture resetStream(ChannelHandlerContext var1, int var2, long var3, ChannelPromise var5);

    public ChannelFuture goAway(ChannelHandlerContext var1, int var2, long var3, ByteBuf var5, ChannelPromise var6);

    public void onError(ChannelHandlerContext var1, Throwable var2);
}

