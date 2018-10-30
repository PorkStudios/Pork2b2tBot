/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.BigListIterator;
import it.unimi.dsi.fastutil.SafeMath;
import it.unimi.dsi.fastutil.floats.FloatBidirectionalIterator;

public interface FloatBigListIterator
extends FloatBidirectionalIterator,
BigListIterator<Float> {
    @Override
    default public void set(float k) {
        throw new UnsupportedOperationException();
    }

    @Override
    default public void add(float k) {
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

    default public long skip(long n) {
        long i = n;
        while (i-- != 0L && this.hasNext()) {
            this.nextFloat();
        }
        return n - i - 1L;
    }

    default public long back(long n) {
        long i = n;
        while (i-- != 0L && this.hasPrevious()) {
            this.previousFloat();
        }
        return n - i - 1L;
    }

    @Override
    default public int skip(int n) {
        return SafeMath.safeLongToInt(this.skip((long)n));
    }
}

