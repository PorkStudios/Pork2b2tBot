/*
 * Decompiled with CFR 0_132.
 */
package io.netty.buffer;

import io.netty.buffer.AbstractByteBuf;
import io.netty.buffer.AbstractUnsafeSwappedByteBuf;
import io.netty.buffer.ByteBuf;
import io.netty.util.internal.PlatformDependent;

final class UnsafeHeapSwappedByteBuf
extends AbstractUnsafeSwappedByteBuf {
    UnsafeHeapSwappedByteBuf(AbstractByteBuf buf) {
        super(buf);
    }

    private static int idx(ByteBuf wrapped, int index) {
        return wrapped.arrayOffset() + index;
    }

    @Override
    protected long _getLong(AbstractByteBuf wrapped, int index) {
        return PlatformDependent.getLong(wrapped.array(), UnsafeHeapSwappedByteBuf.idx(wrapped, index));
    }

    @Override
    protected int _getInt(AbstractByteBuf wrapped, int index) {
        return PlatformDependent.getInt(wrapped.array(), UnsafeHeapSwappedByteBuf.idx(wrapped, index));
    }

    @Override
    protected short _getShort(AbstractByteBuf wrapped, int index) {
        return PlatformDependent.getShort(wrapped.array(), UnsafeHeapSwappedByteBuf.idx(wrapped, index));
    }

    @Override
    protected void _setShort(AbstractByteBuf wrapped, int index, short value) {
        PlatformDependent.putShort(wrapped.array(), UnsafeHeapSwappedByteBuf.idx(wrapped, index), value);
    }

    @Override
    protected void _setInt(AbstractByteBuf wrapped, int index, int value) {
        PlatformDependent.putInt(wrapped.array(), UnsafeHeapSwappedByteBuf.idx(wrapped, index), value);
    }

    @Override
    protected void _setLong(AbstractByteBuf wrapped, int index, long value) {
        PlatformDependent.putLong(wrapped.array(), UnsafeHeapSwappedByteBuf.idx(wrapped, index), value);
    }
}

