/*
 * Decompiled with CFR 0_132.
 */
package io.netty.buffer;

import io.netty.buffer.AbstractByteBuf;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledHeapByteBuf;
import io.netty.buffer.SwappedByteBuf;
import io.netty.buffer.UnsafeByteBufUtil;
import io.netty.buffer.UnsafeHeapSwappedByteBuf;
import io.netty.util.Recycler;
import io.netty.util.internal.PlatformDependent;

final class PooledUnsafeHeapByteBuf
extends PooledHeapByteBuf {
    private static final Recycler<PooledUnsafeHeapByteBuf> RECYCLER = new Recycler<PooledUnsafeHeapByteBuf>(){

        @Override
        protected PooledUnsafeHeapByteBuf newObject(Recycler.Handle<PooledUnsafeHeapByteBuf> handle) {
            return new PooledUnsafeHeapByteBuf(handle, 0);
        }
    };

    static PooledUnsafeHeapByteBuf newUnsafeInstance(int maxCapacity) {
        PooledUnsafeHeapByteBuf buf = RECYCLER.get();
        buf.reuse(maxCapacity);
        return buf;
    }

    private PooledUnsafeHeapByteBuf(Recycler.Handle<PooledUnsafeHeapByteBuf> recyclerHandle, int maxCapacity) {
        super(recyclerHandle, maxCapacity);
    }

    @Override
    protected byte _getByte(int index) {
        return UnsafeByteBufUtil.getByte((byte[])this.memory, this.idx(index));
    }

    @Override
    protected short _getShort(int index) {
        return UnsafeByteBufUtil.getShort((byte[])this.memory, this.idx(index));
    }

    @Override
    protected short _getShortLE(int index) {
        return UnsafeByteBufUtil.getShortLE((byte[])this.memory, this.idx(index));
    }

    @Override
    protected int _getUnsignedMedium(int index) {
        return UnsafeByteBufUtil.getUnsignedMedium((byte[])this.memory, this.idx(index));
    }

    @Override
    protected int _getUnsignedMediumLE(int index) {
        return UnsafeByteBufUtil.getUnsignedMediumLE((byte[])this.memory, this.idx(index));
    }

    @Override
    protected int _getInt(int index) {
        return UnsafeByteBufUtil.getInt((byte[])this.memory, this.idx(index));
    }

    @Override
    protected int _getIntLE(int index) {
        return UnsafeByteBufUtil.getIntLE((byte[])this.memory, this.idx(index));
    }

    @Override
    protected long _getLong(int index) {
        return UnsafeByteBufUtil.getLong((byte[])this.memory, this.idx(index));
    }

    @Override
    protected long _getLongLE(int index) {
        return UnsafeByteBufUtil.getLongLE((byte[])this.memory, this.idx(index));
    }

    @Override
    protected void _setByte(int index, int value) {
        UnsafeByteBufUtil.setByte((byte[])this.memory, this.idx(index), value);
    }

    @Override
    protected void _setShort(int index, int value) {
        UnsafeByteBufUtil.setShort((byte[])this.memory, this.idx(index), value);
    }

    @Override
    protected void _setShortLE(int index, int value) {
        UnsafeByteBufUtil.setShortLE((byte[])this.memory, this.idx(index), value);
    }

    @Override
    protected void _setMedium(int index, int value) {
        UnsafeByteBufUtil.setMedium((byte[])this.memory, this.idx(index), value);
    }

    @Override
    protected void _setMediumLE(int index, int value) {
        UnsafeByteBufUtil.setMediumLE((byte[])this.memory, this.idx(index), value);
    }

    @Override
    protected void _setInt(int index, int value) {
        UnsafeByteBufUtil.setInt((byte[])this.memory, this.idx(index), value);
    }

    @Override
    protected void _setIntLE(int index, int value) {
        UnsafeByteBufUtil.setIntLE((byte[])this.memory, this.idx(index), value);
    }

    @Override
    protected void _setLong(int index, long value) {
        UnsafeByteBufUtil.setLong((byte[])this.memory, this.idx(index), value);
    }

    @Override
    protected void _setLongLE(int index, long value) {
        UnsafeByteBufUtil.setLongLE((byte[])this.memory, this.idx(index), value);
    }

    @Override
    public ByteBuf setZero(int index, int length) {
        if (PlatformDependent.javaVersion() >= 7) {
            this.checkIndex(index, length);
            UnsafeByteBufUtil.setZero((byte[])this.memory, this.idx(index), length);
            return this;
        }
        return super.setZero(index, length);
    }

    @Override
    public ByteBuf writeZero(int length) {
        if (PlatformDependent.javaVersion() >= 7) {
            this.ensureWritable(length);
            int wIndex = this.writerIndex;
            UnsafeByteBufUtil.setZero((byte[])this.memory, this.idx(wIndex), length);
            this.writerIndex = wIndex + length;
            return this;
        }
        return super.writeZero(length);
    }

    @Deprecated
    @Override
    protected SwappedByteBuf newSwappedByteBuf() {
        if (PlatformDependent.isUnaligned()) {
            return new UnsafeHeapSwappedByteBuf(this);
        }
        return super.newSwappedByteBuf();
    }

}

