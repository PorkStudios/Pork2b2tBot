/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.BigList;
import it.unimi.dsi.fastutil.BigListIterator;
import it.unimi.dsi.fastutil.floats.AbstractFloatBigList;
import it.unimi.dsi.fastutil.floats.FloatBidirectionalIterator;
import it.unimi.dsi.fastutil.floats.FloatBigArrays;
import it.unimi.dsi.fastutil.floats.FloatBigList;
import it.unimi.dsi.fastutil.floats.FloatBigListIterator;
import it.unimi.dsi.fastutil.floats.FloatBigListIterators;
import it.unimi.dsi.fastutil.floats.FloatCollection;
import it.unimi.dsi.fastutil.floats.FloatCollections;
import it.unimi.dsi.fastutil.floats.FloatIterator;
import it.unimi.dsi.fastutil.floats.FloatList;
import it.unimi.dsi.fastutil.floats.FloatListIterator;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.Random;

public final class FloatBigLists {
    public static final EmptyBigList EMPTY_BIG_LIST = new EmptyBigList();

    private FloatBigLists() {
    }

    public static FloatBigList shuffle(FloatBigList l, Random random) {
        long i = l.size64();
        while (i-- != 0L) {
            long p = (random.nextLong() & Long.MAX_VALUE) % (i + 1L);
            float t = l.getFloat(i);
            l.set(i, l.getFloat(p));
            l.set(p, t);
        }
        return l;
    }

    public static FloatBigList singleton(float element) {
        return new Singleton(element);
    }

    public static FloatBigList singleton(Object element) {
        return new Singleton(((Float)element).floatValue());
    }

    public static FloatBigList synchronize(FloatBigList l) {
        return new SynchronizedBigList(l);
    }

    public static FloatBigList synchronize(FloatBigList l, Object sync) {
        return new SynchronizedBigList(l, sync);
    }

    public static FloatBigList unmodifiable(FloatBigList l) {
        return new UnmodifiableBigList(l);
    }

    public static FloatBigList asBigList(FloatList list) {
        return new ListBigList(list);
    }

    public static class ListBigList
    extends AbstractFloatBigList
    implements Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        private final FloatList list;

        protected ListBigList(FloatList list) {
            this.list = list;
        }

        private int intIndex(long index) {
            if (index >= Integer.MAX_VALUE) {
                throw new IndexOutOfBoundsException("This big list is restricted to 32-bit indices");
            }
            return (int)index;
        }

        @Override
        public long size64() {
            return this.list.size();
        }

        @Override
        public void size(long size) {
            this.list.size(this.intIndex(size));
        }

        @Override
        public FloatBigListIterator iterator() {
            return FloatBigListIterators.asBigListIterator(this.list.iterator());
        }

        @Override
        public FloatBigListIterator listIterator() {
            return FloatBigListIterators.asBigListIterator(this.list.listIterator());
        }

        @Override
        public FloatBigListIterator listIterator(long index) {
            return FloatBigListIterators.asBigListIterator(this.list.listIterator(this.intIndex(index)));
        }

        @Override
        public boolean addAll(long index, Collection<? extends Float> c) {
            return this.list.addAll(this.intIndex(index), c);
        }

        @Override
        public FloatBigList subList(long from, long to) {
            return new ListBigList(this.list.subList(this.intIndex(from), this.intIndex(to)));
        }

        @Override
        public boolean contains(float key) {
            return this.list.contains(key);
        }

        @Override
        public float[] toFloatArray() {
            return this.list.toFloatArray();
        }

        @Override
        public void removeElements(long from, long to) {
            this.list.removeElements(this.intIndex(from), this.intIndex(to));
        }

        @Deprecated
        @Override
        public float[] toFloatArray(float[] a) {
            return this.list.toArray(a);
        }

        @Override
        public boolean addAll(long index, FloatCollection c) {
            return this.list.addAll(this.intIndex(index), c);
        }

        @Override
        public boolean addAll(FloatCollection c) {
            return this.list.addAll(c);
        }

        @Override
        public boolean addAll(long index, FloatBigList c) {
            return this.list.addAll(this.intIndex(index), c);
        }

        @Override
        public boolean addAll(FloatBigList c) {
            return this.list.addAll(c);
        }

