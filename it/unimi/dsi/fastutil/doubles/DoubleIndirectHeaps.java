/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.doubles;

import it.unimi.dsi.fastutil.doubles.DoubleArrays;
import it.unimi.dsi.fastutil.doubles.DoubleComparator;
import java.util.Arrays;

public final class DoubleIndirectHeaps {
    private DoubleIndirectHeaps() {
    }

    public static int downHeap(double[] refArray, int[] heap, int[] inv, int size, int i, DoubleComparator c) {
        int e;
        assert (i < size);
        e = heap[i];
        double E = refArray[e];
        if (c == null) {
            int child;
            while ((child = (i << 1) + 1) < size) {
                int t = heap[child];
                int right = child + 1;
                if (right < size && Double.compare(refArray[heap[right]], refArray[t]) < 0) {
                    child = right;
                    t = heap[child];
                }
                if (Double.compare(E, refArray[t]) > 0) {
                    heap[i] = t;
                    inv[heap[i]] = i;
                    i = child;
                    continue;
                }
                break;
            }
        } else {
            int child;
            while ((child = (i << 1) + 1) < size) {
                int t = heap[child];
                int right = child + 1;
                if (right < size && c.compare(refArray[heap[right]], refArray[t]) < 0) {
                    child = right;
                    t = heap[child];
                }
                if (c.compare(E, refArray[t]) > 0) {
                    heap[i] = t;
                    inv[heap[i]] = i;
                    i = child;
                    continue;
                }
                break;
            }
        }
        heap[i] = e;
        inv[e] = i;
        return i;
    }

    public static int upHeap(double[] refArray, int[] heap, int[] inv, int size, int i, DoubleComparator c) {
        assert (i < size);
        int e = heap[i];
        double E = refArray[e];
        if (c == null) {
            int parent;
            int t;
            while (i != 0 && Double.compare(refArray[t = heap[parent = i - 1 >>> 1]], E) > 0) {
                heap[i] = t;
                inv[heap[i]] = i;
                i = parent;
            }
        } else {
            int parent;
            int t;
            while (i != 0 && c.compare(refArray[t = heap[parent = i - 1 >>> 1]], E) > 0) {
                heap[i] = t;
                inv[heap[i]] = i;
                i = parent;
            }
        }
        heap[i] = e;
        inv[e] = i;
        return i;
    }

    public static void makeHeap(double[] refArray, int offset, int length, int[] heap, int[] inv, DoubleComparator c) {
        DoubleArrays.ensureOffsetLength(refArray, offset, length);
        if (heap.length < length) {
            throw new IllegalArgumentException("The heap length (" + heap.length + ") is smaller than the number of elements (" + length + ")");
        }
        if (inv.length < refArray.length) {
            throw new IllegalArgumentException("The inversion array length (" + heap.length + ") is smaller than the length of the reference array (" + refArray.length + ")");
        }
        Arrays.fill(inv, 0, refArray.length, -1);
        int i = length;
        while (i-- != 0) {
            heap[i] = offset + i;
            inv[heap[i]] = i;
        }
        i = length >>> 1;
        while (i-- != 0) {
            DoubleIndirectHeaps.downHeap(refArray, heap, inv, length, i, c);
        }
    }

    public static void makeHeap(double[] refArray, int[] heap, int[] inv, int size, DoubleComparator c) {
        int i = size >>> 1;
        while (i-- != 0) {
            DoubleIndirectHeaps.downHeap(refArray, heap, inv, size, i, c);
        }
    }
}

