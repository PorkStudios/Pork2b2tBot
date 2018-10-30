/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.objects.AbstractReference2ShortMap;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterable;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import it.unimi.dsi.fastutil.objects.ObjectSortedSets;
import it.unimi.dsi.fastutil.objects.Reference2ShortMap;
import it.unimi.dsi.fastutil.objects.Reference2ShortMaps;
import it.unimi.dsi.fastutil.objects.Reference2ShortSortedMap;
import it.unimi.dsi.fastutil.objects.ReferenceSet;
import it.unimi.dsi.fastutil.objects.ReferenceSortedSet;
import it.unimi.dsi.fastutil.objects.ReferenceSortedSets;
import java.io.Serializable;
import java.util.Comparator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.SortedMap;

public final class Reference2ShortSortedMaps {
    public static final EmptySortedMap EMPTY_MAP = new EmptySortedMap();

    private Reference2ShortSortedMaps() {
    }

    public static <K> Comparator<? super Map.Entry<K, ?>> entryComparator(Comparator<? super K> comparator) {
        return (x, y) -> comparator.compare((K)x.getKey(), (K)y.getKey());
    }

    public static <K> ObjectBidirectionalIterator<Reference2ShortMap.Entry<K>> fastIterator(Reference2ShortSortedMap<K> map) {
        ObjectSet entries = map.reference2ShortEntrySet();
        return entries instanceof Reference2ShortSortedMap.FastSortedEntrySet ? ((Reference2ShortSortedMap.FastSortedEntrySet)entries).fastIterator() : entries.iterator();
    }

    public static <K> ObjectBidirectionalIterable<Reference2ShortMap.Entry<K>> fastIterable(Reference2ShortSortedMap<K> map) {
        ObjectSet entries = map.reference2ShortEntrySet();
        return entries instanceof Reference2ShortSortedMap.FastSortedEntrySet ? ((Reference2ShortSortedMap.FastSortedEntrySet)entries)::fastIterator : entries;
    }

    public static <K> Reference2ShortSortedMap<K> emptyMap() {
        return EMPTY_MAP;
    }

    public static <K> Reference2ShortSortedMap<K> singleton(K key, Short value) {
        return new Singleton<K>(key, value);
    }

    public static <K> Reference2ShortSortedMap<K> singleton(K key, Short value, Comparator<? super K> comparator) {
        return new Singleton<K>((K)key, value, comparator);
    }

    public static <K> Reference2ShortSortedMap<K> singleton(K key, short value) {
        return new Singleton<K>(key, value);
    }

    public static <K> Reference2ShortSortedMap<K> singleton(K key, short value, Comparator<? super K> comparator) {
        return new Singleton<K>((K)key, value, comparator);
    }

    public static <K> Reference2ShortSortedMap<K> synchronize(Reference2ShortSortedMap<K> m) {
        return new SynchronizedSortedMap<K>(m);
    }

    public static <K> Reference2ShortSortedMap<K> synchronize(Reference2ShortSortedMap<K> m, Object sync) {
        return new SynchronizedSortedMap<K>(m, sync);
    }

    public static <K> Reference2ShortSortedMap<K> unmodifiable(Reference2ShortSortedMap<K> m) {
        return new UnmodifiableSortedMap<K>(m);
    }

    public static class UnmodifiableSortedMap<K>
    extends Reference2ShortMaps.UnmodifiableMap<K>
    implements Reference2ShortSortedMap<K>,
    Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final Reference2ShortSortedMap<K> sortedMap;

        protected UnmodifiableSortedMap(Reference2ShortSortedMap<K> m) {
            super(m);
            this.sortedMap = m;
        }

        @Override
        public Comparator<? super K> comparator() {
            return this.sortedMap.comparator();
        }

        @Override
        public ObjectSortedSet<Reference2ShortMap.Entry<K>> reference2ShortEntrySet() {
            if (this.entries == null) {
                this.entries = ObjectSortedSets.unmodifiable(this.sortedMap.reference2ShortEntrySet());
            }
            return (ObjectSortedSet)this.entries;
        }

        @Deprecated
        @Override
        public ObjectSortedSet<Map.Entry<K, Short>> entrySet() {
            return this.reference2ShortEntrySet();
        }

        @Override
        public ReferenceSortedSet<K> keySet() {
            if (this.keys == null) {
                this.keys = ReferenceSortedSets.unmodifiable(this.sortedMap.keySet());
            }
            return (ReferenceSortedSet)this.keys;
        }

        @Override
        public Reference2ShortSortedMap<K> subMap(K from, K to) {
            return new UnmodifiableSortedMap<K>((Reference2ShortSortedMap<K>)this.sortedMap.subMap((Object)from, (Object)to));
        }

        @Override
        public Reference2ShortSortedMap<K> headMap(K to) {
            return new UnmodifiableSortedMap<K>((Reference2ShortSortedMap<K>)this.sortedMap.headMap((Object)to));
        }

