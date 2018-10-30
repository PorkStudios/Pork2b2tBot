/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.bytes.ByteArrays;
import it.unimi.dsi.fastutil.bytes.ByteCollection;
import it.unimi.dsi.fastutil.bytes.ByteComparator;
import it.unimi.dsi.fastutil.bytes.ByteHeaps;
import it.unimi.dsi.fastutil.bytes.BytePriorityQueue;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class ByteHeapPriorityQueue
implements BytePriorityQueue,
Serializable {
    private static final long serialVersionUID = 1L;
    protected transient byte[] heap = ByteArrays.EMPTY_ARRAY;
    protected int size;
    protected ByteComparator c;

    public ByteHeapPriorityQueue(int capacity, ByteComparator c) {
        if (capacity > 0) {
            this.heap = new byte[capacity];
        }
        this.c = c;
    }

    public ByteHeapPriorityQueue(int capacity) {
        this(capacity, null);
    }

    public ByteHeapPriorityQueue(ByteComparator c) {
        this(0, c);
    }

    public ByteHeapPriorityQueue() {
        this(0, null);
    }

    public ByteHeapPriorityQueue(byte[] a, int size, ByteComparator c) {
        this(c);
        this.heap = a;
        this.size = size;
        ByteHeaps.makeHeap(a, size, c);
    }

    public ByteHeapPriorityQueue(byte[] a, ByteComparator c) {
        this(a, a.length, c);
    }

    public ByteHeapPriorityQueue(byte[] a, int size) {
        this(a, size, null);
    }

    public ByteHeapPriorityQueue(byte[] a) {
        this(a, a.length);
    }

    public ByteHeapPriorityQueue(ByteCollection collection, ByteComparator c) {
        this(collection.toByteArray(), c);
    }

    public ByteHeapPriorityQueue(ByteCollection collection) {
        this(collection, (ByteComparator)null);
    }

    public ByteHeapPriorityQueue(Collection<? extends Byte> collection, ByteComparator c) {
        this(collection.size(), c);
        Iterator<? extends Byte> iterator = collection.iterator();
        int size = collection.size();
        for (int i = 0; i < size; ++i) {
            this.heap[i] = iterator.next();
        }
    }

    public ByteHeapPriorityQueue(Collection<? extends Byte> collection) {
        this(collection, null);
    }

    @Override
    public void enqueue(byte x) {
        if (this.size == this.heap.length) {
            this.heap = ByteArrays.grow(this.heap, this.size + 1);
        }
        this.heap[this.size++] = x;
        ByteHeaps.upHeap(this.heap, this.size, this.size - 1, this.c);
    }

    @Override
    public byte dequeueByte() {
        if (this.size == 0) {
            throw new NoSuchElementException();
        }
        byte result = this.heap[0];
        this.heap[0] = this.heap[--this.size];
        if (this.size != 0) {
            ByteHeaps.downHeap(this.heap, this.size, 0, this.c);
        }
        return result;
    }

    @Override
    public byte firstByte() {
        if (this.size == 0) {
            throw new NoSuchElementException();
        }
        return this.heap[0];
    }

    @Override
    public void changed() {
        ByteHeaps.downHeap(this.heap, this.size, 0, this.c);
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
        this.heap = ByteArrays.trim(this.heap, this.size);
    }

    @Override
    public ByteComparator comparator() {
        return this.c;
    }

    private void writeObject(ObjectOutputStream s) throws IOException {
        s.defaultWriteObject();
        s.writeInt(this.heap.length);
        for (int i = 0; i < this.size; ++i) {
            s.writeByte(this.heap[i]);
        }
    }

    private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
        s.defaultReadObject();
        this.heap = new byte[s.readInt()];
        for (int i = 0; i < this.size; ++i) {
            this.heap[i] = s.readByte();
        }
    }
}

