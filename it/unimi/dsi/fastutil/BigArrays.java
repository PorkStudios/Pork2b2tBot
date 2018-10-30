/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil;

import it.unimi.dsi.fastutil.BigSwapper;
import it.unimi.dsi.fastutil.ints.IntBigArrays;
import it.unimi.dsi.fastutil.longs.LongComparator;
import java.io.PrintStream;

public class BigArrays {
    public static final int SEGMENT_SHIFT = 27;
    public static final int SEGMENT_SIZE = 134217728;
    public static final int SEGMENT_MASK = 134217727;
    private static final int SMALL = 7;
    private static final int MEDIUM = 40;

    protected BigArrays() {
    }

    public static int segment(long index) {
        return (int)(index >>> 27);
    }

    public static int displacement(long index) {
        return (int)(index & 0x7FFFFFFL);
    }

    public static long start(int segment) {
        return (long)segment << 27;
    }

    public static long index(int segment, int displacement) {
        return BigArrays.start(segment) + (long)displacement;
    }

    public static void ensureFromTo(long bigArrayLength, long from, long to) {
        if (from < 0L) {
            throw new ArrayIndexOutOfBoundsException("Start index (" + from + ") is negative");
        }
        if (from > to) {
            throw new IllegalArgumentException("Start index (" + from + ") is greater than end index (" + to + ")");
        }
        if (to > bigArrayLength) {
            throw new ArrayIndexOutOfBoundsException("End index (" + to + ") is greater than big-array length (" + bigArrayLength + ")");
        }
    }

    public static void ensureOffsetLength(long bigArrayLength, long offset, long length) {
        if (offset < 0L) {
            throw new ArrayIndexOutOfBoundsException("Offset (" + offset + ") is negative");
        }
        if (length < 0L) {
            throw new IllegalArgumentException("Length (" + length + ") is negative");
        }
        if (offset + length > bigArrayLength) {
            throw new ArrayIndexOutOfBoundsException("Last index (" + (offset + length) + ") is greater than big-array length (" + bigArrayLength + ")");
        }
    }

    public static void ensureLength(long bigArrayLength) {
        if (bigArrayLength < 0L) {
            throw new IllegalArgumentException("Negative big-array size: " + bigArrayLength);
        }
        if (bigArrayLength >= 288230376017494016L) {
            throw new IllegalArgumentException("Big-array size too big: " + bigArrayLength);
        }
    }

    private static void inPlaceMerge(long from, long mid, long to, LongComparator comp, BigSwapper swapper) {
        long secondCut;
        long firstCut;
        if (from >= mid || mid >= to) {
            return;
        }
        if (to - from == 2L) {
            if (comp.compare(mid, from) < 0) {
                swapper.swap(from, mid);
            }
            return;
        }
        if (mid - from > to - mid) {
            firstCut = from + (mid - from) / 2L;
            secondCut = BigArrays.lowerBound(mid, to, firstCut, comp);
        } else {
            secondCut = mid + (to - mid) / 2L;
            firstCut = BigArrays.upperBound(from, mid, secondCut, comp);
        }
        long first2 = firstCut;
        long middle2 = mid;
        long last2 = secondCut;
        if (middle2 != first2 && middle2 != last2) {
            long first1 = first2;
            long last1 = middle2;
            while (first1 < --last1) {
                swapper.swap(first1++, last1);
            }
            first1 = middle2;
            last1 = last2;
            while (first1 < --last1) {
                swapper.swap(first1++, last1);
            }
            first1 = first2;
            last1 = last2;
            while (first1 < --last1) {
                swapper.swap(first1++, last1);
            }
        }
        mid = firstCut + (secondCut - mid);
        BigArrays.inPlaceMerge(from, firstCut, mid, comp, swapper);
        BigArrays.inPlaceMerge(mid, secondCut, to, comp, swapper);
    }

    private static long lowerBound(long mid, long to, long firstCut, LongComparator comp) {
        long len = to - mid;
        while (len > 0L) {
            long half = len / 2L;
            long middle = mid + half;
            if (comp.compare(middle, firstCut) < 0) {
                mid = middle + 1L;
                len -= half + 1L;
                continue;
            }
            len = half;
        }
        return mid;
    }

    private static long med3(long a, long b, long c, LongComparator comp) {
        int ab = comp.compare(a, b);
        int ac = comp.compare(a, c);
        int bc = comp.compare(b, c);
        return ab < 0 ? (bc < 0 ? b : (ac < 0 ? c : a)) : (bc > 0 ? b : (ac > 0 ? c : a));
    }

