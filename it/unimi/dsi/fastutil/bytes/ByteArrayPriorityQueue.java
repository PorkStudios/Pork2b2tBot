/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.bytes.ByteArrays;
import it.unimi.dsi.fastutil.bytes.ByteComparator;
import it.unimi.dsi.fastutil.bytes.BytePriorityQueue;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Comparator;
import java.util.NoSuchElementException;

public class ByteArrayPriorityQueue
implements BytePriorityQueue,
Serializable {
    private static final long serialVersionUID = 1L;
    protected transient byte[] array = ByteArrays.EMPTY_ARRAY;
    protected int size;
    protected ByteComparator c;
    protected transient int firstIndex;
    protected transient boolean firstIndexValid;

    public ByteArrayPriorityQueue(int capacity, ByteComparator c) {
        if (capacity > 0) {
            this.array = new byte[capacity];
        }
        this.c = c;
    }

    public ByteArrayPriorityQueue(int capacity) {
        this(capacity, null);
    }

    public ByteArrayPriorityQueue(ByteComparator c) {
        this(0, c);
    }

    public ByteArrayPriorityQueue() {
        this(0, null);
    }

    public ByteArrayPriorityQueue(byte[] a, int size, ByteComparator c) {
        this(c);
        this.array = a;
        this.size = size;
    }

    public ByteArrayPriorityQueue(byte[] a, ByteComparator c) {
        this(a, a.length, c);
    }

    public ByteArrayPriorityQueue(byte[] a, int size) {
        this(a, size, null);
    }

    public ByteArrayPriorityQueue(byte[] a) {
        this(a, a.length);
    }

    private int findFirst() {
        if (this.firstIndexValid) {
            return this.firstIndex;
        }
        this.firstIndexValid = true;
        int i = this.size;
        int firstIndex = --i;
        byte first = this.array[firstIndex];
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
    public void enqueue(byte x) {
        if (this.size == this.array.length) {
            this.array = ByteArrays.grow(this.array, this.size + 1);
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
    public byte dequeueByte() {
        this.ensureNonEmpty();
        int first = this.findFirst();
        byte result = this.array[first];
        System.arraycopy(this.array, first + 1, this.array, first, --this.size - first);
        this.firstIndexValid = false;
        return result;
    }

    @Override
    public byte firstByte() {
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
        this.array = ByteArrays.trim(this.array, this.size);
    }

    @Override
    public ByteComparator comparator() {
        return this.c;
    }

    private void writeObject(ObjectOutputStream s) throws IOException {
        s.defaultWriteObject();
        s.writeInt(this.array.length);
        for (int i = 0; i < this.size; ++i) {
            s.writeByte(this.array[i]);
        }
    }

    private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
        s.defaultReadObject();
        this.array = new byte[s.readInt()];
        for (int i = 0; i < this.size; ++i) {
            this.array[i] = s.readByte();
        }
    }
}

