/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.longs.LongBidirectionalIterator;
import it.unimi.dsi.fastutil.longs.LongComparator;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongIterators;
import it.unimi.dsi.fastutil.longs.LongListIterator;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.longs.LongSets;
import it.unimi.dsi.fastutil.longs.LongSortedSet;
import java.io.Serializable;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.SortedSet;

public final class LongSortedSets {
    public static final EmptySet EMPTY_SET = new EmptySet();

    private LongSortedSets() {
    }

    public static LongSortedSet singleton(long element) {
        return new Singleton(element);
    }

    public static LongSortedSet singleton(long element, LongComparator comparator) {
        return new Singleton(element, comparator);
    }

    public static LongSortedSet singleton(Object element) {
        return new Singleton((long)((Long)element));
    }

    public static LongSortedSet singleton(Object element, LongComparator comparator) {
        return new Singleton((long)((Long)element), comparator);
    }

    public static LongSortedSet synchronize(LongSortedSet s) {
        return new SynchronizedSortedSet(s);
    }

    public static LongSortedSet synchronize(LongSortedSet s, Object sync) {
        return new SynchronizedSortedSet(s, sync);
    }

    public static LongSortedSet unmodifiable(LongSortedSet s) {
        return new UnmodifiableSortedSet(s);
    }

    public static class UnmodifiableSortedSet
    extends LongSets.UnmodifiableSet
    implements LongSortedSet,
    Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final LongSortedSet sortedSet;

        protected UnmodifiableSortedSet(LongSortedSet s) {
            super(s);
            this.sortedSet = s;
        }

        @Override
        public LongComparator comparator() {
            return this.sortedSet.comparator();
        }

        @Override
        public LongSortedSet subSet(long from, long to) {
            return new UnmodifiableSortedSet(this.sortedSet.subSet(from, to));
        }

        @Override
        public LongSortedSet headSet(long to) {
            return new UnmodifiableSortedSet(this.sortedSet.headSet(to));
        }

        @Override
        public LongSortedSet tailSet(long from) {
            return new UnmodifiableSortedSet(this.sortedSet.tailSet(from));
        }

        @Override
        public LongBidirectionalIterator iterator() {
            return LongIterators.unmodifiable(this.sortedSet.iterator());
        }

        @Override
        public LongBidirectionalIterator iterator(long from) {
            return LongIterators.unmodifiable(this.sortedSet.iterator(from));
        }

        @Override
        public long firstLong() {
            return this.sortedSet.firstLong();
        }

        @Override
        public long lastLong() {
            return this.sortedSet.lastLong();
        }

        @Deprecated
        @Override
        public Long first() {
            return this.sortedSet.first();
        }

        @Deprecated
        @Override
        public Long last() {
            return this.sortedSet.last();
        }

        @Deprecated
        @Override
        public LongSortedSet subSet(Long from, Long to) {
            return new UnmodifiableSortedSet(this.sortedSet.subSet(from, to));
        }

        @Deprecated
        @Override
        public LongSortedSet headSet(Long to) {
            return new UnmodifiableSortedSet(this.sortedSet.headSet(to));
        }

