/*
 * Decompiled with CFR 0_132.
 */
package gnu.trove.map;

import gnu.trove.TShortCollection;
import gnu.trove.function.TShortFunction;
import gnu.trove.iterator.TCharShortIterator;
import gnu.trove.procedure.TCharProcedure;
import gnu.trove.procedure.TCharShortProcedure;
import gnu.trove.procedure.TShortProcedure;
import gnu.trove.set.TCharSet;
import java.util.Map;

public interface TCharShortMap {
    public char getNoEntryKey();

    public short getNoEntryValue();

    public short put(char var1, short var2);

    public short putIfAbsent(char var1, short var2);

    public void putAll(Map<? extends Character, ? extends Short> var1);

    public void putAll(TCharShortMap var1);

    public short get(char var1);

    public void clear();

    public boolean isEmpty();

    public short remove(char var1);

    public int size();

    public TCharSet keySet();

    public char[] keys();

    public char[] keys(char[] var1);

    public TShortCollection valueCollection();

    public short[] values();

    public short[] values(short[] var1);

    public boolean containsValue(short var1);

    public boolean containsKey(char var1);

    public TCharShortIterator iterator();

    public boolean forEachKey(TCharProcedure var1);

    public boolean forEachValue(TShortProcedure var1);

    public boolean forEachEntry(TCharShortProcedure var1);

    public void transformValues(TShortFunction var1);

    public boolean retainEntries(TCharShortProcedure var1);

    public boolean increment(char var1);

    public boolean adjustValue(char var1, short var2);

    public short adjustOrPutValue(char var1, short var2, short var3);
}

