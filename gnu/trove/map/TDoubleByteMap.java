/*
 * Decompiled with CFR 0_132.
 */
package gnu.trove.map;

import gnu.trove.TByteCollection;
import gnu.trove.function.TByteFunction;
import gnu.trove.iterator.TDoubleByteIterator;
import gnu.trove.procedure.TByteProcedure;
import gnu.trove.procedure.TDoubleByteProcedure;
import gnu.trove.procedure.TDoubleProcedure;
import gnu.trove.set.TDoubleSet;
import java.util.Map;

public interface TDoubleByteMap {
    public double getNoEntryKey();

    public byte getNoEntryValue();

    public byte put(double var1, byte var3);

    public byte putIfAbsent(double var1, byte var3);

    public void putAll(Map<? extends Double, ? extends Byte> var1);

    public void putAll(TDoubleByteMap var1);

    public byte get(double var1);

    public void clear();

    public boolean isEmpty();

    public byte remove(double var1);

    public int size();

    public TDoubleSet keySet();

    public double[] keys();

    public double[] keys(double[] var1);

    public TByteCollection valueCollection();

    public byte[] values();

    public byte[] values(byte[] var1);

    public boolean containsValue(byte var1);

    public boolean containsKey(double var1);

    public TDoubleByteIterator iterator();

    public boolean forEachKey(TDoubleProcedure var1);

    public boolean forEachValue(TByteProcedure var1);

    public boolean forEachEntry(TDoubleByteProcedure var1);

    public void transformValues(TByteFunction var1);

    public boolean retainEntries(TDoubleByteProcedure var1);

    public boolean increment(double var1);

    public boolean adjustValue(double var1, byte var3);

    public byte adjustOrPutValue(double var1, byte var3, byte var4);
}

