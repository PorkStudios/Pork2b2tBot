/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec.http;

import io.netty.handler.codec.http.HttpMessage;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpVersion;

public interface HttpRequest
extends HttpMessage {
    @Deprecated
    public HttpMethod getMethod();

    public HttpMethod method();

    public HttpRequest setMethod(HttpMethod var1);

    @Deprecated
    public String getUri();

    public String uri();

    public HttpRequest setUri(String var1);

    @Override
    public HttpRequest setProtocolVersion(HttpVersion var1);
}

