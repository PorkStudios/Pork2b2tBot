/*
 * Decompiled with CFR 0_132.
 */
package gnu.trove.impl.unmodifiable;

import gnu.trove.TCollections;
import gnu.trove.TFloatCollection;
import gnu.trove.function.TFloatFunction;
import gnu.trove.iterator.TObjectFloatIterator;
import gnu.trove.map.TObjectFloatMap;
import gnu.trove.procedure.TFloatProcedure;
import gnu.trove.procedure.TObjectFloatProcedure;
import gnu.trove.procedure.TObjectProcedure;
import java.io.Serializable;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class TUnmodifiableObjectFloatMap<K>
implements TObjectFloatMap<K>,
Serializable {
    private static final long serialVersionUID = -1034234728574286014L;
    private final TObjectFloatMap<K> m;
    private transient Set<K> keySet = null;
    private transient TFloatCollection values = null;

    public TUnmodifiableObjectFloatMap(TObjectFloatMap<K> m) {
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
    public boolean containsValue(float val) {
        return this.m.containsValue(val);
    }

    @Override
    public float get(Object key) {
        return this.m.get(key);
    }

    @Override
    public float put(K key, float value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public float remove(Object key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(TObjectFloatMap<? extends K> m) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(Map<? extends K, ? extends Float> map) {
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
    public TFloatCollection valueCollection() {
        if (this.values == null) {
            this.values = TCollections.unmodifiableCollection(this.m.valueCollection());
        }
        return this.values;
    }

    @Override
    public float[] values() {
        return this.m.values();
    }

    @Override
    public float[] values(float[] array) {
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
    public float getNoEntryValue() {
        return this.m.getNoEntryValue();
    }

    @Override
    public boolean forEachKey(TObjectProcedure<? super K> procedure) {
        return this.m.forEachKey(procedure);
    }

    @Override
    public boolean forEachValue(TFloatProcedure procedure) {
        return this.m.forEachValue(procedure);
    }

    @Override
    public boolean forEachEntry(TObjectFloatProcedure<? super K> procedure) {
        return this.m.forEachEntry(procedure);
    }

    @Override
    public TObjectFloatIterator<K> iterator() {
        return new TObjectFloatIterator<K>(){
            TObjectFloatIterator<K> iter;
            {
                this.iter = TUnmodifiableObjectFloatMap.this.m.iterator();
            }

            @Override
            public K key() {
                return this.iter.key();
            }

            @Override
            public float value() {
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
            public float setValue(float val) {
                throw new UnsupportedOperationException();
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    @Override
    public float putIfAbsent(K key, float value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void transformValues(TFloatFunction function) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainEntries(TObjectFloatProcedure<? super K> procedure) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean increment(K key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean adjustValue(K key, float amount) {
        throw new UnsupportedOperationException();
    }

    @Override
    public float adjustOrPutValue(K key, float adjust_amount, float put_amount) {
        throw new UnsupportedOperationException();
    }

}

