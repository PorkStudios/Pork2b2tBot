/*
 * Decompiled with CFR 0_132.
 */
package gnu.trove.map;

import gnu.trove.TLongCollection;
import gnu.trove.function.TLongFunction;
import gnu.trove.iterator.TCharLongIterator;
import gnu.trove.procedure.TCharLongProcedure;
import gnu.trove.procedure.TCharProcedure;
import gnu.trove.procedure.TLongProcedure;
import gnu.trove.set.TCharSet;
import java.util.Map;

public interface TCharLongMap {
    public char getNoEntryKey();

    public long getNoEntryValue();

    public long put(char var1, long var2);

    public long putIfAbsent(char var1, long var2);

    public void putAll(Map<? extends Character, ? extends Long> var1);

    public void putAll(TCharLongMap var1);

    public long get(char var1);

    public void clear();

    public boolean isEmpty();

    public long remove(char var1);

    public int size();

    public TCharSet keySet();

    public char[] keys();

    public char[] keys(char[] var1);

    public TLongCollection valueCollection();

    public long[] values();

    public long[] values(long[] var1);

    public boolean containsValue(long var1);

    public boolean containsKey(char var1);

    public TCharLongIterator iterator();

    public boolean forEachKey(TCharProcedure var1);

    public boolean forEachValue(TLongProcedure var1);

    public boolean forEachEntry(TCharLongProcedure var1);

    public void transformValues(TLongFunction var1);

    public boolean retainEntries(TCharLongProcedure var1);

    public boolean increment(char var1);

    public boolean adjustValue(char var1, long var2);

    public long adjustOrPutValue(char var1, long var2, long var4);
}

