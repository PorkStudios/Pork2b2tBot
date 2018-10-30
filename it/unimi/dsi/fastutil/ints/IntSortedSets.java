/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.ints;

import it.unimi.dsi.fastutil.ints.IntBidirectionalIterator;
import it.unimi.dsi.fastutil.ints.IntComparator;
import it.unimi.dsi.fastutil.ints.IntIterator;
import it.unimi.dsi.fastutil.ints.IntIterators;
import it.unimi.dsi.fastutil.ints.IntListIterator;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.ints.IntSets;
import it.unimi.dsi.fastutil.ints.IntSortedSet;
import java.io.Serializable;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.SortedSet;

public final class IntSortedSets {
    public static final EmptySet EMPTY_SET = new EmptySet();

    private IntSortedSets() {
    }

    public static IntSortedSet singleton(int element) {
        return new Singleton(element);
    }

    public static IntSortedSet singleton(int element, IntComparator comparator) {
        return new Singleton(element, comparator);
    }

    public static IntSortedSet singleton(Object element) {
        return new Singleton((int)((Integer)element));
    }

    public static IntSortedSet singleton(Object element, IntComparator comparator) {
        return new Singleton((int)((Integer)element), comparator);
    }

    public static IntSortedSet synchronize(IntSortedSet s) {
        return new SynchronizedSortedSet(s);
    }

    public static IntSortedSet synchronize(IntSortedSet s, Object sync) {
        return new SynchronizedSortedSet(s, sync);
    }

    public static IntSortedSet unmodifiable(IntSortedSet s) {
        return new UnmodifiableSortedSet(s);
    }

    public static class UnmodifiableSortedSet
    extends IntSets.UnmodifiableSet
    implements IntSortedSet,
    Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final IntSortedSet sortedSet;

        protected UnmodifiableSortedSet(IntSortedSet s) {
            super(s);
            this.sortedSet = s;
        }

        @Override
        public IntComparator comparator() {
            return this.sortedSet.comparator();
        }

        @Override
        public IntSortedSet subSet(int from, int to) {
            return new UnmodifiableSortedSet(this.sortedSet.subSet(from, to));
        }

        @Override
        public IntSortedSet headSet(int to) {
            return new UnmodifiableSortedSet(this.sortedSet.headSet(to));
        }

        @Override
        public IntSortedSet tailSet(int from) {
            return new UnmodifiableSortedSet(this.sortedSet.tailSet(from));
        }

        @Override
        public IntBidirectionalIterator iterator() {
            return IntIterators.unmodifiable(this.sortedSet.iterator());
        }

        @Override
        public IntBidirectionalIterator iterator(int from) {
            return IntIterators.unmodifiable(this.sortedSet.iterator(from));
        }

        @Override
        public int firstInt() {
            return this.sortedSet.firstInt();
        }

        @Override
        public int lastInt() {
            return this.sortedSet.lastInt();
        }

        @Deprecated
        @Override
        public Integer first() {
            return this.sortedSet.first();
        }

        @Deprecated
        @Override
        public Integer last() {
            return this.sortedSet.last();
        }

        @Deprecated
        @Override
        public IntSortedSet subSet(Integer from, Integer to) {
            return new UnmodifiableSortedSet(this.sortedSet.subSet(from, to));
        }

        @Deprecated
        @Override
        public IntSortedSet headSet(Integer to) {
            return new UnmodifiableSortedSet(this.sortedSet.headSet(to));
        }

