/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.booleans;

import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import it.unimi.dsi.fastutil.booleans.BooleanIterator;
import java.util.Iterator;
import java.util.Objects;
import java.util.function.Consumer;

public interface BooleanIterable
extends Iterable<Boolean> {
    public BooleanIterator iterator();

    default public void forEach(BooleanConsumer action) {
        Objects.requireNonNull(action);
        BooleanIterator iterator = this.iterator();
        while (iterator.hasNext()) {
            action.accept(iterator.nextBoolean());
        }
    }

    @Deprecated
    @Override
    default public void forEach(Consumer<? super Boolean> action) {
        this.forEach(action::accept);
    }
}

