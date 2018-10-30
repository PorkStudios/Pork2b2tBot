/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.booleans;

import it.unimi.dsi.fastutil.booleans.BooleanBidirectionalIterator;
import java.util.ListIterator;

public interface BooleanListIterator
extends BooleanBidirectionalIterator,
ListIterator<Boolean> {
    @Override
    default public void set(boolean k) {
        throw new UnsupportedOperationException();
    }

    @Override
    default public void add(boolean k) {
        throw new UnsupportedOperationException();
    }

    @Override
    default public void remove() {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    @Override
    default public void set(Boolean k) {
        this.set((boolean)k);
    }

    @Deprecated
    @Override
    default public void add(Boolean k) {
        this.add((boolean)k);
    }

    @Deprecated
    @Override
    default public Boolean next() {
        return BooleanBidirectionalIterator.super.next();
    }

    @Deprecated
    @Override
    default public Boolean previous() {
        return BooleanBidirectionalIterator.super.previous();
    }
}

