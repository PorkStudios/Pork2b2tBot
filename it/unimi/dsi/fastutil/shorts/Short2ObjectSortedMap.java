/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.shorts;

import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectCollection;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import it.unimi.dsi.fastutil.shorts.Short2ObjectMap;
import it.unimi.dsi.fastutil.shorts.ShortComparator;
import it.unimi.dsi.fastutil.shorts.ShortSet;
import it.unimi.dsi.fastutil.shorts.ShortSortedSet;
import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;

public interface Short2ObjectSortedMap<V>
extends Short2ObjectMap<V>,
SortedMap<Short, V> {
    public Short2ObjectSortedMap<V> subMap(short var1, short var2);

    public Short2ObjectSortedMap<V> headMap(short var1);

    public Short2ObjectSortedMap<V> tailMap(short var1);

    public short firstShortKey();

    public short lastShortKey();

    @Deprecated
    default public Short2ObjectSortedMap<V> subMap(Short from, Short to) {
        return this.subMap((short)from, (short)to);
    }

    @Deprecated
    default public Short2ObjectSortedMap<V> headMap(Short to) {
        return this.headMap((short)to);
    }

    @Deprecated
    default public Short2ObjectSortedMap<V> tailMap(Short from) {
        return this.tailMap((short)from);
    }

    @Deprecated
    @Override
    default public Short firstKey() {
        return this.firstShortKey();
    }

    @Deprecated
    @Override
    default public Short lastKey() {
        return this.lastShortKey();
    }

    @Deprecated
    @Override
    default public ObjectSortedSet<Map.Entry<Short, V>> entrySet() {
        return this.short2ObjectEntrySet();
    }

    @Override
    public ObjectSortedSet<Short2ObjectMap.Entry<V>> short2ObjectEntrySet();

    @Override
    public ShortSortedSet keySet();

    @Override
    public ObjectCollection<V> values();

    public ShortComparator comparator();

    public static interface FastSortedEntrySet<V>
    extends ObjectSortedSet<Short2ObjectMap.Entry<V>>,
    Short2ObjectMap.FastEntrySet<V> {
        @Override
        public ObjectBidirectionalIterator<Short2ObjectMap.Entry<V>> fastIterator();

        public ObjectBidirectionalIterator<Short2ObjectMap.Entry<V>> fastIterator(Short2ObjectMap.Entry<V> var1);
    }

}

