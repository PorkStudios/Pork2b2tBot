/*
 * Decompiled with CFR 0_132.
 */
package gnu.trove.set;

import gnu.trove.TFloatCollection;
import gnu.trove.iterator.TFloatIterator;
import gnu.trove.procedure.TFloatProcedure;
import java.util.Collection;

public interface TFloatSet
extends TFloatCollection {
    @Override
    public float getNoEntryValue();

    @Override
    public int size();

    @Override
    public boolean isEmpty();

    @Override
    public boolean contains(float var1);

    @Override
    public TFloatIterator iterator();

    @Override
    public float[] toArray();

    @Override
    public float[] toArray(float[] var1);

    @Override
    public boolean add(float var1);

    @Override
    public boolean remove(float var1);

    @Override
    public boolean containsAll(Collection<?> var1);

    @Override
    public boolean containsAll(TFloatCollection var1);

    @Override
    public boolean containsAll(float[] var1);

    @Override
    public boolean addAll(Collection<? extends Float> var1);

    @Override
    public boolean addAll(TFloatCollection var1);

    @Override
    public boolean addAll(float[] var1);

    @Override
    public boolean retainAll(Collection<?> var1);

    @Override
    public boolean retainAll(TFloatCollection var1);

    @Override
    public boolean retainAll(float[] var1);

    @Override
    public boolean removeAll(Collection<?> var1);

    @Override
    public boolean removeAll(TFloatCollection var1);

    @Override
    public boolean removeAll(float[] var1);

    @Override
    public void clear();

    @Override
    public boolean forEach(TFloatProcedure var1);

    @Override
    public boolean equals(Object var1);

    @Override
    public int hashCode();
}

