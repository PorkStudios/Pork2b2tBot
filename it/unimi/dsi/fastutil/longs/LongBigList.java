/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.BigList;
import it.unimi.dsi.fastutil.BigListIterator;
import it.unimi.dsi.fastutil.longs.LongBigListIterator;
import it.unimi.dsi.fastutil.longs.LongCollection;
import it.unimi.dsi.fastutil.longs.LongIterator;
import java.util.Iterator;

public interface LongBigList
extends BigList<Long>,
LongCollection,
Comparable<BigList<? extends Long>> {
    @Override
    public LongBigListIterator iterator();

    public LongBigListIterator listIterator();

    public LongBigListIterator listIterator(long var1);

    public LongBigList subList(long var1, long var3);

    public void getElements(long var1, long[][] var3, long var4, long var6);

    public void removeElements(long var1, long var3);

    public void addElements(long var1, long[][] var3);

    public void addElements(long var1, long[][] var3, long var4, long var6);

    @Override
    public void add(long var1, long var3);

    public boolean addAll(long var1, LongCollection var3);

    public boolean addAll(long var1, LongBigList var3);

    public boolean addAll(LongBigList var1);

    public long getLong(long var1);

    public long removeLong(long var1);

    @Override
    public long set(long var1, long var3);

    public long indexOf(long var1);

    public long lastIndexOf(long var1);

    @Deprecated
    @Override
    public void add(long var1, Long var3);

    @Deprecated
    @Override
    public Long get(long var1);

    @Deprecated
    @Override
    public long indexOf(Object var1);

    @Deprecated
    @Override
    public long lastIndexOf(Object var1);

    @Deprecated
    @Override
    public Long remove(long var1);

    @Deprecated
    @Override
    public Long set(long var1, Long var3);
}

