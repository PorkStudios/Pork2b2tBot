/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.booleans;

import it.unimi.dsi.fastutil.booleans.BooleanCollection;
import it.unimi.dsi.fastutil.booleans.BooleanIterator;
import java.util.Iterator;
import java.util.Set;

public interface BooleanSet
extends BooleanCollection,
Set<Boolean> {
    @Override
    public BooleanIterator iterator();

    public boolean remove(boolean var1);

    @Deprecated
    @Override
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
}

