/*
 * Decompiled with CFR 0_132.
 */
package gnu.trove.map;

import gnu.trove.TIntCollection;
import gnu.trove.function.TIntFunction;
import gnu.trove.iterator.TCharIntIterator;
import gnu.trove.procedure.TCharIntProcedure;
import gnu.trove.procedure.TCharProcedure;
import gnu.trove.procedure.TIntProcedure;
import gnu.trove.set.TCharSet;
import java.util.Map;

public interface TCharIntMap {
    public char getNoEntryKey();

    public int getNoEntryValue();

    public int put(char var1, int var2);

    public int putIfAbsent(char var1, int var2);

    public void putAll(Map<? extends Character, ? extends Integer> var1);

    public void putAll(TCharIntMap var1);

    public int get(char var1);

    public void clear();

    public boolean isEmpty();

    public int remove(char var1);

    public int size();

    public TCharSet keySet();

    public char[] keys();

    public char[] keys(char[] var1);

    public TIntCollection valueCollection();

    public int[] values();

    public int[] values(int[] var1);

    public boolean containsValue(int var1);

    public boolean containsKey(char var1);

    public TCharIntIterator iterator();

    public boolean forEachKey(TCharProcedure var1);

    public boolean forEachValue(TIntProcedure var1);

    public boolean forEachEntry(TCharIntProcedure var1);

    public void transformValues(TIntFunction var1);

    public boolean retainEntries(TCharIntProcedure var1);

    public boolean increment(char var1);

    public boolean adjustValue(char var1, int var2);

    public int adjustOrPutValue(char var1, int var2, int var3);
}

