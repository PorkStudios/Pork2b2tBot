/*
 * Decompiled with CFR 0_132.
 */
package gnu.trove.map;

import gnu.trove.TDoubleCollection;
import gnu.trove.function.TDoubleFunction;
import gnu.trove.iterator.TDoubleDoubleIterator;
import gnu.trove.procedure.TDoubleDoubleProcedure;
import gnu.trove.procedure.TDoubleProcedure;
import gnu.trove.set.TDoubleSet;
import java.util.Map;

public interface TDoubleDoubleMap {
    public double getNoEntryKey();

    public double getNoEntryValue();

    public double put(double var1, double var3);

    public double putIfAbsent(double var1, double var3);

    public void putAll(Map<? extends Double, ? extends Double> var1);

    public void putAll(TDoubleDoubleMap var1);

    public double get(double var1);

    public void clear();

    public boolean isEmpty();

    public double remove(double var1);

    public int size();

    public TDoubleSet keySet();

    public double[] keys();

    public double[] keys(double[] var1);

    public TDoubleCollection valueCollection();

    public double[] values();

    public double[] values(double[] var1);

    public boolean containsValue(double var1);

    public boolean containsKey(double var1);

    public TDoubleDoubleIterator iterator();

    public boolean forEachKey(TDoubleProcedure var1);

    public boolean forEachValue(TDoubleProcedure var1);

    public boolean forEachEntry(TDoubleDoubleProcedure var1);

    public void transformValues(TDoubleFunction var1);

    public boolean retainEntries(TDoubleDoubleProcedure var1);

    public boolean increment(double var1);

    public boolean adjustValue(double var1, double var3);

    public double adjustOrPutValue(double var1, double var3, double var5);
}

