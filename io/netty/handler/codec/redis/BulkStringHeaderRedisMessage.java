/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec.redis;

import io.netty.handler.codec.redis.RedisCodecException;
import io.netty.handler.codec.redis.RedisMessage;

public class BulkStringHeaderRedisMessage
implements RedisMessage {
    private final int bulkStringLength;

    public BulkStringHeaderRedisMessage(int bulkStringLength) {
        if (bulkStringLength <= 0) {
            throw new RedisCodecException("bulkStringLength: " + bulkStringLength + " (expected: > 0)");
        }
        this.bulkStringLength = bulkStringLength;
    }

    public final int bulkStringLength() {
        return this.bulkStringLength;
    }

    public boolean isNull() {
        return this.bulkStringLength == -1;
    }
}