        @Deprecated
        @Override
        public LongSortedSet tailSet(Long from) {
            return new UnmodifiableSortedSet(this.sortedSet.tailSet(from));
        }
    }

    public static class SynchronizedSortedSet
    extends LongSets.SynchronizedSet
    implements LongSortedSet,
    Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final LongSortedSet sortedSet;

        protected SynchronizedSortedSet(LongSortedSet s, Object sync) {
            super(s, sync);
            this.sortedSet = s;
        }

        protected SynchronizedSortedSet(LongSortedSet s) {
            super(s);
            this.sortedSet = s;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public LongComparator comparator() {
            Object object = this.sync;
            synchronized (object) {
                return this.sortedSet.comparator();
            }
        }

        @Override
        public LongSortedSet subSet(long from, long to) {
            return new SynchronizedSortedSet(this.sortedSet.subSet(from, to), this.sync);
        }

        @Override
        public LongSortedSet headSet(long to) {
            return new SynchronizedSortedSet(this.sortedSet.headSet(to), this.sync);
        }

        @Override
        public LongSortedSet tailSet(long from) {
            return new SynchronizedSortedSet(this.sortedSet.tailSet(from), this.sync);
        }

        @Override
        public LongBidirectionalIterator iterator() {
            return this.sortedSet.iterator();
        }

        @Override
        public LongBidirectionalIterator iterator(long from) {
            return this.sortedSet.iterator(from);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public long firstLong() {
            Object object = this.sync;
            synchronized (object) {
                return this.sortedSet.firstLong();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public long lastLong() {
            Object object = this.sync;
            synchronized (object) {
                return this.sortedSet.lastLong();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Deprecated
        @Override
        public Long first() {
            Object object = this.sync;
            synchronized (object) {
                return this.sortedSet.first();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Deprecated
        @Override
        public Long last() {
            Object object = this.sync;
            synchronized (object) {
                return this.sortedSet.last();
            }
        }

        @Deprecated
        @Override
        public LongSortedSet subSet(Long from, Long to) {
            return new SynchronizedSortedSet(this.sortedSet.subSet(from, to), this.sync);
        }

        @Deprecated
        @Override
        public LongSortedSet headSet(Long to) {
            return new SynchronizedSortedSet(this.sortedSet.headSet(to), this.sync);
        }

        @Deprecated
        @Override
        public LongSortedSet tailSet(Long from) {
            return new SynchronizedSortedSet(this.sortedSet.tailSet(from), this.sync);
        }
    }

    public static class Singleton
    extends LongSets.Singleton
    implements LongSortedSet,
    Serializable,
    Cloneable {
        private static final long serialVersionUID = -7046029254386353129L;
        final LongComparator comparator;

        protected Singleton(long element, LongComparator comparator) {
            super(element);
            this.comparator = comparator;
        }

        private Singleton(long element) {
            this(element, (LongComparator)null);
        }

        final int compare(long k1, long k2) {
            return this.comparator == null ? Long.compare(k1, k2) : this.comparator.compare(k1, k2);
        }

        @Override
        public LongBidirectionalIterator iterator(long from) {
            LongBidirectionalIterator i = this.iterator();
            if (this.compare(this.element, from) <= 0) {
                i.nextLong();
            }
            return i;
        }

        @Override
        public LongComparator comparator() {
            return this.comparator;
        }

        @Override
        public LongSortedSet subSet(long from, long to) {
            if (this.compare(from, this.element) <= 0 && this.compare(this.element, to) < 0) {
                return this;
            }
            return LongSortedSets.EMPTY_SET;
        }

        @Override
        public LongSortedSet headSet(long to) {
            if (this.compare(this.element, to) < 0) {
                return this;
            }
            return LongSortedSets.EMPTY_SET;
        }

        @Override
        public LongSortedSet tailSet(long from) {
            if (this.compare(from, this.element) <= 0) {
                return this;
            }
            return LongSortedSets.EMPTY_SET;
        }

        @Override
        public long firstLong() {
            return this.element;
        }

        @Override
        public long lastLong() {
            return this.element;
        }

        @Deprecated
        @Override
        public LongSortedSet subSet(Long from, Long to) {
            return this.subSet((long)from, (long)to);
        }

        @Deprecated
        @Override
        public LongSortedSet headSet(Long to) {
            return this.headSet((long)to);
        }

        @Deprecated
        @Override
        public LongSortedSet tailSet(Long from) {
            return this.tailSet((long)from);
        }

        @Deprecated
        @Override
        public Long first() {
            return this.element;
        }

        @Deprecated
        @Override
        public Long last() {
            return this.element;
        }
    }

    public static class EmptySet
    extends LongSets.EmptySet
    implements LongSortedSet,
    Serializable,
    Cloneable {
        private static final long serialVersionUID = -7046029254386353129L;

        protected EmptySet() {
        }

        @Override
        public LongBidirectionalIterator iterator(long from) {
            return LongIterators.EMPTY_ITERATOR;
        }

        @Override
        public LongSortedSet subSet(long from, long to) {
            return LongSortedSets.EMPTY_SET;
        }

        @Override
        public LongSortedSet headSet(long from) {
            return LongSortedSets.EMPTY_SET;
        }

        @Override
        public LongSortedSet tailSet(long to) {
            return LongSortedSets.EMPTY_SET;
        }

        @Override
        public long firstLong() {
            throw new NoSuchElementException();
        }

        @Override
        public long lastLong() {
            throw new NoSuchElementException();
        }

        @Override
        public LongComparator comparator() {
            return null;
        }

        @Deprecated
        @Override
        public LongSortedSet subSet(Long from, Long to) {
            return LongSortedSets.EMPTY_SET;
        }

        @Deprecated
        @Override
        public LongSortedSet headSet(Long from) {
            return LongSortedSets.EMPTY_SET;
        }

        @Deprecated
        @Override
        public LongSortedSet tailSet(Long to) {
            return LongSortedSets.EMPTY_SET;
        }

        @Deprecated
        @Override
        public Long first() {
            throw new NoSuchElementException();
        }

        @Deprecated
        @Override
        public Long last() {
            throw new NoSuchElementException();
        }

        @Override
        public Object clone() {
            return LongSortedSets.EMPTY_SET;
        }

        private Object readResolve() {
            return LongSortedSets.EMPTY_SET;
        }
    }

}

