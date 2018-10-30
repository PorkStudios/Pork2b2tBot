/*
 * Decompiled with CFR 0_132.
 */
package gnu.trove.map;

import gnu.trove.TDoubleCollection;
import gnu.trove.function.TDoubleFunction;
import gnu.trove.iterator.TByteDoubleIterator;
import gnu.trove.procedure.TByteDoubleProcedure;
import gnu.trove.procedure.TByteProcedure;
import gnu.trove.procedure.TDoubleProcedure;
import gnu.trove.set.TByteSet;
import java.util.Map;

public interface TByteDoubleMap {
    public byte getNoEntryKey();

    public double getNoEntryValue();

    public double put(byte var1, double var2);

    public double putIfAbsent(byte var1, double var2);

    public void putAll(Map<? extends Byte, ? extends Double> var1);

    public void putAll(TByteDoubleMap var1);

    public double get(byte var1);

    public void clear();

    public boolean isEmpty();

    public double remove(byte var1);

    public int size();

    public TByteSet keySet();

    public byte[] keys();

    public byte[] keys(byte[] var1);

    public TDoubleCollection valueCollection();

    public double[] values();

    public double[] values(double[] var1);

    public boolean containsValue(double var1);

    public boolean containsKey(byte var1);

    public TByteDoubleIterator iterator();

    public boolean forEachKey(TByteProcedure var1);

    public boolean forEachValue(TDoubleProcedure var1);

    public boolean forEachEntry(TByteDoubleProcedure var1);

    public void transformValues(TDoubleFunction var1);

    public boolean retainEntries(TByteDoubleProcedure var1);

    public boolean increment(byte var1);

    public boolean adjustValue(byte var1, double var2);

    public double adjustOrPutValue(byte var1, double var2, double var4);
}

