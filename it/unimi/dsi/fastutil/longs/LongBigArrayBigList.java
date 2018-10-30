/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.BigArrays;
import it.unimi.dsi.fastutil.BigListIterator;
import it.unimi.dsi.fastutil.longs.AbstractLongBigList;
import it.unimi.dsi.fastutil.longs.LongBigArrays;
import it.unimi.dsi.fastutil.longs.LongBigList;
import it.unimi.dsi.fastutil.longs.LongBigListIterator;
import it.unimi.dsi.fastutil.longs.LongCollection;
import it.unimi.dsi.fastutil.longs.LongIterator;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.RandomAccess;

public class LongBigArrayBigList
extends AbstractLongBigList
implements RandomAccess,
Cloneable,
Serializable {
    private static final long serialVersionUID = -7046029254386353130L;
    public static final int DEFAULT_INITIAL_CAPACITY = 16;
    protected transient long[][] a;
    protected long size;
    private static final boolean ASSERTS = false;

    protected LongBigArrayBigList(long[][] a, boolean dummy) {
        this.a = a;
    }

    public LongBigArrayBigList(long capacity) {
        if (capacity < 0L) {
            throw new IllegalArgumentException("Initial capacity (" + capacity + ") is negative");
        }
        this.a = LongBigArrays.newBigArray(capacity);
    }

    public LongBigArrayBigList() {
        this(16L);
    }

    public LongBigArrayBigList(LongCollection c) {
        this(c.size());
        LongIterator i = c.iterator();
        while (i.hasNext()) {
            this.add(i.nextLong());
        }
    }

    public LongBigArrayBigList(LongBigList l) {
        this(l.size64());
        this.size = l.size64();
        l.getElements(0L, this.a, 0L, this.size);
    }

    public LongBigArrayBigList(long[][] a) {
        this(a, 0L, LongBigArrays.length(a));
    }

    public LongBigArrayBigList(long[][] a, long offset, long length) {
        this(length);
        LongBigArrays.copy(a, offset, this.a, 0L, length);
        this.size = length;
    }

    public LongBigArrayBigList(Iterator<? extends Long> i) {
        this();
        while (i.hasNext()) {
            this.add(i.next());
        }
    }

    public LongBigArrayBigList(LongIterator i) {
        this();
        while (i.hasNext()) {
            this.add(i.nextLong());
        }
    }

    public long[][] elements() {
        return this.a;
    }

    public static LongBigArrayBigList wrap(long[][] a, long length) {
        if (length > LongBigArrays.length(a)) {
            throw new IllegalArgumentException("The specified length (" + length + ") is greater than the array size (" + LongBigArrays.length(a) + ")");
        }
        LongBigArrayBigList l = new LongBigArrayBigList(a, false);
        l.size = length;
        return l;
    }

    public static LongBigArrayBigList wrap(long[][] a) {
        return LongBigArrayBigList.wrap(a, LongBigArrays.length(a));
    }

    public void ensureCapacity(long capacity) {
        this.a = LongBigArrays.ensureCapacity(this.a, capacity, this.size);
    }

    private void grow(long capacity) {
        this.a = LongBigArrays.grow(this.a, capacity, this.size);
    }

    @Override
    public void add(long index, long k) {
        this.ensureIndex(index);
        this.grow(this.size + 1L);
        if (index != this.size) {
            LongBigArrays.copy(this.a, index, this.a, index + 1L, this.size - index);
        }
        LongBigArrays.set(this.a, index, k);
        ++this.size;
    }

    @Override
    public boolean add(long k) {
        this.grow(this.size + 1L);
        LongBigArrays.set(this.a, this.size++, k);
        return true;
    }

    @Override
    public long getLong(long index) {
        if (index >= this.size) {
            throw new IndexOutOfBoundsException("Index (" + index + ") is greater than or equal to list size (" + this.size + ")");
        }
        return LongBigArrays.get(this.a, index);
    }

    @Override
    public long indexOf(long k) {
        for (long i = 0L; i < this.size; ++i) {
            if (k != LongBigArrays.get(this.a, i)) continue;
            return i;
        }
        return -1L;
    }

    @Override
    public long lastIndexOf(long k) {
        long i = this.size;
        while (i-- != 0L) {
            if (k != LongBigArrays.get(this.a, i)) continue;
            return i;
        }
        return -1L;
    }

    @Override
    public long removeLong(long index) {
        if (index >= this.size) {
            throw new IndexOutOfBoundsException("Index (" + index + ") is greater than or equal to list size (" + this.size + ")");
        }
        long old = LongBigArrays.get(this.a, index);
        --this.size;
        if (index != this.size) {
            LongBigArrays.copy(this.a, index + 1L, this.a, index, this.size - index);
        }
        return old;
    }

    @Override
    public boolean rem(long k) {
        long index = this.indexOf(k);
        if (index == -1L) {
            return false;
        }
        this.removeLong(index);
        return true;
    }

    @Override
    public long set(long index, long k) {
        if (index >= this.size) {
            throw new IndexOutOfBoundsException("Index (" + index + ") is greater than or equal to list size (" + this.size + ")");
        }
        long old = LongBigArrays.get(this.a, index);
        LongBigArrays.set(this.a, index, k);
        return old;
    }

    @Override
    public boolean removeAll(LongCollection c) {
        long[] s = null;
        long[] d = null;
        int ss = -1;
        int sd = 134217728;
        int ds = -1;
        int dd = 134217728;
        for (long i = 0L; i < this.size; ++i) {
            if (sd == 134217728) {
                sd = 0;
                s = this.a[++ss];
            }
            if (!c.contains((long)s[sd])) {
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
        long[] s = null;
        long[] d = null;
        int ss = -1;
        int sd = 134217728;
        int ds = -1;
        int dd = 134217728;
        for (long i = 0L; i < this.size; ++i) {
            if (sd == 134217728) {
                sd = 0;
                s = this.a[++ss];
            }
            if (!c.contains((long)s[sd])) {
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
        if (size > LongBigArrays.length(this.a)) {
            this.ensureCapacity(size);
        }
        if (size > this.size) {
            LongBigArrays.fill(this.a, this.size, size, 0L);
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
        long arrayLength = LongBigArrays.length(this.a);
        if (n >= arrayLength || this.size == arrayLength) {
            return;
        }
        this.a = LongBigArrays.trim(this.a, Math.max(n, this.size));
    }

    @Override
    public void getElements(long from, long[][] a, long offset, long length) {
        LongBigArrays.copy(this.a, from, a, offset, length);
    }

    @Override
    public void removeElements(long from, long to) {
        BigArrays.ensureFromTo(this.size, from, to);
        LongBigArrays.copy(this.a, to, this.a, from, this.size - to);
        this.size -= to - from;
    }

    @Override
    public void addElements(long index, long[][] a, long offset, long length) {
        this.ensureIndex(index);
        LongBigArrays.ensureOffsetLength(a, offset, length);
        this.grow(this.size + length);
        LongBigArrays.copy(this.a, index, this.a, index + length, this.size - index);
        LongBigArrays.copy(a, offset, this.a, index, length);
        this.size += length;
    }

    @Override
    public LongBigListIterator listIterator(final long index) {
        this.ensureIndex(index);
        return new LongBigListIterator(){
            long pos;
            long last;
            {
                this.pos = index;
                this.last = -1L;
            }

            @Override
            public boolean hasNext() {
                return this.pos < LongBigArrayBigList.this.size;
            }

            @Override
            public boolean hasPrevious() {
                return this.pos > 0L;
            }

            @Override
            public long nextLong() {
                if (!this.hasNext()) {
                    throw new NoSuchElementException();
                }
                this.last = this.pos++;
                return LongBigArrays.get(LongBigArrayBigList.this.a, this.last);
            }

            @Override
            public long previousLong() {
                if (!this.hasPrevious()) {
                    throw new NoSuchElementException();
                }
                this.last = --this.pos;
                return LongBigArrays.get(LongBigArrayBigList.this.a, this.pos);
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
            public void add(long k) {
                LongBigArrayBigList.this.add(this.pos++, k);
                this.last = -1L;
            }

            @Override
            public void set(long k) {
                if (this.last == -1L) {
                    throw new IllegalStateException();
                }
                LongBigArrayBigList.this.set(this.last, k);
            }

            @Override
            public void remove() {
                if (this.last == -1L) {
                    throw new IllegalStateException();
                }
                LongBigArrayBigList.this.removeLong(this.last);
                if (this.last < this.pos) {
                    --this.pos;
                }
                this.last = -1L;
            }
        };
    }

    public LongBigArrayBigList clone() {
        LongBigArrayBigList c = new LongBigArrayBigList(this.size);
        LongBigArrays.copy(this.a, 0L, c.a, 0L, this.size);
        c.size = this.size;
        return c;
    }

    public boolean equals(LongBigArrayBigList l) {
        if (l == this) {
            return true;
        }
        long s = this.size64();
        if (s != l.size64()) {
            return false;
        }
        long[][] a1 = this.a;
        long[][] a2 = l.a;
        while (s-- != 0L) {
            if (LongBigArrays.get(a1, s) == LongBigArrays.get(a2, s)) continue;
            return false;
        }
        return true;
    }

    @Override
    public int compareTo(LongBigArrayBigList l) {
        long s1 = this.size64();
        long s2 = l.size64();
        long[][] a1 = this.a;
        long[][] a2 = l.a;
        int i = 0;
        while ((long)i < s1 && (long)i < s2) {
            long e2;
            long e1 = LongBigArrays.get(a1, i);
            int r = Long.compare(e1, e2 = LongBigArrays.get(a2, i));
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
            s.writeLong(LongBigArrays.get(this.a, i));
            ++i;
        }
    }

    private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
        s.defaultReadObject();
        this.a = LongBigArrays.newBigArray(this.size);
        int i = 0;
        while ((long)i < this.size) {
            LongBigArrays.set(this.a, i, s.readLong());
            ++i;
        }
    }

}

