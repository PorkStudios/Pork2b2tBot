/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.BigList;
import it.unimi.dsi.fastutil.BigListIterator;
import it.unimi.dsi.fastutil.chars.AbstractCharBigList;
import it.unimi.dsi.fastutil.chars.CharBidirectionalIterator;
import it.unimi.dsi.fastutil.chars.CharBigArrays;
import it.unimi.dsi.fastutil.chars.CharBigList;
import it.unimi.dsi.fastutil.chars.CharBigListIterator;
import it.unimi.dsi.fastutil.chars.CharBigListIterators;
import it.unimi.dsi.fastutil.chars.CharCollection;
import it.unimi.dsi.fastutil.chars.CharCollections;
import it.unimi.dsi.fastutil.chars.CharIterator;
import it.unimi.dsi.fastutil.chars.CharList;
import it.unimi.dsi.fastutil.chars.CharListIterator;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.Random;

public final class CharBigLists {
    public static final EmptyBigList EMPTY_BIG_LIST = new EmptyBigList();

    private CharBigLists() {
    }

    public static CharBigList shuffle(CharBigList l, Random random) {
        long i = l.size64();
        while (i-- != 0L) {
            long p = (random.nextLong() & Long.MAX_VALUE) % (i + 1L);
            char t = l.getChar(i);
            l.set(i, l.getChar(p));
            l.set(p, t);
        }
        return l;
    }

    public static CharBigList singleton(char element) {
        return new Singleton(element);
    }

    public static CharBigList singleton(Object element) {
        return new Singleton(((Character)element).charValue());
    }

    public static CharBigList synchronize(CharBigList l) {
        return new SynchronizedBigList(l);
    }

    public static CharBigList synchronize(CharBigList l, Object sync) {
        return new SynchronizedBigList(l, sync);
    }

    public static CharBigList unmodifiable(CharBigList l) {
        return new UnmodifiableBigList(l);
    }

    public static CharBigList asBigList(CharList list) {
        return new ListBigList(list);
    }

    public static class ListBigList
    extends AbstractCharBigList
    implements Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        private final CharList list;

