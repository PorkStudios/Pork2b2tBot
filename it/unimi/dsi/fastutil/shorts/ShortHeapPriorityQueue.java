/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.shorts;

import it.unimi.dsi.fastutil.shorts.ShortArrays;
import it.unimi.dsi.fastutil.shorts.ShortCollection;
import it.unimi.dsi.fastutil.shorts.ShortComparator;
import it.unimi.dsi.fastutil.shorts.ShortHeaps;
import it.unimi.dsi.fastutil.shorts.ShortPriorityQueue;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class ShortHeapPriorityQueue
implements ShortPriorityQueue,
Serializable {
    private static final long serialVersionUID = 1L;
    protected transient short[] heap = ShortArrays.EMPTY_ARRAY;
    protected int size;
    protected ShortComparator c;

    public ShortHeapPriorityQueue(int capacity, ShortComparator c) {
        if (capacity > 0) {
            this.heap = new short[capacity];
        }
        this.c = c;
    }

    public ShortHeapPriorityQueue(int capacity) {
        this(capacity, null);
    }

    public ShortHeapPriorityQueue(ShortComparator c) {
        this(0, c);
    }

    public ShortHeapPriorityQueue() {
        this(0, null);
    }

    public ShortHeapPriorityQueue(short[] a, int size, ShortComparator c) {
        this(c);
        this.heap = a;
        this.size = size;
        ShortHeaps.makeHeap(a, size, c);
    }

    public ShortHeapPriorityQueue(short[] a, ShortComparator c) {
        this(a, a.length, c);
    }

    public ShortHeapPriorityQueue(short[] a, int size) {
        this(a, size, null);
    }

    public ShortHeapPriorityQueue(short[] a) {
        this(a, a.length);
    }

    public ShortHeapPriorityQueue(ShortCollection collection, ShortComparator c) {
        this(collection.toShortArray(), c);
    }

    public ShortHeapPriorityQueue(ShortCollection collection) {
        this(collection, (ShortComparator)null);
    }

    public ShortHeapPriorityQueue(Collection<? extends Short> collection, ShortComparator c) {
        this(collection.size(), c);
        Iterator<? extends Short> iterator = collection.iterator();
        int size = collection.size();
        for (int i = 0; i < size; ++i) {
            this.heap[i] = iterator.next();
        }
    }

    public ShortHeapPriorityQueue(Collection<? extends Short> collection) {
        this(collection, null);
    }

    @Override
    public void enqueue(short x) {
        if (this.size == this.heap.length) {
            this.heap = ShortArrays.grow(this.heap, this.size + 1);
        }
        this.heap[this.size++] = x;
        ShortHeaps.upHeap(this.heap, this.size, this.size - 1, this.c);
    }

    @Override
    public short dequeueShort() {
        if (this.size == 0) {
            throw new NoSuchElementException();
        }
        short result = this.heap[0];
        this.heap[0] = this.heap[--this.size];
        if (this.size != 0) {
            ShortHeaps.downHeap(this.heap, this.size, 0, this.c);
        }
        return result;
    }

    @Override
    public short firstShort() {
        if (this.size == 0) {
            throw new NoSuchElementException();
        }
        return this.heap[0];
    }

    @Override
    public void changed() {
        ShortHeaps.downHeap(this.heap, this.size, 0, this.c);
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
        this.heap = ShortArrays.trim(this.heap, this.size);
    }

    @Override
    public ShortComparator comparator() {
        return this.c;
    }

    private void writeObject(ObjectOutputStream s) throws IOException {
        s.defaultWriteObject();
        s.writeInt(this.heap.length);
        for (int i = 0; i < this.size; ++i) {
            s.writeShort(this.heap[i]);
        }
    }

    private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
        s.defaultReadObject();
        this.heap = new short[s.readInt()];
        for (int i = 0; i < this.size; ++i) {
            this.heap[i] = s.readShort();
        }
    }
}

