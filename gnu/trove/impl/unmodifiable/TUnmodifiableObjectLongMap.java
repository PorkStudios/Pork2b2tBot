/*
 * Decompiled with CFR 0_132.
 */
package gnu.trove.impl.unmodifiable;

import gnu.trove.TCollections;
import gnu.trove.TLongCollection;
import gnu.trove.function.TLongFunction;
import gnu.trove.iterator.TObjectLongIterator;
import gnu.trove.map.TObjectLongMap;
import gnu.trove.procedure.TLongProcedure;
import gnu.trove.procedure.TObjectLongProcedure;
import gnu.trove.procedure.TObjectProcedure;
import java.io.Serializable;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class TUnmodifiableObjectLongMap<K>
implements TObjectLongMap<K>,
Serializable {
    private static final long serialVersionUID = -1034234728574286014L;
    private final TObjectLongMap<K> m;
    private transient Set<K> keySet = null;
    private transient TLongCollection values = null;

    public TUnmodifiableObjectLongMap(TObjectLongMap<K> m) {
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
    public boolean containsValue(long val) {
        return this.m.containsValue(val);
    }

    @Override
    public long get(Object key) {
        return this.m.get(key);
    }

    @Override
    public long put(K key, long value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public long remove(Object key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(TObjectLongMap<? extends K> m) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(Map<? extends K, ? extends Long> map) {
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
    public TLongCollection valueCollection() {
        if (this.values == null) {
            this.values = TCollections.unmodifiableCollection(this.m.valueCollection());
        }
        return this.values;
    }

    @Override
    public long[] values() {
        return this.m.values();
    }

    @Override
    public long[] values(long[] array) {
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
    public long getNoEntryValue() {
        return this.m.getNoEntryValue();
    }

    @Override
    public boolean forEachKey(TObjectProcedure<? super K> procedure) {
        return this.m.forEachKey(procedure);
    }

    @Override
    public boolean forEachValue(TLongProcedure procedure) {
        return this.m.forEachValue(procedure);
    }

    @Override
    public boolean forEachEntry(TObjectLongProcedure<? super K> procedure) {
        return this.m.forEachEntry(procedure);
    }

    @Override
    public TObjectLongIterator<K> iterator() {
        return new TObjectLongIterator<K>(){
            TObjectLongIterator<K> iter;
            {
                this.iter = TUnmodifiableObjectLongMap.this.m.iterator();
            }

            @Override
            public K key() {
                return this.iter.key();
            }

            @Override
            public long value() {
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
            public long setValue(long val) {
                throw new UnsupportedOperationException();
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    @Override
    public long putIfAbsent(K key, long value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void transformValues(TLongFunction function) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainEntries(TObjectLongProcedure<? super K> procedure) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean increment(K key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean adjustValue(K key, long amount) {
        throw new UnsupportedOperationException();
    }

    @Override
    public long adjustOrPutValue(K key, long adjust_amount, long put_amount) {
        throw new UnsupportedOperationException();
    }

}

