/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec.redis;

import io.netty.handler.codec.redis.AbstractStringRedisMessage;
import io.netty.util.internal.StringUtil;

public final class SimpleStringRedisMessage
extends AbstractStringRedisMessage {
    public SimpleStringRedisMessage(String content) {
        super(content);
    }

    public String toString() {
        return StringUtil.simpleClassName(this) + '[' + "content=" + this.content() + ']';
    }
}

