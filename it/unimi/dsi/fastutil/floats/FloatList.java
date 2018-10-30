/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.floats.FloatCollection;
import it.unimi.dsi.fastutil.floats.FloatIterator;
import it.unimi.dsi.fastutil.floats.FloatListIterator;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public interface FloatList
extends List<Float>,
Comparable<List<? extends Float>>,
FloatCollection {
    @Override
    public FloatListIterator iterator();

    public FloatListIterator listIterator();

    public FloatListIterator listIterator(int var1);

    public FloatList subList(int var1, int var2);

    public void size(int var1);

    public void getElements(int var1, float[] var2, int var3, int var4);

    public void removeElements(int var1, int var2);

    public void addElements(int var1, float[] var2);

    public void addElements(int var1, float[] var2, int var3, int var4);

    @Override
    public boolean add(float var1);

    @Override
    public void add(int var1, float var2);

    @Deprecated
    @Override
    public void add(int var1, Float var2);

    public boolean addAll(int var1, FloatCollection var2);

    public boolean addAll(int var1, FloatList var2);

    public boolean addAll(FloatList var1);

    @Override
    public float set(int var1, float var2);

    public float getFloat(int var1);

    public int indexOf(float var1);

    public int lastIndexOf(float var1);

    @Deprecated
    @Override
    public Float get(int var1);

    @Deprecated
    @Override
    public int indexOf(Object var1);

    @Deprecated
    @Override
    public int lastIndexOf(Object var1);

    @Deprecated
    @Override
    public boolean add(Float var1);

    public float removeFloat(int var1);

    @Deprecated
    @Override
    public Float remove(int var1);

    @Deprecated
    @Override
    public Float set(int var1, Float var2);
}

