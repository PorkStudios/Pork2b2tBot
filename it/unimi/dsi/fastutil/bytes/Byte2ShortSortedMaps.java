/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.bytes.AbstractByte2ShortMap;
import it.unimi.dsi.fastutil.bytes.Byte2ShortMap;
import it.unimi.dsi.fastutil.bytes.Byte2ShortMaps;
import it.unimi.dsi.fastutil.bytes.Byte2ShortSortedMap;
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

public final class Byte2ShortSortedMaps {
    public static final EmptySortedMap EMPTY_MAP = new EmptySortedMap();

    private Byte2ShortSortedMaps() {
    }

    public static Comparator<? super Map.Entry<Byte, ?>> entryComparator(ByteComparator comparator) {
        return (x, y) -> comparator.compare((byte)((Byte)x.getKey()), (byte)((Byte)y.getKey()));
    }

    public static ObjectBidirectionalIterator<Byte2ShortMap.Entry> fastIterator(Byte2ShortSortedMap map) {
        ObjectSet entries = map.byte2ShortEntrySet();
        return entries instanceof Byte2ShortSortedMap.FastSortedEntrySet ? ((Byte2ShortSortedMap.FastSortedEntrySet)entries).fastIterator() : entries.iterator();
    }

    public static ObjectBidirectionalIterable<Byte2ShortMap.Entry> fastIterable(Byte2ShortSortedMap map) {
        ObjectSet entries = map.byte2ShortEntrySet();
        return entries instanceof Byte2ShortSortedMap.FastSortedEntrySet ? ((Byte2ShortSortedMap.FastSortedEntrySet)entries)::fastIterator : entries;
    }

    public static Byte2ShortSortedMap singleton(Byte key, Short value) {
        return new Singleton(key, value);
    }

    public static Byte2ShortSortedMap singleton(Byte key, Short value, ByteComparator comparator) {
        return new Singleton(key, value, comparator);
    }

    public static Byte2ShortSortedMap singleton(byte key, short value) {
        return new Singleton(key, value);
    }

    public static Byte2ShortSortedMap singleton(byte key, short value, ByteComparator comparator) {
        return new Singleton(key, value, comparator);
    }

    public static Byte2ShortSortedMap synchronize(Byte2ShortSortedMap m) {
        return new SynchronizedSortedMap(m);
    }

    public static Byte2ShortSortedMap synchronize(Byte2ShortSortedMap m, Object sync) {
        return new SynchronizedSortedMap(m, sync);
    }

    public static Byte2ShortSortedMap unmodifiable(Byte2ShortSortedMap m) {
        return new UnmodifiableSortedMap(m);
    }

    public static class UnmodifiableSortedMap
    extends Byte2ShortMaps.UnmodifiableMap
    implements Byte2ShortSortedMap,
    Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final Byte2ShortSortedMap sortedMap;

        protected UnmodifiableSortedMap(Byte2ShortSortedMap m) {
            super(m);
            this.sortedMap = m;
        }

        @Override
        public ByteComparator comparator() {
            return this.sortedMap.comparator();
        }

        @Override
        public ObjectSortedSet<Byte2ShortMap.Entry> byte2ShortEntrySet() {
            if (this.entries == null) {
                this.entries = ObjectSortedSets.unmodifiable(this.sortedMap.byte2ShortEntrySet());
            }
            return (ObjectSortedSet)this.entries;
        }

        @Deprecated
        @Override
        public ObjectSortedSet<Map.Entry<Byte, Short>> entrySet() {
            return this.byte2ShortEntrySet();
        }

        @Override
        public ByteSortedSet keySet() {
            if (this.keys == null) {
                this.keys = ByteSortedSets.unmodifiable(this.sortedMap.keySet());
            }
            return (ByteSortedSet)this.keys;
        }

        @Override
        public Byte2ShortSortedMap subMap(byte from, byte to) {
            return new UnmodifiableSortedMap(this.sortedMap.subMap(from, to));
        }

        @Override
        public Byte2ShortSortedMap headMap(byte to) {
            return new UnmodifiableSortedMap(this.sortedMap.headMap(to));
        }

        @Override
        public Byte2ShortSortedMap tailMap(byte from) {
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
        public Byte2ShortSortedMap subMap(Byte from, Byte to) {
            return new UnmodifiableSortedMap(this.sortedMap.subMap(from, to));
        }

        @Deprecated
        @Override
        public Byte2ShortSortedMap headMap(Byte to) {
            return new UnmodifiableSortedMap(this.sortedMap.headMap(to));
        }

        @Deprecated
        @Override
        public Byte2ShortSortedMap tailMap(Byte from) {
            return new UnmodifiableSortedMap(this.sortedMap.tailMap(from));
        }
    }

    public static class SynchronizedSortedMap
    extends Byte2ShortMaps.SynchronizedMap
    implements Byte2ShortSortedMap,
    Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final Byte2ShortSortedMap sortedMap;

        protected SynchronizedSortedMap(Byte2ShortSortedMap m, Object sync) {
            super(m, sync);
            this.sortedMap = m;
        }

