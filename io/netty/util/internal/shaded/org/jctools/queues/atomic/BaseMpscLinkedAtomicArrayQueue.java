/*
 * Decompiled with CFR 0_132.
 */
package io.netty.util.internal.shaded.org.jctools.queues.atomic;

import io.netty.util.internal.shaded.org.jctools.queues.MessagePassingQueue;
import io.netty.util.internal.shaded.org.jctools.queues.QueueProgressIndicators;
import io.netty.util.internal.shaded.org.jctools.queues.atomic.BaseMpscLinkedAtomicArrayQueueColdProducerFields;
import io.netty.util.internal.shaded.org.jctools.queues.atomic.LinkedAtomicArrayQueueUtil;
import io.netty.util.internal.shaded.org.jctools.util.PortableJvmInfo;
import io.netty.util.internal.shaded.org.jctools.util.Pow2;
import io.netty.util.internal.shaded.org.jctools.util.RangeUtil;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicReferenceArray;

public abstract class BaseMpscLinkedAtomicArrayQueue<E>
extends BaseMpscLinkedAtomicArrayQueueColdProducerFields<E>
implements MessagePassingQueue<E>,
QueueProgressIndicators {
    private static final Object JUMP = new Object();

    public BaseMpscLinkedAtomicArrayQueue(int initialCapacity) {
        AtomicReferenceArray buffer;
        RangeUtil.checkGreaterThanOrEqual(initialCapacity, 2, "initialCapacity");
        int p2capacity = Pow2.roundToPowerOfTwo(initialCapacity);
        long mask = p2capacity - 1 << 1;
        this.producerBuffer = buffer = LinkedAtomicArrayQueueUtil.allocate(p2capacity + 1);
        this.producerMask = mask;
        this.consumerBuffer = buffer;
        this.consumerMask = mask;
        this.soProducerLimit(mask);
    }

    @Override
    public final Iterator<E> iterator() {
        throw new UnsupportedOperationException();
    }

    @Override
    public final int size() {
        long before;
        long currentProducerIndex;
        long after = this.lvConsumerIndex();
        do {
            before = after;
            currentProducerIndex = this.lvProducerIndex();
        } while (before != (after = this.lvConsumerIndex()));
        long size = currentProducerIndex - after >> 1;
        if (size > Integer.MAX_VALUE) {
            return Integer.MAX_VALUE;
        }
        return (int)size;
    }

    @Override
    public final boolean isEmpty() {
        return this.lvConsumerIndex() == this.lvProducerIndex();
    }

    @Override
    public String toString() {
        return this.getClass().getName();
    }

    @Override
    public boolean offer(E e) {
        long pIndex;
        AtomicReferenceArray buffer;
        long mask;
        if (null == e) {
            throw new NullPointerException();
        }
        block6 : do {
            long producerLimit = this.lvProducerLimit();
            pIndex = this.lvProducerIndex();
            if ((pIndex & 1L) == 1L) continue;
            mask = this.producerMask;
            buffer = this.producerBuffer;
            if (producerLimit <= pIndex) {
                int result = this.offerSlowPath(mask, pIndex, producerLimit);
                switch (result) {
                    case 0: {
                        break;
                    }
                    case 1: {
                        continue block6;
                    }
                    case 2: {
                        return false;
                    }
                    case 3: {
                        this.resize(mask, buffer, pIndex, e);
                        return true;
                    }
                }
            }
            if (this.casProducerIndex(pIndex, pIndex + 2L)) break;
        } while (true);
        int offset = LinkedAtomicArrayQueueUtil.modifiedCalcElementOffset(pIndex, mask);
        LinkedAtomicArrayQueueUtil.soElement(buffer, offset, e);
        return true;
    }

    @Override
    public E poll() {
        AtomicReferenceArray buffer = this.consumerBuffer;
        long index = this.consumerIndex;
        long mask = this.consumerMask;
        int offset = LinkedAtomicArrayQueueUtil.modifiedCalcElementOffset(index, mask);
        Object e = LinkedAtomicArrayQueueUtil.lvElement(buffer, offset);
        if (e == null) {
            if (index != this.lvProducerIndex()) {
                while ((e = LinkedAtomicArrayQueueUtil.lvElement(buffer, offset)) == null) {
                }
            } else {
                return null;
            }
        }
        if (e == JUMP) {
            AtomicReferenceArray<E> nextBuffer = this.getNextBuffer(buffer, mask);
            return this.newBufferPoll(nextBuffer, index);
        }
        LinkedAtomicArrayQueueUtil.soElement(buffer, offset, null);
        this.soConsumerIndex(index + 2L);
        return e;
    }

    @Override
    public E peek() {
        AtomicReferenceArray buffer = this.consumerBuffer;
        long index = this.consumerIndex;
        long mask = this.consumerMask;
        int offset = LinkedAtomicArrayQueueUtil.modifiedCalcElementOffset(index, mask);
        Object e = LinkedAtomicArrayQueueUtil.lvElement(buffer, offset);
        if (e == null && index != this.lvProducerIndex()) {
            while ((e = LinkedAtomicArrayQueueUtil.lvElement(buffer, offset)) == null) {
            }
        }
        if (e == JUMP) {
            return this.newBufferPeek(this.getNextBuffer(buffer, mask), index);
        }
        return e;
    }

    private int offerSlowPath(long mask, long pIndex, long producerLimit) {
        long cIndex = this.lvConsumerIndex();
        long bufferCapacity = this.getCurrentBufferCapacity(mask);
        int result = 0;
        if (cIndex + bufferCapacity > pIndex) {
            if (!this.casProducerLimit(producerLimit, cIndex + bufferCapacity)) {
                result = 1;
            }
        } else {
            result = this.availableInQueue(pIndex, cIndex) <= 0L ? 2 : (this.casProducerIndex(pIndex, pIndex + 1L) ? 3 : 1);
        }
        return result;
    }

    protected abstract long availableInQueue(long var1, long var3);

    private AtomicReferenceArray<E> getNextBuffer(AtomicReferenceArray<E> buffer, long mask) {
        int offset = this.nextArrayOffset(mask);
        AtomicReferenceArray nextBuffer = (AtomicReferenceArray)LinkedAtomicArrayQueueUtil.lvElement(buffer, offset);
        LinkedAtomicArrayQueueUtil.soElement(buffer, offset, null);
        return nextBuffer;
    }

    private int nextArrayOffset(long mask) {
        return LinkedAtomicArrayQueueUtil.modifiedCalcElementOffset(mask + 2L, Long.MAX_VALUE);
    }

    private E newBufferPoll(AtomicReferenceArray<E> nextBuffer, long index) {
        int offset = this.newBufferAndOffset(nextBuffer, index);
        E n = LinkedAtomicArrayQueueUtil.lvElement(nextBuffer, offset);
        if (n == null) {
            throw new IllegalStateException("new buffer must have at least one element");
        }
        LinkedAtomicArrayQueueUtil.soElement(nextBuffer, offset, null);
        this.soConsumerIndex(index + 2L);
        return n;
    }

    private E newBufferPeek(AtomicReferenceArray<E> nextBuffer, long index) {
        int offset = this.newBufferAndOffset(nextBuffer, index);
        E n = LinkedAtomicArrayQueueUtil.lvElement(nextBuffer, offset);
        if (null == n) {
            throw new IllegalStateException("new buffer must have at least one element");
        }
        return n;
    }

    private int newBufferAndOffset(AtomicReferenceArray<E> nextBuffer, long index) {
        this.consumerBuffer = nextBuffer;
        this.consumerMask = LinkedAtomicArrayQueueUtil.length(nextBuffer) - 2 << 1;
        int offsetInNew = LinkedAtomicArrayQueueUtil.modifiedCalcElementOffset(index, this.consumerMask);
        return offsetInNew;
    }

    @Override
    public long currentProducerIndex() {
        return this.lvProducerIndex() / 2L;
    }

    @Override
    public long currentConsumerIndex() {
        return this.lvConsumerIndex() / 2L;
    }

    @Override
    public abstract int capacity();

    @Override
    public boolean relaxedOffer(E e) {
        return this.offer(e);
    }

    @Override
    public E relaxedPoll() {
        AtomicReferenceArray buffer = this.consumerBuffer;
        long index = this.consumerIndex;
        long mask = this.consumerMask;
        int offset = LinkedAtomicArrayQueueUtil.modifiedCalcElementOffset(index, mask);
        E e = LinkedAtomicArrayQueueUtil.lvElement(buffer, offset);
        if (e == null) {
            return null;
        }
        if (e == JUMP) {
            AtomicReferenceArray<E> nextBuffer = this.getNextBuffer(buffer, mask);
            return this.newBufferPoll(nextBuffer, index);
        }
        LinkedAtomicArrayQueueUtil.soElement(buffer, offset, null);
        this.soConsumerIndex(index + 2L);
        return e;
    }

    @Override
    public E relaxedPeek() {
        AtomicReferenceArray buffer = this.consumerBuffer;
        long index = this.consumerIndex;
        long mask = this.consumerMask;
        int offset = LinkedAtomicArrayQueueUtil.modifiedCalcElementOffset(index, mask);
        E e = LinkedAtomicArrayQueueUtil.lvElement(buffer, offset);
        if (e == JUMP) {
            return this.newBufferPeek(this.getNextBuffer(buffer, mask), index);
        }
        return e;
    }

    @Override
    public int fill(MessagePassingQueue.Supplier<E> s) {
        int filled;
        long result = 0L;
        int capacity = this.capacity();
        do {
            if ((filled = this.fill(s, PortableJvmInfo.RECOMENDED_OFFER_BATCH)) != 0) continue;
            return (int)result;
        } while ((result += (long)filled) <= (long)capacity);
        return (int)result;
    }

    @Override
    public int fill(MessagePassingQueue.Supplier<E> s, int batchSize) {
        long pIndex;
        long batchIndex;
        AtomicReferenceArray buffer;
        long mask;
        block5 : do {
            long producerLimit = this.lvProducerLimit();
            pIndex = this.lvProducerIndex();
            if ((pIndex & 1L) == 1L) continue;
            mask = this.producerMask;
            buffer = this.producerBuffer;
            batchIndex = Math.min(producerLimit, pIndex + (long)(2 * batchSize));
            if (pIndex == producerLimit || producerLimit < batchIndex) {
                int result = this.offerSlowPath(mask, pIndex, producerLimit);
                switch (result) {
                    case 1: {
                        continue block5;
                    }
                    case 2: {
                        return 0;
                    }
                    case 3: {
                        this.resize(mask, buffer, pIndex, s.get());
                        return 1;
                    }
                }
            }
            if (this.casProducerIndex(pIndex, batchIndex)) break;
        } while (true);
        int claimedSlots = (int)((batchIndex - pIndex) / 2L);
        int i = 0;
        for (i = 0; i < claimedSlots; ++i) {
            int offset = LinkedAtomicArrayQueueUtil.modifiedCalcElementOffset(pIndex + (long)(2 * i), mask);
            LinkedAtomicArrayQueueUtil.soElement(buffer, offset, s.get());
        }
        return claimedSlots;
    }

    @Override
    public void fill(MessagePassingQueue.Supplier<E> s, MessagePassingQueue.WaitStrategy w, MessagePassingQueue.ExitCondition exit) {
        while (exit.keepRunning()) {
            while (this.fill(s, PortableJvmInfo.RECOMENDED_OFFER_BATCH) != 0 && exit.keepRunning()) {
            }
            int idleCounter = 0;
            while (exit.keepRunning() && this.fill(s, PortableJvmInfo.RECOMENDED_OFFER_BATCH) == 0) {
                idleCounter = w.idle(idleCounter);
            }
        }
    }

    @Override
    public int drain(MessagePassingQueue.Consumer<E> c) {
        return this.drain(c, this.capacity());
    }

    @Override
    public int drain(MessagePassingQueue.Consumer<E> c, int limit) {
        int i;
        E m;
        for (i = 0; i < limit && (m = this.relaxedPoll()) != null; ++i) {
            c.accept(m);
        }
        return i;
    }

    @Override
    public void drain(MessagePassingQueue.Consumer<E> c, MessagePassingQueue.WaitStrategy w, MessagePassingQueue.ExitCondition exit) {
        int idleCounter = 0;
        while (exit.keepRunning()) {
            E e = this.relaxedPoll();
            if (e == null) {
                idleCounter = w.idle(idleCounter);
                continue;
            }
            idleCounter = 0;
            c.accept(e);
        }
    }

    private void resize(long oldMask, AtomicReferenceArray<E> oldBuffer, long pIndex, E e) {
        AtomicReferenceArray<E> newBuffer;
        int newBufferLength = this.getNextBufferSize(oldBuffer);
        this.producerBuffer = newBuffer = LinkedAtomicArrayQueueUtil.allocate(newBufferLength);
        int newMask = newBufferLength - 2 << 1;
        this.producerMask = newMask;
        int offsetInOld = LinkedAtomicArrayQueueUtil.modifiedCalcElementOffset(pIndex, oldMask);
        int offsetInNew = LinkedAtomicArrayQueueUtil.modifiedCalcElementOffset(pIndex, newMask);
        LinkedAtomicArrayQueueUtil.soElement(newBuffer, offsetInNew, e);
        LinkedAtomicArrayQueueUtil.soElement(oldBuffer, this.nextArrayOffset(oldMask), newBuffer);
        long cIndex = this.lvConsumerIndex();
        long availableInQueue = this.availableInQueue(pIndex, cIndex);
        RangeUtil.checkPositive(availableInQueue, "availableInQueue");
        this.soProducerLimit(pIndex + Math.min((long)newMask, availableInQueue));
        this.soProducerIndex(pIndex + 2L);
        LinkedAtomicArrayQueueUtil.soElement(oldBuffer, offsetInOld, JUMP);
    }

    protected abstract int getNextBufferSize(AtomicReferenceArray<E> var1);

    protected abstract long getCurrentBufferCapacity(long var1);
}

