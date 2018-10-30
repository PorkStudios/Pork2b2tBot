/*
 * Decompiled with CFR 0_132.
 */
package gnu.trove.map;

import gnu.trove.TLongCollection;
import gnu.trove.function.TLongFunction;
import gnu.trove.iterator.TDoubleLongIterator;
import gnu.trove.procedure.TDoubleLongProcedure;
import gnu.trove.procedure.TDoubleProcedure;
import gnu.trove.procedure.TLongProcedure;
import gnu.trove.set.TDoubleSet;
import java.util.Map;

public interface TDoubleLongMap {
    public double getNoEntryKey();

    public long getNoEntryValue();

    public long put(double var1, long var3);

    public long putIfAbsent(double var1, long var3);

    public void putAll(Map<? extends Double, ? extends Long> var1);

    public void putAll(TDoubleLongMap var1);

    public long get(double var1);

    public void clear();

    public boolean isEmpty();

    public long remove(double var1);

    public int size();

    public TDoubleSet keySet();

    public double[] keys();

    public double[] keys(double[] var1);

    public TLongCollection valueCollection();

    public long[] values();

    public long[] values(long[] var1);

    public boolean containsValue(long var1);

    public boolean containsKey(double var1);

    public TDoubleLongIterator iterator();

    public boolean forEachKey(TDoubleProcedure var1);

    public boolean forEachValue(TLongProcedure var1);

    public boolean forEachEntry(TDoubleLongProcedure var1);

    public void transformValues(TLongFunction var1);

    public boolean retainEntries(TDoubleLongProcedure var1);

    public boolean increment(double var1);

    public boolean adjustValue(double var1, long var3);

    public long adjustOrPutValue(double var1, long var3, long var5);
}

