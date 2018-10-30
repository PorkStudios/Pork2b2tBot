/*
 * Decompiled with CFR 0_132.
 */
package io.netty.buffer;

import io.netty.buffer.AbstractByteBuf;
import io.netty.buffer.AbstractReferenceCountedByteBuf;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.SwappedByteBuf;
import io.netty.buffer.UnsafeByteBufUtil;
import io.netty.buffer.UnsafeDirectSwappedByteBuf;
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

public class UnpooledUnsafeDirectByteBuf
extends AbstractReferenceCountedByteBuf {
    private final ByteBufAllocator alloc;
    private ByteBuffer tmpNioBuf;
    private int capacity;
    private boolean doNotFree;
    ByteBuffer buffer;
    long memoryAddress;

    public UnpooledUnsafeDirectByteBuf(ByteBufAllocator alloc, int initialCapacity, int maxCapacity) {
        super(maxCapacity);
        if (alloc == null) {
            throw new NullPointerException("alloc");
        }
        if (initialCapacity < 0) {
            throw new IllegalArgumentException("initialCapacity: " + initialCapacity);
        }
        if (maxCapacity < 0) {
            throw new IllegalArgumentException("maxCapacity: " + maxCapacity);
        }
        if (initialCapacity > maxCapacity) {
            throw new IllegalArgumentException(String.format("initialCapacity(%d) > maxCapacity(%d)", initialCapacity, maxCapacity));
        }
        this.alloc = alloc;
        this.setByteBuffer(this.allocateDirect(initialCapacity), false);
    }

    protected UnpooledUnsafeDirectByteBuf(ByteBufAllocator alloc, ByteBuffer initialBuffer, int maxCapacity) {
        this(alloc, initialBuffer.slice(), maxCapacity, false);
    }

    UnpooledUnsafeDirectByteBuf(ByteBufAllocator alloc, ByteBuffer initialBuffer, int maxCapacity, boolean doFree) {
        super(maxCapacity);
        if (alloc == null) {
            throw new NullPointerException("alloc");
        }
        if (initialBuffer == null) {
            throw new NullPointerException("initialBuffer");
        }
        if (!initialBuffer.isDirect()) {
            throw new IllegalArgumentException("initialBuffer is not a direct buffer.");
        }
        if (initialBuffer.isReadOnly()) {
            throw new IllegalArgumentException("initialBuffer is a read-only buffer.");
        }
        int initialCapacity = initialBuffer.remaining();
        if (initialCapacity > maxCapacity) {
            throw new IllegalArgumentException(String.format("initialCapacity(%d) > maxCapacity(%d)", initialCapacity, maxCapacity));
        }
        this.alloc = alloc;
        this.doNotFree = !doFree;
        this.setByteBuffer(initialBuffer.order(ByteOrder.BIG_ENDIAN), false);
        this.writerIndex(initialCapacity);
    }

    protected ByteBuffer allocateDirect(int initialCapacity) {
        return ByteBuffer.allocateDirect(initialCapacity);
    }

    protected void freeDirect(ByteBuffer buffer) {
        PlatformDependent.freeDirectBuffer(buffer);
    }

    final void setByteBuffer(ByteBuffer buffer, boolean tryFree) {
        ByteBuffer oldBuffer;
        if (tryFree && (oldBuffer = this.buffer) != null) {
            if (this.doNotFree) {
                this.doNotFree = false;
            } else {
                this.freeDirect(oldBuffer);
            }
        }
        this.buffer = buffer;
        this.memoryAddress = PlatformDependent.directBufferAddress(buffer);
        this.tmpNioBuf = null;
        this.capacity = buffer.remaining();
    }

    @Override
    public boolean isDirect() {
        return true;
    }

    @Override
    public int capacity() {
        return this.capacity;
    }

    @Override
    public ByteBuf capacity(int newCapacity) {
        this.checkNewCapacity(newCapacity);
        int readerIndex = this.readerIndex();
        int writerIndex = this.writerIndex();
        int oldCapacity = this.capacity;
        if (newCapacity > oldCapacity) {
            ByteBuffer oldBuffer = this.buffer;
            ByteBuffer newBuffer = this.allocateDirect(newCapacity);
            oldBuffer.position(0).limit(oldBuffer.capacity());
            newBuffer.position(0).limit(oldBuffer.capacity());
            newBuffer.put(oldBuffer);
            newBuffer.clear();
            this.setByteBuffer(newBuffer, true);
        } else if (newCapacity < oldCapacity) {
            ByteBuffer oldBuffer = this.buffer;
            ByteBuffer newBuffer = this.allocateDirect(newCapacity);
            if (readerIndex < newCapacity) {
                if (writerIndex > newCapacity) {
                    writerIndex = newCapacity;
                    this.writerIndex(writerIndex);
                }
                oldBuffer.position(readerIndex).limit(writerIndex);
                newBuffer.position(readerIndex).limit(writerIndex);
                newBuffer.put(oldBuffer);
                newBuffer.clear();
            } else {
                this.setIndex(newCapacity, newCapacity);
            }
            this.setByteBuffer(newBuffer, true);
        }
        return this;
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
    public boolean hasArray() {
        return false;
    }

    @Override
    public byte[] array() {
        throw new UnsupportedOperationException("direct buffer");
    }

    @Override
    public int arrayOffset() {
        throw new UnsupportedOperationException("direct buffer");
    }

    @Override
    public boolean hasMemoryAddress() {
        return true;
    }

    @Override
    public long memoryAddress() {
        this.ensureAccessible();
        return this.memoryAddress;
    }

    @Override
    protected byte _getByte(int index) {
        return UnsafeByteBufUtil.getByte(this.addr(index));
    }

    @Override
    protected short _getShort(int index) {
        return UnsafeByteBufUtil.getShort(this.addr(index));
    }

    @Override
    protected short _getShortLE(int index) {
        return UnsafeByteBufUtil.getShortLE(this.addr(index));
    }

    @Override
    protected int _getUnsignedMedium(int index) {
        return UnsafeByteBufUtil.getUnsignedMedium(this.addr(index));
    }

    @Override
    protected int _getUnsignedMediumLE(int index) {
        return UnsafeByteBufUtil.getUnsignedMediumLE(this.addr(index));
    }

    @Override
    protected int _getInt(int index) {
        return UnsafeByteBufUtil.getInt(this.addr(index));
    }

    @Override
    protected int _getIntLE(int index) {
        return UnsafeByteBufUtil.getIntLE(this.addr(index));
    }

    @Override
    protected long _getLong(int index) {
        return UnsafeByteBufUtil.getLong(this.addr(index));
    }

    @Override
    protected long _getLongLE(int index) {
        return UnsafeByteBufUtil.getLongLE(this.addr(index));
    }

    @Override
    public ByteBuf getBytes(int index, ByteBuf dst, int dstIndex, int length) {
        UnsafeByteBufUtil.getBytes((AbstractByteBuf)this, this.addr(index), index, dst, dstIndex, length);
        return this;
    }

    @Override
    public ByteBuf getBytes(int index, byte[] dst, int dstIndex, int length) {
        UnsafeByteBufUtil.getBytes((AbstractByteBuf)this, this.addr(index), index, dst, dstIndex, length);
        return this;
    }

    @Override
    public ByteBuf getBytes(int index, ByteBuffer dst) {
        UnsafeByteBufUtil.getBytes(this, this.addr(index), index, dst);
        return this;
    }

    @Override
    public ByteBuf readBytes(ByteBuffer dst) {
        int length = dst.remaining();
        this.checkReadableBytes(length);
        this.getBytes(this.readerIndex, dst);
        this.readerIndex += length;
        return this;
    }

    @Override
    protected void _setByte(int index, int value) {
        UnsafeByteBufUtil.setByte(this.addr(index), value);
    }

    @Override
    protected void _setShort(int index, int value) {
        UnsafeByteBufUtil.setShort(this.addr(index), value);
    }

    @Override
    protected void _setShortLE(int index, int value) {
        UnsafeByteBufUtil.setShortLE(this.addr(index), value);
    }

    @Override
    protected void _setMedium(int index, int value) {
        UnsafeByteBufUtil.setMedium(this.addr(index), value);
    }

    @Override
    protected void _setMediumLE(int index, int value) {
        UnsafeByteBufUtil.setMediumLE(this.addr(index), value);
    }

    @Override
    protected void _setInt(int index, int value) {
        UnsafeByteBufUtil.setInt(this.addr(index), value);
    }

    @Override
    protected void _setIntLE(int index, int value) {
        UnsafeByteBufUtil.setIntLE(this.addr(index), value);
    }

    @Override
    protected void _setLong(int index, long value) {
        UnsafeByteBufUtil.setLong(this.addr(index), value);
    }

    @Override
    protected void _setLongLE(int index, long value) {
        UnsafeByteBufUtil.setLongLE(this.addr(index), value);
    }

    @Override
    public ByteBuf setBytes(int index, ByteBuf src, int srcIndex, int length) {
        UnsafeByteBufUtil.setBytes((AbstractByteBuf)this, this.addr(index), index, src, srcIndex, length);
        return this;
    }

    @Override
    public ByteBuf setBytes(int index, byte[] src, int srcIndex, int length) {
        UnsafeByteBufUtil.setBytes((AbstractByteBuf)this, this.addr(index), index, src, srcIndex, length);
        return this;
    }

    @Override
    public ByteBuf setBytes(int index, ByteBuffer src) {
        UnsafeByteBufUtil.setBytes(this, this.addr(index), index, src);
        return this;
    }

    @Override
    public ByteBuf getBytes(int index, OutputStream out, int length) throws IOException {
        UnsafeByteBufUtil.getBytes(this, this.addr(index), index, out, length);
        return this;
    }

    @Override
    public int getBytes(int index, GatheringByteChannel out, int length) throws IOException {
        return this.getBytes(index, out, length, false);
    }

    private int getBytes(int index, GatheringByteChannel out, int length, boolean internal) throws IOException {
        this.ensureAccessible();
        if (length == 0) {
            return 0;
        }
        ByteBuffer tmpBuf = internal ? this.internalNioBuffer() : this.buffer.duplicate();
        tmpBuf.clear().position(index).limit(index + length);
        return out.write(tmpBuf);
    }

    @Override
    public int getBytes(int index, FileChannel out, long position, int length) throws IOException {
        return this.getBytes(index, out, position, length, false);
    }

    private int getBytes(int index, FileChannel out, long position, int length, boolean internal) throws IOException {
        this.ensureAccessible();
        if (length == 0) {
            return 0;
        }
        ByteBuffer tmpBuf = internal ? this.internalNioBuffer() : this.buffer.duplicate();
        tmpBuf.clear().position(index).limit(index + length);
        return out.write(tmpBuf, position);
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
    public int setBytes(int index, InputStream in, int length) throws IOException {
        return UnsafeByteBufUtil.setBytes(this, this.addr(index), index, in, length);
    }

    @Override
    public int setBytes(int index, ScatteringByteChannel in, int length) throws IOException {
        this.ensureAccessible();
        ByteBuffer tmpBuf = this.internalNioBuffer();
        tmpBuf.clear().position(index).limit(index + length);
        try {
            return in.read(tmpBuf);
        }
        catch (ClosedChannelException ignored) {
            return -1;
        }
    }

    @Override
    public int setBytes(int index, FileChannel in, long position, int length) throws IOException {
        this.ensureAccessible();
        ByteBuffer tmpBuf = this.internalNioBuffer();
        tmpBuf.clear().position(index).limit(index + length);
        try {
            return in.read(tmpBuf, position);
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
    public ByteBuffer[] nioBuffers(int index, int length) {
        return new ByteBuffer[]{this.nioBuffer(index, length)};
    }

    @Override
    public ByteBuf copy(int index, int length) {
        return UnsafeByteBufUtil.copy(this, this.addr(index), index, length);
    }

    @Override
    public ByteBuffer internalNioBuffer(int index, int length) {
        this.checkIndex(index, length);
        return (ByteBuffer)this.internalNioBuffer().clear().position(index).limit(index + length);
    }

    private ByteBuffer internalNioBuffer() {
        ByteBuffer tmpNioBuf = this.tmpNioBuf;
        if (tmpNioBuf == null) {
            this.tmpNioBuf = tmpNioBuf = this.buffer.duplicate();
        }
        return tmpNioBuf;
    }

    @Override
    public ByteBuffer nioBuffer(int index, int length) {
        this.checkIndex(index, length);
        return ((ByteBuffer)this.buffer.duplicate().position(index).limit(index + length)).slice();
    }

    @Override
    protected void deallocate() {
        ByteBuffer buffer = this.buffer;
        if (buffer == null) {
            return;
        }
        this.buffer = null;
        if (!this.doNotFree) {
            this.freeDirect(buffer);
        }
    }

    @Override
    public ByteBuf unwrap() {
        return null;
    }

    long addr(int index) {
        return this.memoryAddress + (long)index;
    }

    @Override
    protected SwappedByteBuf newSwappedByteBuf() {
        if (PlatformDependent.isUnaligned()) {
            return new UnsafeDirectSwappedByteBuf(this);
        }
        return super.newSwappedByteBuf();
    }

    @Override
    public ByteBuf setZero(int index, int length) {
        this.checkIndex(index, length);
        UnsafeByteBufUtil.setZero(this.addr(index), length);
        return this;
    }

    @Override
    public ByteBuf writeZero(int length) {
        this.ensureWritable(length);
        int wIndex = this.writerIndex;
        UnsafeByteBufUtil.setZero(this.addr(wIndex), length);
        this.writerIndex = wIndex + length;
        return this;
    }
}

