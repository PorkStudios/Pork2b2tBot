/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec.stomp;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufHolder;
import io.netty.handler.codec.DecoderResult;
import io.netty.handler.codec.stomp.DefaultStompContentSubframe;
import io.netty.handler.codec.stomp.LastStompContentSubframe;
import io.netty.handler.codec.stomp.StompContentSubframe;
import io.netty.util.ReferenceCounted;

public class DefaultLastStompContentSubframe
extends DefaultStompContentSubframe
implements LastStompContentSubframe {
    public DefaultLastStompContentSubframe(ByteBuf content) {
        super(content);
    }

    @Override
    public LastStompContentSubframe copy() {
        return (LastStompContentSubframe)super.copy();
    }

    @Override
    public LastStompContentSubframe duplicate() {
        return (LastStompContentSubframe)super.duplicate();
    }

    @Override
    public LastStompContentSubframe retainedDuplicate() {
        return (LastStompContentSubframe)super.retainedDuplicate();
    }

    @Override
    public LastStompContentSubframe replace(ByteBuf content) {
        return new DefaultLastStompContentSubframe(content);
    }

    @Override
    public DefaultLastStompContentSubframe retain() {
        super.retain();
        return this;
    }

    @Override
    public LastStompContentSubframe retain(int increment) {
        super.retain(increment);
        return this;
    }

    @Override
    public LastStompContentSubframe touch() {
        super.touch();
        return this;
    }

    @Override
    public LastStompContentSubframe touch(Object hint) {
        super.touch(hint);
        return this;
    }

    @Override
    public String toString() {
        return "DefaultLastStompContent{decoderResult=" + this.decoderResult() + '}';
    }
}

