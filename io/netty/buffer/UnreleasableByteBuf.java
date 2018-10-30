/*
 * Decompiled with CFR 0_132.
 */
package io.netty.buffer;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.SwappedByteBuf;
import io.netty.buffer.WrappedByteBuf;
import io.netty.util.ReferenceCounted;
import java.nio.ByteOrder;

final class UnreleasableByteBuf
extends WrappedByteBuf {
    private SwappedByteBuf swappedBuf;

    UnreleasableByteBuf(ByteBuf buf) {
        super(buf instanceof UnreleasableByteBuf ? buf.unwrap() : buf);
    }

    @Override
    public ByteBuf order(ByteOrder endianness) {
        if (endianness == null) {
            throw new NullPointerException("endianness");
        }
        if (endianness == this.order()) {
            return this;
        }
        SwappedByteBuf swappedBuf = this.swappedBuf;
        if (swappedBuf == null) {
            this.swappedBuf = swappedBuf = new SwappedByteBuf(this);
        }
        return swappedBuf;
    }

    @Override
    public ByteBuf asReadOnly() {
        return this.buf.isReadOnly() ? this : new UnreleasableByteBuf(this.buf.asReadOnly());
    }

    @Override
    public ByteBuf readSlice(int length) {
        return new UnreleasableByteBuf(this.buf.readSlice(length));
    }

    @Override
    public ByteBuf readRetainedSlice(int length) {
        return this.readSlice(length);
    }

    @Override
    public ByteBuf slice() {
        return new UnreleasableByteBuf(this.buf.slice());
    }

    @Override
    public ByteBuf retainedSlice() {
        return this.slice();
    }

    @Override
    public ByteBuf slice(int index, int length) {
        return new UnreleasableByteBuf(this.buf.slice(index, length));
    }

    @Override
    public ByteBuf retainedSlice(int index, int length) {
        return this.slice(index, length);
    }

    @Override
    public ByteBuf duplicate() {
        return new UnreleasableByteBuf(this.buf.duplicate());
    }

    @Override
    public ByteBuf retainedDuplicate() {
        return this.duplicate();
    }

    @Override
    public ByteBuf retain(int increment) {
        return this;
    }

    @Override
    public ByteBuf retain() {
        return this;
    }

    @Override
    public ByteBuf touch() {
        return this;
    }

    @Override
    public ByteBuf touch(Object hint) {
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
}

