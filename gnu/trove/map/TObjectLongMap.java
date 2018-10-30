/*
 * Decompiled with CFR 0_132.
 */
package gnu.trove.map;

import gnu.trove.TLongCollection;
import gnu.trove.function.TLongFunction;
import gnu.trove.iterator.TObjectLongIterator;
import gnu.trove.procedure.TLongProcedure;
import gnu.trove.procedure.TObjectLongProcedure;
import gnu.trove.procedure.TObjectProcedure;
import java.util.Map;
import java.util.Set;

public interface TObjectLongMap<K> {
    public long getNoEntryValue();

    public int size();

    public boolean isEmpty();

    public boolean containsKey(Object var1);

    public boolean containsValue(long var1);

    public long get(Object var1);

    public long put(K var1, long var2);

    public long putIfAbsent(K var1, long var2);

    public long remove(Object var1);

    public void putAll(Map<? extends K, ? extends Long> var1);

    public void putAll(TObjectLongMap<? extends K> var1);

    public void clear();

    public Set<K> keySet();

    public Object[] keys();

    public K[] keys(K[] var1);

    public TLongCollection valueCollection();

    public long[] values();

    public long[] values(long[] var1);

    public TObjectLongIterator<K> iterator();

    public boolean increment(K var1);

    public boolean adjustValue(K var1, long var2);

    public long adjustOrPutValue(K var1, long var2, long var4);

    public boolean forEachKey(TObjectProcedure<? super K> var1);

    public boolean forEachValue(TLongProcedure var1);

    public boolean forEachEntry(TObjectLongProcedure<? super K> var1);

    public void transformValues(TLongFunction var1);

    public boolean retainEntries(TObjectLongProcedure<? super K> var1);

    public boolean equals(Object var1);

    public int hashCode();
}

