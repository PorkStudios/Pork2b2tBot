/*
 * Decompiled with CFR 0_132.
 */
package gnu.trove;

import gnu.trove.iterator.TFloatIterator;
import gnu.trove.procedure.TFloatProcedure;
import java.util.Collection;

public interface TFloatCollection {
    public static final long serialVersionUID = 1L;

    public float getNoEntryValue();

    public int size();

    public boolean isEmpty();

    public boolean contains(float var1);

    public TFloatIterator iterator();

    public float[] toArray();

    public float[] toArray(float[] var1);

    public boolean add(float var1);

    public boolean remove(float var1);

    public boolean containsAll(Collection<?> var1);

    public boolean containsAll(TFloatCollection var1);

    public boolean containsAll(float[] var1);

    public boolean addAll(Collection<? extends Float> var1);

    public boolean addAll(TFloatCollection var1);

    public boolean addAll(float[] var1);

    public boolean retainAll(Collection<?> var1);

    public boolean retainAll(TFloatCollection var1);

    public boolean retainAll(float[] var1);

    public boolean removeAll(Collection<?> var1);

    public boolean removeAll(TFloatCollection var1);

    public boolean removeAll(float[] var1);

    public void clear();

    public boolean forEach(TFloatProcedure var1);

    public boolean equals(Object var1);

    public int hashCode();
}

