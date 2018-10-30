/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.ints;

import it.unimi.dsi.fastutil.BigArrays;
import it.unimi.dsi.fastutil.BigListIterator;
import it.unimi.dsi.fastutil.ints.AbstractIntBigList;
import it.unimi.dsi.fastutil.ints.IntBigArrays;
import it.unimi.dsi.fastutil.ints.IntBigList;
import it.unimi.dsi.fastutil.ints.IntBigListIterator;
import it.unimi.dsi.fastutil.ints.IntCollection;
import it.unimi.dsi.fastutil.ints.IntIterator;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.RandomAccess;

public class IntBigArrayBigList
extends AbstractIntBigList
implements RandomAccess,
Cloneable,
Serializable {
    private static final long serialVersionUID = -7046029254386353130L;
    public static final int DEFAULT_INITIAL_CAPACITY = 16;
    protected transient int[][] a;
    protected long size;
    private static final boolean ASSERTS = false;

    protected IntBigArrayBigList(int[][] a, boolean dummy) {
        this.a = a;
    }

    public IntBigArrayBigList(long capacity) {
        if (capacity < 0L) {
            throw new IllegalArgumentException("Initial capacity (" + capacity + ") is negative");
        }
        this.a = IntBigArrays.newBigArray(capacity);
    }

    public IntBigArrayBigList() {
        this(16L);
    }

    public IntBigArrayBigList(IntCollection c) {
        this(c.size());
        IntIterator i = c.iterator();
        while (i.hasNext()) {
            this.add(i.nextInt());
        }
    }

    public IntBigArrayBigList(IntBigList l) {
        this(l.size64());
        this.size = l.size64();
        l.getElements(0L, this.a, 0L, this.size);
    }

    public IntBigArrayBigList(int[][] a) {
        this(a, 0L, IntBigArrays.length(a));
    }

    public IntBigArrayBigList(int[][] a, long offset, long length) {
        this(length);
        IntBigArrays.copy(a, offset, this.a, 0L, length);
        this.size = length;
    }

    public IntBigArrayBigList(Iterator<? extends Integer> i) {
        this();
        while (i.hasNext()) {
            this.add(i.next());
        }
    }

    public IntBigArrayBigList(IntIterator i) {
        this();
        while (i.hasNext()) {
            this.add(i.nextInt());
        }
    }

    public int[][] elements() {
        return this.a;
    }

    public static IntBigArrayBigList wrap(int[][] a, long length) {
        if (length > IntBigArrays.length(a)) {
            throw new IllegalArgumentException("The specified length (" + length + ") is greater than the array size (" + IntBigArrays.length(a) + ")");
        }
        IntBigArrayBigList l = new IntBigArrayBigList(a, false);
        l.size = length;
        return l;
    }

    public static IntBigArrayBigList wrap(int[][] a) {
        return IntBigArrayBigList.wrap(a, IntBigArrays.length(a));
    }

    public void ensureCapacity(long capacity) {
        this.a = IntBigArrays.ensureCapacity(this.a, capacity, this.size);
    }

    private void grow(long capacity) {
        this.a = IntBigArrays.grow(this.a, capacity, this.size);
    }

    @Override
    public void add(long index, int k) {
        this.ensureIndex(index);
        this.grow(this.size + 1L);
        if (index != this.size) {
            IntBigArrays.copy(this.a, index, this.a, index + 1L, this.size - index);
        }
        IntBigArrays.set(this.a, index, k);
        ++this.size;
    }

    @Override
    public boolean add(int k) {
        this.grow(this.size + 1L);
        IntBigArrays.set(this.a, this.size++, k);
        return true;
    }

    @Override
    public int getInt(long index) {
        if (index >= this.size) {
            throw new IndexOutOfBoundsException("Index (" + index + ") is greater than or equal to list size (" + this.size + ")");
        }
        return IntBigArrays.get(this.a, index);
    }

    @Override
    public long indexOf(int k) {
        for (long i = 0L; i < this.size; ++i) {
            if (k != IntBigArrays.get(this.a, i)) continue;
            return i;
        }
        return -1L;
    }

    @Override
    public long lastIndexOf(int k) {
        long i = this.size;
        while (i-- != 0L) {
            if (k != IntBigArrays.get(this.a, i)) continue;
            return i;
        }
        return -1L;
    }

    @Override
    public int removeInt(long index) {
        if (index >= this.size) {
            throw new IndexOutOfBoundsException("Index (" + index + ") is greater than or equal to list size (" + this.size + ")");
        }
        int old = IntBigArrays.get(this.a, index);
        --this.size;
        if (index != this.size) {
            IntBigArrays.copy(this.a, index + 1L, this.a, index, this.size - index);
        }
        return old;
    }

    @Override
    public boolean rem(int k) {
        long index = this.indexOf(k);
        if (index == -1L) {
            return false;
        }
        this.removeInt(index);
        return true;
    }

    @Override
    public int set(long index, int k) {
        if (index >= this.size) {
            throw new IndexOutOfBoundsException("Index (" + index + ") is greater than or equal to list size (" + this.size + ")");
        }
        int old = IntBigArrays.get(this.a, index);
        IntBigArrays.set(this.a, index, k);
        return old;
    }

    @Override
    public boolean removeAll(IntCollection c) {
        int[] s = null;
        int[] d = null;
        int ss = -1;
        int sd = 134217728;
        int ds = -1;
        int dd = 134217728;
        for (long i = 0L; i < this.size; ++i) {
            if (sd == 134217728) {
                sd = 0;
                s = this.a[++ss];
            }
            if (!c.contains((int)s[sd])) {
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
        int[] s = null;
        int[] d = null;
        int ss = -1;
        int sd = 134217728;
        int ds = -1;
        int dd = 134217728;
        for (long i = 0L; i < this.size; ++i) {
            if (sd == 134217728) {
                sd = 0;
                s = this.a[++ss];
            }
            if (!c.contains((int)s[sd])) {
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
        if (size > IntBigArrays.length(this.a)) {
            this.ensureCapacity(size);
        }
        if (size > this.size) {
            IntBigArrays.fill(this.a, this.size, size, 0);
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
        long arrayLength = IntBigArrays.length(this.a);
        if (n >= arrayLength || this.size == arrayLength) {
            return;
        }
        this.a = IntBigArrays.trim(this.a, Math.max(n, this.size));
    }

    @Override
    public void getElements(long from, int[][] a, long offset, long length) {
        IntBigArrays.copy(this.a, from, a, offset, length);
    }

    @Override
    public void removeElements(long from, long to) {
        BigArrays.ensureFromTo(this.size, from, to);
        IntBigArrays.copy(this.a, to, this.a, from, this.size - to);
        this.size -= to - from;
    }

    @Override
    public void addElements(long index, int[][] a, long offset, long length) {
        this.ensureIndex(index);
        IntBigArrays.ensureOffsetLength(a, offset, length);
        this.grow(this.size + length);
        IntBigArrays.copy(this.a, index, this.a, index + length, this.size - index);
        IntBigArrays.copy(a, offset, this.a, index, length);
        this.size += length;
    }

    @Override
    public IntBigListIterator listIterator(final long index) {
        this.ensureIndex(index);
        return new IntBigListIterator(){
            long pos;
            long last;
            {
                this.pos = index;
                this.last = -1L;
            }

            @Override
            public boolean hasNext() {
                return this.pos < IntBigArrayBigList.this.size;
            }

            @Override
            public boolean hasPrevious() {
                return this.pos > 0L;
            }

            @Override
            public int nextInt() {
                if (!this.hasNext()) {
                    throw new NoSuchElementException();
                }
                this.last = this.pos++;
                return IntBigArrays.get(IntBigArrayBigList.this.a, this.last);
            }

            @Override
            public int previousInt() {
                if (!this.hasPrevious()) {
                    throw new NoSuchElementException();
                }
                this.last = --this.pos;
                return IntBigArrays.get(IntBigArrayBigList.this.a, this.pos);
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
            public void add(int k) {
                IntBigArrayBigList.this.add(this.pos++, k);
                this.last = -1L;
            }

            @Override
            public void set(int k) {
                if (this.last == -1L) {
                    throw new IllegalStateException();
                }
                IntBigArrayBigList.this.set(this.last, k);
            }

            @Override
            public void remove() {
                if (this.last == -1L) {
                    throw new IllegalStateException();
                }
                IntBigArrayBigList.this.removeInt(this.last);
                if (this.last < this.pos) {
                    --this.pos;
                }
                this.last = -1L;
            }
        };
    }

    public IntBigArrayBigList clone() {
        IntBigArrayBigList c = new IntBigArrayBigList(this.size);
        IntBigArrays.copy(this.a, 0L, c.a, 0L, this.size);
        c.size = this.size;
        return c;
    }

    public boolean equals(IntBigArrayBigList l) {
        if (l == this) {
            return true;
        }
        long s = this.size64();
        if (s != l.size64()) {
            return false;
        }
        int[][] a1 = this.a;
        int[][] a2 = l.a;
        while (s-- != 0L) {
            if (IntBigArrays.get(a1, s) == IntBigArrays.get(a2, s)) continue;
            return false;
        }
        return true;
    }

    @Override
    public int compareTo(IntBigArrayBigList l) {
        long s1 = this.size64();
        long s2 = l.size64();
        int[][] a1 = this.a;
        int[][] a2 = l.a;
        int i = 0;
        while ((long)i < s1 && (long)i < s2) {
            int e2;
            int e1 = IntBigArrays.get(a1, i);
            int r = Integer.compare(e1, e2 = IntBigArrays.get(a2, i));
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
            s.writeInt(IntBigArrays.get(this.a, i));
            ++i;
        }
    }

    private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
        s.defaultReadObject();
        this.a = IntBigArrays.newBigArray(this.size);
        int i = 0;
        while ((long)i < this.size) {
            IntBigArrays.set(this.a, i, s.readInt());
            ++i;
        }
    }

}

