/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.BigList;
import it.unimi.dsi.fastutil.BigListIterator;
import it.unimi.dsi.fastutil.longs.AbstractLongBigList;
import it.unimi.dsi.fastutil.longs.LongBidirectionalIterator;
import it.unimi.dsi.fastutil.longs.LongBigArrays;
import it.unimi.dsi.fastutil.longs.LongBigList;
import it.unimi.dsi.fastutil.longs.LongBigListIterator;
import it.unimi.dsi.fastutil.longs.LongBigListIterators;
import it.unimi.dsi.fastutil.longs.LongCollection;
import it.unimi.dsi.fastutil.longs.LongCollections;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongList;
import it.unimi.dsi.fastutil.longs.LongListIterator;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.Random;

public final class LongBigLists {
    public static final EmptyBigList EMPTY_BIG_LIST = new EmptyBigList();

    private LongBigLists() {
    }

    public static LongBigList shuffle(LongBigList l, Random random) {
        long i = l.size64();
        while (i-- != 0L) {
            long p = (random.nextLong() & Long.MAX_VALUE) % (i + 1L);
            long t = l.getLong(i);
            l.set(i, l.getLong(p));
            l.set(p, t);
        }
        return l;
    }

    public static LongBigList singleton(long element) {
        return new Singleton(element);
    }

    public static LongBigList singleton(Object element) {
        return new Singleton((Long)element);
    }

    public static LongBigList synchronize(LongBigList l) {
        return new SynchronizedBigList(l);
    }

    public static LongBigList synchronize(LongBigList l, Object sync) {
        return new SynchronizedBigList(l, sync);
    }

    public static LongBigList unmodifiable(LongBigList l) {
        return new UnmodifiableBigList(l);
    }

    public static LongBigList asBigList(LongList list) {
        return new ListBigList(list);
    }

    public static class ListBigList
    extends AbstractLongBigList
    implements Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        private final LongList list;

