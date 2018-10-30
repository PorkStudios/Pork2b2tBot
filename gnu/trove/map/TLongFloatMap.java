/*
 * Decompiled with CFR 0_132.
 */
package gnu.trove.map;

import gnu.trove.TFloatCollection;
import gnu.trove.function.TFloatFunction;
import gnu.trove.iterator.TLongFloatIterator;
import gnu.trove.procedure.TFloatProcedure;
import gnu.trove.procedure.TLongFloatProcedure;
import gnu.trove.procedure.TLongProcedure;
import gnu.trove.set.TLongSet;
import java.util.Map;

public interface TLongFloatMap {
    public long getNoEntryKey();

    public float getNoEntryValue();

    public float put(long var1, float var3);

    public float putIfAbsent(long var1, float var3);

    public void putAll(Map<? extends Long, ? extends Float> var1);

    public void putAll(TLongFloatMap var1);

    public float get(long var1);

    public void clear();

    public boolean isEmpty();

    public float remove(long var1);

    public int size();

    public TLongSet keySet();

    public long[] keys();

    public long[] keys(long[] var1);

    public TFloatCollection valueCollection();

    public float[] values();

    public float[] values(float[] var1);

    public boolean containsValue(float var1);

    public boolean containsKey(long var1);

    public TLongFloatIterator iterator();

    public boolean forEachKey(TLongProcedure var1);

    public boolean forEachValue(TFloatProcedure var1);

    public boolean forEachEntry(TLongFloatProcedure var1);

    public void transformValues(TFloatFunction var1);

    public boolean retainEntries(TLongFloatProcedure var1);

    public boolean increment(long var1);

    public boolean adjustValue(long var1, float var3);

    public float adjustOrPutValue(long var1, float var3, float var4);
}

