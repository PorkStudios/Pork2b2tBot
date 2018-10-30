/*
 * Decompiled with CFR 0_132.
 */
package io.netty.util.internal.shaded.org.jctools.queues;

import io.netty.util.internal.shaded.org.jctools.queues.CircularArrayOffsetCalculator;
import io.netty.util.internal.shaded.org.jctools.queues.ConcurrentCircularArrayQueueL0Pad;
import io.netty.util.internal.shaded.org.jctools.queues.IndexedQueueSizeUtil;
import io.netty.util.internal.shaded.org.jctools.util.Pow2;
import java.util.Iterator;

public abstract class ConcurrentCircularArrayQueue<E>
extends ConcurrentCircularArrayQueueL0Pad<E> {
    protected final long mask;
    protected final E[] buffer;

    public ConcurrentCircularArrayQueue(int capacity) {
        int actualCapacity = Pow2.roundToPowerOfTwo(capacity);
        this.mask = actualCapacity - 1;
        this.buffer = CircularArrayOffsetCalculator.allocate(actualCapacity);
    }

    protected static long calcElementOffset(long index, long mask) {
        return CircularArrayOffsetCalculator.calcElementOffset(index, mask);
    }

    protected final long calcElementOffset(long index) {
        return ConcurrentCircularArrayQueue.calcElementOffset(index, this.mask);
    }

    @Override
    public Iterator<E> iterator() {
        throw new UnsupportedOperationException();
    }

    @Override
    public final int size() {
        return IndexedQueueSizeUtil.size(this);
    }

    @Override
    public final boolean isEmpty() {
        return IndexedQueueSizeUtil.isEmpty(this);
    }

    @Override
    public String toString() {
        return this.getClass().getName();
    }

    @Override
    public void clear() {
        while (this.poll() != null) {
        }
    }

    @Override
    public int capacity() {
        return (int)(this.mask + 1L);
    }

    @Override
    public final long currentProducerIndex() {
        return this.lvProducerIndex();
    }

    @Override
    public final long currentConsumerIndex() {
        return this.lvConsumerIndex();
    }
}

