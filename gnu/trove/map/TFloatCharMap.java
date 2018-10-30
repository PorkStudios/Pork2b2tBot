/*
 * Decompiled with CFR 0_132.
 */
package gnu.trove.map;

import gnu.trove.TCharCollection;
import gnu.trove.function.TCharFunction;
import gnu.trove.iterator.TFloatCharIterator;
import gnu.trove.procedure.TCharProcedure;
import gnu.trove.procedure.TFloatCharProcedure;
import gnu.trove.procedure.TFloatProcedure;
import gnu.trove.set.TFloatSet;
import java.util.Map;

public interface TFloatCharMap {
    public float getNoEntryKey();

    public char getNoEntryValue();

    public char put(float var1, char var2);

    public char putIfAbsent(float var1, char var2);

    public void putAll(Map<? extends Float, ? extends Character> var1);

    public void putAll(TFloatCharMap var1);

    public char get(float var1);

    public void clear();

    public boolean isEmpty();

    public char remove(float var1);

    public int size();

    public TFloatSet keySet();

    public float[] keys();

    public float[] keys(float[] var1);

    public TCharCollection valueCollection();

    public char[] values();

    public char[] values(char[] var1);

    public boolean containsValue(char var1);

    public boolean containsKey(float var1);

    public TFloatCharIterator iterator();

    public boolean forEachKey(TFloatProcedure var1);

    public boolean forEachValue(TCharProcedure var1);

    public boolean forEachEntry(TFloatCharProcedure var1);

    public void transformValues(TCharFunction var1);

    public boolean retainEntries(TFloatCharProcedure var1);

    public boolean increment(float var1);

    public boolean adjustValue(float var1, char var2);

    public char adjustOrPutValue(float var1, char var2, char var3);
}

