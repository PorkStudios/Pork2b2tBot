/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.booleans;

import it.unimi.dsi.fastutil.booleans.BooleanIterator;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;

public interface BooleanBidirectionalIterator
extends BooleanIterator,
ObjectBidirectionalIterator<Boolean> {
    public boolean previousBoolean();

    @Deprecated
    @Override
    default public Boolean previous() {
        return this.previousBoolean();
    }

    @Override
    default public int back(int n) {
        int i = n;
        while (i-- != 0 && this.hasPrevious()) {
            this.previousBoolean();
        }
        return n - i - 1;
    }

    @Override
    default public int skip(int n) {
        return BooleanIterator.super.skip(n);
    }
}

