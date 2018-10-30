/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.floats.FloatComparator;

public final class FloatHeaps {
    private FloatHeaps() {
    }

    public static int downHeap(float[] heap, int size, int i, FloatComparator c) {
        float e;
        assert (i < size);
        e = heap[i];
        if (c == null) {
            int child;
            while ((child = (i << 1) + 1) < size) {
                float t = heap[child];
                int right = child + 1;
                if (right < size && Float.compare(heap[right], t) < 0) {
                    child = right;
                    t = heap[child];
                }
                if (Float.compare(e, t) > 0) {
                    heap[i] = t;
                    i = child;
                    continue;
                }
                break;
            }
        } else {
            int child;
            while ((child = (i << 1) + 1) < size) {
                float t = heap[child];
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

    public static int upHeap(float[] heap, int size, int i, FloatComparator c) {
        assert (i < size);
        float e = heap[i];
        if (c == null) {
            int parent;
            float t;
            while (i != 0 && Float.compare(t = heap[parent = i - 1 >>> 1], e) > 0) {
                heap[i] = t;
                i = parent;
            }
        } else {
            int parent;
            float t;
            while (i != 0 && c.compare(t = heap[parent = i - 1 >>> 1], e) > 0) {
                heap[i] = t;
                i = parent;
            }
        }
        heap[i] = e;
        return i;
    }

    public static void makeHeap(float[] heap, int size, FloatComparator c) {
        int i = size >>> 1;
        while (i-- != 0) {
            FloatHeaps.downHeap(heap, size, i, c);
        }
    }
}

