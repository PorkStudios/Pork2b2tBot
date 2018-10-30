/*
 * Decompiled with CFR 0_132.
 */
package gnu.trove.set;

import gnu.trove.TShortCollection;
import gnu.trove.iterator.TShortIterator;
import gnu.trove.procedure.TShortProcedure;
import java.util.Collection;

public interface TShortSet
extends TShortCollection {
    @Override
    public short getNoEntryValue();

    @Override
    public int size();

    @Override
    public boolean isEmpty();

    @Override
    public boolean contains(short var1);

    @Override
    public TShortIterator iterator();

    @Override
    public short[] toArray();

    @Override
    public short[] toArray(short[] var1);

    @Override
    public boolean add(short var1);

    @Override
    public boolean remove(short var1);

    @Override
    public boolean containsAll(Collection<?> var1);

    @Override
    public boolean containsAll(TShortCollection var1);

    @Override
    public boolean containsAll(short[] var1);

    @Override
    public boolean addAll(Collection<? extends Short> var1);

    @Override
    public boolean addAll(TShortCollection var1);

    @Override
    public boolean addAll(short[] var1);

    @Override
    public boolean retainAll(Collection<?> var1);

    @Override
    public boolean retainAll(TShortCollection var1);

    @Override
    public boolean retainAll(short[] var1);

    @Override
    public boolean removeAll(Collection<?> var1);

    @Override
    public boolean removeAll(TShortCollection var1);

    @Override
    public boolean removeAll(short[] var1);

    @Override
    public void clear();

    @Override
    public boolean forEach(TShortProcedure var1);

    @Override
    public boolean equals(Object var1);

    @Override
    public int hashCode();
}

