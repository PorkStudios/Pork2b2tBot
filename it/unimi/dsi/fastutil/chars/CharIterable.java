/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.chars.CharIterator;
import java.util.Iterator;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.IntConsumer;

public interface CharIterable
extends Iterable<Character> {
    public CharIterator iterator();

    default public void forEach(IntConsumer action) {
        Objects.requireNonNull(action);
        CharIterator iterator = this.iterator();
        while (iterator.hasNext()) {
            action.accept(iterator.nextChar());
        }
    }

    @Deprecated
    @Override
    default public void forEach(final Consumer<? super Character> action) {
        this.forEach(new IntConsumer(){

            @Override
            public void accept(int key) {
                action.accept(Character.valueOf((char)key));
            }
        });
    }

}

