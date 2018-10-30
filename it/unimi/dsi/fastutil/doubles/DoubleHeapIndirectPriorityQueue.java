/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.doubles;

import it.unimi.dsi.fastutil.doubles.DoubleComparator;
import it.unimi.dsi.fastutil.doubles.DoubleHeapSemiIndirectPriorityQueue;
import it.unimi.dsi.fastutil.doubles.DoubleIndirectHeaps;
import it.unimi.dsi.fastutil.ints.IntArrays;
import java.util.Arrays;
import java.util.NoSuchElementException;

public class DoubleHeapIndirectPriorityQueue
extends DoubleHeapSemiIndirectPriorityQueue {
    protected final int[] inv;

    public DoubleHeapIndirectPriorityQueue(double[] refArray, int capacity, DoubleComparator c) {
        super(refArray, capacity, c);
        if (capacity > 0) {
            this.heap = new int[capacity];
        }
        this.c = c;
        this.inv = new int[refArray.length];
        Arrays.fill(this.inv, -1);
    }

    public DoubleHeapIndirectPriorityQueue(double[] refArray, int capacity) {
        this(refArray, capacity, null);
    }

    public DoubleHeapIndirectPriorityQueue(double[] refArray, DoubleComparator c) {
        this(refArray, refArray.length, c);
    }

    public DoubleHeapIndirectPriorityQueue(double[] refArray) {
        this(refArray, refArray.length, null);
    }

    public DoubleHeapIndirectPriorityQueue(double[] refArray, int[] a, int size, DoubleComparator c) {
        this(refArray, 0, c);
        this.heap = a;
        this.size = size;
        int i = size;
        while (i-- != 0) {
            if (this.inv[a[i]] != -1) {
                throw new IllegalArgumentException("Index " + a[i] + " appears twice in the heap");
            }
            this.inv[a[i]] = i;
        }
        DoubleIndirectHeaps.makeHeap(refArray, a, this.inv, size, c);
    }

    public DoubleHeapIndirectPriorityQueue(double[] refArray, int[] a, DoubleComparator c) {
        this(refArray, a, a.length, c);
    }

    public DoubleHeapIndirectPriorityQueue(double[] refArray, int[] a, int size) {
        this(refArray, a, size, null);
    }

    public DoubleHeapIndirectPriorityQueue(double[] refArray, int[] a) {
        this(refArray, a, a.length);
    }

    @Override
    public void enqueue(int x) {
        if (this.inv[x] >= 0) {
            throw new IllegalArgumentException("Index " + x + " belongs to the queue");
        }
        if (this.size == this.heap.length) {
            this.heap = IntArrays.grow(this.heap, this.size + 1);
        }
        this.heap[this.size] = x;
        this.inv[this.heap[this.size]] = this.size++;
        DoubleIndirectHeaps.upHeap(this.refArray, this.heap, this.inv, this.size, this.size - 1, this.c);
    }

    @Override
    public boolean contains(int index) {
        return this.inv[index] >= 0;
    }

    @Override
    public int dequeue() {
        if (this.size == 0) {
            throw new NoSuchElementException();
        }
        int result = this.heap[0];
        if (--this.size != 0) {
            this.heap[0] = this.heap[this.size];
            this.inv[this.heap[0]] = 0;
        }
        this.inv[result] = -1;
        if (this.size != 0) {
            DoubleIndirectHeaps.downHeap(this.refArray, this.heap, this.inv, this.size, 0, this.c);
        }
        return result;
    }

    @Override
    public void changed() {
        DoubleIndirectHeaps.downHeap(this.refArray, this.heap, this.inv, this.size, 0, this.c);
    }

    @Override
    public void changed(int index) {
        int pos = this.inv[index];
        if (pos < 0) {
            throw new IllegalArgumentException("Index " + index + " does not belong to the queue");
        }
        int newPos = DoubleIndirectHeaps.upHeap(this.refArray, this.heap, this.inv, this.size, pos, this.c);
        DoubleIndirectHeaps.downHeap(this.refArray, this.heap, this.inv, this.size, newPos, this.c);
    }

    @Override
    public void allChanged() {
        DoubleIndirectHeaps.makeHeap(this.refArray, this.heap, this.inv, this.size, this.c);
    }

    @Override
    public boolean remove(int index) {
        int result = this.inv[index];
        if (result < 0) {
            return false;
        }
        this.inv[index] = -1;
        if (result < --this.size) {
            this.heap[result] = this.heap[this.size];
            this.inv[this.heap[result]] = result;
            int newPos = DoubleIndirectHeaps.upHeap(this.refArray, this.heap, this.inv, this.size, result, this.c);
            DoubleIndirectHeaps.downHeap(this.refArray, this.heap, this.inv, this.size, newPos, this.c);
        }
        return true;
    }

    @Override
    public void clear() {
        this.size = 0;
        Arrays.fill(this.inv, -1);
    }
}

