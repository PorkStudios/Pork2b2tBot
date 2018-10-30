/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.bytes.AbstractByteSet;
import it.unimi.dsi.fastutil.bytes.ByteCollection;
import it.unimi.dsi.fastutil.bytes.ByteCollections;
import it.unimi.dsi.fastutil.bytes.ByteIterator;
import it.unimi.dsi.fastutil.bytes.ByteIterators;
import it.unimi.dsi.fastutil.bytes.ByteListIterator;
import it.unimi.dsi.fastutil.bytes.ByteSet;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

public final class ByteSets {
    public static final EmptySet EMPTY_SET = new EmptySet();

    private ByteSets() {
    }

    public static ByteSet singleton(byte element) {
        return new Singleton(element);
    }

    public static ByteSet singleton(Byte element) {
        return new Singleton(element);
    }

    public static ByteSet synchronize(ByteSet s) {
        return new SynchronizedSet(s);
    }

    public static ByteSet synchronize(ByteSet s, Object sync) {
        return new SynchronizedSet(s, sync);
    }

    public static ByteSet unmodifiable(ByteSet s) {
        return new UnmodifiableSet(s);
    }

    public static class UnmodifiableSet
    extends ByteCollections.UnmodifiableCollection
    implements ByteSet,
    Serializable {
        private static final long serialVersionUID = -7046029254386353129L;

        protected UnmodifiableSet(ByteSet s) {
            super(s);
        }

        @Override
        public boolean remove(byte k) {
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
        public boolean rem(byte k) {
            return super.rem(k);
        }
    }

    public static class SynchronizedSet
    extends ByteCollections.SynchronizedCollection
    implements ByteSet,
    Serializable {
        private static final long serialVersionUID = -7046029254386353129L;

        protected SynchronizedSet(ByteSet s, Object sync) {
            super(s, sync);
        }

        protected SynchronizedSet(ByteSet s) {
            super(s);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean remove(byte k) {
            Object object = this.sync;
            synchronized (object) {
                return this.collection.rem(k);
            }
        }

        @Deprecated
        @Override
        public boolean rem(byte k) {
            return super.rem(k);
        }
    }

    public static class Singleton
    extends AbstractByteSet
    implements Serializable,
    Cloneable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final byte element;

        protected Singleton(byte element) {
            this.element = element;
        }

        @Override
        public boolean contains(byte k) {
            return k == this.element;
        }

        @Override
        public boolean remove(byte k) {
            throw new UnsupportedOperationException();
        }

        @Override
        public ByteListIterator iterator() {
            return ByteIterators.singleton(this.element);
        }

        @Override
        public int size() {
            return 1;
        }

        @Override
        public boolean addAll(Collection<? extends Byte> c) {
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
        public boolean addAll(ByteCollection c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean removeAll(ByteCollection c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean retainAll(ByteCollection c) {
            throw new UnsupportedOperationException();
        }

        public Object clone() {
            return this;
        }
    }

    public static class EmptySet
    extends ByteCollections.EmptyCollection
    implements ByteSet,
    Serializable,
    Cloneable {
        private static final long serialVersionUID = -7046029254386353129L;

        protected EmptySet() {
        }

        @Override
        public boolean remove(byte ok) {
            throw new UnsupportedOperationException();
        }

        public Object clone() {
            return ByteSets.EMPTY_SET;
        }

        @Override
        public boolean equals(Object o) {
            return o instanceof Set && ((Set)o).isEmpty();
        }

        @Deprecated
        @Override
        public boolean rem(byte k) {
            return super.rem(k);
        }

        private Object readResolve() {
            return ByteSets.EMPTY_SET;
        }
    }

}

