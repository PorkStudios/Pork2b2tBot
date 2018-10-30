/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec.rtsp;

import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.DefaultHttpRequest;
import io.netty.handler.codec.http.DefaultHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMessage;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpObjectDecoder;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.rtsp.RtspHeaderNames;
import io.netty.handler.codec.rtsp.RtspMethods;
import io.netty.handler.codec.rtsp.RtspVersions;
import io.netty.util.AsciiString;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RtspDecoder
extends HttpObjectDecoder {
    private static final HttpResponseStatus UNKNOWN_STATUS = new HttpResponseStatus(999, "Unknown");
    private boolean isDecodingRequest;
    private static final Pattern versionPattern = Pattern.compile("RTSP/\\d\\.\\d");
    public static final int DEFAULT_MAX_INITIAL_LINE_LENGTH = 4096;
    public static final int DEFAULT_MAX_HEADER_SIZE = 8192;
    public static final int DEFAULT_MAX_CONTENT_LENGTH = 8192;

    public RtspDecoder() {
        this(4096, 8192, 8192);
    }

    public RtspDecoder(int maxInitialLineLength, int maxHeaderSize, int maxContentLength) {
        super(maxInitialLineLength, maxHeaderSize, maxContentLength * 2, false);
    }

    public RtspDecoder(int maxInitialLineLength, int maxHeaderSize, int maxContentLength, boolean validateHeaders) {
        super(maxInitialLineLength, maxHeaderSize, maxContentLength * 2, false, validateHeaders);
    }

    @Override
    protected HttpMessage createMessage(String[] initialLine) throws Exception {
        if (versionPattern.matcher(initialLine[0]).matches()) {
            this.isDecodingRequest = false;
            return new DefaultHttpResponse(RtspVersions.valueOf(initialLine[0]), new HttpResponseStatus(Integer.parseInt(initialLine[1]), initialLine[2]), this.validateHeaders);
        }
        this.isDecodingRequest = true;
        return new DefaultHttpRequest(RtspVersions.valueOf(initialLine[2]), RtspMethods.valueOf(initialLine[0]), initialLine[1], this.validateHeaders);
    }

    @Override
    protected boolean isContentAlwaysEmpty(HttpMessage msg) {
        return super.isContentAlwaysEmpty(msg) || !msg.headers().contains(RtspHeaderNames.CONTENT_LENGTH);
    }

    @Override
    protected HttpMessage createInvalidMessage() {
        if (this.isDecodingRequest) {
            return new DefaultFullHttpRequest(RtspVersions.RTSP_1_0, RtspMethods.OPTIONS, "/bad-request", this.validateHeaders);
        }
        return new DefaultFullHttpResponse(RtspVersions.RTSP_1_0, UNKNOWN_STATUS, this.validateHeaders);
    }

    @Override
    protected boolean isDecodingRequest() {
        return this.isDecodingRequest;
    }
}

