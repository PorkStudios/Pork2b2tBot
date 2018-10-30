/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.shorts;

import it.unimi.dsi.fastutil.ints.IntCollection;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import it.unimi.dsi.fastutil.shorts.Short2IntMap;
import it.unimi.dsi.fastutil.shorts.ShortComparator;
import it.unimi.dsi.fastutil.shorts.ShortSet;
import it.unimi.dsi.fastutil.shorts.ShortSortedSet;
import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;

public interface Short2IntSortedMap
extends Short2IntMap,
SortedMap<Short, Integer> {
    public Short2IntSortedMap subMap(short var1, short var2);

    public Short2IntSortedMap headMap(short var1);

    public Short2IntSortedMap tailMap(short var1);

    public short firstShortKey();

    public short lastShortKey();

    @Deprecated
    default public Short2IntSortedMap subMap(Short from, Short to) {
        return this.subMap((short)from, (short)to);
    }

    @Deprecated
    default public Short2IntSortedMap headMap(Short to) {
        return this.headMap((short)to);
    }

    @Deprecated
    default public Short2IntSortedMap tailMap(Short from) {
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
    default public ObjectSortedSet<Map.Entry<Short, Integer>> entrySet() {
        return this.short2IntEntrySet();
    }

    public ObjectSortedSet<Short2IntMap.Entry> short2IntEntrySet();

    @Override
    public ShortSortedSet keySet();

    @Override
    public IntCollection values();

    public ShortComparator comparator();

    public static interface FastSortedEntrySet
    extends ObjectSortedSet<Short2IntMap.Entry>,
    Short2IntMap.FastEntrySet {
        public ObjectBidirectionalIterator<Short2IntMap.Entry> fastIterator();

        public ObjectBidirectionalIterator<Short2IntMap.Entry> fastIterator(Short2IntMap.Entry var1);
    }

}

