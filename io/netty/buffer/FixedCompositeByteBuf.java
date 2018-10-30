/*
 * Decompiled with CFR 0_132.
 */
package io.netty.buffer;

import io.netty.buffer.AbstractReferenceCountedByteBuf;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.Unpooled;
import io.netty.util.internal.EmptyArrays;
import io.netty.util.internal.RecyclableArrayList;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ReadOnlyBufferException;
import java.nio.channels.FileChannel;
import java.nio.channels.GatheringByteChannel;
import java.nio.channels.ScatteringByteChannel;
import java.util.Collections;

final class FixedCompositeByteBuf
extends AbstractReferenceCountedByteBuf {
    private static final ByteBuf[] EMPTY = new ByteBuf[]{Unpooled.EMPTY_BUFFER};
    private final int nioBufferCount;
    private final int capacity;
    private final ByteBufAllocator allocator;
    private final ByteOrder order;
    private final Object[] buffers;
    private final boolean direct;

    /* varargs */ FixedCompositeByteBuf(ByteBufAllocator allocator, ByteBuf ... buffers) {
        super(Integer.MAX_VALUE);
        if (buffers.length == 0) {
            this.buffers = EMPTY;
            this.order = ByteOrder.BIG_ENDIAN;
            this.nioBufferCount = 1;
            this.capacity = 0;
            this.direct = false;
        } else {
            ByteBuf b = buffers[0];
            this.buffers = new Object[buffers.length];
            this.buffers[0] = b;
            boolean direct = true;
            int nioBufferCount = b.nioBufferCount();
            int capacity = b.readableBytes();
            this.order = b.order();
            for (int i = 1; i < buffers.length; ++i) {
                b = buffers[i];
                if (buffers[i].order() != this.order) {
                    throw new IllegalArgumentException("All ByteBufs need to have same ByteOrder");
                }
                nioBufferCount += b.nioBufferCount();
                capacity += b.readableBytes();
                if (!b.isDirect()) {
                    direct = false;
                }
                this.buffers[i] = b;
            }
            this.nioBufferCount = nioBufferCount;
            this.capacity = capacity;
            this.direct = direct;
        }
        this.setIndex(0, this.capacity());
        this.allocator = allocator;
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
    public int capacity() {
        return this.capacity;
    }

    @Override
    public int maxCapacity() {
        return this.capacity;
    }

    @Override
    public ByteBuf capacity(int newCapacity) {
        throw new ReadOnlyBufferException();
    }

    @Override
    public ByteBufAllocator alloc() {
        return this.allocator;
    }

    @Override
    public ByteOrder order() {
        return this.order;
    }

    @Override
    public ByteBuf unwrap() {
        return null;
    }

    @Override
    public boolean isDirect() {
        return this.direct;
    }

    private Component findComponent(int index) {
        int readable = 0;
        for (int i = 0; i < this.buffers.length; ++i) {
            ByteBuf b;
            boolean isBuffer;
            Component comp = null;
            Object obj = this.buffers[i];
            if (obj instanceof ByteBuf) {
                b = (ByteBuf)obj;
                isBuffer = true;
            } else {
                comp = (Component)obj;
                b = comp.buf;
                isBuffer = false;
            }
            if (index >= (readable += b.readableBytes())) continue;
            if (isBuffer) {
                comp = new Component(i, readable - b.readableBytes(), b);
                this.buffers[i] = comp;
            }
            return comp;
        }
        throw new IllegalStateException();
    }

    private ByteBuf buffer(int i) {
        Object obj = this.buffers[i];
        if (obj instanceof ByteBuf) {
            return (ByteBuf)obj;
        }
        return ((Component)obj).buf;
    }

    @Override
    public byte getByte(int index) {
        return this._getByte(index);
    }

    @Override
    protected byte _getByte(int index) {
        Component c = this.findComponent(index);
        return c.buf.getByte(index - c.offset);
    }

    @Override
    protected short _getShort(int index) {
        Component c = this.findComponent(index);
        if (index + 2 <= c.endOffset) {
            return c.buf.getShort(index - c.offset);
        }
        if (this.order() == ByteOrder.BIG_ENDIAN) {
            return (short)((this._getByte(index) & 255) << 8 | this._getByte(index + 1) & 255);
        }
        return (short)(this._getByte(index) & 255 | (this._getByte(index + 1) & 255) << 8);
    }

    @Override
    protected short _getShortLE(int index) {
        Component c = this.findComponent(index);
        if (index + 2 <= c.endOffset) {
            return c.buf.getShortLE(index - c.offset);
        }
        if (this.order() == ByteOrder.BIG_ENDIAN) {
            return (short)(this._getByte(index) & 255 | (this._getByte(index + 1) & 255) << 8);
        }
        return (short)((this._getByte(index) & 255) << 8 | this._getByte(index + 1) & 255);
    }

    @Override
    protected int _getUnsignedMedium(int index) {
        Component c = this.findComponent(index);
        if (index + 3 <= c.endOffset) {
            return c.buf.getUnsignedMedium(index - c.offset);
        }
        if (this.order() == ByteOrder.BIG_ENDIAN) {
            return (this._getShort(index) & 65535) << 8 | this._getByte(index + 2) & 255;
        }
        return this._getShort(index) & 65535 | (this._getByte(index + 2) & 255) << 16;
    }

    @Override
    protected int _getUnsignedMediumLE(int index) {
        Component c = this.findComponent(index);
        if (index + 3 <= c.endOffset) {
            return c.buf.getUnsignedMediumLE(index - c.offset);
        }
        if (this.order() == ByteOrder.BIG_ENDIAN) {
            return this._getShortLE(index) & 65535 | (this._getByte(index + 2) & 255) << 16;
        }
        return (this._getShortLE(index) & 65535) << 8 | this._getByte(index + 2) & 255;
    }

    @Override
    protected int _getInt(int index) {
        Component c = this.findComponent(index);
        if (index + 4 <= c.endOffset) {
            return c.buf.getInt(index - c.offset);
        }
        if (this.order() == ByteOrder.BIG_ENDIAN) {
            return (this._getShort(index) & 65535) << 16 | this._getShort(index + 2) & 65535;
        }
        return this._getShort(index) & 65535 | (this._getShort(index + 2) & 65535) << 16;
    }

    @Override
    protected int _getIntLE(int index) {
        Component c = this.findComponent(index);
        if (index + 4 <= c.endOffset) {
            return c.buf.getIntLE(index - c.offset);
        }
        if (this.order() == ByteOrder.BIG_ENDIAN) {
            return this._getShortLE(index) & 65535 | (this._getShortLE(index + 2) & 65535) << 16;
        }
        return (this._getShortLE(index) & 65535) << 16 | this._getShortLE(index + 2) & 65535;
    }

    @Override
    protected long _getLong(int index) {
        Component c = this.findComponent(index);
        if (index + 8 <= c.endOffset) {
            return c.buf.getLong(index - c.offset);
        }
        if (this.order() == ByteOrder.BIG_ENDIAN) {
            return ((long)this._getInt(index) & 0xFFFFFFFFL) << 32 | (long)this._getInt(index + 4) & 0xFFFFFFFFL;
        }
        return (long)this._getInt(index) & 0xFFFFFFFFL | ((long)this._getInt(index + 4) & 0xFFFFFFFFL) << 32;
    }

    @Override
    protected long _getLongLE(int index) {
        Component c = this.findComponent(index);
        if (index + 8 <= c.endOffset) {
            return c.buf.getLongLE(index - c.offset);
        }
        if (this.order() == ByteOrder.BIG_ENDIAN) {
            return (long)this._getIntLE(index) & 0xFFFFFFFFL | ((long)this._getIntLE(index + 4) & 0xFFFFFFFFL) << 32;
        }
        return ((long)this._getIntLE(index) & 0xFFFFFFFFL) << 32 | (long)this._getIntLE(index + 4) & 0xFFFFFFFFL;
    }

    @Override
    public ByteBuf getBytes(int index, byte[] dst, int dstIndex, int length) {
        this.checkDstIndex(index, length, dstIndex, dst.length);
        if (length == 0) {
            return this;
        }
        Component c = this.findComponent(index);
        int i = c.index;
        int adjustment = c.offset;
        ByteBuf s = c.buf;
        do {
            int localLength = Math.min(length, s.readableBytes() - (index - adjustment));
            s.getBytes(index - adjustment, dst, dstIndex, localLength);
            index += localLength;
            dstIndex += localLength;
            adjustment += s.readableBytes();
            if ((length -= localLength) <= 0) break;
            s = this.buffer(++i);
        } while (true);
        return this;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public ByteBuf getBytes(int index, ByteBuffer dst) {
        int limit = dst.limit();
        int length = dst.remaining();
        this.checkIndex(index, length);
        if (length == 0) {
            return this;
        }
        try {
            Component c = this.findComponent(index);
            int i = c.index;
            int adjustment = c.offset;
            ByteBuf s = c.buf;
            do {
                int localLength = Math.min(length, s.readableBytes() - (index - adjustment));
                dst.limit(dst.position() + localLength);
                s.getBytes(index - adjustment, dst);
                index += localLength;
                adjustment += s.readableBytes();
                if ((length -= localLength) <= 0) {
                    break;
                }
                s = this.buffer(++i);
            } while (true);
        }
        finally {
            dst.limit(limit);
        }
        return this;
    }

    @Override
    public ByteBuf getBytes(int index, ByteBuf dst, int dstIndex, int length) {
        this.checkDstIndex(index, length, dstIndex, dst.capacity());
        if (length == 0) {
            return this;
        }
        Component c = this.findComponent(index);
        int i = c.index;
        int adjustment = c.offset;
        ByteBuf s = c.buf;
        do {
            int localLength = Math.min(length, s.readableBytes() - (index - adjustment));
            s.getBytes(index - adjustment, dst, dstIndex, localLength);
            index += localLength;
            dstIndex += localLength;
            adjustment += s.readableBytes();
            if ((length -= localLength) <= 0) break;
            s = this.buffer(++i);
        } while (true);
        return this;
    }

    @Override
    public int getBytes(int index, GatheringByteChannel out, int length) throws IOException {
        int count = this.nioBufferCount();
        if (count == 1) {
            return out.write(this.internalNioBuffer(index, length));
        }
        long writtenBytes = out.write(this.nioBuffers(index, length));
        if (writtenBytes > Integer.MAX_VALUE) {
            return Integer.MAX_VALUE;
        }
        return (int)writtenBytes;
    }

    @Override
    public int getBytes(int index, FileChannel out, long position, int length) throws IOException {
        int count = this.nioBufferCount();
        if (count == 1) {
            return out.write(this.internalNioBuffer(index, length), position);
        }
        long writtenBytes = 0L;
        for (ByteBuffer buf : this.nioBuffers(index, length)) {
            writtenBytes += (long)out.write(buf, position + writtenBytes);
        }
        if (writtenBytes > Integer.MAX_VALUE) {
            return Integer.MAX_VALUE;
        }
        return (int)writtenBytes;
    }

    @Override
    public ByteBuf getBytes(int index, OutputStream out, int length) throws IOException {
        this.checkIndex(index, length);
        if (length == 0) {
            return this;
        }
        Component c = this.findComponent(index);
        int i = c.index;
        int adjustment = c.offset;
        ByteBuf s = c.buf;
        do {
            int localLength = Math.min(length, s.readableBytes() - (index - adjustment));
            s.getBytes(index - adjustment, out, localLength);
            index += localLength;
            adjustment += s.readableBytes();
            if ((length -= localLength) <= 0) break;
            s = this.buffer(++i);
        } while (true);
        return this;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public ByteBuf copy(int index, int length) {
        this.checkIndex(index, length);
        boolean release = true;
        ByteBuf buf = this.alloc().buffer(length);
        try {
            buf.writeBytes(this, index, length);
            release = false;
            ByteBuf byteBuf = buf;
            return byteBuf;
        }
        finally {
            if (release) {
                buf.release();
            }
        }
    }

    @Override
    public int nioBufferCount() {
        return this.nioBufferCount;
    }

    @Override
    public ByteBuffer nioBuffer(int index, int length) {
        ByteBuf buf;
        this.checkIndex(index, length);
        if (this.buffers.length == 1 && (buf = this.buffer(0)).nioBufferCount() == 1) {
            return buf.nioBuffer(index, length);
        }
        ByteBuffer merged = ByteBuffer.allocate(length).order(this.order());
        ByteBuffer[] buffers = this.nioBuffers(index, length);
        for (int i = 0; i < buffers.length; ++i) {
            merged.put(buffers[i]);
        }
        merged.flip();
        return merged;
    }

    @Override
    public ByteBuffer internalNioBuffer(int index, int length) {
        if (this.buffers.length == 1) {
            return this.buffer(0).internalNioBuffer(index, length);
        }
        throw new UnsupportedOperationException();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public ByteBuffer[] nioBuffers(int index, int length) {
        this.checkIndex(index, length);
        if (length == 0) {
            return EmptyArrays.EMPTY_BYTE_BUFFERS;
        }
        RecyclableArrayList array = RecyclableArrayList.newInstance(this.buffers.length);
        try {
            Component c = this.findComponent(index);
            int i = c.index;
            int adjustment = c.offset;
            ByteBuf s = c.buf;
            do {
                int localLength = Math.min(length, s.readableBytes() - (index - adjustment));
                switch (s.nioBufferCount()) {
                    case 0: {
                        throw new UnsupportedOperationException();
                    }
                    case 1: {
                        array.add(s.nioBuffer(index - adjustment, localLength));
                        break;
                    }
                    default: {
                        Collections.addAll(array, s.nioBuffers(index - adjustment, localLength));
                    }
                }
                index += localLength;
                adjustment += s.readableBytes();
                if ((length -= localLength) <= 0) break;
                s = this.buffer(++i);
            } while (true);
            ByteBuffer[] localLength = array.toArray(new ByteBuffer[array.size()]);
            return localLength;
        }
        finally {
            array.recycle();
        }
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
    protected void deallocate() {
        for (int i = 0; i < this.buffers.length; ++i) {
            this.buffer(i).release();
        }
    }

    @Override
    public String toString() {
        String result = super.toString();
        result = result.substring(0, result.length() - 1);
        return result + ", components=" + this.buffers.length + ')';
    }

    private static final class Component {
        private final int index;
        private final int offset;
        private final ByteBuf buf;
        private final int endOffset;

        Component(int index, int offset, ByteBuf buf) {
            this.index = index;
            this.offset = offset;
            this.endOffset = offset + buf.readableBytes();
            this.buf = buf;
        }
    }

}

