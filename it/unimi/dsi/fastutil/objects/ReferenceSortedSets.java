/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectIterators;
import it.unimi.dsi.fastutil.objects.ReferenceSet;
import it.unimi.dsi.fastutil.objects.ReferenceSets;
import it.unimi.dsi.fastutil.objects.ReferenceSortedSet;
import java.io.Serializable;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.SortedSet;

public final class ReferenceSortedSets {
    public static final EmptySet EMPTY_SET = new EmptySet();

    private ReferenceSortedSets() {
    }

    public static <K> ReferenceSet<K> emptySet() {
        return EMPTY_SET;
    }

    public static <K> ReferenceSortedSet<K> singleton(K element) {
        return new Singleton((Object)element);
    }

    public static <K> ReferenceSortedSet<K> singleton(K element, Comparator<? super K> comparator) {
        return new Singleton<K>((K)element, comparator);
    }

    public static <K> ReferenceSortedSet<K> synchronize(ReferenceSortedSet<K> s) {
        return new SynchronizedSortedSet<K>(s);
    }

    public static <K> ReferenceSortedSet<K> synchronize(ReferenceSortedSet<K> s, Object sync) {
        return new SynchronizedSortedSet<K>(s, sync);
    }

    public static <K> ReferenceSortedSet<K> unmodifiable(ReferenceSortedSet<K> s) {
        return new UnmodifiableSortedSet<K>(s);
    }

    public static class UnmodifiableSortedSet<K>
    extends ReferenceSets.UnmodifiableSet<K>
    implements ReferenceSortedSet<K>,
    Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final ReferenceSortedSet<K> sortedSet;

        protected UnmodifiableSortedSet(ReferenceSortedSet<K> s) {
            super(s);
            this.sortedSet = s;
        }

        @Override
        public Comparator<? super K> comparator() {
            return this.sortedSet.comparator();
        }

        @Override
        public ReferenceSortedSet<K> subSet(K from, K to) {
            return new UnmodifiableSortedSet<K>((ReferenceSortedSet<K>)this.sortedSet.subSet((Object)from, (Object)to));
        }

        @Override
        public ReferenceSortedSet<K> headSet(K to) {
            return new UnmodifiableSortedSet<K>((ReferenceSortedSet<K>)this.sortedSet.headSet((Object)to));
        }

        @Override
        public ReferenceSortedSet<K> tailSet(K from) {
            return new UnmodifiableSortedSet<K>((ReferenceSortedSet<K>)this.sortedSet.tailSet((Object)from));
        }

        @Override
        public ObjectBidirectionalIterator<K> iterator() {
            return ObjectIterators.unmodifiable(this.sortedSet.iterator());
        }

        @Override
        public ObjectBidirectionalIterator<K> iterator(K from) {
            return ObjectIterators.unmodifiable(this.sortedSet.iterator(from));
        }

        @Override
        public K first() {
            return this.sortedSet.first();
        }

