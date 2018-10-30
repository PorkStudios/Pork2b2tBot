/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.ints;

import it.unimi.dsi.fastutil.ints.IntBidirectionalIterable;
import it.unimi.dsi.fastutil.ints.IntBidirectionalIterator;
import it.unimi.dsi.fastutil.ints.IntComparator;
import it.unimi.dsi.fastutil.ints.IntIterator;
import it.unimi.dsi.fastutil.ints.IntSet;
import java.util.Comparator;
import java.util.Iterator;
import java.util.SortedSet;

public interface IntSortedSet
extends IntSet,
SortedSet<Integer>,
IntBidirectionalIterable {
    public IntBidirectionalIterator iterator(int var1);

    @Override
    public IntBidirectionalIterator iterator();

    public IntSortedSet subSet(int var1, int var2);

    public IntSortedSet headSet(int var1);

    public IntSortedSet tailSet(int var1);

    public IntComparator comparator();

    public int firstInt();

    public int lastInt();

    @Deprecated
    default public IntSortedSet subSet(Integer from, Integer to) {
        return this.subSet((int)from, (int)to);
    }

    @Deprecated
    default public IntSortedSet headSet(Integer to) {
        return this.headSet((int)to);
    }

    @Deprecated
    default public IntSortedSet tailSet(Integer from) {
        return this.tailSet((int)from);
    }

    @Deprecated
    @Override
    default public Integer first() {
        return this.firstInt();
    }

    @Deprecated
    @Override
    default public Integer last() {
        return this.lastInt();
    }
}

