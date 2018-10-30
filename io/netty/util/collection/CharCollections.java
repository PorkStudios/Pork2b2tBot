/*
 * Decompiled with CFR 0_132.
 */
package io.netty.util.collection;

import io.netty.util.collection.CharObjectMap;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

public final class CharCollections {
    private static final CharObjectMap<Object> EMPTY_MAP = new EmptyMap();

    private CharCollections() {
    }

    public static <V> CharObjectMap<V> emptyMap() {
        return EMPTY_MAP;
    }

    public static <V> CharObjectMap<V> unmodifiableMap(CharObjectMap<V> map) {
        return new UnmodifiableMap<V>(map);
    }

    private static final class UnmodifiableMap<V>
    implements CharObjectMap<V> {
        private final CharObjectMap<V> map;
        private Set<Character> keySet;
        private Set<Map.Entry<Character, V>> entrySet;
        private Collection<V> values;
        private Iterable<CharObjectMap.PrimitiveEntry<V>> entries;

        UnmodifiableMap(CharObjectMap<V> map) {
            this.map = map;
        }

        @Override
        public V get(char key) {
            return this.map.get(key);
        }

        @Override
        public V put(char key, V value) {
            throw new UnsupportedOperationException("put");
        }

        @Override
        public V remove(char key) {
            throw new UnsupportedOperationException("remove");
        }

        @Override
        public int size() {
            return this.map.size();
        }

        @Override
        public boolean isEmpty() {
            return this.map.isEmpty();
        }

        @Override
        public void clear() {
            throw new UnsupportedOperationException("clear");
        }

        @Override
        public boolean containsKey(char key) {
            return this.map.containsKey(key);
        }

        @Override
        public boolean containsValue(Object value) {
            return this.map.containsValue(value);
        }

        @Override
        public boolean containsKey(Object key) {
            return this.map.containsKey(key);
        }

        @Override
        public V get(Object key) {
            return this.map.get(key);
        }

        @Override
        public V put(Character key, V value) {
            throw new UnsupportedOperationException("put");
        }

        @Override
        public V remove(Object key) {
            throw new UnsupportedOperationException("remove");
        }

        @Override
        public void putAll(Map<? extends Character, ? extends V> m) {
            throw new UnsupportedOperationException("putAll");
        }

        @Override
        public Iterable<CharObjectMap.PrimitiveEntry<V>> entries() {
            if (this.entries == null) {
                this.entries = new Iterable<CharObjectMap.PrimitiveEntry<V>>(){

                    @Override
                    public Iterator<CharObjectMap.PrimitiveEntry<V>> iterator() {
                        return new IteratorImpl(UnmodifiableMap.this.map.entries().iterator());
                    }
                };
            }
            return this.entries;
        }

        @Override
        public Set<Character> keySet() {
            if (this.keySet == null) {
                this.keySet = Collections.unmodifiableSet(this.map.keySet());
            }
            return this.keySet;
        }

        @Override
        public Set<Map.Entry<Character, V>> entrySet() {
            if (this.entrySet == null) {
                this.entrySet = Collections.unmodifiableSet(this.map.entrySet());
            }
            return this.entrySet;
        }

        @Override
        public Collection<V> values() {
            if (this.values == null) {
                this.values = Collections.unmodifiableCollection(this.map.values());
            }
            return this.values;
        }

        private class EntryImpl
        implements CharObjectMap.PrimitiveEntry<V> {
            private final CharObjectMap.PrimitiveEntry<V> entry;

            EntryImpl(CharObjectMap.PrimitiveEntry<V> entry) {
                this.entry = entry;
            }

            @Override
            public char key() {
                return this.entry.key();
            }

            @Override
            public V value() {
                return this.entry.value();
            }

            @Override
            public void setValue(V value) {
                throw new UnsupportedOperationException("setValue");
            }
        }

        private class IteratorImpl
        implements Iterator<CharObjectMap.PrimitiveEntry<V>> {
            final Iterator<CharObjectMap.PrimitiveEntry<V>> iter;

            IteratorImpl(Iterator<CharObjectMap.PrimitiveEntry<V>> iter) {
                this.iter = iter;
            }

            @Override
            public boolean hasNext() {
                return this.iter.hasNext();
            }

            @Override
            public CharObjectMap.PrimitiveEntry<V> next() {
                if (!this.hasNext()) {
                    throw new NoSuchElementException();
                }
                return new EntryImpl(this.iter.next());
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException("remove");
            }
        }

    }

    private static final class EmptyMap
    implements CharObjectMap<Object> {
        private EmptyMap() {
        }

        @Override
        public Object get(char key) {
            return null;
        }

        @Override
        public Object put(char key, Object value) {
            throw new UnsupportedOperationException("put");
        }

        @Override
        public Object remove(char key) {
            return null;
        }

        @Override
        public int size() {
            return 0;
        }

        @Override
        public boolean isEmpty() {
            return true;
        }

        @Override
        public boolean containsKey(Object key) {
            return false;
        }

        @Override
        public void clear() {
        }

        @Override
        public Set<Character> keySet() {
            return Collections.emptySet();
        }

        @Override
        public boolean containsKey(char key) {
            return false;
        }

        @Override
        public boolean containsValue(Object value) {
            return false;
        }

        @Override
        public Iterable<CharObjectMap.PrimitiveEntry<Object>> entries() {
            return Collections.emptySet();
        }

        @Override
        public Object get(Object key) {
            return null;
        }

        @Override
        public Object put(Character key, Object value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Object remove(Object key) {
            return null;
        }

        @Override
        public void putAll(Map<? extends Character, ?> m) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Collection<Object> values() {
            return Collections.emptyList();
        }

        @Override
        public Set<Map.Entry<Character, Object>> entrySet() {
            return Collections.emptySet();
        }
    }

}

