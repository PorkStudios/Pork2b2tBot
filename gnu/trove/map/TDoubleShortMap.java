/*
 * Decompiled with CFR 0_132.
 */
package gnu.trove.map;

import gnu.trove.TShortCollection;
import gnu.trove.function.TShortFunction;
import gnu.trove.iterator.TDoubleShortIterator;
import gnu.trove.procedure.TDoubleProcedure;
import gnu.trove.procedure.TDoubleShortProcedure;
import gnu.trove.procedure.TShortProcedure;
import gnu.trove.set.TDoubleSet;
import java.util.Map;

public interface TDoubleShortMap {
    public double getNoEntryKey();

    public short getNoEntryValue();

    public short put(double var1, short var3);

    public short putIfAbsent(double var1, short var3);

    public void putAll(Map<? extends Double, ? extends Short> var1);

    public void putAll(TDoubleShortMap var1);

    public short get(double var1);

    public void clear();

    public boolean isEmpty();

    public short remove(double var1);

    public int size();

    public TDoubleSet keySet();

    public double[] keys();

    public double[] keys(double[] var1);

    public TShortCollection valueCollection();

    public short[] values();

    public short[] values(short[] var1);

    public boolean containsValue(short var1);

    public boolean containsKey(double var1);

    public TDoubleShortIterator iterator();

    public boolean forEachKey(TDoubleProcedure var1);

    public boolean forEachValue(TShortProcedure var1);

    public boolean forEachEntry(TDoubleShortProcedure var1);

    public void transformValues(TShortFunction var1);

    public boolean retainEntries(TDoubleShortProcedure var1);

    public boolean increment(double var1);

    public boolean adjustValue(double var1, short var3);

    public short adjustOrPutValue(double var1, short var3, short var4);
}

