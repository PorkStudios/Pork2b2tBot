/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.longs.AbstractLongSet;
import it.unimi.dsi.fastutil.longs.LongCollection;
import it.unimi.dsi.fastutil.longs.LongCollections;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongIterators;
import it.unimi.dsi.fastutil.longs.LongListIterator;
import it.unimi.dsi.fastutil.longs.LongSet;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

public final class LongSets {
    public static final EmptySet EMPTY_SET = new EmptySet();

    private LongSets() {
    }

    public static LongSet singleton(long element) {
        return new Singleton(element);
    }

    public static LongSet singleton(Long element) {
        return new Singleton(element);
    }

    public static LongSet synchronize(LongSet s) {
        return new SynchronizedSet(s);
    }

    public static LongSet synchronize(LongSet s, Object sync) {
        return new SynchronizedSet(s, sync);
    }

    public static LongSet unmodifiable(LongSet s) {
        return new UnmodifiableSet(s);
    }

    public static class UnmodifiableSet
    extends LongCollections.UnmodifiableCollection
    implements LongSet,
    Serializable {
        private static final long serialVersionUID = -7046029254386353129L;

        protected UnmodifiableSet(LongSet s) {
            super(s);
        }

        @Override
        public boolean remove(long k) {
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
        public boolean rem(long k) {
            return super.rem(k);
        }
    }

    public static class SynchronizedSet
    extends LongCollections.SynchronizedCollection
    implements LongSet,
    Serializable {
        private static final long serialVersionUID = -7046029254386353129L;

        protected SynchronizedSet(LongSet s, Object sync) {
            super(s, sync);
        }

        protected SynchronizedSet(LongSet s) {
            super(s);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean remove(long k) {
            Object object = this.sync;
            synchronized (object) {
                return this.collection.rem(k);
            }
        }

        @Deprecated
        @Override
        public boolean rem(long k) {
            return super.rem(k);
        }
    }

    public static class Singleton
    extends AbstractLongSet
    implements Serializable,
    Cloneable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final long element;

        protected Singleton(long element) {
            this.element = element;
        }

        @Override
        public boolean contains(long k) {
            return k == this.element;
        }

        @Override
        public boolean remove(long k) {
            throw new UnsupportedOperationException();
        }

        @Override
        public LongListIterator iterator() {
            return LongIterators.singleton(this.element);
        }

        @Override
        public int size() {
            return 1;
        }

        @Override
        public boolean addAll(Collection<? extends Long> c) {
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
        public boolean addAll(LongCollection c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean removeAll(LongCollection c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean retainAll(LongCollection c) {
            throw new UnsupportedOperationException();
        }

        public Object clone() {
            return this;
        }
    }

    public static class EmptySet
    extends LongCollections.EmptyCollection
    implements LongSet,
    Serializable,
    Cloneable {
        private static final long serialVersionUID = -7046029254386353129L;

        protected EmptySet() {
        }

        @Override
        public boolean remove(long ok) {
            throw new UnsupportedOperationException();
        }

        public Object clone() {
            return LongSets.EMPTY_SET;
        }

        @Override
        public boolean equals(Object o) {
            return o instanceof Set && ((Set)o).isEmpty();
        }

        @Deprecated
        @Override
        public boolean rem(long k) {
            return super.rem(k);
        }

        private Object readResolve() {
            return LongSets.EMPTY_SET;
        }
    }

}

