/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec.socksx.v5;

import io.netty.handler.codec.DecoderResult;
import io.netty.handler.codec.socksx.v5.AbstractSocks5Message;
import io.netty.handler.codec.socksx.v5.Socks5AuthMethod;
import io.netty.handler.codec.socksx.v5.Socks5InitialResponse;
import io.netty.util.internal.StringUtil;

public class DefaultSocks5InitialResponse
extends AbstractSocks5Message
implements Socks5InitialResponse {
    private final Socks5AuthMethod authMethod;

    public DefaultSocks5InitialResponse(Socks5AuthMethod authMethod) {
        if (authMethod == null) {
            throw new NullPointerException("authMethod");
        }
        this.authMethod = authMethod;
    }

    @Override
    public Socks5AuthMethod authMethod() {
        return this.authMethod;
    }

    public String toString() {
        StringBuilder buf = new StringBuilder(StringUtil.simpleClassName(this));
        DecoderResult decoderResult = this.decoderResult();
        if (!decoderResult.isSuccess()) {
            buf.append("(decoderResult: ");
            buf.append(decoderResult);
            buf.append(", authMethod: ");
        } else {
            buf.append("(authMethod: ");
        }
        buf.append(this.authMethod());
        buf.append(')');
        return buf.toString();
    }
}

