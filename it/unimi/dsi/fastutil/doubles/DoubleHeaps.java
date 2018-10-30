/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.doubles;

import it.unimi.dsi.fastutil.doubles.DoubleComparator;

public final class DoubleHeaps {
    private DoubleHeaps() {
    }

    public static int downHeap(double[] heap, int size, int i, DoubleComparator c) {
        double e;
        assert (i < size);
        e = heap[i];
        if (c == null) {
            int child;
            while ((child = (i << 1) + 1) < size) {
                double t = heap[child];
                int right = child + 1;
                if (right < size && Double.compare(heap[right], t) < 0) {
                    child = right;
                    t = heap[child];
                }
                if (Double.compare(e, t) > 0) {
                    heap[i] = t;
                    i = child;
                    continue;
                }
                break;
            }
        } else {
            int child;
            while ((child = (i << 1) + 1) < size) {
                double t = heap[child];
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

    public static int upHeap(double[] heap, int size, int i, DoubleComparator c) {
        assert (i < size);
        double e = heap[i];
        if (c == null) {
            int parent;
            double t;
            while (i != 0 && Double.compare(t = heap[parent = i - 1 >>> 1], e) > 0) {
                heap[i] = t;
                i = parent;
            }
        } else {
            int parent;
            double t;
            while (i != 0 && c.compare(t = heap[parent = i - 1 >>> 1], e) > 0) {
                heap[i] = t;
                i = parent;
            }
        }
        heap[i] = e;
        return i;
    }

    public static void makeHeap(double[] heap, int size, DoubleComparator c) {
        int i = size >>> 1;
        while (i-- != 0) {
            DoubleHeaps.downHeap(heap, size, i, c);
        }
    }
}

