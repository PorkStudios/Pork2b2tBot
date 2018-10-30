/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.shorts;

import it.unimi.dsi.fastutil.shorts.AbstractShortSet;
import it.unimi.dsi.fastutil.shorts.ShortArrays;
import it.unimi.dsi.fastutil.shorts.ShortCollection;
import it.unimi.dsi.fastutil.shorts.ShortIterator;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class ShortArraySet
extends AbstractShortSet
implements Serializable,
Cloneable {
    private static final long serialVersionUID = 1L;
    private transient short[] a;
    private int size;

    public ShortArraySet(short[] a) {
        this.a = a;
        this.size = a.length;
    }

    public ShortArraySet() {
        this.a = ShortArrays.EMPTY_ARRAY;
    }

    public ShortArraySet(int capacity) {
        this.a = new short[capacity];
    }

    public ShortArraySet(ShortCollection c) {
        this(c.size());
        this.addAll(c);
    }

    public ShortArraySet(Collection<? extends Short> c) {
        this(c.size());
        this.addAll(c);
    }

    public ShortArraySet(short[] a, int size) {
        this.a = a;
        this.size = size;
        if (size > a.length) {
            throw new IllegalArgumentException("The provided size (" + size + ") is larger than or equal to the array size (" + a.length + ")");
        }
    }

    private int findKey(short o) {
        int i = this.size;
        while (i-- != 0) {
            if (this.a[i] != o) continue;
            return i;
        }
        return -1;
    }

    @Override
    public ShortIterator iterator() {
        return new ShortIterator(){
            int next = 0;

            @Override
            public boolean hasNext() {
                return this.next < ShortArraySet.this.size;
            }

            @Override
            public short nextShort() {
                if (!this.hasNext()) {
                    throw new NoSuchElementException();
                }
                return ShortArraySet.this.a[this.next++];
            }

            @Override
            public void remove() {
                int tail = ShortArraySet.this.size-- - this.next--;
                System.arraycopy(ShortArraySet.this.a, this.next + 1, ShortArraySet.this.a, this.next, tail);
            }
        };
    }

    @Override
    public boolean contains(short k) {
        return this.findKey(k) != -1;
    }

    @Override
    public int size() {
        return this.size;
    }

    @Override
    public boolean remove(short k) {
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
    public boolean add(short k) {
        int pos = this.findKey(k);
        if (pos != -1) {
            return false;
        }
        if (this.size == this.a.length) {
            short[] b = new short[this.size == 0 ? 2 : this.size * 2];
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

    public ShortArraySet clone() {
        ShortArraySet c;
        try {
            c = (ShortArraySet)Object.super.clone();
        }
        catch (CloneNotSupportedException cantHappen) {
            throw new InternalError();
        }
        c.a = (short[])this.a.clone();
        return c;
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

