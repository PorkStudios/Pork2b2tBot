/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.shorts;

import it.unimi.dsi.fastutil.BigListIterator;
import it.unimi.dsi.fastutil.SafeMath;
import it.unimi.dsi.fastutil.shorts.ShortBidirectionalIterator;

public interface ShortBigListIterator
extends ShortBidirectionalIterator,
BigListIterator<Short> {
    @Override
    default public void set(short k) {
        throw new UnsupportedOperationException();
    }

    @Override
    default public void add(short k) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    @Override
    default public void set(Short k) {
        this.set((short)k);
    }

    @Deprecated
    @Override
    default public void add(Short k) {
        this.add((short)k);
    }

    default public long skip(long n) {
        long i = n;
        while (i-- != 0L && this.hasNext()) {
            this.nextShort();
        }
        return n - i - 1L;
    }

    default public long back(long n) {
        long i = n;
        while (i-- != 0L && this.hasPrevious()) {
            this.previousShort();
        }
        return n - i - 1L;
    }

    @Override
    default public int skip(int n) {
        return SafeMath.safeLongToInt(this.skip((long)n));
    }
}

