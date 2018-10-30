/*
 * Decompiled with CFR 0_132.
 */
package gnu.trove.map;

import gnu.trove.TCharCollection;
import gnu.trove.function.TCharFunction;
import gnu.trove.iterator.TDoubleCharIterator;
import gnu.trove.procedure.TCharProcedure;
import gnu.trove.procedure.TDoubleCharProcedure;
import gnu.trove.procedure.TDoubleProcedure;
import gnu.trove.set.TDoubleSet;
import java.util.Map;

public interface TDoubleCharMap {
    public double getNoEntryKey();

    public char getNoEntryValue();

    public char put(double var1, char var3);

    public char putIfAbsent(double var1, char var3);

    public void putAll(Map<? extends Double, ? extends Character> var1);

    public void putAll(TDoubleCharMap var1);

    public char get(double var1);

    public void clear();

    public boolean isEmpty();

    public char remove(double var1);

    public int size();

    public TDoubleSet keySet();

    public double[] keys();

    public double[] keys(double[] var1);

    public TCharCollection valueCollection();

    public char[] values();

    public char[] values(char[] var1);

    public boolean containsValue(char var1);

    public boolean containsKey(double var1);

    public TDoubleCharIterator iterator();

    public boolean forEachKey(TDoubleProcedure var1);

    public boolean forEachValue(TCharProcedure var1);

    public boolean forEachEntry(TDoubleCharProcedure var1);

    public void transformValues(TCharFunction var1);

    public boolean retainEntries(TDoubleCharProcedure var1);

    public boolean increment(double var1);

    public boolean adjustValue(double var1, char var3);

    public char adjustOrPutValue(double var1, char var3, char var4);
}

