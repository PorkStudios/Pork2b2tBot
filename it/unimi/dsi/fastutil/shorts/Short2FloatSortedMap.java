/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.shorts;

import it.unimi.dsi.fastutil.floats.FloatCollection;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import it.unimi.dsi.fastutil.shorts.Short2FloatMap;
import it.unimi.dsi.fastutil.shorts.ShortComparator;
import it.unimi.dsi.fastutil.shorts.ShortSet;
import it.unimi.dsi.fastutil.shorts.ShortSortedSet;
import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;

public interface Short2FloatSortedMap
extends Short2FloatMap,
SortedMap<Short, Float> {
    public Short2FloatSortedMap subMap(short var1, short var2);

    public Short2FloatSortedMap headMap(short var1);

    public Short2FloatSortedMap tailMap(short var1);

    public short firstShortKey();

    public short lastShortKey();

    @Deprecated
    default public Short2FloatSortedMap subMap(Short from, Short to) {
        return this.subMap((short)from, (short)to);
    }

    @Deprecated
    default public Short2FloatSortedMap headMap(Short to) {
        return this.headMap((short)to);
    }

    @Deprecated
    default public Short2FloatSortedMap tailMap(Short from) {
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
    default public ObjectSortedSet<Map.Entry<Short, Float>> entrySet() {
        return this.short2FloatEntrySet();
    }

    public ObjectSortedSet<Short2FloatMap.Entry> short2FloatEntrySet();

    @Override
    public ShortSortedSet keySet();

    @Override
    public FloatCollection values();

    public ShortComparator comparator();

    public static interface FastSortedEntrySet
    extends ObjectSortedSet<Short2FloatMap.Entry>,
    Short2FloatMap.FastEntrySet {
        public ObjectBidirectionalIterator<Short2FloatMap.Entry> fastIterator();

        public ObjectBidirectionalIterator<Short2FloatMap.Entry> fastIterator(Short2FloatMap.Entry var1);
    }

}

