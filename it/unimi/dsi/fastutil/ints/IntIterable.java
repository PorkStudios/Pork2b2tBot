/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.ints;

import it.unimi.dsi.fastutil.ints.IntIterator;
import java.util.Iterator;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.IntConsumer;

public interface IntIterable
extends Iterable<Integer> {
    public IntIterator iterator();

    default public void forEach(IntConsumer action) {
        Objects.requireNonNull(action);
        IntIterator iterator = this.iterator();
        while (iterator.hasNext()) {
            action.accept(iterator.nextInt());
        }
    }

    @Deprecated
    @Override
    default public void forEach(Consumer<? super Integer> action) {
        this.forEach(action::accept);
    }
}

