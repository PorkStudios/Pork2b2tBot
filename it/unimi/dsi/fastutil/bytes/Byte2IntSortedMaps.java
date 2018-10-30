/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.bytes.AbstractByte2IntMap;
import it.unimi.dsi.fastutil.bytes.Byte2IntMap;
import it.unimi.dsi.fastutil.bytes.Byte2IntMaps;
import it.unimi.dsi.fastutil.bytes.Byte2IntSortedMap;
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

public final class Byte2IntSortedMaps {
    public static final EmptySortedMap EMPTY_MAP = new EmptySortedMap();

    private Byte2IntSortedMaps() {
    }

    public static Comparator<? super Map.Entry<Byte, ?>> entryComparator(ByteComparator comparator) {
        return (x, y) -> comparator.compare((byte)((Byte)x.getKey()), (byte)((Byte)y.getKey()));
    }

    public static ObjectBidirectionalIterator<Byte2IntMap.Entry> fastIterator(Byte2IntSortedMap map) {
        ObjectSet entries = map.byte2IntEntrySet();
        return entries instanceof Byte2IntSortedMap.FastSortedEntrySet ? ((Byte2IntSortedMap.FastSortedEntrySet)entries).fastIterator() : entries.iterator();
    }

    public static ObjectBidirectionalIterable<Byte2IntMap.Entry> fastIterable(Byte2IntSortedMap map) {
        ObjectSet entries = map.byte2IntEntrySet();
        return entries instanceof Byte2IntSortedMap.FastSortedEntrySet ? ((Byte2IntSortedMap.FastSortedEntrySet)entries)::fastIterator : entries;
    }

    public static Byte2IntSortedMap singleton(Byte key, Integer value) {
        return new Singleton(key, value);
    }

    public static Byte2IntSortedMap singleton(Byte key, Integer value, ByteComparator comparator) {
        return new Singleton(key, value, comparator);
    }

    public static Byte2IntSortedMap singleton(byte key, int value) {
        return new Singleton(key, value);
    }

    public static Byte2IntSortedMap singleton(byte key, int value, ByteComparator comparator) {
        return new Singleton(key, value, comparator);
    }

    public static Byte2IntSortedMap synchronize(Byte2IntSortedMap m) {
        return new SynchronizedSortedMap(m);
    }

    public static Byte2IntSortedMap synchronize(Byte2IntSortedMap m, Object sync) {
        return new SynchronizedSortedMap(m, sync);
    }

    public static Byte2IntSortedMap unmodifiable(Byte2IntSortedMap m) {
        return new UnmodifiableSortedMap(m);
    }

    public static class UnmodifiableSortedMap
    extends Byte2IntMaps.UnmodifiableMap
    implements Byte2IntSortedMap,
    Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final Byte2IntSortedMap sortedMap;

        protected UnmodifiableSortedMap(Byte2IntSortedMap m) {
            super(m);
            this.sortedMap = m;
        }

        @Override
        public ByteComparator comparator() {
            return this.sortedMap.comparator();
        }

        @Override
        public ObjectSortedSet<Byte2IntMap.Entry> byte2IntEntrySet() {
            if (this.entries == null) {
                this.entries = ObjectSortedSets.unmodifiable(this.sortedMap.byte2IntEntrySet());
            }
            return (ObjectSortedSet)this.entries;
        }

        @Deprecated
        @Override
        public ObjectSortedSet<Map.Entry<Byte, Integer>> entrySet() {
            return this.byte2IntEntrySet();
        }

        @Override
        public ByteSortedSet keySet() {
            if (this.keys == null) {
                this.keys = ByteSortedSets.unmodifiable(this.sortedMap.keySet());
            }
            return (ByteSortedSet)this.keys;
        }

        @Override
        public Byte2IntSortedMap subMap(byte from, byte to) {
            return new UnmodifiableSortedMap(this.sortedMap.subMap(from, to));
        }

        @Override
        public Byte2IntSortedMap headMap(byte to) {
            return new UnmodifiableSortedMap(this.sortedMap.headMap(to));
        }

        @Override
        public Byte2IntSortedMap tailMap(byte from) {
            return new UnmodifiableSortedMap(this.sortedMap.tailMap(from));
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
        public Byte2IntSortedMap subMap(Byte from, Byte to) {
            return new UnmodifiableSortedMap(this.sortedMap.subMap(from, to));
        }

        @Deprecated
        @Override
        public Byte2IntSortedMap headMap(Byte to) {
            return new UnmodifiableSortedMap(this.sortedMap.headMap(to));
        }

        @Deprecated
        @Override
        public Byte2IntSortedMap tailMap(Byte from) {
            return new UnmodifiableSortedMap(this.sortedMap.tailMap(from));
        }
    }

    public static class SynchronizedSortedMap
    extends Byte2IntMaps.SynchronizedMap
    implements Byte2IntSortedMap,
    Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final Byte2IntSortedMap sortedMap;

        protected SynchronizedSortedMap(Byte2IntSortedMap m, Object sync) {
            super(m, sync);
            this.sortedMap = m;
        }

