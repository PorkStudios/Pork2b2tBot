/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.objects.AbstractReference2ObjectMap;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterable;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import it.unimi.dsi.fastutil.objects.ObjectSortedSets;
import it.unimi.dsi.fastutil.objects.Reference2ObjectMap;
import it.unimi.dsi.fastutil.objects.Reference2ObjectMaps;
import it.unimi.dsi.fastutil.objects.Reference2ObjectSortedMap;
import it.unimi.dsi.fastutil.objects.ReferenceSet;
import it.unimi.dsi.fastutil.objects.ReferenceSortedSet;
import it.unimi.dsi.fastutil.objects.ReferenceSortedSets;
import java.io.Serializable;
import java.util.Comparator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.SortedMap;

public final class Reference2ObjectSortedMaps {
    public static final EmptySortedMap EMPTY_MAP = new EmptySortedMap();

    private Reference2ObjectSortedMaps() {
    }

    public static <K> Comparator<? super Map.Entry<K, ?>> entryComparator(Comparator<? super K> comparator) {
        return (x, y) -> comparator.compare((K)x.getKey(), (K)y.getKey());
    }

    public static <K, V> ObjectBidirectionalIterator<Reference2ObjectMap.Entry<K, V>> fastIterator(Reference2ObjectSortedMap<K, V> map) {
        ObjectSet entries = map.reference2ObjectEntrySet();
        return entries instanceof Reference2ObjectSortedMap.FastSortedEntrySet ? ((Reference2ObjectSortedMap.FastSortedEntrySet)entries).fastIterator() : entries.iterator();
    }

    public static <K, V> ObjectBidirectionalIterable<Reference2ObjectMap.Entry<K, V>> fastIterable(Reference2ObjectSortedMap<K, V> map) {
        ObjectSet entries = map.reference2ObjectEntrySet();
        return entries instanceof Reference2ObjectSortedMap.FastSortedEntrySet ? ((Reference2ObjectSortedMap.FastSortedEntrySet)entries)::fastIterator : entries;
    }

    public static <K, V> Reference2ObjectSortedMap<K, V> emptyMap() {
        return EMPTY_MAP;
    }

    public static <K, V> Reference2ObjectSortedMap<K, V> singleton(K key, V value) {
        return new Singleton<K, V>(key, value);
    }

    public static <K, V> Reference2ObjectSortedMap<K, V> singleton(K key, V value, Comparator<? super K> comparator) {
        return new Singleton<K, V>((K)key, value, comparator);
    }

    public static <K, V> Reference2ObjectSortedMap<K, V> synchronize(Reference2ObjectSortedMap<K, V> m) {
        return new SynchronizedSortedMap<K, V>(m);
    }

    public static <K, V> Reference2ObjectSortedMap<K, V> synchronize(Reference2ObjectSortedMap<K, V> m, Object sync) {
        return new SynchronizedSortedMap<K, V>(m, sync);
    }

    public static <K, V> Reference2ObjectSortedMap<K, V> unmodifiable(Reference2ObjectSortedMap<K, V> m) {
        return new UnmodifiableSortedMap<K, V>(m);
    }

    public static class UnmodifiableSortedMap<K, V>
    extends Reference2ObjectMaps.UnmodifiableMap<K, V>
    implements Reference2ObjectSortedMap<K, V>,
    Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final Reference2ObjectSortedMap<K, V> sortedMap;

        protected UnmodifiableSortedMap(Reference2ObjectSortedMap<K, V> m) {
            super(m);
            this.sortedMap = m;
        }

        @Override
        public Comparator<? super K> comparator() {
            return this.sortedMap.comparator();
        }

        @Override
        public ObjectSortedSet<Reference2ObjectMap.Entry<K, V>> reference2ObjectEntrySet() {
            if (this.entries == null) {
                this.entries = ObjectSortedSets.unmodifiable(this.sortedMap.reference2ObjectEntrySet());
            }
            return (ObjectSortedSet)this.entries;
        }

        @Override
        public ObjectSortedSet<Map.Entry<K, V>> entrySet() {
            return this.reference2ObjectEntrySet();
        }

        @Override
        public ReferenceSortedSet<K> keySet() {
            if (this.keys == null) {
                this.keys = ReferenceSortedSets.unmodifiable(this.sortedMap.keySet());
            }
            return (ReferenceSortedSet)this.keys;
        }

        @Override
        public Reference2ObjectSortedMap<K, V> subMap(K from, K to) {
            return new UnmodifiableSortedMap<K, V>((Reference2ObjectSortedMap<K, V>)this.sortedMap.subMap((Object)from, (Object)to));
        }

        @Override
        public Reference2ObjectSortedMap<K, V> headMap(K to) {
            return new UnmodifiableSortedMap<K, V>((Reference2ObjectSortedMap<K, V>)this.sortedMap.headMap((Object)to));
        }

