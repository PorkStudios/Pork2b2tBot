/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.doubles;

import it.unimi.dsi.fastutil.doubles.DoubleCollection;
import it.unimi.dsi.fastutil.doubles.DoubleIterator;
import java.util.Iterator;
import java.util.Set;

public interface DoubleSet
extends DoubleCollection,
Set<Double> {
    @Override
    public DoubleIterator iterator();

    public boolean remove(double var1);

    @Deprecated
    @Override
    public boolean rem(double var1);

    @Deprecated
    @Override
    public boolean add(Double var1);

    @Deprecated
    @Override
    public boolean contains(Object var1);

    @Deprecated
    @Override
    public boolean remove(Object var1);
}

