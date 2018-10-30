/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.chars.CharComparator;
import it.unimi.dsi.fastutil.chars.CharIndirectPriorityQueue;
import it.unimi.dsi.fastutil.chars.CharSemiIndirectHeaps;
import it.unimi.dsi.fastutil.ints.IntArrays;
import java.util.Comparator;
import java.util.NoSuchElementException;

public class CharHeapSemiIndirectPriorityQueue
implements CharIndirectPriorityQueue {
    protected final char[] refArray;
    protected int[] heap = IntArrays.EMPTY_ARRAY;
    protected int size;
    protected CharComparator c;

    public CharHeapSemiIndirectPriorityQueue(char[] refArray, int capacity, CharComparator c) {
        if (capacity > 0) {
            this.heap = new int[capacity];
        }
        this.refArray = refArray;
        this.c = c;
    }

    public CharHeapSemiIndirectPriorityQueue(char[] refArray, int capacity) {
        this(refArray, capacity, null);
    }

    public CharHeapSemiIndirectPriorityQueue(char[] refArray, CharComparator c) {
        this(refArray, refArray.length, c);
    }

    public CharHeapSemiIndirectPriorityQueue(char[] refArray) {
        this(refArray, refArray.length, null);
    }

    public CharHeapSemiIndirectPriorityQueue(char[] refArray, int[] a, int size, CharComparator c) {
        this(refArray, 0, c);
        this.heap = a;
        this.size = size;
        CharSemiIndirectHeaps.makeHeap(refArray, a, size, c);
    }

    public CharHeapSemiIndirectPriorityQueue(char[] refArray, int[] a, CharComparator c) {
        this(refArray, a, a.length, c);
    }

    public CharHeapSemiIndirectPriorityQueue(char[] refArray, int[] a, int size) {
        this(refArray, a, size, null);
    }

    public CharHeapSemiIndirectPriorityQueue(char[] refArray, int[] a) {
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
        CharSemiIndirectHeaps.upHeap(this.refArray, this.heap, this.size, this.size - 1, this.c);
    }

    @Override
    public int dequeue() {
        if (this.size == 0) {
            throw new NoSuchElementException();
        }
        int result = this.heap[0];
        this.heap[0] = this.heap[--this.size];
        if (this.size != 0) {
            CharSemiIndirectHeaps.downHeap(this.refArray, this.heap, this.size, 0, this.c);
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
        CharSemiIndirectHeaps.downHeap(this.refArray, this.heap, this.size, 0, this.c);
    }

    @Override
    public void allChanged() {
        CharSemiIndirectHeaps.makeHeap(this.refArray, this.heap, this.size, this.c);
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
    public CharComparator comparator() {
        return this.c;
    }

    @Override
    public int front(int[] a) {
        return this.c == null ? CharSemiIndirectHeaps.front(this.refArray, this.heap, this.size, a) : CharSemiIndirectHeaps.front(this.refArray, this.heap, this.size, a, this.c);
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

