/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.chars.AbstractCharList;
import it.unimi.dsi.fastutil.chars.CharBidirectionalIterator;
import it.unimi.dsi.fastutil.chars.CharCollection;
import it.unimi.dsi.fastutil.chars.CharCollections;
import it.unimi.dsi.fastutil.chars.CharIterator;
import it.unimi.dsi.fastutil.chars.CharIterators;
import it.unimi.dsi.fastutil.chars.CharList;
import it.unimi.dsi.fastutil.chars.CharListIterator;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Random;
import java.util.RandomAccess;

public final class CharLists {
    public static final EmptyList EMPTY_LIST = new EmptyList();

    private CharLists() {
    }

    public static CharList shuffle(CharList l, Random random) {
        int i = l.size();
        while (i-- != 0) {
            int p = random.nextInt(i + 1);
            char t = l.getChar(i);
            l.set(i, l.getChar(p));
            l.set(p, t);
        }
        return l;
    }

    public static CharList singleton(char element) {
        return new Singleton(element);
    }

    public static CharList singleton(Object element) {
        return new Singleton(((Character)element).charValue());
    }

    public static CharList synchronize(CharList l) {
        return l instanceof RandomAccess ? new SynchronizedRandomAccessList(l) : new SynchronizedList(l);
    }

    public static CharList synchronize(CharList l, Object sync) {
        return l instanceof RandomAccess ? new SynchronizedRandomAccessList(l, sync) : new SynchronizedList(l, sync);
    }

    public static CharList unmodifiable(CharList l) {
        return l instanceof RandomAccess ? new UnmodifiableRandomAccessList(l) : new UnmodifiableList(l);
    }

    public static class UnmodifiableRandomAccessList
    extends UnmodifiableList
    implements RandomAccess,
    Serializable {
        private static final long serialVersionUID = 0L;

        protected UnmodifiableRandomAccessList(CharList l) {
            super(l);
        }

        @Override
        public CharList subList(int from, int to) {
            return new UnmodifiableRandomAccessList(this.list.subList(from, to));
        }
    }

    public static class UnmodifiableList
    extends CharCollections.UnmodifiableCollection
    implements CharList,
    Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final CharList list;

        protected UnmodifiableList(CharList l) {
            super(l);
            this.list = l;
        }

        @Override
        public char getChar(int i) {
            return this.list.getChar(i);
        }

        @Override
        public char set(int i, char k) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void add(int i, char k) {
            throw new UnsupportedOperationException();
        }

        @Override
        public char removeChar(int i) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int indexOf(char k) {
            return this.list.indexOf(k);
        }

        @Override
        public int lastIndexOf(char k) {
            return this.list.lastIndexOf(k);
        }

