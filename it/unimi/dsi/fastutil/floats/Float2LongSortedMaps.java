/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.floats.AbstractFloat2LongMap;
import it.unimi.dsi.fastutil.floats.Float2LongMap;
import it.unimi.dsi.fastutil.floats.Float2LongMaps;
import it.unimi.dsi.fastutil.floats.Float2LongSortedMap;
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

public final class Float2LongSortedMaps {
    public static final EmptySortedMap EMPTY_MAP = new EmptySortedMap();

    private Float2LongSortedMaps() {
    }

    public static Comparator<? super Map.Entry<Float, ?>> entryComparator(FloatComparator comparator) {
        return (x, y) -> comparator.compare(((Float)x.getKey()).floatValue(), ((Float)y.getKey()).floatValue());
    }

    public static ObjectBidirectionalIterator<Float2LongMap.Entry> fastIterator(Float2LongSortedMap map) {
        ObjectSet entries = map.float2LongEntrySet();
        return entries instanceof Float2LongSortedMap.FastSortedEntrySet ? ((Float2LongSortedMap.FastSortedEntrySet)entries).fastIterator() : entries.iterator();
    }

    public static ObjectBidirectionalIterable<Float2LongMap.Entry> fastIterable(Float2LongSortedMap map) {
        ObjectSet entries = map.float2LongEntrySet();
        return entries instanceof Float2LongSortedMap.FastSortedEntrySet ? ((Float2LongSortedMap.FastSortedEntrySet)entries)::fastIterator : entries;
    }

    public static Float2LongSortedMap singleton(Float key, Long value) {
        return new Singleton(key.floatValue(), value);
    }

    public static Float2LongSortedMap singleton(Float key, Long value, FloatComparator comparator) {
        return new Singleton(key.floatValue(), value, comparator);
    }

    public static Float2LongSortedMap singleton(float key, long value) {
        return new Singleton(key, value);
    }

    public static Float2LongSortedMap singleton(float key, long value, FloatComparator comparator) {
        return new Singleton(key, value, comparator);
    }

    public static Float2LongSortedMap synchronize(Float2LongSortedMap m) {
        return new SynchronizedSortedMap(m);
    }

    public static Float2LongSortedMap synchronize(Float2LongSortedMap m, Object sync) {
        return new SynchronizedSortedMap(m, sync);
    }

    public static Float2LongSortedMap unmodifiable(Float2LongSortedMap m) {
        return new UnmodifiableSortedMap(m);
    }

    public static class UnmodifiableSortedMap
    extends Float2LongMaps.UnmodifiableMap
    implements Float2LongSortedMap,
    Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final Float2LongSortedMap sortedMap;

        protected UnmodifiableSortedMap(Float2LongSortedMap m) {
            super(m);
            this.sortedMap = m;
        }

        @Override
        public FloatComparator comparator() {
            return this.sortedMap.comparator();
        }

        @Override
        public ObjectSortedSet<Float2LongMap.Entry> float2LongEntrySet() {
            if (this.entries == null) {
                this.entries = ObjectSortedSets.unmodifiable(this.sortedMap.float2LongEntrySet());
            }
            return (ObjectSortedSet)this.entries;
        }

        @Deprecated
        @Override
        public ObjectSortedSet<Map.Entry<Float, Long>> entrySet() {
            return this.float2LongEntrySet();
        }

        @Override
        public FloatSortedSet keySet() {
            if (this.keys == null) {
                this.keys = FloatSortedSets.unmodifiable(this.sortedMap.keySet());
            }
            return (FloatSortedSet)this.keys;
        }

        @Override
        public Float2LongSortedMap subMap(float from, float to) {
            return new UnmodifiableSortedMap(this.sortedMap.subMap(from, to));
        }

        @Override
        public Float2LongSortedMap headMap(float to) {
            return new UnmodifiableSortedMap(this.sortedMap.headMap(to));
        }

        @Override
        public Float2LongSortedMap tailMap(float from) {
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
        public Float2LongSortedMap subMap(Float from, Float to) {
            return new UnmodifiableSortedMap(this.sortedMap.subMap(from, to));
        }

        @Deprecated
        @Override
        public Float2LongSortedMap headMap(Float to) {
            return new UnmodifiableSortedMap(this.sortedMap.headMap(to));
        }

        @Deprecated
        @Override
        public Float2LongSortedMap tailMap(Float from) {
            return new UnmodifiableSortedMap(this.sortedMap.tailMap(from));
        }
    }

    public static class SynchronizedSortedMap
    extends Float2LongMaps.SynchronizedMap
    implements Float2LongSortedMap,
    Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final Float2LongSortedMap sortedMap;

        protected SynchronizedSortedMap(Float2LongSortedMap m, Object sync) {
            super(m, sync);
            this.sortedMap = m;
        }

