/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.shorts;

import it.unimi.dsi.fastutil.objects.ObjectArrays;
import it.unimi.dsi.fastutil.shorts.AbstractShortCollection;
import it.unimi.dsi.fastutil.shorts.ShortBidirectionalIterator;
import it.unimi.dsi.fastutil.shorts.ShortCollection;
import it.unimi.dsi.fastutil.shorts.ShortIterable;
import it.unimi.dsi.fastutil.shorts.ShortIterator;
import it.unimi.dsi.fastutil.shorts.ShortIterators;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;

public final class ShortCollections {
    private ShortCollections() {
    }

    public static ShortCollection synchronize(ShortCollection c) {
        return new SynchronizedCollection(c);
    }

    public static ShortCollection synchronize(ShortCollection c, Object sync) {
        return new SynchronizedCollection(c, sync);
    }

    public static ShortCollection unmodifiable(ShortCollection c) {
        return new UnmodifiableCollection(c);
    }

    public static ShortCollection asCollection(ShortIterable iterable) {
        if (iterable instanceof ShortCollection) {
            return (ShortCollection)iterable;
        }
        return new IterableCollection(iterable);
    }

    public static class IterableCollection
    extends AbstractShortCollection
    implements Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final ShortIterable iterable;

        protected IterableCollection(ShortIterable iterable) {
            if (iterable == null) {
                throw new NullPointerException();
            }
            this.iterable = iterable;
        }

        @Override
        public int size() {
            int c = 0;
            ShortIterator iterator = this.iterator();
            while (iterator.hasNext()) {
                iterator.nextShort();
                ++c;
            }
            return c;
        }

        @Override
        public boolean isEmpty() {
            return !this.iterable.iterator().hasNext();
        }

