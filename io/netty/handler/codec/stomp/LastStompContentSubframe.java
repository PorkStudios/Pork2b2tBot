/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec.stomp;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufHolder;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.DecoderResult;
import io.netty.handler.codec.stomp.DefaultLastStompContentSubframe;
import io.netty.handler.codec.stomp.StompContentSubframe;
import io.netty.util.ReferenceCounted;

public interface LastStompContentSubframe
extends StompContentSubframe {
    public static final LastStompContentSubframe EMPTY_LAST_CONTENT = new LastStompContentSubframe(){

        @Override
        public ByteBuf content() {
            return Unpooled.EMPTY_BUFFER;
        }

        @Override
        public LastStompContentSubframe copy() {
            return EMPTY_LAST_CONTENT;
        }

        @Override
        public LastStompContentSubframe duplicate() {
            return this;
        }

        @Override
        public LastStompContentSubframe retainedDuplicate() {
            return this;
        }

        @Override
        public LastStompContentSubframe replace(ByteBuf content) {
            return new DefaultLastStompContentSubframe(content);
        }

        @Override
        public LastStompContentSubframe retain() {
            return this;
        }

        @Override
        public LastStompContentSubframe retain(int increment) {
            return this;
        }

        @Override
        public LastStompContentSubframe touch() {
            return this;
        }

        @Override
        public LastStompContentSubframe touch(Object hint) {
            return this;
        }

        @Override
        public int refCnt() {
            return 1;
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
        public DecoderResult decoderResult() {
            return DecoderResult.SUCCESS;
        }

        @Override
        public void setDecoderResult(DecoderResult result) {
            throw new UnsupportedOperationException("read only");
        }
    };

    @Override
    public LastStompContentSubframe copy();

    @Override
    public LastStompContentSubframe duplicate();

    @Override
    public LastStompContentSubframe retainedDuplicate();

    @Override
    public LastStompContentSubframe replace(ByteBuf var1);

    @Override
    public LastStompContentSubframe retain();

    @Override
    public LastStompContentSubframe retain(int var1);

    @Override
    public LastStompContentSubframe touch();

    @Override
    public LastStompContentSubframe touch(Object var1);

}

