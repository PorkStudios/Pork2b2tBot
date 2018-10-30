/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec.http2;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http2.Http2Exception;
import io.netty.handler.codec.http2.Http2Flags;
import io.netty.handler.codec.http2.Http2FrameListener;
import io.netty.handler.codec.http2.Http2Headers;
import io.netty.handler.codec.http2.Http2Settings;
import io.netty.util.internal.ObjectUtil;

public class Http2FrameListenerDecorator
implements Http2FrameListener {
    protected final Http2FrameListener listener;

    public Http2FrameListenerDecorator(Http2FrameListener listener) {
        this.listener = ObjectUtil.checkNotNull(listener, "listener");
    }

    @Override
    public int onDataRead(ChannelHandlerContext ctx, int streamId, ByteBuf data, int padding, boolean endOfStream) throws Http2Exception {
        return this.listener.onDataRead(ctx, streamId, data, padding, endOfStream);
    }

    @Override
    public void onHeadersRead(ChannelHandlerContext ctx, int streamId, Http2Headers headers, int padding, boolean endStream) throws Http2Exception {
        this.listener.onHeadersRead(ctx, streamId, headers, padding, endStream);
    }

    @Override
    public void onHeadersRead(ChannelHandlerContext ctx, int streamId, Http2Headers headers, int streamDependency, short weight, boolean exclusive, int padding, boolean endStream) throws Http2Exception {
        this.listener.onHeadersRead(ctx, streamId, headers, streamDependency, weight, exclusive, padding, endStream);
    }

    @Override
    public void onPriorityRead(ChannelHandlerContext ctx, int streamId, int streamDependency, short weight, boolean exclusive) throws Http2Exception {
        this.listener.onPriorityRead(ctx, streamId, streamDependency, weight, exclusive);
    }

    @Override
    public void onRstStreamRead(ChannelHandlerContext ctx, int streamId, long errorCode) throws Http2Exception {
        this.listener.onRstStreamRead(ctx, streamId, errorCode);
    }

    @Override
    public void onSettingsAckRead(ChannelHandlerContext ctx) throws Http2Exception {
        this.listener.onSettingsAckRead(ctx);
    }

    @Override
    public void onSettingsRead(ChannelHandlerContext ctx, Http2Settings settings) throws Http2Exception {
        this.listener.onSettingsRead(ctx, settings);
    }

    @Override
    public void onPingRead(ChannelHandlerContext ctx, ByteBuf data) throws Http2Exception {
        this.listener.onPingRead(ctx, data);
    }

    @Override
    public void onPingAckRead(ChannelHandlerContext ctx, ByteBuf data) throws Http2Exception {
        this.listener.onPingAckRead(ctx, data);
    }

    @Override
    public void onPushPromiseRead(ChannelHandlerContext ctx, int streamId, int promisedStreamId, Http2Headers headers, int padding) throws Http2Exception {
        this.listener.onPushPromiseRead(ctx, streamId, promisedStreamId, headers, padding);
    }

    @Override
    public void onGoAwayRead(ChannelHandlerContext ctx, int lastStreamId, long errorCode, ByteBuf debugData) throws Http2Exception {
        this.listener.onGoAwayRead(ctx, lastStreamId, errorCode, debugData);
    }

    @Override
    public void onWindowUpdateRead(ChannelHandlerContext ctx, int streamId, int windowSizeIncrement) throws Http2Exception {
        this.listener.onWindowUpdateRead(ctx, streamId, windowSizeIncrement);
    }

    @Override
    public void onUnknownFrame(ChannelHandlerContext ctx, byte frameType, int streamId, Http2Flags flags, ByteBuf payload) throws Http2Exception {
        this.listener.onUnknownFrame(ctx, frameType, streamId, flags, payload);
    }
}

