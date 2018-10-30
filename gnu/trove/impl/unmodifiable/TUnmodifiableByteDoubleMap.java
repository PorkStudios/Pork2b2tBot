/*
 * Decompiled with CFR 0_132.
 */
package gnu.trove.impl.unmodifiable;

import gnu.trove.TCollections;
import gnu.trove.TDoubleCollection;
import gnu.trove.function.TDoubleFunction;
import gnu.trove.iterator.TByteDoubleIterator;
import gnu.trove.map.TByteDoubleMap;
import gnu.trove.procedure.TByteDoubleProcedure;
import gnu.trove.procedure.TByteProcedure;
import gnu.trove.procedure.TDoubleProcedure;
import gnu.trove.set.TByteSet;
import java.io.Serializable;
import java.util.Map;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class TUnmodifiableByteDoubleMap
implements TByteDoubleMap,
Serializable {
    private static final long serialVersionUID = -1034234728574286014L;
    private final TByteDoubleMap m;
    private transient TByteSet keySet = null;
    private transient TDoubleCollection values = null;

    public TUnmodifiableByteDoubleMap(TByteDoubleMap m) {
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
    public boolean containsValue(double val) {
        return this.m.containsValue(val);
    }

    @Override
    public double get(byte key) {
        return this.m.get(key);
    }

    @Override
    public double put(byte key, double value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public double remove(byte key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(TByteDoubleMap m) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(Map<? extends Byte, ? extends Double> map) {
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
    public byte getNoEntryKey() {
        return this.m.getNoEntryKey();
    }

    @Override
    public double getNoEntryValue() {
        return this.m.getNoEntryValue();
    }

    @Override
    public boolean forEachKey(TByteProcedure procedure) {
        return this.m.forEachKey(procedure);
    }

    @Override
    public boolean forEachValue(TDoubleProcedure procedure) {
        return this.m.forEachValue(procedure);
    }

    @Override
    public boolean forEachEntry(TByteDoubleProcedure procedure) {
        return this.m.forEachEntry(procedure);
    }

    @Override
    public TByteDoubleIterator iterator() {
        return new TByteDoubleIterator(){
            TByteDoubleIterator iter;
            {
                this.iter = TUnmodifiableByteDoubleMap.this.m.iterator();
            }

            public byte key() {
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
    public double putIfAbsent(byte key, double value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void transformValues(TDoubleFunction function) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainEntries(TByteDoubleProcedure procedure) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean increment(byte key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean adjustValue(byte key, double amount) {
        throw new UnsupportedOperationException();
    }

    @Override
    public double adjustOrPutValue(byte key, double adjust_amount, double put_amount) {
        throw new UnsupportedOperationException();
    }

}