        protected SynchronizedSortedMap(Float2LongSortedMap m) {
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
        public ObjectSortedSet<Float2LongMap.Entry> float2LongEntrySet() {
            if (this.entries == null) {
                this.entries = ObjectSortedSets.synchronize(this.sortedMap.float2LongEntrySet(), this.sync);
            }
            return (ObjectSortedSet)this.entries;
        }

        @Deprecated
        @Override
        public ObjectSortedSet<Map.Entry<Float, Long>> entrySet() {
            return this.float2LongEntrySet();
        }

        @Override
        public FloatSortedSet keySet() {
            if (this.keys == null) {
                this.keys = FloatSortedSets.synchronize(this.sortedMap.keySet(), this.sync);
            }
            return (FloatSortedSet)this.keys;
        }

        @Override
        public Float2LongSortedMap subMap(float from, float to) {
            return new SynchronizedSortedMap(this.sortedMap.subMap(from, to), this.sync);
        }

        @Override
        public Float2LongSortedMap headMap(float to) {
            return new SynchronizedSortedMap(this.sortedMap.headMap(to), this.sync);
        }

        @Override
        public Float2LongSortedMap tailMap(float from) {
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
        public Float2LongSortedMap subMap(Float from, Float to) {
            return new SynchronizedSortedMap(this.sortedMap.subMap(from, to), this.sync);
        }

        @Deprecated
        @Override
        public Float2LongSortedMap headMap(Float to) {
            return new SynchronizedSortedMap(this.sortedMap.headMap(to), this.sync);
        }

        @Deprecated
        @Override
        public Float2LongSortedMap tailMap(Float from) {
            return new SynchronizedSortedMap(this.sortedMap.tailMap(from), this.sync);
        }
    }

    public static class Singleton
    extends Float2LongMaps.Singleton
    implements Float2LongSortedMap,
    Serializable,
    Cloneable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final FloatComparator comparator;

        protected Singleton(float key, long value, FloatComparator comparator) {
            super(key, value);
            this.comparator = comparator;
        }

        protected Singleton(float key, long value) {
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
        public ObjectSortedSet<Float2LongMap.Entry> float2LongEntrySet() {
            if (this.entries == null) {
                this.entries = ObjectSortedSets.singleton(new AbstractFloat2LongMap.BasicEntry(this.key, this.value), Float2LongSortedMaps.entryComparator(this.comparator));
            }
            return (ObjectSortedSet)this.entries;
        }

        @Deprecated
        @Override
        public ObjectSortedSet<Map.Entry<Float, Long>> entrySet() {
            return this.float2LongEntrySet();
        }

        @Override
        public FloatSortedSet keySet() {
            if (this.keys == null) {
                this.keys = FloatSortedSets.singleton(this.key, this.comparator);
            }
            return (FloatSortedSet)this.keys;
        }

        @Override
        public Float2LongSortedMap subMap(float from, float to) {
            if (this.compare(from, this.key) <= 0 && this.compare(this.key, to) < 0) {
                return this;
            }
            return Float2LongSortedMaps.EMPTY_MAP;
        }

        @Override
        public Float2LongSortedMap headMap(float to) {
            if (this.compare(this.key, to) < 0) {
                return this;
            }
            return Float2LongSortedMaps.EMPTY_MAP;
        }

        @Override
        public Float2LongSortedMap tailMap(float from) {
            if (this.compare(from, this.key) <= 0) {
                return this;
            }
            return Float2LongSortedMaps.EMPTY_MAP;
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
        public Float2LongSortedMap headMap(Float oto) {
            return this.headMap(oto.floatValue());
        }

        @Deprecated
        @Override
        public Float2LongSortedMap tailMap(Float ofrom) {
            return this.tailMap(ofrom.floatValue());
        }

        @Deprecated
        @Override
        public Float2LongSortedMap subMap(Float ofrom, Float oto) {
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
    extends Float2LongMaps.EmptyMap
    implements Float2LongSortedMap,
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
        public ObjectSortedSet<Float2LongMap.Entry> float2LongEntrySet() {
            return ObjectSortedSets.EMPTY_SET;
        }

        @Deprecated
        @Override
        public ObjectSortedSet<Map.Entry<Float, Long>> entrySet() {
            return ObjectSortedSets.EMPTY_SET;
        }

        @Override
        public FloatSortedSet keySet() {
            return FloatSortedSets.EMPTY_SET;
        }

        @Override
        public Float2LongSortedMap subMap(float from, float to) {
            return Float2LongSortedMaps.EMPTY_MAP;
        }

        @Override
        public Float2LongSortedMap headMap(float to) {
            return Float2LongSortedMaps.EMPTY_MAP;
        }

        @Override
        public Float2LongSortedMap tailMap(float from) {
            return Float2LongSortedMaps.EMPTY_MAP;
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
        public Float2LongSortedMap headMap(Float oto) {
            return this.headMap(oto.floatValue());
        }

        @Deprecated
        @Override
        public Float2LongSortedMap tailMap(Float ofrom) {
            return this.tailMap(ofrom.floatValue());
        }

        @Deprecated
        @Override
        public Float2LongSortedMap subMap(Float ofrom, Float oto) {
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

