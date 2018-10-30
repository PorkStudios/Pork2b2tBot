/*
 * Decompiled with CFR 0_132.
 */
package gnu.trove.map;

import gnu.trove.TDoubleCollection;
import gnu.trove.function.TDoubleFunction;
import gnu.trove.iterator.TShortDoubleIterator;
import gnu.trove.procedure.TDoubleProcedure;
import gnu.trove.procedure.TShortDoubleProcedure;
import gnu.trove.procedure.TShortProcedure;
import gnu.trove.set.TShortSet;
import java.util.Map;

public interface TShortDoubleMap {
    public short getNoEntryKey();

    public double getNoEntryValue();

    public double put(short var1, double var2);

    public double putIfAbsent(short var1, double var2);

    public void putAll(Map<? extends Short, ? extends Double> var1);

    public void putAll(TShortDoubleMap var1);

    public double get(short var1);

    public void clear();

    public boolean isEmpty();

    public double remove(short var1);

    public int size();

    public TShortSet keySet();

    public short[] keys();

    public short[] keys(short[] var1);

    public TDoubleCollection valueCollection();

    public double[] values();

    public double[] values(double[] var1);

    public boolean containsValue(double var1);

    public boolean containsKey(short var1);

    public TShortDoubleIterator iterator();

    public boolean forEachKey(TShortProcedure var1);

    public boolean forEachValue(TDoubleProcedure var1);

    public boolean forEachEntry(TShortDoubleProcedure var1);

    public void transformValues(TDoubleFunction var1);

    public boolean retainEntries(TShortDoubleProcedure var1);

    public boolean increment(short var1);

    public boolean adjustValue(short var1, double var2);

    public double adjustOrPutValue(short var1, double var2, double var4);
}

