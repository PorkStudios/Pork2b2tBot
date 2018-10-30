/*
 * Decompiled with CFR 0_132.
 */
package gnu.trove.map;

import gnu.trove.TDoubleCollection;
import gnu.trove.function.TDoubleFunction;
import gnu.trove.iterator.TObjectDoubleIterator;
import gnu.trove.procedure.TDoubleProcedure;
import gnu.trove.procedure.TObjectDoubleProcedure;
import gnu.trove.procedure.TObjectProcedure;
import java.util.Map;
import java.util.Set;

public interface TObjectDoubleMap<K> {
    public double getNoEntryValue();

    public int size();

    public boolean isEmpty();

    public boolean containsKey(Object var1);

    public boolean containsValue(double var1);

    public double get(Object var1);

    public double put(K var1, double var2);

    public double putIfAbsent(K var1, double var2);

    public double remove(Object var1);

    public void putAll(Map<? extends K, ? extends Double> var1);

    public void putAll(TObjectDoubleMap<? extends K> var1);

    public void clear();

    public Set<K> keySet();

    public Object[] keys();

    public K[] keys(K[] var1);

    public TDoubleCollection valueCollection();

    public double[] values();

    public double[] values(double[] var1);

    public TObjectDoubleIterator<K> iterator();

    public boolean increment(K var1);

    public boolean adjustValue(K var1, double var2);

    public double adjustOrPutValue(K var1, double var2, double var4);

    public boolean forEachKey(TObjectProcedure<? super K> var1);

    public boolean forEachValue(TDoubleProcedure var1);

    public boolean forEachEntry(TObjectDoubleProcedure<? super K> var1);

    public void transformValues(TDoubleFunction var1);

    public boolean retainEntries(TObjectDoubleProcedure<? super K> var1);

    public boolean equals(Object var1);

    public int hashCode();
}

