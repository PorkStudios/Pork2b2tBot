/*
 * Decompiled with CFR 0_132.
 */
package io.netty.util.internal.shaded.org.jctools.queues.atomic;

import io.netty.util.internal.shaded.org.jctools.queues.atomic.MpscAtomicArrayQueueL2Pad;
import java.util.concurrent.atomic.AtomicLongFieldUpdater;

abstract class MpscAtomicArrayQueueConsumerIndexField<E>
extends MpscAtomicArrayQueueL2Pad<E> {
    private static final AtomicLongFieldUpdater<MpscAtomicArrayQueueConsumerIndexField> C_INDEX_UPDATER = AtomicLongFieldUpdater.newUpdater(MpscAtomicArrayQueueConsumerIndexField.class, "consumerIndex");
    protected volatile long consumerIndex;

    public MpscAtomicArrayQueueConsumerIndexField(int capacity) {
        super(capacity);
    }

    protected final long lpConsumerIndex() {
        return this.consumerIndex;
    }

    @Override
    public final long lvConsumerIndex() {
        return this.consumerIndex;
    }

    protected void soConsumerIndex(long newValue) {
        C_INDEX_UPDATER.lazySet(this, newValue);
    }
}

