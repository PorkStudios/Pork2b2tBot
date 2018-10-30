/*
 * Decompiled with CFR 0_132.
 */
package gnu.trove.map;

import gnu.trove.TIntCollection;
import gnu.trove.function.TIntFunction;
import gnu.trove.iterator.TShortIntIterator;
import gnu.trove.procedure.TIntProcedure;
import gnu.trove.procedure.TShortIntProcedure;
import gnu.trove.procedure.TShortProcedure;
import gnu.trove.set.TShortSet;
import java.util.Map;

public interface TShortIntMap {
    public short getNoEntryKey();

    public int getNoEntryValue();

    public int put(short var1, int var2);

    public int putIfAbsent(short var1, int var2);

    public void putAll(Map<? extends Short, ? extends Integer> var1);

    public void putAll(TShortIntMap var1);

    public int get(short var1);

    public void clear();

    public boolean isEmpty();

    public int remove(short var1);

    public int size();

    public TShortSet keySet();

    public short[] keys();

    public short[] keys(short[] var1);

    public TIntCollection valueCollection();

    public int[] values();

    public int[] values(int[] var1);

    public boolean containsValue(int var1);

    public boolean containsKey(short var1);

    public TShortIntIterator iterator();

    public boolean forEachKey(TShortProcedure var1);

    public boolean forEachValue(TIntProcedure var1);

    public boolean forEachEntry(TShortIntProcedure var1);

    public void transformValues(TIntFunction var1);

    public boolean retainEntries(TShortIntProcedure var1);

    public boolean increment(short var1);

    public boolean adjustValue(short var1, int var2);

    public int adjustOrPutValue(short var1, int var2, int var3);
}

