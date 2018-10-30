/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec.memcache;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufHolder;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.DecoderResult;
import io.netty.handler.codec.memcache.DefaultLastMemcacheContent;
import io.netty.handler.codec.memcache.MemcacheContent;
import io.netty.util.ReferenceCounted;

public interface LastMemcacheContent
extends MemcacheContent {
    public static final LastMemcacheContent EMPTY_LAST_CONTENT = new LastMemcacheContent(){

        @Override
        public LastMemcacheContent copy() {
            return EMPTY_LAST_CONTENT;
        }

        @Override
        public LastMemcacheContent duplicate() {
            return this;
        }

        @Override
        public LastMemcacheContent retainedDuplicate() {
            return this;
        }

        @Override
        public LastMemcacheContent replace(ByteBuf content) {
            return new DefaultLastMemcacheContent(content);
        }

        @Override
        public LastMemcacheContent retain(int increment) {
            return this;
        }

        @Override
        public LastMemcacheContent retain() {
            return this;
        }

        @Override
        public LastMemcacheContent touch() {
            return this;
        }

        @Override
        public LastMemcacheContent touch(Object hint) {
            return this;
        }

        @Override
        public ByteBuf content() {
            return Unpooled.EMPTY_BUFFER;
        }

        @Override
        public DecoderResult decoderResult() {
            return DecoderResult.SUCCESS;
        }

        @Override
        public void setDecoderResult(DecoderResult result) {
            throw new UnsupportedOperationException("read only");
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
    };

    @Override
    public LastMemcacheContent copy();

    @Override
    public LastMemcacheContent duplicate();

    @Override
    public LastMemcacheContent retainedDuplicate();

    @Override
    public LastMemcacheContent replace(ByteBuf var1);

    @Override
    public LastMemcacheContent retain(int var1);

    @Override
    public LastMemcacheContent retain();

    @Override
    public LastMemcacheContent touch();

    @Override
    public LastMemcacheContent touch(Object var1);

}

