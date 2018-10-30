/*
 * Decompiled with CFR 0_132.
 */
package gnu.trove.impl.unmodifiable;

import gnu.trove.TCollections;
import gnu.trove.TFloatCollection;
import gnu.trove.function.TFloatFunction;
import gnu.trove.iterator.TByteFloatIterator;
import gnu.trove.map.TByteFloatMap;
import gnu.trove.procedure.TByteFloatProcedure;
import gnu.trove.procedure.TByteProcedure;
import gnu.trove.procedure.TFloatProcedure;
import gnu.trove.set.TByteSet;
import java.io.Serializable;
import java.util.Map;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class TUnmodifiableByteFloatMap
implements TByteFloatMap,
Serializable {
    private static final long serialVersionUID = -1034234728574286014L;
    private final TByteFloatMap m;
    private transient TByteSet keySet = null;
    private transient TFloatCollection values = null;

    public TUnmodifiableByteFloatMap(TByteFloatMap m) {
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
    public boolean containsValue(float val) {
        return this.m.containsValue(val);
    }

    @Override
    public float get(byte key) {
        return this.m.get(key);
    }

    @Override
    public float put(byte key, float value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public float remove(byte key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(TByteFloatMap m) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(Map<? extends Byte, ? extends Float> map) {
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
    public byte getNoEntryKey() {
        return this.m.getNoEntryKey();
    }

    @Override
    public float getNoEntryValue() {
        return this.m.getNoEntryValue();
    }

    @Override
    public boolean forEachKey(TByteProcedure procedure) {
        return this.m.forEachKey(procedure);
    }

    @Override
    public boolean forEachValue(TFloatProcedure procedure) {
        return this.m.forEachValue(procedure);
    }

    @Override
    public boolean forEachEntry(TByteFloatProcedure procedure) {
        return this.m.forEachEntry(procedure);
    }

    @Override
    public TByteFloatIterator iterator() {
        return new TByteFloatIterator(){
            TByteFloatIterator iter;
            {
                this.iter = TUnmodifiableByteFloatMap.this.m.iterator();
            }

            public byte key() {
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
    public float putIfAbsent(byte key, float value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void transformValues(TFloatFunction function) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainEntries(TByteFloatProcedure procedure) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean increment(byte key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean adjustValue(byte key, float amount) {
        throw new UnsupportedOperationException();
    }

    @Override
    public float adjustOrPutValue(byte key, float adjust_amount, float put_amount) {
        throw new UnsupportedOperationException();
    }

}

