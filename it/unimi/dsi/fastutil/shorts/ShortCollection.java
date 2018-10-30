/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.shorts;

import it.unimi.dsi.fastutil.shorts.ShortIterable;
import it.unimi.dsi.fastutil.shorts.ShortIterator;
import java.util.Collection;
import java.util.Iterator;

public interface ShortCollection
extends Collection<Short>,
ShortIterable {
    @Override
    public ShortIterator iterator();

    @Override
    public boolean add(short var1);

    public boolean contains(short var1);

    public boolean rem(short var1);

    @Deprecated
    @Override
    public boolean add(Short var1);

    @Deprecated
    @Override
    public boolean contains(Object var1);

    @Deprecated
    @Override
    public boolean remove(Object var1);

    public short[] toShortArray();

    @Deprecated
    public short[] toShortArray(short[] var1);

    public short[] toArray(short[] var1);

    public boolean addAll(ShortCollection var1);

    public boolean containsAll(ShortCollection var1);

    public boolean removeAll(ShortCollection var1);

    public boolean retainAll(ShortCollection var1);
}

