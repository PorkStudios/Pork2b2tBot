/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec.http2;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.http2.Http2Flags;
import io.netty.handler.codec.http2.Http2FrameWriter;
import io.netty.handler.codec.http2.Http2Headers;
import io.netty.handler.codec.http2.Http2Settings;
import io.netty.util.internal.ObjectUtil;

public class DecoratingHttp2FrameWriter
implements Http2FrameWriter {
    private final Http2FrameWriter delegate;

    public DecoratingHttp2FrameWriter(Http2FrameWriter delegate) {
        this.delegate = ObjectUtil.checkNotNull(delegate, "delegate");
    }

    @Override
    public ChannelFuture writeData(ChannelHandlerContext ctx, int streamId, ByteBuf data, int padding, boolean endStream, ChannelPromise promise) {
        return this.delegate.writeData(ctx, streamId, data, padding, endStream, promise);
    }

    @Override
    public ChannelFuture writeHeaders(ChannelHandlerContext ctx, int streamId, Http2Headers headers, int padding, boolean endStream, ChannelPromise promise) {
        return this.delegate.writeHeaders(ctx, streamId, headers, padding, endStream, promise);
    }

    @Override
    public ChannelFuture writeHeaders(ChannelHandlerContext ctx, int streamId, Http2Headers headers, int streamDependency, short weight, boolean exclusive, int padding, boolean endStream, ChannelPromise promise) {
        return this.delegate.writeHeaders(ctx, streamId, headers, streamDependency, weight, exclusive, padding, endStream, promise);
    }

    @Override
    public ChannelFuture writePriority(ChannelHandlerContext ctx, int streamId, int streamDependency, short weight, boolean exclusive, ChannelPromise promise) {
        return this.delegate.writePriority(ctx, streamId, streamDependency, weight, exclusive, promise);
    }

    @Override
    public ChannelFuture writeRstStream(ChannelHandlerContext ctx, int streamId, long errorCode, ChannelPromise promise) {
        return this.delegate.writeRstStream(ctx, streamId, errorCode, promise);
    }

    @Override
    public ChannelFuture writeSettings(ChannelHandlerContext ctx, Http2Settings settings, ChannelPromise promise) {
        return this.delegate.writeSettings(ctx, settings, promise);
    }

    @Override
    public ChannelFuture writeSettingsAck(ChannelHandlerContext ctx, ChannelPromise promise) {
        return this.delegate.writeSettingsAck(ctx, promise);
    }

    @Override
    public ChannelFuture writePing(ChannelHandlerContext ctx, boolean ack, ByteBuf data, ChannelPromise promise) {
        return this.delegate.writePing(ctx, ack, data, promise);
    }

    @Override
    public ChannelFuture writePushPromise(ChannelHandlerContext ctx, int streamId, int promisedStreamId, Http2Headers headers, int padding, ChannelPromise promise) {
        return this.delegate.writePushPromise(ctx, streamId, promisedStreamId, headers, padding, promise);
    }

    @Override
    public ChannelFuture writeGoAway(ChannelHandlerContext ctx, int lastStreamId, long errorCode, ByteBuf debugData, ChannelPromise promise) {
        return this.delegate.writeGoAway(ctx, lastStreamId, errorCode, debugData, promise);
    }

    @Override
    public ChannelFuture writeWindowUpdate(ChannelHandlerContext ctx, int streamId, int windowSizeIncrement, ChannelPromise promise) {
        return this.delegate.writeWindowUpdate(ctx, streamId, windowSizeIncrement, promise);
    }

    @Override
    public ChannelFuture writeFrame(ChannelHandlerContext ctx, byte frameType, int streamId, Http2Flags flags, ByteBuf payload, ChannelPromise promise) {
        return this.delegate.writeFrame(ctx, frameType, streamId, flags, payload, promise);
    }

    @Override
    public Http2FrameWriter.Configuration configuration() {
        return this.delegate.configuration();
    }

    @Override
    public void close() {
        this.delegate.close();
    }
}

