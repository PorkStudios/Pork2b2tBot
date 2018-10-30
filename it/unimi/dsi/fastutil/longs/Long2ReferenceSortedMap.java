/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.longs.Long2ReferenceMap;
import it.unimi.dsi.fastutil.longs.LongComparator;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.longs.LongSortedSet;
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

public interface Long2ReferenceSortedMap<V>
extends Long2ReferenceMap<V>,
SortedMap<Long, V> {
    public Long2ReferenceSortedMap<V> subMap(long var1, long var3);

    public Long2ReferenceSortedMap<V> headMap(long var1);

    public Long2ReferenceSortedMap<V> tailMap(long var1);

    public long firstLongKey();

    public long lastLongKey();

    @Deprecated
    default public Long2ReferenceSortedMap<V> subMap(Long from, Long to) {
        return this.subMap((long)from, (long)to);
    }

    @Deprecated
    default public Long2ReferenceSortedMap<V> headMap(Long to) {
        return this.headMap((long)to);
    }

    @Deprecated
    default public Long2ReferenceSortedMap<V> tailMap(Long from) {
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
    default public ObjectSortedSet<Map.Entry<Long, V>> entrySet() {
        return this.long2ReferenceEntrySet();
    }

    @Override
    public ObjectSortedSet<Long2ReferenceMap.Entry<V>> long2ReferenceEntrySet();

    @Override
    public LongSortedSet keySet();

    @Override
    public ReferenceCollection<V> values();

    public LongComparator comparator();

    public static interface FastSortedEntrySet<V>
    extends ObjectSortedSet<Long2ReferenceMap.Entry<V>>,
    Long2ReferenceMap.FastEntrySet<V> {
        @Override
        public ObjectBidirectionalIterator<Long2ReferenceMap.Entry<V>> fastIterator();

        public ObjectBidirectionalIterator<Long2ReferenceMap.Entry<V>> fastIterator(Long2ReferenceMap.Entry<V> var1);
    }

}

