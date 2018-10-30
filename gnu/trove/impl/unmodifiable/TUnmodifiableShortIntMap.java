/*
 * Decompiled with CFR 0_132.
 */
package gnu.trove.impl.unmodifiable;

import gnu.trove.TCollections;
import gnu.trove.TIntCollection;
import gnu.trove.function.TIntFunction;
import gnu.trove.iterator.TShortIntIterator;
import gnu.trove.map.TShortIntMap;
import gnu.trove.procedure.TIntProcedure;
import gnu.trove.procedure.TShortIntProcedure;
import gnu.trove.procedure.TShortProcedure;
import gnu.trove.set.TShortSet;
import java.io.Serializable;
import java.util.Map;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class TUnmodifiableShortIntMap
implements TShortIntMap,
Serializable {
    private static final long serialVersionUID = -1034234728574286014L;
    private final TShortIntMap m;
    private transient TShortSet keySet = null;
    private transient TIntCollection values = null;

    public TUnmodifiableShortIntMap(TShortIntMap m) {
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
    public boolean containsValue(int val) {
        return this.m.containsValue(val);
    }

    @Override
    public int get(short key) {
        return this.m.get(key);
    }

    @Override
    public int put(short key, int value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int remove(short key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(TShortIntMap m) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(Map<? extends Short, ? extends Integer> map) {
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
    public TIntCollection valueCollection() {
        if (this.values == null) {
            this.values = TCollections.unmodifiableCollection(this.m.valueCollection());
        }
        return this.values;
    }

    @Override
    public int[] values() {
        return this.m.values();
    }

    @Override
    public int[] values(int[] array) {
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
    public int getNoEntryValue() {
        return this.m.getNoEntryValue();
    }

    @Override
    public boolean forEachKey(TShortProcedure procedure) {
        return this.m.forEachKey(procedure);
    }

    @Override
    public boolean forEachValue(TIntProcedure procedure) {
        return this.m.forEachValue(procedure);
    }

    @Override
    public boolean forEachEntry(TShortIntProcedure procedure) {
        return this.m.forEachEntry(procedure);
    }

    @Override
    public TShortIntIterator iterator() {
        return new TShortIntIterator(){
            TShortIntIterator iter;
            {
                this.iter = TUnmodifiableShortIntMap.this.m.iterator();
            }

            public short key() {
                return this.iter.key();
            }

            public int value() {
                return this.iter.value();
            }

            public void advance() {
                this.iter.advance();
            }

            public boolean hasNext() {
                return this.iter.hasNext();
            }

            public int setValue(int val) {
                throw new UnsupportedOperationException();
            }

            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    @Override
    public int putIfAbsent(short key, int value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void transformValues(TIntFunction function) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainEntries(TShortIntProcedure procedure) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean increment(short key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean adjustValue(short key, int amount) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int adjustOrPutValue(short key, int adjust_amount, int put_amount) {
        throw new UnsupportedOperationException();
    }

}

