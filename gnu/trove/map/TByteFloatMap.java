/*
 * Decompiled with CFR 0_132.
 */
package gnu.trove.map;

import gnu.trove.TFloatCollection;
import gnu.trove.function.TFloatFunction;
import gnu.trove.iterator.TByteFloatIterator;
import gnu.trove.procedure.TByteFloatProcedure;
import gnu.trove.procedure.TByteProcedure;
import gnu.trove.procedure.TFloatProcedure;
import gnu.trove.set.TByteSet;
import java.util.Map;

public interface TByteFloatMap {
    public byte getNoEntryKey();

    public float getNoEntryValue();

    public float put(byte var1, float var2);

    public float putIfAbsent(byte var1, float var2);

    public void putAll(Map<? extends Byte, ? extends Float> var1);

    public void putAll(TByteFloatMap var1);

    public float get(byte var1);

    public void clear();

    public boolean isEmpty();

    public float remove(byte var1);

    public int size();

    public TByteSet keySet();

    public byte[] keys();

    public byte[] keys(byte[] var1);

    public TFloatCollection valueCollection();

    public float[] values();

    public float[] values(float[] var1);

    public boolean containsValue(float var1);

    public boolean containsKey(byte var1);

    public TByteFloatIterator iterator();

    public boolean forEachKey(TByteProcedure var1);

    public boolean forEachValue(TFloatProcedure var1);

    public boolean forEachEntry(TByteFloatProcedure var1);

    public void transformValues(TFloatFunction var1);

    public boolean retainEntries(TByteFloatProcedure var1);

    public boolean increment(byte var1);

    public boolean adjustValue(byte var1, float var2);

    public float adjustOrPutValue(byte var1, float var2, float var3);
}

