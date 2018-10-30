/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.doubles;

import it.unimi.dsi.fastutil.doubles.AbstractDouble2FloatMap;
import it.unimi.dsi.fastutil.doubles.Double2FloatMap;
import it.unimi.dsi.fastutil.doubles.Double2FloatMaps;
import it.unimi.dsi.fastutil.doubles.Double2FloatSortedMap;
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

public final class Double2FloatSortedMaps {
    public static final EmptySortedMap EMPTY_MAP = new EmptySortedMap();

    private Double2FloatSortedMaps() {
    }

    public static Comparator<? super Map.Entry<Double, ?>> entryComparator(DoubleComparator comparator) {
        return (x, y) -> comparator.compare((double)((Double)x.getKey()), (double)((Double)y.getKey()));
    }

    public static ObjectBidirectionalIterator<Double2FloatMap.Entry> fastIterator(Double2FloatSortedMap map) {
        ObjectSet entries = map.double2FloatEntrySet();
        return entries instanceof Double2FloatSortedMap.FastSortedEntrySet ? ((Double2FloatSortedMap.FastSortedEntrySet)entries).fastIterator() : entries.iterator();
    }

    public static ObjectBidirectionalIterable<Double2FloatMap.Entry> fastIterable(Double2FloatSortedMap map) {
        ObjectSet entries = map.double2FloatEntrySet();
        return entries instanceof Double2FloatSortedMap.FastSortedEntrySet ? ((Double2FloatSortedMap.FastSortedEntrySet)entries)::fastIterator : entries;
    }

    public static Double2FloatSortedMap singleton(Double key, Float value) {
        return new Singleton(key, value.floatValue());
    }

    public static Double2FloatSortedMap singleton(Double key, Float value, DoubleComparator comparator) {
        return new Singleton(key, value.floatValue(), comparator);
    }

    public static Double2FloatSortedMap singleton(double key, float value) {
        return new Singleton(key, value);
    }

    public static Double2FloatSortedMap singleton(double key, float value, DoubleComparator comparator) {
        return new Singleton(key, value, comparator);
    }

    public static Double2FloatSortedMap synchronize(Double2FloatSortedMap m) {
        return new SynchronizedSortedMap(m);
    }

    public static Double2FloatSortedMap synchronize(Double2FloatSortedMap m, Object sync) {
        return new SynchronizedSortedMap(m, sync);
    }

    public static Double2FloatSortedMap unmodifiable(Double2FloatSortedMap m) {
        return new UnmodifiableSortedMap(m);
    }

    public static class UnmodifiableSortedMap
    extends Double2FloatMaps.UnmodifiableMap
    implements Double2FloatSortedMap,
    Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final Double2FloatSortedMap sortedMap;

        protected UnmodifiableSortedMap(Double2FloatSortedMap m) {
            super(m);
            this.sortedMap = m;
        }

        @Override
        public DoubleComparator comparator() {
            return this.sortedMap.comparator();
        }

        @Override
        public ObjectSortedSet<Double2FloatMap.Entry> double2FloatEntrySet() {
            if (this.entries == null) {
                this.entries = ObjectSortedSets.unmodifiable(this.sortedMap.double2FloatEntrySet());
            }
            return (ObjectSortedSet)this.entries;
        }

        @Deprecated
        @Override
        public ObjectSortedSet<Map.Entry<Double, Float>> entrySet() {
            return this.double2FloatEntrySet();
        }

        @Override
        public DoubleSortedSet keySet() {
            if (this.keys == null) {
                this.keys = DoubleSortedSets.unmodifiable(this.sortedMap.keySet());
            }
            return (DoubleSortedSet)this.keys;
        }

        @Override
        public Double2FloatSortedMap subMap(double from, double to) {
            return new UnmodifiableSortedMap(this.sortedMap.subMap(from, to));
        }

        @Override
        public Double2FloatSortedMap headMap(double to) {
            return new UnmodifiableSortedMap(this.sortedMap.headMap(to));
        }

        @Override
        public Double2FloatSortedMap tailMap(double from) {
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
        public Double2FloatSortedMap subMap(Double from, Double to) {
            return new UnmodifiableSortedMap(this.sortedMap.subMap(from, to));
        }

        @Deprecated
        @Override
        public Double2FloatSortedMap headMap(Double to) {
            return new UnmodifiableSortedMap(this.sortedMap.headMap(to));
        }

        @Deprecated
        @Override
        public Double2FloatSortedMap tailMap(Double from) {
            return new UnmodifiableSortedMap(this.sortedMap.tailMap(from));
        }
    }

    public static class SynchronizedSortedMap
    extends Double2FloatMaps.SynchronizedMap
    implements Double2FloatSortedMap,
    Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final Double2FloatSortedMap sortedMap;

        protected SynchronizedSortedMap(Double2FloatSortedMap m, Object sync) {
            super(m, sync);
            this.sortedMap = m;
        }

