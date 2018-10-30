/*
 * Decompiled with CFR 0_132.
 */
package io.netty.util.internal.shaded.org.jctools.queues.atomic;

import io.netty.util.internal.shaded.org.jctools.queues.atomic.LinkedAtomicArrayQueueUtil;
import io.netty.util.internal.shaded.org.jctools.queues.atomic.MpscChunkedAtomicArrayQueue;
import io.netty.util.internal.shaded.org.jctools.util.Pow2;
import io.netty.util.internal.shaded.org.jctools.util.RangeUtil;
import java.util.concurrent.atomic.AtomicReferenceArray;

public class MpscGrowableAtomicArrayQueue<E>
extends MpscChunkedAtomicArrayQueue<E> {
    public MpscGrowableAtomicArrayQueue(int maxCapacity) {
        super(Math.max(2, Pow2.roundToPowerOfTwo(maxCapacity / 8)), maxCapacity);
    }

    public MpscGrowableAtomicArrayQueue(int initialCapacity, int maxCapacity) {
        super(initialCapacity, maxCapacity);
    }

    @Override
    protected int getNextBufferSize(AtomicReferenceArray<E> buffer) {
        long maxSize = this.maxQueueCapacity / 2L;
        RangeUtil.checkLessThanOrEqual(LinkedAtomicArrayQueueUtil.length(buffer), maxSize, "buffer.length");
        int newSize = 2 * (LinkedAtomicArrayQueueUtil.length(buffer) - 1);
        return newSize + 1;
    }

    @Override
    protected long getCurrentBufferCapacity(long mask) {
        return mask + 2L == this.maxQueueCapacity ? this.maxQueueCapacity : mask;
    }
}

