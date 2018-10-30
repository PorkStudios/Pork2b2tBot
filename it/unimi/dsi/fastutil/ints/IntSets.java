/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.ints;

import it.unimi.dsi.fastutil.ints.AbstractIntSet;
import it.unimi.dsi.fastutil.ints.IntCollection;
import it.unimi.dsi.fastutil.ints.IntCollections;
import it.unimi.dsi.fastutil.ints.IntIterator;
import it.unimi.dsi.fastutil.ints.IntIterators;
import it.unimi.dsi.fastutil.ints.IntListIterator;
import it.unimi.dsi.fastutil.ints.IntSet;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

public final class IntSets {
    public static final EmptySet EMPTY_SET = new EmptySet();

    private IntSets() {
    }

    public static IntSet singleton(int element) {
        return new Singleton(element);
    }

    public static IntSet singleton(Integer element) {
        return new Singleton(element);
    }

    public static IntSet synchronize(IntSet s) {
        return new SynchronizedSet(s);
    }

    public static IntSet synchronize(IntSet s, Object sync) {
        return new SynchronizedSet(s, sync);
    }

    public static IntSet unmodifiable(IntSet s) {
        return new UnmodifiableSet(s);
    }

    public static class UnmodifiableSet
    extends IntCollections.UnmodifiableCollection
    implements IntSet,
    Serializable {
        private static final long serialVersionUID = -7046029254386353129L;

        protected UnmodifiableSet(IntSet s) {
            super(s);
        }

        @Override
        public boolean remove(int k) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            return this.collection.equals(o);
        }

        @Override
        public int hashCode() {
            return this.collection.hashCode();
        }

        @Deprecated
        @Override
        public boolean rem(int k) {
            return super.rem(k);
        }
    }

    public static class SynchronizedSet
    extends IntCollections.SynchronizedCollection
    implements IntSet,
    Serializable {
        private static final long serialVersionUID = -7046029254386353129L;

        protected SynchronizedSet(IntSet s, Object sync) {
            super(s, sync);
        }

        protected SynchronizedSet(IntSet s) {
            super(s);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean remove(int k) {
            Object object = this.sync;
            synchronized (object) {
                return this.collection.rem(k);
            }
        }

        @Deprecated
        @Override
        public boolean rem(int k) {
            return super.rem(k);
        }
    }

    public static class Singleton
    extends AbstractIntSet
    implements Serializable,
    Cloneable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final int element;

        protected Singleton(int element) {
            this.element = element;
        }

        @Override
        public boolean contains(int k) {
            return k == this.element;
        }

        @Override
        public boolean remove(int k) {
            throw new UnsupportedOperationException();
        }

        @Override
        public IntListIterator iterator() {
            return IntIterators.singleton(this.element);
        }

        @Override
        public int size() {
            return 1;
        }

        @Override
        public boolean addAll(Collection<? extends Integer> c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean removeAll(Collection<?> c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean retainAll(Collection<?> c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(IntCollection c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean removeAll(IntCollection c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean retainAll(IntCollection c) {
            throw new UnsupportedOperationException();
        }

        public Object clone() {
            return this;
        }
    }

    public static class EmptySet
    extends IntCollections.EmptyCollection
    implements IntSet,
    Serializable,
    Cloneable {
        private static final long serialVersionUID = -7046029254386353129L;

        protected EmptySet() {
        }

        @Override
        public boolean remove(int ok) {
            throw new UnsupportedOperationException();
        }

        public Object clone() {
            return IntSets.EMPTY_SET;
        }

        @Override
        public boolean equals(Object o) {
            return o instanceof Set && ((Set)o).isEmpty();
        }

        @Deprecated
        @Override
        public boolean rem(int k) {
            return super.rem(k);
        }

        private Object readResolve() {
            return IntSets.EMPTY_SET;
        }
    }

}

