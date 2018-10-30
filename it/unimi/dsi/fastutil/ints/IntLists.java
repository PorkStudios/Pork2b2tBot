/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.ints;

import it.unimi.dsi.fastutil.ints.AbstractIntList;
import it.unimi.dsi.fastutil.ints.IntBidirectionalIterator;
import it.unimi.dsi.fastutil.ints.IntCollection;
import it.unimi.dsi.fastutil.ints.IntCollections;
import it.unimi.dsi.fastutil.ints.IntIterator;
import it.unimi.dsi.fastutil.ints.IntIterators;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.ints.IntListIterator;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Random;
import java.util.RandomAccess;

public final class IntLists {
    public static final EmptyList EMPTY_LIST = new EmptyList();

    private IntLists() {
    }

    public static IntList shuffle(IntList l, Random random) {
        int i = l.size();
        while (i-- != 0) {
            int p = random.nextInt(i + 1);
            int t = l.getInt(i);
            l.set(i, l.getInt(p));
            l.set(p, t);
        }
        return l;
    }

    public static IntList singleton(int element) {
        return new Singleton(element);
    }

    public static IntList singleton(Object element) {
        return new Singleton((Integer)element);
    }

    public static IntList synchronize(IntList l) {
        return l instanceof RandomAccess ? new SynchronizedRandomAccessList(l) : new SynchronizedList(l);
    }

    public static IntList synchronize(IntList l, Object sync) {
        return l instanceof RandomAccess ? new SynchronizedRandomAccessList(l, sync) : new SynchronizedList(l, sync);
    }

    public static IntList unmodifiable(IntList l) {
        return l instanceof RandomAccess ? new UnmodifiableRandomAccessList(l) : new UnmodifiableList(l);
    }

    public static class UnmodifiableRandomAccessList
    extends UnmodifiableList
    implements RandomAccess,
    Serializable {
        private static final long serialVersionUID = 0L;

        protected UnmodifiableRandomAccessList(IntList l) {
            super(l);
        }

        @Override
        public IntList subList(int from, int to) {
            return new UnmodifiableRandomAccessList(this.list.subList(from, to));
        }
    }

    public static class UnmodifiableList
    extends IntCollections.UnmodifiableCollection
    implements IntList,
    Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final IntList list;

        protected UnmodifiableList(IntList l) {
            super(l);
            this.list = l;
        }

        @Override
        public int getInt(int i) {
            return this.list.getInt(i);
        }

        @Override
        public int set(int i, int k) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void add(int i, int k) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int removeInt(int i) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int indexOf(int k) {
            return this.list.indexOf(k);
        }

        @Override
        public int lastIndexOf(int k) {
            return this.list.lastIndexOf(k);
        }

