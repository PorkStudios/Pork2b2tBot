/*
 * Decompiled with CFR 0_132.
 */
package gnu.trove.map;

import gnu.trove.TLongCollection;
import gnu.trove.function.TLongFunction;
import gnu.trove.iterator.TShortLongIterator;
import gnu.trove.procedure.TLongProcedure;
import gnu.trove.procedure.TShortLongProcedure;
import gnu.trove.procedure.TShortProcedure;
import gnu.trove.set.TShortSet;
import java.util.Map;

public interface TShortLongMap {
    public short getNoEntryKey();

    public long getNoEntryValue();

    public long put(short var1, long var2);

    public long putIfAbsent(short var1, long var2);

    public void putAll(Map<? extends Short, ? extends Long> var1);

    public void putAll(TShortLongMap var1);

    public long get(short var1);

    public void clear();

    public boolean isEmpty();

    public long remove(short var1);

    public int size();

    public TShortSet keySet();

    public short[] keys();

    public short[] keys(short[] var1);

    public TLongCollection valueCollection();

    public long[] values();

    public long[] values(long[] var1);

    public boolean containsValue(long var1);

    public boolean containsKey(short var1);

    public TShortLongIterator iterator();

    public boolean forEachKey(TShortProcedure var1);

    public boolean forEachValue(TLongProcedure var1);

    public boolean forEachEntry(TShortLongProcedure var1);

    public void transformValues(TLongFunction var1);

    public boolean retainEntries(TShortLongProcedure var1);

    public boolean increment(short var1);

    public boolean adjustValue(short var1, long var2);

    public long adjustOrPutValue(short var1, long var2, long var4);
}

