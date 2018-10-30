/*
 * Decompiled with CFR 0_132.
 */
package gnu.trove.impl.unmodifiable;

import gnu.trove.TCollections;
import gnu.trove.function.TObjectFunction;
import gnu.trove.iterator.TByteObjectIterator;
import gnu.trove.map.TByteObjectMap;
import gnu.trove.procedure.TByteObjectProcedure;
import gnu.trove.procedure.TByteProcedure;
import gnu.trove.procedure.TObjectProcedure;
import gnu.trove.set.TByteSet;
import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class TUnmodifiableByteObjectMap<V>
implements TByteObjectMap<V>,
Serializable {
    private static final long serialVersionUID = -1034234728574286014L;
    private final TByteObjectMap<V> m;
    private transient TByteSet keySet = null;
    private transient Collection<V> values = null;

    public TUnmodifiableByteObjectMap(TByteObjectMap<V> m) {
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
    public boolean containsKey(byte key) {
        return this.m.containsKey(key);
    }

    @Override
    public boolean containsValue(Object val) {
        return this.m.containsValue(val);
    }

    @Override
    public V get(byte key) {
        return this.m.get(key);
    }

    @Override
    public V put(byte key, V value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public V remove(byte key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(TByteObjectMap<? extends V> m) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(Map<? extends Byte, ? extends V> map) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }

    @Override
    public TByteSet keySet() {
        if (this.keySet == null) {
            this.keySet = TCollections.unmodifiableSet(this.m.keySet());
        }
        return this.keySet;
    }

    @Override
    public byte[] keys() {
        return this.m.keys();
    }

    @Override
    public byte[] keys(byte[] array) {
        return this.m.keys(array);
    }

    @Override
    public Collection<V> valueCollection() {
        if (this.values == null) {
            this.values = Collections.unmodifiableCollection(this.m.valueCollection());
        }
        return this.values;
    }

    @Override
    public Object[] values() {
        return this.m.values();
    }

    @Override
    public V[] values(V[] array) {
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
    public byte getNoEntryKey() {
        return this.m.getNoEntryKey();
    }

    @Override
    public boolean forEachKey(TByteProcedure procedure) {
        return this.m.forEachKey(procedure);
    }

    @Override
    public boolean forEachValue(TObjectProcedure<? super V> procedure) {
        return this.m.forEachValue(procedure);
    }

    @Override
    public boolean forEachEntry(TByteObjectProcedure<? super V> procedure) {
        return this.m.forEachEntry(procedure);
    }

    @Override
    public TByteObjectIterator<V> iterator() {
        return new TByteObjectIterator<V>(){
            TByteObjectIterator<V> iter;
            {
                this.iter = TUnmodifiableByteObjectMap.this.m.iterator();
            }

            @Override
            public byte key() {
                return this.iter.key();
            }

            @Override
            public V value() {
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
            public V setValue(V val) {
                throw new UnsupportedOperationException();
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    @Override
    public V putIfAbsent(byte key, V value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void transformValues(TObjectFunction<V, V> function) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainEntries(TByteObjectProcedure<? super V> procedure) {
        throw new UnsupportedOperationException();
    }

}

