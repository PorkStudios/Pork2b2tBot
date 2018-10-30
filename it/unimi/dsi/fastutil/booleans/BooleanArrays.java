/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.booleans;

import it.unimi.dsi.fastutil.Arrays;
import it.unimi.dsi.fastutil.Hash;
import it.unimi.dsi.fastutil.booleans.BooleanComparator;
import it.unimi.dsi.fastutil.ints.IntArrays;
import java.io.Serializable;
import java.util.Random;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveAction;

public final class BooleanArrays {
    public static final boolean[] EMPTY_ARRAY = new boolean[0];
    private static final int QUICKSORT_NO_REC = 16;
    private static final int PARALLEL_QUICKSORT_NO_FORK = 8192;
    private static final int QUICKSORT_MEDIAN_OF_9 = 128;
    private static final int MERGESORT_NO_REC = 16;
    public static final Hash.Strategy<boolean[]> HASH_STRATEGY = new ArrayHashStrategy();

    private BooleanArrays() {
    }

    public static boolean[] ensureCapacity(boolean[] array, int length) {
        if (length > array.length) {
            boolean[] t = new boolean[length];
            System.arraycopy(array, 0, t, 0, array.length);
            return t;
        }
        return array;
    }

    public static boolean[] ensureCapacity(boolean[] array, int length, int preserve) {
        if (length > array.length) {
            boolean[] t = new boolean[length];
            System.arraycopy(array, 0, t, 0, preserve);
            return t;
        }
        return array;
    }

    public static boolean[] grow(boolean[] array, int length) {
        if (length > array.length) {
            int newLength = (int)Math.max(Math.min(2L * (long)array.length, 0x7FFFFFF7L), (long)length);
            boolean[] t = new boolean[newLength];
            System.arraycopy(array, 0, t, 0, array.length);
            return t;
        }
        return array;
    }

    public static boolean[] grow(boolean[] array, int length, int preserve) {
        if (length > array.length) {
            int newLength = (int)Math.max(Math.min(2L * (long)array.length, 0x7FFFFFF7L), (long)length);
            boolean[] t = new boolean[newLength];
            System.arraycopy(array, 0, t, 0, preserve);
            return t;
        }
        return array;
    }

    public static boolean[] trim(boolean[] array, int length) {
        if (length >= array.length) {
            return array;
        }
        boolean[] t = length == 0 ? EMPTY_ARRAY : new boolean[length];
        System.arraycopy(array, 0, t, 0, length);
        return t;
    }

    public static boolean[] setLength(boolean[] array, int length) {
        if (length == array.length) {
            return array;
        }
        if (length < array.length) {
            return BooleanArrays.trim(array, length);
        }
        return BooleanArrays.ensureCapacity(array, length);
    }

    public static boolean[] copy(boolean[] array, int offset, int length) {
        BooleanArrays.ensureOffsetLength(array, offset, length);
        boolean[] a = length == 0 ? EMPTY_ARRAY : new boolean[length];
        System.arraycopy(array, offset, a, 0, length);
        return a;
    }

    public static boolean[] copy(boolean[] array) {
        return (boolean[])array.clone();
    }

    @Deprecated
    public static void fill(boolean[] array, boolean value) {
        int i = array.length;
        while (i-- != 0) {
            array[i] = value;
        }
    }

