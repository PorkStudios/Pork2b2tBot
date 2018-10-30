/*
 * Decompiled with CFR 0_132.
 */
package gnu.trove;

import gnu.trove.iterator.TByteIterator;
import gnu.trove.procedure.TByteProcedure;
import java.util.Collection;

public interface TByteCollection {
    public static final long serialVersionUID = 1L;

    public byte getNoEntryValue();

    public int size();

    public boolean isEmpty();

    public boolean contains(byte var1);

    public TByteIterator iterator();

    public byte[] toArray();

    public byte[] toArray(byte[] var1);

    public boolean add(byte var1);

    public boolean remove(byte var1);

    public boolean containsAll(Collection<?> var1);

    public boolean containsAll(TByteCollection var1);

    public boolean containsAll(byte[] var1);

    public boolean addAll(Collection<? extends Byte> var1);

    public boolean addAll(TByteCollection var1);

    public boolean addAll(byte[] var1);

    public boolean retainAll(Collection<?> var1);

    public boolean retainAll(TByteCollection var1);

    public boolean retainAll(byte[] var1);

    public boolean removeAll(Collection<?> var1);

    public boolean removeAll(TByteCollection var1);

    public boolean removeAll(byte[] var1);

    public void clear();

    public boolean forEach(TByteProcedure var1);

    public boolean equals(Object var1);

    public int hashCode();
}