        protected SynchronizedSortedMap(Double2FloatSortedMap m) {
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
        public ObjectSortedSet<Double2FloatMap.Entry> double2FloatEntrySet() {
            if (this.entries == null) {
                this.entries = ObjectSortedSets.synchronize(this.sortedMap.double2FloatEntrySet(), this.sync);
            }
            return (ObjectSortedSet)this.entries;
        }

        @Deprecated
        @Override
        public ObjectSortedSet<Map.Entry<Double, Float>> entrySet() {
            return this.double2FloatEntrySet();
        }

        @Override
        public DoubleSortedSet keySet() {
            if (this.keys == null) {
                this.keys = DoubleSortedSets.synchronize(this.sortedMap.keySet(), this.sync);
            }
            return (DoubleSortedSet)this.keys;
        }

        @Override
        public Double2FloatSortedMap subMap(double from, double to) {
            return new SynchronizedSortedMap(this.sortedMap.subMap(from, to), this.sync);
        }

        @Override
        public Double2FloatSortedMap headMap(double to) {
            return new SynchronizedSortedMap(this.sortedMap.headMap(to), this.sync);
        }

        @Override
        public Double2FloatSortedMap tailMap(double from) {
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
        public Double2FloatSortedMap subMap(Double from, Double to) {
            return new SynchronizedSortedMap(this.sortedMap.subMap(from, to), this.sync);
        }

        @Deprecated
        @Override
        public Double2FloatSortedMap headMap(Double to) {
            return new SynchronizedSortedMap(this.sortedMap.headMap(to), this.sync);
        }

        @Deprecated
        @Override
        public Double2FloatSortedMap tailMap(Double from) {
            return new SynchronizedSortedMap(this.sortedMap.tailMap(from), this.sync);
        }
    }

    public static class Singleton
    extends Double2FloatMaps.Singleton
    implements Double2FloatSortedMap,
    Serializable,
    Cloneable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final DoubleComparator comparator;

        protected Singleton(double key, float value, DoubleComparator comparator) {
            super(key, value);
            this.comparator = comparator;
        }

        protected Singleton(double key, float value) {
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
        public ObjectSortedSet<Double2FloatMap.Entry> double2FloatEntrySet() {
            if (this.entries == null) {
                this.entries = ObjectSortedSets.singleton(new AbstractDouble2FloatMap.BasicEntry(this.key, this.value), Double2FloatSortedMaps.entryComparator(this.comparator));
            }
            return (ObjectSortedSet)this.entries;
        }

        @Deprecated
        @Override
        public ObjectSortedSet<Map.Entry<Double, Float>> entrySet() {
            return this.double2FloatEntrySet();
        }

        @Override
        public DoubleSortedSet keySet() {
            if (this.keys == null) {
                this.keys = DoubleSortedSets.singleton(this.key, this.comparator);
            }
            return (DoubleSortedSet)this.keys;
        }

        @Override
        public Double2FloatSortedMap subMap(double from, double to) {
            if (this.compare(from, this.key) <= 0 && this.compare(this.key, to) < 0) {
                return this;
            }
            return Double2FloatSortedMaps.EMPTY_MAP;
        }

        @Override
        public Double2FloatSortedMap headMap(double to) {
            if (this.compare(this.key, to) < 0) {
                return this;
            }
            return Double2FloatSortedMaps.EMPTY_MAP;
        }

        @Override
        public Double2FloatSortedMap tailMap(double from) {
            if (this.compare(from, this.key) <= 0) {
                return this;
            }
            return Double2FloatSortedMaps.EMPTY_MAP;
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
        public Double2FloatSortedMap headMap(Double oto) {
            return this.headMap((double)oto);
        }

        @Deprecated
        @Override
        public Double2FloatSortedMap tailMap(Double ofrom) {
            return this.tailMap((double)ofrom);
        }

        @Deprecated
        @Override
        public Double2FloatSortedMap subMap(Double ofrom, Double oto) {
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
    extends Double2FloatMaps.EmptyMap
    implements Double2FloatSortedMap,
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
        public ObjectSortedSet<Double2FloatMap.Entry> double2FloatEntrySet() {
            return ObjectSortedSets.EMPTY_SET;
        }

        @Deprecated
        @Override
        public ObjectSortedSet<Map.Entry<Double, Float>> entrySet() {
            return ObjectSortedSets.EMPTY_SET;
        }

        @Override
        public DoubleSortedSet keySet() {
            return DoubleSortedSets.EMPTY_SET;
        }

        @Override
        public Double2FloatSortedMap subMap(double from, double to) {
            return Double2FloatSortedMaps.EMPTY_MAP;
        }

        @Override
        public Double2FloatSortedMap headMap(double to) {
            return Double2FloatSortedMaps.EMPTY_MAP;
        }

        @Override
        public Double2FloatSortedMap tailMap(double from) {
            return Double2FloatSortedMaps.EMPTY_MAP;
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
        public Double2FloatSortedMap headMap(Double oto) {
            return this.headMap((double)oto);
        }

        @Deprecated
        @Override
        public Double2FloatSortedMap tailMap(Double ofrom) {
            return this.tailMap((double)ofrom);
        }

        @Deprecated
        @Override
        public Double2FloatSortedMap subMap(Double ofrom, Double oto) {
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

