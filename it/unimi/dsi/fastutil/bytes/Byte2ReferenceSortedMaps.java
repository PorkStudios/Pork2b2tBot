/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.bytes.AbstractByte2ReferenceMap;
import it.unimi.dsi.fastutil.bytes.Byte2ReferenceMap;
import it.unimi.dsi.fastutil.bytes.Byte2ReferenceMaps;
import it.unimi.dsi.fastutil.bytes.Byte2ReferenceSortedMap;
import it.unimi.dsi.fastutil.bytes.ByteComparator;
import it.unimi.dsi.fastutil.bytes.ByteSet;
import it.unimi.dsi.fastutil.bytes.ByteSortedSet;
import it.unimi.dsi.fastutil.bytes.ByteSortedSets;
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

public final class Byte2ReferenceSortedMaps {
    public static final EmptySortedMap EMPTY_MAP = new EmptySortedMap();

    private Byte2ReferenceSortedMaps() {
    }

    public static Comparator<? super Map.Entry<Byte, ?>> entryComparator(ByteComparator comparator) {
        return (x, y) -> comparator.compare((byte)((Byte)x.getKey()), (byte)((Byte)y.getKey()));
    }

    public static <V> ObjectBidirectionalIterator<Byte2ReferenceMap.Entry<V>> fastIterator(Byte2ReferenceSortedMap<V> map) {
        ObjectSet entries = map.byte2ReferenceEntrySet();
        return entries instanceof Byte2ReferenceSortedMap.FastSortedEntrySet ? ((Byte2ReferenceSortedMap.FastSortedEntrySet)entries).fastIterator() : entries.iterator();
    }

    public static <V> ObjectBidirectionalIterable<Byte2ReferenceMap.Entry<V>> fastIterable(Byte2ReferenceSortedMap<V> map) {
        ObjectSet entries = map.byte2ReferenceEntrySet();
        return entries instanceof Byte2ReferenceSortedMap.FastSortedEntrySet ? ((Byte2ReferenceSortedMap.FastSortedEntrySet)entries)::fastIterator : entries;
    }

    public static <V> Byte2ReferenceSortedMap<V> emptyMap() {
        return EMPTY_MAP;
    }

    public static <V> Byte2ReferenceSortedMap<V> singleton(Byte key, V value) {
        return new Singleton<V>(key, value);
    }

    public static <V> Byte2ReferenceSortedMap<V> singleton(Byte key, V value, ByteComparator comparator) {
        return new Singleton<V>(key, value, comparator);
    }

    public static <V> Byte2ReferenceSortedMap<V> singleton(byte key, V value) {
        return new Singleton<V>(key, value);
    }

    public static <V> Byte2ReferenceSortedMap<V> singleton(byte key, V value, ByteComparator comparator) {
        return new Singleton<V>(key, value, comparator);
    }

    public static <V> Byte2ReferenceSortedMap<V> synchronize(Byte2ReferenceSortedMap<V> m) {
        return new SynchronizedSortedMap<V>(m);
    }

    public static <V> Byte2ReferenceSortedMap<V> synchronize(Byte2ReferenceSortedMap<V> m, Object sync) {
        return new SynchronizedSortedMap<V>(m, sync);
    }

    public static <V> Byte2ReferenceSortedMap<V> unmodifiable(Byte2ReferenceSortedMap<V> m) {
        return new UnmodifiableSortedMap<V>(m);
    }

    public static class UnmodifiableSortedMap<V>
    extends Byte2ReferenceMaps.UnmodifiableMap<V>
    implements Byte2ReferenceSortedMap<V>,
    Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final Byte2ReferenceSortedMap<V> sortedMap;

        protected UnmodifiableSortedMap(Byte2ReferenceSortedMap<V> m) {
            super(m);
            this.sortedMap = m;
        }

        @Override
        public ByteComparator comparator() {
            return this.sortedMap.comparator();
        }

        @Override
        public ObjectSortedSet<Byte2ReferenceMap.Entry<V>> byte2ReferenceEntrySet() {
            if (this.entries == null) {
                this.entries = ObjectSortedSets.unmodifiable(this.sortedMap.byte2ReferenceEntrySet());
            }
            return (ObjectSortedSet)this.entries;
        }

        @Deprecated
        @Override
        public ObjectSortedSet<Map.Entry<Byte, V>> entrySet() {
            return this.byte2ReferenceEntrySet();
        }

        @Override
        public ByteSortedSet keySet() {
            if (this.keys == null) {
                this.keys = ByteSortedSets.unmodifiable(this.sortedMap.keySet());
            }
            return (ByteSortedSet)this.keys;
        }

        @Override
        public Byte2ReferenceSortedMap<V> subMap(byte from, byte to) {
            return new UnmodifiableSortedMap<V>(this.sortedMap.subMap(from, to));
        }

        @Override
        public Byte2ReferenceSortedMap<V> headMap(byte to) {
            return new UnmodifiableSortedMap<V>(this.sortedMap.headMap(to));
        }

