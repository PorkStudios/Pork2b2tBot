/*
 * Decompiled with CFR 0_132.
 */
package gnu.trove.map;

import gnu.trove.TCharCollection;
import gnu.trove.function.TCharFunction;
import gnu.trove.iterator.TShortCharIterator;
import gnu.trove.procedure.TCharProcedure;
import gnu.trove.procedure.TShortCharProcedure;
import gnu.trove.procedure.TShortProcedure;
import gnu.trove.set.TShortSet;
import java.util.Map;

public interface TShortCharMap {
    public short getNoEntryKey();

    public char getNoEntryValue();

    public char put(short var1, char var2);

    public char putIfAbsent(short var1, char var2);

    public void putAll(Map<? extends Short, ? extends Character> var1);

    public void putAll(TShortCharMap var1);

    public char get(short var1);

    public void clear();

    public boolean isEmpty();

    public char remove(short var1);

    public int size();

    public TShortSet keySet();

    public short[] keys();

    public short[] keys(short[] var1);

    public TCharCollection valueCollection();

    public char[] values();

    public char[] values(char[] var1);

    public boolean containsValue(char var1);

    public boolean containsKey(short var1);

    public TShortCharIterator iterator();

    public boolean forEachKey(TShortProcedure var1);

    public boolean forEachValue(TCharProcedure var1);

    public boolean forEachEntry(TShortCharProcedure var1);

    public void transformValues(TCharFunction var1);

    public boolean retainEntries(TShortCharProcedure var1);

    public boolean increment(short var1);

    public boolean adjustValue(short var1, char var2);

    public char adjustOrPutValue(short var1, char var2, char var3);
}

