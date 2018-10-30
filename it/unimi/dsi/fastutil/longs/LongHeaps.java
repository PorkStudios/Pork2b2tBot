/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.longs.LongComparator;

public final class LongHeaps {
    private LongHeaps() {
    }

    public static int downHeap(long[] heap, int size, int i, LongComparator c) {
        long e;
        assert (i < size);
        e = heap[i];
        if (c == null) {
            int child;
            while ((child = (i << 1) + 1) < size) {
                long t = heap[child];
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
                long t = heap[child];
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

    public static int upHeap(long[] heap, int size, int i, LongComparator c) {
        assert (i < size);
        long e = heap[i];
        if (c == null) {
            int parent;
            long t;
            while (i != 0 && (t = heap[parent = i - 1 >>> 1]) > e) {
                heap[i] = t;
                i = parent;
            }
        } else {
            int parent;
            long t;
            while (i != 0 && c.compare(t = heap[parent = i - 1 >>> 1], e) > 0) {
                heap[i] = t;
                i = parent;
            }
        }
        heap[i] = e;
        return i;
    }

    public static void makeHeap(long[] heap, int size, LongComparator c) {
        int i = size >>> 1;
        while (i-- != 0) {
            LongHeaps.downHeap(heap, size, i, c);
        }
    }
}

