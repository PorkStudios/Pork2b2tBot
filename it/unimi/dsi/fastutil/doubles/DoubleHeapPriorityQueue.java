/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.doubles;

import it.unimi.dsi.fastutil.doubles.DoubleArrays;
import it.unimi.dsi.fastutil.doubles.DoubleCollection;
import it.unimi.dsi.fastutil.doubles.DoubleComparator;
import it.unimi.dsi.fastutil.doubles.DoubleHeaps;
import it.unimi.dsi.fastutil.doubles.DoublePriorityQueue;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class DoubleHeapPriorityQueue
implements DoublePriorityQueue,
Serializable {
    private static final long serialVersionUID = 1L;
    protected transient double[] heap = DoubleArrays.EMPTY_ARRAY;
    protected int size;
    protected DoubleComparator c;

    public DoubleHeapPriorityQueue(int capacity, DoubleComparator c) {
        if (capacity > 0) {
            this.heap = new double[capacity];
        }
        this.c = c;
    }

    public DoubleHeapPriorityQueue(int capacity) {
        this(capacity, null);
    }

    public DoubleHeapPriorityQueue(DoubleComparator c) {
        this(0, c);
    }

    public DoubleHeapPriorityQueue() {
        this(0, null);
    }

    public DoubleHeapPriorityQueue(double[] a, int size, DoubleComparator c) {
        this(c);
        this.heap = a;
        this.size = size;
        DoubleHeaps.makeHeap(a, size, c);
    }

    public DoubleHeapPriorityQueue(double[] a, DoubleComparator c) {
        this(a, a.length, c);
    }

    public DoubleHeapPriorityQueue(double[] a, int size) {
        this(a, size, null);
    }

    public DoubleHeapPriorityQueue(double[] a) {
        this(a, a.length);
    }

    public DoubleHeapPriorityQueue(DoubleCollection collection, DoubleComparator c) {
        this(collection.toDoubleArray(), c);
    }

    public DoubleHeapPriorityQueue(DoubleCollection collection) {
        this(collection, (DoubleComparator)null);
    }

    public DoubleHeapPriorityQueue(Collection<? extends Double> collection, DoubleComparator c) {
        this(collection.size(), c);
        Iterator<? extends Double> iterator = collection.iterator();
        int size = collection.size();
        for (int i = 0; i < size; ++i) {
            this.heap[i] = iterator.next();
        }
    }

    public DoubleHeapPriorityQueue(Collection<? extends Double> collection) {
        this(collection, null);
    }

    @Override
    public void enqueue(double x) {
        if (this.size == this.heap.length) {
            this.heap = DoubleArrays.grow(this.heap, this.size + 1);
        }
        this.heap[this.size++] = x;
        DoubleHeaps.upHeap(this.heap, this.size, this.size - 1, this.c);
    }

    @Override
    public double dequeueDouble() {
        if (this.size == 0) {
            throw new NoSuchElementException();
        }
        double result = this.heap[0];
        this.heap[0] = this.heap[--this.size];
        if (this.size != 0) {
            DoubleHeaps.downHeap(this.heap, this.size, 0, this.c);
        }
        return result;
    }

    @Override
    public double firstDouble() {
        if (this.size == 0) {
            throw new NoSuchElementException();
        }
        return this.heap[0];
    }

    @Override
    public void changed() {
        DoubleHeaps.downHeap(this.heap, this.size, 0, this.c);
    }

    @Override
    public int size() {
        return this.size;
    }

    @Override
    public void clear() {
        this.size = 0;
    }

    public void trim() {
        this.heap = DoubleArrays.trim(this.heap, this.size);
    }

    @Override
    public DoubleComparator comparator() {
        return this.c;
    }

    private void writeObject(ObjectOutputStream s) throws IOException {
        s.defaultWriteObject();
        s.writeInt(this.heap.length);
        for (int i = 0; i < this.size; ++i) {
            s.writeDouble(this.heap[i]);
        }
    }

    private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
        s.defaultReadObject();
        this.heap = new double[s.readInt()];
        for (int i = 0; i < this.size; ++i) {
            this.heap[i] = s.readDouble();
        }
    }
}

