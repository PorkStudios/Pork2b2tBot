/*
 * Decompiled with CFR 0_132.
 */
package io.netty.util.collection;

import io.netty.util.collection.CharObjectMap;
import io.netty.util.internal.MathUtil;
import java.util.AbstractCollection;
import java.util.AbstractSet;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

public class CharObjectHashMap<V>
implements CharObjectMap<V> {
    public static final int DEFAULT_CAPACITY = 8;
    public static final float DEFAULT_LOAD_FACTOR = 0.5f;
    private static final Object NULL_VALUE = new Object();
    private int maxSize;
    private final float loadFactor;
    private char[] keys;
    private V[] values;
    private int size;
    private int mask;
    private final Set<Character> keySet = new KeySet();
    private final Set<Map.Entry<Character, V>> entrySet = new EntrySet();
    private final Iterable<CharObjectMap.PrimitiveEntry<V>> entries = new Iterable<CharObjectMap.PrimitiveEntry<V>>(){

        @Override
        public Iterator<CharObjectMap.PrimitiveEntry<V>> iterator() {
            return new PrimitiveIterator();
        }
    };

    public CharObjectHashMap() {
        this(8, 0.5f);
    }

    public CharObjectHashMap(int initialCapacity) {
        this(initialCapacity, 0.5f);
    }

    public CharObjectHashMap(int initialCapacity, float loadFactor) {
        if (loadFactor <= 0.0f || loadFactor > 1.0f) {
            throw new IllegalArgumentException("loadFactor must be > 0 and <= 1");
        }
        this.loadFactor = loadFactor;
        int capacity = MathUtil.safeFindNextPositivePowerOfTwo(initialCapacity);
        this.mask = capacity - 1;
        this.keys = new char[capacity];
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
    public V get(char key) {
        int index = this.indexOf(key);
        return index == -1 ? null : (V)CharObjectHashMap.toExternal(this.values[index]);
    }

    @Override
    public V put(char key, V value) {
        int startIndex;
        int index = startIndex = this.hashIndex(key);
        do {
            if (this.values[index] == null) {
                this.keys[index] = key;
                this.values[index] = CharObjectHashMap.toInternal(value);
                this.growSize();
                return null;
            }
            if (this.keys[index] != key) continue;
            V previousValue = this.values[index];
            this.values[index] = CharObjectHashMap.toInternal(value);
            return CharObjectHashMap.toExternal(previousValue);
        } while ((index = this.probeNext(index)) != startIndex);
        throw new IllegalStateException("Unable to insert");
    }

    @Override
    public void putAll(Map<? extends Character, ? extends V> sourceMap) {
        if (sourceMap instanceof CharObjectHashMap) {
            CharObjectHashMap source = (CharObjectHashMap)sourceMap;
            for (int i = 0; i < source.values.length; ++i) {
                V sourceValue = source.values[i];
                if (sourceValue == null) continue;
                this.put(source.keys[i], sourceValue);
            }
            return;
        }
        for (Map.Entry<Character, V> entry : sourceMap.entrySet()) {
            this.put(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public V remove(char key) {
        int index = this.indexOf(key);
        if (index == -1) {
            return null;
        }
        V prev = this.values[index];
        this.removeAt(index);
        return CharObjectHashMap.toExternal(prev);
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
        Arrays.fill(this.keys, '\u0000');
        Arrays.fill(this.values, null);
        this.size = 0;
    }

    @Override
    public boolean containsKey(char key) {
        return this.indexOf(key) >= 0;
    }

    @Override
    public boolean containsValue(Object value) {
        Object v1 = CharObjectHashMap.toInternal(value);
        for (V v2 : this.values) {
            if (v2 == null || !v2.equals(v1)) continue;
            return true;
        }
        return false;
    }

    @Override
    public Iterable<CharObjectMap.PrimitiveEntry<V>> entries() {
        return this.entries;
    }

    @Override
    public Collection<V> values() {
        return new AbstractCollection<V>(){

            @Override
            public Iterator<V> iterator() {
                return new Iterator<V>(){
                    final CharObjectHashMap<V> iter;
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
                return CharObjectHashMap.this.size;
            }

        };
    }

    @Override
    public int hashCode() {
        int hash = this.size;
        for (char key : this.keys) {
            hash ^= CharObjectHashMap.hashCode(key);
        }
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof CharObjectMap)) {
            return false;
        }
        CharObjectMap other = (CharObjectMap)obj;
        if (this.size != other.size()) {
            return false;
        }
        for (int i = 0; i < this.values.length; ++i) {
            V value = this.values[i];
            if (value == null) continue;
            char key = this.keys[i];
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
    public V put(Character key, V value) {
        return this.put(this.objectToKey(key), value);
    }

    @Override
    public V remove(Object key) {
        return this.remove(this.objectToKey(key));
    }

    @Override
    public Set<Character> keySet() {
        return this.keySet;
    }

    @Override
    public Set<Map.Entry<Character, V>> entrySet() {
        return this.entrySet;
    }

    private char objectToKey(Object key) {
        return ((Character)key).charValue();
    }

    private int indexOf(char key) {
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

    private int hashIndex(char key) {
        return CharObjectHashMap.hashCode(key) & this.mask;
    }

    private static int hashCode(char key) {
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
        this.keys[index] = '\u0000';
        this.values[index] = null;
        int nextFree = index;
        int i = this.probeNext(index);
        V value = this.values[i];
        while (value != null) {
            char key = this.keys[i];
            int bucket = this.hashIndex(key);
            if (i < bucket && (bucket <= nextFree || nextFree <= i) || bucket <= nextFree && nextFree <= i) {
                this.keys[nextFree] = key;
                this.values[nextFree] = value;
                this.keys[i] = '\u0000';
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
        char[] oldKeys = this.keys;
        V[] oldVals = this.values;
        this.keys = new char[newCapacity];
        Object[] temp = new Object[newCapacity];
        this.values = temp;
        this.maxSize = this.calcMaxSize(newCapacity);
        this.mask = newCapacity - 1;
        block0 : for (int i = 0; i < oldVals.length; ++i) {
            V oldVal = oldVals[i];
            if (oldVal == null) continue;
            char oldKey = oldKeys[i];
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
            sb.append(this.keyToString(this.keys[i])).append('=').append((Object)(value == this ? "(this Map)" : CharObjectHashMap.toExternal(value)));
            first = false;
        }
        return sb.append('}').toString();
    }

    protected String keyToString(char key) {
        return Character.toString(key);
    }

    final class MapEntry
    implements Map.Entry<Character, V> {
        private final int entryIndex;

        MapEntry(int entryIndex) {
            this.entryIndex = entryIndex;
        }

        @Override
        public Character getKey() {
            this.verifyExists();
            return Character.valueOf(CharObjectHashMap.this.keys[this.entryIndex]);
        }

        @Override
        public V getValue() {
            this.verifyExists();
            return (V)CharObjectHashMap.toExternal(CharObjectHashMap.this.values[this.entryIndex]);
        }

        @Override
        public V setValue(V value) {
            this.verifyExists();
            Object prevValue = CharObjectHashMap.toExternal(CharObjectHashMap.this.values[this.entryIndex]);
            CharObjectHashMap.access$600((CharObjectHashMap)CharObjectHashMap.this)[this.entryIndex] = CharObjectHashMap.toInternal(value);
            return (V)prevValue;
        }

        private void verifyExists() {
            if (CharObjectHashMap.this.values[this.entryIndex] == null) {
                throw new IllegalStateException("The map entry has been removed");
            }
        }
    }

    private final class MapIterator
    implements Iterator<Map.Entry<Character, V>> {
        private final CharObjectHashMap<V> iter;

        private MapIterator() {
            this.iter = new PrimitiveIterator();
        }

        @Override
        public boolean hasNext() {
            return this.iter.hasNext();
        }

        @Override
        public Map.Entry<Character, V> next() {
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
    implements Iterator<CharObjectMap.PrimitiveEntry<V>>,
    CharObjectMap.PrimitiveEntry<V> {
        private int prevIndex = -1;
        private int nextIndex = -1;
        private int entryIndex = -1;

        private PrimitiveIterator() {
        }

        private void scanNext() {
            while (++this.nextIndex != CharObjectHashMap.this.values.length && CharObjectHashMap.this.values[this.nextIndex] == null) {
            }
        }

        @Override
        public boolean hasNext() {
            if (this.nextIndex == -1) {
                this.scanNext();
            }
            return this.nextIndex != CharObjectHashMap.this.values.length;
        }

        @Override
        public CharObjectMap.PrimitiveEntry<V> next() {
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
            if (CharObjectHashMap.this.removeAt(this.prevIndex)) {
                this.nextIndex = this.prevIndex;
            }
            this.prevIndex = -1;
        }

        @Override
        public char key() {
            return CharObjectHashMap.this.keys[this.entryIndex];
        }

        @Override
        public V value() {
            return (V)CharObjectHashMap.toExternal(CharObjectHashMap.this.values[this.entryIndex]);
        }

        @Override
        public void setValue(V value) {
            CharObjectHashMap.access$600((CharObjectHashMap)CharObjectHashMap.this)[this.entryIndex] = CharObjectHashMap.toInternal(value);
        }
    }

    private final class KeySet
    extends AbstractSet<Character> {
        private KeySet() {
        }

        @Override
        public int size() {
            return CharObjectHashMap.this.size();
        }

        @Override
        public boolean contains(Object o) {
            return CharObjectHashMap.this.containsKey(o);
        }

        @Override
        public boolean remove(Object o) {
            return CharObjectHashMap.this.remove(o) != null;
        }

        @Override
        public boolean retainAll(Collection<?> retainedKeys) {
            boolean changed = false;
            Iterator iter = CharObjectHashMap.this.entries().iterator();
            while (iter.hasNext()) {
                CharObjectMap.PrimitiveEntry entry = iter.next();
                if (retainedKeys.contains(Character.valueOf(entry.key()))) continue;
                changed = true;
                iter.remove();
            }
            return changed;
        }

        @Override
        public void clear() {
            CharObjectHashMap.this.clear();
        }

        @Override
        public Iterator<Character> iterator() {
            return new Iterator<Character>(){
                private final Iterator<Map.Entry<Character, V>> iter;
                {
                    this.iter = CharObjectHashMap.this.entrySet.iterator();
                }

                @Override
                public boolean hasNext() {
                    return this.iter.hasNext();
                }

                @Override
                public Character next() {
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
    extends AbstractSet<Map.Entry<Character, V>> {
        private EntrySet() {
        }

        @Override
        public Iterator<Map.Entry<Character, V>> iterator() {
            return new MapIterator();
        }

        @Override
        public int size() {
            return CharObjectHashMap.this.size();
        }
    }

}

