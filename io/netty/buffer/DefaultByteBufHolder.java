/*
 * Decompiled with CFR 0_132.
 */
package io.netty.buffer;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufHolder;
import io.netty.util.IllegalReferenceCountException;
import io.netty.util.ReferenceCounted;
import io.netty.util.internal.StringUtil;

public class DefaultByteBufHolder
implements ByteBufHolder {
    private final ByteBuf data;

    public DefaultByteBufHolder(ByteBuf data) {
        if (data == null) {
            throw new NullPointerException("data");
        }
        this.data = data;
    }

    @Override
    public ByteBuf content() {
        if (this.data.refCnt() <= 0) {
            throw new IllegalReferenceCountException(this.data.refCnt());
        }
        return this.data;
    }

    @Override
    public ByteBufHolder copy() {
        return this.replace(this.data.copy());
    }

    @Override
    public ByteBufHolder duplicate() {
        return this.replace(this.data.duplicate());
    }

    @Override
    public ByteBufHolder retainedDuplicate() {
        return this.replace(this.data.retainedDuplicate());
    }

    @Override
    public ByteBufHolder replace(ByteBuf content) {
        return new DefaultByteBufHolder(content);
    }

    @Override
    public int refCnt() {
        return this.data.refCnt();
    }

    @Override
    public ByteBufHolder retain() {
        this.data.retain();
        return this;
    }

    @Override
    public ByteBufHolder retain(int increment) {
        this.data.retain(increment);
        return this;
    }

    @Override
    public ByteBufHolder touch() {
        this.data.touch();
        return this;
    }

    @Override
    public ByteBufHolder touch(Object hint) {
        this.data.touch(hint);
        return this;
    }

    @Override
    public boolean release() {
        return this.data.release();
    }

    @Override
    public boolean release(int decrement) {
        return this.data.release(decrement);
    }

    protected final String contentToString() {
        return this.data.toString();
    }

    public String toString() {
        return StringUtil.simpleClassName(this) + '(' + this.contentToString() + ')';
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof ByteBufHolder) {
            return this.data.equals(((ByteBufHolder)o).content());
        }
        return false;
    }

    public int hashCode() {
        return this.data.hashCode();
    }
}

