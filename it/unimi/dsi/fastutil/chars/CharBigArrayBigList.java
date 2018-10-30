/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.BigArrays;
import it.unimi.dsi.fastutil.BigListIterator;
import it.unimi.dsi.fastutil.chars.AbstractCharBigList;
import it.unimi.dsi.fastutil.chars.CharBigArrays;
import it.unimi.dsi.fastutil.chars.CharBigList;
import it.unimi.dsi.fastutil.chars.CharBigListIterator;
import it.unimi.dsi.fastutil.chars.CharCollection;
import it.unimi.dsi.fastutil.chars.CharIterator;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.RandomAccess;

public class CharBigArrayBigList
extends AbstractCharBigList
implements RandomAccess,
Cloneable,
Serializable {
    private static final long serialVersionUID = -7046029254386353130L;
    public static final int DEFAULT_INITIAL_CAPACITY = 16;
    protected transient char[][] a;
    protected long size;
    private static final boolean ASSERTS = false;

    protected CharBigArrayBigList(char[][] a, boolean dummy) {
        this.a = a;
    }

    public CharBigArrayBigList(long capacity) {
        if (capacity < 0L) {
            throw new IllegalArgumentException("Initial capacity (" + capacity + ") is negative");
        }
        this.a = CharBigArrays.newBigArray(capacity);
    }

    public CharBigArrayBigList() {
        this(16L);
    }

    public CharBigArrayBigList(CharCollection c) {
        this(c.size());
        CharIterator i = c.iterator();
        while (i.hasNext()) {
            this.add(i.nextChar());
        }
    }

    public CharBigArrayBigList(CharBigList l) {
        this(l.size64());
        this.size = l.size64();
        l.getElements(0L, this.a, 0L, this.size);
    }

    public CharBigArrayBigList(char[][] a) {
        this(a, 0L, CharBigArrays.length(a));
    }

    public CharBigArrayBigList(char[][] a, long offset, long length) {
        this(length);
        CharBigArrays.copy(a, offset, this.a, 0L, length);
        this.size = length;
    }

    public CharBigArrayBigList(Iterator<? extends Character> i) {
        this();
        while (i.hasNext()) {
            this.add(i.next().charValue());
        }
    }

    public CharBigArrayBigList(CharIterator i) {
        this();
        while (i.hasNext()) {
            this.add(i.nextChar());
        }
    }

    public char[][] elements() {
        return this.a;
    }

    public static CharBigArrayBigList wrap(char[][] a, long length) {
        if (length > CharBigArrays.length(a)) {
            throw new IllegalArgumentException("The specified length (" + length + ") is greater than the array size (" + CharBigArrays.length(a) + ")");
        }
        CharBigArrayBigList l = new CharBigArrayBigList(a, false);
        l.size = length;
        return l;
    }

    public static CharBigArrayBigList wrap(char[][] a) {
        return CharBigArrayBigList.wrap(a, CharBigArrays.length(a));
    }

    public void ensureCapacity(long capacity) {
        this.a = CharBigArrays.ensureCapacity(this.a, capacity, this.size);
    }

    private void grow(long capacity) {
        this.a = CharBigArrays.grow(this.a, capacity, this.size);
    }

    @Override
    public void add(long index, char k) {
        this.ensureIndex(index);
        this.grow(this.size + 1L);
        if (index != this.size) {
            CharBigArrays.copy(this.a, index, this.a, index + 1L, this.size - index);
        }
        CharBigArrays.set(this.a, index, k);
        ++this.size;
    }

    @Override
    public boolean add(char k) {
        this.grow(this.size + 1L);
        CharBigArrays.set(this.a, this.size++, k);
        return true;
    }

    @Override
    public char getChar(long index) {
        if (index >= this.size) {
            throw new IndexOutOfBoundsException("Index (" + index + ") is greater than or equal to list size (" + this.size + ")");
        }
        return CharBigArrays.get(this.a, index);
    }

    @Override
    public long indexOf(char k) {
        for (long i = 0L; i < this.size; ++i) {
            if (k != CharBigArrays.get(this.a, i)) continue;
            return i;
        }
        return -1L;
    }

    @Override
    public long lastIndexOf(char k) {
        long i = this.size;
        while (i-- != 0L) {
            if (k != CharBigArrays.get(this.a, i)) continue;
            return i;
        }
        return -1L;
    }

    @Override
    public char removeChar(long index) {
        if (index >= this.size) {
            throw new IndexOutOfBoundsException("Index (" + index + ") is greater than or equal to list size (" + this.size + ")");
        }
        char old = CharBigArrays.get(this.a, index);
        --this.size;
        if (index != this.size) {
            CharBigArrays.copy(this.a, index + 1L, this.a, index, this.size - index);
        }
        return old;
    }

    @Override
    public boolean rem(char k) {
        long index = this.indexOf(k);
        if (index == -1L) {
            return false;
        }
        this.removeChar(index);
        return true;
    }

    @Override
    public char set(long index, char k) {
        if (index >= this.size) {
            throw new IndexOutOfBoundsException("Index (" + index + ") is greater than or equal to list size (" + this.size + ")");
        }
        char old = CharBigArrays.get(this.a, index);
        CharBigArrays.set(this.a, index, k);
        return old;
    }

    @Override
    public boolean removeAll(CharCollection c) {
        char[] s = null;
        char[] d = null;
        int ss = -1;
        int sd = 134217728;
        int ds = -1;
        int dd = 134217728;
        for (long i = 0L; i < this.size; ++i) {
            if (sd == 134217728) {
                sd = 0;
                s = this.a[++ss];
            }
            if (!c.contains((char)s[sd])) {
                if (dd == 134217728) {
                    d = this.a[++ds];
                    dd = 0;
                }
                d[dd++] = s[sd];
            }
            ++sd;
        }
        long j = BigArrays.index(ds, dd);
        boolean modified = this.size != j;
        this.size = j;
        return modified;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        char[] s = null;
        char[] d = null;
        int ss = -1;
        int sd = 134217728;
        int ds = -1;
        int dd = 134217728;
        for (long i = 0L; i < this.size; ++i) {
            if (sd == 134217728) {
                sd = 0;
                s = this.a[++ss];
            }
            if (!c.contains(Character.valueOf((char)s[sd]))) {
                if (dd == 134217728) {
                    d = this.a[++ds];
                    dd = 0;
                }
                d[dd++] = s[sd];
            }
            ++sd;
        }
        long j = BigArrays.index(ds, dd);
        boolean modified = this.size != j;
        this.size = j;
        return modified;
    }

    @Override
    public void clear() {
        this.size = 0L;
    }

    @Override
    public long size64() {
        return this.size;
    }

    @Override
    public void size(long size) {
        if (size > CharBigArrays.length(this.a)) {
            this.ensureCapacity(size);
        }
        if (size > this.size) {
            CharBigArrays.fill(this.a, this.size, size, '\u0000');
        }
        this.size = size;
    }

    @Override
    public boolean isEmpty() {
        return this.size == 0L;
    }

    public void trim() {
        this.trim(0L);
    }

    public void trim(long n) {
        long arrayLength = CharBigArrays.length(this.a);
        if (n >= arrayLength || this.size == arrayLength) {
            return;
        }
        this.a = CharBigArrays.trim(this.a, Math.max(n, this.size));
    }

    @Override
    public void getElements(long from, char[][] a, long offset, long length) {
        CharBigArrays.copy(this.a, from, a, offset, length);
    }

    @Override
    public void removeElements(long from, long to) {
        BigArrays.ensureFromTo(this.size, from, to);
        CharBigArrays.copy(this.a, to, this.a, from, this.size - to);
        this.size -= to - from;
    }

    @Override
    public void addElements(long index, char[][] a, long offset, long length) {
        this.ensureIndex(index);
        CharBigArrays.ensureOffsetLength(a, offset, length);
        this.grow(this.size + length);
        CharBigArrays.copy(this.a, index, this.a, index + length, this.size - index);
        CharBigArrays.copy(a, offset, this.a, index, length);
        this.size += length;
    }

    @Override
    public CharBigListIterator listIterator(final long index) {
        this.ensureIndex(index);
        return new CharBigListIterator(){
            long pos;
            long last;
            {
                this.pos = index;
                this.last = -1L;
            }

            @Override
            public boolean hasNext() {
                return this.pos < CharBigArrayBigList.this.size;
            }

            @Override
            public boolean hasPrevious() {
                return this.pos > 0L;
            }

            @Override
            public char nextChar() {
                if (!this.hasNext()) {
                    throw new NoSuchElementException();
                }
                this.last = this.pos++;
                return CharBigArrays.get(CharBigArrayBigList.this.a, this.last);
            }

            @Override
            public char previousChar() {
                if (!this.hasPrevious()) {
                    throw new NoSuchElementException();
                }
                this.last = --this.pos;
                return CharBigArrays.get(CharBigArrayBigList.this.a, this.pos);
            }

            @Override
            public long nextIndex() {
                return this.pos;
            }

            @Override
            public long previousIndex() {
                return this.pos - 1L;
            }

            @Override
            public void add(char k) {
                CharBigArrayBigList.this.add(this.pos++, k);
                this.last = -1L;
            }

            @Override
            public void set(char k) {
                if (this.last == -1L) {
                    throw new IllegalStateException();
                }
                CharBigArrayBigList.this.set(this.last, k);
            }

            @Override
            public void remove() {
                if (this.last == -1L) {
                    throw new IllegalStateException();
                }
                CharBigArrayBigList.this.removeChar(this.last);
                if (this.last < this.pos) {
                    --this.pos;
                }
                this.last = -1L;
            }
        };
    }

    public CharBigArrayBigList clone() {
        CharBigArrayBigList c = new CharBigArrayBigList(this.size);
        CharBigArrays.copy(this.a, 0L, c.a, 0L, this.size);
        c.size = this.size;
        return c;
    }

    public boolean equals(CharBigArrayBigList l) {
        if (l == this) {
            return true;
        }
        long s = this.size64();
        if (s != l.size64()) {
            return false;
        }
        char[][] a1 = this.a;
        char[][] a2 = l.a;
        while (s-- != 0L) {
            if (CharBigArrays.get(a1, s) == CharBigArrays.get(a2, s)) continue;
            return false;
        }
        return true;
    }

    @Override
    public int compareTo(CharBigArrayBigList l) {
        long s1 = this.size64();
        long s2 = l.size64();
        char[][] a1 = this.a;
        char[][] a2 = l.a;
        int i = 0;
        while ((long)i < s1 && (long)i < s2) {
            char e2;
            char e1 = CharBigArrays.get(a1, i);
            int r = Character.compare(e1, e2 = CharBigArrays.get(a2, i));
            if (r != 0) {
                return r;
            }
            ++i;
        }
        return (long)i < s2 ? -1 : ((long)i < s1 ? 1 : 0);
    }

    private void writeObject(ObjectOutputStream s) throws IOException {
        s.defaultWriteObject();
        int i = 0;
        while ((long)i < this.size) {
            s.writeChar(CharBigArrays.get(this.a, i));
            ++i;
        }
    }

    private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
        s.defaultReadObject();
        this.a = CharBigArrays.newBigArray(this.size);
        int i = 0;
        while ((long)i < this.size) {
            CharBigArrays.set(this.a, i, s.readChar());
            ++i;
        }
    }

}

