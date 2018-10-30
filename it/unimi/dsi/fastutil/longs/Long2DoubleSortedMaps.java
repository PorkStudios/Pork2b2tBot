/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.longs.AbstractLong2DoubleMap;
import it.unimi.dsi.fastutil.longs.Long2DoubleMap;
import it.unimi.dsi.fastutil.longs.Long2DoubleMaps;
import it.unimi.dsi.fastutil.longs.Long2DoubleSortedMap;
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

public final class Long2DoubleSortedMaps {
    public static final EmptySortedMap EMPTY_MAP = new EmptySortedMap();

    private Long2DoubleSortedMaps() {
    }

    public static Comparator<? super Map.Entry<Long, ?>> entryComparator(LongComparator comparator) {
        return (x, y) -> comparator.compare((long)((Long)x.getKey()), (long)((Long)y.getKey()));
    }

    public static ObjectBidirectionalIterator<Long2DoubleMap.Entry> fastIterator(Long2DoubleSortedMap map) {
        ObjectSet entries = map.long2DoubleEntrySet();
        return entries instanceof Long2DoubleSortedMap.FastSortedEntrySet ? ((Long2DoubleSortedMap.FastSortedEntrySet)entries).fastIterator() : entries.iterator();
    }

    public static ObjectBidirectionalIterable<Long2DoubleMap.Entry> fastIterable(Long2DoubleSortedMap map) {
        ObjectSet entries = map.long2DoubleEntrySet();
        return entries instanceof Long2DoubleSortedMap.FastSortedEntrySet ? ((Long2DoubleSortedMap.FastSortedEntrySet)entries)::fastIterator : entries;
    }

    public static Long2DoubleSortedMap singleton(Long key, Double value) {
        return new Singleton(key, value);
    }

    public static Long2DoubleSortedMap singleton(Long key, Double value, LongComparator comparator) {
        return new Singleton(key, value, comparator);
    }

    public static Long2DoubleSortedMap singleton(long key, double value) {
        return new Singleton(key, value);
    }

    public static Long2DoubleSortedMap singleton(long key, double value, LongComparator comparator) {
        return new Singleton(key, value, comparator);
    }

    public static Long2DoubleSortedMap synchronize(Long2DoubleSortedMap m) {
        return new SynchronizedSortedMap(m);
    }

    public static Long2DoubleSortedMap synchronize(Long2DoubleSortedMap m, Object sync) {
        return new SynchronizedSortedMap(m, sync);
    }

    public static Long2DoubleSortedMap unmodifiable(Long2DoubleSortedMap m) {
        return new UnmodifiableSortedMap(m);
    }

    public static class UnmodifiableSortedMap
    extends Long2DoubleMaps.UnmodifiableMap
    implements Long2DoubleSortedMap,
    Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final Long2DoubleSortedMap sortedMap;

        protected UnmodifiableSortedMap(Long2DoubleSortedMap m) {
            super(m);
            this.sortedMap = m;
        }

        @Override
        public LongComparator comparator() {
            return this.sortedMap.comparator();
        }

        @Override
        public ObjectSortedSet<Long2DoubleMap.Entry> long2DoubleEntrySet() {
            if (this.entries == null) {
                this.entries = ObjectSortedSets.unmodifiable(this.sortedMap.long2DoubleEntrySet());
            }
            return (ObjectSortedSet)this.entries;
        }

        @Deprecated
        @Override
        public ObjectSortedSet<Map.Entry<Long, Double>> entrySet() {
            return this.long2DoubleEntrySet();
        }

        @Override
        public LongSortedSet keySet() {
            if (this.keys == null) {
                this.keys = LongSortedSets.unmodifiable(this.sortedMap.keySet());
            }
            return (LongSortedSet)this.keys;
        }

        @Override
        public Long2DoubleSortedMap subMap(long from, long to) {
            return new UnmodifiableSortedMap(this.sortedMap.subMap(from, to));
        }

        @Override
        public Long2DoubleSortedMap headMap(long to) {
            return new UnmodifiableSortedMap(this.sortedMap.headMap(to));
        }

        @Override
        public Long2DoubleSortedMap tailMap(long from) {
            return new UnmodifiableSortedMap(this.sortedMap.tailMap(from));
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
        public Long2DoubleSortedMap subMap(Long from, Long to) {
            return new UnmodifiableSortedMap(this.sortedMap.subMap(from, to));
        }

        @Deprecated
        @Override
        public Long2DoubleSortedMap headMap(Long to) {
            return new UnmodifiableSortedMap(this.sortedMap.headMap(to));
        }

        @Deprecated
        @Override
        public Long2DoubleSortedMap tailMap(Long from) {
            return new UnmodifiableSortedMap(this.sortedMap.tailMap(from));
        }
    }

    public static class SynchronizedSortedMap
    extends Long2DoubleMaps.SynchronizedMap
    implements Long2DoubleSortedMap,
    Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final Long2DoubleSortedMap sortedMap;

        protected SynchronizedSortedMap(Long2DoubleSortedMap m, Object sync) {
            super(m, sync);
            this.sortedMap = m;
        }

