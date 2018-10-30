/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.floats.AbstractFloatList;
import it.unimi.dsi.fastutil.floats.FloatBidirectionalIterator;
import it.unimi.dsi.fastutil.floats.FloatCollection;
import it.unimi.dsi.fastutil.floats.FloatCollections;
import it.unimi.dsi.fastutil.floats.FloatIterator;
import it.unimi.dsi.fastutil.floats.FloatIterators;
import it.unimi.dsi.fastutil.floats.FloatList;
import it.unimi.dsi.fastutil.floats.FloatListIterator;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Random;
import java.util.RandomAccess;

public final class FloatLists {
    public static final EmptyList EMPTY_LIST = new EmptyList();

    private FloatLists() {
    }

    public static FloatList shuffle(FloatList l, Random random) {
        int i = l.size();
        while (i-- != 0) {
            int p = random.nextInt(i + 1);
            float t = l.getFloat(i);
            l.set(i, l.getFloat(p));
            l.set(p, t);
        }
        return l;
    }

    public static FloatList singleton(float element) {
        return new Singleton(element);
    }

    public static FloatList singleton(Object element) {
        return new Singleton(((Float)element).floatValue());
    }

    public static FloatList synchronize(FloatList l) {
        return l instanceof RandomAccess ? new SynchronizedRandomAccessList(l) : new SynchronizedList(l);
    }

    public static FloatList synchronize(FloatList l, Object sync) {
        return l instanceof RandomAccess ? new SynchronizedRandomAccessList(l, sync) : new SynchronizedList(l, sync);
    }

    public static FloatList unmodifiable(FloatList l) {
        return l instanceof RandomAccess ? new UnmodifiableRandomAccessList(l) : new UnmodifiableList(l);
    }

    public static class UnmodifiableRandomAccessList
    extends UnmodifiableList
    implements RandomAccess,
    Serializable {
        private static final long serialVersionUID = 0L;

        protected UnmodifiableRandomAccessList(FloatList l) {
            super(l);
        }

        @Override
        public FloatList subList(int from, int to) {
            return new UnmodifiableRandomAccessList(this.list.subList(from, to));
        }
    }

    public static class UnmodifiableList
    extends FloatCollections.UnmodifiableCollection
    implements FloatList,
    Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final FloatList list;

        protected UnmodifiableList(FloatList l) {
            super(l);
            this.list = l;
        }

        @Override
        public float getFloat(int i) {
            return this.list.getFloat(i);
        }

        @Override
        public float set(int i, float k) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void add(int i, float k) {
            throw new UnsupportedOperationException();
        }

        @Override
        public float removeFloat(int i) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int indexOf(float k) {
            return this.list.indexOf(k);
        }

        @Override
        public int lastIndexOf(float k) {
            return this.list.lastIndexOf(k);
        }

