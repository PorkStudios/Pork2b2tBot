/*
 * Decompiled with CFR 0_132.
 */
package gnu.trove.map;

import gnu.trove.TByteCollection;
import gnu.trove.function.TByteFunction;
import gnu.trove.iterator.TObjectByteIterator;
import gnu.trove.procedure.TByteProcedure;
import gnu.trove.procedure.TObjectByteProcedure;
import gnu.trove.procedure.TObjectProcedure;
import java.util.Map;
import java.util.Set;

public interface TObjectByteMap<K> {
    public byte getNoEntryValue();

    public int size();

    public boolean isEmpty();

    public boolean containsKey(Object var1);

    public boolean containsValue(byte var1);

    public byte get(Object var1);

    public byte put(K var1, byte var2);

    public byte putIfAbsent(K var1, byte var2);

    public byte remove(Object var1);

    public void putAll(Map<? extends K, ? extends Byte> var1);

    public void putAll(TObjectByteMap<? extends K> var1);

    public void clear();

    public Set<K> keySet();

    public Object[] keys();

    public K[] keys(K[] var1);

    public TByteCollection valueCollection();

    public byte[] values();

    public byte[] values(byte[] var1);

    public TObjectByteIterator<K> iterator();

    public boolean increment(K var1);

    public boolean adjustValue(K var1, byte var2);

    public byte adjustOrPutValue(K var1, byte var2, byte var3);

    public boolean forEachKey(TObjectProcedure<? super K> var1);

    public boolean forEachValue(TByteProcedure var1);

    public boolean forEachEntry(TObjectByteProcedure<? super K> var1);

    public void transformValues(TByteFunction var1);

    public boolean retainEntries(TObjectByteProcedure<? super K> var1);

    public boolean equals(Object var1);

    public int hashCode();
}

