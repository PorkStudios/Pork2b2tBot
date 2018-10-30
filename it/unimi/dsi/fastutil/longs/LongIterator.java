/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.longs;

import java.util.PrimitiveIterator;
import java.util.function.Consumer;
import java.util.function.LongConsumer;

public interface LongIterator
extends PrimitiveIterator.OfLong {
    @Override
    public long nextLong();

    @Deprecated
    @Override
    default public Long next() {
        return this.nextLong();
    }

    @Deprecated
    @Override
    default public void forEachRemaining(Consumer<? super Long> action) {
        this.forEachRemaining(action::accept);
    }

    default public int skip(int n) {
        if (n < 0) {
            throw new IllegalArgumentException("Argument must be nonnegative: " + n);
        }
        int i = n;
        while (i-- != 0 && this.hasNext()) {
            this.nextLong();
        }
        return n - i - 1;
    }
}

