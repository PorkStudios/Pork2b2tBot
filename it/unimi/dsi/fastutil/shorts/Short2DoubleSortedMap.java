/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.shorts;

import it.unimi.dsi.fastutil.doubles.DoubleCollection;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import it.unimi.dsi.fastutil.shorts.Short2DoubleMap;
import it.unimi.dsi.fastutil.shorts.ShortComparator;
import it.unimi.dsi.fastutil.shorts.ShortSet;
import it.unimi.dsi.fastutil.shorts.ShortSortedSet;
import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;

public interface Short2DoubleSortedMap
extends Short2DoubleMap,
SortedMap<Short, Double> {
    public Short2DoubleSortedMap subMap(short var1, short var2);

    public Short2DoubleSortedMap headMap(short var1);

    public Short2DoubleSortedMap tailMap(short var1);

    public short firstShortKey();

    public short lastShortKey();

    @Deprecated
    default public Short2DoubleSortedMap subMap(Short from, Short to) {
        return this.subMap((short)from, (short)to);
    }

    @Deprecated
    default public Short2DoubleSortedMap headMap(Short to) {
        return this.headMap((short)to);
    }

    @Deprecated
    default public Short2DoubleSortedMap tailMap(Short from) {
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
    default public ObjectSortedSet<Map.Entry<Short, Double>> entrySet() {
        return this.short2DoubleEntrySet();
    }

    public ObjectSortedSet<Short2DoubleMap.Entry> short2DoubleEntrySet();

    @Override
    public ShortSortedSet keySet();

    @Override
    public DoubleCollection values();

    public ShortComparator comparator();

    public static interface FastSortedEntrySet
    extends ObjectSortedSet<Short2DoubleMap.Entry>,
    Short2DoubleMap.FastEntrySet {
        public ObjectBidirectionalIterator<Short2DoubleMap.Entry> fastIterator();

        public ObjectBidirectionalIterator<Short2DoubleMap.Entry> fastIterator(Short2DoubleMap.Entry var1);
    }

}

