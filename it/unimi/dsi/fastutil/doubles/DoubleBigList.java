/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.doubles;

import it.unimi.dsi.fastutil.BigList;
import it.unimi.dsi.fastutil.BigListIterator;
import it.unimi.dsi.fastutil.doubles.DoubleBigListIterator;
import it.unimi.dsi.fastutil.doubles.DoubleCollection;
import it.unimi.dsi.fastutil.doubles.DoubleIterator;
import java.util.Iterator;

public interface DoubleBigList
extends BigList<Double>,
DoubleCollection,
Comparable<BigList<? extends Double>> {
    @Override
    public DoubleBigListIterator iterator();

    public DoubleBigListIterator listIterator();

    public DoubleBigListIterator listIterator(long var1);

    public DoubleBigList subList(long var1, long var3);

    public void getElements(long var1, double[][] var3, long var4, long var6);

    public void removeElements(long var1, long var3);

    public void addElements(long var1, double[][] var3);

    public void addElements(long var1, double[][] var3, long var4, long var6);

    @Override
    public void add(long var1, double var3);

    public boolean addAll(long var1, DoubleCollection var3);

    public boolean addAll(long var1, DoubleBigList var3);

    public boolean addAll(DoubleBigList var1);

    public double getDouble(long var1);

    public double removeDouble(long var1);

    @Override
    public double set(long var1, double var3);

    public long indexOf(double var1);

    public long lastIndexOf(double var1);

    @Deprecated
    @Override
    public void add(long var1, Double var3);

    @Deprecated
    @Override
    public Double get(long var1);

    @Deprecated
    @Override
    public long indexOf(Object var1);

    @Deprecated
    @Override
    public long lastIndexOf(Object var1);

    @Deprecated
    @Override
    public Double remove(long var1);

    @Deprecated
    @Override
    public Double set(long var1, Double var3);
}

