/*
 * Decompiled with CFR 0_132.
 */
package io.netty.buffer;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.UnpooledUnsafeDirectByteBuf;
import io.netty.util.internal.PlatformDependent;
import java.nio.ByteBuffer;

class UnpooledUnsafeNoCleanerDirectByteBuf
extends UnpooledUnsafeDirectByteBuf {
    UnpooledUnsafeNoCleanerDirectByteBuf(ByteBufAllocator alloc, int initialCapacity, int maxCapacity) {
        super(alloc, initialCapacity, maxCapacity);
    }

    @Override
    protected ByteBuffer allocateDirect(int initialCapacity) {
        return PlatformDependent.allocateDirectNoCleaner(initialCapacity);
    }

    ByteBuffer reallocateDirect(ByteBuffer oldBuffer, int initialCapacity) {
        return PlatformDependent.reallocateDirectNoCleaner(oldBuffer, initialCapacity);
    }

    @Override
    protected void freeDirect(ByteBuffer buffer) {
        PlatformDependent.freeDirectNoCleaner(buffer);
    }

    @Override
    public ByteBuf capacity(int newCapacity) {
        this.checkNewCapacity(newCapacity);
        int oldCapacity = this.capacity();
        if (newCapacity == oldCapacity) {
            return this;
        }
        ByteBuffer newBuffer = this.reallocateDirect(this.buffer, newCapacity);
        if (newCapacity < oldCapacity) {
            if (this.readerIndex() < newCapacity) {
                if (this.writerIndex() > newCapacity) {
                    this.writerIndex(newCapacity);
                }
            } else {
                this.setIndex(newCapacity, newCapacity);
            }
        }
        this.setByteBuffer(newBuffer, false);
        return this;
    }
}

