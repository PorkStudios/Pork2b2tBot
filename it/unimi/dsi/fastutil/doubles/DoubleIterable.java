/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.doubles;

import it.unimi.dsi.fastutil.doubles.DoubleIterator;
import java.util.Iterator;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.DoubleConsumer;

public interface DoubleIterable
extends Iterable<Double> {
    public DoubleIterator iterator();

    default public void forEach(DoubleConsumer action) {
        Objects.requireNonNull(action);
        DoubleIterator iterator = this.iterator();
        while (iterator.hasNext()) {
            action.accept(iterator.nextDouble());
        }
    }

    @Deprecated
    @Override
    default public void forEach(Consumer<? super Double> action) {
        this.forEach(action::accept);
    }
}

