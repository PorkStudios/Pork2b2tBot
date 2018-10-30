/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.bytes.Byte2IntMap;
import it.unimi.dsi.fastutil.bytes.ByteComparator;
import it.unimi.dsi.fastutil.bytes.ByteSet;
import it.unimi.dsi.fastutil.bytes.ByteSortedSet;
import it.unimi.dsi.fastutil.ints.IntCollection;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;

public interface Byte2IntSortedMap
extends Byte2IntMap,
SortedMap<Byte, Integer> {
    public Byte2IntSortedMap subMap(byte var1, byte var2);

    public Byte2IntSortedMap headMap(byte var1);

    public Byte2IntSortedMap tailMap(byte var1);

    public byte firstByteKey();

    public byte lastByteKey();

    @Deprecated
    default public Byte2IntSortedMap subMap(Byte from, Byte to) {
        return this.subMap((byte)from, (byte)to);
    }

    @Deprecated
    default public Byte2IntSortedMap headMap(Byte to) {
        return this.headMap((byte)to);
    }

    @Deprecated
    default public Byte2IntSortedMap tailMap(Byte from) {
        return this.tailMap((byte)from);
    }

    @Deprecated
    @Override
    default public Byte firstKey() {
        return this.firstByteKey();
    }

    @Deprecated
    @Override
    default public Byte lastKey() {
        return this.lastByteKey();
    }

    @Deprecated
    @Override
    default public ObjectSortedSet<Map.Entry<Byte, Integer>> entrySet() {
        return this.byte2IntEntrySet();
    }

    public ObjectSortedSet<Byte2IntMap.Entry> byte2IntEntrySet();

    @Override
    public ByteSortedSet keySet();

    @Override
    public IntCollection values();

    public ByteComparator comparator();

    public static interface FastSortedEntrySet
    extends ObjectSortedSet<Byte2IntMap.Entry>,
    Byte2IntMap.FastEntrySet {
        public ObjectBidirectionalIterator<Byte2IntMap.Entry> fastIterator();

        public ObjectBidirectionalIterator<Byte2IntMap.Entry> fastIterator(Byte2IntMap.Entry var1);
    }

}

