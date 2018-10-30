/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.shorts;

import it.unimi.dsi.fastutil.Arrays;
import it.unimi.dsi.fastutil.shorts.AbstractShortList;
import it.unimi.dsi.fastutil.shorts.ShortArrays;
import it.unimi.dsi.fastutil.shorts.ShortCollection;
import it.unimi.dsi.fastutil.shorts.ShortIterator;
import it.unimi.dsi.fastutil.shorts.ShortIterators;
import it.unimi.dsi.fastutil.shorts.ShortList;
import it.unimi.dsi.fastutil.shorts.ShortListIterator;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.RandomAccess;

public class ShortArrayList
extends AbstractShortList
implements RandomAccess,
Cloneable,
Serializable {
    private static final long serialVersionUID = -7046029254386353130L;
    public static final int DEFAULT_INITIAL_CAPACITY = 16;
    protected transient short[] a;
    protected int size;
    private static final boolean ASSERTS = false;

    protected ShortArrayList(short[] a, boolean dummy) {
        this.a = a;
    }

    public ShortArrayList(int capacity) {
        if (capacity < 0) {
            throw new IllegalArgumentException("Initial capacity (" + capacity + ") is negative");
        }
        this.a = new short[capacity];
    }

    public ShortArrayList() {
        this(16);
    }

    public ShortArrayList(Collection<? extends Short> c) {
        this(c.size());
        this.size = ShortIterators.unwrap(ShortIterators.asShortIterator(c.iterator()), this.a);
    }

    public ShortArrayList(ShortCollection c) {
        this(c.size());
        this.size = ShortIterators.unwrap(c.iterator(), this.a);
    }

    public ShortArrayList(ShortList l) {
        this(l.size());
        this.size = l.size();
        l.getElements(0, this.a, 0, this.size);
    }

    public ShortArrayList(short[] a) {
        this(a, 0, a.length);
    }

    public ShortArrayList(short[] a, int offset, int length) {
        this(length);
        System.arraycopy(a, offset, this.a, 0, length);
        this.size = length;
    }

    public ShortArrayList(Iterator<? extends Short> i) {
        this();
        while (i.hasNext()) {
            this.add(i.next());
        }
    }

    public ShortArrayList(ShortIterator i) {
        this();
        while (i.hasNext()) {
            this.add(i.nextShort());
        }
    }

    public short[] elements() {
        return this.a;
    }

    public static ShortArrayList wrap(short[] a, int length) {
        if (length > a.length) {
            throw new IllegalArgumentException("The specified length (" + length + ") is greater than the array size (" + a.length + ")");
        }
        ShortArrayList l = new ShortArrayList(a, false);
        l.size = length;
        return l;
    }

    public static ShortArrayList wrap(short[] a) {
        return ShortArrayList.wrap(a, a.length);
    }

    public void ensureCapacity(int capacity) {
        this.a = ShortArrays.ensureCapacity(this.a, capacity, this.size);
    }

    private void grow(int capacity) {
        this.a = ShortArrays.grow(this.a, capacity, this.size);
    }

    @Override
    public void add(int index, short k) {
        this.ensureIndex(index);
        this.grow(this.size + 1);
        if (index != this.size) {
            System.arraycopy(this.a, index, this.a, index + 1, this.size - index);
        }
        this.a[index] = k;
        ++this.size;
    }

    @Override
    public boolean add(short k) {
        this.grow(this.size + 1);
        this.a[this.size++] = k;
        return true;
    }

    @Override
    public short getShort(int index) {
        if (index >= this.size) {
            throw new IndexOutOfBoundsException("Index (" + index + ") is greater than or equal to list size (" + this.size + ")");
        }
        return this.a[index];
    }

    @Override
    public int indexOf(short k) {
        for (int i = 0; i < this.size; ++i) {
            if (k != this.a[i]) continue;
            return i;
        }
        return -1;
    }

    @Override
    public int lastIndexOf(short k) {
        int i = this.size;
        while (i-- != 0) {
            if (k != this.a[i]) continue;
            return i;
        }
        return -1;
    }

    @Override
    public short removeShort(int index) {
        if (index >= this.size) {
            throw new IndexOutOfBoundsException("Index (" + index + ") is greater than or equal to list size (" + this.size + ")");
        }
        short old = this.a[index];
        --this.size;
        if (index != this.size) {
            System.arraycopy(this.a, index + 1, this.a, index, this.size - index);
        }
        return old;
    }

    @Override
    public boolean rem(short k) {
        int index = this.indexOf(k);
        if (index == -1) {
            return false;
        }
        this.removeShort(index);
        return true;
    }

    @Override
    public short set(int index, short k) {
        if (index >= this.size) {
            throw new IndexOutOfBoundsException("Index (" + index + ") is greater than or equal to list size (" + this.size + ")");
        }
        short old = this.a[index];
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
            java.util.Arrays.fill(this.a, this.size, size, (short)0);
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
        short[] t = new short[Math.max(n, this.size)];
        System.arraycopy(this.a, 0, t, 0, this.size);
        this.a = t;
    }

    @Override
    public void getElements(int from, short[] a, int offset, int length) {
        ShortArrays.ensureOffsetLength(a, offset, length);
        System.arraycopy(this.a, from, a, offset, length);
    }

    @Override
    public void removeElements(int from, int to) {
        Arrays.ensureFromTo(this.size, from, to);
        System.arraycopy(this.a, to, this.a, from, this.size - to);
        this.size -= to - from;
    }

    @Override
    public void addElements(int index, short[] a, int offset, int length) {
        this.ensureIndex(index);
        ShortArrays.ensureOffsetLength(a, offset, length);
        this.grow(this.size + length);
        System.arraycopy(this.a, index, this.a, index + length, this.size - index);
        System.arraycopy(a, offset, this.a, index, length);
        this.size += length;
    }

    @Override
    public short[] toArray(short[] a) {
        if (a == null || a.length < this.size) {
            a = new short[this.size];
        }
        System.arraycopy(this.a, 0, a, 0, this.size);
        return a;
    }

    @Override
    public boolean addAll(int index, ShortCollection c) {
        this.ensureIndex(index);
        int n = c.size();
        if (n == 0) {
            return false;
        }
        this.grow(this.size + n);
        if (index != this.size) {
            System.arraycopy(this.a, index, this.a, index + n, this.size - index);
        }
        ShortIterator i = c.iterator();
        this.size += n;
        while (n-- != 0) {
            this.a[index++] = i.nextShort();
        }
        return true;
    }

    @Override
    public boolean addAll(int index, ShortList l) {
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
    public boolean removeAll(ShortCollection c) {
        short[] a = this.a;
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
        short[] a = this.a;
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
    public ShortListIterator listIterator(final int index) {
        this.ensureIndex(index);
        return new ShortListIterator(){
            int pos;
            int last;
            {
                this.pos = index;
                this.last = -1;
            }

            @Override
            public boolean hasNext() {
                return this.pos < ShortArrayList.this.size;
            }

            @Override
            public boolean hasPrevious() {
                return this.pos > 0;
            }

            @Override
            public short nextShort() {
                if (!this.hasNext()) {
                    throw new NoSuchElementException();
                }
                this.last = this.pos++;
                return ShortArrayList.this.a[this.last];
            }

            @Override
            public short previousShort() {
                if (!this.hasPrevious()) {
                    throw new NoSuchElementException();
                }
                this.last = --this.pos;
                return ShortArrayList.this.a[this.pos];
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
            public void add(short k) {
                ShortArrayList.this.add(this.pos++, k);
                this.last = -1;
            }

            @Override
            public void set(short k) {
                if (this.last == -1) {
                    throw new IllegalStateException();
                }
                ShortArrayList.this.set(this.last, k);
            }

            @Override
            public void remove() {
                if (this.last == -1) {
                    throw new IllegalStateException();
                }
                ShortArrayList.this.removeShort(this.last);
                if (this.last < this.pos) {
                    --this.pos;
                }
                this.last = -1;
            }
        };
    }

    public ShortArrayList clone() {
        ShortArrayList c = new ShortArrayList(this.size);
        System.arraycopy(this.a, 0, c.a, 0, this.size);
        c.size = this.size;
        return c;
    }

    public boolean equals(ShortArrayList l) {
        if (l == this) {
            return true;
        }
        int s = this.size();
        if (s != l.size()) {
            return false;
        }
        short[] a1 = this.a;
        short[] a2 = l.a;
        while (s-- != 0) {
            if (a1[s] == a2[s]) continue;
            return false;
        }
        return true;
    }

    @Override
    public int compareTo(ShortArrayList l) {
        int i;
        int s1 = this.size();
        int s2 = l.size();
        short[] a1 = this.a;
        short[] a2 = l.a;
        for (i = 0; i < s1 && i < s2; ++i) {
            short e1 = a1[i];
            short e2 = a2[i];
            int r = Short.compare(e1, e2);
            if (r == 0) continue;
            return r;
        }
        return i < s2 ? -1 : (i < s1 ? 1 : 0);
    }

    private void writeObject(ObjectOutputStream s) throws IOException {
        s.defaultWriteObject();
        for (int i = 0; i < this.size; ++i) {
            s.writeShort(this.a[i]);
        }
    }

    private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
        s.defaultReadObject();
        this.a = new short[this.size];
        for (int i = 0; i < this.size; ++i) {
            this.a[i] = s.readShort();
        }
    }

}

