/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.ints;

import it.unimi.dsi.fastutil.ints.AbstractInt2ByteMap;
import it.unimi.dsi.fastutil.ints.Int2ByteMap;
import it.unimi.dsi.fastutil.ints.Int2ByteMaps;
import it.unimi.dsi.fastutil.ints.Int2ByteSortedMap;
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

public final class Int2ByteSortedMaps {
    public static final EmptySortedMap EMPTY_MAP = new EmptySortedMap();

    private Int2ByteSortedMaps() {
    }

    public static Comparator<? super Map.Entry<Integer, ?>> entryComparator(IntComparator comparator) {
        return (x, y) -> comparator.compare((int)((Integer)x.getKey()), (int)((Integer)y.getKey()));
    }

    public static ObjectBidirectionalIterator<Int2ByteMap.Entry> fastIterator(Int2ByteSortedMap map) {
        ObjectSet entries = map.int2ByteEntrySet();
        return entries instanceof Int2ByteSortedMap.FastSortedEntrySet ? ((Int2ByteSortedMap.FastSortedEntrySet)entries).fastIterator() : entries.iterator();
    }

    public static ObjectBidirectionalIterable<Int2ByteMap.Entry> fastIterable(Int2ByteSortedMap map) {
        ObjectSet entries = map.int2ByteEntrySet();
        return entries instanceof Int2ByteSortedMap.FastSortedEntrySet ? ((Int2ByteSortedMap.FastSortedEntrySet)entries)::fastIterator : entries;
    }

    public static Int2ByteSortedMap singleton(Integer key, Byte value) {
        return new Singleton(key, value);
    }

    public static Int2ByteSortedMap singleton(Integer key, Byte value, IntComparator comparator) {
        return new Singleton(key, value, comparator);
    }

    public static Int2ByteSortedMap singleton(int key, byte value) {
        return new Singleton(key, value);
    }

    public static Int2ByteSortedMap singleton(int key, byte value, IntComparator comparator) {
        return new Singleton(key, value, comparator);
    }

    public static Int2ByteSortedMap synchronize(Int2ByteSortedMap m) {
        return new SynchronizedSortedMap(m);
    }

    public static Int2ByteSortedMap synchronize(Int2ByteSortedMap m, Object sync) {
        return new SynchronizedSortedMap(m, sync);
    }

    public static Int2ByteSortedMap unmodifiable(Int2ByteSortedMap m) {
        return new UnmodifiableSortedMap(m);
    }

    public static class UnmodifiableSortedMap
    extends Int2ByteMaps.UnmodifiableMap
    implements Int2ByteSortedMap,
    Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final Int2ByteSortedMap sortedMap;

        protected UnmodifiableSortedMap(Int2ByteSortedMap m) {
            super(m);
            this.sortedMap = m;
        }

        @Override
        public IntComparator comparator() {
            return this.sortedMap.comparator();
        }

        @Override
        public ObjectSortedSet<Int2ByteMap.Entry> int2ByteEntrySet() {
            if (this.entries == null) {
                this.entries = ObjectSortedSets.unmodifiable(this.sortedMap.int2ByteEntrySet());
            }
            return (ObjectSortedSet)this.entries;
        }

        @Deprecated
        @Override
        public ObjectSortedSet<Map.Entry<Integer, Byte>> entrySet() {
            return this.int2ByteEntrySet();
        }

        @Override
        public IntSortedSet keySet() {
            if (this.keys == null) {
                this.keys = IntSortedSets.unmodifiable(this.sortedMap.keySet());
            }
            return (IntSortedSet)this.keys;
        }

        @Override
        public Int2ByteSortedMap subMap(int from, int to) {
            return new UnmodifiableSortedMap(this.sortedMap.subMap(from, to));
        }

        @Override
        public Int2ByteSortedMap headMap(int to) {
            return new UnmodifiableSortedMap(this.sortedMap.headMap(to));
        }

        @Override
        public Int2ByteSortedMap tailMap(int from) {
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
        public Int2ByteSortedMap subMap(Integer from, Integer to) {
            return new UnmodifiableSortedMap(this.sortedMap.subMap(from, to));
        }

        @Deprecated
        @Override
        public Int2ByteSortedMap headMap(Integer to) {
            return new UnmodifiableSortedMap(this.sortedMap.headMap(to));
        }

        @Deprecated
        @Override
        public Int2ByteSortedMap tailMap(Integer from) {
            return new UnmodifiableSortedMap(this.sortedMap.tailMap(from));
        }
    }

    public static class SynchronizedSortedMap
    extends Int2ByteMaps.SynchronizedMap
    implements Int2ByteSortedMap,
    Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final Int2ByteSortedMap sortedMap;

        protected SynchronizedSortedMap(Int2ByteSortedMap m, Object sync) {
            super(m, sync);
            this.sortedMap = m;
        }

