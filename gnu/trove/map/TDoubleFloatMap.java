/*
 * Decompiled with CFR 0_132.
 */
package gnu.trove.map;

import gnu.trove.TFloatCollection;
import gnu.trove.function.TFloatFunction;
import gnu.trove.iterator.TDoubleFloatIterator;
import gnu.trove.procedure.TDoubleFloatProcedure;
import gnu.trove.procedure.TDoubleProcedure;
import gnu.trove.procedure.TFloatProcedure;
import gnu.trove.set.TDoubleSet;
import java.util.Map;

public interface TDoubleFloatMap {
    public double getNoEntryKey();

    public float getNoEntryValue();

    public float put(double var1, float var3);

    public float putIfAbsent(double var1, float var3);

    public void putAll(Map<? extends Double, ? extends Float> var1);

    public void putAll(TDoubleFloatMap var1);

    public float get(double var1);

    public void clear();

    public boolean isEmpty();

    public float remove(double var1);

    public int size();

    public TDoubleSet keySet();

    public double[] keys();

    public double[] keys(double[] var1);

    public TFloatCollection valueCollection();

    public float[] values();

    public float[] values(float[] var1);

    public boolean containsValue(float var1);

    public boolean containsKey(double var1);

    public TDoubleFloatIterator iterator();

    public boolean forEachKey(TDoubleProcedure var1);

    public boolean forEachValue(TFloatProcedure var1);

    public boolean forEachEntry(TDoubleFloatProcedure var1);

    public void transformValues(TFloatFunction var1);

    public boolean retainEntries(TDoubleFloatProcedure var1);

    public boolean increment(double var1);

    public boolean adjustValue(double var1, float var3);

    public float adjustOrPutValue(double var1, float var3, float var4);
}