        protected ListBigList(LongList list) {
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
        public LongBigListIterator iterator() {
            return LongBigListIterators.asBigListIterator(this.list.iterator());
        }

        @Override
        public LongBigListIterator listIterator() {
            return LongBigListIterators.asBigListIterator(this.list.listIterator());
        }

        @Override
        public LongBigListIterator listIterator(long index) {
            return LongBigListIterators.asBigListIterator(this.list.listIterator(this.intIndex(index)));
        }

        @Override
        public boolean addAll(long index, Collection<? extends Long> c) {
            return this.list.addAll(this.intIndex(index), c);
        }

        @Override
        public LongBigList subList(long from, long to) {
            return new ListBigList(this.list.subList(this.intIndex(from), this.intIndex(to)));
        }

        @Override
        public boolean contains(long key) {
            return this.list.contains(key);
        }

        @Override
        public long[] toLongArray() {
            return this.list.toLongArray();
        }

        @Override
        public void removeElements(long from, long to) {
            this.list.removeElements(this.intIndex(from), this.intIndex(to));
        }

        @Deprecated
        @Override
        public long[] toLongArray(long[] a) {
            return this.list.toArray(a);
        }

        @Override
        public boolean addAll(long index, LongCollection c) {
            return this.list.addAll(this.intIndex(index), c);
        }

        @Override
        public boolean addAll(LongCollection c) {
            return this.list.addAll(c);
        }

        @Override
        public boolean addAll(long index, LongBigList c) {
            return this.list.addAll(this.intIndex(index), c);
        }

        @Override
        public boolean addAll(LongBigList c) {
            return this.list.addAll(c);
        }

        @Override
        public boolean containsAll(LongCollection c) {
            return this.list.containsAll(c);
        }

        @Override
        public boolean removeAll(LongCollection c) {
            return this.list.removeAll(c);
        }

        @Override
        public boolean retainAll(LongCollection c) {
            return this.list.retainAll(c);
        }

        @Override
        public void add(long index, long key) {
            this.list.add(this.intIndex(index), key);
        }

        @Override
        public boolean add(long key) {
            return this.list.add(key);
        }

        @Override
        public long getLong(long index) {
            return this.list.getLong(this.intIndex(index));
        }

        @Override
        public long indexOf(long k) {
            return this.list.indexOf(k);
        }

        @Override
        public long lastIndexOf(long k) {
            return this.list.lastIndexOf(k);
        }

        @Override
        public long removeLong(long index) {
            return this.list.removeLong(this.intIndex(index));
        }

        @Override
        public long set(long index, long k) {
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
        public boolean addAll(Collection<? extends Long> c) {
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
    extends LongCollections.UnmodifiableCollection
    implements LongBigList,
    Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final LongBigList list;

        protected UnmodifiableBigList(LongBigList l) {
            super(l);
            this.list = l;
        }

        @Override
        public long getLong(long i) {
            return this.list.getLong(i);
        }

        @Override
        public long set(long i, long k) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void add(long i, long k) {
            throw new UnsupportedOperationException();
        }

        @Override
        public long removeLong(long i) {
            throw new UnsupportedOperationException();
        }

        @Override
        public long indexOf(long k) {
            return this.list.indexOf(k);
        }

        @Override
        public long lastIndexOf(long k) {
            return this.list.lastIndexOf(k);
        }

        @Override
        public boolean addAll(long index, Collection<? extends Long> c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void getElements(long from, long[][] a, long offset, long length) {
            this.list.getElements(from, a, offset, length);
        }

        @Override
        public void removeElements(long from, long to) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void addElements(long index, long[][] a, long offset, long length) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void addElements(long index, long[][] a) {
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
        public LongBigListIterator iterator() {
            return this.listIterator();
        }

        @Override
        public LongBigListIterator listIterator() {
            return LongBigListIterators.unmodifiable(this.list.listIterator());
        }

        @Override
        public LongBigListIterator listIterator(long i) {
            return LongBigListIterators.unmodifiable(this.list.listIterator(i));
        }

        @Override
        public LongBigList subList(long from, long to) {
            return LongBigLists.unmodifiable(this.list.subList(from, to));
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
        public int compareTo(BigList<? extends Long> o) {
            return this.list.compareTo(o);
        }

        @Override
        public boolean addAll(long index, LongCollection c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(LongBigList l) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(long index, LongBigList l) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public Long get(long i) {
            return this.list.get(i);
        }

        @Deprecated
        @Override
        public void add(long i, Long k) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public Long set(long index, Long k) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public Long remove(long i) {
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
    extends LongCollections.SynchronizedCollection
    implements LongBigList,
    Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final LongBigList list;

        protected SynchronizedBigList(LongBigList l, Object sync) {
            super(l, sync);
            this.list = l;
        }

        protected SynchronizedBigList(LongBigList l) {
            super(l);
            this.list = l;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public long getLong(long i) {
            Object object = this.sync;
            synchronized (object) {
                return this.list.getLong(i);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public long set(long i, long k) {
            Object object = this.sync;
            synchronized (object) {
                return this.list.set(i, k);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void add(long i, long k) {
            Object object = this.sync;
            synchronized (object) {
                this.list.add(i, k);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public long removeLong(long i) {
            Object object = this.sync;
            synchronized (object) {
                return this.list.removeLong(i);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public long indexOf(long k) {
            Object object = this.sync;
            synchronized (object) {
                return this.list.indexOf(k);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public long lastIndexOf(long k) {
            Object object = this.sync;
            synchronized (object) {
                return this.list.lastIndexOf(k);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean addAll(long index, Collection<? extends Long> c) {
            Object object = this.sync;
            synchronized (object) {
                return this.list.addAll(index, c);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void getElements(long from, long[][] a, long offset, long length) {
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
        public void addElements(long index, long[][] a, long offset, long length) {
            Object object = this.sync;
            synchronized (object) {
                this.list.addElements(index, a, offset, length);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void addElements(long index, long[][] a) {
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
        public LongBigListIterator iterator() {
            return this.list.listIterator();
        }

        @Override
        public LongBigListIterator listIterator() {
            return this.list.listIterator();
        }

        @Override
        public LongBigListIterator listIterator(long i) {
            return this.list.listIterator(i);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public LongBigList subList(long from, long to) {
            Object object = this.sync;
            synchronized (object) {
                return LongBigLists.synchronize(this.list.subList(from, to), this.sync);
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
        public int compareTo(BigList<? extends Long> o) {
            Object object = this.sync;
            synchronized (object) {
                return this.list.compareTo(o);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean addAll(long index, LongCollection c) {
            Object object = this.sync;
            synchronized (object) {
                return this.list.addAll(index, c);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean addAll(long index, LongBigList l) {
            Object object = this.sync;
            synchronized (object) {
                return this.list.addAll(index, l);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean addAll(LongBigList l) {
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
        public void add(long i, Long k) {
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
        public Long get(long i) {
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
        public Long set(long index, Long k) {
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
        public Long remove(long i) {
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
    extends AbstractLongBigList
    implements Serializable,
    Cloneable {
        private static final long serialVersionUID = -7046029254386353129L;
        private final long element;

        protected Singleton(long element) {
            this.element = element;
        }

        @Override
        public long getLong(long i) {
            if (i == 0L) {
                return this.element;
            }
            throw new IndexOutOfBoundsException();
        }

        @Override
        public boolean rem(long k) {
            throw new UnsupportedOperationException();
        }

        @Override
        public long removeLong(long i) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean contains(long k) {
            return k == this.element;
        }

        @Override
        public long[] toLongArray() {
            long[] a = new long[]{this.element};
            return a;
        }

        @Override
        public LongBigListIterator listIterator() {
            return LongBigListIterators.singleton(this.element);
        }

        @Override
        public LongBigListIterator listIterator(long i) {
            if (i > 1L || i < 0L) {
                throw new IndexOutOfBoundsException();
            }
            LongBigListIterator l = this.listIterator();
            if (i == 1L) {
                l.nextLong();
            }
            return l;
        }

        @Override
        public LongBigList subList(long from, long to) {
            this.ensureIndex(from);
            this.ensureIndex(to);
            if (from > to) {
                throw new IndexOutOfBoundsException("Start index (" + from + ") is greater than end index (" + to + ")");
            }
            if (from != 0L || to != 1L) {
                return LongBigLists.EMPTY_BIG_LIST;
            }
            return this;
        }

        @Override
        public boolean addAll(long i, Collection<? extends Long> c) {
            throw new UnsupportedOperationException();
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
        public boolean addAll(LongBigList c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(long i, LongBigList c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(long i, LongCollection c) {
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
    extends LongCollections.EmptyCollection
    implements LongBigList,
    Serializable,
    Cloneable {
        private static final long serialVersionUID = -7046029254386353129L;

        protected EmptyBigList() {
        }

        @Override
        public long getLong(long i) {
            throw new IndexOutOfBoundsException();
        }

        @Override
        public boolean rem(long k) {
            throw new UnsupportedOperationException();
        }

        @Override
        public long removeLong(long i) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void add(long index, long k) {
            throw new UnsupportedOperationException();
        }

        @Override
        public long set(long index, long k) {
            throw new UnsupportedOperationException();
        }

        @Override
        public long indexOf(long k) {
            return -1L;
        }

        @Override
        public long lastIndexOf(long k) {
            return -1L;
        }

        @Override
        public boolean addAll(long i, Collection<? extends Long> c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(LongCollection c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(LongBigList c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(long i, LongCollection c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(long i, LongBigList c) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public void add(long index, Long k) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public boolean add(Long k) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public Long get(long i) {
            throw new IndexOutOfBoundsException();
        }

        @Deprecated
        @Override
        public Long set(long index, Long k) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public Long remove(long k) {
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
        public LongBigListIterator listIterator() {
            return LongBigListIterators.EMPTY_BIG_LIST_ITERATOR;
        }

        @Override
        public LongBigListIterator iterator() {
            return LongBigListIterators.EMPTY_BIG_LIST_ITERATOR;
        }

        @Override
        public LongBigListIterator listIterator(long i) {
            if (i == 0L) {
                return LongBigListIterators.EMPTY_BIG_LIST_ITERATOR;
            }
            throw new IndexOutOfBoundsException(String.valueOf(i));
        }

        @Override
        public LongBigList subList(long from, long to) {
            if (from == 0L && to == 0L) {
                return this;
            }
            throw new IndexOutOfBoundsException();
        }

        @Override
        public void getElements(long from, long[][] a, long offset, long length) {
            LongBigArrays.ensureOffsetLength(a, offset, length);
            if (from != 0L) {
                throw new IndexOutOfBoundsException();
            }
        }

        @Override
        public void removeElements(long from, long to) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void addElements(long index, long[][] a, long offset, long length) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void addElements(long index, long[][] a) {
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
        public int compareTo(BigList<? extends Long> o) {
            if (o == this) {
                return 0;
            }
            return o.isEmpty() ? 0 : -1;
        }

        public Object clone() {
            return LongBigLists.EMPTY_BIG_LIST;
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
            return LongBigLists.EMPTY_BIG_LIST;
        }
    }

}

