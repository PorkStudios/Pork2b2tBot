/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.Arrays;
import it.unimi.dsi.fastutil.Hash;
import it.unimi.dsi.fastutil.bytes.ByteComparator;
import it.unimi.dsi.fastutil.ints.IntArrays;
import java.io.Serializable;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.atomic.AtomicInteger;

public final class ByteArrays {
    public static final byte[] EMPTY_ARRAY = new byte[0];
    private static final int QUICKSORT_NO_REC = 16;
    private static final int PARALLEL_QUICKSORT_NO_FORK = 8192;
    private static final int QUICKSORT_MEDIAN_OF_9 = 128;
    private static final int MERGESORT_NO_REC = 16;
    private static final int DIGIT_BITS = 8;
    private static final int DIGIT_MASK = 255;
    private static final int DIGITS_PER_ELEMENT = 1;
    private static final int RADIXSORT_NO_REC = 1024;
    private static final int PARALLEL_RADIXSORT_NO_FORK = 1024;
    protected static final Segment POISON_PILL = new Segment(-1, -1, -1);
    public static final Hash.Strategy<byte[]> HASH_STRATEGY = new ArrayHashStrategy();

    private ByteArrays() {
    }

    public static byte[] ensureCapacity(byte[] array, int length) {
        if (length > array.length) {
            byte[] t = new byte[length];
            System.arraycopy(array, 0, t, 0, array.length);
            return t;
        }
        return array;
    }

    public static byte[] ensureCapacity(byte[] array, int length, int preserve) {
        if (length > array.length) {
            byte[] t = new byte[length];
            System.arraycopy(array, 0, t, 0, preserve);
            return t;
        }
        return array;
    }

    public static byte[] grow(byte[] array, int length) {
        if (length > array.length) {
            int newLength = (int)Math.max(Math.min(2L * (long)array.length, 0x7FFFFFF7L), (long)length);
            byte[] t = new byte[newLength];
            System.arraycopy(array, 0, t, 0, array.length);
            return t;
        }
        return array;
    }

    public static byte[] grow(byte[] array, int length, int preserve) {
        if (length > array.length) {
            int newLength = (int)Math.max(Math.min(2L * (long)array.length, 0x7FFFFFF7L), (long)length);
            byte[] t = new byte[newLength];
            System.arraycopy(array, 0, t, 0, preserve);
            return t;
        }
        return array;
    }

    public static byte[] trim(byte[] array, int length) {
        if (length >= array.length) {
            return array;
        }
        byte[] t = length == 0 ? EMPTY_ARRAY : new byte[length];
        System.arraycopy(array, 0, t, 0, length);
        return t;
    }

    public static byte[] setLength(byte[] array, int length) {
        if (length == array.length) {
            return array;
        }
        if (length < array.length) {
            return ByteArrays.trim(array, length);
        }
        return ByteArrays.ensureCapacity(array, length);
    }

    public static byte[] copy(byte[] array, int offset, int length) {
        ByteArrays.ensureOffsetLength(array, offset, length);
        byte[] a = length == 0 ? EMPTY_ARRAY : new byte[length];
        System.arraycopy(array, offset, a, 0, length);
        return a;
    }

    public static byte[] copy(byte[] array) {
        return (byte[])array.clone();
    }

    @Deprecated
    public static void fill(byte[] array, byte value) {
        int i = array.length;
        while (i-- != 0) {
            array[i] = value;
        }
    }

    @Deprecated
    public static void fill(byte[] array, int from, int to, byte value) {
        ByteArrays.ensureFromTo(array, from, to);
        if (from == 0) {
            while (to-- != 0) {
                array[to] = value;
            }
        } else {
            for (int i = from; i < to; ++i) {
                array[i] = value;
            }
        }
    }

    @Deprecated
    public static boolean equals(byte[] a1, byte[] a2) {
        int i = a1.length;
        if (i != a2.length) {
            return false;
        }
        while (i-- != 0) {
            if (a1[i] == a2[i]) continue;
            return false;
        }
        return true;
    }

    public static void ensureFromTo(byte[] a, int from, int to) {
        Arrays.ensureFromTo(a.length, from, to);
    }

    public static void ensureOffsetLength(byte[] a, int offset, int length) {
        Arrays.ensureOffsetLength(a.length, offset, length);
    }

    public static void ensureSameLength(byte[] a, byte[] b) {
        if (a.length != b.length) {
            throw new IllegalArgumentException("Array size mismatch: " + a.length + " != " + b.length);
        }
    }

    public static void swap(byte[] x, int a, int b) {
        byte t = x[a];
        x[a] = x[b];
        x[b] = t;
    }

    public static void swap(byte[] x, int a, int b, int n) {
        int i = 0;
        while (i < n) {
            ByteArrays.swap(x, a, b);
            ++i;
            ++a;
            ++b;
        }
    }

    private static int med3(byte[] x, int a, int b, int c, ByteComparator comp) {
        int ab = comp.compare(x[a], x[b]);
        int ac = comp.compare(x[a], x[c]);
        int bc = comp.compare(x[b], x[c]);
        return ab < 0 ? (bc < 0 ? b : (ac < 0 ? c : a)) : (bc > 0 ? b : (ac > 0 ? c : a));
    }

    private static void selectionSort(byte[] a, int from, int to, ByteComparator comp) {
        for (int i = from; i < to - 1; ++i) {
            int m = i;
            for (int j = i + 1; j < to; ++j) {
                if (comp.compare(a[j], a[m]) >= 0) continue;
                m = j;
            }
            if (m == i) continue;
            byte u = a[i];
            a[i] = a[m];
            a[m] = u;
        }
    }

    private static void insertionSort(byte[] a, int from, int to, ByteComparator comp) {
        int i = from;
        while (++i < to) {
            byte t = a[i];
            int j = i;
            byte u = a[j - 1];
            while (comp.compare(t, u) < 0) {
                a[j] = u;
                if (from == j - 1) {
                    --j;
                    break;
                }
                u = a[--j - 1];
            }
            a[j] = t;
        }
    }

    public static void quickSort(byte[] x, int from, int to, ByteComparator comp) {
        int a;
        int c;
        int len = to - from;
        if (len < 16) {
            ByteArrays.selectionSort(x, from, to, comp);
            return;
        }
        int m = from + len / 2;
        int l = from;
        int n = to - 1;
        if (len > 128) {
            int s = len / 8;
            l = ByteArrays.med3(x, l, l + s, l + 2 * s, comp);
            m = ByteArrays.med3(x, m - s, m, m + s, comp);
            n = ByteArrays.med3(x, n - 2 * s, n - s, n, comp);
        }
        m = ByteArrays.med3(x, l, m, n, comp);
        byte v = x[m];
        int b = a = from;
        int d = c = to - 1;
        do {
            int comparison;
            if (b <= c && (comparison = comp.compare(x[b], v)) <= 0) {
                if (comparison == 0) {
                    ByteArrays.swap(x, a++, b);
                }
                ++b;
                continue;
            }
            while (c >= b && (comparison = comp.compare(x[c], v)) >= 0) {
                if (comparison == 0) {
                    ByteArrays.swap(x, c, d--);
                }
                --c;
            }
            if (b > c) break;
            ByteArrays.swap(x, b++, c--);
        } while (true);
        int s = Math.min(a - from, b - a);
        ByteArrays.swap(x, from, b - s, s);
        s = Math.min(d - c, to - d - 1);
        ByteArrays.swap(x, b, to - s, s);
        s = b - a;
        if (s > 1) {
            ByteArrays.quickSort(x, from, from + s, comp);
        }
        if ((s = d - c) > 1) {
            ByteArrays.quickSort(x, to - s, to, comp);
        }
    }

    public static void quickSort(byte[] x, ByteComparator comp) {
        ByteArrays.quickSort(x, 0, x.length, comp);
    }

