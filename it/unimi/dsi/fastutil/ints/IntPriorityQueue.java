/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.ints;

import it.unimi.dsi.fastutil.PriorityQueue;
import it.unimi.dsi.fastutil.ints.IntComparator;
import java.util.Comparator;

public interface IntPriorityQueue
extends PriorityQueue<Integer> {
    @Override
    public void enqueue(int var1);

    public int dequeueInt();

    public int firstInt();

    default public int lastInt() {
        throw new UnsupportedOperationException();
    }

    public IntComparator comparator();

    @Deprecated
    @Override
    default public void enqueue(Integer x) {
        this.enqueue((int)x);
    }

    @Deprecated
    @Override
    default public Integer dequeue() {
        return this.dequeueInt();
    }

    @Deprecated
    @Override
    default public Integer first() {
        return this.firstInt();
    }

    @Deprecated
    @Override
    default public Integer last() {
        return this.lastInt();
    }
}

