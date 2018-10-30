/*
 * Decompiled with CFR 0_132.
 */
package gnu.trove;

import gnu.trove.iterator.TLongIterator;
import gnu.trove.procedure.TLongProcedure;
import java.util.Collection;

public interface TLongCollection {
    public static final long serialVersionUID = 1L;

    public long getNoEntryValue();

    public int size();

    public boolean isEmpty();

    public boolean contains(long var1);

    public TLongIterator iterator();

    public long[] toArray();

    public long[] toArray(long[] var1);

    public boolean add(long var1);

    public boolean remove(long var1);

    public boolean containsAll(Collection<?> var1);

    public boolean containsAll(TLongCollection var1);

    public boolean containsAll(long[] var1);

    public boolean addAll(Collection<? extends Long> var1);

    public boolean addAll(TLongCollection var1);

    public boolean addAll(long[] var1);

    public boolean retainAll(Collection<?> var1);

    public boolean retainAll(TLongCollection var1);

    public boolean retainAll(long[] var1);

    public boolean removeAll(Collection<?> var1);

    public boolean removeAll(TLongCollection var1);

    public boolean removeAll(long[] var1);

    public void clear();

    public boolean forEach(TLongProcedure var1);

    public boolean equals(Object var1);

    public int hashCode();
}

