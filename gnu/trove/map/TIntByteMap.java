/*
 * Decompiled with CFR 0_132.
 */
package gnu.trove.map;

import gnu.trove.TByteCollection;
import gnu.trove.function.TByteFunction;
import gnu.trove.iterator.TIntByteIterator;
import gnu.trove.procedure.TByteProcedure;
import gnu.trove.procedure.TIntByteProcedure;
import gnu.trove.procedure.TIntProcedure;
import gnu.trove.set.TIntSet;
import java.util.Map;

public interface TIntByteMap {
    public int getNoEntryKey();

    public byte getNoEntryValue();

    public byte put(int var1, byte var2);

    public byte putIfAbsent(int var1, byte var2);

    public void putAll(Map<? extends Integer, ? extends Byte> var1);

    public void putAll(TIntByteMap var1);

    public byte get(int var1);

    public void clear();

    public boolean isEmpty();

    public byte remove(int var1);

    public int size();

    public TIntSet keySet();

    public int[] keys();

    public int[] keys(int[] var1);

    public TByteCollection valueCollection();

    public byte[] values();

    public byte[] values(byte[] var1);

    public boolean containsValue(byte var1);

    public boolean containsKey(int var1);

    public TIntByteIterator iterator();

    public boolean forEachKey(TIntProcedure var1);

    public boolean forEachValue(TByteProcedure var1);

    public boolean forEachEntry(TIntByteProcedure var1);

    public void transformValues(TByteFunction var1);

    public boolean retainEntries(TIntByteProcedure var1);

    public boolean increment(int var1);

    public boolean adjustValue(int var1, byte var2);

    public byte adjustOrPutValue(int var1, byte var2, byte var3);
}

