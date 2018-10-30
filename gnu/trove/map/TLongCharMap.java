/*
 * Decompiled with CFR 0_132.
 */
package gnu.trove.map;

import gnu.trove.TCharCollection;
import gnu.trove.function.TCharFunction;
import gnu.trove.iterator.TLongCharIterator;
import gnu.trove.procedure.TCharProcedure;
import gnu.trove.procedure.TLongCharProcedure;
import gnu.trove.procedure.TLongProcedure;
import gnu.trove.set.TLongSet;
import java.util.Map;

public interface TLongCharMap {
    public long getNoEntryKey();

    public char getNoEntryValue();

    public char put(long var1, char var3);

    public char putIfAbsent(long var1, char var3);

    public void putAll(Map<? extends Long, ? extends Character> var1);

    public void putAll(TLongCharMap var1);

    public char get(long var1);

    public void clear();

    public boolean isEmpty();

    public char remove(long var1);

    public int size();

    public TLongSet keySet();

    public long[] keys();

    public long[] keys(long[] var1);

    public TCharCollection valueCollection();

    public char[] values();

    public char[] values(char[] var1);

    public boolean containsValue(char var1);

    public boolean containsKey(long var1);

    public TLongCharIterator iterator();

    public boolean forEachKey(TLongProcedure var1);

    public boolean forEachValue(TCharProcedure var1);

    public boolean forEachEntry(TLongCharProcedure var1);

    public void transformValues(TCharFunction var1);

    public boolean retainEntries(TLongCharProcedure var1);

    public boolean increment(long var1);

    public boolean adjustValue(long var1, char var3);

    public char adjustOrPutValue(long var1, char var3, char var4);
}

