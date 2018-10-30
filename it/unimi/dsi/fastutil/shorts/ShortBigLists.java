/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.shorts;

import it.unimi.dsi.fastutil.BigList;
import it.unimi.dsi.fastutil.BigListIterator;
import it.unimi.dsi.fastutil.shorts.AbstractShortBigList;
import it.unimi.dsi.fastutil.shorts.ShortBidirectionalIterator;
import it.unimi.dsi.fastutil.shorts.ShortBigArrays;
import it.unimi.dsi.fastutil.shorts.ShortBigList;
import it.unimi.dsi.fastutil.shorts.ShortBigListIterator;
import it.unimi.dsi.fastutil.shorts.ShortBigListIterators;
import it.unimi.dsi.fastutil.shorts.ShortCollection;
import it.unimi.dsi.fastutil.shorts.ShortCollections;
import it.unimi.dsi.fastutil.shorts.ShortIterator;
import it.unimi.dsi.fastutil.shorts.ShortList;
import it.unimi.dsi.fastutil.shorts.ShortListIterator;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.Random;

public final class ShortBigLists {
    public static final EmptyBigList EMPTY_BIG_LIST = new EmptyBigList();

    private ShortBigLists() {
    }

    public static ShortBigList shuffle(ShortBigList l, Random random) {
        long i = l.size64();
        while (i-- != 0L) {
            long p = (random.nextLong() & Long.MAX_VALUE) % (i + 1L);
            short t = l.getShort(i);
            l.set(i, l.getShort(p));
            l.set(p, t);
        }
        return l;
    }

    public static ShortBigList singleton(short element) {
        return new Singleton(element);
    }

    public static ShortBigList singleton(Object element) {
        return new Singleton((Short)element);
    }

    public static ShortBigList synchronize(ShortBigList l) {
        return new SynchronizedBigList(l);
    }

    public static ShortBigList synchronize(ShortBigList l, Object sync) {
        return new SynchronizedBigList(l, sync);
    }

    public static ShortBigList unmodifiable(ShortBigList l) {
        return new UnmodifiableBigList(l);
    }

    public static ShortBigList asBigList(ShortList list) {
        return new ListBigList(list);
    }

    public static class ListBigList
    extends AbstractShortBigList
    implements Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        private final ShortList list;

