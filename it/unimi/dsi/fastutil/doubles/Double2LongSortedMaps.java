/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.doubles;

import it.unimi.dsi.fastutil.doubles.AbstractDouble2LongMap;
import it.unimi.dsi.fastutil.doubles.Double2LongMap;
import it.unimi.dsi.fastutil.doubles.Double2LongMaps;
import it.unimi.dsi.fastutil.doubles.Double2LongSortedMap;
import it.unimi.dsi.fastutil.doubles.DoubleComparator;
import it.unimi.dsi.fastutil.doubles.DoubleSet;
import it.unimi.dsi.fastutil.doubles.DoubleSortedSet;
import it.unimi.dsi.fastutil.doubles.DoubleSortedSets;
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

public final class Double2LongSortedMaps {
    public static final EmptySortedMap EMPTY_MAP = new EmptySortedMap();

    private Double2LongSortedMaps() {
    }

    public static Comparator<? super Map.Entry<Double, ?>> entryComparator(DoubleComparator comparator) {
        return (x, y) -> comparator.compare((double)((Double)x.getKey()), (double)((Double)y.getKey()));
    }

    public static ObjectBidirectionalIterator<Double2LongMap.Entry> fastIterator(Double2LongSortedMap map) {
        ObjectSet entries = map.double2LongEntrySet();
        return entries instanceof Double2LongSortedMap.FastSortedEntrySet ? ((Double2LongSortedMap.FastSortedEntrySet)entries).fastIterator() : entries.iterator();
    }

    public static ObjectBidirectionalIterable<Double2LongMap.Entry> fastIterable(Double2LongSortedMap map) {
        ObjectSet entries = map.double2LongEntrySet();
        return entries instanceof Double2LongSortedMap.FastSortedEntrySet ? ((Double2LongSortedMap.FastSortedEntrySet)entries)::fastIterator : entries;
    }

    public static Double2LongSortedMap singleton(Double key, Long value) {
        return new Singleton(key, value);
    }

    public static Double2LongSortedMap singleton(Double key, Long value, DoubleComparator comparator) {
        return new Singleton(key, value, comparator);
    }

    public static Double2LongSortedMap singleton(double key, long value) {
        return new Singleton(key, value);
    }

    public static Double2LongSortedMap singleton(double key, long value, DoubleComparator comparator) {
        return new Singleton(key, value, comparator);
    }

    public static Double2LongSortedMap synchronize(Double2LongSortedMap m) {
        return new SynchronizedSortedMap(m);
    }

    public static Double2LongSortedMap synchronize(Double2LongSortedMap m, Object sync) {
        return new SynchronizedSortedMap(m, sync);
    }

    public static Double2LongSortedMap unmodifiable(Double2LongSortedMap m) {
        return new UnmodifiableSortedMap(m);
    }

    public static class UnmodifiableSortedMap
    extends Double2LongMaps.UnmodifiableMap
    implements Double2LongSortedMap,
    Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final Double2LongSortedMap sortedMap;

        protected UnmodifiableSortedMap(Double2LongSortedMap m) {
            super(m);
            this.sortedMap = m;
        }

        @Override
        public DoubleComparator comparator() {
            return this.sortedMap.comparator();
        }

        @Override
        public ObjectSortedSet<Double2LongMap.Entry> double2LongEntrySet() {
            if (this.entries == null) {
                this.entries = ObjectSortedSets.unmodifiable(this.sortedMap.double2LongEntrySet());
            }
            return (ObjectSortedSet)this.entries;
        }

        @Deprecated
        @Override
        public ObjectSortedSet<Map.Entry<Double, Long>> entrySet() {
            return this.double2LongEntrySet();
        }

        @Override
        public DoubleSortedSet keySet() {
            if (this.keys == null) {
                this.keys = DoubleSortedSets.unmodifiable(this.sortedMap.keySet());
            }
            return (DoubleSortedSet)this.keys;
        }

        @Override
        public Double2LongSortedMap subMap(double from, double to) {
            return new UnmodifiableSortedMap(this.sortedMap.subMap(from, to));
        }

        @Override
        public Double2LongSortedMap headMap(double to) {
            return new UnmodifiableSortedMap(this.sortedMap.headMap(to));
        }

        @Override
        public Double2LongSortedMap tailMap(double from) {
            return new UnmodifiableSortedMap(this.sortedMap.tailMap(from));
        }

