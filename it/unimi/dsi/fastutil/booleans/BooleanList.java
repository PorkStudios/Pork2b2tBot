/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.booleans;

import it.unimi.dsi.fastutil.booleans.BooleanCollection;
import it.unimi.dsi.fastutil.booleans.BooleanIterator;
import it.unimi.dsi.fastutil.booleans.BooleanListIterator;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public interface BooleanList
extends List<Boolean>,
Comparable<List<? extends Boolean>>,
BooleanCollection {
    @Override
    public BooleanListIterator iterator();

    public BooleanListIterator listIterator();

    public BooleanListIterator listIterator(int var1);

    public BooleanList subList(int var1, int var2);

    public void size(int var1);

    public void getElements(int var1, boolean[] var2, int var3, int var4);

    public void removeElements(int var1, int var2);

    public void addElements(int var1, boolean[] var2);

    public void addElements(int var1, boolean[] var2, int var3, int var4);

    @Override
    public boolean add(boolean var1);

    @Override
    public void add(int var1, boolean var2);

    @Deprecated
    @Override
    public void add(int var1, Boolean var2);

    public boolean addAll(int var1, BooleanCollection var2);

    public boolean addAll(int var1, BooleanList var2);

    public boolean addAll(BooleanList var1);

    @Override
    public boolean set(int var1, boolean var2);

    public boolean getBoolean(int var1);

    public int indexOf(boolean var1);

    public int lastIndexOf(boolean var1);

    @Deprecated
    @Override
    public Boolean get(int var1);

    @Deprecated
    @Override
    public int indexOf(Object var1);

    @Deprecated
    @Override
    public int lastIndexOf(Object var1);

    @Deprecated
    @Override
    public boolean add(Boolean var1);

    public boolean removeBoolean(int var1);

    @Deprecated
    @Override
    public Boolean remove(int var1);

    @Deprecated
    @Override
    public Boolean set(int var1, Boolean var2);
}

