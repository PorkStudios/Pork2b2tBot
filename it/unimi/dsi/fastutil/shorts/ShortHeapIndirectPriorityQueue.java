/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.shorts;

import it.unimi.dsi.fastutil.ints.IntArrays;
import it.unimi.dsi.fastutil.shorts.ShortComparator;
import it.unimi.dsi.fastutil.shorts.ShortHeapSemiIndirectPriorityQueue;
import it.unimi.dsi.fastutil.shorts.ShortIndirectHeaps;
import java.util.Arrays;
import java.util.NoSuchElementException;

public class ShortHeapIndirectPriorityQueue
extends ShortHeapSemiIndirectPriorityQueue {
    protected final int[] inv;

    public ShortHeapIndirectPriorityQueue(short[] refArray, int capacity, ShortComparator c) {
        super(refArray, capacity, c);
        if (capacity > 0) {
            this.heap = new int[capacity];
        }
        this.c = c;
        this.inv = new int[refArray.length];
        Arrays.fill(this.inv, -1);
    }

    public ShortHeapIndirectPriorityQueue(short[] refArray, int capacity) {
        this(refArray, capacity, null);
    }

    public ShortHeapIndirectPriorityQueue(short[] refArray, ShortComparator c) {
        this(refArray, refArray.length, c);
    }

    public ShortHeapIndirectPriorityQueue(short[] refArray) {
        this(refArray, refArray.length, null);
    }

    public ShortHeapIndirectPriorityQueue(short[] refArray, int[] a, int size, ShortComparator c) {
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
        ShortIndirectHeaps.makeHeap(refArray, a, this.inv, size, c);
    }

    public ShortHeapIndirectPriorityQueue(short[] refArray, int[] a, ShortComparator c) {
        this(refArray, a, a.length, c);
    }

    public ShortHeapIndirectPriorityQueue(short[] refArray, int[] a, int size) {
        this(refArray, a, size, null);
    }

    public ShortHeapIndirectPriorityQueue(short[] refArray, int[] a) {
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
        ShortIndirectHeaps.upHeap(this.refArray, this.heap, this.inv, this.size, this.size - 1, this.c);
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
            ShortIndirectHeaps.downHeap(this.refArray, this.heap, this.inv, this.size, 0, this.c);
        }
        return result;
    }

    @Override
    public void changed() {
        ShortIndirectHeaps.downHeap(this.refArray, this.heap, this.inv, this.size, 0, this.c);
    }

    @Override
    public void changed(int index) {
        int pos = this.inv[index];
        if (pos < 0) {
            throw new IllegalArgumentException("Index " + index + " does not belong to the queue");
        }
        int newPos = ShortIndirectHeaps.upHeap(this.refArray, this.heap, this.inv, this.size, pos, this.c);
        ShortIndirectHeaps.downHeap(this.refArray, this.heap, this.inv, this.size, newPos, this.c);
    }

    @Override
    public void allChanged() {
        ShortIndirectHeaps.makeHeap(this.refArray, this.heap, this.inv, this.size, this.c);
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
            int newPos = ShortIndirectHeaps.upHeap(this.refArray, this.heap, this.inv, this.size, result, this.c);
            ShortIndirectHeaps.downHeap(this.refArray, this.heap, this.inv, this.size, newPos, this.c);
        }
        return true;
    }

    @Override
    public void clear() {
        this.size = 0;
        Arrays.fill(this.inv, -1);
    }
}

