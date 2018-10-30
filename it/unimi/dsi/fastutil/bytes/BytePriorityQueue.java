/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.PriorityQueue;
import it.unimi.dsi.fastutil.bytes.ByteComparator;
import java.util.Comparator;

public interface BytePriorityQueue
extends PriorityQueue<Byte> {
    @Override
    public void enqueue(byte var1);

    public byte dequeueByte();

    public byte firstByte();

    default public byte lastByte() {
        throw new UnsupportedOperationException();
    }

    public ByteComparator comparator();

    @Deprecated
    @Override
    default public void enqueue(Byte x) {
        this.enqueue((byte)x);
    }

    @Deprecated
    @Override
    default public Byte dequeue() {
        return this.dequeueByte();
    }

    @Deprecated
    @Override
    default public Byte first() {
        return this.firstByte();
    }

    @Deprecated
    @Override
    default public Byte last() {
        return this.lastByte();
    }
}

