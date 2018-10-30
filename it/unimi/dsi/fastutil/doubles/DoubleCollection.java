/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.doubles;

import it.unimi.dsi.fastutil.doubles.DoubleIterable;
import it.unimi.dsi.fastutil.doubles.DoubleIterator;
import java.util.Collection;
import java.util.Iterator;

public interface DoubleCollection
extends Collection<Double>,
DoubleIterable {
    @Override
    public DoubleIterator iterator();

    @Override
    public boolean add(double var1);

    public boolean contains(double var1);

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

    public double[] toDoubleArray();

    @Deprecated
    public double[] toDoubleArray(double[] var1);

    public double[] toArray(double[] var1);

    public boolean addAll(DoubleCollection var1);

    public boolean containsAll(DoubleCollection var1);

    public boolean removeAll(DoubleCollection var1);

    public boolean retainAll(DoubleCollection var1);
}

