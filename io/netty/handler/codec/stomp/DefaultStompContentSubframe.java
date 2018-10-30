/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec.stomp;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufHolder;
import io.netty.buffer.DefaultByteBufHolder;
import io.netty.handler.codec.DecoderResult;
import io.netty.handler.codec.stomp.StompContentSubframe;
import io.netty.util.ReferenceCounted;

public class DefaultStompContentSubframe
extends DefaultByteBufHolder
implements StompContentSubframe {
    private DecoderResult decoderResult = DecoderResult.SUCCESS;

    public DefaultStompContentSubframe(ByteBuf content) {
        super(content);
    }

    @Override
    public StompContentSubframe copy() {
        return (StompContentSubframe)super.copy();
    }

    @Override
    public StompContentSubframe duplicate() {
        return (StompContentSubframe)super.duplicate();
    }

    @Override
    public StompContentSubframe retainedDuplicate() {
        return (StompContentSubframe)super.retainedDuplicate();
    }

    @Override
    public StompContentSubframe replace(ByteBuf content) {
        return new DefaultStompContentSubframe(content);
    }

    @Override
    public StompContentSubframe retain() {
        super.retain();
        return this;
    }

    @Override
    public StompContentSubframe retain(int increment) {
        super.retain(increment);
        return this;
    }

    @Override
    public StompContentSubframe touch() {
        super.touch();
        return this;
    }

    @Override
    public StompContentSubframe touch(Object hint) {
        super.touch(hint);
        return this;
    }

    @Override
    public DecoderResult decoderResult() {
        return this.decoderResult;
    }

    @Override
    public void setDecoderResult(DecoderResult decoderResult) {
        this.decoderResult = decoderResult;
    }

    @Override
    public String toString() {
        return "DefaultStompContent{decoderResult=" + this.decoderResult + '}';
    }
}

