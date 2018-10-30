/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.floats.FloatIterator;
import java.util.Iterator;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.DoubleConsumer;

public interface FloatIterable
extends Iterable<Float> {
    public FloatIterator iterator();

    default public void forEach(DoubleConsumer action) {
        Objects.requireNonNull(action);
        FloatIterator iterator = this.iterator();
        while (iterator.hasNext()) {
            action.accept(iterator.nextFloat());
        }
    }

    @Deprecated
    @Override
    default public void forEach(final Consumer<? super Float> action) {
        this.forEach(new DoubleConsumer(){

            @Override
            public void accept(double key) {
                action.accept(Float.valueOf((float)key));
            }
        });
    }

}

