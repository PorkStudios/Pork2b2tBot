/*
 * Decompiled with CFR 0_132.
 */
package gnu.trove.map;

import gnu.trove.TShortCollection;
import gnu.trove.function.TShortFunction;
import gnu.trove.iterator.TIntShortIterator;
import gnu.trove.procedure.TIntProcedure;
import gnu.trove.procedure.TIntShortProcedure;
import gnu.trove.procedure.TShortProcedure;
import gnu.trove.set.TIntSet;
import java.util.Map;

public interface TIntShortMap {
    public int getNoEntryKey();

    public short getNoEntryValue();

    public short put(int var1, short var2);

    public short putIfAbsent(int var1, short var2);

    public void putAll(Map<? extends Integer, ? extends Short> var1);

    public void putAll(TIntShortMap var1);

    public short get(int var1);

    public void clear();

    public boolean isEmpty();

    public short remove(int var1);

    public int size();

    public TIntSet keySet();

    public int[] keys();

    public int[] keys(int[] var1);

    public TShortCollection valueCollection();

    public short[] values();

    public short[] values(short[] var1);

    public boolean containsValue(short var1);

    public boolean containsKey(int var1);

    public TIntShortIterator iterator();

    public boolean forEachKey(TIntProcedure var1);

    public boolean forEachValue(TShortProcedure var1);

    public boolean forEachEntry(TIntShortProcedure var1);

    public void transformValues(TShortFunction var1);

    public boolean retainEntries(TIntShortProcedure var1);

    public boolean increment(int var1);

    public boolean adjustValue(int var1, short var2);

    public short adjustOrPutValue(int var1, short var2, short var3);
}

