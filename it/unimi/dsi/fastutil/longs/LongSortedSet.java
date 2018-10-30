/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.longs.LongBidirectionalIterable;
import it.unimi.dsi.fastutil.longs.LongBidirectionalIterator;
import it.unimi.dsi.fastutil.longs.LongComparator;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongSet;
import java.util.Comparator;
import java.util.Iterator;
import java.util.SortedSet;

public interface LongSortedSet
extends LongSet,
SortedSet<Long>,
LongBidirectionalIterable {
    public LongBidirectionalIterator iterator(long var1);

    @Override
    public LongBidirectionalIterator iterator();

    public LongSortedSet subSet(long var1, long var3);

    public LongSortedSet headSet(long var1);

    public LongSortedSet tailSet(long var1);

    public LongComparator comparator();

    public long firstLong();

    public long lastLong();

    @Deprecated
    default public LongSortedSet subSet(Long from, Long to) {
        return this.subSet((long)from, (long)to);
    }

    @Deprecated
    default public LongSortedSet headSet(Long to) {
        return this.headSet((long)to);
    }

    @Deprecated
    default public LongSortedSet tailSet(Long from) {
        return this.tailSet((long)from);
    }

    @Deprecated
    @Override
    default public Long first() {
        return this.firstLong();
    }

    @Deprecated
    @Override
    default public Long last() {
        return this.lastLong();
    }
}

