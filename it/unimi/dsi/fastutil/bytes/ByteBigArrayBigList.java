/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.BigArrays;
import it.unimi.dsi.fastutil.BigListIterator;
import it.unimi.dsi.fastutil.bytes.AbstractByteBigList;
import it.unimi.dsi.fastutil.bytes.ByteBigArrays;
import it.unimi.dsi.fastutil.bytes.ByteBigList;
import it.unimi.dsi.fastutil.bytes.ByteBigListIterator;
import it.unimi.dsi.fastutil.bytes.ByteCollection;
import it.unimi.dsi.fastutil.bytes.ByteIterator;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.RandomAccess;

public class ByteBigArrayBigList
extends AbstractByteBigList
implements RandomAccess,
Cloneable,
Serializable {
    private static final long serialVersionUID = -7046029254386353130L;
    public static final int DEFAULT_INITIAL_CAPACITY = 16;
    protected transient byte[][] a;
    protected long size;
    private static final boolean ASSERTS = false;

    protected ByteBigArrayBigList(byte[][] a, boolean dummy) {
        this.a = a;
    }

    public ByteBigArrayBigList(long capacity) {
        if (capacity < 0L) {
            throw new IllegalArgumentException("Initial capacity (" + capacity + ") is negative");
        }
        this.a = ByteBigArrays.newBigArray(capacity);
    }

    public ByteBigArrayBigList() {
        this(16L);
    }

    public ByteBigArrayBigList(ByteCollection c) {
        this(c.size());
        ByteIterator i = c.iterator();
        while (i.hasNext()) {
            this.add(i.nextByte());
        }
    }

    public ByteBigArrayBigList(ByteBigList l) {
        this(l.size64());
        this.size = l.size64();
        l.getElements(0L, this.a, 0L, this.size);
    }

    public ByteBigArrayBigList(byte[][] a) {
        this(a, 0L, ByteBigArrays.length(a));
    }

    public ByteBigArrayBigList(byte[][] a, long offset, long length) {
        this(length);
        ByteBigArrays.copy(a, offset, this.a, 0L, length);
        this.size = length;
    }

    public ByteBigArrayBigList(Iterator<? extends Byte> i) {
        this();
        while (i.hasNext()) {
            this.add(i.next());
        }
    }

    public ByteBigArrayBigList(ByteIterator i) {
        this();
        while (i.hasNext()) {
            this.add(i.nextByte());
        }
    }

    public byte[][] elements() {
        return this.a;
    }

    public static ByteBigArrayBigList wrap(byte[][] a, long length) {
        if (length > ByteBigArrays.length(a)) {
            throw new IllegalArgumentException("The specified length (" + length + ") is greater than the array size (" + ByteBigArrays.length(a) + ")");
        }
        ByteBigArrayBigList l = new ByteBigArrayBigList(a, false);
        l.size = length;
        return l;
    }

    public static ByteBigArrayBigList wrap(byte[][] a) {
        return ByteBigArrayBigList.wrap(a, ByteBigArrays.length(a));
    }

    public void ensureCapacity(long capacity) {
        this.a = ByteBigArrays.ensureCapacity(this.a, capacity, this.size);
    }

    private void grow(long capacity) {
        this.a = ByteBigArrays.grow(this.a, capacity, this.size);
    }

    @Override
    public void add(long index, byte k) {
        this.ensureIndex(index);
        this.grow(this.size + 1L);
        if (index != this.size) {
            ByteBigArrays.copy(this.a, index, this.a, index + 1L, this.size - index);
        }
        ByteBigArrays.set(this.a, index, k);
        ++this.size;
    }

    @Override
    public boolean add(byte k) {
        this.grow(this.size + 1L);
        ByteBigArrays.set(this.a, this.size++, k);
        return true;
    }

    @Override
    public byte getByte(long index) {
        if (index >= this.size) {
            throw new IndexOutOfBoundsException("Index (" + index + ") is greater than or equal to list size (" + this.size + ")");
        }
        return ByteBigArrays.get(this.a, index);
    }

    @Override
    public long indexOf(byte k) {
        for (long i = 0L; i < this.size; ++i) {
            if (k != ByteBigArrays.get(this.a, i)) continue;
            return i;
        }
        return -1L;
    }

    @Override
    public long lastIndexOf(byte k) {
        long i = this.size;
        while (i-- != 0L) {
            if (k != ByteBigArrays.get(this.a, i)) continue;
            return i;
        }
        return -1L;
    }

    @Override
    public byte removeByte(long index) {
        if (index >= this.size) {
            throw new IndexOutOfBoundsException("Index (" + index + ") is greater than or equal to list size (" + this.size + ")");
        }
        byte old = ByteBigArrays.get(this.a, index);
        --this.size;
        if (index != this.size) {
            ByteBigArrays.copy(this.a, index + 1L, this.a, index, this.size - index);
        }
        return old;
    }

    @Override
    public boolean rem(byte k) {
        long index = this.indexOf(k);
        if (index == -1L) {
            return false;
        }
        this.removeByte(index);
        return true;
    }

    @Override
    public byte set(long index, byte k) {
        if (index >= this.size) {
            throw new IndexOutOfBoundsException("Index (" + index + ") is greater than or equal to list size (" + this.size + ")");
        }
        byte old = ByteBigArrays.get(this.a, index);
        ByteBigArrays.set(this.a, index, k);
        return old;
    }

    @Override
    public boolean removeAll(ByteCollection c) {
        byte[] s = null;
        byte[] d = null;
        int ss = -1;
        int sd = 134217728;
        int ds = -1;
        int dd = 134217728;
        for (long i = 0L; i < this.size; ++i) {
            if (sd == 134217728) {
                sd = 0;
                s = this.a[++ss];
            }
            if (!c.contains((byte)s[sd])) {
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
        byte[] s = null;
        byte[] d = null;
        int ss = -1;
        int sd = 134217728;
        int ds = -1;
        int dd = 134217728;
        for (long i = 0L; i < this.size; ++i) {
            if (sd == 134217728) {
                sd = 0;
                s = this.a[++ss];
            }
            if (!c.contains((byte)s[sd])) {
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
        if (size > ByteBigArrays.length(this.a)) {
            this.ensureCapacity(size);
        }
        if (size > this.size) {
            ByteBigArrays.fill(this.a, this.size, size, (byte)0);
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
        long arrayLength = ByteBigArrays.length(this.a);
        if (n >= arrayLength || this.size == arrayLength) {
            return;
        }
        this.a = ByteBigArrays.trim(this.a, Math.max(n, this.size));
    }

    @Override
    public void getElements(long from, byte[][] a, long offset, long length) {
        ByteBigArrays.copy(this.a, from, a, offset, length);
    }

    @Override
    public void removeElements(long from, long to) {
        BigArrays.ensureFromTo(this.size, from, to);
        ByteBigArrays.copy(this.a, to, this.a, from, this.size - to);
        this.size -= to - from;
    }

    @Override
    public void addElements(long index, byte[][] a, long offset, long length) {
        this.ensureIndex(index);
        ByteBigArrays.ensureOffsetLength(a, offset, length);
        this.grow(this.size + length);
        ByteBigArrays.copy(this.a, index, this.a, index + length, this.size - index);
        ByteBigArrays.copy(a, offset, this.a, index, length);
        this.size += length;
    }

    @Override
    public ByteBigListIterator listIterator(final long index) {
        this.ensureIndex(index);
        return new ByteBigListIterator(){
            long pos;
            long last;
            {
                this.pos = index;
                this.last = -1L;
            }

            @Override
            public boolean hasNext() {
                return this.pos < ByteBigArrayBigList.this.size;
            }

            @Override
            public boolean hasPrevious() {
                return this.pos > 0L;
            }

            @Override
            public byte nextByte() {
                if (!this.hasNext()) {
                    throw new NoSuchElementException();
                }
                this.last = this.pos++;
                return ByteBigArrays.get(ByteBigArrayBigList.this.a, this.last);
            }

            @Override
            public byte previousByte() {
                if (!this.hasPrevious()) {
                    throw new NoSuchElementException();
                }
                this.last = --this.pos;
                return ByteBigArrays.get(ByteBigArrayBigList.this.a, this.pos);
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
            public void add(byte k) {
                ByteBigArrayBigList.this.add(this.pos++, k);
                this.last = -1L;
            }

            @Override
            public void set(byte k) {
                if (this.last == -1L) {
                    throw new IllegalStateException();
                }
                ByteBigArrayBigList.this.set(this.last, k);
            }

            @Override
            public void remove() {
                if (this.last == -1L) {
                    throw new IllegalStateException();
                }
                ByteBigArrayBigList.this.removeByte(this.last);
                if (this.last < this.pos) {
                    --this.pos;
                }
                this.last = -1L;
            }
        };
    }

    public ByteBigArrayBigList clone() {
        ByteBigArrayBigList c = new ByteBigArrayBigList(this.size);
        ByteBigArrays.copy(this.a, 0L, c.a, 0L, this.size);
        c.size = this.size;
        return c;
    }

    public boolean equals(ByteBigArrayBigList l) {
        if (l == this) {
            return true;
        }
        long s = this.size64();
        if (s != l.size64()) {
            return false;
        }
        byte[][] a1 = this.a;
        byte[][] a2 = l.a;
        while (s-- != 0L) {
            if (ByteBigArrays.get(a1, s) == ByteBigArrays.get(a2, s)) continue;
            return false;
        }
        return true;
    }

    @Override
    public int compareTo(ByteBigArrayBigList l) {
        long s1 = this.size64();
        long s2 = l.size64();
        byte[][] a1 = this.a;
        byte[][] a2 = l.a;
        int i = 0;
        while ((long)i < s1 && (long)i < s2) {
            byte e2;
            byte e1 = ByteBigArrays.get(a1, i);
            int r = Byte.compare(e1, e2 = ByteBigArrays.get(a2, i));
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
            s.writeByte(ByteBigArrays.get(this.a, i));
            ++i;
        }
    }

    private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
        s.defaultReadObject();
        this.a = ByteBigArrays.newBigArray(this.size);
        int i = 0;
        while ((long)i < this.size) {
            ByteBigArrays.set(this.a, i, s.readByte());
            ++i;
        }
    }

}

