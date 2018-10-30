/*
 * Decompiled with CFR 0_132.
 */
package gnu.trove.map;

import gnu.trove.TCharCollection;
import gnu.trove.function.TCharFunction;
import gnu.trove.iterator.TCharCharIterator;
import gnu.trove.procedure.TCharCharProcedure;
import gnu.trove.procedure.TCharProcedure;
import gnu.trove.set.TCharSet;
import java.util.Map;

public interface TCharCharMap {
    public char getNoEntryKey();

    public char getNoEntryValue();

    public char put(char var1, char var2);

    public char putIfAbsent(char var1, char var2);

    public void putAll(Map<? extends Character, ? extends Character> var1);

    public void putAll(TCharCharMap var1);

    public char get(char var1);

    public void clear();

    public boolean isEmpty();

    public char remove(char var1);

    public int size();

    public TCharSet keySet();

    public char[] keys();

    public char[] keys(char[] var1);

    public TCharCollection valueCollection();

    public char[] values();

    public char[] values(char[] var1);

    public boolean containsValue(char var1);

    public boolean containsKey(char var1);

    public TCharCharIterator iterator();

    public boolean forEachKey(TCharProcedure var1);

    public boolean forEachValue(TCharProcedure var1);

    public boolean forEachEntry(TCharCharProcedure var1);

    public void transformValues(TCharFunction var1);

    public boolean retainEntries(TCharCharProcedure var1);

    public boolean increment(char var1);

    public boolean adjustValue(char var1, char var2);

    public char adjustOrPutValue(char var1, char var2, char var3);
}

