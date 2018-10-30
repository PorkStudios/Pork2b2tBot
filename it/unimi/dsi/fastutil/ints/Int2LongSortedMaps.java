/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.ints;

import it.unimi.dsi.fastutil.ints.AbstractInt2LongMap;
import it.unimi.dsi.fastutil.ints.Int2LongMap;
import it.unimi.dsi.fastutil.ints.Int2LongMaps;
import it.unimi.dsi.fastutil.ints.Int2LongSortedMap;
import it.unimi.dsi.fastutil.ints.IntComparator;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.ints.IntSortedSet;
import it.unimi.dsi.fastutil.ints.IntSortedSets;
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

public final class Int2LongSortedMaps {
    public static final EmptySortedMap EMPTY_MAP = new EmptySortedMap();

    private Int2LongSortedMaps() {
    }

    public static Comparator<? super Map.Entry<Integer, ?>> entryComparator(IntComparator comparator) {
        return (x, y) -> comparator.compare((int)((Integer)x.getKey()), (int)((Integer)y.getKey()));
    }

    public static ObjectBidirectionalIterator<Int2LongMap.Entry> fastIterator(Int2LongSortedMap map) {
        ObjectSet entries = map.int2LongEntrySet();
        return entries instanceof Int2LongSortedMap.FastSortedEntrySet ? ((Int2LongSortedMap.FastSortedEntrySet)entries).fastIterator() : entries.iterator();
    }

    public static ObjectBidirectionalIterable<Int2LongMap.Entry> fastIterable(Int2LongSortedMap map) {
        ObjectSet entries = map.int2LongEntrySet();
        return entries instanceof Int2LongSortedMap.FastSortedEntrySet ? ((Int2LongSortedMap.FastSortedEntrySet)entries)::fastIterator : entries;
    }

    public static Int2LongSortedMap singleton(Integer key, Long value) {
        return new Singleton(key, value);
    }

    public static Int2LongSortedMap singleton(Integer key, Long value, IntComparator comparator) {
        return new Singleton(key, value, comparator);
    }

    public static Int2LongSortedMap singleton(int key, long value) {
        return new Singleton(key, value);
    }

    public static Int2LongSortedMap singleton(int key, long value, IntComparator comparator) {
        return new Singleton(key, value, comparator);
    }

    public static Int2LongSortedMap synchronize(Int2LongSortedMap m) {
        return new SynchronizedSortedMap(m);
    }

    public static Int2LongSortedMap synchronize(Int2LongSortedMap m, Object sync) {
        return new SynchronizedSortedMap(m, sync);
    }

    public static Int2LongSortedMap unmodifiable(Int2LongSortedMap m) {
        return new UnmodifiableSortedMap(m);
    }

    public static class UnmodifiableSortedMap
    extends Int2LongMaps.UnmodifiableMap
    implements Int2LongSortedMap,
    Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final Int2LongSortedMap sortedMap;

        protected UnmodifiableSortedMap(Int2LongSortedMap m) {
            super(m);
            this.sortedMap = m;
        }

        @Override
        public IntComparator comparator() {
            return this.sortedMap.comparator();
        }

        @Override
        public ObjectSortedSet<Int2LongMap.Entry> int2LongEntrySet() {
            if (this.entries == null) {
                this.entries = ObjectSortedSets.unmodifiable(this.sortedMap.int2LongEntrySet());
            }
            return (ObjectSortedSet)this.entries;
        }

        @Deprecated
        @Override
        public ObjectSortedSet<Map.Entry<Integer, Long>> entrySet() {
            return this.int2LongEntrySet();
        }

        @Override
        public IntSortedSet keySet() {
            if (this.keys == null) {
                this.keys = IntSortedSets.unmodifiable(this.sortedMap.keySet());
            }
            return (IntSortedSet)this.keys;
        }

        @Override
        public Int2LongSortedMap subMap(int from, int to) {
            return new UnmodifiableSortedMap(this.sortedMap.subMap(from, to));
        }

        @Override
        public Int2LongSortedMap headMap(int to) {
            return new UnmodifiableSortedMap(this.sortedMap.headMap(to));
        }

        @Override
        public Int2LongSortedMap tailMap(int from) {
            return new UnmodifiableSortedMap(this.sortedMap.tailMap(from));
        }

