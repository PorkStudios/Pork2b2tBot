/*
 * Decompiled with CFR 0_132.
 */
package gnu.trove.map;

import gnu.trove.TCharCollection;
import gnu.trove.function.TCharFunction;
import gnu.trove.iterator.TByteCharIterator;
import gnu.trove.procedure.TByteCharProcedure;
import gnu.trove.procedure.TByteProcedure;
import gnu.trove.procedure.TCharProcedure;
import gnu.trove.set.TByteSet;
import java.util.Map;

public interface TByteCharMap {
    public byte getNoEntryKey();

    public char getNoEntryValue();

    public char put(byte var1, char var2);

    public char putIfAbsent(byte var1, char var2);

    public void putAll(Map<? extends Byte, ? extends Character> var1);

    public void putAll(TByteCharMap var1);

    public char get(byte var1);

    public void clear();

    public boolean isEmpty();

    public char remove(byte var1);

    public int size();

    public TByteSet keySet();

    public byte[] keys();

    public byte[] keys(byte[] var1);

    public TCharCollection valueCollection();

    public char[] values();

    public char[] values(char[] var1);

    public boolean containsValue(char var1);

    public boolean containsKey(byte var1);

    public TByteCharIterator iterator();

    public boolean forEachKey(TByteProcedure var1);

    public boolean forEachValue(TCharProcedure var1);

    public boolean forEachEntry(TByteCharProcedure var1);

    public void transformValues(TCharFunction var1);

    public boolean retainEntries(TByteCharProcedure var1);

    public boolean increment(byte var1);

    public boolean adjustValue(byte var1, char var2);

    public char adjustOrPutValue(byte var1, char var2, char var3);
}

