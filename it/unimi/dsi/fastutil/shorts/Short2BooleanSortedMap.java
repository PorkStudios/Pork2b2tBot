/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.shorts;

import it.unimi.dsi.fastutil.booleans.BooleanCollection;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import it.unimi.dsi.fastutil.shorts.Short2BooleanMap;
import it.unimi.dsi.fastutil.shorts.ShortComparator;
import it.unimi.dsi.fastutil.shorts.ShortSet;
import it.unimi.dsi.fastutil.shorts.ShortSortedSet;
import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;

public interface Short2BooleanSortedMap
extends Short2BooleanMap,
SortedMap<Short, Boolean> {
    public Short2BooleanSortedMap subMap(short var1, short var2);

    public Short2BooleanSortedMap headMap(short var1);

    public Short2BooleanSortedMap tailMap(short var1);

    public short firstShortKey();

    public short lastShortKey();

    @Deprecated
    default public Short2BooleanSortedMap subMap(Short from, Short to) {
        return this.subMap((short)from, (short)to);
    }

    @Deprecated
    default public Short2BooleanSortedMap headMap(Short to) {
        return this.headMap((short)to);
    }

    @Deprecated
    default public Short2BooleanSortedMap tailMap(Short from) {
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
    default public ObjectSortedSet<Map.Entry<Short, Boolean>> entrySet() {
        return this.short2BooleanEntrySet();
    }

    public ObjectSortedSet<Short2BooleanMap.Entry> short2BooleanEntrySet();

    @Override
    public ShortSortedSet keySet();

    @Override
    public BooleanCollection values();

    public ShortComparator comparator();

    public static interface FastSortedEntrySet
    extends ObjectSortedSet<Short2BooleanMap.Entry>,
    Short2BooleanMap.FastEntrySet {
        public ObjectBidirectionalIterator<Short2BooleanMap.Entry> fastIterator();

        public ObjectBidirectionalIterator<Short2BooleanMap.Entry> fastIterator(Short2BooleanMap.Entry var1);
    }

}

