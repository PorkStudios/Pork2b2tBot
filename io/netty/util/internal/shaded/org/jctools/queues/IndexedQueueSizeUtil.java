/*
 * Decompiled with CFR 0_132.
 */
package io.netty.util.internal.shaded.org.jctools.queues;

public final class IndexedQueueSizeUtil {
    public static int size(IndexedQueue iq) {
        long before;
        long currentProducerIndex;
        long after = iq.lvConsumerIndex();
        do {
            before = after;
            currentProducerIndex = iq.lvProducerIndex();
        } while (before != (after = iq.lvConsumerIndex()));
        long size = currentProducerIndex - after;
        if (size > Integer.MAX_VALUE) {
            return Integer.MAX_VALUE;
        }
        return (int)size;
    }

    public static boolean isEmpty(IndexedQueue iq) {
        return iq.lvConsumerIndex() == iq.lvProducerIndex();
    }

    public static interface IndexedQueue {
        public long lvConsumerIndex();

        public long lvProducerIndex();
    }

}

