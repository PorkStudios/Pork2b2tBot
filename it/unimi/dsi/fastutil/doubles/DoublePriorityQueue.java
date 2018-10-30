/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.doubles;

import it.unimi.dsi.fastutil.PriorityQueue;
import it.unimi.dsi.fastutil.doubles.DoubleComparator;
import java.util.Comparator;

public interface DoublePriorityQueue
extends PriorityQueue<Double> {
    @Override
    public void enqueue(double var1);

    public double dequeueDouble();

    public double firstDouble();

    default public double lastDouble() {
        throw new UnsupportedOperationException();
    }

    public DoubleComparator comparator();

    @Deprecated
    @Override
    default public void enqueue(Double x) {
        this.enqueue((double)x);
    }

    @Deprecated
    @Override
    default public Double dequeue() {
        return this.dequeueDouble();
    }

    @Deprecated
    @Override
    default public Double first() {
        return this.firstDouble();
    }

    @Deprecated
    @Override
    default public Double last() {
        return this.lastDouble();
    }
}

