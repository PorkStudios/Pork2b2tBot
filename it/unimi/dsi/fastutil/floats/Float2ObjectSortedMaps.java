/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.floats.AbstractFloat2ObjectMap;
import it.unimi.dsi.fastutil.floats.Float2ObjectMap;
import it.unimi.dsi.fastutil.floats.Float2ObjectMaps;
import it.unimi.dsi.fastutil.floats.Float2ObjectSortedMap;
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

public final class Float2ObjectSortedMaps {
    public static final EmptySortedMap EMPTY_MAP = new EmptySortedMap();

    private Float2ObjectSortedMaps() {
    }

    public static Comparator<? super Map.Entry<Float, ?>> entryComparator(FloatComparator comparator) {
        return (x, y) -> comparator.compare(((Float)x.getKey()).floatValue(), ((Float)y.getKey()).floatValue());
    }

    public static <V> ObjectBidirectionalIterator<Float2ObjectMap.Entry<V>> fastIterator(Float2ObjectSortedMap<V> map) {
        ObjectSet entries = map.float2ObjectEntrySet();
        return entries instanceof Float2ObjectSortedMap.FastSortedEntrySet ? ((Float2ObjectSortedMap.FastSortedEntrySet)entries).fastIterator() : entries.iterator();
    }

    public static <V> ObjectBidirectionalIterable<Float2ObjectMap.Entry<V>> fastIterable(Float2ObjectSortedMap<V> map) {
        ObjectSet entries = map.float2ObjectEntrySet();
        return entries instanceof Float2ObjectSortedMap.FastSortedEntrySet ? ((Float2ObjectSortedMap.FastSortedEntrySet)entries)::fastIterator : entries;
    }

    public static <V> Float2ObjectSortedMap<V> emptyMap() {
        return EMPTY_MAP;
    }

    public static <V> Float2ObjectSortedMap<V> singleton(Float key, V value) {
        return new Singleton<V>(key.floatValue(), value);
    }

    public static <V> Float2ObjectSortedMap<V> singleton(Float key, V value, FloatComparator comparator) {
        return new Singleton<V>(key.floatValue(), value, comparator);
    }

    public static <V> Float2ObjectSortedMap<V> singleton(float key, V value) {
        return new Singleton<V>(key, value);
    }

    public static <V> Float2ObjectSortedMap<V> singleton(float key, V value, FloatComparator comparator) {
        return new Singleton<V>(key, value, comparator);
    }

    public static <V> Float2ObjectSortedMap<V> synchronize(Float2ObjectSortedMap<V> m) {
        return new SynchronizedSortedMap<V>(m);
    }

    public static <V> Float2ObjectSortedMap<V> synchronize(Float2ObjectSortedMap<V> m, Object sync) {
        return new SynchronizedSortedMap<V>(m, sync);
    }

    public static <V> Float2ObjectSortedMap<V> unmodifiable(Float2ObjectSortedMap<V> m) {
        return new UnmodifiableSortedMap<V>(m);
    }

    public static class UnmodifiableSortedMap<V>
    extends Float2ObjectMaps.UnmodifiableMap<V>
    implements Float2ObjectSortedMap<V>,
    Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final Float2ObjectSortedMap<V> sortedMap;

        protected UnmodifiableSortedMap(Float2ObjectSortedMap<V> m) {
            super(m);
            this.sortedMap = m;
        }

        @Override
        public FloatComparator comparator() {
            return this.sortedMap.comparator();
        }

        @Override
        public ObjectSortedSet<Float2ObjectMap.Entry<V>> float2ObjectEntrySet() {
            if (this.entries == null) {
                this.entries = ObjectSortedSets.unmodifiable(this.sortedMap.float2ObjectEntrySet());
            }
            return (ObjectSortedSet)this.entries;
        }

        @Deprecated
        @Override
        public ObjectSortedSet<Map.Entry<Float, V>> entrySet() {
            return this.float2ObjectEntrySet();
        }

        @Override
        public FloatSortedSet keySet() {
            if (this.keys == null) {
                this.keys = FloatSortedSets.unmodifiable(this.sortedMap.keySet());
            }
            return (FloatSortedSet)this.keys;
        }

        @Override
        public Float2ObjectSortedMap<V> subMap(float from, float to) {
            return new UnmodifiableSortedMap<V>(this.sortedMap.subMap(from, to));
        }

        @Override
        public Float2ObjectSortedMap<V> headMap(float to) {
            return new UnmodifiableSortedMap<V>(this.sortedMap.headMap(to));
        }

        @Override
        public Float2ObjectSortedMap<V> tailMap(float from) {
            return new UnmodifiableSortedMap<V>(this.sortedMap.tailMap(from));
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
        public Float2ObjectSortedMap<V> subMap(Float from, Float to) {
            return new UnmodifiableSortedMap<V>(this.sortedMap.subMap(from, to));
        }

        @Deprecated
        @Override
        public Float2ObjectSortedMap<V> headMap(Float to) {
            return new UnmodifiableSortedMap<V>(this.sortedMap.headMap(to));
        }

        @Deprecated
        @Override
        public Float2ObjectSortedMap<V> tailMap(Float from) {
            return new UnmodifiableSortedMap<V>(this.sortedMap.tailMap(from));
        }
    }

    public static class SynchronizedSortedMap<V>
    extends Float2ObjectMaps.SynchronizedMap<V>
    implements Float2ObjectSortedMap<V>,
    Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final Float2ObjectSortedMap<V> sortedMap;

        protected SynchronizedSortedMap(Float2ObjectSortedMap<V> m, Object sync) {
            super(m, sync);
            this.sortedMap = m;
        }

