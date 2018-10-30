/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec.redis;

import io.netty.handler.codec.redis.RedisMessage;
import io.netty.util.AbstractReferenceCounted;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.ReferenceCounted;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.StringUtil;
import java.util.Collections;
import java.util.List;

public class ArrayRedisMessage
extends AbstractReferenceCounted
implements RedisMessage {
    private final List<RedisMessage> children;
    public static final ArrayRedisMessage NULL_INSTANCE = new ArrayRedisMessage(){

        @Override
        public boolean isNull() {
            return true;
        }

        @Override
        public ArrayRedisMessage retain() {
            return this;
        }

        @Override
        public ArrayRedisMessage retain(int increment) {
            return this;
        }

        @Override
        public ArrayRedisMessage touch() {
            return this;
        }

        @Override
        public ArrayRedisMessage touch(Object hint) {
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

        @Override
        public String toString() {
            return "NullArrayRedisMessage";
        }
    };
    public static final ArrayRedisMessage EMPTY_INSTANCE = new ArrayRedisMessage(){

        @Override
        public boolean isNull() {
            return false;
        }

        @Override
        public ArrayRedisMessage retain() {
            return this;
        }

        @Override
        public ArrayRedisMessage retain(int increment) {
            return this;
        }

        @Override
        public ArrayRedisMessage touch() {
            return this;
        }

        @Override
        public ArrayRedisMessage touch(Object hint) {
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

        @Override
        public String toString() {
            return "EmptyArrayRedisMessage";
        }
    };

    private ArrayRedisMessage() {
        this.children = Collections.emptyList();
    }

    public ArrayRedisMessage(List<RedisMessage> children) {
        this.children = ObjectUtil.checkNotNull(children, "children");
    }

    public final List<RedisMessage> children() {
        return this.children;
    }

    public boolean isNull() {
        return false;
    }

    @Override
    protected void deallocate() {
        for (RedisMessage msg : this.children) {
            ReferenceCountUtil.release(msg);
        }
    }

    @Override
    public ArrayRedisMessage touch(Object hint) {
        for (RedisMessage msg : this.children) {
            ReferenceCountUtil.touch(msg);
        }
        return this;
    }

    public String toString() {
        return StringUtil.simpleClassName(this) + '[' + "children=" + this.children.size() + ']';
    }

}

