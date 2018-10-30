/*
 * Decompiled with CFR 0_132.
 */
package gnu.trove;

import gnu.trove.iterator.TCharIterator;
import gnu.trove.procedure.TCharProcedure;
import java.util.Collection;

public interface TCharCollection {
    public static final long serialVersionUID = 1L;

    public char getNoEntryValue();

    public int size();

    public boolean isEmpty();

    public boolean contains(char var1);

    public TCharIterator iterator();

    public char[] toArray();

    public char[] toArray(char[] var1);

    public boolean add(char var1);

    public boolean remove(char var1);

    public boolean containsAll(Collection<?> var1);

    public boolean containsAll(TCharCollection var1);

    public boolean containsAll(char[] var1);

    public boolean addAll(Collection<? extends Character> var1);

    public boolean addAll(TCharCollection var1);

    public boolean addAll(char[] var1);

    public boolean retainAll(Collection<?> var1);

    public boolean retainAll(TCharCollection var1);

    public boolean retainAll(char[] var1);

    public boolean removeAll(Collection<?> var1);

    public boolean removeAll(TCharCollection var1);

    public boolean removeAll(char[] var1);

    public void clear();

    public boolean forEach(TCharProcedure var1);

    public boolean equals(Object var1);

    public int hashCode();
}

