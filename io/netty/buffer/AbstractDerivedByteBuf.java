/*
 * Decompiled with CFR 0_132.
 */
package io.netty.buffer;

import io.netty.buffer.AbstractByteBuf;
import io.netty.buffer.ByteBuf;
import io.netty.util.ReferenceCounted;
import java.nio.ByteBuffer;

@Deprecated
public abstract class AbstractDerivedByteBuf
extends AbstractByteBuf {
    protected AbstractDerivedByteBuf(int maxCapacity) {
        super(maxCapacity);
    }

    @Override
    public final int refCnt() {
        return this.refCnt0();
    }

    int refCnt0() {
        return this.unwrap().refCnt();
    }

    @Override
    public final ByteBuf retain() {
        return this.retain0();
    }

    ByteBuf retain0() {
        this.unwrap().retain();
        return this;
    }

    @Override
    public final ByteBuf retain(int increment) {
        return this.retain0(increment);
    }

    ByteBuf retain0(int increment) {
        this.unwrap().retain(increment);
        return this;
    }

    @Override
    public final ByteBuf touch() {
        return this.touch0();
    }

    ByteBuf touch0() {
        this.unwrap().touch();
        return this;
    }

    @Override
    public final ByteBuf touch(Object hint) {
        return this.touch0(hint);
    }

    ByteBuf touch0(Object hint) {
        this.unwrap().touch(hint);
        return this;
    }

    @Override
    public final boolean release() {
        return this.release0();
    }

    boolean release0() {
        return this.unwrap().release();
    }

    @Override
    public final boolean release(int decrement) {
        return this.release0(decrement);
    }

    boolean release0(int decrement) {
        return this.unwrap().release(decrement);
    }

    @Override
    public boolean isReadOnly() {
        return this.unwrap().isReadOnly();
    }

    @Override
    public ByteBuffer internalNioBuffer(int index, int length) {
        return this.nioBuffer(index, length);
    }

    @Override
    public ByteBuffer nioBuffer(int index, int length) {
        return this.unwrap().nioBuffer(index, length);
    }
}

