/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.shorts;

import it.unimi.dsi.fastutil.shorts.ShortBidirectionalIterator;
import java.util.ListIterator;

public interface ShortListIterator
extends ShortBidirectionalIterator,
ListIterator<Short> {
    @Override
    default public void set(short k) {
        throw new UnsupportedOperationException();
    }

    @Override
    default public void add(short k) {
        throw new UnsupportedOperationException();
    }

    @Override
    default public void remove() {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    @Override
    default public void set(Short k) {
        this.set((short)k);
    }

    @Deprecated
    @Override
    default public void add(Short k) {
        this.add((short)k);
    }

    @Deprecated
    @Override
    default public Short next() {
        return ShortBidirectionalIterator.super.next();
    }

    @Deprecated
    @Override
    default public Short previous() {
        return ShortBidirectionalIterator.super.previous();
    }
}

