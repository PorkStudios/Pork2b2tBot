/*
 * Decompiled with CFR 0_132.
 */
package gnu.trove.map;

import gnu.trove.TFloatCollection;
import gnu.trove.function.TFloatFunction;
import gnu.trove.iterator.TIntFloatIterator;
import gnu.trove.procedure.TFloatProcedure;
import gnu.trove.procedure.TIntFloatProcedure;
import gnu.trove.procedure.TIntProcedure;
import gnu.trove.set.TIntSet;
import java.util.Map;

public interface TIntFloatMap {
    public int getNoEntryKey();

    public float getNoEntryValue();

    public float put(int var1, float var2);

    public float putIfAbsent(int var1, float var2);

    public void putAll(Map<? extends Integer, ? extends Float> var1);

    public void putAll(TIntFloatMap var1);

    public float get(int var1);

    public void clear();

    public boolean isEmpty();

    public float remove(int var1);

    public int size();

    public TIntSet keySet();

    public int[] keys();

    public int[] keys(int[] var1);

    public TFloatCollection valueCollection();

    public float[] values();

    public float[] values(float[] var1);

    public boolean containsValue(float var1);

    public boolean containsKey(int var1);

    public TIntFloatIterator iterator();

    public boolean forEachKey(TIntProcedure var1);

    public boolean forEachValue(TFloatProcedure var1);

    public boolean forEachEntry(TIntFloatProcedure var1);

    public void transformValues(TFloatFunction var1);

    public boolean retainEntries(TIntFloatProcedure var1);

    public boolean increment(int var1);

    public boolean adjustValue(int var1, float var2);

    public float adjustOrPutValue(int var1, float var2, float var3);
}

