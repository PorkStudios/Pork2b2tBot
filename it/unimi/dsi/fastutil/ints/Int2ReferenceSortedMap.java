/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.ints;

import it.unimi.dsi.fastutil.ints.Int2ReferenceMap;
import it.unimi.dsi.fastutil.ints.IntComparator;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.ints.IntSortedSet;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import it.unimi.dsi.fastutil.objects.ReferenceCollection;
import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;

public interface Int2ReferenceSortedMap<V>
extends Int2ReferenceMap<V>,
SortedMap<Integer, V> {
    public Int2ReferenceSortedMap<V> subMap(int var1, int var2);

    public Int2ReferenceSortedMap<V> headMap(int var1);

    public Int2ReferenceSortedMap<V> tailMap(int var1);

    public int firstIntKey();

    public int lastIntKey();

    @Deprecated
    default public Int2ReferenceSortedMap<V> subMap(Integer from, Integer to) {
        return this.subMap((int)from, (int)to);
    }

    @Deprecated
    default public Int2ReferenceSortedMap<V> headMap(Integer to) {
        return this.headMap((int)to);
    }

    @Deprecated
    default public Int2ReferenceSortedMap<V> tailMap(Integer from) {
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
    default public ObjectSortedSet<Map.Entry<Integer, V>> entrySet() {
        return this.int2ReferenceEntrySet();
    }

    @Override
    public ObjectSortedSet<Int2ReferenceMap.Entry<V>> int2ReferenceEntrySet();

    @Override
    public IntSortedSet keySet();

    @Override
    public ReferenceCollection<V> values();

    public IntComparator comparator();

    public static interface FastSortedEntrySet<V>
    extends ObjectSortedSet<Int2ReferenceMap.Entry<V>>,
    Int2ReferenceMap.FastEntrySet<V> {
        @Override
        public ObjectBidirectionalIterator<Int2ReferenceMap.Entry<V>> fastIterator();

        public ObjectBidirectionalIterator<Int2ReferenceMap.Entry<V>> fastIterator(Int2ReferenceMap.Entry<V> var1);
    }

}

