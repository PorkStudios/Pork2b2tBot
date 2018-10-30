/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.floats.FloatBidirectionalIterator;
import it.unimi.dsi.fastutil.floats.FloatComparator;
import it.unimi.dsi.fastutil.floats.FloatIterator;
import it.unimi.dsi.fastutil.floats.FloatIterators;
import it.unimi.dsi.fastutil.floats.FloatListIterator;
import it.unimi.dsi.fastutil.floats.FloatSet;
import it.unimi.dsi.fastutil.floats.FloatSets;
import it.unimi.dsi.fastutil.floats.FloatSortedSet;
import java.io.Serializable;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.SortedSet;

public final class FloatSortedSets {
    public static final EmptySet EMPTY_SET = new EmptySet();

    private FloatSortedSets() {
    }

    public static FloatSortedSet singleton(float element) {
        return new Singleton(element);
    }

    public static FloatSortedSet singleton(float element, FloatComparator comparator) {
        return new Singleton(element, comparator);
    }

    public static FloatSortedSet singleton(Object element) {
        return new Singleton(((Float)element).floatValue());
    }

    public static FloatSortedSet singleton(Object element, FloatComparator comparator) {
        return new Singleton(((Float)element).floatValue(), comparator);
    }

    public static FloatSortedSet synchronize(FloatSortedSet s) {
        return new SynchronizedSortedSet(s);
    }

    public static FloatSortedSet synchronize(FloatSortedSet s, Object sync) {
        return new SynchronizedSortedSet(s, sync);
    }

    public static FloatSortedSet unmodifiable(FloatSortedSet s) {
        return new UnmodifiableSortedSet(s);
    }

    public static class UnmodifiableSortedSet
    extends FloatSets.UnmodifiableSet
    implements FloatSortedSet,
    Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final FloatSortedSet sortedSet;

        protected UnmodifiableSortedSet(FloatSortedSet s) {
            super(s);
            this.sortedSet = s;
        }

        @Override
        public FloatComparator comparator() {
            return this.sortedSet.comparator();
        }

        @Override
        public FloatSortedSet subSet(float from, float to) {
            return new UnmodifiableSortedSet(this.sortedSet.subSet(from, to));
        }

        @Override
        public FloatSortedSet headSet(float to) {
            return new UnmodifiableSortedSet(this.sortedSet.headSet(to));
        }

        @Override
        public FloatSortedSet tailSet(float from) {
            return new UnmodifiableSortedSet(this.sortedSet.tailSet(from));
        }

        @Override
        public FloatBidirectionalIterator iterator() {
            return FloatIterators.unmodifiable(this.sortedSet.iterator());
        }

        @Override
        public FloatBidirectionalIterator iterator(float from) {
            return FloatIterators.unmodifiable(this.sortedSet.iterator(from));
        }

        @Override
        public float firstFloat() {
            return this.sortedSet.firstFloat();
        }

        @Override
        public float lastFloat() {
            return this.sortedSet.lastFloat();
        }

        @Deprecated
        @Override
        public Float first() {
            return this.sortedSet.first();
        }

        @Deprecated
        @Override
        public Float last() {
            return this.sortedSet.last();
        }

        @Deprecated
        @Override
        public FloatSortedSet subSet(Float from, Float to) {
            return new UnmodifiableSortedSet(this.sortedSet.subSet(from, to));
        }

        @Deprecated
        @Override
        public FloatSortedSet headSet(Float to) {
            return new UnmodifiableSortedSet(this.sortedSet.headSet(to));
        }

