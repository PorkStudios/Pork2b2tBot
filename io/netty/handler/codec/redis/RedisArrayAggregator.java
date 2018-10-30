/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec.redis;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.CodecException;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.handler.codec.redis.ArrayHeaderRedisMessage;
import io.netty.handler.codec.redis.ArrayRedisMessage;
import io.netty.handler.codec.redis.RedisMessage;
import io.netty.util.ReferenceCountUtil;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

public final class RedisArrayAggregator
extends MessageToMessageDecoder<RedisMessage> {
    private final Deque<AggregateState> depths = new ArrayDeque<AggregateState>(4);

    @Override
    protected void decode(ChannelHandlerContext ctx, RedisMessage msg, List<Object> out) throws Exception {
        if (msg instanceof ArrayHeaderRedisMessage) {
            if ((msg = this.decodeRedisArrayHeader((ArrayHeaderRedisMessage)msg)) == null) {
                return;
            }
        } else {
            ReferenceCountUtil.retain(msg);
        }
        while (!this.depths.isEmpty()) {
            AggregateState current = this.depths.peek();
            current.children.add(msg);
            if (current.children.size() == current.length) {
                msg = new ArrayRedisMessage(current.children);
                this.depths.pop();
                continue;
            }
            return;
        }
        out.add(msg);
    }

    private RedisMessage decodeRedisArrayHeader(ArrayHeaderRedisMessage header) {
        if (header.isNull()) {
            return ArrayRedisMessage.NULL_INSTANCE;
        }
        if (header.length() == 0L) {
            return ArrayRedisMessage.EMPTY_INSTANCE;
        }
        if (header.length() > 0L) {
            if (header.length() > Integer.MAX_VALUE) {
                throw new CodecException("this codec doesn't support longer length than 2147483647");
            }
            this.depths.push(new AggregateState((int)header.length()));
            return null;
        }
        throw new CodecException("bad length: " + header.length());
    }

    private static final class AggregateState {
        private final int length;
        private final List<RedisMessage> children;

        AggregateState(int length) {
            this.length = length;
            this.children = new ArrayList<RedisMessage>(length);
        }
    }

}

