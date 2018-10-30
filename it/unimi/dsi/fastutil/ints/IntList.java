/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.ints;

import it.unimi.dsi.fastutil.ints.IntCollection;
import it.unimi.dsi.fastutil.ints.IntIterator;
import it.unimi.dsi.fastutil.ints.IntListIterator;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public interface IntList
extends List<Integer>,
Comparable<List<? extends Integer>>,
IntCollection {
    @Override
    public IntListIterator iterator();

    public IntListIterator listIterator();

    public IntListIterator listIterator(int var1);

    public IntList subList(int var1, int var2);

    public void size(int var1);

    public void getElements(int var1, int[] var2, int var3, int var4);

    public void removeElements(int var1, int var2);

    public void addElements(int var1, int[] var2);

    public void addElements(int var1, int[] var2, int var3, int var4);

    @Override
    public boolean add(int var1);

    @Override
    public void add(int var1, int var2);

    @Deprecated
    @Override
    public void add(int var1, Integer var2);

    public boolean addAll(int var1, IntCollection var2);

    public boolean addAll(int var1, IntList var2);

    public boolean addAll(IntList var1);

    @Override
    public int set(int var1, int var2);

    public int getInt(int var1);

    public int indexOf(int var1);

    public int lastIndexOf(int var1);

    @Deprecated
    @Override
    public Integer get(int var1);

    @Deprecated
    @Override
    public int indexOf(Object var1);

    @Deprecated
    @Override
    public int lastIndexOf(Object var1);

    @Deprecated
    @Override
    public boolean add(Integer var1);

    public int removeInt(int var1);

    @Deprecated
    @Override
    public Integer remove(int var1);

    @Deprecated
    @Override
    public Integer set(int var1, Integer var2);
}