    public static void parallelQuickSort(byte[] x, int from, int to, ByteComparator comp) {
        if (to - from < 8192) {
            ByteArrays.quickSort(x, from, to, comp);
        } else {
            ForkJoinPool pool = new ForkJoinPool(Runtime.getRuntime().availableProcessors());
            pool.invoke(new ForkJoinQuickSortComp(x, from, to, comp));
            pool.shutdown();
        }
    }

    public static void parallelQuickSort(byte[] x, ByteComparator comp) {
        ByteArrays.parallelQuickSort(x, 0, x.length, comp);
    }

    private static int med3(byte[] x, int a, int b, int c) {
        int ab = Byte.compare(x[a], x[b]);
        int ac = Byte.compare(x[a], x[c]);
        int bc = Byte.compare(x[b], x[c]);
        return ab < 0 ? (bc < 0 ? b : (ac < 0 ? c : a)) : (bc > 0 ? b : (ac > 0 ? c : a));
    }

    private static void selectionSort(byte[] a, int from, int to) {
        for (int i = from; i < to - 1; ++i) {
            int m = i;
            for (int j = i + 1; j < to; ++j) {
                if (a[j] >= a[m]) continue;
                m = j;
            }
            if (m == i) continue;
            byte u = a[i];
            a[i] = a[m];
            a[m] = u;
        }
    }

    private static void insertionSort(byte[] a, int from, int to) {
        int i = from;
        while (++i < to) {
            byte t = a[i];
            int j = i;
            byte u = a[j - 1];
            while (t < u) {
                a[j] = u;
                if (from == j - 1) {
                    --j;
                    break;
                }
                u = a[--j - 1];
            }
            a[j] = t;
        }
    }

    public static void quickSort(byte[] x, int from, int to) {
        int a;
        int c;
        int len = to - from;
        if (len < 16) {
            ByteArrays.selectionSort(x, from, to);
            return;
        }
        int m = from + len / 2;
        int l = from;
        int n = to - 1;
        if (len > 128) {
            int s = len / 8;
            l = ByteArrays.med3(x, l, l + s, l + 2 * s);
            m = ByteArrays.med3(x, m - s, m, m + s);
            n = ByteArrays.med3(x, n - 2 * s, n - s, n);
        }
        m = ByteArrays.med3(x, l, m, n);
        byte v = x[m];
        int b = a = from;
        int d = c = to - 1;
        do {
            int comparison;
            if (b <= c && (comparison = Byte.compare(x[b], v)) <= 0) {
                if (comparison == 0) {
                    ByteArrays.swap(x, a++, b);
                }
                ++b;
                continue;
            }
            while (c >= b && (comparison = Byte.compare(x[c], v)) >= 0) {
                if (comparison == 0) {
                    ByteArrays.swap(x, c, d--);
                }
                --c;
            }
            if (b > c) break;
            ByteArrays.swap(x, b++, c--);
        } while (true);
        int s = Math.min(a - from, b - a);
        ByteArrays.swap(x, from, b - s, s);
        s = Math.min(d - c, to - d - 1);
        ByteArrays.swap(x, b, to - s, s);
        s = b - a;
        if (s > 1) {
            ByteArrays.quickSort(x, from, from + s);
        }
        if ((s = d - c) > 1) {
            ByteArrays.quickSort(x, to - s, to);
        }
    }

    public static void quickSort(byte[] x) {
        ByteArrays.quickSort(x, 0, x.length);
    }

    public static void parallelQuickSort(byte[] x, int from, int to) {
        if (to - from < 8192) {
            ByteArrays.quickSort(x, from, to);
        } else {
            ForkJoinPool pool = new ForkJoinPool(Runtime.getRuntime().availableProcessors());
            pool.invoke(new ForkJoinQuickSort(x, from, to));
            pool.shutdown();
        }
    }

    public static void parallelQuickSort(byte[] x) {
        ByteArrays.parallelQuickSort(x, 0, x.length);
    }

    private static int med3Indirect(int[] perm, byte[] x, int a, int b, int c) {
        byte aa = x[perm[a]];
        byte bb = x[perm[b]];
        byte cc = x[perm[c]];
        int ab = Byte.compare(aa, bb);
        int ac = Byte.compare(aa, cc);
        int bc = Byte.compare(bb, cc);
        return ab < 0 ? (bc < 0 ? b : (ac < 0 ? c : a)) : (bc > 0 ? b : (ac > 0 ? c : a));
    }

    private static void insertionSortIndirect(int[] perm, byte[] a, int from, int to) {
        int i = from;
        while (++i < to) {
            int t = perm[i];
            int j = i;
            int u = perm[j - 1];
            while (a[t] < a[u]) {
                perm[j] = u;
                if (from == j - 1) {
                    --j;
                    break;
                }
                u = perm[--j - 1];
            }
            perm[j] = t;
        }
    }

    public static void quickSortIndirect(int[] perm, byte[] x, int from, int to) {
        int a;
        int c;
        int len = to - from;
        if (len < 16) {
            ByteArrays.insertionSortIndirect(perm, x, from, to);
            return;
        }
        int m = from + len / 2;
        int l = from;
        int n = to - 1;
        if (len > 128) {
            int s = len / 8;
            l = ByteArrays.med3Indirect(perm, x, l, l + s, l + 2 * s);
            m = ByteArrays.med3Indirect(perm, x, m - s, m, m + s);
            n = ByteArrays.med3Indirect(perm, x, n - 2 * s, n - s, n);
        }
        m = ByteArrays.med3Indirect(perm, x, l, m, n);
        byte v = x[perm[m]];
        int b = a = from;
        int d = c = to - 1;
        do {
            int comparison;
            if (b <= c && (comparison = Byte.compare(x[perm[b]], v)) <= 0) {
                if (comparison == 0) {
                    IntArrays.swap(perm, a++, b);
                }
                ++b;
                continue;
            }
            while (c >= b && (comparison = Byte.compare(x[perm[c]], v)) >= 0) {
                if (comparison == 0) {
                    IntArrays.swap(perm, c, d--);
                }
                --c;
            }
            if (b > c) break;
            IntArrays.swap(perm, b++, c--);
        } while (true);
        int s = Math.min(a - from, b - a);
        IntArrays.swap(perm, from, b - s, s);
        s = Math.min(d - c, to - d - 1);
        IntArrays.swap(perm, b, to - s, s);
        s = b - a;
        if (s > 1) {
            ByteArrays.quickSortIndirect(perm, x, from, from + s);
        }
        if ((s = d - c) > 1) {
            ByteArrays.quickSortIndirect(perm, x, to - s, to);
        }
    }

    public static void quickSortIndirect(int[] perm, byte[] x) {
        ByteArrays.quickSortIndirect(perm, x, 0, x.length);
    }

    public static void parallelQuickSortIndirect(int[] perm, byte[] x, int from, int to) {
        if (to - from < 8192) {
            ByteArrays.quickSortIndirect(perm, x, from, to);
        } else {
            ForkJoinPool pool = new ForkJoinPool(Runtime.getRuntime().availableProcessors());
            pool.invoke(new ForkJoinQuickSortIndirect(perm, x, from, to));
            pool.shutdown();
        }
    }

    public static void parallelQuickSortIndirect(int[] perm, byte[] x) {
        ByteArrays.parallelQuickSortIndirect(perm, x, 0, x.length);
    }

    public static void stabilize(int[] perm, byte[] x, int from, int to) {
        int curr = from;
        for (int i = from + 1; i < to; ++i) {
            if (x[perm[i]] == x[perm[curr]]) continue;
            if (i - curr > 1) {
                IntArrays.parallelQuickSort(perm, curr, i);
            }
            curr = i;
        }
        if (to - curr > 1) {
            IntArrays.parallelQuickSort(perm, curr, to);
        }
    }

    public static void stabilize(int[] perm, byte[] x) {
        ByteArrays.stabilize(perm, x, 0, perm.length);
    }

