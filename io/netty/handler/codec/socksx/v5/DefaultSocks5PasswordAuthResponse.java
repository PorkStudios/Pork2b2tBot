/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec.socksx.v5;

import io.netty.handler.codec.DecoderResult;
import io.netty.handler.codec.socksx.v5.AbstractSocks5Message;
import io.netty.handler.codec.socksx.v5.Socks5PasswordAuthResponse;
import io.netty.handler.codec.socksx.v5.Socks5PasswordAuthStatus;
import io.netty.util.internal.StringUtil;

public class DefaultSocks5PasswordAuthResponse
extends AbstractSocks5Message
implements Socks5PasswordAuthResponse {
    private final Socks5PasswordAuthStatus status;

    public DefaultSocks5PasswordAuthResponse(Socks5PasswordAuthStatus status) {
        if (status == null) {
            throw new NullPointerException("status");
        }
        this.status = status;
    }

    @Override
    public Socks5PasswordAuthStatus status() {
        return this.status;
    }

    public String toString() {
        StringBuilder buf = new StringBuilder(StringUtil.simpleClassName(this));
        DecoderResult decoderResult = this.decoderResult();
        if (!decoderResult.isSuccess()) {
            buf.append("(decoderResult: ");
            buf.append(decoderResult);
            buf.append(", status: ");
        } else {
            buf.append("(status: ");
        }
        buf.append(this.status());
        buf.append(')');
        return buf.toString();
    }
}

