/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.longs.AbstractLongSet;
import it.unimi.dsi.fastutil.longs.LongArrays;
import it.unimi.dsi.fastutil.longs.LongCollection;
import it.unimi.dsi.fastutil.longs.LongIterator;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class LongArraySet
extends AbstractLongSet
implements Serializable,
Cloneable {
    private static final long serialVersionUID = 1L;
    private transient long[] a;
    private int size;

    public LongArraySet(long[] a) {
        this.a = a;
        this.size = a.length;
    }

    public LongArraySet() {
        this.a = LongArrays.EMPTY_ARRAY;
    }

    public LongArraySet(int capacity) {
        this.a = new long[capacity];
    }

    public LongArraySet(LongCollection c) {
        this(c.size());
        this.addAll(c);
    }

    public LongArraySet(Collection<? extends Long> c) {
        this(c.size());
        this.addAll(c);
    }

    public LongArraySet(long[] a, int size) {
        this.a = a;
        this.size = size;
        if (size > a.length) {
            throw new IllegalArgumentException("The provided size (" + size + ") is larger than or equal to the array size (" + a.length + ")");
        }
    }

    private int findKey(long o) {
        int i = this.size;
        while (i-- != 0) {
            if (this.a[i] != o) continue;
            return i;
        }
        return -1;
    }

    @Override
    public LongIterator iterator() {
        return new LongIterator(){
            int next = 0;

            @Override
            public boolean hasNext() {
                return this.next < LongArraySet.this.size;
            }

            @Override
            public long nextLong() {
                if (!this.hasNext()) {
                    throw new NoSuchElementException();
                }
                return LongArraySet.this.a[this.next++];
            }

            @Override
            public void remove() {
                int tail = LongArraySet.this.size-- - this.next--;
                System.arraycopy(LongArraySet.this.a, this.next + 1, LongArraySet.this.a, this.next, tail);
            }
        };
    }

    @Override
    public boolean contains(long k) {
        return this.findKey(k) != -1;
    }

    @Override
    public int size() {
        return this.size;
    }

    @Override
    public boolean remove(long k) {
        int pos = this.findKey(k);
        if (pos == -1) {
            return false;
        }
        int tail = this.size - pos - 1;
        for (int i = 0; i < tail; ++i) {
            this.a[pos + i] = this.a[pos + i + 1];
        }
        --this.size;
        return true;
    }

    @Override
    public boolean add(long k) {
        int pos = this.findKey(k);
        if (pos != -1) {
            return false;
        }
        if (this.size == this.a.length) {
            long[] b = new long[this.size == 0 ? 2 : this.size * 2];
            int i = this.size;
            while (i-- != 0) {
                b[i] = this.a[i];
            }
            this.a = b;
        }
        this.a[this.size++] = k;
        return true;
    }

    @Override
    public void clear() {
        this.size = 0;
    }

    @Override
    public boolean isEmpty() {
        return this.size == 0;
    }

    public LongArraySet clone() {
        LongArraySet c;
        try {
            c = (LongArraySet)Object.super.clone();
        }
        catch (CloneNotSupportedException cantHappen) {
            throw new InternalError();
        }
        c.a = (long[])this.a.clone();
        return c;
    }

    private void writeObject(ObjectOutputStream s) throws IOException {
        s.defaultWriteObject();
        for (int i = 0; i < this.size; ++i) {
            s.writeLong(this.a[i]);
        }
    }

    private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
        s.defaultReadObject();
        this.a = new long[this.size];
        for (int i = 0; i < this.size; ++i) {
            this.a[i] = s.readLong();
        }
    }

}