        @Override
        public boolean addAll(int index, Collection<? extends Integer> c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void getElements(int from, int[] a, int offset, int length) {
            this.list.getElements(from, a, offset, length);
        }

        @Override
        public void removeElements(int from, int to) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void addElements(int index, int[] a, int offset, int length) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void addElements(int index, int[] a) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void size(int size) {
            this.list.size(size);
        }

        @Override
        public IntListIterator listIterator() {
            return IntIterators.unmodifiable(this.list.listIterator());
        }

        @Override
        public IntListIterator iterator() {
            return this.listIterator();
        }

        @Override
        public IntListIterator listIterator(int i) {
            return IntIterators.unmodifiable(this.list.listIterator(i));
        }

        @Override
        public IntList subList(int from, int to) {
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
        public int compareTo(List<? extends Integer> o) {
            return this.list.compareTo(o);
        }

        @Override
        public boolean addAll(int index, IntCollection c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(IntList l) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(int index, IntList l) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public Integer get(int i) {
            return this.list.get(i);
        }

        @Deprecated
        @Override
        public void add(int i, Integer k) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public Integer set(int index, Integer k) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public Integer remove(int i) {
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

        protected SynchronizedRandomAccessList(IntList l, Object sync) {
            super(l, sync);
        }

        protected SynchronizedRandomAccessList(IntList l) {
            super(l);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public IntList subList(int from, int to) {
            Object object = this.sync;
            synchronized (object) {
                return new SynchronizedRandomAccessList(this.list.subList(from, to), this.sync);
            }
        }
    }

    public static class SynchronizedList
    extends IntCollections.SynchronizedCollection
    implements IntList,
    Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final IntList list;

        protected SynchronizedList(IntList l, Object sync) {
            super(l, sync);
            this.list = l;
        }

        protected SynchronizedList(IntList l) {
            super(l);
            this.list = l;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public int getInt(int i) {
            Object object = this.sync;
            synchronized (object) {
                return this.list.getInt(i);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public int set(int i, int k) {
            Object object = this.sync;
            synchronized (object) {
                return this.list.set(i, k);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void add(int i, int k) {
            Object object = this.sync;
            synchronized (object) {
                this.list.add(i, k);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public int removeInt(int i) {
            Object object = this.sync;
            synchronized (object) {
                return this.list.removeInt(i);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public int indexOf(int k) {
            Object object = this.sync;
            synchronized (object) {
                return this.list.indexOf(k);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public int lastIndexOf(int k) {
            Object object = this.sync;
            synchronized (object) {
                return this.list.lastIndexOf(k);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean addAll(int index, Collection<? extends Integer> c) {
            Object object = this.sync;
            synchronized (object) {
                return this.list.addAll(index, c);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void getElements(int from, int[] a, int offset, int length) {
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
        public void addElements(int index, int[] a, int offset, int length) {
            Object object = this.sync;
            synchronized (object) {
                this.list.addElements(index, a, offset, length);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void addElements(int index, int[] a) {
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
        public IntListIterator listIterator() {
            return this.list.listIterator();
        }

        @Override
        public IntListIterator iterator() {
            return this.listIterator();
        }

        @Override
        public IntListIterator listIterator(int i) {
            return this.list.listIterator(i);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public IntList subList(int from, int to) {
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
        public int compareTo(List<? extends Integer> o) {
            Object object = this.sync;
            synchronized (object) {
                return this.list.compareTo(o);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean addAll(int index, IntCollection c) {
            Object object = this.sync;
            synchronized (object) {
                return this.list.addAll(index, c);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean addAll(int index, IntList l) {
            Object object = this.sync;
            synchronized (object) {
                return this.list.addAll(index, l);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean addAll(IntList l) {
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
        public Integer get(int i) {
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
        public void add(int i, Integer k) {
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
        public Integer set(int index, Integer k) {
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
        public Integer remove(int i) {
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
    extends AbstractIntList
    implements RandomAccess,
    Serializable,
    Cloneable {
        private static final long serialVersionUID = -7046029254386353129L;
        private final int element;

        protected Singleton(int element) {
            this.element = element;
        }

        @Override
        public int getInt(int i) {
            if (i == 0) {
                return this.element;
            }
            throw new IndexOutOfBoundsException();
        }

        @Override
        public boolean rem(int k) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int removeInt(int i) {
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
        public IntListIterator listIterator() {
            return IntIterators.singleton(this.element);
        }

        @Override
        public IntListIterator iterator() {
            return this.listIterator();
        }

        @Override
        public IntListIterator listIterator(int i) {
            if (i > 1 || i < 0) {
                throw new IndexOutOfBoundsException();
            }
            IntListIterator l = this.listIterator();
            if (i == 1) {
                l.nextInt();
            }
            return l;
        }

        @Override
        public IntList subList(int from, int to) {
            this.ensureIndex(from);
            this.ensureIndex(to);
            if (from > to) {
                throw new IndexOutOfBoundsException("Start index (" + from + ") is greater than end index (" + to + ")");
            }
            if (from != 0 || to != 1) {
                return IntLists.EMPTY_LIST;
            }
            return this;
        }

        @Override
        public boolean addAll(int i, Collection<? extends Integer> c) {
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
        public boolean addAll(IntList c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(int i, IntList c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(int i, IntCollection c) {
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
    extends IntCollections.EmptyCollection
    implements IntList,
    RandomAccess,
    Serializable,
    Cloneable {
        private static final long serialVersionUID = -7046029254386353129L;

        protected EmptyList() {
        }

        @Override
        public int getInt(int i) {
            throw new IndexOutOfBoundsException();
        }

        @Override
        public boolean rem(int k) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int removeInt(int i) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void add(int index, int k) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int set(int index, int k) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int indexOf(int k) {
            return -1;
        }

        @Override
        public int lastIndexOf(int k) {
            return -1;
        }

        @Override
        public boolean addAll(int i, Collection<? extends Integer> c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(IntList c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(int i, IntCollection c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(int i, IntList c) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public void add(int index, Integer k) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public Integer get(int index) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public boolean add(Integer k) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public Integer set(int index, Integer k) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public Integer remove(int k) {
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
        public IntListIterator listIterator() {
            return IntIterators.EMPTY_ITERATOR;
        }

        @Override
        public IntListIterator iterator() {
            return IntIterators.EMPTY_ITERATOR;
        }

        @Override
        public IntListIterator listIterator(int i) {
            if (i == 0) {
                return IntIterators.EMPTY_ITERATOR;
            }
            throw new IndexOutOfBoundsException(String.valueOf(i));
        }

        @Override
        public IntList subList(int from, int to) {
            if (from == 0 && to == 0) {
                return this;
            }
            throw new IndexOutOfBoundsException();
        }

        @Override
        public void getElements(int from, int[] a, int offset, int length) {
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
        public void addElements(int index, int[] a, int offset, int length) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void addElements(int index, int[] a) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void size(int s) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int compareTo(List<? extends Integer> o) {
            if (o == this) {
                return 0;
            }
            return o.isEmpty() ? 0 : -1;
        }

        public Object clone() {
            return IntLists.EMPTY_LIST;
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
            return IntLists.EMPTY_LIST;
        }
    }

}

