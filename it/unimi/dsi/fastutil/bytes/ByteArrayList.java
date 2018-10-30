/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.Arrays;
import it.unimi.dsi.fastutil.bytes.AbstractByteList;
import it.unimi.dsi.fastutil.bytes.ByteArrays;
import it.unimi.dsi.fastutil.bytes.ByteCollection;
import it.unimi.dsi.fastutil.bytes.ByteIterator;
import it.unimi.dsi.fastutil.bytes.ByteIterators;
import it.unimi.dsi.fastutil.bytes.ByteList;
import it.unimi.dsi.fastutil.bytes.ByteListIterator;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.RandomAccess;

public class ByteArrayList
extends AbstractByteList
implements RandomAccess,
Cloneable,
Serializable {
    private static final long serialVersionUID = -7046029254386353130L;
    public static final int DEFAULT_INITIAL_CAPACITY = 16;
    protected transient byte[] a;
    protected int size;
    private static final boolean ASSERTS = false;

    protected ByteArrayList(byte[] a, boolean dummy) {
        this.a = a;
    }

    public ByteArrayList(int capacity) {
        if (capacity < 0) {
            throw new IllegalArgumentException("Initial capacity (" + capacity + ") is negative");
        }
        this.a = new byte[capacity];
    }

    public ByteArrayList() {
        this(16);
    }

    public ByteArrayList(Collection<? extends Byte> c) {
        this(c.size());
        this.size = ByteIterators.unwrap(ByteIterators.asByteIterator(c.iterator()), this.a);
    }

    public ByteArrayList(ByteCollection c) {
        this(c.size());
        this.size = ByteIterators.unwrap(c.iterator(), this.a);
    }

    public ByteArrayList(ByteList l) {
        this(l.size());
        this.size = l.size();
        l.getElements(0, this.a, 0, this.size);
    }

    public ByteArrayList(byte[] a) {
        this(a, 0, a.length);
    }

    public ByteArrayList(byte[] a, int offset, int length) {
        this(length);
        System.arraycopy(a, offset, this.a, 0, length);
        this.size = length;
    }

    public ByteArrayList(Iterator<? extends Byte> i) {
        this();
        while (i.hasNext()) {
            this.add(i.next());
        }
    }

    public ByteArrayList(ByteIterator i) {
        this();
        while (i.hasNext()) {
            this.add(i.nextByte());
        }
    }

    public byte[] elements() {
        return this.a;
    }

    public static ByteArrayList wrap(byte[] a, int length) {
        if (length > a.length) {
            throw new IllegalArgumentException("The specified length (" + length + ") is greater than the array size (" + a.length + ")");
        }
        ByteArrayList l = new ByteArrayList(a, false);
        l.size = length;
        return l;
    }

    public static ByteArrayList wrap(byte[] a) {
        return ByteArrayList.wrap(a, a.length);
    }

    public void ensureCapacity(int capacity) {
        this.a = ByteArrays.ensureCapacity(this.a, capacity, this.size);
    }

    private void grow(int capacity) {
        this.a = ByteArrays.grow(this.a, capacity, this.size);
    }

    @Override
    public void add(int index, byte k) {
        this.ensureIndex(index);
        this.grow(this.size + 1);
        if (index != this.size) {
            System.arraycopy(this.a, index, this.a, index + 1, this.size - index);
        }
        this.a[index] = k;
        ++this.size;
    }

    @Override
    public boolean add(byte k) {
        this.grow(this.size + 1);
        this.a[this.size++] = k;
        return true;
    }

    @Override
    public byte getByte(int index) {
        if (index >= this.size) {
            throw new IndexOutOfBoundsException("Index (" + index + ") is greater than or equal to list size (" + this.size + ")");
        }
        return this.a[index];
    }

    @Override
    public int indexOf(byte k) {
        for (int i = 0; i < this.size; ++i) {
            if (k != this.a[i]) continue;
            return i;
        }
        return -1;
    }

    @Override
    public int lastIndexOf(byte k) {
        int i = this.size;
        while (i-- != 0) {
            if (k != this.a[i]) continue;
            return i;
        }
        return -1;
    }

    @Override
    public byte removeByte(int index) {
        if (index >= this.size) {
            throw new IndexOutOfBoundsException("Index (" + index + ") is greater than or equal to list size (" + this.size + ")");
        }
        byte old = this.a[index];
        --this.size;
        if (index != this.size) {
            System.arraycopy(this.a, index + 1, this.a, index, this.size - index);
        }
        return old;
    }

    @Override
    public boolean rem(byte k) {
        int index = this.indexOf(k);
        if (index == -1) {
            return false;
        }
        this.removeByte(index);
        return true;
    }

    @Override
    public byte set(int index, byte k) {
        if (index >= this.size) {
            throw new IndexOutOfBoundsException("Index (" + index + ") is greater than or equal to list size (" + this.size + ")");
        }
        byte old = this.a[index];
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
            java.util.Arrays.fill(this.a, this.size, size, (byte)0);
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
        byte[] t = new byte[Math.max(n, this.size)];
        System.arraycopy(this.a, 0, t, 0, this.size);
        this.a = t;
    }

    @Override
    public void getElements(int from, byte[] a, int offset, int length) {
        ByteArrays.ensureOffsetLength(a, offset, length);
        System.arraycopy(this.a, from, a, offset, length);
    }

    @Override
    public void removeElements(int from, int to) {
        Arrays.ensureFromTo(this.size, from, to);
        System.arraycopy(this.a, to, this.a, from, this.size - to);
        this.size -= to - from;
    }

    @Override
    public void addElements(int index, byte[] a, int offset, int length) {
        this.ensureIndex(index);
        ByteArrays.ensureOffsetLength(a, offset, length);
        this.grow(this.size + length);
        System.arraycopy(this.a, index, this.a, index + length, this.size - index);
        System.arraycopy(a, offset, this.a, index, length);
        this.size += length;
    }

    @Override
    public byte[] toArray(byte[] a) {
        if (a == null || a.length < this.size) {
            a = new byte[this.size];
        }
        System.arraycopy(this.a, 0, a, 0, this.size);
        return a;
    }

    @Override
    public boolean addAll(int index, ByteCollection c) {
        this.ensureIndex(index);
        int n = c.size();
        if (n == 0) {
            return false;
        }
        this.grow(this.size + n);
        if (index != this.size) {
            System.arraycopy(this.a, index, this.a, index + n, this.size - index);
        }
        ByteIterator i = c.iterator();
        this.size += n;
        while (n-- != 0) {
            this.a[index++] = i.nextByte();
        }
        return true;
    }

    @Override
    public boolean addAll(int index, ByteList l) {
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
    public boolean removeAll(ByteCollection c) {
        byte[] a = this.a;
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
        byte[] a = this.a;
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
    public ByteListIterator listIterator(final int index) {
        this.ensureIndex(index);
        return new ByteListIterator(){
            int pos;
            int last;
            {
                this.pos = index;
                this.last = -1;
            }

            @Override
            public boolean hasNext() {
                return this.pos < ByteArrayList.this.size;
            }

            @Override
            public boolean hasPrevious() {
                return this.pos > 0;
            }

            @Override
            public byte nextByte() {
                if (!this.hasNext()) {
                    throw new NoSuchElementException();
                }
                this.last = this.pos++;
                return ByteArrayList.this.a[this.last];
            }

            @Override
            public byte previousByte() {
                if (!this.hasPrevious()) {
                    throw new NoSuchElementException();
                }
                this.last = --this.pos;
                return ByteArrayList.this.a[this.pos];
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
            public void add(byte k) {
                ByteArrayList.this.add(this.pos++, k);
                this.last = -1;
            }

            @Override
            public void set(byte k) {
                if (this.last == -1) {
                    throw new IllegalStateException();
                }
                ByteArrayList.this.set(this.last, k);
            }

            @Override
            public void remove() {
                if (this.last == -1) {
                    throw new IllegalStateException();
                }
                ByteArrayList.this.removeByte(this.last);
                if (this.last < this.pos) {
                    --this.pos;
                }
                this.last = -1;
            }
        };
    }

    public ByteArrayList clone() {
        ByteArrayList c = new ByteArrayList(this.size);
        System.arraycopy(this.a, 0, c.a, 0, this.size);
        c.size = this.size;
        return c;
    }

    public boolean equals(ByteArrayList l) {
        if (l == this) {
            return true;
        }
        int s = this.size();
        if (s != l.size()) {
            return false;
        }
        byte[] a1 = this.a;
        byte[] a2 = l.a;
        while (s-- != 0) {
            if (a1[s] == a2[s]) continue;
            return false;
        }
        return true;
    }

    @Override
    public int compareTo(ByteArrayList l) {
        int i;
        int s1 = this.size();
        int s2 = l.size();
        byte[] a1 = this.a;
        byte[] a2 = l.a;
        for (i = 0; i < s1 && i < s2; ++i) {
            byte e1 = a1[i];
            byte e2 = a2[i];
            int r = Byte.compare(e1, e2);
            if (r == 0) continue;
            return r;
        }
        return i < s2 ? -1 : (i < s1 ? 1 : 0);
    }

    private void writeObject(ObjectOutputStream s) throws IOException {
        s.defaultWriteObject();
        for (int i = 0; i < this.size; ++i) {
            s.writeByte(this.a[i]);
        }
    }

    private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
        s.defaultReadObject();
        this.a = new byte[this.size];
        for (int i = 0; i < this.size; ++i) {
            this.a[i] = s.readByte();
        }
    }

}

