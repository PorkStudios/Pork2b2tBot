/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.ints;

import it.unimi.dsi.fastutil.floats.FloatCollection;
import it.unimi.dsi.fastutil.ints.Int2FloatMap;
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

public interface Int2FloatSortedMap
extends Int2FloatMap,
SortedMap<Integer, Float> {
    public Int2FloatSortedMap subMap(int var1, int var2);

    public Int2FloatSortedMap headMap(int var1);

    public Int2FloatSortedMap tailMap(int var1);

    public int firstIntKey();

    public int lastIntKey();

    @Deprecated
    default public Int2FloatSortedMap subMap(Integer from, Integer to) {
        return this.subMap((int)from, (int)to);
    }

    @Deprecated
    default public Int2FloatSortedMap headMap(Integer to) {
        return this.headMap((int)to);
    }

    @Deprecated
    default public Int2FloatSortedMap tailMap(Integer from) {
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
    default public ObjectSortedSet<Map.Entry<Integer, Float>> entrySet() {
        return this.int2FloatEntrySet();
    }

    public ObjectSortedSet<Int2FloatMap.Entry> int2FloatEntrySet();

    @Override
    public IntSortedSet keySet();

    @Override
    public FloatCollection values();

    public IntComparator comparator();

    public static interface FastSortedEntrySet
    extends ObjectSortedSet<Int2FloatMap.Entry>,
    Int2FloatMap.FastEntrySet {
        public ObjectBidirectionalIterator<Int2FloatMap.Entry> fastIterator();

        public ObjectBidirectionalIterator<Int2FloatMap.Entry> fastIterator(Int2FloatMap.Entry var1);
    }

}

