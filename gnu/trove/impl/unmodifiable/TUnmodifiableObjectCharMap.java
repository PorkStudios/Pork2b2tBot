/*
 * Decompiled with CFR 0_132.
 */
package gnu.trove.impl.unmodifiable;

import gnu.trove.TCharCollection;
import gnu.trove.TCollections;
import gnu.trove.function.TCharFunction;
import gnu.trove.iterator.TObjectCharIterator;
import gnu.trove.map.TObjectCharMap;
import gnu.trove.procedure.TCharProcedure;
import gnu.trove.procedure.TObjectCharProcedure;
import gnu.trove.procedure.TObjectProcedure;
import java.io.Serializable;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class TUnmodifiableObjectCharMap<K>
implements TObjectCharMap<K>,
Serializable {
    private static final long serialVersionUID = -1034234728574286014L;
    private final TObjectCharMap<K> m;
    private transient Set<K> keySet = null;
    private transient TCharCollection values = null;

    public TUnmodifiableObjectCharMap(TObjectCharMap<K> m) {
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
    public boolean containsValue(char val) {
        return this.m.containsValue(val);
    }

    @Override
    public char get(Object key) {
        return this.m.get(key);
    }

    @Override
    public char put(K key, char value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public char remove(Object key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(TObjectCharMap<? extends K> m) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(Map<? extends K, ? extends Character> map) {
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
    public TCharCollection valueCollection() {
        if (this.values == null) {
            this.values = TCollections.unmodifiableCollection(this.m.valueCollection());
        }
        return this.values;
    }

    @Override
    public char[] values() {
        return this.m.values();
    }

    @Override
    public char[] values(char[] array) {
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
    public char getNoEntryValue() {
        return this.m.getNoEntryValue();
    }

    @Override
    public boolean forEachKey(TObjectProcedure<? super K> procedure) {
        return this.m.forEachKey(procedure);
    }

    @Override
    public boolean forEachValue(TCharProcedure procedure) {
        return this.m.forEachValue(procedure);
    }

    @Override
    public boolean forEachEntry(TObjectCharProcedure<? super K> procedure) {
        return this.m.forEachEntry(procedure);
    }

    @Override
    public TObjectCharIterator<K> iterator() {
        return new TObjectCharIterator<K>(){
            TObjectCharIterator<K> iter;
            {
                this.iter = TUnmodifiableObjectCharMap.this.m.iterator();
            }

            @Override
            public K key() {
                return this.iter.key();
            }

            @Override
            public char value() {
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
            public char setValue(char val) {
                throw new UnsupportedOperationException();
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    @Override
    public char putIfAbsent(K key, char value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void transformValues(TCharFunction function) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainEntries(TObjectCharProcedure<? super K> procedure) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean increment(K key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean adjustValue(K key, char amount) {
        throw new UnsupportedOperationException();
    }

    @Override
    public char adjustOrPutValue(K key, char adjust_amount, char put_amount) {
        throw new UnsupportedOperationException();
    }

}

