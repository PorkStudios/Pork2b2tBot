/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.booleans;

import it.unimi.dsi.fastutil.booleans.AbstractBooleanSet;
import it.unimi.dsi.fastutil.booleans.BooleanArrays;
import it.unimi.dsi.fastutil.booleans.BooleanCollection;
import it.unimi.dsi.fastutil.booleans.BooleanIterator;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class BooleanArraySet
extends AbstractBooleanSet
implements Serializable,
Cloneable {
    private static final long serialVersionUID = 1L;
    private transient boolean[] a;
    private int size;

    public BooleanArraySet(boolean[] a) {
        this.a = a;
        this.size = a.length;
    }

    public BooleanArraySet() {
        this.a = BooleanArrays.EMPTY_ARRAY;
    }

    public BooleanArraySet(int capacity) {
        this.a = new boolean[capacity];
    }

    public BooleanArraySet(BooleanCollection c) {
        this(c.size());
        this.addAll(c);
    }

    public BooleanArraySet(Collection<? extends Boolean> c) {
        this(c.size());
        this.addAll(c);
    }

    public BooleanArraySet(boolean[] a, int size) {
        this.a = a;
        this.size = size;
        if (size > a.length) {
            throw new IllegalArgumentException("The provided size (" + size + ") is larger than or equal to the array size (" + a.length + ")");
        }
    }

    private int findKey(boolean o) {
        int i = this.size;
        while (i-- != 0) {
            if (this.a[i] != o) continue;
            return i;
        }
        return -1;
    }

    @Override
    public BooleanIterator iterator() {
        return new BooleanIterator(){
            int next = 0;

            @Override
            public boolean hasNext() {
                return this.next < BooleanArraySet.this.size;
            }

            @Override
            public boolean nextBoolean() {
                if (!this.hasNext()) {
                    throw new NoSuchElementException();
                }
                return BooleanArraySet.this.a[this.next++];
            }

            @Override
            public void remove() {
                int tail = BooleanArraySet.this.size-- - this.next--;
                System.arraycopy(BooleanArraySet.this.a, this.next + 1, BooleanArraySet.this.a, this.next, tail);
            }
        };
    }

    @Override
    public boolean contains(boolean k) {
        return this.findKey(k) != -1;
    }

    @Override
    public int size() {
        return this.size;
    }

    @Override
    public boolean remove(boolean k) {
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
    public boolean add(boolean k) {
        int pos = this.findKey(k);
        if (pos != -1) {
            return false;
        }
        if (this.size == this.a.length) {
            boolean[] b = new boolean[this.size == 0 ? 2 : this.size * 2];
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

    public BooleanArraySet clone() {
        BooleanArraySet c;
        try {
            c = (BooleanArraySet)Object.super.clone();
        }
        catch (CloneNotSupportedException cantHappen) {
            throw new InternalError();
        }
        c.a = (boolean[])this.a.clone();
        return c;
    }

    private void writeObject(ObjectOutputStream s) throws IOException {
        s.defaultWriteObject();
        for (int i = 0; i < this.size; ++i) {
            s.writeBoolean(this.a[i]);
        }
    }

    private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
        s.defaultReadObject();
        this.a = new boolean[this.size];
        for (int i = 0; i < this.size; ++i) {
            this.a[i] = s.readBoolean();
        }
    }

}

