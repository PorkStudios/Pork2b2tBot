/*
 * Decompiled with CFR 0_132.
 */
package gnu.trove.impl.unmodifiable;

import gnu.trove.TCollections;
import gnu.trove.TIntCollection;
import gnu.trove.function.TIntFunction;
import gnu.trove.iterator.TCharIntIterator;
import gnu.trove.map.TCharIntMap;
import gnu.trove.procedure.TCharIntProcedure;
import gnu.trove.procedure.TCharProcedure;
import gnu.trove.procedure.TIntProcedure;
import gnu.trove.set.TCharSet;
import java.io.Serializable;
import java.util.Map;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class TUnmodifiableCharIntMap
implements TCharIntMap,
Serializable {
    private static final long serialVersionUID = -1034234728574286014L;
    private final TCharIntMap m;
    private transient TCharSet keySet = null;
    private transient TIntCollection values = null;

    public TUnmodifiableCharIntMap(TCharIntMap m) {
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
    public boolean containsValue(int val) {
        return this.m.containsValue(val);
    }

    @Override
    public int get(char key) {
        return this.m.get(key);
    }

    @Override
    public int put(char key, int value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int remove(char key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(TCharIntMap m) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(Map<? extends Character, ? extends Integer> map) {
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
    public char getNoEntryKey() {
        return this.m.getNoEntryKey();
    }

    @Override
    public int getNoEntryValue() {
        return this.m.getNoEntryValue();
    }

    @Override
    public boolean forEachKey(TCharProcedure procedure) {
        return this.m.forEachKey(procedure);
    }

    @Override
    public boolean forEachValue(TIntProcedure procedure) {
        return this.m.forEachValue(procedure);
    }

    @Override
    public boolean forEachEntry(TCharIntProcedure procedure) {
        return this.m.forEachEntry(procedure);
    }

    @Override
    public TCharIntIterator iterator() {
        return new TCharIntIterator(){
            TCharIntIterator iter;
            {
                this.iter = TUnmodifiableCharIntMap.this.m.iterator();
            }

            public char key() {
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
    public int putIfAbsent(char key, int value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void transformValues(TIntFunction function) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainEntries(TCharIntProcedure procedure) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean increment(char key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean adjustValue(char key, int amount) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int adjustOrPutValue(char key, int adjust_amount, int put_amount) {
        throw new UnsupportedOperationException();
    }

}

