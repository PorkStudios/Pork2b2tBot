/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.shorts;

import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import it.unimi.dsi.fastutil.objects.ReferenceCollection;
import it.unimi.dsi.fastutil.shorts.Short2ReferenceMap;
import it.unimi.dsi.fastutil.shorts.ShortComparator;
import it.unimi.dsi.fastutil.shorts.ShortSet;
import it.unimi.dsi.fastutil.shorts.ShortSortedSet;
import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;

public interface Short2ReferenceSortedMap<V>
extends Short2ReferenceMap<V>,
SortedMap<Short, V> {
    public Short2ReferenceSortedMap<V> subMap(short var1, short var2);

    public Short2ReferenceSortedMap<V> headMap(short var1);

    public Short2ReferenceSortedMap<V> tailMap(short var1);

    public short firstShortKey();

    public short lastShortKey();

    @Deprecated
    default public Short2ReferenceSortedMap<V> subMap(Short from, Short to) {
        return this.subMap((short)from, (short)to);
    }

    @Deprecated
    default public Short2ReferenceSortedMap<V> headMap(Short to) {
        return this.headMap((short)to);
    }

    @Deprecated
    default public Short2ReferenceSortedMap<V> tailMap(Short from) {
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
        return this.short2ReferenceEntrySet();
    }

    @Override
    public ObjectSortedSet<Short2ReferenceMap.Entry<V>> short2ReferenceEntrySet();

    @Override
    public ShortSortedSet keySet();

    @Override
    public ReferenceCollection<V> values();

    public ShortComparator comparator();

    public static interface FastSortedEntrySet<V>
    extends ObjectSortedSet<Short2ReferenceMap.Entry<V>>,
    Short2ReferenceMap.FastEntrySet<V> {
        @Override
        public ObjectBidirectionalIterator<Short2ReferenceMap.Entry<V>> fastIterator();

        public ObjectBidirectionalIterator<Short2ReferenceMap.Entry<V>> fastIterator(Short2ReferenceMap.Entry<V> var1);
    }

}

