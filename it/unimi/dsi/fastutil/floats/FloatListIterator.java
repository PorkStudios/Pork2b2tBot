/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.floats.FloatBidirectionalIterator;
import java.util.ListIterator;

public interface FloatListIterator
extends FloatBidirectionalIterator,
ListIterator<Float> {
    @Override
    default public void set(float k) {
        throw new UnsupportedOperationException();
    }

    @Override
    default public void add(float k) {
        throw new UnsupportedOperationException();
    }

    @Override
    default public void remove() {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    @Override
    default public void set(Float k) {
        this.set(k.floatValue());
    }

    @Deprecated
    @Override
    default public void add(Float k) {
        this.add(k.floatValue());
    }

    @Deprecated
    @Override
    default public Float next() {
        return FloatBidirectionalIterator.super.next();
    }

    @Deprecated
    @Override
    default public Float previous() {
        return FloatBidirectionalIterator.super.previous();
    }
}

