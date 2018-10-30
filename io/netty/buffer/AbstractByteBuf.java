/*
 * Decompiled with CFR 0_132.
 */
package io.netty.buffer;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.SwappedByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.buffer.UnpooledDuplicatedByteBuf;
import io.netty.buffer.UnpooledSlicedByteBuf;
import io.netty.util.ByteProcessor;
import io.netty.util.CharsetUtil;
import io.netty.util.IllegalReferenceCountException;
import io.netty.util.ResourceLeakDetector;
import io.netty.util.ResourceLeakDetectorFactory;
import io.netty.util.internal.MathUtil;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.StringUtil;
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

public abstract class AbstractByteBuf
extends ByteBuf {
    private static final InternalLogger logger = InternalLoggerFactory.getInstance(AbstractByteBuf.class);
    private static final String PROP_MODE = "io.netty.buffer.bytebuf.checkAccessible";
    private static final boolean checkAccessible = SystemPropertyUtil.getBoolean("io.netty.buffer.bytebuf.checkAccessible", true);
    static final ResourceLeakDetector<ByteBuf> leakDetector;
    int readerIndex;
    int writerIndex;
    private int markedReaderIndex;
    private int markedWriterIndex;
    private int maxCapacity;

    protected AbstractByteBuf(int maxCapacity) {
        if (maxCapacity < 0) {
            throw new IllegalArgumentException("maxCapacity: " + maxCapacity + " (expected: >= 0)");
        }
        this.maxCapacity = maxCapacity;
    }

    @Override
    public boolean isReadOnly() {
        return false;
    }

    @Override
    public ByteBuf asReadOnly() {
        if (this.isReadOnly()) {
            return this;
        }
        return Unpooled.unmodifiableBuffer((ByteBuf)this);
    }

    @Override
    public int maxCapacity() {
        return this.maxCapacity;
    }

    protected final void maxCapacity(int maxCapacity) {
        this.maxCapacity = maxCapacity;
    }

    @Override
    public int readerIndex() {
        return this.readerIndex;
    }

    @Override
    public ByteBuf readerIndex(int readerIndex) {
        if (readerIndex < 0 || readerIndex > this.writerIndex) {
            throw new IndexOutOfBoundsException(String.format("readerIndex: %d (expected: 0 <= readerIndex <= writerIndex(%d))", readerIndex, this.writerIndex));
        }
        this.readerIndex = readerIndex;
        return this;
    }

    @Override
    public int writerIndex() {
        return this.writerIndex;
    }

    @Override
    public ByteBuf writerIndex(int writerIndex) {
        if (writerIndex < this.readerIndex || writerIndex > this.capacity()) {
            throw new IndexOutOfBoundsException(String.format("writerIndex: %d (expected: readerIndex(%d) <= writerIndex <= capacity(%d))", writerIndex, this.readerIndex, this.capacity()));
        }
        this.writerIndex = writerIndex;
        return this;
    }

    @Override
    public ByteBuf setIndex(int readerIndex, int writerIndex) {
        if (readerIndex < 0 || readerIndex > writerIndex || writerIndex > this.capacity()) {
            throw new IndexOutOfBoundsException(String.format("readerIndex: %d, writerIndex: %d (expected: 0 <= readerIndex <= writerIndex <= capacity(%d))", readerIndex, writerIndex, this.capacity()));
        }
        this.setIndex0(readerIndex, writerIndex);
        return this;
    }

    @Override
    public ByteBuf clear() {
        this.writerIndex = 0;
        this.readerIndex = 0;
        return this;
    }

    @Override
    public boolean isReadable() {
        return this.writerIndex > this.readerIndex;
    }

    @Override
    public boolean isReadable(int numBytes) {
        return this.writerIndex - this.readerIndex >= numBytes;
    }

    @Override
    public boolean isWritable() {
        return this.capacity() > this.writerIndex;
    }

    @Override
    public boolean isWritable(int numBytes) {
        return this.capacity() - this.writerIndex >= numBytes;
    }

    @Override
    public int readableBytes() {
        return this.writerIndex - this.readerIndex;
    }

    @Override
    public int writableBytes() {
        return this.capacity() - this.writerIndex;
    }

    @Override
    public int maxWritableBytes() {
        return this.maxCapacity() - this.writerIndex;
    }

    @Override
    public ByteBuf markReaderIndex() {
        this.markedReaderIndex = this.readerIndex;
        return this;
    }

    @Override
    public ByteBuf resetReaderIndex() {
        this.readerIndex(this.markedReaderIndex);
        return this;
    }

    @Override
    public ByteBuf markWriterIndex() {
        this.markedWriterIndex = this.writerIndex;
        return this;
    }

    @Override
    public ByteBuf resetWriterIndex() {
        this.writerIndex = this.markedWriterIndex;
        return this;
    }

    @Override
    public ByteBuf discardReadBytes() {
        this.ensureAccessible();
        if (this.readerIndex == 0) {
            return this;
        }
        if (this.readerIndex != this.writerIndex) {
            this.setBytes(0, this, this.readerIndex, this.writerIndex - this.readerIndex);
            this.writerIndex -= this.readerIndex;
            this.adjustMarkers(this.readerIndex);
            this.readerIndex = 0;
        } else {
            this.adjustMarkers(this.readerIndex);
            this.readerIndex = 0;
            this.writerIndex = 0;
        }
        return this;
    }

    @Override
    public ByteBuf discardSomeReadBytes() {
        this.ensureAccessible();
        if (this.readerIndex == 0) {
            return this;
        }
        if (this.readerIndex == this.writerIndex) {
            this.adjustMarkers(this.readerIndex);
            this.readerIndex = 0;
            this.writerIndex = 0;
            return this;
        }
        if (this.readerIndex >= this.capacity() >>> 1) {
            this.setBytes(0, this, this.readerIndex, this.writerIndex - this.readerIndex);
            this.writerIndex -= this.readerIndex;
            this.adjustMarkers(this.readerIndex);
            this.readerIndex = 0;
        }
        return this;
    }

    protected final void adjustMarkers(int decrement) {
        int markedReaderIndex = this.markedReaderIndex;
        if (markedReaderIndex <= decrement) {
            this.markedReaderIndex = 0;
            int markedWriterIndex = this.markedWriterIndex;
            this.markedWriterIndex = markedWriterIndex <= decrement ? 0 : markedWriterIndex - decrement;
        } else {
            this.markedReaderIndex = markedReaderIndex - decrement;
            this.markedWriterIndex -= decrement;
        }
    }

    @Override
    public ByteBuf ensureWritable(int minWritableBytes) {
        if (minWritableBytes < 0) {
            throw new IllegalArgumentException(String.format("minWritableBytes: %d (expected: >= 0)", minWritableBytes));
        }
        this.ensureWritable0(minWritableBytes);
        return this;
    }

    final void ensureWritable0(int minWritableBytes) {
        this.ensureAccessible();
        if (minWritableBytes <= this.writableBytes()) {
            return;
        }
        if (minWritableBytes > this.maxCapacity - this.writerIndex) {
            throw new IndexOutOfBoundsException(String.format("writerIndex(%d) + minWritableBytes(%d) exceeds maxCapacity(%d): %s", this.writerIndex, minWritableBytes, this.maxCapacity, this));
        }
        int newCapacity = this.alloc().calculateNewCapacity(this.writerIndex + minWritableBytes, this.maxCapacity);
        this.capacity(newCapacity);
    }

    @Override
    public int ensureWritable(int minWritableBytes, boolean force) {
        int writerIndex;
        this.ensureAccessible();
        if (minWritableBytes < 0) {
            throw new IllegalArgumentException(String.format("minWritableBytes: %d (expected: >= 0)", minWritableBytes));
        }
        if (minWritableBytes <= this.writableBytes()) {
            return 0;
        }
        int maxCapacity = this.maxCapacity();
        if (minWritableBytes > maxCapacity - (writerIndex = this.writerIndex())) {
            if (!force || this.capacity() == maxCapacity) {
                return 1;
            }
            this.capacity(maxCapacity);
            return 3;
        }
        int newCapacity = this.alloc().calculateNewCapacity(writerIndex + minWritableBytes, maxCapacity);
        this.capacity(newCapacity);
        return 2;
    }

    @Override
    public ByteBuf order(ByteOrder endianness) {
        if (endianness == null) {
            throw new NullPointerException("endianness");
        }
        if (endianness == this.order()) {
            return this;
        }
        return this.newSwappedByteBuf();
    }

    protected SwappedByteBuf newSwappedByteBuf() {
        return new SwappedByteBuf(this);
    }

    @Override
    public byte getByte(int index) {
        this.checkIndex(index);
        return this._getByte(index);
    }

    protected abstract byte _getByte(int var1);

    @Override
    public boolean getBoolean(int index) {
        return this.getByte(index) != 0;
    }

    @Override
    public short getUnsignedByte(int index) {
        return (short)(this.getByte(index) & 255);
    }

    @Override
    public short getShort(int index) {
        this.checkIndex(index, 2);
        return this._getShort(index);
    }

    protected abstract short _getShort(int var1);

    @Override
    public short getShortLE(int index) {
        this.checkIndex(index, 2);
        return this._getShortLE(index);
    }

    protected abstract short _getShortLE(int var1);

    @Override
    public int getUnsignedShort(int index) {
        return this.getShort(index) & 65535;
    }

    @Override
    public int getUnsignedShortLE(int index) {
        return this.getShortLE(index) & 65535;
    }

    @Override
    public int getUnsignedMedium(int index) {
        this.checkIndex(index, 3);
        return this._getUnsignedMedium(index);
    }

    protected abstract int _getUnsignedMedium(int var1);

    @Override
    public int getUnsignedMediumLE(int index) {
        this.checkIndex(index, 3);
        return this._getUnsignedMediumLE(index);
    }

    protected abstract int _getUnsignedMediumLE(int var1);

    @Override
    public int getMedium(int index) {
        int value = this.getUnsignedMedium(index);
        if ((value & 8388608) != 0) {
            value |= -16777216;
        }
        return value;
    }

    @Override
    public int getMediumLE(int index) {
        int value = this.getUnsignedMediumLE(index);
        if ((value & 8388608) != 0) {
            value |= -16777216;
        }
        return value;
    }

    @Override
    public int getInt(int index) {
        this.checkIndex(index, 4);
        return this._getInt(index);
    }

    protected abstract int _getInt(int var1);

    @Override
    public int getIntLE(int index) {
        this.checkIndex(index, 4);
        return this._getIntLE(index);
    }

    protected abstract int _getIntLE(int var1);

    @Override
    public long getUnsignedInt(int index) {
        return (long)this.getInt(index) & 0xFFFFFFFFL;
    }

    @Override
    public long getUnsignedIntLE(int index) {
        return (long)this.getIntLE(index) & 0xFFFFFFFFL;
    }

    @Override
    public long getLong(int index) {
        this.checkIndex(index, 8);
        return this._getLong(index);
    }

    protected abstract long _getLong(int var1);

    @Override
    public long getLongLE(int index) {
        this.checkIndex(index, 8);
        return this._getLongLE(index);
    }

    protected abstract long _getLongLE(int var1);

    @Override
    public char getChar(int index) {
        return (char)this.getShort(index);
    }

    @Override
    public float getFloat(int index) {
        return Float.intBitsToFloat(this.getInt(index));
    }

    @Override
    public double getDouble(int index) {
        return Double.longBitsToDouble(this.getLong(index));
    }

    @Override
    public ByteBuf getBytes(int index, byte[] dst) {
        this.getBytes(index, dst, 0, dst.length);
        return this;
    }

    @Override
    public ByteBuf getBytes(int index, ByteBuf dst) {
        this.getBytes(index, dst, dst.writableBytes());
        return this;
    }

    @Override
    public ByteBuf getBytes(int index, ByteBuf dst, int length) {
        this.getBytes(index, dst, dst.writerIndex(), length);
        dst.writerIndex(dst.writerIndex() + length);
        return this;
    }

    @Override
    public CharSequence getCharSequence(int index, int length, Charset charset) {
        return this.toString(index, length, charset);
    }

    @Override
    public CharSequence readCharSequence(int length, Charset charset) {
        CharSequence sequence = this.getCharSequence(this.readerIndex, length, charset);
        this.readerIndex += length;
        return sequence;
    }

    @Override
    public ByteBuf setByte(int index, int value) {
        this.checkIndex(index);
        this._setByte(index, value);
        return this;
    }

    protected abstract void _setByte(int var1, int var2);

    @Override
    public ByteBuf setBoolean(int index, boolean value) {
        this.setByte(index, value ? 1 : 0);
        return this;
    }

    @Override
    public ByteBuf setShort(int index, int value) {
        this.checkIndex(index, 2);
        this._setShort(index, value);
        return this;
    }

    protected abstract void _setShort(int var1, int var2);

    @Override
    public ByteBuf setShortLE(int index, int value) {
        this.checkIndex(index, 2);
        this._setShortLE(index, value);
        return this;
    }

    protected abstract void _setShortLE(int var1, int var2);

    @Override
    public ByteBuf setChar(int index, int value) {
        this.setShort(index, value);
        return this;
    }

    @Override
    public ByteBuf setMedium(int index, int value) {
        this.checkIndex(index, 3);
        this._setMedium(index, value);
        return this;
    }

    protected abstract void _setMedium(int var1, int var2);

    @Override
    public ByteBuf setMediumLE(int index, int value) {
        this.checkIndex(index, 3);
        this._setMediumLE(index, value);
        return this;
    }

    protected abstract void _setMediumLE(int var1, int var2);

    @Override
    public ByteBuf setInt(int index, int value) {
        this.checkIndex(index, 4);
        this._setInt(index, value);
        return this;
    }

    protected abstract void _setInt(int var1, int var2);

    @Override
    public ByteBuf setIntLE(int index, int value) {
        this.checkIndex(index, 4);
        this._setIntLE(index, value);
        return this;
    }

    protected abstract void _setIntLE(int var1, int var2);

    @Override
    public ByteBuf setFloat(int index, float value) {
        this.setInt(index, Float.floatToRawIntBits(value));
        return this;
    }

    @Override
    public ByteBuf setLong(int index, long value) {
        this.checkIndex(index, 8);
        this._setLong(index, value);
        return this;
    }

    protected abstract void _setLong(int var1, long var2);

    @Override
    public ByteBuf setLongLE(int index, long value) {
        this.checkIndex(index, 8);
        this._setLongLE(index, value);
        return this;
    }

    protected abstract void _setLongLE(int var1, long var2);

    @Override
    public ByteBuf setDouble(int index, double value) {
        this.setLong(index, Double.doubleToRawLongBits(value));
        return this;
    }

    @Override
    public ByteBuf setBytes(int index, byte[] src) {
        this.setBytes(index, src, 0, src.length);
        return this;
    }

    @Override
    public ByteBuf setBytes(int index, ByteBuf src) {
        this.setBytes(index, src, src.readableBytes());
        return this;
    }

    @Override
    public ByteBuf setBytes(int index, ByteBuf src, int length) {
        this.checkIndex(index, length);
        if (src == null) {
            throw new NullPointerException("src");
        }
        if (length > src.readableBytes()) {
            throw new IndexOutOfBoundsException(String.format("length(%d) exceeds src.readableBytes(%d) where src is: %s", length, src.readableBytes(), src));
        }
        this.setBytes(index, src, src.readerIndex(), length);
        src.readerIndex(src.readerIndex() + length);
        return this;
    }

    @Override
    public ByteBuf setZero(int index, int length) {
        int i;
        if (length == 0) {
            return this;
        }
        this.checkIndex(index, length);
        int nLong = length >>> 3;
        int nBytes = length & 7;
        for (i = nLong; i > 0; --i) {
            this._setLong(index, 0L);
            index += 8;
        }
        if (nBytes == 4) {
            this._setInt(index, 0);
        } else if (nBytes < 4) {
            for (i = nBytes; i > 0; --i) {
                this._setByte(index, 0);
                ++index;
            }
        } else {
            this._setInt(index, 0);
            index += 4;
            for (i = nBytes - 4; i > 0; --i) {
                this._setByte(index, 0);
                ++index;
            }
        }
        return this;
    }

    @Override
    public int setCharSequence(int index, CharSequence sequence, Charset charset) {
        return this.setCharSequence0(index, sequence, charset, false);
    }

    private int setCharSequence0(int index, CharSequence sequence, Charset charset, boolean expand) {
        if (charset.equals(CharsetUtil.UTF_8)) {
            int length = ByteBufUtil.utf8MaxBytes(sequence);
            if (expand) {
                this.ensureWritable0(length);
                this.checkIndex0(index, length);
            } else {
                this.checkIndex(index, length);
            }
            return ByteBufUtil.writeUtf8(this, index, sequence, sequence.length());
        }
        if (charset.equals(CharsetUtil.US_ASCII) || charset.equals(CharsetUtil.ISO_8859_1)) {
            int length = sequence.length();
            if (expand) {
                this.ensureWritable0(length);
                this.checkIndex0(index, length);
            } else {
                this.checkIndex(index, length);
            }
            return ByteBufUtil.writeAscii(this, index, sequence, length);
        }
        byte[] bytes = sequence.toString().getBytes(charset);
        if (expand) {
            this.ensureWritable0(bytes.length);
        }
        this.setBytes(index, bytes);
        return bytes.length;
    }

    @Override
    public byte readByte() {
        this.checkReadableBytes0(1);
        int i = this.readerIndex;
        byte b = this._getByte(i);
        this.readerIndex = i + 1;
        return b;
    }

    @Override
    public boolean readBoolean() {
        return this.readByte() != 0;
    }

    @Override
    public short readUnsignedByte() {
        return (short)(this.readByte() & 255);
    }

    @Override
    public short readShort() {
        this.checkReadableBytes0(2);
        short v = this._getShort(this.readerIndex);
        this.readerIndex += 2;
        return v;
    }

    @Override
    public short readShortLE() {
        this.checkReadableBytes0(2);
        short v = this._getShortLE(this.readerIndex);
        this.readerIndex += 2;
        return v;
    }

    @Override
    public int readUnsignedShort() {
        return this.readShort() & 65535;
    }

    @Override
    public int readUnsignedShortLE() {
        return this.readShortLE() & 65535;
    }

    @Override
    public int readMedium() {
        int value = this.readUnsignedMedium();
        if ((value & 8388608) != 0) {
            value |= -16777216;
        }
        return value;
    }

    @Override
    public int readMediumLE() {
        int value = this.readUnsignedMediumLE();
        if ((value & 8388608) != 0) {
            value |= -16777216;
        }
        return value;
    }

    @Override
    public int readUnsignedMedium() {
        this.checkReadableBytes0(3);
        int v = this._getUnsignedMedium(this.readerIndex);
        this.readerIndex += 3;
        return v;
    }

    @Override
    public int readUnsignedMediumLE() {
        this.checkReadableBytes0(3);
        int v = this._getUnsignedMediumLE(this.readerIndex);
        this.readerIndex += 3;
        return v;
    }

    @Override
    public int readInt() {
        this.checkReadableBytes0(4);
        int v = this._getInt(this.readerIndex);
        this.readerIndex += 4;
        return v;
    }

    @Override
    public int readIntLE() {
        this.checkReadableBytes0(4);
        int v = this._getIntLE(this.readerIndex);
        this.readerIndex += 4;
        return v;
    }

    @Override
    public long readUnsignedInt() {
        return (long)this.readInt() & 0xFFFFFFFFL;
    }

    @Override
    public long readUnsignedIntLE() {
        return (long)this.readIntLE() & 0xFFFFFFFFL;
    }

    @Override
    public long readLong() {
        this.checkReadableBytes0(8);
        long v = this._getLong(this.readerIndex);
        this.readerIndex += 8;
        return v;
    }

    @Override
    public long readLongLE() {
        this.checkReadableBytes0(8);
        long v = this._getLongLE(this.readerIndex);
        this.readerIndex += 8;
        return v;
    }

    @Override
    public char readChar() {
        return (char)this.readShort();
    }

    @Override
    public float readFloat() {
        return Float.intBitsToFloat(this.readInt());
    }

    @Override
    public double readDouble() {
        return Double.longBitsToDouble(this.readLong());
    }

    @Override
    public ByteBuf readBytes(int length) {
        this.checkReadableBytes(length);
        if (length == 0) {
            return Unpooled.EMPTY_BUFFER;
        }
        ByteBuf buf = this.alloc().buffer(length, this.maxCapacity);
        buf.writeBytes(this, this.readerIndex, length);
        this.readerIndex += length;
        return buf;
    }

    @Override
    public ByteBuf readSlice(int length) {
        this.checkReadableBytes(length);
        ByteBuf slice = this.slice(this.readerIndex, length);
        this.readerIndex += length;
        return slice;
    }

    @Override
    public ByteBuf readRetainedSlice(int length) {
        this.checkReadableBytes(length);
        ByteBuf slice = this.retainedSlice(this.readerIndex, length);
        this.readerIndex += length;
        return slice;
    }

    @Override
    public ByteBuf readBytes(byte[] dst, int dstIndex, int length) {
        this.checkReadableBytes(length);
        this.getBytes(this.readerIndex, dst, dstIndex, length);
        this.readerIndex += length;
        return this;
    }

    @Override
    public ByteBuf readBytes(byte[] dst) {
        this.readBytes(dst, 0, dst.length);
        return this;
    }

    @Override
    public ByteBuf readBytes(ByteBuf dst) {
        this.readBytes(dst, dst.writableBytes());
        return this;
    }

    @Override
    public ByteBuf readBytes(ByteBuf dst, int length) {
        if (length > dst.writableBytes()) {
            throw new IndexOutOfBoundsException(String.format("length(%d) exceeds dst.writableBytes(%d) where dst is: %s", length, dst.writableBytes(), dst));
        }
        this.readBytes(dst, dst.writerIndex(), length);
        dst.writerIndex(dst.writerIndex() + length);
        return this;
    }

    @Override
    public ByteBuf readBytes(ByteBuf dst, int dstIndex, int length) {
        this.checkReadableBytes(length);
        this.getBytes(this.readerIndex, dst, dstIndex, length);
        this.readerIndex += length;
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
    public int readBytes(GatheringByteChannel out, int length) throws IOException {
        this.checkReadableBytes(length);
        int readBytes = this.getBytes(this.readerIndex, out, length);
        this.readerIndex += readBytes;
        return readBytes;
    }

    @Override
    public int readBytes(FileChannel out, long position, int length) throws IOException {
        this.checkReadableBytes(length);
        int readBytes = this.getBytes(this.readerIndex, out, position, length);
        this.readerIndex += readBytes;
        return readBytes;
    }

    @Override
    public ByteBuf readBytes(OutputStream out, int length) throws IOException {
        this.checkReadableBytes(length);
        this.getBytes(this.readerIndex, out, length);
        this.readerIndex += length;
        return this;
    }

    @Override
    public ByteBuf skipBytes(int length) {
        this.checkReadableBytes(length);
        this.readerIndex += length;
        return this;
    }

    @Override
    public ByteBuf writeBoolean(boolean value) {
        this.writeByte(value ? 1 : 0);
        return this;
    }

    @Override
    public ByteBuf writeByte(int value) {
        this.ensureWritable0(1);
        this._setByte(this.writerIndex++, value);
        return this;
    }

    @Override
    public ByteBuf writeShort(int value) {
        this.ensureWritable0(2);
        this._setShort(this.writerIndex, value);
        this.writerIndex += 2;
        return this;
    }

    @Override
    public ByteBuf writeShortLE(int value) {
        this.ensureWritable0(2);
        this._setShortLE(this.writerIndex, value);
        this.writerIndex += 2;
        return this;
    }

    @Override
    public ByteBuf writeMedium(int value) {
        this.ensureWritable0(3);
        this._setMedium(this.writerIndex, value);
        this.writerIndex += 3;
        return this;
    }

    @Override
    public ByteBuf writeMediumLE(int value) {
        this.ensureWritable0(3);
        this._setMediumLE(this.writerIndex, value);
        this.writerIndex += 3;
        return this;
    }

    @Override
    public ByteBuf writeInt(int value) {
        this.ensureWritable0(4);
        this._setInt(this.writerIndex, value);
        this.writerIndex += 4;
        return this;
    }

    @Override
    public ByteBuf writeIntLE(int value) {
        this.ensureWritable0(4);
        this._setIntLE(this.writerIndex, value);
        this.writerIndex += 4;
        return this;
    }

    @Override
    public ByteBuf writeLong(long value) {
        this.ensureWritable0(8);
        this._setLong(this.writerIndex, value);
        this.writerIndex += 8;
        return this;
    }

    @Override
    public ByteBuf writeLongLE(long value) {
        this.ensureWritable0(8);
        this._setLongLE(this.writerIndex, value);
        this.writerIndex += 8;
        return this;
    }

    @Override
    public ByteBuf writeChar(int value) {
        this.writeShort(value);
        return this;
    }

    @Override
    public ByteBuf writeFloat(float value) {
        this.writeInt(Float.floatToRawIntBits(value));
        return this;
    }

    @Override
    public ByteBuf writeDouble(double value) {
        this.writeLong(Double.doubleToRawLongBits(value));
        return this;
    }

    @Override
    public ByteBuf writeBytes(byte[] src, int srcIndex, int length) {
        this.ensureWritable(length);
        this.setBytes(this.writerIndex, src, srcIndex, length);
        this.writerIndex += length;
        return this;
    }

    @Override
    public ByteBuf writeBytes(byte[] src) {
        this.writeBytes(src, 0, src.length);
        return this;
    }

    @Override
    public ByteBuf writeBytes(ByteBuf src) {
        this.writeBytes(src, src.readableBytes());
        return this;
    }

    @Override
    public ByteBuf writeBytes(ByteBuf src, int length) {
        if (length > src.readableBytes()) {
            throw new IndexOutOfBoundsException(String.format("length(%d) exceeds src.readableBytes(%d) where src is: %s", length, src.readableBytes(), src));
        }
        this.writeBytes(src, src.readerIndex(), length);
        src.readerIndex(src.readerIndex() + length);
        return this;
    }

    @Override
    public ByteBuf writeBytes(ByteBuf src, int srcIndex, int length) {
        this.ensureWritable(length);
        this.setBytes(this.writerIndex, src, srcIndex, length);
        this.writerIndex += length;
        return this;
    }

    @Override
    public ByteBuf writeBytes(ByteBuffer src) {
        int length = src.remaining();
        this.ensureWritable0(length);
        this.setBytes(this.writerIndex, src);
        this.writerIndex += length;
        return this;
    }

    @Override
    public int writeBytes(InputStream in, int length) throws IOException {
        this.ensureWritable(length);
        int writtenBytes = this.setBytes(this.writerIndex, in, length);
        if (writtenBytes > 0) {
            this.writerIndex += writtenBytes;
        }
        return writtenBytes;
    }

    @Override
    public int writeBytes(ScatteringByteChannel in, int length) throws IOException {
        this.ensureWritable(length);
        int writtenBytes = this.setBytes(this.writerIndex, in, length);
        if (writtenBytes > 0) {
            this.writerIndex += writtenBytes;
        }
        return writtenBytes;
    }

    @Override
    public int writeBytes(FileChannel in, long position, int length) throws IOException {
        this.ensureWritable(length);
        int writtenBytes = this.setBytes(this.writerIndex, in, position, length);
        if (writtenBytes > 0) {
            this.writerIndex += writtenBytes;
        }
        return writtenBytes;
    }

    @Override
    public ByteBuf writeZero(int length) {
        int i;
        if (length == 0) {
            return this;
        }
        this.ensureWritable(length);
        int wIndex = this.writerIndex;
        this.checkIndex0(wIndex, length);
        int nLong = length >>> 3;
        int nBytes = length & 7;
        for (i = nLong; i > 0; --i) {
            this._setLong(wIndex, 0L);
            wIndex += 8;
        }
        if (nBytes == 4) {
            this._setInt(wIndex, 0);
            wIndex += 4;
        } else if (nBytes < 4) {
            for (i = nBytes; i > 0; --i) {
                this._setByte(wIndex, 0);
                ++wIndex;
            }
        } else {
            this._setInt(wIndex, 0);
            wIndex += 4;
            for (i = nBytes - 4; i > 0; --i) {
                this._setByte(wIndex, 0);
                ++wIndex;
            }
        }
        this.writerIndex = wIndex;
        return this;
    }

    @Override
    public int writeCharSequence(CharSequence sequence, Charset charset) {
        int written = this.setCharSequence0(this.writerIndex, sequence, charset, true);
        this.writerIndex += written;
        return written;
    }

    @Override
    public ByteBuf copy() {
        return this.copy(this.readerIndex, this.readableBytes());
    }

    @Override
    public ByteBuf duplicate() {
        return new UnpooledDuplicatedByteBuf(this);
    }

    @Override
    public ByteBuf retainedDuplicate() {
        return this.duplicate().retain();
    }

    @Override
    public ByteBuf slice() {
        return this.slice(this.readerIndex, this.readableBytes());
    }

    @Override
    public ByteBuf retainedSlice() {
        return this.slice().retain();
    }

    @Override
    public ByteBuf slice(int index, int length) {
        return new UnpooledSlicedByteBuf(this, index, length);
    }

    @Override
    public ByteBuf retainedSlice(int index, int length) {
        return this.slice(index, length).retain();
    }

    @Override
    public ByteBuffer nioBuffer() {
        return this.nioBuffer(this.readerIndex, this.readableBytes());
    }

    @Override
    public ByteBuffer[] nioBuffers() {
        return this.nioBuffers(this.readerIndex, this.readableBytes());
    }

    @Override
    public String toString(Charset charset) {
        return this.toString(this.readerIndex, this.readableBytes(), charset);
    }

    @Override
    public String toString(int index, int length, Charset charset) {
        return ByteBufUtil.decodeString(this, index, length, charset);
    }

    @Override
    public int indexOf(int fromIndex, int toIndex, byte value) {
        return ByteBufUtil.indexOf(this, fromIndex, toIndex, value);
    }

    @Override
    public int bytesBefore(byte value) {
        return this.bytesBefore(this.readerIndex(), this.readableBytes(), value);
    }

    @Override
    public int bytesBefore(int length, byte value) {
        this.checkReadableBytes(length);
        return this.bytesBefore(this.readerIndex(), length, value);
    }

    @Override
    public int bytesBefore(int index, int length, byte value) {
        int endIndex = this.indexOf(index, index + length, value);
        if (endIndex < 0) {
            return -1;
        }
        return endIndex - index;
    }

    @Override
    public int forEachByte(ByteProcessor processor) {
        this.ensureAccessible();
        try {
            return this.forEachByteAsc0(this.readerIndex, this.writerIndex, processor);
        }
        catch (Exception e) {
            PlatformDependent.throwException(e);
            return -1;
        }
    }

    @Override
    public int forEachByte(int index, int length, ByteProcessor processor) {
        this.checkIndex(index, length);
        try {
            return this.forEachByteAsc0(index, index + length, processor);
        }
        catch (Exception e) {
            PlatformDependent.throwException(e);
            return -1;
        }
    }

    private int forEachByteAsc0(int start, int end, ByteProcessor processor) throws Exception {
        while (start < end) {
            if (!processor.process(this._getByte(start))) {
                return start;
            }
            ++start;
        }
        return -1;
    }

    @Override
    public int forEachByteDesc(ByteProcessor processor) {
        this.ensureAccessible();
        try {
            return this.forEachByteDesc0(this.writerIndex - 1, this.readerIndex, processor);
        }
        catch (Exception e) {
            PlatformDependent.throwException(e);
            return -1;
        }
    }

    @Override
    public int forEachByteDesc(int index, int length, ByteProcessor processor) {
        this.checkIndex(index, length);
        try {
            return this.forEachByteDesc0(index + length - 1, index, processor);
        }
        catch (Exception e) {
            PlatformDependent.throwException(e);
            return -1;
        }
    }

    private int forEachByteDesc0(int rStart, int rEnd, ByteProcessor processor) throws Exception {
        while (rStart >= rEnd) {
            if (!processor.process(this._getByte(rStart))) {
                return rStart;
            }
            --rStart;
        }
        return -1;
    }

    @Override
    public int hashCode() {
        return ByteBufUtil.hashCode(this);
    }

    @Override
    public boolean equals(Object o) {
        return this == o || o instanceof ByteBuf && ByteBufUtil.equals(this, (ByteBuf)o);
    }

    @Override
    public int compareTo(ByteBuf that) {
        return ByteBufUtil.compare(this, that);
    }

    @Override
    public String toString() {
        ByteBuf unwrapped;
        if (this.refCnt() == 0) {
            return StringUtil.simpleClassName(this) + "(freed)";
        }
        StringBuilder buf = new StringBuilder().append(StringUtil.simpleClassName(this)).append("(ridx: ").append(this.readerIndex).append(", widx: ").append(this.writerIndex).append(", cap: ").append(this.capacity());
        if (this.maxCapacity != Integer.MAX_VALUE) {
            buf.append('/').append(this.maxCapacity);
        }
        if ((unwrapped = this.unwrap()) != null) {
            buf.append(", unwrapped: ").append(unwrapped);
        }
        buf.append(')');
        return buf.toString();
    }

    protected final void checkIndex(int index) {
        this.checkIndex(index, 1);
    }

    protected final void checkIndex(int index, int fieldLength) {
        this.ensureAccessible();
        this.checkIndex0(index, fieldLength);
    }

    final void checkIndex0(int index, int fieldLength) {
        if (MathUtil.isOutOfBounds(index, fieldLength, this.capacity())) {
            throw new IndexOutOfBoundsException(String.format("index: %d, length: %d (expected: range(0, %d))", index, fieldLength, this.capacity()));
        }
    }

    protected final void checkSrcIndex(int index, int length, int srcIndex, int srcCapacity) {
        this.checkIndex(index, length);
        if (MathUtil.isOutOfBounds(srcIndex, length, srcCapacity)) {
            throw new IndexOutOfBoundsException(String.format("srcIndex: %d, length: %d (expected: range(0, %d))", srcIndex, length, srcCapacity));
        }
    }

    protected final void checkDstIndex(int index, int length, int dstIndex, int dstCapacity) {
        this.checkIndex(index, length);
        if (MathUtil.isOutOfBounds(dstIndex, length, dstCapacity)) {
            throw new IndexOutOfBoundsException(String.format("dstIndex: %d, length: %d (expected: range(0, %d))", dstIndex, length, dstCapacity));
        }
    }

    protected final void checkReadableBytes(int minimumReadableBytes) {
        if (minimumReadableBytes < 0) {
            throw new IllegalArgumentException("minimumReadableBytes: " + minimumReadableBytes + " (expected: >= 0)");
        }
        this.checkReadableBytes0(minimumReadableBytes);
    }

    protected final void checkNewCapacity(int newCapacity) {
        this.ensureAccessible();
        if (newCapacity < 0 || newCapacity > this.maxCapacity()) {
            throw new IllegalArgumentException("newCapacity: " + newCapacity + " (expected: 0-" + this.maxCapacity() + ')');
        }
    }

    private void checkReadableBytes0(int minimumReadableBytes) {
        this.ensureAccessible();
        if (this.readerIndex > this.writerIndex - minimumReadableBytes) {
            throw new IndexOutOfBoundsException(String.format("readerIndex(%d) + length(%d) exceeds writerIndex(%d): %s", this.readerIndex, minimumReadableBytes, this.writerIndex, this));
        }
    }

    protected final void ensureAccessible() {
        if (checkAccessible && this.refCnt() == 0) {
            throw new IllegalReferenceCountException(0);
        }
    }

    final void setIndex0(int readerIndex, int writerIndex) {
        this.readerIndex = readerIndex;
        this.writerIndex = writerIndex;
    }

    final void discardMarks() {
        this.markedWriterIndex = 0;
        this.markedReaderIndex = 0;
    }

    static {
        if (logger.isDebugEnabled()) {
            logger.debug("-D{}: {}", (Object)"io.netty.buffer.bytebuf.checkAccessible", (Object)checkAccessible);
        }
        leakDetector = ResourceLeakDetectorFactory.instance().newResourceLeakDetector(ByteBuf.class);
    }
}

