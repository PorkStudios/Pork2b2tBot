/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.bytes.ByteConsumer;
import java.util.Iterator;
import java.util.Objects;
import java.util.function.Consumer;

public interface ByteIterator
extends Iterator<Byte> {
    public byte nextByte();

    @Deprecated
    @Override
    default public Byte next() {
        return this.nextByte();
    }

    default public void forEachRemaining(ByteConsumer action) {
        Objects.requireNonNull(action);
        while (this.hasNext()) {
            action.accept(this.nextByte());
        }
    }

    @Deprecated
    @Override
    default public void forEachRemaining(Consumer<? super Byte> action) {
        this.forEachRemaining(action::accept);
    }

    default public int skip(int n) {
        if (n < 0) {
            throw new IllegalArgumentException("Argument must be nonnegative: " + n);
        }
        int i = n;
        while (i-- != 0 && this.hasNext()) {
            this.nextByte();
        }
        return n - i - 1;
    }
}

