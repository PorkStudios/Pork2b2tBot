/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec.memcache;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufHolder;
import io.netty.handler.codec.DecoderResult;
import io.netty.handler.codec.memcache.AbstractMemcacheObject;
import io.netty.handler.codec.memcache.MemcacheContent;
import io.netty.util.ReferenceCounted;
import io.netty.util.internal.StringUtil;

public class DefaultMemcacheContent
extends AbstractMemcacheObject
implements MemcacheContent {
    private final ByteBuf content;

    public DefaultMemcacheContent(ByteBuf content) {
        if (content == null) {
            throw new NullPointerException("Content cannot be null.");
        }
        this.content = content;
    }

    @Override
    public ByteBuf content() {
        return this.content;
    }

    @Override
    public MemcacheContent copy() {
        return this.replace(this.content.copy());
    }

    @Override
    public MemcacheContent duplicate() {
        return this.replace(this.content.duplicate());
    }

    @Override
    public MemcacheContent retainedDuplicate() {
        return this.replace(this.content.retainedDuplicate());
    }

    @Override
    public MemcacheContent replace(ByteBuf content) {
        return new DefaultMemcacheContent(content);
    }

    @Override
    public MemcacheContent retain() {
        super.retain();
        return this;
    }

    @Override
    public MemcacheContent retain(int increment) {
        super.retain(increment);
        return this;
    }

    @Override
    public MemcacheContent touch() {
        super.touch();
        return this;
    }

    @Override
    public MemcacheContent touch(Object hint) {
        this.content.touch(hint);
        return this;
    }

    @Override
    protected void deallocate() {
        this.content.release();
    }

    public String toString() {
        return StringUtil.simpleClassName(this) + "(data: " + this.content() + ", decoderResult: " + this.decoderResult() + ')';
    }
}

