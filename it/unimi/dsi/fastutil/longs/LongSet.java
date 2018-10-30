/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.longs.LongCollection;
import it.unimi.dsi.fastutil.longs.LongIterator;
import java.util.Iterator;
import java.util.Set;

public interface LongSet
extends LongCollection,
Set<Long> {
    @Override
    public LongIterator iterator();

    public boolean remove(long var1);

    @Deprecated
    @Override
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
}

