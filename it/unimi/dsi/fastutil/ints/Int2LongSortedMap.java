/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.ints;

import it.unimi.dsi.fastutil.ints.Int2LongMap;
import it.unimi.dsi.fastutil.ints.IntComparator;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.ints.IntSortedSet;
import it.unimi.dsi.fastutil.longs.LongCollection;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;

public interface Int2LongSortedMap
extends Int2LongMap,
SortedMap<Integer, Long> {
    public Int2LongSortedMap subMap(int var1, int var2);

    public Int2LongSortedMap headMap(int var1);

    public Int2LongSortedMap tailMap(int var1);

    public int firstIntKey();

    public int lastIntKey();

    @Deprecated
    default public Int2LongSortedMap subMap(Integer from, Integer to) {
        return this.subMap((int)from, (int)to);
    }

    @Deprecated
    default public Int2LongSortedMap headMap(Integer to) {
        return this.headMap((int)to);
    }

    @Deprecated
    default public Int2LongSortedMap tailMap(Integer from) {
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
    default public ObjectSortedSet<Map.Entry<Integer, Long>> entrySet() {
        return this.int2LongEntrySet();
    }

    public ObjectSortedSet<Int2LongMap.Entry> int2LongEntrySet();

    @Override
    public IntSortedSet keySet();

    @Override
    public LongCollection values();

    public IntComparator comparator();

    public static interface FastSortedEntrySet
    extends ObjectSortedSet<Int2LongMap.Entry>,
    Int2LongMap.FastEntrySet {
        public ObjectBidirectionalIterator<Int2LongMap.Entry> fastIterator();

        public ObjectBidirectionalIterator<Int2LongMap.Entry> fastIterator(Int2LongMap.Entry var1);
    }

}

