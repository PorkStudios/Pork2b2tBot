/*
 * Decompiled with CFR 0_132.
 */
package gnu.trove.impl.unmodifiable;

import gnu.trove.TCollections;
import gnu.trove.TDoubleCollection;
import gnu.trove.function.TDoubleFunction;
import gnu.trove.iterator.TCharDoubleIterator;
import gnu.trove.map.TCharDoubleMap;
import gnu.trove.procedure.TCharDoubleProcedure;
import gnu.trove.procedure.TCharProcedure;
import gnu.trove.procedure.TDoubleProcedure;
import gnu.trove.set.TCharSet;
import java.io.Serializable;
import java.util.Map;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class TUnmodifiableCharDoubleMap
implements TCharDoubleMap,
Serializable {
    private static final long serialVersionUID = -1034234728574286014L;
    private final TCharDoubleMap m;
    private transient TCharSet keySet = null;
    private transient TDoubleCollection values = null;

    public TUnmodifiableCharDoubleMap(TCharDoubleMap m) {
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
    public boolean containsKey(char key) {
        return this.m.containsKey(key);
    }

    @Override
    public boolean containsValue(double val) {
        return this.m.containsValue(val);
    }

    @Override
    public double get(char key) {
        return this.m.get(key);
    }

    @Override
    public double put(char key, double value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public double remove(char key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(TCharDoubleMap m) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(Map<? extends Character, ? extends Double> map) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }

    @Override
    public TCharSet keySet() {
        if (this.keySet == null) {
            this.keySet = TCollections.unmodifiableSet(this.m.keySet());
        }
        return this.keySet;
    }

    @Override
    public char[] keys() {
        return this.m.keys();
    }

    @Override
    public char[] keys(char[] array) {
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
    public char getNoEntryKey() {
        return this.m.getNoEntryKey();
    }

    @Override
    public double getNoEntryValue() {
        return this.m.getNoEntryValue();
    }

    @Override
    public boolean forEachKey(TCharProcedure procedure) {
        return this.m.forEachKey(procedure);
    }

    @Override
    public boolean forEachValue(TDoubleProcedure procedure) {
        return this.m.forEachValue(procedure);
    }

    @Override
    public boolean forEachEntry(TCharDoubleProcedure procedure) {
        return this.m.forEachEntry(procedure);
    }

    @Override
    public TCharDoubleIterator iterator() {
        return new TCharDoubleIterator(){
            TCharDoubleIterator iter;
            {
                this.iter = TUnmodifiableCharDoubleMap.this.m.iterator();
            }

            public char key() {
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
    public double putIfAbsent(char key, double value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void transformValues(TDoubleFunction function) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainEntries(TCharDoubleProcedure procedure) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean increment(char key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean adjustValue(char key, double amount) {
        throw new UnsupportedOperationException();
    }

    @Override
    public double adjustOrPutValue(char key, double adjust_amount, double put_amount) {
        throw new UnsupportedOperationException();
    }

}

