/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.floats.FloatCollection;
import it.unimi.dsi.fastutil.floats.FloatIterator;
import java.util.Iterator;
import java.util.Set;

public interface FloatSet
extends FloatCollection,
Set<Float> {
    @Override
    public FloatIterator iterator();

    public boolean remove(float var1);

    @Deprecated
    @Override
    public boolean rem(float var1);

    @Deprecated
    @Override
    public boolean add(Float var1);

    @Deprecated
    @Override
    public boolean contains(Object var1);

    @Deprecated
    @Override
    public boolean remove(Object var1);
}

