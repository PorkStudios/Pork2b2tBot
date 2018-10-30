/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.SafeMath;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.IntConsumer;

@FunctionalInterface
public interface ByteConsumer
extends Consumer<Byte>,
IntConsumer {
    @Override
    public void accept(byte var1);

    @Deprecated
    @Override
    default public void accept(int t) {
        this.accept(SafeMath.safeIntToByte(t));
    }

    @Deprecated
    @Override
    default public void accept(Byte t) {
        this.accept((byte)t);
    }

    default public ByteConsumer andThen(ByteConsumer after) {
        Objects.requireNonNull(after);
        return t -> {
            this.accept(t);
            after.accept(t);
        };
    }

    @Deprecated
    @Override
    default public ByteConsumer andThen(IntConsumer after) {
        Objects.requireNonNull(after);
        return t -> {
            this.accept(t);
            after.accept(t);
        };
    }

    @Deprecated
    @Override
    default public Consumer<Byte> andThen(Consumer<? super Byte> after) {
        return Consumer.super.andThen(after);
    }
}

