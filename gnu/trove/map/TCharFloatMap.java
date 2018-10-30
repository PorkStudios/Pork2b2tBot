/*
 * Decompiled with CFR 0_132.
 */
package gnu.trove.map;

import gnu.trove.TFloatCollection;
import gnu.trove.function.TFloatFunction;
import gnu.trove.iterator.TCharFloatIterator;
import gnu.trove.procedure.TCharFloatProcedure;
import gnu.trove.procedure.TCharProcedure;
import gnu.trove.procedure.TFloatProcedure;
import gnu.trove.set.TCharSet;
import java.util.Map;

public interface TCharFloatMap {
    public char getNoEntryKey();

    public float getNoEntryValue();

    public float put(char var1, float var2);

    public float putIfAbsent(char var1, float var2);

    public void putAll(Map<? extends Character, ? extends Float> var1);

    public void putAll(TCharFloatMap var1);

    public float get(char var1);

    public void clear();

    public boolean isEmpty();

    public float remove(char var1);

    public int size();

    public TCharSet keySet();

    public char[] keys();

    public char[] keys(char[] var1);

    public TFloatCollection valueCollection();

    public float[] values();

    public float[] values(float[] var1);

    public boolean containsValue(float var1);

    public boolean containsKey(char var1);

    public TCharFloatIterator iterator();

    public boolean forEachKey(TCharProcedure var1);

    public boolean forEachValue(TFloatProcedure var1);

    public boolean forEachEntry(TCharFloatProcedure var1);

    public void transformValues(TFloatFunction var1);

    public boolean retainEntries(TCharFloatProcedure var1);

    public boolean increment(char var1);

    public boolean adjustValue(char var1, float var2);

    public float adjustOrPutValue(char var1, float var2, float var3);
}

