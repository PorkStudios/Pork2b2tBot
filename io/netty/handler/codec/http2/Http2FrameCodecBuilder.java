/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec.http2;

import io.netty.handler.codec.http2.AbstractHttp2ConnectionHandlerBuilder;
import io.netty.handler.codec.http2.DefaultHttp2Connection;
import io.netty.handler.codec.http2.DefaultHttp2ConnectionDecoder;
import io.netty.handler.codec.http2.DefaultHttp2ConnectionEncoder;
import io.netty.handler.codec.http2.DefaultHttp2FrameReader;
import io.netty.handler.codec.http2.DefaultHttp2HeadersDecoder;
import io.netty.handler.codec.http2.Http2Connection;
import io.netty.handler.codec.http2.Http2ConnectionDecoder;
import io.netty.handler.codec.http2.Http2ConnectionEncoder;
import io.netty.handler.codec.http2.Http2ConnectionHandler;
import io.netty.handler.codec.http2.Http2FrameCodec;
import io.netty.handler.codec.http2.Http2FrameLogger;
import io.netty.handler.codec.http2.Http2FrameReader;
import io.netty.handler.codec.http2.Http2FrameWriter;
import io.netty.handler.codec.http2.Http2HeadersDecoder;
import io.netty.handler.codec.http2.Http2HeadersEncoder;
import io.netty.handler.codec.http2.Http2InboundFrameLogger;
import io.netty.handler.codec.http2.Http2OutboundFrameLogger;
import io.netty.handler.codec.http2.Http2Settings;
import io.netty.handler.codec.http2.StreamBufferingEncoder;
import io.netty.util.internal.ObjectUtil;

public class Http2FrameCodecBuilder
extends AbstractHttp2ConnectionHandlerBuilder<Http2FrameCodec, Http2FrameCodecBuilder> {
    private Http2FrameWriter frameWriter;

    Http2FrameCodecBuilder(boolean server) {
        this.server(server);
    }

    public static Http2FrameCodecBuilder forClient() {
        return new Http2FrameCodecBuilder(false);
    }

    public static Http2FrameCodecBuilder forServer() {
        return new Http2FrameCodecBuilder(true);
    }

    Http2FrameCodecBuilder frameWriter(Http2FrameWriter frameWriter) {
        this.frameWriter = ObjectUtil.checkNotNull(frameWriter, "frameWriter");
        return this;
    }

    @Override
    public Http2Settings initialSettings() {
        return super.initialSettings();
    }

    @Override
    public Http2FrameCodecBuilder initialSettings(Http2Settings settings) {
        return (Http2FrameCodecBuilder)super.initialSettings(settings);
    }

    @Override
    public long gracefulShutdownTimeoutMillis() {
        return super.gracefulShutdownTimeoutMillis();
    }

    @Override
    public Http2FrameCodecBuilder gracefulShutdownTimeoutMillis(long gracefulShutdownTimeoutMillis) {
        return (Http2FrameCodecBuilder)super.gracefulShutdownTimeoutMillis(gracefulShutdownTimeoutMillis);
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
    public Http2FrameCodecBuilder maxReservedStreams(int maxReservedStreams) {
        return (Http2FrameCodecBuilder)super.maxReservedStreams(maxReservedStreams);
    }

    @Override
    public boolean isValidateHeaders() {
        return super.isValidateHeaders();
    }

    @Override
    public Http2FrameCodecBuilder validateHeaders(boolean validateHeaders) {
        return (Http2FrameCodecBuilder)super.validateHeaders(validateHeaders);
    }

    @Override
    public Http2FrameLogger frameLogger() {
        return super.frameLogger();
    }

    @Override
    public Http2FrameCodecBuilder frameLogger(Http2FrameLogger frameLogger) {
        return (Http2FrameCodecBuilder)super.frameLogger(frameLogger);
    }

    @Override
    public boolean encoderEnforceMaxConcurrentStreams() {
        return super.encoderEnforceMaxConcurrentStreams();
    }

    @Override
    public Http2FrameCodecBuilder encoderEnforceMaxConcurrentStreams(boolean encoderEnforceMaxConcurrentStreams) {
        return (Http2FrameCodecBuilder)super.encoderEnforceMaxConcurrentStreams(encoderEnforceMaxConcurrentStreams);
    }

    @Override
    public Http2HeadersEncoder.SensitivityDetector headerSensitivityDetector() {
        return super.headerSensitivityDetector();
    }

    @Override
    public Http2FrameCodecBuilder headerSensitivityDetector(Http2HeadersEncoder.SensitivityDetector headerSensitivityDetector) {
        return (Http2FrameCodecBuilder)super.headerSensitivityDetector(headerSensitivityDetector);
    }

    @Override
    public Http2FrameCodecBuilder encoderIgnoreMaxHeaderListSize(boolean ignoreMaxHeaderListSize) {
        return (Http2FrameCodecBuilder)super.encoderIgnoreMaxHeaderListSize(ignoreMaxHeaderListSize);
    }

    @Override
    public Http2FrameCodecBuilder initialHuffmanDecodeCapacity(int initialHuffmanDecodeCapacity) {
        return (Http2FrameCodecBuilder)super.initialHuffmanDecodeCapacity(initialHuffmanDecodeCapacity);
    }

    @Override
    public Http2FrameCodec build() {
        Http2FrameWriter frameWriter = this.frameWriter;
        if (frameWriter != null) {
            DefaultHttp2Connection connection = new DefaultHttp2Connection(this.isServer(), this.maxReservedStreams());
            Long maxHeaderListSize = this.initialSettings().maxHeaderListSize();
            Http2FrameReader frameReader = new DefaultHttp2FrameReader(maxHeaderListSize == null ? new DefaultHttp2HeadersDecoder(true) : new DefaultHttp2HeadersDecoder(true, maxHeaderListSize));
            if (this.frameLogger() != null) {
                frameWriter = new Http2OutboundFrameLogger(frameWriter, this.frameLogger());
                frameReader = new Http2InboundFrameLogger(frameReader, this.frameLogger());
            }
            Http2ConnectionEncoder encoder = new DefaultHttp2ConnectionEncoder(connection, frameWriter);
            if (this.encoderEnforceMaxConcurrentStreams()) {
                encoder = new StreamBufferingEncoder(encoder);
            }
            DefaultHttp2ConnectionDecoder decoder = new DefaultHttp2ConnectionDecoder(connection, encoder, frameReader);
            return this.build(decoder, encoder, this.initialSettings());
        }
        return (Http2FrameCodec)super.build();
    }

    @Override
    protected Http2FrameCodec build(Http2ConnectionDecoder decoder, Http2ConnectionEncoder encoder, Http2Settings initialSettings) {
        return new Http2FrameCodec(encoder, decoder, initialSettings);
    }
}

