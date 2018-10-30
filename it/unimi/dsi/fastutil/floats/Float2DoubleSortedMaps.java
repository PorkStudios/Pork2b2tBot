/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.floats.AbstractFloat2DoubleMap;
import it.unimi.dsi.fastutil.floats.Float2DoubleMap;
import it.unimi.dsi.fastutil.floats.Float2DoubleMaps;
import it.unimi.dsi.fastutil.floats.Float2DoubleSortedMap;
import it.unimi.dsi.fastutil.floats.FloatComparator;
import it.unimi.dsi.fastutil.floats.FloatSet;
import it.unimi.dsi.fastutil.floats.FloatSortedSet;
import it.unimi.dsi.fastutil.floats.FloatSortedSets;
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

public final class Float2DoubleSortedMaps {
    public static final EmptySortedMap EMPTY_MAP = new EmptySortedMap();

    private Float2DoubleSortedMaps() {
    }

    public static Comparator<? super Map.Entry<Float, ?>> entryComparator(FloatComparator comparator) {
        return (x, y) -> comparator.compare(((Float)x.getKey()).floatValue(), ((Float)y.getKey()).floatValue());
    }

    public static ObjectBidirectionalIterator<Float2DoubleMap.Entry> fastIterator(Float2DoubleSortedMap map) {
        ObjectSet entries = map.float2DoubleEntrySet();
        return entries instanceof Float2DoubleSortedMap.FastSortedEntrySet ? ((Float2DoubleSortedMap.FastSortedEntrySet)entries).fastIterator() : entries.iterator();
    }

    public static ObjectBidirectionalIterable<Float2DoubleMap.Entry> fastIterable(Float2DoubleSortedMap map) {
        ObjectSet entries = map.float2DoubleEntrySet();
        return entries instanceof Float2DoubleSortedMap.FastSortedEntrySet ? ((Float2DoubleSortedMap.FastSortedEntrySet)entries)::fastIterator : entries;
    }

    public static Float2DoubleSortedMap singleton(Float key, Double value) {
        return new Singleton(key.floatValue(), value);
    }

    public static Float2DoubleSortedMap singleton(Float key, Double value, FloatComparator comparator) {
        return new Singleton(key.floatValue(), value, comparator);
    }

    public static Float2DoubleSortedMap singleton(float key, double value) {
        return new Singleton(key, value);
    }

    public static Float2DoubleSortedMap singleton(float key, double value, FloatComparator comparator) {
        return new Singleton(key, value, comparator);
    }

    public static Float2DoubleSortedMap synchronize(Float2DoubleSortedMap m) {
        return new SynchronizedSortedMap(m);
    }

    public static Float2DoubleSortedMap synchronize(Float2DoubleSortedMap m, Object sync) {
        return new SynchronizedSortedMap(m, sync);
    }

    public static Float2DoubleSortedMap unmodifiable(Float2DoubleSortedMap m) {
        return new UnmodifiableSortedMap(m);
    }

    public static class UnmodifiableSortedMap
    extends Float2DoubleMaps.UnmodifiableMap
    implements Float2DoubleSortedMap,
    Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final Float2DoubleSortedMap sortedMap;

        protected UnmodifiableSortedMap(Float2DoubleSortedMap m) {
            super(m);
            this.sortedMap = m;
        }

        @Override
        public FloatComparator comparator() {
            return this.sortedMap.comparator();
        }

        @Override
        public ObjectSortedSet<Float2DoubleMap.Entry> float2DoubleEntrySet() {
            if (this.entries == null) {
                this.entries = ObjectSortedSets.unmodifiable(this.sortedMap.float2DoubleEntrySet());
            }
            return (ObjectSortedSet)this.entries;
        }

        @Deprecated
        @Override
        public ObjectSortedSet<Map.Entry<Float, Double>> entrySet() {
            return this.float2DoubleEntrySet();
        }

        @Override
        public FloatSortedSet keySet() {
            if (this.keys == null) {
                this.keys = FloatSortedSets.unmodifiable(this.sortedMap.keySet());
            }
            return (FloatSortedSet)this.keys;
        }

        @Override
        public Float2DoubleSortedMap subMap(float from, float to) {
            return new UnmodifiableSortedMap(this.sortedMap.subMap(from, to));
        }

        @Override
        public Float2DoubleSortedMap headMap(float to) {
            return new UnmodifiableSortedMap(this.sortedMap.headMap(to));
        }

        @Override
        public Float2DoubleSortedMap tailMap(float from) {
            return new UnmodifiableSortedMap(this.sortedMap.tailMap(from));
        }

