/*
 * Decompiled with CFR 0_132.
 */
package gnu.trove.map;

import gnu.trove.TIntCollection;
import gnu.trove.function.TIntFunction;
import gnu.trove.iterator.TDoubleIntIterator;
import gnu.trove.procedure.TDoubleIntProcedure;
import gnu.trove.procedure.TDoubleProcedure;
import gnu.trove.procedure.TIntProcedure;
import gnu.trove.set.TDoubleSet;
import java.util.Map;

public interface TDoubleIntMap {
    public double getNoEntryKey();

    public int getNoEntryValue();

    public int put(double var1, int var3);

    public int putIfAbsent(double var1, int var3);

    public void putAll(Map<? extends Double, ? extends Integer> var1);

    public void putAll(TDoubleIntMap var1);

    public int get(double var1);

    public void clear();

    public boolean isEmpty();

    public int remove(double var1);

    public int size();

    public TDoubleSet keySet();

    public double[] keys();

    public double[] keys(double[] var1);

    public TIntCollection valueCollection();

    public int[] values();

    public int[] values(int[] var1);

    public boolean containsValue(int var1);

    public boolean containsKey(double var1);

    public TDoubleIntIterator iterator();

    public boolean forEachKey(TDoubleProcedure var1);

    public boolean forEachValue(TIntProcedure var1);

    public boolean forEachEntry(TDoubleIntProcedure var1);

    public void transformValues(TIntFunction var1);

    public boolean retainEntries(TDoubleIntProcedure var1);

    public boolean increment(double var1);

    public boolean adjustValue(double var1, int var3);

    public int adjustOrPutValue(double var1, int var3, int var4);
}

