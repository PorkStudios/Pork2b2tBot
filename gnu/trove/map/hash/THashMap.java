/*
 * Decompiled with CFR 0_132.
 * 
 * Could not load the following classes:
 *  gnu.trove.map.hash.THashMap.MapBackedView
 */
package gnu.trove.map.hash;

import gnu.trove.function.TObjectFunction;
import gnu.trove.impl.HashFunctions;
import gnu.trove.impl.hash.TObjectHash;
import gnu.trove.iterator.hash.TObjectHashIterator;
import gnu.trove.map.TMap;
import gnu.trove.procedure.TObjectObjectProcedure;
import gnu.trove.procedure.TObjectProcedure;
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
public class THashMap<K, V>
extends TObjectHash<K>
implements TMap<K, V>,
Externalizable {
    static final long serialVersionUID = 1L;
    protected transient V[] _values;

    public THashMap() {
    }

    public THashMap(int initialCapacity) {
        super(initialCapacity);
    }

    public THashMap(int initialCapacity, float loadFactor) {
        super(initialCapacity, loadFactor);
    }

    public THashMap(Map<? extends K, ? extends V> map) {
        this(map.size());
        this.putAll(map);
    }

    public THashMap(THashMap<? extends K, ? extends V> map) {
        this(map.size());
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
        int count = 0;
        int i = oldCapacity;
        while (i-- > 0) {
            Object o = oldKeys[i];
            if (o == FREE || o == REMOVED) continue;
            int index = this.insertKey(o);
            if (index < 0) {
                this.throwObjectContractViolation(this._set[- index - 1], o, this.size(), oldSize, oldKeys);
            }
            this._values[index] = oldVals[i];
            ++count;
        }
        THashMap.reportPotentialConcurrentMod(this.size(), oldSize);
    }

    @Override
    public V get(Object key) {
        int index = this.index(key);
        return index < 0 ? null : (V)this._values[index];
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
                if (set[i] == FREE || set[i] == REMOVED || val != vals[i] && !this.equals(val, vals[i])) continue;
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
            if (THashMap.this._values[this.index] != this.val) {
                throw new ConcurrentModificationException();
            }
            V retval = this.val;
            THashMap.this._values[this.index] = o;
            this.val = o;
            return retval;
        }

        @Override
        public boolean equals(Object o) {
            if (o instanceof Map.Entry) {
                Entry e1 = this;
                Map.Entry e2 = (Map.Entry)o;
                return THashMap.this.equals(e1.getKey(), e2.getKey()) && THashMap.this.equals(e1.getValue(), e1.getValue());
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
    extends THashMap<K, V> {
        protected KeyView() {
            super();
        }

        public Iterator<K> iterator() {
            return new TObjectHashIterator(THashMap.this);
        }

        public boolean removeElement(K key) {
            return null != THashMap.this.remove(key);
        }

        public boolean containsElement(K key) {
            return THashMap.this.contains(key);
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
            try {
                return this.removeElement(o);
            }
            catch (ClassCastException ex) {
                return false;
            }
        }

        @Override
        public void clear() {
            THashMap.this.clear();
        }

        @Override
        public boolean add(E obj) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int size() {
            return THashMap.this.size();
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
            return THashMap.this.isEmpty();
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
    extends gnu.trove.map.hash.THashMap.MapBackedView<Map.Entry<K, V>> {
        protected EntryView() {
        }

        public Iterator<Map.Entry<K, V>> iterator() {
            return new EntryIterator(THashMap.this);
        }

        public boolean removeElement(Map.Entry<K, V> entry) {
            V val;
            if (entry == null) {
                return false;
            }
            K key = this.keyForEntry(entry);
            int index = THashMap.this.index(key);
            if (index >= 0 && ((val = this.valueForEntry(entry)) == THashMap.this._values[index] || null != val && THashMap.this.equals(val, THashMap.this._values[index]))) {
                THashMap.this.removeAt(index);
                return true;
            }
            return false;
        }

        public boolean containsElement(Map.Entry<K, V> entry) {
            Object val = THashMap.this.get(this.keyForEntry(entry));
            V entryValue = entry.getValue();
            return entryValue == val || null != val && THashMap.this.equals(val, entryValue);
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
            EntryIterator(THashMap<K, V> map) {
                super(map);
            }

            @Override
            public THashMap<K, V> objectAtIndex(int index) {
                return new Entry(THashMap.this._set[index], THashMap.this._values[index], index);
            }
        }

    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    protected class ValueView
    extends THashMap<K, V> {
        protected ValueView() {
        }

        public Iterator<V> iterator() {
            return new TObjectHashIterator(THashMap.this){

                @Override
                protected V objectAtIndex(int index) {
                    return THashMap.this._values[index];
                }
            };
        }

        public boolean containsElement(V value) {
            return THashMap.this.containsValue(value);
        }

        public boolean removeElement(V value) {
            V[] values = THashMap.this._values;
            Object[] set = THashMap.this._set;
            int i = values.length;
            while (i-- > 0) {
                if ((set[i] == TObjectHash.FREE || set[i] == TObjectHash.REMOVED || value != values[i]) && (null == values[i] || !THashMap.this.equals(values[i], value))) continue;
                THashMap.this.removeAt(i);
                return true;
            }
            return false;
        }

    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private final class EqProcedure<K, V>
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
            return oValue == value || oValue != null && THashMap.this.equals(oValue, value);
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

