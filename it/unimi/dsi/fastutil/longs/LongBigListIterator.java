/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.BigListIterator;
import it.unimi.dsi.fastutil.SafeMath;
import it.unimi.dsi.fastutil.longs.LongBidirectionalIterator;

public interface LongBigListIterator
extends LongBidirectionalIterator,
BigListIterator<Long> {
    @Override
    default public void set(long k) {
        throw new UnsupportedOperationException();
    }

    @Override
    default public void add(long k) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    @Override
    default public void set(Long k) {
        this.set((long)k);
    }

    @Deprecated
    @Override
    default public void add(Long k) {
        this.add((long)k);
    }

    default public long skip(long n) {
        long i = n;
        while (i-- != 0L && this.hasNext()) {
            this.nextLong();
        }
        return n - i - 1L;
    }

    default public long back(long n) {
        long i = n;
        while (i-- != 0L && this.hasPrevious()) {
            this.previousLong();
        }
        return n - i - 1L;
    }

    @Override
    default public int skip(int n) {
        return SafeMath.safeLongToInt(this.skip((long)n));
    }
}

