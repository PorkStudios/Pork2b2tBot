/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.booleans.BooleanCollection;
import it.unimi.dsi.fastutil.bytes.Byte2BooleanMap;
import it.unimi.dsi.fastutil.bytes.ByteComparator;
import it.unimi.dsi.fastutil.bytes.ByteSet;
import it.unimi.dsi.fastutil.bytes.ByteSortedSet;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;

public interface Byte2BooleanSortedMap
extends Byte2BooleanMap,
SortedMap<Byte, Boolean> {
    public Byte2BooleanSortedMap subMap(byte var1, byte var2);

    public Byte2BooleanSortedMap headMap(byte var1);

    public Byte2BooleanSortedMap tailMap(byte var1);

    public byte firstByteKey();

    public byte lastByteKey();

    @Deprecated
    default public Byte2BooleanSortedMap subMap(Byte from, Byte to) {
        return this.subMap((byte)from, (byte)to);
    }

    @Deprecated
    default public Byte2BooleanSortedMap headMap(Byte to) {
        return this.headMap((byte)to);
    }

    @Deprecated
    default public Byte2BooleanSortedMap tailMap(Byte from) {
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
    default public ObjectSortedSet<Map.Entry<Byte, Boolean>> entrySet() {
        return this.byte2BooleanEntrySet();
    }

    public ObjectSortedSet<Byte2BooleanMap.Entry> byte2BooleanEntrySet();

    @Override
    public ByteSortedSet keySet();

    @Override
    public BooleanCollection values();

    public ByteComparator comparator();

    public static interface FastSortedEntrySet
    extends ObjectSortedSet<Byte2BooleanMap.Entry>,
    Byte2BooleanMap.FastEntrySet {
        public ObjectBidirectionalIterator<Byte2BooleanMap.Entry> fastIterator();

        public ObjectBidirectionalIterator<Byte2BooleanMap.Entry> fastIterator(Byte2BooleanMap.Entry var1);
    }

}

