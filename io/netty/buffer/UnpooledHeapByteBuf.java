/*
 * Decompiled with CFR 0_132.
 */
package io.netty.buffer;

import io.netty.buffer.AbstractReferenceCountedByteBuf;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.HeapByteBufUtil;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.PlatformDependent;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.FileChannel;
import java.nio.channels.GatheringByteChannel;
import java.nio.channels.ScatteringByteChannel;

public class UnpooledHeapByteBuf
extends AbstractReferenceCountedByteBuf {
    private final ByteBufAllocator alloc;
    byte[] array;
    private ByteBuffer tmpNioBuf;

    public UnpooledHeapByteBuf(ByteBufAllocator alloc, int initialCapacity, int maxCapacity) {
        super(maxCapacity);
        ObjectUtil.checkNotNull(alloc, "alloc");
        if (initialCapacity > maxCapacity) {
            throw new IllegalArgumentException(String.format("initialCapacity(%d) > maxCapacity(%d)", initialCapacity, maxCapacity));
        }
        this.alloc = alloc;
        this.setArray(this.allocateArray(initialCapacity));
        this.setIndex(0, 0);
    }

    protected UnpooledHeapByteBuf(ByteBufAllocator alloc, byte[] initialArray, int maxCapacity) {
        super(maxCapacity);
        ObjectUtil.checkNotNull(alloc, "alloc");
        ObjectUtil.checkNotNull(initialArray, "initialArray");
        if (initialArray.length > maxCapacity) {
            throw new IllegalArgumentException(String.format("initialCapacity(%d) > maxCapacity(%d)", initialArray.length, maxCapacity));
        }
        this.alloc = alloc;
        this.setArray(initialArray);
        this.setIndex(0, initialArray.length);
    }

    byte[] allocateArray(int initialCapacity) {
        return new byte[initialCapacity];
    }

    void freeArray(byte[] array) {
    }

    private void setArray(byte[] initialArray) {
        this.array = initialArray;
        this.tmpNioBuf = null;
    }

    @Override
    public ByteBufAllocator alloc() {
        return this.alloc;
    }

    @Override
    public ByteOrder order() {
        return ByteOrder.BIG_ENDIAN;
    }

    @Override
    public boolean isDirect() {
        return false;
    }

    @Override
    public int capacity() {
        this.ensureAccessible();
        return this.array.length;
    }

    @Override
    public ByteBuf capacity(int newCapacity) {
        this.checkNewCapacity(newCapacity);
        int oldCapacity = this.array.length;
        byte[] oldArray = this.array;
        if (newCapacity > oldCapacity) {
            byte[] newArray = this.allocateArray(newCapacity);
            System.arraycopy(oldArray, 0, newArray, 0, oldArray.length);
            this.setArray(newArray);
            this.freeArray(oldArray);
        } else if (newCapacity < oldCapacity) {
            byte[] newArray = this.allocateArray(newCapacity);
            int readerIndex = this.readerIndex();
            if (readerIndex < newCapacity) {
                int writerIndex = this.writerIndex();
                if (writerIndex > newCapacity) {
                    writerIndex = newCapacity;
                    this.writerIndex(writerIndex);
                }
                System.arraycopy(oldArray, readerIndex, newArray, readerIndex, writerIndex - readerIndex);
            } else {
                this.setIndex(newCapacity, newCapacity);
            }
            this.setArray(newArray);
            this.freeArray(oldArray);
        }
        return this;
    }

    @Override
    public boolean hasArray() {
        return true;
    }

    @Override
    public byte[] array() {
        this.ensureAccessible();
        return this.array;
    }

    @Override
    public int arrayOffset() {
        return 0;
    }

    @Override
    public boolean hasMemoryAddress() {
        return false;
    }

    @Override
    public long memoryAddress() {
        throw new UnsupportedOperationException();
    }

    @Override
    public ByteBuf getBytes(int index, ByteBuf dst, int dstIndex, int length) {
        this.checkDstIndex(index, length, dstIndex, dst.capacity());
        if (dst.hasMemoryAddress()) {
            PlatformDependent.copyMemory(this.array, index, dst.memoryAddress() + (long)dstIndex, (long)length);
        } else if (dst.hasArray()) {
            this.getBytes(index, dst.array(), dst.arrayOffset() + dstIndex, length);
        } else {
            dst.setBytes(dstIndex, this.array, index, length);
        }
        return this;
    }

    @Override
    public ByteBuf getBytes(int index, byte[] dst, int dstIndex, int length) {
        this.checkDstIndex(index, length, dstIndex, dst.length);
        System.arraycopy(this.array, index, dst, dstIndex, length);
        return this;
    }

    @Override
    public ByteBuf getBytes(int index, ByteBuffer dst) {
        this.checkIndex(index, dst.remaining());
        dst.put(this.array, index, dst.remaining());
        return this;
    }

    @Override
    public ByteBuf getBytes(int index, OutputStream out, int length) throws IOException {
        this.ensureAccessible();
        out.write(this.array, index, length);
        return this;
    }

    @Override
    public int getBytes(int index, GatheringByteChannel out, int length) throws IOException {
        this.ensureAccessible();
        return this.getBytes(index, out, length, false);
    }

    @Override
    public int getBytes(int index, FileChannel out, long position, int length) throws IOException {
        this.ensureAccessible();
        return this.getBytes(index, out, position, length, false);
    }

    private int getBytes(int index, GatheringByteChannel out, int length, boolean internal) throws IOException {
        this.ensureAccessible();
        ByteBuffer tmpBuf = internal ? this.internalNioBuffer() : ByteBuffer.wrap(this.array);
        return out.write((ByteBuffer)tmpBuf.clear().position(index).limit(index + length));
    }

    private int getBytes(int index, FileChannel out, long position, int length, boolean internal) throws IOException {
        this.ensureAccessible();
        ByteBuffer tmpBuf = internal ? this.internalNioBuffer() : ByteBuffer.wrap(this.array);
        return out.write((ByteBuffer)tmpBuf.clear().position(index).limit(index + length), position);
    }

    @Override
    public int readBytes(GatheringByteChannel out, int length) throws IOException {
        this.checkReadableBytes(length);
        int readBytes = this.getBytes(this.readerIndex, out, length, true);
        this.readerIndex += readBytes;
        return readBytes;
    }

    @Override
    public int readBytes(FileChannel out, long position, int length) throws IOException {
        this.checkReadableBytes(length);
        int readBytes = this.getBytes(this.readerIndex, out, position, length, true);
        this.readerIndex += readBytes;
        return readBytes;
    }

    @Override
    public ByteBuf setBytes(int index, ByteBuf src, int srcIndex, int length) {
        this.checkSrcIndex(index, length, srcIndex, src.capacity());
        if (src.hasMemoryAddress()) {
            PlatformDependent.copyMemory(src.memoryAddress() + (long)srcIndex, this.array, index, (long)length);
        } else if (src.hasArray()) {
            this.setBytes(index, src.array(), src.arrayOffset() + srcIndex, length);
        } else {
            src.getBytes(srcIndex, this.array, index, length);
        }
        return this;
    }

    @Override
    public ByteBuf setBytes(int index, byte[] src, int srcIndex, int length) {
        this.checkSrcIndex(index, length, srcIndex, src.length);
        System.arraycopy(src, srcIndex, this.array, index, length);
        return this;
    }

    @Override
    public ByteBuf setBytes(int index, ByteBuffer src) {
        this.ensureAccessible();
        src.get(this.array, index, src.remaining());
        return this;
    }

    @Override
    public int setBytes(int index, InputStream in, int length) throws IOException {
        this.ensureAccessible();
        return in.read(this.array, index, length);
    }

    @Override
    public int setBytes(int index, ScatteringByteChannel in, int length) throws IOException {
        this.ensureAccessible();
        try {
            return in.read((ByteBuffer)this.internalNioBuffer().clear().position(index).limit(index + length));
        }
        catch (ClosedChannelException ignored) {
            return -1;
        }
    }

    @Override
    public int setBytes(int index, FileChannel in, long position, int length) throws IOException {
        this.ensureAccessible();
        try {
            return in.read((ByteBuffer)this.internalNioBuffer().clear().position(index).limit(index + length), position);
        }
        catch (ClosedChannelException ignored) {
            return -1;
        }
    }

    @Override
    public int nioBufferCount() {
        return 1;
    }

    @Override
    public ByteBuffer nioBuffer(int index, int length) {
        this.ensureAccessible();
        return ByteBuffer.wrap(this.array, index, length).slice();
    }

    @Override
    public ByteBuffer[] nioBuffers(int index, int length) {
        return new ByteBuffer[]{this.nioBuffer(index, length)};
    }

    @Override
    public ByteBuffer internalNioBuffer(int index, int length) {
        this.checkIndex(index, length);
        return (ByteBuffer)this.internalNioBuffer().clear().position(index).limit(index + length);
    }

    @Override
    public byte getByte(int index) {
        this.ensureAccessible();
        return this._getByte(index);
    }

    @Override
    protected byte _getByte(int index) {
        return HeapByteBufUtil.getByte(this.array, index);
    }

    @Override
    public short getShort(int index) {
        this.ensureAccessible();
        return this._getShort(index);
    }

    @Override
    protected short _getShort(int index) {
        return HeapByteBufUtil.getShort(this.array, index);
    }

    @Override
    public short getShortLE(int index) {
        this.ensureAccessible();
        return this._getShortLE(index);
    }

    @Override
    protected short _getShortLE(int index) {
        return HeapByteBufUtil.getShortLE(this.array, index);
    }

    @Override
    public int getUnsignedMedium(int index) {
        this.ensureAccessible();
        return this._getUnsignedMedium(index);
    }

    @Override
    protected int _getUnsignedMedium(int index) {
        return HeapByteBufUtil.getUnsignedMedium(this.array, index);
    }

    @Override
    public int getUnsignedMediumLE(int index) {
        this.ensureAccessible();
        return this._getUnsignedMediumLE(index);
    }

    @Override
    protected int _getUnsignedMediumLE(int index) {
        return HeapByteBufUtil.getUnsignedMediumLE(this.array, index);
    }

    @Override
    public int getInt(int index) {
        this.ensureAccessible();
        return this._getInt(index);
    }

    @Override
    protected int _getInt(int index) {
        return HeapByteBufUtil.getInt(this.array, index);
    }

    @Override
    public int getIntLE(int index) {
        this.ensureAccessible();
        return this._getIntLE(index);
    }

    @Override
    protected int _getIntLE(int index) {
        return HeapByteBufUtil.getIntLE(this.array, index);
    }

    @Override
    public long getLong(int index) {
        this.ensureAccessible();
        return this._getLong(index);
    }

    @Override
    protected long _getLong(int index) {
        return HeapByteBufUtil.getLong(this.array, index);
    }

    @Override
    public long getLongLE(int index) {
        this.ensureAccessible();
        return this._getLongLE(index);
    }

    @Override
    protected long _getLongLE(int index) {
        return HeapByteBufUtil.getLongLE(this.array, index);
    }

    @Override
    public ByteBuf setByte(int index, int value) {
        this.ensureAccessible();
        this._setByte(index, value);
        return this;
    }

    @Override
    protected void _setByte(int index, int value) {
        HeapByteBufUtil.setByte(this.array, index, value);
    }

    @Override
    public ByteBuf setShort(int index, int value) {
        this.ensureAccessible();
        this._setShort(index, value);
        return this;
    }

    @Override
    protected void _setShort(int index, int value) {
        HeapByteBufUtil.setShort(this.array, index, value);
    }

    @Override
    public ByteBuf setShortLE(int index, int value) {
        this.ensureAccessible();
        this._setShortLE(index, value);
        return this;
    }

    @Override
    protected void _setShortLE(int index, int value) {
        HeapByteBufUtil.setShortLE(this.array, index, value);
    }

    @Override
    public ByteBuf setMedium(int index, int value) {
        this.ensureAccessible();
        this._setMedium(index, value);
        return this;
    }

    @Override
    protected void _setMedium(int index, int value) {
        HeapByteBufUtil.setMedium(this.array, index, value);
    }

    @Override
    public ByteBuf setMediumLE(int index, int value) {
        this.ensureAccessible();
        this._setMediumLE(index, value);
        return this;
    }

    @Override
    protected void _setMediumLE(int index, int value) {
        HeapByteBufUtil.setMediumLE(this.array, index, value);
    }

    @Override
    public ByteBuf setInt(int index, int value) {
        this.ensureAccessible();
        this._setInt(index, value);
        return this;
    }

    @Override
    protected void _setInt(int index, int value) {
        HeapByteBufUtil.setInt(this.array, index, value);
    }

    @Override
    public ByteBuf setIntLE(int index, int value) {
        this.ensureAccessible();
        this._setIntLE(index, value);
        return this;
    }

    @Override
    protected void _setIntLE(int index, int value) {
        HeapByteBufUtil.setIntLE(this.array, index, value);
    }

    @Override
    public ByteBuf setLong(int index, long value) {
        this.ensureAccessible();
        this._setLong(index, value);
        return this;
    }

    @Override
    protected void _setLong(int index, long value) {
        HeapByteBufUtil.setLong(this.array, index, value);
    }

    @Override
    public ByteBuf setLongLE(int index, long value) {
        this.ensureAccessible();
        this._setLongLE(index, value);
        return this;
    }

    @Override
    protected void _setLongLE(int index, long value) {
        HeapByteBufUtil.setLongLE(this.array, index, value);
    }

    @Override
    public ByteBuf copy(int index, int length) {
        this.checkIndex(index, length);
        byte[] copiedArray = new byte[length];
        System.arraycopy(this.array, index, copiedArray, 0, length);
        return new UnpooledHeapByteBuf(this.alloc(), copiedArray, this.maxCapacity());
    }

    private ByteBuffer internalNioBuffer() {
        ByteBuffer tmpNioBuf = this.tmpNioBuf;
        if (tmpNioBuf == null) {
            this.tmpNioBuf = tmpNioBuf = ByteBuffer.wrap(this.array);
        }
        return tmpNioBuf;
    }

    @Override
    protected void deallocate() {
        this.freeArray(this.array);
        this.array = null;
    }

    @Override
    public ByteBuf unwrap() {
        return null;
    }
}

