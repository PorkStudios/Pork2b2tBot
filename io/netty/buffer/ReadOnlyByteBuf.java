/*
 * Decompiled with CFR 0_132.
 */
package io.netty.buffer;

import io.netty.buffer.AbstractDerivedByteBuf;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.DuplicatedByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.util.ByteProcessor;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ReadOnlyBufferException;
import java.nio.channels.FileChannel;
import java.nio.channels.GatheringByteChannel;
import java.nio.channels.ScatteringByteChannel;

@Deprecated
public class ReadOnlyByteBuf
extends AbstractDerivedByteBuf {
    private final ByteBuf buffer;

    public ReadOnlyByteBuf(ByteBuf buffer) {
        super(buffer.maxCapacity());
        this.buffer = buffer instanceof ReadOnlyByteBuf || buffer instanceof DuplicatedByteBuf ? buffer.unwrap() : buffer;
        this.setIndex(buffer.readerIndex(), buffer.writerIndex());
    }

    @Override
    public boolean isReadOnly() {
        return true;
    }

    @Override
    public boolean isWritable() {
        return false;
    }

    @Override
    public boolean isWritable(int numBytes) {
        return false;
    }

    @Override
    public int ensureWritable(int minWritableBytes, boolean force) {
        return 1;
    }

    @Override
    public ByteBuf ensureWritable(int minWritableBytes) {
        throw new ReadOnlyBufferException();
    }

    @Override
    public ByteBuf unwrap() {
        return this.buffer;
    }

    @Override
    public ByteBufAllocator alloc() {
        return this.unwrap().alloc();
    }

    @Deprecated
    @Override
    public ByteOrder order() {
        return this.unwrap().order();
    }

    @Override
    public boolean isDirect() {
        return this.unwrap().isDirect();
    }

    @Override
    public boolean hasArray() {
        return false;
    }

    @Override
    public byte[] array() {
        throw new ReadOnlyBufferException();
    }

    @Override
    public int arrayOffset() {
        throw new ReadOnlyBufferException();
    }

    @Override
    public boolean hasMemoryAddress() {
        return this.unwrap().hasMemoryAddress();
    }

    @Override
    public long memoryAddress() {
        return this.unwrap().memoryAddress();
    }

    @Override
    public ByteBuf discardReadBytes() {
        throw new ReadOnlyBufferException();
    }

    @Override
    public ByteBuf setBytes(int index, ByteBuf src, int srcIndex, int length) {
        throw new ReadOnlyBufferException();
    }

    @Override
    public ByteBuf setBytes(int index, byte[] src, int srcIndex, int length) {
        throw new ReadOnlyBufferException();
    }

    @Override
    public ByteBuf setBytes(int index, ByteBuffer src) {
        throw new ReadOnlyBufferException();
    }

    @Override
    public ByteBuf setByte(int index, int value) {
        throw new ReadOnlyBufferException();
    }

    @Override
    protected void _setByte(int index, int value) {
        throw new ReadOnlyBufferException();
    }

    @Override
    public ByteBuf setShort(int index, int value) {
        throw new ReadOnlyBufferException();
    }

    @Override
    protected void _setShort(int index, int value) {
        throw new ReadOnlyBufferException();
    }

    @Override
    public ByteBuf setShortLE(int index, int value) {
        throw new ReadOnlyBufferException();
    }

    @Override
    protected void _setShortLE(int index, int value) {
        throw new ReadOnlyBufferException();
    }

    @Override
    public ByteBuf setMedium(int index, int value) {
        throw new ReadOnlyBufferException();
    }

    @Override
    protected void _setMedium(int index, int value) {
        throw new ReadOnlyBufferException();
    }

    @Override
    public ByteBuf setMediumLE(int index, int value) {
        throw new ReadOnlyBufferException();
    }

    @Override
    protected void _setMediumLE(int index, int value) {
        throw new ReadOnlyBufferException();
    }

    @Override
    public ByteBuf setInt(int index, int value) {
        throw new ReadOnlyBufferException();
    }

    @Override
    protected void _setInt(int index, int value) {
        throw new ReadOnlyBufferException();
    }

    @Override
    public ByteBuf setIntLE(int index, int value) {
        throw new ReadOnlyBufferException();
    }

    @Override
    protected void _setIntLE(int index, int value) {
        throw new ReadOnlyBufferException();
    }

    @Override
    public ByteBuf setLong(int index, long value) {
        throw new ReadOnlyBufferException();
    }

    @Override
    protected void _setLong(int index, long value) {
        throw new ReadOnlyBufferException();
    }

    @Override
    public ByteBuf setLongLE(int index, long value) {
        throw new ReadOnlyBufferException();
    }

    @Override
    protected void _setLongLE(int index, long value) {
        throw new ReadOnlyBufferException();
    }

    @Override
    public int setBytes(int index, InputStream in, int length) {
        throw new ReadOnlyBufferException();
    }

    @Override
    public int setBytes(int index, ScatteringByteChannel in, int length) {
        throw new ReadOnlyBufferException();
    }

    @Override
    public int setBytes(int index, FileChannel in, long position, int length) {
        throw new ReadOnlyBufferException();
    }

    @Override
    public int getBytes(int index, GatheringByteChannel out, int length) throws IOException {
        return this.unwrap().getBytes(index, out, length);
    }

    @Override
    public int getBytes(int index, FileChannel out, long position, int length) throws IOException {
        return this.unwrap().getBytes(index, out, position, length);
    }

    @Override
    public ByteBuf getBytes(int index, OutputStream out, int length) throws IOException {
        this.unwrap().getBytes(index, out, length);
        return this;
    }

    @Override
    public ByteBuf getBytes(int index, byte[] dst, int dstIndex, int length) {
        this.unwrap().getBytes(index, dst, dstIndex, length);
        return this;
    }

    @Override
    public ByteBuf getBytes(int index, ByteBuf dst, int dstIndex, int length) {
        this.unwrap().getBytes(index, dst, dstIndex, length);
        return this;
    }

    @Override
    public ByteBuf getBytes(int index, ByteBuffer dst) {
        this.unwrap().getBytes(index, dst);
        return this;
    }

    @Override
    public ByteBuf duplicate() {
        return new ReadOnlyByteBuf(this);
    }

    @Override
    public ByteBuf copy(int index, int length) {
        return this.unwrap().copy(index, length);
    }

    @Override
    public ByteBuf slice(int index, int length) {
        return Unpooled.unmodifiableBuffer(this.unwrap().slice(index, length));
    }

    @Override
    public byte getByte(int index) {
        return this.unwrap().getByte(index);
    }

    @Override
    protected byte _getByte(int index) {
        return this.unwrap().getByte(index);
    }

    @Override
    public short getShort(int index) {
        return this.unwrap().getShort(index);
    }

    @Override
    protected short _getShort(int index) {
        return this.unwrap().getShort(index);
    }

    @Override
    public short getShortLE(int index) {
        return this.unwrap().getShortLE(index);
    }

    @Override
    protected short _getShortLE(int index) {
        return this.unwrap().getShortLE(index);
    }

    @Override
    public int getUnsignedMedium(int index) {
        return this.unwrap().getUnsignedMedium(index);
    }

    @Override
    protected int _getUnsignedMedium(int index) {
        return this.unwrap().getUnsignedMedium(index);
    }

    @Override
    public int getUnsignedMediumLE(int index) {
        return this.unwrap().getUnsignedMediumLE(index);
    }

    @Override
    protected int _getUnsignedMediumLE(int index) {
        return this.unwrap().getUnsignedMediumLE(index);
    }

    @Override
    public int getInt(int index) {
        return this.unwrap().getInt(index);
    }

    @Override
    protected int _getInt(int index) {
        return this.unwrap().getInt(index);
    }

    @Override
    public int getIntLE(int index) {
        return this.unwrap().getIntLE(index);
    }

    @Override
    protected int _getIntLE(int index) {
        return this.unwrap().getIntLE(index);
    }

    @Override
    public long getLong(int index) {
        return this.unwrap().getLong(index);
    }

    @Override
    protected long _getLong(int index) {
        return this.unwrap().getLong(index);
    }

    @Override
    public long getLongLE(int index) {
        return this.unwrap().getLongLE(index);
    }

    @Override
    protected long _getLongLE(int index) {
        return this.unwrap().getLongLE(index);
    }

    @Override
    public int nioBufferCount() {
        return this.unwrap().nioBufferCount();
    }

    @Override
    public ByteBuffer nioBuffer(int index, int length) {
        return this.unwrap().nioBuffer(index, length).asReadOnlyBuffer();
    }

    @Override
    public ByteBuffer[] nioBuffers(int index, int length) {
        return this.unwrap().nioBuffers(index, length);
    }

    @Override
    public int forEachByte(int index, int length, ByteProcessor processor) {
        return this.unwrap().forEachByte(index, length, processor);
    }

    @Override
    public int forEachByteDesc(int index, int length, ByteProcessor processor) {
        return this.unwrap().forEachByteDesc(index, length, processor);
    }

    @Override
    public int capacity() {
        return this.unwrap().capacity();
    }

    @Override
    public ByteBuf capacity(int newCapacity) {
        throw new ReadOnlyBufferException();
    }

    @Override
    public ByteBuf asReadOnly() {
        return this;
    }
}