        @Override
        public boolean containsAll(FloatCollection c) {
            return this.list.containsAll(c);
        }

        @Override
        public boolean removeAll(FloatCollection c) {
            return this.list.removeAll(c);
        }

        @Override
        public boolean retainAll(FloatCollection c) {
            return this.list.retainAll(c);
        }

        @Override
        public void add(long index, float key) {
            this.list.add(this.intIndex(index), key);
        }

        @Override
        public boolean add(float key) {
            return this.list.add(key);
        }

        @Override
        public float getFloat(long index) {
            return this.list.getFloat(this.intIndex(index));
        }

        @Override
        public long indexOf(float k) {
            return this.list.indexOf(k);
        }

        @Override
        public long lastIndexOf(float k) {
            return this.list.lastIndexOf(k);
        }

        @Override
        public float removeFloat(long index) {
            return this.list.removeFloat(this.intIndex(index));
        }

        @Override
        public float set(long index, float k) {
            return this.list.set(this.intIndex(index), k);
        }

        @Override
        public boolean isEmpty() {
            return this.list.isEmpty();
        }

        @Override
        public <T> T[] toArray(T[] a) {
            return this.list.toArray(a);
        }

        @Override
        public boolean containsAll(Collection<?> c) {
            return this.list.containsAll(c);
        }

        @Override
        public boolean addAll(Collection<? extends Float> c) {
            return this.list.addAll(c);
        }

        @Override
        public boolean removeAll(Collection<?> c) {
            return this.list.removeAll(c);
        }

        @Override
        public boolean retainAll(Collection<?> c) {
            return this.list.retainAll(c);
        }

        @Override
        public void clear() {
            this.list.clear();
        }

