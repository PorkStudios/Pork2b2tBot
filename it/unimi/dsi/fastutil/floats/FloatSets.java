/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.floats.AbstractFloatSet;
import it.unimi.dsi.fastutil.floats.FloatCollection;
import it.unimi.dsi.fastutil.floats.FloatCollections;
import it.unimi.dsi.fastutil.floats.FloatIterator;
import it.unimi.dsi.fastutil.floats.FloatIterators;
import it.unimi.dsi.fastutil.floats.FloatListIterator;
import it.unimi.dsi.fastutil.floats.FloatSet;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

public final class FloatSets {
    public static final EmptySet EMPTY_SET = new EmptySet();

    private FloatSets() {
    }

    public static FloatSet singleton(float element) {
        return new Singleton(element);
    }

    public static FloatSet singleton(Float element) {
        return new Singleton(element.floatValue());
    }

    public static FloatSet synchronize(FloatSet s) {
        return new SynchronizedSet(s);
    }

    public static FloatSet synchronize(FloatSet s, Object sync) {
        return new SynchronizedSet(s, sync);
    }

    public static FloatSet unmodifiable(FloatSet s) {
        return new UnmodifiableSet(s);
    }

    public static class UnmodifiableSet
    extends FloatCollections.UnmodifiableCollection
    implements FloatSet,
    Serializable {
        private static final long serialVersionUID = -7046029254386353129L;

        protected UnmodifiableSet(FloatSet s) {
            super(s);
        }

        @Override
        public boolean remove(float k) {
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
        public boolean rem(float k) {
            return super.rem(k);
        }
    }

    public static class SynchronizedSet
    extends FloatCollections.SynchronizedCollection
    implements FloatSet,
    Serializable {
        private static final long serialVersionUID = -7046029254386353129L;

        protected SynchronizedSet(FloatSet s, Object sync) {
            super(s, sync);
        }

        protected SynchronizedSet(FloatSet s) {
            super(s);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean remove(float k) {
            Object object = this.sync;
            synchronized (object) {
                return this.collection.rem(k);
            }
        }

        @Deprecated
        @Override
        public boolean rem(float k) {
            return super.rem(k);
        }
    }

    public static class Singleton
    extends AbstractFloatSet
    implements Serializable,
    Cloneable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final float element;

        protected Singleton(float element) {
            this.element = element;
        }

        @Override
        public boolean contains(float k) {
            return Float.floatToIntBits(k) == Float.floatToIntBits(this.element);
        }

        @Override
        public boolean remove(float k) {
            throw new UnsupportedOperationException();
        }

        @Override
        public FloatListIterator iterator() {
            return FloatIterators.singleton(this.element);
        }

        @Override
        public int size() {
            return 1;
        }

        @Override
        public boolean addAll(Collection<? extends Float> c) {
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
        public boolean addAll(FloatCollection c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean removeAll(FloatCollection c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean retainAll(FloatCollection c) {
            throw new UnsupportedOperationException();
        }

        public Object clone() {
            return this;
        }
    }

    public static class EmptySet
    extends FloatCollections.EmptyCollection
    implements FloatSet,
    Serializable,
    Cloneable {
        private static final long serialVersionUID = -7046029254386353129L;

        protected EmptySet() {
        }

        @Override
        public boolean remove(float ok) {
            throw new UnsupportedOperationException();
        }

        public Object clone() {
            return FloatSets.EMPTY_SET;
        }

        @Override
        public boolean equals(Object o) {
            return o instanceof Set && ((Set)o).isEmpty();
        }

        @Deprecated
        @Override
        public boolean rem(float k) {
            return super.rem(k);
        }

        private Object readResolve() {
            return FloatSets.EMPTY_SET;
        }
    }

}

