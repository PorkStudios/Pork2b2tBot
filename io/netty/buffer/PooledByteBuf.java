/*
 * Decompiled with CFR 0_132.
 */
package io.netty.buffer;

import io.netty.buffer.AbstractReferenceCountedByteBuf;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.PoolArena;
import io.netty.buffer.PoolChunk;
import io.netty.buffer.PoolThreadCache;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.buffer.PooledDuplicatedByteBuf;
import io.netty.buffer.PooledSlicedByteBuf;
import io.netty.util.Recycler;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

abstract class PooledByteBuf<T>
extends AbstractReferenceCountedByteBuf {
    private final Recycler.Handle<PooledByteBuf<T>> recyclerHandle;
    protected PoolChunk<T> chunk;
    protected long handle;
    protected T memory;
    protected int offset;
    protected int length;
    int maxLength;
    PoolThreadCache cache;
    private ByteBuffer tmpNioBuf;
    private ByteBufAllocator allocator;

    protected PooledByteBuf(Recycler.Handle<? extends PooledByteBuf<T>> recyclerHandle, int maxCapacity) {
        super(maxCapacity);
        this.recyclerHandle = recyclerHandle;
    }

    void init(PoolChunk<T> chunk, long handle, int offset, int length, int maxLength, PoolThreadCache cache) {
        this.init0(chunk, handle, offset, length, maxLength, cache);
    }

    void initUnpooled(PoolChunk<T> chunk, int length) {
        this.init0(chunk, 0L, chunk.offset, length, length, null);
    }

    private void init0(PoolChunk<T> chunk, long handle, int offset, int length, int maxLength, PoolThreadCache cache) {
        assert (handle >= 0L);
        assert (chunk != null);
        this.chunk = chunk;
        this.memory = chunk.memory;
        this.allocator = chunk.arena.parent;
        this.cache = cache;
        this.handle = handle;
        this.offset = offset;
        this.length = length;
        this.maxLength = maxLength;
        this.tmpNioBuf = null;
    }

    final void reuse(int maxCapacity) {
        this.maxCapacity(maxCapacity);
        this.setRefCnt(1);
        this.setIndex0(0, 0);
        this.discardMarks();
    }

    @Override
    public final int capacity() {
        return this.length;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    @Override
    public final ByteBuf capacity(int newCapacity) {
        this.checkNewCapacity(newCapacity);
        if (this.chunk.unpooled) {
            if (newCapacity == this.length) {
                return this;
            }
        } else if (newCapacity > this.length) {
            if (newCapacity <= this.maxLength) {
                this.length = newCapacity;
                return this;
            }
        } else {
            if (newCapacity >= this.length) return this;
            if (newCapacity > this.maxLength >>> 1) {
                if (this.maxLength <= 512) {
                    if (newCapacity > this.maxLength - 16) {
                        this.length = newCapacity;
                        this.setIndex(Math.min(this.readerIndex(), newCapacity), Math.min(this.writerIndex(), newCapacity));
                        return this;
                    }
                } else {
                    this.length = newCapacity;
                    this.setIndex(Math.min(this.readerIndex(), newCapacity), Math.min(this.writerIndex(), newCapacity));
                    return this;
                }
            }
        }
        this.chunk.arena.reallocate(this, newCapacity, true);
        return this;
    }

    @Override
    public final ByteBufAllocator alloc() {
        return this.allocator;
    }

    @Override
    public final ByteOrder order() {
        return ByteOrder.BIG_ENDIAN;
    }

    @Override
    public final ByteBuf unwrap() {
        return null;
    }

    @Override
    public final ByteBuf retainedDuplicate() {
        return PooledDuplicatedByteBuf.newInstance(this, this, this.readerIndex(), this.writerIndex());
    }

    @Override
    public final ByteBuf retainedSlice() {
        int index = this.readerIndex();
        return this.retainedSlice(index, this.writerIndex() - index);
    }

    @Override
    public final ByteBuf retainedSlice(int index, int length) {
        return PooledSlicedByteBuf.newInstance(this, this, index, length);
    }

    protected final ByteBuffer internalNioBuffer() {
        ByteBuffer tmpNioBuf = this.tmpNioBuf;
        if (tmpNioBuf == null) {
            this.tmpNioBuf = tmpNioBuf = this.newInternalNioBuffer(this.memory);
        }
        return tmpNioBuf;
    }

    protected abstract ByteBuffer newInternalNioBuffer(T var1);

    @Override
    protected final void deallocate() {
        if (this.handle >= 0L) {
            long handle = this.handle;
            this.handle = -1L;
            this.memory = null;
            this.tmpNioBuf = null;
            this.chunk.arena.free(this.chunk, handle, this.maxLength, this.cache);
            this.chunk = null;
            this.recycle();
        }
    }

    private void recycle() {
        this.recyclerHandle.recycle(this);
    }

    protected final int idx(int index) {
        return this.offset + index;
    }
}

