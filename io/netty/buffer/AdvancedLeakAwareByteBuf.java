/*
 * Decompiled with CFR 0_132.
 */
package io.netty.buffer;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.SimpleLeakAwareByteBuf;
import io.netty.util.ByteProcessor;
import io.netty.util.ReferenceCounted;
import io.netty.util.ResourceLeakDetector;
import io.netty.util.ResourceLeakTracker;
import io.netty.util.internal.SystemPropertyUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.nio.channels.GatheringByteChannel;
import java.nio.channels.ScatteringByteChannel;
import java.nio.charset.Charset;

final class AdvancedLeakAwareByteBuf
extends SimpleLeakAwareByteBuf {
    private static final String PROP_ACQUIRE_AND_RELEASE_ONLY = "io.netty.leakDetection.acquireAndReleaseOnly";
    private static final boolean ACQUIRE_AND_RELEASE_ONLY;
    private static final InternalLogger logger;

    AdvancedLeakAwareByteBuf(ByteBuf buf, ResourceLeakTracker<ByteBuf> leak) {
        super(buf, leak);
    }

    AdvancedLeakAwareByteBuf(ByteBuf wrapped, ByteBuf trackedByteBuf, ResourceLeakTracker<ByteBuf> leak) {
        super(wrapped, trackedByteBuf, leak);
    }

    static void recordLeakNonRefCountingOperation(ResourceLeakTracker<ByteBuf> leak) {
        if (!ACQUIRE_AND_RELEASE_ONLY) {
            leak.record();
        }
    }

    @Override
    public ByteBuf order(ByteOrder endianness) {
        AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
        return super.order(endianness);
    }

    @Override
    public ByteBuf slice() {
        AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
        return super.slice();
    }

    @Override
    public ByteBuf slice(int index, int length) {
        AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
        return super.slice(index, length);
    }

    @Override
    public ByteBuf retainedSlice() {
        AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
        return super.retainedSlice();
    }

    @Override
    public ByteBuf retainedSlice(int index, int length) {
        AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
        return super.retainedSlice(index, length);
    }

    @Override
    public ByteBuf retainedDuplicate() {
        AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
        return super.retainedDuplicate();
    }

    @Override
    public ByteBuf readRetainedSlice(int length) {
        AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
        return super.readRetainedSlice(length);
    }

    @Override
    public ByteBuf duplicate() {
        AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
        return super.duplicate();
    }

    @Override
    public ByteBuf readSlice(int length) {
        AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
        return super.readSlice(length);
    }

    @Override
    public ByteBuf discardReadBytes() {
        AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
        return super.discardReadBytes();
    }

    @Override
    public ByteBuf discardSomeReadBytes() {
        AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
        return super.discardSomeReadBytes();
    }

    @Override
    public ByteBuf ensureWritable(int minWritableBytes) {
        AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
        return super.ensureWritable(minWritableBytes);
    }

    @Override
    public int ensureWritable(int minWritableBytes, boolean force) {
        AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
        return super.ensureWritable(minWritableBytes, force);
    }

    @Override
    public boolean getBoolean(int index) {
        AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
        return super.getBoolean(index);
    }

    @Override
    public byte getByte(int index) {
        AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
        return super.getByte(index);
    }

    @Override
    public short getUnsignedByte(int index) {
        AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
        return super.getUnsignedByte(index);
    }

    @Override
    public short getShort(int index) {
        AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
        return super.getShort(index);
    }

    @Override
    public int getUnsignedShort(int index) {
        AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
        return super.getUnsignedShort(index);
    }

    @Override
    public int getMedium(int index) {
        AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
        return super.getMedium(index);
    }

    @Override
    public int getUnsignedMedium(int index) {
        AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
        return super.getUnsignedMedium(index);
    }

    @Override
    public int getInt(int index) {
        AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
        return super.getInt(index);
    }

    @Override
    public long getUnsignedInt(int index) {
        AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
        return super.getUnsignedInt(index);
    }

    @Override
    public long getLong(int index) {
        AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
        return super.getLong(index);
    }

    @Override
    public char getChar(int index) {
        AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
        return super.getChar(index);
    }

    @Override
    public float getFloat(int index) {
        AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
        return super.getFloat(index);
    }

    @Override
    public double getDouble(int index) {
        AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
        return super.getDouble(index);
    }

    @Override
    public ByteBuf getBytes(int index, ByteBuf dst) {
        AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
        return super.getBytes(index, dst);
    }

    @Override
    public ByteBuf getBytes(int index, ByteBuf dst, int length) {
        AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
        return super.getBytes(index, dst, length);
    }

    @Override
    public ByteBuf getBytes(int index, ByteBuf dst, int dstIndex, int length) {
        AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
        return super.getBytes(index, dst, dstIndex, length);
    }

    @Override
    public ByteBuf getBytes(int index, byte[] dst) {
        AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
        return super.getBytes(index, dst);
    }

    @Override
    public ByteBuf getBytes(int index, byte[] dst, int dstIndex, int length) {
        AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
        return super.getBytes(index, dst, dstIndex, length);
    }

    @Override
    public ByteBuf getBytes(int index, ByteBuffer dst) {
        AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
        return super.getBytes(index, dst);
    }

    @Override
    public ByteBuf getBytes(int index, OutputStream out, int length) throws IOException {
        AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
        return super.getBytes(index, out, length);
    }

    @Override
    public int getBytes(int index, GatheringByteChannel out, int length) throws IOException {
        AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
        return super.getBytes(index, out, length);
    }

    @Override
    public CharSequence getCharSequence(int index, int length, Charset charset) {
        AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
        return super.getCharSequence(index, length, charset);
    }

    @Override
    public ByteBuf setBoolean(int index, boolean value) {
        AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
        return super.setBoolean(index, value);
    }

    @Override
    public ByteBuf setByte(int index, int value) {
        AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
        return super.setByte(index, value);
    }

    @Override
    public ByteBuf setShort(int index, int value) {
        AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
        return super.setShort(index, value);
    }

    @Override
    public ByteBuf setMedium(int index, int value) {
        AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
        return super.setMedium(index, value);
    }

    @Override
    public ByteBuf setInt(int index, int value) {
        AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
        return super.setInt(index, value);
    }

    @Override
    public ByteBuf setLong(int index, long value) {
        AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
        return super.setLong(index, value);
    }

    @Override
    public ByteBuf setChar(int index, int value) {
        AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
        return super.setChar(index, value);
    }

    @Override
    public ByteBuf setFloat(int index, float value) {
        AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
        return super.setFloat(index, value);
    }

    @Override
    public ByteBuf setDouble(int index, double value) {
        AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
        return super.setDouble(index, value);
    }

    @Override
    public ByteBuf setBytes(int index, ByteBuf src) {
        AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
        return super.setBytes(index, src);
    }

    @Override
    public ByteBuf setBytes(int index, ByteBuf src, int length) {
        AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
        return super.setBytes(index, src, length);
    }

    @Override
    public ByteBuf setBytes(int index, ByteBuf src, int srcIndex, int length) {
        AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
        return super.setBytes(index, src, srcIndex, length);
    }

    @Override
    public ByteBuf setBytes(int index, byte[] src) {
        AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
        return super.setBytes(index, src);
    }

    @Override
    public ByteBuf setBytes(int index, byte[] src, int srcIndex, int length) {
        AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
        return super.setBytes(index, src, srcIndex, length);
    }

    @Override
    public ByteBuf setBytes(int index, ByteBuffer src) {
        AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
        return super.setBytes(index, src);
    }

    @Override
    public int setBytes(int index, InputStream in, int length) throws IOException {
        AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
        return super.setBytes(index, in, length);
    }

    @Override
    public int setBytes(int index, ScatteringByteChannel in, int length) throws IOException {
        AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
        return super.setBytes(index, in, length);
    }

    @Override
    public ByteBuf setZero(int index, int length) {
        AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
        return super.setZero(index, length);
    }

    @Override
    public int setCharSequence(int index, CharSequence sequence, Charset charset) {
        AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
        return super.setCharSequence(index, sequence, charset);
    }

    @Override
    public boolean readBoolean() {
        AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
        return super.readBoolean();
    }

    @Override
    public byte readByte() {
        AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
        return super.readByte();
    }

    @Override
    public short readUnsignedByte() {
        AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
        return super.readUnsignedByte();
    }

    @Override
    public short readShort() {
        AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
        return super.readShort();
    }

    @Override
    public int readUnsignedShort() {
        AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
        return super.readUnsignedShort();
    }

    @Override
    public int readMedium() {
        AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
        return super.readMedium();
    }

    @Override
    public int readUnsignedMedium() {
        AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
        return super.readUnsignedMedium();
    }

    @Override
    public int readInt() {
        AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
        return super.readInt();
    }

    @Override
    public long readUnsignedInt() {
        AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
        return super.readUnsignedInt();
    }

    @Override
    public long readLong() {
        AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
        return super.readLong();
    }

    @Override
    public char readChar() {
        AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
        return super.readChar();
    }

    @Override
    public float readFloat() {
        AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
        return super.readFloat();
    }

    @Override
    public double readDouble() {
        AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
        return super.readDouble();
    }

    @Override
    public ByteBuf readBytes(int length) {
        AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
        return super.readBytes(length);
    }

    @Override
    public ByteBuf readBytes(ByteBuf dst) {
        AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
        return super.readBytes(dst);
    }

    @Override
    public ByteBuf readBytes(ByteBuf dst, int length) {
        AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
        return super.readBytes(dst, length);
    }

    @Override
    public ByteBuf readBytes(ByteBuf dst, int dstIndex, int length) {
        AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
        return super.readBytes(dst, dstIndex, length);
    }

    @Override
    public ByteBuf readBytes(byte[] dst) {
        AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
        return super.readBytes(dst);
    }

    @Override
    public ByteBuf readBytes(byte[] dst, int dstIndex, int length) {
        AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
        return super.readBytes(dst, dstIndex, length);
    }

    @Override
    public ByteBuf readBytes(ByteBuffer dst) {
        AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
        return super.readBytes(dst);
    }

    @Override
    public ByteBuf readBytes(OutputStream out, int length) throws IOException {
        AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
        return super.readBytes(out, length);
    }

    @Override
    public int readBytes(GatheringByteChannel out, int length) throws IOException {
        AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
        return super.readBytes(out, length);
    }

    @Override
    public CharSequence readCharSequence(int length, Charset charset) {
        AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
        return super.readCharSequence(length, charset);
    }

    @Override
    public ByteBuf skipBytes(int length) {
        AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
        return super.skipBytes(length);
    }

    @Override
    public ByteBuf writeBoolean(boolean value) {
        AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
        return super.writeBoolean(value);
    }

    @Override
    public ByteBuf writeByte(int value) {
        AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
        return super.writeByte(value);
    }

    @Override
    public ByteBuf writeShort(int value) {
        AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
        return super.writeShort(value);
    }

    @Override
    public ByteBuf writeMedium(int value) {
        AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
        return super.writeMedium(value);
    }

    @Override
    public ByteBuf writeInt(int value) {
        AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
        return super.writeInt(value);
    }

    @Override
    public ByteBuf writeLong(long value) {
        AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
        return super.writeLong(value);
    }

    @Override
    public ByteBuf writeChar(int value) {
        AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
        return super.writeChar(value);
    }

    @Override
    public ByteBuf writeFloat(float value) {
        AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
        return super.writeFloat(value);
    }

    @Override
    public ByteBuf writeDouble(double value) {
        AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
        return super.writeDouble(value);
    }

    @Override
    public ByteBuf writeBytes(ByteBuf src) {
        AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
        return super.writeBytes(src);
    }

    @Override
    public ByteBuf writeBytes(ByteBuf src, int length) {
        AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
        return super.writeBytes(src, length);
    }

    @Override
    public ByteBuf writeBytes(ByteBuf src, int srcIndex, int length) {
        AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
        return super.writeBytes(src, srcIndex, length);
    }

    @Override
    public ByteBuf writeBytes(byte[] src) {
        AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
        return super.writeBytes(src);
    }

    @Override
    public ByteBuf writeBytes(byte[] src, int srcIndex, int length) {
        AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
        return super.writeBytes(src, srcIndex, length);
    }

    @Override
    public ByteBuf writeBytes(ByteBuffer src) {
        AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
        return super.writeBytes(src);
    }

    @Override
    public int writeBytes(InputStream in, int length) throws IOException {
        AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
        return super.writeBytes(in, length);
    }

    @Override
    public int writeBytes(ScatteringByteChannel in, int length) throws IOException {
        AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
        return super.writeBytes(in, length);
    }

    @Override
    public ByteBuf writeZero(int length) {
        AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
        return super.writeZero(length);
    }

    @Override
    public int indexOf(int fromIndex, int toIndex, byte value) {
        AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
        return super.indexOf(fromIndex, toIndex, value);
    }

    @Override
    public int bytesBefore(byte value) {
        AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
        return super.bytesBefore(value);
    }

    @Override
    public int bytesBefore(int length, byte value) {
        AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
        return super.bytesBefore(length, value);
    }

    @Override
    public int bytesBefore(int index, int length, byte value) {
        AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
        return super.bytesBefore(index, length, value);
    }

    @Override
    public int forEachByte(ByteProcessor processor) {
        AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
        return super.forEachByte(processor);
    }

    @Override
    public int forEachByte(int index, int length, ByteProcessor processor) {
        AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
        return super.forEachByte(index, length, processor);
    }

    @Override
    public int forEachByteDesc(ByteProcessor processor) {
        AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
        return super.forEachByteDesc(processor);
    }

    @Override
    public int forEachByteDesc(int index, int length, ByteProcessor processor) {
        AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
        return super.forEachByteDesc(index, length, processor);
    }

    @Override
    public ByteBuf copy() {
        AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
        return super.copy();
    }

    @Override
    public ByteBuf copy(int index, int length) {
        AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
        return super.copy(index, length);
    }

    @Override
    public int nioBufferCount() {
        AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
        return super.nioBufferCount();
    }

    @Override
    public ByteBuffer nioBuffer() {
        AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
        return super.nioBuffer();
    }

    @Override
    public ByteBuffer nioBuffer(int index, int length) {
        AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
        return super.nioBuffer(index, length);
    }

    @Override
    public ByteBuffer[] nioBuffers() {
        AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
        return super.nioBuffers();
    }

    @Override
    public ByteBuffer[] nioBuffers(int index, int length) {
        AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
        return super.nioBuffers(index, length);
    }

    @Override
    public ByteBuffer internalNioBuffer(int index, int length) {
        AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
        return super.internalNioBuffer(index, length);
    }

    @Override
    public String toString(Charset charset) {
        AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
        return super.toString(charset);
    }

    @Override
    public String toString(int index, int length, Charset charset) {
        AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
        return super.toString(index, length, charset);
    }

    @Override
    public ByteBuf capacity(int newCapacity) {
        AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
        return super.capacity(newCapacity);
    }

    @Override
    public short getShortLE(int index) {
        AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
        return super.getShortLE(index);
    }

    @Override
    public int getUnsignedShortLE(int index) {
        AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
        return super.getUnsignedShortLE(index);
    }

    @Override
    public int getMediumLE(int index) {
        AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
        return super.getMediumLE(index);
    }

    @Override
    public int getUnsignedMediumLE(int index) {
        AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
        return super.getUnsignedMediumLE(index);
    }

    @Override
    public int getIntLE(int index) {
        AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
        return super.getIntLE(index);
    }

    @Override
    public long getUnsignedIntLE(int index) {
        AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
        return super.getUnsignedIntLE(index);
    }

    @Override
    public long getLongLE(int index) {
        AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
        return super.getLongLE(index);
    }

    @Override
    public ByteBuf setShortLE(int index, int value) {
        AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
        return super.setShortLE(index, value);
    }

    @Override
    public ByteBuf setIntLE(int index, int value) {
        AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
        return super.setIntLE(index, value);
    }

    @Override
    public ByteBuf setMediumLE(int index, int value) {
        AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
        return super.setMediumLE(index, value);
    }

    @Override
    public ByteBuf setLongLE(int index, long value) {
        AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
        return super.setLongLE(index, value);
    }

    @Override
    public short readShortLE() {
        AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
        return super.readShortLE();
    }

    @Override
    public int readUnsignedShortLE() {
        AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
        return super.readUnsignedShortLE();
    }

    @Override
    public int readMediumLE() {
        AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
        return super.readMediumLE();
    }

    @Override
    public int readUnsignedMediumLE() {
        AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
        return super.readUnsignedMediumLE();
    }

    @Override
    public int readIntLE() {
        AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
        return super.readIntLE();
    }

    @Override
    public long readUnsignedIntLE() {
        AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
        return super.readUnsignedIntLE();
    }

    @Override
    public long readLongLE() {
        AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
        return super.readLongLE();
    }

    @Override
    public ByteBuf writeShortLE(int value) {
        AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
        return super.writeShortLE(value);
    }

    @Override
    public ByteBuf writeMediumLE(int value) {
        AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
        return super.writeMediumLE(value);
    }

    @Override
    public ByteBuf writeIntLE(int value) {
        AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
        return super.writeIntLE(value);
    }

    @Override
    public ByteBuf writeLongLE(long value) {
        AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
        return super.writeLongLE(value);
    }

    @Override
    public int writeCharSequence(CharSequence sequence, Charset charset) {
        AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
        return super.writeCharSequence(sequence, charset);
    }

    @Override
    public int getBytes(int index, FileChannel out, long position, int length) throws IOException {
        AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
        return super.getBytes(index, out, position, length);
    }

    @Override
    public int setBytes(int index, FileChannel in, long position, int length) throws IOException {
        AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
        return super.setBytes(index, in, position, length);
    }

    @Override
    public int readBytes(FileChannel out, long position, int length) throws IOException {
        AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
        return super.readBytes(out, position, length);
    }

    @Override
    public int writeBytes(FileChannel in, long position, int length) throws IOException {
        AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
        return super.writeBytes(in, position, length);
    }

    @Override
    public ByteBuf asReadOnly() {
        AdvancedLeakAwareByteBuf.recordLeakNonRefCountingOperation(this.leak);
        return super.asReadOnly();
    }

    @Override
    public ByteBuf retain() {
        this.leak.record();
        return super.retain();
    }

    @Override
    public ByteBuf retain(int increment) {
        this.leak.record();
        return super.retain(increment);
    }

    @Override
    public boolean release() {
        this.leak.record();
        return super.release();
    }

    @Override
    public boolean release(int decrement) {
        this.leak.record();
        return super.release(decrement);
    }

    @Override
    public ByteBuf touch() {
        this.leak.record();
        return this;
    }

    @Override
    public ByteBuf touch(Object hint) {
        this.leak.record(hint);
        return this;
    }

    @Override
    protected AdvancedLeakAwareByteBuf newLeakAwareByteBuf(ByteBuf buf, ByteBuf trackedByteBuf, ResourceLeakTracker<ByteBuf> leakTracker) {
        return new AdvancedLeakAwareByteBuf(buf, trackedByteBuf, leakTracker);
    }

    static {
        logger = InternalLoggerFactory.getInstance(AdvancedLeakAwareByteBuf.class);
        ACQUIRE_AND_RELEASE_ONLY = SystemPropertyUtil.getBoolean(PROP_ACQUIRE_AND_RELEASE_ONLY, false);
        if (logger.isDebugEnabled()) {
            logger.debug("-D{}: {}", (Object)PROP_ACQUIRE_AND_RELEASE_ONLY, (Object)ACQUIRE_AND_RELEASE_ONLY);
        }
        ResourceLeakDetector.addExclusions(AdvancedLeakAwareByteBuf.class, "touch", "recordLeakNonRefCountingOperation");
    }
}

