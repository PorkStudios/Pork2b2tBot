/*
 * Decompiled with CFR 0_132.
 */
package gnu.trove.map.hash;

import gnu.trove.TLongCollection;
import gnu.trove.function.TObjectFunction;
import gnu.trove.impl.Constants;
import gnu.trove.impl.HashFunctions;
import gnu.trove.impl.hash.THashPrimitiveIterator;
import gnu.trove.impl.hash.TLongHash;
import gnu.trove.impl.hash.TPrimitiveHash;
import gnu.trove.iterator.TLongIterator;
import gnu.trove.iterator.TLongObjectIterator;
import gnu.trove.map.TLongObjectMap;
import gnu.trove.procedure.TLongObjectProcedure;
import gnu.trove.procedure.TLongProcedure;
import gnu.trove.procedure.TObjectProcedure;
import gnu.trove.set.TLongSet;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.lang.reflect.Array;
import java.util.AbstractSet;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class TLongObjectHashMap<V>
extends TLongHash
implements TLongObjectMap<V>,
Externalizable {
    static final long serialVersionUID = 1L;
    private final TLongObjectProcedure<V> PUT_ALL_PROC = new TLongObjectProcedure<V>(){

        @Override
        public boolean execute(long key, V value) {
            TLongObjectHashMap.this.put(key, value);
            return true;
        }
    };
    protected transient V[] _values;
    protected long no_entry_key;

    public TLongObjectHashMap() {
    }

    public TLongObjectHashMap(int initialCapacity) {
        super(initialCapacity);
        this.no_entry_key = Constants.DEFAULT_LONG_NO_ENTRY_VALUE;
    }

    public TLongObjectHashMap(int initialCapacity, float loadFactor) {
        super(initialCapacity, loadFactor);
        this.no_entry_key = Constants.DEFAULT_LONG_NO_ENTRY_VALUE;
    }

    public TLongObjectHashMap(int initialCapacity, float loadFactor, long noEntryKey) {
        super(initialCapacity, loadFactor);
        this.no_entry_key = noEntryKey;
    }

    public TLongObjectHashMap(TLongObjectMap<? extends V> map) {
        this(map.size(), 0.5f, map.getNoEntryKey());
        this.putAll(map);
    }

    @Override
    protected int setUp(int initialCapacity) {
        int capacity = super.setUp(initialCapacity);
        this._values = new Object[capacity];
        return capacity;
    }

    @Override
    protected void rehash(int newCapacity) {
        int oldCapacity = this._set.length;
        long[] oldKeys = this._set;
        V[] oldVals = this._values;
        byte[] oldStates = this._states;
        this._set = new long[newCapacity];
        this._values = new Object[newCapacity];
        this._states = new byte[newCapacity];
        int i = oldCapacity;
        while (i-- > 0) {
            if (oldStates[i] != 1) continue;
            long o = oldKeys[i];
            int index = this.insertKey(o);
            this._values[index] = oldVals[i];
        }
    }

    @Override
    public long getNoEntryKey() {
        return this.no_entry_key;
    }

    @Override
    public boolean containsKey(long key) {
        return this.contains(key);
    }

    @Override
    public boolean containsValue(Object val) {
        byte[] states = this._states;
        V[] vals = this._values;
        if (null == val) {
            int i = vals.length;
            while (i-- > 0) {
                if (states[i] != 1 || null != vals[i]) continue;
                return true;
            }
        } else {
            int i = vals.length;
            while (i-- > 0) {
                if (states[i] != 1 || val != vals[i] && !val.equals(vals[i])) continue;
                return true;
            }
        }
        return false;
    }

    @Override
    public V get(long key) {
        int index = this.index(key);
        return index < 0 ? null : (V)this._values[index];
    }

    @Override
    public V put(long key, V value) {
        int index = this.insertKey(key);
        return this.doPut(value, index);
    }

    @Override
    public V putIfAbsent(long key, V value) {
        int index = this.insertKey(key);
        if (index < 0) {
            return this._values[- index - 1];
        }
        return this.doPut(value, index);
    }

    private V doPut(V value, int index) {
        V previous = null;
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
    public V remove(long key) {
        V prev = null;
        int index = this.index(key);
        if (index >= 0) {
            prev = this._values[index];
            this.removeAt(index);
        }
        return prev;
    }

    @Override
    protected void removeAt(int index) {
        this._values[index] = null;
        super.removeAt(index);
    }

    @Override
    public void putAll(Map<? extends Long, ? extends V> map) {
        Set<Map.Entry<Long, V>> set = map.entrySet();
        for (Map.Entry<Long, V> entry : set) {
            this.put(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public void putAll(TLongObjectMap<? extends V> map) {
        map.forEachEntry(this.PUT_ALL_PROC);
    }

    @Override
    public void clear() {
        super.clear();
        Arrays.fill(this._set, 0, this._set.length, this.no_entry_key);
        Arrays.fill(this._states, 0, this._states.length, (byte)0);
        Arrays.fill(this._values, 0, this._values.length, null);
    }

    @Override
    public TLongSet keySet() {
        return new KeyView();
    }

    @Override
    public long[] keys() {
        long[] keys = new long[this.size()];
        long[] k = this._set;
        byte[] states = this._states;
        int i = k.length;
        int j = 0;
        while (i-- > 0) {
            if (states[i] != 1) continue;
            keys[j++] = k[i];
        }
        return keys;
    }

    @Override
    public long[] keys(long[] dest) {
        if (dest.length < this._size) {
            dest = new long[this._size];
        }
        long[] k = this._set;
        byte[] states = this._states;
        int i = k.length;
        int j = 0;
        while (i-- > 0) {
            if (states[i] != 1) continue;
            dest[j++] = k[i];
        }
        return dest;
    }

    @Override
    public Collection<V> valueCollection() {
        return new ValueView();
    }

    @Override
    public Object[] values() {
        Object[] vals = new Object[this.size()];
        V[] v = this._values;
        byte[] states = this._states;
        int i = v.length;
        int j = 0;
        while (i-- > 0) {
            if (states[i] != 1) continue;
            vals[j++] = v[i];
        }
        return vals;
    }

    @Override
    public V[] values(V[] dest) {
        if (dest.length < this._size) {
            dest = (Object[])Array.newInstance(dest.getClass().getComponentType(), this._size);
        }
        V[] v = this._values;
        byte[] states = this._states;
        int i = v.length;
        int j = 0;
        while (i-- > 0) {
            if (states[i] != 1) continue;
            dest[j++] = v[i];
        }
        return dest;
    }

    @Override
    public TLongObjectIterator<V> iterator() {
        return new TLongObjectHashIterator(this);
    }

    @Override
    public boolean forEachKey(TLongProcedure procedure) {
        return this.forEach(procedure);
    }

    @Override
    public boolean forEachValue(TObjectProcedure<? super V> procedure) {
        byte[] states = this._states;
        V[] values = this._values;
        int i = values.length;
        while (i-- > 0) {
            if (states[i] != 1 || procedure.execute(values[i])) continue;
            return false;
        }
        return true;
    }

    @Override
    public boolean forEachEntry(TLongObjectProcedure<? super V> procedure) {
        byte[] states = this._states;
        long[] keys = this._set;
        V[] values = this._values;
        int i = keys.length;
        while (i-- > 0) {
            if (states[i] != 1 || procedure.execute(keys[i], values[i])) continue;
            return false;
        }
        return true;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean retainEntries(TLongObjectProcedure<? super V> procedure) {
        boolean modified;
        modified = false;
        byte[] states = this._states;
        long[] keys = this._set;
        V[] values = this._values;
        this.tempDisableAutoCompaction();
        try {
            int i = keys.length;
            while (i-- > 0) {
                if (states[i] != 1 || procedure.execute(keys[i], values[i])) continue;
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
    public void transformValues(TObjectFunction<V, V> function) {
        byte[] states = this._states;
        V[] values = this._values;
        int i = values.length;
        while (i-- > 0) {
            if (states[i] != 1) continue;
            values[i] = function.execute(values[i]);
        }
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof TLongObjectMap)) {
            return false;
        }
        TLongObjectMap that = (TLongObjectMap)other;
        if (that.size() != this.size()) {
            return false;
        }
        try {
            TLongObjectIterator<V> iter = this.iterator();
            while (iter.hasNext()) {
                iter.advance();
                long key = iter.key();
                V value = iter.value();
                if (!(value == null ? that.get(key) != null || !that.containsKey(key) : !value.equals(that.get(key)))) continue;
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
        V[] values = this._values;
        byte[] states = this._states;
        int i = values.length;
        while (i-- > 0) {
            if (states[i] != 1) continue;
            hashcode += HashFunctions.hash(this._set[i]) ^ (values[i] == null ? 0 : values[i].hashCode());
        }
        return hashcode;
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeByte(0);
        super.writeExternal(out);
        out.writeLong(this.no_entry_key);
        out.writeInt(this._size);
        int i = this._states.length;
        while (i-- > 0) {
            if (this._states[i] != 1) continue;
            out.writeLong(this._set[i]);
            out.writeObject(this._values[i]);
        }
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        in.readByte();
        super.readExternal(in);
        this.no_entry_key = in.readLong();
        int size = in.readInt();
        this.setUp(size);
        while (size-- > 0) {
            long key = in.readLong();
            Object val = in.readObject();
            this.put(key, val);
        }
    }

    public String toString() {
        final StringBuilder buf = new StringBuilder("{");
        this.forEachEntry(new TLongObjectProcedure<V>(){
            private boolean first = true;

            @Override
            public boolean execute(long key, Object value) {
                if (this.first) {
                    this.first = false;
                } else {
                    buf.append(",");
                }
                buf.append(key);
                buf.append("=");
                buf.append(value);
                return true;
            }
        });
        buf.append("}");
        return buf.toString();
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    class TLongObjectHashIterator<V>
    extends THashPrimitiveIterator
    implements TLongObjectIterator<V> {
        private final TLongObjectHashMap<V> _map;

        public TLongObjectHashIterator(TLongObjectHashMap<V> map) {
            super(map);
            this._map = map;
        }

        @Override
        public void advance() {
            this.moveToNextIndex();
        }

        @Override
        public long key() {
            return this._map._set[this._index];
        }

        @Override
        public V value() {
            return this._map._values[this._index];
        }

        @Override
        public V setValue(V val) {
            V old = this.value();
            this._map._values[this._index] = val;
            return old;
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

        @Override
        public abstract Iterator<E> iterator();

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
            TLongObjectHashMap.this.clear();
        }

        @Override
        public boolean add(E obj) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int size() {
            return TLongObjectHashMap.this.size();
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
            return TLongObjectHashMap.this.isEmpty();
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
    protected class ValueView
    extends TLongObjectHashMap<V> {
        protected ValueView() {
        }

        @Override
        public Iterator<V> iterator() {
            return new TLongObjectValueHashIterator(TLongObjectHashMap.this){

                @Override
                protected V objectAtIndex(int index) {
                    return TLongObjectHashMap.this._values[index];
                }
            };
        }

        public boolean containsElement(V value) {
            return TLongObjectHashMap.this.containsValue(value);
        }

        public boolean removeElement(V value) {
            V[] values = TLongObjectHashMap.this._values;
            byte[] states = TLongObjectHashMap.this._states;
            int i = values.length;
            while (i-- > 0) {
                if (states[i] != 1 || value != values[i] && (null == values[i] || !values[i].equals(value))) continue;
                TLongObjectHashMap.this.removeAt(i);
                return true;
            }
            return false;
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        class TLongObjectValueHashIterator
        extends THashPrimitiveIterator
        implements Iterator<V> {
            protected final TLongObjectHashMap _map;

            public TLongObjectValueHashIterator(TLongObjectHashMap map) {
                super(map);
                this._map = map;
            }

            protected V objectAtIndex(int index) {
                byte[] states = TLongObjectHashMap.this._states;
                Object value = this._map._values[index];
                if (states[index] != 1) {
                    return null;
                }
                return value;
            }

            @Override
            public V next() {
                this.moveToNextIndex();
                return this._map._values[this._index];
            }
        }

    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    class KeyView
    implements TLongSet {
        KeyView() {
        }

        @Override
        public long getNoEntryValue() {
            return TLongObjectHashMap.this.no_entry_key;
        }

        @Override
        public int size() {
            return TLongObjectHashMap.this._size;
        }

        @Override
        public boolean isEmpty() {
            return TLongObjectHashMap.this._size == 0;
        }

        @Override
        public boolean contains(long entry) {
            return TLongObjectHashMap.this.containsKey(entry);
        }

        @Override
        public TLongIterator iterator() {
            return new TLongHashIterator(TLongObjectHashMap.this);
        }

        @Override
        public long[] toArray() {
            return TLongObjectHashMap.this.keys();
        }

        @Override
        public long[] toArray(long[] dest) {
            return TLongObjectHashMap.this.keys(dest);
        }

        @Override
        public boolean add(long entry) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean remove(long entry) {
            return null != TLongObjectHashMap.this.remove(entry);
        }

        @Override
        public boolean containsAll(Collection<?> collection) {
            for (Object element : collection) {
                if (TLongObjectHashMap.this.containsKey((Long)element)) continue;
                return false;
            }
            return true;
        }

        @Override
        public boolean containsAll(TLongCollection collection) {
            if (collection == this) {
                return true;
            }
            TLongIterator iter = collection.iterator();
            while (iter.hasNext()) {
                if (TLongObjectHashMap.this.containsKey(iter.next())) continue;
                return false;
            }
            return true;
        }

        @Override
        public boolean containsAll(long[] array) {
            for (long element : array) {
                if (TLongObjectHashMap.this.containsKey(element)) continue;
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
            long[] set = TLongObjectHashMap.this._set;
            byte[] states = TLongObjectHashMap.this._states;
            int i = set.length;
            while (i-- > 0) {
                if (states[i] != 1 || Arrays.binarySearch(array, set[i]) >= 0) continue;
                TLongObjectHashMap.this.removeAt(i);
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
            if (collection == this) {
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
            TLongObjectHashMap.this.clear();
        }

        @Override
        public boolean forEach(TLongProcedure procedure) {
            return TLongObjectHashMap.this.forEachKey(procedure);
        }

        @Override
        public boolean equals(Object other) {
            if (!(other instanceof TLongSet)) {
                return false;
            }
            TLongSet that = (TLongSet)other;
            if (that.size() != this.size()) {
                return false;
            }
            int i = TLongObjectHashMap.this._states.length;
            while (i-- > 0) {
                if (TLongObjectHashMap.this._states[i] != 1 || that.contains(TLongObjectHashMap.this._set[i])) continue;
                return false;
            }
            return true;
        }

        @Override
        public int hashCode() {
            int hashcode = 0;
            int i = TLongObjectHashMap.this._states.length;
            while (i-- > 0) {
                if (TLongObjectHashMap.this._states[i] != 1) continue;
                hashcode += HashFunctions.hash(TLongObjectHashMap.this._set[i]);
            }
            return hashcode;
        }

        public String toString() {
            StringBuilder buf = new StringBuilder("{");
            boolean first = true;
            int i = TLongObjectHashMap.this._states.length;
            while (i-- > 0) {
                if (TLongObjectHashMap.this._states[i] != 1) continue;
                if (first) {
                    first = false;
                } else {
                    buf.append(",");
                }
                buf.append(TLongObjectHashMap.this._set[i]);
            }
            return buf.toString();
        }

        class TLongHashIterator
        extends THashPrimitiveIterator
        implements TLongIterator {
            private final TLongHash _hash;

            public TLongHashIterator(TLongHash hash) {
                super(hash);
                this._hash = hash;
            }

            public long next() {
                this.moveToNextIndex();
                return this._hash._set[this._index];
            }
        }

    }

}

