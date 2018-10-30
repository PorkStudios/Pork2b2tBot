/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.doubles;

import it.unimi.dsi.fastutil.Arrays;
import it.unimi.dsi.fastutil.doubles.AbstractDoubleList;
import it.unimi.dsi.fastutil.doubles.DoubleArrays;
import it.unimi.dsi.fastutil.doubles.DoubleCollection;
import it.unimi.dsi.fastutil.doubles.DoubleIterator;
import it.unimi.dsi.fastutil.doubles.DoubleIterators;
import it.unimi.dsi.fastutil.doubles.DoubleList;
import it.unimi.dsi.fastutil.doubles.DoubleListIterator;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.RandomAccess;

public class DoubleArrayList
extends AbstractDoubleList
implements RandomAccess,
Cloneable,
Serializable {
    private static final long serialVersionUID = -7046029254386353130L;
    public static final int DEFAULT_INITIAL_CAPACITY = 16;
    protected transient double[] a;
    protected int size;
    private static final boolean ASSERTS = false;

    protected DoubleArrayList(double[] a, boolean dummy) {
        this.a = a;
    }

    public DoubleArrayList(int capacity) {
        if (capacity < 0) {
            throw new IllegalArgumentException("Initial capacity (" + capacity + ") is negative");
        }
        this.a = new double[capacity];
    }

    public DoubleArrayList() {
        this(16);
    }

    public DoubleArrayList(Collection<? extends Double> c) {
        this(c.size());
        this.size = DoubleIterators.unwrap(DoubleIterators.asDoubleIterator(c.iterator()), this.a);
    }

    public DoubleArrayList(DoubleCollection c) {
        this(c.size());
        this.size = DoubleIterators.unwrap(c.iterator(), this.a);
    }

    public DoubleArrayList(DoubleList l) {
        this(l.size());
        this.size = l.size();
        l.getElements(0, this.a, 0, this.size);
    }

    public DoubleArrayList(double[] a) {
        this(a, 0, a.length);
    }

    public DoubleArrayList(double[] a, int offset, int length) {
        this(length);
        System.arraycopy(a, offset, this.a, 0, length);
        this.size = length;
    }

    public DoubleArrayList(Iterator<? extends Double> i) {
        this();
        while (i.hasNext()) {
            this.add(i.next());
        }
    }

    public DoubleArrayList(DoubleIterator i) {
        this();
        while (i.hasNext()) {
            this.add(i.nextDouble());
        }
    }

    public double[] elements() {
        return this.a;
    }

    public static DoubleArrayList wrap(double[] a, int length) {
        if (length > a.length) {
            throw new IllegalArgumentException("The specified length (" + length + ") is greater than the array size (" + a.length + ")");
        }
        DoubleArrayList l = new DoubleArrayList(a, false);
        l.size = length;
        return l;
    }

    public static DoubleArrayList wrap(double[] a) {
        return DoubleArrayList.wrap(a, a.length);
    }

    public void ensureCapacity(int capacity) {
        this.a = DoubleArrays.ensureCapacity(this.a, capacity, this.size);
    }

    private void grow(int capacity) {
        this.a = DoubleArrays.grow(this.a, capacity, this.size);
    }

    @Override
    public void add(int index, double k) {
        this.ensureIndex(index);
        this.grow(this.size + 1);
        if (index != this.size) {
            System.arraycopy(this.a, index, this.a, index + 1, this.size - index);
        }
        this.a[index] = k;
        ++this.size;
    }

    @Override
    public boolean add(double k) {
        this.grow(this.size + 1);
        this.a[this.size++] = k;
        return true;
    }

    @Override
    public double getDouble(int index) {
        if (index >= this.size) {
            throw new IndexOutOfBoundsException("Index (" + index + ") is greater than or equal to list size (" + this.size + ")");
        }
        return this.a[index];
    }

    @Override
    public int indexOf(double k) {
        for (int i = 0; i < this.size; ++i) {
            if (Double.doubleToLongBits(k) != Double.doubleToLongBits(this.a[i])) continue;
            return i;
        }
        return -1;
    }

    @Override
    public int lastIndexOf(double k) {
        int i = this.size;
        while (i-- != 0) {
            if (Double.doubleToLongBits(k) != Double.doubleToLongBits(this.a[i])) continue;
            return i;
        }
        return -1;
    }

    @Override
    public double removeDouble(int index) {
        if (index >= this.size) {
            throw new IndexOutOfBoundsException("Index (" + index + ") is greater than or equal to list size (" + this.size + ")");
        }
        double old = this.a[index];
        --this.size;
        if (index != this.size) {
            System.arraycopy(this.a, index + 1, this.a, index, this.size - index);
        }
        return old;
    }

    @Override
    public boolean rem(double k) {
        int index = this.indexOf(k);
        if (index == -1) {
            return false;
        }
        this.removeDouble(index);
        return true;
    }

    @Override
    public double set(int index, double k) {
        if (index >= this.size) {
            throw new IndexOutOfBoundsException("Index (" + index + ") is greater than or equal to list size (" + this.size + ")");
        }
        double old = this.a[index];
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
            java.util.Arrays.fill(this.a, this.size, size, 0.0);
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
        double[] t = new double[Math.max(n, this.size)];
        System.arraycopy(this.a, 0, t, 0, this.size);
        this.a = t;
    }

    @Override
    public void getElements(int from, double[] a, int offset, int length) {
        DoubleArrays.ensureOffsetLength(a, offset, length);
        System.arraycopy(this.a, from, a, offset, length);
    }

    @Override
    public void removeElements(int from, int to) {
        Arrays.ensureFromTo(this.size, from, to);
        System.arraycopy(this.a, to, this.a, from, this.size - to);
        this.size -= to - from;
    }

    @Override
    public void addElements(int index, double[] a, int offset, int length) {
        this.ensureIndex(index);
        DoubleArrays.ensureOffsetLength(a, offset, length);
        this.grow(this.size + length);
        System.arraycopy(this.a, index, this.a, index + length, this.size - index);
        System.arraycopy(a, offset, this.a, index, length);
        this.size += length;
    }

    @Override
    public double[] toArray(double[] a) {
        if (a == null || a.length < this.size) {
            a = new double[this.size];
        }
        System.arraycopy(this.a, 0, a, 0, this.size);
        return a;
    }

    @Override
    public boolean addAll(int index, DoubleCollection c) {
        this.ensureIndex(index);
        int n = c.size();
        if (n == 0) {
            return false;
        }
        this.grow(this.size + n);
        if (index != this.size) {
            System.arraycopy(this.a, index, this.a, index + n, this.size - index);
        }
        DoubleIterator i = c.iterator();
        this.size += n;
        while (n-- != 0) {
            this.a[index++] = i.nextDouble();
        }
        return true;
    }

    @Override
    public boolean addAll(int index, DoubleList l) {
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
    public boolean removeAll(DoubleCollection c) {
        double[] a = this.a;
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
        double[] a = this.a;
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
    public DoubleListIterator listIterator(final int index) {
        this.ensureIndex(index);
        return new DoubleListIterator(){
            int pos;
            int last;
            {
                this.pos = index;
                this.last = -1;
            }

            @Override
            public boolean hasNext() {
                return this.pos < DoubleArrayList.this.size;
            }

            @Override
            public boolean hasPrevious() {
                return this.pos > 0;
            }

            @Override
            public double nextDouble() {
                if (!this.hasNext()) {
                    throw new NoSuchElementException();
                }
                this.last = this.pos++;
                return DoubleArrayList.this.a[this.last];
            }

            @Override
            public double previousDouble() {
                if (!this.hasPrevious()) {
                    throw new NoSuchElementException();
                }
                this.last = --this.pos;
                return DoubleArrayList.this.a[this.pos];
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
            public void add(double k) {
                DoubleArrayList.this.add(this.pos++, k);
                this.last = -1;
            }

            @Override
            public void set(double k) {
                if (this.last == -1) {
                    throw new IllegalStateException();
                }
                DoubleArrayList.this.set(this.last, k);
            }

            @Override
            public void remove() {
                if (this.last == -1) {
                    throw new IllegalStateException();
                }
                DoubleArrayList.this.removeDouble(this.last);
                if (this.last < this.pos) {
                    --this.pos;
                }
                this.last = -1;
            }
        };
    }

    public DoubleArrayList clone() {
        DoubleArrayList c = new DoubleArrayList(this.size);
        System.arraycopy(this.a, 0, c.a, 0, this.size);
        c.size = this.size;
        return c;
    }

    public boolean equals(DoubleArrayList l) {
        if (l == this) {
            return true;
        }
        int s = this.size();
        if (s != l.size()) {
            return false;
        }
        double[] a1 = this.a;
        double[] a2 = l.a;
        while (s-- != 0) {
            if (a1[s] == a2[s]) continue;
            return false;
        }
        return true;
    }

    @Override
    public int compareTo(DoubleArrayList l) {
        int i;
        int s1 = this.size();
        int s2 = l.size();
        double[] a1 = this.a;
        double[] a2 = l.a;
        for (i = 0; i < s1 && i < s2; ++i) {
            double e1 = a1[i];
            double e2 = a2[i];
            int r = Double.compare(e1, e2);
            if (r == 0) continue;
            return r;
        }
        return i < s2 ? -1 : (i < s1 ? 1 : 0);
    }

    private void writeObject(ObjectOutputStream s) throws IOException {
        s.defaultWriteObject();
        for (int i = 0; i < this.size; ++i) {
            s.writeDouble(this.a[i]);
        }
    }

    private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
        s.defaultReadObject();
        this.a = new double[this.size];
        for (int i = 0; i < this.size; ++i) {
            this.a[i] = s.readDouble();
        }
    }

}

