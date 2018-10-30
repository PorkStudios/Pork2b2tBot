/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.ints;

import it.unimi.dsi.fastutil.ints.AbstractInt2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntMaps;
import it.unimi.dsi.fastutil.ints.Int2IntSortedMap;
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

public final class Int2IntSortedMaps {
    public static final EmptySortedMap EMPTY_MAP = new EmptySortedMap();

    private Int2IntSortedMaps() {
    }

    public static Comparator<? super Map.Entry<Integer, ?>> entryComparator(IntComparator comparator) {
        return (x, y) -> comparator.compare((int)((Integer)x.getKey()), (int)((Integer)y.getKey()));
    }

    public static ObjectBidirectionalIterator<Int2IntMap.Entry> fastIterator(Int2IntSortedMap map) {
        ObjectSet entries = map.int2IntEntrySet();
        return entries instanceof Int2IntSortedMap.FastSortedEntrySet ? ((Int2IntSortedMap.FastSortedEntrySet)entries).fastIterator() : entries.iterator();
    }

    public static ObjectBidirectionalIterable<Int2IntMap.Entry> fastIterable(Int2IntSortedMap map) {
        ObjectSet entries = map.int2IntEntrySet();
        return entries instanceof Int2IntSortedMap.FastSortedEntrySet ? ((Int2IntSortedMap.FastSortedEntrySet)entries)::fastIterator : entries;
    }

    public static Int2IntSortedMap singleton(Integer key, Integer value) {
        return new Singleton(key, value);
    }

    public static Int2IntSortedMap singleton(Integer key, Integer value, IntComparator comparator) {
        return new Singleton(key, value, comparator);
    }

    public static Int2IntSortedMap singleton(int key, int value) {
        return new Singleton(key, value);
    }

    public static Int2IntSortedMap singleton(int key, int value, IntComparator comparator) {
        return new Singleton(key, value, comparator);
    }

    public static Int2IntSortedMap synchronize(Int2IntSortedMap m) {
        return new SynchronizedSortedMap(m);
    }

    public static Int2IntSortedMap synchronize(Int2IntSortedMap m, Object sync) {
        return new SynchronizedSortedMap(m, sync);
    }

    public static Int2IntSortedMap unmodifiable(Int2IntSortedMap m) {
        return new UnmodifiableSortedMap(m);
    }

    public static class UnmodifiableSortedMap
    extends Int2IntMaps.UnmodifiableMap
    implements Int2IntSortedMap,
    Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final Int2IntSortedMap sortedMap;

        protected UnmodifiableSortedMap(Int2IntSortedMap m) {
            super(m);
            this.sortedMap = m;
        }

        @Override
        public IntComparator comparator() {
            return this.sortedMap.comparator();
        }

        @Override
        public ObjectSortedSet<Int2IntMap.Entry> int2IntEntrySet() {
            if (this.entries == null) {
                this.entries = ObjectSortedSets.unmodifiable(this.sortedMap.int2IntEntrySet());
            }
            return (ObjectSortedSet)this.entries;
        }

        @Deprecated
        @Override
        public ObjectSortedSet<Map.Entry<Integer, Integer>> entrySet() {
            return this.int2IntEntrySet();
        }

        @Override
        public IntSortedSet keySet() {
            if (this.keys == null) {
                this.keys = IntSortedSets.unmodifiable(this.sortedMap.keySet());
            }
            return (IntSortedSet)this.keys;
        }

        @Override
        public Int2IntSortedMap subMap(int from, int to) {
            return new UnmodifiableSortedMap(this.sortedMap.subMap(from, to));
        }

        @Override
        public Int2IntSortedMap headMap(int to) {
            return new UnmodifiableSortedMap(this.sortedMap.headMap(to));
        }

        @Override
        public Int2IntSortedMap tailMap(int from) {
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
        public Int2IntSortedMap subMap(Integer from, Integer to) {
            return new UnmodifiableSortedMap(this.sortedMap.subMap(from, to));
        }

        @Deprecated
        @Override
        public Int2IntSortedMap headMap(Integer to) {
            return new UnmodifiableSortedMap(this.sortedMap.headMap(to));
        }

        @Deprecated
        @Override
        public Int2IntSortedMap tailMap(Integer from) {
            return new UnmodifiableSortedMap(this.sortedMap.tailMap(from));
        }
    }

    public static class SynchronizedSortedMap
    extends Int2IntMaps.SynchronizedMap
    implements Int2IntSortedMap,
    Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final Int2IntSortedMap sortedMap;

        protected SynchronizedSortedMap(Int2IntSortedMap m, Object sync) {
            super(m, sync);
            this.sortedMap = m;
        }

