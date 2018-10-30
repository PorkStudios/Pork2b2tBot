/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec.http;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufHolder;
import io.netty.handler.codec.DecoderResult;
import io.netty.handler.codec.http.DefaultHttpObject;
import io.netty.handler.codec.http.HttpContent;
import io.netty.util.ReferenceCounted;
import io.netty.util.internal.StringUtil;

public class DefaultHttpContent
extends DefaultHttpObject
implements HttpContent {
    private final ByteBuf content;

    public DefaultHttpContent(ByteBuf content) {
        if (content == null) {
            throw new NullPointerException("content");
        }
        this.content = content;
    }

    @Override
    public ByteBuf content() {
        return this.content;
    }

    @Override
    public HttpContent copy() {
        return this.replace(this.content.copy());
    }

    @Override
    public HttpContent duplicate() {
        return this.replace(this.content.duplicate());
    }

    @Override
    public HttpContent retainedDuplicate() {
        return this.replace(this.content.retainedDuplicate());
    }

    @Override
    public HttpContent replace(ByteBuf content) {
        return new DefaultHttpContent(content);
    }

    @Override
    public int refCnt() {
        return this.content.refCnt();
    }

    @Override
    public HttpContent retain() {
        this.content.retain();
        return this;
    }

    @Override
    public HttpContent retain(int increment) {
        this.content.retain(increment);
        return this;
    }

    @Override
    public HttpContent touch() {
        this.content.touch();
        return this;
    }

    @Override
    public HttpContent touch(Object hint) {
        this.content.touch(hint);
        return this;
    }

    @Override
    public boolean release() {
        return this.content.release();
    }

    @Override
    public boolean release(int decrement) {
        return this.content.release(decrement);
    }

    public String toString() {
        return StringUtil.simpleClassName(this) + "(data: " + this.content() + ", decoderResult: " + this.decoderResult() + ')';
    }
}

