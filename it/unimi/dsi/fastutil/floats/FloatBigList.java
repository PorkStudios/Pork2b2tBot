/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.BigList;
import it.unimi.dsi.fastutil.BigListIterator;
import it.unimi.dsi.fastutil.floats.FloatBigListIterator;
import it.unimi.dsi.fastutil.floats.FloatCollection;
import it.unimi.dsi.fastutil.floats.FloatIterator;
import java.util.Iterator;

public interface FloatBigList
extends BigList<Float>,
FloatCollection,
Comparable<BigList<? extends Float>> {
    @Override
    public FloatBigListIterator iterator();

    public FloatBigListIterator listIterator();

    public FloatBigListIterator listIterator(long var1);

    public FloatBigList subList(long var1, long var3);

    public void getElements(long var1, float[][] var3, long var4, long var6);

    public void removeElements(long var1, long var3);

    public void addElements(long var1, float[][] var3);

    public void addElements(long var1, float[][] var3, long var4, long var6);

    @Override
    public void add(long var1, float var3);

    public boolean addAll(long var1, FloatCollection var3);

    public boolean addAll(long var1, FloatBigList var3);

    public boolean addAll(FloatBigList var1);

    public float getFloat(long var1);

    public float removeFloat(long var1);

    @Override
    public float set(long var1, float var3);

    public long indexOf(float var1);

    public long lastIndexOf(float var1);

    @Deprecated
    @Override
    public void add(long var1, Float var3);

    @Deprecated
    @Override
    public Float get(long var1);

    @Deprecated
    @Override
    public long indexOf(Object var1);

    @Deprecated
    @Override
    public long lastIndexOf(Object var1);

    @Deprecated
    @Override
    public Float remove(long var1);

    @Deprecated
    @Override
    public Float set(long var1, Float var3);
}