        @Override
        public float firstFloatKey() {
            return this.sortedMap.firstFloatKey();
        }

        @Override
        public float lastFloatKey() {
            return this.sortedMap.lastFloatKey();
        }

        @Deprecated
        @Override
        public Float firstKey() {
            return this.sortedMap.firstKey();
        }

        @Deprecated
        @Override
        public Float lastKey() {
            return this.sortedMap.lastKey();
        }

        @Deprecated
        @Override
        public Float2DoubleSortedMap subMap(Float from, Float to) {
            return new UnmodifiableSortedMap(this.sortedMap.subMap(from, to));
        }

        @Deprecated
        @Override
        public Float2DoubleSortedMap headMap(Float to) {
            return new UnmodifiableSortedMap(this.sortedMap.headMap(to));
        }

        @Deprecated
        @Override
        public Float2DoubleSortedMap tailMap(Float from) {
            return new UnmodifiableSortedMap(this.sortedMap.tailMap(from));
        }
    }

    public static class SynchronizedSortedMap
    extends Float2DoubleMaps.SynchronizedMap
    implements Float2DoubleSortedMap,
    Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final Float2DoubleSortedMap sortedMap;

        protected SynchronizedSortedMap(Float2DoubleSortedMap m, Object sync) {
            super(m, sync);
            this.sortedMap = m;
        }

        protected SynchronizedSortedMap(Float2DoubleSortedMap m) {
            super(m);
            this.sortedMap = m;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public FloatComparator comparator() {
            Object object = this.sync;
            synchronized (object) {
                return this.sortedMap.comparator();
            }
        }

        @Override
        public ObjectSortedSet<Float2DoubleMap.Entry> float2DoubleEntrySet() {
            if (this.entries == null) {
                this.entries = ObjectSortedSets.synchronize(this.sortedMap.float2DoubleEntrySet(), this.sync);
            }
            return (ObjectSortedSet)this.entries;
        }

        @Deprecated
        @Override
        public ObjectSortedSet<Map.Entry<Float, Double>> entrySet() {
            return this.float2DoubleEntrySet();
        }

        @Override
        public FloatSortedSet keySet() {
            if (this.keys == null) {
                this.keys = FloatSortedSets.synchronize(this.sortedMap.keySet(), this.sync);
            }
            return (FloatSortedSet)this.keys;
        }

        @Override
        public Float2DoubleSortedMap subMap(float from, float to) {
            return new SynchronizedSortedMap(this.sortedMap.subMap(from, to), this.sync);
        }

        @Override
        public Float2DoubleSortedMap headMap(float to) {
            return new SynchronizedSortedMap(this.sortedMap.headMap(to), this.sync);
        }

        @Override
        public Float2DoubleSortedMap tailMap(float from) {
            return new SynchronizedSortedMap(this.sortedMap.tailMap(from), this.sync);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public float firstFloatKey() {
            Object object = this.sync;
            synchronized (object) {
                return this.sortedMap.firstFloatKey();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public float lastFloatKey() {
            Object object = this.sync;
            synchronized (object) {
                return this.sortedMap.lastFloatKey();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Deprecated
        @Override
        public Float firstKey() {
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
        public Float lastKey() {
            Object object = this.sync;
            synchronized (object) {
                return this.sortedMap.lastKey();
            }
        }

        @Deprecated
        @Override
        public Float2DoubleSortedMap subMap(Float from, Float to) {
            return new SynchronizedSortedMap(this.sortedMap.subMap(from, to), this.sync);
        }

        @Deprecated
        @Override
        public Float2DoubleSortedMap headMap(Float to) {
            return new SynchronizedSortedMap(this.sortedMap.headMap(to), this.sync);
        }

        @Deprecated
        @Override
        public Float2DoubleSortedMap tailMap(Float from) {
            return new SynchronizedSortedMap(this.sortedMap.tailMap(from), this.sync);
        }
    }

    public static class Singleton
    extends Float2DoubleMaps.Singleton
    implements Float2DoubleSortedMap,
    Serializable,
    Cloneable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final FloatComparator comparator;

        protected Singleton(float key, double value, FloatComparator comparator) {
            super(key, value);
            this.comparator = comparator;
        }

        protected Singleton(float key, double value) {
            this(key, value, null);
        }

        final int compare(float k1, float k2) {
            return this.comparator == null ? Float.compare(k1, k2) : this.comparator.compare(k1, k2);
        }

        @Override
        public FloatComparator comparator() {
            return this.comparator;
        }

        @Override
        public ObjectSortedSet<Float2DoubleMap.Entry> float2DoubleEntrySet() {
            if (this.entries == null) {
                this.entries = ObjectSortedSets.singleton(new AbstractFloat2DoubleMap.BasicEntry(this.key, this.value), Float2DoubleSortedMaps.entryComparator(this.comparator));
            }
            return (ObjectSortedSet)this.entries;
        }

        @Deprecated
        @Override
        public ObjectSortedSet<Map.Entry<Float, Double>> entrySet() {
            return this.float2DoubleEntrySet();
        }

        @Override
        public FloatSortedSet keySet() {
            if (this.keys == null) {
                this.keys = FloatSortedSets.singleton(this.key, this.comparator);
            }
            return (FloatSortedSet)this.keys;
        }

        @Override
        public Float2DoubleSortedMap subMap(float from, float to) {
            if (this.compare(from, this.key) <= 0 && this.compare(this.key, to) < 0) {
                return this;
            }
            return Float2DoubleSortedMaps.EMPTY_MAP;
        }

        @Override
        public Float2DoubleSortedMap headMap(float to) {
            if (this.compare(this.key, to) < 0) {
                return this;
            }
            return Float2DoubleSortedMaps.EMPTY_MAP;
        }

        @Override
        public Float2DoubleSortedMap tailMap(float from) {
            if (this.compare(from, this.key) <= 0) {
                return this;
            }
            return Float2DoubleSortedMaps.EMPTY_MAP;
        }

        @Override
        public float firstFloatKey() {
            return this.key;
        }

        @Override
        public float lastFloatKey() {
            return this.key;
        }

        @Deprecated
        @Override
        public Float2DoubleSortedMap headMap(Float oto) {
            return this.headMap(oto.floatValue());
        }

        @Deprecated
        @Override
        public Float2DoubleSortedMap tailMap(Float ofrom) {
            return this.tailMap(ofrom.floatValue());
        }

        @Deprecated
        @Override
        public Float2DoubleSortedMap subMap(Float ofrom, Float oto) {
            return this.subMap(ofrom.floatValue(), oto.floatValue());
        }

        @Deprecated
        @Override
        public Float firstKey() {
            return Float.valueOf(this.firstFloatKey());
        }

        @Deprecated
        @Override
        public Float lastKey() {
            return Float.valueOf(this.lastFloatKey());
        }
    }

    public static class EmptySortedMap
    extends Float2DoubleMaps.EmptyMap
    implements Float2DoubleSortedMap,
    Serializable,
    Cloneable {
        private static final long serialVersionUID = -7046029254386353129L;

        protected EmptySortedMap() {
        }

        @Override
        public FloatComparator comparator() {
            return null;
        }

        @Override
        public ObjectSortedSet<Float2DoubleMap.Entry> float2DoubleEntrySet() {
            return ObjectSortedSets.EMPTY_SET;
        }

        @Deprecated
        @Override
        public ObjectSortedSet<Map.Entry<Float, Double>> entrySet() {
            return ObjectSortedSets.EMPTY_SET;
        }

        @Override
        public FloatSortedSet keySet() {
            return FloatSortedSets.EMPTY_SET;
        }

        @Override
        public Float2DoubleSortedMap subMap(float from, float to) {
            return Float2DoubleSortedMaps.EMPTY_MAP;
        }

        @Override
        public Float2DoubleSortedMap headMap(float to) {
            return Float2DoubleSortedMaps.EMPTY_MAP;
        }

        @Override
        public Float2DoubleSortedMap tailMap(float from) {
            return Float2DoubleSortedMaps.EMPTY_MAP;
        }

        @Override
        public float firstFloatKey() {
            throw new NoSuchElementException();
        }

        @Override
        public float lastFloatKey() {
            throw new NoSuchElementException();
        }

        @Deprecated
        @Override
        public Float2DoubleSortedMap headMap(Float oto) {
            return this.headMap(oto.floatValue());
        }

        @Deprecated
        @Override
        public Float2DoubleSortedMap tailMap(Float ofrom) {
            return this.tailMap(ofrom.floatValue());
        }

        @Deprecated
        @Override
        public Float2DoubleSortedMap subMap(Float ofrom, Float oto) {
            return this.subMap(ofrom.floatValue(), oto.floatValue());
        }

        @Deprecated
        @Override
        public Float firstKey() {
            return Float.valueOf(this.firstFloatKey());
        }

        @Deprecated
        @Override
        public Float lastKey() {
            return Float.valueOf(this.lastFloatKey());
        }
    }

}

