/*
 * Decompiled with CFR 0_132.
 */
package gnu.trove.set;

import gnu.trove.TIntCollection;
import gnu.trove.iterator.TIntIterator;
import gnu.trove.procedure.TIntProcedure;
import java.util.Collection;

public interface TIntSet
extends TIntCollection {
    @Override
    public int getNoEntryValue();

    @Override
    public int size();

    @Override
    public boolean isEmpty();

    @Override
    public boolean contains(int var1);

    @Override
    public TIntIterator iterator();

    @Override
    public int[] toArray();

    @Override
    public int[] toArray(int[] var1);

    @Override
    public boolean add(int var1);

    @Override
    public boolean remove(int var1);

    @Override
    public boolean containsAll(Collection<?> var1);

    @Override
    public boolean containsAll(TIntCollection var1);

    @Override
    public boolean containsAll(int[] var1);

    @Override
    public boolean addAll(Collection<? extends Integer> var1);

    @Override
    public boolean addAll(TIntCollection var1);

    @Override
    public boolean addAll(int[] var1);

    @Override
    public boolean retainAll(Collection<?> var1);

    @Override
    public boolean retainAll(TIntCollection var1);

    @Override
    public boolean retainAll(int[] var1);

    @Override
    public boolean removeAll(Collection<?> var1);

    @Override
    public boolean removeAll(TIntCollection var1);

    @Override
    public boolean removeAll(int[] var1);

    @Override
    public void clear();

    @Override
    public boolean forEach(TIntProcedure var1);

    @Override
    public boolean equals(Object var1);

    @Override
    public int hashCode();
}

