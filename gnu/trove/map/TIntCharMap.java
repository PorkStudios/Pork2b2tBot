/*
 * Decompiled with CFR 0_132.
 */
package gnu.trove.map;

import gnu.trove.TCharCollection;
import gnu.trove.function.TCharFunction;
import gnu.trove.iterator.TIntCharIterator;
import gnu.trove.procedure.TCharProcedure;
import gnu.trove.procedure.TIntCharProcedure;
import gnu.trove.procedure.TIntProcedure;
import gnu.trove.set.TIntSet;
import java.util.Map;

public interface TIntCharMap {
    public int getNoEntryKey();

    public char getNoEntryValue();

    public char put(int var1, char var2);

    public char putIfAbsent(int var1, char var2);

    public void putAll(Map<? extends Integer, ? extends Character> var1);

    public void putAll(TIntCharMap var1);

    public char get(int var1);

    public void clear();

    public boolean isEmpty();

    public char remove(int var1);

    public int size();

    public TIntSet keySet();

    public int[] keys();

    public int[] keys(int[] var1);

    public TCharCollection valueCollection();

    public char[] values();

    public char[] values(char[] var1);

    public boolean containsValue(char var1);

    public boolean containsKey(int var1);

    public TIntCharIterator iterator();

    public boolean forEachKey(TIntProcedure var1);

    public boolean forEachValue(TCharProcedure var1);

    public boolean forEachEntry(TIntCharProcedure var1);

    public void transformValues(TCharFunction var1);

    public boolean retainEntries(TIntCharProcedure var1);

    public boolean increment(int var1);

    public boolean adjustValue(int var1, char var2);

    public char adjustOrPutValue(int var1, char var2, char var3);
}

