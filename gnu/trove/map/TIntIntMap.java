/*
 * Decompiled with CFR 0_132.
 */
package gnu.trove.map;

import gnu.trove.TIntCollection;
import gnu.trove.function.TIntFunction;
import gnu.trove.iterator.TIntIntIterator;
import gnu.trove.procedure.TIntIntProcedure;
import gnu.trove.procedure.TIntProcedure;
import gnu.trove.set.TIntSet;
import java.util.Map;

public interface TIntIntMap {
    public int getNoEntryKey();

    public int getNoEntryValue();

    public int put(int var1, int var2);

    public int putIfAbsent(int var1, int var2);

    public void putAll(Map<? extends Integer, ? extends Integer> var1);

    public void putAll(TIntIntMap var1);

    public int get(int var1);

    public void clear();

    public boolean isEmpty();

    public int remove(int var1);

    public int size();

    public TIntSet keySet();

    public int[] keys();

    public int[] keys(int[] var1);

    public TIntCollection valueCollection();

    public int[] values();

    public int[] values(int[] var1);

    public boolean containsValue(int var1);

    public boolean containsKey(int var1);

    public TIntIntIterator iterator();

    public boolean forEachKey(TIntProcedure var1);

    public boolean forEachValue(TIntProcedure var1);

    public boolean forEachEntry(TIntIntProcedure var1);

    public void transformValues(TIntFunction var1);

    public boolean retainEntries(TIntIntProcedure var1);

    public boolean increment(int var1);

    public boolean adjustValue(int var1, int var2);

    public int adjustOrPutValue(int var1, int var2, int var3);
}

