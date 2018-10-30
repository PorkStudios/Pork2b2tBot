/*
 * Decompiled with CFR 0_132.
 */
package io.netty.buffer;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.util.ByteProcessor;
import io.netty.util.ReferenceCounted;
import io.netty.util.internal.StringUtil;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.nio.channels.GatheringByteChannel;
import java.nio.channels.ScatteringByteChannel;
import java.nio.charset.Charset;

class WrappedByteBuf
extends ByteBuf {
    protected final ByteBuf buf;

    protected WrappedByteBuf(ByteBuf buf) {
        if (buf == null) {
            throw new NullPointerException("buf");
        }
        this.buf = buf;
    }

    @Override
    public final boolean hasMemoryAddress() {
        return this.buf.hasMemoryAddress();
    }

    @Override
    public final long memoryAddress() {
        return this.buf.memoryAddress();
    }

    @Override
    public final int capacity() {
        return this.buf.capacity();
    }

    @Override
    public ByteBuf capacity(int newCapacity) {
        this.buf.capacity(newCapacity);
        return this;
    }

    @Override
    public final int maxCapacity() {
        return this.buf.maxCapacity();
    }

    @Override
    public final ByteBufAllocator alloc() {
        return this.buf.alloc();
    }

    @Override
    public final ByteOrder order() {
        return this.buf.order();
    }

    @Override
    public ByteBuf order(ByteOrder endianness) {
        return this.buf.order(endianness);
    }

    @Override
    public final ByteBuf unwrap() {
        return this.buf;
    }

    @Override
    public ByteBuf asReadOnly() {
        return this.buf.asReadOnly();
    }

    @Override
    public boolean isReadOnly() {
        return this.buf.isReadOnly();
    }

    @Override
    public final boolean isDirect() {
        return this.buf.isDirect();
    }

    @Override
    public final int readerIndex() {
        return this.buf.readerIndex();
    }

    @Override
    public final ByteBuf readerIndex(int readerIndex) {
        this.buf.readerIndex(readerIndex);
        return this;
    }

    @Override
    public final int writerIndex() {
        return this.buf.writerIndex();
    }

    @Override
    public final ByteBuf writerIndex(int writerIndex) {
        this.buf.writerIndex(writerIndex);
        return this;
    }

    @Override
    public ByteBuf setIndex(int readerIndex, int writerIndex) {
        this.buf.setIndex(readerIndex, writerIndex);
        return this;
    }

    @Override
    public final int readableBytes() {
        return this.buf.readableBytes();
    }

    @Override
    public final int writableBytes() {
        return this.buf.writableBytes();
    }

    @Override
    public final int maxWritableBytes() {
        return this.buf.maxWritableBytes();
    }

    @Override
    public final boolean isReadable() {
        return this.buf.isReadable();
    }

    @Override
    public final boolean isWritable() {
        return this.buf.isWritable();
    }

    @Override
    public final ByteBuf clear() {
        this.buf.clear();
        return this;
    }

    @Override
    public final ByteBuf markReaderIndex() {
        this.buf.markReaderIndex();
        return this;
    }

    @Override
    public final ByteBuf resetReaderIndex() {
        this.buf.resetReaderIndex();
        return this;
    }

    @Override
    public final ByteBuf markWriterIndex() {
        this.buf.markWriterIndex();
        return this;
    }

    @Override
    public final ByteBuf resetWriterIndex() {
        this.buf.resetWriterIndex();
        return this;
    }

    @Override
    public ByteBuf discardReadBytes() {
        this.buf.discardReadBytes();
        return this;
    }

    @Override
    public ByteBuf discardSomeReadBytes() {
        this.buf.discardSomeReadBytes();
        return this;
    }

    @Override
    public ByteBuf ensureWritable(int minWritableBytes) {
        this.buf.ensureWritable(minWritableBytes);
        return this;
    }

    @Override
    public int ensureWritable(int minWritableBytes, boolean force) {
        return this.buf.ensureWritable(minWritableBytes, force);
    }

    @Override
    public boolean getBoolean(int index) {
        return this.buf.getBoolean(index);
    }

    @Override
    public byte getByte(int index) {
        return this.buf.getByte(index);
    }

    @Override
    public short getUnsignedByte(int index) {
        return this.buf.getUnsignedByte(index);
    }

    @Override
    public short getShort(int index) {
        return this.buf.getShort(index);
    }

    @Override
    public short getShortLE(int index) {
        return this.buf.getShortLE(index);
    }

    @Override
    public int getUnsignedShort(int index) {
        return this.buf.getUnsignedShort(index);
    }

    @Override
    public int getUnsignedShortLE(int index) {
        return this.buf.getUnsignedShortLE(index);
    }

    @Override
    public int getMedium(int index) {
        return this.buf.getMedium(index);
    }

    @Override
    public int getMediumLE(int index) {
        return this.buf.getMediumLE(index);
    }

    @Override
    public int getUnsignedMedium(int index) {
        return this.buf.getUnsignedMedium(index);
    }

    @Override
    public int getUnsignedMediumLE(int index) {
        return this.buf.getUnsignedMediumLE(index);
    }

    @Override
    public int getInt(int index) {
        return this.buf.getInt(index);
    }

    @Override
    public int getIntLE(int index) {
        return this.buf.getIntLE(index);
    }

    @Override
    public long getUnsignedInt(int index) {
        return this.buf.getUnsignedInt(index);
    }

    @Override
    public long getUnsignedIntLE(int index) {
        return this.buf.getUnsignedIntLE(index);
    }

    @Override
    public long getLong(int index) {
        return this.buf.getLong(index);
    }

    @Override
    public long getLongLE(int index) {
        return this.buf.getLongLE(index);
    }

    @Override
    public char getChar(int index) {
        return this.buf.getChar(index);
    }

    @Override
    public float getFloat(int index) {
        return this.buf.getFloat(index);
    }

    @Override
    public double getDouble(int index) {
        return this.buf.getDouble(index);
    }

    @Override
    public ByteBuf getBytes(int index, ByteBuf dst) {
        this.buf.getBytes(index, dst);
        return this;
    }

    @Override
    public ByteBuf getBytes(int index, ByteBuf dst, int length) {
        this.buf.getBytes(index, dst, length);
        return this;
    }

    @Override
    public ByteBuf getBytes(int index, ByteBuf dst, int dstIndex, int length) {
        this.buf.getBytes(index, dst, dstIndex, length);
        return this;
    }

    @Override
    public ByteBuf getBytes(int index, byte[] dst) {
        this.buf.getBytes(index, dst);
        return this;
    }

    @Override
    public ByteBuf getBytes(int index, byte[] dst, int dstIndex, int length) {
        this.buf.getBytes(index, dst, dstIndex, length);
        return this;
    }

    @Override
    public ByteBuf getBytes(int index, ByteBuffer dst) {
        this.buf.getBytes(index, dst);
        return this;
    }

    @Override
    public ByteBuf getBytes(int index, OutputStream out, int length) throws IOException {
        this.buf.getBytes(index, out, length);
        return this;
    }

    @Override
    public int getBytes(int index, GatheringByteChannel out, int length) throws IOException {
        return this.buf.getBytes(index, out, length);
    }

    @Override
    public int getBytes(int index, FileChannel out, long position, int length) throws IOException {
        return this.buf.getBytes(index, out, position, length);
    }

    @Override
    public CharSequence getCharSequence(int index, int length, Charset charset) {
        return this.buf.getCharSequence(index, length, charset);
    }

    @Override
    public ByteBuf setBoolean(int index, boolean value) {
        this.buf.setBoolean(index, value);
        return this;
    }

    @Override
    public ByteBuf setByte(int index, int value) {
        this.buf.setByte(index, value);
        return this;
    }

    @Override
    public ByteBuf setShort(int index, int value) {
        this.buf.setShort(index, value);
        return this;
    }

    @Override
    public ByteBuf setShortLE(int index, int value) {
        this.buf.setShortLE(index, value);
        return this;
    }

    @Override
    public ByteBuf setMedium(int index, int value) {
        this.buf.setMedium(index, value);
        return this;
    }

    @Override
    public ByteBuf setMediumLE(int index, int value) {
        this.buf.setMediumLE(index, value);
        return this;
    }

    @Override
    public ByteBuf setInt(int index, int value) {
        this.buf.setInt(index, value);
        return this;
    }

    @Override
    public ByteBuf setIntLE(int index, int value) {
        this.buf.setIntLE(index, value);
        return this;
    }

    @Override
    public ByteBuf setLong(int index, long value) {
        this.buf.setLong(index, value);
        return this;
    }

    @Override
    public ByteBuf setLongLE(int index, long value) {
        this.buf.setLongLE(index, value);
        return this;
    }

    @Override
    public ByteBuf setChar(int index, int value) {
        this.buf.setChar(index, value);
        return this;
    }

    @Override
    public ByteBuf setFloat(int index, float value) {
        this.buf.setFloat(index, value);
        return this;
    }

    @Override
    public ByteBuf setDouble(int index, double value) {
        this.buf.setDouble(index, value);
        return this;
    }

    @Override
    public ByteBuf setBytes(int index, ByteBuf src) {
        this.buf.setBytes(index, src);
        return this;
    }

    @Override
    public ByteBuf setBytes(int index, ByteBuf src, int length) {
        this.buf.setBytes(index, src, length);
        return this;
    }

    @Override
    public ByteBuf setBytes(int index, ByteBuf src, int srcIndex, int length) {
        this.buf.setBytes(index, src, srcIndex, length);
        return this;
    }

    @Override
    public ByteBuf setBytes(int index, byte[] src) {
        this.buf.setBytes(index, src);
        return this;
    }

    @Override
    public ByteBuf setBytes(int index, byte[] src, int srcIndex, int length) {
        this.buf.setBytes(index, src, srcIndex, length);
        return this;
    }

    @Override
    public ByteBuf setBytes(int index, ByteBuffer src) {
        this.buf.setBytes(index, src);
        return this;
    }

    @Override
    public int setBytes(int index, InputStream in, int length) throws IOException {
        return this.buf.setBytes(index, in, length);
    }

    @Override
    public int setBytes(int index, ScatteringByteChannel in, int length) throws IOException {
        return this.buf.setBytes(index, in, length);
    }

    @Override
    public int setBytes(int index, FileChannel in, long position, int length) throws IOException {
        return this.buf.setBytes(index, in, position, length);
    }

    @Override
    public ByteBuf setZero(int index, int length) {
        this.buf.setZero(index, length);
        return this;
    }

    @Override
    public int setCharSequence(int index, CharSequence sequence, Charset charset) {
        return this.buf.setCharSequence(index, sequence, charset);
    }

    @Override
    public boolean readBoolean() {
        return this.buf.readBoolean();
    }

    @Override
    public byte readByte() {
        return this.buf.readByte();
    }

    @Override
    public short readUnsignedByte() {
        return this.buf.readUnsignedByte();
    }

    @Override
    public short readShort() {
        return this.buf.readShort();
    }

    @Override
    public short readShortLE() {
        return this.buf.readShortLE();
    }

    @Override
    public int readUnsignedShort() {
        return this.buf.readUnsignedShort();
    }

    @Override
    public int readUnsignedShortLE() {
        return this.buf.readUnsignedShortLE();
    }

    @Override
    public int readMedium() {
        return this.buf.readMedium();
    }

    @Override
    public int readMediumLE() {
        return this.buf.readMediumLE();
    }

    @Override
    public int readUnsignedMedium() {
        return this.buf.readUnsignedMedium();
    }

    @Override
    public int readUnsignedMediumLE() {
        return this.buf.readUnsignedMediumLE();
    }

    @Override
    public int readInt() {
        return this.buf.readInt();
    }

    @Override
    public int readIntLE() {
        return this.buf.readIntLE();
    }

    @Override
    public long readUnsignedInt() {
        return this.buf.readUnsignedInt();
    }

    @Override
    public long readUnsignedIntLE() {
        return this.buf.readUnsignedIntLE();
    }

    @Override
    public long readLong() {
        return this.buf.readLong();
    }

    @Override
    public long readLongLE() {
        return this.buf.readLongLE();
    }

    @Override
    public char readChar() {
        return this.buf.readChar();
    }

    @Override
    public float readFloat() {
        return this.buf.readFloat();
    }

    @Override
    public double readDouble() {
        return this.buf.readDouble();
    }

    @Override
    public ByteBuf readBytes(int length) {
        return this.buf.readBytes(length);
    }

    @Override
    public ByteBuf readSlice(int length) {
        return this.buf.readSlice(length);
    }

    @Override
    public ByteBuf readRetainedSlice(int length) {
        return this.buf.readRetainedSlice(length);
    }

    @Override
    public ByteBuf readBytes(ByteBuf dst) {
        this.buf.readBytes(dst);
        return this;
    }

    @Override
    public ByteBuf readBytes(ByteBuf dst, int length) {
        this.buf.readBytes(dst, length);
        return this;
    }

    @Override
    public ByteBuf readBytes(ByteBuf dst, int dstIndex, int length) {
        this.buf.readBytes(dst, dstIndex, length);
        return this;
    }

    @Override
    public ByteBuf readBytes(byte[] dst) {
        this.buf.readBytes(dst);
        return this;
    }

    @Override
    public ByteBuf readBytes(byte[] dst, int dstIndex, int length) {
        this.buf.readBytes(dst, dstIndex, length);
        return this;
    }

    @Override
    public ByteBuf readBytes(ByteBuffer dst) {
        this.buf.readBytes(dst);
        return this;
    }

    @Override
    public ByteBuf readBytes(OutputStream out, int length) throws IOException {
        this.buf.readBytes(out, length);
        return this;
    }

    @Override
    public int readBytes(GatheringByteChannel out, int length) throws IOException {
        return this.buf.readBytes(out, length);
    }

    @Override
    public int readBytes(FileChannel out, long position, int length) throws IOException {
        return this.buf.readBytes(out, position, length);
    }

    @Override
    public CharSequence readCharSequence(int length, Charset charset) {
        return this.buf.readCharSequence(length, charset);
    }

    @Override
    public ByteBuf skipBytes(int length) {
        this.buf.skipBytes(length);
        return this;
    }

    @Override
    public ByteBuf writeBoolean(boolean value) {
        this.buf.writeBoolean(value);
        return this;
    }

    @Override
    public ByteBuf writeByte(int value) {
        this.buf.writeByte(value);
        return this;
    }

    @Override
    public ByteBuf writeShort(int value) {
        this.buf.writeShort(value);
        return this;
    }

    @Override
    public ByteBuf writeShortLE(int value) {
        this.buf.writeShortLE(value);
        return this;
    }

    @Override
    public ByteBuf writeMedium(int value) {
        this.buf.writeMedium(value);
        return this;
    }

    @Override
    public ByteBuf writeMediumLE(int value) {
        this.buf.writeMediumLE(value);
        return this;
    }

    @Override
    public ByteBuf writeInt(int value) {
        this.buf.writeInt(value);
        return this;
    }

    @Override
    public ByteBuf writeIntLE(int value) {
        this.buf.writeIntLE(value);
        return this;
    }

    @Override
    public ByteBuf writeLong(long value) {
        this.buf.writeLong(value);
        return this;
    }

    @Override
    public ByteBuf writeLongLE(long value) {
        this.buf.writeLongLE(value);
        return this;
    }

    @Override
    public ByteBuf writeChar(int value) {
        this.buf.writeChar(value);
        return this;
    }

    @Override
    public ByteBuf writeFloat(float value) {
        this.buf.writeFloat(value);
        return this;
    }

    @Override
    public ByteBuf writeDouble(double value) {
        this.buf.writeDouble(value);
        return this;
    }

    @Override
    public ByteBuf writeBytes(ByteBuf src) {
        this.buf.writeBytes(src);
        return this;
    }

    @Override
    public ByteBuf writeBytes(ByteBuf src, int length) {
        this.buf.writeBytes(src, length);
        return this;
    }

    @Override
    public ByteBuf writeBytes(ByteBuf src, int srcIndex, int length) {
        this.buf.writeBytes(src, srcIndex, length);
        return this;
    }

    @Override
    public ByteBuf writeBytes(byte[] src) {
        this.buf.writeBytes(src);
        return this;
    }

    @Override
    public ByteBuf writeBytes(byte[] src, int srcIndex, int length) {
        this.buf.writeBytes(src, srcIndex, length);
        return this;
    }

    @Override
    public ByteBuf writeBytes(ByteBuffer src) {
        this.buf.writeBytes(src);
        return this;
    }

    @Override
    public int writeBytes(InputStream in, int length) throws IOException {
        return this.buf.writeBytes(in, length);
    }

    @Override
    public int writeBytes(ScatteringByteChannel in, int length) throws IOException {
        return this.buf.writeBytes(in, length);
    }

    @Override
    public int writeBytes(FileChannel in, long position, int length) throws IOException {
        return this.buf.writeBytes(in, position, length);
    }

    @Override
    public ByteBuf writeZero(int length) {
        this.buf.writeZero(length);
        return this;
    }

    @Override
    public int writeCharSequence(CharSequence sequence, Charset charset) {
        return this.buf.writeCharSequence(sequence, charset);
    }

    @Override
    public int indexOf(int fromIndex, int toIndex, byte value) {
        return this.buf.indexOf(fromIndex, toIndex, value);
    }

    @Override
    public int bytesBefore(byte value) {
        return this.buf.bytesBefore(value);
    }

    @Override
    public int bytesBefore(int length, byte value) {
        return this.buf.bytesBefore(length, value);
    }

    @Override
    public int bytesBefore(int index, int length, byte value) {
        return this.buf.bytesBefore(index, length, value);
    }

    @Override
    public int forEachByte(ByteProcessor processor) {
        return this.buf.forEachByte(processor);
    }

    @Override
    public int forEachByte(int index, int length, ByteProcessor processor) {
        return this.buf.forEachByte(index, length, processor);
    }

    @Override
    public int forEachByteDesc(ByteProcessor processor) {
        return this.buf.forEachByteDesc(processor);
    }

    @Override
    public int forEachByteDesc(int index, int length, ByteProcessor processor) {
        return this.buf.forEachByteDesc(index, length, processor);
    }

    @Override
    public ByteBuf copy() {
        return this.buf.copy();
    }

    @Override
    public ByteBuf copy(int index, int length) {
        return this.buf.copy(index, length);
    }

    @Override
    public ByteBuf slice() {
        return this.buf.slice();
    }

    @Override
    public ByteBuf retainedSlice() {
        return this.buf.retainedSlice();
    }

    @Override
    public ByteBuf slice(int index, int length) {
        return this.buf.slice(index, length);
    }

    @Override
    public ByteBuf retainedSlice(int index, int length) {
        return this.buf.retainedSlice(index, length);
    }

    @Override
    public ByteBuf duplicate() {
        return this.buf.duplicate();
    }

    @Override
    public ByteBuf retainedDuplicate() {
        return this.buf.retainedDuplicate();
    }

    @Override
    public int nioBufferCount() {
        return this.buf.nioBufferCount();
    }

    @Override
    public ByteBuffer nioBuffer() {
        return this.buf.nioBuffer();
    }

    @Override
    public ByteBuffer nioBuffer(int index, int length) {
        return this.buf.nioBuffer(index, length);
    }

    @Override
    public ByteBuffer[] nioBuffers() {
        return this.buf.nioBuffers();
    }

    @Override
    public ByteBuffer[] nioBuffers(int index, int length) {
        return this.buf.nioBuffers(index, length);
    }

    @Override
    public ByteBuffer internalNioBuffer(int index, int length) {
        return this.buf.internalNioBuffer(index, length);
    }

    @Override
    public boolean hasArray() {
        return this.buf.hasArray();
    }

    @Override
    public byte[] array() {
        return this.buf.array();
    }

    @Override
    public int arrayOffset() {
        return this.buf.arrayOffset();
    }

    @Override
    public String toString(Charset charset) {
        return this.buf.toString(charset);
    }

    @Override
    public String toString(int index, int length, Charset charset) {
        return this.buf.toString(index, length, charset);
    }

    @Override
    public int hashCode() {
        return this.buf.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return this.buf.equals(obj);
    }

    @Override
    public int compareTo(ByteBuf buffer) {
        return this.buf.compareTo(buffer);
    }

    @Override
    public String toString() {
        return StringUtil.simpleClassName(this) + '(' + this.buf.toString() + ')';
    }

    @Override
    public ByteBuf retain(int increment) {
        this.buf.retain(increment);
        return this;
    }

    @Override
    public ByteBuf retain() {
        this.buf.retain();
        return this;
    }

    @Override
    public ByteBuf touch() {
        this.buf.touch();
        return this;
    }

    @Override
    public ByteBuf touch(Object hint) {
        this.buf.touch(hint);
        return this;
    }

    @Override
    public final boolean isReadable(int size) {
        return this.buf.isReadable(size);
    }

    @Override
    public final boolean isWritable(int size) {
        return this.buf.isWritable(size);
    }

    @Override
    public final int refCnt() {
        return this.buf.refCnt();
    }

    @Override
    public boolean release() {
        return this.buf.release();
    }

    @Override
    public boolean release(int decrement) {
        return this.buf.release(decrement);
    }
}

