/*
 * Decompiled with CFR 0_132.
 */
package io.netty.util.internal.shaded.org.jctools.queues.atomic;

import io.netty.util.internal.shaded.org.jctools.queues.atomic.BaseMpscLinkedAtomicArrayQueue;
import io.netty.util.internal.shaded.org.jctools.util.Pow2;
import io.netty.util.internal.shaded.org.jctools.util.RangeUtil;

abstract class MpscChunkedAtomicArrayQueueColdProducerFields<E>
extends BaseMpscLinkedAtomicArrayQueue<E> {
    protected final long maxQueueCapacity;

    public MpscChunkedAtomicArrayQueueColdProducerFields(int initialCapacity, int maxCapacity) {
        super(initialCapacity);
        RangeUtil.checkGreaterThanOrEqual(maxCapacity, 4, "maxCapacity");
        RangeUtil.checkLessThan(Pow2.roundToPowerOfTwo(initialCapacity), Pow2.roundToPowerOfTwo(maxCapacity), "initialCapacity");
        this.maxQueueCapacity = (long)Pow2.roundToPowerOfTwo(maxCapacity) << 1;
    }
}

