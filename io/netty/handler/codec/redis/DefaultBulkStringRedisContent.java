/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec.redis;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufHolder;
import io.netty.buffer.DefaultByteBufHolder;
import io.netty.handler.codec.redis.BulkStringRedisContent;
import io.netty.util.ReferenceCounted;
import io.netty.util.internal.StringUtil;

public class DefaultBulkStringRedisContent
extends DefaultByteBufHolder
implements BulkStringRedisContent {
    public DefaultBulkStringRedisContent(ByteBuf content) {
        super(content);
    }

    @Override
    public BulkStringRedisContent copy() {
        return (BulkStringRedisContent)super.copy();
    }

    @Override
    public BulkStringRedisContent duplicate() {
        return (BulkStringRedisContent)super.duplicate();
    }

    @Override
    public BulkStringRedisContent retainedDuplicate() {
        return (BulkStringRedisContent)super.retainedDuplicate();
    }

    @Override
    public BulkStringRedisContent replace(ByteBuf content) {
        return new DefaultBulkStringRedisContent(content);
    }

    @Override
    public BulkStringRedisContent retain() {
        super.retain();
        return this;
    }

    @Override
    public BulkStringRedisContent retain(int increment) {
        super.retain(increment);
        return this;
    }

    @Override
    public BulkStringRedisContent touch() {
        super.touch();
        return this;
    }

    @Override
    public BulkStringRedisContent touch(Object hint) {
        super.touch(hint);
        return this;
    }

    @Override
    public String toString() {
        return StringUtil.simpleClassName(this) + '[' + "content=" + this.content() + ']';
    }
}

