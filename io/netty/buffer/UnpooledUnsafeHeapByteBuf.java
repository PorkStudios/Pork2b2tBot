/*
 * Decompiled with CFR 0_132.
 */
package io.netty.buffer;

import io.netty.buffer.AbstractByteBuf;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.SwappedByteBuf;
import io.netty.buffer.UnpooledHeapByteBuf;
import io.netty.buffer.UnsafeByteBufUtil;
import io.netty.buffer.UnsafeHeapSwappedByteBuf;
import io.netty.util.internal.PlatformDependent;

class UnpooledUnsafeHeapByteBuf
extends UnpooledHeapByteBuf {
    UnpooledUnsafeHeapByteBuf(ByteBufAllocator alloc, int initialCapacity, int maxCapacity) {
        super(alloc, initialCapacity, maxCapacity);
    }

    @Override
    byte[] allocateArray(int initialCapacity) {
        return PlatformDependent.allocateUninitializedArray(initialCapacity);
    }

    @Override
    public byte getByte(int index) {
        this.checkIndex(index);
        return this._getByte(index);
    }

    @Override
    protected byte _getByte(int index) {
        return UnsafeByteBufUtil.getByte(this.array, index);
    }

    @Override
    public short getShort(int index) {
        this.checkIndex(index, 2);
        return this._getShort(index);
    }

    @Override
    protected short _getShort(int index) {
        return UnsafeByteBufUtil.getShort(this.array, index);
    }

    @Override
    public short getShortLE(int index) {
        this.checkIndex(index, 2);
        return this._getShortLE(index);
    }

    @Override
    protected short _getShortLE(int index) {
        return UnsafeByteBufUtil.getShortLE(this.array, index);
    }

    @Override
    public int getUnsignedMedium(int index) {
        this.checkIndex(index, 3);
        return this._getUnsignedMedium(index);
    }

    @Override
    protected int _getUnsignedMedium(int index) {
        return UnsafeByteBufUtil.getUnsignedMedium(this.array, index);
    }

    @Override
    public int getUnsignedMediumLE(int index) {
        this.checkIndex(index, 3);
        return this._getUnsignedMediumLE(index);
    }

    @Override
    protected int _getUnsignedMediumLE(int index) {
        return UnsafeByteBufUtil.getUnsignedMediumLE(this.array, index);
    }

    @Override
    public int getInt(int index) {
        this.checkIndex(index, 4);
        return this._getInt(index);
    }

    @Override
    protected int _getInt(int index) {
        return UnsafeByteBufUtil.getInt(this.array, index);
    }

    @Override
    public int getIntLE(int index) {
        this.checkIndex(index, 4);
        return this._getIntLE(index);
    }

    @Override
    protected int _getIntLE(int index) {
        return UnsafeByteBufUtil.getIntLE(this.array, index);
    }

    @Override
    public long getLong(int index) {
        this.checkIndex(index, 8);
        return this._getLong(index);
    }

    @Override
    protected long _getLong(int index) {
        return UnsafeByteBufUtil.getLong(this.array, index);
    }

    @Override
    public long getLongLE(int index) {
        this.checkIndex(index, 8);
        return this._getLongLE(index);
    }

    @Override
    protected long _getLongLE(int index) {
        return UnsafeByteBufUtil.getLongLE(this.array, index);
    }

    @Override
    public ByteBuf setByte(int index, int value) {
        this.checkIndex(index);
        this._setByte(index, value);
        return this;
    }

    @Override
    protected void _setByte(int index, int value) {
        UnsafeByteBufUtil.setByte(this.array, index, value);
    }

    @Override
    public ByteBuf setShort(int index, int value) {
        this.checkIndex(index, 2);
        this._setShort(index, value);
        return this;
    }

    @Override
    protected void _setShort(int index, int value) {
        UnsafeByteBufUtil.setShort(this.array, index, value);
    }

    @Override
    public ByteBuf setShortLE(int index, int value) {
        this.checkIndex(index, 2);
        this._setShortLE(index, value);
        return this;
    }

    @Override
    protected void _setShortLE(int index, int value) {
        UnsafeByteBufUtil.setShortLE(this.array, index, value);
    }

    @Override
    public ByteBuf setMedium(int index, int value) {
        this.checkIndex(index, 3);
        this._setMedium(index, value);
        return this;
    }

    @Override
    protected void _setMedium(int index, int value) {
        UnsafeByteBufUtil.setMedium(this.array, index, value);
    }

    @Override
    public ByteBuf setMediumLE(int index, int value) {
        this.checkIndex(index, 3);
        this._setMediumLE(index, value);
        return this;
    }

    @Override
    protected void _setMediumLE(int index, int value) {
        UnsafeByteBufUtil.setMediumLE(this.array, index, value);
    }

    @Override
    public ByteBuf setInt(int index, int value) {
        this.checkIndex(index, 4);
        this._setInt(index, value);
        return this;
    }

    @Override
    protected void _setInt(int index, int value) {
        UnsafeByteBufUtil.setInt(this.array, index, value);
    }

    @Override
    public ByteBuf setIntLE(int index, int value) {
        this.checkIndex(index, 4);
        this._setIntLE(index, value);
        return this;
    }

    @Override
    protected void _setIntLE(int index, int value) {
        UnsafeByteBufUtil.setIntLE(this.array, index, value);
    }

    @Override
    public ByteBuf setLong(int index, long value) {
        this.checkIndex(index, 8);
        this._setLong(index, value);
        return this;
    }

    @Override
    protected void _setLong(int index, long value) {
        UnsafeByteBufUtil.setLong(this.array, index, value);
    }

    @Override
    public ByteBuf setLongLE(int index, long value) {
        this.checkIndex(index, 8);
        this._setLongLE(index, value);
        return this;
    }

    @Override
    protected void _setLongLE(int index, long value) {
        UnsafeByteBufUtil.setLongLE(this.array, index, value);
    }

    @Override
    public ByteBuf setZero(int index, int length) {
        if (PlatformDependent.javaVersion() >= 7) {
            this.checkIndex(index, length);
            UnsafeByteBufUtil.setZero(this.array, index, length);
            return this;
        }
        return super.setZero(index, length);
    }

    @Override
    public ByteBuf writeZero(int length) {
        if (PlatformDependent.javaVersion() >= 7) {
            this.ensureWritable(length);
            int wIndex = this.writerIndex;
            UnsafeByteBufUtil.setZero(this.array, wIndex, length);
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

