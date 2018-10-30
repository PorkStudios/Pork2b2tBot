/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec.redis;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufHolder;
import io.netty.handler.codec.redis.RedisMessage;

public interface BulkStringRedisContent
extends RedisMessage,
ByteBufHolder {
    @Override
    public BulkStringRedisContent copy();

    @Override
    public BulkStringRedisContent duplicate();

    @Override
    public BulkStringRedisContent retainedDuplicate();

    @Override
    public BulkStringRedisContent replace(ByteBuf var1);

    @Override
    public BulkStringRedisContent retain();

    @Override
    public BulkStringRedisContent retain(int var1);

    @Override
    public BulkStringRedisContent touch();

    @Override
    public BulkStringRedisContent touch(Object var1);
}

