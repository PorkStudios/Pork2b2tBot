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
import io.netty.handler.codec.http2.HttpToHttp2ConnectionHandler;

public final class HttpToHttp2ConnectionHandlerBuilder
extends AbstractHttp2ConnectionHandlerBuilder<HttpToHttp2ConnectionHandler, HttpToHttp2ConnectionHandlerBuilder> {
    @Override
    public HttpToHttp2ConnectionHandlerBuilder validateHeaders(boolean validateHeaders) {
        return (HttpToHttp2ConnectionHandlerBuilder)super.validateHeaders(validateHeaders);
    }

    @Override
    public HttpToHttp2ConnectionHandlerBuilder initialSettings(Http2Settings settings) {
        return (HttpToHttp2ConnectionHandlerBuilder)super.initialSettings(settings);
    }

    @Override
    public HttpToHttp2ConnectionHandlerBuilder frameListener(Http2FrameListener frameListener) {
        return (HttpToHttp2ConnectionHandlerBuilder)super.frameListener(frameListener);
    }

    @Override
    public HttpToHttp2ConnectionHandlerBuilder gracefulShutdownTimeoutMillis(long gracefulShutdownTimeoutMillis) {
        return (HttpToHttp2ConnectionHandlerBuilder)super.gracefulShutdownTimeoutMillis(gracefulShutdownTimeoutMillis);
    }

    @Override
    public HttpToHttp2ConnectionHandlerBuilder server(boolean isServer) {
        return (HttpToHttp2ConnectionHandlerBuilder)super.server(isServer);
    }

    @Override
    public HttpToHttp2ConnectionHandlerBuilder connection(Http2Connection connection) {
        return (HttpToHttp2ConnectionHandlerBuilder)super.connection(connection);
    }

    @Override
    public HttpToHttp2ConnectionHandlerBuilder codec(Http2ConnectionDecoder decoder, Http2ConnectionEncoder encoder) {
        return (HttpToHttp2ConnectionHandlerBuilder)super.codec(decoder, encoder);
    }

    @Override
    public HttpToHttp2ConnectionHandlerBuilder frameLogger(Http2FrameLogger frameLogger) {
        return (HttpToHttp2ConnectionHandlerBuilder)super.frameLogger(frameLogger);
    }

    @Override
    public HttpToHttp2ConnectionHandlerBuilder encoderEnforceMaxConcurrentStreams(boolean encoderEnforceMaxConcurrentStreams) {
        return (HttpToHttp2ConnectionHandlerBuilder)super.encoderEnforceMaxConcurrentStreams(encoderEnforceMaxConcurrentStreams);
    }

    @Override
    public HttpToHttp2ConnectionHandlerBuilder headerSensitivityDetector(Http2HeadersEncoder.SensitivityDetector headerSensitivityDetector) {
        return (HttpToHttp2ConnectionHandlerBuilder)super.headerSensitivityDetector(headerSensitivityDetector);
    }

    @Override
    public HttpToHttp2ConnectionHandlerBuilder initialHuffmanDecodeCapacity(int initialHuffmanDecodeCapacity) {
        return (HttpToHttp2ConnectionHandlerBuilder)super.initialHuffmanDecodeCapacity(initialHuffmanDecodeCapacity);
    }

    @Override
    public HttpToHttp2ConnectionHandler build() {
        return (HttpToHttp2ConnectionHandler)super.build();
    }

    @Override
    protected HttpToHttp2ConnectionHandler build(Http2ConnectionDecoder decoder, Http2ConnectionEncoder encoder, Http2Settings initialSettings) {
        return new HttpToHttp2ConnectionHandler(decoder, encoder, initialSettings, this.isValidateHeaders());
    }
}