        @Override
        public boolean addAll(int index, Collection<? extends Character> c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void getElements(int from, char[] a, int offset, int length) {
            this.list.getElements(from, a, offset, length);
        }

        @Override
        public void removeElements(int from, int to) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void addElements(int index, char[] a, int offset, int length) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void addElements(int index, char[] a) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void size(int size) {
            this.list.size(size);
        }

        @Override
        public CharListIterator listIterator() {
            return CharIterators.unmodifiable(this.list.listIterator());
        }

        @Override
        public CharListIterator iterator() {
            return this.listIterator();
        }

        @Override
        public CharListIterator listIterator(int i) {
            return CharIterators.unmodifiable(this.list.listIterator(i));
        }

        @Override
        public CharList subList(int from, int to) {
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
        public int compareTo(List<? extends Character> o) {
            return this.list.compareTo(o);
        }

        @Override
        public boolean addAll(int index, CharCollection c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(CharList l) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(int index, CharList l) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public Character get(int i) {
            return this.list.get(i);
        }

        @Deprecated
        @Override
        public void add(int i, Character k) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public Character set(int index, Character k) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public Character remove(int i) {
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

        protected SynchronizedRandomAccessList(CharList l, Object sync) {
            super(l, sync);
        }

        protected SynchronizedRandomAccessList(CharList l) {
            super(l);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public CharList subList(int from, int to) {
            Object object = this.sync;
            synchronized (object) {
                return new SynchronizedRandomAccessList(this.list.subList(from, to), this.sync);
            }
        }
    }

    public static class SynchronizedList
    extends CharCollections.SynchronizedCollection
    implements CharList,
    Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final CharList list;

        protected SynchronizedList(CharList l, Object sync) {
            super(l, sync);
            this.list = l;
        }

        protected SynchronizedList(CharList l) {
            super(l);
            this.list = l;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public char getChar(int i) {
            Object object = this.sync;
            synchronized (object) {
                return this.list.getChar(i);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public char set(int i, char k) {
            Object object = this.sync;
            synchronized (object) {
                return this.list.set(i, k);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void add(int i, char k) {
            Object object = this.sync;
            synchronized (object) {
                this.list.add(i, k);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public char removeChar(int i) {
            Object object = this.sync;
            synchronized (object) {
                return this.list.removeChar(i);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public int indexOf(char k) {
            Object object = this.sync;
            synchronized (object) {
                return this.list.indexOf(k);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public int lastIndexOf(char k) {
            Object object = this.sync;
            synchronized (object) {
                return this.list.lastIndexOf(k);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean addAll(int index, Collection<? extends Character> c) {
            Object object = this.sync;
            synchronized (object) {
                return this.list.addAll(index, c);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void getElements(int from, char[] a, int offset, int length) {
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
        public void addElements(int index, char[] a, int offset, int length) {
            Object object = this.sync;
            synchronized (object) {
                this.list.addElements(index, a, offset, length);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void addElements(int index, char[] a) {
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
        public CharListIterator listIterator() {
            return this.list.listIterator();
        }

        @Override
        public CharListIterator iterator() {
            return this.listIterator();
        }

        @Override
        public CharListIterator listIterator(int i) {
            return this.list.listIterator(i);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public CharList subList(int from, int to) {
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
        public int compareTo(List<? extends Character> o) {
            Object object = this.sync;
            synchronized (object) {
                return this.list.compareTo(o);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean addAll(int index, CharCollection c) {
            Object object = this.sync;
            synchronized (object) {
                return this.list.addAll(index, c);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean addAll(int index, CharList l) {
            Object object = this.sync;
            synchronized (object) {
                return this.list.addAll(index, l);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean addAll(CharList l) {
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
        public Character get(int i) {
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
        public void add(int i, Character k) {
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
        public Character set(int index, Character k) {
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
        public Character remove(int i) {
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
    extends AbstractCharList
    implements RandomAccess,
    Serializable,
    Cloneable {
        private static final long serialVersionUID = -7046029254386353129L;
        private final char element;

        protected Singleton(char element) {
            this.element = element;
        }

        @Override
        public char getChar(int i) {
            if (i == 0) {
                return this.element;
            }
            throw new IndexOutOfBoundsException();
        }

        @Override
        public boolean rem(char k) {
            throw new UnsupportedOperationException();
        }

        @Override
        public char removeChar(int i) {
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
        public CharListIterator listIterator() {
            return CharIterators.singleton(this.element);
        }

        @Override
        public CharListIterator iterator() {
            return this.listIterator();
        }

        @Override
        public CharListIterator listIterator(int i) {
            if (i > 1 || i < 0) {
                throw new IndexOutOfBoundsException();
            }
            CharListIterator l = this.listIterator();
            if (i == 1) {
                l.nextChar();
            }
            return l;
        }

        @Override
        public CharList subList(int from, int to) {
            this.ensureIndex(from);
            this.ensureIndex(to);
            if (from > to) {
                throw new IndexOutOfBoundsException("Start index (" + from + ") is greater than end index (" + to + ")");
            }
            if (from != 0 || to != 1) {
                return CharLists.EMPTY_LIST;
            }
            return this;
        }

        @Override
        public boolean addAll(int i, Collection<? extends Character> c) {
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
        public boolean addAll(CharList c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(int i, CharList c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(int i, CharCollection c) {
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
    extends CharCollections.EmptyCollection
    implements CharList,
    RandomAccess,
    Serializable,
    Cloneable {
        private static final long serialVersionUID = -7046029254386353129L;

        protected EmptyList() {
        }

        @Override
        public char getChar(int i) {
            throw new IndexOutOfBoundsException();
        }

        @Override
        public boolean rem(char k) {
            throw new UnsupportedOperationException();
        }

        @Override
        public char removeChar(int i) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void add(int index, char k) {
            throw new UnsupportedOperationException();
        }

        @Override
        public char set(int index, char k) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int indexOf(char k) {
            return -1;
        }

        @Override
        public int lastIndexOf(char k) {
            return -1;
        }

        @Override
        public boolean addAll(int i, Collection<? extends Character> c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(CharList c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(int i, CharCollection c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(int i, CharList c) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public void add(int index, Character k) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public Character get(int index) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public boolean add(Character k) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public Character set(int index, Character k) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public Character remove(int k) {
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
        public CharListIterator listIterator() {
            return CharIterators.EMPTY_ITERATOR;
        }

        @Override
        public CharListIterator iterator() {
            return CharIterators.EMPTY_ITERATOR;
        }

        @Override
        public CharListIterator listIterator(int i) {
            if (i == 0) {
                return CharIterators.EMPTY_ITERATOR;
            }
            throw new IndexOutOfBoundsException(String.valueOf(i));
        }

        @Override
        public CharList subList(int from, int to) {
            if (from == 0 && to == 0) {
                return this;
            }
            throw new IndexOutOfBoundsException();
        }

        @Override
        public void getElements(int from, char[] a, int offset, int length) {
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
        public void addElements(int index, char[] a, int offset, int length) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void addElements(int index, char[] a) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void size(int s) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int compareTo(List<? extends Character> o) {
            if (o == this) {
                return 0;
            }
            return o.isEmpty() ? 0 : -1;
        }

        public Object clone() {
            return CharLists.EMPTY_LIST;
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
            return CharLists.EMPTY_LIST;
        }
    }

}

