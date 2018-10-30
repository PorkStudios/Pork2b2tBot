/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.doubles;

import java.util.PrimitiveIterator;
import java.util.function.Consumer;
import java.util.function.DoubleConsumer;

public interface DoubleIterator
extends PrimitiveIterator.OfDouble {
    @Override
    public double nextDouble();

    @Deprecated
    @Override
    default public Double next() {
        return this.nextDouble();
    }

    @Deprecated
    @Override
    default public void forEachRemaining(Consumer<? super Double> action) {
        this.forEachRemaining(action::accept);
    }

    default public int skip(int n) {
        if (n < 0) {
            throw new IllegalArgumentException("Argument must be nonnegative: " + n);
        }
        int i = n;
        while (i-- != 0 && this.hasNext()) {
            this.nextDouble();
        }
        return n - i - 1;
    }
}

