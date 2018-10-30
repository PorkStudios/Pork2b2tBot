/*
 * Decompiled with CFR 0_132.
 */
package gnu.trove.map;

import gnu.trove.TLongCollection;
import gnu.trove.function.TLongFunction;
import gnu.trove.iterator.TIntLongIterator;
import gnu.trove.procedure.TIntLongProcedure;
import gnu.trove.procedure.TIntProcedure;
import gnu.trove.procedure.TLongProcedure;
import gnu.trove.set.TIntSet;
import java.util.Map;

public interface TIntLongMap {
    public int getNoEntryKey();

    public long getNoEntryValue();

    public long put(int var1, long var2);

    public long putIfAbsent(int var1, long var2);

    public void putAll(Map<? extends Integer, ? extends Long> var1);

    public void putAll(TIntLongMap var1);

    public long get(int var1);

    public void clear();

    public boolean isEmpty();

    public long remove(int var1);

    public int size();

    public TIntSet keySet();

    public int[] keys();

    public int[] keys(int[] var1);

    public TLongCollection valueCollection();

    public long[] values();

    public long[] values(long[] var1);

    public boolean containsValue(long var1);

    public boolean containsKey(int var1);

    public TIntLongIterator iterator();

    public boolean forEachKey(TIntProcedure var1);

    public boolean forEachValue(TLongProcedure var1);

    public boolean forEachEntry(TIntLongProcedure var1);

    public void transformValues(TLongFunction var1);

    public boolean retainEntries(TIntLongProcedure var1);

    public boolean increment(int var1);

    public boolean adjustValue(int var1, long var2);

    public long adjustOrPutValue(int var1, long var2, long var4);
}

