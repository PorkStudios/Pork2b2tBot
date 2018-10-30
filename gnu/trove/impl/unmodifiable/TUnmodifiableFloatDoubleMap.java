/*
 * Decompiled with CFR 0_132.
 */
package gnu.trove.impl.unmodifiable;

import gnu.trove.TCollections;
import gnu.trove.TDoubleCollection;
import gnu.trove.function.TDoubleFunction;
import gnu.trove.iterator.TFloatDoubleIterator;
import gnu.trove.map.TFloatDoubleMap;
import gnu.trove.procedure.TDoubleProcedure;
import gnu.trove.procedure.TFloatDoubleProcedure;
import gnu.trove.procedure.TFloatProcedure;
import gnu.trove.set.TFloatSet;
import java.io.Serializable;
import java.util.Map;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class TUnmodifiableFloatDoubleMap
implements TFloatDoubleMap,
Serializable {
    private static final long serialVersionUID = -1034234728574286014L;
    private final TFloatDoubleMap m;
    private transient TFloatSet keySet = null;
    private transient TDoubleCollection values = null;

    public TUnmodifiableFloatDoubleMap(TFloatDoubleMap m) {
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
    public boolean containsKey(float key) {
        return this.m.containsKey(key);
    }

    @Override
    public boolean containsValue(double val) {
        return this.m.containsValue(val);
    }

    @Override
    public double get(float key) {
        return this.m.get(key);
    }

    @Override
    public double put(float key, double value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public double remove(float key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(TFloatDoubleMap m) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(Map<? extends Float, ? extends Double> map) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }

    @Override
    public TFloatSet keySet() {
        if (this.keySet == null) {
            this.keySet = TCollections.unmodifiableSet(this.m.keySet());
        }
        return this.keySet;
    }

    @Override
    public float[] keys() {
        return this.m.keys();
    }

    @Override
    public float[] keys(float[] array) {
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
    public float getNoEntryKey() {
        return this.m.getNoEntryKey();
    }

    @Override
    public double getNoEntryValue() {
        return this.m.getNoEntryValue();
    }

    @Override
    public boolean forEachKey(TFloatProcedure procedure) {
        return this.m.forEachKey(procedure);
    }

    @Override
    public boolean forEachValue(TDoubleProcedure procedure) {
        return this.m.forEachValue(procedure);
    }

    @Override
    public boolean forEachEntry(TFloatDoubleProcedure procedure) {
        return this.m.forEachEntry(procedure);
    }

    @Override
    public TFloatDoubleIterator iterator() {
        return new TFloatDoubleIterator(){
            TFloatDoubleIterator iter;
            {
                this.iter = TUnmodifiableFloatDoubleMap.this.m.iterator();
            }

            public float key() {
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
    public double putIfAbsent(float key, double value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void transformValues(TDoubleFunction function) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainEntries(TFloatDoubleProcedure procedure) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean increment(float key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean adjustValue(float key, double amount) {
        throw new UnsupportedOperationException();
    }

    @Override
    public double adjustOrPutValue(float key, double adjust_amount, double put_amount) {
        throw new UnsupportedOperationException();
    }

}

