/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.doubles;

import it.unimi.dsi.fastutil.doubles.DoubleCollection;
import it.unimi.dsi.fastutil.doubles.DoubleIterator;
import it.unimi.dsi.fastutil.doubles.DoubleListIterator;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public interface DoubleList
extends List<Double>,
Comparable<List<? extends Double>>,
DoubleCollection {
    @Override
    public DoubleListIterator iterator();

    public DoubleListIterator listIterator();

    public DoubleListIterator listIterator(int var1);

    public DoubleList subList(int var1, int var2);

    public void size(int var1);

    public void getElements(int var1, double[] var2, int var3, int var4);

    public void removeElements(int var1, int var2);

    public void addElements(int var1, double[] var2);

    public void addElements(int var1, double[] var2, int var3, int var4);

    @Override
    public boolean add(double var1);

    @Override
    public void add(int var1, double var2);

    @Deprecated
    @Override
    public void add(int var1, Double var2);

    public boolean addAll(int var1, DoubleCollection var2);

    public boolean addAll(int var1, DoubleList var2);

    public boolean addAll(DoubleList var1);

    @Override
    public double set(int var1, double var2);

    public double getDouble(int var1);

    public int indexOf(double var1);

    public int lastIndexOf(double var1);

    @Deprecated
    @Override
    public Double get(int var1);

    @Deprecated
    @Override
    public int indexOf(Object var1);

    @Deprecated
    @Override
    public int lastIndexOf(Object var1);

    @Deprecated
    @Override
    public boolean add(Double var1);

    public double removeDouble(int var1);

    @Deprecated
    @Override
    public Double remove(int var1);

    @Deprecated
    @Override
    public Double set(int var1, Double var2);
}

