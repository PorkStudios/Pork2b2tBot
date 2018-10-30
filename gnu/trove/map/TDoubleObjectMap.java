/*
 * Decompiled with CFR 0_132.
 */
package gnu.trove.map;

import gnu.trove.function.TObjectFunction;
import gnu.trove.iterator.TDoubleObjectIterator;
import gnu.trove.procedure.TDoubleObjectProcedure;
import gnu.trove.procedure.TDoubleProcedure;
import gnu.trove.procedure.TObjectProcedure;
import gnu.trove.set.TDoubleSet;
import java.util.Collection;
import java.util.Map;

public interface TDoubleObjectMap<V> {
    public double getNoEntryKey();

    public int size();

    public boolean isEmpty();

    public boolean containsKey(double var1);

    public boolean containsValue(Object var1);

    public V get(double var1);

    public V put(double var1, V var3);

    public V putIfAbsent(double var1, V var3);

    public V remove(double var1);

    public void putAll(Map<? extends Double, ? extends V> var1);

    public void putAll(TDoubleObjectMap<? extends V> var1);

    public void clear();

    public TDoubleSet keySet();

    public double[] keys();

    public double[] keys(double[] var1);

    public Collection<V> valueCollection();

    public Object[] values();

    public V[] values(V[] var1);

    public TDoubleObjectIterator<V> iterator();

    public boolean forEachKey(TDoubleProcedure var1);

    public boolean forEachValue(TObjectProcedure<? super V> var1);

    public boolean forEachEntry(TDoubleObjectProcedure<? super V> var1);

    public void transformValues(TObjectFunction<V, V> var1);

    public boolean retainEntries(TDoubleObjectProcedure<? super V> var1);

    public boolean equals(Object var1);

    public int hashCode();
}

