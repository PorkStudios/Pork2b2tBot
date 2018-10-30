/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.ints;

import it.unimi.dsi.fastutil.ints.IntArrays;
import it.unimi.dsi.fastutil.ints.IntComparator;
import it.unimi.dsi.fastutil.ints.IntPriorityQueue;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Comparator;
import java.util.NoSuchElementException;

public class IntArrayPriorityQueue
implements IntPriorityQueue,
Serializable {
    private static final long serialVersionUID = 1L;
    protected transient int[] array = IntArrays.EMPTY_ARRAY;
    protected int size;
    protected IntComparator c;
    protected transient int firstIndex;
    protected transient boolean firstIndexValid;

    public IntArrayPriorityQueue(int capacity, IntComparator c) {
        if (capacity > 0) {
            this.array = new int[capacity];
        }
        this.c = c;
    }

    public IntArrayPriorityQueue(int capacity) {
        this(capacity, null);
    }

    public IntArrayPriorityQueue(IntComparator c) {
        this(0, c);
    }

    public IntArrayPriorityQueue() {
        this(0, null);
    }

    public IntArrayPriorityQueue(int[] a, int size, IntComparator c) {
        this(c);
        this.array = a;
        this.size = size;
    }

    public IntArrayPriorityQueue(int[] a, IntComparator c) {
        this(a, a.length, c);
    }

    public IntArrayPriorityQueue(int[] a, int size) {
        this(a, size, null);
    }

    public IntArrayPriorityQueue(int[] a) {
        this(a, a.length);
    }

    private int findFirst() {
        if (this.firstIndexValid) {
            return this.firstIndex;
        }
        this.firstIndexValid = true;
        int i = this.size;
        int firstIndex = --i;
        int first = this.array[firstIndex];
        if (this.c == null) {
            while (i-- != 0) {
                if (this.array[i] >= first) continue;
                firstIndex = i;
                first = this.array[firstIndex];
            }
        } else {
            while (i-- != 0) {
                if (this.c.compare(this.array[i], first) >= 0) continue;
                firstIndex = i;
                first = this.array[firstIndex];
            }
        }
        this.firstIndex = firstIndex;
        return this.firstIndex;
    }

    private void ensureNonEmpty() {
        if (this.size == 0) {
            throw new NoSuchElementException();
        }
    }

    @Override
    public void enqueue(int x) {
        if (this.size == this.array.length) {
            this.array = IntArrays.grow(this.array, this.size + 1);
        }
        if (this.firstIndexValid) {
            if (this.c == null) {
                if (x < this.array[this.firstIndex]) {
                    this.firstIndex = this.size;
                }
            } else if (this.c.compare(x, this.array[this.firstIndex]) < 0) {
                this.firstIndex = this.size;
            }
        } else {
            this.firstIndexValid = false;
        }
        this.array[this.size++] = x;
    }

    @Override
    public int dequeueInt() {
        this.ensureNonEmpty();
        int first = this.findFirst();
        int result = this.array[first];
        System.arraycopy(this.array, first + 1, this.array, first, --this.size - first);
        this.firstIndexValid = false;
        return result;
    }

    @Override
    public int firstInt() {
        this.ensureNonEmpty();
        return this.array[this.findFirst()];
    }

    @Override
    public void changed() {
        this.ensureNonEmpty();
        this.firstIndexValid = false;
    }

    @Override
    public int size() {
        return this.size;
    }

    @Override
    public void clear() {
        this.size = 0;
        this.firstIndexValid = false;
    }

    public void trim() {
        this.array = IntArrays.trim(this.array, this.size);
    }

    @Override
    public IntComparator comparator() {
        return this.c;
    }

    private void writeObject(ObjectOutputStream s) throws IOException {
        s.defaultWriteObject();
        s.writeInt(this.array.length);
        for (int i = 0; i < this.size; ++i) {
            s.writeInt(this.array[i]);
        }
    }

    private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
        s.defaultReadObject();
        this.array = new int[s.readInt()];
        for (int i = 0; i < this.size; ++i) {
            this.array[i] = s.readInt();
        }
    }
}

