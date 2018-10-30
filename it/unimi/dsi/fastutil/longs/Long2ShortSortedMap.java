/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.longs.Long2ShortMap;
import it.unimi.dsi.fastutil.longs.LongComparator;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.longs.LongSortedSet;
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

public interface Long2ShortSortedMap
extends Long2ShortMap,
SortedMap<Long, Short> {
    public Long2ShortSortedMap subMap(long var1, long var3);

    public Long2ShortSortedMap headMap(long var1);

    public Long2ShortSortedMap tailMap(long var1);

    public long firstLongKey();

    public long lastLongKey();

    @Deprecated
    default public Long2ShortSortedMap subMap(Long from, Long to) {
        return this.subMap((long)from, (long)to);
    }

    @Deprecated
    default public Long2ShortSortedMap headMap(Long to) {
        return this.headMap((long)to);
    }

    @Deprecated
    default public Long2ShortSortedMap tailMap(Long from) {
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
    default public ObjectSortedSet<Map.Entry<Long, Short>> entrySet() {
        return this.long2ShortEntrySet();
    }

    public ObjectSortedSet<Long2ShortMap.Entry> long2ShortEntrySet();

    @Override
    public LongSortedSet keySet();

    @Override
    public ShortCollection values();

    public LongComparator comparator();

    public static interface FastSortedEntrySet
    extends ObjectSortedSet<Long2ShortMap.Entry>,
    Long2ShortMap.FastEntrySet {
        public ObjectBidirectionalIterator<Long2ShortMap.Entry> fastIterator();

        public ObjectBidirectionalIterator<Long2ShortMap.Entry> fastIterator(Long2ShortMap.Entry var1);
    }

}

