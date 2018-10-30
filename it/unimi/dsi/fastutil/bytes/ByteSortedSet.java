/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.bytes.ByteBidirectionalIterable;
import it.unimi.dsi.fastutil.bytes.ByteBidirectionalIterator;
import it.unimi.dsi.fastutil.bytes.ByteComparator;
import it.unimi.dsi.fastutil.bytes.ByteIterator;
import it.unimi.dsi.fastutil.bytes.ByteSet;
import java.util.Comparator;
import java.util.Iterator;
import java.util.SortedSet;

public interface ByteSortedSet
extends ByteSet,
SortedSet<Byte>,
ByteBidirectionalIterable {
    public ByteBidirectionalIterator iterator(byte var1);

    @Override
    public ByteBidirectionalIterator iterator();

    public ByteSortedSet subSet(byte var1, byte var2);

    public ByteSortedSet headSet(byte var1);

    public ByteSortedSet tailSet(byte var1);

    public ByteComparator comparator();

    public byte firstByte();

    public byte lastByte();

    @Deprecated
    default public ByteSortedSet subSet(Byte from, Byte to) {
        return this.subSet((byte)from, (byte)to);
    }

    @Deprecated
    default public ByteSortedSet headSet(Byte to) {
        return this.headSet((byte)to);
    }

    @Deprecated
    default public ByteSortedSet tailSet(Byte from) {
        return this.tailSet((byte)from);
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