        @Override
        public Reference2ObjectSortedMap<K, V> tailMap(K from) {
            return new UnmodifiableSortedMap<K, V>((Reference2ObjectSortedMap<K, V>)this.sortedMap.tailMap((Object)from));
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
    extends Reference2ObjectMaps.SynchronizedMap<K, V>
    implements Reference2ObjectSortedMap<K, V>,
    Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final Reference2ObjectSortedMap<K, V> sortedMap;

        protected SynchronizedSortedMap(Reference2ObjectSortedMap<K, V> m, Object sync) {
            super(m, sync);
            this.sortedMap = m;
        }

        protected SynchronizedSortedMap(Reference2ObjectSortedMap<K, V> m) {
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
        public ObjectSortedSet<Reference2ObjectMap.Entry<K, V>> reference2ObjectEntrySet() {
            if (this.entries == null) {
                this.entries = ObjectSortedSets.synchronize(this.sortedMap.reference2ObjectEntrySet(), this.sync);
            }
            return (ObjectSortedSet)this.entries;
        }

        @Override
        public ObjectSortedSet<Map.Entry<K, V>> entrySet() {
            return this.reference2ObjectEntrySet();
        }

        @Override
        public ReferenceSortedSet<K> keySet() {
            if (this.keys == null) {
                this.keys = ReferenceSortedSets.synchronize(this.sortedMap.keySet(), this.sync);
            }
            return (ReferenceSortedSet)this.keys;
        }

        @Override
        public Reference2ObjectSortedMap<K, V> subMap(K from, K to) {
            return new SynchronizedSortedMap<K, V>((Reference2ObjectSortedMap<K, V>)this.sortedMap.subMap((Object)from, (Object)to), this.sync);
        }

        @Override
        public Reference2ObjectSortedMap<K, V> headMap(K to) {
            return new SynchronizedSortedMap<K, V>((Reference2ObjectSortedMap<K, V>)this.sortedMap.headMap((Object)to), this.sync);
        }

        @Override
        public Reference2ObjectSortedMap<K, V> tailMap(K from) {
            return new SynchronizedSortedMap<K, V>((Reference2ObjectSortedMap<K, V>)this.sortedMap.tailMap((Object)from), this.sync);
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
    extends Reference2ObjectMaps.Singleton<K, V>
    implements Reference2ObjectSortedMap<K, V>,
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
        public ObjectSortedSet<Reference2ObjectMap.Entry<K, V>> reference2ObjectEntrySet() {
            if (this.entries == null) {
                this.entries = ObjectSortedSets.singleton(new AbstractReference2ObjectMap.BasicEntry<Object, Object>(this.key, this.value), Reference2ObjectSortedMaps.entryComparator(this.comparator));
            }
            return (ObjectSortedSet)this.entries;
        }

        @Override
        public ObjectSortedSet<Map.Entry<K, V>> entrySet() {
            return this.reference2ObjectEntrySet();
        }

        @Override
        public ReferenceSortedSet<K> keySet() {
            if (this.keys == null) {
                this.keys = ReferenceSortedSets.singleton(this.key, this.comparator);
            }
            return (ReferenceSortedSet)this.keys;
        }

        @Override
        public Reference2ObjectSortedMap<K, V> subMap(K from, K to) {
            if (this.compare(from, this.key) <= 0 && this.compare(this.key, to) < 0) {
                return this;
            }
            return Reference2ObjectSortedMaps.EMPTY_MAP;
        }

        @Override
        public Reference2ObjectSortedMap<K, V> headMap(K to) {
            if (this.compare(this.key, to) < 0) {
                return this;
            }
            return Reference2ObjectSortedMaps.EMPTY_MAP;
        }

        @Override
        public Reference2ObjectSortedMap<K, V> tailMap(K from) {
            if (this.compare(from, this.key) <= 0) {
                return this;
            }
            return Reference2ObjectSortedMaps.EMPTY_MAP;
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
    extends Reference2ObjectMaps.EmptyMap<K, V>
    implements Reference2ObjectSortedMap<K, V>,
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
        public ObjectSortedSet<Reference2ObjectMap.Entry<K, V>> reference2ObjectEntrySet() {
            return ObjectSortedSets.EMPTY_SET;
        }

        @Override
        public ObjectSortedSet<Map.Entry<K, V>> entrySet() {
            return ObjectSortedSets.EMPTY_SET;
        }

        @Override
        public ReferenceSortedSet<K> keySet() {
            return ReferenceSortedSets.EMPTY_SET;
        }

        @Override
        public Reference2ObjectSortedMap<K, V> subMap(K from, K to) {
            return Reference2ObjectSortedMaps.EMPTY_MAP;
        }

        @Override
        public Reference2ObjectSortedMap<K, V> headMap(K to) {
            return Reference2ObjectSortedMaps.EMPTY_MAP;
        }

        @Override
        public Reference2ObjectSortedMap<K, V> tailMap(K from) {
            return Reference2ObjectSortedMaps.EMPTY_MAP;
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

