/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.floats.AbstractFloatSet;
import it.unimi.dsi.fastutil.floats.FloatArrays;
import it.unimi.dsi.fastutil.floats.FloatCollection;
import it.unimi.dsi.fastutil.floats.FloatIterator;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class FloatArraySet
extends AbstractFloatSet
implements Serializable,
Cloneable {
    private static final long serialVersionUID = 1L;
    private transient float[] a;
    private int size;

    public FloatArraySet(float[] a) {
        this.a = a;
        this.size = a.length;
    }

    public FloatArraySet() {
        this.a = FloatArrays.EMPTY_ARRAY;
    }

    public FloatArraySet(int capacity) {
        this.a = new float[capacity];
    }

    public FloatArraySet(FloatCollection c) {
        this(c.size());
        this.addAll(c);
    }

    public FloatArraySet(Collection<? extends Float> c) {
        this(c.size());
        this.addAll(c);
    }

    public FloatArraySet(float[] a, int size) {
        this.a = a;
        this.size = size;
        if (size > a.length) {
            throw new IllegalArgumentException("The provided size (" + size + ") is larger than or equal to the array size (" + a.length + ")");
        }
    }

    private int findKey(float o) {
        int i = this.size;
        while (i-- != 0) {
            if (Float.floatToIntBits(this.a[i]) != Float.floatToIntBits(o)) continue;
            return i;
        }
        return -1;
    }

    @Override
    public FloatIterator iterator() {
        return new FloatIterator(){
            int next = 0;

            @Override
            public boolean hasNext() {
                return this.next < FloatArraySet.this.size;
            }

            @Override
            public float nextFloat() {
                if (!this.hasNext()) {
                    throw new NoSuchElementException();
                }
                return FloatArraySet.this.a[this.next++];
            }

            @Override
            public void remove() {
                int tail = FloatArraySet.this.size-- - this.next--;
                System.arraycopy(FloatArraySet.this.a, this.next + 1, FloatArraySet.this.a, this.next, tail);
            }
        };
    }

    @Override
    public boolean contains(float k) {
        return this.findKey(k) != -1;
    }

    @Override
    public int size() {
        return this.size;
    }

    @Override
    public boolean remove(float k) {
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
    public boolean add(float k) {
        int pos = this.findKey(k);
        if (pos != -1) {
            return false;
        }
        if (this.size == this.a.length) {
            float[] b = new float[this.size == 0 ? 2 : this.size * 2];
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

    public FloatArraySet clone() {
        FloatArraySet c;
        try {
            c = (FloatArraySet)Object.super.clone();
        }
        catch (CloneNotSupportedException cantHappen) {
            throw new InternalError();
        }
        c.a = (float[])this.a.clone();
        return c;
    }

    private void writeObject(ObjectOutputStream s) throws IOException {
        s.defaultWriteObject();
        for (int i = 0; i < this.size; ++i) {
            s.writeFloat(this.a[i]);
        }
    }

    private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
        s.defaultReadObject();
        this.a = new float[this.size];
        for (int i = 0; i < this.size; ++i) {
            this.a[i] = s.readFloat();
        }
    }

}

