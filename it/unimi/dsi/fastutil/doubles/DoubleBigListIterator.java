/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.doubles;

import it.unimi.dsi.fastutil.BigListIterator;
import it.unimi.dsi.fastutil.SafeMath;
import it.unimi.dsi.fastutil.doubles.DoubleBidirectionalIterator;

public interface DoubleBigListIterator
extends DoubleBidirectionalIterator,
BigListIterator<Double> {
    @Override
    default public void set(double k) {
        throw new UnsupportedOperationException();
    }

    @Override
    default public void add(double k) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    @Override
    default public void set(Double k) {
        this.set((double)k);
    }

    @Deprecated
    @Override
    default public void add(Double k) {
        this.add((double)k);
    }

    default public long skip(long n) {
        long i = n;
        while (i-- != 0L && this.hasNext()) {
            this.nextDouble();
        }
        return n - i - 1L;
    }

    default public long back(long n) {
        long i = n;
        while (i-- != 0L && this.hasPrevious()) {
            this.previousDouble();
        }
        return n - i - 1L;
    }

    @Override
    default public int skip(int n) {
        return SafeMath.safeLongToInt(this.skip((long)n));
    }
}

