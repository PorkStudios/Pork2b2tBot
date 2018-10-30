/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.shorts;

import it.unimi.dsi.fastutil.SafeMath;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.IntConsumer;

@FunctionalInterface
public interface ShortConsumer
extends Consumer<Short>,
IntConsumer {
    @Override
    public void accept(short var1);

    @Deprecated
    @Override
    default public void accept(int t) {
        this.accept(SafeMath.safeIntToShort(t));
    }

    @Deprecated
    @Override
    default public void accept(Short t) {
        this.accept((short)t);
    }

    default public ShortConsumer andThen(ShortConsumer after) {
        Objects.requireNonNull(after);
        return t -> {
            this.accept(t);
            after.accept(t);
        };
    }

    @Deprecated
    @Override
    default public ShortConsumer andThen(IntConsumer after) {
        Objects.requireNonNull(after);
        return t -> {
            this.accept(t);
            after.accept(t);
        };
    }

    @Deprecated
    @Override
    default public Consumer<Short> andThen(Consumer<? super Short> after) {
        return Consumer.super.andThen(after);
    }
}

