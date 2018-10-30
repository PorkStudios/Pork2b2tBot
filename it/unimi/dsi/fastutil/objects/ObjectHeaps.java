/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.objects;

import java.util.Comparator;

public final class ObjectHeaps {
    private ObjectHeaps() {
    }

    public static <K> int downHeap(K[] heap, int size, int i, Comparator<? super K> c) {
        K e;
        assert (i < size);
        e = heap[i];
        if (c == null) {
            int child;
            while ((child = (i << 1) + 1) < size) {
                K t = heap[child];
                int right = child + 1;
                if (right < size && ((Comparable)heap[right]).compareTo(t) < 0) {
                    child = right;
                    t = heap[child];
                }
                if (((Comparable)e).compareTo(t) > 0) {
                    heap[i] = t;
                    i = child;
                    continue;
                }
                break;
            }
        } else {
            int child;
            while ((child = (i << 1) + 1) < size) {
                K t = heap[child];
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

    public static <K> int upHeap(K[] heap, int size, int i, Comparator<K> c) {
        assert (i < size);
        K e = heap[i];
        if (c == null) {
            int parent;
            K t;
            while (i != 0 && ((Comparable)(t = heap[parent = i - 1 >>> 1])).compareTo(e) > 0) {
                heap[i] = t;
                i = parent;
            }
        } else {
            int parent;
            K t;
            while (i != 0 && c.compare(t = heap[parent = i - 1 >>> 1], e) > 0) {
                heap[i] = t;
                i = parent;
            }
        }
        heap[i] = e;
        return i;
    }

    public static <K> void makeHeap(K[] heap, int size, Comparator<K> c) {
        int i = size >>> 1;
        while (i-- != 0) {
            ObjectHeaps.downHeap(heap, size, i, c);
        }
    }
}

