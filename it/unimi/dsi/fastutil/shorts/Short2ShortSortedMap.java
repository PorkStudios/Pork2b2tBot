/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.shorts;

import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import it.unimi.dsi.fastutil.shorts.Short2ShortMap;
import it.unimi.dsi.fastutil.shorts.ShortCollection;
import it.unimi.dsi.fastutil.shorts.ShortComparator;
import it.unimi.dsi.fastutil.shorts.ShortSet;
import it.unimi.dsi.fastutil.shorts.ShortSortedSet;
import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;

public interface Short2ShortSortedMap
extends Short2ShortMap,
SortedMap<Short, Short> {
    public Short2ShortSortedMap subMap(short var1, short var2);

    public Short2ShortSortedMap headMap(short var1);

    public Short2ShortSortedMap tailMap(short var1);

    public short firstShortKey();

    public short lastShortKey();

    @Deprecated
    default public Short2ShortSortedMap subMap(Short from, Short to) {
        return this.subMap((short)from, (short)to);
    }

    @Deprecated
    default public Short2ShortSortedMap headMap(Short to) {
        return this.headMap((short)to);
    }

    @Deprecated
    default public Short2ShortSortedMap tailMap(Short from) {
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
    default public ObjectSortedSet<Map.Entry<Short, Short>> entrySet() {
        return this.short2ShortEntrySet();
    }

    public ObjectSortedSet<Short2ShortMap.Entry> short2ShortEntrySet();

    @Override
    public ShortSortedSet keySet();

    @Override
    public ShortCollection values();

    public ShortComparator comparator();

    public static interface FastSortedEntrySet
    extends ObjectSortedSet<Short2ShortMap.Entry>,
    Short2ShortMap.FastEntrySet {
        public ObjectBidirectionalIterator<Short2ShortMap.Entry> fastIterator();

        public ObjectBidirectionalIterator<Short2ShortMap.Entry> fastIterator(Short2ShortMap.Entry var1);
    }

}