    private static int med3(byte[] x, byte[] y, int a, int b, int c) {
        int bc;
        int t = Byte.compare(x[a], x[b]);
        int ab = t == 0 ? Byte.compare(y[a], y[b]) : t;
        t = Byte.compare(x[a], x[c]);
        int ac = t == 0 ? Byte.compare(y[a], y[c]) : t;
        t = Byte.compare(x[b], x[c]);
        int n = bc = t == 0 ? Byte.compare(y[b], y[c]) : t;
        return ab < 0 ? (bc < 0 ? b : (ac < 0 ? c : a)) : (bc > 0 ? b : (ac > 0 ? c : a));
    }

    private static void swap(byte[] x, byte[] y, int a, int b) {
        byte t = x[a];
        byte u = y[a];
        x[a] = x[b];
        y[a] = y[b];
        x[b] = t;
        y[b] = u;
    }

    private static void swap(byte[] x, byte[] y, int a, int b, int n) {
        int i = 0;
        while (i < n) {
            ByteArrays.swap(x, y, a, b);
            ++i;
            ++a;
            ++b;
        }
    }

    private static void selectionSort(byte[] a, byte[] b, int from, int to) {
        for (int i = from; i < to - 1; ++i) {
            int m = i;
            for (int j = i + 1; j < to; ++j) {
                int u = Byte.compare(a[j], a[m]);
                if (u >= 0 && (u != 0 || b[j] >= b[m])) continue;
                m = j;
            }
            if (m == i) continue;
            byte t = a[i];
            a[i] = a[m];
            a[m] = t;
            t = b[i];
            b[i] = b[m];
            b[m] = t;
        }
    }

    public static void quickSort(byte[] x, byte[] y, int from, int to) {
        int c;
        int a;
        int len = to - from;
        if (len < 16) {
            ByteArrays.selectionSort(x, y, from, to);
            return;
        }
        int m = from + len / 2;
        int l = from;
        int n = to - 1;
        if (len > 128) {
            int s = len / 8;
            l = ByteArrays.med3(x, y, l, l + s, l + 2 * s);
            m = ByteArrays.med3(x, y, m - s, m, m + s);
            n = ByteArrays.med3(x, y, n - 2 * s, n - s, n);
        }
        m = ByteArrays.med3(x, y, l, m, n);
        byte v = x[m];
        byte w = y[m];
        int b = a = from;
        int d = c = to - 1;
        do {
            int t;
            int comparison;
            if (b <= c && (comparison = (t = Byte.compare(x[b], v)) == 0 ? Byte.compare(y[b], w) : t) <= 0) {
                if (comparison == 0) {
                    ByteArrays.swap(x, y, a++, b);
                }
                ++b;
                continue;
            }
            while (c >= b && (comparison = (t = Byte.compare(x[c], v)) == 0 ? Byte.compare(y[c], w) : t) >= 0) {
                if (comparison == 0) {
                    ByteArrays.swap(x, y, c, d--);
                }
                --c;
            }
            if (b > c) break;
            ByteArrays.swap(x, y, b++, c--);
        } while (true);
        int s = Math.min(a - from, b - a);
        ByteArrays.swap(x, y, from, b - s, s);
        s = Math.min(d - c, to - d - 1);
        ByteArrays.swap(x, y, b, to - s, s);
        s = b - a;
        if (s > 1) {
            ByteArrays.quickSort(x, y, from, from + s);
        }
        if ((s = d - c) > 1) {
            ByteArrays.quickSort(x, y, to - s, to);
        }
    }

    public static void quickSort(byte[] x, byte[] y) {
        ByteArrays.ensureSameLength(x, y);
        ByteArrays.quickSort(x, y, 0, x.length);
    }

    public static void parallelQuickSort(byte[] x, byte[] y, int from, int to) {
        if (to - from < 8192) {
            ByteArrays.quickSort(x, y, from, to);
        }
        ForkJoinPool pool = new ForkJoinPool(Runtime.getRuntime().availableProcessors());
        pool.invoke(new ForkJoinQuickSort2(x, y, from, to));
        pool.shutdown();
    }

    public static void parallelQuickSort(byte[] x, byte[] y) {
        ByteArrays.ensureSameLength(x, y);
        ByteArrays.parallelQuickSort(x, y, 0, x.length);
    }

    public static void mergeSort(byte[] a, int from, int to, byte[] supp) {
        int len = to - from;
        if (len < 16) {
            ByteArrays.insertionSort(a, from, to);
            return;
        }
        int mid = from + to >>> 1;
        ByteArrays.mergeSort(supp, from, mid, a);
        ByteArrays.mergeSort(supp, mid, to, a);
        if (supp[mid - 1] <= supp[mid]) {
            System.arraycopy(supp, from, a, from, len);
            return;
        }
        int p = from;
        int q = mid;
        for (int i = from; i < to; ++i) {
            a[i] = q >= to || p < mid && supp[p] <= supp[q] ? supp[p++] : supp[q++];
        }
    }

    public static void mergeSort(byte[] a, int from, int to) {
        ByteArrays.mergeSort(a, from, to, (byte[])a.clone());
    }

    public static void mergeSort(byte[] a) {
        ByteArrays.mergeSort(a, 0, a.length);
    }

    public static void mergeSort(byte[] a, int from, int to, ByteComparator comp, byte[] supp) {
        int len = to - from;
        if (len < 16) {
            ByteArrays.insertionSort(a, from, to, comp);
            return;
        }
        int mid = from + to >>> 1;
        ByteArrays.mergeSort(supp, from, mid, comp, a);
        ByteArrays.mergeSort(supp, mid, to, comp, a);
        if (comp.compare(supp[mid - 1], supp[mid]) <= 0) {
            System.arraycopy(supp, from, a, from, len);
            return;
        }
        int p = from;
        int q = mid;
        for (int i = from; i < to; ++i) {
            a[i] = q >= to || p < mid && comp.compare(supp[p], supp[q]) <= 0 ? supp[p++] : supp[q++];
        }
    }

    public static void mergeSort(byte[] a, int from, int to, ByteComparator comp) {
        ByteArrays.mergeSort(a, from, to, comp, (byte[])a.clone());
    }

    public static void mergeSort(byte[] a, ByteComparator comp) {
        ByteArrays.mergeSort(a, 0, a.length, comp);
    }

    public static int binarySearch(byte[] a, int from, int to, byte key) {
        --to;
        while (from <= to) {
            int mid = from + to >>> 1;
            byte midVal = a[mid];
            if (midVal < key) {
                from = mid + 1;
                continue;
            }
            if (midVal > key) {
                to = mid - 1;
                continue;
            }
            return mid;
        }
        return - from + 1;
    }

    public static int binarySearch(byte[] a, byte key) {
        return ByteArrays.binarySearch(a, 0, a.length, key);
    }

    public static int binarySearch(byte[] a, int from, int to, byte key, ByteComparator c) {
        --to;
        while (from <= to) {
            int mid = from + to >>> 1;
            byte midVal = a[mid];
            int cmp = c.compare(midVal, key);
            if (cmp < 0) {
                from = mid + 1;
                continue;
            }
            if (cmp > 0) {
                to = mid - 1;
                continue;
            }
            return mid;
        }
        return - from + 1;
    }

    public static int binarySearch(byte[] a, byte key, ByteComparator c) {
        return ByteArrays.binarySearch(a, 0, a.length, key, c);
    }

    public static void radixSort(byte[] a) {
        ByteArrays.radixSort(a, 0, a.length);
    }

