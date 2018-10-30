/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.ints;

import it.unimi.dsi.fastutil.ints.IntCollection;
import it.unimi.dsi.fastutil.ints.IntIterator;
import java.util.Iterator;
import java.util.Set;

public interface IntSet
extends IntCollection,
Set<Integer> {
    @Override
    public IntIterator iterator();

    public boolean remove(int var1);

    @Deprecated
    @Override
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
}