    public static void mergeSort(long from, long to, LongComparator comp, BigSwapper swapper) {
        long length = to - from;
        if (length < 7L) {
            for (long i = from; i < to; ++i) {
                for (long j = i; j > from && comp.compare(j - 1L, j) > 0; --j) {
                    swapper.swap(j, j - 1L);
                }
            }
            return;
        }
        long mid = from + to >>> 1;
        BigArrays.mergeSort(from, mid, comp, swapper);
        BigArrays.mergeSort(mid, to, comp, swapper);
        if (comp.compare(mid - 1L, mid) <= 0) {
            return;
        }
        BigArrays.inPlaceMerge(from, mid, to, comp, swapper);
    }

    public static void quickSort(long from, long to, LongComparator comp, BigSwapper swapper) {
        long a;
        long c;
        long len = to - from;
        if (len < 7L) {
            for (long i = from; i < to; ++i) {
                for (long j = i; j > from && comp.compare(j - 1L, j) > 0; --j) {
                    swapper.swap(j, j - 1L);
                }
            }
            return;
        }
        long m = from + len / 2L;
        if (len > 7L) {
            long l = from;
            long n = to - 1L;
            if (len > 40L) {
                long s = len / 8L;
                l = BigArrays.med3(l, l + s, l + 2L * s, comp);
                m = BigArrays.med3(m - s, m, m + s, comp);
                n = BigArrays.med3(n - 2L * s, n - s, n, comp);
            }
            m = BigArrays.med3(l, m, n, comp);
        }
        long b = a = from;
        long d = c = to - 1L;
        do {
            int comparison;
            if (b <= c && (comparison = comp.compare(b, m)) <= 0) {
                if (comparison == 0) {
                    if (a == m) {
                        m = b;
                    } else if (b == m) {
                        m = a;
                    }
                    swapper.swap(a++, b);
                }
                ++b;
                continue;
            }
            while (c >= b && (comparison = comp.compare(c, m)) >= 0) {
                if (comparison == 0) {
                    if (c == m) {
                        m = d;
                    } else if (d == m) {
                        m = c;
                    }
                    swapper.swap(c, d--);
                }
                --c;
            }
            if (b > c) break;
            if (b == m) {
                m = d;
            } else if (c == m) {
                m = c;
            }
            swapper.swap(b++, c--);
        } while (true);
        long n = from + len;
        long s = Math.min(a - from, b - a);
        BigArrays.vecSwap(swapper, from, b - s, s);
        s = Math.min(d - c, n - d - 1L);
        BigArrays.vecSwap(swapper, b, n - s, s);
        s = b - a;
        if (s > 1L) {
            BigArrays.quickSort(from, from + s, comp, swapper);
        }
        if ((s = d - c) > 1L) {
            BigArrays.quickSort(n - s, n, comp, swapper);
        }
    }

    private static long upperBound(long from, long mid, long secondCut, LongComparator comp) {
        long len = mid - from;
        while (len > 0L) {
            long half = len / 2L;
            long middle = from + half;
            if (comp.compare(secondCut, middle) < 0) {
                len = half;
                continue;
            }
            from = middle + 1L;
            len -= half + 1L;
        }
        return from;
    }

    private static void vecSwap(BigSwapper swapper, long from, long l, long s) {
        int i = 0;
        while ((long)i < s) {
            swapper.swap(from, l);
            ++i;
            ++from;
            ++l;
        }
    }

    public static void main(String[] arg) {
        int[][] a = IntBigArrays.newBigArray(1L << Integer.parseInt(arg[0]));
        int k = 10;
        while (k-- != 0) {
            long start = - System.currentTimeMillis();
            long x = 0L;
            long i = IntBigArrays.length(a);
            while (i-- != 0L) {
                x ^= i ^ (long)IntBigArrays.get(a, i);
            }
            if (x == 0L) {
                System.err.println();
            }
            System.out.println("Single loop: " + (start + System.currentTimeMillis()) + "ms");
            start = - System.currentTimeMillis();
            long y = 0L;
            int i2 = a.length;
            while (i2-- != 0) {
                int[] t = a[i2];
                int d = t.length;
                while (d-- != 0) {
                    y ^= (long)t[d] ^ BigArrays.index(i2, d);
                }
            }
            if (y == 0L) {
                System.err.println();
            }
            if (x != y) {
                throw new AssertionError();
            }
            System.out.println("Double loop: " + (start + System.currentTimeMillis()) + "ms");
            long z = 0L;
            long j = IntBigArrays.length(a);
            int i3 = a.length;
            while (i3-- != 0) {
                int[] t = a[i3];
                int d = t.length;
                while (d-- != 0) {
                    y ^= (long)t[d] ^ --j;
                }
            }
            if (z == 0L) {
                System.err.println();
            }
            if (x != z) {
                throw new AssertionError();
            }
            System.out.println("Double loop (with additional index): " + (start + System.currentTimeMillis()) + "ms");
        }
    }
}

