/*
 * Decompiled with CFR 0_132.
 */
package io.netty.util.collection;

import io.netty.util.collection.LongObjectMap;
import io.netty.util.internal.MathUtil;
import java.util.AbstractCollection;
import java.util.AbstractSet;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

public class LongObjectHashMap<V>
implements LongObjectMap<V> {
    public static final int DEFAULT_CAPACITY = 8;
    public static final float DEFAULT_LOAD_FACTOR = 0.5f;
    private static final Object NULL_VALUE = new Object();
    private int maxSize;
    private final float loadFactor;
    private long[] keys;
    private V[] values;
    private int size;
    private int mask;
    private final Set<Long> keySet = new KeySet();
    private final Set<Map.Entry<Long, V>> entrySet = new EntrySet();
    private final Iterable<LongObjectMap.PrimitiveEntry<V>> entries = new Iterable<LongObjectMap.PrimitiveEntry<V>>(){

        @Override
        public Iterator<LongObjectMap.PrimitiveEntry<V>> iterator() {
            return new PrimitiveIterator();
        }
    };

    public LongObjectHashMap() {
        this(8, 0.5f);
    }

    public LongObjectHashMap(int initialCapacity) {
        this(initialCapacity, 0.5f);
    }

    public LongObjectHashMap(int initialCapacity, float loadFactor) {
        if (loadFactor <= 0.0f || loadFactor > 1.0f) {
            throw new IllegalArgumentException("loadFactor must be > 0 and <= 1");
        }
        this.loadFactor = loadFactor;
        int capacity = MathUtil.safeFindNextPositivePowerOfTwo(initialCapacity);
        this.mask = capacity - 1;
        this.keys = new long[capacity];
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
    public V get(long key) {
        int index = this.indexOf(key);
        return index == -1 ? null : (V)LongObjectHashMap.toExternal(this.values[index]);
    }

    @Override
    public V put(long key, V value) {
        int startIndex;
        int index = startIndex = this.hashIndex(key);
        do {
            if (this.values[index] == null) {
                this.keys[index] = key;
                this.values[index] = LongObjectHashMap.toInternal(value);
                this.growSize();
                return null;
            }
            if (this.keys[index] != key) continue;
            V previousValue = this.values[index];
            this.values[index] = LongObjectHashMap.toInternal(value);
            return LongObjectHashMap.toExternal(previousValue);
        } while ((index = this.probeNext(index)) != startIndex);
        throw new IllegalStateException("Unable to insert");
    }

    @Override
    public void putAll(Map<? extends Long, ? extends V> sourceMap) {
        if (sourceMap instanceof LongObjectHashMap) {
            LongObjectHashMap source = (LongObjectHashMap)sourceMap;
            for (int i = 0; i < source.values.length; ++i) {
                V sourceValue = source.values[i];
                if (sourceValue == null) continue;
                this.put(source.keys[i], sourceValue);
            }
            return;
        }
        for (Map.Entry<Long, V> entry : sourceMap.entrySet()) {
            this.put(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public V remove(long key) {
        int index = this.indexOf(key);
        if (index == -1) {
            return null;
        }
        V prev = this.values[index];
        this.removeAt(index);
        return LongObjectHashMap.toExternal(prev);
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
        Arrays.fill(this.keys, 0L);
        Arrays.fill(this.values, null);
        this.size = 0;
    }

    @Override
    public boolean containsKey(long key) {
        return this.indexOf(key) >= 0;
    }

    @Override
    public boolean containsValue(Object value) {
        Object v1 = LongObjectHashMap.toInternal(value);
        for (V v2 : this.values) {
            if (v2 == null || !v2.equals(v1)) continue;
            return true;
        }
        return false;
    }

    @Override
    public Iterable<LongObjectMap.PrimitiveEntry<V>> entries() {
        return this.entries;
    }

    @Override
    public Collection<V> values() {
        return new AbstractCollection<V>(){

            @Override
            public Iterator<V> iterator() {
                return new Iterator<V>(){
                    final LongObjectHashMap<V> iter;
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
                return LongObjectHashMap.this.size;
            }

        };
    }

    @Override
    public int hashCode() {
        int hash = this.size;
        for (long key : this.keys) {
            hash ^= LongObjectHashMap.hashCode(key);
        }
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof LongObjectMap)) {
            return false;
        }
        LongObjectMap other = (LongObjectMap)obj;
        if (this.size != other.size()) {
            return false;
        }
        for (int i = 0; i < this.values.length; ++i) {
            V value = this.values[i];
            if (value == null) continue;
            long key = this.keys[i];
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
    public V put(Long key, V value) {
        return this.put(this.objectToKey(key), value);
    }

    @Override
    public V remove(Object key) {
        return this.remove(this.objectToKey(key));
    }

    @Override
    public Set<Long> keySet() {
        return this.keySet;
    }

    @Override
    public Set<Map.Entry<Long, V>> entrySet() {
        return this.entrySet;
    }

    private long objectToKey(Object key) {
        return (Long)key;
    }

    private int indexOf(long key) {
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

    private int hashIndex(long key) {
        return LongObjectHashMap.hashCode(key) & this.mask;
    }

    private static int hashCode(long key) {
        return (int)(key ^ key >>> 32);
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
        this.keys[index] = 0L;
        this.values[index] = null;
        int nextFree = index;
        int i = this.probeNext(index);
        V value = this.values[i];
        while (value != null) {
            long key = this.keys[i];
            int bucket = this.hashIndex(key);
            if (i < bucket && (bucket <= nextFree || nextFree <= i) || bucket <= nextFree && nextFree <= i) {
                this.keys[nextFree] = key;
                this.values[nextFree] = value;
                this.keys[i] = 0L;
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
        long[] oldKeys = this.keys;
        V[] oldVals = this.values;
        this.keys = new long[newCapacity];
        Object[] temp = new Object[newCapacity];
        this.values = temp;
        this.maxSize = this.calcMaxSize(newCapacity);
        this.mask = newCapacity - 1;
        block0 : for (int i = 0; i < oldVals.length; ++i) {
            V oldVal = oldVals[i];
            if (oldVal == null) continue;
            long oldKey = oldKeys[i];
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
            sb.append(this.keyToString(this.keys[i])).append('=').append((Object)(value == this ? "(this Map)" : LongObjectHashMap.toExternal(value)));
            first = false;
        }
        return sb.append('}').toString();
    }

    protected String keyToString(long key) {
        return Long.toString(key);
    }

    final class MapEntry
    implements Map.Entry<Long, V> {
        private final int entryIndex;

        MapEntry(int entryIndex) {
            this.entryIndex = entryIndex;
        }

        @Override
        public Long getKey() {
            this.verifyExists();
            return LongObjectHashMap.this.keys[this.entryIndex];
        }

        @Override
        public V getValue() {
            this.verifyExists();
            return (V)LongObjectHashMap.toExternal(LongObjectHashMap.this.values[this.entryIndex]);
        }

        @Override
        public V setValue(V value) {
            this.verifyExists();
            Object prevValue = LongObjectHashMap.toExternal(LongObjectHashMap.this.values[this.entryIndex]);
            LongObjectHashMap.access$600((LongObjectHashMap)LongObjectHashMap.this)[this.entryIndex] = LongObjectHashMap.toInternal(value);
            return (V)prevValue;
        }

        private void verifyExists() {
            if (LongObjectHashMap.this.values[this.entryIndex] == null) {
                throw new IllegalStateException("The map entry has been removed");
            }
        }
    }

    private final class MapIterator
    implements Iterator<Map.Entry<Long, V>> {
        private final LongObjectHashMap<V> iter;

        private MapIterator() {
            this.iter = new PrimitiveIterator();
        }

        @Override
        public boolean hasNext() {
            return this.iter.hasNext();
        }

        @Override
        public Map.Entry<Long, V> next() {
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
    implements Iterator<LongObjectMap.PrimitiveEntry<V>>,
    LongObjectMap.PrimitiveEntry<V> {
        private int prevIndex = -1;
        private int nextIndex = -1;
        private int entryIndex = -1;

        private PrimitiveIterator() {
        }

        private void scanNext() {
            while (++this.nextIndex != LongObjectHashMap.this.values.length && LongObjectHashMap.this.values[this.nextIndex] == null) {
            }
        }

        @Override
        public boolean hasNext() {
            if (this.nextIndex == -1) {
                this.scanNext();
            }
            return this.nextIndex != LongObjectHashMap.this.values.length;
        }

        @Override
        public LongObjectMap.PrimitiveEntry<V> next() {
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
            if (LongObjectHashMap.this.removeAt(this.prevIndex)) {
                this.nextIndex = this.prevIndex;
            }
            this.prevIndex = -1;
        }

        @Override
        public long key() {
            return LongObjectHashMap.this.keys[this.entryIndex];
        }

        @Override
        public V value() {
            return (V)LongObjectHashMap.toExternal(LongObjectHashMap.this.values[this.entryIndex]);
        }

        @Override
        public void setValue(V value) {
            LongObjectHashMap.access$600((LongObjectHashMap)LongObjectHashMap.this)[this.entryIndex] = LongObjectHashMap.toInternal(value);
        }
    }

    private final class KeySet
    extends AbstractSet<Long> {
        private KeySet() {
        }

        @Override
        public int size() {
            return LongObjectHashMap.this.size();
        }

        @Override
        public boolean contains(Object o) {
            return LongObjectHashMap.this.containsKey(o);
        }

        @Override
        public boolean remove(Object o) {
            return LongObjectHashMap.this.remove(o) != null;
        }

        @Override
        public boolean retainAll(Collection<?> retainedKeys) {
            boolean changed = false;
            Iterator iter = LongObjectHashMap.this.entries().iterator();
            while (iter.hasNext()) {
                LongObjectMap.PrimitiveEntry entry = iter.next();
                if (retainedKeys.contains(entry.key())) continue;
                changed = true;
                iter.remove();
            }
            return changed;
        }

        @Override
        public void clear() {
            LongObjectHashMap.this.clear();
        }

        @Override
        public Iterator<Long> iterator() {
            return new Iterator<Long>(){
                private final Iterator<Map.Entry<Long, V>> iter;
                {
                    this.iter = LongObjectHashMap.this.entrySet.iterator();
                }

                @Override
                public boolean hasNext() {
                    return this.iter.hasNext();
                }

                @Override
                public Long next() {
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
    extends AbstractSet<Map.Entry<Long, V>> {
        private EntrySet() {
        }

        @Override
        public Iterator<Map.Entry<Long, V>> iterator() {
            return new MapIterator();
        }

        @Override
        public int size() {
            return LongObjectHashMap.this.size();
        }
    }

}

