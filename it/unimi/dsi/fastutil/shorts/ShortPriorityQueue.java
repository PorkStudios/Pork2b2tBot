/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.shorts;

import it.unimi.dsi.fastutil.PriorityQueue;
import it.unimi.dsi.fastutil.shorts.ShortComparator;
import java.util.Comparator;

public interface ShortPriorityQueue
extends PriorityQueue<Short> {
    @Override
    public void enqueue(short var1);

    public short dequeueShort();

    public short firstShort();

    default public short lastShort() {
        throw new UnsupportedOperationException();
    }

    public ShortComparator comparator();

    @Deprecated
    @Override
    default public void enqueue(Short x) {
        this.enqueue((short)x);
    }

    @Deprecated
    @Override
    default public Short dequeue() {
        return this.dequeueShort();
    }

    @Deprecated
    @Override
    default public Short first() {
        return this.firstShort();
    }

    @Deprecated
    @Override
    default public Short last() {
        return this.lastShort();
    }
}

