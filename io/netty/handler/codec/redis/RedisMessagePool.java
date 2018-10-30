/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec.redis;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.redis.ErrorRedisMessage;
import io.netty.handler.codec.redis.IntegerRedisMessage;
import io.netty.handler.codec.redis.SimpleStringRedisMessage;

public interface RedisMessagePool {
    public SimpleStringRedisMessage getSimpleString(String var1);

    public SimpleStringRedisMessage getSimpleString(ByteBuf var1);

    public ErrorRedisMessage getError(String var1);

    public ErrorRedisMessage getError(ByteBuf var1);

    public IntegerRedisMessage getInteger(long var1);

    public IntegerRedisMessage getInteger(ByteBuf var1);

    public byte[] getByteBufOfInteger(long var1);
}

