/*
 * Decompiled with CFR 0_132.
 */
package io.netty.util.collection;

import io.netty.util.collection.IntObjectMap;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

public final class IntCollections {
    private static final IntObjectMap<Object> EMPTY_MAP = new EmptyMap();

    private IntCollections() {
    }

    public static <V> IntObjectMap<V> emptyMap() {
        return EMPTY_MAP;
    }

    public static <V> IntObjectMap<V> unmodifiableMap(IntObjectMap<V> map) {
        return new UnmodifiableMap<V>(map);
    }

    private static final class UnmodifiableMap<V>
    implements IntObjectMap<V> {
        private final IntObjectMap<V> map;
        private Set<Integer> keySet;
        private Set<Map.Entry<Integer, V>> entrySet;
        private Collection<V> values;
        private Iterable<IntObjectMap.PrimitiveEntry<V>> entries;

        UnmodifiableMap(IntObjectMap<V> map) {
            this.map = map;
        }

        @Override
        public V get(int key) {
            return this.map.get(key);
        }

        @Override
        public V put(int key, V value) {
            throw new UnsupportedOperationException("put");
        }

        @Override
        public V remove(int key) {
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
        public boolean containsKey(int key) {
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
        public V put(Integer key, V value) {
            throw new UnsupportedOperationException("put");
        }

        @Override
        public V remove(Object key) {
            throw new UnsupportedOperationException("remove");
        }

        @Override
        public void putAll(Map<? extends Integer, ? extends V> m) {
            throw new UnsupportedOperationException("putAll");
        }

        @Override
        public Iterable<IntObjectMap.PrimitiveEntry<V>> entries() {
            if (this.entries == null) {
                this.entries = new Iterable<IntObjectMap.PrimitiveEntry<V>>(){

                    @Override
                    public Iterator<IntObjectMap.PrimitiveEntry<V>> iterator() {
                        return new IteratorImpl(UnmodifiableMap.this.map.entries().iterator());
                    }
                };
            }
            return this.entries;
        }

        @Override
        public Set<Integer> keySet() {
            if (this.keySet == null) {
                this.keySet = Collections.unmodifiableSet(this.map.keySet());
            }
            return this.keySet;
        }

        @Override
        public Set<Map.Entry<Integer, V>> entrySet() {
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
        implements IntObjectMap.PrimitiveEntry<V> {
            private final IntObjectMap.PrimitiveEntry<V> entry;

            EntryImpl(IntObjectMap.PrimitiveEntry<V> entry) {
                this.entry = entry;
            }

            @Override
            public int key() {
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
        implements Iterator<IntObjectMap.PrimitiveEntry<V>> {
            final Iterator<IntObjectMap.PrimitiveEntry<V>> iter;

            IteratorImpl(Iterator<IntObjectMap.PrimitiveEntry<V>> iter) {
                this.iter = iter;
            }

            @Override
            public boolean hasNext() {
                return this.iter.hasNext();
            }

            @Override
            public IntObjectMap.PrimitiveEntry<V> next() {
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
    implements IntObjectMap<Object> {
        private EmptyMap() {
        }

        @Override
        public Object get(int key) {
            return null;
        }

        @Override
        public Object put(int key, Object value) {
            throw new UnsupportedOperationException("put");
        }

        @Override
        public Object remove(int key) {
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
        public Set<Integer> keySet() {
            return Collections.emptySet();
        }

        @Override
        public boolean containsKey(int key) {
            return false;
        }

        @Override
        public boolean containsValue(Object value) {
            return false;
        }

        @Override
        public Iterable<IntObjectMap.PrimitiveEntry<Object>> entries() {
            return Collections.emptySet();
        }

        @Override
        public Object get(Object key) {
            return null;
        }

        @Override
        public Object put(Integer key, Object value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Object remove(Object key) {
            return null;
        }

        @Override
        public void putAll(Map<? extends Integer, ?> m) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Collection<Object> values() {
            return Collections.emptyList();
        }

        @Override
        public Set<Map.Entry<Integer, Object>> entrySet() {
            return Collections.emptySet();
        }
    }

}

