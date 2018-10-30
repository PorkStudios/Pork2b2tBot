/*
 * Decompiled with CFR 0_132.
 */
package gnu.trove.map;

import gnu.trove.TByteCollection;
import gnu.trove.function.TByteFunction;
import gnu.trove.iterator.TCharByteIterator;
import gnu.trove.procedure.TByteProcedure;
import gnu.trove.procedure.TCharByteProcedure;
import gnu.trove.procedure.TCharProcedure;
import gnu.trove.set.TCharSet;
import java.util.Map;

public interface TCharByteMap {
    public char getNoEntryKey();

    public byte getNoEntryValue();

    public byte put(char var1, byte var2);

    public byte putIfAbsent(char var1, byte var2);

    public void putAll(Map<? extends Character, ? extends Byte> var1);

    public void putAll(TCharByteMap var1);

    public byte get(char var1);

    public void clear();

    public boolean isEmpty();

    public byte remove(char var1);

    public int size();

    public TCharSet keySet();

    public char[] keys();

    public char[] keys(char[] var1);

    public TByteCollection valueCollection();

    public byte[] values();

    public byte[] values(byte[] var1);

    public boolean containsValue(byte var1);

    public boolean containsKey(char var1);

    public TCharByteIterator iterator();

    public boolean forEachKey(TCharProcedure var1);

    public boolean forEachValue(TByteProcedure var1);

    public boolean forEachEntry(TCharByteProcedure var1);

    public void transformValues(TByteFunction var1);

    public boolean retainEntries(TCharByteProcedure var1);

    public boolean increment(char var1);

    public boolean adjustValue(char var1, byte var2);

    public byte adjustOrPutValue(char var1, byte var2, byte var3);
}

