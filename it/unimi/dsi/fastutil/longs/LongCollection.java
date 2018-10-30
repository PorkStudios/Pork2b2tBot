/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.longs.LongIterable;
import it.unimi.dsi.fastutil.longs.LongIterator;
import java.util.Collection;
import java.util.Iterator;

public interface LongCollection
extends Collection<Long>,
LongIterable {
    @Override
    public LongIterator iterator();

    @Override
    public boolean add(long var1);

    public boolean contains(long var1);

    public boolean rem(long var1);

    @Deprecated
    @Override
    public boolean add(Long var1);

    @Deprecated
    @Override
    public boolean contains(Object var1);

    @Deprecated
    @Override
    public boolean remove(Object var1);

    public long[] toLongArray();

    @Deprecated
    public long[] toLongArray(long[] var1);

    public long[] toArray(long[] var1);

    public boolean addAll(LongCollection var1);

    public boolean containsAll(LongCollection var1);

    public boolean removeAll(LongCollection var1);

    public boolean retainAll(LongCollection var1);
}

