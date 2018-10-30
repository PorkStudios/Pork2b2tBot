/*
 * Decompiled with CFR 0_132.
 */
package io.netty.util.collection;

import io.netty.util.collection.ByteObjectMap;
import io.netty.util.internal.MathUtil;
import java.util.AbstractCollection;
import java.util.AbstractSet;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

public class ByteObjectHashMap<V>
implements ByteObjectMap<V> {
    public static final int DEFAULT_CAPACITY = 8;
    public static final float DEFAULT_LOAD_FACTOR = 0.5f;
    private static final Object NULL_VALUE = new Object();
    private int maxSize;
    private final float loadFactor;
    private byte[] keys;
    private V[] values;
    private int size;
    private int mask;
    private final Set<Byte> keySet = new KeySet();
    private final Set<Map.Entry<Byte, V>> entrySet = new EntrySet();
    private final Iterable<ByteObjectMap.PrimitiveEntry<V>> entries = new Iterable<ByteObjectMap.PrimitiveEntry<V>>(){

        @Override
        public Iterator<ByteObjectMap.PrimitiveEntry<V>> iterator() {
            return new PrimitiveIterator();
        }
    };

    public ByteObjectHashMap() {
        this(8, 0.5f);
    }

    public ByteObjectHashMap(int initialCapacity) {
        this(initialCapacity, 0.5f);
    }

    public ByteObjectHashMap(int initialCapacity, float loadFactor) {
        if (loadFactor <= 0.0f || loadFactor > 1.0f) {
            throw new IllegalArgumentException("loadFactor must be > 0 and <= 1");
        }
        this.loadFactor = loadFactor;
        int capacity = MathUtil.safeFindNextPositivePowerOfTwo(initialCapacity);
        this.mask = capacity - 1;
        this.keys = new byte[capacity];
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
    public V get(byte key) {
        int index = this.indexOf(key);
        return index == -1 ? null : (V)ByteObjectHashMap.toExternal(this.values[index]);
    }

    @Override
    public V put(byte key, V value) {
        int startIndex;
        int index = startIndex = this.hashIndex(key);
        do {
            if (this.values[index] == null) {
                this.keys[index] = key;
                this.values[index] = ByteObjectHashMap.toInternal(value);
                this.growSize();
                return null;
            }
            if (this.keys[index] != key) continue;
            V previousValue = this.values[index];
            this.values[index] = ByteObjectHashMap.toInternal(value);
            return ByteObjectHashMap.toExternal(previousValue);
        } while ((index = this.probeNext(index)) != startIndex);
        throw new IllegalStateException("Unable to insert");
    }

    @Override
    public void putAll(Map<? extends Byte, ? extends V> sourceMap) {
        if (sourceMap instanceof ByteObjectHashMap) {
            ByteObjectHashMap source = (ByteObjectHashMap)sourceMap;
            for (int i = 0; i < source.values.length; ++i) {
                V sourceValue = source.values[i];
                if (sourceValue == null) continue;
                this.put(source.keys[i], sourceValue);
            }
            return;
        }
        for (Map.Entry<Byte, V> entry : sourceMap.entrySet()) {
            this.put(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public V remove(byte key) {
        int index = this.indexOf(key);
        if (index == -1) {
            return null;
        }
        V prev = this.values[index];
        this.removeAt(index);
        return ByteObjectHashMap.toExternal(prev);
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
        Arrays.fill(this.keys, (byte)0);
        Arrays.fill(this.values, null);
        this.size = 0;
    }

    @Override
    public boolean containsKey(byte key) {
        return this.indexOf(key) >= 0;
    }

    @Override
    public boolean containsValue(Object value) {
        Object v1 = ByteObjectHashMap.toInternal(value);
        for (V v2 : this.values) {
            if (v2 == null || !v2.equals(v1)) continue;
            return true;
        }
        return false;
    }

    @Override
    public Iterable<ByteObjectMap.PrimitiveEntry<V>> entries() {
        return this.entries;
    }

    @Override
    public Collection<V> values() {
        return new AbstractCollection<V>(){

            @Override
            public Iterator<V> iterator() {
                return new Iterator<V>(){
                    final ByteObjectHashMap<V> iter;
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
                return ByteObjectHashMap.this.size;
            }

        };
    }

    @Override
    public int hashCode() {
        int hash = this.size;
        for (byte key : this.keys) {
            hash ^= ByteObjectHashMap.hashCode(key);
        }
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof ByteObjectMap)) {
            return false;
        }
        ByteObjectMap other = (ByteObjectMap)obj;
        if (this.size != other.size()) {
            return false;
        }
        for (int i = 0; i < this.values.length; ++i) {
            V value = this.values[i];
            if (value == null) continue;
            byte key = this.keys[i];
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
    public V put(Byte key, V value) {
        return this.put(this.objectToKey(key), value);
    }

    @Override
    public V remove(Object key) {
        return this.remove(this.objectToKey(key));
    }

    @Override
    public Set<Byte> keySet() {
        return this.keySet;
    }

    @Override
    public Set<Map.Entry<Byte, V>> entrySet() {
        return this.entrySet;
    }

    private byte objectToKey(Object key) {
        return (Byte)key;
    }

    private int indexOf(byte key) {
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

    private int hashIndex(byte key) {
        return ByteObjectHashMap.hashCode(key) & this.mask;
    }

    private static int hashCode(byte key) {
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
            byte key = this.keys[i];
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
        byte[] oldKeys = this.keys;
        V[] oldVals = this.values;
        this.keys = new byte[newCapacity];
        Object[] temp = new Object[newCapacity];
        this.values = temp;
        this.maxSize = this.calcMaxSize(newCapacity);
        this.mask = newCapacity - 1;
        block0 : for (int i = 0; i < oldVals.length; ++i) {
            V oldVal = oldVals[i];
            if (oldVal == null) continue;
            byte oldKey = oldKeys[i];
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
            sb.append(this.keyToString(this.keys[i])).append('=').append((Object)(value == this ? "(this Map)" : ByteObjectHashMap.toExternal(value)));
            first = false;
        }
        return sb.append('}').toString();
    }

    protected String keyToString(byte key) {
        return Byte.toString(key);
    }

    final class MapEntry
    implements Map.Entry<Byte, V> {
        private final int entryIndex;

        MapEntry(int entryIndex) {
            this.entryIndex = entryIndex;
        }

        @Override
        public Byte getKey() {
            this.verifyExists();
            return ByteObjectHashMap.this.keys[this.entryIndex];
        }

        @Override
        public V getValue() {
            this.verifyExists();
            return (V)ByteObjectHashMap.toExternal(ByteObjectHashMap.this.values[this.entryIndex]);
        }

        @Override
        public V setValue(V value) {
            this.verifyExists();
            Object prevValue = ByteObjectHashMap.toExternal(ByteObjectHashMap.this.values[this.entryIndex]);
            ByteObjectHashMap.access$600((ByteObjectHashMap)ByteObjectHashMap.this)[this.entryIndex] = ByteObjectHashMap.toInternal(value);
            return (V)prevValue;
        }

        private void verifyExists() {
            if (ByteObjectHashMap.this.values[this.entryIndex] == null) {
                throw new IllegalStateException("The map entry has been removed");
            }
        }
    }

    private final class MapIterator
    implements Iterator<Map.Entry<Byte, V>> {
        private final ByteObjectHashMap<V> iter;

        private MapIterator() {
            this.iter = new PrimitiveIterator();
        }

        @Override
        public boolean hasNext() {
            return this.iter.hasNext();
        }

        @Override
        public Map.Entry<Byte, V> next() {
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
    implements Iterator<ByteObjectMap.PrimitiveEntry<V>>,
    ByteObjectMap.PrimitiveEntry<V> {
        private int prevIndex = -1;
        private int nextIndex = -1;
        private int entryIndex = -1;

        private PrimitiveIterator() {
        }

        private void scanNext() {
            while (++this.nextIndex != ByteObjectHashMap.this.values.length && ByteObjectHashMap.this.values[this.nextIndex] == null) {
            }
        }

        @Override
        public boolean hasNext() {
            if (this.nextIndex == -1) {
                this.scanNext();
            }
            return this.nextIndex != ByteObjectHashMap.this.values.length;
        }

        @Override
        public ByteObjectMap.PrimitiveEntry<V> next() {
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
            if (ByteObjectHashMap.this.removeAt(this.prevIndex)) {
                this.nextIndex = this.prevIndex;
            }
            this.prevIndex = -1;
        }

        @Override
        public byte key() {
            return ByteObjectHashMap.this.keys[this.entryIndex];
        }

        @Override
        public V value() {
            return (V)ByteObjectHashMap.toExternal(ByteObjectHashMap.this.values[this.entryIndex]);
        }

        @Override
        public void setValue(V value) {
            ByteObjectHashMap.access$600((ByteObjectHashMap)ByteObjectHashMap.this)[this.entryIndex] = ByteObjectHashMap.toInternal(value);
        }
    }

    private final class KeySet
    extends AbstractSet<Byte> {
        private KeySet() {
        }

        @Override
        public int size() {
            return ByteObjectHashMap.this.size();
        }

        @Override
        public boolean contains(Object o) {
            return ByteObjectHashMap.this.containsKey(o);
        }

        @Override
        public boolean remove(Object o) {
            return ByteObjectHashMap.this.remove(o) != null;
        }

        @Override
        public boolean retainAll(Collection<?> retainedKeys) {
            boolean changed = false;
            Iterator iter = ByteObjectHashMap.this.entries().iterator();
            while (iter.hasNext()) {
                ByteObjectMap.PrimitiveEntry entry = iter.next();
                if (retainedKeys.contains(entry.key())) continue;
                changed = true;
                iter.remove();
            }
            return changed;
        }

        @Override
        public void clear() {
            ByteObjectHashMap.this.clear();
        }

        @Override
        public Iterator<Byte> iterator() {
            return new Iterator<Byte>(){
                private final Iterator<Map.Entry<Byte, V>> iter;
                {
                    this.iter = ByteObjectHashMap.this.entrySet.iterator();
                }

                @Override
                public boolean hasNext() {
                    return this.iter.hasNext();
                }

                @Override
                public Byte next() {
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
    extends AbstractSet<Map.Entry<Byte, V>> {
        private EntrySet() {
        }

        @Override
        public Iterator<Map.Entry<Byte, V>> iterator() {
            return new MapIterator();
        }

        @Override
        public int size() {
            return ByteObjectHashMap.this.size();
        }
    }

}

