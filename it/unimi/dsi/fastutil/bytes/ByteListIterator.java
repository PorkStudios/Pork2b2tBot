/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.bytes.ByteBidirectionalIterator;
import java.util.ListIterator;

public interface ByteListIterator
extends ByteBidirectionalIterator,
ListIterator<Byte> {
    @Override
    default public void set(byte k) {
        throw new UnsupportedOperationException();
    }

    @Override
    default public void add(byte k) {
        throw new UnsupportedOperationException();
    }

    @Override
    default public void remove() {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    @Override
    default public void set(Byte k) {
        this.set((byte)k);
    }

    @Deprecated
    @Override
    default public void add(Byte k) {
        this.add((byte)k);
    }

    @Deprecated
    @Override
    default public Byte next() {
        return ByteBidirectionalIterator.super.next();
    }

    @Deprecated
    @Override
    default public Byte previous() {
        return ByteBidirectionalIterator.super.previous();
    }
}

