/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.doubles.DoubleCollection;
import it.unimi.dsi.fastutil.longs.Long2DoubleMap;
import it.unimi.dsi.fastutil.longs.LongComparator;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.longs.LongSortedSet;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;

public interface Long2DoubleSortedMap
extends Long2DoubleMap,
SortedMap<Long, Double> {
    public Long2DoubleSortedMap subMap(long var1, long var3);

    public Long2DoubleSortedMap headMap(long var1);

    public Long2DoubleSortedMap tailMap(long var1);

    public long firstLongKey();

    public long lastLongKey();

    @Deprecated
    default public Long2DoubleSortedMap subMap(Long from, Long to) {
        return this.subMap((long)from, (long)to);
    }

    @Deprecated
    default public Long2DoubleSortedMap headMap(Long to) {
        return this.headMap((long)to);
    }

    @Deprecated
    default public Long2DoubleSortedMap tailMap(Long from) {
        return this.tailMap((long)from);
    }

    @Deprecated
    @Override
    default public Long firstKey() {
        return this.firstLongKey();
    }

    @Deprecated
    @Override
    default public Long lastKey() {
        return this.lastLongKey();
    }

    @Deprecated
    @Override
    default public ObjectSortedSet<Map.Entry<Long, Double>> entrySet() {
        return this.long2DoubleEntrySet();
    }

    public ObjectSortedSet<Long2DoubleMap.Entry> long2DoubleEntrySet();

    @Override
    public LongSortedSet keySet();

    @Override
    public DoubleCollection values();

    public LongComparator comparator();

    public static interface FastSortedEntrySet
    extends ObjectSortedSet<Long2DoubleMap.Entry>,
    Long2DoubleMap.FastEntrySet {
        public ObjectBidirectionalIterator<Long2DoubleMap.Entry> fastIterator();

        public ObjectBidirectionalIterator<Long2DoubleMap.Entry> fastIterator(Long2DoubleMap.Entry var1);
    }

}

