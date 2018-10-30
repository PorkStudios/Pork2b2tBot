/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.shorts;

import it.unimi.dsi.fastutil.shorts.AbstractShortSet;
import it.unimi.dsi.fastutil.shorts.ShortCollection;
import it.unimi.dsi.fastutil.shorts.ShortCollections;
import it.unimi.dsi.fastutil.shorts.ShortIterator;
import it.unimi.dsi.fastutil.shorts.ShortIterators;
import it.unimi.dsi.fastutil.shorts.ShortListIterator;
import it.unimi.dsi.fastutil.shorts.ShortSet;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

public final class ShortSets {
    public static final EmptySet EMPTY_SET = new EmptySet();

    private ShortSets() {
    }

    public static ShortSet singleton(short element) {
        return new Singleton(element);
    }

    public static ShortSet singleton(Short element) {
        return new Singleton(element);
    }

    public static ShortSet synchronize(ShortSet s) {
        return new SynchronizedSet(s);
    }

    public static ShortSet synchronize(ShortSet s, Object sync) {
        return new SynchronizedSet(s, sync);
    }

    public static ShortSet unmodifiable(ShortSet s) {
        return new UnmodifiableSet(s);
    }

    public static class UnmodifiableSet
    extends ShortCollections.UnmodifiableCollection
    implements ShortSet,
    Serializable {
        private static final long serialVersionUID = -7046029254386353129L;

        protected UnmodifiableSet(ShortSet s) {
            super(s);
        }

        @Override
        public boolean remove(short k) {
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
        public boolean rem(short k) {
            return super.rem(k);
        }
    }

    public static class SynchronizedSet
    extends ShortCollections.SynchronizedCollection
    implements ShortSet,
    Serializable {
        private static final long serialVersionUID = -7046029254386353129L;

        protected SynchronizedSet(ShortSet s, Object sync) {
            super(s, sync);
        }

        protected SynchronizedSet(ShortSet s) {
            super(s);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean remove(short k) {
            Object object = this.sync;
            synchronized (object) {
                return this.collection.rem(k);
            }
        }

        @Deprecated
        @Override
        public boolean rem(short k) {
            return super.rem(k);
        }
    }

    public static class Singleton
    extends AbstractShortSet
    implements Serializable,
    Cloneable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final short element;

        protected Singleton(short element) {
            this.element = element;
        }

        @Override
        public boolean contains(short k) {
            return k == this.element;
        }

        @Override
        public boolean remove(short k) {
            throw new UnsupportedOperationException();
        }

        @Override
        public ShortListIterator iterator() {
            return ShortIterators.singleton(this.element);
        }

        @Override
        public int size() {
            return 1;
        }

        @Override
        public boolean addAll(Collection<? extends Short> c) {
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
        public boolean addAll(ShortCollection c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean removeAll(ShortCollection c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean retainAll(ShortCollection c) {
            throw new UnsupportedOperationException();
        }

        public Object clone() {
            return this;
        }
    }

    public static class EmptySet
    extends ShortCollections.EmptyCollection
    implements ShortSet,
    Serializable,
    Cloneable {
        private static final long serialVersionUID = -7046029254386353129L;

        protected EmptySet() {
        }

        @Override
        public boolean remove(short ok) {
            throw new UnsupportedOperationException();
        }

        public Object clone() {
            return ShortSets.EMPTY_SET;
        }

        @Override
        public boolean equals(Object o) {
            return o instanceof Set && ((Set)o).isEmpty();
        }

        @Deprecated
        @Override
        public boolean rem(short k) {
            return super.rem(k);
        }

        private Object readResolve() {
            return ShortSets.EMPTY_SET;
        }
    }

}

