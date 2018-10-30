/*
 * Decompiled with CFR 0_132.
 */
package gnu.trove.map;

import gnu.trove.function.TObjectFunction;
import gnu.trove.iterator.TFloatObjectIterator;
import gnu.trove.procedure.TFloatObjectProcedure;
import gnu.trove.procedure.TFloatProcedure;
import gnu.trove.procedure.TObjectProcedure;
import gnu.trove.set.TFloatSet;
import java.util.Collection;
import java.util.Map;

public interface TFloatObjectMap<V> {
    public float getNoEntryKey();

    public int size();

    public boolean isEmpty();

    public boolean containsKey(float var1);

    public boolean containsValue(Object var1);

    public V get(float var1);

    public V put(float var1, V var2);

    public V putIfAbsent(float var1, V var2);

    public V remove(float var1);

    public void putAll(Map<? extends Float, ? extends V> var1);

    public void putAll(TFloatObjectMap<? extends V> var1);

    public void clear();

    public TFloatSet keySet();

    public float[] keys();

    public float[] keys(float[] var1);

    public Collection<V> valueCollection();

    public Object[] values();

    public V[] values(V[] var1);

    public TFloatObjectIterator<V> iterator();

    public boolean forEachKey(TFloatProcedure var1);

    public boolean forEachValue(TObjectProcedure<? super V> var1);

    public boolean forEachEntry(TFloatObjectProcedure<? super V> var1);

    public void transformValues(TObjectFunction<V, V> var1);

    public boolean retainEntries(TFloatObjectProcedure<? super V> var1);

    public boolean equals(Object var1);

    public int hashCode();
}

