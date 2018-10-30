/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.SafeMath;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.DoubleConsumer;

@FunctionalInterface
public interface FloatConsumer
extends Consumer<Float>,
DoubleConsumer {
    @Override
    public void accept(float var1);

    @Deprecated
    @Override
    default public void accept(double t) {
        this.accept(SafeMath.safeDoubleToFloat(t));
    }

    @Deprecated
    @Override
    default public void accept(Float t) {
        this.accept(t.floatValue());
    }

    default public FloatConsumer andThen(FloatConsumer after) {
        Objects.requireNonNull(after);
        return t -> {
            this.accept(t);
            after.accept(t);
        };
    }

    @Deprecated
    @Override
    default public FloatConsumer andThen(DoubleConsumer after) {
        Objects.requireNonNull(after);
        return t -> {
            this.accept(t);
            after.accept(t);
        };
    }

    @Deprecated
    @Override
    default public Consumer<Float> andThen(Consumer<? super Float> after) {
        return Consumer.super.andThen(after);
    }
}

