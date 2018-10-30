/*
 * Decompiled with CFR 0_132.
 */
package gnu.trove.map;

import gnu.trove.function.TObjectFunction;
import gnu.trove.iterator.TLongObjectIterator;
import gnu.trove.procedure.TLongObjectProcedure;
import gnu.trove.procedure.TLongProcedure;
import gnu.trove.procedure.TObjectProcedure;
import gnu.trove.set.TLongSet;
import java.util.Collection;
import java.util.Map;

public interface TLongObjectMap<V> {
    public long getNoEntryKey();

    public int size();

    public boolean isEmpty();

    public boolean containsKey(long var1);

    public boolean containsValue(Object var1);

    public V get(long var1);

    public V put(long var1, V var3);

    public V putIfAbsent(long var1, V var3);

    public V remove(long var1);

    public void putAll(Map<? extends Long, ? extends V> var1);

    public void putAll(TLongObjectMap<? extends V> var1);

    public void clear();

    public TLongSet keySet();

    public long[] keys();

    public long[] keys(long[] var1);

    public Collection<V> valueCollection();

    public Object[] values();

    public V[] values(V[] var1);

    public TLongObjectIterator<V> iterator();

    public boolean forEachKey(TLongProcedure var1);

    public boolean forEachValue(TObjectProcedure<? super V> var1);

    public boolean forEachEntry(TLongObjectProcedure<? super V> var1);

    public void transformValues(TObjectFunction<V, V> var1);

    public boolean retainEntries(TLongObjectProcedure<? super V> var1);

    public boolean equals(Object var1);

    public int hashCode();
}

