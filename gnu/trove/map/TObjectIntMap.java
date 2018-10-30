/*
 * Decompiled with CFR 0_132.
 */
package gnu.trove.map;

import gnu.trove.TIntCollection;
import gnu.trove.function.TIntFunction;
import gnu.trove.iterator.TObjectIntIterator;
import gnu.trove.procedure.TIntProcedure;
import gnu.trove.procedure.TObjectIntProcedure;
import gnu.trove.procedure.TObjectProcedure;
import java.util.Map;
import java.util.Set;

public interface TObjectIntMap<K> {
    public int getNoEntryValue();

    public int size();

    public boolean isEmpty();

    public boolean containsKey(Object var1);

    public boolean containsValue(int var1);

    public int get(Object var1);

    public int put(K var1, int var2);

    public int putIfAbsent(K var1, int var2);

    public int remove(Object var1);

    public void putAll(Map<? extends K, ? extends Integer> var1);

    public void putAll(TObjectIntMap<? extends K> var1);

    public void clear();

    public Set<K> keySet();

    public Object[] keys();

    public K[] keys(K[] var1);

    public TIntCollection valueCollection();

    public int[] values();

    public int[] values(int[] var1);

    public TObjectIntIterator<K> iterator();

    public boolean increment(K var1);

    public boolean adjustValue(K var1, int var2);

    public int adjustOrPutValue(K var1, int var2, int var3);

    public boolean forEachKey(TObjectProcedure<? super K> var1);

    public boolean forEachValue(TIntProcedure var1);

    public boolean forEachEntry(TObjectIntProcedure<? super K> var1);

    public void transformValues(TIntFunction var1);

    public boolean retainEntries(TObjectIntProcedure<? super K> var1);

    public boolean equals(Object var1);

    public int hashCode();
}

