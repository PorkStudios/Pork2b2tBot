/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.chars.AbstractChar2BooleanMap;
import it.unimi.dsi.fastutil.chars.Char2BooleanMap;
import it.unimi.dsi.fastutil.chars.Char2BooleanMaps;
import it.unimi.dsi.fastutil.chars.Char2BooleanSortedMap;
import it.unimi.dsi.fastutil.chars.CharComparator;
import it.unimi.dsi.fastutil.chars.CharSet;
import it.unimi.dsi.fastutil.chars.CharSortedSet;
import it.unimi.dsi.fastutil.chars.CharSortedSets;
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

public final class Char2BooleanSortedMaps {
    public static final EmptySortedMap EMPTY_MAP = new EmptySortedMap();

    private Char2BooleanSortedMaps() {
    }

    public static Comparator<? super Map.Entry<Character, ?>> entryComparator(CharComparator comparator) {
        return (x, y) -> comparator.compare(((Character)x.getKey()).charValue(), ((Character)y.getKey()).charValue());
    }

    public static ObjectBidirectionalIterator<Char2BooleanMap.Entry> fastIterator(Char2BooleanSortedMap map) {
        ObjectSet entries = map.char2BooleanEntrySet();
        return entries instanceof Char2BooleanSortedMap.FastSortedEntrySet ? ((Char2BooleanSortedMap.FastSortedEntrySet)entries).fastIterator() : entries.iterator();
    }

    public static ObjectBidirectionalIterable<Char2BooleanMap.Entry> fastIterable(Char2BooleanSortedMap map) {
        ObjectSet entries = map.char2BooleanEntrySet();
        return entries instanceof Char2BooleanSortedMap.FastSortedEntrySet ? ((Char2BooleanSortedMap.FastSortedEntrySet)entries)::fastIterator : entries;
    }

    public static Char2BooleanSortedMap singleton(Character key, Boolean value) {
        return new Singleton(key.charValue(), value);
    }

    public static Char2BooleanSortedMap singleton(Character key, Boolean value, CharComparator comparator) {
        return new Singleton(key.charValue(), value, comparator);
    }

    public static Char2BooleanSortedMap singleton(char key, boolean value) {
        return new Singleton(key, value);
    }

    public static Char2BooleanSortedMap singleton(char key, boolean value, CharComparator comparator) {
        return new Singleton(key, value, comparator);
    }

    public static Char2BooleanSortedMap synchronize(Char2BooleanSortedMap m) {
        return new SynchronizedSortedMap(m);
    }

    public static Char2BooleanSortedMap synchronize(Char2BooleanSortedMap m, Object sync) {
        return new SynchronizedSortedMap(m, sync);
    }

    public static Char2BooleanSortedMap unmodifiable(Char2BooleanSortedMap m) {
        return new UnmodifiableSortedMap(m);
    }

    public static class UnmodifiableSortedMap
    extends Char2BooleanMaps.UnmodifiableMap
    implements Char2BooleanSortedMap,
    Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final Char2BooleanSortedMap sortedMap;

        protected UnmodifiableSortedMap(Char2BooleanSortedMap m) {
            super(m);
            this.sortedMap = m;
        }

        @Override
        public CharComparator comparator() {
            return this.sortedMap.comparator();
        }

        @Override
        public ObjectSortedSet<Char2BooleanMap.Entry> char2BooleanEntrySet() {
            if (this.entries == null) {
                this.entries = ObjectSortedSets.unmodifiable(this.sortedMap.char2BooleanEntrySet());
            }
            return (ObjectSortedSet)this.entries;
        }

        @Deprecated
        @Override
        public ObjectSortedSet<Map.Entry<Character, Boolean>> entrySet() {
            return this.char2BooleanEntrySet();
        }

        @Override
        public CharSortedSet keySet() {
            if (this.keys == null) {
                this.keys = CharSortedSets.unmodifiable(this.sortedMap.keySet());
            }
            return (CharSortedSet)this.keys;
        }

        @Override
        public Char2BooleanSortedMap subMap(char from, char to) {
            return new UnmodifiableSortedMap(this.sortedMap.subMap(from, to));
        }

        @Override
        public Char2BooleanSortedMap headMap(char to) {
            return new UnmodifiableSortedMap(this.sortedMap.headMap(to));
        }

        @Override
        public Char2BooleanSortedMap tailMap(char from) {
            return new UnmodifiableSortedMap(this.sortedMap.tailMap(from));
        }

