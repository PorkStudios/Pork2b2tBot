/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.ints;

import it.unimi.dsi.fastutil.ints.Int2ShortMap;
import it.unimi.dsi.fastutil.ints.IntComparator;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.ints.IntSortedSet;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import it.unimi.dsi.fastutil.shorts.ShortCollection;
import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;

public interface Int2ShortSortedMap
extends Int2ShortMap,
SortedMap<Integer, Short> {
    public Int2ShortSortedMap subMap(int var1, int var2);

    public Int2ShortSortedMap headMap(int var1);

    public Int2ShortSortedMap tailMap(int var1);

    public int firstIntKey();

    public int lastIntKey();

    @Deprecated
    default public Int2ShortSortedMap subMap(Integer from, Integer to) {
        return this.subMap((int)from, (int)to);
    }

    @Deprecated
    default public Int2ShortSortedMap headMap(Integer to) {
        return this.headMap((int)to);
    }

    @Deprecated
    default public Int2ShortSortedMap tailMap(Integer from) {
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
    default public ObjectSortedSet<Map.Entry<Integer, Short>> entrySet() {
        return this.int2ShortEntrySet();
    }

    public ObjectSortedSet<Int2ShortMap.Entry> int2ShortEntrySet();

    @Override
    public IntSortedSet keySet();

    @Override
    public ShortCollection values();

    public IntComparator comparator();

    public static interface FastSortedEntrySet
    extends ObjectSortedSet<Int2ShortMap.Entry>,
    Int2ShortMap.FastEntrySet {
        public ObjectBidirectionalIterator<Int2ShortMap.Entry> fastIterator();

        public ObjectBidirectionalIterator<Int2ShortMap.Entry> fastIterator(Int2ShortMap.Entry var1);
    }

}

