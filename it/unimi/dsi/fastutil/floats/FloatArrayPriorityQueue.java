/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.floats.FloatArrays;
import it.unimi.dsi.fastutil.floats.FloatComparator;
import it.unimi.dsi.fastutil.floats.FloatPriorityQueue;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Comparator;
import java.util.NoSuchElementException;

public class FloatArrayPriorityQueue
implements FloatPriorityQueue,
Serializable {
    private static final long serialVersionUID = 1L;
    protected transient float[] array = FloatArrays.EMPTY_ARRAY;
    protected int size;
    protected FloatComparator c;
    protected transient int firstIndex;
    protected transient boolean firstIndexValid;

    public FloatArrayPriorityQueue(int capacity, FloatComparator c) {
        if (capacity > 0) {
            this.array = new float[capacity];
        }
        this.c = c;
    }

    public FloatArrayPriorityQueue(int capacity) {
        this(capacity, null);
    }

    public FloatArrayPriorityQueue(FloatComparator c) {
        this(0, c);
    }

    public FloatArrayPriorityQueue() {
        this(0, null);
    }

    public FloatArrayPriorityQueue(float[] a, int size, FloatComparator c) {
        this(c);
        this.array = a;
        this.size = size;
    }

    public FloatArrayPriorityQueue(float[] a, FloatComparator c) {
        this(a, a.length, c);
    }

    public FloatArrayPriorityQueue(float[] a, int size) {
        this(a, size, null);
    }

    public FloatArrayPriorityQueue(float[] a) {
        this(a, a.length);
    }

    private int findFirst() {
        if (this.firstIndexValid) {
            return this.firstIndex;
        }
        this.firstIndexValid = true;
        int i = this.size;
        int firstIndex = --i;
        float first = this.array[firstIndex];
        if (this.c == null) {
            while (i-- != 0) {
                if (Float.compare(this.array[i], first) >= 0) continue;
                firstIndex = i;
                first = this.array[firstIndex];
            }
        } else {
            while (i-- != 0) {
                if (this.c.compare(this.array[i], first) >= 0) continue;
                firstIndex = i;
                first = this.array[firstIndex];
            }
        }
        this.firstIndex = firstIndex;
        return this.firstIndex;
    }

    private void ensureNonEmpty() {
        if (this.size == 0) {
            throw new NoSuchElementException();
        }
    }

    @Override
    public void enqueue(float x) {
        if (this.size == this.array.length) {
            this.array = FloatArrays.grow(this.array, this.size + 1);
        }
        if (this.firstIndexValid) {
            if (this.c == null) {
                if (Float.compare(x, this.array[this.firstIndex]) < 0) {
                    this.firstIndex = this.size;
                }
            } else if (this.c.compare(x, this.array[this.firstIndex]) < 0) {
                this.firstIndex = this.size;
            }
        } else {
            this.firstIndexValid = false;
        }
        this.array[this.size++] = x;
    }

    @Override
    public float dequeueFloat() {
        this.ensureNonEmpty();
        int first = this.findFirst();
        float result = this.array[first];
        System.arraycopy(this.array, first + 1, this.array, first, --this.size - first);
        this.firstIndexValid = false;
        return result;
    }

    @Override
    public float firstFloat() {
        this.ensureNonEmpty();
        return this.array[this.findFirst()];
    }

    @Override
    public void changed() {
        this.ensureNonEmpty();
        this.firstIndexValid = false;
    }

    @Override
    public int size() {
        return this.size;
    }

    @Override
    public void clear() {
        this.size = 0;
        this.firstIndexValid = false;
    }

    public void trim() {
        this.array = FloatArrays.trim(this.array, this.size);
    }

    @Override
    public FloatComparator comparator() {
        return this.c;
    }

    private void writeObject(ObjectOutputStream s) throws IOException {
        s.defaultWriteObject();
        s.writeInt(this.array.length);
        for (int i = 0; i < this.size; ++i) {
            s.writeFloat(this.array[i]);
        }
    }

    private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
        s.defaultReadObject();
        this.array = new float[s.readInt()];
        for (int i = 0; i < this.size; ++i) {
            this.array[i] = s.readFloat();
        }
    }
}

