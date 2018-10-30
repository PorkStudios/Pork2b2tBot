/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.Arrays;
import it.unimi.dsi.fastutil.chars.AbstractCharList;
import it.unimi.dsi.fastutil.chars.CharArrays;
import it.unimi.dsi.fastutil.chars.CharCollection;
import it.unimi.dsi.fastutil.chars.CharIterator;
import it.unimi.dsi.fastutil.chars.CharIterators;
import it.unimi.dsi.fastutil.chars.CharList;
import it.unimi.dsi.fastutil.chars.CharListIterator;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.RandomAccess;

public class CharArrayList
extends AbstractCharList
implements RandomAccess,
Cloneable,
Serializable {
    private static final long serialVersionUID = -7046029254386353130L;
    public static final int DEFAULT_INITIAL_CAPACITY = 16;
    protected transient char[] a;
    protected int size;
    private static final boolean ASSERTS = false;

    protected CharArrayList(char[] a, boolean dummy) {
        this.a = a;
    }

    public CharArrayList(int capacity) {
        if (capacity < 0) {
            throw new IllegalArgumentException("Initial capacity (" + capacity + ") is negative");
        }
        this.a = new char[capacity];
    }

    public CharArrayList() {
        this(16);
    }

    public CharArrayList(Collection<? extends Character> c) {
        this(c.size());
        this.size = CharIterators.unwrap(CharIterators.asCharIterator(c.iterator()), this.a);
    }

    public CharArrayList(CharCollection c) {
        this(c.size());
        this.size = CharIterators.unwrap(c.iterator(), this.a);
    }

    public CharArrayList(CharList l) {
        this(l.size());
        this.size = l.size();
        l.getElements(0, this.a, 0, this.size);
    }

    public CharArrayList(char[] a) {
        this(a, 0, a.length);
    }

    public CharArrayList(char[] a, int offset, int length) {
        this(length);
        System.arraycopy(a, offset, this.a, 0, length);
        this.size = length;
    }

    public CharArrayList(Iterator<? extends Character> i) {
        this();
        while (i.hasNext()) {
            this.add(i.next().charValue());
        }
    }

    public CharArrayList(CharIterator i) {
        this();
        while (i.hasNext()) {
            this.add(i.nextChar());
        }
    }

    public char[] elements() {
        return this.a;
    }

    public static CharArrayList wrap(char[] a, int length) {
        if (length > a.length) {
            throw new IllegalArgumentException("The specified length (" + length + ") is greater than the array size (" + a.length + ")");
        }
        CharArrayList l = new CharArrayList(a, false);
        l.size = length;
        return l;
    }

    public static CharArrayList wrap(char[] a) {
        return CharArrayList.wrap(a, a.length);
    }

    public void ensureCapacity(int capacity) {
        this.a = CharArrays.ensureCapacity(this.a, capacity, this.size);
    }

    private void grow(int capacity) {
        this.a = CharArrays.grow(this.a, capacity, this.size);
    }

    @Override
    public void add(int index, char k) {
        this.ensureIndex(index);
        this.grow(this.size + 1);
        if (index != this.size) {
            System.arraycopy(this.a, index, this.a, index + 1, this.size - index);
        }
        this.a[index] = k;
        ++this.size;
    }

    @Override
    public boolean add(char k) {
        this.grow(this.size + 1);
        this.a[this.size++] = k;
        return true;
    }

    @Override
    public char getChar(int index) {
        if (index >= this.size) {
            throw new IndexOutOfBoundsException("Index (" + index + ") is greater than or equal to list size (" + this.size + ")");
        }
        return this.a[index];
    }

    @Override
    public int indexOf(char k) {
        for (int i = 0; i < this.size; ++i) {
            if (k != this.a[i]) continue;
            return i;
        }
        return -1;
    }

    @Override
    public int lastIndexOf(char k) {
        int i = this.size;
        while (i-- != 0) {
            if (k != this.a[i]) continue;
            return i;
        }
        return -1;
    }

    @Override
    public char removeChar(int index) {
        if (index >= this.size) {
            throw new IndexOutOfBoundsException("Index (" + index + ") is greater than or equal to list size (" + this.size + ")");
        }
        char old = this.a[index];
        --this.size;
        if (index != this.size) {
            System.arraycopy(this.a, index + 1, this.a, index, this.size - index);
        }
        return old;
    }

    @Override
    public boolean rem(char k) {
        int index = this.indexOf(k);
        if (index == -1) {
            return false;
        }
        this.removeChar(index);
        return true;
    }

    @Override
    public char set(int index, char k) {
        if (index >= this.size) {
            throw new IndexOutOfBoundsException("Index (" + index + ") is greater than or equal to list size (" + this.size + ")");
        }
        char old = this.a[index];
        this.a[index] = k;
        return old;
    }

    @Override
    public void clear() {
        this.size = 0;
    }

    @Override
    public int size() {
        return this.size;
    }

    @Override
    public void size(int size) {
        if (size > this.a.length) {
            this.ensureCapacity(size);
        }
        if (size > this.size) {
            java.util.Arrays.fill(this.a, this.size, size, '\u0000');
        }
        this.size = size;
    }

    @Override
    public boolean isEmpty() {
        return this.size == 0;
    }

    public void trim() {
        this.trim(0);
    }

    public void trim(int n) {
        if (n >= this.a.length || this.size == this.a.length) {
            return;
        }
        char[] t = new char[Math.max(n, this.size)];
        System.arraycopy(this.a, 0, t, 0, this.size);
        this.a = t;
    }

    @Override
    public void getElements(int from, char[] a, int offset, int length) {
        CharArrays.ensureOffsetLength(a, offset, length);
        System.arraycopy(this.a, from, a, offset, length);
    }

    @Override
    public void removeElements(int from, int to) {
        Arrays.ensureFromTo(this.size, from, to);
        System.arraycopy(this.a, to, this.a, from, this.size - to);
        this.size -= to - from;
    }

    @Override
    public void addElements(int index, char[] a, int offset, int length) {
        this.ensureIndex(index);
        CharArrays.ensureOffsetLength(a, offset, length);
        this.grow(this.size + length);
        System.arraycopy(this.a, index, this.a, index + length, this.size - index);
        System.arraycopy(a, offset, this.a, index, length);
        this.size += length;
    }

    @Override
    public char[] toArray(char[] a) {
        if (a == null || a.length < this.size) {
            a = new char[this.size];
        }
        System.arraycopy(this.a, 0, a, 0, this.size);
        return a;
    }

    @Override
    public boolean addAll(int index, CharCollection c) {
        this.ensureIndex(index);
        int n = c.size();
        if (n == 0) {
            return false;
        }
        this.grow(this.size + n);
        if (index != this.size) {
            System.arraycopy(this.a, index, this.a, index + n, this.size - index);
        }
        CharIterator i = c.iterator();
        this.size += n;
        while (n-- != 0) {
            this.a[index++] = i.nextChar();
        }
        return true;
    }

    @Override
    public boolean addAll(int index, CharList l) {
        this.ensureIndex(index);
        int n = l.size();
        if (n == 0) {
            return false;
        }
        this.grow(this.size + n);
        if (index != this.size) {
            System.arraycopy(this.a, index, this.a, index + n, this.size - index);
        }
        l.getElements(0, this.a, index, n);
        this.size += n;
        return true;
    }

    @Override
    public boolean removeAll(CharCollection c) {
        char[] a = this.a;
        int j = 0;
        for (int i = 0; i < this.size; ++i) {
            if (c.contains(a[i])) continue;
            a[j++] = a[i];
        }
        boolean modified = this.size != j;
        this.size = j;
        return modified;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        char[] a = this.a;
        int j = 0;
        for (int i = 0; i < this.size; ++i) {
            if (c.contains(Character.valueOf(a[i]))) continue;
            a[j++] = a[i];
        }
        boolean modified = this.size != j;
        this.size = j;
        return modified;
    }

    @Override
    public CharListIterator listIterator(final int index) {
        this.ensureIndex(index);
        return new CharListIterator(){
            int pos;
            int last;
            {
                this.pos = index;
                this.last = -1;
            }

            @Override
            public boolean hasNext() {
                return this.pos < CharArrayList.this.size;
            }

            @Override
            public boolean hasPrevious() {
                return this.pos > 0;
            }

            @Override
            public char nextChar() {
                if (!this.hasNext()) {
                    throw new NoSuchElementException();
                }
                this.last = this.pos++;
                return CharArrayList.this.a[this.last];
            }

            @Override
            public char previousChar() {
                if (!this.hasPrevious()) {
                    throw new NoSuchElementException();
                }
                this.last = --this.pos;
                return CharArrayList.this.a[this.pos];
            }

            @Override
            public int nextIndex() {
                return this.pos;
            }

            @Override
            public int previousIndex() {
                return this.pos - 1;
            }

            @Override
            public void add(char k) {
                CharArrayList.this.add(this.pos++, k);
                this.last = -1;
            }

            @Override
            public void set(char k) {
                if (this.last == -1) {
                    throw new IllegalStateException();
                }
                CharArrayList.this.set(this.last, k);
            }

            @Override
            public void remove() {
                if (this.last == -1) {
                    throw new IllegalStateException();
                }
                CharArrayList.this.removeChar(this.last);
                if (this.last < this.pos) {
                    --this.pos;
                }
                this.last = -1;
            }
        };
    }

    public CharArrayList clone() {
        CharArrayList c = new CharArrayList(this.size);
        System.arraycopy(this.a, 0, c.a, 0, this.size);
        c.size = this.size;
        return c;
    }

    public boolean equals(CharArrayList l) {
        if (l == this) {
            return true;
        }
        int s = this.size();
        if (s != l.size()) {
            return false;
        }
        char[] a1 = this.a;
        char[] a2 = l.a;
        while (s-- != 0) {
            if (a1[s] == a2[s]) continue;
            return false;
        }
        return true;
    }

    @Override
    public int compareTo(CharArrayList l) {
        int i;
        int s1 = this.size();
        int s2 = l.size();
        char[] a1 = this.a;
        char[] a2 = l.a;
        for (i = 0; i < s1 && i < s2; ++i) {
            char e1 = a1[i];
            char e2 = a2[i];
            int r = Character.compare(e1, e2);
            if (r == 0) continue;
            return r;
        }
        return i < s2 ? -1 : (i < s1 ? 1 : 0);
    }

    private void writeObject(ObjectOutputStream s) throws IOException {
        s.defaultWriteObject();
        for (int i = 0; i < this.size; ++i) {
            s.writeChar(this.a[i]);
        }
    }

    private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
        s.defaultReadObject();
        this.a = new char[this.size];
        for (int i = 0; i < this.size; ++i) {
            this.a[i] = s.readChar();
        }
    }

}

