/*
 * Decompiled with CFR 0_132.
 */
package gnu.trove.map;

import gnu.trove.TLongCollection;
import gnu.trove.function.TLongFunction;
import gnu.trove.iterator.TLongLongIterator;
import gnu.trove.procedure.TLongLongProcedure;
import gnu.trove.procedure.TLongProcedure;
import gnu.trove.set.TLongSet;
import java.util.Map;

public interface TLongLongMap {
    public long getNoEntryKey();

    public long getNoEntryValue();

    public long put(long var1, long var3);

    public long putIfAbsent(long var1, long var3);

    public void putAll(Map<? extends Long, ? extends Long> var1);

    public void putAll(TLongLongMap var1);

    public long get(long var1);

    public void clear();

    public boolean isEmpty();

    public long remove(long var1);

    public int size();

    public TLongSet keySet();

    public long[] keys();

    public long[] keys(long[] var1);

    public TLongCollection valueCollection();

    public long[] values();

    public long[] values(long[] var1);

    public boolean containsValue(long var1);

    public boolean containsKey(long var1);

    public TLongLongIterator iterator();

    public boolean forEachKey(TLongProcedure var1);

    public boolean forEachValue(TLongProcedure var1);

    public boolean forEachEntry(TLongLongProcedure var1);

    public void transformValues(TLongFunction var1);

    public boolean retainEntries(TLongLongProcedure var1);

    public boolean increment(long var1);

    public boolean adjustValue(long var1, long var3);

    public long adjustOrPutValue(long var1, long var3, long var5);
}