    public static void radixSort(byte[] a, int from, int to) {
        if (to - from < 1024) {
            ByteArrays.quickSort(a, from, to);
            return;
        }
        boolean maxLevel = false;
        boolean stackSize = true;
        int stackPos = 0;
        int[] offsetStack = new int[1];
        int[] lengthStack = new int[1];
        int[] levelStack = new int[1];
        offsetStack[stackPos] = from;
        lengthStack[stackPos] = to - from;
        levelStack[stackPos++] = 0;
        int[] count = new int[256];
        int[] pos = new int[256];
        while (stackPos > 0) {
            int first = offsetStack[--stackPos];
            int length = lengthStack[stackPos];
            int level = levelStack[stackPos];
            int signMask = level % 1 == 0 ? 128 : 0;
            int shift = (0 - level % 1) * 8;
            int i = first + length;
            while (i-- != first) {
                int[] arrn = count;
                int n = a[i] >>> shift & 255 ^ signMask;
                arrn[n] = arrn[n] + 1;
            }
            int lastUsed = -1;
            int p = first;
            for (int i2 = 0; i2 < 256; ++i2) {
                if (count[i2] != 0) {
                    lastUsed = i2;
                }
                pos[i2] = p += count[i2];
            }
            int end = first + length - count[lastUsed];
            int c = -1;
            for (int i3 = first; i3 <= end; i3 += count[c]) {
                byte t = a[i3];
                c = t >>> shift & 255 ^ signMask;
                if (i3 < end) {
                    do {
                        int[] arrn = pos;
                        int n = c;
                        int n2 = arrn[n] - 1;
                        arrn[n] = n2;
                        int d = n2;
                        if (n2 <= i3) break;
                        byte z = t;
                        t = a[d];
                        a[d] = z;
                        c = t >>> shift & 255 ^ signMask;
                    } while (true);
                    a[i3] = t;
                }
                if (level < 0 && count[c] > 1) {
                    if (count[c] < 1024) {
                        ByteArrays.quickSort(a, i3, i3 + count[c]);
                    } else {
                        offsetStack[stackPos] = i3;
                        lengthStack[stackPos] = count[c];
                        levelStack[stackPos++] = level + 1;
                    }
                }
                count[c] = 0;
            }
        }
    }

