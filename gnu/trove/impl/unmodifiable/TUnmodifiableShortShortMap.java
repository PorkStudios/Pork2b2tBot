/*
 * Decompiled with CFR 0_132.
 */
package gnu.trove.impl.unmodifiable;

import gnu.trove.TCollections;
import gnu.trove.TShortCollection;
import gnu.trove.function.TShortFunction;
import gnu.trove.iterator.TShortShortIterator;
import gnu.trove.map.TShortShortMap;
import gnu.trove.procedure.TShortProcedure;
import gnu.trove.procedure.TShortShortProcedure;
import gnu.trove.set.TShortSet;
import java.io.Serializable;
import java.util.Map;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class TUnmodifiableShortShortMap
implements TShortShortMap,
Serializable {
    private static final long serialVersionUID = -1034234728574286014L;
    private final TShortShortMap m;
    private transient TShortSet keySet = null;
    private transient TShortCollection values = null;

    public TUnmodifiableShortShortMap(TShortShortMap m) {
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
    public boolean containsKey(short key) {
        return this.m.containsKey(key);
    }

    @Override
    public boolean containsValue(short val) {
        return this.m.containsValue(val);
    }

    @Override
    public short get(short key) {
        return this.m.get(key);
    }

    @Override
    public short put(short key, short value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public short remove(short key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(TShortShortMap m) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(Map<? extends Short, ? extends Short> map) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }

    @Override
    public TShortSet keySet() {
        if (this.keySet == null) {
            this.keySet = TCollections.unmodifiableSet(this.m.keySet());
        }
        return this.keySet;
    }

    @Override
    public short[] keys() {
        return this.m.keys();
    }

    @Override
    public short[] keys(short[] array) {
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

    public boolean equals(Object o) {
        return o == this || this.m.equals(o);
    }

    public int hashCode() {
        return this.m.hashCode();
    }

    public String toString() {
        return this.m.toString();
    }

    @Override
    public short getNoEntryKey() {
        return this.m.getNoEntryKey();
    }

    @Override
    public short getNoEntryValue() {
        return this.m.getNoEntryValue();
    }

    @Override
    public boolean forEachKey(TShortProcedure procedure) {
        return this.m.forEachKey(procedure);
    }

    @Override
    public boolean forEachValue(TShortProcedure procedure) {
        return this.m.forEachValue(procedure);
    }

    @Override
    public boolean forEachEntry(TShortShortProcedure procedure) {
        return this.m.forEachEntry(procedure);
    }

    @Override
    public TShortShortIterator iterator() {
        return new TShortShortIterator(){
            TShortShortIterator iter;
            {
                this.iter = TUnmodifiableShortShortMap.this.m.iterator();
            }

            public short key() {
                return this.iter.key();
            }

            public short value() {
                return this.iter.value();
            }

            public void advance() {
                this.iter.advance();
            }

            public boolean hasNext() {
                return this.iter.hasNext();
            }

            public short setValue(short val) {
                throw new UnsupportedOperationException();
            }

            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    @Override
    public short putIfAbsent(short key, short value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void transformValues(TShortFunction function) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainEntries(TShortShortProcedure procedure) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean increment(short key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean adjustValue(short key, short amount) {
        throw new UnsupportedOperationException();
    }

    @Override
    public short adjustOrPutValue(short key, short adjust_amount, short put_amount) {
        throw new UnsupportedOperationException();
    }

}