        @Override
        public int firstIntKey() {
            return this.sortedMap.firstIntKey();
        }

        @Override
        public int lastIntKey() {
            return this.sortedMap.lastIntKey();
        }

        @Deprecated
        @Override
        public Integer firstKey() {
            return this.sortedMap.firstKey();
        }

        @Deprecated
        @Override
        public Integer lastKey() {
            return this.sortedMap.lastKey();
        }

        @Deprecated
        @Override
        public Int2LongSortedMap subMap(Integer from, Integer to) {
            return new UnmodifiableSortedMap(this.sortedMap.subMap(from, to));
        }

        @Deprecated
        @Override
        public Int2LongSortedMap headMap(Integer to) {
            return new UnmodifiableSortedMap(this.sortedMap.headMap(to));
        }

        @Deprecated
        @Override
        public Int2LongSortedMap tailMap(Integer from) {
            return new UnmodifiableSortedMap(this.sortedMap.tailMap(from));
        }
    }

    public static class SynchronizedSortedMap
    extends Int2LongMaps.SynchronizedMap
    implements Int2LongSortedMap,
    Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final Int2LongSortedMap sortedMap;

        protected SynchronizedSortedMap(Int2LongSortedMap m, Object sync) {
            super(m, sync);
            this.sortedMap = m;
        }

        protected SynchronizedSortedMap(Int2LongSortedMap m) {
            super(m);
            this.sortedMap = m;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public IntComparator comparator() {
            Object object = this.sync;
            synchronized (object) {
                return this.sortedMap.comparator();
            }
        }

        @Override
        public ObjectSortedSet<Int2LongMap.Entry> int2LongEntrySet() {
            if (this.entries == null) {
                this.entries = ObjectSortedSets.synchronize(this.sortedMap.int2LongEntrySet(), this.sync);
            }
            return (ObjectSortedSet)this.entries;
        }

        @Deprecated
        @Override
        public ObjectSortedSet<Map.Entry<Integer, Long>> entrySet() {
            return this.int2LongEntrySet();
        }

        @Override
        public IntSortedSet keySet() {
            if (this.keys == null) {
                this.keys = IntSortedSets.synchronize(this.sortedMap.keySet(), this.sync);
            }
            return (IntSortedSet)this.keys;
        }

        @Override
        public Int2LongSortedMap subMap(int from, int to) {
            return new SynchronizedSortedMap(this.sortedMap.subMap(from, to), this.sync);
        }

        @Override
        public Int2LongSortedMap headMap(int to) {
            return new SynchronizedSortedMap(this.sortedMap.headMap(to), this.sync);
        }

        @Override
        public Int2LongSortedMap tailMap(int from) {
            return new SynchronizedSortedMap(this.sortedMap.tailMap(from), this.sync);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public int firstIntKey() {
            Object object = this.sync;
            synchronized (object) {
                return this.sortedMap.firstIntKey();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public int lastIntKey() {
            Object object = this.sync;
            synchronized (object) {
                return this.sortedMap.lastIntKey();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Deprecated
        @Override
        public Integer firstKey() {
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
        public Integer lastKey() {
            Object object = this.sync;
            synchronized (object) {
                return this.sortedMap.lastKey();
            }
        }

        @Deprecated
        @Override
        public Int2LongSortedMap subMap(Integer from, Integer to) {
            return new SynchronizedSortedMap(this.sortedMap.subMap(from, to), this.sync);
        }

        @Deprecated
        @Override
        public Int2LongSortedMap headMap(Integer to) {
            return new SynchronizedSortedMap(this.sortedMap.headMap(to), this.sync);
        }

        @Deprecated
        @Override
        public Int2LongSortedMap tailMap(Integer from) {
            return new SynchronizedSortedMap(this.sortedMap.tailMap(from), this.sync);
        }
    }

    public static class Singleton
    extends Int2LongMaps.Singleton
    implements Int2LongSortedMap,
    Serializable,
    Cloneable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final IntComparator comparator;

        protected Singleton(int key, long value, IntComparator comparator) {
            super(key, value);
            this.comparator = comparator;
        }

        protected Singleton(int key, long value) {
            this(key, value, null);
        }

        final int compare(int k1, int k2) {
            return this.comparator == null ? Integer.compare(k1, k2) : this.comparator.compare(k1, k2);
        }

        @Override
        public IntComparator comparator() {
            return this.comparator;
        }

        @Override
        public ObjectSortedSet<Int2LongMap.Entry> int2LongEntrySet() {
            if (this.entries == null) {
                this.entries = ObjectSortedSets.singleton(new AbstractInt2LongMap.BasicEntry(this.key, this.value), Int2LongSortedMaps.entryComparator(this.comparator));
            }
            return (ObjectSortedSet)this.entries;
        }

        @Deprecated
        @Override
        public ObjectSortedSet<Map.Entry<Integer, Long>> entrySet() {
            return this.int2LongEntrySet();
        }

        @Override
        public IntSortedSet keySet() {
            if (this.keys == null) {
                this.keys = IntSortedSets.singleton(this.key, this.comparator);
            }
            return (IntSortedSet)this.keys;
        }

        @Override
        public Int2LongSortedMap subMap(int from, int to) {
            if (this.compare(from, this.key) <= 0 && this.compare(this.key, to) < 0) {
                return this;
            }
            return Int2LongSortedMaps.EMPTY_MAP;
        }

        @Override
        public Int2LongSortedMap headMap(int to) {
            if (this.compare(this.key, to) < 0) {
                return this;
            }
            return Int2LongSortedMaps.EMPTY_MAP;
        }

        @Override
        public Int2LongSortedMap tailMap(int from) {
            if (this.compare(from, this.key) <= 0) {
                return this;
            }
            return Int2LongSortedMaps.EMPTY_MAP;
        }

        @Override
        public int firstIntKey() {
            return this.key;
        }

        @Override
        public int lastIntKey() {
            return this.key;
        }

        @Deprecated
        @Override
        public Int2LongSortedMap headMap(Integer oto) {
            return this.headMap((int)oto);
        }

        @Deprecated
        @Override
        public Int2LongSortedMap tailMap(Integer ofrom) {
            return this.tailMap((int)ofrom);
        }

        @Deprecated
        @Override
        public Int2LongSortedMap subMap(Integer ofrom, Integer oto) {
            return this.subMap((int)ofrom, (int)oto);
        }

        @Deprecated
        @Override
        public Integer firstKey() {
            return this.firstIntKey();
        }

        @Deprecated
        @Override
        public Integer lastKey() {
            return this.lastIntKey();
        }
    }

    public static class EmptySortedMap
    extends Int2LongMaps.EmptyMap
    implements Int2LongSortedMap,
    Serializable,
    Cloneable {
        private static final long serialVersionUID = -7046029254386353129L;

        protected EmptySortedMap() {
        }

        @Override
        public IntComparator comparator() {
            return null;
        }

        @Override
        public ObjectSortedSet<Int2LongMap.Entry> int2LongEntrySet() {
            return ObjectSortedSets.EMPTY_SET;
        }

        @Deprecated
        @Override
        public ObjectSortedSet<Map.Entry<Integer, Long>> entrySet() {
            return ObjectSortedSets.EMPTY_SET;
        }

        @Override
        public IntSortedSet keySet() {
            return IntSortedSets.EMPTY_SET;
        }

        @Override
        public Int2LongSortedMap subMap(int from, int to) {
            return Int2LongSortedMaps.EMPTY_MAP;
        }

        @Override
        public Int2LongSortedMap headMap(int to) {
            return Int2LongSortedMaps.EMPTY_MAP;
        }

        @Override
        public Int2LongSortedMap tailMap(int from) {
            return Int2LongSortedMaps.EMPTY_MAP;
        }

        @Override
        public int firstIntKey() {
            throw new NoSuchElementException();
        }

        @Override
        public int lastIntKey() {
            throw new NoSuchElementException();
        }

        @Deprecated
        @Override
        public Int2LongSortedMap headMap(Integer oto) {
            return this.headMap((int)oto);
        }

        @Deprecated
        @Override
        public Int2LongSortedMap tailMap(Integer ofrom) {
            return this.tailMap((int)ofrom);
        }

        @Deprecated
        @Override
        public Int2LongSortedMap subMap(Integer ofrom, Integer oto) {
            return this.subMap((int)ofrom, (int)oto);
        }

        @Deprecated
        @Override
        public Integer firstKey() {
            return this.firstIntKey();
        }

        @Deprecated
        @Override
        public Integer lastKey() {
            return this.lastIntKey();
        }
    }

}

