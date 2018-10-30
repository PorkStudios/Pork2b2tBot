/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.ints.IntCollection;
import it.unimi.dsi.fastutil.longs.Long2IntMap;
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

public interface Long2IntSortedMap
extends Long2IntMap,
SortedMap<Long, Integer> {
    public Long2IntSortedMap subMap(long var1, long var3);

    public Long2IntSortedMap headMap(long var1);

    public Long2IntSortedMap tailMap(long var1);

    public long firstLongKey();

    public long lastLongKey();

    @Deprecated
    default public Long2IntSortedMap subMap(Long from, Long to) {
        return this.subMap((long)from, (long)to);
    }

    @Deprecated
    default public Long2IntSortedMap headMap(Long to) {
        return this.headMap((long)to);
    }

    @Deprecated
    default public Long2IntSortedMap tailMap(Long from) {
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
    default public ObjectSortedSet<Map.Entry<Long, Integer>> entrySet() {
        return this.long2IntEntrySet();
    }

    public ObjectSortedSet<Long2IntMap.Entry> long2IntEntrySet();

    @Override
    public LongSortedSet keySet();

    @Override
    public IntCollection values();

    public LongComparator comparator();

    public static interface FastSortedEntrySet
    extends ObjectSortedSet<Long2IntMap.Entry>,
    Long2IntMap.FastEntrySet {
        public ObjectBidirectionalIterator<Long2IntMap.Entry> fastIterator();

        public ObjectBidirectionalIterator<Long2IntMap.Entry> fastIterator(Long2IntMap.Entry var1);
    }

}

