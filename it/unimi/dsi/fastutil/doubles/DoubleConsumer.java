/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.doubles;

import java.util.Objects;
import java.util.function.Consumer;

@FunctionalInterface
public interface DoubleConsumer
extends Consumer<Double>,
java.util.function.DoubleConsumer {
    @Deprecated
    @Override
    default public void accept(Double t) {
        this.accept(t.doubleValue());
    }

    @Override
    default public DoubleConsumer andThen(java.util.function.DoubleConsumer after) {
        Objects.requireNonNull(after);
        return t -> {
            this.accept(t);
            after.accept(t);
        };
    }

    @Deprecated
    @Override
    default public Consumer<Double> andThen(Consumer<? super Double> after) {
        return Consumer.super.andThen(after);
    }
}

