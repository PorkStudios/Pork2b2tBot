/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec.http;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufHolder;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.CombinedHttpHeaders;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.DefaultHttpResponse;
import io.netty.handler.codec.http.FullHttpMessage;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMessage;
import io.netty.handler.codec.http.HttpMessageUtil;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.util.IllegalReferenceCountException;
import io.netty.util.ReferenceCounted;
import io.netty.util.internal.ObjectUtil;

public class DefaultFullHttpResponse
extends DefaultHttpResponse
implements FullHttpResponse {
    private final ByteBuf content;
    private final HttpHeaders trailingHeaders;
    private int hash;

    public DefaultFullHttpResponse(HttpVersion version, HttpResponseStatus status) {
        this(version, status, Unpooled.buffer(0));
    }

    public DefaultFullHttpResponse(HttpVersion version, HttpResponseStatus status, ByteBuf content) {
        this(version, status, content, true);
    }

    public DefaultFullHttpResponse(HttpVersion version, HttpResponseStatus status, boolean validateHeaders) {
        this(version, status, Unpooled.buffer(0), validateHeaders, false);
    }

    public DefaultFullHttpResponse(HttpVersion version, HttpResponseStatus status, boolean validateHeaders, boolean singleFieldHeaders) {
        this(version, status, Unpooled.buffer(0), validateHeaders, singleFieldHeaders);
    }

    public DefaultFullHttpResponse(HttpVersion version, HttpResponseStatus status, ByteBuf content, boolean validateHeaders) {
        this(version, status, content, validateHeaders, false);
    }

    public DefaultFullHttpResponse(HttpVersion version, HttpResponseStatus status, ByteBuf content, boolean validateHeaders, boolean singleFieldHeaders) {
        super(version, status, validateHeaders, singleFieldHeaders);
        this.content = ObjectUtil.checkNotNull(content, "content");
        this.trailingHeaders = singleFieldHeaders ? new CombinedHttpHeaders(validateHeaders) : new DefaultHttpHeaders(validateHeaders);
    }

    public DefaultFullHttpResponse(HttpVersion version, HttpResponseStatus status, ByteBuf content, HttpHeaders headers, HttpHeaders trailingHeaders) {
        super(version, status, headers);
        this.content = ObjectUtil.checkNotNull(content, "content");
        this.trailingHeaders = ObjectUtil.checkNotNull(trailingHeaders, "trailingHeaders");
    }

    @Override
    public HttpHeaders trailingHeaders() {
        return this.trailingHeaders;
    }

    @Override
    public ByteBuf content() {
        return this.content;
    }

    @Override
    public int refCnt() {
        return this.content.refCnt();
    }

    @Override
    public FullHttpResponse retain() {
        this.content.retain();
        return this;
    }

    @Override
    public FullHttpResponse retain(int increment) {
        this.content.retain(increment);
        return this;
    }

    @Override
    public FullHttpResponse touch() {
        this.content.touch();
        return this;
    }

    @Override
    public FullHttpResponse touch(Object hint) {
        this.content.touch(hint);
        return this;
    }

    @Override
    public boolean release() {
        return this.content.release();
    }

    @Override
    public boolean release(int decrement) {
        return this.content.release(decrement);
    }

    @Override
    public FullHttpResponse setProtocolVersion(HttpVersion version) {
        super.setProtocolVersion(version);
        return this;
    }

    @Override
    public FullHttpResponse setStatus(HttpResponseStatus status) {
        super.setStatus(status);
        return this;
    }

    @Override
    public FullHttpResponse copy() {
        return this.replace(this.content().copy());
    }

    @Override
    public FullHttpResponse duplicate() {
        return this.replace(this.content().duplicate());
    }

    @Override
    public FullHttpResponse retainedDuplicate() {
        return this.replace(this.content().retainedDuplicate());
    }

    @Override
    public FullHttpResponse replace(ByteBuf content) {
        return new DefaultFullHttpResponse(this.protocolVersion(), this.status(), content, this.headers(), this.trailingHeaders());
    }

    @Override
    public int hashCode() {
        int hash = this.hash;
        if (hash == 0) {
            if (this.content().refCnt() != 0) {
                try {
                    hash = 31 + this.content().hashCode();
                }
                catch (IllegalReferenceCountException ignored) {
                    hash = 31;
                }
            } else {
                hash = 31;
            }
            hash = 31 * hash + this.trailingHeaders().hashCode();
            this.hash = hash = 31 * hash + super.hashCode();
        }
        return hash;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof DefaultFullHttpResponse)) {
            return false;
        }
        DefaultFullHttpResponse other = (DefaultFullHttpResponse)o;
        return super.equals(other) && this.content().equals(other.content()) && this.trailingHeaders().equals(other.trailingHeaders());
    }

    @Override
    public String toString() {
        return HttpMessageUtil.appendFullResponse(new StringBuilder(256), this).toString();
    }
}

