/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.ints;

import java.util.PrimitiveIterator;
import java.util.function.Consumer;
import java.util.function.IntConsumer;

public interface IntIterator
extends PrimitiveIterator.OfInt {
    @Override
    public int nextInt();

    @Deprecated
    @Override
    default public Integer next() {
        return this.nextInt();
    }

    @Deprecated
    @Override
    default public void forEachRemaining(Consumer<? super Integer> action) {
        this.forEachRemaining(action::accept);
    }

    default public int skip(int n) {
        if (n < 0) {
            throw new IllegalArgumentException("Argument must be nonnegative: " + n);
        }
        int i = n;
        while (i-- != 0 && this.hasNext()) {
            this.nextInt();
        }
        return n - i - 1;
    }
}

