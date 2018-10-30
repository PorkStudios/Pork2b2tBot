/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec.http2;

import io.netty.handler.codec.http2.AbstractHttp2ConnectionHandlerBuilder;
import io.netty.handler.codec.http2.Http2Connection;
import io.netty.handler.codec.http2.Http2ConnectionDecoder;
import io.netty.handler.codec.http2.Http2ConnectionEncoder;
import io.netty.handler.codec.http2.Http2ConnectionHandler;
import io.netty.handler.codec.http2.Http2FrameListener;
import io.netty.handler.codec.http2.Http2FrameLogger;
import io.netty.handler.codec.http2.Http2HeadersEncoder;
import io.netty.handler.codec.http2.Http2Settings;

public final class Http2ConnectionHandlerBuilder
extends AbstractHttp2ConnectionHandlerBuilder<Http2ConnectionHandler, Http2ConnectionHandlerBuilder> {
    @Override
    public Http2ConnectionHandlerBuilder validateHeaders(boolean validateHeaders) {
        return (Http2ConnectionHandlerBuilder)super.validateHeaders(validateHeaders);
    }

    @Override
    public Http2ConnectionHandlerBuilder initialSettings(Http2Settings settings) {
        return (Http2ConnectionHandlerBuilder)super.initialSettings(settings);
    }

    @Override
    public Http2ConnectionHandlerBuilder frameListener(Http2FrameListener frameListener) {
        return (Http2ConnectionHandlerBuilder)super.frameListener(frameListener);
    }

    @Override
    public Http2ConnectionHandlerBuilder gracefulShutdownTimeoutMillis(long gracefulShutdownTimeoutMillis) {
        return (Http2ConnectionHandlerBuilder)super.gracefulShutdownTimeoutMillis(gracefulShutdownTimeoutMillis);
    }

    @Override
    public Http2ConnectionHandlerBuilder server(boolean isServer) {
        return (Http2ConnectionHandlerBuilder)super.server(isServer);
    }

    @Override
    public Http2ConnectionHandlerBuilder connection(Http2Connection connection) {
        return (Http2ConnectionHandlerBuilder)super.connection(connection);
    }

    @Override
    public Http2ConnectionHandlerBuilder maxReservedStreams(int maxReservedStreams) {
        return (Http2ConnectionHandlerBuilder)super.maxReservedStreams(maxReservedStreams);
    }

    @Override
    public Http2ConnectionHandlerBuilder codec(Http2ConnectionDecoder decoder, Http2ConnectionEncoder encoder) {
        return (Http2ConnectionHandlerBuilder)super.codec(decoder, encoder);
    }

    @Override
    public Http2ConnectionHandlerBuilder frameLogger(Http2FrameLogger frameLogger) {
        return (Http2ConnectionHandlerBuilder)super.frameLogger(frameLogger);
    }

    @Override
    public Http2ConnectionHandlerBuilder encoderEnforceMaxConcurrentStreams(boolean encoderEnforceMaxConcurrentStreams) {
        return (Http2ConnectionHandlerBuilder)super.encoderEnforceMaxConcurrentStreams(encoderEnforceMaxConcurrentStreams);
    }

    @Override
    public Http2ConnectionHandlerBuilder encoderIgnoreMaxHeaderListSize(boolean encoderIgnoreMaxHeaderListSize) {
        return (Http2ConnectionHandlerBuilder)super.encoderIgnoreMaxHeaderListSize(encoderIgnoreMaxHeaderListSize);
    }

    @Override
    public Http2ConnectionHandlerBuilder headerSensitivityDetector(Http2HeadersEncoder.SensitivityDetector headerSensitivityDetector) {
        return (Http2ConnectionHandlerBuilder)super.headerSensitivityDetector(headerSensitivityDetector);
    }

    @Override
    public Http2ConnectionHandlerBuilder initialHuffmanDecodeCapacity(int initialHuffmanDecodeCapacity) {
        return (Http2ConnectionHandlerBuilder)super.initialHuffmanDecodeCapacity(initialHuffmanDecodeCapacity);
    }

    @Override
    public Http2ConnectionHandler build() {
        return super.build();
    }

    @Override
    protected Http2ConnectionHandler build(Http2ConnectionDecoder decoder, Http2ConnectionEncoder encoder, Http2Settings initialSettings) {
        return new Http2ConnectionHandler(decoder, encoder, initialSettings);
    }
}

