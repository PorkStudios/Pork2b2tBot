/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.longs.AbstractLong2ReferenceMap;
import it.unimi.dsi.fastutil.longs.Long2ReferenceMap;
import it.unimi.dsi.fastutil.longs.Long2ReferenceMaps;
import it.unimi.dsi.fastutil.longs.Long2ReferenceSortedMap;
import it.unimi.dsi.fastutil.longs.LongComparator;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.longs.LongSortedSet;
import it.unimi.dsi.fastutil.longs.LongSortedSets;
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

public final class Long2ReferenceSortedMaps {
    public static final EmptySortedMap EMPTY_MAP = new EmptySortedMap();

    private Long2ReferenceSortedMaps() {
    }

    public static Comparator<? super Map.Entry<Long, ?>> entryComparator(LongComparator comparator) {
        return (x, y) -> comparator.compare((long)((Long)x.getKey()), (long)((Long)y.getKey()));
    }

    public static <V> ObjectBidirectionalIterator<Long2ReferenceMap.Entry<V>> fastIterator(Long2ReferenceSortedMap<V> map) {
        ObjectSet entries = map.long2ReferenceEntrySet();
        return entries instanceof Long2ReferenceSortedMap.FastSortedEntrySet ? ((Long2ReferenceSortedMap.FastSortedEntrySet)entries).fastIterator() : entries.iterator();
    }

    public static <V> ObjectBidirectionalIterable<Long2ReferenceMap.Entry<V>> fastIterable(Long2ReferenceSortedMap<V> map) {
        ObjectSet entries = map.long2ReferenceEntrySet();
        return entries instanceof Long2ReferenceSortedMap.FastSortedEntrySet ? ((Long2ReferenceSortedMap.FastSortedEntrySet)entries)::fastIterator : entries;
    }

    public static <V> Long2ReferenceSortedMap<V> emptyMap() {
        return EMPTY_MAP;
    }

    public static <V> Long2ReferenceSortedMap<V> singleton(Long key, V value) {
        return new Singleton<V>(key, value);
    }

    public static <V> Long2ReferenceSortedMap<V> singleton(Long key, V value, LongComparator comparator) {
        return new Singleton<V>(key, value, comparator);
    }

    public static <V> Long2ReferenceSortedMap<V> singleton(long key, V value) {
        return new Singleton<V>(key, value);
    }

    public static <V> Long2ReferenceSortedMap<V> singleton(long key, V value, LongComparator comparator) {
        return new Singleton<V>(key, value, comparator);
    }

    public static <V> Long2ReferenceSortedMap<V> synchronize(Long2ReferenceSortedMap<V> m) {
        return new SynchronizedSortedMap<V>(m);
    }

    public static <V> Long2ReferenceSortedMap<V> synchronize(Long2ReferenceSortedMap<V> m, Object sync) {
        return new SynchronizedSortedMap<V>(m, sync);
    }

    public static <V> Long2ReferenceSortedMap<V> unmodifiable(Long2ReferenceSortedMap<V> m) {
        return new UnmodifiableSortedMap<V>(m);
    }

    public static class UnmodifiableSortedMap<V>
    extends Long2ReferenceMaps.UnmodifiableMap<V>
    implements Long2ReferenceSortedMap<V>,
    Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final Long2ReferenceSortedMap<V> sortedMap;

        protected UnmodifiableSortedMap(Long2ReferenceSortedMap<V> m) {
            super(m);
            this.sortedMap = m;
        }

        @Override
        public LongComparator comparator() {
            return this.sortedMap.comparator();
        }

        @Override
        public ObjectSortedSet<Long2ReferenceMap.Entry<V>> long2ReferenceEntrySet() {
            if (this.entries == null) {
                this.entries = ObjectSortedSets.unmodifiable(this.sortedMap.long2ReferenceEntrySet());
            }
            return (ObjectSortedSet)this.entries;
        }

        @Deprecated
        @Override
        public ObjectSortedSet<Map.Entry<Long, V>> entrySet() {
            return this.long2ReferenceEntrySet();
        }

        @Override
        public LongSortedSet keySet() {
            if (this.keys == null) {
                this.keys = LongSortedSets.unmodifiable(this.sortedMap.keySet());
            }
            return (LongSortedSet)this.keys;
        }

        @Override
        public Long2ReferenceSortedMap<V> subMap(long from, long to) {
            return new UnmodifiableSortedMap<V>(this.sortedMap.subMap(from, to));
        }

        @Override
        public Long2ReferenceSortedMap<V> headMap(long to) {
            return new UnmodifiableSortedMap<V>(this.sortedMap.headMap(to));
        }

        @Override
        public Long2ReferenceSortedMap<V> tailMap(long from) {
            return new UnmodifiableSortedMap<V>(this.sortedMap.tailMap(from));
        }