        protected SynchronizedSortedMap(Int2IntSortedMap m) {
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
        public ObjectSortedSet<Int2IntMap.Entry> int2IntEntrySet() {
            if (this.entries == null) {
                this.entries = ObjectSortedSets.synchronize(this.sortedMap.int2IntEntrySet(), this.sync);
            }
            return (ObjectSortedSet)this.entries;
        }

        @Deprecated
        @Override
        public ObjectSortedSet<Map.Entry<Integer, Integer>> entrySet() {
            return this.int2IntEntrySet();
        }

        @Override
        public IntSortedSet keySet() {
            if (this.keys == null) {
                this.keys = IntSortedSets.synchronize(this.sortedMap.keySet(), this.sync);
            }
            return (IntSortedSet)this.keys;
        }

        @Override
        public Int2IntSortedMap subMap(int from, int to) {
            return new SynchronizedSortedMap(this.sortedMap.subMap(from, to), this.sync);
        }

        @Override
        public Int2IntSortedMap headMap(int to) {
            return new SynchronizedSortedMap(this.sortedMap.headMap(to), this.sync);
        }

        @Override
        public Int2IntSortedMap tailMap(int from) {
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
        public Int2IntSortedMap subMap(Integer from, Integer to) {
            return new SynchronizedSortedMap(this.sortedMap.subMap(from, to), this.sync);
        }

        @Deprecated
        @Override
        public Int2IntSortedMap headMap(Integer to) {
            return new SynchronizedSortedMap(this.sortedMap.headMap(to), this.sync);
        }

        @Deprecated
        @Override
        public Int2IntSortedMap tailMap(Integer from) {
            return new SynchronizedSortedMap(this.sortedMap.tailMap(from), this.sync);
        }
    }

    public static class Singleton
    extends Int2IntMaps.Singleton
    implements Int2IntSortedMap,
    Serializable,
    Cloneable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final IntComparator comparator;

        protected Singleton(int key, int value, IntComparator comparator) {
            super(key, value);
            this.comparator = comparator;
        }

        protected Singleton(int key, int value) {
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
        public ObjectSortedSet<Int2IntMap.Entry> int2IntEntrySet() {
            if (this.entries == null) {
                this.entries = ObjectSortedSets.singleton(new AbstractInt2IntMap.BasicEntry(this.key, this.value), Int2IntSortedMaps.entryComparator(this.comparator));
            }
            return (ObjectSortedSet)this.entries;
        }

        @Deprecated
        @Override
        public ObjectSortedSet<Map.Entry<Integer, Integer>> entrySet() {
            return this.int2IntEntrySet();
        }

        @Override
        public IntSortedSet keySet() {
            if (this.keys == null) {
                this.keys = IntSortedSets.singleton(this.key, this.comparator);
            }
            return (IntSortedSet)this.keys;
        }

        @Override
        public Int2IntSortedMap subMap(int from, int to) {
            if (this.compare(from, this.key) <= 0 && this.compare(this.key, to) < 0) {
                return this;
            }
            return Int2IntSortedMaps.EMPTY_MAP;
        }

        @Override
        public Int2IntSortedMap headMap(int to) {
            if (this.compare(this.key, to) < 0) {
                return this;
            }
            return Int2IntSortedMaps.EMPTY_MAP;
        }

        @Override
        public Int2IntSortedMap tailMap(int from) {
            if (this.compare(from, this.key) <= 0) {
                return this;
            }
            return Int2IntSortedMaps.EMPTY_MAP;
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
        public Int2IntSortedMap headMap(Integer oto) {
            return this.headMap((int)oto);
        }

        @Deprecated
        @Override
        public Int2IntSortedMap tailMap(Integer ofrom) {
            return this.tailMap((int)ofrom);
        }

        @Deprecated
        @Override
        public Int2IntSortedMap subMap(Integer ofrom, Integer oto) {
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
    extends Int2IntMaps.EmptyMap
    implements Int2IntSortedMap,
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
        public ObjectSortedSet<Int2IntMap.Entry> int2IntEntrySet() {
            return ObjectSortedSets.EMPTY_SET;
        }

        @Deprecated
        @Override
        public ObjectSortedSet<Map.Entry<Integer, Integer>> entrySet() {
            return ObjectSortedSets.EMPTY_SET;
        }

        @Override
        public IntSortedSet keySet() {
            return IntSortedSets.EMPTY_SET;
        }

        @Override
        public Int2IntSortedMap subMap(int from, int to) {
            return Int2IntSortedMaps.EMPTY_MAP;
        }

        @Override
        public Int2IntSortedMap headMap(int to) {
            return Int2IntSortedMaps.EMPTY_MAP;
        }

        @Override
        public Int2IntSortedMap tailMap(int from) {
            return Int2IntSortedMaps.EMPTY_MAP;
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
        public Int2IntSortedMap headMap(Integer oto) {
            return this.headMap((int)oto);
        }

        @Deprecated
        @Override
        public Int2IntSortedMap tailMap(Integer ofrom) {
            return this.tailMap((int)ofrom);
        }

        @Deprecated
        @Override
        public Int2IntSortedMap subMap(Integer ofrom, Integer oto) {
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

