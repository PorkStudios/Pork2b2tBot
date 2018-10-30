/*
 * Decompiled with CFR 0_132.
 */
package gnu.trove.impl.unmodifiable;

import gnu.trove.TCollections;
import gnu.trove.TFloatCollection;
import gnu.trove.function.TFloatFunction;
import gnu.trove.iterator.TCharFloatIterator;
import gnu.trove.map.TCharFloatMap;
import gnu.trove.procedure.TCharFloatProcedure;
import gnu.trove.procedure.TCharProcedure;
import gnu.trove.procedure.TFloatProcedure;
import gnu.trove.set.TCharSet;
import java.io.Serializable;
import java.util.Map;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class TUnmodifiableCharFloatMap
implements TCharFloatMap,
Serializable {
    private static final long serialVersionUID = -1034234728574286014L;
    private final TCharFloatMap m;
    private transient TCharSet keySet = null;
    private transient TFloatCollection values = null;

    public TUnmodifiableCharFloatMap(TCharFloatMap m) {
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
    public boolean containsValue(float val) {
        return this.m.containsValue(val);
    }

    @Override
    public float get(char key) {
        return this.m.get(key);
    }

    @Override
    public float put(char key, float value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public float remove(char key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(TCharFloatMap m) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(Map<? extends Character, ? extends Float> map) {
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
    public char getNoEntryKey() {
        return this.m.getNoEntryKey();
    }

    @Override
    public float getNoEntryValue() {
        return this.m.getNoEntryValue();
    }

    @Override
    public boolean forEachKey(TCharProcedure procedure) {
        return this.m.forEachKey(procedure);
    }

    @Override
    public boolean forEachValue(TFloatProcedure procedure) {
        return this.m.forEachValue(procedure);
    }

    @Override
    public boolean forEachEntry(TCharFloatProcedure procedure) {
        return this.m.forEachEntry(procedure);
    }

    @Override
    public TCharFloatIterator iterator() {
        return new TCharFloatIterator(){
            TCharFloatIterator iter;
            {
                this.iter = TUnmodifiableCharFloatMap.this.m.iterator();
            }

            public char key() {
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
    public float putIfAbsent(char key, float value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void transformValues(TFloatFunction function) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainEntries(TCharFloatProcedure procedure) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean increment(char key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean adjustValue(char key, float amount) {
        throw new UnsupportedOperationException();
    }

    @Override
    public float adjustOrPutValue(char key, float adjust_amount, float put_amount) {
        throw new UnsupportedOperationException();
    }

}

