/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.shorts;

import it.unimi.dsi.fastutil.shorts.ShortComparator;

public final class ShortHeaps {
    private ShortHeaps() {
    }

    public static int downHeap(short[] heap, int size, int i, ShortComparator c) {
        short e;
        assert (i < size);
        e = heap[i];
        if (c == null) {
            int child;
            while ((child = (i << 1) + 1) < size) {
                short t = heap[child];
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
                short t = heap[child];
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

    public static int upHeap(short[] heap, int size, int i, ShortComparator c) {
        assert (i < size);
        short e = heap[i];
        if (c == null) {
            int parent;
            short t;
            while (i != 0 && (t = heap[parent = i - 1 >>> 1]) > e) {
                heap[i] = t;
                i = parent;
            }
        } else {
            int parent;
            short t;
            while (i != 0 && c.compare(t = heap[parent = i - 1 >>> 1], e) > 0) {
                heap[i] = t;
                i = parent;
            }
        }
        heap[i] = e;
        return i;
    }

    public static void makeHeap(short[] heap, int size, ShortComparator c) {
        int i = size >>> 1;
        while (i-- != 0) {
            ShortHeaps.downHeap(heap, size, i, c);
        }
    }
}

