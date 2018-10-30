/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.bytes.AbstractByteSet;
import it.unimi.dsi.fastutil.bytes.ByteArrays;
import it.unimi.dsi.fastutil.bytes.ByteCollection;
import it.unimi.dsi.fastutil.bytes.ByteIterator;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class ByteArraySet
extends AbstractByteSet
implements Serializable,
Cloneable {
    private static final long serialVersionUID = 1L;
    private transient byte[] a;
    private int size;

    public ByteArraySet(byte[] a) {
        this.a = a;
        this.size = a.length;
    }

    public ByteArraySet() {
        this.a = ByteArrays.EMPTY_ARRAY;
    }

    public ByteArraySet(int capacity) {
        this.a = new byte[capacity];
    }

    public ByteArraySet(ByteCollection c) {
        this(c.size());
        this.addAll(c);
    }

    public ByteArraySet(Collection<? extends Byte> c) {
        this(c.size());
        this.addAll(c);
    }

    public ByteArraySet(byte[] a, int size) {
        this.a = a;
        this.size = size;
        if (size > a.length) {
            throw new IllegalArgumentException("The provided size (" + size + ") is larger than or equal to the array size (" + a.length + ")");
        }
    }

    private int findKey(byte o) {
        int i = this.size;
        while (i-- != 0) {
            if (this.a[i] != o) continue;
            return i;
        }
        return -1;
    }

    @Override
    public ByteIterator iterator() {
        return new ByteIterator(){
            int next = 0;

            @Override
            public boolean hasNext() {
                return this.next < ByteArraySet.this.size;
            }

            @Override
            public byte nextByte() {
                if (!this.hasNext()) {
                    throw new NoSuchElementException();
                }
                return ByteArraySet.this.a[this.next++];
            }

            @Override
            public void remove() {
                int tail = ByteArraySet.this.size-- - this.next--;
                System.arraycopy(ByteArraySet.this.a, this.next + 1, ByteArraySet.this.a, this.next, tail);
            }
        };
    }

    @Override
    public boolean contains(byte k) {
        return this.findKey(k) != -1;
    }

    @Override
    public int size() {
        return this.size;
    }

    @Override
    public boolean remove(byte k) {
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
    public boolean add(byte k) {
        int pos = this.findKey(k);
        if (pos != -1) {
            return false;
        }
        if (this.size == this.a.length) {
            byte[] b = new byte[this.size == 0 ? 2 : this.size * 2];
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

    public ByteArraySet clone() {
        ByteArraySet c;
        try {
            c = (ByteArraySet)Object.super.clone();
        }
        catch (CloneNotSupportedException cantHappen) {
            throw new InternalError();
        }
        c.a = (byte[])this.a.clone();
        return c;
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