        @Override
        public char firstCharKey() {
            return this.sortedMap.firstCharKey();
        }

        @Override
        public char lastCharKey() {
            return this.sortedMap.lastCharKey();
        }

        @Deprecated
        @Override
        public Character firstKey() {
            return this.sortedMap.firstKey();
        }

        @Deprecated
        @Override
        public Character lastKey() {
            return this.sortedMap.lastKey();
        }

        @Deprecated
        @Override
        public Char2BooleanSortedMap subMap(Character from, Character to) {
            return new UnmodifiableSortedMap(this.sortedMap.subMap(from, to));
        }

        @Deprecated
        @Override
        public Char2BooleanSortedMap headMap(Character to) {
            return new UnmodifiableSortedMap(this.sortedMap.headMap(to));
        }

        @Deprecated
        @Override
        public Char2BooleanSortedMap tailMap(Character from) {
            return new UnmodifiableSortedMap(this.sortedMap.tailMap(from));
        }
    }

    public static class SynchronizedSortedMap
    extends Char2BooleanMaps.SynchronizedMap
    implements Char2BooleanSortedMap,
    Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final Char2BooleanSortedMap sortedMap;

        protected SynchronizedSortedMap(Char2BooleanSortedMap m, Object sync) {
            super(m, sync);
            this.sortedMap = m;
        }

        protected SynchronizedSortedMap(Char2BooleanSortedMap m) {
            super(m);
            this.sortedMap = m;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public CharComparator comparator() {
            Object object = this.sync;
            synchronized (object) {
                return this.sortedMap.comparator();
            }
        }

        @Override
        public ObjectSortedSet<Char2BooleanMap.Entry> char2BooleanEntrySet() {
            if (this.entries == null) {
                this.entries = ObjectSortedSets.synchronize(this.sortedMap.char2BooleanEntrySet(), this.sync);
            }
            return (ObjectSortedSet)this.entries;
        }

        @Deprecated
        @Override
        public ObjectSortedSet<Map.Entry<Character, Boolean>> entrySet() {
            return this.char2BooleanEntrySet();
        }

        @Override
        public CharSortedSet keySet() {
            if (this.keys == null) {
                this.keys = CharSortedSets.synchronize(this.sortedMap.keySet(), this.sync);
            }
            return (CharSortedSet)this.keys;
        }

        @Override
        public Char2BooleanSortedMap subMap(char from, char to) {
            return new SynchronizedSortedMap(this.sortedMap.subMap(from, to), this.sync);
        }

        @Override
        public Char2BooleanSortedMap headMap(char to) {
            return new SynchronizedSortedMap(this.sortedMap.headMap(to), this.sync);
        }

        @Override
        public Char2BooleanSortedMap tailMap(char from) {
            return new SynchronizedSortedMap(this.sortedMap.tailMap(from), this.sync);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public char firstCharKey() {
            Object object = this.sync;
            synchronized (object) {
                return this.sortedMap.firstCharKey();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public char lastCharKey() {
            Object object = this.sync;
            synchronized (object) {
                return this.sortedMap.lastCharKey();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Deprecated
        @Override
        public Character firstKey() {
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
        public Character lastKey() {
            Object object = this.sync;
            synchronized (object) {
                return this.sortedMap.lastKey();
            }
        }

        @Deprecated
        @Override
        public Char2BooleanSortedMap subMap(Character from, Character to) {
            return new SynchronizedSortedMap(this.sortedMap.subMap(from, to), this.sync);
        }

        @Deprecated
        @Override
        public Char2BooleanSortedMap headMap(Character to) {
            return new SynchronizedSortedMap(this.sortedMap.headMap(to), this.sync);
        }

        @Deprecated
        @Override
        public Char2BooleanSortedMap tailMap(Character from) {
            return new SynchronizedSortedMap(this.sortedMap.tailMap(from), this.sync);
        }
    }

    public static class Singleton
    extends Char2BooleanMaps.Singleton
    implements Char2BooleanSortedMap,
    Serializable,
    Cloneable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final CharComparator comparator;

        protected Singleton(char key, boolean value, CharComparator comparator) {
            super(key, value);
            this.comparator = comparator;
        }

        protected Singleton(char key, boolean value) {
            this(key, value, null);
        }

        final int compare(char k1, char k2) {
            return this.comparator == null ? Character.compare(k1, k2) : this.comparator.compare(k1, k2);
        }

        @Override
        public CharComparator comparator() {
            return this.comparator;
        }

        @Override
        public ObjectSortedSet<Char2BooleanMap.Entry> char2BooleanEntrySet() {
            if (this.entries == null) {
                this.entries = ObjectSortedSets.singleton(new AbstractChar2BooleanMap.BasicEntry(this.key, this.value), Char2BooleanSortedMaps.entryComparator(this.comparator));
            }
            return (ObjectSortedSet)this.entries;
        }

        @Deprecated
        @Override
        public ObjectSortedSet<Map.Entry<Character, Boolean>> entrySet() {
            return this.char2BooleanEntrySet();
        }

        @Override
        public CharSortedSet keySet() {
            if (this.keys == null) {
                this.keys = CharSortedSets.singleton(this.key, this.comparator);
            }
            return (CharSortedSet)this.keys;
        }

        @Override
        public Char2BooleanSortedMap subMap(char from, char to) {
            if (this.compare(from, this.key) <= 0 && this.compare(this.key, to) < 0) {
                return this;
            }
            return Char2BooleanSortedMaps.EMPTY_MAP;
        }

        @Override
        public Char2BooleanSortedMap headMap(char to) {
            if (this.compare(this.key, to) < 0) {
                return this;
            }
            return Char2BooleanSortedMaps.EMPTY_MAP;
        }

        @Override
        public Char2BooleanSortedMap tailMap(char from) {
            if (this.compare(from, this.key) <= 0) {
                return this;
            }
            return Char2BooleanSortedMaps.EMPTY_MAP;
        }

        @Override
        public char firstCharKey() {
            return this.key;
        }

        @Override
        public char lastCharKey() {
            return this.key;
        }

        @Deprecated
        @Override
        public Char2BooleanSortedMap headMap(Character oto) {
            return this.headMap(oto.charValue());
        }

        @Deprecated
        @Override
        public Char2BooleanSortedMap tailMap(Character ofrom) {
            return this.tailMap(ofrom.charValue());
        }

        @Deprecated
        @Override
        public Char2BooleanSortedMap subMap(Character ofrom, Character oto) {
            return this.subMap(ofrom.charValue(), oto.charValue());
        }

        @Deprecated
        @Override
        public Character firstKey() {
            return Character.valueOf(this.firstCharKey());
        }

        @Deprecated
        @Override
        public Character lastKey() {
            return Character.valueOf(this.lastCharKey());
        }
    }

    public static class EmptySortedMap
    extends Char2BooleanMaps.EmptyMap
    implements Char2BooleanSortedMap,
    Serializable,
    Cloneable {
        private static final long serialVersionUID = -7046029254386353129L;

        protected EmptySortedMap() {
        }

        @Override
        public CharComparator comparator() {
            return null;
        }

        @Override
        public ObjectSortedSet<Char2BooleanMap.Entry> char2BooleanEntrySet() {
            return ObjectSortedSets.EMPTY_SET;
        }

        @Deprecated
        @Override
        public ObjectSortedSet<Map.Entry<Character, Boolean>> entrySet() {
            return ObjectSortedSets.EMPTY_SET;
        }

        @Override
        public CharSortedSet keySet() {
            return CharSortedSets.EMPTY_SET;
        }

        @Override
        public Char2BooleanSortedMap subMap(char from, char to) {
            return Char2BooleanSortedMaps.EMPTY_MAP;
        }

        @Override
        public Char2BooleanSortedMap headMap(char to) {
            return Char2BooleanSortedMaps.EMPTY_MAP;
        }

        @Override
        public Char2BooleanSortedMap tailMap(char from) {
            return Char2BooleanSortedMaps.EMPTY_MAP;
        }

        @Override
        public char firstCharKey() {
            throw new NoSuchElementException();
        }

        @Override
        public char lastCharKey() {
            throw new NoSuchElementException();
        }

        @Deprecated
        @Override
        public Char2BooleanSortedMap headMap(Character oto) {
            return this.headMap(oto.charValue());
        }

        @Deprecated
        @Override
        public Char2BooleanSortedMap tailMap(Character ofrom) {
            return this.tailMap(ofrom.charValue());
        }

        @Deprecated
        @Override
        public Char2BooleanSortedMap subMap(Character ofrom, Character oto) {
            return this.subMap(ofrom.charValue(), oto.charValue());
        }

        @Deprecated
        @Override
        public Character firstKey() {
            return Character.valueOf(this.firstCharKey());
        }

        @Deprecated
        @Override
        public Character lastKey() {
            return Character.valueOf(this.lastCharKey());
        }
    }

}

