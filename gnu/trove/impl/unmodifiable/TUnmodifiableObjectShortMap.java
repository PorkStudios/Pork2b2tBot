/*
 * Decompiled with CFR 0_132.
 */
package gnu.trove.impl.unmodifiable;

import gnu.trove.TCollections;
import gnu.trove.TShortCollection;
import gnu.trove.function.TShortFunction;
import gnu.trove.iterator.TObjectShortIterator;
import gnu.trove.map.TObjectShortMap;
import gnu.trove.procedure.TObjectProcedure;
import gnu.trove.procedure.TObjectShortProcedure;
import gnu.trove.procedure.TShortProcedure;
import java.io.Serializable;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class TUnmodifiableObjectShortMap<K>
implements TObjectShortMap<K>,
Serializable {
    private static final long serialVersionUID = -1034234728574286014L;
    private final TObjectShortMap<K> m;
    private transient Set<K> keySet = null;
    private transient TShortCollection values = null;

    public TUnmodifiableObjectShortMap(TObjectShortMap<K> m) {
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
    public boolean containsValue(short val) {
        return this.m.containsValue(val);
    }

    @Override
    public short get(Object key) {
        return this.m.get(key);
    }

    @Override
    public short put(K key, short value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public short remove(Object key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(TObjectShortMap<? extends K> m) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(Map<? extends K, ? extends Short> map) {
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
    public TShortCollection valueCollection() {
        if (this.values == null) {
            this.values = TCollections.unmodifiableCollection(this.m.valueCollection());
        }
        return this.values;
    }

    @Override
    public short[] values() {
        return this.m.values();
    }

    @Override
    public short[] values(short[] array) {
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
    public short getNoEntryValue() {
        return this.m.getNoEntryValue();
    }

    @Override
    public boolean forEachKey(TObjectProcedure<? super K> procedure) {
        return this.m.forEachKey(procedure);
    }

    @Override
    public boolean forEachValue(TShortProcedure procedure) {
        return this.m.forEachValue(procedure);
    }

    @Override
    public boolean forEachEntry(TObjectShortProcedure<? super K> procedure) {
        return this.m.forEachEntry(procedure);
    }

    @Override
    public TObjectShortIterator<K> iterator() {
        return new TObjectShortIterator<K>(){
            TObjectShortIterator<K> iter;
            {
                this.iter = TUnmodifiableObjectShortMap.this.m.iterator();
            }

            @Override
            public K key() {
                return this.iter.key();
            }

            @Override
            public short value() {
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
            public short setValue(short val) {
                throw new UnsupportedOperationException();
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    @Override
    public short putIfAbsent(K key, short value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void transformValues(TShortFunction function) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainEntries(TObjectShortProcedure<? super K> procedure) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean increment(K key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean adjustValue(K key, short amount) {
        throw new UnsupportedOperationException();
    }

    @Override
    public short adjustOrPutValue(K key, short adjust_amount, short put_amount) {
        throw new UnsupportedOperationException();
    }

}

