/*
 * Decompiled with CFR 0_132.
 */
package gnu.trove.map;

import gnu.trove.function.TObjectFunction;
import gnu.trove.iterator.TByteObjectIterator;
import gnu.trove.procedure.TByteObjectProcedure;
import gnu.trove.procedure.TByteProcedure;
import gnu.trove.procedure.TObjectProcedure;
import gnu.trove.set.TByteSet;
import java.util.Collection;
import java.util.Map;

public interface TByteObjectMap<V> {
    public byte getNoEntryKey();

    public int size();

    public boolean isEmpty();

    public boolean containsKey(byte var1);

    public boolean containsValue(Object var1);

    public V get(byte var1);

    public V put(byte var1, V var2);

    public V putIfAbsent(byte var1, V var2);

    public V remove(byte var1);

    public void putAll(Map<? extends Byte, ? extends V> var1);

    public void putAll(TByteObjectMap<? extends V> var1);

    public void clear();

    public TByteSet keySet();

    public byte[] keys();

    public byte[] keys(byte[] var1);

    public Collection<V> valueCollection();

    public Object[] values();

    public V[] values(V[] var1);

    public TByteObjectIterator<V> iterator();

    public boolean forEachKey(TByteProcedure var1);

    public boolean forEachValue(TObjectProcedure<? super V> var1);

    public boolean forEachEntry(TByteObjectProcedure<? super V> var1);

    public void transformValues(TObjectFunction<V, V> var1);

    public boolean retainEntries(TByteObjectProcedure<? super V> var1);

    public boolean equals(Object var1);

    public int hashCode();
}

