/*
 * Decompiled with CFR 0_132.
 */
package gnu.trove.map;

import gnu.trove.TCharCollection;
import gnu.trove.function.TCharFunction;
import gnu.trove.iterator.TObjectCharIterator;
import gnu.trove.procedure.TCharProcedure;
import gnu.trove.procedure.TObjectCharProcedure;
import gnu.trove.procedure.TObjectProcedure;
import java.util.Map;
import java.util.Set;

public interface TObjectCharMap<K> {
    public char getNoEntryValue();

    public int size();

    public boolean isEmpty();

    public boolean containsKey(Object var1);

    public boolean containsValue(char var1);

    public char get(Object var1);

    public char put(K var1, char var2);

    public char putIfAbsent(K var1, char var2);

    public char remove(Object var1);

    public void putAll(Map<? extends K, ? extends Character> var1);

    public void putAll(TObjectCharMap<? extends K> var1);

    public void clear();

    public Set<K> keySet();

    public Object[] keys();

    public K[] keys(K[] var1);

    public TCharCollection valueCollection();

    public char[] values();

    public char[] values(char[] var1);

    public TObjectCharIterator<K> iterator();

    public boolean increment(K var1);

    public boolean adjustValue(K var1, char var2);

    public char adjustOrPutValue(K var1, char var2, char var3);

    public boolean forEachKey(TObjectProcedure<? super K> var1);

    public boolean forEachValue(TCharProcedure var1);

    public boolean forEachEntry(TObjectCharProcedure<? super K> var1);

    public void transformValues(TCharFunction var1);

    public boolean retainEntries(TObjectCharProcedure<? super K> var1);

    public boolean equals(Object var1);

    public int hashCode();
}

