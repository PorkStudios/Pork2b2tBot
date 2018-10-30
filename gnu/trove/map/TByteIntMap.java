/*
 * Decompiled with CFR 0_132.
 */
package gnu.trove.map;

import gnu.trove.TIntCollection;
import gnu.trove.function.TIntFunction;
import gnu.trove.iterator.TByteIntIterator;
import gnu.trove.procedure.TByteIntProcedure;
import gnu.trove.procedure.TByteProcedure;
import gnu.trove.procedure.TIntProcedure;
import gnu.trove.set.TByteSet;
import java.util.Map;

public interface TByteIntMap {
    public byte getNoEntryKey();

    public int getNoEntryValue();

    public int put(byte var1, int var2);

    public int putIfAbsent(byte var1, int var2);

    public void putAll(Map<? extends Byte, ? extends Integer> var1);

    public void putAll(TByteIntMap var1);

    public int get(byte var1);

    public void clear();

    public boolean isEmpty();

    public int remove(byte var1);

    public int size();

    public TByteSet keySet();

    public byte[] keys();

    public byte[] keys(byte[] var1);

    public TIntCollection valueCollection();

    public int[] values();

    public int[] values(int[] var1);

    public boolean containsValue(int var1);

    public boolean containsKey(byte var1);

    public TByteIntIterator iterator();

    public boolean forEachKey(TByteProcedure var1);

    public boolean forEachValue(TIntProcedure var1);

    public boolean forEachEntry(TByteIntProcedure var1);

    public void transformValues(TIntFunction var1);

    public boolean retainEntries(TByteIntProcedure var1);

    public boolean increment(byte var1);

    public boolean adjustValue(byte var1, int var2);

    public int adjustOrPutValue(byte var1, int var2, int var3);
}

