/*
 * Decompiled with CFR 0_132.
 */
package io.netty.channel;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.CompositeByteBuf;
import io.netty.util.internal.ObjectUtil;

public final class PreferHeapByteBufAllocator
implements ByteBufAllocator {
    private final ByteBufAllocator allocator;

    public PreferHeapByteBufAllocator(ByteBufAllocator allocator) {
        this.allocator = ObjectUtil.checkNotNull(allocator, "allocator");
    }

    @Override
    public ByteBuf buffer() {
        return this.allocator.heapBuffer();
    }

    @Override
    public ByteBuf buffer(int initialCapacity) {
        return this.allocator.heapBuffer(initialCapacity);
    }

    @Override
    public ByteBuf buffer(int initialCapacity, int maxCapacity) {
        return this.allocator.heapBuffer(initialCapacity, maxCapacity);
    }

    @Override
    public ByteBuf ioBuffer() {
        return this.allocator.heapBuffer();
    }

    @Override
    public ByteBuf ioBuffer(int initialCapacity) {
        return this.allocator.heapBuffer(initialCapacity);
    }

    @Override
    public ByteBuf ioBuffer(int initialCapacity, int maxCapacity) {
        return this.allocator.heapBuffer(initialCapacity, maxCapacity);
    }

    @Override
    public ByteBuf heapBuffer() {
        return this.allocator.heapBuffer();
    }

    @Override
    public ByteBuf heapBuffer(int initialCapacity) {
        return this.allocator.heapBuffer(initialCapacity);
    }

    @Override
    public ByteBuf heapBuffer(int initialCapacity, int maxCapacity) {
        return this.allocator.heapBuffer(initialCapacity, maxCapacity);
    }

    @Override
    public ByteBuf directBuffer() {
        return this.allocator.directBuffer();
    }

    @Override
    public ByteBuf directBuffer(int initialCapacity) {
        return this.allocator.directBuffer(initialCapacity);
    }

    @Override
    public ByteBuf directBuffer(int initialCapacity, int maxCapacity) {
        return this.allocator.directBuffer(initialCapacity, maxCapacity);
    }

    @Override
    public CompositeByteBuf compositeBuffer() {
        return this.allocator.compositeHeapBuffer();
    }

    @Override
    public CompositeByteBuf compositeBuffer(int maxNumComponents) {
        return this.allocator.compositeHeapBuffer(maxNumComponents);
    }

    @Override
    public CompositeByteBuf compositeHeapBuffer() {
        return this.allocator.compositeHeapBuffer();
    }

    @Override
    public CompositeByteBuf compositeHeapBuffer(int maxNumComponents) {
        return this.allocator.compositeHeapBuffer(maxNumComponents);
    }

    @Override
    public CompositeByteBuf compositeDirectBuffer() {
        return this.allocator.compositeDirectBuffer();
    }

    @Override
    public CompositeByteBuf compositeDirectBuffer(int maxNumComponents) {
        return this.allocator.compositeDirectBuffer(maxNumComponents);
    }

    @Override
    public boolean isDirectBufferPooled() {
        return this.allocator.isDirectBufferPooled();
    }

    @Override
    public int calculateNewCapacity(int minNewCapacity, int maxCapacity) {
        return this.allocator.calculateNewCapacity(minNewCapacity, maxCapacity);
    }
}

