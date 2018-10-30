/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec.http2;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.http2.Http2Flags;
import io.netty.handler.codec.http2.Http2FrameLogger;
import io.netty.handler.codec.http2.Http2FrameWriter;
import io.netty.handler.codec.http2.Http2Headers;
import io.netty.handler.codec.http2.Http2Settings;
import io.netty.util.internal.ObjectUtil;

public class Http2OutboundFrameLogger
implements Http2FrameWriter {
    private final Http2FrameWriter writer;
    private final Http2FrameLogger logger;

    public Http2OutboundFrameLogger(Http2FrameWriter writer, Http2FrameLogger logger) {
        this.writer = ObjectUtil.checkNotNull(writer, "writer");
        this.logger = ObjectUtil.checkNotNull(logger, "logger");
    }

    @Override
    public ChannelFuture writeData(ChannelHandlerContext ctx, int streamId, ByteBuf data, int padding, boolean endStream, ChannelPromise promise) {
        this.logger.logData(Http2FrameLogger.Direction.OUTBOUND, ctx, streamId, data, padding, endStream);
        return this.writer.writeData(ctx, streamId, data, padding, endStream, promise);
    }

    @Override
    public ChannelFuture writeHeaders(ChannelHandlerContext ctx, int streamId, Http2Headers headers, int padding, boolean endStream, ChannelPromise promise) {
        this.logger.logHeaders(Http2FrameLogger.Direction.OUTBOUND, ctx, streamId, headers, padding, endStream);
        return this.writer.writeHeaders(ctx, streamId, headers, padding, endStream, promise);
    }

    @Override
    public ChannelFuture writeHeaders(ChannelHandlerContext ctx, int streamId, Http2Headers headers, int streamDependency, short weight, boolean exclusive, int padding, boolean endStream, ChannelPromise promise) {
        this.logger.logHeaders(Http2FrameLogger.Direction.OUTBOUND, ctx, streamId, headers, streamDependency, weight, exclusive, padding, endStream);
        return this.writer.writeHeaders(ctx, streamId, headers, streamDependency, weight, exclusive, padding, endStream, promise);
    }

    @Override
    public ChannelFuture writePriority(ChannelHandlerContext ctx, int streamId, int streamDependency, short weight, boolean exclusive, ChannelPromise promise) {
        this.logger.logPriority(Http2FrameLogger.Direction.OUTBOUND, ctx, streamId, streamDependency, weight, exclusive);
        return this.writer.writePriority(ctx, streamId, streamDependency, weight, exclusive, promise);
    }

    @Override
    public ChannelFuture writeRstStream(ChannelHandlerContext ctx, int streamId, long errorCode, ChannelPromise promise) {
        this.logger.logRstStream(Http2FrameLogger.Direction.OUTBOUND, ctx, streamId, errorCode);
        return this.writer.writeRstStream(ctx, streamId, errorCode, promise);
    }

    @Override
    public ChannelFuture writeSettings(ChannelHandlerContext ctx, Http2Settings settings, ChannelPromise promise) {
        this.logger.logSettings(Http2FrameLogger.Direction.OUTBOUND, ctx, settings);
        return this.writer.writeSettings(ctx, settings, promise);
    }

    @Override
    public ChannelFuture writeSettingsAck(ChannelHandlerContext ctx, ChannelPromise promise) {
        this.logger.logSettingsAck(Http2FrameLogger.Direction.OUTBOUND, ctx);
        return this.writer.writeSettingsAck(ctx, promise);
    }

    @Override
    public ChannelFuture writePing(ChannelHandlerContext ctx, boolean ack, ByteBuf data, ChannelPromise promise) {
        if (ack) {
            this.logger.logPingAck(Http2FrameLogger.Direction.OUTBOUND, ctx, data);
        } else {
            this.logger.logPing(Http2FrameLogger.Direction.OUTBOUND, ctx, data);
        }
        return this.writer.writePing(ctx, ack, data, promise);
    }

    @Override
    public ChannelFuture writePushPromise(ChannelHandlerContext ctx, int streamId, int promisedStreamId, Http2Headers headers, int padding, ChannelPromise promise) {
        this.logger.logPushPromise(Http2FrameLogger.Direction.OUTBOUND, ctx, streamId, promisedStreamId, headers, padding);
        return this.writer.writePushPromise(ctx, streamId, promisedStreamId, headers, padding, promise);
    }

    @Override
    public ChannelFuture writeGoAway(ChannelHandlerContext ctx, int lastStreamId, long errorCode, ByteBuf debugData, ChannelPromise promise) {
        this.logger.logGoAway(Http2FrameLogger.Direction.OUTBOUND, ctx, lastStreamId, errorCode, debugData);
        return this.writer.writeGoAway(ctx, lastStreamId, errorCode, debugData, promise);
    }

    @Override
    public ChannelFuture writeWindowUpdate(ChannelHandlerContext ctx, int streamId, int windowSizeIncrement, ChannelPromise promise) {
        this.logger.logWindowsUpdate(Http2FrameLogger.Direction.OUTBOUND, ctx, streamId, windowSizeIncrement);
        return this.writer.writeWindowUpdate(ctx, streamId, windowSizeIncrement, promise);
    }

    @Override
    public ChannelFuture writeFrame(ChannelHandlerContext ctx, byte frameType, int streamId, Http2Flags flags, ByteBuf payload, ChannelPromise promise) {
        this.logger.logUnknownFrame(Http2FrameLogger.Direction.OUTBOUND, ctx, frameType, streamId, flags, payload);
        return this.writer.writeFrame(ctx, frameType, streamId, flags, payload, promise);
    }

    @Override
    public void close() {
        this.writer.close();
    }

    @Override
    public Http2FrameWriter.Configuration configuration() {
        return this.writer.configuration();
    }
}