        @Deprecated
        @Override
        public FloatSortedSet tailSet(Float from) {
            return new UnmodifiableSortedSet(this.sortedSet.tailSet(from));
        }
    }

    public static class SynchronizedSortedSet
    extends FloatSets.SynchronizedSet
    implements FloatSortedSet,
    Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final FloatSortedSet sortedSet;

        protected SynchronizedSortedSet(FloatSortedSet s, Object sync) {
            super(s, sync);
            this.sortedSet = s;
        }

        protected SynchronizedSortedSet(FloatSortedSet s) {
            super(s);
            this.sortedSet = s;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public FloatComparator comparator() {
            Object object = this.sync;
            synchronized (object) {
                return this.sortedSet.comparator();
            }
        }

        @Override
        public FloatSortedSet subSet(float from, float to) {
            return new SynchronizedSortedSet(this.sortedSet.subSet(from, to), this.sync);
        }

        @Override
        public FloatSortedSet headSet(float to) {
            return new SynchronizedSortedSet(this.sortedSet.headSet(to), this.sync);
        }

        @Override
        public FloatSortedSet tailSet(float from) {
            return new SynchronizedSortedSet(this.sortedSet.tailSet(from), this.sync);
        }

        @Override
        public FloatBidirectionalIterator iterator() {
            return this.sortedSet.iterator();
        }

        @Override
        public FloatBidirectionalIterator iterator(float from) {
            return this.sortedSet.iterator(from);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public float firstFloat() {
            Object object = this.sync;
            synchronized (object) {
                return this.sortedSet.firstFloat();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public float lastFloat() {
            Object object = this.sync;
            synchronized (object) {
                return this.sortedSet.lastFloat();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Deprecated
        @Override
        public Float first() {
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
        public Float last() {
            Object object = this.sync;
            synchronized (object) {
                return this.sortedSet.last();
            }
        }

        @Deprecated
        @Override
        public FloatSortedSet subSet(Float from, Float to) {
            return new SynchronizedSortedSet(this.sortedSet.subSet(from, to), this.sync);
        }

        @Deprecated
        @Override
        public FloatSortedSet headSet(Float to) {
            return new SynchronizedSortedSet(this.sortedSet.headSet(to), this.sync);
        }

        @Deprecated
        @Override
        public FloatSortedSet tailSet(Float from) {
            return new SynchronizedSortedSet(this.sortedSet.tailSet(from), this.sync);
        }
    }

    public static class Singleton
    extends FloatSets.Singleton
    implements FloatSortedSet,
    Serializable,
    Cloneable {
        private static final long serialVersionUID = -7046029254386353129L;
        final FloatComparator comparator;

        protected Singleton(float element, FloatComparator comparator) {
            super(element);
            this.comparator = comparator;
        }

        private Singleton(float element) {
            this(element, (FloatComparator)null);
        }

        final int compare(float k1, float k2) {
            return this.comparator == null ? Float.compare(k1, k2) : this.comparator.compare(k1, k2);
        }

        @Override
        public FloatBidirectionalIterator iterator(float from) {
            FloatBidirectionalIterator i = this.iterator();
            if (this.compare(this.element, from) <= 0) {
                i.nextFloat();
            }
            return i;
        }

        @Override
        public FloatComparator comparator() {
            return this.comparator;
        }

        @Override
        public FloatSortedSet subSet(float from, float to) {
            if (this.compare(from, this.element) <= 0 && this.compare(this.element, to) < 0) {
                return this;
            }
            return FloatSortedSets.EMPTY_SET;
        }

        @Override
        public FloatSortedSet headSet(float to) {
            if (this.compare(this.element, to) < 0) {
                return this;
            }
            return FloatSortedSets.EMPTY_SET;
        }

        @Override
        public FloatSortedSet tailSet(float from) {
            if (this.compare(from, this.element) <= 0) {
                return this;
            }
            return FloatSortedSets.EMPTY_SET;
        }

        @Override
        public float firstFloat() {
            return this.element;
        }

        @Override
        public float lastFloat() {
            return this.element;
        }

        @Deprecated
        @Override
        public FloatSortedSet subSet(Float from, Float to) {
            return this.subSet(from.floatValue(), to.floatValue());
        }

        @Deprecated
        @Override
        public FloatSortedSet headSet(Float to) {
            return this.headSet(to.floatValue());
        }

        @Deprecated
        @Override
        public FloatSortedSet tailSet(Float from) {
            return this.tailSet(from.floatValue());
        }

        @Deprecated
        @Override
        public Float first() {
            return Float.valueOf(this.element);
        }

        @Deprecated
        @Override
        public Float last() {
            return Float.valueOf(this.element);
        }
    }

    public static class EmptySet
    extends FloatSets.EmptySet
    implements FloatSortedSet,
    Serializable,
    Cloneable {
        private static final long serialVersionUID = -7046029254386353129L;

        protected EmptySet() {
        }

        @Override
        public FloatBidirectionalIterator iterator(float from) {
            return FloatIterators.EMPTY_ITERATOR;
        }

        @Override
        public FloatSortedSet subSet(float from, float to) {
            return FloatSortedSets.EMPTY_SET;
        }

        @Override
        public FloatSortedSet headSet(float from) {
            return FloatSortedSets.EMPTY_SET;
        }

        @Override
        public FloatSortedSet tailSet(float to) {
            return FloatSortedSets.EMPTY_SET;
        }

        @Override
        public float firstFloat() {
            throw new NoSuchElementException();
        }

        @Override
        public float lastFloat() {
            throw new NoSuchElementException();
        }

        @Override
        public FloatComparator comparator() {
            return null;
        }

        @Deprecated
        @Override
        public FloatSortedSet subSet(Float from, Float to) {
            return FloatSortedSets.EMPTY_SET;
        }

        @Deprecated
        @Override
        public FloatSortedSet headSet(Float from) {
            return FloatSortedSets.EMPTY_SET;
        }

        @Deprecated
        @Override
        public FloatSortedSet tailSet(Float to) {
            return FloatSortedSets.EMPTY_SET;
        }

        @Deprecated
        @Override
        public Float first() {
            throw new NoSuchElementException();
        }

        @Deprecated
        @Override
        public Float last() {
            throw new NoSuchElementException();
        }

        @Override
        public Object clone() {
            return FloatSortedSets.EMPTY_SET;
        }

        private Object readResolve() {
            return FloatSortedSets.EMPTY_SET;
        }
    }

}