        @Override
        public K last() {
            return this.sortedSet.last();
        }
    }

    public static class SynchronizedSortedSet<K>
    extends ReferenceSets.SynchronizedSet<K>
    implements ReferenceSortedSet<K>,
    Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final ReferenceSortedSet<K> sortedSet;

        protected SynchronizedSortedSet(ReferenceSortedSet<K> s, Object sync) {
            super(s, sync);
            this.sortedSet = s;
        }

        protected SynchronizedSortedSet(ReferenceSortedSet<K> s) {
            super(s);
            this.sortedSet = s;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public Comparator<? super K> comparator() {
            Object object = this.sync;
            synchronized (object) {
                return this.sortedSet.comparator();
            }
        }

        @Override
        public ReferenceSortedSet<K> subSet(K from, K to) {
            return new SynchronizedSortedSet<K>((ReferenceSortedSet<K>)this.sortedSet.subSet((Object)from, (Object)to), this.sync);
        }

        @Override
        public ReferenceSortedSet<K> headSet(K to) {
            return new SynchronizedSortedSet<K>((ReferenceSortedSet<K>)this.sortedSet.headSet((Object)to), this.sync);
        }

        @Override
        public ReferenceSortedSet<K> tailSet(K from) {
            return new SynchronizedSortedSet<K>((ReferenceSortedSet<K>)this.sortedSet.tailSet((Object)from), this.sync);
        }

        @Override
        public ObjectBidirectionalIterator<K> iterator() {
            return this.sortedSet.iterator();
        }

        @Override
        public ObjectBidirectionalIterator<K> iterator(K from) {
            return this.sortedSet.iterator(from);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public K first() {
            Object object = this.sync;
            synchronized (object) {
                return this.sortedSet.first();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public K last() {
            Object object = this.sync;
            synchronized (object) {
                return this.sortedSet.last();
            }
        }
    }

    public static class Singleton<K>
    extends ReferenceSets.Singleton<K>
    implements ReferenceSortedSet<K>,
    Serializable,
    Cloneable {
        private static final long serialVersionUID = -7046029254386353129L;
        final Comparator<? super K> comparator;

        protected Singleton(K element, Comparator<? super K> comparator) {
            super(element);
            this.comparator = comparator;
        }

        private Singleton(K element) {
            this(element, (Comparator<K>)null);
        }

        final int compare(K k1, K k2) {
            return this.comparator == null ? ((Comparable)k1).compareTo(k2) : this.comparator.compare(k1, k2);
        }

        @Override
        public ObjectBidirectionalIterator<K> iterator(K from) {
            ObjectBidirectionalIterator i = this.iterator();
            if (this.compare(this.element, from) <= 0) {
                i.next();
            }
            return i;
        }

        @Override
        public Comparator<? super K> comparator() {
            return this.comparator;
        }

        @Override
        public ReferenceSortedSet<K> subSet(K from, K to) {
            if (this.compare(from, this.element) <= 0 && this.compare(this.element, to) < 0) {
                return this;
            }
            return ReferenceSortedSets.EMPTY_SET;
        }

        @Override
        public ReferenceSortedSet<K> headSet(K to) {
            if (this.compare(this.element, to) < 0) {
                return this;
            }
            return ReferenceSortedSets.EMPTY_SET;
        }

        @Override
        public ReferenceSortedSet<K> tailSet(K from) {
            if (this.compare(from, this.element) <= 0) {
                return this;
            }
            return ReferenceSortedSets.EMPTY_SET;
        }

        @Override
        public K first() {
            return (K)this.element;
        }

        @Override
        public K last() {
            return (K)this.element;
        }
    }

    public static class EmptySet<K>
    extends ReferenceSets.EmptySet<K>
    implements ReferenceSortedSet<K>,
    Serializable,
    Cloneable {
        private static final long serialVersionUID = -7046029254386353129L;

        protected EmptySet() {
        }

        @Override
        public ObjectBidirectionalIterator<K> iterator(K from) {
            return ObjectIterators.EMPTY_ITERATOR;
        }

        @Override
        public ReferenceSortedSet<K> subSet(K from, K to) {
            return ReferenceSortedSets.EMPTY_SET;
        }

        @Override
        public ReferenceSortedSet<K> headSet(K from) {
            return ReferenceSortedSets.EMPTY_SET;
        }

        @Override
        public ReferenceSortedSet<K> tailSet(K to) {
            return ReferenceSortedSets.EMPTY_SET;
        }

        @Override
        public K first() {
            throw new NoSuchElementException();
        }

        @Override
        public K last() {
            throw new NoSuchElementException();
        }

        @Override
        public Comparator<? super K> comparator() {
            return null;
        }

        @Override
        public Object clone() {
            return ReferenceSortedSets.EMPTY_SET;
        }

        private Object readResolve() {
            return ReferenceSortedSets.EMPTY_SET;
        }
    }

}

