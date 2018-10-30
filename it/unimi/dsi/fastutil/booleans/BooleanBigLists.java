/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.booleans;

import it.unimi.dsi.fastutil.BigList;
import it.unimi.dsi.fastutil.BigListIterator;
import it.unimi.dsi.fastutil.booleans.AbstractBooleanBigList;
import it.unimi.dsi.fastutil.booleans.BooleanBidirectionalIterator;
import it.unimi.dsi.fastutil.booleans.BooleanBigArrays;
import it.unimi.dsi.fastutil.booleans.BooleanBigList;
import it.unimi.dsi.fastutil.booleans.BooleanBigListIterator;
import it.unimi.dsi.fastutil.booleans.BooleanBigListIterators;
import it.unimi.dsi.fastutil.booleans.BooleanCollection;
import it.unimi.dsi.fastutil.booleans.BooleanCollections;
import it.unimi.dsi.fastutil.booleans.BooleanIterator;
import it.unimi.dsi.fastutil.booleans.BooleanList;
import it.unimi.dsi.fastutil.booleans.BooleanListIterator;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.Random;

public final class BooleanBigLists {
    public static final EmptyBigList EMPTY_BIG_LIST = new EmptyBigList();

    private BooleanBigLists() {
    }

    public static BooleanBigList shuffle(BooleanBigList l, Random random) {
        long i = l.size64();
        while (i-- != 0L) {
            long p = (random.nextLong() & Long.MAX_VALUE) % (i + 1L);
            boolean t = l.getBoolean(i);
            l.set(i, l.getBoolean(p));
            l.set(p, t);
        }
        return l;
    }

    public static BooleanBigList singleton(boolean element) {
        return new Singleton(element);
    }

    public static BooleanBigList singleton(Object element) {
        return new Singleton((Boolean)element);
    }

    public static BooleanBigList synchronize(BooleanBigList l) {
        return new SynchronizedBigList(l);
    }

    public static BooleanBigList synchronize(BooleanBigList l, Object sync) {
        return new SynchronizedBigList(l, sync);
    }

    public static BooleanBigList unmodifiable(BooleanBigList l) {
        return new UnmodifiableBigList(l);
    }

    public static BooleanBigList asBigList(BooleanList list) {
        return new ListBigList(list);
    }

    public static class ListBigList
    extends AbstractBooleanBigList
    implements Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        private final BooleanList list;

