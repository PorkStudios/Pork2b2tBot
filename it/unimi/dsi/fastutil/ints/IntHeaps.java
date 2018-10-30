/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.ints;

import it.unimi.dsi.fastutil.ints.IntComparator;

public final class IntHeaps {
    private IntHeaps() {
    }

    public static int downHeap(int[] heap, int size, int i, IntComparator c) {
        int e;
        assert (i < size);
        e = heap[i];
        if (c == null) {
            int child;
            while ((child = (i << 1) + 1) < size) {
                int t = heap[child];
                int right = child + 1;
                if (right < size && heap[right] < t) {
                    child = right;
                    t = heap[child];
                }
                if (e > t) {
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
                if (right < size && c.compare(heap[right], t) < 0) {
                    child = right;
                    t = heap[child];
                }
                if (c.compare(e, t) > 0) {
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

    public static int upHeap(int[] heap, int size, int i, IntComparator c) {
        assert (i < size);
        int e = heap[i];
        if (c == null) {
            int parent;
            int t;
            while (i != 0 && (t = heap[parent = i - 1 >>> 1]) > e) {
                heap[i] = t;
                i = parent;
            }
        } else {
            int parent;
            int t;
            while (i != 0 && c.compare(t = heap[parent = i - 1 >>> 1], e) > 0) {
                heap[i] = t;
                i = parent;
            }
        }
        heap[i] = e;
        return i;
    }

    public static void makeHeap(int[] heap, int size, IntComparator c) {
        int i = size >>> 1;
        while (i-- != 0) {
            IntHeaps.downHeap(heap, size, i, c);
        }
    }
}

