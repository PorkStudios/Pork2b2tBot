/*
 * Decompiled with CFR 0_132.
 */
package gnu.trove.map;

import gnu.trove.TFloatCollection;
import gnu.trove.function.TFloatFunction;
import gnu.trove.iterator.TFloatFloatIterator;
import gnu.trove.procedure.TFloatFloatProcedure;
import gnu.trove.procedure.TFloatProcedure;
import gnu.trove.set.TFloatSet;
import java.util.Map;

public interface TFloatFloatMap {
    public float getNoEntryKey();

    public float getNoEntryValue();

    public float put(float var1, float var2);

    public float putIfAbsent(float var1, float var2);

    public void putAll(Map<? extends Float, ? extends Float> var1);

    public void putAll(TFloatFloatMap var1);

    public float get(float var1);

    public void clear();

    public boolean isEmpty();

    public float remove(float var1);

    public int size();

    public TFloatSet keySet();

    public float[] keys();

    public float[] keys(float[] var1);

    public TFloatCollection valueCollection();

    public float[] values();

    public float[] values(float[] var1);

    public boolean containsValue(float var1);

    public boolean containsKey(float var1);

    public TFloatFloatIterator iterator();

    public boolean forEachKey(TFloatProcedure var1);

    public boolean forEachValue(TFloatProcedure var1);

    public boolean forEachEntry(TFloatFloatProcedure var1);

    public void transformValues(TFloatFunction var1);

    public boolean retainEntries(TFloatFloatProcedure var1);

    public boolean increment(float var1);

    public boolean adjustValue(float var1, float var2);

    public float adjustOrPutValue(float var1, float var2, float var3);
}

