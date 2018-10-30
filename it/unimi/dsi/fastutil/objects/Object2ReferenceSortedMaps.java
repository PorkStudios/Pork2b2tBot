/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.objects.AbstractObject2ReferenceMap;
import it.unimi.dsi.fastutil.objects.Object2ReferenceMap;
import it.unimi.dsi.fastutil.objects.Object2ReferenceMaps;
import it.unimi.dsi.fastutil.objects.Object2ReferenceSortedMap;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterable;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import it.unimi.dsi.fastutil.objects.ObjectSortedSets;
import java.io.Serializable;
import java.util.Comparator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.SortedMap;

public final class Object2ReferenceSortedMaps {
    public static final EmptySortedMap EMPTY_MAP = new EmptySortedMap();

    private Object2ReferenceSortedMaps() {
    }

    public static <K> Comparator<? super Map.Entry<K, ?>> entryComparator(Comparator<? super K> comparator) {
        return (x, y) -> comparator.compare((K)x.getKey(), (K)y.getKey());
    }

    public static <K, V> ObjectBidirectionalIterator<Object2ReferenceMap.Entry<K, V>> fastIterator(Object2ReferenceSortedMap<K, V> map) {
        ObjectSet entries = map.object2ReferenceEntrySet();
        return entries instanceof Object2ReferenceSortedMap.FastSortedEntrySet ? ((Object2ReferenceSortedMap.FastSortedEntrySet)entries).fastIterator() : entries.iterator();
    }

    public static <K, V> ObjectBidirectionalIterable<Object2ReferenceMap.Entry<K, V>> fastIterable(Object2ReferenceSortedMap<K, V> map) {
        ObjectSet entries = map.object2ReferenceEntrySet();
        return entries instanceof Object2ReferenceSortedMap.FastSortedEntrySet ? ((Object2ReferenceSortedMap.FastSortedEntrySet)entries)::fastIterator : entries;
    }

    public static <K, V> Object2ReferenceSortedMap<K, V> emptyMap() {
        return EMPTY_MAP;
    }

    public static <K, V> Object2ReferenceSortedMap<K, V> singleton(K key, V value) {
        return new Singleton<K, V>(key, value);
    }

    public static <K, V> Object2ReferenceSortedMap<K, V> singleton(K key, V value, Comparator<? super K> comparator) {
        return new Singleton<K, V>((K)key, value, comparator);
    }

    public static <K, V> Object2ReferenceSortedMap<K, V> synchronize(Object2ReferenceSortedMap<K, V> m) {
        return new SynchronizedSortedMap<K, V>(m);
    }

    public static <K, V> Object2ReferenceSortedMap<K, V> synchronize(Object2ReferenceSortedMap<K, V> m, Object sync) {
        return new SynchronizedSortedMap<K, V>(m, sync);
    }

    public static <K, V> Object2ReferenceSortedMap<K, V> unmodifiable(Object2ReferenceSortedMap<K, V> m) {
        return new UnmodifiableSortedMap<K, V>(m);
    }

    public static class UnmodifiableSortedMap<K, V>
    extends Object2ReferenceMaps.UnmodifiableMap<K, V>
    implements Object2ReferenceSortedMap<K, V>,
    Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final Object2ReferenceSortedMap<K, V> sortedMap;

        protected UnmodifiableSortedMap(Object2ReferenceSortedMap<K, V> m) {
            super(m);
            this.sortedMap = m;
        }

        @Override
        public Comparator<? super K> comparator() {
            return this.sortedMap.comparator();
        }

        @Override
        public ObjectSortedSet<Object2ReferenceMap.Entry<K, V>> object2ReferenceEntrySet() {
            if (this.entries == null) {
                this.entries = ObjectSortedSets.unmodifiable(this.sortedMap.object2ReferenceEntrySet());
            }
            return (ObjectSortedSet)this.entries;
        }

        @Override
        public ObjectSortedSet<Map.Entry<K, V>> entrySet() {
            return this.object2ReferenceEntrySet();
        }

        @Override
        public ObjectSortedSet<K> keySet() {
            if (this.keys == null) {
                this.keys = ObjectSortedSets.unmodifiable(this.sortedMap.keySet());
            }
            return (ObjectSortedSet)this.keys;
        }

        @Override
        public Object2ReferenceSortedMap<K, V> subMap(K from, K to) {
            return new UnmodifiableSortedMap<K, V>((Object2ReferenceSortedMap<K, V>)this.sortedMap.subMap((Object)from, (Object)to));
        }

        @Override
        public Object2ReferenceSortedMap<K, V> headMap(K to) {
            return new UnmodifiableSortedMap<K, V>((Object2ReferenceSortedMap<K, V>)this.sortedMap.headMap((Object)to));
        }

        @Override
        public Object2ReferenceSortedMap<K, V> tailMap(K from) {
            return new UnmodifiableSortedMap<K, V>((Object2ReferenceSortedMap<K, V>)this.sortedMap.tailMap((Object)from));
        }

        @Override
        public K firstKey() {
            return this.sortedMap.firstKey();
        }

