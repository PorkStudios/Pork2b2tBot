/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec.http;

import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.DefaultHttpResponse;
import io.netty.handler.codec.http.HttpMessage;
import io.netty.handler.codec.http.HttpObjectDecoder;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;

public class HttpResponseDecoder
extends HttpObjectDecoder {
    private static final HttpResponseStatus UNKNOWN_STATUS = new HttpResponseStatus(999, "Unknown");

    public HttpResponseDecoder() {
    }

    public HttpResponseDecoder(int maxInitialLineLength, int maxHeaderSize, int maxChunkSize) {
        super(maxInitialLineLength, maxHeaderSize, maxChunkSize, true);
    }

    public HttpResponseDecoder(int maxInitialLineLength, int maxHeaderSize, int maxChunkSize, boolean validateHeaders) {
        super(maxInitialLineLength, maxHeaderSize, maxChunkSize, true, validateHeaders);
    }

    public HttpResponseDecoder(int maxInitialLineLength, int maxHeaderSize, int maxChunkSize, boolean validateHeaders, int initialBufferSize) {
        super(maxInitialLineLength, maxHeaderSize, maxChunkSize, true, validateHeaders, initialBufferSize);
    }

    @Override
    protected HttpMessage createMessage(String[] initialLine) {
        return new DefaultHttpResponse(HttpVersion.valueOf(initialLine[0]), new HttpResponseStatus(Integer.parseInt(initialLine[1]), initialLine[2]), this.validateHeaders);
    }

    @Override
    protected HttpMessage createInvalidMessage() {
        return new DefaultFullHttpResponse(HttpVersion.HTTP_1_0, UNKNOWN_STATUS, this.validateHeaders);
    }

    @Override
    protected boolean isDecodingRequest() {
        return false;
    }
}

