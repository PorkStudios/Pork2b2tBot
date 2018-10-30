/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec.redis;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufHolder;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.MessageAggregator;
import io.netty.handler.codec.redis.BulkStringHeaderRedisMessage;
import io.netty.handler.codec.redis.BulkStringRedisContent;
import io.netty.handler.codec.redis.FullBulkStringRedisMessage;
import io.netty.handler.codec.redis.LastBulkStringRedisContent;
import io.netty.handler.codec.redis.RedisMessage;

public final class RedisBulkStringAggregator
extends MessageAggregator<RedisMessage, BulkStringHeaderRedisMessage, BulkStringRedisContent, FullBulkStringRedisMessage> {
    public RedisBulkStringAggregator() {
        super(536870912);
    }

    @Override
    protected boolean isStartMessage(RedisMessage msg) throws Exception {
        return msg instanceof BulkStringHeaderRedisMessage && !this.isAggregated(msg);
    }

    @Override
    protected boolean isContentMessage(RedisMessage msg) throws Exception {
        return msg instanceof BulkStringRedisContent;
    }

    @Override
    protected boolean isLastContentMessage(BulkStringRedisContent msg) throws Exception {
        return msg instanceof LastBulkStringRedisContent;
    }

    @Override
    protected boolean isAggregated(RedisMessage msg) throws Exception {
        return msg instanceof FullBulkStringRedisMessage;
    }

    @Override
    protected boolean isContentLengthInvalid(BulkStringHeaderRedisMessage start, int maxContentLength) throws Exception {
        return start.bulkStringLength() > maxContentLength;
    }

    @Override
    protected Object newContinueResponse(BulkStringHeaderRedisMessage start, int maxContentLength, ChannelPipeline pipeline) throws Exception {
        return null;
    }

    @Override
    protected boolean closeAfterContinueResponse(Object msg) throws Exception {
        throw new UnsupportedOperationException();
    }

    @Override
    protected boolean ignoreContentAfterContinueResponse(Object msg) throws Exception {
        throw new UnsupportedOperationException();
    }

    @Override
    protected FullBulkStringRedisMessage beginAggregation(BulkStringHeaderRedisMessage start, ByteBuf content) throws Exception {
        return new FullBulkStringRedisMessage(content);
    }
}

