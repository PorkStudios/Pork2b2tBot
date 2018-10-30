/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.shorts;

import it.unimi.dsi.fastutil.shorts.ShortCollection;
import it.unimi.dsi.fastutil.shorts.ShortIterator;
import java.util.Iterator;
import java.util.Set;

public interface ShortSet
extends ShortCollection,
Set<Short> {
    @Override
    public ShortIterator iterator();

    public boolean remove(short var1);

    @Deprecated
    @Override
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
}

