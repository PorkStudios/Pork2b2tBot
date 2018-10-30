/*
 * Decompiled with CFR 0_132.
 */
package gnu.trove.map;

import gnu.trove.TDoubleCollection;
import gnu.trove.function.TDoubleFunction;
import gnu.trove.iterator.TIntDoubleIterator;
import gnu.trove.procedure.TDoubleProcedure;
import gnu.trove.procedure.TIntDoubleProcedure;
import gnu.trove.procedure.TIntProcedure;
import gnu.trove.set.TIntSet;
import java.util.Map;

public interface TIntDoubleMap {
    public int getNoEntryKey();

    public double getNoEntryValue();

    public double put(int var1, double var2);

    public double putIfAbsent(int var1, double var2);

    public void putAll(Map<? extends Integer, ? extends Double> var1);

    public void putAll(TIntDoubleMap var1);

    public double get(int var1);

    public void clear();

    public boolean isEmpty();

    public double remove(int var1);

    public int size();

    public TIntSet keySet();

    public int[] keys();

    public int[] keys(int[] var1);

    public TDoubleCollection valueCollection();

    public double[] values();

    public double[] values(double[] var1);

    public boolean containsValue(double var1);

    public boolean containsKey(int var1);

    public TIntDoubleIterator iterator();

    public boolean forEachKey(TIntProcedure var1);

    public boolean forEachValue(TDoubleProcedure var1);

    public boolean forEachEntry(TIntDoubleProcedure var1);

    public void transformValues(TDoubleFunction var1);

    public boolean retainEntries(TIntDoubleProcedure var1);

    public boolean increment(int var1);

    public boolean adjustValue(int var1, double var2);

    public double adjustOrPutValue(int var1, double var2, double var4);
}

