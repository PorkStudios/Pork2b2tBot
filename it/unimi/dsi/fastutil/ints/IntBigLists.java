/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.ints;

import it.unimi.dsi.fastutil.BigList;
import it.unimi.dsi.fastutil.BigListIterator;
import it.unimi.dsi.fastutil.ints.AbstractIntBigList;
import it.unimi.dsi.fastutil.ints.IntBidirectionalIterator;
import it.unimi.dsi.fastutil.ints.IntBigArrays;
import it.unimi.dsi.fastutil.ints.IntBigList;
import it.unimi.dsi.fastutil.ints.IntBigListIterator;
import it.unimi.dsi.fastutil.ints.IntBigListIterators;
import it.unimi.dsi.fastutil.ints.IntCollection;
import it.unimi.dsi.fastutil.ints.IntCollections;
import it.unimi.dsi.fastutil.ints.IntIterator;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.ints.IntListIterator;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.Random;

public final class IntBigLists {
    public static final EmptyBigList EMPTY_BIG_LIST = new EmptyBigList();

    private IntBigLists() {
    }

    public static IntBigList shuffle(IntBigList l, Random random) {
        long i = l.size64();
        while (i-- != 0L) {
            long p = (random.nextLong() & Long.MAX_VALUE) % (i + 1L);
            int t = l.getInt(i);
            l.set(i, l.getInt(p));
            l.set(p, t);
        }
        return l;
    }

    public static IntBigList singleton(int element) {
        return new Singleton(element);
    }

    public static IntBigList singleton(Object element) {
        return new Singleton((Integer)element);
    }

    public static IntBigList synchronize(IntBigList l) {
        return new SynchronizedBigList(l);
    }

    public static IntBigList synchronize(IntBigList l, Object sync) {
        return new SynchronizedBigList(l, sync);
    }

    public static IntBigList unmodifiable(IntBigList l) {
        return new UnmodifiableBigList(l);
    }

    public static IntBigList asBigList(IntList list) {
        return new ListBigList(list);
    }

    public static class ListBigList
    extends AbstractIntBigList
    implements Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        private final IntList list;