        @Override
        public double firstDoubleKey() {
            return this.sortedMap.firstDoubleKey();
        }

        @Override
        public double lastDoubleKey() {
            return this.sortedMap.lastDoubleKey();
        }

        @Deprecated
        @Override
        public Double firstKey() {
            return this.sortedMap.firstKey();
        }

        @Deprecated
        @Override
        public Double lastKey() {
            return this.sortedMap.lastKey();
        }

        @Deprecated
        @Override
        public Double2LongSortedMap subMap(Double from, Double to) {
            return new UnmodifiableSortedMap(this.sortedMap.subMap(from, to));
        }

        @Deprecated
        @Override
        public Double2LongSortedMap headMap(Double to) {
            return new UnmodifiableSortedMap(this.sortedMap.headMap(to));
        }

        @Deprecated
        @Override
        public Double2LongSortedMap tailMap(Double from) {
            return new UnmodifiableSortedMap(this.sortedMap.tailMap(from));
        }
    }

    public static class SynchronizedSortedMap
    extends Double2LongMaps.SynchronizedMap
    implements Double2LongSortedMap,
    Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final Double2LongSortedMap sortedMap;

        protected SynchronizedSortedMap(Double2LongSortedMap m, Object sync) {
            super(m, sync);
            this.sortedMap = m;
        }

        protected SynchronizedSortedMap(Double2LongSortedMap m) {
            super(m);
            this.sortedMap = m;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public DoubleComparator comparator() {
            Object object = this.sync;
            synchronized (object) {
                return this.sortedMap.comparator();
            }
        }

        @Override
        public ObjectSortedSet<Double2LongMap.Entry> double2LongEntrySet() {
            if (this.entries == null) {
                this.entries = ObjectSortedSets.synchronize(this.sortedMap.double2LongEntrySet(), this.sync);
            }
            return (ObjectSortedSet)this.entries;
        }

        @Deprecated
        @Override
        public ObjectSortedSet<Map.Entry<Double, Long>> entrySet() {
            return this.double2LongEntrySet();
        }

        @Override
        public DoubleSortedSet keySet() {
            if (this.keys == null) {
                this.keys = DoubleSortedSets.synchronize(this.sortedMap.keySet(), this.sync);
            }
            return (DoubleSortedSet)this.keys;
        }

        @Override
        public Double2LongSortedMap subMap(double from, double to) {
            return new SynchronizedSortedMap(this.sortedMap.subMap(from, to), this.sync);
        }

        @Override
        public Double2LongSortedMap headMap(double to) {
            return new SynchronizedSortedMap(this.sortedMap.headMap(to), this.sync);
        }

        @Override
        public Double2LongSortedMap tailMap(double from) {
            return new SynchronizedSortedMap(this.sortedMap.tailMap(from), this.sync);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public double firstDoubleKey() {
            Object object = this.sync;
            synchronized (object) {
                return this.sortedMap.firstDoubleKey();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public double lastDoubleKey() {
            Object object = this.sync;
            synchronized (object) {
                return this.sortedMap.lastDoubleKey();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Deprecated
        @Override
        public Double firstKey() {
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
        public Double lastKey() {
            Object object = this.sync;
            synchronized (object) {
                return this.sortedMap.lastKey();
            }
        }

        @Deprecated
        @Override
        public Double2LongSortedMap subMap(Double from, Double to) {
            return new SynchronizedSortedMap(this.sortedMap.subMap(from, to), this.sync);
        }

        @Deprecated
        @Override
        public Double2LongSortedMap headMap(Double to) {
            return new SynchronizedSortedMap(this.sortedMap.headMap(to), this.sync);
        }

        @Deprecated
        @Override
        public Double2LongSortedMap tailMap(Double from) {
            return new SynchronizedSortedMap(this.sortedMap.tailMap(from), this.sync);
        }
    }

    public static class Singleton
    extends Double2LongMaps.Singleton
    implements Double2LongSortedMap,
    Serializable,
    Cloneable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final DoubleComparator comparator;

        protected Singleton(double key, long value, DoubleComparator comparator) {
            super(key, value);
            this.comparator = comparator;
        }

        protected Singleton(double key, long value) {
            this(key, value, null);
        }

        final int compare(double k1, double k2) {
            return this.comparator == null ? Double.compare(k1, k2) : this.comparator.compare(k1, k2);
        }

        @Override
        public DoubleComparator comparator() {
            return this.comparator;
        }

        @Override
        public ObjectSortedSet<Double2LongMap.Entry> double2LongEntrySet() {
            if (this.entries == null) {
                this.entries = ObjectSortedSets.singleton(new AbstractDouble2LongMap.BasicEntry(this.key, this.value), Double2LongSortedMaps.entryComparator(this.comparator));
            }
            return (ObjectSortedSet)this.entries;
        }

        @Deprecated
        @Override
        public ObjectSortedSet<Map.Entry<Double, Long>> entrySet() {
            return this.double2LongEntrySet();
        }

        @Override
        public DoubleSortedSet keySet() {
            if (this.keys == null) {
                this.keys = DoubleSortedSets.singleton(this.key, this.comparator);
            }
            return (DoubleSortedSet)this.keys;
        }

        @Override
        public Double2LongSortedMap subMap(double from, double to) {
            if (this.compare(from, this.key) <= 0 && this.compare(this.key, to) < 0) {
                return this;
            }
            return Double2LongSortedMaps.EMPTY_MAP;
        }

        @Override
        public Double2LongSortedMap headMap(double to) {
            if (this.compare(this.key, to) < 0) {
                return this;
            }
            return Double2LongSortedMaps.EMPTY_MAP;
        }

        @Override
        public Double2LongSortedMap tailMap(double from) {
            if (this.compare(from, this.key) <= 0) {
                return this;
            }
            return Double2LongSortedMaps.EMPTY_MAP;
        }

        @Override
        public double firstDoubleKey() {
            return this.key;
        }

        @Override
        public double lastDoubleKey() {
            return this.key;
        }

        @Deprecated
        @Override
        public Double2LongSortedMap headMap(Double oto) {
            return this.headMap((double)oto);
        }

        @Deprecated
        @Override
        public Double2LongSortedMap tailMap(Double ofrom) {
            return this.tailMap((double)ofrom);
        }

        @Deprecated
        @Override
        public Double2LongSortedMap subMap(Double ofrom, Double oto) {
            return this.subMap((double)ofrom, (double)oto);
        }

        @Deprecated
        @Override
        public Double firstKey() {
            return this.firstDoubleKey();
        }

        @Deprecated
        @Override
        public Double lastKey() {
            return this.lastDoubleKey();
        }
    }

    public static class EmptySortedMap
    extends Double2LongMaps.EmptyMap
    implements Double2LongSortedMap,
    Serializable,
    Cloneable {
        private static final long serialVersionUID = -7046029254386353129L;

        protected EmptySortedMap() {
        }

        @Override
        public DoubleComparator comparator() {
            return null;
        }

        @Override
        public ObjectSortedSet<Double2LongMap.Entry> double2LongEntrySet() {
            return ObjectSortedSets.EMPTY_SET;
        }

        @Deprecated
        @Override
        public ObjectSortedSet<Map.Entry<Double, Long>> entrySet() {
            return ObjectSortedSets.EMPTY_SET;
        }

        @Override
        public DoubleSortedSet keySet() {
            return DoubleSortedSets.EMPTY_SET;
        }

        @Override
        public Double2LongSortedMap subMap(double from, double to) {
            return Double2LongSortedMaps.EMPTY_MAP;
        }

        @Override
        public Double2LongSortedMap headMap(double to) {
            return Double2LongSortedMaps.EMPTY_MAP;
        }

        @Override
        public Double2LongSortedMap tailMap(double from) {
            return Double2LongSortedMaps.EMPTY_MAP;
        }

        @Override
        public double firstDoubleKey() {
            throw new NoSuchElementException();
        }

        @Override
        public double lastDoubleKey() {
            throw new NoSuchElementException();
        }

        @Deprecated
        @Override
        public Double2LongSortedMap headMap(Double oto) {
            return this.headMap((double)oto);
        }

        @Deprecated
        @Override
        public Double2LongSortedMap tailMap(Double ofrom) {
            return this.tailMap((double)ofrom);
        }

        @Deprecated
        @Override
        public Double2LongSortedMap subMap(Double ofrom, Double oto) {
            return this.subMap((double)ofrom, (double)oto);
        }

        @Deprecated
        @Override
        public Double firstKey() {
            return this.firstDoubleKey();
        }

        @Deprecated
        @Override
        public Double lastKey() {
            return this.lastDoubleKey();
        }
    }

}

