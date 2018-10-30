/*
 * Decompiled with CFR 0_132.
 */
package gnu.trove.map;

import gnu.trove.TByteCollection;
import gnu.trove.function.TByteFunction;
import gnu.trove.iterator.TByteByteIterator;
import gnu.trove.procedure.TByteByteProcedure;
import gnu.trove.procedure.TByteProcedure;
import gnu.trove.set.TByteSet;
import java.util.Map;

public interface TByteByteMap {
    public byte getNoEntryKey();

    public byte getNoEntryValue();

    public byte put(byte var1, byte var2);

    public byte putIfAbsent(byte var1, byte var2);

    public void putAll(Map<? extends Byte, ? extends Byte> var1);

    public void putAll(TByteByteMap var1);

    public byte get(byte var1);

    public void clear();

    public boolean isEmpty();

    public byte remove(byte var1);

    public int size();

    public TByteSet keySet();

    public byte[] keys();

    public byte[] keys(byte[] var1);

    public TByteCollection valueCollection();

    public byte[] values();

    public byte[] values(byte[] var1);

    public boolean containsValue(byte var1);

    public boolean containsKey(byte var1);

    public TByteByteIterator iterator();

    public boolean forEachKey(TByteProcedure var1);

    public boolean forEachValue(TByteProcedure var1);

    public boolean forEachEntry(TByteByteProcedure var1);

    public void transformValues(TByteFunction var1);

    public boolean retainEntries(TByteByteProcedure var1);

    public boolean increment(byte var1);

    public boolean adjustValue(byte var1, byte var2);

    public byte adjustOrPutValue(byte var1, byte var2, byte var3);
}

