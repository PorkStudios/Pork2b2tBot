/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.floats.FloatArrays;
import it.unimi.dsi.fastutil.floats.FloatCollection;
import it.unimi.dsi.fastutil.floats.FloatComparator;
import it.unimi.dsi.fastutil.floats.FloatHeaps;
import it.unimi.dsi.fastutil.floats.FloatPriorityQueue;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class FloatHeapPriorityQueue
implements FloatPriorityQueue,
Serializable {
    private static final long serialVersionUID = 1L;
    protected transient float[] heap = FloatArrays.EMPTY_ARRAY;
    protected int size;
    protected FloatComparator c;

    public FloatHeapPriorityQueue(int capacity, FloatComparator c) {
        if (capacity > 0) {
            this.heap = new float[capacity];
        }
        this.c = c;
    }

    public FloatHeapPriorityQueue(int capacity) {
        this(capacity, null);
    }

    public FloatHeapPriorityQueue(FloatComparator c) {
        this(0, c);
    }

    public FloatHeapPriorityQueue() {
        this(0, null);
    }

    public FloatHeapPriorityQueue(float[] a, int size, FloatComparator c) {
        this(c);
        this.heap = a;
        this.size = size;
        FloatHeaps.makeHeap(a, size, c);
    }

    public FloatHeapPriorityQueue(float[] a, FloatComparator c) {
        this(a, a.length, c);
    }

    public FloatHeapPriorityQueue(float[] a, int size) {
        this(a, size, null);
    }

    public FloatHeapPriorityQueue(float[] a) {
        this(a, a.length);
    }

    public FloatHeapPriorityQueue(FloatCollection collection, FloatComparator c) {
        this(collection.toFloatArray(), c);
    }

    public FloatHeapPriorityQueue(FloatCollection collection) {
        this(collection, (FloatComparator)null);
    }

    public FloatHeapPriorityQueue(Collection<? extends Float> collection, FloatComparator c) {
        this(collection.size(), c);
        Iterator<? extends Float> iterator = collection.iterator();
        int size = collection.size();
        for (int i = 0; i < size; ++i) {
            this.heap[i] = iterator.next().floatValue();
        }
    }

    public FloatHeapPriorityQueue(Collection<? extends Float> collection) {
        this(collection, null);
    }

    @Override
    public void enqueue(float x) {
        if (this.size == this.heap.length) {
            this.heap = FloatArrays.grow(this.heap, this.size + 1);
        }
        this.heap[this.size++] = x;
        FloatHeaps.upHeap(this.heap, this.size, this.size - 1, this.c);
    }

    @Override
    public float dequeueFloat() {
        if (this.size == 0) {
            throw new NoSuchElementException();
        }
        float result = this.heap[0];
        this.heap[0] = this.heap[--this.size];
        if (this.size != 0) {
            FloatHeaps.downHeap(this.heap, this.size, 0, this.c);
        }
        return result;
    }

    @Override
    public float firstFloat() {
        if (this.size == 0) {
            throw new NoSuchElementException();
        }
        return this.heap[0];
    }

    @Override
    public void changed() {
        FloatHeaps.downHeap(this.heap, this.size, 0, this.c);
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
        this.heap = FloatArrays.trim(this.heap, this.size);
    }

    @Override
    public FloatComparator comparator() {
        return this.c;
    }

    private void writeObject(ObjectOutputStream s) throws IOException {
        s.defaultWriteObject();
        s.writeInt(this.heap.length);
        for (int i = 0; i < this.size; ++i) {
            s.writeFloat(this.heap[i]);
        }
    }

    private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
        s.defaultReadObject();
        this.heap = new float[s.readInt()];
        for (int i = 0; i < this.size; ++i) {
            this.heap[i] = s.readFloat();
        }
    }
}