        protected SynchronizedSortedMap(Int2ByteSortedMap m) {
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
        public ObjectSortedSet<Int2ByteMap.Entry> int2ByteEntrySet() {
            if (this.entries == null) {
                this.entries = ObjectSortedSets.synchronize(this.sortedMap.int2ByteEntrySet(), this.sync);
            }
            return (ObjectSortedSet)this.entries;
        }

        @Deprecated
        @Override
        public ObjectSortedSet<Map.Entry<Integer, Byte>> entrySet() {
            return this.int2ByteEntrySet();
        }

        @Override
        public IntSortedSet keySet() {
            if (this.keys == null) {
                this.keys = IntSortedSets.synchronize(this.sortedMap.keySet(), this.sync);
            }
            return (IntSortedSet)this.keys;
        }

        @Override
        public Int2ByteSortedMap subMap(int from, int to) {
            return new SynchronizedSortedMap(this.sortedMap.subMap(from, to), this.sync);
        }

        @Override
        public Int2ByteSortedMap headMap(int to) {
            return new SynchronizedSortedMap(this.sortedMap.headMap(to), this.sync);
        }

        @Override
        public Int2ByteSortedMap tailMap(int from) {
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
        public Int2ByteSortedMap subMap(Integer from, Integer to) {
            return new SynchronizedSortedMap(this.sortedMap.subMap(from, to), this.sync);
        }

        @Deprecated
        @Override
        public Int2ByteSortedMap headMap(Integer to) {
            return new SynchronizedSortedMap(this.sortedMap.headMap(to), this.sync);
        }

        @Deprecated
        @Override
        public Int2ByteSortedMap tailMap(Integer from) {
            return new SynchronizedSortedMap(this.sortedMap.tailMap(from), this.sync);
        }
    }

    public static class Singleton
    extends Int2ByteMaps.Singleton
    implements Int2ByteSortedMap,
    Serializable,
    Cloneable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final IntComparator comparator;

        protected Singleton(int key, byte value, IntComparator comparator) {
            super(key, value);
            this.comparator = comparator;
        }

        protected Singleton(int key, byte value) {
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
        public ObjectSortedSet<Int2ByteMap.Entry> int2ByteEntrySet() {
            if (this.entries == null) {
                this.entries = ObjectSortedSets.singleton(new AbstractInt2ByteMap.BasicEntry(this.key, this.value), Int2ByteSortedMaps.entryComparator(this.comparator));
            }
            return (ObjectSortedSet)this.entries;
        }

        @Deprecated
        @Override
        public ObjectSortedSet<Map.Entry<Integer, Byte>> entrySet() {
            return this.int2ByteEntrySet();
        }

        @Override
        public IntSortedSet keySet() {
            if (this.keys == null) {
                this.keys = IntSortedSets.singleton(this.key, this.comparator);
            }
            return (IntSortedSet)this.keys;
        }

        @Override
        public Int2ByteSortedMap subMap(int from, int to) {
            if (this.compare(from, this.key) <= 0 && this.compare(this.key, to) < 0) {
                return this;
            }
            return Int2ByteSortedMaps.EMPTY_MAP;
        }

        @Override
        public Int2ByteSortedMap headMap(int to) {
            if (this.compare(this.key, to) < 0) {
                return this;
            }
            return Int2ByteSortedMaps.EMPTY_MAP;
        }

        @Override
        public Int2ByteSortedMap tailMap(int from) {
            if (this.compare(from, this.key) <= 0) {
                return this;
            }
            return Int2ByteSortedMaps.EMPTY_MAP;
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
        public Int2ByteSortedMap headMap(Integer oto) {
            return this.headMap((int)oto);
        }

        @Deprecated
        @Override
        public Int2ByteSortedMap tailMap(Integer ofrom) {
            return this.tailMap((int)ofrom);
        }

        @Deprecated
        @Override
        public Int2ByteSortedMap subMap(Integer ofrom, Integer oto) {
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
    extends Int2ByteMaps.EmptyMap
    implements Int2ByteSortedMap,
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
        public ObjectSortedSet<Int2ByteMap.Entry> int2ByteEntrySet() {
            return ObjectSortedSets.EMPTY_SET;
        }

        @Deprecated
        @Override
        public ObjectSortedSet<Map.Entry<Integer, Byte>> entrySet() {
            return ObjectSortedSets.EMPTY_SET;
        }

        @Override
        public IntSortedSet keySet() {
            return IntSortedSets.EMPTY_SET;
        }

        @Override
        public Int2ByteSortedMap subMap(int from, int to) {
            return Int2ByteSortedMaps.EMPTY_MAP;
        }

        @Override
        public Int2ByteSortedMap headMap(int to) {
            return Int2ByteSortedMaps.EMPTY_MAP;
        }

        @Override
        public Int2ByteSortedMap tailMap(int from) {
            return Int2ByteSortedMaps.EMPTY_MAP;
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
        public Int2ByteSortedMap headMap(Integer oto) {
            return this.headMap((int)oto);
        }

        @Deprecated
        @Override
        public Int2ByteSortedMap tailMap(Integer ofrom) {
            return this.tailMap((int)ofrom);
        }

        @Deprecated
        @Override
        public Int2ByteSortedMap subMap(Integer ofrom, Integer oto) {
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

