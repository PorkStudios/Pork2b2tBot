/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.booleans;

import it.unimi.dsi.fastutil.booleans.BooleanIterable;
import it.unimi.dsi.fastutil.booleans.BooleanIterator;
import java.util.Collection;
import java.util.Iterator;

public interface BooleanCollection
extends Collection<Boolean>,
BooleanIterable {
    @Override
    public BooleanIterator iterator();

    @Override
    public boolean add(boolean var1);

    public boolean contains(boolean var1);

    public boolean rem(boolean var1);

    @Deprecated
    @Override
    public boolean add(Boolean var1);

    @Deprecated
    @Override
    public boolean contains(Object var1);

    @Deprecated
    @Override
    public boolean remove(Object var1);

    public boolean[] toBooleanArray();

    @Deprecated
    public boolean[] toBooleanArray(boolean[] var1);

    public boolean[] toArray(boolean[] var1);

    public boolean addAll(BooleanCollection var1);

    public boolean containsAll(BooleanCollection var1);

    public boolean removeAll(BooleanCollection var1);

    public boolean retainAll(BooleanCollection var1);
}

