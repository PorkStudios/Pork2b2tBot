/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.ints;

import java.util.Objects;
import java.util.function.Consumer;

@FunctionalInterface
public interface IntConsumer
extends Consumer<Integer>,
java.util.function.IntConsumer {
    @Deprecated
    @Override
    default public void accept(Integer t) {
        this.accept(t.intValue());
    }

    @Override
    default public IntConsumer andThen(java.util.function.IntConsumer after) {
        Objects.requireNonNull(after);
        return t -> {
            this.accept(t);
            after.accept(t);
        };
    }

    @Deprecated
    @Override
    default public Consumer<Integer> andThen(Consumer<? super Integer> after) {
        return Consumer.super.andThen(after);
    }
}

