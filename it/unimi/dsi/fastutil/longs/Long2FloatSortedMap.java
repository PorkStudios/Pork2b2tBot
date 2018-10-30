/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.floats.FloatCollection;
import it.unimi.dsi.fastutil.longs.Long2FloatMap;
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

public interface Long2FloatSortedMap
extends Long2FloatMap,
SortedMap<Long, Float> {
    public Long2FloatSortedMap subMap(long var1, long var3);

    public Long2FloatSortedMap headMap(long var1);

    public Long2FloatSortedMap tailMap(long var1);

    public long firstLongKey();

    public long lastLongKey();

    @Deprecated
    default public Long2FloatSortedMap subMap(Long from, Long to) {
        return this.subMap((long)from, (long)to);
    }

    @Deprecated
    default public Long2FloatSortedMap headMap(Long to) {
        return this.headMap((long)to);
    }

    @Deprecated
    default public Long2FloatSortedMap tailMap(Long from) {
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
    default public ObjectSortedSet<Map.Entry<Long, Float>> entrySet() {
        return this.long2FloatEntrySet();
    }

    public ObjectSortedSet<Long2FloatMap.Entry> long2FloatEntrySet();

    @Override
    public LongSortedSet keySet();

    @Override
    public FloatCollection values();

    public LongComparator comparator();

    public static interface FastSortedEntrySet
    extends ObjectSortedSet<Long2FloatMap.Entry>,
    Long2FloatMap.FastEntrySet {
        public ObjectBidirectionalIterator<Long2FloatMap.Entry> fastIterator();

        public ObjectBidirectionalIterator<Long2FloatMap.Entry> fastIterator(Long2FloatMap.Entry var1);
    }

}

