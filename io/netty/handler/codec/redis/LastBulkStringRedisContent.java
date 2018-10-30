/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec.redis;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufHolder;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.redis.BulkStringRedisContent;
import io.netty.handler.codec.redis.DefaultLastBulkStringRedisContent;
import io.netty.util.ReferenceCounted;

public interface LastBulkStringRedisContent
extends BulkStringRedisContent {
    public static final LastBulkStringRedisContent EMPTY_LAST_CONTENT = new LastBulkStringRedisContent(){

        @Override
        public ByteBuf content() {
            return Unpooled.EMPTY_BUFFER;
        }

        @Override
        public LastBulkStringRedisContent copy() {
            return this;
        }

        @Override
        public LastBulkStringRedisContent duplicate() {
            return this;
        }

        @Override
        public LastBulkStringRedisContent retainedDuplicate() {
            return this;
        }

        @Override
        public LastBulkStringRedisContent replace(ByteBuf content) {
            return new DefaultLastBulkStringRedisContent(content);
        }

        @Override
        public LastBulkStringRedisContent retain(int increment) {
            return this;
        }

        @Override
        public LastBulkStringRedisContent retain() {
            return this;
        }

        @Override
        public int refCnt() {
            return 1;
        }

        @Override
        public LastBulkStringRedisContent touch() {
            return this;
        }

        @Override
        public LastBulkStringRedisContent touch(Object hint) {
            return this;
        }

        @Override
        public boolean release() {
            return false;
        }

        @Override
        public boolean release(int decrement) {
            return false;
        }
    };

    @Override
    public LastBulkStringRedisContent copy();

    @Override
    public LastBulkStringRedisContent duplicate();

    @Override
    public LastBulkStringRedisContent retainedDuplicate();

    @Override
    public LastBulkStringRedisContent replace(ByteBuf var1);

    @Override
    public LastBulkStringRedisContent retain();

    @Override
    public LastBulkStringRedisContent retain(int var1);

    @Override
    public LastBulkStringRedisContent touch();

    @Override
    public LastBulkStringRedisContent touch(Object var1);

}

