/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.shorts;

import it.unimi.dsi.fastutil.shorts.ShortIterator;
import java.util.Iterator;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.IntConsumer;

public interface ShortIterable
extends Iterable<Short> {
    public ShortIterator iterator();

    default public void forEach(IntConsumer action) {
        Objects.requireNonNull(action);
        ShortIterator iterator = this.iterator();
        while (iterator.hasNext()) {
            action.accept(iterator.nextShort());
        }
    }

    @Deprecated
    @Override
    default public void forEach(final Consumer<? super Short> action) {
        this.forEach(new IntConsumer(){

            @Override
            public void accept(int key) {
                action.accept((short)key);
            }
        });
    }

}

