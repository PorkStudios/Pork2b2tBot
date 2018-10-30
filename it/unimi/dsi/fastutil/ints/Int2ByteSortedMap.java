/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.ints;

import it.unimi.dsi.fastutil.bytes.ByteCollection;
import it.unimi.dsi.fastutil.ints.Int2ByteMap;
import it.unimi.dsi.fastutil.ints.IntComparator;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.ints.IntSortedSet;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;

public interface Int2ByteSortedMap
extends Int2ByteMap,
SortedMap<Integer, Byte> {
    public Int2ByteSortedMap subMap(int var1, int var2);

    public Int2ByteSortedMap headMap(int var1);

    public Int2ByteSortedMap tailMap(int var1);

    public int firstIntKey();

    public int lastIntKey();

    @Deprecated
    default public Int2ByteSortedMap subMap(Integer from, Integer to) {
        return this.subMap((int)from, (int)to);
    }

    @Deprecated
    default public Int2ByteSortedMap headMap(Integer to) {
        return this.headMap((int)to);
    }

    @Deprecated
    default public Int2ByteSortedMap tailMap(Integer from) {
        return this.tailMap((int)from);
    }

    @Deprecated
    @Override
    default public Integer firstKey() {
        return this.firstIntKey();
    }

    @Deprecated
    @Override
    default public Integer lastKey() {
        return this.lastIntKey();
    }

    @Deprecated
    @Override
    default public ObjectSortedSet<Map.Entry<Integer, Byte>> entrySet() {
        return this.int2ByteEntrySet();
    }

    public ObjectSortedSet<Int2ByteMap.Entry> int2ByteEntrySet();

    @Override
    public IntSortedSet keySet();

    @Override
    public ByteCollection values();

    public IntComparator comparator();

    public static interface FastSortedEntrySet
    extends ObjectSortedSet<Int2ByteMap.Entry>,
    Int2ByteMap.FastEntrySet {
        public ObjectBidirectionalIterator<Int2ByteMap.Entry> fastIterator();

        public ObjectBidirectionalIterator<Int2ByteMap.Entry> fastIterator(Int2ByteMap.Entry var1);
    }

}

