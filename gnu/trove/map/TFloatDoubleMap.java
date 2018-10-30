/*
 * Decompiled with CFR 0_132.
 */
package gnu.trove.map;

import gnu.trove.TDoubleCollection;
import gnu.trove.function.TDoubleFunction;
import gnu.trove.iterator.TFloatDoubleIterator;
import gnu.trove.procedure.TDoubleProcedure;
import gnu.trove.procedure.TFloatDoubleProcedure;
import gnu.trove.procedure.TFloatProcedure;
import gnu.trove.set.TFloatSet;
import java.util.Map;

public interface TFloatDoubleMap {
    public float getNoEntryKey();

    public double getNoEntryValue();

    public double put(float var1, double var2);

    public double putIfAbsent(float var1, double var2);

    public void putAll(Map<? extends Float, ? extends Double> var1);

    public void putAll(TFloatDoubleMap var1);

    public double get(float var1);

    public void clear();

    public boolean isEmpty();

    public double remove(float var1);

    public int size();

    public TFloatSet keySet();

    public float[] keys();

    public float[] keys(float[] var1);

    public TDoubleCollection valueCollection();

    public double[] values();

    public double[] values(double[] var1);

    public boolean containsValue(double var1);

    public boolean containsKey(float var1);

    public TFloatDoubleIterator iterator();

    public boolean forEachKey(TFloatProcedure var1);

    public boolean forEachValue(TDoubleProcedure var1);

    public boolean forEachEntry(TFloatDoubleProcedure var1);

    public void transformValues(TDoubleFunction var1);

    public boolean retainEntries(TFloatDoubleProcedure var1);

    public boolean increment(float var1);

    public boolean adjustValue(float var1, double var2);

    public double adjustOrPutValue(float var1, double var2, double var4);
}