        @Override
        public long firstLongKey() {
            return this.sortedMap.firstLongKey();
        }

        @Override
        public long lastLongKey() {
            return this.sortedMap.lastLongKey();
        }

        @Deprecated
        @Override
        public Long firstKey() {
            return this.sortedMap.firstKey();
        }

        @Deprecated
        @Override
        public Long lastKey() {
            return this.sortedMap.lastKey();
        }

        @Deprecated
        @Override
        public Long2ReferenceSortedMap<V> subMap(Long from, Long to) {
            return new UnmodifiableSortedMap<V>(this.sortedMap.subMap(from, to));
        }

        @Deprecated
        @Override
        public Long2ReferenceSortedMap<V> headMap(Long to) {
            return new UnmodifiableSortedMap<V>(this.sortedMap.headMap(to));
        }

        @Deprecated
        @Override
        public Long2ReferenceSortedMap<V> tailMap(Long from) {
            return new UnmodifiableSortedMap<V>(this.sortedMap.tailMap(from));
        }
    }

    public static class SynchronizedSortedMap<V>
    extends Long2ReferenceMaps.SynchronizedMap<V>
    implements Long2ReferenceSortedMap<V>,
    Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final Long2ReferenceSortedMap<V> sortedMap;

        protected SynchronizedSortedMap(Long2ReferenceSortedMap<V> m, Object sync) {
            super(m, sync);
            this.sortedMap = m;
        }

        protected SynchronizedSortedMap(Long2ReferenceSortedMap<V> m) {
            super(m);
            this.sortedMap = m;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public LongComparator comparator() {
            Object object = this.sync;
            synchronized (object) {
                return this.sortedMap.comparator();
            }
        }

        @Override
        public ObjectSortedSet<Long2ReferenceMap.Entry<V>> long2ReferenceEntrySet() {
            if (this.entries == null) {
                this.entries = ObjectSortedSets.synchronize(this.sortedMap.long2ReferenceEntrySet(), this.sync);
            }
            return (ObjectSortedSet)this.entries;
        }

        @Deprecated
        @Override
        public ObjectSortedSet<Map.Entry<Long, V>> entrySet() {
            return this.long2ReferenceEntrySet();
        }

        @Override
        public LongSortedSet keySet() {
            if (this.keys == null) {
                this.keys = LongSortedSets.synchronize(this.sortedMap.keySet(), this.sync);
            }
            return (LongSortedSet)this.keys;
        }

        @Override
        public Long2ReferenceSortedMap<V> subMap(long from, long to) {
            return new SynchronizedSortedMap<V>(this.sortedMap.subMap(from, to), this.sync);
        }

        @Override
        public Long2ReferenceSortedMap<V> headMap(long to) {
            return new SynchronizedSortedMap<V>(this.sortedMap.headMap(to), this.sync);
        }

        @Override
        public Long2ReferenceSortedMap<V> tailMap(long from) {
            return new SynchronizedSortedMap<V>(this.sortedMap.tailMap(from), this.sync);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public long firstLongKey() {
            Object object = this.sync;
            synchronized (object) {
                return this.sortedMap.firstLongKey();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public long lastLongKey() {
            Object object = this.sync;
            synchronized (object) {
                return this.sortedMap.lastLongKey();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Deprecated
        @Override
        public Long firstKey() {
            Object object = this.sync;
            synchronized (object) {
                return this.sortedMap.firstKey();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Deprecated
        @Override
        public Long lastKey() {
            Object object = this.sync;
            synchronized (object) {
                return this.sortedMap.lastKey();
            }
        }

        @Deprecated
        @Override
        public Long2ReferenceSortedMap<V> subMap(Long from, Long to) {
            return new SynchronizedSortedMap<V>(this.sortedMap.subMap(from, to), this.sync);
        }

        @Deprecated
        @Override
        public Long2ReferenceSortedMap<V> headMap(Long to) {
            return new SynchronizedSortedMap<V>(this.sortedMap.headMap(to), this.sync);
        }

        @Deprecated
        @Override
        public Long2ReferenceSortedMap<V> tailMap(Long from) {
            return new SynchronizedSortedMap<V>(this.sortedMap.tailMap(from), this.sync);
        }
    }

    public static class Singleton<V>
    extends Long2ReferenceMaps.Singleton<V>
    implements Long2ReferenceSortedMap<V>,
    Serializable,
    Cloneable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final LongComparator comparator;

        protected Singleton(long key, V value, LongComparator comparator) {
            super(key, value);
            this.comparator = comparator;
        }

        protected Singleton(long key, V value) {
            this(key, value, null);
        }

        final int compare(long k1, long k2) {
            return this.comparator == null ? Long.compare(k1, k2) : this.comparator.compare(k1, k2);
        }

        @Override
        public LongComparator comparator() {
            return this.comparator;
        }

        @Override
        public ObjectSortedSet<Long2ReferenceMap.Entry<V>> long2ReferenceEntrySet() {
            if (this.entries == null) {
                this.entries = ObjectSortedSets.singleton(new AbstractLong2ReferenceMap.BasicEntry<Object>(this.key, this.value), Long2ReferenceSortedMaps.entryComparator(this.comparator));
            }
            return (ObjectSortedSet)this.entries;
        }

        @Deprecated
        @Override
        public ObjectSortedSet<Map.Entry<Long, V>> entrySet() {
            return this.long2ReferenceEntrySet();
        }

        @Override
        public LongSortedSet keySet() {
            if (this.keys == null) {
                this.keys = LongSortedSets.singleton(this.key, this.comparator);
            }
            return (LongSortedSet)this.keys;
        }

        @Override
        public Long2ReferenceSortedMap<V> subMap(long from, long to) {
            if (this.compare(from, this.key) <= 0 && this.compare(this.key, to) < 0) {
                return this;
            }
            return Long2ReferenceSortedMaps.EMPTY_MAP;
        }

        @Override
        public Long2ReferenceSortedMap<V> headMap(long to) {
            if (this.compare(this.key, to) < 0) {
                return this;
            }
            return Long2ReferenceSortedMaps.EMPTY_MAP;
        }

        @Override
        public Long2ReferenceSortedMap<V> tailMap(long from) {
            if (this.compare(from, this.key) <= 0) {
                return this;
            }
            return Long2ReferenceSortedMaps.EMPTY_MAP;
        }

        @Override
        public long firstLongKey() {
            return this.key;
        }

        @Override
        public long lastLongKey() {
            return this.key;
        }

        @Deprecated
        @Override
        public Long2ReferenceSortedMap<V> headMap(Long oto) {
            return this.headMap((long)oto);
        }

        @Deprecated
        @Override
        public Long2ReferenceSortedMap<V> tailMap(Long ofrom) {
            return this.tailMap((long)ofrom);
        }

        @Deprecated
        @Override
        public Long2ReferenceSortedMap<V> subMap(Long ofrom, Long oto) {
            return this.subMap((long)ofrom, (long)oto);
        }

        @Deprecated
        @Override
        public Long firstKey() {
            return this.firstLongKey();
        }

        @Deprecated
        @Override
        public Long lastKey() {
            return this.lastLongKey();
        }
    }

    public static class EmptySortedMap<V>
    extends Long2ReferenceMaps.EmptyMap<V>
    implements Long2ReferenceSortedMap<V>,
    Serializable,
    Cloneable {
        private static final long serialVersionUID = -7046029254386353129L;

        protected EmptySortedMap() {
        }

        @Override
        public LongComparator comparator() {
            return null;
        }

        @Override
        public ObjectSortedSet<Long2ReferenceMap.Entry<V>> long2ReferenceEntrySet() {
            return ObjectSortedSets.EMPTY_SET;
        }

        @Deprecated
        @Override
        public ObjectSortedSet<Map.Entry<Long, V>> entrySet() {
            return ObjectSortedSets.EMPTY_SET;
        }

        @Override
        public LongSortedSet keySet() {
            return LongSortedSets.EMPTY_SET;
        }

        @Override
        public Long2ReferenceSortedMap<V> subMap(long from, long to) {
            return Long2ReferenceSortedMaps.EMPTY_MAP;
        }

        @Override
        public Long2ReferenceSortedMap<V> headMap(long to) {
            return Long2ReferenceSortedMaps.EMPTY_MAP;
        }

        @Override
        public Long2ReferenceSortedMap<V> tailMap(long from) {
            return Long2ReferenceSortedMaps.EMPTY_MAP;
        }

        @Override
        public long firstLongKey() {
            throw new NoSuchElementException();
        }

        @Override
        public long lastLongKey() {
            throw new NoSuchElementException();
        }

        @Deprecated
        @Override
        public Long2ReferenceSortedMap<V> headMap(Long oto) {
            return this.headMap((long)oto);
        }

        @Deprecated
        @Override
        public Long2ReferenceSortedMap<V> tailMap(Long ofrom) {
            return this.tailMap((long)ofrom);
        }

        @Deprecated
        @Override
        public Long2ReferenceSortedMap<V> subMap(Long ofrom, Long oto) {
            return this.subMap((long)ofrom, (long)oto);
        }

        @Deprecated
        @Override
        public Long firstKey() {
            return this.firstLongKey();
        }

        @Deprecated
        @Override
        public Long lastKey() {
            return this.lastLongKey();
        }
    }

}

