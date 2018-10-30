/*
 * Decompiled with CFR 0_132.
 */
package gnu.trove.map.hash;

import gnu.trove.TFloatCollection;
import gnu.trove.function.TObjectFunction;
import gnu.trove.impl.Constants;
import gnu.trove.impl.HashFunctions;
import gnu.trove.impl.hash.TFloatHash;
import gnu.trove.impl.hash.THashPrimitiveIterator;
import gnu.trove.impl.hash.TPrimitiveHash;
import gnu.trove.iterator.TFloatIterator;
import gnu.trove.iterator.TFloatObjectIterator;
import gnu.trove.map.TFloatObjectMap;
import gnu.trove.procedure.TFloatObjectProcedure;
import gnu.trove.procedure.TFloatProcedure;
import gnu.trove.procedure.TObjectProcedure;
import gnu.trove.set.TFloatSet;
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
public class TFloatObjectHashMap<V>
extends TFloatHash
implements TFloatObjectMap<V>,
Externalizable {
    static final long serialVersionUID = 1L;
    private final TFloatObjectProcedure<V> PUT_ALL_PROC = new TFloatObjectProcedure<V>(){

        @Override
        public boolean execute(float key, V value) {
            TFloatObjectHashMap.this.put(key, value);
            return true;
        }
    };
    protected transient V[] _values;
    protected float no_entry_key;

    public TFloatObjectHashMap() {
    }

    public TFloatObjectHashMap(int initialCapacity) {
        super(initialCapacity);
        this.no_entry_key = Constants.DEFAULT_FLOAT_NO_ENTRY_VALUE;
    }

    public TFloatObjectHashMap(int initialCapacity, float loadFactor) {
        super(initialCapacity, loadFactor);
        this.no_entry_key = Constants.DEFAULT_FLOAT_NO_ENTRY_VALUE;
    }

    public TFloatObjectHashMap(int initialCapacity, float loadFactor, float noEntryKey) {
        super(initialCapacity, loadFactor);
        this.no_entry_key = noEntryKey;
    }

    public TFloatObjectHashMap(TFloatObjectMap<? extends V> map) {
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
        float[] oldKeys = this._set;
        V[] oldVals = this._values;
        byte[] oldStates = this._states;
        this._set = new float[newCapacity];
        this._values = new Object[newCapacity];
        this._states = new byte[newCapacity];
        int i = oldCapacity;
        while (i-- > 0) {
            if (oldStates[i] != 1) continue;
            float o = oldKeys[i];
            int index = this.insertKey(o);
            this._values[index] = oldVals[i];
        }
    }

    @Override
    public float getNoEntryKey() {
        return this.no_entry_key;
    }

    @Override
    public boolean containsKey(float key) {
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
    public V get(float key) {
        int index = this.index(key);
        return index < 0 ? null : (V)this._values[index];
    }

    @Override
    public V put(float key, V value) {
        int index = this.insertKey(key);
        return this.doPut(value, index);
    }

    @Override
    public V putIfAbsent(float key, V value) {
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
    public V remove(float key) {
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
    public void putAll(Map<? extends Float, ? extends V> map) {
        Set<Map.Entry<Float, V>> set = map.entrySet();
        for (Map.Entry<Float, V> entry : set) {
            this.put(entry.getKey().floatValue(), entry.getValue());
        }
    }

    @Override
    public void putAll(TFloatObjectMap<? extends V> map) {
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
    public TFloatSet keySet() {
        return new KeyView();
    }

    @Override
    public float[] keys() {
        float[] keys = new float[this.size()];
        float[] k = this._set;
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
    public float[] keys(float[] dest) {
        if (dest.length < this._size) {
            dest = new float[this._size];
        }
        float[] k = this._set;
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
    public TFloatObjectIterator<V> iterator() {
        return new TFloatObjectHashIterator(this);
    }

    @Override
    public boolean forEachKey(TFloatProcedure procedure) {
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
    public boolean forEachEntry(TFloatObjectProcedure<? super V> procedure) {
        byte[] states = this._states;
        float[] keys = this._set;
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
    public boolean retainEntries(TFloatObjectProcedure<? super V> procedure) {
        boolean modified;
        modified = false;
        byte[] states = this._states;
        float[] keys = this._set;
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
        if (!(other instanceof TFloatObjectMap)) {
            return false;
        }
        TFloatObjectMap that = (TFloatObjectMap)other;
        if (that.size() != this.size()) {
            return false;
        }
        try {
            TFloatObjectIterator<V> iter = this.iterator();
            while (iter.hasNext()) {
                iter.advance();
                float key = iter.key();
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
        out.writeFloat(this.no_entry_key);
        out.writeInt(this._size);
        int i = this._states.length;
        while (i-- > 0) {
            if (this._states[i] != 1) continue;
            out.writeFloat(this._set[i]);
            out.writeObject(this._values[i]);
        }
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        in.readByte();
        super.readExternal(in);
        this.no_entry_key = in.readFloat();
        int size = in.readInt();
        this.setUp(size);
        while (size-- > 0) {
            float key = in.readFloat();
            Object val = in.readObject();
            this.put(key, val);
        }
    }

    public String toString() {
        final StringBuilder buf = new StringBuilder("{");
        this.forEachEntry(new TFloatObjectProcedure<V>(){
            private boolean first = true;

            @Override
            public boolean execute(float key, Object value) {
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
    class TFloatObjectHashIterator<V>
    extends THashPrimitiveIterator
    implements TFloatObjectIterator<V> {
        private final TFloatObjectHashMap<V> _map;

        public TFloatObjectHashIterator(TFloatObjectHashMap<V> map) {
            super(map);
            this._map = map;
        }

        @Override
        public void advance() {
            this.moveToNextIndex();
        }

        @Override
        public float key() {
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
            TFloatObjectHashMap.this.clear();
        }

        @Override
        public boolean add(E obj) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int size() {
            return TFloatObjectHashMap.this.size();
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
            return TFloatObjectHashMap.this.isEmpty();
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
    extends TFloatObjectHashMap<V> {
        protected ValueView() {
        }

        @Override
        public Iterator<V> iterator() {
            return new TFloatObjectValueHashIterator(TFloatObjectHashMap.this){

                @Override
                protected V objectAtIndex(int index) {
                    return TFloatObjectHashMap.this._values[index];
                }
            };
        }

        public boolean containsElement(V value) {
            return TFloatObjectHashMap.this.containsValue(value);
        }

        public boolean removeElement(V value) {
            V[] values = TFloatObjectHashMap.this._values;
            byte[] states = TFloatObjectHashMap.this._states;
            int i = values.length;
            while (i-- > 0) {
                if (states[i] != 1 || value != values[i] && (null == values[i] || !values[i].equals(value))) continue;
                TFloatObjectHashMap.this.removeAt(i);
                return true;
            }
            return false;
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        class TFloatObjectValueHashIterator
        extends THashPrimitiveIterator
        implements Iterator<V> {
            protected final TFloatObjectHashMap _map;

            public TFloatObjectValueHashIterator(TFloatObjectHashMap map) {
                super(map);
                this._map = map;
            }

            protected V objectAtIndex(int index) {
                byte[] states = TFloatObjectHashMap.this._states;
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
    implements TFloatSet {
        KeyView() {
        }

        @Override
        public float getNoEntryValue() {
            return TFloatObjectHashMap.this.no_entry_key;
        }

        @Override
        public int size() {
            return TFloatObjectHashMap.this._size;
        }

        @Override
        public boolean isEmpty() {
            return TFloatObjectHashMap.this._size == 0;
        }

        @Override
        public boolean contains(float entry) {
            return TFloatObjectHashMap.this.containsKey(entry);
        }

        @Override
        public TFloatIterator iterator() {
            return new TFloatHashIterator(TFloatObjectHashMap.this);
        }

        @Override
        public float[] toArray() {
            return TFloatObjectHashMap.this.keys();
        }

        @Override
        public float[] toArray(float[] dest) {
            return TFloatObjectHashMap.this.keys(dest);
        }

        @Override
        public boolean add(float entry) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean remove(float entry) {
            return null != TFloatObjectHashMap.this.remove(entry);
        }

        @Override
        public boolean containsAll(Collection<?> collection) {
            for (Object element : collection) {
                if (TFloatObjectHashMap.this.containsKey(((Float)element).floatValue())) continue;
                return false;
            }
            return true;
        }

        @Override
        public boolean containsAll(TFloatCollection collection) {
            if (collection == this) {
                return true;
            }
            TFloatIterator iter = collection.iterator();
            while (iter.hasNext()) {
                if (TFloatObjectHashMap.this.containsKey(iter.next())) continue;
                return false;
            }
            return true;
        }

        @Override
        public boolean containsAll(float[] array) {
            for (float element : array) {
                if (TFloatObjectHashMap.this.containsKey(element)) continue;
                return false;
            }
            return true;
        }

        @Override
        public boolean addAll(Collection<? extends Float> collection) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(TFloatCollection collection) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(float[] array) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean retainAll(Collection<?> collection) {
            boolean modified = false;
            TFloatIterator iter = this.iterator();
            while (iter.hasNext()) {
                if (collection.contains(Float.valueOf(iter.next()))) continue;
                iter.remove();
                modified = true;
            }
            return modified;
        }

        @Override
        public boolean retainAll(TFloatCollection collection) {
            if (this == collection) {
                return false;
            }
            boolean modified = false;
            TFloatIterator iter = this.iterator();
            while (iter.hasNext()) {
                if (collection.contains(iter.next())) continue;
                iter.remove();
                modified = true;
            }
            return modified;
        }

        @Override
        public boolean retainAll(float[] array) {
            boolean changed = false;
            Arrays.sort(array);
            float[] set = TFloatObjectHashMap.this._set;
            byte[] states = TFloatObjectHashMap.this._states;
            int i = set.length;
            while (i-- > 0) {
                if (states[i] != 1 || Arrays.binarySearch(array, set[i]) >= 0) continue;
                TFloatObjectHashMap.this.removeAt(i);
                changed = true;
            }
            return changed;
        }

        @Override
        public boolean removeAll(Collection<?> collection) {
            boolean changed = false;
            for (Object element : collection) {
                float c;
                if (!(element instanceof Float) || !this.remove(c = ((Float)element).floatValue())) continue;
                changed = true;
            }
            return changed;
        }

        @Override
        public boolean removeAll(TFloatCollection collection) {
            if (collection == this) {
                this.clear();
                return true;
            }
            boolean changed = false;
            TFloatIterator iter = collection.iterator();
            while (iter.hasNext()) {
                float element = iter.next();
                if (!this.remove(element)) continue;
                changed = true;
            }
            return changed;
        }

        @Override
        public boolean removeAll(float[] array) {
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
            TFloatObjectHashMap.this.clear();
        }

        @Override
        public boolean forEach(TFloatProcedure procedure) {
            return TFloatObjectHashMap.this.forEachKey(procedure);
        }

        @Override
        public boolean equals(Object other) {
            if (!(other instanceof TFloatSet)) {
                return false;
            }
            TFloatSet that = (TFloatSet)other;
            if (that.size() != this.size()) {
                return false;
            }
            int i = TFloatObjectHashMap.this._states.length;
            while (i-- > 0) {
                if (TFloatObjectHashMap.this._states[i] != 1 || that.contains(TFloatObjectHashMap.this._set[i])) continue;
                return false;
            }
            return true;
        }

        @Override
        public int hashCode() {
            int hashcode = 0;
            int i = TFloatObjectHashMap.this._states.length;
            while (i-- > 0) {
                if (TFloatObjectHashMap.this._states[i] != 1) continue;
                hashcode += HashFunctions.hash(TFloatObjectHashMap.this._set[i]);
            }
            return hashcode;
        }

        public String toString() {
            StringBuilder buf = new StringBuilder("{");
            boolean first = true;
            int i = TFloatObjectHashMap.this._states.length;
            while (i-- > 0) {
                if (TFloatObjectHashMap.this._states[i] != 1) continue;
                if (first) {
                    first = false;
                } else {
                    buf.append(",");
                }
                buf.append(TFloatObjectHashMap.this._set[i]);
            }
            return buf.toString();
        }

        class TFloatHashIterator
        extends THashPrimitiveIterator
        implements TFloatIterator {
            private final TFloatHash _hash;

            public TFloatHashIterator(TFloatHash hash) {
                super(hash);
                this._hash = hash;
            }

            public float next() {
                this.moveToNextIndex();
                return this._hash._set[this._index];
            }
        }

    }

}