        protected SynchronizedSortedMap(Byte2IntSortedMap m) {
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
        public ObjectSortedSet<Byte2IntMap.Entry> byte2IntEntrySet() {
            if (this.entries == null) {
                this.entries = ObjectSortedSets.synchronize(this.sortedMap.byte2IntEntrySet(), this.sync);
            }
            return (ObjectSortedSet)this.entries;
        }

        @Deprecated
        @Override
        public ObjectSortedSet<Map.Entry<Byte, Integer>> entrySet() {
            return this.byte2IntEntrySet();
        }

        @Override
        public ByteSortedSet keySet() {
            if (this.keys == null) {
                this.keys = ByteSortedSets.synchronize(this.sortedMap.keySet(), this.sync);
            }
            return (ByteSortedSet)this.keys;
        }

        @Override
        public Byte2IntSortedMap subMap(byte from, byte to) {
            return new SynchronizedSortedMap(this.sortedMap.subMap(from, to), this.sync);
        }

        @Override
        public Byte2IntSortedMap headMap(byte to) {
            return new SynchronizedSortedMap(this.sortedMap.headMap(to), this.sync);
        }

        @Override
        public Byte2IntSortedMap tailMap(byte from) {
            return new SynchronizedSortedMap(this.sortedMap.tailMap(from), this.sync);
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
        public Byte2IntSortedMap subMap(Byte from, Byte to) {
            return new SynchronizedSortedMap(this.sortedMap.subMap(from, to), this.sync);
        }

        @Deprecated
        @Override
        public Byte2IntSortedMap headMap(Byte to) {
            return new SynchronizedSortedMap(this.sortedMap.headMap(to), this.sync);
        }

        @Deprecated
        @Override
        public Byte2IntSortedMap tailMap(Byte from) {
            return new SynchronizedSortedMap(this.sortedMap.tailMap(from), this.sync);
        }
    }

    public static class Singleton
    extends Byte2IntMaps.Singleton
    implements Byte2IntSortedMap,
    Serializable,
    Cloneable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final ByteComparator comparator;

        protected Singleton(byte key, int value, ByteComparator comparator) {
            super(key, value);
            this.comparator = comparator;
        }

        protected Singleton(byte key, int value) {
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
        public ObjectSortedSet<Byte2IntMap.Entry> byte2IntEntrySet() {
            if (this.entries == null) {
                this.entries = ObjectSortedSets.singleton(new AbstractByte2IntMap.BasicEntry(this.key, this.value), Byte2IntSortedMaps.entryComparator(this.comparator));
            }
            return (ObjectSortedSet)this.entries;
        }

        @Deprecated
        @Override
        public ObjectSortedSet<Map.Entry<Byte, Integer>> entrySet() {
            return this.byte2IntEntrySet();
        }

        @Override
        public ByteSortedSet keySet() {
            if (this.keys == null) {
                this.keys = ByteSortedSets.singleton(this.key, this.comparator);
            }
            return (ByteSortedSet)this.keys;
        }

        @Override
        public Byte2IntSortedMap subMap(byte from, byte to) {
            if (this.compare(from, this.key) <= 0 && this.compare(this.key, to) < 0) {
                return this;
            }
            return Byte2IntSortedMaps.EMPTY_MAP;
        }

        @Override
        public Byte2IntSortedMap headMap(byte to) {
            if (this.compare(this.key, to) < 0) {
                return this;
            }
            return Byte2IntSortedMaps.EMPTY_MAP;
        }

        @Override
        public Byte2IntSortedMap tailMap(byte from) {
            if (this.compare(from, this.key) <= 0) {
                return this;
            }
            return Byte2IntSortedMaps.EMPTY_MAP;
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
        public Byte2IntSortedMap headMap(Byte oto) {
            return this.headMap((byte)oto);
        }

        @Deprecated
        @Override
        public Byte2IntSortedMap tailMap(Byte ofrom) {
            return this.tailMap((byte)ofrom);
        }

        @Deprecated
        @Override
        public Byte2IntSortedMap subMap(Byte ofrom, Byte oto) {
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

    public static class EmptySortedMap
    extends Byte2IntMaps.EmptyMap
    implements Byte2IntSortedMap,
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
        public ObjectSortedSet<Byte2IntMap.Entry> byte2IntEntrySet() {
            return ObjectSortedSets.EMPTY_SET;
        }

        @Deprecated
        @Override
        public ObjectSortedSet<Map.Entry<Byte, Integer>> entrySet() {
            return ObjectSortedSets.EMPTY_SET;
        }

        @Override
        public ByteSortedSet keySet() {
            return ByteSortedSets.EMPTY_SET;
        }

        @Override
        public Byte2IntSortedMap subMap(byte from, byte to) {
            return Byte2IntSortedMaps.EMPTY_MAP;
        }

        @Override
        public Byte2IntSortedMap headMap(byte to) {
            return Byte2IntSortedMaps.EMPTY_MAP;
        }

        @Override
        public Byte2IntSortedMap tailMap(byte from) {
            return Byte2IntSortedMaps.EMPTY_MAP;
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
        public Byte2IntSortedMap headMap(Byte oto) {
            return this.headMap((byte)oto);
        }

        @Deprecated
        @Override
        public Byte2IntSortedMap tailMap(Byte ofrom) {
            return this.tailMap((byte)ofrom);
        }

        @Deprecated
        @Override
        public Byte2IntSortedMap subMap(Byte ofrom, Byte oto) {
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

