/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.ints;

import it.unimi.dsi.fastutil.BigListIterator;
import it.unimi.dsi.fastutil.SafeMath;
import it.unimi.dsi.fastutil.ints.IntBidirectionalIterator;

public interface IntBigListIterator
extends IntBidirectionalIterator,
BigListIterator<Integer> {
    @Override
    default public void set(int k) {
        throw new UnsupportedOperationException();
    }

    @Override
    default public void add(int k) {
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

    default public long skip(long n) {
        long i = n;
        while (i-- != 0L && this.hasNext()) {
            this.nextInt();
        }
        return n - i - 1L;
    }

    default public long back(long n) {
        long i = n;
        while (i-- != 0L && this.hasPrevious()) {
            this.previousInt();
        }
        return n - i - 1L;
    }

    @Override
    default public int skip(int n) {
        return SafeMath.safeLongToInt(this.skip((long)n));
    }
}

