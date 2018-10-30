/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.shorts;

import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterable;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import it.unimi.dsi.fastutil.objects.ObjectSortedSets;
import it.unimi.dsi.fastutil.shorts.AbstractShort2ByteMap;
import it.unimi.dsi.fastutil.shorts.Short2ByteMap;
import it.unimi.dsi.fastutil.shorts.Short2ByteMaps;
import it.unimi.dsi.fastutil.shorts.Short2ByteSortedMap;
import it.unimi.dsi.fastutil.shorts.ShortComparator;
import it.unimi.dsi.fastutil.shorts.ShortSet;
import it.unimi.dsi.fastutil.shorts.ShortSortedSet;
import it.unimi.dsi.fastutil.shorts.ShortSortedSets;
import java.io.Serializable;
import java.util.Comparator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.SortedMap;

public final class Short2ByteSortedMaps {
    public static final EmptySortedMap EMPTY_MAP = new EmptySortedMap();

    private Short2ByteSortedMaps() {
    }

    public static Comparator<? super Map.Entry<Short, ?>> entryComparator(ShortComparator comparator) {
        return (x, y) -> comparator.compare((short)((Short)x.getKey()), (short)((Short)y.getKey()));
    }

    public static ObjectBidirectionalIterator<Short2ByteMap.Entry> fastIterator(Short2ByteSortedMap map) {
        ObjectSet entries = map.short2ByteEntrySet();
        return entries instanceof Short2ByteSortedMap.FastSortedEntrySet ? ((Short2ByteSortedMap.FastSortedEntrySet)entries).fastIterator() : entries.iterator();
    }

    public static ObjectBidirectionalIterable<Short2ByteMap.Entry> fastIterable(Short2ByteSortedMap map) {
        ObjectSet entries = map.short2ByteEntrySet();
        return entries instanceof Short2ByteSortedMap.FastSortedEntrySet ? ((Short2ByteSortedMap.FastSortedEntrySet)entries)::fastIterator : entries;
    }

    public static Short2ByteSortedMap singleton(Short key, Byte value) {
        return new Singleton(key, value);
    }

    public static Short2ByteSortedMap singleton(Short key, Byte value, ShortComparator comparator) {
        return new Singleton(key, value, comparator);
    }

    public static Short2ByteSortedMap singleton(short key, byte value) {
        return new Singleton(key, value);
    }

    public static Short2ByteSortedMap singleton(short key, byte value, ShortComparator comparator) {
        return new Singleton(key, value, comparator);
    }

    public static Short2ByteSortedMap synchronize(Short2ByteSortedMap m) {
        return new SynchronizedSortedMap(m);
    }

    public static Short2ByteSortedMap synchronize(Short2ByteSortedMap m, Object sync) {
        return new SynchronizedSortedMap(m, sync);
    }

    public static Short2ByteSortedMap unmodifiable(Short2ByteSortedMap m) {
        return new UnmodifiableSortedMap(m);
    }

    public static class UnmodifiableSortedMap
    extends Short2ByteMaps.UnmodifiableMap
    implements Short2ByteSortedMap,
    Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final Short2ByteSortedMap sortedMap;

        protected UnmodifiableSortedMap(Short2ByteSortedMap m) {
            super(m);
            this.sortedMap = m;
        }

        @Override
        public ShortComparator comparator() {
            return this.sortedMap.comparator();
        }

        @Override
        public ObjectSortedSet<Short2ByteMap.Entry> short2ByteEntrySet() {
            if (this.entries == null) {
                this.entries = ObjectSortedSets.unmodifiable(this.sortedMap.short2ByteEntrySet());
            }
            return (ObjectSortedSet)this.entries;
        }

        @Deprecated
        @Override
        public ObjectSortedSet<Map.Entry<Short, Byte>> entrySet() {
            return this.short2ByteEntrySet();
        }

        @Override
        public ShortSortedSet keySet() {
            if (this.keys == null) {
                this.keys = ShortSortedSets.unmodifiable(this.sortedMap.keySet());
            }
            return (ShortSortedSet)this.keys;
        }

        @Override
        public Short2ByteSortedMap subMap(short from, short to) {
            return new UnmodifiableSortedMap(this.sortedMap.subMap(from, to));
        }

        @Override
        public Short2ByteSortedMap headMap(short to) {
            return new UnmodifiableSortedMap(this.sortedMap.headMap(to));
        }

