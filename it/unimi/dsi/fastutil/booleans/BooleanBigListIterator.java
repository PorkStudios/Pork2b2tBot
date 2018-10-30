/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.booleans;

import it.unimi.dsi.fastutil.BigListIterator;
import it.unimi.dsi.fastutil.SafeMath;
import it.unimi.dsi.fastutil.booleans.BooleanBidirectionalIterator;

public interface BooleanBigListIterator
extends BooleanBidirectionalIterator,
BigListIterator<Boolean> {
    @Override
    default public void set(boolean k) {
        throw new UnsupportedOperationException();
    }

    @Override
    default public void add(boolean k) {
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

    default public long skip(long n) {
        long i = n;
        while (i-- != 0L && this.hasNext()) {
            this.nextBoolean();
        }
        return n - i - 1L;
    }

    default public long back(long n) {
        long i = n;
        while (i-- != 0L && this.hasPrevious()) {
            this.previousBoolean();
        }
        return n - i - 1L;
    }

    @Override
    default public int skip(int n) {
        return SafeMath.safeLongToInt(this.skip((long)n));
    }
}

