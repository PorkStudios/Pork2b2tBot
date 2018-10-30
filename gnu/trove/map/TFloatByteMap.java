/*
 * Decompiled with CFR 0_132.
 */
package gnu.trove.map;

import gnu.trove.TByteCollection;
import gnu.trove.function.TByteFunction;
import gnu.trove.iterator.TFloatByteIterator;
import gnu.trove.procedure.TByteProcedure;
import gnu.trove.procedure.TFloatByteProcedure;
import gnu.trove.procedure.TFloatProcedure;
import gnu.trove.set.TFloatSet;
import java.util.Map;

public interface TFloatByteMap {
    public float getNoEntryKey();

    public byte getNoEntryValue();

    public byte put(float var1, byte var2);

    public byte putIfAbsent(float var1, byte var2);

    public void putAll(Map<? extends Float, ? extends Byte> var1);

    public void putAll(TFloatByteMap var1);

    public byte get(float var1);

    public void clear();

    public boolean isEmpty();

    public byte remove(float var1);

    public int size();

    public TFloatSet keySet();

    public float[] keys();

    public float[] keys(float[] var1);

    public TByteCollection valueCollection();

    public byte[] values();

    public byte[] values(byte[] var1);

    public boolean containsValue(byte var1);

    public boolean containsKey(float var1);

    public TFloatByteIterator iterator();

    public boolean forEachKey(TFloatProcedure var1);

    public boolean forEachValue(TByteProcedure var1);

    public boolean forEachEntry(TFloatByteProcedure var1);

    public void transformValues(TByteFunction var1);

    public boolean retainEntries(TFloatByteProcedure var1);

    public boolean increment(float var1);

    public boolean adjustValue(float var1, byte var2);

    public byte adjustOrPutValue(float var1, byte var2, byte var3);
}

