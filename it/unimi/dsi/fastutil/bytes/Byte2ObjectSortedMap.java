/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.bytes.Byte2ObjectMap;
import it.unimi.dsi.fastutil.bytes.ByteComparator;
import it.unimi.dsi.fastutil.bytes.ByteSet;
import it.unimi.dsi.fastutil.bytes.ByteSortedSet;
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

public interface Byte2ObjectSortedMap<V>
extends Byte2ObjectMap<V>,
SortedMap<Byte, V> {
    public Byte2ObjectSortedMap<V> subMap(byte var1, byte var2);

    public Byte2ObjectSortedMap<V> headMap(byte var1);

    public Byte2ObjectSortedMap<V> tailMap(byte var1);

    public byte firstByteKey();

    public byte lastByteKey();

    @Deprecated
    default public Byte2ObjectSortedMap<V> subMap(Byte from, Byte to) {
        return this.subMap((byte)from, (byte)to);
    }

    @Deprecated
    default public Byte2ObjectSortedMap<V> headMap(Byte to) {
        return this.headMap((byte)to);
    }

    @Deprecated
    default public Byte2ObjectSortedMap<V> tailMap(Byte from) {
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
    default public ObjectSortedSet<Map.Entry<Byte, V>> entrySet() {
        return this.byte2ObjectEntrySet();
    }

    @Override
    public ObjectSortedSet<Byte2ObjectMap.Entry<V>> byte2ObjectEntrySet();

    @Override
    public ByteSortedSet keySet();

    @Override
    public ObjectCollection<V> values();

    public ByteComparator comparator();

    public static interface FastSortedEntrySet<V>
    extends ObjectSortedSet<Byte2ObjectMap.Entry<V>>,
    Byte2ObjectMap.FastEntrySet<V> {
        @Override
        public ObjectBidirectionalIterator<Byte2ObjectMap.Entry<V>> fastIterator();

        public ObjectBidirectionalIterator<Byte2ObjectMap.Entry<V>> fastIterator(Byte2ObjectMap.Entry<V> var1);
    }

}

