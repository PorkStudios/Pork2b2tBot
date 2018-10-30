/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.bytes.Byte2LongMap;
import it.unimi.dsi.fastutil.bytes.ByteComparator;
import it.unimi.dsi.fastutil.bytes.ByteSet;
import it.unimi.dsi.fastutil.bytes.ByteSortedSet;
import it.unimi.dsi.fastutil.longs.LongCollection;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;

public interface Byte2LongSortedMap
extends Byte2LongMap,
SortedMap<Byte, Long> {
    public Byte2LongSortedMap subMap(byte var1, byte var2);

    public Byte2LongSortedMap headMap(byte var1);

    public Byte2LongSortedMap tailMap(byte var1);

    public byte firstByteKey();

    public byte lastByteKey();

    @Deprecated
    default public Byte2LongSortedMap subMap(Byte from, Byte to) {
        return this.subMap((byte)from, (byte)to);
    }

    @Deprecated
    default public Byte2LongSortedMap headMap(Byte to) {
        return this.headMap((byte)to);
    }

    @Deprecated
    default public Byte2LongSortedMap tailMap(Byte from) {
        return this.tailMap((byte)from);
    }

    @Deprecated
    @Override
    default public Byte firstKey() {
        return this.firstByteKey();
    }

    @Deprecated
    @Override
    default public Byte lastKey() {
        return this.lastByteKey();
    }

    @Deprecated
    @Override
    default public ObjectSortedSet<Map.Entry<Byte, Long>> entrySet() {
        return this.byte2LongEntrySet();
    }

    public ObjectSortedSet<Byte2LongMap.Entry> byte2LongEntrySet();

    @Override
    public ByteSortedSet keySet();

    @Override
    public LongCollection values();

    public ByteComparator comparator();

    public static interface FastSortedEntrySet
    extends ObjectSortedSet<Byte2LongMap.Entry>,
    Byte2LongMap.FastEntrySet {
        public ObjectBidirectionalIterator<Byte2LongMap.Entry> fastIterator();

        public ObjectBidirectionalIterator<Byte2LongMap.Entry> fastIterator(Byte2LongMap.Entry var1);
    }

}

