/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.longs.LongIterator;
import java.util.Iterator;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.LongConsumer;

public interface LongIterable
extends Iterable<Long> {
    public LongIterator iterator();

    default public void forEach(LongConsumer action) {
        Objects.requireNonNull(action);
        LongIterator iterator = this.iterator();
        while (iterator.hasNext()) {
            action.accept(iterator.nextLong());
        }
    }

    @Deprecated
    @Override
    default public void forEach(Consumer<? super Long> action) {
        this.forEach(action::accept);
    }
}

