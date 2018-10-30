/*
 * Decompiled with CFR 0_132.
 */
package gnu.trove.impl.unmodifiable;

import gnu.trove.TCollections;
import gnu.trove.TLongCollection;
import gnu.trove.function.TLongFunction;
import gnu.trove.iterator.TFloatLongIterator;
import gnu.trove.map.TFloatLongMap;
import gnu.trove.procedure.TFloatLongProcedure;
import gnu.trove.procedure.TFloatProcedure;
import gnu.trove.procedure.TLongProcedure;
import gnu.trove.set.TFloatSet;
import java.io.Serializable;
import java.util.Map;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class TUnmodifiableFloatLongMap
implements TFloatLongMap,
Serializable {
    private static final long serialVersionUID = -1034234728574286014L;
    private final TFloatLongMap m;
    private transient TFloatSet keySet = null;
    private transient TLongCollection values = null;

    public TUnmodifiableFloatLongMap(TFloatLongMap m) {
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
    public boolean containsValue(long val) {
        return this.m.containsValue(val);
    }

    @Override
    public long get(float key) {
        return this.m.get(key);
    }

    @Override
    public long put(float key, long value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public long remove(float key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(TFloatLongMap m) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(Map<? extends Float, ? extends Long> map) {
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
    public TLongCollection valueCollection() {
        if (this.values == null) {
            this.values = TCollections.unmodifiableCollection(this.m.valueCollection());
        }
        return this.values;
    }

    @Override
    public long[] values() {
        return this.m.values();
    }

    @Override
    public long[] values(long[] array) {
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
    public long getNoEntryValue() {
        return this.m.getNoEntryValue();
    }

    @Override
    public boolean forEachKey(TFloatProcedure procedure) {
        return this.m.forEachKey(procedure);
    }

    @Override
    public boolean forEachValue(TLongProcedure procedure) {
        return this.m.forEachValue(procedure);
    }

    @Override
    public boolean forEachEntry(TFloatLongProcedure procedure) {
        return this.m.forEachEntry(procedure);
    }

    @Override
    public TFloatLongIterator iterator() {
        return new TFloatLongIterator(){
            TFloatLongIterator iter;
            {
                this.iter = TUnmodifiableFloatLongMap.this.m.iterator();
            }

            public float key() {
                return this.iter.key();
            }

            public long value() {
                return this.iter.value();
            }

            public void advance() {
                this.iter.advance();
            }

            public boolean hasNext() {
                return this.iter.hasNext();
            }

            public long setValue(long val) {
                throw new UnsupportedOperationException();
            }

            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    @Override
    public long putIfAbsent(float key, long value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void transformValues(TLongFunction function) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainEntries(TFloatLongProcedure procedure) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean increment(float key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean adjustValue(float key, long amount) {
        throw new UnsupportedOperationException();
    }

    @Override
    public long adjustOrPutValue(float key, long adjust_amount, long put_amount) {
        throw new UnsupportedOperationException();
    }

}

