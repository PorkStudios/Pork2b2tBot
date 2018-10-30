/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.ints;

import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.IntCollection;
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

public interface Int2IntSortedMap
extends Int2IntMap,
SortedMap<Integer, Integer> {
    public Int2IntSortedMap subMap(int var1, int var2);

    public Int2IntSortedMap headMap(int var1);

    public Int2IntSortedMap tailMap(int var1);

    public int firstIntKey();

    public int lastIntKey();

    @Deprecated
    default public Int2IntSortedMap subMap(Integer from, Integer to) {
        return this.subMap((int)from, (int)to);
    }

    @Deprecated
    default public Int2IntSortedMap headMap(Integer to) {
        return this.headMap((int)to);
    }

    @Deprecated
    default public Int2IntSortedMap tailMap(Integer from) {
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
    default public ObjectSortedSet<Map.Entry<Integer, Integer>> entrySet() {
        return this.int2IntEntrySet();
    }

    public ObjectSortedSet<Int2IntMap.Entry> int2IntEntrySet();

    @Override
    public IntSortedSet keySet();

    @Override
    public IntCollection values();

    public IntComparator comparator();

    public static interface FastSortedEntrySet
    extends ObjectSortedSet<Int2IntMap.Entry>,
    Int2IntMap.FastEntrySet {
        public ObjectBidirectionalIterator<Int2IntMap.Entry> fastIterator();

        public ObjectBidirectionalIterator<Int2IntMap.Entry> fastIterator(Int2IntMap.Entry var1);
    }

}

