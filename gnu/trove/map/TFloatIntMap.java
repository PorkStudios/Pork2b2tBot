/*
 * Decompiled with CFR 0_132.
 */
package gnu.trove.map;

import gnu.trove.TIntCollection;
import gnu.trove.function.TIntFunction;
import gnu.trove.iterator.TFloatIntIterator;
import gnu.trove.procedure.TFloatIntProcedure;
import gnu.trove.procedure.TFloatProcedure;
import gnu.trove.procedure.TIntProcedure;
import gnu.trove.set.TFloatSet;
import java.util.Map;

public interface TFloatIntMap {
    public float getNoEntryKey();

    public int getNoEntryValue();

    public int put(float var1, int var2);

    public int putIfAbsent(float var1, int var2);

    public void putAll(Map<? extends Float, ? extends Integer> var1);

    public void putAll(TFloatIntMap var1);

    public int get(float var1);

    public void clear();

    public boolean isEmpty();

    public int remove(float var1);

    public int size();

    public TFloatSet keySet();

    public float[] keys();

    public float[] keys(float[] var1);

    public TIntCollection valueCollection();

    public int[] values();

    public int[] values(int[] var1);

    public boolean containsValue(int var1);

    public boolean containsKey(float var1);

    public TFloatIntIterator iterator();

    public boolean forEachKey(TFloatProcedure var1);

    public boolean forEachValue(TIntProcedure var1);

    public boolean forEachEntry(TFloatIntProcedure var1);

    public void transformValues(TIntFunction var1);

    public boolean retainEntries(TFloatIntProcedure var1);

    public boolean increment(float var1);

    public boolean adjustValue(float var1, int var2);

    public int adjustOrPutValue(float var1, int var2, int var3);
}

