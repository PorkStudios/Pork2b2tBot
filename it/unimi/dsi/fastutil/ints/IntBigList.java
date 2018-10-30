/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.ints;

import it.unimi.dsi.fastutil.BigList;
import it.unimi.dsi.fastutil.BigListIterator;
import it.unimi.dsi.fastutil.ints.IntBigListIterator;
import it.unimi.dsi.fastutil.ints.IntCollection;
import it.unimi.dsi.fastutil.ints.IntIterator;
import java.util.Iterator;

public interface IntBigList
extends BigList<Integer>,
IntCollection,
Comparable<BigList<? extends Integer>> {
    @Override
    public IntBigListIterator iterator();

    public IntBigListIterator listIterator();

    public IntBigListIterator listIterator(long var1);

    public IntBigList subList(long var1, long var3);

    public void getElements(long var1, int[][] var3, long var4, long var6);

    public void removeElements(long var1, long var3);

    public void addElements(long var1, int[][] var3);

    public void addElements(long var1, int[][] var3, long var4, long var6);

    @Override
    public void add(long var1, int var3);

    public boolean addAll(long var1, IntCollection var3);

    public boolean addAll(long var1, IntBigList var3);

    public boolean addAll(IntBigList var1);

    public int getInt(long var1);

    public int removeInt(long var1);

    @Override
    public int set(long var1, int var3);

    public long indexOf(int var1);

    public long lastIndexOf(int var1);

    @Deprecated
    @Override
    public void add(long var1, Integer var3);

    @Deprecated
    @Override
    public Integer get(long var1);

    @Deprecated
    @Override
    public long indexOf(Object var1);

    @Deprecated
    @Override
    public long lastIndexOf(Object var1);

    @Deprecated
    @Override
    public Integer remove(long var1);

    @Deprecated
    @Override
    public Integer set(long var1, Integer var3);
}

