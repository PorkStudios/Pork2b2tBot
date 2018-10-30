/*
 * Decompiled with CFR 0_132.
 */
package io.netty.util.internal.shaded.org.jctools.queues;

import io.netty.util.internal.shaded.org.jctools.queues.BaseMpscLinkedArrayQueue;
import io.netty.util.internal.shaded.org.jctools.queues.LinkedArrayQueueUtil;
import io.netty.util.internal.shaded.org.jctools.queues.MessagePassingQueue;
import io.netty.util.internal.shaded.org.jctools.util.PortableJvmInfo;

public class MpscUnboundedArrayQueue<E>
extends BaseMpscLinkedArrayQueue<E> {
    long p0;
    long p1;
    long p2;
    long p3;
    long p4;
    long p5;
    long p6;
    long p7;
    long p10;
    long p11;
    long p12;
    long p13;
    long p14;
    long p15;
    long p16;
    long p17;

    public MpscUnboundedArrayQueue(int chunkSize) {
        super(chunkSize);
    }

    @Override
    protected long availableInQueue(long pIndex, long cIndex) {
        return Integer.MAX_VALUE;
    }

    @Override
    public int capacity() {
        return -1;
    }

    @Override
    public int drain(MessagePassingQueue.Consumer<E> c) {
        return this.drain(c, 4096);
    }

    @Override
    public int fill(MessagePassingQueue.Supplier<E> s) {
        int filled;
        long result = 0L;
        int capacity = 4096;
        do {
            if ((filled = this.fill(s, PortableJvmInfo.RECOMENDED_OFFER_BATCH)) != 0) continue;
            return (int)result;
        } while ((result += (long)filled) <= 4096L);
        return (int)result;
    }

    @Override
    protected int getNextBufferSize(E[] buffer) {
        return LinkedArrayQueueUtil.length(buffer);
    }

    @Override
    protected long getCurrentBufferCapacity(long mask) {
        return mask;
    }
}

