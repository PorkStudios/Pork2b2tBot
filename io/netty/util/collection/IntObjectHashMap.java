/*
 * Decompiled with CFR 0_132.
 */
package io.netty.util.collection;

import io.netty.util.collection.IntObjectMap;
import io.netty.util.internal.MathUtil;
import java.util.AbstractCollection;
import java.util.AbstractSet;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

public class IntObjectHashMap<V>
implements IntObjectMap<V> {
    public static final int DEFAULT_CAPACITY = 8;
    public static final float DEFAULT_LOAD_FACTOR = 0.5f;
    private static final Object NULL_VALUE = new Object();
    private int maxSize;
    private final float loadFactor;
    private int[] keys;
    private V[] values;
    private int size;
    private int mask;
    private final Set<Integer> keySet = new KeySet();
    private final Set<Map.Entry<Integer, V>> entrySet = new EntrySet();
    private final Iterable<IntObjectMap.PrimitiveEntry<V>> entries = new Iterable<IntObjectMap.PrimitiveEntry<V>>(){

        @Override
        public Iterator<IntObjectMap.PrimitiveEntry<V>> iterator() {
            return new PrimitiveIterator();
        }
    };

    public IntObjectHashMap() {
        this(8, 0.5f);
    }

    public IntObjectHashMap(int initialCapacity) {
        this(initialCapacity, 0.5f);
    }

    public IntObjectHashMap(int initialCapacity, float loadFactor) {
        if (loadFactor <= 0.0f || loadFactor > 1.0f) {
            throw new IllegalArgumentException("loadFactor must be > 0 and <= 1");
        }
        this.loadFactor = loadFactor;
        int capacity = MathUtil.safeFindNextPositivePowerOfTwo(initialCapacity);
        this.mask = capacity - 1;
        this.keys = new int[capacity];
        Object[] temp = new Object[capacity];
        this.values = temp;
        this.maxSize = this.calcMaxSize(capacity);
    }

    private static <T> T toExternal(T value) {
        assert (value != null);
        return value == NULL_VALUE ? null : (T)value;
    }

    private static <T> T toInternal(T value) {
        return (T)(value == null ? NULL_VALUE : value);
    }

    @Override
    public V get(int key) {
        int index = this.indexOf(key);
        return index == -1 ? null : (V)IntObjectHashMap.toExternal(this.values[index]);
    }

    @Override
    public V put(int key, V value) {
        int startIndex;
        int index = startIndex = this.hashIndex(key);
        do {
            if (this.values[index] == null) {
                this.keys[index] = key;
                this.values[index] = IntObjectHashMap.toInternal(value);
                this.growSize();
                return null;
            }
            if (this.keys[index] != key) continue;
            V previousValue = this.values[index];
            this.values[index] = IntObjectHashMap.toInternal(value);
            return IntObjectHashMap.toExternal(previousValue);
        } while ((index = this.probeNext(index)) != startIndex);
        throw new IllegalStateException("Unable to insert");
    }

    @Override
    public void putAll(Map<? extends Integer, ? extends V> sourceMap) {
        if (sourceMap instanceof IntObjectHashMap) {
            IntObjectHashMap source = (IntObjectHashMap)sourceMap;
            for (int i = 0; i < source.values.length; ++i) {
                V sourceValue = source.values[i];
                if (sourceValue == null) continue;
                this.put(source.keys[i], sourceValue);
            }
            return;
        }
        for (Map.Entry<Integer, V> entry : sourceMap.entrySet()) {
            this.put(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public V remove(int key) {
        int index = this.indexOf(key);
        if (index == -1) {
            return null;
        }
        V prev = this.values[index];
        this.removeAt(index);
        return IntObjectHashMap.toExternal(prev);
    }

    @Override
    public int size() {
        return this.size;
    }

    @Override
    public boolean isEmpty() {
        return this.size == 0;
    }

    @Override
    public void clear() {
        Arrays.fill(this.keys, 0);
        Arrays.fill(this.values, null);
        this.size = 0;
    }

    @Override
    public boolean containsKey(int key) {
        return this.indexOf(key) >= 0;
    }

    @Override
    public boolean containsValue(Object value) {
        Object v1 = IntObjectHashMap.toInternal(value);
        for (V v2 : this.values) {
            if (v2 == null || !v2.equals(v1)) continue;
            return true;
        }
        return false;
    }

    @Override
    public Iterable<IntObjectMap.PrimitiveEntry<V>> entries() {
        return this.entries;
    }

    @Override
    public Collection<V> values() {
        return new AbstractCollection<V>(){

            @Override
            public Iterator<V> iterator() {
                return new Iterator<V>(){
                    final IntObjectHashMap<V> iter;
                    {
                        this.iter = new PrimitiveIterator();
                    }

                    @Override
                    public boolean hasNext() {
                        return this.iter.hasNext();
                    }

                    @Override
                    public V next() {
                        return this.iter.next().value();
                    }

                    @Override
                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }

            @Override
            public int size() {
                return IntObjectHashMap.this.size;
            }

        };
    }

    @Override
    public int hashCode() {
        int hash = this.size;
        for (int key : this.keys) {
            hash ^= IntObjectHashMap.hashCode(key);
        }
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof IntObjectMap)) {
            return false;
        }
        IntObjectMap other = (IntObjectMap)obj;
        if (this.size != other.size()) {
            return false;
        }
        for (int i = 0; i < this.values.length; ++i) {
            V value = this.values[i];
            if (value == null) continue;
            int key = this.keys[i];
            Object otherValue = other.get(key);
            if (!(value == NULL_VALUE ? otherValue != null : !value.equals(otherValue))) continue;
            return false;
        }
        return true;
    }

    @Override
    public boolean containsKey(Object key) {
        return this.containsKey(this.objectToKey(key));
    }

    @Override
    public V get(Object key) {
        return this.get(this.objectToKey(key));
    }

    @Override
    public V put(Integer key, V value) {
        return this.put(this.objectToKey(key), value);
    }

    @Override
    public V remove(Object key) {
        return this.remove(this.objectToKey(key));
    }

    @Override
    public Set<Integer> keySet() {
        return this.keySet;
    }

    @Override
    public Set<Map.Entry<Integer, V>> entrySet() {
        return this.entrySet;
    }

    private int objectToKey(Object key) {
        return (Integer)key;
    }

    private int indexOf(int key) {
        int startIndex;
        int index = startIndex = this.hashIndex(key);
        do {
            if (this.values[index] == null) {
                return -1;
            }
            if (key != this.keys[index]) continue;
            return index;
        } while ((index = this.probeNext(index)) != startIndex);
        return -1;
    }

    private int hashIndex(int key) {
        return IntObjectHashMap.hashCode(key) & this.mask;
    }

    private static int hashCode(int key) {
        return key;
    }

    private int probeNext(int index) {
        return index + 1 & this.mask;
    }

    private void growSize() {
        ++this.size;
        if (this.size > this.maxSize) {
            if (this.keys.length == Integer.MAX_VALUE) {
                throw new IllegalStateException("Max capacity reached at size=" + this.size);
            }
            this.rehash(this.keys.length << 1);
        }
    }

    private boolean removeAt(int index) {
        --this.size;
        this.keys[index] = 0;
        this.values[index] = null;
        int nextFree = index;
        int i = this.probeNext(index);
        V value = this.values[i];
        while (value != null) {
            int key = this.keys[i];
            int bucket = this.hashIndex(key);
            if (i < bucket && (bucket <= nextFree || nextFree <= i) || bucket <= nextFree && nextFree <= i) {
                this.keys[nextFree] = key;
                this.values[nextFree] = value;
                this.keys[i] = 0;
                this.values[i] = null;
                nextFree = i;
            }
            i = this.probeNext(i);
            value = this.values[i];
        }
        return nextFree != index;
    }

    private int calcMaxSize(int capacity) {
        int upperBound = capacity - 1;
        return Math.min(upperBound, (int)((float)capacity * this.loadFactor));
    }

    private void rehash(int newCapacity) {
        int[] oldKeys = this.keys;
        V[] oldVals = this.values;
        this.keys = new int[newCapacity];
        Object[] temp = new Object[newCapacity];
        this.values = temp;
        this.maxSize = this.calcMaxSize(newCapacity);
        this.mask = newCapacity - 1;
        block0 : for (int i = 0; i < oldVals.length; ++i) {
            V oldVal = oldVals[i];
            if (oldVal == null) continue;
            int oldKey = oldKeys[i];
            int index = this.hashIndex(oldKey);
            do {
                if (this.values[index] == null) {
                    this.keys[index] = oldKey;
                    this.values[index] = oldVal;
                    continue block0;
                }
                index = this.probeNext(index);
            } while (true);
        }
    }

    public String toString() {
        if (this.isEmpty()) {
            return "{}";
        }
        StringBuilder sb = new StringBuilder(4 * this.size);
        sb.append('{');
        boolean first = true;
        for (int i = 0; i < this.values.length; ++i) {
            V value = this.values[i];
            if (value == null) continue;
            if (!first) {
                sb.append(", ");
            }
            sb.append(this.keyToString(this.keys[i])).append('=').append((Object)(value == this ? "(this Map)" : IntObjectHashMap.toExternal(value)));
            first = false;
        }
        return sb.append('}').toString();
    }

    protected String keyToString(int key) {
        return Integer.toString(key);
    }

    final class MapEntry
    implements Map.Entry<Integer, V> {
        private final int entryIndex;

        MapEntry(int entryIndex) {
            this.entryIndex = entryIndex;
        }

        @Override
        public Integer getKey() {
            this.verifyExists();
            return IntObjectHashMap.this.keys[this.entryIndex];
        }

        @Override
        public V getValue() {
            this.verifyExists();
            return (V)IntObjectHashMap.toExternal(IntObjectHashMap.this.values[this.entryIndex]);
        }

        @Override
        public V setValue(V value) {
            this.verifyExists();
            Object prevValue = IntObjectHashMap.toExternal(IntObjectHashMap.this.values[this.entryIndex]);
            IntObjectHashMap.access$600((IntObjectHashMap)IntObjectHashMap.this)[this.entryIndex] = IntObjectHashMap.toInternal(value);
            return (V)prevValue;
        }

        private void verifyExists() {
            if (IntObjectHashMap.this.values[this.entryIndex] == null) {
                throw new IllegalStateException("The map entry has been removed");
            }
        }
    }

    private final class MapIterator
    implements Iterator<Map.Entry<Integer, V>> {
        private final IntObjectHashMap<V> iter;

        private MapIterator() {
            this.iter = new PrimitiveIterator();
        }

        @Override
        public boolean hasNext() {
            return this.iter.hasNext();
        }

        @Override
        public Map.Entry<Integer, V> next() {
            if (!this.hasNext()) {
                throw new NoSuchElementException();
            }
            this.iter.next();
            return new MapEntry(this.iter.entryIndex);
        }

        @Override
        public void remove() {
            this.iter.remove();
        }
    }

    private final class PrimitiveIterator
    implements Iterator<IntObjectMap.PrimitiveEntry<V>>,
    IntObjectMap.PrimitiveEntry<V> {
        private int prevIndex = -1;
        private int nextIndex = -1;
        private int entryIndex = -1;

        private PrimitiveIterator() {
        }

        private void scanNext() {
            while (++this.nextIndex != IntObjectHashMap.this.values.length && IntObjectHashMap.this.values[this.nextIndex] == null) {
            }
        }

        @Override
        public boolean hasNext() {
            if (this.nextIndex == -1) {
                this.scanNext();
            }
            return this.nextIndex != IntObjectHashMap.this.values.length;
        }

        @Override
        public IntObjectMap.PrimitiveEntry<V> next() {
            if (!this.hasNext()) {
                throw new NoSuchElementException();
            }
            this.prevIndex = this.nextIndex;
            this.scanNext();
            this.entryIndex = this.prevIndex;
            return this;
        }

        @Override
        public void remove() {
            if (this.prevIndex == -1) {
                throw new IllegalStateException("next must be called before each remove.");
            }
            if (IntObjectHashMap.this.removeAt(this.prevIndex)) {
                this.nextIndex = this.prevIndex;
            }
            this.prevIndex = -1;
        }

        @Override
        public int key() {
            return IntObjectHashMap.this.keys[this.entryIndex];
        }

        @Override
        public V value() {
            return (V)IntObjectHashMap.toExternal(IntObjectHashMap.this.values[this.entryIndex]);
        }

        @Override
        public void setValue(V value) {
            IntObjectHashMap.access$600((IntObjectHashMap)IntObjectHashMap.this)[this.entryIndex] = IntObjectHashMap.toInternal(value);
        }
    }

    private final class KeySet
    extends AbstractSet<Integer> {
        private KeySet() {
        }

        @Override
        public int size() {
            return IntObjectHashMap.this.size();
        }

        @Override
        public boolean contains(Object o) {
            return IntObjectHashMap.this.containsKey(o);
        }

        @Override
        public boolean remove(Object o) {
            return IntObjectHashMap.this.remove(o) != null;
        }

        @Override
        public boolean retainAll(Collection<?> retainedKeys) {
            boolean changed = false;
            Iterator iter = IntObjectHashMap.this.entries().iterator();
            while (iter.hasNext()) {
                IntObjectMap.PrimitiveEntry entry = iter.next();
                if (retainedKeys.contains(entry.key())) continue;
                changed = true;
                iter.remove();
            }
            return changed;
        }

        @Override
        public void clear() {
            IntObjectHashMap.this.clear();
        }

        @Override
        public Iterator<Integer> iterator() {
            return new Iterator<Integer>(){
                private final Iterator<Map.Entry<Integer, V>> iter;
                {
                    this.iter = IntObjectHashMap.this.entrySet.iterator();
                }

                @Override
                public boolean hasNext() {
                    return this.iter.hasNext();
                }

                @Override
                public Integer next() {
                    return this.iter.next().getKey();
                }

                @Override
                public void remove() {
                    this.iter.remove();
                }
            };
        }

    }

    private final class EntrySet
    extends AbstractSet<Map.Entry<Integer, V>> {
        private EntrySet() {
        }

        @Override
        public Iterator<Map.Entry<Integer, V>> iterator() {
            return new MapIterator();
        }

        @Override
        public int size() {
            return IntObjectHashMap.this.size();
        }
    }

}

