/*
 * Decompiled with CFR 0_132.
 */
package io.netty.buffer;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.CompositeByteBuf;
import io.netty.buffer.SwappedByteBuf;
import io.netty.util.ByteProcessor;
import io.netty.util.ReferenceCounted;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.nio.channels.GatheringByteChannel;
import java.nio.channels.ScatteringByteChannel;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.List;

class WrappedCompositeByteBuf
extends CompositeByteBuf {
    private final CompositeByteBuf wrapped;

    WrappedCompositeByteBuf(CompositeByteBuf wrapped) {
        super(wrapped.alloc());
        this.wrapped = wrapped;
    }

    @Override
    public boolean release() {
        return this.wrapped.release();
    }

    @Override
    public boolean release(int decrement) {
        return this.wrapped.release(decrement);
    }

    @Override
    public final int maxCapacity() {
        return this.wrapped.maxCapacity();
    }

    @Override
    public final int readerIndex() {
        return this.wrapped.readerIndex();
    }

    @Override
    public final int writerIndex() {
        return this.wrapped.writerIndex();
    }

    @Override
    public final boolean isReadable() {
        return this.wrapped.isReadable();
    }

    @Override
    public final boolean isReadable(int numBytes) {
        return this.wrapped.isReadable(numBytes);
    }

    @Override
    public final boolean isWritable() {
        return this.wrapped.isWritable();
    }

    @Override
    public final boolean isWritable(int numBytes) {
        return this.wrapped.isWritable(numBytes);
    }

    @Override
    public final int readableBytes() {
        return this.wrapped.readableBytes();
    }

    @Override
    public final int writableBytes() {
        return this.wrapped.writableBytes();
    }

    @Override
    public final int maxWritableBytes() {
        return this.wrapped.maxWritableBytes();
    }

    @Override
    public int ensureWritable(int minWritableBytes, boolean force) {
        return this.wrapped.ensureWritable(minWritableBytes, force);
    }

    @Override
    public ByteBuf order(ByteOrder endianness) {
        return this.wrapped.order(endianness);
    }

    @Override
    public boolean getBoolean(int index) {
        return this.wrapped.getBoolean(index);
    }

    @Override
    public short getUnsignedByte(int index) {
        return this.wrapped.getUnsignedByte(index);
    }

    @Override
    public short getShort(int index) {
        return this.wrapped.getShort(index);
    }

    @Override
    public short getShortLE(int index) {
        return this.wrapped.getShortLE(index);
    }

    @Override
    public int getUnsignedShort(int index) {
        return this.wrapped.getUnsignedShort(index);
    }

    @Override
    public int getUnsignedShortLE(int index) {
        return this.wrapped.getUnsignedShortLE(index);
    }

    @Override
    public int getUnsignedMedium(int index) {
        return this.wrapped.getUnsignedMedium(index);
    }

    @Override
    public int getUnsignedMediumLE(int index) {
        return this.wrapped.getUnsignedMediumLE(index);
    }

    @Override
    public int getMedium(int index) {
        return this.wrapped.getMedium(index);
    }

    @Override
    public int getMediumLE(int index) {
        return this.wrapped.getMediumLE(index);
    }

    @Override
    public int getInt(int index) {
        return this.wrapped.getInt(index);
    }

    @Override
    public int getIntLE(int index) {
        return this.wrapped.getIntLE(index);
    }

    @Override
    public long getUnsignedInt(int index) {
        return this.wrapped.getUnsignedInt(index);
    }

    @Override
    public long getUnsignedIntLE(int index) {
        return this.wrapped.getUnsignedIntLE(index);
    }

    @Override
    public long getLong(int index) {
        return this.wrapped.getLong(index);
    }

    @Override
    public long getLongLE(int index) {
        return this.wrapped.getLongLE(index);
    }

    @Override
    public char getChar(int index) {
        return this.wrapped.getChar(index);
    }

    @Override
    public float getFloat(int index) {
        return this.wrapped.getFloat(index);
    }

    @Override
    public double getDouble(int index) {
        return this.wrapped.getDouble(index);
    }

    @Override
    public ByteBuf setShortLE(int index, int value) {
        return this.wrapped.setShortLE(index, value);
    }

    @Override
    public ByteBuf setMediumLE(int index, int value) {
        return this.wrapped.setMediumLE(index, value);
    }

    @Override
    public ByteBuf setIntLE(int index, int value) {
        return this.wrapped.setIntLE(index, value);
    }

    @Override
    public ByteBuf setLongLE(int index, long value) {
        return this.wrapped.setLongLE(index, value);
    }

    @Override
    public byte readByte() {
        return this.wrapped.readByte();
    }

    @Override
    public boolean readBoolean() {
        return this.wrapped.readBoolean();
    }

    @Override
    public short readUnsignedByte() {
        return this.wrapped.readUnsignedByte();
    }

    @Override
    public short readShort() {
        return this.wrapped.readShort();
    }

    @Override
    public short readShortLE() {
        return this.wrapped.readShortLE();
    }

    @Override
    public int readUnsignedShort() {
        return this.wrapped.readUnsignedShort();
    }

    @Override
    public int readUnsignedShortLE() {
        return this.wrapped.readUnsignedShortLE();
    }

    @Override
    public int readMedium() {
        return this.wrapped.readMedium();
    }

    @Override
    public int readMediumLE() {
        return this.wrapped.readMediumLE();
    }

    @Override
    public int readUnsignedMedium() {
        return this.wrapped.readUnsignedMedium();
    }

    @Override
    public int readUnsignedMediumLE() {
        return this.wrapped.readUnsignedMediumLE();
    }

    @Override
    public int readInt() {
        return this.wrapped.readInt();
    }

    @Override
    public int readIntLE() {
        return this.wrapped.readIntLE();
    }

    @Override
    public long readUnsignedInt() {
        return this.wrapped.readUnsignedInt();
    }

    @Override
    public long readUnsignedIntLE() {
        return this.wrapped.readUnsignedIntLE();
    }

    @Override
    public long readLong() {
        return this.wrapped.readLong();
    }

    @Override
    public long readLongLE() {
        return this.wrapped.readLongLE();
    }

    @Override
    public char readChar() {
        return this.wrapped.readChar();
    }

    @Override
    public float readFloat() {
        return this.wrapped.readFloat();
    }

    @Override
    public double readDouble() {
        return this.wrapped.readDouble();
    }

    @Override
    public ByteBuf readBytes(int length) {
        return this.wrapped.readBytes(length);
    }

    @Override
    public ByteBuf slice() {
        return this.wrapped.slice();
    }

    @Override
    public ByteBuf retainedSlice() {
        return this.wrapped.retainedSlice();
    }

    @Override
    public ByteBuf slice(int index, int length) {
        return this.wrapped.slice(index, length);
    }

    @Override
    public ByteBuf retainedSlice(int index, int length) {
        return this.wrapped.retainedSlice(index, length);
    }

    @Override
    public ByteBuffer nioBuffer() {
        return this.wrapped.nioBuffer();
    }

    @Override
    public String toString(Charset charset) {
        return this.wrapped.toString(charset);
    }

    @Override
    public String toString(int index, int length, Charset charset) {
        return this.wrapped.toString(index, length, charset);
    }

    @Override
    public int indexOf(int fromIndex, int toIndex, byte value) {
        return this.wrapped.indexOf(fromIndex, toIndex, value);
    }

    @Override
    public int bytesBefore(byte value) {
        return this.wrapped.bytesBefore(value);
    }

    @Override
    public int bytesBefore(int length, byte value) {
        return this.wrapped.bytesBefore(length, value);
    }

    @Override
    public int bytesBefore(int index, int length, byte value) {
        return this.wrapped.bytesBefore(index, length, value);
    }

    @Override
    public int forEachByte(ByteProcessor processor) {
        return this.wrapped.forEachByte(processor);
    }

    @Override
    public int forEachByte(int index, int length, ByteProcessor processor) {
        return this.wrapped.forEachByte(index, length, processor);
    }

    @Override
    public int forEachByteDesc(ByteProcessor processor) {
        return this.wrapped.forEachByteDesc(processor);
    }

    @Override
    public int forEachByteDesc(int index, int length, ByteProcessor processor) {
        return this.wrapped.forEachByteDesc(index, length, processor);
    }

    @Override
    public final int hashCode() {
        return this.wrapped.hashCode();
    }

    @Override
    public final boolean equals(Object o) {
        return this.wrapped.equals(o);
    }

    @Override
    public final int compareTo(ByteBuf that) {
        return this.wrapped.compareTo(that);
    }

    @Override
    public final int refCnt() {
        return this.wrapped.refCnt();
    }

    @Override
    public ByteBuf duplicate() {
        return this.wrapped.duplicate();
    }

    @Override
    public ByteBuf retainedDuplicate() {
        return this.wrapped.retainedDuplicate();
    }

    @Override
    public ByteBuf readSlice(int length) {
        return this.wrapped.readSlice(length);
    }

    @Override
    public ByteBuf readRetainedSlice(int length) {
        return this.wrapped.readRetainedSlice(length);
    }

    @Override
    public int readBytes(GatheringByteChannel out, int length) throws IOException {
        return this.wrapped.readBytes(out, length);
    }

    @Override
    public ByteBuf writeShortLE(int value) {
        return this.wrapped.writeShortLE(value);
    }

    @Override
    public ByteBuf writeMediumLE(int value) {
        return this.wrapped.writeMediumLE(value);
    }

    @Override
    public ByteBuf writeIntLE(int value) {
        return this.wrapped.writeIntLE(value);
    }

    @Override
    public ByteBuf writeLongLE(long value) {
        return this.wrapped.writeLongLE(value);
    }

    @Override
    public int writeBytes(InputStream in, int length) throws IOException {
        return this.wrapped.writeBytes(in, length);
    }

    @Override
    public int writeBytes(ScatteringByteChannel in, int length) throws IOException {
        return this.wrapped.writeBytes(in, length);
    }

    @Override
    public ByteBuf copy() {
        return this.wrapped.copy();
    }

    @Override
    public CompositeByteBuf addComponent(ByteBuf buffer) {
        this.wrapped.addComponent(buffer);
        return this;
    }

    @Override
    public /* varargs */ CompositeByteBuf addComponents(ByteBuf ... buffers) {
        this.wrapped.addComponents(buffers);
        return this;
    }

    @Override
    public CompositeByteBuf addComponents(Iterable<ByteBuf> buffers) {
        this.wrapped.addComponents(buffers);
        return this;
    }

    @Override
    public CompositeByteBuf addComponent(int cIndex, ByteBuf buffer) {
        this.wrapped.addComponent(cIndex, buffer);
        return this;
    }

    @Override
    public /* varargs */ CompositeByteBuf addComponents(int cIndex, ByteBuf ... buffers) {
        this.wrapped.addComponents(cIndex, buffers);
        return this;
    }

    @Override
    public CompositeByteBuf addComponents(int cIndex, Iterable<ByteBuf> buffers) {
        this.wrapped.addComponents(cIndex, buffers);
        return this;
    }

    @Override
    public CompositeByteBuf addComponent(boolean increaseWriterIndex, ByteBuf buffer) {
        this.wrapped.addComponent(increaseWriterIndex, buffer);
        return this;
    }

    @Override
    public /* varargs */ CompositeByteBuf addComponents(boolean increaseWriterIndex, ByteBuf ... buffers) {
        this.wrapped.addComponents(increaseWriterIndex, buffers);
        return this;
    }

    @Override
    public CompositeByteBuf addComponents(boolean increaseWriterIndex, Iterable<ByteBuf> buffers) {
        this.wrapped.addComponents(increaseWriterIndex, buffers);
        return this;
    }

    @Override
    public CompositeByteBuf addComponent(boolean increaseWriterIndex, int cIndex, ByteBuf buffer) {
        this.wrapped.addComponent(increaseWriterIndex, cIndex, buffer);
        return this;
    }

    @Override
    public CompositeByteBuf removeComponent(int cIndex) {
        this.wrapped.removeComponent(cIndex);
        return this;
    }

    @Override
    public CompositeByteBuf removeComponents(int cIndex, int numComponents) {
        this.wrapped.removeComponents(cIndex, numComponents);
        return this;
    }

    @Override
    public Iterator<ByteBuf> iterator() {
        return this.wrapped.iterator();
    }

    @Override
    public List<ByteBuf> decompose(int offset, int length) {
        return this.wrapped.decompose(offset, length);
    }

    @Override
    public final boolean isDirect() {
        return this.wrapped.isDirect();
    }

    @Override
    public final boolean hasArray() {
        return this.wrapped.hasArray();
    }

    @Override
    public final byte[] array() {
        return this.wrapped.array();
    }

    @Override
    public final int arrayOffset() {
        return this.wrapped.arrayOffset();
    }

    @Override
    public final boolean hasMemoryAddress() {
        return this.wrapped.hasMemoryAddress();
    }

    @Override
    public final long memoryAddress() {
        return this.wrapped.memoryAddress();
    }

    @Override
    public final int capacity() {
        return this.wrapped.capacity();
    }

    @Override
    public CompositeByteBuf capacity(int newCapacity) {
        this.wrapped.capacity(newCapacity);
        return this;
    }

    @Override
    public final ByteBufAllocator alloc() {
        return this.wrapped.alloc();
    }

    @Override
    public final ByteOrder order() {
        return this.wrapped.order();
    }

    @Override
    public final int numComponents() {
        return this.wrapped.numComponents();
    }

    @Override
    public final int maxNumComponents() {
        return this.wrapped.maxNumComponents();
    }

    @Override
    public final int toComponentIndex(int offset) {
        return this.wrapped.toComponentIndex(offset);
    }

    @Override
    public final int toByteIndex(int cIndex) {
        return this.wrapped.toByteIndex(cIndex);
    }

    @Override
    public byte getByte(int index) {
        return this.wrapped.getByte(index);
    }

    @Override
    protected final byte _getByte(int index) {
        return this.wrapped._getByte(index);
    }

    @Override
    protected final short _getShort(int index) {
        return this.wrapped._getShort(index);
    }

    @Override
    protected final short _getShortLE(int index) {
        return this.wrapped._getShortLE(index);
    }

    @Override
    protected final int _getUnsignedMedium(int index) {
        return this.wrapped._getUnsignedMedium(index);
    }

    @Override
    protected final int _getUnsignedMediumLE(int index) {
        return this.wrapped._getUnsignedMediumLE(index);
    }

    @Override
    protected final int _getInt(int index) {
        return this.wrapped._getInt(index);
    }

    @Override
    protected final int _getIntLE(int index) {
        return this.wrapped._getIntLE(index);
    }

    @Override
    protected final long _getLong(int index) {
        return this.wrapped._getLong(index);
    }

    @Override
    protected final long _getLongLE(int index) {
        return this.wrapped._getLongLE(index);
    }

    @Override
    public CompositeByteBuf getBytes(int index, byte[] dst, int dstIndex, int length) {
        this.wrapped.getBytes(index, dst, dstIndex, length);
        return this;
    }

    @Override
    public CompositeByteBuf getBytes(int index, ByteBuffer dst) {
        this.wrapped.getBytes(index, dst);
        return this;
    }

    @Override
    public CompositeByteBuf getBytes(int index, ByteBuf dst, int dstIndex, int length) {
        this.wrapped.getBytes(index, dst, dstIndex, length);
        return this;
    }

    @Override
    public int getBytes(int index, GatheringByteChannel out, int length) throws IOException {
        return this.wrapped.getBytes(index, out, length);
    }

    @Override
    public CompositeByteBuf getBytes(int index, OutputStream out, int length) throws IOException {
        this.wrapped.getBytes(index, out, length);
        return this;
    }

    @Override
    public CompositeByteBuf setByte(int index, int value) {
        this.wrapped.setByte(index, value);
        return this;
    }

    @Override
    protected final void _setByte(int index, int value) {
        this.wrapped._setByte(index, value);
    }

    @Override
    public CompositeByteBuf setShort(int index, int value) {
        this.wrapped.setShort(index, value);
        return this;
    }

    @Override
    protected final void _setShort(int index, int value) {
        this.wrapped._setShort(index, value);
    }

    @Override
    protected final void _setShortLE(int index, int value) {
        this.wrapped._setShortLE(index, value);
    }

    @Override
    public CompositeByteBuf setMedium(int index, int value) {
        this.wrapped.setMedium(index, value);
        return this;
    }

    @Override
    protected final void _setMedium(int index, int value) {
        this.wrapped._setMedium(index, value);
    }

    @Override
    protected final void _setMediumLE(int index, int value) {
        this.wrapped._setMediumLE(index, value);
    }

    @Override
    public CompositeByteBuf setInt(int index, int value) {
        this.wrapped.setInt(index, value);
        return this;
    }

    @Override
    protected final void _setInt(int index, int value) {
        this.wrapped._setInt(index, value);
    }

    @Override
    protected final void _setIntLE(int index, int value) {
        this.wrapped._setIntLE(index, value);
    }

    @Override
    public CompositeByteBuf setLong(int index, long value) {
        this.wrapped.setLong(index, value);
        return this;
    }

    @Override
    protected final void _setLong(int index, long value) {
        this.wrapped._setLong(index, value);
    }

    @Override
    protected final void _setLongLE(int index, long value) {
        this.wrapped._setLongLE(index, value);
    }

    @Override
    public CompositeByteBuf setBytes(int index, byte[] src, int srcIndex, int length) {
        this.wrapped.setBytes(index, src, srcIndex, length);
        return this;
    }

    @Override
    public CompositeByteBuf setBytes(int index, ByteBuffer src) {
        this.wrapped.setBytes(index, src);
        return this;
    }

    @Override
    public CompositeByteBuf setBytes(int index, ByteBuf src, int srcIndex, int length) {
        this.wrapped.setBytes(index, src, srcIndex, length);
        return this;
    }

    @Override
    public int setBytes(int index, InputStream in, int length) throws IOException {
        return this.wrapped.setBytes(index, in, length);
    }

    @Override
    public int setBytes(int index, ScatteringByteChannel in, int length) throws IOException {
        return this.wrapped.setBytes(index, in, length);
    }

    @Override
    public ByteBuf copy(int index, int length) {
        return this.wrapped.copy(index, length);
    }

    @Override
    public final ByteBuf component(int cIndex) {
        return this.wrapped.component(cIndex);
    }

    @Override
    public final ByteBuf componentAtOffset(int offset) {
        return this.wrapped.componentAtOffset(offset);
    }

    @Override
    public final ByteBuf internalComponent(int cIndex) {
        return this.wrapped.internalComponent(cIndex);
    }

    @Override
    public final ByteBuf internalComponentAtOffset(int offset) {
        return this.wrapped.internalComponentAtOffset(offset);
    }

    @Override
    public int nioBufferCount() {
        return this.wrapped.nioBufferCount();
    }

    @Override
    public ByteBuffer internalNioBuffer(int index, int length) {
        return this.wrapped.internalNioBuffer(index, length);
    }

    @Override
    public ByteBuffer nioBuffer(int index, int length) {
        return this.wrapped.nioBuffer(index, length);
    }

    @Override
    public ByteBuffer[] nioBuffers(int index, int length) {
        return this.wrapped.nioBuffers(index, length);
    }

    @Override
    public CompositeByteBuf consolidate() {
        this.wrapped.consolidate();
        return this;
    }

    @Override
    public CompositeByteBuf consolidate(int cIndex, int numComponents) {
        this.wrapped.consolidate(cIndex, numComponents);
        return this;
    }

    @Override
    public CompositeByteBuf discardReadComponents() {
        this.wrapped.discardReadComponents();
        return this;
    }

    @Override
    public CompositeByteBuf discardReadBytes() {
        this.wrapped.discardReadBytes();
        return this;
    }

    @Override
    public final String toString() {
        return this.wrapped.toString();
    }

    @Override
    public final CompositeByteBuf readerIndex(int readerIndex) {
        this.wrapped.readerIndex(readerIndex);
        return this;
    }

    @Override
    public final CompositeByteBuf writerIndex(int writerIndex) {
        this.wrapped.writerIndex(writerIndex);
        return this;
    }

    @Override
    public final CompositeByteBuf setIndex(int readerIndex, int writerIndex) {
        this.wrapped.setIndex(readerIndex, writerIndex);
        return this;
    }

    @Override
    public final CompositeByteBuf clear() {
        this.wrapped.clear();
        return this;
    }

    @Override
    public final CompositeByteBuf markReaderIndex() {
        this.wrapped.markReaderIndex();
        return this;
    }

    @Override
    public final CompositeByteBuf resetReaderIndex() {
        this.wrapped.resetReaderIndex();
        return this;
    }

    @Override
    public final CompositeByteBuf markWriterIndex() {
        this.wrapped.markWriterIndex();
        return this;
    }

    @Override
    public final CompositeByteBuf resetWriterIndex() {
        this.wrapped.resetWriterIndex();
        return this;
    }

    @Override
    public CompositeByteBuf ensureWritable(int minWritableBytes) {
        this.wrapped.ensureWritable(minWritableBytes);
        return this;
    }

    @Override
    public CompositeByteBuf getBytes(int index, ByteBuf dst) {
        this.wrapped.getBytes(index, dst);
        return this;
    }

    @Override
    public CompositeByteBuf getBytes(int index, ByteBuf dst, int length) {
        this.wrapped.getBytes(index, dst, length);
        return this;
    }

    @Override
    public CompositeByteBuf getBytes(int index, byte[] dst) {
        this.wrapped.getBytes(index, dst);
        return this;
    }

    @Override
    public CompositeByteBuf setBoolean(int index, boolean value) {
        this.wrapped.setBoolean(index, value);
        return this;
    }

    @Override
    public CompositeByteBuf setChar(int index, int value) {
        this.wrapped.setChar(index, value);
        return this;
    }

    @Override
    public CompositeByteBuf setFloat(int index, float value) {
        this.wrapped.setFloat(index, value);
        return this;
    }

    @Override
    public CompositeByteBuf setDouble(int index, double value) {
        this.wrapped.setDouble(index, value);
        return this;
    }

    @Override
    public CompositeByteBuf setBytes(int index, ByteBuf src) {
        this.wrapped.setBytes(index, src);
        return this;
    }

    @Override
    public CompositeByteBuf setBytes(int index, ByteBuf src, int length) {
        this.wrapped.setBytes(index, src, length);
        return this;
    }

    @Override
    public CompositeByteBuf setBytes(int index, byte[] src) {
        this.wrapped.setBytes(index, src);
        return this;
    }

    @Override
    public CompositeByteBuf setZero(int index, int length) {
        this.wrapped.setZero(index, length);
        return this;
    }

    @Override
    public CompositeByteBuf readBytes(ByteBuf dst) {
        this.wrapped.readBytes(dst);
        return this;
    }

    @Override
    public CompositeByteBuf readBytes(ByteBuf dst, int length) {
        this.wrapped.readBytes(dst, length);
        return this;
    }

    @Override
    public CompositeByteBuf readBytes(ByteBuf dst, int dstIndex, int length) {
        this.wrapped.readBytes(dst, dstIndex, length);
        return this;
    }

    @Override
    public CompositeByteBuf readBytes(byte[] dst) {
        this.wrapped.readBytes(dst);
        return this;
    }

    @Override
    public CompositeByteBuf readBytes(byte[] dst, int dstIndex, int length) {
        this.wrapped.readBytes(dst, dstIndex, length);
        return this;
    }

    @Override
    public CompositeByteBuf readBytes(ByteBuffer dst) {
        this.wrapped.readBytes(dst);
        return this;
    }

    @Override
    public CompositeByteBuf readBytes(OutputStream out, int length) throws IOException {
        this.wrapped.readBytes(out, length);
        return this;
    }

    @Override
    public int getBytes(int index, FileChannel out, long position, int length) throws IOException {
        return this.wrapped.getBytes(index, out, position, length);
    }

    @Override
    public int setBytes(int index, FileChannel in, long position, int length) throws IOException {
        return this.wrapped.setBytes(index, in, position, length);
    }

    @Override
    public boolean isReadOnly() {
        return this.wrapped.isReadOnly();
    }

    @Override
    public ByteBuf asReadOnly() {
        return this.wrapped.asReadOnly();
    }

    @Override
    protected SwappedByteBuf newSwappedByteBuf() {
        return this.wrapped.newSwappedByteBuf();
    }

    @Override
    public CharSequence getCharSequence(int index, int length, Charset charset) {
        return this.wrapped.getCharSequence(index, length, charset);
    }

    @Override
    public CharSequence readCharSequence(int length, Charset charset) {
        return this.wrapped.readCharSequence(length, charset);
    }

    @Override
    public int setCharSequence(int index, CharSequence sequence, Charset charset) {
        return this.wrapped.setCharSequence(index, sequence, charset);
    }

    @Override
    public int readBytes(FileChannel out, long position, int length) throws IOException {
        return this.wrapped.readBytes(out, position, length);
    }

    @Override
    public int writeBytes(FileChannel in, long position, int length) throws IOException {
        return this.wrapped.writeBytes(in, position, length);
    }

    @Override
    public int writeCharSequence(CharSequence sequence, Charset charset) {
        return this.wrapped.writeCharSequence(sequence, charset);
    }

    @Override
    public CompositeByteBuf skipBytes(int length) {
        this.wrapped.skipBytes(length);
        return this;
    }

    @Override
    public CompositeByteBuf writeBoolean(boolean value) {
        this.wrapped.writeBoolean(value);
        return this;
    }

    @Override
    public CompositeByteBuf writeByte(int value) {
        this.wrapped.writeByte(value);
        return this;
    }

    @Override
    public CompositeByteBuf writeShort(int value) {
        this.wrapped.writeShort(value);
        return this;
    }

    @Override
    public CompositeByteBuf writeMedium(int value) {
        this.wrapped.writeMedium(value);
        return this;
    }

    @Override
    public CompositeByteBuf writeInt(int value) {
        this.wrapped.writeInt(value);
        return this;
    }

    @Override
    public CompositeByteBuf writeLong(long value) {
        this.wrapped.writeLong(value);
        return this;
    }

    @Override
    public CompositeByteBuf writeChar(int value) {
        this.wrapped.writeChar(value);
        return this;
    }

    @Override
    public CompositeByteBuf writeFloat(float value) {
        this.wrapped.writeFloat(value);
        return this;
    }

    @Override
    public CompositeByteBuf writeDouble(double value) {
        this.wrapped.writeDouble(value);
        return this;
    }

    @Override
    public CompositeByteBuf writeBytes(ByteBuf src) {
        this.wrapped.writeBytes(src);
        return this;
    }

    @Override
    public CompositeByteBuf writeBytes(ByteBuf src, int length) {
        this.wrapped.writeBytes(src, length);
        return this;
    }

    @Override
    public CompositeByteBuf writeBytes(ByteBuf src, int srcIndex, int length) {
        this.wrapped.writeBytes(src, srcIndex, length);
        return this;
    }

    @Override
    public CompositeByteBuf writeBytes(byte[] src) {
        this.wrapped.writeBytes(src);
        return this;
    }

    @Override
    public CompositeByteBuf writeBytes(byte[] src, int srcIndex, int length) {
        this.wrapped.writeBytes(src, srcIndex, length);
        return this;
    }

    @Override
    public CompositeByteBuf writeBytes(ByteBuffer src) {
        this.wrapped.writeBytes(src);
        return this;
    }

    @Override
    public CompositeByteBuf writeZero(int length) {
        this.wrapped.writeZero(length);
        return this;
    }

    @Override
    public CompositeByteBuf retain(int increment) {
        this.wrapped.retain(increment);
        return this;
    }

    @Override
    public CompositeByteBuf retain() {
        this.wrapped.retain();
        return this;
    }

    @Override
    public CompositeByteBuf touch() {
        this.wrapped.touch();
        return this;
    }

    @Override
    public CompositeByteBuf touch(Object hint) {
        this.wrapped.touch(hint);
        return this;
    }

    @Override
    public ByteBuffer[] nioBuffers() {
        return this.wrapped.nioBuffers();
    }

    @Override
    public CompositeByteBuf discardSomeReadBytes() {
        this.wrapped.discardSomeReadBytes();
        return this;
    }

    @Override
    public final void deallocate() {
        this.wrapped.deallocate();
    }

    @Override
    public final ByteBuf unwrap() {
        return this.wrapped;
    }
}

