/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.LongComparator;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.longs.LongSortedSet;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectCollection;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;

public interface Long2ObjectSortedMap<V>
extends Long2ObjectMap<V>,
SortedMap<Long, V> {
    public Long2ObjectSortedMap<V> subMap(long var1, long var3);

    public Long2ObjectSortedMap<V> headMap(long var1);

    public Long2ObjectSortedMap<V> tailMap(long var1);

    public long firstLongKey();

    public long lastLongKey();

    @Deprecated
    default public Long2ObjectSortedMap<V> subMap(Long from, Long to) {
        return this.subMap((long)from, (long)to);
    }

    @Deprecated
    default public Long2ObjectSortedMap<V> headMap(Long to) {
        return this.headMap((long)to);
    }

    @Deprecated
    default public Long2ObjectSortedMap<V> tailMap(Long from) {
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
        return this.long2ObjectEntrySet();
    }

    @Override
    public ObjectSortedSet<Long2ObjectMap.Entry<V>> long2ObjectEntrySet();

    @Override
    public LongSortedSet keySet();

    @Override
    public ObjectCollection<V> values();

    public LongComparator comparator();

    public static interface FastSortedEntrySet<V>
    extends ObjectSortedSet<Long2ObjectMap.Entry<V>>,
    Long2ObjectMap.FastEntrySet<V> {
        @Override
        public ObjectBidirectionalIterator<Long2ObjectMap.Entry<V>> fastIterator();

        public ObjectBidirectionalIterator<Long2ObjectMap.Entry<V>> fastIterator(Long2ObjectMap.Entry<V> var1);
    }

}

