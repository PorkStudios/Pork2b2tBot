/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.BigListIterator;
import it.unimi.dsi.fastutil.SafeMath;
import it.unimi.dsi.fastutil.bytes.ByteBidirectionalIterator;

public interface ByteBigListIterator
extends ByteBidirectionalIterator,
BigListIterator<Byte> {
    @Override
    default public void set(byte k) {
        throw new UnsupportedOperationException();
    }

    @Override
    default public void add(byte k) {
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

    default public long skip(long n) {
        long i = n;
        while (i-- != 0L && this.hasNext()) {
            this.nextByte();
        }
        return n - i - 1L;
    }

    default public long back(long n) {
        long i = n;
        while (i-- != 0L && this.hasPrevious()) {
            this.previousByte();
        }
        return n - i - 1L;
    }

    @Override
    default public int skip(int n) {
        return SafeMath.safeLongToInt(this.skip((long)n));
    }
}

