/*
 * Decompiled with CFR 0_132.
 */
package io.netty.util.collection;

import io.netty.util.collection.LongObjectMap;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

public final class LongCollections {
    private static final LongObjectMap<Object> EMPTY_MAP = new EmptyMap();

    private LongCollections() {
    }

    public static <V> LongObjectMap<V> emptyMap() {
        return EMPTY_MAP;
    }

    public static <V> LongObjectMap<V> unmodifiableMap(LongObjectMap<V> map) {
        return new UnmodifiableMap<V>(map);
    }

    private static final class UnmodifiableMap<V>
    implements LongObjectMap<V> {
        private final LongObjectMap<V> map;
        private Set<Long> keySet;
        private Set<Map.Entry<Long, V>> entrySet;
        private Collection<V> values;
        private Iterable<LongObjectMap.PrimitiveEntry<V>> entries;

        UnmodifiableMap(LongObjectMap<V> map) {
            this.map = map;
        }

        @Override
        public V get(long key) {
            return this.map.get(key);
        }

        @Override
        public V put(long key, V value) {
            throw new UnsupportedOperationException("put");
        }

        @Override
        public V remove(long key) {
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
        public boolean containsKey(long key) {
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
        public V put(Long key, V value) {
            throw new UnsupportedOperationException("put");
        }

        @Override
        public V remove(Object key) {
            throw new UnsupportedOperationException("remove");
        }

        @Override
        public void putAll(Map<? extends Long, ? extends V> m) {
            throw new UnsupportedOperationException("putAll");
        }

        @Override
        public Iterable<LongObjectMap.PrimitiveEntry<V>> entries() {
            if (this.entries == null) {
                this.entries = new Iterable<LongObjectMap.PrimitiveEntry<V>>(){

                    @Override
                    public Iterator<LongObjectMap.PrimitiveEntry<V>> iterator() {
                        return new IteratorImpl(UnmodifiableMap.this.map.entries().iterator());
                    }
                };
            }
            return this.entries;
        }

        @Override
        public Set<Long> keySet() {
            if (this.keySet == null) {
                this.keySet = Collections.unmodifiableSet(this.map.keySet());
            }
            return this.keySet;
        }

        @Override
        public Set<Map.Entry<Long, V>> entrySet() {
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
        implements LongObjectMap.PrimitiveEntry<V> {
            private final LongObjectMap.PrimitiveEntry<V> entry;

            EntryImpl(LongObjectMap.PrimitiveEntry<V> entry) {
                this.entry = entry;
            }

            @Override
            public long key() {
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
        implements Iterator<LongObjectMap.PrimitiveEntry<V>> {
            final Iterator<LongObjectMap.PrimitiveEntry<V>> iter;

            IteratorImpl(Iterator<LongObjectMap.PrimitiveEntry<V>> iter) {
                this.iter = iter;
            }

            @Override
            public boolean hasNext() {
                return this.iter.hasNext();
            }

            @Override
            public LongObjectMap.PrimitiveEntry<V> next() {
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
    implements LongObjectMap<Object> {
        private EmptyMap() {
        }

        @Override
        public Object get(long key) {
            return null;
        }

        @Override
        public Object put(long key, Object value) {
            throw new UnsupportedOperationException("put");
        }

        @Override
        public Object remove(long key) {
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
        public Set<Long> keySet() {
            return Collections.emptySet();
        }

        @Override
        public boolean containsKey(long key) {
            return false;
        }

        @Override
        public boolean containsValue(Object value) {
            return false;
        }

        @Override
        public Iterable<LongObjectMap.PrimitiveEntry<Object>> entries() {
            return Collections.emptySet();
        }

        @Override
        public Object get(Object key) {
            return null;
        }

        @Override
        public Object put(Long key, Object value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Object remove(Object key) {
            return null;
        }

        @Override
        public void putAll(Map<? extends Long, ?> m) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Collection<Object> values() {
            return Collections.emptyList();
        }

        @Override
        public Set<Map.Entry<Long, Object>> entrySet() {
            return Collections.emptySet();
        }
    }

}

