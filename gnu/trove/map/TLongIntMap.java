/*
 * Decompiled with CFR 0_132.
 */
package gnu.trove.map;

import gnu.trove.TIntCollection;
import gnu.trove.function.TIntFunction;
import gnu.trove.iterator.TLongIntIterator;
import gnu.trove.procedure.TIntProcedure;
import gnu.trove.procedure.TLongIntProcedure;
import gnu.trove.procedure.TLongProcedure;
import gnu.trove.set.TLongSet;
import java.util.Map;

public interface TLongIntMap {
    public long getNoEntryKey();

    public int getNoEntryValue();

    public int put(long var1, int var3);

    public int putIfAbsent(long var1, int var3);

    public void putAll(Map<? extends Long, ? extends Integer> var1);

    public void putAll(TLongIntMap var1);

    public int get(long var1);

    public void clear();

    public boolean isEmpty();

    public int remove(long var1);

    public int size();

    public TLongSet keySet();

    public long[] keys();

    public long[] keys(long[] var1);

    public TIntCollection valueCollection();

    public int[] values();

    public int[] values(int[] var1);

    public boolean containsValue(int var1);

    public boolean containsKey(long var1);

    public TLongIntIterator iterator();

    public boolean forEachKey(TLongProcedure var1);

    public boolean forEachValue(TIntProcedure var1);

    public boolean forEachEntry(TLongIntProcedure var1);

    public void transformValues(TIntFunction var1);

    public boolean retainEntries(TLongIntProcedure var1);

    public boolean increment(long var1);

    public boolean adjustValue(long var1, int var3);

    public int adjustOrPutValue(long var1, int var3, int var4);
}

