/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.doubles;

import it.unimi.dsi.fastutil.doubles.DoubleBidirectionalIterator;
import it.unimi.dsi.fastutil.doubles.DoubleComparator;
import it.unimi.dsi.fastutil.doubles.DoubleIterator;
import it.unimi.dsi.fastutil.doubles.DoubleIterators;
import it.unimi.dsi.fastutil.doubles.DoubleListIterator;
import it.unimi.dsi.fastutil.doubles.DoubleSet;
import it.unimi.dsi.fastutil.doubles.DoubleSets;
import it.unimi.dsi.fastutil.doubles.DoubleSortedSet;
import java.io.Serializable;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.SortedSet;

public final class DoubleSortedSets {
    public static final EmptySet EMPTY_SET = new EmptySet();

    private DoubleSortedSets() {
    }

    public static DoubleSortedSet singleton(double element) {
        return new Singleton(element);
    }

    public static DoubleSortedSet singleton(double element, DoubleComparator comparator) {
        return new Singleton(element, comparator);
    }

    public static DoubleSortedSet singleton(Object element) {
        return new Singleton((double)((Double)element));
    }

    public static DoubleSortedSet singleton(Object element, DoubleComparator comparator) {
        return new Singleton((double)((Double)element), comparator);
    }

    public static DoubleSortedSet synchronize(DoubleSortedSet s) {
        return new SynchronizedSortedSet(s);
    }

    public static DoubleSortedSet synchronize(DoubleSortedSet s, Object sync) {
        return new SynchronizedSortedSet(s, sync);
    }

    public static DoubleSortedSet unmodifiable(DoubleSortedSet s) {
        return new UnmodifiableSortedSet(s);
    }

    public static class UnmodifiableSortedSet
    extends DoubleSets.UnmodifiableSet
    implements DoubleSortedSet,
    Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final DoubleSortedSet sortedSet;

        protected UnmodifiableSortedSet(DoubleSortedSet s) {
            super(s);
            this.sortedSet = s;
        }

        @Override
        public DoubleComparator comparator() {
            return this.sortedSet.comparator();
        }

        @Override
        public DoubleSortedSet subSet(double from, double to) {
            return new UnmodifiableSortedSet(this.sortedSet.subSet(from, to));
        }

        @Override
        public DoubleSortedSet headSet(double to) {
            return new UnmodifiableSortedSet(this.sortedSet.headSet(to));
        }

        @Override
        public DoubleSortedSet tailSet(double from) {
            return new UnmodifiableSortedSet(this.sortedSet.tailSet(from));
        }

        @Override
        public DoubleBidirectionalIterator iterator() {
            return DoubleIterators.unmodifiable(this.sortedSet.iterator());
        }

        @Override
        public DoubleBidirectionalIterator iterator(double from) {
            return DoubleIterators.unmodifiable(this.sortedSet.iterator(from));
        }

        @Override
        public double firstDouble() {
            return this.sortedSet.firstDouble();
        }

        @Override
        public double lastDouble() {
            return this.sortedSet.lastDouble();
        }

        @Deprecated
        @Override
        public Double first() {
            return this.sortedSet.first();
        }

        @Deprecated
        @Override
        public Double last() {
            return this.sortedSet.last();
        }

        @Deprecated
        @Override
        public DoubleSortedSet subSet(Double from, Double to) {
            return new UnmodifiableSortedSet(this.sortedSet.subSet(from, to));
        }

        @Deprecated
        @Override
        public DoubleSortedSet headSet(Double to) {
            return new UnmodifiableSortedSet(this.sortedSet.headSet(to));
        }

