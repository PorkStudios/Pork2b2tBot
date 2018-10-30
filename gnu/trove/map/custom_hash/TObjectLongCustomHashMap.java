/*
 * Decompiled with CFR 0_132.
 */
package gnu.trove.map.custom_hash;

import gnu.trove.TLongCollection;
import gnu.trove.function.TLongFunction;
import gnu.trove.impl.Constants;
import gnu.trove.impl.HashFunctions;
import gnu.trove.impl.hash.TCustomObjectHash;
import gnu.trove.impl.hash.THash;
import gnu.trove.impl.hash.TObjectHash;
import gnu.trove.iterator.TLongIterator;
import gnu.trove.iterator.TObjectLongIterator;
import gnu.trove.iterator.hash.TObjectHashIterator;
import gnu.trove.map.TObjectLongMap;
import gnu.trove.procedure.TLongProcedure;
import gnu.trove.procedure.TObjectLongProcedure;
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
public class TObjectLongCustomHashMap<K>
extends TCustomObjectHash<K>
implements TObjectLongMap<K>,
Externalizable {
    static final long serialVersionUID = 1L;
    private final TObjectLongProcedure<K> PUT_ALL_PROC = new TObjectLongProcedure<K>(){

        @Override
        public boolean execute(K key, long value) {
            TObjectLongCustomHashMap.this.put(key, value);
            return true;
        }
    };
    protected transient long[] _values;
    protected long no_entry_value;

    public TObjectLongCustomHashMap() {
    }

    public TObjectLongCustomHashMap(HashingStrategy<? super K> strategy) {
        super(strategy);
        this.no_entry_value = Constants.DEFAULT_LONG_NO_ENTRY_VALUE;
    }

    public TObjectLongCustomHashMap(HashingStrategy<? super K> strategy, int initialCapacity) {
        super(strategy, initialCapacity);
        this.no_entry_value = Constants.DEFAULT_LONG_NO_ENTRY_VALUE;
    }

    public TObjectLongCustomHashMap(HashingStrategy<? super K> strategy, int initialCapacity, float loadFactor) {
        super(strategy, initialCapacity, loadFactor);
        this.no_entry_value = Constants.DEFAULT_LONG_NO_ENTRY_VALUE;
    }

    public TObjectLongCustomHashMap(HashingStrategy<? super K> strategy, int initialCapacity, float loadFactor, long noEntryValue) {
        super(strategy, initialCapacity, loadFactor);
        this.no_entry_value = noEntryValue;
        if (this.no_entry_value != 0L) {
            Arrays.fill(this._values, this.no_entry_value);
        }
    }

    public TObjectLongCustomHashMap(HashingStrategy<? super K> strategy, TObjectLongMap<? extends K> map) {
        this(strategy, map.size(), 0.5f, map.getNoEntryValue());
        if (map instanceof TObjectLongCustomHashMap) {
            TObjectLongCustomHashMap hashmap = (TObjectLongCustomHashMap)map;
            this._loadFactor = hashmap._loadFactor;
            this.no_entry_value = hashmap.no_entry_value;
            this.strategy = hashmap.strategy;
            if (this.no_entry_value != 0L) {
                Arrays.fill(this._values, this.no_entry_value);
            }
            this.setUp((int)Math.ceil(10.0f / this._loadFactor));
        }
        this.putAll(map);
    }

    @Override
    public int setUp(int initialCapacity) {
        int capacity = super.setUp(initialCapacity);
        this._values = new long[capacity];
        return capacity;
    }

    @Override
    protected void rehash(int newCapacity) {
        int oldCapacity = this._set.length;
        Object[] oldKeys = this._set;
        long[] oldVals = this._values;
        this._set = new Object[newCapacity];
        Arrays.fill(this._set, FREE);
        this._values = new long[newCapacity];
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
    public long getNoEntryValue() {
        return this.no_entry_value;
    }

    @Override
    public boolean containsKey(Object key) {
        return this.contains(key);
    }

    @Override
    public boolean containsValue(long val) {
        Object[] keys = this._set;
        long[] vals = this._values;
        int i = vals.length;
        while (i-- > 0) {
            if (keys[i] == FREE || keys[i] == REMOVED || val != vals[i]) continue;
            return true;
        }
        return false;
    }

    @Override
    public long get(Object key) {
        int index = this.index(key);
        return index < 0 ? this.no_entry_value : this._values[index];
    }

    @Override
    public long put(K key, long value) {
        int index = this.insertKey(key);
        return this.doPut(value, index);
    }

    @Override
    public long putIfAbsent(K key, long value) {
        int index = this.insertKey(key);
        if (index < 0) {
            return this._values[- index - 1];
        }
        return this.doPut(value, index);
    }

    private long doPut(long value, int index) {
        long previous = this.no_entry_value;
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
    public long remove(Object key) {
        long prev = this.no_entry_value;
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
    public void putAll(Map<? extends K, ? extends Long> map) {
        Set<Map.Entry<K, Long>> set = map.entrySet();
        for (Map.Entry<K, Long> entry : set) {
            this.put(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public void putAll(TObjectLongMap<? extends K> map) {
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
    public TLongCollection valueCollection() {
        return new TLongValueCollection();
    }

    @Override
    public long[] values() {
        long[] vals = new long[this.size()];
        long[] v = this._values;
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
    public long[] values(long[] array) {
        int size = this.size();
        if (array.length < size) {
            array = new long[size];
        }
        long[] v = this._values;
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
    public TObjectLongIterator<K> iterator() {
        return new TObjectLongHashIterator(this);
    }

    @Override
    public boolean increment(K key) {
        return this.adjustValue(key, 1L);
    }

    @Override
    public boolean adjustValue(K key, long amount) {
        int index = this.index(key);
        if (index < 0) {
            return false;
        }
        long[] arrl = this._values;
        int n = index;
        arrl[n] = arrl[n] + amount;
        return true;
    }

    @Override
    public long adjustOrPutValue(K key, long adjust_amount, long put_amount) {
        boolean isNewMapping;
        long newValue;
        int index = this.insertKey(key);
        if (index < 0) {
            index = - index - 1;
            long[] arrl = this._values;
            int n = index;
            long l = arrl[n] + adjust_amount;
            arrl[n] = l;
            newValue = l;
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
    public boolean forEachValue(TLongProcedure procedure) {
        Object[] keys = this._set;
        long[] values = this._values;
        int i = values.length;
        while (i-- > 0) {
            if (keys[i] == FREE || keys[i] == REMOVED || procedure.execute(values[i])) continue;
            return false;
        }
        return true;
    }

    @Override
    public boolean forEachEntry(TObjectLongProcedure<? super K> procedure) {
        Object[] keys = this._set;
        long[] values = this._values;
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
    public boolean retainEntries(TObjectLongProcedure<? super K> procedure) {
        boolean modified;
        modified = false;
        Object[] keys = this._set;
        long[] values = this._values;
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
    public void transformValues(TLongFunction function) {
        Object[] keys = this._set;
        long[] values = this._values;
        int i = values.length;
        while (i-- > 0) {
            if (keys[i] == null || keys[i] == REMOVED) continue;
            values[i] = function.execute(values[i]);
        }
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof TObjectLongMap)) {
            return false;
        }
        TObjectLongMap that = (TObjectLongMap)other;
        if (that.size() != this.size()) {
            return false;
        }
        try {
            TObjectLongIterator<K> iter = this.iterator();
            while (iter.hasNext()) {
                iter.advance();
                K key = iter.key();
                long value = iter.value();
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
        long[] values = this._values;
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
        out.writeLong(this.no_entry_value);
        out.writeInt(this._size);
        int i = this._set.length;
        while (i-- > 0) {
            if (this._set[i] == REMOVED || this._set[i] == FREE) continue;
            out.writeObject(this._set[i]);
            out.writeLong(this._values[i]);
        }
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        in.readByte();
        super.readExternal(in);
        this.strategy = (HashingStrategy)in.readObject();
        this.no_entry_value = in.readLong();
        int size = in.readInt();
        this.setUp(size);
        while (size-- > 0) {
            Object key = in.readObject();
            long val = in.readLong();
            this.put(key, val);
        }
    }

    public String toString() {
        final StringBuilder buf = new StringBuilder("{");
        this.forEachEntry(new TObjectLongProcedure<K>(){
            private boolean first = true;

            @Override
            public boolean execute(K key, long value) {
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
    class TObjectLongHashIterator<K>
    extends TObjectHashIterator<K>
    implements TObjectLongIterator<K> {
        private final TObjectLongCustomHashMap<K> _map;

        public TObjectLongHashIterator(TObjectLongCustomHashMap<K> map) {
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
        public long value() {
            return this._map._values[this._index];
        }

        @Override
        public long setValue(long val) {
            long old = this.value();
            this._map._values[this._index] = val;
            return old;
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    class TLongValueCollection
    implements TLongCollection {
        TLongValueCollection() {
        }

        @Override
        public TLongIterator iterator() {
            return new TObjectLongValueHashIterator();
        }

        @Override
        public long getNoEntryValue() {
            return TObjectLongCustomHashMap.this.no_entry_value;
        }

        @Override
        public int size() {
            return TObjectLongCustomHashMap.this._size;
        }

        @Override
        public boolean isEmpty() {
            return 0 == TObjectLongCustomHashMap.this._size;
        }

        @Override
        public boolean contains(long entry) {
            return TObjectLongCustomHashMap.this.containsValue(entry);
        }

        @Override
        public long[] toArray() {
            return TObjectLongCustomHashMap.this.values();
        }

        @Override
        public long[] toArray(long[] dest) {
            return TObjectLongCustomHashMap.this.values(dest);
        }

        @Override
        public boolean add(long entry) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean remove(long entry) {
            long[] values = TObjectLongCustomHashMap.this._values;
            Object[] set = TObjectLongCustomHashMap.this._set;
            int i = values.length;
            while (i-- > 0) {
                if (set[i] == TObjectHash.FREE || set[i] == TObjectHash.REMOVED || entry != values[i]) continue;
                TObjectLongCustomHashMap.this.removeAt(i);
                return true;
            }
            return false;
        }

        @Override
        public boolean containsAll(Collection<?> collection) {
            for (Object element : collection) {
                if (element instanceof Long) {
                    long ele = (Long)element;
                    if (TObjectLongCustomHashMap.this.containsValue(ele)) continue;
                    return false;
                }
                return false;
            }
            return true;
        }

        @Override
        public boolean containsAll(TLongCollection collection) {
            TLongIterator iter = collection.iterator();
            while (iter.hasNext()) {
                if (TObjectLongCustomHashMap.this.containsValue(iter.next())) continue;
                return false;
            }
            return true;
        }

        @Override
        public boolean containsAll(long[] array) {
            for (long element : array) {
                if (TObjectLongCustomHashMap.this.containsValue(element)) continue;
                return false;
            }
            return true;
        }

        @Override
        public boolean addAll(Collection<? extends Long> collection) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(TLongCollection collection) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(long[] array) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean retainAll(Collection<?> collection) {
            boolean modified = false;
            TLongIterator iter = this.iterator();
            while (iter.hasNext()) {
                if (collection.contains(iter.next())) continue;
                iter.remove();
                modified = true;
            }
            return modified;
        }

        @Override
        public boolean retainAll(TLongCollection collection) {
            if (this == collection) {
                return false;
            }
            boolean modified = false;
            TLongIterator iter = this.iterator();
            while (iter.hasNext()) {
                if (collection.contains(iter.next())) continue;
                iter.remove();
                modified = true;
            }
            return modified;
        }

        @Override
        public boolean retainAll(long[] array) {
            boolean changed = false;
            Arrays.sort(array);
            long[] values = TObjectLongCustomHashMap.this._values;
            Object[] set = TObjectLongCustomHashMap.this._set;
            int i = set.length;
            while (i-- > 0) {
                if (set[i] == TObjectHash.FREE || set[i] == TObjectHash.REMOVED || Arrays.binarySearch(array, values[i]) >= 0) continue;
                TObjectLongCustomHashMap.this.removeAt(i);
                changed = true;
            }
            return changed;
        }

        @Override
        public boolean removeAll(Collection<?> collection) {
            boolean changed = false;
            for (Object element : collection) {
                long c;
                if (!(element instanceof Long) || !this.remove(c = ((Long)element).longValue())) continue;
                changed = true;
            }
            return changed;
        }

        @Override
        public boolean removeAll(TLongCollection collection) {
            if (this == collection) {
                this.clear();
                return true;
            }
            boolean changed = false;
            TLongIterator iter = collection.iterator();
            while (iter.hasNext()) {
                long element = iter.next();
                if (!this.remove(element)) continue;
                changed = true;
            }
            return changed;
        }

        @Override
        public boolean removeAll(long[] array) {
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
            TObjectLongCustomHashMap.this.clear();
        }

        @Override
        public boolean forEach(TLongProcedure procedure) {
            return TObjectLongCustomHashMap.this.forEachValue(procedure);
        }

        public String toString() {
            final StringBuilder buf = new StringBuilder("{");
            TObjectLongCustomHashMap.this.forEachValue(new TLongProcedure(){
                private boolean first = true;

                public boolean execute(long value) {
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

        class TObjectLongValueHashIterator
        implements TLongIterator {
            protected THash _hash;
            protected int _expectedSize;
            protected int _index;

            TObjectLongValueHashIterator() {
                this._hash = TObjectLongCustomHashMap.this;
                this._expectedSize = this._hash.size();
                this._index = this._hash.capacity();
            }

            public boolean hasNext() {
                return this.nextIndex() >= 0;
            }

            public long next() {
                this.moveToNextIndex();
                return TObjectLongCustomHashMap.this._values[this._index];
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
                    TObjectLongCustomHashMap.this.removeAt(this._index);
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
                Object[] set = TObjectLongCustomHashMap.this._set;
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
            TObjectLongCustomHashMap.this.clear();
        }

        @Override
        public boolean add(E obj) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int size() {
            return TObjectLongCustomHashMap.this.size();
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
            return TObjectLongCustomHashMap.this.isEmpty();
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
    extends TObjectLongCustomHashMap<K> {
        protected KeyView() {
        }

        @Override
        public Iterator<K> iterator() {
            return new TObjectHashIterator(TObjectLongCustomHashMap.this);
        }

        public boolean removeElement(K key) {
            return TObjectLongCustomHashMap.this.no_entry_value != TObjectLongCustomHashMap.this.remove(key);
        }

        public boolean containsElement(K key) {
            return TObjectLongCustomHashMap.this.contains(key);
        }
    }

}

