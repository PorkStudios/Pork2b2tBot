/*
 * Decompiled with CFR 0_132.
 */
package gnu.trove.map;

import gnu.trove.function.TObjectFunction;
import gnu.trove.iterator.TIntObjectIterator;
import gnu.trove.procedure.TIntObjectProcedure;
import gnu.trove.procedure.TIntProcedure;
import gnu.trove.procedure.TObjectProcedure;
import gnu.trove.set.TIntSet;
import java.util.Collection;
import java.util.Map;

public interface TIntObjectMap<V> {
    public int getNoEntryKey();

    public int size();

    public boolean isEmpty();

    public boolean containsKey(int var1);

    public boolean containsValue(Object var1);

    public V get(int var1);

    public V put(int var1, V var2);

    public V putIfAbsent(int var1, V var2);

    public V remove(int var1);

    public void putAll(Map<? extends Integer, ? extends V> var1);

    public void putAll(TIntObjectMap<? extends V> var1);

    public void clear();

    public TIntSet keySet();

    public int[] keys();

    public int[] keys(int[] var1);

    public Collection<V> valueCollection();

    public Object[] values();

    public V[] values(V[] var1);

    public TIntObjectIterator<V> iterator();

    public boolean forEachKey(TIntProcedure var1);

    public boolean forEachValue(TObjectProcedure<? super V> var1);

    public boolean forEachEntry(TIntObjectProcedure<? super V> var1);

    public void transformValues(TObjectFunction<V, V> var1);

    public boolean retainEntries(TIntObjectProcedure<? super V> var1);

    public boolean equals(Object var1);

    public int hashCode();
}

