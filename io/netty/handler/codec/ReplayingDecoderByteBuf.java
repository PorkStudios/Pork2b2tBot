/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.SwappedByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.ReplayingDecoder;
import io.netty.util.ByteProcessor;
import io.netty.util.ReferenceCounted;
import io.netty.util.Signal;
import io.netty.util.internal.StringUtil;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.nio.channels.GatheringByteChannel;
import java.nio.channels.ScatteringByteChannel;
import java.nio.charset.Charset;

final class ReplayingDecoderByteBuf
extends ByteBuf {
    private static final Signal REPLAY = ReplayingDecoder.REPLAY;
    private ByteBuf buffer;
    private boolean terminated;
    private SwappedByteBuf swapped;
    static final ReplayingDecoderByteBuf EMPTY_BUFFER = new ReplayingDecoderByteBuf(Unpooled.EMPTY_BUFFER);

    ReplayingDecoderByteBuf() {
    }

    ReplayingDecoderByteBuf(ByteBuf buffer) {
        this.setCumulation(buffer);
    }

    void setCumulation(ByteBuf buffer) {
        this.buffer = buffer;
    }

    void terminate() {
        this.terminated = true;
    }

    @Override
    public int capacity() {
        if (this.terminated) {
            return this.buffer.capacity();
        }
        return Integer.MAX_VALUE;
    }

    @Override
    public ByteBuf capacity(int newCapacity) {
        throw ReplayingDecoderByteBuf.reject();
    }

    @Override
    public int maxCapacity() {
        return this.capacity();
    }

    @Override
    public ByteBufAllocator alloc() {
        return this.buffer.alloc();
    }

    @Override
    public boolean isReadOnly() {
        return false;
    }

    @Override
    public ByteBuf asReadOnly() {
        return Unpooled.unmodifiableBuffer((ByteBuf)this);
    }

    @Override
    public boolean isDirect() {
        return this.buffer.isDirect();
    }

    @Override
    public boolean hasArray() {
        return false;
    }

    @Override
    public byte[] array() {
        throw new UnsupportedOperationException();
    }

    @Override
    public int arrayOffset() {
        throw new UnsupportedOperationException();
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
    public ByteBuf clear() {
        throw ReplayingDecoderByteBuf.reject();
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj;
    }

    @Override
    public int compareTo(ByteBuf buffer) {
        throw ReplayingDecoderByteBuf.reject();
    }

    @Override
    public ByteBuf copy() {
        throw ReplayingDecoderByteBuf.reject();
    }

    @Override
    public ByteBuf copy(int index, int length) {
        this.checkIndex(index, length);
        return this.buffer.copy(index, length);
    }

    @Override
    public ByteBuf discardReadBytes() {
        throw ReplayingDecoderByteBuf.reject();
    }

    @Override
    public ByteBuf ensureWritable(int writableBytes) {
        throw ReplayingDecoderByteBuf.reject();
    }

    @Override
    public int ensureWritable(int minWritableBytes, boolean force) {
        throw ReplayingDecoderByteBuf.reject();
    }

    @Override
    public ByteBuf duplicate() {
        throw ReplayingDecoderByteBuf.reject();
    }

    @Override
    public ByteBuf retainedDuplicate() {
        throw ReplayingDecoderByteBuf.reject();
    }

    @Override
    public boolean getBoolean(int index) {
        this.checkIndex(index, 1);
        return this.buffer.getBoolean(index);
    }

    @Override
    public byte getByte(int index) {
        this.checkIndex(index, 1);
        return this.buffer.getByte(index);
    }

    @Override
    public short getUnsignedByte(int index) {
        this.checkIndex(index, 1);
        return this.buffer.getUnsignedByte(index);
    }

    @Override
    public ByteBuf getBytes(int index, byte[] dst, int dstIndex, int length) {
        this.checkIndex(index, length);
        this.buffer.getBytes(index, dst, dstIndex, length);
        return this;
    }

    @Override
    public ByteBuf getBytes(int index, byte[] dst) {
        this.checkIndex(index, dst.length);
        this.buffer.getBytes(index, dst);
        return this;
    }

    @Override
    public ByteBuf getBytes(int index, ByteBuffer dst) {
        throw ReplayingDecoderByteBuf.reject();
    }

    @Override
    public ByteBuf getBytes(int index, ByteBuf dst, int dstIndex, int length) {
        this.checkIndex(index, length);
        this.buffer.getBytes(index, dst, dstIndex, length);
        return this;
    }

    @Override
    public ByteBuf getBytes(int index, ByteBuf dst, int length) {
        throw ReplayingDecoderByteBuf.reject();
    }

    @Override
    public ByteBuf getBytes(int index, ByteBuf dst) {
        throw ReplayingDecoderByteBuf.reject();
    }

    @Override
    public int getBytes(int index, GatheringByteChannel out, int length) {
        throw ReplayingDecoderByteBuf.reject();
    }

    @Override
    public int getBytes(int index, FileChannel out, long position, int length) {
        throw ReplayingDecoderByteBuf.reject();
    }

    @Override
    public ByteBuf getBytes(int index, OutputStream out, int length) {
        throw ReplayingDecoderByteBuf.reject();
    }

    @Override
    public int getInt(int index) {
        this.checkIndex(index, 4);
        return this.buffer.getInt(index);
    }

    @Override
    public int getIntLE(int index) {
        this.checkIndex(index, 4);
        return this.buffer.getIntLE(index);
    }

    @Override
    public long getUnsignedInt(int index) {
        this.checkIndex(index, 4);
        return this.buffer.getUnsignedInt(index);
    }

    @Override
    public long getUnsignedIntLE(int index) {
        this.checkIndex(index, 4);
        return this.buffer.getUnsignedIntLE(index);
    }

    @Override
    public long getLong(int index) {
        this.checkIndex(index, 8);
        return this.buffer.getLong(index);
    }

    @Override
    public long getLongLE(int index) {
        this.checkIndex(index, 8);
        return this.buffer.getLongLE(index);
    }

    @Override
    public int getMedium(int index) {
        this.checkIndex(index, 3);
        return this.buffer.getMedium(index);
    }

    @Override
    public int getMediumLE(int index) {
        this.checkIndex(index, 3);
        return this.buffer.getMediumLE(index);
    }

    @Override
    public int getUnsignedMedium(int index) {
        this.checkIndex(index, 3);
        return this.buffer.getUnsignedMedium(index);
    }

    @Override
    public int getUnsignedMediumLE(int index) {
        this.checkIndex(index, 3);
        return this.buffer.getUnsignedMediumLE(index);
    }

    @Override
    public short getShort(int index) {
        this.checkIndex(index, 2);
        return this.buffer.getShort(index);
    }

    @Override
    public short getShortLE(int index) {
        this.checkIndex(index, 2);
        return this.buffer.getShortLE(index);
    }

    @Override
    public int getUnsignedShort(int index) {
        this.checkIndex(index, 2);
        return this.buffer.getUnsignedShort(index);
    }

    @Override
    public int getUnsignedShortLE(int index) {
        this.checkIndex(index, 2);
        return this.buffer.getUnsignedShortLE(index);
    }

    @Override
    public char getChar(int index) {
        this.checkIndex(index, 2);
        return this.buffer.getChar(index);
    }

    @Override
    public float getFloat(int index) {
        this.checkIndex(index, 4);
        return this.buffer.getFloat(index);
    }

    @Override
    public double getDouble(int index) {
        this.checkIndex(index, 8);
        return this.buffer.getDouble(index);
    }

    @Override
    public CharSequence getCharSequence(int index, int length, Charset charset) {
        this.checkIndex(index, length);
        return this.buffer.getCharSequence(index, length, charset);
    }

    @Override
    public int hashCode() {
        throw ReplayingDecoderByteBuf.reject();
    }

    @Override
    public int indexOf(int fromIndex, int toIndex, byte value) {
        if (fromIndex == toIndex) {
            return -1;
        }
        if (Math.max(fromIndex, toIndex) > this.buffer.writerIndex()) {
            throw REPLAY;
        }
        return this.buffer.indexOf(fromIndex, toIndex, value);
    }

    @Override
    public int bytesBefore(byte value) {
        int bytes = this.buffer.bytesBefore(value);
        if (bytes < 0) {
            throw REPLAY;
        }
        return bytes;
    }

    @Override
    public int bytesBefore(int length, byte value) {
        return this.bytesBefore(this.buffer.readerIndex(), length, value);
    }

    @Override
    public int bytesBefore(int index, int length, byte value) {
        int writerIndex = this.buffer.writerIndex();
        if (index >= writerIndex) {
            throw REPLAY;
        }
        if (index <= writerIndex - length) {
            return this.buffer.bytesBefore(index, length, value);
        }
        int res = this.buffer.bytesBefore(index, writerIndex - index, value);
        if (res < 0) {
            throw REPLAY;
        }
        return res;
    }

    @Override
    public int forEachByte(ByteProcessor processor) {
        int ret = this.buffer.forEachByte(processor);
        if (ret < 0) {
            throw REPLAY;
        }
        return ret;
    }

    @Override
    public int forEachByte(int index, int length, ByteProcessor processor) {
        int writerIndex = this.buffer.writerIndex();
        if (index >= writerIndex) {
            throw REPLAY;
        }
        if (index <= writerIndex - length) {
            return this.buffer.forEachByte(index, length, processor);
        }
        int ret = this.buffer.forEachByte(index, writerIndex - index, processor);
        if (ret < 0) {
            throw REPLAY;
        }
        return ret;
    }

    @Override
    public int forEachByteDesc(ByteProcessor processor) {
        if (this.terminated) {
            return this.buffer.forEachByteDesc(processor);
        }
        throw ReplayingDecoderByteBuf.reject();
    }

    @Override
    public int forEachByteDesc(int index, int length, ByteProcessor processor) {
        if (index + length > this.buffer.writerIndex()) {
            throw REPLAY;
        }
        return this.buffer.forEachByteDesc(index, length, processor);
    }

    @Override
    public ByteBuf markReaderIndex() {
        this.buffer.markReaderIndex();
        return this;
    }

    @Override
    public ByteBuf markWriterIndex() {
        throw ReplayingDecoderByteBuf.reject();
    }

    @Override
    public ByteOrder order() {
        return this.buffer.order();
    }

    @Override
    public ByteBuf order(ByteOrder endianness) {
        if (endianness == null) {
            throw new NullPointerException("endianness");
        }
        if (endianness == this.order()) {
            return this;
        }
        SwappedByteBuf swapped = this.swapped;
        if (swapped == null) {
            this.swapped = swapped = new SwappedByteBuf(this);
        }
        return swapped;
    }

    @Override
    public boolean isReadable() {
        return this.terminated ? this.buffer.isReadable() : true;
    }

    @Override
    public boolean isReadable(int size) {
        return this.terminated ? this.buffer.isReadable(size) : true;
    }

    @Override
    public int readableBytes() {
        if (this.terminated) {
            return this.buffer.readableBytes();
        }
        return Integer.MAX_VALUE - this.buffer.readerIndex();
    }

    @Override
    public boolean readBoolean() {
        this.checkReadableBytes(1);
        return this.buffer.readBoolean();
    }

    @Override
    public byte readByte() {
        this.checkReadableBytes(1);
        return this.buffer.readByte();
    }

    @Override
    public short readUnsignedByte() {
        this.checkReadableBytes(1);
        return this.buffer.readUnsignedByte();
    }

    @Override
    public ByteBuf readBytes(byte[] dst, int dstIndex, int length) {
        this.checkReadableBytes(length);
        this.buffer.readBytes(dst, dstIndex, length);
        return this;
    }

    @Override
    public ByteBuf readBytes(byte[] dst) {
        this.checkReadableBytes(dst.length);
        this.buffer.readBytes(dst);
        return this;
    }

    @Override
    public ByteBuf readBytes(ByteBuffer dst) {
        throw ReplayingDecoderByteBuf.reject();
    }

    @Override
    public ByteBuf readBytes(ByteBuf dst, int dstIndex, int length) {
        this.checkReadableBytes(length);
        this.buffer.readBytes(dst, dstIndex, length);
        return this;
    }

    @Override
    public ByteBuf readBytes(ByteBuf dst, int length) {
        throw ReplayingDecoderByteBuf.reject();
    }

    @Override
    public ByteBuf readBytes(ByteBuf dst) {
        this.checkReadableBytes(dst.writableBytes());
        this.buffer.readBytes(dst);
        return this;
    }

    @Override
    public int readBytes(GatheringByteChannel out, int length) {
        throw ReplayingDecoderByteBuf.reject();
    }

    @Override
    public int readBytes(FileChannel out, long position, int length) {
        throw ReplayingDecoderByteBuf.reject();
    }

    @Override
    public ByteBuf readBytes(int length) {
        this.checkReadableBytes(length);
        return this.buffer.readBytes(length);
    }

    @Override
    public ByteBuf readSlice(int length) {
        this.checkReadableBytes(length);
        return this.buffer.readSlice(length);
    }

    @Override
    public ByteBuf readRetainedSlice(int length) {
        this.checkReadableBytes(length);
        return this.buffer.readRetainedSlice(length);
    }

    @Override
    public ByteBuf readBytes(OutputStream out, int length) {
        throw ReplayingDecoderByteBuf.reject();
    }

    @Override
    public int readerIndex() {
        return this.buffer.readerIndex();
    }

    @Override
    public ByteBuf readerIndex(int readerIndex) {
        this.buffer.readerIndex(readerIndex);
        return this;
    }

    @Override
    public int readInt() {
        this.checkReadableBytes(4);
        return this.buffer.readInt();
    }

    @Override
    public int readIntLE() {
        this.checkReadableBytes(4);
        return this.buffer.readIntLE();
    }

    @Override
    public long readUnsignedInt() {
        this.checkReadableBytes(4);
        return this.buffer.readUnsignedInt();
    }

    @Override
    public long readUnsignedIntLE() {
        this.checkReadableBytes(4);
        return this.buffer.readUnsignedIntLE();
    }

    @Override
    public long readLong() {
        this.checkReadableBytes(8);
        return this.buffer.readLong();
    }

    @Override
    public long readLongLE() {
        this.checkReadableBytes(8);
        return this.buffer.readLongLE();
    }

    @Override
    public int readMedium() {
        this.checkReadableBytes(3);
        return this.buffer.readMedium();
    }

    @Override
    public int readMediumLE() {
        this.checkReadableBytes(3);
        return this.buffer.readMediumLE();
    }

    @Override
    public int readUnsignedMedium() {
        this.checkReadableBytes(3);
        return this.buffer.readUnsignedMedium();
    }

    @Override
    public int readUnsignedMediumLE() {
        this.checkReadableBytes(3);
        return this.buffer.readUnsignedMediumLE();
    }

    @Override
    public short readShort() {
        this.checkReadableBytes(2);
        return this.buffer.readShort();
    }

    @Override
    public short readShortLE() {
        this.checkReadableBytes(2);
        return this.buffer.readShortLE();
    }

    @Override
    public int readUnsignedShort() {
        this.checkReadableBytes(2);
        return this.buffer.readUnsignedShort();
    }

    @Override
    public int readUnsignedShortLE() {
        this.checkReadableBytes(2);
        return this.buffer.readUnsignedShortLE();
    }

    @Override
    public char readChar() {
        this.checkReadableBytes(2);
        return this.buffer.readChar();
    }

    @Override
    public float readFloat() {
        this.checkReadableBytes(4);
        return this.buffer.readFloat();
    }

    @Override
    public double readDouble() {
        this.checkReadableBytes(8);
        return this.buffer.readDouble();
    }

    @Override
    public CharSequence readCharSequence(int length, Charset charset) {
        this.checkReadableBytes(length);
        return this.buffer.readCharSequence(length, charset);
    }

    @Override
    public ByteBuf resetReaderIndex() {
        this.buffer.resetReaderIndex();
        return this;
    }

    @Override
    public ByteBuf resetWriterIndex() {
        throw ReplayingDecoderByteBuf.reject();
    }

    @Override
    public ByteBuf setBoolean(int index, boolean value) {
        throw ReplayingDecoderByteBuf.reject();
    }

    @Override
    public ByteBuf setByte(int index, int value) {
        throw ReplayingDecoderByteBuf.reject();
    }

    @Override
    public ByteBuf setBytes(int index, byte[] src, int srcIndex, int length) {
        throw ReplayingDecoderByteBuf.reject();
    }

    @Override
    public ByteBuf setBytes(int index, byte[] src) {
        throw ReplayingDecoderByteBuf.reject();
    }

    @Override
    public ByteBuf setBytes(int index, ByteBuffer src) {
        throw ReplayingDecoderByteBuf.reject();
    }

    @Override
    public ByteBuf setBytes(int index, ByteBuf src, int srcIndex, int length) {
        throw ReplayingDecoderByteBuf.reject();
    }

    @Override
    public ByteBuf setBytes(int index, ByteBuf src, int length) {
        throw ReplayingDecoderByteBuf.reject();
    }

    @Override
    public ByteBuf setBytes(int index, ByteBuf src) {
        throw ReplayingDecoderByteBuf.reject();
    }

    @Override
    public int setBytes(int index, InputStream in, int length) {
        throw ReplayingDecoderByteBuf.reject();
    }

    @Override
    public ByteBuf setZero(int index, int length) {
        throw ReplayingDecoderByteBuf.reject();
    }

    @Override
    public int setBytes(int index, ScatteringByteChannel in, int length) {
        throw ReplayingDecoderByteBuf.reject();
    }

    @Override
    public int setBytes(int index, FileChannel in, long position, int length) {
        throw ReplayingDecoderByteBuf.reject();
    }

    @Override
    public ByteBuf setIndex(int readerIndex, int writerIndex) {
        throw ReplayingDecoderByteBuf.reject();
    }

    @Override
    public ByteBuf setInt(int index, int value) {
        throw ReplayingDecoderByteBuf.reject();
    }

    @Override
    public ByteBuf setIntLE(int index, int value) {
        throw ReplayingDecoderByteBuf.reject();
    }

    @Override
    public ByteBuf setLong(int index, long value) {
        throw ReplayingDecoderByteBuf.reject();
    }

    @Override
    public ByteBuf setLongLE(int index, long value) {
        throw ReplayingDecoderByteBuf.reject();
    }

    @Override
    public ByteBuf setMedium(int index, int value) {
        throw ReplayingDecoderByteBuf.reject();
    }

    @Override
    public ByteBuf setMediumLE(int index, int value) {
        throw ReplayingDecoderByteBuf.reject();
    }

    @Override
    public ByteBuf setShort(int index, int value) {
        throw ReplayingDecoderByteBuf.reject();
    }

    @Override
    public ByteBuf setShortLE(int index, int value) {
        throw ReplayingDecoderByteBuf.reject();
    }

    @Override
    public ByteBuf setChar(int index, int value) {
        throw ReplayingDecoderByteBuf.reject();
    }

    @Override
    public ByteBuf setFloat(int index, float value) {
        throw ReplayingDecoderByteBuf.reject();
    }

    @Override
    public ByteBuf setDouble(int index, double value) {
        throw ReplayingDecoderByteBuf.reject();
    }

    @Override
    public ByteBuf skipBytes(int length) {
        this.checkReadableBytes(length);
        this.buffer.skipBytes(length);
        return this;
    }

    @Override
    public ByteBuf slice() {
        throw ReplayingDecoderByteBuf.reject();
    }

    @Override
    public ByteBuf retainedSlice() {
        throw ReplayingDecoderByteBuf.reject();
    }

    @Override
    public ByteBuf slice(int index, int length) {
        this.checkIndex(index, length);
        return this.buffer.slice(index, length);
    }

    @Override
    public ByteBuf retainedSlice(int index, int length) {
        this.checkIndex(index, length);
        return this.buffer.slice(index, length);
    }

    @Override
    public int nioBufferCount() {
        return this.buffer.nioBufferCount();
    }

    @Override
    public ByteBuffer nioBuffer() {
        throw ReplayingDecoderByteBuf.reject();
    }

    @Override
    public ByteBuffer nioBuffer(int index, int length) {
        this.checkIndex(index, length);
        return this.buffer.nioBuffer(index, length);
    }

    @Override
    public ByteBuffer[] nioBuffers() {
        throw ReplayingDecoderByteBuf.reject();
    }

    @Override
    public ByteBuffer[] nioBuffers(int index, int length) {
        this.checkIndex(index, length);
        return this.buffer.nioBuffers(index, length);
    }

    @Override
    public ByteBuffer internalNioBuffer(int index, int length) {
        this.checkIndex(index, length);
        return this.buffer.internalNioBuffer(index, length);
    }

    @Override
    public String toString(int index, int length, Charset charset) {
        this.checkIndex(index, length);
        return this.buffer.toString(index, length, charset);
    }

    @Override
    public String toString(Charset charsetName) {
        throw ReplayingDecoderByteBuf.reject();
    }

    @Override
    public String toString() {
        return StringUtil.simpleClassName(this) + '(' + "ridx=" + this.readerIndex() + ", widx=" + this.writerIndex() + ')';
    }

    @Override
    public boolean isWritable() {
        return false;
    }

    @Override
    public boolean isWritable(int size) {
        return false;
    }

    @Override
    public int writableBytes() {
        return 0;
    }

    @Override
    public int maxWritableBytes() {
        return 0;
    }

    @Override
    public ByteBuf writeBoolean(boolean value) {
        throw ReplayingDecoderByteBuf.reject();
    }

    @Override
    public ByteBuf writeByte(int value) {
        throw ReplayingDecoderByteBuf.reject();
    }

    @Override
    public ByteBuf writeBytes(byte[] src, int srcIndex, int length) {
        throw ReplayingDecoderByteBuf.reject();
    }

    @Override
    public ByteBuf writeBytes(byte[] src) {
        throw ReplayingDecoderByteBuf.reject();
    }

    @Override
    public ByteBuf writeBytes(ByteBuffer src) {
        throw ReplayingDecoderByteBuf.reject();
    }

    @Override
    public ByteBuf writeBytes(ByteBuf src, int srcIndex, int length) {
        throw ReplayingDecoderByteBuf.reject();
    }

    @Override
    public ByteBuf writeBytes(ByteBuf src, int length) {
        throw ReplayingDecoderByteBuf.reject();
    }

    @Override
    public ByteBuf writeBytes(ByteBuf src) {
        throw ReplayingDecoderByteBuf.reject();
    }

    @Override
    public int writeBytes(InputStream in, int length) {
        throw ReplayingDecoderByteBuf.reject();
    }

    @Override
    public int writeBytes(ScatteringByteChannel in, int length) {
        throw ReplayingDecoderByteBuf.reject();
    }

    @Override
    public int writeBytes(FileChannel in, long position, int length) {
        throw ReplayingDecoderByteBuf.reject();
    }

    @Override
    public ByteBuf writeInt(int value) {
        throw ReplayingDecoderByteBuf.reject();
    }

    @Override
    public ByteBuf writeIntLE(int value) {
        throw ReplayingDecoderByteBuf.reject();
    }

    @Override
    public ByteBuf writeLong(long value) {
        throw ReplayingDecoderByteBuf.reject();
    }

    @Override
    public ByteBuf writeLongLE(long value) {
        throw ReplayingDecoderByteBuf.reject();
    }

    @Override
    public ByteBuf writeMedium(int value) {
        throw ReplayingDecoderByteBuf.reject();
    }

    @Override
    public ByteBuf writeMediumLE(int value) {
        throw ReplayingDecoderByteBuf.reject();
    }

    @Override
    public ByteBuf writeZero(int length) {
        throw ReplayingDecoderByteBuf.reject();
    }

    @Override
    public int writerIndex() {
        return this.buffer.writerIndex();
    }

    @Override
    public ByteBuf writerIndex(int writerIndex) {
        throw ReplayingDecoderByteBuf.reject();
    }

    @Override
    public ByteBuf writeShort(int value) {
        throw ReplayingDecoderByteBuf.reject();
    }

    @Override
    public ByteBuf writeShortLE(int value) {
        throw ReplayingDecoderByteBuf.reject();
    }

    @Override
    public ByteBuf writeChar(int value) {
        throw ReplayingDecoderByteBuf.reject();
    }

    @Override
    public ByteBuf writeFloat(float value) {
        throw ReplayingDecoderByteBuf.reject();
    }

    @Override
    public ByteBuf writeDouble(double value) {
        throw ReplayingDecoderByteBuf.reject();
    }

    @Override
    public int setCharSequence(int index, CharSequence sequence, Charset charset) {
        throw ReplayingDecoderByteBuf.reject();
    }

    @Override
    public int writeCharSequence(CharSequence sequence, Charset charset) {
        throw ReplayingDecoderByteBuf.reject();
    }

    private void checkIndex(int index, int length) {
        if (index + length > this.buffer.writerIndex()) {
            throw REPLAY;
        }
    }

    private void checkReadableBytes(int readableBytes) {
        if (this.buffer.readableBytes() < readableBytes) {
            throw REPLAY;
        }
    }

    @Override
    public ByteBuf discardSomeReadBytes() {
        throw ReplayingDecoderByteBuf.reject();
    }

    @Override
    public int refCnt() {
        return this.buffer.refCnt();
    }

    @Override
    public ByteBuf retain() {
        throw ReplayingDecoderByteBuf.reject();
    }

    @Override
    public ByteBuf retain(int increment) {
        throw ReplayingDecoderByteBuf.reject();
    }

    @Override
    public ByteBuf touch() {
        this.buffer.touch();
        return this;
    }

    @Override
    public ByteBuf touch(Object hint) {
        this.buffer.touch(hint);
        return this;
    }

    @Override
    public boolean release() {
        throw ReplayingDecoderByteBuf.reject();
    }

    @Override
    public boolean release(int decrement) {
        throw ReplayingDecoderByteBuf.reject();
    }

    @Override
    public ByteBuf unwrap() {
        throw ReplayingDecoderByteBuf.reject();
    }

    private static UnsupportedOperationException reject() {
        return new UnsupportedOperationException("not a replayable operation");
    }

    static {
        EMPTY_BUFFER.terminate();
    }
}

