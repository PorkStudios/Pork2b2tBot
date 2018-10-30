/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.bytes.ByteComparator;
import it.unimi.dsi.fastutil.bytes.ByteHeapSemiIndirectPriorityQueue;
import it.unimi.dsi.fastutil.bytes.ByteIndirectHeaps;
import it.unimi.dsi.fastutil.ints.IntArrays;
import java.util.Arrays;
import java.util.NoSuchElementException;

public class ByteHeapIndirectPriorityQueue
extends ByteHeapSemiIndirectPriorityQueue {
    protected final int[] inv;

    public ByteHeapIndirectPriorityQueue(byte[] refArray, int capacity, ByteComparator c) {
        super(refArray, capacity, c);
        if (capacity > 0) {
            this.heap = new int[capacity];
        }
        this.c = c;
        this.inv = new int[refArray.length];
        Arrays.fill(this.inv, -1);
    }

    public ByteHeapIndirectPriorityQueue(byte[] refArray, int capacity) {
        this(refArray, capacity, null);
    }

    public ByteHeapIndirectPriorityQueue(byte[] refArray, ByteComparator c) {
        this(refArray, refArray.length, c);
    }

    public ByteHeapIndirectPriorityQueue(byte[] refArray) {
        this(refArray, refArray.length, null);
    }

    public ByteHeapIndirectPriorityQueue(byte[] refArray, int[] a, int size, ByteComparator c) {
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
        ByteIndirectHeaps.makeHeap(refArray, a, this.inv, size, c);
    }

    public ByteHeapIndirectPriorityQueue(byte[] refArray, int[] a, ByteComparator c) {
        this(refArray, a, a.length, c);
    }

    public ByteHeapIndirectPriorityQueue(byte[] refArray, int[] a, int size) {
        this(refArray, a, size, null);
    }

    public ByteHeapIndirectPriorityQueue(byte[] refArray, int[] a) {
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
        ByteIndirectHeaps.upHeap(this.refArray, this.heap, this.inv, this.size, this.size - 1, this.c);
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
            ByteIndirectHeaps.downHeap(this.refArray, this.heap, this.inv, this.size, 0, this.c);
        }
        return result;
    }

    @Override
    public void changed() {
        ByteIndirectHeaps.downHeap(this.refArray, this.heap, this.inv, this.size, 0, this.c);
    }

    @Override
    public void changed(int index) {
        int pos = this.inv[index];
        if (pos < 0) {
            throw new IllegalArgumentException("Index " + index + " does not belong to the queue");
        }
        int newPos = ByteIndirectHeaps.upHeap(this.refArray, this.heap, this.inv, this.size, pos, this.c);
        ByteIndirectHeaps.downHeap(this.refArray, this.heap, this.inv, this.size, newPos, this.c);
    }

    @Override
    public void allChanged() {
        ByteIndirectHeaps.makeHeap(this.refArray, this.heap, this.inv, this.size, this.c);
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
            int newPos = ByteIndirectHeaps.upHeap(this.refArray, this.heap, this.inv, this.size, result, this.c);
            ByteIndirectHeaps.downHeap(this.refArray, this.heap, this.inv, this.size, newPos, this.c);
        }
        return true;
    }

    @Override
    public void clear() {
        this.size = 0;
        Arrays.fill(this.inv, -1);
    }
}

