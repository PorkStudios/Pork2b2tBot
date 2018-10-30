/*
 * Decompiled with CFR 0_132.
 */
package gnu.trove.map;

import gnu.trove.TShortCollection;
import gnu.trove.function.TShortFunction;
import gnu.trove.iterator.TLongShortIterator;
import gnu.trove.procedure.TLongProcedure;
import gnu.trove.procedure.TLongShortProcedure;
import gnu.trove.procedure.TShortProcedure;
import gnu.trove.set.TLongSet;
import java.util.Map;

public interface TLongShortMap {
    public long getNoEntryKey();

    public short getNoEntryValue();

    public short put(long var1, short var3);

    public short putIfAbsent(long var1, short var3);

    public void putAll(Map<? extends Long, ? extends Short> var1);

    public void putAll(TLongShortMap var1);

    public short get(long var1);

    public void clear();

    public boolean isEmpty();

    public short remove(long var1);

    public int size();

    public TLongSet keySet();

    public long[] keys();

    public long[] keys(long[] var1);

    public TShortCollection valueCollection();

    public short[] values();

    public short[] values(short[] var1);

    public boolean containsValue(short var1);

    public boolean containsKey(long var1);

    public TLongShortIterator iterator();

    public boolean forEachKey(TLongProcedure var1);

    public boolean forEachValue(TShortProcedure var1);

    public boolean forEachEntry(TLongShortProcedure var1);

    public void transformValues(TShortFunction var1);

    public boolean retainEntries(TLongShortProcedure var1);

    public boolean increment(long var1);

    public boolean adjustValue(long var1, short var3);

    public short adjustOrPutValue(long var1, short var3, short var4);
}