        protected ListBigList(CharList list) {
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
        public CharBigListIterator iterator() {
            return CharBigListIterators.asBigListIterator(this.list.iterator());
        }

        @Override
        public CharBigListIterator listIterator() {
            return CharBigListIterators.asBigListIterator(this.list.listIterator());
        }

        @Override
        public CharBigListIterator listIterator(long index) {
            return CharBigListIterators.asBigListIterator(this.list.listIterator(this.intIndex(index)));
        }

        @Override
        public boolean addAll(long index, Collection<? extends Character> c) {
            return this.list.addAll(this.intIndex(index), c);
        }

        @Override
        public CharBigList subList(long from, long to) {
            return new ListBigList(this.list.subList(this.intIndex(from), this.intIndex(to)));
        }

        @Override
        public boolean contains(char key) {
            return this.list.contains(key);
        }

        @Override
        public char[] toCharArray() {
            return this.list.toCharArray();
        }

        @Override
        public void removeElements(long from, long to) {
            this.list.removeElements(this.intIndex(from), this.intIndex(to));
        }

        @Deprecated
        @Override
        public char[] toCharArray(char[] a) {
            return this.list.toArray(a);
        }

        @Override
        public boolean addAll(long index, CharCollection c) {
            return this.list.addAll(this.intIndex(index), c);
        }

        @Override
        public boolean addAll(CharCollection c) {
            return this.list.addAll(c);
        }

        @Override
        public boolean addAll(long index, CharBigList c) {
            return this.list.addAll(this.intIndex(index), c);
        }

        @Override
        public boolean addAll(CharBigList c) {
            return this.list.addAll(c);
        }

        @Override
        public boolean containsAll(CharCollection c) {
            return this.list.containsAll(c);
        }

        @Override
        public boolean removeAll(CharCollection c) {
            return this.list.removeAll(c);
        }

        @Override
        public boolean retainAll(CharCollection c) {
            return this.list.retainAll(c);
        }

        @Override
        public void add(long index, char key) {
            this.list.add(this.intIndex(index), key);
        }

        @Override
        public boolean add(char key) {
            return this.list.add(key);
        }

        @Override
        public char getChar(long index) {
            return this.list.getChar(this.intIndex(index));
        }

        @Override
        public long indexOf(char k) {
            return this.list.indexOf(k);
        }

        @Override
        public long lastIndexOf(char k) {
            return this.list.lastIndexOf(k);
        }

        @Override
        public char removeChar(long index) {
            return this.list.removeChar(this.intIndex(index));
        }

        @Override
        public char set(long index, char k) {
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
        public boolean addAll(Collection<? extends Character> c) {
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
    extends CharCollections.UnmodifiableCollection
    implements CharBigList,
    Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final CharBigList list;

        protected UnmodifiableBigList(CharBigList l) {
            super(l);
            this.list = l;
        }

        @Override
        public char getChar(long i) {
            return this.list.getChar(i);
        }

        @Override
        public char set(long i, char k) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void add(long i, char k) {
            throw new UnsupportedOperationException();
        }

        @Override
        public char removeChar(long i) {
            throw new UnsupportedOperationException();
        }

        @Override
        public long indexOf(char k) {
            return this.list.indexOf(k);
        }

        @Override
        public long lastIndexOf(char k) {
            return this.list.lastIndexOf(k);
        }

        @Override
        public boolean addAll(long index, Collection<? extends Character> c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void getElements(long from, char[][] a, long offset, long length) {
            this.list.getElements(from, a, offset, length);
        }

        @Override
        public void removeElements(long from, long to) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void addElements(long index, char[][] a, long offset, long length) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void addElements(long index, char[][] a) {
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
        public CharBigListIterator iterator() {
            return this.listIterator();
        }

        @Override
        public CharBigListIterator listIterator() {
            return CharBigListIterators.unmodifiable(this.list.listIterator());
        }

        @Override
        public CharBigListIterator listIterator(long i) {
            return CharBigListIterators.unmodifiable(this.list.listIterator(i));
        }

        @Override
        public CharBigList subList(long from, long to) {
            return CharBigLists.unmodifiable(this.list.subList(from, to));
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
        public int compareTo(BigList<? extends Character> o) {
            return this.list.compareTo(o);
        }

        @Override
        public boolean addAll(long index, CharCollection c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(CharBigList l) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(long index, CharBigList l) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public Character get(long i) {
            return this.list.get(i);
        }

        @Deprecated
        @Override
        public void add(long i, Character k) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public Character set(long index, Character k) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public Character remove(long i) {
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
    extends CharCollections.SynchronizedCollection
    implements CharBigList,
    Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final CharBigList list;

        protected SynchronizedBigList(CharBigList l, Object sync) {
            super(l, sync);
            this.list = l;
        }

        protected SynchronizedBigList(CharBigList l) {
            super(l);
            this.list = l;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public char getChar(long i) {
            Object object = this.sync;
            synchronized (object) {
                return this.list.getChar(i);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public char set(long i, char k) {
            Object object = this.sync;
            synchronized (object) {
                return this.list.set(i, k);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void add(long i, char k) {
            Object object = this.sync;
            synchronized (object) {
                this.list.add(i, k);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public char removeChar(long i) {
            Object object = this.sync;
            synchronized (object) {
                return this.list.removeChar(i);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public long indexOf(char k) {
            Object object = this.sync;
            synchronized (object) {
                return this.list.indexOf(k);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public long lastIndexOf(char k) {
            Object object = this.sync;
            synchronized (object) {
                return this.list.lastIndexOf(k);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean addAll(long index, Collection<? extends Character> c) {
            Object object = this.sync;
            synchronized (object) {
                return this.list.addAll(index, c);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void getElements(long from, char[][] a, long offset, long length) {
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
        public void addElements(long index, char[][] a, long offset, long length) {
            Object object = this.sync;
            synchronized (object) {
                this.list.addElements(index, a, offset, length);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void addElements(long index, char[][] a) {
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
        public CharBigListIterator iterator() {
            return this.list.listIterator();
        }

        @Override
        public CharBigListIterator listIterator() {
            return this.list.listIterator();
        }

        @Override
        public CharBigListIterator listIterator(long i) {
            return this.list.listIterator(i);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public CharBigList subList(long from, long to) {
            Object object = this.sync;
            synchronized (object) {
                return CharBigLists.synchronize(this.list.subList(from, to), this.sync);
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
        public int compareTo(BigList<? extends Character> o) {
            Object object = this.sync;
            synchronized (object) {
                return this.list.compareTo(o);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean addAll(long index, CharCollection c) {
            Object object = this.sync;
            synchronized (object) {
                return this.list.addAll(index, c);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean addAll(long index, CharBigList l) {
            Object object = this.sync;
            synchronized (object) {
                return this.list.addAll(index, l);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean addAll(CharBigList l) {
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
        public void add(long i, Character k) {
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
        public Character get(long i) {
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
        public Character set(long index, Character k) {
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
        public Character remove(long i) {
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
    extends AbstractCharBigList
    implements Serializable,
    Cloneable {
        private static final long serialVersionUID = -7046029254386353129L;
        private final char element;

        protected Singleton(char element) {
            this.element = element;
        }

        @Override
        public char getChar(long i) {
            if (i == 0L) {
                return this.element;
            }
            throw new IndexOutOfBoundsException();
        }

        @Override
        public boolean rem(char k) {
            throw new UnsupportedOperationException();
        }

        @Override
        public char removeChar(long i) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean contains(char k) {
            return k == this.element;
        }

        @Override
        public char[] toCharArray() {
            char[] a = new char[]{this.element};
            return a;
        }

        @Override
        public CharBigListIterator listIterator() {
            return CharBigListIterators.singleton(this.element);
        }

        @Override
        public CharBigListIterator listIterator(long i) {
            if (i > 1L || i < 0L) {
                throw new IndexOutOfBoundsException();
            }
            CharBigListIterator l = this.listIterator();
            if (i == 1L) {
                l.nextChar();
            }
            return l;
        }

        @Override
        public CharBigList subList(long from, long to) {
            this.ensureIndex(from);
            this.ensureIndex(to);
            if (from > to) {
                throw new IndexOutOfBoundsException("Start index (" + from + ") is greater than end index (" + to + ")");
            }
            if (from != 0L || to != 1L) {
                return CharBigLists.EMPTY_BIG_LIST;
            }
            return this;
        }

        @Override
        public boolean addAll(long i, Collection<? extends Character> c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(Collection<? extends Character> c) {
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
        public boolean addAll(CharBigList c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(long i, CharBigList c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(long i, CharCollection c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(CharCollection c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean removeAll(CharCollection c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean retainAll(CharCollection c) {
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
    extends CharCollections.EmptyCollection
    implements CharBigList,
    Serializable,
    Cloneable {
        private static final long serialVersionUID = -7046029254386353129L;

        protected EmptyBigList() {
        }

        @Override
        public char getChar(long i) {
            throw new IndexOutOfBoundsException();
        }

        @Override
        public boolean rem(char k) {
            throw new UnsupportedOperationException();
        }

        @Override
        public char removeChar(long i) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void add(long index, char k) {
            throw new UnsupportedOperationException();
        }

        @Override
        public char set(long index, char k) {
            throw new UnsupportedOperationException();
        }

        @Override
        public long indexOf(char k) {
            return -1L;
        }

        @Override
        public long lastIndexOf(char k) {
            return -1L;
        }

        @Override
        public boolean addAll(long i, Collection<? extends Character> c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(CharCollection c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(CharBigList c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(long i, CharCollection c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(long i, CharBigList c) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public void add(long index, Character k) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public boolean add(Character k) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public Character get(long i) {
            throw new IndexOutOfBoundsException();
        }

        @Deprecated
        @Override
        public Character set(long index, Character k) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public Character remove(long k) {
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
        public CharBigListIterator listIterator() {
            return CharBigListIterators.EMPTY_BIG_LIST_ITERATOR;
        }

        @Override
        public CharBigListIterator iterator() {
            return CharBigListIterators.EMPTY_BIG_LIST_ITERATOR;
        }

        @Override
        public CharBigListIterator listIterator(long i) {
            if (i == 0L) {
                return CharBigListIterators.EMPTY_BIG_LIST_ITERATOR;
            }
            throw new IndexOutOfBoundsException(String.valueOf(i));
        }

        @Override
        public CharBigList subList(long from, long to) {
            if (from == 0L && to == 0L) {
                return this;
            }
            throw new IndexOutOfBoundsException();
        }

        @Override
        public void getElements(long from, char[][] a, long offset, long length) {
            CharBigArrays.ensureOffsetLength(a, offset, length);
            if (from != 0L) {
                throw new IndexOutOfBoundsException();
            }
        }

        @Override
        public void removeElements(long from, long to) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void addElements(long index, char[][] a, long offset, long length) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void addElements(long index, char[][] a) {
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
        public int compareTo(BigList<? extends Character> o) {
            if (o == this) {
                return 0;
            }
            return o.isEmpty() ? 0 : -1;
        }

        public Object clone() {
            return CharBigLists.EMPTY_BIG_LIST;
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
            return CharBigLists.EMPTY_BIG_LIST;
        }
    }

}

