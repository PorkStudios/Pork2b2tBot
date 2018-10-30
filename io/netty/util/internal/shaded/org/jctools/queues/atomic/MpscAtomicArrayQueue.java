/*
 * Decompiled with CFR 0_132.
 */
package io.netty.util.internal.shaded.org.jctools.queues.atomic;

import io.netty.util.internal.shaded.org.jctools.queues.MessagePassingQueue;
import io.netty.util.internal.shaded.org.jctools.queues.atomic.MpscAtomicArrayQueueL3Pad;
import io.netty.util.internal.shaded.org.jctools.util.PortableJvmInfo;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicReferenceArray;

public class MpscAtomicArrayQueue<E>
extends MpscAtomicArrayQueueL3Pad<E> {
    public MpscAtomicArrayQueue(int capacity) {
        super(capacity);
    }

    public boolean offerIfBelowThreshold(E e, int threshold) {
        long pIndex;
        if (null == e) {
            throw new NullPointerException();
        }
        int mask = this.mask;
        long capacity = mask + 1;
        long producerLimit = this.lvProducerLimit();
        do {
            long size;
            long available;
            if ((size = capacity - (available = producerLimit - (pIndex = this.lvProducerIndex()))) < (long)threshold) continue;
            long cIndex = this.lvConsumerIndex();
            size = pIndex - cIndex;
            if (size >= (long)threshold) {
                return false;
            }
            producerLimit = cIndex + capacity;
            this.soProducerLimit(producerLimit);
        } while (!this.casProducerIndex(pIndex, pIndex + 1L));
        int offset = this.calcElementOffset(pIndex, mask);
        MpscAtomicArrayQueue.soElement(this.buffer, offset, e);
        return true;
    }

    @Override
    public boolean offer(E e) {
        long pIndex;
        if (null == e) {
            throw new NullPointerException();
        }
        int mask = this.mask;
        long producerLimit = this.lvProducerLimit();
        do {
            if ((pIndex = this.lvProducerIndex()) < producerLimit) continue;
            long cIndex = this.lvConsumerIndex();
            producerLimit = cIndex + (long)mask + 1L;
            if (pIndex >= producerLimit) {
                return false;
            }
            this.soProducerLimit(producerLimit);
        } while (!this.casProducerIndex(pIndex, pIndex + 1L));
        int offset = this.calcElementOffset(pIndex, mask);
        MpscAtomicArrayQueue.soElement(this.buffer, offset, e);
        return true;
    }

    public final int failFastOffer(E e) {
        long producerLimit;
        if (null == e) {
            throw new NullPointerException();
        }
        int mask = this.mask;
        long capacity = mask + 1;
        long pIndex = this.lvProducerIndex();
        if (pIndex >= (producerLimit = this.lvProducerLimit())) {
            long cIndex = this.lvConsumerIndex();
            producerLimit = cIndex + capacity;
            if (pIndex >= producerLimit) {
                return 1;
            }
            this.soProducerLimit(producerLimit);
        }
        if (!this.casProducerIndex(pIndex, pIndex + 1L)) {
            return -1;
        }
        int offset = this.calcElementOffset(pIndex, mask);
        MpscAtomicArrayQueue.soElement(this.buffer, offset, e);
        return 0;
    }

    @Override
    public E poll() {
        AtomicReferenceArray buffer = this.buffer;
        long cIndex = this.lpConsumerIndex();
        int offset = this.calcElementOffset(cIndex);
        Object e = MpscAtomicArrayQueue.lvElement(buffer, offset);
        if (null == e) {
            if (cIndex != this.lvProducerIndex()) {
                while ((e = MpscAtomicArrayQueue.lvElement(buffer, offset)) == null) {
                }
            } else {
                return null;
            }
        }
        MpscAtomicArrayQueue.spElement(buffer, offset, null);
        this.soConsumerIndex(cIndex + 1L);
        return e;
    }

    @Override
    public E peek() {
        AtomicReferenceArray buffer = this.buffer;
        long cIndex = this.lpConsumerIndex();
        int offset = this.calcElementOffset(cIndex);
        Object e = MpscAtomicArrayQueue.lvElement(buffer, offset);
        if (null == e) {
            if (cIndex != this.lvProducerIndex()) {
                while ((e = MpscAtomicArrayQueue.lvElement(buffer, offset)) == null) {
                }
            } else {
                return null;
            }
        }
        return e;
    }

    @Override
    public boolean relaxedOffer(E e) {
        return this.offer(e);
    }

    @Override
    public E relaxedPoll() {
        AtomicReferenceArray buffer = this.buffer;
        long cIndex = this.lpConsumerIndex();
        int offset = this.calcElementOffset(cIndex);
        Object e = MpscAtomicArrayQueue.lvElement(buffer, offset);
        if (null == e) {
            return null;
        }
        MpscAtomicArrayQueue.spElement(buffer, offset, null);
        this.soConsumerIndex(cIndex + 1L);
        return e;
    }

    @Override
    public E relaxedPeek() {
        AtomicReferenceArray buffer = this.buffer;
        int mask = this.mask;
        long cIndex = this.lpConsumerIndex();
        return MpscAtomicArrayQueue.lvElement(buffer, this.calcElementOffset(cIndex, mask));
    }

    @Override
    public int drain(MessagePassingQueue.Consumer<E> c) {
        return this.drain(c, this.capacity());
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
    public int drain(MessagePassingQueue.Consumer<E> c, int limit) {
        AtomicReferenceArray buffer = this.buffer;
        int mask = this.mask;
        long cIndex = this.lpConsumerIndex();
        for (int i = 0; i < limit; ++i) {
            long index = cIndex + (long)i;
            int offset = this.calcElementOffset(index, mask);
            Object e = MpscAtomicArrayQueue.lvElement(buffer, offset);
            if (null == e) {
                return i;
            }
            MpscAtomicArrayQueue.spElement(buffer, offset, null);
            this.soConsumerIndex(index + 1L);
            c.accept(e);
        }
        return limit;
    }

    @Override
    public int fill(MessagePassingQueue.Supplier<E> s, int limit) {
        long pIndex;
        long available;
        int mask = this.mask;
        long capacity = mask + 1;
        long producerLimit = this.lvProducerLimit();
        int actualLimit = 0;
        do {
            if ((available = producerLimit - (pIndex = this.lvProducerIndex())) > 0L) continue;
            long cIndex = this.lvConsumerIndex();
            producerLimit = cIndex + capacity;
            available = producerLimit - pIndex;
            if (available <= 0L) {
                return 0;
            }
            this.soProducerLimit(producerLimit);
        } while (!this.casProducerIndex(pIndex, pIndex + (long)(actualLimit = Math.min((int)available, limit))));
        AtomicReferenceArray buffer = this.buffer;
        for (int i = 0; i < actualLimit; ++i) {
            int offset = this.calcElementOffset(pIndex + (long)i, mask);
            MpscAtomicArrayQueue.soElement(buffer, offset, s.get());
        }
        return actualLimit;
    }

    @Override
    public void drain(MessagePassingQueue.Consumer<E> c, MessagePassingQueue.WaitStrategy w, MessagePassingQueue.ExitCondition exit) {
        AtomicReferenceArray buffer = this.buffer;
        int mask = this.mask;
        long cIndex = this.lpConsumerIndex();
        int counter = 0;
        while (exit.keepRunning()) {
            for (int i = 0; i < 4096; ++i) {
                int offset = this.calcElementOffset(cIndex, mask);
                Object e = MpscAtomicArrayQueue.lvElement(buffer, offset);
                if (null == e) {
                    counter = w.idle(counter);
                    continue;
                }
                counter = 0;
                MpscAtomicArrayQueue.spElement(buffer, offset, null);
                this.soConsumerIndex(++cIndex);
                c.accept(e);
            }
        }
    }

    @Override
    public void fill(MessagePassingQueue.Supplier<E> s, MessagePassingQueue.WaitStrategy w, MessagePassingQueue.ExitCondition exit) {
        int idleCounter = 0;
        while (exit.keepRunning()) {
            if (this.fill(s, PortableJvmInfo.RECOMENDED_OFFER_BATCH) == 0) {
                idleCounter = w.idle(idleCounter);
                continue;
            }
            idleCounter = 0;
        }
    }

    @Deprecated
    public int weakOffer(E e) {
        return this.failFastOffer(e);
    }
}

