/*
 * Decompiled with CFR 0_132.
 */
package gnu.trove.set;

import gnu.trove.TByteCollection;
import gnu.trove.iterator.TByteIterator;
import gnu.trove.procedure.TByteProcedure;
import java.util.Collection;

public interface TByteSet
extends TByteCollection {
    @Override
    public byte getNoEntryValue();

    @Override
    public int size();

    @Override
    public boolean isEmpty();

    @Override
    public boolean contains(byte var1);

    @Override
    public TByteIterator iterator();

    @Override
    public byte[] toArray();

    @Override
    public byte[] toArray(byte[] var1);

    @Override
    public boolean add(byte var1);

    @Override
    public boolean remove(byte var1);

    @Override
    public boolean containsAll(Collection<?> var1);

    @Override
    public boolean containsAll(TByteCollection var1);

    @Override
    public boolean containsAll(byte[] var1);

    @Override
    public boolean addAll(Collection<? extends Byte> var1);

    @Override
    public boolean addAll(TByteCollection var1);

    @Override
    public boolean addAll(byte[] var1);

    @Override
    public boolean retainAll(Collection<?> var1);

    @Override
    public boolean retainAll(TByteCollection var1);

    @Override
    public boolean retainAll(byte[] var1);

    @Override
    public boolean removeAll(Collection<?> var1);

    @Override
    public boolean removeAll(TByteCollection var1);

    @Override
    public boolean removeAll(byte[] var1);

    @Override
    public void clear();

    @Override
    public boolean forEach(TByteProcedure var1);

    @Override
    public boolean equals(Object var1);

    @Override
    public int hashCode();
}

