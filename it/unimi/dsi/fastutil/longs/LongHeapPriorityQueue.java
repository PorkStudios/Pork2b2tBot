/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.longs.LongArrays;
import it.unimi.dsi.fastutil.longs.LongCollection;
import it.unimi.dsi.fastutil.longs.LongComparator;
import it.unimi.dsi.fastutil.longs.LongHeaps;
import it.unimi.dsi.fastutil.longs.LongPriorityQueue;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class LongHeapPriorityQueue
implements LongPriorityQueue,
Serializable {
    private static final long serialVersionUID = 1L;
    protected transient long[] heap = LongArrays.EMPTY_ARRAY;
    protected int size;
    protected LongComparator c;

    public LongHeapPriorityQueue(int capacity, LongComparator c) {
        if (capacity > 0) {
            this.heap = new long[capacity];
        }
        this.c = c;
    }

    public LongHeapPriorityQueue(int capacity) {
        this(capacity, null);
    }

    public LongHeapPriorityQueue(LongComparator c) {
        this(0, c);
    }

    public LongHeapPriorityQueue() {
        this(0, null);
    }

    public LongHeapPriorityQueue(long[] a, int size, LongComparator c) {
        this(c);
        this.heap = a;
        this.size = size;
        LongHeaps.makeHeap(a, size, c);
    }

    public LongHeapPriorityQueue(long[] a, LongComparator c) {
        this(a, a.length, c);
    }

    public LongHeapPriorityQueue(long[] a, int size) {
        this(a, size, null);
    }

    public LongHeapPriorityQueue(long[] a) {
        this(a, a.length);
    }

    public LongHeapPriorityQueue(LongCollection collection, LongComparator c) {
        this(collection.toLongArray(), c);
    }

    public LongHeapPriorityQueue(LongCollection collection) {
        this(collection, (LongComparator)null);
    }

    public LongHeapPriorityQueue(Collection<? extends Long> collection, LongComparator c) {
        this(collection.size(), c);
        Iterator<? extends Long> iterator = collection.iterator();
        int size = collection.size();
        for (int i = 0; i < size; ++i) {
            this.heap[i] = iterator.next();
        }
    }

    public LongHeapPriorityQueue(Collection<? extends Long> collection) {
        this(collection, null);
    }

    @Override
    public void enqueue(long x) {
        if (this.size == this.heap.length) {
            this.heap = LongArrays.grow(this.heap, this.size + 1);
        }
        this.heap[this.size++] = x;
        LongHeaps.upHeap(this.heap, this.size, this.size - 1, this.c);
    }

    @Override
    public long dequeueLong() {
        if (this.size == 0) {
            throw new NoSuchElementException();
        }
        long result = this.heap[0];
        this.heap[0] = this.heap[--this.size];
        if (this.size != 0) {
            LongHeaps.downHeap(this.heap, this.size, 0, this.c);
        }
        return result;
    }

    @Override
    public long firstLong() {
        if (this.size == 0) {
            throw new NoSuchElementException();
        }
        return this.heap[0];
    }

    @Override
    public void changed() {
        LongHeaps.downHeap(this.heap, this.size, 0, this.c);
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
        this.heap = LongArrays.trim(this.heap, this.size);
    }

    @Override
    public LongComparator comparator() {
        return this.c;
    }

    private void writeObject(ObjectOutputStream s) throws IOException {
        s.defaultWriteObject();
        s.writeInt(this.heap.length);
        for (int i = 0; i < this.size; ++i) {
            s.writeLong(this.heap[i]);
        }
    }

    private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
        s.defaultReadObject();
        this.heap = new long[s.readInt()];
        for (int i = 0; i < this.size; ++i) {
            this.heap[i] = s.readLong();
        }
    }
}

