/*
 * Decompiled with CFR 0_132.
 */
package gnu.trove.impl.unmodifiable;

import gnu.trove.TByteCollection;
import gnu.trove.TCollections;
import gnu.trove.function.TByteFunction;
import gnu.trove.iterator.TIntByteIterator;
import gnu.trove.map.TIntByteMap;
import gnu.trove.procedure.TByteProcedure;
import gnu.trove.procedure.TIntByteProcedure;
import gnu.trove.procedure.TIntProcedure;
import gnu.trove.set.TIntSet;
import java.io.Serializable;
import java.util.Map;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class TUnmodifiableIntByteMap
implements TIntByteMap,
Serializable {
    private static final long serialVersionUID = -1034234728574286014L;
    private final TIntByteMap m;
    private transient TIntSet keySet = null;
    private transient TByteCollection values = null;

    public TUnmodifiableIntByteMap(TIntByteMap m) {
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
    public boolean containsKey(int key) {
        return this.m.containsKey(key);
    }

    @Override
    public boolean containsValue(byte val) {
        return this.m.containsValue(val);
    }

    @Override
    public byte get(int key) {
        return this.m.get(key);
    }

    @Override
    public byte put(int key, byte value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public byte remove(int key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(TIntByteMap m) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(Map<? extends Integer, ? extends Byte> map) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }

    @Override
    public TIntSet keySet() {
        if (this.keySet == null) {
            this.keySet = TCollections.unmodifiableSet(this.m.keySet());
        }
        return this.keySet;
    }

    @Override
    public int[] keys() {
        return this.m.keys();
    }

    @Override
    public int[] keys(int[] array) {
        return this.m.keys(array);
    }

    @Override
    public TByteCollection valueCollection() {
        if (this.values == null) {
            this.values = TCollections.unmodifiableCollection(this.m.valueCollection());
        }
        return this.values;
    }

    @Override
    public byte[] values() {
        return this.m.values();
    }

    @Override
    public byte[] values(byte[] array) {
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
    public int getNoEntryKey() {
        return this.m.getNoEntryKey();
    }

    @Override
    public byte getNoEntryValue() {
        return this.m.getNoEntryValue();
    }

    @Override
    public boolean forEachKey(TIntProcedure procedure) {
        return this.m.forEachKey(procedure);
    }

    @Override
    public boolean forEachValue(TByteProcedure procedure) {
        return this.m.forEachValue(procedure);
    }

    @Override
    public boolean forEachEntry(TIntByteProcedure procedure) {
        return this.m.forEachEntry(procedure);
    }

    @Override
    public TIntByteIterator iterator() {
        return new TIntByteIterator(){
            TIntByteIterator iter;
            {
                this.iter = TUnmodifiableIntByteMap.this.m.iterator();
            }

            public int key() {
                return this.iter.key();
            }

            public byte value() {
                return this.iter.value();
            }

            public void advance() {
                this.iter.advance();
            }

            public boolean hasNext() {
                return this.iter.hasNext();
            }

            public byte setValue(byte val) {
                throw new UnsupportedOperationException();
            }

            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    @Override
    public byte putIfAbsent(int key, byte value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void transformValues(TByteFunction function) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainEntries(TIntByteProcedure procedure) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean increment(int key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean adjustValue(int key, byte amount) {
        throw new UnsupportedOperationException();
    }

    @Override
    public byte adjustOrPutValue(int key, byte adjust_amount, byte put_amount) {
        throw new UnsupportedOperationException();
    }

}

