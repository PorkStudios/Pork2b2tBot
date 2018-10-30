/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.ints;

import it.unimi.dsi.fastutil.ints.IntBidirectionalIterator;
import java.util.ListIterator;

public interface IntListIterator
extends IntBidirectionalIterator,
ListIterator<Integer> {
    @Override
    default public void set(int k) {
        throw new UnsupportedOperationException();
    }

    @Override
    default public void add(int k) {
        throw new UnsupportedOperationException();
    }

    @Override
    default public void remove() {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    @Override
    default public void set(Integer k) {
        this.set((int)k);
    }

    @Deprecated
    @Override
    default public void add(Integer k) {
        this.add((int)k);
    }

    @Deprecated
    @Override
    default public Integer next() {
        return IntBidirectionalIterator.super.next();
    }

    @Deprecated
    @Override
    default public Integer previous() {
        return IntBidirectionalIterator.super.previous();
    }
}

