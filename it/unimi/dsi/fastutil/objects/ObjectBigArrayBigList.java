/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.BigArrays;
import it.unimi.dsi.fastutil.BigListIterator;
import it.unimi.dsi.fastutil.objects.AbstractObjectBigList;
import it.unimi.dsi.fastutil.objects.ObjectBigArrays;
import it.unimi.dsi.fastutil.objects.ObjectBigList;
import it.unimi.dsi.fastutil.objects.ObjectBigListIterator;
import it.unimi.dsi.fastutil.objects.ObjectCollection;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.RandomAccess;

public class ObjectBigArrayBigList<K>
extends AbstractObjectBigList<K>
implements RandomAccess,
Cloneable,
Serializable {
    private static final long serialVersionUID = -7046029254386353131L;
    public static final int DEFAULT_INITIAL_CAPACITY = 16;
    protected final boolean wrapped;
    protected transient K[][] a;
    protected long size;
    private static final boolean ASSERTS = false;

    protected ObjectBigArrayBigList(K[][] a, boolean dummy) {
        this.a = a;
        this.wrapped = true;
    }

    public ObjectBigArrayBigList(long capacity) {
        if (capacity < 0L) {
            throw new IllegalArgumentException("Initial capacity (" + capacity + ") is negative");
        }
        this.a = ObjectBigArrays.newBigArray(capacity);
        this.wrapped = false;
    }

    public ObjectBigArrayBigList() {
        this(16L);
    }

    public ObjectBigArrayBigList(ObjectCollection<? extends K> c) {
        this(c.size());
        Iterator i = c.iterator();
        while (i.hasNext()) {
            this.add(i.next());
        }
    }

    public ObjectBigArrayBigList(ObjectBigList<? extends K> l) {
        this(l.size64());
        this.size = l.size64();
        l.getElements(0L, this.a, 0L, this.size);
    }

    public ObjectBigArrayBigList(K[][] a) {
        this(a, 0L, ObjectBigArrays.length(a));
    }

    public ObjectBigArrayBigList(K[][] a, long offset, long length) {
        this(length);
        ObjectBigArrays.copy(a, offset, this.a, 0L, length);
        this.size = length;
    }

    public ObjectBigArrayBigList(Iterator<? extends K> i) {
        this();
        while (i.hasNext()) {
            this.add(i.next());
        }
    }

    public ObjectBigArrayBigList(ObjectIterator<? extends K> i) {
        this();
        while (i.hasNext()) {
            this.add(i.next());
        }
    }

    public K[][] elements() {
        return this.a;
    }

    public static <K> ObjectBigArrayBigList<K> wrap(K[][] a, long length) {
        if (length > ObjectBigArrays.length(a)) {
            throw new IllegalArgumentException("The specified length (" + length + ") is greater than the array size (" + ObjectBigArrays.length(a) + ")");
        }
        ObjectBigArrayBigList<K> l = new ObjectBigArrayBigList<K>(a, false);
        l.size = length;
        return l;
    }

    public static <K> ObjectBigArrayBigList<K> wrap(K[][] a) {
        return ObjectBigArrayBigList.wrap(a, ObjectBigArrays.length(a));
    }

    public void ensureCapacity(long capacity) {
        if (this.wrapped) {
            this.a = ObjectBigArrays.ensureCapacity(this.a, capacity, this.size);
        } else if (capacity > ObjectBigArrays.length(this.a)) {
            Object[][] t = ObjectBigArrays.newBigArray(capacity);
            ObjectBigArrays.copy(this.a, 0L, t, 0L, this.size);
            this.a = t;
        }
    }

    private void grow(long capacity) {
        if (this.wrapped) {
            this.a = ObjectBigArrays.grow(this.a, capacity, this.size);
        } else if (capacity > ObjectBigArrays.length(this.a)) {
            int newLength = (int)Math.max(Math.min(2L * ObjectBigArrays.length(this.a), 0x7FFFFFF7L), capacity);
            Object[][] t = ObjectBigArrays.newBigArray(newLength);
            ObjectBigArrays.copy(this.a, 0L, t, 0L, this.size);
            this.a = t;
        }
    }

    @Override
    public void add(long index, K k) {
        this.ensureIndex(index);
        this.grow(this.size + 1L);
        if (index != this.size) {
            ObjectBigArrays.copy(this.a, index, this.a, index + 1L, this.size - index);
        }
        ObjectBigArrays.set(this.a, index, k);
        ++this.size;
    }

    @Override
    public boolean add(K k) {
        this.grow(this.size + 1L);
        ObjectBigArrays.set(this.a, this.size++, k);
        return true;
    }

    @Override
    public K get(long index) {
        if (index >= this.size) {
            throw new IndexOutOfBoundsException("Index (" + index + ") is greater than or equal to list size (" + this.size + ")");
        }
        return ObjectBigArrays.get(this.a, index);
    }

    @Override
    public long indexOf(Object k) {
        for (long i = 0L; i < this.size; ++i) {
            if (!Objects.equals(k, ObjectBigArrays.get(this.a, i))) continue;
            return i;
        }
        return -1L;
    }

    @Override
    public long lastIndexOf(Object k) {
        long i = this.size;
        while (i-- != 0L) {
            if (!Objects.equals(k, ObjectBigArrays.get(this.a, i))) continue;
            return i;
        }
        return -1L;
    }

    @Override
    public K remove(long index) {
        if (index >= this.size) {
            throw new IndexOutOfBoundsException("Index (" + index + ") is greater than or equal to list size (" + this.size + ")");
        }
        K old = ObjectBigArrays.get(this.a, index);
        --this.size;
        if (index != this.size) {
            ObjectBigArrays.copy(this.a, index + 1L, this.a, index, this.size - index);
        }
        ObjectBigArrays.set(this.a, this.size, null);
        return old;
    }

    @Override
    public boolean remove(Object k) {
        long index = this.indexOf(k);
        if (index == -1L) {
            return false;
        }
        this.remove(index);
        return true;
    }

    @Override
    public K set(long index, K k) {
        if (index >= this.size) {
            throw new IndexOutOfBoundsException("Index (" + index + ") is greater than or equal to list size (" + this.size + ")");
        }
        K old = ObjectBigArrays.get(this.a, index);
        ObjectBigArrays.set(this.a, index, k);
        return old;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        K[] s = null;
        K[] d = null;
        int ss = -1;
        int sd = 134217728;
        int ds = -1;
        int dd = 134217728;
        for (long i = 0L; i < this.size; ++i) {
            if (sd == 134217728) {
                sd = 0;
                s = this.a[++ss];
            }
            if (!c.contains(s[sd])) {
                if (dd == 134217728) {
                    d = this.a[++ds];
                    dd = 0;
                }
                d[dd++] = s[sd];
            }
            ++sd;
        }
        long j = BigArrays.index(ds, dd);
        ObjectBigArrays.fill(this.a, j, this.size, null);
        boolean modified = this.size != j;
        this.size = j;
        return modified;
    }

    @Override
    public void clear() {
        ObjectBigArrays.fill(this.a, 0L, this.size, null);
        this.size = 0L;
    }

    @Override
    public long size64() {
        return this.size;
    }

    @Override
    public void size(long size) {
        if (size > ObjectBigArrays.length(this.a)) {
            this.ensureCapacity(size);
        }
        if (size > this.size) {
            ObjectBigArrays.fill(this.a, this.size, size, null);
        } else {
            ObjectBigArrays.fill(this.a, size, this.size, null);
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
        long arrayLength = ObjectBigArrays.length(this.a);
        if (n >= arrayLength || this.size == arrayLength) {
            return;
        }
        this.a = ObjectBigArrays.trim(this.a, Math.max(n, this.size));
    }

    @Override
    public void getElements(long from, Object[][] a, long offset, long length) {
        ObjectBigArrays.copy(this.a, from, a, offset, length);
    }

    @Override
    public void removeElements(long from, long to) {
        BigArrays.ensureFromTo(this.size, from, to);
        ObjectBigArrays.copy(this.a, to, this.a, from, this.size - to);
        this.size -= to - from;
        ObjectBigArrays.fill(this.a, this.size, this.size + to - from, null);
    }

    @Override
    public void addElements(long index, K[][] a, long offset, long length) {
        this.ensureIndex(index);
        ObjectBigArrays.ensureOffsetLength(a, offset, length);
        this.grow(this.size + length);
        ObjectBigArrays.copy(this.a, index, this.a, index + length, this.size - index);
        ObjectBigArrays.copy(a, offset, this.a, index, length);
        this.size += length;
    }

    @Override
    public ObjectBigListIterator<K> listIterator(final long index) {
        this.ensureIndex(index);
        return new ObjectBigListIterator<K>(){
            long pos;
            long last;
            {
                this.pos = index;
                this.last = -1L;
            }

            @Override
            public boolean hasNext() {
                return this.pos < ObjectBigArrayBigList.this.size;
            }

            @Override
            public boolean hasPrevious() {
                return this.pos > 0L;
            }

            @Override
            public K next() {
                if (!this.hasNext()) {
                    throw new NoSuchElementException();
                }
                this.last = this.pos++;
                return ObjectBigArrays.get(ObjectBigArrayBigList.this.a, this.last);
            }

            @Override
            public K previous() {
                if (!this.hasPrevious()) {
                    throw new NoSuchElementException();
                }
                this.last = --this.pos;
                return ObjectBigArrays.get(ObjectBigArrayBigList.this.a, this.pos);
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
            public void add(K k) {
                ObjectBigArrayBigList.this.add(this.pos++, k);
                this.last = -1L;
            }

            @Override
            public void set(K k) {
                if (this.last == -1L) {
                    throw new IllegalStateException();
                }
                ObjectBigArrayBigList.this.set(this.last, k);
            }

            @Override
            public void remove() {
                if (this.last == -1L) {
                    throw new IllegalStateException();
                }
                ObjectBigArrayBigList.this.remove(this.last);
                if (this.last < this.pos) {
                    --this.pos;
                }
                this.last = -1L;
            }
        };
    }

    public ObjectBigArrayBigList<K> clone() {
        ObjectBigArrayBigList<K> c = new ObjectBigArrayBigList<K>(this.size);
        ObjectBigArrays.copy(this.a, 0L, c.a, 0L, this.size);
        c.size = this.size;
        return c;
    }

    private boolean valEquals(K a, K b) {
        return a == null ? b == null : a.equals(b);
    }

    public boolean equals(ObjectBigArrayBigList<K> l) {
        if (l == this) {
            return true;
        }
        long s = this.size64();
        if (s != l.size64()) {
            return false;
        }
        K[][] a1 = this.a;
        K[][] a2 = l.a;
        while (s-- != 0L) {
            if (this.valEquals(ObjectBigArrays.get(a1, s), ObjectBigArrays.get(a2, s))) continue;
            return false;
        }
        return true;
    }

    @Override
    public int compareTo(ObjectBigArrayBigList<? extends K> l) {
        long s1 = this.size64();
        long s2 = l.size64();
        K[][] a1 = this.a;
        K[][] a2 = l.a;
        int i = 0;
        while ((long)i < s1 && (long)i < s2) {
            K e2;
            K e1 = ObjectBigArrays.get(a1, i);
            int r = ((Comparable)e1).compareTo(e2 = ObjectBigArrays.get(a2, i));
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
            s.writeObject(ObjectBigArrays.get(this.a, i));
            ++i;
        }
    }

    private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
        s.defaultReadObject();
        this.a = ObjectBigArrays.newBigArray(this.size);
        int i = 0;
        while ((long)i < this.size) {
            ObjectBigArrays.set(this.a, i, s.readObject());
            ++i;
        }
    }

}

