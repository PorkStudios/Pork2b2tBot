/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.shorts;

import it.unimi.dsi.fastutil.ints.IntArrays;
import it.unimi.dsi.fastutil.shorts.ShortArrays;
import it.unimi.dsi.fastutil.shorts.ShortComparator;

public final class ShortSemiIndirectHeaps {
    private ShortSemiIndirectHeaps() {
    }

    public static int downHeap(short[] refArray, int[] heap, int size, int i, ShortComparator c) {
        int e;
        assert (i < size);
        e = heap[i];
        short E = refArray[e];
        if (c == null) {
            int child;
            while ((child = (i << 1) + 1) < size) {
                int t = heap[child];
                int right = child + 1;
                if (right < size && refArray[heap[right]] < refArray[t]) {
                    child = right;
                    t = heap[child];
                }
                if (E > refArray[t]) {
                    heap[i] = t;
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
                    i = child;
                    continue;
                }
                break;
            }
        }
        heap[i] = e;
        return i;
    }

    public static int upHeap(short[] refArray, int[] heap, int size, int i, ShortComparator c) {
        assert (i < size);
        int e = heap[i];
        short E = refArray[e];
        if (c == null) {
            int parent;
            int t;
            while (i != 0 && refArray[t = heap[parent = i - 1 >>> 1]] > E) {
                heap[i] = t;
                i = parent;
            }
        } else {
            int t;
            int parent;
            while (i != 0 && c.compare(refArray[t = heap[parent = i - 1 >>> 1]], E) > 0) {
                heap[i] = t;
                i = parent;
            }
        }
        heap[i] = e;
        return i;
    }

    public static void makeHeap(short[] refArray, int offset, int length, int[] heap, ShortComparator c) {
        ShortArrays.ensureOffsetLength(refArray, offset, length);
        if (heap.length < length) {
            throw new IllegalArgumentException("The heap length (" + heap.length + ") is smaller than the number of elements (" + length + ")");
        }
        int i = length;
        while (i-- != 0) {
            heap[i] = offset + i;
        }
        i = length >>> 1;
        while (i-- != 0) {
            ShortSemiIndirectHeaps.downHeap(refArray, heap, length, i, c);
        }
    }

    public static int[] makeHeap(short[] refArray, int offset, int length, ShortComparator c) {
        int[] heap = length <= 0 ? IntArrays.EMPTY_ARRAY : new int[length];
        ShortSemiIndirectHeaps.makeHeap(refArray, offset, length, heap, c);
        return heap;
    }

    public static void makeHeap(short[] refArray, int[] heap, int size, ShortComparator c) {
        int i = size >>> 1;
        while (i-- != 0) {
            ShortSemiIndirectHeaps.downHeap(refArray, heap, size, i, c);
        }
    }

    public static int front(short[] refArray, int[] heap, int size, int[] a) {
        short top = refArray[heap[0]];
        int j = 0;
        int l = 0;
        int r = 1;
        int f = 0;
        for (int i = 0; i < r; ++i) {
            if (i == f) {
                if (l >= r) break;
                f = (f << 1) + 1;
                i = l;
                l = -1;
            }
            if (top != refArray[heap[i]]) continue;
            a[j++] = heap[i];
            if (l == -1) {
                l = i * 2 + 1;
            }
            r = Math.min(size, i * 2 + 3);
        }
        return j;
    }

    public static int front(short[] refArray, int[] heap, int size, int[] a, ShortComparator c) {
        short top = refArray[heap[0]];
        int j = 0;
        int l = 0;
        int r = 1;
        int f = 0;
        for (int i = 0; i < r; ++i) {
            if (i == f) {
                if (l >= r) break;
                f = (f << 1) + 1;
                i = l;
                l = -1;
            }
            if (c.compare(top, refArray[heap[i]]) != 0) continue;
            a[j++] = heap[i];
            if (l == -1) {
                l = i * 2 + 1;
            }
            r = Math.min(size, i * 2 + 3);
        }
        return j;
    }
}

