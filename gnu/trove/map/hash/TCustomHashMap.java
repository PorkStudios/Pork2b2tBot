/*
 * Decompiled with CFR 0_132.
 * 
 * Could not load the following classes:
 *  gnu.trove.map.hash.TCustomHashMap.MapBackedView
 */
package gnu.trove.map.hash;

import gnu.trove.function.TObjectFunction;
import gnu.trove.impl.HashFunctions;
import gnu.trove.impl.hash.TCustomObjectHash;
import gnu.trove.impl.hash.TObjectHash;
import gnu.trove.iterator.hash.TObjectHashIterator;
import gnu.trove.map.TMap;
import gnu.trove.procedure.TObjectObjectProcedure;
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
import java.util.Set;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class TCustomHashMap<K, V>
extends TCustomObjectHash<K>
implements TMap<K, V>,
Externalizable {
    static final long serialVersionUID = 1L;
    protected transient V[] _values;

    public TCustomHashMap() {
    }

    public TCustomHashMap(HashingStrategy<? super K> strategy) {
        super(strategy);
    }

    public TCustomHashMap(HashingStrategy<? super K> strategy, int initialCapacity) {
        super(strategy, initialCapacity);
    }

    public TCustomHashMap(HashingStrategy<? super K> strategy, int initialCapacity, float loadFactor) {
        super(strategy, initialCapacity, loadFactor);
    }

    public TCustomHashMap(HashingStrategy<? super K> strategy, Map<? extends K, ? extends V> map) {
        this(strategy, map.size());
        this.putAll(map);
    }

    public TCustomHashMap(HashingStrategy<? super K> strategy, TCustomHashMap<? extends K, ? extends V> map) {
        this(strategy, map.size());
        this.putAll(map);
    }

    @Override
    public int setUp(int initialCapacity) {
        int capacity = super.setUp(initialCapacity);
        this._values = new Object[capacity];
        return capacity;
    }

    @Override
    public V put(K key, V value) {
        int index = this.insertKey(key);
        return this.doPut(value, index);
    }

    @Override
    public V putIfAbsent(K key, V value) {
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
    public boolean equals(Object other) {
        if (!(other instanceof Map)) {
            return false;
        }
        Map that = (Map)other;
        if (that.size() != this.size()) {
            return false;
        }
        return this.forEachEntry(new EqProcedure(that));
    }

    @Override
    public int hashCode() {
        HashProcedure p = new HashProcedure();
        this.forEachEntry(p);
        return p.getHashCode();
    }

    public String toString() {
        final StringBuilder buf = new StringBuilder("{");
        this.forEachEntry(new TObjectObjectProcedure<K, V>(){
            private boolean first = true;

            @Override
            public boolean execute(K key, V value) {
                if (this.first) {
                    this.first = false;
                } else {
                    buf.append(", ");
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

    @Override
    public boolean forEachKey(TObjectProcedure<? super K> procedure) {
        return this.forEach(procedure);
    }

    @Override
    public boolean forEachValue(TObjectProcedure<? super V> procedure) {
        V[] values = this._values;
        Object[] set = this._set;
        int i = values.length;
        while (i-- > 0) {
            if (set[i] == FREE || set[i] == REMOVED || procedure.execute(values[i])) continue;
            return false;
        }
        return true;
    }

    @Override
    public boolean forEachEntry(TObjectObjectProcedure<? super K, ? super V> procedure) {
        Object[] keys = this._set;
        V[] values = this._values;
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
    public boolean retainEntries(TObjectObjectProcedure<? super K, ? super V> procedure) {
        boolean modified;
        modified = false;
        Object[] keys = this._set;
        V[] values = this._values;
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
    public void transformValues(TObjectFunction<V, V> function) {
        V[] values = this._values;
        Object[] set = this._set;
        int i = values.length;
        while (i-- > 0) {
            if (set[i] == FREE || set[i] == REMOVED) continue;
            values[i] = function.execute(values[i]);
        }
    }

    @Override
    protected void rehash(int newCapacity) {
        int oldCapacity = this._set.length;
        int oldSize = this.size();
        Object[] oldKeys = this._set;
        V[] oldVals = this._values;
        this._set = new Object[newCapacity];
        Arrays.fill(this._set, FREE);
        this._values = new Object[newCapacity];
        int i = oldCapacity;
        while (i-- > 0) {
            Object o = oldKeys[i];
            if (o == FREE || o == REMOVED) continue;
            int index = this.insertKey(o);
            if (index < 0) {
                this.throwObjectContractViolation(this._set[- index - 1], o, this.size(), oldSize, oldKeys);
            }
            this._values[index] = oldVals[i];
        }
    }

    @Override
    public V get(Object key) {
        int index = this.index(key);
        if (index < 0 || !this.strategy.equals(this._set[index], key)) {
            return null;
        }
        return this._values[index];
    }

    @Override
    public void clear() {
        if (this.size() == 0) {
            return;
        }
        super.clear();
        Arrays.fill(this._set, 0, this._set.length, FREE);
        Arrays.fill(this._values, 0, this._values.length, null);
    }

    @Override
    public V remove(Object key) {
        V prev = null;
        int index = this.index(key);
        if (index >= 0) {
            prev = this._values[index];
            this.removeAt(index);
        }
        return prev;
    }

    @Override
    public void removeAt(int index) {
        this._values[index] = null;
        super.removeAt(index);
    }

    @Override
    public Collection<V> values() {
        return new ValueView();
    }

    @Override
    public Set<K> keySet() {
        return new KeyView();
    }

    @Override
    public Set<Map.Entry<K, V>> entrySet() {
        return new EntryView();
    }

    @Override
    public boolean containsValue(Object val) {
        Object[] set = this._set;
        V[] vals = this._values;
        if (null == val) {
            int i = vals.length;
            while (i-- > 0) {
                if (set[i] == FREE || set[i] == REMOVED || val != vals[i]) continue;
                return true;
            }
        } else {
            int i = vals.length;
            while (i-- > 0) {
                if (set[i] == FREE || set[i] == REMOVED || val != vals[i] && !this.strategy.equals(val, vals[i])) continue;
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean containsKey(Object key) {
        return this.contains(key);
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> map) {
        this.ensureCapacity(map.size());
        for (Map.Entry<K, V> e : map.entrySet()) {
            this.put(e.getKey(), e.getValue());
        }
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeByte(1);
        super.writeExternal(out);
        out.writeInt(this._size);
        int i = this._set.length;
        while (i-- > 0) {
            if (this._set[i] == REMOVED || this._set[i] == FREE) continue;
            out.writeObject(this._set[i]);
            out.writeObject(this._values[i]);
        }
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        byte version = in.readByte();
        if (version != 0) {
            super.readExternal(in);
        }
        int size = in.readInt();
        this.setUp(size);
        while (size-- > 0) {
            Object key = in.readObject();
            Object val = in.readObject();
            this.put(key, val);
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    final class Entry
    implements Map.Entry<K, V> {
        private K key;
        private V val;
        private final int index;

        Entry(K key, V value, int index) {
            this.key = key;
            this.val = value;
            this.index = index;
        }

        @Override
        public K getKey() {
            return this.key;
        }

        @Override
        public V getValue() {
            return this.val;
        }

        @Override
        public V setValue(V o) {
            if (TCustomHashMap.this._values[this.index] != this.val) {
                throw new ConcurrentModificationException();
            }
            V retval = this.val;
            TCustomHashMap.this._values[this.index] = o;
            this.val = o;
            return retval;
        }

        @Override
        public boolean equals(Object o) {
            if (o instanceof Map.Entry) {
                Entry e1 = this;
                Map.Entry e2 = (Map.Entry)o;
                return (e1.getKey() == null ? e2.getKey() == null : TCustomHashMap.this.strategy.equals(e1.getKey(), e2.getKey())) && (e1.getValue() == null ? e2.getValue() == null : e1.getValue().equals(e2.getValue()));
            }
            return false;
        }

        @Override
        public int hashCode() {
            return (this.getKey() == null ? 0 : this.getKey().hashCode()) ^ (this.getValue() == null ? 0 : this.getValue().hashCode());
        }

        public String toString() {
            return this.key + "=" + this.val;
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    protected class KeyView
    extends TCustomHashMap<K, V> {
        protected KeyView() {
            super();
        }

        public Iterator<K> iterator() {
            return new TObjectHashIterator(TCustomHashMap.this);
        }

        public boolean removeElement(K key) {
            return null != TCustomHashMap.this.remove(key);
        }

        public boolean containsElement(K key) {
            return TCustomHashMap.this.contains(key);
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
            TCustomHashMap.this.clear();
        }

        @Override
        public boolean add(E obj) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int size() {
            return TCustomHashMap.this.size();
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
            return TCustomHashMap.this.isEmpty();
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

        @Override
        public String toString() {
            Iterator<E> i = this.iterator();
            if (!i.hasNext()) {
                return "{}";
            }
            StringBuilder sb = new StringBuilder();
            sb.append('{');
            do {
                E e;
                sb.append((Object)((e = i.next()) == this ? "(this Collection)" : e));
                if (!i.hasNext()) {
                    return sb.append('}').toString();
                }
                sb.append(", ");
            } while (true);
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    protected class EntryView
    extends gnu.trove.map.hash.TCustomHashMap.MapBackedView<Map.Entry<K, V>> {
        protected EntryView() {
        }

        public Iterator<Map.Entry<K, V>> iterator() {
            return new EntryIterator(TCustomHashMap.this);
        }

        public boolean removeElement(Map.Entry<K, V> entry) {
            V val;
            K key = this.keyForEntry(entry);
            int index = TCustomHashMap.this.index(key);
            if (index >= 0 && ((val = this.valueForEntry(entry)) == TCustomHashMap.this._values[index] || null != val && TCustomHashMap.this.strategy.equals(val, TCustomHashMap.this._values[index]))) {
                TCustomHashMap.this.removeAt(index);
                return true;
            }
            return false;
        }

        public boolean containsElement(Map.Entry<K, V> entry) {
            Object val = TCustomHashMap.this.get(this.keyForEntry(entry));
            V entryValue = entry.getValue();
            return entryValue == val || null != val && TCustomHashMap.this.strategy.equals(val, entryValue);
        }

        protected V valueForEntry(Map.Entry<K, V> entry) {
            return entry.getValue();
        }

        protected K keyForEntry(Map.Entry<K, V> entry) {
            return entry.getKey();
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        private final class EntryIterator
        extends TObjectHashIterator {
            EntryIterator(TCustomHashMap<K, V> map) {
                super(map);
            }

            @Override
            public TCustomHashMap<K, V> objectAtIndex(int index) {
                return new Entry(TCustomHashMap.this._set[index], TCustomHashMap.this._values[index], index);
            }
        }

    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    protected class ValueView
    extends TCustomHashMap<K, V> {
        protected ValueView() {
        }

        public Iterator<V> iterator() {
            return new TObjectHashIterator(TCustomHashMap.this){

                @Override
                protected V objectAtIndex(int index) {
                    return TCustomHashMap.this._values[index];
                }
            };
        }

        public boolean containsElement(V value) {
            return TCustomHashMap.this.containsValue(value);
        }

        public boolean removeElement(V value) {
            V[] values = TCustomHashMap.this._values;
            Object[] set = TCustomHashMap.this._set;
            int i = values.length;
            while (i-- > 0) {
                if ((set[i] == TObjectHash.FREE || set[i] == TObjectHash.REMOVED || value != values[i]) && (null == values[i] || !TCustomHashMap.this.strategy.equals(values[i], value))) continue;
                TCustomHashMap.this.removeAt(i);
                return true;
            }
            return false;
        }

    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private static final class EqProcedure<K, V>
    implements TObjectObjectProcedure<K, V> {
        private final Map<K, V> _otherMap;

        EqProcedure(Map<K, V> otherMap) {
            this._otherMap = otherMap;
        }

        @Override
        public final boolean execute(K key, V value) {
            if (value == null && !this._otherMap.containsKey(key)) {
                return false;
            }
            V oValue = this._otherMap.get(key);
            return oValue == value || oValue != null && oValue.equals(value);
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private final class HashProcedure
    implements TObjectObjectProcedure<K, V> {
        private int h = 0;

        private HashProcedure() {
        }

        public int getHashCode() {
            return this.h;
        }

        @Override
        public final boolean execute(K key, V value) {
            this.h += HashFunctions.hash(key) ^ (value == null ? 0 : value.hashCode());
            return true;
        }
    }

}

