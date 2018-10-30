/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec.redis;

import io.netty.handler.codec.CodecException;

public final class RedisCodecException
extends CodecException {
    private static final long serialVersionUID = 5570454251549268063L;

    public RedisCodecException(String message) {
        super(message);
    }

    public RedisCodecException(Throwable cause) {
        super(cause);
    }
}

