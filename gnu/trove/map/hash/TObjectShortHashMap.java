/*
 * Decompiled with CFR 0_132.
 */
package gnu.trove.map.hash;

import gnu.trove.TShortCollection;
import gnu.trove.function.TShortFunction;
import gnu.trove.impl.Constants;
import gnu.trove.impl.HashFunctions;
import gnu.trove.impl.hash.THash;
import gnu.trove.impl.hash.TObjectHash;
import gnu.trove.iterator.TObjectShortIterator;
import gnu.trove.iterator.TShortIterator;
import gnu.trove.iterator.hash.TObjectHashIterator;
import gnu.trove.map.TObjectShortMap;
import gnu.trove.procedure.TObjectProcedure;
import gnu.trove.procedure.TObjectShortProcedure;
import gnu.trove.procedure.TShortProcedure;
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
public class TObjectShortHashMap<K>
extends TObjectHash<K>
implements TObjectShortMap<K>,
Externalizable {
    static final long serialVersionUID = 1L;
    private final TObjectShortProcedure<K> PUT_ALL_PROC = new TObjectShortProcedure<K>(){

        @Override
        public boolean execute(K key, short value) {
            TObjectShortHashMap.this.put(key, value);
            return true;
        }
    };
    protected transient short[] _values;
    protected short no_entry_value;

    public TObjectShortHashMap() {
        this.no_entry_value = Constants.DEFAULT_SHORT_NO_ENTRY_VALUE;
    }

    public TObjectShortHashMap(int initialCapacity) {
        super(initialCapacity);
        this.no_entry_value = Constants.DEFAULT_SHORT_NO_ENTRY_VALUE;
    }

    public TObjectShortHashMap(int initialCapacity, float loadFactor) {
        super(initialCapacity, loadFactor);
        this.no_entry_value = Constants.DEFAULT_SHORT_NO_ENTRY_VALUE;
    }

    public TObjectShortHashMap(int initialCapacity, float loadFactor, short noEntryValue) {
        super(initialCapacity, loadFactor);
        this.no_entry_value = noEntryValue;
        if (this.no_entry_value != 0) {
            Arrays.fill(this._values, this.no_entry_value);
        }
    }

    public TObjectShortHashMap(TObjectShortMap<? extends K> map) {
        this(map.size(), 0.5f, map.getNoEntryValue());
        if (map instanceof TObjectShortHashMap) {
            TObjectShortHashMap hashmap = (TObjectShortHashMap)map;
            this._loadFactor = hashmap._loadFactor;
            this.no_entry_value = hashmap.no_entry_value;
            if (this.no_entry_value != 0) {
                Arrays.fill(this._values, this.no_entry_value);
            }
            this.setUp((int)Math.ceil(10.0f / this._loadFactor));
        }
        this.putAll(map);
    }

    @Override
    public int setUp(int initialCapacity) {
        int capacity = super.setUp(initialCapacity);
        this._values = new short[capacity];
        return capacity;
    }

    @Override
    protected void rehash(int newCapacity) {
        int oldCapacity = this._set.length;
        Object[] oldKeys = this._set;
        short[] oldVals = this._values;
        this._set = new Object[newCapacity];
        Arrays.fill(this._set, FREE);
        this._values = new short[newCapacity];
        Arrays.fill(this._values, this.no_entry_value);
        int i = oldCapacity;
        while (i-- > 0) {
            if (oldKeys[i] == FREE || oldKeys[i] == REMOVED) continue;
            Object o = oldKeys[i];
            int index = this.insertKey(o);
            if (index < 0) {
                this.throwObjectContractViolation(this._set[- index - 1], o);
            }
            this._set[index] = o;
            this._values[index] = oldVals[i];
        }
    }

    @Override
    public short getNoEntryValue() {
        return this.no_entry_value;
    }

    @Override
    public boolean containsKey(Object key) {
        return this.contains(key);
    }

    @Override
    public boolean containsValue(short val) {
        Object[] keys = this._set;
        short[] vals = this._values;
        int i = vals.length;
        while (i-- > 0) {
            if (keys[i] == FREE || keys[i] == REMOVED || val != vals[i]) continue;
            return true;
        }
        return false;
    }

    @Override
    public short get(Object key) {
        int index = this.index(key);
        return index < 0 ? this.no_entry_value : this._values[index];
    }

    @Override
    public short put(K key, short value) {
        int index = this.insertKey(key);
        return this.doPut(value, index);
    }

    @Override
    public short putIfAbsent(K key, short value) {
        int index = this.insertKey(key);
        if (index < 0) {
            return this._values[- index - 1];
        }
        return this.doPut(value, index);
    }

    private short doPut(short value, int index) {
        short previous = this.no_entry_value;
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
    public short remove(Object key) {
        short prev = this.no_entry_value;
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
    public void putAll(Map<? extends K, ? extends Short> map) {
        Set<Map.Entry<K, Short>> set = map.entrySet();
        for (Map.Entry<K, Short> entry : set) {
            this.put(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public void putAll(TObjectShortMap<? extends K> map) {
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
    public TShortCollection valueCollection() {
        return new TShortValueCollection();
    }

    @Override
    public short[] values() {
        short[] vals = new short[this.size()];
        short[] v = this._values;
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
    public short[] values(short[] array) {
        int size = this.size();
        if (array.length < size) {
            array = new short[size];
        }
        short[] v = this._values;
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
    public TObjectShortIterator<K> iterator() {
        return new TObjectShortHashIterator(this);
    }

    @Override
    public boolean increment(K key) {
        return this.adjustValue(key, (short)1);
    }

    @Override
    public boolean adjustValue(K key, short amount) {
        int index = this.index(key);
        if (index < 0) {
            return false;
        }
        short[] arrs = this._values;
        int n = index;
        arrs[n] = (short)(arrs[n] + amount);
        return true;
    }

    @Override
    public short adjustOrPutValue(K key, short adjust_amount, short put_amount) {
        short newValue;
        boolean isNewMapping;
        int index = this.insertKey(key);
        if (index < 0) {
            index = - index - 1;
            short[] arrs = this._values;
            int n = index;
            short s = (short)(arrs[n] + adjust_amount);
            arrs[n] = s;
            newValue = s;
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
    public boolean forEachValue(TShortProcedure procedure) {
        Object[] keys = this._set;
        short[] values = this._values;
        int i = values.length;
        while (i-- > 0) {
            if (keys[i] == FREE || keys[i] == REMOVED || procedure.execute(values[i])) continue;
            return false;
        }
        return true;
    }

    @Override
    public boolean forEachEntry(TObjectShortProcedure<? super K> procedure) {
        Object[] keys = this._set;
        short[] values = this._values;
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
    public boolean retainEntries(TObjectShortProcedure<? super K> procedure) {
        boolean modified;
        modified = false;
        Object[] keys = this._set;
        short[] values = this._values;
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
    public void transformValues(TShortFunction function) {
        Object[] keys = this._set;
        short[] values = this._values;
        int i = values.length;
        while (i-- > 0) {
            if (keys[i] == null || keys[i] == REMOVED) continue;
            values[i] = function.execute(values[i]);
        }
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof TObjectShortMap)) {
            return false;
        }
        TObjectShortMap that = (TObjectShortMap)other;
        if (that.size() != this.size()) {
            return false;
        }
        try {
            TObjectShortIterator<K> iter = this.iterator();
            while (iter.hasNext()) {
                iter.advance();
                K key = iter.key();
                short value = iter.value();
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
        short[] values = this._values;
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
        out.writeShort(this.no_entry_value);
        out.writeInt(this._size);
        int i = this._set.length;
        while (i-- > 0) {
            if (this._set[i] == REMOVED || this._set[i] == FREE) continue;
            out.writeObject(this._set[i]);
            out.writeShort(this._values[i]);
        }
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        in.readByte();
        super.readExternal(in);
        this.no_entry_value = in.readShort();
        int size = in.readInt();
        this.setUp(size);
        while (size-- > 0) {
            Object key = in.readObject();
            short val = in.readShort();
            this.put(key, val);
        }
    }

    public String toString() {
        final StringBuilder buf = new StringBuilder("{");
        this.forEachEntry(new TObjectShortProcedure<K>(){
            private boolean first = true;

            @Override
            public boolean execute(K key, short value) {
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
    class TObjectShortHashIterator<K>
    extends TObjectHashIterator<K>
    implements TObjectShortIterator<K> {
        private final TObjectShortHashMap<K> _map;

        public TObjectShortHashIterator(TObjectShortHashMap<K> map) {
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
        public short value() {
            return this._map._values[this._index];
        }

        @Override
        public short setValue(short val) {
            short old = this.value();
            this._map._values[this._index] = val;
            return old;
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    class TShortValueCollection
    implements TShortCollection {
        TShortValueCollection() {
        }

        @Override
        public TShortIterator iterator() {
            return new TObjectShortValueHashIterator();
        }

        @Override
        public short getNoEntryValue() {
            return TObjectShortHashMap.this.no_entry_value;
        }

        @Override
        public int size() {
            return TObjectShortHashMap.this._size;
        }

        @Override
        public boolean isEmpty() {
            return 0 == TObjectShortHashMap.this._size;
        }

        @Override
        public boolean contains(short entry) {
            return TObjectShortHashMap.this.containsValue(entry);
        }

        @Override
        public short[] toArray() {
            return TObjectShortHashMap.this.values();
        }

        @Override
        public short[] toArray(short[] dest) {
            return TObjectShortHashMap.this.values(dest);
        }

        @Override
        public boolean add(short entry) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean remove(short entry) {
            short[] values = TObjectShortHashMap.this._values;
            Object[] set = TObjectShortHashMap.this._set;
            int i = values.length;
            while (i-- > 0) {
                if (set[i] == TObjectHash.FREE || set[i] == TObjectHash.REMOVED || entry != values[i]) continue;
                TObjectShortHashMap.this.removeAt(i);
                return true;
            }
            return false;
        }

        @Override
        public boolean containsAll(Collection<?> collection) {
            for (Object element : collection) {
                if (element instanceof Short) {
                    short ele = (Short)element;
                    if (TObjectShortHashMap.this.containsValue(ele)) continue;
                    return false;
                }
                return false;
            }
            return true;
        }

        @Override
        public boolean containsAll(TShortCollection collection) {
            TShortIterator iter = collection.iterator();
            while (iter.hasNext()) {
                if (TObjectShortHashMap.this.containsValue(iter.next())) continue;
                return false;
            }
            return true;
        }

        @Override
        public boolean containsAll(short[] array) {
            for (short element : array) {
                if (TObjectShortHashMap.this.containsValue(element)) continue;
                return false;
            }
            return true;
        }

        @Override
        public boolean addAll(Collection<? extends Short> collection) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(TShortCollection collection) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(short[] array) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean retainAll(Collection<?> collection) {
            boolean modified = false;
            TShortIterator iter = this.iterator();
            while (iter.hasNext()) {
                if (collection.contains(iter.next())) continue;
                iter.remove();
                modified = true;
            }
            return modified;
        }

        @Override
        public boolean retainAll(TShortCollection collection) {
            if (this == collection) {
                return false;
            }
            boolean modified = false;
            TShortIterator iter = this.iterator();
            while (iter.hasNext()) {
                if (collection.contains(iter.next())) continue;
                iter.remove();
                modified = true;
            }
            return modified;
        }

        @Override
        public boolean retainAll(short[] array) {
            boolean changed = false;
            Arrays.sort(array);
            short[] values = TObjectShortHashMap.this._values;
            Object[] set = TObjectShortHashMap.this._set;
            int i = set.length;
            while (i-- > 0) {
                if (set[i] == TObjectHash.FREE || set[i] == TObjectHash.REMOVED || Arrays.binarySearch(array, values[i]) >= 0) continue;
                TObjectShortHashMap.this.removeAt(i);
                changed = true;
            }
            return changed;
        }

        @Override
        public boolean removeAll(Collection<?> collection) {
            boolean changed = false;
            for (Object element : collection) {
                short c;
                if (!(element instanceof Short) || !this.remove(c = ((Short)element).shortValue())) continue;
                changed = true;
            }
            return changed;
        }

        @Override
        public boolean removeAll(TShortCollection collection) {
            if (this == collection) {
                this.clear();
                return true;
            }
            boolean changed = false;
            TShortIterator iter = collection.iterator();
            while (iter.hasNext()) {
                short element = iter.next();
                if (!this.remove(element)) continue;
                changed = true;
            }
            return changed;
        }

        @Override
        public boolean removeAll(short[] array) {
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
            TObjectShortHashMap.this.clear();
        }

        @Override
        public boolean forEach(TShortProcedure procedure) {
            return TObjectShortHashMap.this.forEachValue(procedure);
        }

        public String toString() {
            final StringBuilder buf = new StringBuilder("{");
            TObjectShortHashMap.this.forEachValue(new TShortProcedure(){
                private boolean first = true;

                public boolean execute(short value) {
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

        class TObjectShortValueHashIterator
        implements TShortIterator {
            protected THash _hash;
            protected int _expectedSize;
            protected int _index;

            TObjectShortValueHashIterator() {
                this._hash = TObjectShortHashMap.this;
                this._expectedSize = this._hash.size();
                this._index = this._hash.capacity();
            }

            public boolean hasNext() {
                return this.nextIndex() >= 0;
            }

            public short next() {
                this.moveToNextIndex();
                return TObjectShortHashMap.this._values[this._index];
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
                    TObjectShortHashMap.this.removeAt(this._index);
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
                Object[] set = TObjectShortHashMap.this._set;
                int i = this._index;
                while (i-- > 0 && (set[i] == TObjectHash.FREE || set[i] == TObjectHash.REMOVED)) {
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
            TObjectShortHashMap.this.clear();
        }

        @Override
        public boolean add(E obj) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int size() {
            return TObjectShortHashMap.this.size();
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
            return TObjectShortHashMap.this.isEmpty();
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
    extends TObjectShortHashMap<K> {
        protected KeyView() {
        }

        @Override
        public Iterator<K> iterator() {
            return new TObjectHashIterator(TObjectShortHashMap.this);
        }

        public boolean removeElement(K key) {
            return TObjectShortHashMap.this.no_entry_value != TObjectShortHashMap.this.remove(key);
        }

        public boolean containsElement(K key) {
            return TObjectShortHashMap.this.contains(key);
        }
    }

}

