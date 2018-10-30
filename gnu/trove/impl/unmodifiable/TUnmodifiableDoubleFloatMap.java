/*
 * Decompiled with CFR 0_132.
 */
package gnu.trove.impl.unmodifiable;

import gnu.trove.TCollections;
import gnu.trove.TFloatCollection;
import gnu.trove.function.TFloatFunction;
import gnu.trove.iterator.TDoubleFloatIterator;
import gnu.trove.map.TDoubleFloatMap;
import gnu.trove.procedure.TDoubleFloatProcedure;
import gnu.trove.procedure.TDoubleProcedure;
import gnu.trove.procedure.TFloatProcedure;
import gnu.trove.set.TDoubleSet;
import java.io.Serializable;
import java.util.Map;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class TUnmodifiableDoubleFloatMap
implements TDoubleFloatMap,
Serializable {
    private static final long serialVersionUID = -1034234728574286014L;
    private final TDoubleFloatMap m;
    private transient TDoubleSet keySet = null;
    private transient TFloatCollection values = null;

    public TUnmodifiableDoubleFloatMap(TDoubleFloatMap m) {
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
    public boolean containsKey(double key) {
        return this.m.containsKey(key);
    }

    @Override
    public boolean containsValue(float val) {
        return this.m.containsValue(val);
    }

    @Override
    public float get(double key) {
        return this.m.get(key);
    }

    @Override
    public float put(double key, float value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public float remove(double key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(TDoubleFloatMap m) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(Map<? extends Double, ? extends Float> map) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }

    @Override
    public TDoubleSet keySet() {
        if (this.keySet == null) {
            this.keySet = TCollections.unmodifiableSet(this.m.keySet());
        }
        return this.keySet;
    }

    @Override
    public double[] keys() {
        return this.m.keys();
    }

    @Override
    public double[] keys(double[] array) {
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
    public double getNoEntryKey() {
        return this.m.getNoEntryKey();
    }

    @Override
    public float getNoEntryValue() {
        return this.m.getNoEntryValue();
    }

    @Override
    public boolean forEachKey(TDoubleProcedure procedure) {
        return this.m.forEachKey(procedure);
    }

    @Override
    public boolean forEachValue(TFloatProcedure procedure) {
        return this.m.forEachValue(procedure);
    }

    @Override
    public boolean forEachEntry(TDoubleFloatProcedure procedure) {
        return this.m.forEachEntry(procedure);
    }

    @Override
    public TDoubleFloatIterator iterator() {
        return new TDoubleFloatIterator(){
            TDoubleFloatIterator iter;
            {
                this.iter = TUnmodifiableDoubleFloatMap.this.m.iterator();
            }

            public double key() {
                return this.iter.key();
            }

            public float value() {
                return this.iter.value();
            }

            public void advance() {
                this.iter.advance();
            }

            public boolean hasNext() {
                return this.iter.hasNext();
            }

            public float setValue(float val) {
                throw new UnsupportedOperationException();
            }

            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    @Override
    public float putIfAbsent(double key, float value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void transformValues(TFloatFunction function) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainEntries(TDoubleFloatProcedure procedure) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean increment(double key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean adjustValue(double key, float amount) {
        throw new UnsupportedOperationException();
    }

    @Override
    public float adjustOrPutValue(double key, float adjust_amount, float put_amount) {
        throw new UnsupportedOperationException();
    }

}

