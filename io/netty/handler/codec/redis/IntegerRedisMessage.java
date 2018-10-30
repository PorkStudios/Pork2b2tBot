/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec.redis;

import io.netty.handler.codec.redis.RedisMessage;
import io.netty.util.internal.StringUtil;

public final class IntegerRedisMessage
implements RedisMessage {
    private final long value;

    public IntegerRedisMessage(long value) {
        this.value = value;
    }

    public long value() {
        return this.value;
    }

    public String toString() {
        return StringUtil.simpleClassName(this) + '[' + "value=" + this.value + ']';
    }
}

