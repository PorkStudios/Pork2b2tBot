/*
 * Decompiled with CFR 0_132.
 */
package gnu.trove.map;

import gnu.trove.function.TObjectFunction;
import gnu.trove.iterator.TShortObjectIterator;
import gnu.trove.procedure.TObjectProcedure;
import gnu.trove.procedure.TShortObjectProcedure;
import gnu.trove.procedure.TShortProcedure;
import gnu.trove.set.TShortSet;
import java.util.Collection;
import java.util.Map;

public interface TShortObjectMap<V> {
    public short getNoEntryKey();

    public int size();

    public boolean isEmpty();

    public boolean containsKey(short var1);

    public boolean containsValue(Object var1);

    public V get(short var1);

    public V put(short var1, V var2);

    public V putIfAbsent(short var1, V var2);

    public V remove(short var1);

    public void putAll(Map<? extends Short, ? extends V> var1);

    public void putAll(TShortObjectMap<? extends V> var1);

    public void clear();

    public TShortSet keySet();

    public short[] keys();

    public short[] keys(short[] var1);

    public Collection<V> valueCollection();

    public Object[] values();

    public V[] values(V[] var1);

    public TShortObjectIterator<V> iterator();

    public boolean forEachKey(TShortProcedure var1);

    public boolean forEachValue(TObjectProcedure<? super V> var1);

    public boolean forEachEntry(TShortObjectProcedure<? super V> var1);

    public void transformValues(TObjectFunction<V, V> var1);

    public boolean retainEntries(TShortObjectProcedure<? super V> var1);

    public boolean equals(Object var1);

    public int hashCode();
}

