/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec.http2;

import io.netty.handler.codec.http2.AbstractInboundHttp2ToHttpAdapterBuilder;
import io.netty.handler.codec.http2.Http2Connection;
import io.netty.handler.codec.http2.InboundHttp2ToHttpAdapter;

public final class InboundHttp2ToHttpAdapterBuilder
extends AbstractInboundHttp2ToHttpAdapterBuilder<InboundHttp2ToHttpAdapter, InboundHttp2ToHttpAdapterBuilder> {
    public InboundHttp2ToHttpAdapterBuilder(Http2Connection connection) {
        super(connection);
    }

    @Override
    public InboundHttp2ToHttpAdapterBuilder maxContentLength(int maxContentLength) {
        return (InboundHttp2ToHttpAdapterBuilder)super.maxContentLength(maxContentLength);
    }

    @Override
    public InboundHttp2ToHttpAdapterBuilder validateHttpHeaders(boolean validate) {
        return (InboundHttp2ToHttpAdapterBuilder)super.validateHttpHeaders(validate);
    }

    @Override
    public InboundHttp2ToHttpAdapterBuilder propagateSettings(boolean propagate) {
        return (InboundHttp2ToHttpAdapterBuilder)super.propagateSettings(propagate);
    }

    @Override
    public InboundHttp2ToHttpAdapter build() {
        return super.build();
    }

    @Override
    protected InboundHttp2ToHttpAdapter build(Http2Connection connection, int maxContentLength, boolean validateHttpHeaders, boolean propagateSettings) throws Exception {
        return new InboundHttp2ToHttpAdapter(connection, maxContentLength, validateHttpHeaders, propagateSettings);
    }
}

