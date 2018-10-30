/*
 * Decompiled with CFR 0_132.
 */
package io.netty.util.collection;

import io.netty.util.collection.ShortObjectMap;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

public final class ShortCollections {
    private static final ShortObjectMap<Object> EMPTY_MAP = new EmptyMap();

    private ShortCollections() {
    }

    public static <V> ShortObjectMap<V> emptyMap() {
        return EMPTY_MAP;
    }

    public static <V> ShortObjectMap<V> unmodifiableMap(ShortObjectMap<V> map) {
        return new UnmodifiableMap<V>(map);
    }

    private static final class UnmodifiableMap<V>
    implements ShortObjectMap<V> {
        private final ShortObjectMap<V> map;
        private Set<Short> keySet;
        private Set<Map.Entry<Short, V>> entrySet;
        private Collection<V> values;
        private Iterable<ShortObjectMap.PrimitiveEntry<V>> entries;

        UnmodifiableMap(ShortObjectMap<V> map) {
            this.map = map;
        }

        @Override
        public V get(short key) {
            return this.map.get(key);
        }

        @Override
        public V put(short key, V value) {
            throw new UnsupportedOperationException("put");
        }

        @Override
        public V remove(short key) {
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
        public boolean containsKey(short key) {
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
        public V put(Short key, V value) {
            throw new UnsupportedOperationException("put");
        }

        @Override
        public V remove(Object key) {
            throw new UnsupportedOperationException("remove");
        }

        @Override
        public void putAll(Map<? extends Short, ? extends V> m) {
            throw new UnsupportedOperationException("putAll");
        }

        @Override
        public Iterable<ShortObjectMap.PrimitiveEntry<V>> entries() {
            if (this.entries == null) {
                this.entries = new Iterable<ShortObjectMap.PrimitiveEntry<V>>(){

                    @Override
                    public Iterator<ShortObjectMap.PrimitiveEntry<V>> iterator() {
                        return new IteratorImpl(UnmodifiableMap.this.map.entries().iterator());
                    }
                };
            }
            return this.entries;
        }

        @Override
        public Set<Short> keySet() {
            if (this.keySet == null) {
                this.keySet = Collections.unmodifiableSet(this.map.keySet());
            }
            return this.keySet;
        }

        @Override
        public Set<Map.Entry<Short, V>> entrySet() {
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
        implements ShortObjectMap.PrimitiveEntry<V> {
            private final ShortObjectMap.PrimitiveEntry<V> entry;

            EntryImpl(ShortObjectMap.PrimitiveEntry<V> entry) {
                this.entry = entry;
            }

            @Override
            public short key() {
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
        implements Iterator<ShortObjectMap.PrimitiveEntry<V>> {
            final Iterator<ShortObjectMap.PrimitiveEntry<V>> iter;

            IteratorImpl(Iterator<ShortObjectMap.PrimitiveEntry<V>> iter) {
                this.iter = iter;
            }

            @Override
            public boolean hasNext() {
                return this.iter.hasNext();
            }

            @Override
            public ShortObjectMap.PrimitiveEntry<V> next() {
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
    implements ShortObjectMap<Object> {
        private EmptyMap() {
        }

        @Override
        public Object get(short key) {
            return null;
        }

        @Override
        public Object put(short key, Object value) {
            throw new UnsupportedOperationException("put");
        }

        @Override
        public Object remove(short key) {
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
        public Set<Short> keySet() {
            return Collections.emptySet();
        }

        @Override
        public boolean containsKey(short key) {
            return false;
        }

        @Override
        public boolean containsValue(Object value) {
            return false;
        }

        @Override
        public Iterable<ShortObjectMap.PrimitiveEntry<Object>> entries() {
            return Collections.emptySet();
        }

        @Override
        public Object get(Object key) {
            return null;
        }

        @Override
        public Object put(Short key, Object value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Object remove(Object key) {
            return null;
        }

        @Override
        public void putAll(Map<? extends Short, ?> m) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Collection<Object> values() {
            return Collections.emptyList();
        }

        @Override
        public Set<Map.Entry<Short, Object>> entrySet() {
            return Collections.emptySet();
        }
    }

}

