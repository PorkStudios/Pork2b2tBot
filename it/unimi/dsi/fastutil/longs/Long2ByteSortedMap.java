/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.bytes.ByteCollection;
import it.unimi.dsi.fastutil.longs.Long2ByteMap;
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

public interface Long2ByteSortedMap
extends Long2ByteMap,
SortedMap<Long, Byte> {
    public Long2ByteSortedMap subMap(long var1, long var3);

    public Long2ByteSortedMap headMap(long var1);

    public Long2ByteSortedMap tailMap(long var1);

    public long firstLongKey();

    public long lastLongKey();

    @Deprecated
    default public Long2ByteSortedMap subMap(Long from, Long to) {
        return this.subMap((long)from, (long)to);
    }

    @Deprecated
    default public Long2ByteSortedMap headMap(Long to) {
        return this.headMap((long)to);
    }

    @Deprecated
    default public Long2ByteSortedMap tailMap(Long from) {
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
    default public ObjectSortedSet<Map.Entry<Long, Byte>> entrySet() {
        return this.long2ByteEntrySet();
    }

    public ObjectSortedSet<Long2ByteMap.Entry> long2ByteEntrySet();

    @Override
    public LongSortedSet keySet();

    @Override
    public ByteCollection values();

    public LongComparator comparator();

    public static interface FastSortedEntrySet
    extends ObjectSortedSet<Long2ByteMap.Entry>,
    Long2ByteMap.FastEntrySet {
        public ObjectBidirectionalIterator<Long2ByteMap.Entry> fastIterator();

        public ObjectBidirectionalIterator<Long2ByteMap.Entry> fastIterator(Long2ByteMap.Entry var1);
    }

}

