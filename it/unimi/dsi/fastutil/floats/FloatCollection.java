/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.floats.FloatIterable;
import it.unimi.dsi.fastutil.floats.FloatIterator;
import java.util.Collection;
import java.util.Iterator;

public interface FloatCollection
extends Collection<Float>,
FloatIterable {
    @Override
    public FloatIterator iterator();

    @Override
    public boolean add(float var1);

    public boolean contains(float var1);

    public boolean rem(float var1);

    @Deprecated
    @Override
    public boolean add(Float var1);

    @Deprecated
    @Override
    public boolean contains(Object var1);

    @Deprecated
    @Override
    public boolean remove(Object var1);

    public float[] toFloatArray();

    @Deprecated
    public float[] toFloatArray(float[] var1);

    public float[] toArray(float[] var1);

    public boolean addAll(FloatCollection var1);

    public boolean containsAll(FloatCollection var1);

    public boolean removeAll(FloatCollection var1);

    public boolean retainAll(FloatCollection var1);
}

