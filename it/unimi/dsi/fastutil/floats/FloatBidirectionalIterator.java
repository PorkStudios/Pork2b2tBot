/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.floats.FloatIterator;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;

public interface FloatBidirectionalIterator
extends FloatIterator,
ObjectBidirectionalIterator<Float> {
    public float previousFloat();

    @Deprecated
    @Override
    default public Float previous() {
        return Float.valueOf(this.previousFloat());
    }

    @Override
    default public int back(int n) {
        int i = n;
        while (i-- != 0 && this.hasPrevious()) {
            this.previousFloat();
        }
        return n - i - 1;
    }

    @Override
    default public int skip(int n) {
        return FloatIterator.super.skip(n);
    }
}