    @Deprecated
    public static void fill(boolean[] array, int from, int to, boolean value) {
        BooleanArrays.ensureFromTo(array, from, to);
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
    public static boolean equals(boolean[] a1, boolean[] a2) {
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

    public static void ensureFromTo(boolean[] a, int from, int to) {
        Arrays.ensureFromTo(a.length, from, to);
    }

    public static void ensureOffsetLength(boolean[] a, int offset, int length) {
        Arrays.ensureOffsetLength(a.length, offset, length);
    }

    public static void ensureSameLength(boolean[] a, boolean[] b) {
        if (a.length != b.length) {
            throw new IllegalArgumentException("Array size mismatch: " + a.length + " != " + b.length);
        }
    }

    public static void swap(boolean[] x, int a, int b) {
        boolean t = x[a];
        x[a] = x[b];
        x[b] = t;
    }

    public static void swap(boolean[] x, int a, int b, int n) {
        int i = 0;
        while (i < n) {
            BooleanArrays.swap(x, a, b);
            ++i;
            ++a;
            ++b;
        }
    }

    private static int med3(boolean[] x, int a, int b, int c, BooleanComparator comp) {
        int ab = comp.compare(x[a], x[b]);
        int ac = comp.compare(x[a], x[c]);
        int bc = comp.compare(x[b], x[c]);
        return ab < 0 ? (bc < 0 ? b : (ac < 0 ? c : a)) : (bc > 0 ? b : (ac > 0 ? c : a));
    }

    private static void selectionSort(boolean[] a, int from, int to, BooleanComparator comp) {
        for (int i = from; i < to - 1; ++i) {
            int m = i;
            for (int j = i + 1; j < to; ++j) {
                if (comp.compare(a[j], a[m]) >= 0) continue;
                m = j;
            }
            if (m == i) continue;
            boolean u = a[i];
            a[i] = a[m];
            a[m] = u;
        }
    }

    private static void insertionSort(boolean[] a, int from, int to, BooleanComparator comp) {
        int i = from;
        while (++i < to) {
            boolean t = a[i];
            int j = i;
            boolean u = a[j - 1];
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

    public static void quickSort(boolean[] x, int from, int to, BooleanComparator comp) {
        int a;
        int c;
        int len = to - from;
        if (len < 16) {
            BooleanArrays.selectionSort(x, from, to, comp);
            return;
        }
        int m = from + len / 2;
        int l = from;
        int n = to - 1;
        if (len > 128) {
            int s = len / 8;
            l = BooleanArrays.med3(x, l, l + s, l + 2 * s, comp);
            m = BooleanArrays.med3(x, m - s, m, m + s, comp);
            n = BooleanArrays.med3(x, n - 2 * s, n - s, n, comp);
        }
        m = BooleanArrays.med3(x, l, m, n, comp);
        boolean v = x[m];
        int b = a = from;
        int d = c = to - 1;
        do {
            int comparison;
            if (b <= c && (comparison = comp.compare(x[b], v)) <= 0) {
                if (comparison == 0) {
                    BooleanArrays.swap(x, a++, b);
                }
                ++b;
                continue;
            }
            while (c >= b && (comparison = comp.compare(x[c], v)) >= 0) {
                if (comparison == 0) {
                    BooleanArrays.swap(x, c, d--);
                }
                --c;
            }
            if (b > c) break;
            BooleanArrays.swap(x, b++, c--);
        } while (true);
        int s = Math.min(a - from, b - a);
        BooleanArrays.swap(x, from, b - s, s);
        s = Math.min(d - c, to - d - 1);
        BooleanArrays.swap(x, b, to - s, s);
        s = b - a;
        if (s > 1) {
            BooleanArrays.quickSort(x, from, from + s, comp);
        }
        if ((s = d - c) > 1) {
            BooleanArrays.quickSort(x, to - s, to, comp);
        }
    }

    public static void quickSort(boolean[] x, BooleanComparator comp) {
        BooleanArrays.quickSort(x, 0, x.length, comp);
    }

    public static void parallelQuickSort(boolean[] x, int from, int to, BooleanComparator comp) {
        if (to - from < 8192) {
            BooleanArrays.quickSort(x, from, to, comp);
        } else {
            ForkJoinPool pool = new ForkJoinPool(Runtime.getRuntime().availableProcessors());
            pool.invoke(new ForkJoinQuickSortComp(x, from, to, comp));
            pool.shutdown();
        }
    }

    public static void parallelQuickSort(boolean[] x, BooleanComparator comp) {
        BooleanArrays.parallelQuickSort(x, 0, x.length, comp);
    }

    private static int med3(boolean[] x, int a, int b, int c) {
        int ab = Boolean.compare(x[a], x[b]);
        int ac = Boolean.compare(x[a], x[c]);
        int bc = Boolean.compare(x[b], x[c]);
        return ab < 0 ? (bc < 0 ? b : (ac < 0 ? c : a)) : (bc > 0 ? b : (ac > 0 ? c : a));
    }

    private static void selectionSort(boolean[] a, int from, int to) {
        for (int i = from; i < to - 1; ++i) {
            int m = i;
            for (int j = i + 1; j < to; ++j) {
                if (a[j] || !a[m]) continue;
                m = j;
            }
            if (m == i) continue;
            boolean u = a[i];
            a[i] = a[m];
            a[m] = u;
        }
    }

    private static void insertionSort(boolean[] a, int from, int to) {
        int i = from;
        while (++i < to) {
            boolean t = a[i];
            int j = i;
            boolean u = a[j - 1];
            while (!t && u) {
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

    public static void quickSort(boolean[] x, int from, int to) {
        int a;
        int c;
        int len = to - from;
        if (len < 16) {
            BooleanArrays.selectionSort(x, from, to);
            return;
        }
        int m = from + len / 2;
        int l = from;
        int n = to - 1;
        if (len > 128) {
            int s = len / 8;
            l = BooleanArrays.med3(x, l, l + s, l + 2 * s);
            m = BooleanArrays.med3(x, m - s, m, m + s);
            n = BooleanArrays.med3(x, n - 2 * s, n - s, n);
        }
        m = BooleanArrays.med3(x, l, m, n);
        boolean v = x[m];
        int b = a = from;
        int d = c = to - 1;
        do {
            int comparison;
            if (b <= c && (comparison = Boolean.compare(x[b], v)) <= 0) {
                if (comparison == 0) {
                    BooleanArrays.swap(x, a++, b);
                }
                ++b;
                continue;
            }
            while (c >= b && (comparison = Boolean.compare(x[c], v)) >= 0) {
                if (comparison == 0) {
                    BooleanArrays.swap(x, c, d--);
                }
                --c;
            }
            if (b > c) break;
            BooleanArrays.swap(x, b++, c--);
        } while (true);
        int s = Math.min(a - from, b - a);
        BooleanArrays.swap(x, from, b - s, s);
        s = Math.min(d - c, to - d - 1);
        BooleanArrays.swap(x, b, to - s, s);
        s = b - a;
        if (s > 1) {
            BooleanArrays.quickSort(x, from, from + s);
        }
        if ((s = d - c) > 1) {
            BooleanArrays.quickSort(x, to - s, to);
        }
    }

    public static void quickSort(boolean[] x) {
        BooleanArrays.quickSort(x, 0, x.length);
    }

    public static void parallelQuickSort(boolean[] x, int from, int to) {
        if (to - from < 8192) {
            BooleanArrays.quickSort(x, from, to);
        } else {
            ForkJoinPool pool = new ForkJoinPool(Runtime.getRuntime().availableProcessors());
            pool.invoke(new ForkJoinQuickSort(x, from, to));
            pool.shutdown();
        }
    }

    public static void parallelQuickSort(boolean[] x) {
        BooleanArrays.parallelQuickSort(x, 0, x.length);
    }

    private static int med3Indirect(int[] perm, boolean[] x, int a, int b, int c) {
        boolean aa = x[perm[a]];
        boolean bb = x[perm[b]];
        boolean cc = x[perm[c]];
        int ab = Boolean.compare(aa, bb);
        int ac = Boolean.compare(aa, cc);
        int bc = Boolean.compare(bb, cc);
        return ab < 0 ? (bc < 0 ? b : (ac < 0 ? c : a)) : (bc > 0 ? b : (ac > 0 ? c : a));
    }

    private static void insertionSortIndirect(int[] perm, boolean[] a, int from, int to) {
        int i = from;
        while (++i < to) {
            int t = perm[i];
            int j = i;
            int u = perm[j - 1];
            while (!a[t] && a[u]) {
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

    public static void quickSortIndirect(int[] perm, boolean[] x, int from, int to) {
        int a;
        int c;
        int len = to - from;
        if (len < 16) {
            BooleanArrays.insertionSortIndirect(perm, x, from, to);
            return;
        }
        int m = from + len / 2;
        int l = from;
        int n = to - 1;
        if (len > 128) {
            int s = len / 8;
            l = BooleanArrays.med3Indirect(perm, x, l, l + s, l + 2 * s);
            m = BooleanArrays.med3Indirect(perm, x, m - s, m, m + s);
            n = BooleanArrays.med3Indirect(perm, x, n - 2 * s, n - s, n);
        }
        m = BooleanArrays.med3Indirect(perm, x, l, m, n);
        boolean v = x[perm[m]];
        int b = a = from;
        int d = c = to - 1;
        do {
            int comparison;
            if (b <= c && (comparison = Boolean.compare(x[perm[b]], v)) <= 0) {
                if (comparison == 0) {
                    IntArrays.swap(perm, a++, b);
                }
                ++b;
                continue;
            }
            while (c >= b && (comparison = Boolean.compare(x[perm[c]], v)) >= 0) {
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
            BooleanArrays.quickSortIndirect(perm, x, from, from + s);
        }
        if ((s = d - c) > 1) {
            BooleanArrays.quickSortIndirect(perm, x, to - s, to);
        }
    }

    public static void quickSortIndirect(int[] perm, boolean[] x) {
        BooleanArrays.quickSortIndirect(perm, x, 0, x.length);
    }

    public static void parallelQuickSortIndirect(int[] perm, boolean[] x, int from, int to) {
        if (to - from < 8192) {
            BooleanArrays.quickSortIndirect(perm, x, from, to);
        } else {
            ForkJoinPool pool = new ForkJoinPool(Runtime.getRuntime().availableProcessors());
            pool.invoke(new ForkJoinQuickSortIndirect(perm, x, from, to));
            pool.shutdown();
        }
    }

    public static void parallelQuickSortIndirect(int[] perm, boolean[] x) {
        BooleanArrays.parallelQuickSortIndirect(perm, x, 0, x.length);
    }

    public static void stabilize(int[] perm, boolean[] x, int from, int to) {
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

    public static void stabilize(int[] perm, boolean[] x) {
        BooleanArrays.stabilize(perm, x, 0, perm.length);
    }

    private static int med3(boolean[] x, boolean[] y, int a, int b, int c) {
        int bc;
        int t = Boolean.compare(x[a], x[b]);
        int ab = t == 0 ? Boolean.compare(y[a], y[b]) : t;
        t = Boolean.compare(x[a], x[c]);
        int ac = t == 0 ? Boolean.compare(y[a], y[c]) : t;
        t = Boolean.compare(x[b], x[c]);
        int n = bc = t == 0 ? Boolean.compare(y[b], y[c]) : t;
        return ab < 0 ? (bc < 0 ? b : (ac < 0 ? c : a)) : (bc > 0 ? b : (ac > 0 ? c : a));
    }

    private static void swap(boolean[] x, boolean[] y, int a, int b) {
        boolean t = x[a];
        boolean u = y[a];
        x[a] = x[b];
        y[a] = y[b];
        x[b] = t;
        y[b] = u;
    }

    private static void swap(boolean[] x, boolean[] y, int a, int b, int n) {
        int i = 0;
        while (i < n) {
            BooleanArrays.swap(x, y, a, b);
            ++i;
            ++a;
            ++b;
        }
    }

    private static void selectionSort(boolean[] a, boolean[] b, int from, int to) {
        for (int i = from; i < to - 1; ++i) {
            int m = i;
            for (int j = i + 1; j < to; ++j) {
                int u = Boolean.compare(a[j], a[m]);
                if (u >= 0 && (u != 0 || b[j] || !b[m])) continue;
                m = j;
            }
            if (m == i) continue;
            boolean t = a[i];
            a[i] = a[m];
            a[m] = t;
            t = b[i];
            b[i] = b[m];
            b[m] = t;
        }
    }

    public static void quickSort(boolean[] x, boolean[] y, int from, int to) {
        int c;
        int a;
        int len = to - from;
        if (len < 16) {
            BooleanArrays.selectionSort(x, y, from, to);
            return;
        }
        int m = from + len / 2;
        int l = from;
        int n = to - 1;
        if (len > 128) {
            int s = len / 8;
            l = BooleanArrays.med3(x, y, l, l + s, l + 2 * s);
            m = BooleanArrays.med3(x, y, m - s, m, m + s);
            n = BooleanArrays.med3(x, y, n - 2 * s, n - s, n);
        }
        m = BooleanArrays.med3(x, y, l, m, n);
        boolean v = x[m];
        boolean w = y[m];
        int b = a = from;
        int d = c = to - 1;
        do {
            int t;
            int comparison;
            if (b <= c && (comparison = (t = Boolean.compare(x[b], v)) == 0 ? Boolean.compare(y[b], w) : t) <= 0) {
                if (comparison == 0) {
                    BooleanArrays.swap(x, y, a++, b);
                }
                ++b;
                continue;
            }
            while (c >= b && (comparison = (t = Boolean.compare(x[c], v)) == 0 ? Boolean.compare(y[c], w) : t) >= 0) {
                if (comparison == 0) {
                    BooleanArrays.swap(x, y, c, d--);
                }
                --c;
            }
            if (b > c) break;
            BooleanArrays.swap(x, y, b++, c--);
        } while (true);
        int s = Math.min(a - from, b - a);
        BooleanArrays.swap(x, y, from, b - s, s);
        s = Math.min(d - c, to - d - 1);
        BooleanArrays.swap(x, y, b, to - s, s);
        s = b - a;
        if (s > 1) {
            BooleanArrays.quickSort(x, y, from, from + s);
        }
        if ((s = d - c) > 1) {
            BooleanArrays.quickSort(x, y, to - s, to);
        }
    }

    public static void quickSort(boolean[] x, boolean[] y) {
        BooleanArrays.ensureSameLength(x, y);
        BooleanArrays.quickSort(x, y, 0, x.length);
    }

    public static void parallelQuickSort(boolean[] x, boolean[] y, int from, int to) {
        if (to - from < 8192) {
            BooleanArrays.quickSort(x, y, from, to);
        }
        ForkJoinPool pool = new ForkJoinPool(Runtime.getRuntime().availableProcessors());
        pool.invoke(new ForkJoinQuickSort2(x, y, from, to));
        pool.shutdown();
    }

    public static void parallelQuickSort(boolean[] x, boolean[] y) {
        BooleanArrays.ensureSameLength(x, y);
        BooleanArrays.parallelQuickSort(x, y, 0, x.length);
    }

    public static void mergeSort(boolean[] a, int from, int to, boolean[] supp) {
        int len = to - from;
        if (len < 16) {
            BooleanArrays.insertionSort(a, from, to);
            return;
        }
        int mid = from + to >>> 1;
        BooleanArrays.mergeSort(supp, from, mid, a);
        BooleanArrays.mergeSort(supp, mid, to, a);
        if (!supp[mid - 1] || supp[mid]) {
            System.arraycopy(supp, from, a, from, len);
            return;
        }
        int p = from;
        int q = mid;
        for (int i = from; i < to; ++i) {
            a[i] = q >= to || p < mid && (!supp[p] || supp[q]) ? supp[p++] : supp[q++];
        }
    }

    public static void mergeSort(boolean[] a, int from, int to) {
        BooleanArrays.mergeSort(a, from, to, (boolean[])a.clone());
    }

    public static void mergeSort(boolean[] a) {
        BooleanArrays.mergeSort(a, 0, a.length);
    }

    public static void mergeSort(boolean[] a, int from, int to, BooleanComparator comp, boolean[] supp) {
        int len = to - from;
        if (len < 16) {
            BooleanArrays.insertionSort(a, from, to, comp);
            return;
        }
        int mid = from + to >>> 1;
        BooleanArrays.mergeSort(supp, from, mid, comp, a);
        BooleanArrays.mergeSort(supp, mid, to, comp, a);
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

    public static void mergeSort(boolean[] a, int from, int to, BooleanComparator comp) {
        BooleanArrays.mergeSort(a, from, to, comp, (boolean[])a.clone());
    }

    public static void mergeSort(boolean[] a, BooleanComparator comp) {
        BooleanArrays.mergeSort(a, 0, a.length, comp);
    }

    public static boolean[] shuffle(boolean[] a, int from, int to, Random random) {
        int i = to - from;
        while (i-- != 0) {
            int p = random.nextInt(i + 1);
            boolean t = a[from + i];
            a[from + i] = a[from + p];
            a[from + p] = t;
        }
        return a;
    }

    public static boolean[] shuffle(boolean[] a, Random random) {
        int i = a.length;
        while (i-- != 0) {
            int p = random.nextInt(i + 1);
            boolean t = a[i];
            a[i] = a[p];
            a[p] = t;
        }
        return a;
    }

    public static boolean[] reverse(boolean[] a) {
        int length = a.length;
        int i = length / 2;
        while (i-- != 0) {
            boolean t = a[length - i - 1];
            a[length - i - 1] = a[i];
            a[i] = t;
        }
        return a;
    }

    public static boolean[] reverse(boolean[] a, int from, int to) {
        int length = to - from;
        int i = length / 2;
        while (i-- != 0) {
            boolean t = a[from + length - i - 1];
            a[from + length - i - 1] = a[from + i];
            a[from + i] = t;
        }
        return a;
    }

    private static final class ArrayHashStrategy
    implements Hash.Strategy<boolean[]>,
    Serializable {
        private static final long serialVersionUID = -7046029254386353129L;

        private ArrayHashStrategy() {
        }

        @Override
        public int hashCode(boolean[] o) {
            return java.util.Arrays.hashCode(o);
        }

        @Override
        public boolean equals(boolean[] a, boolean[] b) {
            return java.util.Arrays.equals(a, b);
        }
    }

    protected static class ForkJoinQuickSort2
    extends RecursiveAction {
        private static final long serialVersionUID = 1L;
        private final int from;
        private final int to;
        private final boolean[] x;
        private final boolean[] y;

        public ForkJoinQuickSort2(boolean[] x, boolean[] y, int from, int to) {
            this.from = from;
            this.to = to;
            this.x = x;
            this.y = y;
        }

        @Override
        protected void compute() {
            int c;
            int a;
            boolean[] x = this.x;
            boolean[] y = this.y;
            int len = this.to - this.from;
            if (len < 8192) {
                BooleanArrays.quickSort(x, y, this.from, this.to);
                return;
            }
            int m = this.from + len / 2;
            int l = this.from;
            int n = this.to - 1;
            int s = len / 8;
            l = BooleanArrays.med3(x, y, l, l + s, l + 2 * s);
            m = BooleanArrays.med3(x, y, m - s, m, m + s);
            n = BooleanArrays.med3(x, y, n - 2 * s, n - s, n);
            m = BooleanArrays.med3(x, y, l, m, n);
            boolean v = x[m];
            boolean w = y[m];
            int b = a = this.from;
            int d = c = this.to - 1;
            do {
                int t;
                int comparison;
                if (b <= c && (comparison = (t = Boolean.compare(x[b], v)) == 0 ? Boolean.compare(y[b], w) : t) <= 0) {
                    if (comparison == 0) {
                        BooleanArrays.swap(x, y, a++, b);
                    }
                    ++b;
                    continue;
                }
                while (c >= b && (comparison = (t = Boolean.compare(x[c], v)) == 0 ? Boolean.compare(y[c], w) : t) >= 0) {
                    if (comparison == 0) {
                        BooleanArrays.swap(x, y, c, d--);
                    }
                    --c;
                }
                if (b > c) break;
                BooleanArrays.swap(x, y, b++, c--);
            } while (true);
            s = Math.min(a - this.from, b - a);
            BooleanArrays.swap(x, y, this.from, b - s, s);
            s = Math.min(d - c, this.to - d - 1);
            BooleanArrays.swap(x, y, b, this.to - s, s);
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
        private final boolean[] x;

        public ForkJoinQuickSortIndirect(int[] perm, boolean[] x, int from, int to) {
            this.from = from;
            this.to = to;
            this.x = x;
            this.perm = perm;
        }

        @Override
        protected void compute() {
            int a;
            int c;
            boolean[] x = this.x;
            int len = this.to - this.from;
            if (len < 8192) {
                BooleanArrays.quickSortIndirect(this.perm, x, this.from, this.to);
                return;
            }
            int m = this.from + len / 2;
            int l = this.from;
            int n = this.to - 1;
            int s = len / 8;
            l = BooleanArrays.med3Indirect(this.perm, x, l, l + s, l + 2 * s);
            m = BooleanArrays.med3Indirect(this.perm, x, m - s, m, m + s);
            n = BooleanArrays.med3Indirect(this.perm, x, n - 2 * s, n - s, n);
            m = BooleanArrays.med3Indirect(this.perm, x, l, m, n);
            boolean v = x[this.perm[m]];
            int b = a = this.from;
            int d = c = this.to - 1;
            do {
                int comparison;
                if (b <= c && (comparison = Boolean.compare(x[this.perm[b]], v)) <= 0) {
                    if (comparison == 0) {
                        IntArrays.swap(this.perm, a++, b);
                    }
                    ++b;
                    continue;
                }
                while (c >= b && (comparison = Boolean.compare(x[this.perm[c]], v)) >= 0) {
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
        private final boolean[] x;

        public ForkJoinQuickSort(boolean[] x, int from, int to) {
            this.from = from;
            this.to = to;
            this.x = x;
        }

        @Override
        protected void compute() {
            int a;
            int c;
            boolean[] x = this.x;
            int len = this.to - this.from;
            if (len < 8192) {
                BooleanArrays.quickSort(x, this.from, this.to);
                return;
            }
            int m = this.from + len / 2;
            int l = this.from;
            int n = this.to - 1;
            int s = len / 8;
            l = BooleanArrays.med3(x, l, l + s, l + 2 * s);
            m = BooleanArrays.med3(x, m - s, m, m + s);
            n = BooleanArrays.med3(x, n - 2 * s, n - s, n);
            m = BooleanArrays.med3(x, l, m, n);
            boolean v = x[m];
            int b = a = this.from;
            int d = c = this.to - 1;
            do {
                int comparison;
                if (b <= c && (comparison = Boolean.compare(x[b], v)) <= 0) {
                    if (comparison == 0) {
                        BooleanArrays.swap(x, a++, b);
                    }
                    ++b;
                    continue;
                }
                while (c >= b && (comparison = Boolean.compare(x[c], v)) >= 0) {
                    if (comparison == 0) {
                        BooleanArrays.swap(x, c, d--);
                    }
                    --c;
                }
                if (b > c) break;
                BooleanArrays.swap(x, b++, c--);
            } while (true);
            s = Math.min(a - this.from, b - a);
            BooleanArrays.swap(x, this.from, b - s, s);
            s = Math.min(d - c, this.to - d - 1);
            BooleanArrays.swap(x, b, this.to - s, s);
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
        private final boolean[] x;
        private final BooleanComparator comp;

        public ForkJoinQuickSortComp(boolean[] x, int from, int to, BooleanComparator comp) {
            this.from = from;
            this.to = to;
            this.x = x;
            this.comp = comp;
        }

        @Override
        protected void compute() {
            int a;
            int c;
            boolean[] x = this.x;
            int len = this.to - this.from;
            if (len < 8192) {
                BooleanArrays.quickSort(x, this.from, this.to, this.comp);
                return;
            }
            int m = this.from + len / 2;
            int l = this.from;
            int n = this.to - 1;
            int s = len / 8;
            l = BooleanArrays.med3(x, l, l + s, l + 2 * s, this.comp);
            m = BooleanArrays.med3(x, m - s, m, m + s, this.comp);
            n = BooleanArrays.med3(x, n - 2 * s, n - s, n, this.comp);
            m = BooleanArrays.med3(x, l, m, n, this.comp);
            boolean v = x[m];
            int b = a = this.from;
            int d = c = this.to - 1;
            do {
                int comparison;
                if (b <= c && (comparison = this.comp.compare(x[b], v)) <= 0) {
                    if (comparison == 0) {
                        BooleanArrays.swap(x, a++, b);
                    }
                    ++b;
                    continue;
                }
                while (c >= b && (comparison = this.comp.compare(x[c], v)) >= 0) {
                    if (comparison == 0) {
                        BooleanArrays.swap(x, c, d--);
                    }
                    --c;
                }
                if (b > c) break;
                BooleanArrays.swap(x, b++, c--);
            } while (true);
            s = Math.min(a - this.from, b - a);
            BooleanArrays.swap(x, this.from, b - s, s);
            s = Math.min(d - c, this.to - d - 1);
            BooleanArrays.swap(x, b, this.to - s, s);
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

