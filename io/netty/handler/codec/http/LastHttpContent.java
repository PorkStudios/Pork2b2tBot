/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec.http;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufHolder;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.DecoderResult;
import io.netty.handler.codec.http.DefaultLastHttpContent;
import io.netty.handler.codec.http.EmptyHttpHeaders;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.util.ReferenceCounted;

public interface LastHttpContent
extends HttpContent {
    public static final LastHttpContent EMPTY_LAST_CONTENT = new LastHttpContent(){

        @Override
        public ByteBuf content() {
            return Unpooled.EMPTY_BUFFER;
        }

        @Override
        public LastHttpContent copy() {
            return EMPTY_LAST_CONTENT;
        }

        @Override
        public LastHttpContent duplicate() {
            return this;
        }

        @Override
        public LastHttpContent replace(ByteBuf content) {
            return new DefaultLastHttpContent(content);
        }

        @Override
        public LastHttpContent retainedDuplicate() {
            return this;
        }

        @Override
        public HttpHeaders trailingHeaders() {
            return EmptyHttpHeaders.INSTANCE;
        }

        @Override
        public DecoderResult decoderResult() {
            return DecoderResult.SUCCESS;
        }

        @Deprecated
        @Override
        public DecoderResult getDecoderResult() {
            return this.decoderResult();
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
        public LastHttpContent retain() {
            return this;
        }

        @Override
        public LastHttpContent retain(int increment) {
            return this;
        }

        @Override
        public LastHttpContent touch() {
            return this;
        }

        @Override
        public LastHttpContent touch(Object hint) {
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

        public String toString() {
            return "EmptyLastHttpContent";
        }
    };

    public HttpHeaders trailingHeaders();

    @Override
    public LastHttpContent copy();

    @Override
    public LastHttpContent duplicate();

    @Override
    public LastHttpContent retainedDuplicate();

    @Override
    public LastHttpContent replace(ByteBuf var1);

    @Override
    public LastHttpContent retain(int var1);

    @Override
    public LastHttpContent retain();

    @Override
    public LastHttpContent touch();

    @Override
    public LastHttpContent touch(Object var1);

}

