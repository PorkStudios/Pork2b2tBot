/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.objects.AbstractObject2ByteMap;
import it.unimi.dsi.fastutil.objects.Object2ByteMap;
import it.unimi.dsi.fastutil.objects.Object2ByteMaps;
import it.unimi.dsi.fastutil.objects.Object2ByteSortedMap;
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

public final class Object2ByteSortedMaps {
    public static final EmptySortedMap EMPTY_MAP = new EmptySortedMap();

    private Object2ByteSortedMaps() {
    }

    public static <K> Comparator<? super Map.Entry<K, ?>> entryComparator(Comparator<? super K> comparator) {
        return (x, y) -> comparator.compare((K)x.getKey(), (K)y.getKey());
    }

    public static <K> ObjectBidirectionalIterator<Object2ByteMap.Entry<K>> fastIterator(Object2ByteSortedMap<K> map) {
        ObjectSet entries = map.object2ByteEntrySet();
        return entries instanceof Object2ByteSortedMap.FastSortedEntrySet ? ((Object2ByteSortedMap.FastSortedEntrySet)entries).fastIterator() : entries.iterator();
    }

    public static <K> ObjectBidirectionalIterable<Object2ByteMap.Entry<K>> fastIterable(Object2ByteSortedMap<K> map) {
        ObjectSet entries = map.object2ByteEntrySet();
        return entries instanceof Object2ByteSortedMap.FastSortedEntrySet ? ((Object2ByteSortedMap.FastSortedEntrySet)entries)::fastIterator : entries;
    }

    public static <K> Object2ByteSortedMap<K> emptyMap() {
        return EMPTY_MAP;
    }

    public static <K> Object2ByteSortedMap<K> singleton(K key, Byte value) {
        return new Singleton<K>(key, value);
    }

    public static <K> Object2ByteSortedMap<K> singleton(K key, Byte value, Comparator<? super K> comparator) {
        return new Singleton<K>((K)key, value, comparator);
    }

    public static <K> Object2ByteSortedMap<K> singleton(K key, byte value) {
        return new Singleton<K>(key, value);
    }

    public static <K> Object2ByteSortedMap<K> singleton(K key, byte value, Comparator<? super K> comparator) {
        return new Singleton<K>((K)key, value, comparator);
    }

    public static <K> Object2ByteSortedMap<K> synchronize(Object2ByteSortedMap<K> m) {
        return new SynchronizedSortedMap<K>(m);
    }

    public static <K> Object2ByteSortedMap<K> synchronize(Object2ByteSortedMap<K> m, Object sync) {
        return new SynchronizedSortedMap<K>(m, sync);
    }

    public static <K> Object2ByteSortedMap<K> unmodifiable(Object2ByteSortedMap<K> m) {
        return new UnmodifiableSortedMap<K>(m);
    }

    public static class UnmodifiableSortedMap<K>
    extends Object2ByteMaps.UnmodifiableMap<K>
    implements Object2ByteSortedMap<K>,
    Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final Object2ByteSortedMap<K> sortedMap;

        protected UnmodifiableSortedMap(Object2ByteSortedMap<K> m) {
            super(m);
            this.sortedMap = m;
        }

        @Override
        public Comparator<? super K> comparator() {
            return this.sortedMap.comparator();
        }

        @Override
        public ObjectSortedSet<Object2ByteMap.Entry<K>> object2ByteEntrySet() {
            if (this.entries == null) {
                this.entries = ObjectSortedSets.unmodifiable(this.sortedMap.object2ByteEntrySet());
            }
            return (ObjectSortedSet)this.entries;
        }

        @Deprecated
        @Override
        public ObjectSortedSet<Map.Entry<K, Byte>> entrySet() {
            return this.object2ByteEntrySet();
        }

        @Override
        public ObjectSortedSet<K> keySet() {
            if (this.keys == null) {
                this.keys = ObjectSortedSets.unmodifiable(this.sortedMap.keySet());
            }
            return (ObjectSortedSet)this.keys;
        }

        @Override
        public Object2ByteSortedMap<K> subMap(K from, K to) {
            return new UnmodifiableSortedMap<K>((Object2ByteSortedMap<K>)this.sortedMap.subMap((Object)from, (Object)to));
        }

        @Override
        public Object2ByteSortedMap<K> headMap(K to) {
            return new UnmodifiableSortedMap<K>((Object2ByteSortedMap<K>)this.sortedMap.headMap((Object)to));
        }

        @Override
        public Object2ByteSortedMap<K> tailMap(K from) {
            return new UnmodifiableSortedMap<K>((Object2ByteSortedMap<K>)this.sortedMap.tailMap((Object)from));
        }

        @Override
        public K firstKey() {
            return this.sortedMap.firstKey();
        }