        @Override
        public K lastKey() {
            return this.sortedMap.lastKey();
        }
    }

    public static class SynchronizedSortedMap<K, V>
    extends Object2ReferenceMaps.SynchronizedMap<K, V>
    implements Object2ReferenceSortedMap<K, V>,
    Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final Object2ReferenceSortedMap<K, V> sortedMap;

        protected SynchronizedSortedMap(Object2ReferenceSortedMap<K, V> m, Object sync) {
            super(m, sync);
            this.sortedMap = m;
        }

        protected SynchronizedSortedMap(Object2ReferenceSortedMap<K, V> m) {
            super(m);
            this.sortedMap = m;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public Comparator<? super K> comparator() {
            Object object = this.sync;
            synchronized (object) {
                return this.sortedMap.comparator();
            }
        }

        @Override
        public ObjectSortedSet<Object2ReferenceMap.Entry<K, V>> object2ReferenceEntrySet() {
            if (this.entries == null) {
                this.entries = ObjectSortedSets.synchronize(this.sortedMap.object2ReferenceEntrySet(), this.sync);
            }
            return (ObjectSortedSet)this.entries;
        }

        @Override
        public ObjectSortedSet<Map.Entry<K, V>> entrySet() {
            return this.object2ReferenceEntrySet();
        }

        @Override
        public ObjectSortedSet<K> keySet() {
            if (this.keys == null) {
                this.keys = ObjectSortedSets.synchronize(this.sortedMap.keySet(), this.sync);
            }
            return (ObjectSortedSet)this.keys;
        }

        @Override
        public Object2ReferenceSortedMap<K, V> subMap(K from, K to) {
            return new SynchronizedSortedMap<K, V>((Object2ReferenceSortedMap<K, V>)this.sortedMap.subMap((Object)from, (Object)to), this.sync);
        }

        @Override
        public Object2ReferenceSortedMap<K, V> headMap(K to) {
            return new SynchronizedSortedMap<K, V>((Object2ReferenceSortedMap<K, V>)this.sortedMap.headMap((Object)to), this.sync);
        }

        @Override
        public Object2ReferenceSortedMap<K, V> tailMap(K from) {
            return new SynchronizedSortedMap<K, V>((Object2ReferenceSortedMap<K, V>)this.sortedMap.tailMap((Object)from), this.sync);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public K firstKey() {
            Object object = this.sync;
            synchronized (object) {
                return this.sortedMap.firstKey();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public K lastKey() {
            Object object = this.sync;
            synchronized (object) {
                return this.sortedMap.lastKey();
            }
        }
    }

    public static class Singleton<K, V>
    extends Object2ReferenceMaps.Singleton<K, V>
    implements Object2ReferenceSortedMap<K, V>,
    Serializable,
    Cloneable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final Comparator<? super K> comparator;

        protected Singleton(K key, V value, Comparator<? super K> comparator) {
            super(key, value);
            this.comparator = comparator;
        }

        protected Singleton(K key, V value) {
            this(key, value, null);
        }

        final int compare(K k1, K k2) {
            return this.comparator == null ? ((Comparable)k1).compareTo(k2) : this.comparator.compare(k1, k2);
        }

        @Override
        public Comparator<? super K> comparator() {
            return this.comparator;
        }

        @Override
        public ObjectSortedSet<Object2ReferenceMap.Entry<K, V>> object2ReferenceEntrySet() {
            if (this.entries == null) {
                this.entries = ObjectSortedSets.singleton(new AbstractObject2ReferenceMap.BasicEntry<Object, Object>(this.key, this.value), Object2ReferenceSortedMaps.entryComparator(this.comparator));
            }
            return (ObjectSortedSet)this.entries;
        }

        @Override
        public ObjectSortedSet<Map.Entry<K, V>> entrySet() {
            return this.object2ReferenceEntrySet();
        }

        @Override
        public ObjectSortedSet<K> keySet() {
            if (this.keys == null) {
                this.keys = ObjectSortedSets.singleton(this.key, this.comparator);
            }
            return (ObjectSortedSet)this.keys;
        }

        @Override
        public Object2ReferenceSortedMap<K, V> subMap(K from, K to) {
            if (this.compare(from, this.key) <= 0 && this.compare(this.key, to) < 0) {
                return this;
            }
            return Object2ReferenceSortedMaps.EMPTY_MAP;
        }

        @Override
        public Object2ReferenceSortedMap<K, V> headMap(K to) {
            if (this.compare(this.key, to) < 0) {
                return this;
            }
            return Object2ReferenceSortedMaps.EMPTY_MAP;
        }

        @Override
        public Object2ReferenceSortedMap<K, V> tailMap(K from) {
            if (this.compare(from, this.key) <= 0) {
                return this;
            }
            return Object2ReferenceSortedMaps.EMPTY_MAP;
        }

        @Override
        public K firstKey() {
            return (K)this.key;
        }

        @Override
        public K lastKey() {
            return (K)this.key;
        }
    }

    public static class EmptySortedMap<K, V>
    extends Object2ReferenceMaps.EmptyMap<K, V>
    implements Object2ReferenceSortedMap<K, V>,
    Serializable,
    Cloneable {
        private static final long serialVersionUID = -7046029254386353129L;

        protected EmptySortedMap() {
        }

        @Override
        public Comparator<? super K> comparator() {
            return null;
        }

        @Override
        public ObjectSortedSet<Object2ReferenceMap.Entry<K, V>> object2ReferenceEntrySet() {
            return ObjectSortedSets.EMPTY_SET;
        }

        @Override
        public ObjectSortedSet<Map.Entry<K, V>> entrySet() {
            return ObjectSortedSets.EMPTY_SET;
        }

        @Override
        public ObjectSortedSet<K> keySet() {
            return ObjectSortedSets.EMPTY_SET;
        }

        @Override
        public Object2ReferenceSortedMap<K, V> subMap(K from, K to) {
            return Object2ReferenceSortedMaps.EMPTY_MAP;
        }

        @Override
        public Object2ReferenceSortedMap<K, V> headMap(K to) {
            return Object2ReferenceSortedMaps.EMPTY_MAP;
        }

        @Override
        public Object2ReferenceSortedMap<K, V> tailMap(K from) {
            return Object2ReferenceSortedMaps.EMPTY_MAP;
        }

        @Override
        public K firstKey() {
            throw new NoSuchElementException();
        }

        @Override
        public K lastKey() {
            throw new NoSuchElementException();
        }
    }

}

