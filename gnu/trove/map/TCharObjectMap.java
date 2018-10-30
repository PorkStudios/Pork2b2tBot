/*
 * Decompiled with CFR 0_132.
 */
package gnu.trove.map;

import gnu.trove.function.TObjectFunction;
import gnu.trove.iterator.TCharObjectIterator;
import gnu.trove.procedure.TCharObjectProcedure;
import gnu.trove.procedure.TCharProcedure;
import gnu.trove.procedure.TObjectProcedure;
import gnu.trove.set.TCharSet;
import java.util.Collection;
import java.util.Map;

public interface TCharObjectMap<V> {
    public char getNoEntryKey();

    public int size();

    public boolean isEmpty();

    public boolean containsKey(char var1);

    public boolean containsValue(Object var1);

    public V get(char var1);

    public V put(char var1, V var2);

    public V putIfAbsent(char var1, V var2);

    public V remove(char var1);

    public void putAll(Map<? extends Character, ? extends V> var1);

    public void putAll(TCharObjectMap<? extends V> var1);

    public void clear();

    public TCharSet keySet();

    public char[] keys();

    public char[] keys(char[] var1);

    public Collection<V> valueCollection();

    public Object[] values();

    public V[] values(V[] var1);

    public TCharObjectIterator<V> iterator();

    public boolean forEachKey(TCharProcedure var1);

    public boolean forEachValue(TObjectProcedure<? super V> var1);

    public boolean forEachEntry(TCharObjectProcedure<? super V> var1);

    public void transformValues(TObjectFunction<V, V> var1);

    public boolean retainEntries(TCharObjectProcedure<? super V> var1);

    public boolean equals(Object var1);

    public int hashCode();
}

