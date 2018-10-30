/*
 * Decompiled with CFR 0_132.
 */
package gnu.trove.map;

import gnu.trove.TShortCollection;
import gnu.trove.function.TShortFunction;
import gnu.trove.iterator.TByteShortIterator;
import gnu.trove.procedure.TByteProcedure;
import gnu.trove.procedure.TByteShortProcedure;
import gnu.trove.procedure.TShortProcedure;
import gnu.trove.set.TByteSet;
import java.util.Map;

public interface TByteShortMap {
    public byte getNoEntryKey();

    public short getNoEntryValue();

    public short put(byte var1, short var2);

    public short putIfAbsent(byte var1, short var2);

    public void putAll(Map<? extends Byte, ? extends Short> var1);

    public void putAll(TByteShortMap var1);

    public short get(byte var1);

    public void clear();

    public boolean isEmpty();

    public short remove(byte var1);

    public int size();

    public TByteSet keySet();

    public byte[] keys();

    public byte[] keys(byte[] var1);

    public TShortCollection valueCollection();

    public short[] values();

    public short[] values(short[] var1);

    public boolean containsValue(short var1);

    public boolean containsKey(byte var1);

    public TByteShortIterator iterator();

    public boolean forEachKey(TByteProcedure var1);

    public boolean forEachValue(TShortProcedure var1);

    public boolean forEachEntry(TByteShortProcedure var1);

    public void transformValues(TShortFunction var1);

    public boolean retainEntries(TByteShortProcedure var1);

    public boolean increment(byte var1);

    public boolean adjustValue(byte var1, short var2);

    public short adjustOrPutValue(byte var1, short var2, short var3);
}