    public static void parallelRadixSort(byte[] a, int from, int to) {
        if (to - from < 1024) {
            ByteArrays.quickSort(a, from, to);
            return;
        }
        boolean maxLevel = false;
        LinkedBlockingQueue<Segment> queue = new LinkedBlockingQueue<Segment>();
        queue.add(new Segment(from, to - from, 0));
        AtomicInteger queueSize = new AtomicInteger(1);
        int numberOfThreads = Runtime.getRuntime().availableProcessors();
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads, Executors.defaultThreadFactory());
        ExecutorCompletionService<Void> executorCompletionService = new ExecutorCompletionService<Void>(executorService);
        int j = numberOfThreads;
        while (j-- != 0) {
            executorCompletionService.submit(() -> {
                int[] count = new int[256];
                int[] pos = new int[256];
                do {
                    Segment segment;
                    if (queueSize.get() == 0) {
                        int i = numberOfThreads;
                        while (i-- != 0) {
                            queue.add(POISON_PILL);
                        }
                    }
                    if ((segment = (Segment)queue.take()) == POISON_PILL) {
                        return null;
                    }
                    int first = segment.offset;
                    int length = segment.length;
                    int level = segment.level;
                    int signMask = level % 1 == 0 ? 128 : 0;
                    int shift = (0 - level % 1) * 8;
                    int i = first + length;
                    while (i-- != first) {
                        int[] arrn = count;
                        int n = a[i] >>> shift & 255 ^ signMask;
                        arrn[n] = arrn[n] + 1;
                    }
                    int lastUsed = -1;
                    int p = first;
                    for (int i2 = 0; i2 < 256; ++i2) {
                        if (count[i2] != 0) {
                            lastUsed = i2;
                        }
                        pos[i2] = p += count[i2];
                    }
                    int end = first + length - count[lastUsed];
                    int c = -1;
                    for (int i3 = first; i3 <= end; i3 += count[c]) {
                        byte t = a[i3];
                        c = t >>> shift & 255 ^ signMask;
                        if (i3 < end) {
                            do {
                                int[] arrn = pos;
                                int n = c;
                                int n2 = arrn[n] - 1;
                                arrn[n] = n2;
                                int d = n2;
                                if (n2 <= i3) break;
                                byte z = t;
                                t = a[d];
                                a[d] = z;
                                c = t >>> shift & 255 ^ signMask;
                            } while (true);
                            a[i3] = t;
                        }
                        if (level < 0 && count[c] > 1) {
                            if (count[c] < 1024) {
                                ByteArrays.quickSort(a, i3, i3 + count[c]);
                            } else {
                                queueSize.incrementAndGet();
                                queue.add(new Segment(i3, count[c], level + 1));
                            }
                        }
                        count[c] = 0;
                    }
                    queueSize.decrementAndGet();
                } while (true);
            });
        }
        Throwable problem = null;
        int i = numberOfThreads;
        while (i-- != 0) {
            try {
                executorCompletionService.take().get();
            }
            catch (Exception e) {
                problem = e.getCause();
            }
        }
        executorService.shutdown();
        if (problem != null) {
            throw problem instanceof RuntimeException ? (RuntimeException)problem : new RuntimeException(problem);
        }
    }

    public static void parallelRadixSort(byte[] a) {
        ByteArrays.parallelRadixSort(a, 0, a.length);
    }

    public static void radixSortIndirect(int[] perm, byte[] a, boolean stable) {
        ByteArrays.radixSortIndirect(perm, a, 0, perm.length, stable);
    }

    public static void radixSortIndirect(int[] perm, byte[] a, int from, int to, boolean stable) {
        int[] support;
        if (to - from < 1024) {
            ByteArrays.insertionSortIndirect(perm, a, from, to);
            return;
        }
        boolean maxLevel = false;
        boolean stackSize = true;
        int stackPos = 0;
        int[] offsetStack = new int[1];
        int[] lengthStack = new int[1];
        int[] levelStack = new int[1];
        offsetStack[stackPos] = from;
        lengthStack[stackPos] = to - from;
        levelStack[stackPos++] = 0;
        int[] count = new int[256];
        int[] pos = new int[256];
        int[] arrn = support = stable ? new int[perm.length] : null;
        while (stackPos > 0) {
            int p;
            int i;
            int first = offsetStack[--stackPos];
            int length = lengthStack[stackPos];
            int level = levelStack[stackPos];
            int signMask = level % 1 == 0 ? 128 : 0;
            int shift = (0 - level % 1) * 8;
            int i2 = first + length;
            while (i2-- != first) {
                int[] arrn2 = count;
                int n = a[perm[i2]] >>> shift & 255 ^ signMask;
                arrn2[n] = arrn2[n] + 1;
            }
            int lastUsed = -1;
            int n = p = stable ? 0 : first;
            for (i = 0; i < 256; ++i) {
                if (count[i] != 0) {
                    lastUsed = i;
                }
                pos[i] = p += count[i];
            }
            if (stable) {
                i = first + length;
                while (i-- != first) {
                    int[] arrn3 = pos;
                    int n2 = a[perm[i]] >>> shift & 255 ^ signMask;
                    int n3 = arrn3[n2] - 1;
                    arrn3[n2] = n3;
                    support[n3] = perm[i];
                }
                System.arraycopy(support, 0, perm, first, length);
                p = first;
                for (i = 0; i <= lastUsed; ++i) {
                    if (level < 0 && count[i] > 1) {
                        if (count[i] < 1024) {
                            ByteArrays.insertionSortIndirect(perm, a, p, p + count[i]);
                        } else {
                            offsetStack[stackPos] = p;
                            lengthStack[stackPos] = count[i];
                            levelStack[stackPos++] = level + 1;
                        }
                    }
                    p += count[i];
                }
                java.util.Arrays.fill(count, 0);
                continue;
            }
            int end = first + length - count[lastUsed];
            int c = -1;
            for (int i3 = first; i3 <= end; i3 += count[c]) {
                int t = perm[i3];
                c = a[t] >>> shift & 255 ^ signMask;
                if (i3 < end) {
                    do {
                        int[] arrn4 = pos;
                        int n4 = c;
                        int n5 = arrn4[n4] - 1;
                        arrn4[n4] = n5;
                        int d = n5;
                        if (n5 <= i3) break;
                        int z = t;
                        t = perm[d];
                        perm[d] = z;
                        c = a[t] >>> shift & 255 ^ signMask;
                    } while (true);
                    perm[i3] = t;
                }
                if (level < 0 && count[c] > 1) {
                    if (count[c] < 1024) {
                        ByteArrays.insertionSortIndirect(perm, a, i3, i3 + count[c]);
                    } else {
                        offsetStack[stackPos] = i3;
                        lengthStack[stackPos] = count[c];
                        levelStack[stackPos++] = level + 1;
                    }
                }
                count[c] = 0;
            }
        }
    }

    public static void parallelRadixSortIndirect(int[] perm, byte[] a, int from, int to, boolean stable) {
        if (to - from < 1024) {
            ByteArrays.radixSortIndirect(perm, a, from, to, stable);
            return;
        }
        boolean maxLevel = false;
        LinkedBlockingQueue<Segment> queue = new LinkedBlockingQueue<Segment>();
        queue.add(new Segment(from, to - from, 0));
        AtomicInteger queueSize = new AtomicInteger(1);
        int numberOfThreads = Runtime.getRuntime().availableProcessors();
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads, Executors.defaultThreadFactory());
        ExecutorCompletionService<Void> executorCompletionService = new ExecutorCompletionService<Void>(executorService);
        int[] support = stable ? new int[perm.length] : null;
        int j = numberOfThreads;
        while (j-- != 0) {
            executorCompletionService.submit(() -> {
                int[] count = new int[256];
                int[] pos = new int[256];
                do {
                    int i;
                    Segment segment;
                    if (queueSize.get() == 0) {
                        int i2 = numberOfThreads;
                        while (i2-- != 0) {
                            queue.add(POISON_PILL);
                        }
                    }
                    if ((segment = (Segment)queue.take()) == POISON_PILL) {
                        return null;
                    }
                    int first = segment.offset;
                    int length = segment.length;
                    int level = segment.level;
                    int signMask = level % 1 == 0 ? 128 : 0;
                    int shift = (0 - level % 1) * 8;
                    int i3 = first + length;
                    while (i3-- != first) {
                        int[] arrn = count;
                        int n = a[perm[i3]] >>> shift & 255 ^ signMask;
                        arrn[n] = arrn[n] + 1;
                    }
                    int lastUsed = -1;
                    int p = first;
                    for (i = 0; i < 256; ++i) {
                        if (count[i] != 0) {
                            lastUsed = i;
                        }
                        pos[i] = p += count[i];
                    }
                    if (stable) {
                        i = first + length;
                        while (i-- != first) {
                            int[] arrn = pos;
                            int n = a[perm[i]] >>> shift & 255 ^ signMask;
                            int n2 = arrn[n] - 1;
                            arrn[n] = n2;
                            support[n2] = perm[i];
                        }
                        System.arraycopy(support, first, perm, first, length);
                        p = first;
                        for (i = 0; i <= lastUsed; ++i) {
                            if (level < 0 && count[i] > 1) {
                                if (count[i] < 1024) {
                                    ByteArrays.radixSortIndirect(perm, a, p, p + count[i], stable);
                                } else {
                                    queueSize.incrementAndGet();
                                    queue.add(new Segment(p, count[i], level + 1));
                                }
                            }
                            p += count[i];
                        }
                        java.util.Arrays.fill(count, 0);
                    } else {
                        int end = first + length - count[lastUsed];
                        int c = -1;
                        for (int i4 = first; i4 <= end; i4 += count[c]) {
                            int t = perm[i4];
                            c = a[t] >>> shift & 255 ^ signMask;
                            if (i4 < end) {
                                do {
                                    int[] arrn = pos;
                                    int n = c;
                                    int n3 = arrn[n] - 1;
                                    arrn[n] = n3;
                                    int d = n3;
                                    if (n3 <= i4) break;
                                    int z = t;
                                    t = perm[d];
                                    perm[d] = z;
                                    c = a[t] >>> shift & 255 ^ signMask;
                                } while (true);
                                perm[i4] = t;
                            }
                            if (level < 0 && count[c] > 1) {
                                if (count[c] < 1024) {
                                    ByteArrays.radixSortIndirect(perm, a, i4, i4 + count[c], stable);
                                } else {
                                    queueSize.incrementAndGet();
                                    queue.add(new Segment(i4, count[c], level + 1));
                                }
                            }
                            count[c] = 0;
                        }
                    }
                    queueSize.decrementAndGet();
                } while (true);
            });
        }
        Throwable problem = null;
        int i = numberOfThreads;
        while (i-- != 0) {
            try {
                executorCompletionService.take().get();
            }
            catch (Exception e) {
                problem = e.getCause();
            }
        }
        executorService.shutdown();
        if (problem != null) {
            throw problem instanceof RuntimeException ? (RuntimeException)problem : new RuntimeException(problem);
        }
    }

    public static void parallelRadixSortIndirect(int[] perm, byte[] a, boolean stable) {
        ByteArrays.parallelRadixSortIndirect(perm, a, 0, a.length, stable);
    }

    public static void radixSort(byte[] a, byte[] b) {
        ByteArrays.ensureSameLength(a, b);
        ByteArrays.radixSort(a, b, 0, a.length);
    }

    public static void radixSort(byte[] a, byte[] b, int from, int to) {
        if (to - from < 1024) {
            ByteArrays.selectionSort(a, b, from, to);
            return;
        }
        int layers = 2;
        boolean maxLevel = true;
        int stackSize = 256;
        int stackPos = 0;
        int[] offsetStack = new int[256];
        int[] lengthStack = new int[256];
        int[] levelStack = new int[256];
        offsetStack[stackPos] = from;
        lengthStack[stackPos] = to - from;
        levelStack[stackPos++] = 0;
        int[] count = new int[256];
        int[] pos = new int[256];
        while (stackPos > 0) {
            int first = offsetStack[--stackPos];
            int length = lengthStack[stackPos];
            int level = levelStack[stackPos];
            int signMask = level % 1 == 0 ? 128 : 0;
            byte[] k = level < 1 ? a : b;
            int shift = (0 - level % 1) * 8;
            int i = first + length;
            while (i-- != first) {
                int[] arrn = count;
                int n = k[i] >>> shift & 255 ^ signMask;
                arrn[n] = arrn[n] + 1;
            }
            int lastUsed = -1;
            int p = first;
            for (int i2 = 0; i2 < 256; ++i2) {
                if (count[i2] != 0) {
                    lastUsed = i2;
                }
                pos[i2] = p += count[i2];
            }
            int end = first + length - count[lastUsed];
            int c = -1;
            for (int i3 = first; i3 <= end; i3 += count[c]) {
                byte t = a[i3];
                byte u = b[i3];
                c = k[i3] >>> shift & 255 ^ signMask;
                if (i3 < end) {
                    do {
                        int[] arrn = pos;
                        int n = c;
                        int n2 = arrn[n] - 1;
                        arrn[n] = n2;
                        int d = n2;
                        if (n2 <= i3) break;
                        c = k[d] >>> shift & 255 ^ signMask;
                        byte z = t;
                        t = a[d];
                        a[d] = z;
                        z = u;
                        u = b[d];
                        b[d] = z;
                    } while (true);
                    a[i3] = t;
                    b[i3] = u;
                }
                if (level < 1 && count[c] > 1) {
                    if (count[c] < 1024) {
                        ByteArrays.selectionSort(a, b, i3, i3 + count[c]);
                    } else {
                        offsetStack[stackPos] = i3;
                        lengthStack[stackPos] = count[c];
                        levelStack[stackPos++] = level + 1;
                    }
                }
                count[c] = 0;
            }
        }
    }

    public static void parallelRadixSort(byte[] a, byte[] b, int from, int to) {
        if (to - from < 1024) {
            ByteArrays.quickSort(a, b, from, to);
            return;
        }
        int layers = 2;
        if (a.length != b.length) {
            throw new IllegalArgumentException("Array size mismatch.");
        }
        boolean maxLevel = true;
        LinkedBlockingQueue<Segment> queue = new LinkedBlockingQueue<Segment>();
        queue.add(new Segment(from, to - from, 0));
        AtomicInteger queueSize = new AtomicInteger(1);
        int numberOfThreads = Runtime.getRuntime().availableProcessors();
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads, Executors.defaultThreadFactory());
        ExecutorCompletionService<Void> executorCompletionService = new ExecutorCompletionService<Void>(executorService);
        int j = numberOfThreads;
        while (j-- != 0) {
            executorCompletionService.submit(() -> {
                int[] count = new int[256];
                int[] pos = new int[256];
                do {
                    Segment segment;
                    if (queueSize.get() == 0) {
                        int i = numberOfThreads;
                        while (i-- != 0) {
                            queue.add(POISON_PILL);
                        }
                    }
                    if ((segment = (Segment)queue.take()) == POISON_PILL) {
                        return null;
                    }
                    int first = segment.offset;
                    int length = segment.length;
                    int level = segment.level;
                    int signMask = level % 1 == 0 ? 128 : 0;
                    byte[] k = level < 1 ? a : b;
                    int shift = (0 - level % 1) * 8;
                    int i = first + length;
                    while (i-- != first) {
                        int[] arrn = count;
                        int n = k[i] >>> shift & 255 ^ signMask;
                        arrn[n] = arrn[n] + 1;
                    }
                    int lastUsed = -1;
                    int p = first;
                    for (int i2 = 0; i2 < 256; ++i2) {
                        if (count[i2] != 0) {
                            lastUsed = i2;
                        }
                        pos[i2] = p += count[i2];
                    }
                    int end = first + length - count[lastUsed];
                    int c = -1;
                    for (int i3 = first; i3 <= end; i3 += count[c]) {
                        byte t = a[i3];
                        byte u = b[i3];
                        c = k[i3] >>> shift & 255 ^ signMask;
                        if (i3 < end) {
                            do {
                                int[] arrn = pos;
                                int n = c;
                                int n2 = arrn[n] - 1;
                                arrn[n] = n2;
                                int d = n2;
                                if (n2 <= i3) break;
                                c = k[d] >>> shift & 255 ^ signMask;
                                byte z = t;
                                byte w = u;
                                t = a[d];
                                u = b[d];
                                a[d] = z;
                                b[d] = w;
                            } while (true);
                            a[i3] = t;
                            b[i3] = u;
                        }
                        if (level < 1 && count[c] > 1) {
                            if (count[c] < 1024) {
                                ByteArrays.quickSort(a, b, i3, i3 + count[c]);
                            } else {
                                queueSize.incrementAndGet();
                                queue.add(new Segment(i3, count[c], level + 1));
                            }
                        }
                        count[c] = 0;
                    }
                    queueSize.decrementAndGet();
                } while (true);
            });
        }
        Throwable problem = null;
        int i = numberOfThreads;
        while (i-- != 0) {
            try {
                executorCompletionService.take().get();
            }
            catch (Exception e) {
                problem = e.getCause();
            }
        }
        executorService.shutdown();
        if (problem != null) {
            throw problem instanceof RuntimeException ? (RuntimeException)problem : new RuntimeException(problem);
        }
    }

    public static void parallelRadixSort(byte[] a, byte[] b) {
        ByteArrays.ensureSameLength(a, b);
        ByteArrays.parallelRadixSort(a, b, 0, a.length);
    }

    private static void insertionSortIndirect(int[] perm, byte[] a, byte[] b, int from, int to) {
        int i = from;
        while (++i < to) {
            int t = perm[i];
            int j = i;
            int u = perm[j - 1];
            while (a[t] < a[u] || a[t] == a[u] && b[t] < b[u]) {
                perm[j] = u;
                if (from == j - 1) {
                    --j;
                    break;
                }
                u = perm[--j - 1];
            }
            perm[j] = t;
        }
    }

    public static void radixSortIndirect(int[] perm, byte[] a, byte[] b, boolean stable) {
        ByteArrays.ensureSameLength(a, b);
        ByteArrays.radixSortIndirect(perm, a, b, 0, a.length, stable);
    }

    public static void radixSortIndirect(int[] perm, byte[] a, byte[] b, int from, int to, boolean stable) {
        int[] support;
        if (to - from < 1024) {
            ByteArrays.insertionSortIndirect(perm, a, b, from, to);
            return;
        }
        int layers = 2;
        boolean maxLevel = true;
        int stackSize = 256;
        int stackPos = 0;
        int[] offsetStack = new int[256];
        int[] lengthStack = new int[256];
        int[] levelStack = new int[256];
        offsetStack[stackPos] = from;
        lengthStack[stackPos] = to - from;
        levelStack[stackPos++] = 0;
        int[] count = new int[256];
        int[] pos = new int[256];
        int[] arrn = support = stable ? new int[perm.length] : null;
        while (stackPos > 0) {
            int i;
            int p;
            int first = offsetStack[--stackPos];
            int length = lengthStack[stackPos];
            int level = levelStack[stackPos];
            int signMask = level % 1 == 0 ? 128 : 0;
            byte[] k = level < 1 ? a : b;
            int shift = (0 - level % 1) * 8;
            int i2 = first + length;
            while (i2-- != first) {
                int[] arrn2 = count;
                int n = k[perm[i2]] >>> shift & 255 ^ signMask;
                arrn2[n] = arrn2[n] + 1;
            }
            int lastUsed = -1;
            int n = p = stable ? 0 : first;
            for (i = 0; i < 256; ++i) {
                if (count[i] != 0) {
                    lastUsed = i;
                }
                pos[i] = p += count[i];
            }
            if (stable) {
                i = first + length;
                while (i-- != first) {
                    int[] arrn3 = pos;
                    int n2 = k[perm[i]] >>> shift & 255 ^ signMask;
                    int n3 = arrn3[n2] - 1;
                    arrn3[n2] = n3;
                    support[n3] = perm[i];
                }
                System.arraycopy(support, 0, perm, first, length);
                p = first;
                for (i = 0; i < 256; ++i) {
                    if (level < 1 && count[i] > 1) {
                        if (count[i] < 1024) {
                            ByteArrays.insertionSortIndirect(perm, a, b, p, p + count[i]);
                        } else {
                            offsetStack[stackPos] = p;
                            lengthStack[stackPos] = count[i];
                            levelStack[stackPos++] = level + 1;
                        }
                    }
                    p += count[i];
                }
                java.util.Arrays.fill(count, 0);
                continue;
            }
            int end = first + length - count[lastUsed];
            int c = -1;
            for (int i3 = first; i3 <= end; i3 += count[c]) {
                int t = perm[i3];
                c = k[t] >>> shift & 255 ^ signMask;
                if (i3 < end) {
                    do {
                        int[] arrn4 = pos;
                        int n4 = c;
                        int n5 = arrn4[n4] - 1;
                        arrn4[n4] = n5;
                        int d = n5;
                        if (n5 <= i3) break;
                        int z = t;
                        t = perm[d];
                        perm[d] = z;
                        c = k[t] >>> shift & 255 ^ signMask;
                    } while (true);
                    perm[i3] = t;
                }
                if (level < 1 && count[c] > 1) {
                    if (count[c] < 1024) {
                        ByteArrays.insertionSortIndirect(perm, a, b, i3, i3 + count[c]);
                    } else {
                        offsetStack[stackPos] = i3;
                        lengthStack[stackPos] = count[c];
                        levelStack[stackPos++] = level + 1;
                    }
                }
                count[c] = 0;
            }
        }
    }

    private static void selectionSort(byte[][] a, int from, int to, int level) {
        int layers = a.length;
        int firstLayer = level / 1;
        for (int i = from; i < to - 1; ++i) {
            int m = i;
            block1 : for (int j = i + 1; j < to; ++j) {
                for (int p = firstLayer; p < layers; ++p) {
                    if (a[p][j] < a[p][m]) {
                        m = j;
                        continue block1;
                    }
                    if (a[p][j] > a[p][m]) continue block1;
                }
            }
            if (m == i) continue;
            int p = layers;
            while (p-- != 0) {
                byte u = a[p][i];
                a[p][i] = a[p][m];
                a[p][m] = u;
            }
        }
    }

    public static void radixSort(byte[][] a) {
        ByteArrays.radixSort(a, 0, a[0].length);
    }

    public static void radixSort(byte[][] a, int from, int to) {
        if (to - from < 1024) {
            ByteArrays.selectionSort(a, from, to, 0);
            return;
        }
        int layers = a.length;
        int maxLevel = 1 * layers - 1;
        int p = layers;
        int l = a[0].length;
        while (p-- != 0) {
            if (a[p].length == l) continue;
            throw new IllegalArgumentException("The array of index " + p + " has not the same length of the array of index 0.");
        }
        int stackSize = 255 * (layers * 1 - 1) + 1;
        int stackPos = 0;
        int[] offsetStack = new int[stackSize];
        int[] lengthStack = new int[stackSize];
        int[] levelStack = new int[stackSize];
        offsetStack[stackPos] = from;
        lengthStack[stackPos] = to - from;
        levelStack[stackPos++] = 0;
        int[] count = new int[256];
        int[] pos = new int[256];
        byte[] t = new byte[layers];
        while (stackPos > 0) {
            int first = offsetStack[--stackPos];
            int length = lengthStack[stackPos];
            int level = levelStack[stackPos];
            int signMask = level % 1 == 0 ? 128 : 0;
            byte[] k = a[level / 1];
            int shift = (0 - level % 1) * 8;
            int i = first + length;
            while (i-- != first) {
                int[] arrn = count;
                int n = k[i] >>> shift & 255 ^ signMask;
                arrn[n] = arrn[n] + 1;
            }
            int lastUsed = -1;
            int p2 = first;
            for (int i2 = 0; i2 < 256; ++i2) {
                if (count[i2] != 0) {
                    lastUsed = i2;
                }
                pos[i2] = p2 += count[i2];
            }
            int end = first + length - count[lastUsed];
            int c = -1;
            for (int i3 = first; i3 <= end; i3 += count[c]) {
                int p3 = layers;
                while (p3-- != 0) {
                    t[p3] = a[p3][i3];
                }
                c = k[i3] >>> shift & 255 ^ signMask;
                if (i3 < end) {
                    block6 : do {
                        int[] arrn = pos;
                        int n = c;
                        int n2 = arrn[n] - 1;
                        arrn[n] = n2;
                        int d = n2;
                        if (n2 <= i3) break;
                        c = k[d] >>> shift & 255 ^ signMask;
                        p3 = layers;
                        do {
                            if (p3-- == 0) continue block6;
                            byte u = t[p3];
                            t[p3] = a[p3][d];
                            a[p3][d] = u;
                        } while (true);
                        break;
                    } while (true);
                    p3 = layers;
                    while (p3-- != 0) {
                        a[p3][i3] = t[p3];
                    }
                }
                if (level < maxLevel && count[c] > 1) {
                    if (count[c] < 1024) {
                        ByteArrays.selectionSort(a, i3, i3 + count[c], level + 1);
                    } else {
                        offsetStack[stackPos] = i3;
                        lengthStack[stackPos] = count[c];
                        levelStack[stackPos++] = level + 1;
                    }
                }
                count[c] = 0;
            }
        }
    }

    public static byte[] shuffle(byte[] a, int from, int to, Random random) {
        int i = to - from;
        while (i-- != 0) {
            int p = random.nextInt(i + 1);
            byte t = a[from + i];
            a[from + i] = a[from + p];
            a[from + p] = t;
        }
        return a;
    }

    public static byte[] shuffle(byte[] a, Random random) {
        int i = a.length;
        while (i-- != 0) {
            int p = random.nextInt(i + 1);
            byte t = a[i];
            a[i] = a[p];
            a[p] = t;
        }
        return a;
    }

    public static byte[] reverse(byte[] a) {
        int length = a.length;
        int i = length / 2;
        while (i-- != 0) {
            byte t = a[length - i - 1];
            a[length - i - 1] = a[i];
            a[i] = t;
        }
        return a;
    }

    public static byte[] reverse(byte[] a, int from, int to) {
        int length = to - from;
        int i = length / 2;
        while (i-- != 0) {
            byte t = a[from + length - i - 1];
            a[from + length - i - 1] = a[from + i];
            a[from + i] = t;
        }
        return a;
    }

    private static final class ArrayHashStrategy
    implements Hash.Strategy<byte[]>,
    Serializable {
        private static final long serialVersionUID = -7046029254386353129L;

        private ArrayHashStrategy() {
        }

        @Override
        public int hashCode(byte[] o) {
            return java.util.Arrays.hashCode(o);
        }

        @Override
        public boolean equals(byte[] a, byte[] b) {
            return java.util.Arrays.equals(a, b);
        }
    }

    protected static final class Segment {
        protected final int offset;
        protected final int length;
        protected final int level;

        protected Segment(int offset, int length, int level) {
            this.offset = offset;
            this.length = length;
            this.level = level;
        }

        public String toString() {
            return "Segment [offset=" + this.offset + ", length=" + this.length + ", level=" + this.level + "]";
        }
    }

    protected static class ForkJoinQuickSort2
    extends RecursiveAction {
        private static final long serialVersionUID = 1L;
        private final int from;
        private final int to;
        private final byte[] x;
        private final byte[] y;

        public ForkJoinQuickSort2(byte[] x, byte[] y, int from, int to) {
            this.from = from;
            this.to = to;
            this.x = x;
            this.y = y;
        }

        @Override
        protected void compute() {
            int c;
            int a;
            byte[] x = this.x;
            byte[] y = this.y;
            int len = this.to - this.from;
            if (len < 8192) {
                ByteArrays.quickSort(x, y, this.from, this.to);
                return;
            }
            int m = this.from + len / 2;
            int l = this.from;
            int n = this.to - 1;
            int s = len / 8;
            l = ByteArrays.med3(x, y, l, l + s, l + 2 * s);
            m = ByteArrays.med3(x, y, m - s, m, m + s);
            n = ByteArrays.med3(x, y, n - 2 * s, n - s, n);
            m = ByteArrays.med3(x, y, l, m, n);
            byte v = x[m];
            byte w = y[m];
            int b = a = this.from;
            int d = c = this.to - 1;
            do {
                int t;
                int comparison;
                if (b <= c && (comparison = (t = Byte.compare(x[b], v)) == 0 ? Byte.compare(y[b], w) : t) <= 0) {
                    if (comparison == 0) {
                        ByteArrays.swap(x, y, a++, b);
                    }
                    ++b;
                    continue;
                }
                while (c >= b && (comparison = (t = Byte.compare(x[c], v)) == 0 ? Byte.compare(y[c], w) : t) >= 0) {
                    if (comparison == 0) {
                        ByteArrays.swap(x, y, c, d--);
                    }
                    --c;
                }
                if (b > c) break;
                ByteArrays.swap(x, y, b++, c--);
            } while (true);
            s = Math.min(a - this.from, b - a);
            ByteArrays.swap(x, y, this.from, b - s, s);
            s = Math.min(d - c, this.to - d - 1);
            ByteArrays.swap(x, y, b, this.to - s, s);
            s = b - a;
            int t = d - c;
            if (s > 1 && t > 1) {
                ForkJoinQuickSort2.invokeAll(new ForkJoinQuickSort2(x, y, this.from, this.from + s), new ForkJoinQuickSort2(x, y, this.to - t, this.to));
            } else if (s > 1) {
                ForkJoinQuickSort2.invokeAll(new ForkJoinQuickSort2(x, y, this.from, this.from + s));
            } else {
                ForkJoinQuickSort2.invokeAll(new ForkJoinQuickSort2(x, y, this.to - t, this.to));
            }
        }
    }

    protected static class ForkJoinQuickSortIndirect
    extends RecursiveAction {
        private static final long serialVersionUID = 1L;
        private final int from;
        private final int to;
        private final int[] perm;
        private final byte[] x;

        public ForkJoinQuickSortIndirect(int[] perm, byte[] x, int from, int to) {
            this.from = from;
            this.to = to;
            this.x = x;
            this.perm = perm;
        }

        @Override
        protected void compute() {
            int a;
            int c;
            byte[] x = this.x;
            int len = this.to - this.from;
            if (len < 8192) {
                ByteArrays.quickSortIndirect(this.perm, x, this.from, this.to);
                return;
            }
            int m = this.from + len / 2;
            int l = this.from;
            int n = this.to - 1;
            int s = len / 8;
            l = ByteArrays.med3Indirect(this.perm, x, l, l + s, l + 2 * s);
            m = ByteArrays.med3Indirect(this.perm, x, m - s, m, m + s);
            n = ByteArrays.med3Indirect(this.perm, x, n - 2 * s, n - s, n);
            m = ByteArrays.med3Indirect(this.perm, x, l, m, n);
            byte v = x[this.perm[m]];
            int b = a = this.from;
            int d = c = this.to - 1;
            do {
                int comparison;
                if (b <= c && (comparison = Byte.compare(x[this.perm[b]], v)) <= 0) {
                    if (comparison == 0) {
                        IntArrays.swap(this.perm, a++, b);
                    }
                    ++b;
                    continue;
                }
                while (c >= b && (comparison = Byte.compare(x[this.perm[c]], v)) >= 0) {
                    if (comparison == 0) {
                        IntArrays.swap(this.perm, c, d--);
                    }
                    --c;
                }
                if (b > c) break;
                IntArrays.swap(this.perm, b++, c--);
            } while (true);
            s = Math.min(a - this.from, b - a);
            IntArrays.swap(this.perm, this.from, b - s, s);
            s = Math.min(d - c, this.to - d - 1);
            IntArrays.swap(this.perm, b, this.to - s, s);
            s = b - a;
            int t = d - c;
            if (s > 1 && t > 1) {
                ForkJoinQuickSortIndirect.invokeAll(new ForkJoinQuickSortIndirect(this.perm, x, this.from, this.from + s), new ForkJoinQuickSortIndirect(this.perm, x, this.to - t, this.to));
            } else if (s > 1) {
                ForkJoinQuickSortIndirect.invokeAll(new ForkJoinQuickSortIndirect(this.perm, x, this.from, this.from + s));
            } else {
                ForkJoinQuickSortIndirect.invokeAll(new ForkJoinQuickSortIndirect(this.perm, x, this.to - t, this.to));
            }
        }
    }

    protected static class ForkJoinQuickSort
    extends RecursiveAction {
        private static final long serialVersionUID = 1L;
        private final int from;
        private final int to;
        private final byte[] x;

        public ForkJoinQuickSort(byte[] x, int from, int to) {
            this.from = from;
            this.to = to;
            this.x = x;
        }

        @Override
        protected void compute() {
            int a;
            int c;
            byte[] x = this.x;
            int len = this.to - this.from;
            if (len < 8192) {
                ByteArrays.quickSort(x, this.from, this.to);
                return;
            }
            int m = this.from + len / 2;
            int l = this.from;
            int n = this.to - 1;
            int s = len / 8;
            l = ByteArrays.med3(x, l, l + s, l + 2 * s);
            m = ByteArrays.med3(x, m - s, m, m + s);
            n = ByteArrays.med3(x, n - 2 * s, n - s, n);
            m = ByteArrays.med3(x, l, m, n);
            byte v = x[m];
            int b = a = this.from;
            int d = c = this.to - 1;
            do {
                int comparison;
                if (b <= c && (comparison = Byte.compare(x[b], v)) <= 0) {
                    if (comparison == 0) {
                        ByteArrays.swap(x, a++, b);
                    }
                    ++b;
                    continue;
                }
                while (c >= b && (comparison = Byte.compare(x[c], v)) >= 0) {
                    if (comparison == 0) {
                        ByteArrays.swap(x, c, d--);
                    }
                    --c;
                }
                if (b > c) break;
                ByteArrays.swap(x, b++, c--);
            } while (true);
            s = Math.min(a - this.from, b - a);
            ByteArrays.swap(x, this.from, b - s, s);
            s = Math.min(d - c, this.to - d - 1);
            ByteArrays.swap(x, b, this.to - s, s);
            s = b - a;
            int t = d - c;
            if (s > 1 && t > 1) {
                ForkJoinQuickSort.invokeAll(new ForkJoinQuickSort(x, this.from, this.from + s), new ForkJoinQuickSort(x, this.to - t, this.to));
            } else if (s > 1) {
                ForkJoinQuickSort.invokeAll(new ForkJoinQuickSort(x, this.from, this.from + s));
            } else {
                ForkJoinQuickSort.invokeAll(new ForkJoinQuickSort(x, this.to - t, this.to));
            }
        }
    }

    protected static class ForkJoinQuickSortComp
    extends RecursiveAction {
        private static final long serialVersionUID = 1L;
        private final int from;
        private final int to;
        private final byte[] x;
        private final ByteComparator comp;

        public ForkJoinQuickSortComp(byte[] x, int from, int to, ByteComparator comp) {
            this.from = from;
            this.to = to;
            this.x = x;
            this.comp = comp;
        }

        @Override
        protected void compute() {
            int a;
            int c;
            byte[] x = this.x;
            int len = this.to - this.from;
            if (len < 8192) {
                ByteArrays.quickSort(x, this.from, this.to, this.comp);
                return;
            }
            int m = this.from + len / 2;
            int l = this.from;
            int n = this.to - 1;
            int s = len / 8;
            l = ByteArrays.med3(x, l, l + s, l + 2 * s, this.comp);
            m = ByteArrays.med3(x, m - s, m, m + s, this.comp);
            n = ByteArrays.med3(x, n - 2 * s, n - s, n, this.comp);
            m = ByteArrays.med3(x, l, m, n, this.comp);
            byte v = x[m];
            int b = a = this.from;
            int d = c = this.to - 1;
            do {
                int comparison;
                if (b <= c && (comparison = this.comp.compare(x[b], v)) <= 0) {
                    if (comparison == 0) {
                        ByteArrays.swap(x, a++, b);
                    }
                    ++b;
                    continue;
                }
                while (c >= b && (comparison = this.comp.compare(x[c], v)) >= 0) {
                    if (comparison == 0) {
                        ByteArrays.swap(x, c, d--);
                    }
                    --c;
                }
                if (b > c) break;
                ByteArrays.swap(x, b++, c--);
            } while (true);
            s = Math.min(a - this.from, b - a);
            ByteArrays.swap(x, this.from, b - s, s);
            s = Math.min(d - c, this.to - d - 1);
            ByteArrays.swap(x, b, this.to - s, s);
            s = b - a;
            int t = d - c;
            if (s > 1 && t > 1) {
                ForkJoinQuickSortComp.invokeAll(new ForkJoinQuickSortComp(x, this.from, this.from + s, this.comp), new ForkJoinQuickSortComp(x, this.to - t, this.to, this.comp));
            } else if (s > 1) {
                ForkJoinQuickSortComp.invokeAll(new ForkJoinQuickSortComp(x, this.from, this.from + s, this.comp));
            } else {
                ForkJoinQuickSortComp.invokeAll(new ForkJoinQuickSortComp(x, this.to - t, this.to, this.comp));
            }
        }
    }

}

