/*
 * Decompiled with CFR 0_132.
 */
package gnu.trove.map;

import gnu.trove.TFloatCollection;
import gnu.trove.function.TFloatFunction;
import gnu.trove.iterator.TObjectFloatIterator;
import gnu.trove.procedure.TFloatProcedure;
import gnu.trove.procedure.TObjectFloatProcedure;
import gnu.trove.procedure.TObjectProcedure;
import java.util.Map;
import java.util.Set;

public interface TObjectFloatMap<K> {
    public float getNoEntryValue();

    public int size();

    public boolean isEmpty();

    public boolean containsKey(Object var1);

    public boolean containsValue(float var1);

    public float get(Object var1);

    public float put(K var1, float var2);

    public float putIfAbsent(K var1, float var2);

    public float remove(Object var1);

    public void putAll(Map<? extends K, ? extends Float> var1);

    public void putAll(TObjectFloatMap<? extends K> var1);

    public void clear();

    public Set<K> keySet();

    public Object[] keys();

    public K[] keys(K[] var1);

    public TFloatCollection valueCollection();

    public float[] values();

    public float[] values(float[] var1);

    public TObjectFloatIterator<K> iterator();

    public boolean increment(K var1);

    public boolean adjustValue(K var1, float var2);

    public float adjustOrPutValue(K var1, float var2, float var3);

    public boolean forEachKey(TObjectProcedure<? super K> var1);

    public boolean forEachValue(TFloatProcedure var1);

    public boolean forEachEntry(TObjectFloatProcedure<? super K> var1);

    public void transformValues(TFloatFunction var1);

    public boolean retainEntries(TObjectFloatProcedure<? super K> var1);

    public boolean equals(Object var1);

    public int hashCode();
}

