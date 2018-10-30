/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec.http;

import io.netty.handler.codec.http.DefaultHttpMessage;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMessage;
import io.netty.handler.codec.http.HttpMessageUtil;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.internal.ObjectUtil;

public class DefaultHttpRequest
extends DefaultHttpMessage
implements HttpRequest {
    private static final int HASH_CODE_PRIME = 31;
    private HttpMethod method;
    private String uri;

    public DefaultHttpRequest(HttpVersion httpVersion, HttpMethod method, String uri) {
        this(httpVersion, method, uri, true);
    }

    public DefaultHttpRequest(HttpVersion httpVersion, HttpMethod method, String uri, boolean validateHeaders) {
        super(httpVersion, validateHeaders, false);
        this.method = ObjectUtil.checkNotNull(method, "method");
        this.uri = ObjectUtil.checkNotNull(uri, "uri");
    }

    public DefaultHttpRequest(HttpVersion httpVersion, HttpMethod method, String uri, HttpHeaders headers) {
        super(httpVersion, headers);
        this.method = ObjectUtil.checkNotNull(method, "method");
        this.uri = ObjectUtil.checkNotNull(uri, "uri");
    }

    @Deprecated
    @Override
    public HttpMethod getMethod() {
        return this.method();
    }

    @Override
    public HttpMethod method() {
        return this.method;
    }

    @Deprecated
    @Override
    public String getUri() {
        return this.uri();
    }

    @Override
    public String uri() {
        return this.uri;
    }

    @Override
    public HttpRequest setMethod(HttpMethod method) {
        if (method == null) {
            throw new NullPointerException("method");
        }
        this.method = method;
        return this;
    }

    @Override
    public HttpRequest setUri(String uri) {
        if (uri == null) {
            throw new NullPointerException("uri");
        }
        this.uri = uri;
        return this;
    }

    @Override
    public HttpRequest setProtocolVersion(HttpVersion version) {
        super.setProtocolVersion(version);
        return this;
    }

    @Override
    public int hashCode() {
        int result = 1;
        result = 31 * result + this.method.hashCode();
        result = 31 * result + this.uri.hashCode();
        result = 31 * result + super.hashCode();
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof DefaultHttpRequest)) {
            return false;
        }
        DefaultHttpRequest other = (DefaultHttpRequest)o;
        return this.method().equals(other.method()) && this.uri().equalsIgnoreCase(other.uri()) && super.equals(o);
    }

    public String toString() {
        return HttpMessageUtil.appendRequest(new StringBuilder(256), this).toString();
    }
}

