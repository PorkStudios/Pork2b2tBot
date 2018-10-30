/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.bytes.Byte2ShortMap;
import it.unimi.dsi.fastutil.bytes.ByteComparator;
import it.unimi.dsi.fastutil.bytes.ByteSet;
import it.unimi.dsi.fastutil.bytes.ByteSortedSet;
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

public interface Byte2ShortSortedMap
extends Byte2ShortMap,
SortedMap<Byte, Short> {
    public Byte2ShortSortedMap subMap(byte var1, byte var2);

    public Byte2ShortSortedMap headMap(byte var1);

    public Byte2ShortSortedMap tailMap(byte var1);

    public byte firstByteKey();

    public byte lastByteKey();

    @Deprecated
    default public Byte2ShortSortedMap subMap(Byte from, Byte to) {
        return this.subMap((byte)from, (byte)to);
    }

    @Deprecated
    default public Byte2ShortSortedMap headMap(Byte to) {
        return this.headMap((byte)to);
    }

    @Deprecated
    default public Byte2ShortSortedMap tailMap(Byte from) {
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
    default public ObjectSortedSet<Map.Entry<Byte, Short>> entrySet() {
        return this.byte2ShortEntrySet();
    }

    public ObjectSortedSet<Byte2ShortMap.Entry> byte2ShortEntrySet();

    @Override
    public ByteSortedSet keySet();

    @Override
    public ShortCollection values();

    public ByteComparator comparator();

    public static interface FastSortedEntrySet
    extends ObjectSortedSet<Byte2ShortMap.Entry>,
    Byte2ShortMap.FastEntrySet {
        public ObjectBidirectionalIterator<Byte2ShortMap.Entry> fastIterator();

        public ObjectBidirectionalIterator<Byte2ShortMap.Entry> fastIterator(Byte2ShortMap.Entry var1);
    }

}

