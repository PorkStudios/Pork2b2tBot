/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.floats.FloatComparator;
import it.unimi.dsi.fastutil.floats.FloatIndirectPriorityQueue;
import it.unimi.dsi.fastutil.floats.FloatSemiIndirectHeaps;
import it.unimi.dsi.fastutil.ints.IntArrays;
import java.util.Comparator;
import java.util.NoSuchElementException;

public class FloatHeapSemiIndirectPriorityQueue
implements FloatIndirectPriorityQueue {
    protected final float[] refArray;
    protected int[] heap = IntArrays.EMPTY_ARRAY;
    protected int size;
    protected FloatComparator c;

    public FloatHeapSemiIndirectPriorityQueue(float[] refArray, int capacity, FloatComparator c) {
        if (capacity > 0) {
            this.heap = new int[capacity];
        }
        this.refArray = refArray;
        this.c = c;
    }

    public FloatHeapSemiIndirectPriorityQueue(float[] refArray, int capacity) {
        this(refArray, capacity, null);
    }

    public FloatHeapSemiIndirectPriorityQueue(float[] refArray, FloatComparator c) {
        this(refArray, refArray.length, c);
    }

    public FloatHeapSemiIndirectPriorityQueue(float[] refArray) {
        this(refArray, refArray.length, null);
    }

    public FloatHeapSemiIndirectPriorityQueue(float[] refArray, int[] a, int size, FloatComparator c) {
        this(refArray, 0, c);
        this.heap = a;
        this.size = size;
        FloatSemiIndirectHeaps.makeHeap(refArray, a, size, c);
    }

    public FloatHeapSemiIndirectPriorityQueue(float[] refArray, int[] a, FloatComparator c) {
        this(refArray, a, a.length, c);
    }

    public FloatHeapSemiIndirectPriorityQueue(float[] refArray, int[] a, int size) {
        this(refArray, a, size, null);
    }

    public FloatHeapSemiIndirectPriorityQueue(float[] refArray, int[] a) {
        this(refArray, a, a.length);
    }

    protected void ensureElement(int index) {
        if (index < 0) {
            throw new IndexOutOfBoundsException("Index (" + index + ") is negative");
        }
        if (index >= this.refArray.length) {
            throw new IndexOutOfBoundsException("Index (" + index + ") is larger than or equal to reference array size (" + this.refArray.length + ")");
        }
    }

    @Override
    public void enqueue(int x) {
        this.ensureElement(x);
        if (this.size == this.heap.length) {
            this.heap = IntArrays.grow(this.heap, this.size + 1);
        }
        this.heap[this.size++] = x;
        FloatSemiIndirectHeaps.upHeap(this.refArray, this.heap, this.size, this.size - 1, this.c);
    }

    @Override
    public int dequeue() {
        if (this.size == 0) {
            throw new NoSuchElementException();
        }
        int result = this.heap[0];
        this.heap[0] = this.heap[--this.size];
        if (this.size != 0) {
            FloatSemiIndirectHeaps.downHeap(this.refArray, this.heap, this.size, 0, this.c);
        }
        return result;
    }

    @Override
    public int first() {
        if (this.size == 0) {
            throw new NoSuchElementException();
        }
        return this.heap[0];
    }

    @Override
    public void changed() {
        FloatSemiIndirectHeaps.downHeap(this.refArray, this.heap, this.size, 0, this.c);
    }

    @Override
    public void allChanged() {
        FloatSemiIndirectHeaps.makeHeap(this.refArray, this.heap, this.size, this.c);
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
        this.heap = IntArrays.trim(this.heap, this.size);
    }

    @Override
    public FloatComparator comparator() {
        return this.c;
    }

    @Override
    public int front(int[] a) {
        return this.c == null ? FloatSemiIndirectHeaps.front(this.refArray, this.heap, this.size, a) : FloatSemiIndirectHeaps.front(this.refArray, this.heap, this.size, a, this.c);
    }

    public String toString() {
        StringBuffer s = new StringBuffer();
        s.append("[");
        for (int i = 0; i < this.size; ++i) {
            if (i != 0) {
                s.append(", ");
            }
            s.append(this.refArray[this.heap[i]]);
        }
        s.append("]");
        return s.toString();
    }
}

