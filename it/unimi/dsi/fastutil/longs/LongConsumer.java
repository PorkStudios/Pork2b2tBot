/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.longs;

import java.util.Objects;
import java.util.function.Consumer;

@FunctionalInterface
public interface LongConsumer
extends Consumer<Long>,
java.util.function.LongConsumer {
    @Deprecated
    @Override
    default public void accept(Long t) {
        this.accept(t.longValue());
    }

    @Override
    default public LongConsumer andThen(java.util.function.LongConsumer after) {
        Objects.requireNonNull(after);
        return t -> {
            this.accept(t);
            after.accept(t);
        };
    }

    @Deprecated
    @Override
    default public Consumer<Long> andThen(Consumer<? super Long> after) {
        return Consumer.super.andThen(after);
    }
}

