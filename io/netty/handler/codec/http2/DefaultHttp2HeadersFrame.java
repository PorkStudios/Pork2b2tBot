/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec.http2;

import io.netty.handler.codec.http2.AbstractHttp2StreamFrame;
import io.netty.handler.codec.http2.Http2CodecUtil;
import io.netty.handler.codec.http2.Http2FrameStream;
import io.netty.handler.codec.http2.Http2Headers;
import io.netty.handler.codec.http2.Http2HeadersFrame;
import io.netty.handler.codec.http2.Http2StreamFrame;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.StringUtil;

public final class DefaultHttp2HeadersFrame
extends AbstractHttp2StreamFrame
implements Http2HeadersFrame {
    private final Http2Headers headers;
    private final boolean endStream;
    private final int padding;

    public DefaultHttp2HeadersFrame(Http2Headers headers) {
        this(headers, false);
    }

    public DefaultHttp2HeadersFrame(Http2Headers headers, boolean endStream) {
        this(headers, endStream, 0);
    }

    public DefaultHttp2HeadersFrame(Http2Headers headers, boolean endStream, int padding) {
        this.headers = ObjectUtil.checkNotNull(headers, "headers");
        this.endStream = endStream;
        Http2CodecUtil.verifyPadding(padding);
        this.padding = padding;
    }

    @Override
    public DefaultHttp2HeadersFrame stream(Http2FrameStream stream) {
        super.stream(stream);
        return this;
    }

    @Override
    public String name() {
        return "HEADERS";
    }

    @Override
    public Http2Headers headers() {
        return this.headers;
    }

    @Override
    public boolean isEndStream() {
        return this.endStream;
    }

    @Override
    public int padding() {
        return this.padding;
    }

    public String toString() {
        return StringUtil.simpleClassName(this) + "(stream=" + this.stream() + ", headers=" + this.headers + ", endStream=" + this.endStream + ", padding=" + this.padding + ')';
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof DefaultHttp2HeadersFrame)) {
            return false;
        }
        DefaultHttp2HeadersFrame other = (DefaultHttp2HeadersFrame)o;
        return super.equals(other) && this.headers.equals(other.headers) && this.endStream == other.endStream && this.padding == other.padding;
    }

    @Override
    public int hashCode() {
        int hash = super.hashCode();
        hash = hash * 31 + this.headers.hashCode();
        hash = hash * 31 + (this.endStream ? 0 : 1);
        hash = hash * 31 + this.padding;
        return hash;
    }
}

