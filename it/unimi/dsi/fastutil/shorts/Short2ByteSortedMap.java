/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.shorts;

import it.unimi.dsi.fastutil.bytes.ByteCollection;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import it.unimi.dsi.fastutil.shorts.Short2ByteMap;
import it.unimi.dsi.fastutil.shorts.ShortComparator;
import it.unimi.dsi.fastutil.shorts.ShortSet;
import it.unimi.dsi.fastutil.shorts.ShortSortedSet;
import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;

public interface Short2ByteSortedMap
extends Short2ByteMap,
SortedMap<Short, Byte> {
    public Short2ByteSortedMap subMap(short var1, short var2);

    public Short2ByteSortedMap headMap(short var1);

    public Short2ByteSortedMap tailMap(short var1);

    public short firstShortKey();

    public short lastShortKey();

    @Deprecated
    default public Short2ByteSortedMap subMap(Short from, Short to) {
        return this.subMap((short)from, (short)to);
    }

    @Deprecated
    default public Short2ByteSortedMap headMap(Short to) {
        return this.headMap((short)to);
    }

    @Deprecated
    default public Short2ByteSortedMap tailMap(Short from) {
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
    default public ObjectSortedSet<Map.Entry<Short, Byte>> entrySet() {
        return this.short2ByteEntrySet();
    }

    public ObjectSortedSet<Short2ByteMap.Entry> short2ByteEntrySet();

    @Override
    public ShortSortedSet keySet();

    @Override
    public ByteCollection values();

    public ShortComparator comparator();

    public static interface FastSortedEntrySet
    extends ObjectSortedSet<Short2ByteMap.Entry>,
    Short2ByteMap.FastEntrySet {
        public ObjectBidirectionalIterator<Short2ByteMap.Entry> fastIterator();

        public ObjectBidirectionalIterator<Short2ByteMap.Entry> fastIterator(Short2ByteMap.Entry var1);
    }

}

