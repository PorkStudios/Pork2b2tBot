/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.shorts;

import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.shorts.ShortIterator;

public interface ShortBidirectionalIterator
extends ShortIterator,
ObjectBidirectionalIterator<Short> {
    public short previousShort();

    @Deprecated
    @Override
    default public Short previous() {
        return this.previousShort();
    }

    @Override
    default public int back(int n) {
        int i = n;
        while (i-- != 0 && this.hasPrevious()) {
            this.previousShort();
        }
        return n - i - 1;
    }

    @Override
    default public int skip(int n) {
        return ShortIterator.super.skip(n);
    }
}

