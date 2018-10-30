/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.bytes.Byte2FloatMap;
import it.unimi.dsi.fastutil.bytes.ByteComparator;
import it.unimi.dsi.fastutil.bytes.ByteSet;
import it.unimi.dsi.fastutil.bytes.ByteSortedSet;
import it.unimi.dsi.fastutil.floats.FloatCollection;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;

public interface Byte2FloatSortedMap
extends Byte2FloatMap,
SortedMap<Byte, Float> {
    public Byte2FloatSortedMap subMap(byte var1, byte var2);

    public Byte2FloatSortedMap headMap(byte var1);

    public Byte2FloatSortedMap tailMap(byte var1);

    public byte firstByteKey();

    public byte lastByteKey();

    @Deprecated
    default public Byte2FloatSortedMap subMap(Byte from, Byte to) {
        return this.subMap((byte)from, (byte)to);
    }

    @Deprecated
    default public Byte2FloatSortedMap headMap(Byte to) {
        return this.headMap((byte)to);
    }

    @Deprecated
    default public Byte2FloatSortedMap tailMap(Byte from) {
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
    default public ObjectSortedSet<Map.Entry<Byte, Float>> entrySet() {
        return this.byte2FloatEntrySet();
    }

    public ObjectSortedSet<Byte2FloatMap.Entry> byte2FloatEntrySet();

    @Override
    public ByteSortedSet keySet();

    @Override
    public FloatCollection values();

    public ByteComparator comparator();

    public static interface FastSortedEntrySet
    extends ObjectSortedSet<Byte2FloatMap.Entry>,
    Byte2FloatMap.FastEntrySet {
        public ObjectBidirectionalIterator<Byte2FloatMap.Entry> fastIterator();

        public ObjectBidirectionalIterator<Byte2FloatMap.Entry> fastIterator(Byte2FloatMap.Entry var1);
    }

}

