/*
 * Decompiled with CFR 0_132.
 */
package gnu.trove.impl.unmodifiable;

import gnu.trove.TCollections;
import gnu.trove.TIntCollection;
import gnu.trove.function.TIntFunction;
import gnu.trove.iterator.TObjectIntIterator;
import gnu.trove.map.TObjectIntMap;
import gnu.trove.procedure.TIntProcedure;
import gnu.trove.procedure.TObjectIntProcedure;
import gnu.trove.procedure.TObjectProcedure;
import java.io.Serializable;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class TUnmodifiableObjectIntMap<K>
implements TObjectIntMap<K>,
Serializable {
    private static final long serialVersionUID = -1034234728574286014L;
    private final TObjectIntMap<K> m;
    private transient Set<K> keySet = null;
    private transient TIntCollection values = null;

    public TUnmodifiableObjectIntMap(TObjectIntMap<K> m) {
        if (m == null) {
            throw new NullPointerException();
        }
        this.m = m;
    }

    @Override
    public int size() {
        return this.m.size();
    }

    @Override
    public boolean isEmpty() {
        return this.m.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return this.m.containsKey(key);
    }

    @Override
    public boolean containsValue(int val) {
        return this.m.containsValue(val);
    }

    @Override
    public int get(Object key) {
        return this.m.get(key);
    }

    @Override
    public int put(K key, int value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int remove(Object key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(TObjectIntMap<? extends K> m) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(Map<? extends K, ? extends Integer> map) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Set<K> keySet() {
        if (this.keySet == null) {
            this.keySet = Collections.unmodifiableSet(this.m.keySet());
        }
        return this.keySet;
    }

    @Override
    public Object[] keys() {
        return this.m.keys();
    }

    @Override
    public K[] keys(K[] array) {
        return this.m.keys(array);
    }

    @Override
    public TIntCollection valueCollection() {
        if (this.values == null) {
            this.values = TCollections.unmodifiableCollection(this.m.valueCollection());
        }
        return this.values;
    }

    @Override
    public int[] values() {
        return this.m.values();
    }

    @Override
    public int[] values(int[] array) {
        return this.m.values(array);
    }

    @Override
    public boolean equals(Object o) {
        return o == this || this.m.equals(o);
    }

    @Override
    public int hashCode() {
        return this.m.hashCode();
    }

    public String toString() {
        return this.m.toString();
    }

    @Override
    public int getNoEntryValue() {
        return this.m.getNoEntryValue();
    }

    @Override
    public boolean forEachKey(TObjectProcedure<? super K> procedure) {
        return this.m.forEachKey(procedure);
    }

    @Override
    public boolean forEachValue(TIntProcedure procedure) {
        return this.m.forEachValue(procedure);
    }

    @Override
    public boolean forEachEntry(TObjectIntProcedure<? super K> procedure) {
        return this.m.forEachEntry(procedure);
    }

    @Override
    public TObjectIntIterator<K> iterator() {
        return new TObjectIntIterator<K>(){
            TObjectIntIterator<K> iter;
            {
                this.iter = TUnmodifiableObjectIntMap.this.m.iterator();
            }

            @Override
            public K key() {
                return this.iter.key();
            }

            @Override
            public int value() {
                return this.iter.value();
            }

            @Override
            public void advance() {
                this.iter.advance();
            }

            @Override
            public boolean hasNext() {
                return this.iter.hasNext();
            }

            @Override
            public int setValue(int val) {
                throw new UnsupportedOperationException();
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    @Override
    public int putIfAbsent(K key, int value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void transformValues(TIntFunction function) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainEntries(TObjectIntProcedure<? super K> procedure) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean increment(K key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean adjustValue(K key, int amount) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int adjustOrPutValue(K key, int adjust_amount, int put_amount) {
        throw new UnsupportedOperationException();
    }

}

