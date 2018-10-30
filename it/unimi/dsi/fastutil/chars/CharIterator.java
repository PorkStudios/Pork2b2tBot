/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.chars.CharConsumer;
import java.util.Iterator;
import java.util.Objects;
import java.util.function.Consumer;

public interface CharIterator
extends Iterator<Character> {
    public char nextChar();

    @Deprecated
    @Override
    default public Character next() {
        return Character.valueOf(this.nextChar());
    }

    default public void forEachRemaining(CharConsumer action) {
        Objects.requireNonNull(action);
        while (this.hasNext()) {
            action.accept(this.nextChar());
        }
    }

    @Deprecated
    @Override
    default public void forEachRemaining(Consumer<? super Character> action) {
        this.forEachRemaining(action::accept);
    }

    default public int skip(int n) {
        if (n < 0) {
            throw new IllegalArgumentException("Argument must be nonnegative: " + n);
        }
        int i = n;
        while (i-- != 0 && this.hasNext()) {
            this.nextChar();
        }
        return n - i - 1;
    }
}

