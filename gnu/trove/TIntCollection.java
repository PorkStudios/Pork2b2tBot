/*
 * Decompiled with CFR 0_132.
 */
package gnu.trove;

import gnu.trove.iterator.TIntIterator;
import gnu.trove.procedure.TIntProcedure;
import java.util.Collection;

public interface TIntCollection {
    public static final long serialVersionUID = 1L;

    public int getNoEntryValue();

    public int size();

    public boolean isEmpty();

    public boolean contains(int var1);

    public TIntIterator iterator();

    public int[] toArray();

    public int[] toArray(int[] var1);

    public boolean add(int var1);

    public boolean remove(int var1);

    public boolean containsAll(Collection<?> var1);

    public boolean containsAll(TIntCollection var1);

    public boolean containsAll(int[] var1);

    public boolean addAll(Collection<? extends Integer> var1);

    public boolean addAll(TIntCollection var1);

    public boolean addAll(int[] var1);

    public boolean retainAll(Collection<?> var1);

    public boolean retainAll(TIntCollection var1);

    public boolean retainAll(int[] var1);

    public boolean removeAll(Collection<?> var1);

    public boolean removeAll(TIntCollection var1);

    public boolean removeAll(int[] var1);

    public void clear();

    public boolean forEach(TIntProcedure var1);

    public boolean equals(Object var1);

    public int hashCode();
}

