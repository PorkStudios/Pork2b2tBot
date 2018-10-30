/*
 * Decompiled with CFR 0_132.
 */
package io.netty.buffer;

import io.netty.buffer.AbstractByteBufAllocator;
import io.netty.buffer.AbstractReferenceCountedByteBuf;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufAllocatorMetric;
import io.netty.buffer.ByteBufAllocatorMetricProvider;
import io.netty.buffer.CompositeByteBuf;
import io.netty.buffer.UnpooledDirectByteBuf;
import io.netty.buffer.UnpooledHeapByteBuf;
import io.netty.buffer.UnpooledUnsafeDirectByteBuf;
import io.netty.buffer.UnpooledUnsafeHeapByteBuf;
import io.netty.buffer.UnpooledUnsafeNoCleanerDirectByteBuf;
import io.netty.util.internal.LongCounter;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.StringUtil;
import java.nio.ByteBuffer;

public final class UnpooledByteBufAllocator
extends AbstractByteBufAllocator
implements ByteBufAllocatorMetricProvider {
    private final UnpooledByteBufAllocatorMetric metric = new UnpooledByteBufAllocatorMetric();
    private final boolean disableLeakDetector;
    private final boolean noCleaner;
    public static final UnpooledByteBufAllocator DEFAULT = new UnpooledByteBufAllocator(PlatformDependent.directBufferPreferred());

    public UnpooledByteBufAllocator(boolean preferDirect) {
        this(preferDirect, false);
    }

    public UnpooledByteBufAllocator(boolean preferDirect, boolean disableLeakDetector) {
        this(preferDirect, disableLeakDetector, PlatformDependent.useDirectBufferNoCleaner());
    }

    public UnpooledByteBufAllocator(boolean preferDirect, boolean disableLeakDetector, boolean tryNoCleaner) {
        super(preferDirect);
        this.disableLeakDetector = disableLeakDetector;
        this.noCleaner = tryNoCleaner && PlatformDependent.hasUnsafe() && PlatformDependent.hasDirectBufferNoCleanerConstructor();
    }

    @Override
    protected ByteBuf newHeapBuffer(int initialCapacity, int maxCapacity) {
        return PlatformDependent.hasUnsafe() ? new InstrumentedUnpooledUnsafeHeapByteBuf(this, initialCapacity, maxCapacity) : new InstrumentedUnpooledHeapByteBuf(this, initialCapacity, maxCapacity);
    }

    @Override
    protected ByteBuf newDirectBuffer(int initialCapacity, int maxCapacity) {
        AbstractReferenceCountedByteBuf buf = PlatformDependent.hasUnsafe() ? (this.noCleaner ? new InstrumentedUnpooledUnsafeNoCleanerDirectByteBuf(this, initialCapacity, maxCapacity) : new InstrumentedUnpooledUnsafeDirectByteBuf(this, initialCapacity, maxCapacity)) : new InstrumentedUnpooledDirectByteBuf(this, initialCapacity, maxCapacity);
        return this.disableLeakDetector ? buf : UnpooledByteBufAllocator.toLeakAwareBuffer(buf);
    }

    @Override
    public CompositeByteBuf compositeHeapBuffer(int maxNumComponents) {
        CompositeByteBuf buf = new CompositeByteBuf(this, false, maxNumComponents);
        return this.disableLeakDetector ? buf : UnpooledByteBufAllocator.toLeakAwareBuffer(buf);
    }

    @Override
    public CompositeByteBuf compositeDirectBuffer(int maxNumComponents) {
        CompositeByteBuf buf = new CompositeByteBuf(this, true, maxNumComponents);
        return this.disableLeakDetector ? buf : UnpooledByteBufAllocator.toLeakAwareBuffer(buf);
    }

    @Override
    public boolean isDirectBufferPooled() {
        return false;
    }

    @Override
    public ByteBufAllocatorMetric metric() {
        return this.metric;
    }

    void incrementDirect(int amount) {
        this.metric.directCounter.add(amount);
    }

    void decrementDirect(int amount) {
        this.metric.directCounter.add(- amount);
    }

    void incrementHeap(int amount) {
        this.metric.heapCounter.add(amount);
    }

    void decrementHeap(int amount) {
        this.metric.heapCounter.add(- amount);
    }

    private static final class UnpooledByteBufAllocatorMetric
    implements ByteBufAllocatorMetric {
        final LongCounter directCounter = PlatformDependent.newLongCounter();
        final LongCounter heapCounter = PlatformDependent.newLongCounter();

        private UnpooledByteBufAllocatorMetric() {
        }

        @Override
        public long usedHeapMemory() {
            return this.heapCounter.value();
        }

        @Override
        public long usedDirectMemory() {
            return this.directCounter.value();
        }

        public String toString() {
            return StringUtil.simpleClassName(this) + "(usedHeapMemory: " + this.usedHeapMemory() + "; usedDirectMemory: " + this.usedDirectMemory() + ')';
        }
    }

    private static final class InstrumentedUnpooledDirectByteBuf
    extends UnpooledDirectByteBuf {
        InstrumentedUnpooledDirectByteBuf(UnpooledByteBufAllocator alloc, int initialCapacity, int maxCapacity) {
            super((ByteBufAllocator)alloc, initialCapacity, maxCapacity);
        }

        @Override
        protected ByteBuffer allocateDirect(int initialCapacity) {
            ByteBuffer buffer = super.allocateDirect(initialCapacity);
            ((UnpooledByteBufAllocator)this.alloc()).incrementDirect(buffer.capacity());
            return buffer;
        }

        @Override
        protected void freeDirect(ByteBuffer buffer) {
            int capacity = buffer.capacity();
            super.freeDirect(buffer);
            ((UnpooledByteBufAllocator)this.alloc()).decrementDirect(capacity);
        }
    }

    private static final class InstrumentedUnpooledUnsafeDirectByteBuf
    extends UnpooledUnsafeDirectByteBuf {
        InstrumentedUnpooledUnsafeDirectByteBuf(UnpooledByteBufAllocator alloc, int initialCapacity, int maxCapacity) {
            super((ByteBufAllocator)alloc, initialCapacity, maxCapacity);
        }

        @Override
        protected ByteBuffer allocateDirect(int initialCapacity) {
            ByteBuffer buffer = super.allocateDirect(initialCapacity);
            ((UnpooledByteBufAllocator)this.alloc()).incrementDirect(buffer.capacity());
            return buffer;
        }

        @Override
        protected void freeDirect(ByteBuffer buffer) {
            int capacity = buffer.capacity();
            super.freeDirect(buffer);
            ((UnpooledByteBufAllocator)this.alloc()).decrementDirect(capacity);
        }
    }

    private static final class InstrumentedUnpooledUnsafeNoCleanerDirectByteBuf
    extends UnpooledUnsafeNoCleanerDirectByteBuf {
        InstrumentedUnpooledUnsafeNoCleanerDirectByteBuf(UnpooledByteBufAllocator alloc, int initialCapacity, int maxCapacity) {
            super(alloc, initialCapacity, maxCapacity);
        }

        @Override
        protected ByteBuffer allocateDirect(int initialCapacity) {
            ByteBuffer buffer = super.allocateDirect(initialCapacity);
            ((UnpooledByteBufAllocator)this.alloc()).incrementDirect(buffer.capacity());
            return buffer;
        }

        @Override
        ByteBuffer reallocateDirect(ByteBuffer oldBuffer, int initialCapacity) {
            int capacity = oldBuffer.capacity();
            ByteBuffer buffer = super.reallocateDirect(oldBuffer, initialCapacity);
            ((UnpooledByteBufAllocator)this.alloc()).incrementDirect(buffer.capacity() - capacity);
            return buffer;
        }

        @Override
        protected void freeDirect(ByteBuffer buffer) {
            int capacity = buffer.capacity();
            super.freeDirect(buffer);
            ((UnpooledByteBufAllocator)this.alloc()).decrementDirect(capacity);
        }
    }

    private static final class InstrumentedUnpooledHeapByteBuf
    extends UnpooledHeapByteBuf {
        InstrumentedUnpooledHeapByteBuf(UnpooledByteBufAllocator alloc, int initialCapacity, int maxCapacity) {
            super((ByteBufAllocator)alloc, initialCapacity, maxCapacity);
        }

        @Override
        byte[] allocateArray(int initialCapacity) {
            byte[] bytes = super.allocateArray(initialCapacity);
            ((UnpooledByteBufAllocator)this.alloc()).incrementHeap(bytes.length);
            return bytes;
        }

        @Override
        void freeArray(byte[] array) {
            int length = array.length;
            super.freeArray(array);
            ((UnpooledByteBufAllocator)this.alloc()).decrementHeap(length);
        }
    }

    private static final class InstrumentedUnpooledUnsafeHeapByteBuf
    extends UnpooledUnsafeHeapByteBuf {
        InstrumentedUnpooledUnsafeHeapByteBuf(UnpooledByteBufAllocator alloc, int initialCapacity, int maxCapacity) {
            super(alloc, initialCapacity, maxCapacity);
        }

        @Override
        byte[] allocateArray(int initialCapacity) {
            byte[] bytes = super.allocateArray(initialCapacity);
            ((UnpooledByteBufAllocator)this.alloc()).incrementHeap(bytes.length);
            return bytes;
        }

        @Override
        void freeArray(byte[] array) {
            int length = array.length;
            super.freeArray(array);
            ((UnpooledByteBufAllocator)this.alloc()).decrementHeap(length);
        }
    }

}

