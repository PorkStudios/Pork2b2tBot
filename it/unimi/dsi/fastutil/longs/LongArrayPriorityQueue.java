/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.longs.LongArrays;
import it.unimi.dsi.fastutil.longs.LongComparator;
import it.unimi.dsi.fastutil.longs.LongPriorityQueue;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Comparator;
import java.util.NoSuchElementException;

public class LongArrayPriorityQueue
implements LongPriorityQueue,
Serializable {
    private static final long serialVersionUID = 1L;
    protected transient long[] array = LongArrays.EMPTY_ARRAY;
    protected int size;
    protected LongComparator c;
    protected transient int firstIndex;
    protected transient boolean firstIndexValid;

    public LongArrayPriorityQueue(int capacity, LongComparator c) {
        if (capacity > 0) {
            this.array = new long[capacity];
        }
        this.c = c;
    }

    public LongArrayPriorityQueue(int capacity) {
        this(capacity, null);
    }

    public LongArrayPriorityQueue(LongComparator c) {
        this(0, c);
    }

    public LongArrayPriorityQueue() {
        this(0, null);
    }

    public LongArrayPriorityQueue(long[] a, int size, LongComparator c) {
        this(c);
        this.array = a;
        this.size = size;
    }

    public LongArrayPriorityQueue(long[] a, LongComparator c) {
        this(a, a.length, c);
    }

    public LongArrayPriorityQueue(long[] a, int size) {
        this(a, size, null);
    }

    public LongArrayPriorityQueue(long[] a) {
        this(a, a.length);
    }

    private int findFirst() {
        if (this.firstIndexValid) {
            return this.firstIndex;
        }
        this.firstIndexValid = true;
        int i = this.size;
        int firstIndex = --i;
        long first = this.array[firstIndex];
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
    public void enqueue(long x) {
        if (this.size == this.array.length) {
            this.array = LongArrays.grow(this.array, this.size + 1);
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
    public long dequeueLong() {
        this.ensureNonEmpty();
        int first = this.findFirst();
        long result = this.array[first];
        System.arraycopy(this.array, first + 1, this.array, first, --this.size - first);
        this.firstIndexValid = false;
        return result;
    }

    @Override
    public long firstLong() {
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
        this.array = LongArrays.trim(this.array, this.size);
    }

    @Override
    public LongComparator comparator() {
        return this.c;
    }

    private void writeObject(ObjectOutputStream s) throws IOException {
        s.defaultWriteObject();
        s.writeInt(this.array.length);
        for (int i = 0; i < this.size; ++i) {
            s.writeLong(this.array[i]);
        }
    }

    private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
        s.defaultReadObject();
        this.array = new long[s.readInt()];
        for (int i = 0; i < this.size; ++i) {
            this.array[i] = s.readLong();
        }
    }
}

