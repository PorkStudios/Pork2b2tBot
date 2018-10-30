/*
 * Decompiled with CFR 0_132.
 */
package gnu.trove.map;

import gnu.trove.TShortCollection;
import gnu.trove.function.TShortFunction;
import gnu.trove.iterator.TFloatShortIterator;
import gnu.trove.procedure.TFloatProcedure;
import gnu.trove.procedure.TFloatShortProcedure;
import gnu.trove.procedure.TShortProcedure;
import gnu.trove.set.TFloatSet;
import java.util.Map;

public interface TFloatShortMap {
    public float getNoEntryKey();

    public short getNoEntryValue();

    public short put(float var1, short var2);

    public short putIfAbsent(float var1, short var2);

    public void putAll(Map<? extends Float, ? extends Short> var1);

    public void putAll(TFloatShortMap var1);

    public short get(float var1);

    public void clear();

    public boolean isEmpty();

    public short remove(float var1);

    public int size();

    public TFloatSet keySet();

    public float[] keys();

    public float[] keys(float[] var1);

    public TShortCollection valueCollection();

    public short[] values();

    public short[] values(short[] var1);

    public boolean containsValue(short var1);

    public boolean containsKey(float var1);

    public TFloatShortIterator iterator();

    public boolean forEachKey(TFloatProcedure var1);

    public boolean forEachValue(TShortProcedure var1);

    public boolean forEachEntry(TFloatShortProcedure var1);

    public void transformValues(TShortFunction var1);

    public boolean retainEntries(TFloatShortProcedure var1);

    public boolean increment(float var1);

    public boolean adjustValue(float var1, short var2);

    public short adjustOrPutValue(float var1, short var2, short var3);
}