        @Deprecated
        @Override
        public DoubleSortedSet tailSet(Double from) {
            return new UnmodifiableSortedSet(this.sortedSet.tailSet(from));
        }
    }

    public static class SynchronizedSortedSet
    extends DoubleSets.SynchronizedSet
    implements DoubleSortedSet,
    Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final DoubleSortedSet sortedSet;

        protected SynchronizedSortedSet(DoubleSortedSet s, Object sync) {
            super(s, sync);
            this.sortedSet = s;
        }

        protected SynchronizedSortedSet(DoubleSortedSet s) {
            super(s);
            this.sortedSet = s;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public DoubleComparator comparator() {
            Object object = this.sync;
            synchronized (object) {
                return this.sortedSet.comparator();
            }
        }

        @Override
        public DoubleSortedSet subSet(double from, double to) {
            return new SynchronizedSortedSet(this.sortedSet.subSet(from, to), this.sync);
        }

        @Override
        public DoubleSortedSet headSet(double to) {
            return new SynchronizedSortedSet(this.sortedSet.headSet(to), this.sync);
        }

        @Override
        public DoubleSortedSet tailSet(double from) {
            return new SynchronizedSortedSet(this.sortedSet.tailSet(from), this.sync);
        }

        @Override
        public DoubleBidirectionalIterator iterator() {
            return this.sortedSet.iterator();
        }

        @Override
        public DoubleBidirectionalIterator iterator(double from) {
            return this.sortedSet.iterator(from);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public double firstDouble() {
            Object object = this.sync;
            synchronized (object) {
                return this.sortedSet.firstDouble();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public double lastDouble() {
            Object object = this.sync;
            synchronized (object) {
                return this.sortedSet.lastDouble();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Deprecated
        @Override
        public Double first() {
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
        public Double last() {
            Object object = this.sync;
            synchronized (object) {
                return this.sortedSet.last();
            }
        }

        @Deprecated
        @Override
        public DoubleSortedSet subSet(Double from, Double to) {
            return new SynchronizedSortedSet(this.sortedSet.subSet(from, to), this.sync);
        }

        @Deprecated
        @Override
        public DoubleSortedSet headSet(Double to) {
            return new SynchronizedSortedSet(this.sortedSet.headSet(to), this.sync);
        }

        @Deprecated
        @Override
        public DoubleSortedSet tailSet(Double from) {
            return new SynchronizedSortedSet(this.sortedSet.tailSet(from), this.sync);
        }
    }

    public static class Singleton
    extends DoubleSets.Singleton
    implements DoubleSortedSet,
    Serializable,
    Cloneable {
        private static final long serialVersionUID = -7046029254386353129L;
        final DoubleComparator comparator;

        protected Singleton(double element, DoubleComparator comparator) {
            super(element);
            this.comparator = comparator;
        }

        private Singleton(double element) {
            this(element, (DoubleComparator)null);
        }

        final int compare(double k1, double k2) {
            return this.comparator == null ? Double.compare(k1, k2) : this.comparator.compare(k1, k2);
        }

        @Override
        public DoubleBidirectionalIterator iterator(double from) {
            DoubleBidirectionalIterator i = this.iterator();
            if (this.compare(this.element, from) <= 0) {
                i.nextDouble();
            }
            return i;
        }

        @Override
        public DoubleComparator comparator() {
            return this.comparator;
        }

        @Override
        public DoubleSortedSet subSet(double from, double to) {
            if (this.compare(from, this.element) <= 0 && this.compare(this.element, to) < 0) {
                return this;
            }
            return DoubleSortedSets.EMPTY_SET;
        }

        @Override
        public DoubleSortedSet headSet(double to) {
            if (this.compare(this.element, to) < 0) {
                return this;
            }
            return DoubleSortedSets.EMPTY_SET;
        }

        @Override
        public DoubleSortedSet tailSet(double from) {
            if (this.compare(from, this.element) <= 0) {
                return this;
            }
            return DoubleSortedSets.EMPTY_SET;
        }

        @Override
        public double firstDouble() {
            return this.element;
        }

        @Override
        public double lastDouble() {
            return this.element;
        }

        @Deprecated
        @Override
        public DoubleSortedSet subSet(Double from, Double to) {
            return this.subSet((double)from, (double)to);
        }

        @Deprecated
        @Override
        public DoubleSortedSet headSet(Double to) {
            return this.headSet((double)to);
        }

        @Deprecated
        @Override
        public DoubleSortedSet tailSet(Double from) {
            return this.tailSet((double)from);
        }

        @Deprecated
        @Override
        public Double first() {
            return this.element;
        }

        @Deprecated
        @Override
        public Double last() {
            return this.element;
        }
    }

    public static class EmptySet
    extends DoubleSets.EmptySet
    implements DoubleSortedSet,
    Serializable,
    Cloneable {
        private static final long serialVersionUID = -7046029254386353129L;

        protected EmptySet() {
        }

        @Override
        public DoubleBidirectionalIterator iterator(double from) {
            return DoubleIterators.EMPTY_ITERATOR;
        }

        @Override
        public DoubleSortedSet subSet(double from, double to) {
            return DoubleSortedSets.EMPTY_SET;
        }

        @Override
        public DoubleSortedSet headSet(double from) {
            return DoubleSortedSets.EMPTY_SET;
        }

        @Override
        public DoubleSortedSet tailSet(double to) {
            return DoubleSortedSets.EMPTY_SET;
        }

        @Override
        public double firstDouble() {
            throw new NoSuchElementException();
        }

        @Override
        public double lastDouble() {
            throw new NoSuchElementException();
        }

        @Override
        public DoubleComparator comparator() {
            return null;
        }

        @Deprecated
        @Override
        public DoubleSortedSet subSet(Double from, Double to) {
            return DoubleSortedSets.EMPTY_SET;
        }

        @Deprecated
        @Override
        public DoubleSortedSet headSet(Double from) {
            return DoubleSortedSets.EMPTY_SET;
        }

        @Deprecated
        @Override
        public DoubleSortedSet tailSet(Double to) {
            return DoubleSortedSets.EMPTY_SET;
        }

        @Deprecated
        @Override
        public Double first() {
            throw new NoSuchElementException();
        }

        @Deprecated
        @Override
        public Double last() {
            throw new NoSuchElementException();
        }

        @Override
        public Object clone() {
            return DoubleSortedSets.EMPTY_SET;
        }

        private Object readResolve() {
            return DoubleSortedSets.EMPTY_SET;
        }
    }

}

