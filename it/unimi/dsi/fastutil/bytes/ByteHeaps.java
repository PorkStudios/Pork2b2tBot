/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.bytes.ByteComparator;

public final class ByteHeaps {
    private ByteHeaps() {
    }

    public static int downHeap(byte[] heap, int size, int i, ByteComparator c) {
        byte e;
        assert (i < size);
        e = heap[i];
        if (c == null) {
            int child;
            while ((child = (i << 1) + 1) < size) {
                byte t = heap[child];
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
                byte t = heap[child];
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

    public static int upHeap(byte[] heap, int size, int i, ByteComparator c) {
        assert (i < size);
        byte e = heap[i];
        if (c == null) {
            int parent;
            byte t;
            while (i != 0 && (t = heap[parent = i - 1 >>> 1]) > e) {
                heap[i] = t;
                i = parent;
            }
        } else {
            byte t;
            int parent;
            while (i != 0 && c.compare(t = heap[parent = i - 1 >>> 1], e) > 0) {
                heap[i] = t;
                i = parent;
            }
        }
        heap[i] = e;
        return i;
    }

    public static void makeHeap(byte[] heap, int size, ByteComparator c) {
        int i = size >>> 1;
        while (i-- != 0) {
            ByteHeaps.downHeap(heap, size, i, c);
        }
    }
}