        @Override
        public Byte2ReferenceSortedMap<V> tailMap(byte from) {
            return new UnmodifiableSortedMap<V>(this.sortedMap.tailMap(from));
        }

        @Override
        public byte firstByteKey() {
            return this.sortedMap.firstByteKey();
        }

        @Override
        public byte lastByteKey() {
            return this.sortedMap.lastByteKey();
        }

        @Deprecated
        @Override
        public Byte firstKey() {
            return this.sortedMap.firstKey();
        }

        @Deprecated
        @Override
        public Byte lastKey() {
            return this.sortedMap.lastKey();
        }

        @Deprecated
        @Override
        public Byte2ReferenceSortedMap<V> subMap(Byte from, Byte to) {
            return new UnmodifiableSortedMap<V>(this.sortedMap.subMap(from, to));
        }

        @Deprecated
        @Override
        public Byte2ReferenceSortedMap<V> headMap(Byte to) {
            return new UnmodifiableSortedMap<V>(this.sortedMap.headMap(to));
        }

        @Deprecated
        @Override
        public Byte2ReferenceSortedMap<V> tailMap(Byte from) {
            return new UnmodifiableSortedMap<V>(this.sortedMap.tailMap(from));
        }
    }

    public static class SynchronizedSortedMap<V>
    extends Byte2ReferenceMaps.SynchronizedMap<V>
    implements Byte2ReferenceSortedMap<V>,
    Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final Byte2ReferenceSortedMap<V> sortedMap;

        protected SynchronizedSortedMap(Byte2ReferenceSortedMap<V> m, Object sync) {
            super(m, sync);
            this.sortedMap = m;
        }

        protected SynchronizedSortedMap(Byte2ReferenceSortedMap<V> m) {
            super(m);
            this.sortedMap = m;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public ByteComparator comparator() {
            Object object = this.sync;
            synchronized (object) {
                return this.sortedMap.comparator();
            }
        }

        @Override
        public ObjectSortedSet<Byte2ReferenceMap.Entry<V>> byte2ReferenceEntrySet() {
            if (this.entries == null) {
                this.entries = ObjectSortedSets.synchronize(this.sortedMap.byte2ReferenceEntrySet(), this.sync);
            }
            return (ObjectSortedSet)this.entries;
        }

        @Deprecated
        @Override
        public ObjectSortedSet<Map.Entry<Byte, V>> entrySet() {
            return this.byte2ReferenceEntrySet();
        }

        @Override
        public ByteSortedSet keySet() {
            if (this.keys == null) {
                this.keys = ByteSortedSets.synchronize(this.sortedMap.keySet(), this.sync);
            }
            return (ByteSortedSet)this.keys;
        }

        @Override
        public Byte2ReferenceSortedMap<V> subMap(byte from, byte to) {
            return new SynchronizedSortedMap<V>(this.sortedMap.subMap(from, to), this.sync);
        }

        @Override
        public Byte2ReferenceSortedMap<V> headMap(byte to) {
            return new SynchronizedSortedMap<V>(this.sortedMap.headMap(to), this.sync);
        }

        @Override
        public Byte2ReferenceSortedMap<V> tailMap(byte from) {
            return new SynchronizedSortedMap<V>(this.sortedMap.tailMap(from), this.sync);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public byte firstByteKey() {
            Object object = this.sync;
            synchronized (object) {
                return this.sortedMap.firstByteKey();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public byte lastByteKey() {
            Object object = this.sync;
            synchronized (object) {
                return this.sortedMap.lastByteKey();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Deprecated
        @Override
        public Byte firstKey() {
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
        public Byte lastKey() {
            Object object = this.sync;
            synchronized (object) {
                return this.sortedMap.lastKey();
            }
        }

        @Deprecated
        @Override
        public Byte2ReferenceSortedMap<V> subMap(Byte from, Byte to) {
            return new SynchronizedSortedMap<V>(this.sortedMap.subMap(from, to), this.sync);
        }

        @Deprecated
        @Override
        public Byte2ReferenceSortedMap<V> headMap(Byte to) {
            return new SynchronizedSortedMap<V>(this.sortedMap.headMap(to), this.sync);
        }

        @Deprecated
        @Override
        public Byte2ReferenceSortedMap<V> tailMap(Byte from) {
            return new SynchronizedSortedMap<V>(this.sortedMap.tailMap(from), this.sync);
        }
    }

    public static class Singleton<V>
    extends Byte2ReferenceMaps.Singleton<V>
    implements Byte2ReferenceSortedMap<V>,
    Serializable,
    Cloneable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final ByteComparator comparator;

        protected Singleton(byte key, V value, ByteComparator comparator) {
            super(key, value);
            this.comparator = comparator;
        }

        protected Singleton(byte key, V value) {
            this(key, value, null);
        }

        final int compare(byte k1, byte k2) {
            return this.comparator == null ? Byte.compare(k1, k2) : this.comparator.compare(k1, k2);
        }

        @Override
        public ByteComparator comparator() {
            return this.comparator;
        }

        @Override
        public ObjectSortedSet<Byte2ReferenceMap.Entry<V>> byte2ReferenceEntrySet() {
            if (this.entries == null) {
                this.entries = ObjectSortedSets.singleton(new AbstractByte2ReferenceMap.BasicEntry<Object>(this.key, this.value), Byte2ReferenceSortedMaps.entryComparator(this.comparator));
            }
            return (ObjectSortedSet)this.entries;
        }

        @Deprecated
        @Override
        public ObjectSortedSet<Map.Entry<Byte, V>> entrySet() {
            return this.byte2ReferenceEntrySet();
        }

        @Override
        public ByteSortedSet keySet() {
            if (this.keys == null) {
                this.keys = ByteSortedSets.singleton(this.key, this.comparator);
            }
            return (ByteSortedSet)this.keys;
        }

        @Override
        public Byte2ReferenceSortedMap<V> subMap(byte from, byte to) {
            if (this.compare(from, this.key) <= 0 && this.compare(this.key, to) < 0) {
                return this;
            }
            return Byte2ReferenceSortedMaps.EMPTY_MAP;
        }

        @Override
        public Byte2ReferenceSortedMap<V> headMap(byte to) {
            if (this.compare(this.key, to) < 0) {
                return this;
            }
            return Byte2ReferenceSortedMaps.EMPTY_MAP;
        }

        @Override
        public Byte2ReferenceSortedMap<V> tailMap(byte from) {
            if (this.compare(from, this.key) <= 0) {
                return this;
            }
            return Byte2ReferenceSortedMaps.EMPTY_MAP;
        }

        @Override
        public byte firstByteKey() {
            return this.key;
        }

        @Override
        public byte lastByteKey() {
            return this.key;
        }

        @Deprecated
        @Override
        public Byte2ReferenceSortedMap<V> headMap(Byte oto) {
            return this.headMap((byte)oto);
        }

        @Deprecated
        @Override
        public Byte2ReferenceSortedMap<V> tailMap(Byte ofrom) {
            return this.tailMap((byte)ofrom);
        }

        @Deprecated
        @Override
        public Byte2ReferenceSortedMap<V> subMap(Byte ofrom, Byte oto) {
            return this.subMap((byte)ofrom, (byte)oto);
        }

        @Deprecated
        @Override
        public Byte firstKey() {
            return this.firstByteKey();
        }

        @Deprecated
        @Override
        public Byte lastKey() {
            return this.lastByteKey();
        }
    }

    public static class EmptySortedMap<V>
    extends Byte2ReferenceMaps.EmptyMap<V>
    implements Byte2ReferenceSortedMap<V>,
    Serializable,
    Cloneable {
        private static final long serialVersionUID = -7046029254386353129L;

        protected EmptySortedMap() {
        }

        @Override
        public ByteComparator comparator() {
            return null;
        }

        @Override
        public ObjectSortedSet<Byte2ReferenceMap.Entry<V>> byte2ReferenceEntrySet() {
            return ObjectSortedSets.EMPTY_SET;
        }

        @Deprecated
        @Override
        public ObjectSortedSet<Map.Entry<Byte, V>> entrySet() {
            return ObjectSortedSets.EMPTY_SET;
        }

        @Override
        public ByteSortedSet keySet() {
            return ByteSortedSets.EMPTY_SET;
        }

        @Override
        public Byte2ReferenceSortedMap<V> subMap(byte from, byte to) {
            return Byte2ReferenceSortedMaps.EMPTY_MAP;
        }

        @Override
        public Byte2ReferenceSortedMap<V> headMap(byte to) {
            return Byte2ReferenceSortedMaps.EMPTY_MAP;
        }

        @Override
        public Byte2ReferenceSortedMap<V> tailMap(byte from) {
            return Byte2ReferenceSortedMaps.EMPTY_MAP;
        }

        @Override
        public byte firstByteKey() {
            throw new NoSuchElementException();
        }

        @Override
        public byte lastByteKey() {
            throw new NoSuchElementException();
        }

        @Deprecated
        @Override
        public Byte2ReferenceSortedMap<V> headMap(Byte oto) {
            return this.headMap((byte)oto);
        }

        @Deprecated
        @Override
        public Byte2ReferenceSortedMap<V> tailMap(Byte ofrom) {
            return this.tailMap((byte)ofrom);
        }

        @Deprecated
        @Override
        public Byte2ReferenceSortedMap<V> subMap(Byte ofrom, Byte oto) {
            return this.subMap((byte)ofrom, (byte)oto);
        }

        @Deprecated
        @Override
        public Byte firstKey() {
            return this.firstByteKey();
        }

        @Deprecated
        @Override
        public Byte lastKey() {
            return this.lastByteKey();
        }
    }

}