        @Override
        public boolean addAll(int index, Collection<? extends Float> c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void getElements(int from, float[] a, int offset, int length) {
            this.list.getElements(from, a, offset, length);
        }

        @Override
        public void removeElements(int from, int to) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void addElements(int index, float[] a, int offset, int length) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void addElements(int index, float[] a) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void size(int size) {
            this.list.size(size);
        }

        @Override
        public FloatListIterator listIterator() {
            return FloatIterators.unmodifiable(this.list.listIterator());
        }

        @Override
        public FloatListIterator iterator() {
            return this.listIterator();
        }

        @Override
        public FloatListIterator listIterator(int i) {
            return FloatIterators.unmodifiable(this.list.listIterator(i));
        }

        @Override
        public FloatList subList(int from, int to) {
            return new UnmodifiableList(this.list.subList(from, to));
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

        @Override
        public int compareTo(List<? extends Float> o) {
            return this.list.compareTo(o);
        }

        @Override
        public boolean addAll(int index, FloatCollection c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(FloatList l) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(int index, FloatList l) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public Float get(int i) {
            return this.list.get(i);
        }

        @Deprecated
        @Override
        public void add(int i, Float k) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public Float set(int index, Float k) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public Float remove(int i) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public int indexOf(Object o) {
            return this.list.indexOf(o);
        }

        @Deprecated
        @Override
        public int lastIndexOf(Object o) {
            return this.list.lastIndexOf(o);
        }
    }

    public static class SynchronizedRandomAccessList
    extends SynchronizedList
    implements RandomAccess,
    Serializable {
        private static final long serialVersionUID = 0L;

        protected SynchronizedRandomAccessList(FloatList l, Object sync) {
            super(l, sync);
        }

        protected SynchronizedRandomAccessList(FloatList l) {
            super(l);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public FloatList subList(int from, int to) {
            Object object = this.sync;
            synchronized (object) {
                return new SynchronizedRandomAccessList(this.list.subList(from, to), this.sync);
            }
        }
    }

    public static class SynchronizedList
    extends FloatCollections.SynchronizedCollection
    implements FloatList,
    Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final FloatList list;

        protected SynchronizedList(FloatList l, Object sync) {
            super(l, sync);
            this.list = l;
        }

        protected SynchronizedList(FloatList l) {
            super(l);
            this.list = l;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public float getFloat(int i) {
            Object object = this.sync;
            synchronized (object) {
                return this.list.getFloat(i);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public float set(int i, float k) {
            Object object = this.sync;
            synchronized (object) {
                return this.list.set(i, k);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void add(int i, float k) {
            Object object = this.sync;
            synchronized (object) {
                this.list.add(i, k);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public float removeFloat(int i) {
            Object object = this.sync;
            synchronized (object) {
                return this.list.removeFloat(i);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public int indexOf(float k) {
            Object object = this.sync;
            synchronized (object) {
                return this.list.indexOf(k);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public int lastIndexOf(float k) {
            Object object = this.sync;
            synchronized (object) {
                return this.list.lastIndexOf(k);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean addAll(int index, Collection<? extends Float> c) {
            Object object = this.sync;
            synchronized (object) {
                return this.list.addAll(index, c);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void getElements(int from, float[] a, int offset, int length) {
            Object object = this.sync;
            synchronized (object) {
                this.list.getElements(from, a, offset, length);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void removeElements(int from, int to) {
            Object object = this.sync;
            synchronized (object) {
                this.list.removeElements(from, to);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void addElements(int index, float[] a, int offset, int length) {
            Object object = this.sync;
            synchronized (object) {
                this.list.addElements(index, a, offset, length);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void addElements(int index, float[] a) {
            Object object = this.sync;
            synchronized (object) {
                this.list.addElements(index, a);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void size(int size) {
            Object object = this.sync;
            synchronized (object) {
                this.list.size(size);
            }
        }

        @Override
        public FloatListIterator listIterator() {
            return this.list.listIterator();
        }

        @Override
        public FloatListIterator iterator() {
            return this.listIterator();
        }

        @Override
        public FloatListIterator listIterator(int i) {
            return this.list.listIterator(i);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public FloatList subList(int from, int to) {
            Object object = this.sync;
            synchronized (object) {
                return new SynchronizedList(this.list.subList(from, to), this.sync);
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
        public int compareTo(List<? extends Float> o) {
            Object object = this.sync;
            synchronized (object) {
                return this.list.compareTo(o);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean addAll(int index, FloatCollection c) {
            Object object = this.sync;
            synchronized (object) {
                return this.list.addAll(index, c);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean addAll(int index, FloatList l) {
            Object object = this.sync;
            synchronized (object) {
                return this.list.addAll(index, l);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean addAll(FloatList l) {
            Object object = this.sync;
            synchronized (object) {
                return this.list.addAll(l);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Deprecated
        @Override
        public Float get(int i) {
            Object object = this.sync;
            synchronized (object) {
                return this.list.get(i);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Deprecated
        @Override
        public void add(int i, Float k) {
            Object object = this.sync;
            synchronized (object) {
                this.list.add(i, k);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Deprecated
        @Override
        public Float set(int index, Float k) {
            Object object = this.sync;
            synchronized (object) {
                return this.list.set(index, k);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Deprecated
        @Override
        public Float remove(int i) {
            Object object = this.sync;
            synchronized (object) {
                return this.list.remove(i);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Deprecated
        @Override
        public int indexOf(Object o) {
            Object object = this.sync;
            synchronized (object) {
                return this.list.indexOf(o);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Deprecated
        @Override
        public int lastIndexOf(Object o) {
            Object object = this.sync;
            synchronized (object) {
                return this.list.lastIndexOf(o);
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

    public static class Singleton
    extends AbstractFloatList
    implements RandomAccess,
    Serializable,
    Cloneable {
        private static final long serialVersionUID = -7046029254386353129L;
        private final float element;

        protected Singleton(float element) {
            this.element = element;
        }

        @Override
        public float getFloat(int i) {
            if (i == 0) {
                return this.element;
            }
            throw new IndexOutOfBoundsException();
        }

        @Override
        public boolean rem(float k) {
            throw new UnsupportedOperationException();
        }

        @Override
        public float removeFloat(int i) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean contains(float k) {
            return Float.floatToIntBits(k) == Float.floatToIntBits(this.element);
        }

        @Override
        public float[] toFloatArray() {
            float[] a = new float[]{this.element};
            return a;
        }

        @Override
        public FloatListIterator listIterator() {
            return FloatIterators.singleton(this.element);
        }

        @Override
        public FloatListIterator iterator() {
            return this.listIterator();
        }

        @Override
        public FloatListIterator listIterator(int i) {
            if (i > 1 || i < 0) {
                throw new IndexOutOfBoundsException();
            }
            FloatListIterator l = this.listIterator();
            if (i == 1) {
                l.nextFloat();
            }
            return l;
        }

        @Override
        public FloatList subList(int from, int to) {
            this.ensureIndex(from);
            this.ensureIndex(to);
            if (from > to) {
                throw new IndexOutOfBoundsException("Start index (" + from + ") is greater than end index (" + to + ")");
            }
            if (from != 0 || to != 1) {
                return FloatLists.EMPTY_LIST;
            }
            return this;
        }

        @Override
        public boolean addAll(int i, Collection<? extends Float> c) {
            throw new UnsupportedOperationException();
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
        public boolean addAll(FloatList c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(int i, FloatList c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(int i, FloatCollection c) {
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

        @Override
        public int size() {
            return 1;
        }

        @Override
        public void size(int size) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void clear() {
            throw new UnsupportedOperationException();
        }

        public Object clone() {
            return this;
        }
    }

    public static class EmptyList
    extends FloatCollections.EmptyCollection
    implements FloatList,
    RandomAccess,
    Serializable,
    Cloneable {
        private static final long serialVersionUID = -7046029254386353129L;

        protected EmptyList() {
        }

        @Override
        public float getFloat(int i) {
            throw new IndexOutOfBoundsException();
        }

        @Override
        public boolean rem(float k) {
            throw new UnsupportedOperationException();
        }

        @Override
        public float removeFloat(int i) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void add(int index, float k) {
            throw new UnsupportedOperationException();
        }

        @Override
        public float set(int index, float k) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int indexOf(float k) {
            return -1;
        }

        @Override
        public int lastIndexOf(float k) {
            return -1;
        }

        @Override
        public boolean addAll(int i, Collection<? extends Float> c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(FloatList c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(int i, FloatCollection c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(int i, FloatList c) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public void add(int index, Float k) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public Float get(int index) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public boolean add(Float k) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public Float set(int index, Float k) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public Float remove(int k) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public int indexOf(Object k) {
            return -1;
        }

        @Deprecated
        @Override
        public int lastIndexOf(Object k) {
            return -1;
        }

        @Override
        public FloatListIterator listIterator() {
            return FloatIterators.EMPTY_ITERATOR;
        }

        @Override
        public FloatListIterator iterator() {
            return FloatIterators.EMPTY_ITERATOR;
        }

        @Override
        public FloatListIterator listIterator(int i) {
            if (i == 0) {
                return FloatIterators.EMPTY_ITERATOR;
            }
            throw new IndexOutOfBoundsException(String.valueOf(i));
        }

        @Override
        public FloatList subList(int from, int to) {
            if (from == 0 && to == 0) {
                return this;
            }
            throw new IndexOutOfBoundsException();
        }

        @Override
        public void getElements(int from, float[] a, int offset, int length) {
            if (from == 0 && length == 0 && offset >= 0 && offset <= a.length) {
                return;
            }
            throw new IndexOutOfBoundsException();
        }

        @Override
        public void removeElements(int from, int to) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void addElements(int index, float[] a, int offset, int length) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void addElements(int index, float[] a) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void size(int s) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int compareTo(List<? extends Float> o) {
            if (o == this) {
                return 0;
            }
            return o.isEmpty() ? 0 : -1;
        }

        public Object clone() {
            return FloatLists.EMPTY_LIST;
        }

        @Override
        public int hashCode() {
            return 1;
        }

        @Override
        public boolean equals(Object o) {
            return o instanceof List && ((List)o).isEmpty();
        }

        @Override
        public String toString() {
            return "[]";
        }

        private Object readResolve() {
            return FloatLists.EMPTY_LIST;
        }
    }

}

