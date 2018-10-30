/*
 * Decompiled with CFR 0_132.
 */
package gnu.trove.map;

import gnu.trove.TByteCollection;
import gnu.trove.function.TByteFunction;
import gnu.trove.iterator.TShortByteIterator;
import gnu.trove.procedure.TByteProcedure;
import gnu.trove.procedure.TShortByteProcedure;
import gnu.trove.procedure.TShortProcedure;
import gnu.trove.set.TShortSet;
import java.util.Map;

public interface TShortByteMap {
    public short getNoEntryKey();

    public byte getNoEntryValue();

    public byte put(short var1, byte var2);

    public byte putIfAbsent(short var1, byte var2);

    public void putAll(Map<? extends Short, ? extends Byte> var1);

    public void putAll(TShortByteMap var1);

    public byte get(short var1);

    public void clear();

    public boolean isEmpty();

    public byte remove(short var1);

    public int size();

    public TShortSet keySet();

    public short[] keys();

    public short[] keys(short[] var1);

    public TByteCollection valueCollection();

    public byte[] values();

    public byte[] values(byte[] var1);

    public boolean containsValue(byte var1);

    public boolean containsKey(short var1);

    public TShortByteIterator iterator();

    public boolean forEachKey(TShortProcedure var1);

    public boolean forEachValue(TByteProcedure var1);

    public boolean forEachEntry(TShortByteProcedure var1);

    public void transformValues(TByteFunction var1);

    public boolean retainEntries(TShortByteProcedure var1);

    public boolean increment(short var1);

    public boolean adjustValue(short var1, byte var2);

    public byte adjustOrPutValue(short var1, byte var2, byte var3);
}

