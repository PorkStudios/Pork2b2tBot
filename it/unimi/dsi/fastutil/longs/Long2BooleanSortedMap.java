/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.booleans.BooleanCollection;
import it.unimi.dsi.fastutil.longs.Long2BooleanMap;
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

public interface Long2BooleanSortedMap
extends Long2BooleanMap,
SortedMap<Long, Boolean> {
    public Long2BooleanSortedMap subMap(long var1, long var3);

    public Long2BooleanSortedMap headMap(long var1);

    public Long2BooleanSortedMap tailMap(long var1);

    public long firstLongKey();

    public long lastLongKey();

    @Deprecated
    default public Long2BooleanSortedMap subMap(Long from, Long to) {
        return this.subMap((long)from, (long)to);
    }

    @Deprecated
    default public Long2BooleanSortedMap headMap(Long to) {
        return this.headMap((long)to);
    }

    @Deprecated
    default public Long2BooleanSortedMap tailMap(Long from) {
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
    default public ObjectSortedSet<Map.Entry<Long, Boolean>> entrySet() {
        return this.long2BooleanEntrySet();
    }

    public ObjectSortedSet<Long2BooleanMap.Entry> long2BooleanEntrySet();

    @Override
    public LongSortedSet keySet();

    @Override
    public BooleanCollection values();

    public LongComparator comparator();

    public static interface FastSortedEntrySet
    extends ObjectSortedSet<Long2BooleanMap.Entry>,
    Long2BooleanMap.FastEntrySet {
        public ObjectBidirectionalIterator<Long2BooleanMap.Entry> fastIterator();

        public ObjectBidirectionalIterator<Long2BooleanMap.Entry> fastIterator(Long2BooleanMap.Entry var1);
    }

}

