/*
 * Decompiled with CFR 0_132.
 */
package gnu.trove.set;

import gnu.trove.TDoubleCollection;
import gnu.trove.iterator.TDoubleIterator;
import gnu.trove.procedure.TDoubleProcedure;
import java.util.Collection;

public interface TDoubleSet
extends TDoubleCollection {
    @Override
    public double getNoEntryValue();

    @Override
    public int size();

    @Override
    public boolean isEmpty();

    @Override
    public boolean contains(double var1);

    @Override
    public TDoubleIterator iterator();

    @Override
    public double[] toArray();

    @Override
    public double[] toArray(double[] var1);

    @Override
    public boolean add(double var1);

    @Override
    public boolean remove(double var1);

    @Override
    public boolean containsAll(Collection<?> var1);

    @Override
    public boolean containsAll(TDoubleCollection var1);

    @Override
    public boolean containsAll(double[] var1);

    @Override
    public boolean addAll(Collection<? extends Double> var1);

    @Override
    public boolean addAll(TDoubleCollection var1);

    @Override
    public boolean addAll(double[] var1);

    @Override
    public boolean retainAll(Collection<?> var1);

    @Override
    public boolean retainAll(TDoubleCollection var1);

    @Override
    public boolean retainAll(double[] var1);

    @Override
    public boolean removeAll(Collection<?> var1);

    @Override
    public boolean removeAll(TDoubleCollection var1);

    @Override
    public boolean removeAll(double[] var1);

    @Override
    public void clear();

    @Override
    public boolean forEach(TDoubleProcedure var1);

    @Override
    public boolean equals(Object var1);

    @Override
    public int hashCode();
}

