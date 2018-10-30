/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec.http2;

import io.netty.handler.codec.http2.Http2Connection;
import io.netty.handler.codec.http2.InboundHttp2ToHttpAdapter;
import io.netty.util.internal.ObjectUtil;

public abstract class AbstractInboundHttp2ToHttpAdapterBuilder<T extends InboundHttp2ToHttpAdapter, B extends AbstractInboundHttp2ToHttpAdapterBuilder<T, B>> {
    private final Http2Connection connection;
    private int maxContentLength;
    private boolean validateHttpHeaders;
    private boolean propagateSettings;

    protected AbstractInboundHttp2ToHttpAdapterBuilder(Http2Connection connection) {
        this.connection = ObjectUtil.checkNotNull(connection, "connection");
    }

    protected final B self() {
        return (B)this;
    }

    protected Http2Connection connection() {
        return this.connection;
    }

    protected int maxContentLength() {
        return this.maxContentLength;
    }

    protected B maxContentLength(int maxContentLength) {
        this.maxContentLength = maxContentLength;
        return this.self();
    }

    protected boolean isValidateHttpHeaders() {
        return this.validateHttpHeaders;
    }

    protected B validateHttpHeaders(boolean validate) {
        this.validateHttpHeaders = validate;
        return this.self();
    }

    protected boolean isPropagateSettings() {
        return this.propagateSettings;
    }

    protected B propagateSettings(boolean propagate) {
        this.propagateSettings = propagate;
        return this.self();
    }

    protected T build() {
        T instance;
        try {
            instance = this.build(this.connection(), this.maxContentLength(), this.isValidateHttpHeaders(), this.isPropagateSettings());
        }
        catch (Throwable t) {
            throw new IllegalStateException("failed to create a new InboundHttp2ToHttpAdapter", t);
        }
        this.connection.addListener((Http2Connection.Listener)instance);
        return instance;
    }

    protected abstract T build(Http2Connection var1, int var2, boolean var3, boolean var4) throws Exception;
}

