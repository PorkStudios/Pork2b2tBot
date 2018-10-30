/*
 * Decompiled with CFR 0_132.
 */
package io.netty.buffer;

import io.netty.buffer.AbstractDerivedByteBuf;
import io.netty.buffer.AbstractPooledDerivedByteBuf;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.util.ByteProcessor;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.nio.channels.GatheringByteChannel;
import java.nio.channels.ScatteringByteChannel;

@Deprecated
public class DuplicatedByteBuf
extends AbstractDerivedByteBuf {
    private final ByteBuf buffer;

    public DuplicatedByteBuf(ByteBuf buffer) {
        this(buffer, buffer.readerIndex(), buffer.writerIndex());
    }

    DuplicatedByteBuf(ByteBuf buffer, int readerIndex, int writerIndex) {
        super(buffer.maxCapacity());
        this.buffer = buffer instanceof DuplicatedByteBuf ? ((DuplicatedByteBuf)buffer).buffer : (buffer instanceof AbstractPooledDerivedByteBuf ? buffer.unwrap() : buffer);
        this.setIndex(readerIndex, writerIndex);
        this.markReaderIndex();
        this.markWriterIndex();
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
    public int capacity() {
        return this.unwrap().capacity();
    }

    @Override
    public ByteBuf capacity(int newCapacity) {
        this.unwrap().capacity(newCapacity);
        return this;
    }

    @Override
    public boolean hasArray() {
        return this.unwrap().hasArray();
    }

    @Override
    public byte[] array() {
        return this.unwrap().array();
    }

    @Override
    public int arrayOffset() {
        return this.unwrap().arrayOffset();
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
    public ByteBuf copy(int index, int length) {
        return this.unwrap().copy(index, length);
    }

    @Override
    public ByteBuf slice(int index, int length) {
        return this.unwrap().slice(index, length);
    }

    @Override
    public ByteBuf getBytes(int index, ByteBuf dst, int dstIndex, int length) {
        this.unwrap().getBytes(index, dst, dstIndex, length);
        return this;
    }

    @Override
    public ByteBuf getBytes(int index, byte[] dst, int dstIndex, int length) {
        this.unwrap().getBytes(index, dst, dstIndex, length);
        return this;
    }

    @Override
    public ByteBuf getBytes(int index, ByteBuffer dst) {
        this.unwrap().getBytes(index, dst);
        return this;
    }

    @Override
    public ByteBuf setByte(int index, int value) {
        this.unwrap().setByte(index, value);
        return this;
    }

    @Override
    protected void _setByte(int index, int value) {
        this.unwrap().setByte(index, value);
    }

    @Override
    public ByteBuf setShort(int index, int value) {
        this.unwrap().setShort(index, value);
        return this;
    }

    @Override
    protected void _setShort(int index, int value) {
        this.unwrap().setShort(index, value);
    }

    @Override
    public ByteBuf setShortLE(int index, int value) {
        this.unwrap().setShortLE(index, value);
        return this;
    }

    @Override
    protected void _setShortLE(int index, int value) {
        this.unwrap().setShortLE(index, value);
    }

    @Override
    public ByteBuf setMedium(int index, int value) {
        this.unwrap().setMedium(index, value);
        return this;
    }

    @Override
    protected void _setMedium(int index, int value) {
        this.unwrap().setMedium(index, value);
    }

    @Override
    public ByteBuf setMediumLE(int index, int value) {
        this.unwrap().setMediumLE(index, value);
        return this;
    }

    @Override
    protected void _setMediumLE(int index, int value) {
        this.unwrap().setMediumLE(index, value);
    }

    @Override
    public ByteBuf setInt(int index, int value) {
        this.unwrap().setInt(index, value);
        return this;
    }

    @Override
    protected void _setInt(int index, int value) {
        this.unwrap().setInt(index, value);
    }

    @Override
    public ByteBuf setIntLE(int index, int value) {
        this.unwrap().setIntLE(index, value);
        return this;
    }

    @Override
    protected void _setIntLE(int index, int value) {
        this.unwrap().setIntLE(index, value);
    }

    @Override
    public ByteBuf setLong(int index, long value) {
        this.unwrap().setLong(index, value);
        return this;
    }

    @Override
    protected void _setLong(int index, long value) {
        this.unwrap().setLong(index, value);
    }

    @Override
    public ByteBuf setLongLE(int index, long value) {
        this.unwrap().setLongLE(index, value);
        return this;
    }

    @Override
    protected void _setLongLE(int index, long value) {
        this.unwrap().setLongLE(index, value);
    }

    @Override
    public ByteBuf setBytes(int index, byte[] src, int srcIndex, int length) {
        this.unwrap().setBytes(index, src, srcIndex, length);
        return this;
    }

    @Override
    public ByteBuf setBytes(int index, ByteBuf src, int srcIndex, int length) {
        this.unwrap().setBytes(index, src, srcIndex, length);
        return this;
    }

    @Override
    public ByteBuf setBytes(int index, ByteBuffer src) {
        this.unwrap().setBytes(index, src);
        return this;
    }

    @Override
    public ByteBuf getBytes(int index, OutputStream out, int length) throws IOException {
        this.unwrap().getBytes(index, out, length);
        return this;
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
    public int setBytes(int index, InputStream in, int length) throws IOException {
        return this.unwrap().setBytes(index, in, length);
    }

    @Override
    public int setBytes(int index, ScatteringByteChannel in, int length) throws IOException {
        return this.unwrap().setBytes(index, in, length);
    }

    @Override
    public int setBytes(int index, FileChannel in, long position, int length) throws IOException {
        return this.unwrap().setBytes(index, in, position, length);
    }

    @Override
    public int nioBufferCount() {
        return this.unwrap().nioBufferCount();
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
}

