/*
 * Decompiled with CFR 0_132.
 */
package gnu.trove.map;

import gnu.trove.TShortCollection;
import gnu.trove.function.TShortFunction;
import gnu.trove.iterator.TShortShortIterator;
import gnu.trove.procedure.TShortProcedure;
import gnu.trove.procedure.TShortShortProcedure;
import gnu.trove.set.TShortSet;
import java.util.Map;

public interface TShortShortMap {
    public short getNoEntryKey();

    public short getNoEntryValue();

    public short put(short var1, short var2);

    public short putIfAbsent(short var1, short var2);

    public void putAll(Map<? extends Short, ? extends Short> var1);

    public void putAll(TShortShortMap var1);

    public short get(short var1);

    public void clear();

    public boolean isEmpty();

    public short remove(short var1);

    public int size();

    public TShortSet keySet();

    public short[] keys();

    public short[] keys(short[] var1);

    public TShortCollection valueCollection();

    public short[] values();

    public short[] values(short[] var1);

    public boolean containsValue(short var1);

    public boolean containsKey(short var1);

    public TShortShortIterator iterator();

    public boolean forEachKey(TShortProcedure var1);

    public boolean forEachValue(TShortProcedure var1);

    public boolean forEachEntry(TShortShortProcedure var1);

    public void transformValues(TShortFunction var1);

    public boolean retainEntries(TShortShortProcedure var1);

    public boolean increment(short var1);

    public boolean adjustValue(short var1, short var2);

    public short adjustOrPutValue(short var1, short var2, short var3);
}

