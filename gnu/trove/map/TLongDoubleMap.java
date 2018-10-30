/*
 * Decompiled with CFR 0_132.
 */
package gnu.trove.map;

import gnu.trove.TDoubleCollection;
import gnu.trove.function.TDoubleFunction;
import gnu.trove.iterator.TLongDoubleIterator;
import gnu.trove.procedure.TDoubleProcedure;
import gnu.trove.procedure.TLongDoubleProcedure;
import gnu.trove.procedure.TLongProcedure;
import gnu.trove.set.TLongSet;
import java.util.Map;

public interface TLongDoubleMap {
    public long getNoEntryKey();

    public double getNoEntryValue();

    public double put(long var1, double var3);

    public double putIfAbsent(long var1, double var3);

    public void putAll(Map<? extends Long, ? extends Double> var1);

    public void putAll(TLongDoubleMap var1);

    public double get(long var1);

    public void clear();

    public boolean isEmpty();

    public double remove(long var1);

    public int size();

    public TLongSet keySet();

    public long[] keys();

    public long[] keys(long[] var1);

    public TDoubleCollection valueCollection();

    public double[] values();

    public double[] values(double[] var1);

    public boolean containsValue(double var1);

    public boolean containsKey(long var1);

    public TLongDoubleIterator iterator();

    public boolean forEachKey(TLongProcedure var1);

    public boolean forEachValue(TDoubleProcedure var1);

    public boolean forEachEntry(TLongDoubleProcedure var1);

    public void transformValues(TDoubleFunction var1);

    public boolean retainEntries(TLongDoubleProcedure var1);

    public boolean increment(long var1);

    public boolean adjustValue(long var1, double var3);

    public double adjustOrPutValue(long var1, double var3, double var5);
}