        protected SynchronizedSortedMap(Float2ObjectSortedMap<V> m) {
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
        public ObjectSortedSet<Float2ObjectMap.Entry<V>> float2ObjectEntrySet() {
            if (this.entries == null) {
                this.entries = ObjectSortedSets.synchronize(this.sortedMap.float2ObjectEntrySet(), this.sync);
            }
            return (ObjectSortedSet)this.entries;
        }

        @Deprecated
        @Override
        public ObjectSortedSet<Map.Entry<Float, V>> entrySet() {
            return this.float2ObjectEntrySet();
        }

        @Override
        public FloatSortedSet keySet() {
            if (this.keys == null) {
                this.keys = FloatSortedSets.synchronize(this.sortedMap.keySet(), this.sync);
            }
            return (FloatSortedSet)this.keys;
        }

        @Override
        public Float2ObjectSortedMap<V> subMap(float from, float to) {
            return new SynchronizedSortedMap<V>(this.sortedMap.subMap(from, to), this.sync);
        }

        @Override
        public Float2ObjectSortedMap<V> headMap(float to) {
            return new SynchronizedSortedMap<V>(this.sortedMap.headMap(to), this.sync);
        }

        @Override
        public Float2ObjectSortedMap<V> tailMap(float from) {
            return new SynchronizedSortedMap<V>(this.sortedMap.tailMap(from), this.sync);
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
        public Float2ObjectSortedMap<V> subMap(Float from, Float to) {
            return new SynchronizedSortedMap<V>(this.sortedMap.subMap(from, to), this.sync);
        }

        @Deprecated
        @Override
        public Float2ObjectSortedMap<V> headMap(Float to) {
            return new SynchronizedSortedMap<V>(this.sortedMap.headMap(to), this.sync);
        }

        @Deprecated
        @Override
        public Float2ObjectSortedMap<V> tailMap(Float from) {
            return new SynchronizedSortedMap<V>(this.sortedMap.tailMap(from), this.sync);
        }
    }

    public static class Singleton<V>
    extends Float2ObjectMaps.Singleton<V>
    implements Float2ObjectSortedMap<V>,
    Serializable,
    Cloneable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final FloatComparator comparator;

        protected Singleton(float key, V value, FloatComparator comparator) {
            super(key, value);
            this.comparator = comparator;
        }

        protected Singleton(float key, V value) {
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
        public ObjectSortedSet<Float2ObjectMap.Entry<V>> float2ObjectEntrySet() {
            if (this.entries == null) {
                this.entries = ObjectSortedSets.singleton(new AbstractFloat2ObjectMap.BasicEntry<Object>(this.key, this.value), Float2ObjectSortedMaps.entryComparator(this.comparator));
            }
            return (ObjectSortedSet)this.entries;
        }

        @Deprecated
        @Override
        public ObjectSortedSet<Map.Entry<Float, V>> entrySet() {
            return this.float2ObjectEntrySet();
        }

        @Override
        public FloatSortedSet keySet() {
            if (this.keys == null) {
                this.keys = FloatSortedSets.singleton(this.key, this.comparator);
            }
            return (FloatSortedSet)this.keys;
        }

        @Override
        public Float2ObjectSortedMap<V> subMap(float from, float to) {
            if (this.compare(from, this.key) <= 0 && this.compare(this.key, to) < 0) {
                return this;
            }
            return Float2ObjectSortedMaps.EMPTY_MAP;
        }

        @Override
        public Float2ObjectSortedMap<V> headMap(float to) {
            if (this.compare(this.key, to) < 0) {
                return this;
            }
            return Float2ObjectSortedMaps.EMPTY_MAP;
        }

        @Override
        public Float2ObjectSortedMap<V> tailMap(float from) {
            if (this.compare(from, this.key) <= 0) {
                return this;
            }
            return Float2ObjectSortedMaps.EMPTY_MAP;
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
        public Float2ObjectSortedMap<V> headMap(Float oto) {
            return this.headMap(oto.floatValue());
        }

        @Deprecated
        @Override
        public Float2ObjectSortedMap<V> tailMap(Float ofrom) {
            return this.tailMap(ofrom.floatValue());
        }

        @Deprecated
        @Override
        public Float2ObjectSortedMap<V> subMap(Float ofrom, Float oto) {
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

    public static class EmptySortedMap<V>
    extends Float2ObjectMaps.EmptyMap<V>
    implements Float2ObjectSortedMap<V>,
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
        public ObjectSortedSet<Float2ObjectMap.Entry<V>> float2ObjectEntrySet() {
            return ObjectSortedSets.EMPTY_SET;
        }

        @Deprecated
        @Override
        public ObjectSortedSet<Map.Entry<Float, V>> entrySet() {
            return ObjectSortedSets.EMPTY_SET;
        }

        @Override
        public FloatSortedSet keySet() {
            return FloatSortedSets.EMPTY_SET;
        }

        @Override
        public Float2ObjectSortedMap<V> subMap(float from, float to) {
            return Float2ObjectSortedMaps.EMPTY_MAP;
        }

        @Override
        public Float2ObjectSortedMap<V> headMap(float to) {
            return Float2ObjectSortedMaps.EMPTY_MAP;
        }

        @Override
        public Float2ObjectSortedMap<V> tailMap(float from) {
            return Float2ObjectSortedMaps.EMPTY_MAP;
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
        public Float2ObjectSortedMap<V> headMap(Float oto) {
            return this.headMap(oto.floatValue());
        }

        @Deprecated
        @Override
        public Float2ObjectSortedMap<V> tailMap(Float ofrom) {
            return this.tailMap(ofrom.floatValue());
        }

        @Deprecated
        @Override
        public Float2ObjectSortedMap<V> subMap(Float ofrom, Float oto) {
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