        @Deprecated
        @Override
        public IntSortedSet tailSet(Integer from) {
            return new UnmodifiableSortedSet(this.sortedSet.tailSet(from));
        }
    }

    public static class SynchronizedSortedSet
    extends IntSets.SynchronizedSet
    implements IntSortedSet,
    Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final IntSortedSet sortedSet;

        protected SynchronizedSortedSet(IntSortedSet s, Object sync) {
            super(s, sync);
            this.sortedSet = s;
        }

        protected SynchronizedSortedSet(IntSortedSet s) {
            super(s);
            this.sortedSet = s;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public IntComparator comparator() {
            Object object = this.sync;
            synchronized (object) {
                return this.sortedSet.comparator();
            }
        }

        @Override
        public IntSortedSet subSet(int from, int to) {
            return new SynchronizedSortedSet(this.sortedSet.subSet(from, to), this.sync);
        }

        @Override
        public IntSortedSet headSet(int to) {
            return new SynchronizedSortedSet(this.sortedSet.headSet(to), this.sync);
        }

        @Override
        public IntSortedSet tailSet(int from) {
            return new SynchronizedSortedSet(this.sortedSet.tailSet(from), this.sync);
        }

        @Override
        public IntBidirectionalIterator iterator() {
            return this.sortedSet.iterator();
        }

        @Override
        public IntBidirectionalIterator iterator(int from) {
            return this.sortedSet.iterator(from);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public int firstInt() {
            Object object = this.sync;
            synchronized (object) {
                return this.sortedSet.firstInt();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public int lastInt() {
            Object object = this.sync;
            synchronized (object) {
                return this.sortedSet.lastInt();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Deprecated
        @Override
        public Integer first() {
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
        public Integer last() {
            Object object = this.sync;
            synchronized (object) {
                return this.sortedSet.last();
            }
        }

        @Deprecated
        @Override
        public IntSortedSet subSet(Integer from, Integer to) {
            return new SynchronizedSortedSet(this.sortedSet.subSet(from, to), this.sync);
        }

        @Deprecated
        @Override
        public IntSortedSet headSet(Integer to) {
            return new SynchronizedSortedSet(this.sortedSet.headSet(to), this.sync);
        }

        @Deprecated
        @Override
        public IntSortedSet tailSet(Integer from) {
            return new SynchronizedSortedSet(this.sortedSet.tailSet(from), this.sync);
        }
    }

    public static class Singleton
    extends IntSets.Singleton
    implements IntSortedSet,
    Serializable,
    Cloneable {
        private static final long serialVersionUID = -7046029254386353129L;
        final IntComparator comparator;

        protected Singleton(int element, IntComparator comparator) {
            super(element);
            this.comparator = comparator;
        }

        private Singleton(int element) {
            this(element, (IntComparator)null);
        }

        final int compare(int k1, int k2) {
            return this.comparator == null ? Integer.compare(k1, k2) : this.comparator.compare(k1, k2);
        }

        @Override
        public IntBidirectionalIterator iterator(int from) {
            IntBidirectionalIterator i = this.iterator();
            if (this.compare(this.element, from) <= 0) {
                i.nextInt();
            }
            return i;
        }

        @Override
        public IntComparator comparator() {
            return this.comparator;
        }

        @Override
        public IntSortedSet subSet(int from, int to) {
            if (this.compare(from, this.element) <= 0 && this.compare(this.element, to) < 0) {
                return this;
            }
            return IntSortedSets.EMPTY_SET;
        }

        @Override
        public IntSortedSet headSet(int to) {
            if (this.compare(this.element, to) < 0) {
                return this;
            }
            return IntSortedSets.EMPTY_SET;
        }

        @Override
        public IntSortedSet tailSet(int from) {
            if (this.compare(from, this.element) <= 0) {
                return this;
            }
            return IntSortedSets.EMPTY_SET;
        }

        @Override
        public int firstInt() {
            return this.element;
        }

        @Override
        public int lastInt() {
            return this.element;
        }

        @Deprecated
        @Override
        public IntSortedSet subSet(Integer from, Integer to) {
            return this.subSet((int)from, (int)to);
        }

        @Deprecated
        @Override
        public IntSortedSet headSet(Integer to) {
            return this.headSet((int)to);
        }

        @Deprecated
        @Override
        public IntSortedSet tailSet(Integer from) {
            return this.tailSet((int)from);
        }

        @Deprecated
        @Override
        public Integer first() {
            return this.element;
        }

        @Deprecated
        @Override
        public Integer last() {
            return this.element;
        }
    }

    public static class EmptySet
    extends IntSets.EmptySet
    implements IntSortedSet,
    Serializable,
    Cloneable {
        private static final long serialVersionUID = -7046029254386353129L;

        protected EmptySet() {
        }

        @Override
        public IntBidirectionalIterator iterator(int from) {
            return IntIterators.EMPTY_ITERATOR;
        }

        @Override
        public IntSortedSet subSet(int from, int to) {
            return IntSortedSets.EMPTY_SET;
        }

        @Override
        public IntSortedSet headSet(int from) {
            return IntSortedSets.EMPTY_SET;
        }

        @Override
        public IntSortedSet tailSet(int to) {
            return IntSortedSets.EMPTY_SET;
        }

        @Override
        public int firstInt() {
            throw new NoSuchElementException();
        }

        @Override
        public int lastInt() {
            throw new NoSuchElementException();
        }

        @Override
        public IntComparator comparator() {
            return null;
        }

        @Deprecated
        @Override
        public IntSortedSet subSet(Integer from, Integer to) {
            return IntSortedSets.EMPTY_SET;
        }

        @Deprecated
        @Override
        public IntSortedSet headSet(Integer from) {
            return IntSortedSets.EMPTY_SET;
        }

        @Deprecated
        @Override
        public IntSortedSet tailSet(Integer to) {
            return IntSortedSets.EMPTY_SET;
        }

        @Deprecated
        @Override
        public Integer first() {
            throw new NoSuchElementException();
        }

        @Deprecated
        @Override
        public Integer last() {
            throw new NoSuchElementException();
        }

        @Override
        public Object clone() {
            return IntSortedSets.EMPTY_SET;
        }

        private Object readResolve() {
            return IntSortedSets.EMPTY_SET;
        }
    }

}