        protected ListBigList(IntList list) {
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
        public IntBigListIterator iterator() {
            return IntBigListIterators.asBigListIterator(this.list.iterator());
        }

        @Override
        public IntBigListIterator listIterator() {
            return IntBigListIterators.asBigListIterator(this.list.listIterator());
        }

        @Override
        public IntBigListIterator listIterator(long index) {
            return IntBigListIterators.asBigListIterator(this.list.listIterator(this.intIndex(index)));
        }

        @Override
        public boolean addAll(long index, Collection<? extends Integer> c) {
            return this.list.addAll(this.intIndex(index), c);
        }

        @Override
        public IntBigList subList(long from, long to) {
            return new ListBigList(this.list.subList(this.intIndex(from), this.intIndex(to)));
        }

        @Override
        public boolean contains(int key) {
            return this.list.contains(key);
        }

        @Override
        public int[] toIntArray() {
            return this.list.toIntArray();
        }

        @Override
        public void removeElements(long from, long to) {
            this.list.removeElements(this.intIndex(from), this.intIndex(to));
        }

        @Deprecated
        @Override
        public int[] toIntArray(int[] a) {
            return this.list.toArray(a);
        }

        @Override
        public boolean addAll(long index, IntCollection c) {
            return this.list.addAll(this.intIndex(index), c);
        }

        @Override
        public boolean addAll(IntCollection c) {
            return this.list.addAll(c);
        }

        @Override
        public boolean addAll(long index, IntBigList c) {
            return this.list.addAll(this.intIndex(index), c);
        }

        @Override
        public boolean addAll(IntBigList c) {
            return this.list.addAll(c);
        }

        @Override
        public boolean containsAll(IntCollection c) {
            return this.list.containsAll(c);
        }

        @Override
        public boolean removeAll(IntCollection c) {
            return this.list.removeAll(c);
        }

        @Override
        public boolean retainAll(IntCollection c) {
            return this.list.retainAll(c);
        }

        @Override
        public void add(long index, int key) {
            this.list.add(this.intIndex(index), key);
        }

        @Override
        public boolean add(int key) {
            return this.list.add(key);
        }

        @Override
        public int getInt(long index) {
            return this.list.getInt(this.intIndex(index));
        }

        @Override
        public long indexOf(int k) {
            return this.list.indexOf(k);
        }

        @Override
        public long lastIndexOf(int k) {
            return this.list.lastIndexOf(k);
        }

        @Override
        public int removeInt(long index) {
            return this.list.removeInt(this.intIndex(index));
        }

        @Override
        public int set(long index, int k) {
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
        public boolean addAll(Collection<? extends Integer> c) {
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
    extends IntCollections.UnmodifiableCollection
    implements IntBigList,
    Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final IntBigList list;

        protected UnmodifiableBigList(IntBigList l) {
            super(l);
            this.list = l;
        }

        @Override
        public int getInt(long i) {
            return this.list.getInt(i);
        }

        @Override
        public int set(long i, int k) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void add(long i, int k) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int removeInt(long i) {
            throw new UnsupportedOperationException();
        }

        @Override
        public long indexOf(int k) {
            return this.list.indexOf(k);
        }

        @Override
        public long lastIndexOf(int k) {
            return this.list.lastIndexOf(k);
        }

        @Override
        public boolean addAll(long index, Collection<? extends Integer> c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void getElements(long from, int[][] a, long offset, long length) {
            this.list.getElements(from, a, offset, length);
        }

        @Override
        public void removeElements(long from, long to) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void addElements(long index, int[][] a, long offset, long length) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void addElements(long index, int[][] a) {
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
        public IntBigListIterator iterator() {
            return this.listIterator();
        }

        @Override
        public IntBigListIterator listIterator() {
            return IntBigListIterators.unmodifiable(this.list.listIterator());
        }

        @Override
        public IntBigListIterator listIterator(long i) {
            return IntBigListIterators.unmodifiable(this.list.listIterator(i));
        }

        @Override
        public IntBigList subList(long from, long to) {
            return IntBigLists.unmodifiable(this.list.subList(from, to));
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
        public int compareTo(BigList<? extends Integer> o) {
            return this.list.compareTo(o);
        }

        @Override
        public boolean addAll(long index, IntCollection c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(IntBigList l) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(long index, IntBigList l) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public Integer get(long i) {
            return this.list.get(i);
        }

        @Deprecated
        @Override
        public void add(long i, Integer k) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public Integer set(long index, Integer k) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public Integer remove(long i) {
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
    extends IntCollections.SynchronizedCollection
    implements IntBigList,
    Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final IntBigList list;

        protected SynchronizedBigList(IntBigList l, Object sync) {
            super(l, sync);
            this.list = l;
        }

        protected SynchronizedBigList(IntBigList l) {
            super(l);
            this.list = l;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public int getInt(long i) {
            Object object = this.sync;
            synchronized (object) {
                return this.list.getInt(i);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public int set(long i, int k) {
            Object object = this.sync;
            synchronized (object) {
                return this.list.set(i, k);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void add(long i, int k) {
            Object object = this.sync;
            synchronized (object) {
                this.list.add(i, k);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public int removeInt(long i) {
            Object object = this.sync;
            synchronized (object) {
                return this.list.removeInt(i);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public long indexOf(int k) {
            Object object = this.sync;
            synchronized (object) {
                return this.list.indexOf(k);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public long lastIndexOf(int k) {
            Object object = this.sync;
            synchronized (object) {
                return this.list.lastIndexOf(k);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean addAll(long index, Collection<? extends Integer> c) {
            Object object = this.sync;
            synchronized (object) {
                return this.list.addAll(index, c);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void getElements(long from, int[][] a, long offset, long length) {
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
        public void addElements(long index, int[][] a, long offset, long length) {
            Object object = this.sync;
            synchronized (object) {
                this.list.addElements(index, a, offset, length);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void addElements(long index, int[][] a) {
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
        public IntBigListIterator iterator() {
            return this.list.listIterator();
        }

        @Override
        public IntBigListIterator listIterator() {
            return this.list.listIterator();
        }

        @Override
        public IntBigListIterator listIterator(long i) {
            return this.list.listIterator(i);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public IntBigList subList(long from, long to) {
            Object object = this.sync;
            synchronized (object) {
                return IntBigLists.synchronize(this.list.subList(from, to), this.sync);
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
        public int compareTo(BigList<? extends Integer> o) {
            Object object = this.sync;
            synchronized (object) {
                return this.list.compareTo(o);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean addAll(long index, IntCollection c) {
            Object object = this.sync;
            synchronized (object) {
                return this.list.addAll(index, c);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean addAll(long index, IntBigList l) {
            Object object = this.sync;
            synchronized (object) {
                return this.list.addAll(index, l);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean addAll(IntBigList l) {
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
        public void add(long i, Integer k) {
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
        public Integer get(long i) {
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
        public Integer set(long index, Integer k) {
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
        public Integer remove(long i) {
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
    extends AbstractIntBigList
    implements Serializable,
    Cloneable {
        private static final long serialVersionUID = -7046029254386353129L;
        private final int element;

        protected Singleton(int element) {
            this.element = element;
        }

        @Override
        public int getInt(long i) {
            if (i == 0L) {
                return this.element;
            }
            throw new IndexOutOfBoundsException();
        }

        @Override
        public boolean rem(int k) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int removeInt(long i) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean contains(int k) {
            return k == this.element;
        }

        @Override
        public int[] toIntArray() {
            int[] a = new int[]{this.element};
            return a;
        }

        @Override
        public IntBigListIterator listIterator() {
            return IntBigListIterators.singleton(this.element);
        }

        @Override
        public IntBigListIterator listIterator(long i) {
            if (i > 1L || i < 0L) {
                throw new IndexOutOfBoundsException();
            }
            IntBigListIterator l = this.listIterator();
            if (i == 1L) {
                l.nextInt();
            }
            return l;
        }

        @Override
        public IntBigList subList(long from, long to) {
            this.ensureIndex(from);
            this.ensureIndex(to);
            if (from > to) {
                throw new IndexOutOfBoundsException("Start index (" + from + ") is greater than end index (" + to + ")");
            }
            if (from != 0L || to != 1L) {
                return IntBigLists.EMPTY_BIG_LIST;
            }
            return this;
        }

        @Override
        public boolean addAll(long i, Collection<? extends Integer> c) {
            throw new UnsupportedOperationException();
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
        public boolean addAll(IntBigList c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(long i, IntBigList c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(long i, IntCollection c) {
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
    extends IntCollections.EmptyCollection
    implements IntBigList,
    Serializable,
    Cloneable {
        private static final long serialVersionUID = -7046029254386353129L;

        protected EmptyBigList() {
        }

        @Override
        public int getInt(long i) {
            throw new IndexOutOfBoundsException();
        }

        @Override
        public boolean rem(int k) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int removeInt(long i) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void add(long index, int k) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int set(long index, int k) {
            throw new UnsupportedOperationException();
        }

        @Override
        public long indexOf(int k) {
            return -1L;
        }

        @Override
        public long lastIndexOf(int k) {
            return -1L;
        }

        @Override
        public boolean addAll(long i, Collection<? extends Integer> c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(IntCollection c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(IntBigList c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(long i, IntCollection c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(long i, IntBigList c) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public void add(long index, Integer k) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public boolean add(Integer k) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public Integer get(long i) {
            throw new IndexOutOfBoundsException();
        }

        @Deprecated
        @Override
        public Integer set(long index, Integer k) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public Integer remove(long k) {
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
        public IntBigListIterator listIterator() {
            return IntBigListIterators.EMPTY_BIG_LIST_ITERATOR;
        }

        @Override
        public IntBigListIterator iterator() {
            return IntBigListIterators.EMPTY_BIG_LIST_ITERATOR;
        }

        @Override
        public IntBigListIterator listIterator(long i) {
            if (i == 0L) {
                return IntBigListIterators.EMPTY_BIG_LIST_ITERATOR;
            }
            throw new IndexOutOfBoundsException(String.valueOf(i));
        }

        @Override
        public IntBigList subList(long from, long to) {
            if (from == 0L && to == 0L) {
                return this;
            }
            throw new IndexOutOfBoundsException();
        }

        @Override
        public void getElements(long from, int[][] a, long offset, long length) {
            IntBigArrays.ensureOffsetLength(a, offset, length);
            if (from != 0L) {
                throw new IndexOutOfBoundsException();
            }
        }

        @Override
        public void removeElements(long from, long to) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void addElements(long index, int[][] a, long offset, long length) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void addElements(long index, int[][] a) {
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
        public int compareTo(BigList<? extends Integer> o) {
            if (o == this) {
                return 0;
            }
            return o.isEmpty() ? 0 : -1;
        }

        public Object clone() {
            return IntBigLists.EMPTY_BIG_LIST;
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
            return IntBigLists.EMPTY_BIG_LIST;
        }
    }

}

