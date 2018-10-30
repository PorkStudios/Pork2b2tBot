/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.floats.FloatConsumer;
import java.util.Iterator;
import java.util.Objects;
import java.util.function.Consumer;

public interface FloatIterator
extends Iterator<Float> {
    public float nextFloat();

    @Deprecated
    @Override
    default public Float next() {
        return Float.valueOf(this.nextFloat());
    }

    default public void forEachRemaining(FloatConsumer action) {
        Objects.requireNonNull(action);
        while (this.hasNext()) {
            action.accept(this.nextFloat());
        }
    }

    @Deprecated
    @Override
    default public void forEachRemaining(Consumer<? super Float> action) {
        this.forEachRemaining(action::accept);
    }

    default public int skip(int n) {
        if (n < 0) {
            throw new IllegalArgumentException("Argument must be nonnegative: " + n);
        }
        int i = n;
        while (i-- != 0 && this.hasNext()) {
            this.nextFloat();
        }
        return n - i - 1;
    }
}

