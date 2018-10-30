/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec.http;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.FullHttpMessage;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpVersion;

public interface FullHttpRequest
extends HttpRequest,
FullHttpMessage {
    @Override
    public FullHttpRequest copy();

    @Override
    public FullHttpRequest duplicate();

    @Override
    public FullHttpRequest retainedDuplicate();

    @Override
    public FullHttpRequest replace(ByteBuf var1);

    @Override
    public FullHttpRequest retain(int var1);

    @Override
    public FullHttpRequest retain();

    @Override
    public FullHttpRequest touch();

    @Override
    public FullHttpRequest touch(Object var1);

    @Override
    public FullHttpRequest setProtocolVersion(HttpVersion var1);

    @Override
    public FullHttpRequest setMethod(HttpMethod var1);

    @Override
    public FullHttpRequest setUri(String var1);
}

