/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.ints;

import it.unimi.dsi.fastutil.ints.IntIterable;
import it.unimi.dsi.fastutil.ints.IntIterator;
import java.util.Collection;
import java.util.Iterator;

public interface IntCollection
extends Collection<Integer>,
IntIterable {
    @Override
    public IntIterator iterator();

    @Override
    public boolean add(int var1);

    public boolean contains(int var1);

    public boolean rem(int var1);

    @Deprecated
    @Override
    public boolean add(Integer var1);

    @Deprecated
    @Override
    public boolean contains(Object var1);

    @Deprecated
    @Override
    public boolean remove(Object var1);

    public int[] toIntArray();

    @Deprecated
    public int[] toIntArray(int[] var1);

    public int[] toArray(int[] var1);

    public boolean addAll(IntCollection var1);

    public boolean containsAll(IntCollection var1);

    public boolean removeAll(IntCollection var1);

    public boolean retainAll(IntCollection var1);
}

