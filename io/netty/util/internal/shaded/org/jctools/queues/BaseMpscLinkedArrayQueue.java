/*
 * Decompiled with CFR 0_132.
 */
package io.netty.util.internal.shaded.org.jctools.queues;

import io.netty.util.internal.shaded.org.jctools.queues.BaseMpscLinkedArrayQueueColdProducerFields;
import io.netty.util.internal.shaded.org.jctools.queues.CircularArrayOffsetCalculator;
import io.netty.util.internal.shaded.org.jctools.queues.LinkedArrayQueueUtil;
import io.netty.util.internal.shaded.org.jctools.queues.MessagePassingQueue;
import io.netty.util.internal.shaded.org.jctools.queues.QueueProgressIndicators;
import io.netty.util.internal.shaded.org.jctools.util.PortableJvmInfo;
import io.netty.util.internal.shaded.org.jctools.util.Pow2;
import io.netty.util.internal.shaded.org.jctools.util.RangeUtil;
import io.netty.util.internal.shaded.org.jctools.util.UnsafeRefArrayAccess;
import java.util.Iterator;

public abstract class BaseMpscLinkedArrayQueue<E>
extends BaseMpscLinkedArrayQueueColdProducerFields<E>
implements MessagePassingQueue<E>,
QueueProgressIndicators {
    private static final Object JUMP = new Object();
    private static final int CONTINUE_TO_P_INDEX_CAS = 0;
    private static final int RETRY = 1;
    private static final int QUEUE_FULL = 2;
    private static final int QUEUE_RESIZE = 3;

    public BaseMpscLinkedArrayQueue(int initialCapacity) {
        RangeUtil.checkGreaterThanOrEqual(initialCapacity, 2, "initialCapacity");
        int p2capacity = Pow2.roundToPowerOfTwo(initialCapacity);
        long mask = p2capacity - 1 << 1;
        E[] buffer = CircularArrayOffsetCalculator.allocate(p2capacity + 1);
        this.producerBuffer = buffer;
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
        Object[] buffer;
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
        long offset = LinkedArrayQueueUtil.modifiedCalcElementOffset(pIndex, mask);
        UnsafeRefArrayAccess.soElement(buffer, offset, e);
        return true;
    }

    @Override
    public E poll() {
        Object[] buffer = this.consumerBuffer;
        long index = this.consumerIndex;
        long mask = this.consumerMask;
        long offset = LinkedArrayQueueUtil.modifiedCalcElementOffset(index, mask);
        Object e = UnsafeRefArrayAccess.lvElement(buffer, offset);
        if (e == null) {
            if (index != this.lvProducerIndex()) {
                while ((e = UnsafeRefArrayAccess.lvElement(buffer, offset)) == null) {
                }
            } else {
                return null;
            }
        }
        if (e == JUMP) {
            Object[] nextBuffer = this.getNextBuffer(buffer, mask);
            return (E)this.newBufferPoll(nextBuffer, index);
        }
        UnsafeRefArrayAccess.soElement(buffer, offset, null);
        this.soConsumerIndex(index + 2L);
        return (E)e;
    }

    @Override
    public E peek() {
        Object[] buffer = this.consumerBuffer;
        long index = this.consumerIndex;
        long mask = this.consumerMask;
        long offset = LinkedArrayQueueUtil.modifiedCalcElementOffset(index, mask);
        Object e = UnsafeRefArrayAccess.lvElement(buffer, offset);
        if (e == null && index != this.lvProducerIndex()) {
            while ((e = UnsafeRefArrayAccess.lvElement(buffer, offset)) == null) {
            }
        }
        if (e == JUMP) {
            return (E)this.newBufferPeek(this.getNextBuffer(buffer, mask), index);
        }
        return (E)e;
    }

    private int offerSlowPath(long mask, long pIndex, long producerLimit) {
        long bufferCapacity;
        long cIndex = this.lvConsumerIndex();
        if (cIndex + (bufferCapacity = this.getCurrentBufferCapacity(mask)) > pIndex) {
            if (!this.casProducerLimit(producerLimit, cIndex + bufferCapacity)) {
                return 1;
            }
            return 0;
        }
        if (this.availableInQueue(pIndex, cIndex) <= 0L) {
            return 2;
        }
        if (this.casProducerIndex(pIndex, pIndex + 1L)) {
            return 3;
        }
        return 1;
    }

    protected abstract long availableInQueue(long var1, long var3);

    private E[] getNextBuffer(E[] buffer, long mask) {
        long offset = this.nextArrayOffset(mask);
        Object[] nextBuffer = (Object[])UnsafeRefArrayAccess.lvElement(buffer, offset);
        UnsafeRefArrayAccess.soElement(buffer, offset, null);
        return nextBuffer;
    }

    private long nextArrayOffset(long mask) {
        return LinkedArrayQueueUtil.modifiedCalcElementOffset(mask + 2L, Long.MAX_VALUE);
    }

    private E newBufferPoll(E[] nextBuffer, long index) {
        long offset = this.newBufferAndOffset(nextBuffer, index);
        E n = UnsafeRefArrayAccess.lvElement(nextBuffer, offset);
        if (n == null) {
            throw new IllegalStateException("new buffer must have at least one element");
        }
        UnsafeRefArrayAccess.soElement(nextBuffer, offset, null);
        this.soConsumerIndex(index + 2L);
        return n;
    }

    private E newBufferPeek(E[] nextBuffer, long index) {
        long offset = this.newBufferAndOffset(nextBuffer, index);
        E n = UnsafeRefArrayAccess.lvElement(nextBuffer, offset);
        if (null == n) {
            throw new IllegalStateException("new buffer must have at least one element");
        }
        return n;
    }

    private long newBufferAndOffset(E[] nextBuffer, long index) {
        this.consumerBuffer = nextBuffer;
        this.consumerMask = LinkedArrayQueueUtil.length(nextBuffer) - 2 << 1;
        return LinkedArrayQueueUtil.modifiedCalcElementOffset(index, this.consumerMask);
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
        Object[] buffer = this.consumerBuffer;
        long index = this.consumerIndex;
        long mask = this.consumerMask;
        long offset = LinkedArrayQueueUtil.modifiedCalcElementOffset(index, mask);
        Object e = UnsafeRefArrayAccess.lvElement(buffer, offset);
        if (e == null) {
            return null;
        }
        if (e == JUMP) {
            Object[] nextBuffer = this.getNextBuffer(buffer, mask);
            return (E)this.newBufferPoll(nextBuffer, index);
        }
        UnsafeRefArrayAccess.soElement(buffer, offset, null);
        this.soConsumerIndex(index + 2L);
        return (E)e;
    }

    @Override
    public E relaxedPeek() {
        Object[] buffer = this.consumerBuffer;
        long index = this.consumerIndex;
        long mask = this.consumerMask;
        long offset = LinkedArrayQueueUtil.modifiedCalcElementOffset(index, mask);
        Object e = UnsafeRefArrayAccess.lvElement(buffer, offset);
        if (e == JUMP) {
            return (E)this.newBufferPeek(this.getNextBuffer(buffer, mask), index);
        }
        return (E)e;
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
        Object[] buffer;
        long mask;
        block5 : do {
            long producerLimit = this.lvProducerLimit();
            pIndex = this.lvProducerIndex();
            if ((pIndex & 1L) == 1L) continue;
            mask = this.producerMask;
            buffer = this.producerBuffer;
            batchIndex = Math.min(producerLimit, pIndex + (long)(2 * batchSize));
            if (pIndex >= producerLimit || producerLimit < batchIndex) {
                int result = this.offerSlowPath(mask, pIndex, producerLimit);
                switch (result) {
                    case 0: 
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
        for (int i = 0; i < claimedSlots; ++i) {
            long offset = LinkedArrayQueueUtil.modifiedCalcElementOffset(pIndex + (long)(2 * i), mask);
            UnsafeRefArrayAccess.soElement(buffer, offset, s.get());
        }
        return claimedSlots;
    }

    @Override
    public void fill(MessagePassingQueue.Supplier<E> s, MessagePassingQueue.WaitStrategy w, MessagePassingQueue.ExitCondition exit) {
        while (exit.keepRunning()) {
            if (this.fill(s, PortableJvmInfo.RECOMENDED_OFFER_BATCH) != 0) continue;
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

    private void resize(long oldMask, E[] oldBuffer, long pIndex, E e) {
        int newBufferLength = this.getNextBufferSize(oldBuffer);
        E[] newBuffer = CircularArrayOffsetCalculator.allocate(newBufferLength);
        this.producerBuffer = newBuffer;
        int newMask = newBufferLength - 2 << 1;
        this.producerMask = newMask;
        long offsetInOld = LinkedArrayQueueUtil.modifiedCalcElementOffset(pIndex, oldMask);
        long offsetInNew = LinkedArrayQueueUtil.modifiedCalcElementOffset(pIndex, newMask);
        UnsafeRefArrayAccess.soElement(newBuffer, offsetInNew, e);
        UnsafeRefArrayAccess.soElement(oldBuffer, this.nextArrayOffset(oldMask), newBuffer);
        long cIndex = this.lvConsumerIndex();
        long availableInQueue = this.availableInQueue(pIndex, cIndex);
        RangeUtil.checkPositive(availableInQueue, "availableInQueue");
        this.soProducerLimit(pIndex + Math.min((long)newMask, availableInQueue));
        this.soProducerIndex(pIndex + 2L);
        UnsafeRefArrayAccess.soElement(oldBuffer, offsetInOld, JUMP);
    }

    protected abstract int getNextBufferSize(E[] var1);

    protected abstract long getCurrentBufferCapacity(long var1);
}

