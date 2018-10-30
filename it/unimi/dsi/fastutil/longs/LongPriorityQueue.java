/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.PriorityQueue;
import it.unimi.dsi.fastutil.longs.LongComparator;
import java.util.Comparator;

public interface LongPriorityQueue
extends PriorityQueue<Long> {
    @Override
    public void enqueue(long var1);

    public long dequeueLong();

    public long firstLong();

    default public long lastLong() {
        throw new UnsupportedOperationException();
    }

    public LongComparator comparator();

    @Deprecated
    @Override
    default public void enqueue(Long x) {
        this.enqueue((long)x);
    }

    @Deprecated
    @Override
    default public Long dequeue() {
        return this.dequeueLong();
    }

    @Deprecated
    @Override
    default public Long first() {
        return this.firstLong();
    }

    @Deprecated
    @Override
    default public Long last() {
        return this.lastLong();
    }
}