        protected SynchronizedSortedMap(Byte2ShortSortedMap m) {
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
        public ObjectSortedSet<Byte2ShortMap.Entry> byte2ShortEntrySet() {
            if (this.entries == null) {
                this.entries = ObjectSortedSets.synchronize(this.sortedMap.byte2ShortEntrySet(), this.sync);
            }
            return (ObjectSortedSet)this.entries;
        }

        @Deprecated
        @Override
        public ObjectSortedSet<Map.Entry<Byte, Short>> entrySet() {
            return this.byte2ShortEntrySet();
        }

        @Override
        public ByteSortedSet keySet() {
            if (this.keys == null) {
                this.keys = ByteSortedSets.synchronize(this.sortedMap.keySet(), this.sync);
            }
            return (ByteSortedSet)this.keys;
        }

        @Override
        public Byte2ShortSortedMap subMap(byte from, byte to) {
            return new SynchronizedSortedMap(this.sortedMap.subMap(from, to), this.sync);
        }

        @Override
        public Byte2ShortSortedMap headMap(byte to) {
            return new SynchronizedSortedMap(this.sortedMap.headMap(to), this.sync);
        }

        @Override
        public Byte2ShortSortedMap tailMap(byte from) {
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
        public Byte2ShortSortedMap subMap(Byte from, Byte to) {
            return new SynchronizedSortedMap(this.sortedMap.subMap(from, to), this.sync);
        }

        @Deprecated
        @Override
        public Byte2ShortSortedMap headMap(Byte to) {
            return new SynchronizedSortedMap(this.sortedMap.headMap(to), this.sync);
        }

        @Deprecated
        @Override
        public Byte2ShortSortedMap tailMap(Byte from) {
            return new SynchronizedSortedMap(this.sortedMap.tailMap(from), this.sync);
        }
    }

    public static class Singleton
    extends Byte2ShortMaps.Singleton
    implements Byte2ShortSortedMap,
    Serializable,
    Cloneable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final ByteComparator comparator;

        protected Singleton(byte key, short value, ByteComparator comparator) {
            super(key, value);
            this.comparator = comparator;
        }

        protected Singleton(byte key, short value) {
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
        public ObjectSortedSet<Byte2ShortMap.Entry> byte2ShortEntrySet() {
            if (this.entries == null) {
                this.entries = ObjectSortedSets.singleton(new AbstractByte2ShortMap.BasicEntry(this.key, this.value), Byte2ShortSortedMaps.entryComparator(this.comparator));
            }
            return (ObjectSortedSet)this.entries;
        }

        @Deprecated
        @Override
        public ObjectSortedSet<Map.Entry<Byte, Short>> entrySet() {
            return this.byte2ShortEntrySet();
        }

        @Override
        public ByteSortedSet keySet() {
            if (this.keys == null) {
                this.keys = ByteSortedSets.singleton(this.key, this.comparator);
            }
            return (ByteSortedSet)this.keys;
        }

        @Override
        public Byte2ShortSortedMap subMap(byte from, byte to) {
            if (this.compare(from, this.key) <= 0 && this.compare(this.key, to) < 0) {
                return this;
            }
            return Byte2ShortSortedMaps.EMPTY_MAP;
        }

        @Override
        public Byte2ShortSortedMap headMap(byte to) {
            if (this.compare(this.key, to) < 0) {
                return this;
            }
            return Byte2ShortSortedMaps.EMPTY_MAP;
        }

        @Override
        public Byte2ShortSortedMap tailMap(byte from) {
            if (this.compare(from, this.key) <= 0) {
                return this;
            }
            return Byte2ShortSortedMaps.EMPTY_MAP;
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
        public Byte2ShortSortedMap headMap(Byte oto) {
            return this.headMap((byte)oto);
        }

        @Deprecated
        @Override
        public Byte2ShortSortedMap tailMap(Byte ofrom) {
            return this.tailMap((byte)ofrom);
        }

        @Deprecated
        @Override
        public Byte2ShortSortedMap subMap(Byte ofrom, Byte oto) {
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
    extends Byte2ShortMaps.EmptyMap
    implements Byte2ShortSortedMap,
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
        public ObjectSortedSet<Byte2ShortMap.Entry> byte2ShortEntrySet() {
            return ObjectSortedSets.EMPTY_SET;
        }

        @Deprecated
        @Override
        public ObjectSortedSet<Map.Entry<Byte, Short>> entrySet() {
            return ObjectSortedSets.EMPTY_SET;
        }

        @Override
        public ByteSortedSet keySet() {
            return ByteSortedSets.EMPTY_SET;
        }

        @Override
        public Byte2ShortSortedMap subMap(byte from, byte to) {
            return Byte2ShortSortedMaps.EMPTY_MAP;
        }

        @Override
        public Byte2ShortSortedMap headMap(byte to) {
            return Byte2ShortSortedMaps.EMPTY_MAP;
        }

        @Override
        public Byte2ShortSortedMap tailMap(byte from) {
            return Byte2ShortSortedMaps.EMPTY_MAP;
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
        public Byte2ShortSortedMap headMap(Byte oto) {
            return this.headMap((byte)oto);
        }

        @Deprecated
        @Override
        public Byte2ShortSortedMap tailMap(Byte ofrom) {
            return this.tailMap((byte)ofrom);
        }

        @Deprecated
        @Override
        public Byte2ShortSortedMap subMap(Byte ofrom, Byte oto) {
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

