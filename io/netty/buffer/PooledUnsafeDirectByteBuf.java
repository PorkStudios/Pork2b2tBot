/*
 * Decompiled with CFR 0_132.
 */
package io.netty.buffer;

import io.netty.buffer.AbstractByteBuf;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PoolChunk;
import io.netty.buffer.PoolThreadCache;
import io.netty.buffer.PooledByteBuf;
import io.netty.buffer.SwappedByteBuf;
import io.netty.buffer.UnsafeByteBufUtil;
import io.netty.buffer.UnsafeDirectSwappedByteBuf;
import io.netty.util.Recycler;
import io.netty.util.internal.PlatformDependent;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.FileChannel;
import java.nio.channels.GatheringByteChannel;
import java.nio.channels.ScatteringByteChannel;

final class PooledUnsafeDirectByteBuf
extends PooledByteBuf<ByteBuffer> {
    private static final Recycler<PooledUnsafeDirectByteBuf> RECYCLER = new Recycler<PooledUnsafeDirectByteBuf>(){

        @Override
        protected PooledUnsafeDirectByteBuf newObject(Recycler.Handle<PooledUnsafeDirectByteBuf> handle) {
            return new PooledUnsafeDirectByteBuf(handle, 0);
        }
    };
    private long memoryAddress;

    static PooledUnsafeDirectByteBuf newInstance(int maxCapacity) {
        PooledUnsafeDirectByteBuf buf = RECYCLER.get();
        buf.reuse(maxCapacity);
        return buf;
    }

    private PooledUnsafeDirectByteBuf(Recycler.Handle<PooledUnsafeDirectByteBuf> recyclerHandle, int maxCapacity) {
        super(recyclerHandle, maxCapacity);
    }

    @Override
    void init(PoolChunk<ByteBuffer> chunk, long handle, int offset, int length, int maxLength, PoolThreadCache cache) {
        super.init(chunk, handle, offset, length, maxLength, cache);
        this.initMemoryAddress();
    }

    @Override
    void initUnpooled(PoolChunk<ByteBuffer> chunk, int length) {
        super.initUnpooled(chunk, length);
        this.initMemoryAddress();
    }

    private void initMemoryAddress() {
        this.memoryAddress = PlatformDependent.directBufferAddress((ByteBuffer)this.memory) + (long)this.offset;
    }

    @Override
    protected ByteBuffer newInternalNioBuffer(ByteBuffer memory) {
        return memory.duplicate();
    }

    @Override
    public boolean isDirect() {
        return true;
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
    public ByteBuf getBytes(int index, OutputStream out, int length) throws IOException {
        UnsafeByteBufUtil.getBytes(this, this.addr(index), index, out, length);
        return this;
    }

    @Override
    public int getBytes(int index, GatheringByteChannel out, int length) throws IOException {
        return this.getBytes(index, out, length, false);
    }

    private int getBytes(int index, GatheringByteChannel out, int length, boolean internal) throws IOException {
        this.checkIndex(index, length);
        if (length == 0) {
            return 0;
        }
        ByteBuffer tmpBuf = internal ? this.internalNioBuffer() : ((ByteBuffer)this.memory).duplicate();
        index = this.idx(index);
        tmpBuf.clear().position(index).limit(index + length);
        return out.write(tmpBuf);
    }

    @Override
    public int getBytes(int index, FileChannel out, long position, int length) throws IOException {
        return this.getBytes(index, out, position, length, false);
    }

    private int getBytes(int index, FileChannel out, long position, int length, boolean internal) throws IOException {
        this.checkIndex(index, length);
        if (length == 0) {
            return 0;
        }
        ByteBuffer tmpBuf = internal ? this.internalNioBuffer() : ((ByteBuffer)this.memory).duplicate();
        index = this.idx(index);
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
    protected void _setByte(int index, int value) {
        UnsafeByteBufUtil.setByte(this.addr(index), (byte)value);
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
    public int setBytes(int index, InputStream in, int length) throws IOException {
        return UnsafeByteBufUtil.setBytes(this, this.addr(index), index, in, length);
    }

    @Override
    public int setBytes(int index, ScatteringByteChannel in, int length) throws IOException {
        this.checkIndex(index, length);
        ByteBuffer tmpBuf = this.internalNioBuffer();
        index = this.idx(index);
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
        this.checkIndex(index, length);
        ByteBuffer tmpBuf = this.internalNioBuffer();
        index = this.idx(index);
        tmpBuf.clear().position(index).limit(index + length);
        try {
            return in.read(tmpBuf, position);
        }
        catch (ClosedChannelException ignored) {
            return -1;
        }
    }

    @Override
    public ByteBuf copy(int index, int length) {
        return UnsafeByteBufUtil.copy(this, this.addr(index), index, length);
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
    public ByteBuffer nioBuffer(int index, int length) {
        this.checkIndex(index, length);
        index = this.idx(index);
        return ((ByteBuffer)((ByteBuffer)this.memory).duplicate().position(index).limit(index + length)).slice();
    }

    @Override
    public ByteBuffer internalNioBuffer(int index, int length) {
        this.checkIndex(index, length);
        index = this.idx(index);
        return (ByteBuffer)this.internalNioBuffer().clear().position(index).limit(index + length);
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

    private long addr(int index) {
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

