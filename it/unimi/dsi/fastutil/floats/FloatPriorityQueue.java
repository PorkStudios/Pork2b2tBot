/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.PriorityQueue;
import it.unimi.dsi.fastutil.floats.FloatComparator;
import java.util.Comparator;

public interface FloatPriorityQueue
extends PriorityQueue<Float> {
    @Override
    public void enqueue(float var1);

    public float dequeueFloat();

    public float firstFloat();

    default public float lastFloat() {
        throw new UnsupportedOperationException();
    }

    public FloatComparator comparator();

    @Deprecated
    @Override
    default public void enqueue(Float x) {
        this.enqueue(x.floatValue());
    }

    @Deprecated
    @Override
    default public Float dequeue() {
        return Float.valueOf(this.dequeueFloat());
    }

    @Deprecated
    @Override
    default public Float first() {
        return Float.valueOf(this.firstFloat());
    }

    @Deprecated
    @Override
    default public Float last() {
        return Float.valueOf(this.lastFloat());
    }
}