        @Override
        public K lastKey() {
            return this.sortedMap.lastKey();
        }
    }

    public static class SynchronizedSortedMap<K>
    extends Object2ByteMaps.SynchronizedMap<K>
    implements Object2ByteSortedMap<K>,
    Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final Object2ByteSortedMap<K> sortedMap;

        protected SynchronizedSortedMap(Object2ByteSortedMap<K> m, Object sync) {
            super(m, sync);
            this.sortedMap = m;
        }

        protected SynchronizedSortedMap(Object2ByteSortedMap<K> m) {
            super(m);
            this.sortedMap = m;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public Comparator<? super K> comparator() {
            Object object = this.sync;
            synchronized (object) {
                return this.sortedMap.comparator();
            }
        }

        @Override
        public ObjectSortedSet<Object2ByteMap.Entry<K>> object2ByteEntrySet() {
            if (this.entries == null) {
                this.entries = ObjectSortedSets.synchronize(this.sortedMap.object2ByteEntrySet(), this.sync);
            }
            return (ObjectSortedSet)this.entries;
        }

        @Deprecated
        @Override
        public ObjectSortedSet<Map.Entry<K, Byte>> entrySet() {
            return this.object2ByteEntrySet();
        }

        @Override
        public ObjectSortedSet<K> keySet() {
            if (this.keys == null) {
                this.keys = ObjectSortedSets.synchronize(this.sortedMap.keySet(), this.sync);
            }
            return (ObjectSortedSet)this.keys;
        }

        @Override
        public Object2ByteSortedMap<K> subMap(K from, K to) {
            return new SynchronizedSortedMap<K>((Object2ByteSortedMap<K>)this.sortedMap.subMap((Object)from, (Object)to), this.sync);
        }

        @Override
        public Object2ByteSortedMap<K> headMap(K to) {
            return new SynchronizedSortedMap<K>((Object2ByteSortedMap<K>)this.sortedMap.headMap((Object)to), this.sync);
        }

        @Override
        public Object2ByteSortedMap<K> tailMap(K from) {
            return new SynchronizedSortedMap<K>((Object2ByteSortedMap<K>)this.sortedMap.tailMap((Object)from), this.sync);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public K firstKey() {
            Object object = this.sync;
            synchronized (object) {
                return this.sortedMap.firstKey();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public K lastKey() {
            Object object = this.sync;
            synchronized (object) {
                return this.sortedMap.lastKey();
            }
        }
    }

    public static class Singleton<K>
    extends Object2ByteMaps.Singleton<K>
    implements Object2ByteSortedMap<K>,
    Serializable,
    Cloneable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final Comparator<? super K> comparator;

        protected Singleton(K key, byte value, Comparator<? super K> comparator) {
            super(key, value);
            this.comparator = comparator;
        }

        protected Singleton(K key, byte value) {
            this(key, value, null);
        }

        final int compare(K k1, K k2) {
            return this.comparator == null ? ((Comparable)k1).compareTo(k2) : this.comparator.compare(k1, k2);
        }

        @Override
        public Comparator<? super K> comparator() {
            return this.comparator;
        }

        @Override
        public ObjectSortedSet<Object2ByteMap.Entry<K>> object2ByteEntrySet() {
            if (this.entries == null) {
                this.entries = ObjectSortedSets.singleton(new AbstractObject2ByteMap.BasicEntry<Object>(this.key, this.value), Object2ByteSortedMaps.entryComparator(this.comparator));
            }
            return (ObjectSortedSet)this.entries;
        }

        @Deprecated
        @Override
        public ObjectSortedSet<Map.Entry<K, Byte>> entrySet() {
            return this.object2ByteEntrySet();
        }

        @Override
        public ObjectSortedSet<K> keySet() {
            if (this.keys == null) {
                this.keys = ObjectSortedSets.singleton(this.key, this.comparator);
            }
            return (ObjectSortedSet)this.keys;
        }

        @Override
        public Object2ByteSortedMap<K> subMap(K from, K to) {
            if (this.compare(from, this.key) <= 0 && this.compare(this.key, to) < 0) {
                return this;
            }
            return Object2ByteSortedMaps.EMPTY_MAP;
        }

        @Override
        public Object2ByteSortedMap<K> headMap(K to) {
            if (this.compare(this.key, to) < 0) {
                return this;
            }
            return Object2ByteSortedMaps.EMPTY_MAP;
        }

        @Override
        public Object2ByteSortedMap<K> tailMap(K from) {
            if (this.compare(from, this.key) <= 0) {
                return this;
            }
            return Object2ByteSortedMaps.EMPTY_MAP;
        }

        @Override
        public K firstKey() {
            return (K)this.key;
        }

        @Override
        public K lastKey() {
            return (K)this.key;
        }
    }

    public static class EmptySortedMap<K>
    extends Object2ByteMaps.EmptyMap<K>
    implements Object2ByteSortedMap<K>,
    Serializable,
    Cloneable {
        private static final long serialVersionUID = -7046029254386353129L;

        protected EmptySortedMap() {
        }

        @Override
        public Comparator<? super K> comparator() {
            return null;
        }

        @Override
        public ObjectSortedSet<Object2ByteMap.Entry<K>> object2ByteEntrySet() {
            return ObjectSortedSets.EMPTY_SET;
        }

        @Deprecated
        @Override
        public ObjectSortedSet<Map.Entry<K, Byte>> entrySet() {
            return ObjectSortedSets.EMPTY_SET;
        }

        @Override
        public ObjectSortedSet<K> keySet() {
            return ObjectSortedSets.EMPTY_SET;
        }

        @Override
        public Object2ByteSortedMap<K> subMap(K from, K to) {
            return Object2ByteSortedMaps.EMPTY_MAP;
        }

        @Override
        public Object2ByteSortedMap<K> headMap(K to) {
            return Object2ByteSortedMaps.EMPTY_MAP;
        }

        @Override
        public Object2ByteSortedMap<K> tailMap(K from) {
            return Object2ByteSortedMaps.EMPTY_MAP;
        }

        @Override
        public K firstKey() {
            throw new NoSuchElementException();
        }

        @Override
        public K lastKey() {
            throw new NoSuchElementException();
        }
    }

}