        protected ListBigList(ShortList list) {
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
        public ShortBigListIterator iterator() {
            return ShortBigListIterators.asBigListIterator(this.list.iterator());
        }

        @Override
        public ShortBigListIterator listIterator() {
            return ShortBigListIterators.asBigListIterator(this.list.listIterator());
        }

        @Override
        public ShortBigListIterator listIterator(long index) {
            return ShortBigListIterators.asBigListIterator(this.list.listIterator(this.intIndex(index)));
        }

        @Override
        public boolean addAll(long index, Collection<? extends Short> c) {
            return this.list.addAll(this.intIndex(index), c);
        }

        @Override
        public ShortBigList subList(long from, long to) {
            return new ListBigList(this.list.subList(this.intIndex(from), this.intIndex(to)));
        }

        @Override
        public boolean contains(short key) {
            return this.list.contains(key);
        }

        @Override
        public short[] toShortArray() {
            return this.list.toShortArray();
        }

        @Override
        public void removeElements(long from, long to) {
            this.list.removeElements(this.intIndex(from), this.intIndex(to));
        }

        @Deprecated
        @Override
        public short[] toShortArray(short[] a) {
            return this.list.toArray(a);
        }

        @Override
        public boolean addAll(long index, ShortCollection c) {
            return this.list.addAll(this.intIndex(index), c);
        }

        @Override
        public boolean addAll(ShortCollection c) {
            return this.list.addAll(c);
        }

        @Override
        public boolean addAll(long index, ShortBigList c) {
            return this.list.addAll(this.intIndex(index), c);
        }

        @Override
        public boolean addAll(ShortBigList c) {
            return this.list.addAll(c);
        }

        @Override
        public boolean containsAll(ShortCollection c) {
            return this.list.containsAll(c);
        }

        @Override
        public boolean removeAll(ShortCollection c) {
            return this.list.removeAll(c);
        }

        @Override
        public boolean retainAll(ShortCollection c) {
            return this.list.retainAll(c);
        }

        @Override
        public void add(long index, short key) {
            this.list.add(this.intIndex(index), key);
        }

        @Override
        public boolean add(short key) {
            return this.list.add(key);
        }

        @Override
        public short getShort(long index) {
            return this.list.getShort(this.intIndex(index));
        }

        @Override
        public long indexOf(short k) {
            return this.list.indexOf(k);
        }

        @Override
        public long lastIndexOf(short k) {
            return this.list.lastIndexOf(k);
        }

        @Override
        public short removeShort(long index) {
            return this.list.removeShort(this.intIndex(index));
        }

        @Override
        public short set(long index, short k) {
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
        public boolean addAll(Collection<? extends Short> c) {
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
    extends ShortCollections.UnmodifiableCollection
    implements ShortBigList,
    Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final ShortBigList list;

        protected UnmodifiableBigList(ShortBigList l) {
            super(l);
            this.list = l;
        }

        @Override
        public short getShort(long i) {
            return this.list.getShort(i);
        }

        @Override
        public short set(long i, short k) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void add(long i, short k) {
            throw new UnsupportedOperationException();
        }

        @Override
        public short removeShort(long i) {
            throw new UnsupportedOperationException();
        }

        @Override
        public long indexOf(short k) {
            return this.list.indexOf(k);
        }

        @Override
        public long lastIndexOf(short k) {
            return this.list.lastIndexOf(k);
        }

        @Override
        public boolean addAll(long index, Collection<? extends Short> c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void getElements(long from, short[][] a, long offset, long length) {
            this.list.getElements(from, a, offset, length);
        }

        @Override
        public void removeElements(long from, long to) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void addElements(long index, short[][] a, long offset, long length) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void addElements(long index, short[][] a) {
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
        public ShortBigListIterator iterator() {
            return this.listIterator();
        }

        @Override
        public ShortBigListIterator listIterator() {
            return ShortBigListIterators.unmodifiable(this.list.listIterator());
        }

        @Override
        public ShortBigListIterator listIterator(long i) {
            return ShortBigListIterators.unmodifiable(this.list.listIterator(i));
        }

        @Override
        public ShortBigList subList(long from, long to) {
            return ShortBigLists.unmodifiable(this.list.subList(from, to));
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
        public int compareTo(BigList<? extends Short> o) {
            return this.list.compareTo(o);
        }

        @Override
        public boolean addAll(long index, ShortCollection c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(ShortBigList l) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(long index, ShortBigList l) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public Short get(long i) {
            return this.list.get(i);
        }

        @Deprecated
        @Override
        public void add(long i, Short k) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public Short set(long index, Short k) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public Short remove(long i) {
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
    extends ShortCollections.SynchronizedCollection
    implements ShortBigList,
    Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final ShortBigList list;

        protected SynchronizedBigList(ShortBigList l, Object sync) {
            super(l, sync);
            this.list = l;
        }

        protected SynchronizedBigList(ShortBigList l) {
            super(l);
            this.list = l;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public short getShort(long i) {
            Object object = this.sync;
            synchronized (object) {
                return this.list.getShort(i);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public short set(long i, short k) {
            Object object = this.sync;
            synchronized (object) {
                return this.list.set(i, k);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void add(long i, short k) {
            Object object = this.sync;
            synchronized (object) {
                this.list.add(i, k);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public short removeShort(long i) {
            Object object = this.sync;
            synchronized (object) {
                return this.list.removeShort(i);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public long indexOf(short k) {
            Object object = this.sync;
            synchronized (object) {
                return this.list.indexOf(k);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public long lastIndexOf(short k) {
            Object object = this.sync;
            synchronized (object) {
                return this.list.lastIndexOf(k);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean addAll(long index, Collection<? extends Short> c) {
            Object object = this.sync;
            synchronized (object) {
                return this.list.addAll(index, c);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void getElements(long from, short[][] a, long offset, long length) {
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
        public void addElements(long index, short[][] a, long offset, long length) {
            Object object = this.sync;
            synchronized (object) {
                this.list.addElements(index, a, offset, length);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void addElements(long index, short[][] a) {
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
        public ShortBigListIterator iterator() {
            return this.list.listIterator();
        }

        @Override
        public ShortBigListIterator listIterator() {
            return this.list.listIterator();
        }

        @Override
        public ShortBigListIterator listIterator(long i) {
            return this.list.listIterator(i);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public ShortBigList subList(long from, long to) {
            Object object = this.sync;
            synchronized (object) {
                return ShortBigLists.synchronize(this.list.subList(from, to), this.sync);
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
        public int compareTo(BigList<? extends Short> o) {
            Object object = this.sync;
            synchronized (object) {
                return this.list.compareTo(o);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean addAll(long index, ShortCollection c) {
            Object object = this.sync;
            synchronized (object) {
                return this.list.addAll(index, c);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean addAll(long index, ShortBigList l) {
            Object object = this.sync;
            synchronized (object) {
                return this.list.addAll(index, l);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean addAll(ShortBigList l) {
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
        public void add(long i, Short k) {
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
        public Short get(long i) {
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
        public Short set(long index, Short k) {
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
        public Short remove(long i) {
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
    extends AbstractShortBigList
    implements Serializable,
    Cloneable {
        private static final long serialVersionUID = -7046029254386353129L;
        private final short element;

        protected Singleton(short element) {
            this.element = element;
        }

        @Override
        public short getShort(long i) {
            if (i == 0L) {
                return this.element;
            }
            throw new IndexOutOfBoundsException();
        }

        @Override
        public boolean rem(short k) {
            throw new UnsupportedOperationException();
        }

        @Override
        public short removeShort(long i) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean contains(short k) {
            return k == this.element;
        }

        @Override
        public short[] toShortArray() {
            short[] a = new short[]{this.element};
            return a;
        }

        @Override
        public ShortBigListIterator listIterator() {
            return ShortBigListIterators.singleton(this.element);
        }

        @Override
        public ShortBigListIterator listIterator(long i) {
            if (i > 1L || i < 0L) {
                throw new IndexOutOfBoundsException();
            }
            ShortBigListIterator l = this.listIterator();
            if (i == 1L) {
                l.nextShort();
            }
            return l;
        }

        @Override
        public ShortBigList subList(long from, long to) {
            this.ensureIndex(from);
            this.ensureIndex(to);
            if (from > to) {
                throw new IndexOutOfBoundsException("Start index (" + from + ") is greater than end index (" + to + ")");
            }
            if (from != 0L || to != 1L) {
                return ShortBigLists.EMPTY_BIG_LIST;
            }
            return this;
        }

        @Override
        public boolean addAll(long i, Collection<? extends Short> c) {
            throw new UnsupportedOperationException();
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
        public boolean addAll(ShortBigList c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(long i, ShortBigList c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(long i, ShortCollection c) {
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
    extends ShortCollections.EmptyCollection
    implements ShortBigList,
    Serializable,
    Cloneable {
        private static final long serialVersionUID = -7046029254386353129L;

        protected EmptyBigList() {
        }

        @Override
        public short getShort(long i) {
            throw new IndexOutOfBoundsException();
        }

        @Override
        public boolean rem(short k) {
            throw new UnsupportedOperationException();
        }

        @Override
        public short removeShort(long i) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void add(long index, short k) {
            throw new UnsupportedOperationException();
        }

        @Override
        public short set(long index, short k) {
            throw new UnsupportedOperationException();
        }

        @Override
        public long indexOf(short k) {
            return -1L;
        }

        @Override
        public long lastIndexOf(short k) {
            return -1L;
        }

        @Override
        public boolean addAll(long i, Collection<? extends Short> c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(ShortCollection c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(ShortBigList c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(long i, ShortCollection c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(long i, ShortBigList c) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public void add(long index, Short k) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public boolean add(Short k) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public Short get(long i) {
            throw new IndexOutOfBoundsException();
        }

        @Deprecated
        @Override
        public Short set(long index, Short k) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public Short remove(long k) {
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
        public ShortBigListIterator listIterator() {
            return ShortBigListIterators.EMPTY_BIG_LIST_ITERATOR;
        }

        @Override
        public ShortBigListIterator iterator() {
            return ShortBigListIterators.EMPTY_BIG_LIST_ITERATOR;
        }

        @Override
        public ShortBigListIterator listIterator(long i) {
            if (i == 0L) {
                return ShortBigListIterators.EMPTY_BIG_LIST_ITERATOR;
            }
            throw new IndexOutOfBoundsException(String.valueOf(i));
        }

        @Override
        public ShortBigList subList(long from, long to) {
            if (from == 0L && to == 0L) {
                return this;
            }
            throw new IndexOutOfBoundsException();
        }

        @Override
        public void getElements(long from, short[][] a, long offset, long length) {
            ShortBigArrays.ensureOffsetLength(a, offset, length);
            if (from != 0L) {
                throw new IndexOutOfBoundsException();
            }
        }

        @Override
        public void removeElements(long from, long to) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void addElements(long index, short[][] a, long offset, long length) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void addElements(long index, short[][] a) {
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
        public int compareTo(BigList<? extends Short> o) {
            if (o == this) {
                return 0;
            }
            return o.isEmpty() ? 0 : -1;
        }

        public Object clone() {
            return ShortBigLists.EMPTY_BIG_LIST;
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
            return ShortBigLists.EMPTY_BIG_LIST;
        }
    }

}