        protected SynchronizedSortedMap(Long2DoubleSortedMap m) {
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
        public ObjectSortedSet<Long2DoubleMap.Entry> long2DoubleEntrySet() {
            if (this.entries == null) {
                this.entries = ObjectSortedSets.synchronize(this.sortedMap.long2DoubleEntrySet(), this.sync);
            }
            return (ObjectSortedSet)this.entries;
        }

        @Deprecated
        @Override
        public ObjectSortedSet<Map.Entry<Long, Double>> entrySet() {
            return this.long2DoubleEntrySet();
        }

        @Override
        public LongSortedSet keySet() {
            if (this.keys == null) {
                this.keys = LongSortedSets.synchronize(this.sortedMap.keySet(), this.sync);
            }
            return (LongSortedSet)this.keys;
        }

        @Override
        public Long2DoubleSortedMap subMap(long from, long to) {
            return new SynchronizedSortedMap(this.sortedMap.subMap(from, to), this.sync);
        }

        @Override
        public Long2DoubleSortedMap headMap(long to) {
            return new SynchronizedSortedMap(this.sortedMap.headMap(to), this.sync);
        }

        @Override
        public Long2DoubleSortedMap tailMap(long from) {
            return new SynchronizedSortedMap(this.sortedMap.tailMap(from), this.sync);
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
        public Long2DoubleSortedMap subMap(Long from, Long to) {
            return new SynchronizedSortedMap(this.sortedMap.subMap(from, to), this.sync);
        }

        @Deprecated
        @Override
        public Long2DoubleSortedMap headMap(Long to) {
            return new SynchronizedSortedMap(this.sortedMap.headMap(to), this.sync);
        }

        @Deprecated
        @Override
        public Long2DoubleSortedMap tailMap(Long from) {
            return new SynchronizedSortedMap(this.sortedMap.tailMap(from), this.sync);
        }
    }

    public static class Singleton
    extends Long2DoubleMaps.Singleton
    implements Long2DoubleSortedMap,
    Serializable,
    Cloneable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final LongComparator comparator;

        protected Singleton(long key, double value, LongComparator comparator) {
            super(key, value);
            this.comparator = comparator;
        }

        protected Singleton(long key, double value) {
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
        public ObjectSortedSet<Long2DoubleMap.Entry> long2DoubleEntrySet() {
            if (this.entries == null) {
                this.entries = ObjectSortedSets.singleton(new AbstractLong2DoubleMap.BasicEntry(this.key, this.value), Long2DoubleSortedMaps.entryComparator(this.comparator));
            }
            return (ObjectSortedSet)this.entries;
        }

        @Deprecated
        @Override
        public ObjectSortedSet<Map.Entry<Long, Double>> entrySet() {
            return this.long2DoubleEntrySet();
        }

        @Override
        public LongSortedSet keySet() {
            if (this.keys == null) {
                this.keys = LongSortedSets.singleton(this.key, this.comparator);
            }
            return (LongSortedSet)this.keys;
        }

        @Override
        public Long2DoubleSortedMap subMap(long from, long to) {
            if (this.compare(from, this.key) <= 0 && this.compare(this.key, to) < 0) {
                return this;
            }
            return Long2DoubleSortedMaps.EMPTY_MAP;
        }

        @Override
        public Long2DoubleSortedMap headMap(long to) {
            if (this.compare(this.key, to) < 0) {
                return this;
            }
            return Long2DoubleSortedMaps.EMPTY_MAP;
        }

        @Override
        public Long2DoubleSortedMap tailMap(long from) {
            if (this.compare(from, this.key) <= 0) {
                return this;
            }
            return Long2DoubleSortedMaps.EMPTY_MAP;
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
        public Long2DoubleSortedMap headMap(Long oto) {
            return this.headMap((long)oto);
        }

        @Deprecated
        @Override
        public Long2DoubleSortedMap tailMap(Long ofrom) {
            return this.tailMap((long)ofrom);
        }

        @Deprecated
        @Override
        public Long2DoubleSortedMap subMap(Long ofrom, Long oto) {
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

    public static class EmptySortedMap
    extends Long2DoubleMaps.EmptyMap
    implements Long2DoubleSortedMap,
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
        public ObjectSortedSet<Long2DoubleMap.Entry> long2DoubleEntrySet() {
            return ObjectSortedSets.EMPTY_SET;
        }

        @Deprecated
        @Override
        public ObjectSortedSet<Map.Entry<Long, Double>> entrySet() {
            return ObjectSortedSets.EMPTY_SET;
        }

        @Override
        public LongSortedSet keySet() {
            return LongSortedSets.EMPTY_SET;
        }

        @Override
        public Long2DoubleSortedMap subMap(long from, long to) {
            return Long2DoubleSortedMaps.EMPTY_MAP;
        }

        @Override
        public Long2DoubleSortedMap headMap(long to) {
            return Long2DoubleSortedMaps.EMPTY_MAP;
        }

        @Override
        public Long2DoubleSortedMap tailMap(long from) {
            return Long2DoubleSortedMaps.EMPTY_MAP;
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
        public Long2DoubleSortedMap headMap(Long oto) {
            return this.headMap((long)oto);
        }

        @Deprecated
        @Override
        public Long2DoubleSortedMap tailMap(Long ofrom) {
            return this.tailMap((long)ofrom);
        }

        @Deprecated
        @Override
        public Long2DoubleSortedMap subMap(Long ofrom, Long oto) {
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

