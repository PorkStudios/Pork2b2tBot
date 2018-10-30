/*
 * Decompiled with CFR 0_132.
 */
package gnu.trove.set;

import gnu.trove.TCharCollection;
import gnu.trove.iterator.TCharIterator;
import gnu.trove.procedure.TCharProcedure;
import java.util.Collection;

public interface TCharSet
extends TCharCollection {
    @Override
    public char getNoEntryValue();

    @Override
    public int size();

    @Override
    public boolean isEmpty();

    @Override
    public boolean contains(char var1);

    @Override
    public TCharIterator iterator();

    @Override
    public char[] toArray();

    @Override
    public char[] toArray(char[] var1);

    @Override
    public boolean add(char var1);

    @Override
    public boolean remove(char var1);

    @Override
    public boolean containsAll(Collection<?> var1);

    @Override
    public boolean containsAll(TCharCollection var1);

    @Override
    public boolean containsAll(char[] var1);

    @Override
    public boolean addAll(Collection<? extends Character> var1);

    @Override
    public boolean addAll(TCharCollection var1);

    @Override
    public boolean addAll(char[] var1);

    @Override
    public boolean retainAll(Collection<?> var1);

    @Override
    public boolean retainAll(TCharCollection var1);

    @Override
    public boolean retainAll(char[] var1);

    @Override
    public boolean removeAll(Collection<?> var1);

    @Override
    public boolean removeAll(TCharCollection var1);

    @Override
    public boolean removeAll(char[] var1);

    @Override
    public void clear();

    @Override
    public boolean forEach(TCharProcedure var1);

    @Override
    public boolean equals(Object var1);

    @Override
    public int hashCode();
}

