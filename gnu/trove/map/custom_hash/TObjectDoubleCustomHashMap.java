/*
 * Decompiled with CFR 0_132.
 */
package gnu.trove.map.custom_hash;

import gnu.trove.TDoubleCollection;
import gnu.trove.function.TDoubleFunction;
import gnu.trove.impl.Constants;
import gnu.trove.impl.HashFunctions;
import gnu.trove.impl.hash.TCustomObjectHash;
import gnu.trove.impl.hash.THash;
import gnu.trove.impl.hash.TObjectHash;
import gnu.trove.iterator.TDoubleIterator;
import gnu.trove.iterator.TObjectDoubleIterator;
import gnu.trove.iterator.hash.TObjectHashIterator;
import gnu.trove.map.TObjectDoubleMap;
import gnu.trove.procedure.TDoubleProcedure;
import gnu.trove.procedure.TObjectDoubleProcedure;
import gnu.trove.procedure.TObjectProcedure;
import gnu.trove.strategy.HashingStrategy;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.lang.reflect.Array;
import java.util.AbstractSet;
import java.util.Arrays;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class TObjectDoubleCustomHashMap<K>
extends TCustomObjectHash<K>
implements TObjectDoubleMap<K>,
Externalizable {
    static final long serialVersionUID = 1L;
    private final TObjectDoubleProcedure<K> PUT_ALL_PROC = new TObjectDoubleProcedure<K>(){

        @Override
        public boolean execute(K key, double value) {
            TObjectDoubleCustomHashMap.this.put(key, value);
            return true;
        }
    };
    protected transient double[] _values;
    protected double no_entry_value;

    public TObjectDoubleCustomHashMap() {
    }

    public TObjectDoubleCustomHashMap(HashingStrategy<? super K> strategy) {
        super(strategy);
        this.no_entry_value = Constants.DEFAULT_DOUBLE_NO_ENTRY_VALUE;
    }

    public TObjectDoubleCustomHashMap(HashingStrategy<? super K> strategy, int initialCapacity) {
        super(strategy, initialCapacity);
        this.no_entry_value = Constants.DEFAULT_DOUBLE_NO_ENTRY_VALUE;
    }

    public TObjectDoubleCustomHashMap(HashingStrategy<? super K> strategy, int initialCapacity, float loadFactor) {
        super(strategy, initialCapacity, loadFactor);
        this.no_entry_value = Constants.DEFAULT_DOUBLE_NO_ENTRY_VALUE;
    }

    public TObjectDoubleCustomHashMap(HashingStrategy<? super K> strategy, int initialCapacity, float loadFactor, double noEntryValue) {
        super(strategy, initialCapacity, loadFactor);
        this.no_entry_value = noEntryValue;
        if (this.no_entry_value != 0.0) {
            Arrays.fill(this._values, this.no_entry_value);
        }
    }

    public TObjectDoubleCustomHashMap(HashingStrategy<? super K> strategy, TObjectDoubleMap<? extends K> map) {
        this(strategy, map.size(), 0.5f, map.getNoEntryValue());
        if (map instanceof TObjectDoubleCustomHashMap) {
            TObjectDoubleCustomHashMap hashmap = (TObjectDoubleCustomHashMap)map;
            this._loadFactor = hashmap._loadFactor;
            this.no_entry_value = hashmap.no_entry_value;
            this.strategy = hashmap.strategy;
            if (this.no_entry_value != 0.0) {
                Arrays.fill(this._values, this.no_entry_value);
            }
            this.setUp((int)Math.ceil(10.0f / this._loadFactor));
        }
        this.putAll(map);
    }

    @Override
    public int setUp(int initialCapacity) {
        int capacity = super.setUp(initialCapacity);
        this._values = new double[capacity];
        return capacity;
    }

    @Override
    protected void rehash(int newCapacity) {
        int oldCapacity = this._set.length;
        Object[] oldKeys = this._set;
        double[] oldVals = this._values;
        this._set = new Object[newCapacity];
        Arrays.fill(this._set, FREE);
        this._values = new double[newCapacity];
        Arrays.fill(this._values, this.no_entry_value);
        int i = oldCapacity;
        while (i-- > 0) {
            Object o = oldKeys[i];
            if (o == FREE || o == REMOVED) continue;
            int index = this.insertKey(o);
            if (index < 0) {
                this.throwObjectContractViolation(this._set[- index - 1], o);
            }
            this._values[index] = oldVals[i];
        }
    }

    @Override
    public double getNoEntryValue() {
        return this.no_entry_value;
    }

    @Override
    public boolean containsKey(Object key) {
        return this.contains(key);
    }

    @Override
    public boolean containsValue(double val) {
        Object[] keys = this._set;
        double[] vals = this._values;
        int i = vals.length;
        while (i-- > 0) {
            if (keys[i] == FREE || keys[i] == REMOVED || val != vals[i]) continue;
            return true;
        }
        return false;
    }

    @Override
    public double get(Object key) {
        int index = this.index(key);
        return index < 0 ? this.no_entry_value : this._values[index];
    }

    @Override
    public double put(K key, double value) {
        int index = this.insertKey(key);
        return this.doPut(value, index);
    }

    @Override
    public double putIfAbsent(K key, double value) {
        int index = this.insertKey(key);
        if (index < 0) {
            return this._values[- index - 1];
        }
        return this.doPut(value, index);
    }

    private double doPut(double value, int index) {
        double previous = this.no_entry_value;
        boolean isNewMapping = true;
        if (index < 0) {
            index = - index - 1;
            previous = this._values[index];
            isNewMapping = false;
        }
        this._values[index] = value;
        if (isNewMapping) {
            this.postInsertHook(this.consumeFreeSlot);
        }
        return previous;
    }

    @Override
    public double remove(Object key) {
        double prev = this.no_entry_value;
        int index = this.index(key);
        if (index >= 0) {
            prev = this._values[index];
            this.removeAt(index);
        }
        return prev;
    }

    @Override
    protected void removeAt(int index) {
        this._values[index] = this.no_entry_value;
        super.removeAt(index);
    }

    @Override
    public void putAll(Map<? extends K, ? extends Double> map) {
        Set<Map.Entry<K, Double>> set = map.entrySet();
        for (Map.Entry<K, Double> entry : set) {
            this.put(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public void putAll(TObjectDoubleMap<? extends K> map) {
        map.forEachEntry(this.PUT_ALL_PROC);
    }

    @Override
    public void clear() {
        super.clear();
        Arrays.fill(this._set, 0, this._set.length, FREE);
        Arrays.fill(this._values, 0, this._values.length, this.no_entry_value);
    }

    @Override
    public Set<K> keySet() {
        return new KeyView();
    }

    @Override
    public Object[] keys() {
        Object[] keys = new Object[this.size()];
        Object[] k = this._set;
        int i = k.length;
        int j = 0;
        while (i-- > 0) {
            if (k[i] == FREE || k[i] == REMOVED) continue;
            keys[j++] = k[i];
        }
        return keys;
    }

    @Override
    public K[] keys(K[] a) {
        int size = this.size();
        if (a.length < size) {
            a = (Object[])Array.newInstance(a.getClass().getComponentType(), size);
        }
        Object[] k = this._set;
        int i = k.length;
        int j = 0;
        while (i-- > 0) {
            if (k[i] == FREE || k[i] == REMOVED) continue;
            a[j++] = k[i];
        }
        return a;
    }

    @Override
    public TDoubleCollection valueCollection() {
        return new TDoubleValueCollection();
    }

    @Override
    public double[] values() {
        double[] vals = new double[this.size()];
        double[] v = this._values;
        Object[] keys = this._set;
        int i = v.length;
        int j = 0;
        while (i-- > 0) {
            if (keys[i] == FREE || keys[i] == REMOVED) continue;
            vals[j++] = v[i];
        }
        return vals;
    }

    @Override
    public double[] values(double[] array) {
        int size = this.size();
        if (array.length < size) {
            array = new double[size];
        }
        double[] v = this._values;
        Object[] keys = this._set;
        int i = v.length;
        int j = 0;
        while (i-- > 0) {
            if (keys[i] == FREE || keys[i] == REMOVED) continue;
            array[j++] = v[i];
        }
        if (array.length > size) {
            array[size] = this.no_entry_value;
        }
        return array;
    }

    @Override
    public TObjectDoubleIterator<K> iterator() {
        return new TObjectDoubleHashIterator(this);
    }

    @Override
    public boolean increment(K key) {
        return this.adjustValue(key, 1.0);
    }

    @Override
    public boolean adjustValue(K key, double amount) {
        int index = this.index(key);
        if (index < 0) {
            return false;
        }
        double[] arrd = this._values;
        int n = index;
        arrd[n] = arrd[n] + amount;
        return true;
    }

    @Override
    public double adjustOrPutValue(K key, double adjust_amount, double put_amount) {
        double newValue;
        boolean isNewMapping;
        int index = this.insertKey(key);
        if (index < 0) {
            index = - index - 1;
            double[] arrd = this._values;
            int n = index;
            double d = arrd[n] + adjust_amount;
            arrd[n] = d;
            newValue = d;
            isNewMapping = false;
        } else {
            newValue = this._values[index] = put_amount;
            isNewMapping = true;
        }
        if (isNewMapping) {
            this.postInsertHook(this.consumeFreeSlot);
        }
        return newValue;
    }

    @Override
    public boolean forEachKey(TObjectProcedure<? super K> procedure) {
        return this.forEach(procedure);
    }

    @Override
    public boolean forEachValue(TDoubleProcedure procedure) {
        Object[] keys = this._set;
        double[] values = this._values;
        int i = values.length;
        while (i-- > 0) {
            if (keys[i] == FREE || keys[i] == REMOVED || procedure.execute(values[i])) continue;
            return false;
        }
        return true;
    }

    @Override
    public boolean forEachEntry(TObjectDoubleProcedure<? super K> procedure) {
        Object[] keys = this._set;
        double[] values = this._values;
        int i = keys.length;
        while (i-- > 0) {
            if (keys[i] == FREE || keys[i] == REMOVED || procedure.execute(keys[i], values[i])) continue;
            return false;
        }
        return true;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean retainEntries(TObjectDoubleProcedure<? super K> procedure) {
        boolean modified;
        modified = false;
        Object[] keys = this._set;
        double[] values = this._values;
        this.tempDisableAutoCompaction();
        try {
            int i = keys.length;
            while (i-- > 0) {
                if (keys[i] == FREE || keys[i] == REMOVED || procedure.execute(keys[i], values[i])) continue;
                this.removeAt(i);
                modified = true;
            }
        }
        finally {
            this.reenableAutoCompaction(true);
        }
        return modified;
    }

    @Override
    public void transformValues(TDoubleFunction function) {
        Object[] keys = this._set;
        double[] values = this._values;
        int i = values.length;
        while (i-- > 0) {
            if (keys[i] == null || keys[i] == REMOVED) continue;
            values[i] = function.execute(values[i]);
        }
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof TObjectDoubleMap)) {
            return false;
        }
        TObjectDoubleMap that = (TObjectDoubleMap)other;
        if (that.size() != this.size()) {
            return false;
        }
        try {
            TObjectDoubleIterator<K> iter = this.iterator();
            while (iter.hasNext()) {
                iter.advance();
                K key = iter.key();
                double value = iter.value();
                if (!(value == this.no_entry_value ? that.get(key) != that.getNoEntryValue() || !that.containsKey(key) : value != that.get(key))) continue;
                return false;
            }
        }
        catch (ClassCastException ex) {
            // empty catch block
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hashcode = 0;
        Object[] keys = this._set;
        double[] values = this._values;
        int i = values.length;
        while (i-- > 0) {
            if (keys[i] == FREE || keys[i] == REMOVED) continue;
            hashcode += HashFunctions.hash(values[i]) ^ (keys[i] == null ? 0 : keys[i].hashCode());
        }
        return hashcode;
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeByte(0);
        super.writeExternal(out);
        out.writeObject(this.strategy);
        out.writeDouble(this.no_entry_value);
        out.writeInt(this._size);
        int i = this._set.length;
        while (i-- > 0) {
            if (this._set[i] == REMOVED || this._set[i] == FREE) continue;
            out.writeObject(this._set[i]);
            out.writeDouble(this._values[i]);
        }
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        in.readByte();
        super.readExternal(in);
        this.strategy = (HashingStrategy)in.readObject();
        this.no_entry_value = in.readDouble();
        int size = in.readInt();
        this.setUp(size);
        while (size-- > 0) {
            Object key = in.readObject();
            double val = in.readDouble();
            this.put(key, val);
        }
    }

    public String toString() {
        final StringBuilder buf = new StringBuilder("{");
        this.forEachEntry(new TObjectDoubleProcedure<K>(){
            private boolean first = true;

            @Override
            public boolean execute(K key, double value) {
                if (this.first) {
                    this.first = false;
                } else {
                    buf.append(",");
                }
                buf.append(key).append("=").append(value);
                return true;
            }
        });
        buf.append("}");
        return buf.toString();
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    class TObjectDoubleHashIterator<K>
    extends TObjectHashIterator<K>
    implements TObjectDoubleIterator<K> {
        private final TObjectDoubleCustomHashMap<K> _map;

        public TObjectDoubleHashIterator(TObjectDoubleCustomHashMap<K> map) {
            super(map);
            this._map = map;
        }

        @Override
        public void advance() {
            this.moveToNextIndex();
        }

        @Override
        public K key() {
            return (K)this._map._set[this._index];
        }

        @Override
        public double value() {
            return this._map._values[this._index];
        }

        @Override
        public double setValue(double val) {
            double old = this.value();
            this._map._values[this._index] = val;
            return old;
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    class TDoubleValueCollection
    implements TDoubleCollection {
        TDoubleValueCollection() {
        }

        @Override
        public TDoubleIterator iterator() {
            return new TObjectDoubleValueHashIterator();
        }

        @Override
        public double getNoEntryValue() {
            return TObjectDoubleCustomHashMap.this.no_entry_value;
        }

        @Override
        public int size() {
            return TObjectDoubleCustomHashMap.this._size;
        }

        @Override
        public boolean isEmpty() {
            return 0 == TObjectDoubleCustomHashMap.this._size;
        }

        @Override
        public boolean contains(double entry) {
            return TObjectDoubleCustomHashMap.this.containsValue(entry);
        }

        @Override
        public double[] toArray() {
            return TObjectDoubleCustomHashMap.this.values();
        }

        @Override
        public double[] toArray(double[] dest) {
            return TObjectDoubleCustomHashMap.this.values(dest);
        }

        @Override
        public boolean add(double entry) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean remove(double entry) {
            double[] values = TObjectDoubleCustomHashMap.this._values;
            Object[] set = TObjectDoubleCustomHashMap.this._set;
            int i = values.length;
            while (i-- > 0) {
                if (set[i] == TObjectHash.FREE || set[i] == TObjectHash.REMOVED || entry != values[i]) continue;
                TObjectDoubleCustomHashMap.this.removeAt(i);
                return true;
            }
            return false;
        }

        @Override
        public boolean containsAll(Collection<?> collection) {
            for (Object element : collection) {
                if (element instanceof Double) {
                    double ele = (Double)element;
                    if (TObjectDoubleCustomHashMap.this.containsValue(ele)) continue;
                    return false;
                }
                return false;
            }
            return true;
        }

        @Override
        public boolean containsAll(TDoubleCollection collection) {
            TDoubleIterator iter = collection.iterator();
            while (iter.hasNext()) {
                if (TObjectDoubleCustomHashMap.this.containsValue(iter.next())) continue;
                return false;
            }
            return true;
        }

        @Override
        public boolean containsAll(double[] array) {
            for (double element : array) {
                if (TObjectDoubleCustomHashMap.this.containsValue(element)) continue;
                return false;
            }
            return true;
        }

        @Override
        public boolean addAll(Collection<? extends Double> collection) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(TDoubleCollection collection) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(double[] array) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean retainAll(Collection<?> collection) {
            boolean modified = false;
            TDoubleIterator iter = this.iterator();
            while (iter.hasNext()) {
                if (collection.contains(iter.next())) continue;
                iter.remove();
                modified = true;
            }
            return modified;
        }

        @Override
        public boolean retainAll(TDoubleCollection collection) {
            if (this == collection) {
                return false;
            }
            boolean modified = false;
            TDoubleIterator iter = this.iterator();
            while (iter.hasNext()) {
                if (collection.contains(iter.next())) continue;
                iter.remove();
                modified = true;
            }
            return modified;
        }

        @Override
        public boolean retainAll(double[] array) {
            boolean changed = false;
            Arrays.sort(array);
            double[] values = TObjectDoubleCustomHashMap.this._values;
            Object[] set = TObjectDoubleCustomHashMap.this._set;
            int i = set.length;
            while (i-- > 0) {
                if (set[i] == TObjectHash.FREE || set[i] == TObjectHash.REMOVED || Arrays.binarySearch(array, values[i]) >= 0) continue;
                TObjectDoubleCustomHashMap.this.removeAt(i);
                changed = true;
            }
            return changed;
        }

        @Override
        public boolean removeAll(Collection<?> collection) {
            boolean changed = false;
            for (Object element : collection) {
                double c;
                if (!(element instanceof Double) || !this.remove(c = ((Double)element).doubleValue())) continue;
                changed = true;
            }
            return changed;
        }

        @Override
        public boolean removeAll(TDoubleCollection collection) {
            if (this == collection) {
                this.clear();
                return true;
            }
            boolean changed = false;
            TDoubleIterator iter = collection.iterator();
            while (iter.hasNext()) {
                double element = iter.next();
                if (!this.remove(element)) continue;
                changed = true;
            }
            return changed;
        }

        @Override
        public boolean removeAll(double[] array) {
            boolean changed = false;
            int i = array.length;
            while (i-- > 0) {
                if (!this.remove(array[i])) continue;
                changed = true;
            }
            return changed;
        }

        @Override
        public void clear() {
            TObjectDoubleCustomHashMap.this.clear();
        }

        @Override
        public boolean forEach(TDoubleProcedure procedure) {
            return TObjectDoubleCustomHashMap.this.forEachValue(procedure);
        }

        public String toString() {
            final StringBuilder buf = new StringBuilder("{");
            TObjectDoubleCustomHashMap.this.forEachValue(new TDoubleProcedure(){
                private boolean first = true;

                public boolean execute(double value) {
                    if (this.first) {
                        this.first = false;
                    } else {
                        buf.append(", ");
                    }
                    buf.append(value);
                    return true;
                }
            });
            buf.append("}");
            return buf.toString();
        }

        class TObjectDoubleValueHashIterator
        implements TDoubleIterator {
            protected THash _hash;
            protected int _expectedSize;
            protected int _index;

            TObjectDoubleValueHashIterator() {
                this._hash = TObjectDoubleCustomHashMap.this;
                this._expectedSize = this._hash.size();
                this._index = this._hash.capacity();
            }

            public boolean hasNext() {
                return this.nextIndex() >= 0;
            }

            public double next() {
                this.moveToNextIndex();
                return TObjectDoubleCustomHashMap.this._values[this._index];
            }

            /*
             * WARNING - Removed try catching itself - possible behaviour change.
             */
            public void remove() {
                if (this._expectedSize != this._hash.size()) {
                    throw new ConcurrentModificationException();
                }
                try {
                    this._hash.tempDisableAutoCompaction();
                    TObjectDoubleCustomHashMap.this.removeAt(this._index);
                }
                finally {
                    this._hash.reenableAutoCompaction(false);
                }
                --this._expectedSize;
            }

            protected final void moveToNextIndex() {
                this._index = this.nextIndex();
                if (this._index < 0) {
                    throw new NoSuchElementException();
                }
            }

            protected final int nextIndex() {
                if (this._expectedSize != this._hash.size()) {
                    throw new ConcurrentModificationException();
                }
                Object[] set = TObjectDoubleCustomHashMap.this._set;
                int i = this._index;
                while (i-- > 0 && (set[i] == TCustomObjectHash.FREE || set[i] == TCustomObjectHash.REMOVED)) {
                }
                return i;
            }
        }

    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private abstract class MapBackedView<E>
    extends AbstractSet<E>
    implements Set<E>,
    Iterable<E> {
        private MapBackedView() {
        }

        public abstract boolean removeElement(E var1);

        public abstract boolean containsElement(E var1);

        @Override
        public boolean contains(Object key) {
            return this.containsElement(key);
        }

        @Override
        public boolean remove(Object o) {
            return this.removeElement(o);
        }

        @Override
        public void clear() {
            TObjectDoubleCustomHashMap.this.clear();
        }

        @Override
        public boolean add(E obj) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int size() {
            return TObjectDoubleCustomHashMap.this.size();
        }

        @Override
        public Object[] toArray() {
            Object[] result = new Object[this.size()];
            Iterator<E> e = this.iterator();
            int i = 0;
            while (e.hasNext()) {
                result[i] = e.next();
                ++i;
            }
            return result;
        }

        @Override
        public <T> T[] toArray(T[] a) {
            int size = this.size();
            if (a.length < size) {
                a = (Object[])Array.newInstance(a.getClass().getComponentType(), size);
            }
            Iterator<E> it = this.iterator();
            T[] result = a;
            for (int i = 0; i < size; ++i) {
                result[i] = it.next();
            }
            if (a.length > size) {
                a[size] = null;
            }
            return a;
        }

        @Override
        public boolean isEmpty() {
            return TObjectDoubleCustomHashMap.this.isEmpty();
        }

        @Override
        public boolean addAll(Collection<? extends E> collection) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean retainAll(Collection<?> collection) {
            boolean changed = false;
            Iterator<E> i = this.iterator();
            while (i.hasNext()) {
                if (collection.contains(i.next())) continue;
                i.remove();
                changed = true;
            }
            return changed;
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    protected class KeyView
    extends TObjectDoubleCustomHashMap<K> {
        protected KeyView() {
        }

        @Override
        public Iterator<K> iterator() {
            return new TObjectHashIterator(TObjectDoubleCustomHashMap.this);
        }

        public boolean removeElement(K key) {
            return TObjectDoubleCustomHashMap.this.no_entry_value != TObjectDoubleCustomHashMap.this.remove(key);
        }

        public boolean containsElement(K key) {
            return TObjectDoubleCustomHashMap.this.contains(key);
        }
    }

}

