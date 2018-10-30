/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec.http2;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.handler.codec.http2.AbstractHttp2ConnectionHandlerBuilder;
import io.netty.handler.codec.http2.Http2ConnectionDecoder;
import io.netty.handler.codec.http2.Http2ConnectionEncoder;
import io.netty.handler.codec.http2.Http2ConnectionHandler;
import io.netty.handler.codec.http2.Http2FrameLogger;
import io.netty.handler.codec.http2.Http2HeadersEncoder;
import io.netty.handler.codec.http2.Http2MultiplexCodec;
import io.netty.handler.codec.http2.Http2Settings;
import io.netty.util.internal.ObjectUtil;
import java.lang.annotation.Annotation;

public class Http2MultiplexCodecBuilder
extends AbstractHttp2ConnectionHandlerBuilder<Http2MultiplexCodec, Http2MultiplexCodecBuilder> {
    final ChannelHandler childHandler;

    Http2MultiplexCodecBuilder(boolean server, ChannelHandler childHandler) {
        this.server(server);
        this.childHandler = Http2MultiplexCodecBuilder.checkSharable(ObjectUtil.checkNotNull(childHandler, "childHandler"));
    }

    private static ChannelHandler checkSharable(ChannelHandler handler) {
        if (handler instanceof ChannelHandlerAdapter && !((ChannelHandlerAdapter)handler).isSharable() && !handler.getClass().isAnnotationPresent(ChannelHandler.Sharable.class)) {
            throw new IllegalArgumentException("The handler must be Sharable");
        }
        return handler;
    }

    public static Http2MultiplexCodecBuilder forClient(ChannelHandler childHandler) {
        return new Http2MultiplexCodecBuilder(false, childHandler);
    }

    public static Http2MultiplexCodecBuilder forServer(ChannelHandler childHandler) {
        return new Http2MultiplexCodecBuilder(true, childHandler);
    }

    @Override
    public Http2Settings initialSettings() {
        return super.initialSettings();
    }

    @Override
    public Http2MultiplexCodecBuilder initialSettings(Http2Settings settings) {
        return (Http2MultiplexCodecBuilder)super.initialSettings(settings);
    }

    @Override
    public long gracefulShutdownTimeoutMillis() {
        return super.gracefulShutdownTimeoutMillis();
    }

    @Override
    public Http2MultiplexCodecBuilder gracefulShutdownTimeoutMillis(long gracefulShutdownTimeoutMillis) {
        return (Http2MultiplexCodecBuilder)super.gracefulShutdownTimeoutMillis(gracefulShutdownTimeoutMillis);
    }

    @Override
    public boolean isServer() {
        return super.isServer();
    }

    @Override
    public int maxReservedStreams() {
        return super.maxReservedStreams();
    }

    @Override
    public Http2MultiplexCodecBuilder maxReservedStreams(int maxReservedStreams) {
        return (Http2MultiplexCodecBuilder)super.maxReservedStreams(maxReservedStreams);
    }

    @Override
    public boolean isValidateHeaders() {
        return super.isValidateHeaders();
    }

    @Override
    public Http2MultiplexCodecBuilder validateHeaders(boolean validateHeaders) {
        return (Http2MultiplexCodecBuilder)super.validateHeaders(validateHeaders);
    }

    @Override
    public Http2FrameLogger frameLogger() {
        return super.frameLogger();
    }

    @Override
    public Http2MultiplexCodecBuilder frameLogger(Http2FrameLogger frameLogger) {
        return (Http2MultiplexCodecBuilder)super.frameLogger(frameLogger);
    }

    @Override
    public boolean encoderEnforceMaxConcurrentStreams() {
        return super.encoderEnforceMaxConcurrentStreams();
    }

    @Override
    public Http2MultiplexCodecBuilder encoderEnforceMaxConcurrentStreams(boolean encoderEnforceMaxConcurrentStreams) {
        return (Http2MultiplexCodecBuilder)super.encoderEnforceMaxConcurrentStreams(encoderEnforceMaxConcurrentStreams);
    }

    @Override
    public Http2HeadersEncoder.SensitivityDetector headerSensitivityDetector() {
        return super.headerSensitivityDetector();
    }

    @Override
    public Http2MultiplexCodecBuilder headerSensitivityDetector(Http2HeadersEncoder.SensitivityDetector headerSensitivityDetector) {
        return (Http2MultiplexCodecBuilder)super.headerSensitivityDetector(headerSensitivityDetector);
    }

    @Override
    public Http2MultiplexCodecBuilder encoderIgnoreMaxHeaderListSize(boolean ignoreMaxHeaderListSize) {
        return (Http2MultiplexCodecBuilder)super.encoderIgnoreMaxHeaderListSize(ignoreMaxHeaderListSize);
    }

    @Override
    public Http2MultiplexCodecBuilder initialHuffmanDecodeCapacity(int initialHuffmanDecodeCapacity) {
        return (Http2MultiplexCodecBuilder)super.initialHuffmanDecodeCapacity(initialHuffmanDecodeCapacity);
    }

    @Override
    public Http2MultiplexCodec build() {
        return (Http2MultiplexCodec)super.build();
    }

    @Override
    protected Http2MultiplexCodec build(Http2ConnectionDecoder decoder, Http2ConnectionEncoder encoder, Http2Settings initialSettings) {
        return new Http2MultiplexCodec(encoder, decoder, initialSettings, this.childHandler);
    }
}

