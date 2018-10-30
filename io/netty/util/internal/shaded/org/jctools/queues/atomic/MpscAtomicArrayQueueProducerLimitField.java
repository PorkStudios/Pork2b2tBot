/*
 * Decompiled with CFR 0_132.
 */
package io.netty.util.internal.shaded.org.jctools.queues.atomic;

import io.netty.util.internal.shaded.org.jctools.queues.atomic.MpscAtomicArrayQueueMidPad;
import java.util.concurrent.atomic.AtomicLongFieldUpdater;

abstract class MpscAtomicArrayQueueProducerLimitField<E>
extends MpscAtomicArrayQueueMidPad<E> {
    private static final AtomicLongFieldUpdater<MpscAtomicArrayQueueProducerLimitField> P_LIMIT_UPDATER = AtomicLongFieldUpdater.newUpdater(MpscAtomicArrayQueueProducerLimitField.class, "producerLimit");
    private volatile long producerLimit;

    public MpscAtomicArrayQueueProducerLimitField(int capacity) {
        super(capacity);
        this.producerLimit = capacity;
    }

    protected final long lvProducerLimit() {
        return this.producerLimit;
    }

    protected final void soProducerLimit(long newValue) {
        P_LIMIT_UPDATER.lazySet(this, newValue);
    }
}

