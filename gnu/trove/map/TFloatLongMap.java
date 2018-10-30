/*
 * Decompiled with CFR 0_132.
 */
package gnu.trove.map;

import gnu.trove.TLongCollection;
import gnu.trove.function.TLongFunction;
import gnu.trove.iterator.TFloatLongIterator;
import gnu.trove.procedure.TFloatLongProcedure;
import gnu.trove.procedure.TFloatProcedure;
import gnu.trove.procedure.TLongProcedure;
import gnu.trove.set.TFloatSet;
import java.util.Map;

public interface TFloatLongMap {
    public float getNoEntryKey();

    public long getNoEntryValue();

    public long put(float var1, long var2);

    public long putIfAbsent(float var1, long var2);

    public void putAll(Map<? extends Float, ? extends Long> var1);

    public void putAll(TFloatLongMap var1);

    public long get(float var1);

    public void clear();

    public boolean isEmpty();

    public long remove(float var1);

    public int size();

    public TFloatSet keySet();

    public float[] keys();

    public float[] keys(float[] var1);

    public TLongCollection valueCollection();

    public long[] values();

    public long[] values(long[] var1);

    public boolean containsValue(long var1);

    public boolean containsKey(float var1);

    public TFloatLongIterator iterator();

    public boolean forEachKey(TFloatProcedure var1);

    public boolean forEachValue(TLongProcedure var1);

    public boolean forEachEntry(TFloatLongProcedure var1);

    public void transformValues(TLongFunction var1);

    public boolean retainEntries(TFloatLongProcedure var1);

    public boolean increment(float var1);

    public boolean adjustValue(float var1, long var2);

    public long adjustOrPutValue(float var1, long var2, long var4);
}

