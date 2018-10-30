/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec.http2;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http2.Http2Exception;
import io.netty.handler.codec.http2.Http2Flags;
import io.netty.handler.codec.http2.Http2FrameListener;
import io.netty.handler.codec.http2.Http2FrameLogger;
import io.netty.handler.codec.http2.Http2FrameReader;
import io.netty.handler.codec.http2.Http2Headers;
import io.netty.handler.codec.http2.Http2Settings;
import io.netty.util.internal.ObjectUtil;

public class Http2InboundFrameLogger
implements Http2FrameReader {
    private final Http2FrameReader reader;
    private final Http2FrameLogger logger;

    public Http2InboundFrameLogger(Http2FrameReader reader, Http2FrameLogger logger) {
        this.reader = ObjectUtil.checkNotNull(reader, "reader");
        this.logger = ObjectUtil.checkNotNull(logger, "logger");
    }

    @Override
    public void readFrame(ChannelHandlerContext ctx, ByteBuf input, final Http2FrameListener listener) throws Http2Exception {
        this.reader.readFrame(ctx, input, new Http2FrameListener(){

            @Override
            public int onDataRead(ChannelHandlerContext ctx, int streamId, ByteBuf data, int padding, boolean endOfStream) throws Http2Exception {
                Http2InboundFrameLogger.this.logger.logData(Http2FrameLogger.Direction.INBOUND, ctx, streamId, data, padding, endOfStream);
                return listener.onDataRead(ctx, streamId, data, padding, endOfStream);
            }

            @Override
            public void onHeadersRead(ChannelHandlerContext ctx, int streamId, Http2Headers headers, int padding, boolean endStream) throws Http2Exception {
                Http2InboundFrameLogger.this.logger.logHeaders(Http2FrameLogger.Direction.INBOUND, ctx, streamId, headers, padding, endStream);
                listener.onHeadersRead(ctx, streamId, headers, padding, endStream);
            }

            @Override
            public void onHeadersRead(ChannelHandlerContext ctx, int streamId, Http2Headers headers, int streamDependency, short weight, boolean exclusive, int padding, boolean endStream) throws Http2Exception {
                Http2InboundFrameLogger.this.logger.logHeaders(Http2FrameLogger.Direction.INBOUND, ctx, streamId, headers, streamDependency, weight, exclusive, padding, endStream);
                listener.onHeadersRead(ctx, streamId, headers, streamDependency, weight, exclusive, padding, endStream);
            }

            @Override
            public void onPriorityRead(ChannelHandlerContext ctx, int streamId, int streamDependency, short weight, boolean exclusive) throws Http2Exception {
                Http2InboundFrameLogger.this.logger.logPriority(Http2FrameLogger.Direction.INBOUND, ctx, streamId, streamDependency, weight, exclusive);
                listener.onPriorityRead(ctx, streamId, streamDependency, weight, exclusive);
            }

            @Override
            public void onRstStreamRead(ChannelHandlerContext ctx, int streamId, long errorCode) throws Http2Exception {
                Http2InboundFrameLogger.this.logger.logRstStream(Http2FrameLogger.Direction.INBOUND, ctx, streamId, errorCode);
                listener.onRstStreamRead(ctx, streamId, errorCode);
            }

            @Override
            public void onSettingsAckRead(ChannelHandlerContext ctx) throws Http2Exception {
                Http2InboundFrameLogger.this.logger.logSettingsAck(Http2FrameLogger.Direction.INBOUND, ctx);
                listener.onSettingsAckRead(ctx);
            }

            @Override
            public void onSettingsRead(ChannelHandlerContext ctx, Http2Settings settings) throws Http2Exception {
                Http2InboundFrameLogger.this.logger.logSettings(Http2FrameLogger.Direction.INBOUND, ctx, settings);
                listener.onSettingsRead(ctx, settings);
            }

            @Override
            public void onPingRead(ChannelHandlerContext ctx, ByteBuf data) throws Http2Exception {
                Http2InboundFrameLogger.this.logger.logPing(Http2FrameLogger.Direction.INBOUND, ctx, data);
                listener.onPingRead(ctx, data);
            }

            @Override
            public void onPingAckRead(ChannelHandlerContext ctx, ByteBuf data) throws Http2Exception {
                Http2InboundFrameLogger.this.logger.logPingAck(Http2FrameLogger.Direction.INBOUND, ctx, data);
                listener.onPingAckRead(ctx, data);
            }

            @Override
            public void onPushPromiseRead(ChannelHandlerContext ctx, int streamId, int promisedStreamId, Http2Headers headers, int padding) throws Http2Exception {
                Http2InboundFrameLogger.this.logger.logPushPromise(Http2FrameLogger.Direction.INBOUND, ctx, streamId, promisedStreamId, headers, padding);
                listener.onPushPromiseRead(ctx, streamId, promisedStreamId, headers, padding);
            }

            @Override
            public void onGoAwayRead(ChannelHandlerContext ctx, int lastStreamId, long errorCode, ByteBuf debugData) throws Http2Exception {
                Http2InboundFrameLogger.this.logger.logGoAway(Http2FrameLogger.Direction.INBOUND, ctx, lastStreamId, errorCode, debugData);
                listener.onGoAwayRead(ctx, lastStreamId, errorCode, debugData);
            }

            @Override
            public void onWindowUpdateRead(ChannelHandlerContext ctx, int streamId, int windowSizeIncrement) throws Http2Exception {
                Http2InboundFrameLogger.this.logger.logWindowsUpdate(Http2FrameLogger.Direction.INBOUND, ctx, streamId, windowSizeIncrement);
                listener.onWindowUpdateRead(ctx, streamId, windowSizeIncrement);
            }

            @Override
            public void onUnknownFrame(ChannelHandlerContext ctx, byte frameType, int streamId, Http2Flags flags, ByteBuf payload) throws Http2Exception {
                Http2InboundFrameLogger.this.logger.logUnknownFrame(Http2FrameLogger.Direction.INBOUND, ctx, frameType, streamId, flags, payload);
                listener.onUnknownFrame(ctx, frameType, streamId, flags, payload);
            }
        });
    }

    @Override
    public void close() {
        this.reader.close();
    }

    @Override
    public Http2FrameReader.Configuration configuration() {
        return this.reader.configuration();
    }

}

