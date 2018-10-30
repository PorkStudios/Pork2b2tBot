/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec.http2;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.http2.Http2DataWriter;
import io.netty.handler.codec.http2.Http2Flags;
import io.netty.handler.codec.http2.Http2FrameSizePolicy;
import io.netty.handler.codec.http2.Http2Headers;
import io.netty.handler.codec.http2.Http2HeadersEncoder;
import io.netty.handler.codec.http2.Http2Settings;
import java.io.Closeable;

public interface Http2FrameWriter
extends Http2DataWriter,
Closeable {
    public ChannelFuture writeHeaders(ChannelHandlerContext var1, int var2, Http2Headers var3, int var4, boolean var5, ChannelPromise var6);

    public ChannelFuture writeHeaders(ChannelHandlerContext var1, int var2, Http2Headers var3, int var4, short var5, boolean var6, int var7, boolean var8, ChannelPromise var9);

    public ChannelFuture writePriority(ChannelHandlerContext var1, int var2, int var3, short var4, boolean var5, ChannelPromise var6);

    public ChannelFuture writeRstStream(ChannelHandlerContext var1, int var2, long var3, ChannelPromise var5);

    public ChannelFuture writeSettings(ChannelHandlerContext var1, Http2Settings var2, ChannelPromise var3);

    public ChannelFuture writeSettingsAck(ChannelHandlerContext var1, ChannelPromise var2);

    public ChannelFuture writePing(ChannelHandlerContext var1, boolean var2, ByteBuf var3, ChannelPromise var4);

    public ChannelFuture writePushPromise(ChannelHandlerContext var1, int var2, int var3, Http2Headers var4, int var5, ChannelPromise var6);

    public ChannelFuture writeGoAway(ChannelHandlerContext var1, int var2, long var3, ByteBuf var5, ChannelPromise var6);

    public ChannelFuture writeWindowUpdate(ChannelHandlerContext var1, int var2, int var3, ChannelPromise var4);

    public ChannelFuture writeFrame(ChannelHandlerContext var1, byte var2, int var3, Http2Flags var4, ByteBuf var5, ChannelPromise var6);

    public Configuration configuration();

    @Override
    public void close();

    public static interface Configuration {
        public Http2HeadersEncoder.Configuration headersConfiguration();

        public Http2FrameSizePolicy frameSizePolicy();
    }

}