        protected ListBigList(BooleanList list) {
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
        public BooleanBigListIterator iterator() {
            return BooleanBigListIterators.asBigListIterator(this.list.iterator());
        }

        @Override
        public BooleanBigListIterator listIterator() {
            return BooleanBigListIterators.asBigListIterator(this.list.listIterator());
        }

        @Override
        public BooleanBigListIterator listIterator(long index) {
            return BooleanBigListIterators.asBigListIterator(this.list.listIterator(this.intIndex(index)));
        }

        @Override
        public boolean addAll(long index, Collection<? extends Boolean> c) {
            return this.list.addAll(this.intIndex(index), c);
        }

        @Override
        public BooleanBigList subList(long from, long to) {
            return new ListBigList(this.list.subList(this.intIndex(from), this.intIndex(to)));
        }

        @Override
        public boolean contains(boolean key) {
            return this.list.contains(key);
        }

        @Override
        public boolean[] toBooleanArray() {
            return this.list.toBooleanArray();
        }

        @Override
        public void removeElements(long from, long to) {
            this.list.removeElements(this.intIndex(from), this.intIndex(to));
        }

        @Deprecated
        @Override
        public boolean[] toBooleanArray(boolean[] a) {
            return this.list.toArray(a);
        }

        @Override
        public boolean addAll(long index, BooleanCollection c) {
            return this.list.addAll(this.intIndex(index), c);
        }

        @Override
        public boolean addAll(BooleanCollection c) {
            return this.list.addAll(c);
        }

        @Override
        public boolean addAll(long index, BooleanBigList c) {
            return this.list.addAll(this.intIndex(index), c);
        }

        @Override
        public boolean addAll(BooleanBigList c) {
            return this.list.addAll(c);
        }

        @Override
        public boolean containsAll(BooleanCollection c) {
            return this.list.containsAll(c);
        }

        @Override
        public boolean removeAll(BooleanCollection c) {
            return this.list.removeAll(c);
        }

        @Override
        public boolean retainAll(BooleanCollection c) {
            return this.list.retainAll(c);
        }

        @Override
        public void add(long index, boolean key) {
            this.list.add(this.intIndex(index), key);
        }

        @Override
        public boolean add(boolean key) {
            return this.list.add(key);
        }

        @Override
        public boolean getBoolean(long index) {
            return this.list.getBoolean(this.intIndex(index));
        }

        @Override
        public long indexOf(boolean k) {
            return this.list.indexOf(k);
        }

        @Override
        public long lastIndexOf(boolean k) {
            return this.list.lastIndexOf(k);
        }

        @Override
        public boolean removeBoolean(long index) {
            return this.list.removeBoolean(this.intIndex(index));
        }

        @Override
        public boolean set(long index, boolean k) {
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
        public boolean addAll(Collection<? extends Boolean> c) {
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
    extends BooleanCollections.UnmodifiableCollection
    implements BooleanBigList,
    Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final BooleanBigList list;

        protected UnmodifiableBigList(BooleanBigList l) {
            super(l);
            this.list = l;
        }

        @Override
        public boolean getBoolean(long i) {
            return this.list.getBoolean(i);
        }

        @Override
        public boolean set(long i, boolean k) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void add(long i, boolean k) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean removeBoolean(long i) {
            throw new UnsupportedOperationException();
        }

        @Override
        public long indexOf(boolean k) {
            return this.list.indexOf(k);
        }

        @Override
        public long lastIndexOf(boolean k) {
            return this.list.lastIndexOf(k);
        }

        @Override
        public boolean addAll(long index, Collection<? extends Boolean> c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void getElements(long from, boolean[][] a, long offset, long length) {
            this.list.getElements(from, a, offset, length);
        }

        @Override
        public void removeElements(long from, long to) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void addElements(long index, boolean[][] a, long offset, long length) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void addElements(long index, boolean[][] a) {
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
        public BooleanBigListIterator iterator() {
            return this.listIterator();
        }

        @Override
        public BooleanBigListIterator listIterator() {
            return BooleanBigListIterators.unmodifiable(this.list.listIterator());
        }

        @Override
        public BooleanBigListIterator listIterator(long i) {
            return BooleanBigListIterators.unmodifiable(this.list.listIterator(i));
        }

        @Override
        public BooleanBigList subList(long from, long to) {
            return BooleanBigLists.unmodifiable(this.list.subList(from, to));
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
        public int compareTo(BigList<? extends Boolean> o) {
            return this.list.compareTo(o);
        }

        @Override
        public boolean addAll(long index, BooleanCollection c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(BooleanBigList l) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(long index, BooleanBigList l) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public Boolean get(long i) {
            return this.list.get(i);
        }

        @Deprecated
        @Override
        public void add(long i, Boolean k) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public Boolean set(long index, Boolean k) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public Boolean remove(long i) {
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
    extends BooleanCollections.SynchronizedCollection
    implements BooleanBigList,
    Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final BooleanBigList list;

        protected SynchronizedBigList(BooleanBigList l, Object sync) {
            super(l, sync);
            this.list = l;
        }

        protected SynchronizedBigList(BooleanBigList l) {
            super(l);
            this.list = l;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean getBoolean(long i) {
            Object object = this.sync;
            synchronized (object) {
                return this.list.getBoolean(i);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean set(long i, boolean k) {
            Object object = this.sync;
            synchronized (object) {
                return this.list.set(i, k);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void add(long i, boolean k) {
            Object object = this.sync;
            synchronized (object) {
                this.list.add(i, k);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean removeBoolean(long i) {
            Object object = this.sync;
            synchronized (object) {
                return this.list.removeBoolean(i);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public long indexOf(boolean k) {
            Object object = this.sync;
            synchronized (object) {
                return this.list.indexOf(k);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public long lastIndexOf(boolean k) {
            Object object = this.sync;
            synchronized (object) {
                return this.list.lastIndexOf(k);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean addAll(long index, Collection<? extends Boolean> c) {
            Object object = this.sync;
            synchronized (object) {
                return this.list.addAll(index, c);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void getElements(long from, boolean[][] a, long offset, long length) {
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
        public void addElements(long index, boolean[][] a, long offset, long length) {
            Object object = this.sync;
            synchronized (object) {
                this.list.addElements(index, a, offset, length);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void addElements(long index, boolean[][] a) {
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
        public BooleanBigListIterator iterator() {
            return this.list.listIterator();
        }

        @Override
        public BooleanBigListIterator listIterator() {
            return this.list.listIterator();
        }

        @Override
        public BooleanBigListIterator listIterator(long i) {
            return this.list.listIterator(i);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public BooleanBigList subList(long from, long to) {
            Object object = this.sync;
            synchronized (object) {
                return BooleanBigLists.synchronize(this.list.subList(from, to), this.sync);
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
        public int compareTo(BigList<? extends Boolean> o) {
            Object object = this.sync;
            synchronized (object) {
                return this.list.compareTo(o);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean addAll(long index, BooleanCollection c) {
            Object object = this.sync;
            synchronized (object) {
                return this.list.addAll(index, c);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean addAll(long index, BooleanBigList l) {
            Object object = this.sync;
            synchronized (object) {
                return this.list.addAll(index, l);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean addAll(BooleanBigList l) {
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
        public void add(long i, Boolean k) {
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
        public Boolean get(long i) {
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
        public Boolean set(long index, Boolean k) {
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
        public Boolean remove(long i) {
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
    extends AbstractBooleanBigList
    implements Serializable,
    Cloneable {
        private static final long serialVersionUID = -7046029254386353129L;
        private final boolean element;

        protected Singleton(boolean element) {
            this.element = element;
        }

        @Override
        public boolean getBoolean(long i) {
            if (i == 0L) {
                return this.element;
            }
            throw new IndexOutOfBoundsException();
        }

        @Override
        public boolean rem(boolean k) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean removeBoolean(long i) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean contains(boolean k) {
            return k == this.element;
        }

        @Override
        public boolean[] toBooleanArray() {
            boolean[] a = new boolean[]{this.element};
            return a;
        }

        @Override
        public BooleanBigListIterator listIterator() {
            return BooleanBigListIterators.singleton(this.element);
        }

        @Override
        public BooleanBigListIterator listIterator(long i) {
            if (i > 1L || i < 0L) {
                throw new IndexOutOfBoundsException();
            }
            BooleanBigListIterator l = this.listIterator();
            if (i == 1L) {
                l.nextBoolean();
            }
            return l;
        }

        @Override
        public BooleanBigList subList(long from, long to) {
            this.ensureIndex(from);
            this.ensureIndex(to);
            if (from > to) {
                throw new IndexOutOfBoundsException("Start index (" + from + ") is greater than end index (" + to + ")");
            }
            if (from != 0L || to != 1L) {
                return BooleanBigLists.EMPTY_BIG_LIST;
            }
            return this;
        }

        @Override
        public boolean addAll(long i, Collection<? extends Boolean> c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(Collection<? extends Boolean> c) {
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
        public boolean addAll(BooleanBigList c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(long i, BooleanBigList c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(long i, BooleanCollection c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(BooleanCollection c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean removeAll(BooleanCollection c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean retainAll(BooleanCollection c) {
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
    extends BooleanCollections.EmptyCollection
    implements BooleanBigList,
    Serializable,
    Cloneable {
        private static final long serialVersionUID = -7046029254386353129L;

        protected EmptyBigList() {
        }

        @Override
        public boolean getBoolean(long i) {
            throw new IndexOutOfBoundsException();
        }

        @Override
        public boolean rem(boolean k) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean removeBoolean(long i) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void add(long index, boolean k) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean set(long index, boolean k) {
            throw new UnsupportedOperationException();
        }

        @Override
        public long indexOf(boolean k) {
            return -1L;
        }

        @Override
        public long lastIndexOf(boolean k) {
            return -1L;
        }

        @Override
        public boolean addAll(long i, Collection<? extends Boolean> c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(BooleanCollection c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(BooleanBigList c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(long i, BooleanCollection c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(long i, BooleanBigList c) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public void add(long index, Boolean k) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public boolean add(Boolean k) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public Boolean get(long i) {
            throw new IndexOutOfBoundsException();
        }

        @Deprecated
        @Override
        public Boolean set(long index, Boolean k) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public Boolean remove(long k) {
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
        public BooleanBigListIterator listIterator() {
            return BooleanBigListIterators.EMPTY_BIG_LIST_ITERATOR;
        }

        @Override
        public BooleanBigListIterator iterator() {
            return BooleanBigListIterators.EMPTY_BIG_LIST_ITERATOR;
        }

        @Override
        public BooleanBigListIterator listIterator(long i) {
            if (i == 0L) {
                return BooleanBigListIterators.EMPTY_BIG_LIST_ITERATOR;
            }
            throw new IndexOutOfBoundsException(String.valueOf(i));
        }

        @Override
        public BooleanBigList subList(long from, long to) {
            if (from == 0L && to == 0L) {
                return this;
            }
            throw new IndexOutOfBoundsException();
        }

        @Override
        public void getElements(long from, boolean[][] a, long offset, long length) {
            BooleanBigArrays.ensureOffsetLength(a, offset, length);
            if (from != 0L) {
                throw new IndexOutOfBoundsException();
            }
        }

        @Override
        public void removeElements(long from, long to) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void addElements(long index, boolean[][] a, long offset, long length) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void addElements(long index, boolean[][] a) {
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
        public int compareTo(BigList<? extends Boolean> o) {
            if (o == this) {
                return 0;
            }
            return o.isEmpty() ? 0 : -1;
        }

        public Object clone() {
            return BooleanBigLists.EMPTY_BIG_LIST;
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
            return BooleanBigLists.EMPTY_BIG_LIST;
        }
    }

}