        @Override
        public Short2ByteSortedMap tailMap(short from) {
            return new UnmodifiableSortedMap(this.sortedMap.tailMap(from));
        }

        @Override
        public short firstShortKey() {
            return this.sortedMap.firstShortKey();
        }

        @Override
        public short lastShortKey() {
            return this.sortedMap.lastShortKey();
        }

        @Deprecated
        @Override
        public Short firstKey() {
            return this.sortedMap.firstKey();
        }

        @Deprecated
        @Override
        public Short lastKey() {
            return this.sortedMap.lastKey();
        }

        @Deprecated
        @Override
        public Short2ByteSortedMap subMap(Short from, Short to) {
            return new UnmodifiableSortedMap(this.sortedMap.subMap(from, to));
        }

        @Deprecated
        @Override
        public Short2ByteSortedMap headMap(Short to) {
            return new UnmodifiableSortedMap(this.sortedMap.headMap(to));
        }

        @Deprecated
        @Override
        public Short2ByteSortedMap tailMap(Short from) {
            return new UnmodifiableSortedMap(this.sortedMap.tailMap(from));
        }
    }

    public static class SynchronizedSortedMap
    extends Short2ByteMaps.SynchronizedMap
    implements Short2ByteSortedMap,
    Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final Short2ByteSortedMap sortedMap;

        protected SynchronizedSortedMap(Short2ByteSortedMap m, Object sync) {
            super(m, sync);
            this.sortedMap = m;
        }

        protected SynchronizedSortedMap(Short2ByteSortedMap m) {
            super(m);
            this.sortedMap = m;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public ShortComparator comparator() {
            Object object = this.sync;
            synchronized (object) {
                return this.sortedMap.comparator();
            }
        }

        @Override
        public ObjectSortedSet<Short2ByteMap.Entry> short2ByteEntrySet() {
            if (this.entries == null) {
                this.entries = ObjectSortedSets.synchronize(this.sortedMap.short2ByteEntrySet(), this.sync);
            }
            return (ObjectSortedSet)this.entries;
        }

        @Deprecated
        @Override
        public ObjectSortedSet<Map.Entry<Short, Byte>> entrySet() {
            return this.short2ByteEntrySet();
        }

        @Override
        public ShortSortedSet keySet() {
            if (this.keys == null) {
                this.keys = ShortSortedSets.synchronize(this.sortedMap.keySet(), this.sync);
            }
            return (ShortSortedSet)this.keys;
        }

        @Override
        public Short2ByteSortedMap subMap(short from, short to) {
            return new SynchronizedSortedMap(this.sortedMap.subMap(from, to), this.sync);
        }

        @Override
        public Short2ByteSortedMap headMap(short to) {
            return new SynchronizedSortedMap(this.sortedMap.headMap(to), this.sync);
        }

        @Override
        public Short2ByteSortedMap tailMap(short from) {
            return new SynchronizedSortedMap(this.sortedMap.tailMap(from), this.sync);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public short firstShortKey() {
            Object object = this.sync;
            synchronized (object) {
                return this.sortedMap.firstShortKey();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public short lastShortKey() {
            Object object = this.sync;
            synchronized (object) {
                return this.sortedMap.lastShortKey();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Deprecated
        @Override
        public Short firstKey() {
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
        public Short lastKey() {
            Object object = this.sync;
            synchronized (object) {
                return this.sortedMap.lastKey();
            }
        }

        @Deprecated
        @Override
        public Short2ByteSortedMap subMap(Short from, Short to) {
            return new SynchronizedSortedMap(this.sortedMap.subMap(from, to), this.sync);
        }

        @Deprecated
        @Override
        public Short2ByteSortedMap headMap(Short to) {
            return new SynchronizedSortedMap(this.sortedMap.headMap(to), this.sync);
        }

        @Deprecated
        @Override
        public Short2ByteSortedMap tailMap(Short from) {
            return new SynchronizedSortedMap(this.sortedMap.tailMap(from), this.sync);
        }
    }

    public static class Singleton
    extends Short2ByteMaps.Singleton
    implements Short2ByteSortedMap,
    Serializable,
    Cloneable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final ShortComparator comparator;

        protected Singleton(short key, byte value, ShortComparator comparator) {
            super(key, value);
            this.comparator = comparator;
        }

        protected Singleton(short key, byte value) {
            this(key, value, null);
        }

        final int compare(short k1, short k2) {
            return this.comparator == null ? Short.compare(k1, k2) : this.comparator.compare(k1, k2);
        }

        @Override
        public ShortComparator comparator() {
            return this.comparator;
        }

        @Override
        public ObjectSortedSet<Short2ByteMap.Entry> short2ByteEntrySet() {
            if (this.entries == null) {
                this.entries = ObjectSortedSets.singleton(new AbstractShort2ByteMap.BasicEntry(this.key, this.value), Short2ByteSortedMaps.entryComparator(this.comparator));
            }
            return (ObjectSortedSet)this.entries;
        }

        @Deprecated
        @Override
        public ObjectSortedSet<Map.Entry<Short, Byte>> entrySet() {
            return this.short2ByteEntrySet();
        }

        @Override
        public ShortSortedSet keySet() {
            if (this.keys == null) {
                this.keys = ShortSortedSets.singleton(this.key, this.comparator);
            }
            return (ShortSortedSet)this.keys;
        }

        @Override
        public Short2ByteSortedMap subMap(short from, short to) {
            if (this.compare(from, this.key) <= 0 && this.compare(this.key, to) < 0) {
                return this;
            }
            return Short2ByteSortedMaps.EMPTY_MAP;
        }

        @Override
        public Short2ByteSortedMap headMap(short to) {
            if (this.compare(this.key, to) < 0) {
                return this;
            }
            return Short2ByteSortedMaps.EMPTY_MAP;
        }

        @Override
        public Short2ByteSortedMap tailMap(short from) {
            if (this.compare(from, this.key) <= 0) {
                return this;
            }
            return Short2ByteSortedMaps.EMPTY_MAP;
        }

        @Override
        public short firstShortKey() {
            return this.key;
        }

        @Override
        public short lastShortKey() {
            return this.key;
        }

        @Deprecated
        @Override
        public Short2ByteSortedMap headMap(Short oto) {
            return this.headMap((short)oto);
        }

        @Deprecated
        @Override
        public Short2ByteSortedMap tailMap(Short ofrom) {
            return this.tailMap((short)ofrom);
        }

        @Deprecated
        @Override
        public Short2ByteSortedMap subMap(Short ofrom, Short oto) {
            return this.subMap((short)ofrom, (short)oto);
        }

        @Deprecated
        @Override
        public Short firstKey() {
            return this.firstShortKey();
        }

        @Deprecated
        @Override
        public Short lastKey() {
            return this.lastShortKey();
        }
    }

    public static class EmptySortedMap
    extends Short2ByteMaps.EmptyMap
    implements Short2ByteSortedMap,
    Serializable,
    Cloneable {
        private static final long serialVersionUID = -7046029254386353129L;

        protected EmptySortedMap() {
        }

        @Override
        public ShortComparator comparator() {
            return null;
        }

        @Override
        public ObjectSortedSet<Short2ByteMap.Entry> short2ByteEntrySet() {
            return ObjectSortedSets.EMPTY_SET;
        }

        @Deprecated
        @Override
        public ObjectSortedSet<Map.Entry<Short, Byte>> entrySet() {
            return ObjectSortedSets.EMPTY_SET;
        }

        @Override
        public ShortSortedSet keySet() {
            return ShortSortedSets.EMPTY_SET;
        }

        @Override
        public Short2ByteSortedMap subMap(short from, short to) {
            return Short2ByteSortedMaps.EMPTY_MAP;
        }

        @Override
        public Short2ByteSortedMap headMap(short to) {
            return Short2ByteSortedMaps.EMPTY_MAP;
        }

        @Override
        public Short2ByteSortedMap tailMap(short from) {
            return Short2ByteSortedMaps.EMPTY_MAP;
        }

        @Override
        public short firstShortKey() {
            throw new NoSuchElementException();
        }

        @Override
        public short lastShortKey() {
            throw new NoSuchElementException();
        }

        @Deprecated
        @Override
        public Short2ByteSortedMap headMap(Short oto) {
            return this.headMap((short)oto);
        }

        @Deprecated
        @Override
        public Short2ByteSortedMap tailMap(Short ofrom) {
            return this.tailMap((short)ofrom);
        }

        @Deprecated
        @Override
        public Short2ByteSortedMap subMap(Short ofrom, Short oto) {
            return this.subMap((short)ofrom, (short)oto);
        }

        @Deprecated
        @Override
        public Short firstKey() {
            return this.firstShortKey();
        }

        @Deprecated
        @Override
        public Short lastKey() {
            return this.lastShortKey();
        }
    }

}

