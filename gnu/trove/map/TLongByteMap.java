/*
 * Decompiled with CFR 0_132.
 */
package gnu.trove.map;

import gnu.trove.TByteCollection;
import gnu.trove.function.TByteFunction;
import gnu.trove.iterator.TLongByteIterator;
import gnu.trove.procedure.TByteProcedure;
import gnu.trove.procedure.TLongByteProcedure;
import gnu.trove.procedure.TLongProcedure;
import gnu.trove.set.TLongSet;
import java.util.Map;

public interface TLongByteMap {
    public long getNoEntryKey();

    public byte getNoEntryValue();

    public byte put(long var1, byte var3);

    public byte putIfAbsent(long var1, byte var3);

    public void putAll(Map<? extends Long, ? extends Byte> var1);

    public void putAll(TLongByteMap var1);

    public byte get(long var1);

    public void clear();

    public boolean isEmpty();

    public byte remove(long var1);

    public int size();

    public TLongSet keySet();

    public long[] keys();

    public long[] keys(long[] var1);

    public TByteCollection valueCollection();

    public byte[] values();

    public byte[] values(byte[] var1);

    public boolean containsValue(byte var1);

    public boolean containsKey(long var1);

    public TLongByteIterator iterator();

    public boolean forEachKey(TLongProcedure var1);

    public boolean forEachValue(TByteProcedure var1);

    public boolean forEachEntry(TLongByteProcedure var1);

    public void transformValues(TByteFunction var1);

    public boolean retainEntries(TLongByteProcedure var1);

    public boolean increment(long var1);

    public boolean adjustValue(long var1, byte var3);

    public byte adjustOrPutValue(long var1, byte var3, byte var4);
}

