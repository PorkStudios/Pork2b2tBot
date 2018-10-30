/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.objects.AbstractReference2LongMap;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterable;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import it.unimi.dsi.fastutil.objects.ObjectSortedSets;
import it.unimi.dsi.fastutil.objects.Reference2LongMap;
import it.unimi.dsi.fastutil.objects.Reference2LongMaps;
import it.unimi.dsi.fastutil.objects.Reference2LongSortedMap;
import it.unimi.dsi.fastutil.objects.ReferenceSet;
import it.unimi.dsi.fastutil.objects.ReferenceSortedSet;
import it.unimi.dsi.fastutil.objects.ReferenceSortedSets;
import java.io.Serializable;
import java.util.Comparator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.SortedMap;

public final class Reference2LongSortedMaps {
    public static final EmptySortedMap EMPTY_MAP = new EmptySortedMap();

    private Reference2LongSortedMaps() {
    }

    public static <K> Comparator<? super Map.Entry<K, ?>> entryComparator(Comparator<? super K> comparator) {
        return (x, y) -> comparator.compare((K)x.getKey(), (K)y.getKey());
    }

    public static <K> ObjectBidirectionalIterator<Reference2LongMap.Entry<K>> fastIterator(Reference2LongSortedMap<K> map) {
        ObjectSet entries = map.reference2LongEntrySet();
        return entries instanceof Reference2LongSortedMap.FastSortedEntrySet ? ((Reference2LongSortedMap.FastSortedEntrySet)entries).fastIterator() : entries.iterator();
    }

    public static <K> ObjectBidirectionalIterable<Reference2LongMap.Entry<K>> fastIterable(Reference2LongSortedMap<K> map) {
        ObjectSet entries = map.reference2LongEntrySet();
        return entries instanceof Reference2LongSortedMap.FastSortedEntrySet ? ((Reference2LongSortedMap.FastSortedEntrySet)entries)::fastIterator : entries;
    }

    public static <K> Reference2LongSortedMap<K> emptyMap() {
        return EMPTY_MAP;
    }

    public static <K> Reference2LongSortedMap<K> singleton(K key, Long value) {
        return new Singleton<K>(key, value);
    }

    public static <K> Reference2LongSortedMap<K> singleton(K key, Long value, Comparator<? super K> comparator) {
        return new Singleton<K>((K)key, value, comparator);
    }

    public static <K> Reference2LongSortedMap<K> singleton(K key, long value) {
        return new Singleton<K>(key, value);
    }

    public static <K> Reference2LongSortedMap<K> singleton(K key, long value, Comparator<? super K> comparator) {
        return new Singleton<K>((K)key, value, comparator);
    }

    public static <K> Reference2LongSortedMap<K> synchronize(Reference2LongSortedMap<K> m) {
        return new SynchronizedSortedMap<K>(m);
    }

    public static <K> Reference2LongSortedMap<K> synchronize(Reference2LongSortedMap<K> m, Object sync) {
        return new SynchronizedSortedMap<K>(m, sync);
    }

    public static <K> Reference2LongSortedMap<K> unmodifiable(Reference2LongSortedMap<K> m) {
        return new UnmodifiableSortedMap<K>(m);
    }

    public static class UnmodifiableSortedMap<K>
    extends Reference2LongMaps.UnmodifiableMap<K>
    implements Reference2LongSortedMap<K>,
    Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final Reference2LongSortedMap<K> sortedMap;

        protected UnmodifiableSortedMap(Reference2LongSortedMap<K> m) {
            super(m);
            this.sortedMap = m;
        }

        @Override
        public Comparator<? super K> comparator() {
            return this.sortedMap.comparator();
        }

        @Override
        public ObjectSortedSet<Reference2LongMap.Entry<K>> reference2LongEntrySet() {
            if (this.entries == null) {
                this.entries = ObjectSortedSets.unmodifiable(this.sortedMap.reference2LongEntrySet());
            }
            return (ObjectSortedSet)this.entries;
        }

        @Deprecated
        @Override
        public ObjectSortedSet<Map.Entry<K, Long>> entrySet() {
            return this.reference2LongEntrySet();
        }

        @Override
        public ReferenceSortedSet<K> keySet() {
            if (this.keys == null) {
                this.keys = ReferenceSortedSets.unmodifiable(this.sortedMap.keySet());
            }
            return (ReferenceSortedSet)this.keys;
        }

        @Override
        public Reference2LongSortedMap<K> subMap(K from, K to) {
            return new UnmodifiableSortedMap<K>((Reference2LongSortedMap<K>)this.sortedMap.subMap((Object)from, (Object)to));
        }

        @Override
        public Reference2LongSortedMap<K> headMap(K to) {
            return new UnmodifiableSortedMap<K>((Reference2LongSortedMap<K>)this.sortedMap.headMap((Object)to));
        }

