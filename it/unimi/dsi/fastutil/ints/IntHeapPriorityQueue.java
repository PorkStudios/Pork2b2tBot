/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.ints;

import it.unimi.dsi.fastutil.ints.IntArrays;
import it.unimi.dsi.fastutil.ints.IntCollection;
import it.unimi.dsi.fastutil.ints.IntComparator;
import it.unimi.dsi.fastutil.ints.IntHeaps;
import it.unimi.dsi.fastutil.ints.IntPriorityQueue;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class IntHeapPriorityQueue
implements IntPriorityQueue,
Serializable {
    private static final long serialVersionUID = 1L;
    protected transient int[] heap = IntArrays.EMPTY_ARRAY;
    protected int size;
    protected IntComparator c;

    public IntHeapPriorityQueue(int capacity, IntComparator c) {
        if (capacity > 0) {
            this.heap = new int[capacity];
        }
        this.c = c;
    }

    public IntHeapPriorityQueue(int capacity) {
        this(capacity, null);
    }

    public IntHeapPriorityQueue(IntComparator c) {
        this(0, c);
    }

    public IntHeapPriorityQueue() {
        this(0, null);
    }

    public IntHeapPriorityQueue(int[] a, int size, IntComparator c) {
        this(c);
        this.heap = a;
        this.size = size;
        IntHeaps.makeHeap(a, size, c);
    }

    public IntHeapPriorityQueue(int[] a, IntComparator c) {
        this(a, a.length, c);
    }

    public IntHeapPriorityQueue(int[] a, int size) {
        this(a, size, null);
    }

    public IntHeapPriorityQueue(int[] a) {
        this(a, a.length);
    }

    public IntHeapPriorityQueue(IntCollection collection, IntComparator c) {
        this(collection.toIntArray(), c);
    }

    public IntHeapPriorityQueue(IntCollection collection) {
        this(collection, (IntComparator)null);
    }

    public IntHeapPriorityQueue(Collection<? extends Integer> collection, IntComparator c) {
        this(collection.size(), c);
        Iterator<? extends Integer> iterator = collection.iterator();
        int size = collection.size();
        for (int i = 0; i < size; ++i) {
            this.heap[i] = iterator.next();
        }
    }

    public IntHeapPriorityQueue(Collection<? extends Integer> collection) {
        this(collection, null);
    }

    @Override
    public void enqueue(int x) {
        if (this.size == this.heap.length) {
            this.heap = IntArrays.grow(this.heap, this.size + 1);
        }
        this.heap[this.size++] = x;
        IntHeaps.upHeap(this.heap, this.size, this.size - 1, this.c);
    }

    @Override
    public int dequeueInt() {
        if (this.size == 0) {
            throw new NoSuchElementException();
        }
        int result = this.heap[0];
        this.heap[0] = this.heap[--this.size];
        if (this.size != 0) {
            IntHeaps.downHeap(this.heap, this.size, 0, this.c);
        }
        return result;
    }

    @Override
    public int firstInt() {
        if (this.size == 0) {
            throw new NoSuchElementException();
        }
        return this.heap[0];
    }

    @Override
    public void changed() {
        IntHeaps.downHeap(this.heap, this.size, 0, this.c);
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
    public IntComparator comparator() {
        return this.c;
    }

    private void writeObject(ObjectOutputStream s) throws IOException {
        s.defaultWriteObject();
        s.writeInt(this.heap.length);
        for (int i = 0; i < this.size; ++i) {
            s.writeInt(this.heap[i]);
        }
    }

    private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
        s.defaultReadObject();
        this.heap = new int[s.readInt()];
        for (int i = 0; i < this.size; ++i) {
            this.heap[i] = s.readInt();
        }
    }
}

