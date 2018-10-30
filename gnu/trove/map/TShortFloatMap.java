/*
 * Decompiled with CFR 0_132.
 */
package gnu.trove.map;

import gnu.trove.TFloatCollection;
import gnu.trove.function.TFloatFunction;
import gnu.trove.iterator.TShortFloatIterator;
import gnu.trove.procedure.TFloatProcedure;
import gnu.trove.procedure.TShortFloatProcedure;
import gnu.trove.procedure.TShortProcedure;
import gnu.trove.set.TShortSet;
import java.util.Map;

public interface TShortFloatMap {
    public short getNoEntryKey();

    public float getNoEntryValue();

    public float put(short var1, float var2);

    public float putIfAbsent(short var1, float var2);

    public void putAll(Map<? extends Short, ? extends Float> var1);

    public void putAll(TShortFloatMap var1);

    public float get(short var1);

    public void clear();

    public boolean isEmpty();

    public float remove(short var1);

    public int size();

    public TShortSet keySet();

    public short[] keys();

    public short[] keys(short[] var1);

    public TFloatCollection valueCollection();

    public float[] values();

    public float[] values(float[] var1);

    public boolean containsValue(float var1);

    public boolean containsKey(short var1);

    public TShortFloatIterator iterator();

    public boolean forEachKey(TShortProcedure var1);

    public boolean forEachValue(TFloatProcedure var1);

    public boolean forEachEntry(TShortFloatProcedure var1);

    public void transformValues(TFloatFunction var1);

    public boolean retainEntries(TShortFloatProcedure var1);

    public boolean increment(short var1);

    public boolean adjustValue(short var1, float var2);

    public float adjustOrPutValue(short var1, float var2, float var3);
}

