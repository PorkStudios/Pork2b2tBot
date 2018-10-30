/*
 * Decompiled with CFR 0_132.
 */
package gnu.trove;

import gnu.trove.iterator.TDoubleIterator;
import gnu.trove.procedure.TDoubleProcedure;
import java.util.Collection;

public interface TDoubleCollection {
    public static final long serialVersionUID = 1L;

    public double getNoEntryValue();

    public int size();

    public boolean isEmpty();

    public boolean contains(double var1);

    public TDoubleIterator iterator();

    public double[] toArray();

    public double[] toArray(double[] var1);

    public boolean add(double var1);

    public boolean remove(double var1);

    public boolean containsAll(Collection<?> var1);

    public boolean containsAll(TDoubleCollection var1);

    public boolean containsAll(double[] var1);

    public boolean addAll(Collection<? extends Double> var1);

    public boolean addAll(TDoubleCollection var1);

    public boolean addAll(double[] var1);

    public boolean retainAll(Collection<?> var1);

    public boolean retainAll(TDoubleCollection var1);

    public boolean retainAll(double[] var1);

    public boolean removeAll(Collection<?> var1);

    public boolean removeAll(TDoubleCollection var1);

    public boolean removeAll(double[] var1);

    public void clear();

    public boolean forEach(TDoubleProcedure var1);

    public boolean equals(Object var1);

    public int hashCode();
}