        @Override
        public Reference2LongSortedMap<K> tailMap(K from) {
            return new UnmodifiableSortedMap<K>((Reference2LongSortedMap<K>)this.sortedMap.tailMap((Object)from));
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

    public static class SynchronizedSortedMap<K>
    extends Reference2LongMaps.SynchronizedMap<K>
    implements Reference2LongSortedMap<K>,
    Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final Reference2LongSortedMap<K> sortedMap;

        protected SynchronizedSortedMap(Reference2LongSortedMap<K> m, Object sync) {
            super(m, sync);
            this.sortedMap = m;
        }

        protected SynchronizedSortedMap(Reference2LongSortedMap<K> m) {
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
        public ObjectSortedSet<Reference2LongMap.Entry<K>> reference2LongEntrySet() {
            if (this.entries == null) {
                this.entries = ObjectSortedSets.synchronize(this.sortedMap.reference2LongEntrySet(), this.sync);
            }
            return (ObjectSortedSet)this.entries;
        }

        @Deprecated
        @Override
        public ObjectSortedSet<Map.Entry<K, Long>> entrySet() {
            return this.reference2LongEntrySet();
        }

        @Override
        public ReferenceSortedSet<K> keySet() {
            if (this.keys == null) {
                this.keys = ReferenceSortedSets.synchronize(this.sortedMap.keySet(), this.sync);
            }
            return (ReferenceSortedSet)this.keys;
        }

        @Override
        public Reference2LongSortedMap<K> subMap(K from, K to) {
            return new SynchronizedSortedMap<K>((Reference2LongSortedMap<K>)this.sortedMap.subMap((Object)from, (Object)to), this.sync);
        }

        @Override
        public Reference2LongSortedMap<K> headMap(K to) {
            return new SynchronizedSortedMap<K>((Reference2LongSortedMap<K>)this.sortedMap.headMap((Object)to), this.sync);
        }

        @Override
        public Reference2LongSortedMap<K> tailMap(K from) {
            return new SynchronizedSortedMap<K>((Reference2LongSortedMap<K>)this.sortedMap.tailMap((Object)from), this.sync);
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

    public static class Singleton<K>
    extends Reference2LongMaps.Singleton<K>
    implements Reference2LongSortedMap<K>,
    Serializable,
    Cloneable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final Comparator<? super K> comparator;

        protected Singleton(K key, long value, Comparator<? super K> comparator) {
            super(key, value);
            this.comparator = comparator;
        }

        protected Singleton(K key, long value) {
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
        public ObjectSortedSet<Reference2LongMap.Entry<K>> reference2LongEntrySet() {
            if (this.entries == null) {
                this.entries = ObjectSortedSets.singleton(new AbstractReference2LongMap.BasicEntry<Object>(this.key, this.value), Reference2LongSortedMaps.entryComparator(this.comparator));
            }
            return (ObjectSortedSet)this.entries;
        }

        @Deprecated
        @Override
        public ObjectSortedSet<Map.Entry<K, Long>> entrySet() {
            return this.reference2LongEntrySet();
        }

        @Override
        public ReferenceSortedSet<K> keySet() {
            if (this.keys == null) {
                this.keys = ReferenceSortedSets.singleton(this.key, this.comparator);
            }
            return (ReferenceSortedSet)this.keys;
        }

        @Override
        public Reference2LongSortedMap<K> subMap(K from, K to) {
            if (this.compare(from, this.key) <= 0 && this.compare(this.key, to) < 0) {
                return this;
            }
            return Reference2LongSortedMaps.EMPTY_MAP;
        }

        @Override
        public Reference2LongSortedMap<K> headMap(K to) {
            if (this.compare(this.key, to) < 0) {
                return this;
            }
            return Reference2LongSortedMaps.EMPTY_MAP;
        }

        @Override
        public Reference2LongSortedMap<K> tailMap(K from) {
            if (this.compare(from, this.key) <= 0) {
                return this;
            }
            return Reference2LongSortedMaps.EMPTY_MAP;
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

    public static class EmptySortedMap<K>
    extends Reference2LongMaps.EmptyMap<K>
    implements Reference2LongSortedMap<K>,
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
        public ObjectSortedSet<Reference2LongMap.Entry<K>> reference2LongEntrySet() {
            return ObjectSortedSets.EMPTY_SET;
        }

        @Deprecated
        @Override
        public ObjectSortedSet<Map.Entry<K, Long>> entrySet() {
            return ObjectSortedSets.EMPTY_SET;
        }

        @Override
        public ReferenceSortedSet<K> keySet() {
            return ReferenceSortedSets.EMPTY_SET;
        }

        @Override
        public Reference2LongSortedMap<K> subMap(K from, K to) {
            return Reference2LongSortedMaps.EMPTY_MAP;
        }

        @Override
        public Reference2LongSortedMap<K> headMap(K to) {
            return Reference2LongSortedMaps.EMPTY_MAP;
        }

        @Override
        public Reference2LongSortedMap<K> tailMap(K from) {
            return Reference2LongSortedMaps.EMPTY_MAP;
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

