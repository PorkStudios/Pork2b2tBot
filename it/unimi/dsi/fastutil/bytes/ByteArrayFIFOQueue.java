/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.bytes.ByteComparator;
import it.unimi.dsi.fastutil.bytes.BytePriorityQueue;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Comparator;
import java.util.NoSuchElementException;

public class ByteArrayFIFOQueue
implements BytePriorityQueue,
Serializable {
    private static final long serialVersionUID = 0L;
    public static final int INITIAL_CAPACITY = 4;
    protected transient byte[] array;
    protected transient int length;
    protected transient int start;
    protected transient int end;

    public ByteArrayFIFOQueue(int capacity) {
        if (capacity < 0) {
            throw new IllegalArgumentException("Initial capacity (" + capacity + ") is negative");
        }
        this.array = new byte[Math.max(1, capacity)];
        this.length = this.array.length;
    }

    public ByteArrayFIFOQueue() {
        this(4);
    }

    @Override
    public ByteComparator comparator() {
        return null;
    }

    @Override
    public byte dequeueByte() {
        if (this.start == this.end) {
            throw new NoSuchElementException();
        }
        byte t = this.array[this.start];
        if (++this.start == this.length) {
            this.start = 0;
        }
        this.reduce();
        return t;
    }

    public byte dequeueLastByte() {
        if (this.start == this.end) {
            throw new NoSuchElementException();
        }
        if (this.end == 0) {
            this.end = this.length;
        }
        byte t = this.array[--this.end];
        this.reduce();
        return t;
    }

    private final void resize(int size, int newLength) {
        byte[] newArray = new byte[newLength];
        if (this.start >= this.end) {
            if (size != 0) {
                System.arraycopy(this.array, this.start, newArray, 0, this.length - this.start);
                System.arraycopy(this.array, 0, newArray, this.length - this.start, this.end);
            }
        } else {
            System.arraycopy(this.array, this.start, newArray, 0, this.end - this.start);
        }
        this.start = 0;
        this.end = size;
        this.array = newArray;
        this.length = newLength;
    }

    private final void expand() {
        this.resize(this.length, (int)Math.min(0x7FFFFFF7L, 2L * (long)this.length));
    }

    private final void reduce() {
        int size = this.size();
        if (this.length > 4 && size <= this.length / 4) {
            this.resize(size, this.length / 2);
        }
    }

    @Override
    public void enqueue(byte x) {
        this.array[this.end++] = x;
        if (this.end == this.length) {
            this.end = 0;
        }
        if (this.end == this.start) {
            this.expand();
        }
    }

    public void enqueueFirst(byte x) {
        if (this.start == 0) {
            this.start = this.length;
        }
        this.array[--this.start] = x;
        if (this.end == this.start) {
            this.expand();
        }
    }

    @Override
    public byte firstByte() {
        if (this.start == this.end) {
            throw new NoSuchElementException();
        }
        return this.array[this.start];
    }

    @Override
    public byte lastByte() {
        if (this.start == this.end) {
            throw new NoSuchElementException();
        }
        return this.array[(this.end == 0 ? this.length : this.end) - 1];
    }

    @Override
    public void clear() {
        this.end = 0;
        this.start = 0;
    }

    public void trim() {
        int size = this.size();
        byte[] newArray = new byte[size + 1];
        if (this.start <= this.end) {
            System.arraycopy(this.array, this.start, newArray, 0, this.end - this.start);
        } else {
            System.arraycopy(this.array, this.start, newArray, 0, this.length - this.start);
            System.arraycopy(this.array, 0, newArray, this.length - this.start, this.end);
        }
        this.start = 0;
        this.end = size;
        this.length = this.end + 1;
        this.array = newArray;
    }

    @Override
    public int size() {
        int apparentLength = this.end - this.start;
        return apparentLength >= 0 ? apparentLength : this.length + apparentLength;
    }

    private void writeObject(ObjectOutputStream s) throws IOException {
        s.defaultWriteObject();
        int size = this.size();
        s.writeInt(size);
        int i = this.start;
        while (size-- != 0) {
            s.writeByte(this.array[i++]);
            if (i != this.length) continue;
            i = 0;
        }
    }

    private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
        s.defaultReadObject();
        this.end = s.readInt();
        this.length = HashCommon.nextPowerOfTwo(this.end + 1);
        this.array = new byte[this.length];
        for (int i = 0; i < this.end; ++i) {
            this.array[i] = s.readByte();
        }
    }
}