        @Override
        public Reference2ShortSortedMap<K> tailMap(K from) {
            return new UnmodifiableSortedMap<K>((Reference2ShortSortedMap<K>)this.sortedMap.tailMap((Object)from));
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
    extends Reference2ShortMaps.SynchronizedMap<K>
    implements Reference2ShortSortedMap<K>,
    Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final Reference2ShortSortedMap<K> sortedMap;

        protected SynchronizedSortedMap(Reference2ShortSortedMap<K> m, Object sync) {
            super(m, sync);
            this.sortedMap = m;
        }

        protected SynchronizedSortedMap(Reference2ShortSortedMap<K> m) {
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
        public ObjectSortedSet<Reference2ShortMap.Entry<K>> reference2ShortEntrySet() {
            if (this.entries == null) {
                this.entries = ObjectSortedSets.synchronize(this.sortedMap.reference2ShortEntrySet(), this.sync);
            }
            return (ObjectSortedSet)this.entries;
        }

        @Deprecated
        @Override
        public ObjectSortedSet<Map.Entry<K, Short>> entrySet() {
            return this.reference2ShortEntrySet();
        }

        @Override
        public ReferenceSortedSet<K> keySet() {
            if (this.keys == null) {
                this.keys = ReferenceSortedSets.synchronize(this.sortedMap.keySet(), this.sync);
            }
            return (ReferenceSortedSet)this.keys;
        }

        @Override
        public Reference2ShortSortedMap<K> subMap(K from, K to) {
            return new SynchronizedSortedMap<K>((Reference2ShortSortedMap<K>)this.sortedMap.subMap((Object)from, (Object)to), this.sync);
        }

        @Override
        public Reference2ShortSortedMap<K> headMap(K to) {
            return new SynchronizedSortedMap<K>((Reference2ShortSortedMap<K>)this.sortedMap.headMap((Object)to), this.sync);
        }

        @Override
        public Reference2ShortSortedMap<K> tailMap(K from) {
            return new SynchronizedSortedMap<K>((Reference2ShortSortedMap<K>)this.sortedMap.tailMap((Object)from), this.sync);
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
    extends Reference2ShortMaps.Singleton<K>
    implements Reference2ShortSortedMap<K>,
    Serializable,
    Cloneable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final Comparator<? super K> comparator;

        protected Singleton(K key, short value, Comparator<? super K> comparator) {
            super(key, value);
            this.comparator = comparator;
        }

        protected Singleton(K key, short value) {
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
        public ObjectSortedSet<Reference2ShortMap.Entry<K>> reference2ShortEntrySet() {
            if (this.entries == null) {
                this.entries = ObjectSortedSets.singleton(new AbstractReference2ShortMap.BasicEntry<Object>(this.key, this.value), Reference2ShortSortedMaps.entryComparator(this.comparator));
            }
            return (ObjectSortedSet)this.entries;
        }

        @Deprecated
        @Override
        public ObjectSortedSet<Map.Entry<K, Short>> entrySet() {
            return this.reference2ShortEntrySet();
        }

        @Override
        public ReferenceSortedSet<K> keySet() {
            if (this.keys == null) {
                this.keys = ReferenceSortedSets.singleton(this.key, this.comparator);
            }
            return (ReferenceSortedSet)this.keys;
        }

        @Override
        public Reference2ShortSortedMap<K> subMap(K from, K to) {
            if (this.compare(from, this.key) <= 0 && this.compare(this.key, to) < 0) {
                return this;
            }
            return Reference2ShortSortedMaps.EMPTY_MAP;
        }

        @Override
        public Reference2ShortSortedMap<K> headMap(K to) {
            if (this.compare(this.key, to) < 0) {
                return this;
            }
            return Reference2ShortSortedMaps.EMPTY_MAP;
        }

        @Override
        public Reference2ShortSortedMap<K> tailMap(K from) {
            if (this.compare(from, this.key) <= 0) {
                return this;
            }
            return Reference2ShortSortedMaps.EMPTY_MAP;
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
    extends Reference2ShortMaps.EmptyMap<K>
    implements Reference2ShortSortedMap<K>,
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
        public ObjectSortedSet<Reference2ShortMap.Entry<K>> reference2ShortEntrySet() {
            return ObjectSortedSets.EMPTY_SET;
        }

        @Deprecated
        @Override
        public ObjectSortedSet<Map.Entry<K, Short>> entrySet() {
            return ObjectSortedSets.EMPTY_SET;
        }

        @Override
        public ReferenceSortedSet<K> keySet() {
            return ReferenceSortedSets.EMPTY_SET;
        }

        @Override
        public Reference2ShortSortedMap<K> subMap(K from, K to) {
            return Reference2ShortSortedMaps.EMPTY_MAP;
        }

        @Override
        public Reference2ShortSortedMap<K> headMap(K to) {
            return Reference2ShortSortedMaps.EMPTY_MAP;
        }

        @Override
        public Reference2ShortSortedMap<K> tailMap(K from) {
            return Reference2ShortSortedMaps.EMPTY_MAP;
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

