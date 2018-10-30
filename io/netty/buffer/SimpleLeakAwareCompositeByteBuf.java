/*
 * Decompiled with CFR 0_132.
 */
package io.netty.buffer;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.CompositeByteBuf;
import io.netty.buffer.SimpleLeakAwareByteBuf;
import io.netty.buffer.WrappedCompositeByteBuf;
import io.netty.util.ResourceLeakTracker;
import io.netty.util.internal.ObjectUtil;
import java.nio.ByteOrder;

class SimpleLeakAwareCompositeByteBuf
extends WrappedCompositeByteBuf {
    final ResourceLeakTracker<ByteBuf> leak;

    SimpleLeakAwareCompositeByteBuf(CompositeByteBuf wrapped, ResourceLeakTracker<ByteBuf> leak) {
        super(wrapped);
        this.leak = ObjectUtil.checkNotNull(leak, "leak");
    }

    @Override
    public boolean release() {
        ByteBuf unwrapped = this.unwrap();
        if (super.release()) {
            this.closeLeak(unwrapped);
            return true;
        }
        return false;
    }

    @Override
    public boolean release(int decrement) {
        ByteBuf unwrapped = this.unwrap();
        if (super.release(decrement)) {
            this.closeLeak(unwrapped);
            return true;
        }
        return false;
    }

    private void closeLeak(ByteBuf trackedByteBuf) {
        boolean closed = this.leak.close(trackedByteBuf);
        assert (closed);
    }

    @Override
    public ByteBuf order(ByteOrder endianness) {
        if (this.order() == endianness) {
            return this;
        }
        return this.newLeakAwareByteBuf(super.order(endianness));
    }

    @Override
    public ByteBuf slice() {
        return this.newLeakAwareByteBuf(super.slice());
    }

    @Override
    public ByteBuf retainedSlice() {
        return this.newLeakAwareByteBuf(super.retainedSlice());
    }

    @Override
    public ByteBuf slice(int index, int length) {
        return this.newLeakAwareByteBuf(super.slice(index, length));
    }

    @Override
    public ByteBuf retainedSlice(int index, int length) {
        return this.newLeakAwareByteBuf(super.retainedSlice(index, length));
    }

    @Override
    public ByteBuf duplicate() {
        return this.newLeakAwareByteBuf(super.duplicate());
    }

    @Override
    public ByteBuf retainedDuplicate() {
        return this.newLeakAwareByteBuf(super.retainedDuplicate());
    }

    @Override
    public ByteBuf readSlice(int length) {
        return this.newLeakAwareByteBuf(super.readSlice(length));
    }

    @Override
    public ByteBuf readRetainedSlice(int length) {
        return this.newLeakAwareByteBuf(super.readRetainedSlice(length));
    }

    @Override
    public ByteBuf asReadOnly() {
        return this.newLeakAwareByteBuf(super.asReadOnly());
    }

    private SimpleLeakAwareByteBuf newLeakAwareByteBuf(ByteBuf wrapped) {
        return this.newLeakAwareByteBuf(wrapped, this.unwrap(), this.leak);
    }

    protected SimpleLeakAwareByteBuf newLeakAwareByteBuf(ByteBuf wrapped, ByteBuf trackedByteBuf, ResourceLeakTracker<ByteBuf> leakTracker) {
        return new SimpleLeakAwareByteBuf(wrapped, trackedByteBuf, leakTracker);
    }
}

