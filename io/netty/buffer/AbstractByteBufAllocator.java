/*
 * Decompiled with CFR 0_132.
 */
package io.netty.buffer;

import io.netty.buffer.AbstractByteBuf;
import io.netty.buffer.AdvancedLeakAwareByteBuf;
import io.netty.buffer.AdvancedLeakAwareCompositeByteBuf;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.CompositeByteBuf;
import io.netty.buffer.EmptyByteBuf;
import io.netty.buffer.SimpleLeakAwareByteBuf;
import io.netty.buffer.SimpleLeakAwareCompositeByteBuf;
import io.netty.util.ResourceLeakDetector;
import io.netty.util.ResourceLeakTracker;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.StringUtil;

public abstract class AbstractByteBufAllocator
implements ByteBufAllocator {
    static final int DEFAULT_INITIAL_CAPACITY = 256;
    static final int DEFAULT_MAX_CAPACITY = Integer.MAX_VALUE;
    static final int DEFAULT_MAX_COMPONENTS = 16;
    static final int CALCULATE_THRESHOLD = 4194304;
    private final boolean directByDefault;
    private final ByteBuf emptyBuf;

    protected static ByteBuf toLeakAwareBuffer(ByteBuf buf) {
        switch (ResourceLeakDetector.getLevel()) {
            case SIMPLE: {
                ResourceLeakTracker<ByteBuf> leak = AbstractByteBuf.leakDetector.track(buf);
                if (leak == null) break;
                buf = new SimpleLeakAwareByteBuf(buf, leak);
                break;
            }
            case ADVANCED: 
            case PARANOID: {
                ResourceLeakTracker<ByteBuf> leak = AbstractByteBuf.leakDetector.track(buf);
                if (leak == null) break;
                buf = new AdvancedLeakAwareByteBuf(buf, leak);
                break;
            }
        }
        return buf;
    }

    protected static CompositeByteBuf toLeakAwareBuffer(CompositeByteBuf buf) {
        switch (ResourceLeakDetector.getLevel()) {
            case SIMPLE: {
                ResourceLeakTracker<ByteBuf> leak = AbstractByteBuf.leakDetector.track(buf);
                if (leak == null) break;
                buf = new SimpleLeakAwareCompositeByteBuf(buf, leak);
                break;
            }
            case ADVANCED: 
            case PARANOID: {
                ResourceLeakTracker<ByteBuf> leak = AbstractByteBuf.leakDetector.track(buf);
                if (leak == null) break;
                buf = new AdvancedLeakAwareCompositeByteBuf(buf, leak);
                break;
            }
        }
        return buf;
    }

    protected AbstractByteBufAllocator() {
        this(false);
    }

    protected AbstractByteBufAllocator(boolean preferDirect) {
        this.directByDefault = preferDirect && PlatformDependent.hasUnsafe();
        this.emptyBuf = new EmptyByteBuf(this);
    }

    @Override
    public ByteBuf buffer() {
        if (this.directByDefault) {
            return this.directBuffer();
        }
        return this.heapBuffer();
    }

    @Override
    public ByteBuf buffer(int initialCapacity) {
        if (this.directByDefault) {
            return this.directBuffer(initialCapacity);
        }
        return this.heapBuffer(initialCapacity);
    }

    @Override
    public ByteBuf buffer(int initialCapacity, int maxCapacity) {
        if (this.directByDefault) {
            return this.directBuffer(initialCapacity, maxCapacity);
        }
        return this.heapBuffer(initialCapacity, maxCapacity);
    }

    @Override
    public ByteBuf ioBuffer() {
        if (PlatformDependent.hasUnsafe()) {
            return this.directBuffer(256);
        }
        return this.heapBuffer(256);
    }

    @Override
    public ByteBuf ioBuffer(int initialCapacity) {
        if (PlatformDependent.hasUnsafe()) {
            return this.directBuffer(initialCapacity);
        }
        return this.heapBuffer(initialCapacity);
    }

    @Override
    public ByteBuf ioBuffer(int initialCapacity, int maxCapacity) {
        if (PlatformDependent.hasUnsafe()) {
            return this.directBuffer(initialCapacity, maxCapacity);
        }
        return this.heapBuffer(initialCapacity, maxCapacity);
    }

    @Override
    public ByteBuf heapBuffer() {
        return this.heapBuffer(256, Integer.MAX_VALUE);
    }

    @Override
    public ByteBuf heapBuffer(int initialCapacity) {
        return this.heapBuffer(initialCapacity, Integer.MAX_VALUE);
    }

    @Override
    public ByteBuf heapBuffer(int initialCapacity, int maxCapacity) {
        if (initialCapacity == 0 && maxCapacity == 0) {
            return this.emptyBuf;
        }
        AbstractByteBufAllocator.validate(initialCapacity, maxCapacity);
        return this.newHeapBuffer(initialCapacity, maxCapacity);
    }

    @Override
    public ByteBuf directBuffer() {
        return this.directBuffer(256, Integer.MAX_VALUE);
    }

    @Override
    public ByteBuf directBuffer(int initialCapacity) {
        return this.directBuffer(initialCapacity, Integer.MAX_VALUE);
    }

    @Override
    public ByteBuf directBuffer(int initialCapacity, int maxCapacity) {
        if (initialCapacity == 0 && maxCapacity == 0) {
            return this.emptyBuf;
        }
        AbstractByteBufAllocator.validate(initialCapacity, maxCapacity);
        return this.newDirectBuffer(initialCapacity, maxCapacity);
    }

    @Override
    public CompositeByteBuf compositeBuffer() {
        if (this.directByDefault) {
            return this.compositeDirectBuffer();
        }
        return this.compositeHeapBuffer();
    }

    @Override
    public CompositeByteBuf compositeBuffer(int maxNumComponents) {
        if (this.directByDefault) {
            return this.compositeDirectBuffer(maxNumComponents);
        }
        return this.compositeHeapBuffer(maxNumComponents);
    }

    @Override
    public CompositeByteBuf compositeHeapBuffer() {
        return this.compositeHeapBuffer(16);
    }

    @Override
    public CompositeByteBuf compositeHeapBuffer(int maxNumComponents) {
        return AbstractByteBufAllocator.toLeakAwareBuffer(new CompositeByteBuf(this, false, maxNumComponents));
    }

    @Override
    public CompositeByteBuf compositeDirectBuffer() {
        return this.compositeDirectBuffer(16);
    }

    @Override
    public CompositeByteBuf compositeDirectBuffer(int maxNumComponents) {
        return AbstractByteBufAllocator.toLeakAwareBuffer(new CompositeByteBuf(this, true, maxNumComponents));
    }

    private static void validate(int initialCapacity, int maxCapacity) {
        if (initialCapacity < 0) {
            throw new IllegalArgumentException("initialCapacity: " + initialCapacity + " (expected: 0+)");
        }
        if (initialCapacity > maxCapacity) {
            throw new IllegalArgumentException(String.format("initialCapacity: %d (expected: not greater than maxCapacity(%d)", initialCapacity, maxCapacity));
        }
    }

    protected abstract ByteBuf newHeapBuffer(int var1, int var2);

    protected abstract ByteBuf newDirectBuffer(int var1, int var2);

    public String toString() {
        return StringUtil.simpleClassName(this) + "(directByDefault: " + this.directByDefault + ')';
    }

    @Override
    public int calculateNewCapacity(int minNewCapacity, int maxCapacity) {
        int newCapacity;
        if (minNewCapacity < 0) {
            throw new IllegalArgumentException("minNewCapacity: " + minNewCapacity + " (expected: 0+)");
        }
        if (minNewCapacity > maxCapacity) {
            throw new IllegalArgumentException(String.format("minNewCapacity: %d (expected: not greater than maxCapacity(%d)", minNewCapacity, maxCapacity));
        }
        int threshold = 4194304;
        if (minNewCapacity == 4194304) {
            return 4194304;
        }
        if (minNewCapacity > 4194304) {
            int newCapacity2 = minNewCapacity / 4194304 * 4194304;
            newCapacity2 = newCapacity2 > maxCapacity - 4194304 ? maxCapacity : (newCapacity2 += 4194304);
            return newCapacity2;
        }
        for (newCapacity = 64; newCapacity < minNewCapacity; newCapacity <<= 1) {
        }
        return Math.min(newCapacity, maxCapacity);
    }

    static {
        ResourceLeakDetector.addExclusions(AbstractByteBufAllocator.class, "toLeakAwareBuffer");
    }

}

