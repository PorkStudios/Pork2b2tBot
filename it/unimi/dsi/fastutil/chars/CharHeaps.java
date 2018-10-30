/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.chars.CharComparator;

public final class CharHeaps {
    private CharHeaps() {
    }

    public static int downHeap(char[] heap, int size, int i, CharComparator c) {
        char e;
        assert (i < size);
        e = heap[i];
        if (c == null) {
            int child;
            while ((child = (i << 1) + 1) < size) {
                char t = heap[child];
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
                char t = heap[child];
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

    public static int upHeap(char[] heap, int size, int i, CharComparator c) {
        assert (i < size);
        char e = heap[i];
        if (c == null) {
            int parent;
            char t;
            while (i != 0 && (t = heap[parent = i - 1 >>> 1]) > e) {
                heap[i] = t;
                i = parent;
            }
        } else {
            int parent;
            char t;
            while (i != 0 && c.compare(t = heap[parent = i - 1 >>> 1], e) > 0) {
                heap[i] = t;
                i = parent;
            }
        }
        heap[i] = e;
        return i;
    }

    public static void makeHeap(char[] heap, int size, CharComparator c) {
        int i = size >>> 1;
        while (i-- != 0) {
            CharHeaps.downHeap(heap, size, i, c);
        }
    }
}

