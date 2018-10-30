/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.bytes.Byte2DoubleMap;
import it.unimi.dsi.fastutil.bytes.ByteComparator;
import it.unimi.dsi.fastutil.bytes.ByteSet;
import it.unimi.dsi.fastutil.bytes.ByteSortedSet;
import it.unimi.dsi.fastutil.doubles.DoubleCollection;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;

public interface Byte2DoubleSortedMap
extends Byte2DoubleMap,
SortedMap<Byte, Double> {
    public Byte2DoubleSortedMap subMap(byte var1, byte var2);

    public Byte2DoubleSortedMap headMap(byte var1);

    public Byte2DoubleSortedMap tailMap(byte var1);

    public byte firstByteKey();

    public byte lastByteKey();

    @Deprecated
    default public Byte2DoubleSortedMap subMap(Byte from, Byte to) {
        return this.subMap((byte)from, (byte)to);
    }

    @Deprecated
    default public Byte2DoubleSortedMap headMap(Byte to) {
        return this.headMap((byte)to);
    }

    @Deprecated
    default public Byte2DoubleSortedMap tailMap(Byte from) {
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
    default public ObjectSortedSet<Map.Entry<Byte, Double>> entrySet() {
        return this.byte2DoubleEntrySet();
    }

    public ObjectSortedSet<Byte2DoubleMap.Entry> byte2DoubleEntrySet();

    @Override
    public ByteSortedSet keySet();

    @Override
    public DoubleCollection values();

    public ByteComparator comparator();

    public static interface FastSortedEntrySet
    extends ObjectSortedSet<Byte2DoubleMap.Entry>,
    Byte2DoubleMap.FastEntrySet {
        public ObjectBidirectionalIterator<Byte2DoubleMap.Entry> fastIterator();

        public ObjectBidirectionalIterator<Byte2DoubleMap.Entry> fastIterator(Byte2DoubleMap.Entry var1);
    }

}