        @Override
        public ShortIterator iterator() {
            return this.iterable.iterator();
        }
    }

    public static class UnmodifiableCollection
    implements ShortCollection,
    Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final ShortCollection collection;

        protected UnmodifiableCollection(ShortCollection c) {
            if (c == null) {
                throw new NullPointerException();
            }
            this.collection = c;
        }

        @Override
        public boolean add(short k) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean rem(short k) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int size() {
            return this.collection.size();
        }

        @Override
        public boolean isEmpty() {
            return this.collection.isEmpty();
        }

        @Override
        public boolean contains(short o) {
            return this.collection.contains(o);
        }

        @Override
        public ShortIterator iterator() {
            return ShortIterators.unmodifiable(this.collection.iterator());
        }

        @Override
        public void clear() {
            throw new UnsupportedOperationException();
        }

        @Override
        public <T> T[] toArray(T[] a) {
            return this.collection.toArray(a);
        }

        @Override
        public Object[] toArray() {
            return this.collection.toArray();
        }

        @Override
        public boolean containsAll(Collection<?> c) {
            return this.collection.containsAll(c);
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

        @Deprecated
        @Override
        public boolean add(Short k) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public boolean contains(Object k) {
            return this.collection.contains(k);
        }

        @Deprecated
        @Override
        public boolean remove(Object k) {
            throw new UnsupportedOperationException();
        }

        @Override
        public short[] toShortArray() {
            return this.collection.toShortArray();
        }

        @Deprecated
        @Override
        public short[] toShortArray(short[] a) {
            return this.toArray(a);
        }

        @Override
        public short[] toArray(short[] a) {
            return this.collection.toArray(a);
        }

        @Override
        public boolean containsAll(ShortCollection c) {
            return this.collection.containsAll(c);
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

        public String toString() {
            return this.collection.toString();
        }

        @Override
        public int hashCode() {
            return this.collection.hashCode();
        }

        @Override
        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            return this.collection.equals(o);
        }
    }

    public static class SynchronizedCollection
    implements ShortCollection,
    Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final ShortCollection collection;
        protected final Object sync;

        protected SynchronizedCollection(ShortCollection c, Object sync) {
            if (c == null) {
                throw new NullPointerException();
            }
            this.collection = c;
            this.sync = sync;
        }

        protected SynchronizedCollection(ShortCollection c) {
            if (c == null) {
                throw new NullPointerException();
            }
            this.collection = c;
            this.sync = this;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean add(short k) {
            Object object = this.sync;
            synchronized (object) {
                return this.collection.add(k);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean contains(short k) {
            Object object = this.sync;
            synchronized (object) {
                return this.collection.contains(k);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean rem(short k) {
            Object object = this.sync;
            synchronized (object) {
                return this.collection.rem(k);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public int size() {
            Object object = this.sync;
            synchronized (object) {
                return this.collection.size();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean isEmpty() {
            Object object = this.sync;
            synchronized (object) {
                return this.collection.isEmpty();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public short[] toShortArray() {
            Object object = this.sync;
            synchronized (object) {
                return this.collection.toShortArray();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public Object[] toArray() {
            Object object = this.sync;
            synchronized (object) {
                return this.collection.toArray();
            }
        }

        @Deprecated
        @Override
        public short[] toShortArray(short[] a) {
            return this.toArray(a);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public short[] toArray(short[] a) {
            Object object = this.sync;
            synchronized (object) {
                return this.collection.toArray(a);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean addAll(ShortCollection c) {
            Object object = this.sync;
            synchronized (object) {
                return this.collection.addAll(c);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean containsAll(ShortCollection c) {
            Object object = this.sync;
            synchronized (object) {
                return this.collection.containsAll(c);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean removeAll(ShortCollection c) {
            Object object = this.sync;
            synchronized (object) {
                return this.collection.removeAll(c);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean retainAll(ShortCollection c) {
            Object object = this.sync;
            synchronized (object) {
                return this.collection.retainAll(c);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Deprecated
        @Override
        public boolean add(Short k) {
            Object object = this.sync;
            synchronized (object) {
                return this.collection.add(k);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Deprecated
        @Override
        public boolean contains(Object k) {
            Object object = this.sync;
            synchronized (object) {
                return this.collection.contains(k);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Deprecated
        @Override
        public boolean remove(Object k) {
            Object object = this.sync;
            synchronized (object) {
                return this.collection.remove(k);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public <T> T[] toArray(T[] a) {
            Object object = this.sync;
            synchronized (object) {
                return this.collection.toArray(a);
            }
        }

        @Override
        public ShortIterator iterator() {
            return this.collection.iterator();
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean addAll(Collection<? extends Short> c) {
            Object object = this.sync;
            synchronized (object) {
                return this.collection.addAll(c);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean containsAll(Collection<?> c) {
            Object object = this.sync;
            synchronized (object) {
                return this.collection.containsAll(c);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean removeAll(Collection<?> c) {
            Object object = this.sync;
            synchronized (object) {
                return this.collection.removeAll(c);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean retainAll(Collection<?> c) {
            Object object = this.sync;
            synchronized (object) {
                return this.collection.retainAll(c);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void clear() {
            Object object = this.sync;
            synchronized (object) {
                this.collection.clear();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public String toString() {
            Object object = this.sync;
            synchronized (object) {
                return this.collection.toString();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public int hashCode() {
            Object object = this.sync;
            synchronized (object) {
                return this.collection.hashCode();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            Object object = this.sync;
            synchronized (object) {
                return this.collection.equals(o);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        private void writeObject(ObjectOutputStream s) throws IOException {
            Object object = this.sync;
            synchronized (object) {
                s.defaultWriteObject();
            }
        }
    }

    public static abstract class EmptyCollection
    extends AbstractShortCollection {
        protected EmptyCollection() {
        }

        @Override
        public boolean contains(short k) {
            return false;
        }

        @Override
        public Object[] toArray() {
            return ObjectArrays.EMPTY_ARRAY;
        }

        @Override
        public ShortBidirectionalIterator iterator() {
            return ShortIterators.EMPTY_ITERATOR;
        }

        @Override
        public int size() {
            return 0;
        }

        @Override
        public void clear() {
        }

        @Override
        public int hashCode() {
            return 0;
        }

        @Override
        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            if (!(o instanceof Collection)) {
                return false;
            }
            return ((Collection)o).isEmpty();
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
    }

}

