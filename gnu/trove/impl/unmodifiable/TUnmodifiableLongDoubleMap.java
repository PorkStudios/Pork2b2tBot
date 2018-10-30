/*
 * Decompiled with CFR 0_132.
 */
package gnu.trove.impl.unmodifiable;

import gnu.trove.TCollections;
import gnu.trove.TDoubleCollection;
import gnu.trove.function.TDoubleFunction;
import gnu.trove.iterator.TLongDoubleIterator;
import gnu.trove.map.TLongDoubleMap;
import gnu.trove.procedure.TDoubleProcedure;
import gnu.trove.procedure.TLongDoubleProcedure;
import gnu.trove.procedure.TLongProcedure;
import gnu.trove.set.TLongSet;
import java.io.Serializable;
import java.util.Map;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class TUnmodifiableLongDoubleMap
implements TLongDoubleMap,
Serializable {
    private static final long serialVersionUID = -1034234728574286014L;
    private final TLongDoubleMap m;
    private transient TLongSet keySet = null;
    private transient TDoubleCollection values = null;

    public TUnmodifiableLongDoubleMap(TLongDoubleMap m) {
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
    public boolean containsKey(long key) {
        return this.m.containsKey(key);
    }

    @Override
    public boolean containsValue(double val) {
        return this.m.containsValue(val);
    }

    @Override
    public double get(long key) {
        return this.m.get(key);
    }

    @Override
    public double put(long key, double value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public double remove(long key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(TLongDoubleMap m) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(Map<? extends Long, ? extends Double> map) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }

    @Override
    public TLongSet keySet() {
        if (this.keySet == null) {
            this.keySet = TCollections.unmodifiableSet(this.m.keySet());
        }
        return this.keySet;
    }

    @Override
    public long[] keys() {
        return this.m.keys();
    }

    @Override
    public long[] keys(long[] array) {
        return this.m.keys(array);
    }

    @Override
    public TDoubleCollection valueCollection() {
        if (this.values == null) {
            this.values = TCollections.unmodifiableCollection(this.m.valueCollection());
        }
        return this.values;
    }

    @Override
    public double[] values() {
        return this.m.values();
    }

    @Override
    public double[] values(double[] array) {
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
    public long getNoEntryKey() {
        return this.m.getNoEntryKey();
    }

    @Override
    public double getNoEntryValue() {
        return this.m.getNoEntryValue();
    }

    @Override
    public boolean forEachKey(TLongProcedure procedure) {
        return this.m.forEachKey(procedure);
    }

    @Override
    public boolean forEachValue(TDoubleProcedure procedure) {
        return this.m.forEachValue(procedure);
    }

    @Override
    public boolean forEachEntry(TLongDoubleProcedure procedure) {
        return this.m.forEachEntry(procedure);
    }

    @Override
    public TLongDoubleIterator iterator() {
        return new TLongDoubleIterator(){
            TLongDoubleIterator iter;
            {
                this.iter = TUnmodifiableLongDoubleMap.this.m.iterator();
            }

            public long key() {
                return this.iter.key();
            }

            public double value() {
                return this.iter.value();
            }

            public void advance() {
                this.iter.advance();
            }

            public boolean hasNext() {
                return this.iter.hasNext();
            }

            public double setValue(double val) {
                throw new UnsupportedOperationException();
            }

            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    @Override
    public double putIfAbsent(long key, double value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void transformValues(TDoubleFunction function) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainEntries(TLongDoubleProcedure procedure) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean increment(long key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean adjustValue(long key, double amount) {
        throw new UnsupportedOperationException();
    }

    @Override
    public double adjustOrPutValue(long key, double adjust_amount, double put_amount) {
        throw new UnsupportedOperationException();
    }

}

