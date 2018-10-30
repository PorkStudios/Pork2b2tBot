/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.ints;

import it.unimi.dsi.fastutil.doubles.DoubleCollection;
import it.unimi.dsi.fastutil.ints.Int2DoubleMap;
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

public interface Int2DoubleSortedMap
extends Int2DoubleMap,
SortedMap<Integer, Double> {
    public Int2DoubleSortedMap subMap(int var1, int var2);

    public Int2DoubleSortedMap headMap(int var1);

    public Int2DoubleSortedMap tailMap(int var1);

    public int firstIntKey();

    public int lastIntKey();

    @Deprecated
    default public Int2DoubleSortedMap subMap(Integer from, Integer to) {
        return this.subMap((int)from, (int)to);
    }

    @Deprecated
    default public Int2DoubleSortedMap headMap(Integer to) {
        return this.headMap((int)to);
    }

    @Deprecated
    default public Int2DoubleSortedMap tailMap(Integer from) {
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
    default public ObjectSortedSet<Map.Entry<Integer, Double>> entrySet() {
        return this.int2DoubleEntrySet();
    }

    public ObjectSortedSet<Int2DoubleMap.Entry> int2DoubleEntrySet();

    @Override
    public IntSortedSet keySet();

    @Override
    public DoubleCollection values();

    public IntComparator comparator();

    public static interface FastSortedEntrySet
    extends ObjectSortedSet<Int2DoubleMap.Entry>,
    Int2DoubleMap.FastEntrySet {
        public ObjectBidirectionalIterator<Int2DoubleMap.Entry> fastIterator();

        public ObjectBidirectionalIterator<Int2DoubleMap.Entry> fastIterator(Int2DoubleMap.Entry var1);
    }

}