        @Override
        public int hashCode() {
            return this.list.hashCode();
        }
    }

    public static class UnmodifiableBigList
    extends FloatCollections.UnmodifiableCollection
    implements FloatBigList,
    Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final FloatBigList list;

        protected UnmodifiableBigList(FloatBigList l) {
            super(l);
            this.list = l;
        }

        @Override
        public float getFloat(long i) {
            return this.list.getFloat(i);
        }

        @Override
        public float set(long i, float k) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void add(long i, float k) {
            throw new UnsupportedOperationException();
        }

        @Override
        public float removeFloat(long i) {
            throw new UnsupportedOperationException();
        }

        @Override
        public long indexOf(float k) {
            return this.list.indexOf(k);
        }

        @Override
        public long lastIndexOf(float k) {
            return this.list.lastIndexOf(k);
        }

        @Override
        public boolean addAll(long index, Collection<? extends Float> c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void getElements(long from, float[][] a, long offset, long length) {
            this.list.getElements(from, a, offset, length);
        }

        @Override
        public void removeElements(long from, long to) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void addElements(long index, float[][] a, long offset, long length) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void addElements(long index, float[][] a) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public void size(long size) {
            this.list.size(size);
        }

        @Override
        public long size64() {
            return this.list.size64();
        }

        @Override
        public FloatBigListIterator iterator() {
            return this.listIterator();
        }

        @Override
        public FloatBigListIterator listIterator() {
            return FloatBigListIterators.unmodifiable(this.list.listIterator());
        }

        @Override
        public FloatBigListIterator listIterator(long i) {
            return FloatBigListIterators.unmodifiable(this.list.listIterator(i));
        }

        @Override
        public FloatBigList subList(long from, long to) {
            return FloatBigLists.unmodifiable(this.list.subList(from, to));
        }

        @Override
        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            return this.list.equals(o);
        }

        @Override
        public int hashCode() {
            return this.list.hashCode();
        }

        @Override
        public int compareTo(BigList<? extends Float> o) {
            return this.list.compareTo(o);
        }

        @Override
        public boolean addAll(long index, FloatCollection c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(FloatBigList l) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(long index, FloatBigList l) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public Float get(long i) {
            return this.list.get(i);
        }

        @Deprecated
        @Override
        public void add(long i, Float k) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public Float set(long index, Float k) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public Float remove(long i) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public long indexOf(Object o) {
            return this.list.indexOf(o);
        }

        @Deprecated
        @Override
        public long lastIndexOf(Object o) {
            return this.list.lastIndexOf(o);
        }
    }

    public static class SynchronizedBigList
    extends FloatCollections.SynchronizedCollection
    implements FloatBigList,
    Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final FloatBigList list;

        protected SynchronizedBigList(FloatBigList l, Object sync) {
            super(l, sync);
            this.list = l;
        }

        protected SynchronizedBigList(FloatBigList l) {
            super(l);
            this.list = l;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public float getFloat(long i) {
            Object object = this.sync;
            synchronized (object) {
                return this.list.getFloat(i);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public float set(long i, float k) {
            Object object = this.sync;
            synchronized (object) {
                return this.list.set(i, k);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void add(long i, float k) {
            Object object = this.sync;
            synchronized (object) {
                this.list.add(i, k);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public float removeFloat(long i) {
            Object object = this.sync;
            synchronized (object) {
                return this.list.removeFloat(i);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public long indexOf(float k) {
            Object object = this.sync;
            synchronized (object) {
                return this.list.indexOf(k);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public long lastIndexOf(float k) {
            Object object = this.sync;
            synchronized (object) {
                return this.list.lastIndexOf(k);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean addAll(long index, Collection<? extends Float> c) {
            Object object = this.sync;
            synchronized (object) {
                return this.list.addAll(index, c);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void getElements(long from, float[][] a, long offset, long length) {
            Object object = this.sync;
            synchronized (object) {
                this.list.getElements(from, a, offset, length);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void removeElements(long from, long to) {
            Object object = this.sync;
            synchronized (object) {
                this.list.removeElements(from, to);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void addElements(long index, float[][] a, long offset, long length) {
            Object object = this.sync;
            synchronized (object) {
                this.list.addElements(index, a, offset, length);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void addElements(long index, float[][] a) {
            Object object = this.sync;
            synchronized (object) {
                this.list.addElements(index, a);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Deprecated
        @Override
        public void size(long size) {
            Object object = this.sync;
            synchronized (object) {
                this.list.size(size);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public long size64() {
            Object object = this.sync;
            synchronized (object) {
                return this.list.size64();
            }
        }

        @Override
        public FloatBigListIterator iterator() {
            return this.list.listIterator();
        }

        @Override
        public FloatBigListIterator listIterator() {
            return this.list.listIterator();
        }

        @Override
        public FloatBigListIterator listIterator(long i) {
            return this.list.listIterator(i);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public FloatBigList subList(long from, long to) {
            Object object = this.sync;
            synchronized (object) {
                return FloatBigLists.synchronize(this.list.subList(from, to), this.sync);
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
                return this.list.equals(o);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public int hashCode() {
            Object object = this.sync;
            synchronized (object) {
                return this.list.hashCode();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public int compareTo(BigList<? extends Float> o) {
            Object object = this.sync;
            synchronized (object) {
                return this.list.compareTo(o);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean addAll(long index, FloatCollection c) {
            Object object = this.sync;
            synchronized (object) {
                return this.list.addAll(index, c);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean addAll(long index, FloatBigList l) {
            Object object = this.sync;
            synchronized (object) {
                return this.list.addAll(index, l);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean addAll(FloatBigList l) {
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
        public void add(long i, Float k) {
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
        public Float get(long i) {
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
        public Float set(long index, Float k) {
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
        public Float remove(long i) {
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
        public long indexOf(Object o) {
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
        public long lastIndexOf(Object o) {
            Object object = this.sync;
            synchronized (object) {
                return this.list.lastIndexOf(o);
            }
        }
    }

    public static class Singleton
    extends AbstractFloatBigList
    implements Serializable,
    Cloneable {
        private static final long serialVersionUID = -7046029254386353129L;
        private final float element;

        protected Singleton(float element) {
            this.element = element;
        }

        @Override
        public float getFloat(long i) {
            if (i == 0L) {
                return this.element;
            }
            throw new IndexOutOfBoundsException();
        }

        @Override
        public boolean rem(float k) {
            throw new UnsupportedOperationException();
        }

        @Override
        public float removeFloat(long i) {
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
        public FloatBigListIterator listIterator() {
            return FloatBigListIterators.singleton(this.element);
        }

        @Override
        public FloatBigListIterator listIterator(long i) {
            if (i > 1L || i < 0L) {
                throw new IndexOutOfBoundsException();
            }
            FloatBigListIterator l = this.listIterator();
            if (i == 1L) {
                l.nextFloat();
            }
            return l;
        }

        @Override
        public FloatBigList subList(long from, long to) {
            this.ensureIndex(from);
            this.ensureIndex(to);
            if (from > to) {
                throw new IndexOutOfBoundsException("Start index (" + from + ") is greater than end index (" + to + ")");
            }
            if (from != 0L || to != 1L) {
                return FloatBigLists.EMPTY_BIG_LIST;
            }
            return this;
        }

        @Override
        public boolean addAll(long i, Collection<? extends Float> c) {
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
        public boolean addAll(FloatBigList c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(long i, FloatBigList c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(long i, FloatCollection c) {
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
        public void clear() {
            throw new UnsupportedOperationException();
        }

        @Override
        public long size64() {
            return 1L;
        }

        public Object clone() {
            return this;
        }
    }

    public static class EmptyBigList
    extends FloatCollections.EmptyCollection
    implements FloatBigList,
    Serializable,
    Cloneable {
        private static final long serialVersionUID = -7046029254386353129L;

        protected EmptyBigList() {
        }

        @Override
        public float getFloat(long i) {
            throw new IndexOutOfBoundsException();
        }

        @Override
        public boolean rem(float k) {
            throw new UnsupportedOperationException();
        }

        @Override
        public float removeFloat(long i) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void add(long index, float k) {
            throw new UnsupportedOperationException();
        }

        @Override
        public float set(long index, float k) {
            throw new UnsupportedOperationException();
        }

        @Override
        public long indexOf(float k) {
            return -1L;
        }

        @Override
        public long lastIndexOf(float k) {
            return -1L;
        }

        @Override
        public boolean addAll(long i, Collection<? extends Float> c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(FloatCollection c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(FloatBigList c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(long i, FloatCollection c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(long i, FloatBigList c) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public void add(long index, Float k) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public boolean add(Float k) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public Float get(long i) {
            throw new IndexOutOfBoundsException();
        }

        @Deprecated
        @Override
        public Float set(long index, Float k) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public Float remove(long k) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public long indexOf(Object k) {
            return -1L;
        }

        @Deprecated
        @Override
        public long lastIndexOf(Object k) {
            return -1L;
        }

        @Override
        public FloatBigListIterator listIterator() {
            return FloatBigListIterators.EMPTY_BIG_LIST_ITERATOR;
        }

        @Override
        public FloatBigListIterator iterator() {
            return FloatBigListIterators.EMPTY_BIG_LIST_ITERATOR;
        }

        @Override
        public FloatBigListIterator listIterator(long i) {
            if (i == 0L) {
                return FloatBigListIterators.EMPTY_BIG_LIST_ITERATOR;
            }
            throw new IndexOutOfBoundsException(String.valueOf(i));
        }

        @Override
        public FloatBigList subList(long from, long to) {
            if (from == 0L && to == 0L) {
                return this;
            }
            throw new IndexOutOfBoundsException();
        }

        @Override
        public void getElements(long from, float[][] a, long offset, long length) {
            FloatBigArrays.ensureOffsetLength(a, offset, length);
            if (from != 0L) {
                throw new IndexOutOfBoundsException();
            }
        }

        @Override
        public void removeElements(long from, long to) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void addElements(long index, float[][] a, long offset, long length) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void addElements(long index, float[][] a) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void size(long s) {
            throw new UnsupportedOperationException();
        }

        @Override
        public long size64() {
            return 0L;
        }

        @Override
        public int compareTo(BigList<? extends Float> o) {
            if (o == this) {
                return 0;
            }
            return o.isEmpty() ? 0 : -1;
        }

        public Object clone() {
            return FloatBigLists.EMPTY_BIG_LIST;
        }

        @Override
        public int hashCode() {
            return 1;
        }

        @Override
        public boolean equals(Object o) {
            return o instanceof BigList && ((BigList)o).isEmpty();
        }

        @Override
        public String toString() {
            return "[]";
        }

        private Object readResolve() {
            return FloatBigLists.EMPTY_BIG_LIST;
        }
    }

}

