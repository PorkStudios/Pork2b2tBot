/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.floats.FloatComparator;
import it.unimi.dsi.fastutil.floats.FloatIndirectPriorityQueue;
import it.unimi.dsi.fastutil.ints.IntArrays;
import java.util.Comparator;
import java.util.NoSuchElementException;

public class FloatArrayIndirectPriorityQueue
implements FloatIndirectPriorityQueue {
    protected float[] refArray;
    protected int[] array = IntArrays.EMPTY_ARRAY;
    protected int size;
    protected FloatComparator c;
    protected int firstIndex;
    protected boolean firstIndexValid;

    public FloatArrayIndirectPriorityQueue(float[] refArray, int capacity, FloatComparator c) {
        if (capacity > 0) {
            this.array = new int[capacity];
        }
        this.refArray = refArray;
        this.c = c;
    }

    public FloatArrayIndirectPriorityQueue(float[] refArray, int capacity) {
        this(refArray, capacity, null);
    }

    public FloatArrayIndirectPriorityQueue(float[] refArray, FloatComparator c) {
        this(refArray, refArray.length, c);
    }

    public FloatArrayIndirectPriorityQueue(float[] refArray) {
        this(refArray, refArray.length, null);
    }

    public FloatArrayIndirectPriorityQueue(float[] refArray, int[] a, int size, FloatComparator c) {
        this(refArray, 0, c);
        this.array = a;
        this.size = size;
    }

    public FloatArrayIndirectPriorityQueue(float[] refArray, int[] a, FloatComparator c) {
        this(refArray, a, a.length, c);
    }

    public FloatArrayIndirectPriorityQueue(float[] refArray, int[] a, int size) {
        this(refArray, a, size, null);
    }

    public FloatArrayIndirectPriorityQueue(float[] refArray, int[] a) {
        this(refArray, a, a.length);
    }

    private int findFirst() {
        if (this.firstIndexValid) {
            return this.firstIndex;
        }
        this.firstIndexValid = true;
        int i = this.size;
        int firstIndex = --i;
        float first = this.refArray[this.array[firstIndex]];
        if (this.c == null) {
            while (i-- != 0) {
                if (Float.compare(this.refArray[this.array[i]], first) >= 0) continue;
                firstIndex = i;
                first = this.refArray[this.array[firstIndex]];
            }
        } else {
            while (i-- != 0) {
                if (this.c.compare(this.refArray[this.array[i]], first) >= 0) continue;
                firstIndex = i;
                first = this.refArray[this.array[firstIndex]];
            }
        }
        this.firstIndex = firstIndex;
        return this.firstIndex;
    }

    private int findLast() {
        int lastIndex;
        int i = this.size;
        lastIndex = --i;
        float last = this.refArray[this.array[lastIndex]];
        if (this.c == null) {
            while (i-- != 0) {
                if (Float.compare(last, this.refArray[this.array[i]]) >= 0) continue;
                lastIndex = i;
                last = this.refArray[this.array[lastIndex]];
            }
        } else {
            while (i-- != 0) {
                if (this.c.compare(last, this.refArray[this.array[i]]) >= 0) continue;
                lastIndex = i;
                last = this.refArray[this.array[lastIndex]];
            }
        }
        return lastIndex;
    }

    protected final void ensureNonEmpty() {
        if (this.size == 0) {
            throw new NoSuchElementException();
        }
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
        if (this.size == this.array.length) {
            this.array = IntArrays.grow(this.array, this.size + 1);
        }
        if (this.firstIndexValid) {
            if (this.c == null) {
                if (Float.compare(this.refArray[x], this.refArray[this.array[this.firstIndex]]) < 0) {
                    this.firstIndex = this.size;
                }
            } else if (this.c.compare(this.refArray[x], this.refArray[this.array[this.firstIndex]]) < 0) {
                this.firstIndex = this.size;
            }
        } else {
            this.firstIndexValid = false;
        }
        this.array[this.size++] = x;
    }

    @Override
    public int dequeue() {
        this.ensureNonEmpty();
        int firstIndex = this.findFirst();
        int result = this.array[firstIndex];
        if (--this.size != 0) {
            System.arraycopy(this.array, firstIndex + 1, this.array, firstIndex, this.size - firstIndex);
        }
        this.firstIndexValid = false;
        return result;
    }

    @Override
    public int first() {
        this.ensureNonEmpty();
        return this.array[this.findFirst()];
    }

    @Override
    public int last() {
        this.ensureNonEmpty();
        return this.array[this.findLast()];
    }

    @Override
    public void changed() {
        this.ensureNonEmpty();
        this.firstIndexValid = false;
    }

    @Override
    public void changed(int index) {
        this.ensureElement(index);
        if (index == this.firstIndex) {
            this.firstIndexValid = false;
        }
    }

    @Override
    public void allChanged() {
        this.firstIndexValid = false;
    }

    @Override
    public boolean remove(int index) {
        this.ensureElement(index);
        int[] a = this.array;
        int i = this.size;
        while (i-- != 0 && a[i] != index) {
        }
        if (i < 0) {
            return false;
        }
        this.firstIndexValid = false;
        if (--this.size != 0) {
            System.arraycopy(a, i + 1, a, i, this.size - i);
        }
        return true;
    }

    @Override
    public int front(int[] a) {
        float top = this.refArray[this.array[this.findFirst()]];
        int i = this.size;
        int c = 0;
        while (i-- != 0) {
            if (Float.floatToIntBits(top) != Float.floatToIntBits(this.refArray[this.array[i]])) continue;
            a[c++] = this.array[i];
        }
        return c;
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
    public FloatComparator comparator() {
        return this.c;
    }

    public String toString() {
        StringBuffer s = new StringBuffer();
        s.append("[");
        for (int i = 0; i < this.size; ++i) {
            if (i != 0) {
                s.append(", ");
            }
            s.append(this.refArray[this.array[i]]);
        }
        s.append("]");
        return s.toString();
    }
}

