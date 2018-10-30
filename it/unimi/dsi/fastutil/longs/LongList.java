/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.longs.LongCollection;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongListIterator;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public interface LongList
extends List<Long>,
Comparable<List<? extends Long>>,
LongCollection {
    @Override
    public LongListIterator iterator();

    public LongListIterator listIterator();

    public LongListIterator listIterator(int var1);

    public LongList subList(int var1, int var2);

    public void size(int var1);

    public void getElements(int var1, long[] var2, int var3, int var4);

    public void removeElements(int var1, int var2);

    public void addElements(int var1, long[] var2);

    public void addElements(int var1, long[] var2, int var3, int var4);

    @Override
    public boolean add(long var1);

    @Override
    public void add(int var1, long var2);

    @Deprecated
    @Override
    public void add(int var1, Long var2);

    public boolean addAll(int var1, LongCollection var2);

    public boolean addAll(int var1, LongList var2);

    public boolean addAll(LongList var1);

    @Override
    public long set(int var1, long var2);

    public long getLong(int var1);

    public int indexOf(long var1);

    public int lastIndexOf(long var1);

    @Deprecated
    @Override
    public Long get(int var1);

    @Deprecated
    @Override
    public int indexOf(Object var1);

    @Deprecated
    @Override
    public int lastIndexOf(Object var1);

    @Deprecated
    @Override
    public boolean add(Long var1);

    public long removeLong(int var1);

    @Deprecated
    @Override
    public Long remove(int var1);

    @Deprecated
    @Override
    public Long set(int var1, Long var2);
}

