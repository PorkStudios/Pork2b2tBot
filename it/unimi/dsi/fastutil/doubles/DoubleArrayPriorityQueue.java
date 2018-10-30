/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.doubles;

import it.unimi.dsi.fastutil.doubles.DoubleArrays;
import it.unimi.dsi.fastutil.doubles.DoubleComparator;
import it.unimi.dsi.fastutil.doubles.DoublePriorityQueue;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Comparator;
import java.util.NoSuchElementException;

public class DoubleArrayPriorityQueue
implements DoublePriorityQueue,
Serializable {
    private static final long serialVersionUID = 1L;
    protected transient double[] array = DoubleArrays.EMPTY_ARRAY;
    protected int size;
    protected DoubleComparator c;
    protected transient int firstIndex;
    protected transient boolean firstIndexValid;

    public DoubleArrayPriorityQueue(int capacity, DoubleComparator c) {
        if (capacity > 0) {
            this.array = new double[capacity];
        }
        this.c = c;
    }

    public DoubleArrayPriorityQueue(int capacity) {
        this(capacity, null);
    }

    public DoubleArrayPriorityQueue(DoubleComparator c) {
        this(0, c);
    }

    public DoubleArrayPriorityQueue() {
        this(0, null);
    }

    public DoubleArrayPriorityQueue(double[] a, int size, DoubleComparator c) {
        this(c);
        this.array = a;
        this.size = size;
    }

    public DoubleArrayPriorityQueue(double[] a, DoubleComparator c) {
        this(a, a.length, c);
    }

    public DoubleArrayPriorityQueue(double[] a, int size) {
        this(a, size, null);
    }

    public DoubleArrayPriorityQueue(double[] a) {
        this(a, a.length);
    }

    private int findFirst() {
        if (this.firstIndexValid) {
            return this.firstIndex;
        }
        this.firstIndexValid = true;
        int i = this.size;
        int firstIndex = --i;
        double first = this.array[firstIndex];
        if (this.c == null) {
            while (i-- != 0) {
                if (Double.compare(this.array[i], first) >= 0) continue;
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
    public void enqueue(double x) {
        if (this.size == this.array.length) {
            this.array = DoubleArrays.grow(this.array, this.size + 1);
        }
        if (this.firstIndexValid) {
            if (this.c == null) {
                if (Double.compare(x, this.array[this.firstIndex]) < 0) {
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
    public double dequeueDouble() {
        this.ensureNonEmpty();
        int first = this.findFirst();
        double result = this.array[first];
        System.arraycopy(this.array, first + 1, this.array, first, --this.size - first);
        this.firstIndexValid = false;
        return result;
    }

    @Override
    public double firstDouble() {
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
        this.array = DoubleArrays.trim(this.array, this.size);
    }

    @Override
    public DoubleComparator comparator() {
        return this.c;
    }

    private void writeObject(ObjectOutputStream s) throws IOException {
        s.defaultWriteObject();
        s.writeInt(this.array.length);
        for (int i = 0; i < this.size; ++i) {
            s.writeDouble(this.array[i]);
        }
    }

    private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
        s.defaultReadObject();
        this.array = new double[s.readInt()];
        for (int i = 0; i < this.size; ++i) {
            this.array[i] = s.readDouble();
        }
    }
}

