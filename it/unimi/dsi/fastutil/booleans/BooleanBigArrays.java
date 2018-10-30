/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.booleans;

import it.unimi.dsi.fastutil.BigArrays;
import it.unimi.dsi.fastutil.Hash;
import it.unimi.dsi.fastutil.booleans.BooleanArrays;
import it.unimi.dsi.fastutil.booleans.BooleanComparator;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Random;

public final class BooleanBigArrays {
    public static final boolean[][] EMPTY_BIG_ARRAY = new boolean[0][];
    public static final Hash.Strategy HASH_STRATEGY = new BigArrayHashStrategy();
    private static final int SMALL = 7;
    private static final int MEDIUM = 40;

    private BooleanBigArrays() {
    }

    public static boolean get(boolean[][] array, long index) {
        return array[BigArrays.segment(index)][BigArrays.displacement(index)];
    }

    public static void set(boolean[][] array, long index, boolean value) {
        array[BigArrays.segment((long)index)][BigArrays.displacement((long)index)] = value;
    }

    public static void swap(boolean[][] array, long first, long second) {
        boolean t = array[BigArrays.segment(first)][BigArrays.displacement(first)];
        array[BigArrays.segment((long)first)][BigArrays.displacement((long)first)] = array[BigArrays.segment(second)][BigArrays.displacement(second)];
        array[BigArrays.segment((long)second)][BigArrays.displacement((long)second)] = t;
    }

    public static long length(boolean[][] array) {
        int length = array.length;
        return length == 0 ? 0L : BigArrays.start(length - 1) + (long)array[length - 1].length;
    }

    public static void copy(boolean[][] srcArray, long srcPos, boolean[][] destArray, long destPos, long length) {
        if (destPos <= srcPos) {
            int srcSegment = BigArrays.segment(srcPos);
            int destSegment = BigArrays.segment(destPos);
            int srcDispl = BigArrays.displacement(srcPos);
            int destDispl = BigArrays.displacement(destPos);
            while (length > 0L) {
                int l = (int)Math.min(length, (long)Math.min(srcArray[srcSegment].length - srcDispl, destArray[destSegment].length - destDispl));
                System.arraycopy(srcArray[srcSegment], srcDispl, destArray[destSegment], destDispl, l);
                if ((srcDispl += l) == 134217728) {
                    srcDispl = 0;
                    ++srcSegment;
                }
                if ((destDispl += l) == 134217728) {
                    destDispl = 0;
                    ++destSegment;
                }
                length -= (long)l;
            }
        } else {
            int srcSegment = BigArrays.segment(srcPos + length);
            int destSegment = BigArrays.segment(destPos + length);
            int srcDispl = BigArrays.displacement(srcPos + length);
            int destDispl = BigArrays.displacement(destPos + length);
            while (length > 0L) {
                if (srcDispl == 0) {
                    srcDispl = 134217728;
                    --srcSegment;
                }
                if (destDispl == 0) {
                    destDispl = 134217728;
                    --destSegment;
                }
                int l = (int)Math.min(length, (long)Math.min(srcDispl, destDispl));
                System.arraycopy(srcArray[srcSegment], srcDispl - l, destArray[destSegment], destDispl - l, l);
                srcDispl -= l;
                destDispl -= l;
                length -= (long)l;
            }
        }
    }

    public static void copyFromBig(boolean[][] srcArray, long srcPos, boolean[] destArray, int destPos, int length) {
        int srcSegment = BigArrays.segment(srcPos);
        int srcDispl = BigArrays.displacement(srcPos);
        while (length > 0) {
            int l = Math.min(srcArray[srcSegment].length - srcDispl, length);
            System.arraycopy(srcArray[srcSegment], srcDispl, destArray, destPos, l);
            if ((srcDispl += l) == 134217728) {
                srcDispl = 0;
                ++srcSegment;
            }
            destPos += l;
            length -= l;
        }
    }

    public static void copyToBig(boolean[] srcArray, int srcPos, boolean[][] destArray, long destPos, long length) {
        int destSegment = BigArrays.segment(destPos);
        int destDispl = BigArrays.displacement(destPos);
        while (length > 0L) {
            int l = (int)Math.min((long)(destArray[destSegment].length - destDispl), length);
            System.arraycopy(srcArray, srcPos, destArray[destSegment], destDispl, l);
            if ((destDispl += l) == 134217728) {
                destDispl = 0;
                ++destSegment;
            }
            srcPos += l;
            length -= (long)l;
        }
    }

    public static boolean[][] newBigArray(long length) {
        if (length == 0L) {
            return EMPTY_BIG_ARRAY;
        }
        BigArrays.ensureLength(length);
        int baseLength = (int)(length + 0x7FFFFFFL >>> 27);
        boolean[][] base = new boolean[baseLength][];
        int residual = (int)(length & 0x7FFFFFFL);
        if (residual != 0) {
            for (int i = 0; i < baseLength - 1; ++i) {
                base[i] = new boolean[134217728];
            }
            base[baseLength - 1] = new boolean[residual];
        } else {
            for (int i = 0; i < baseLength; ++i) {
                base[i] = new boolean[134217728];
            }
        }
        return base;
    }

    public static boolean[][] wrap(boolean[] array) {
        if (array.length == 0) {
            return EMPTY_BIG_ARRAY;
        }
        if (array.length <= 134217728) {
            return new boolean[][]{array};
        }
        boolean[][] bigArray = BooleanBigArrays.newBigArray(array.length);
        for (int i = 0; i < bigArray.length; ++i) {
            System.arraycopy(array, (int)BigArrays.start(i), bigArray[i], 0, bigArray[i].length);
        }
        return bigArray;
    }

    public static boolean[][] ensureCapacity(boolean[][] array, long length) {
        return BooleanBigArrays.ensureCapacity(array, length, BooleanBigArrays.length(array));
    }

    public static boolean[][] ensureCapacity(boolean[][] array, long length, long preserve) {
        long oldLength = BooleanBigArrays.length(array);
        if (length > oldLength) {
            BigArrays.ensureLength(length);
            int valid = array.length - (array.length == 0 || array.length > 0 && array[array.length - 1].length == 134217728 ? 0 : 1);
            int baseLength = (int)(length + 0x7FFFFFFL >>> 27);
            boolean[][] base = (boolean[][])Arrays.copyOf(array, baseLength);
            int residual = (int)(length & 0x7FFFFFFL);
            if (residual != 0) {
                for (int i = valid; i < baseLength - 1; ++i) {
                    base[i] = new boolean[134217728];
                }
                base[baseLength - 1] = new boolean[residual];
            } else {
                for (int i = valid; i < baseLength; ++i) {
                    base[i] = new boolean[134217728];
                }
            }
            if (preserve - (long)valid * 0x8000000L > 0L) {
                BooleanBigArrays.copy(array, (long)valid * 0x8000000L, base, (long)valid * 0x8000000L, preserve - (long)valid * 0x8000000L);
            }
            return base;
        }
        return array;
    }

    public static boolean[][] grow(boolean[][] array, long length) {
        long oldLength = BooleanBigArrays.length(array);
        return length > oldLength ? BooleanBigArrays.grow(array, length, oldLength) : array;
    }

    public static boolean[][] grow(boolean[][] array, long length, long preserve) {
        long oldLength = BooleanBigArrays.length(array);
        return length > oldLength ? BooleanBigArrays.ensureCapacity(array, Math.max(2L * oldLength, length), preserve) : array;
    }

    public static boolean[][] trim(boolean[][] array, long length) {
        BigArrays.ensureLength(length);
        long oldLength = BooleanBigArrays.length(array);
        if (length >= oldLength) {
            return array;
        }
        int baseLength = (int)(length + 0x7FFFFFFL >>> 27);
        boolean[][] base = (boolean[][])Arrays.copyOf(array, baseLength);
        int residual = (int)(length & 0x7FFFFFFL);
        if (residual != 0) {
            base[baseLength - 1] = BooleanArrays.trim(base[baseLength - 1], residual);
        }
        return base;
    }

    public static boolean[][] setLength(boolean[][] array, long length) {
        long oldLength = BooleanBigArrays.length(array);
        if (length == oldLength) {
            return array;
        }
        if (length < oldLength) {
            return BooleanBigArrays.trim(array, length);
        }
        return BooleanBigArrays.ensureCapacity(array, length);
    }

    public static boolean[][] copy(boolean[][] array, long offset, long length) {
        BooleanBigArrays.ensureOffsetLength(array, offset, length);
        boolean[][] a = BooleanBigArrays.newBigArray(length);
        BooleanBigArrays.copy(array, offset, a, 0L, length);
        return a;
    }

    public static boolean[][] copy(boolean[][] array) {
        boolean[][] base = (boolean[][])array.clone();
        int i = base.length;
        while (i-- != 0) {
            base[i] = (boolean[])array[i].clone();
        }
        return base;
    }

    public static void fill(boolean[][] array, boolean value) {
        int i = array.length;
        while (i-- != 0) {
            Arrays.fill(array[i], value);
        }
    }

    public static void fill(boolean[][] array, long from, long to, boolean value) {
        long length = BooleanBigArrays.length(array);
        BigArrays.ensureFromTo(length, from, to);
        int fromSegment = BigArrays.segment(from);
        int toSegment = BigArrays.segment(to);
        int fromDispl = BigArrays.displacement(from);
        int toDispl = BigArrays.displacement(to);
        if (fromSegment == toSegment) {
            Arrays.fill(array[fromSegment], fromDispl, toDispl, value);
            return;
        }
        if (toDispl != 0) {
            Arrays.fill(array[toSegment], 0, toDispl, value);
        }
        while (--toSegment > fromSegment) {
            Arrays.fill(array[toSegment], value);
        }
        Arrays.fill(array[fromSegment], fromDispl, 134217728, value);
    }

    public static boolean equals(boolean[][] a1, boolean[][] a2) {
        if (BooleanBigArrays.length(a1) != BooleanBigArrays.length(a2)) {
            return false;
        }
        int i = a1.length;
        while (i-- != 0) {
            boolean[] t = a1[i];
            boolean[] u = a2[i];
            int j = t.length;
            while (j-- != 0) {
                if (t[j] == u[j]) continue;
                return false;
            }
        }
        return true;
    }

    public static String toString(boolean[][] a) {
        if (a == null) {
            return "null";
        }
        long last = BooleanBigArrays.length(a) - 1L;
        if (last == -1L) {
            return "[]";
        }
        StringBuilder b = new StringBuilder();
        b.append('[');
        long i = 0L;
        do {
            b.append(String.valueOf(BooleanBigArrays.get(a, i)));
            if (i == last) {
                return b.append(']').toString();
            }
            b.append(", ");
            ++i;
        } while (true);
    }

    public static void ensureFromTo(boolean[][] a, long from, long to) {
        BigArrays.ensureFromTo(BooleanBigArrays.length(a), from, to);
    }

    public static void ensureOffsetLength(boolean[][] a, long offset, long length) {
        BigArrays.ensureOffsetLength(BooleanBigArrays.length(a), offset, length);
    }

    private static void vecSwap(boolean[][] x, long a, long b, long n) {
        int i = 0;
        while ((long)i < n) {
            BooleanBigArrays.swap(x, a, b);
            ++i;
            ++a;
            ++b;
        }
    }

    private static long med3(boolean[][] x, long a, long b, long c, BooleanComparator comp) {
        int ab = comp.compare(BooleanBigArrays.get(x, a), BooleanBigArrays.get(x, b));
        int ac = comp.compare(BooleanBigArrays.get(x, a), BooleanBigArrays.get(x, c));
        int bc = comp.compare(BooleanBigArrays.get(x, b), BooleanBigArrays.get(x, c));
        return ab < 0 ? (bc < 0 ? b : (ac < 0 ? c : a)) : (bc > 0 ? b : (ac > 0 ? c : a));
    }

    private static void selectionSort(boolean[][] a, long from, long to, BooleanComparator comp) {
        for (long i = from; i < to - 1L; ++i) {
            long m = i;
            for (long j = i + 1L; j < to; ++j) {
                if (comp.compare(BooleanBigArrays.get(a, j), BooleanBigArrays.get(a, m)) >= 0) continue;
                m = j;
            }
            if (m == i) continue;
            BooleanBigArrays.swap(a, i, m);
        }
    }

    public static void quickSort(boolean[][] x, long from, long to, BooleanComparator comp) {
        long c;
        long a;
        long len = to - from;
        if (len < 7L) {
            BooleanBigArrays.selectionSort(x, from, to, comp);
            return;
        }
        long m = from + len / 2L;
        if (len > 7L) {
            long l = from;
            long n = to - 1L;
            if (len > 40L) {
                long s = len / 8L;
                l = BooleanBigArrays.med3(x, l, l + s, l + 2L * s, comp);
                m = BooleanBigArrays.med3(x, m - s, m, m + s, comp);
                n = BooleanBigArrays.med3(x, n - 2L * s, n - s, n, comp);
            }
            m = BooleanBigArrays.med3(x, l, m, n, comp);
        }
        boolean v = BooleanBigArrays.get(x, m);
        long b = a = from;
        long d = c = to - 1L;
        do {
            int comparison;
            if (b <= c && (comparison = comp.compare(BooleanBigArrays.get(x, b), v)) <= 0) {
                if (comparison == 0) {
                    BooleanBigArrays.swap(x, a++, b);
                }
                ++b;
                continue;
            }
            while (c >= b && (comparison = comp.compare(BooleanBigArrays.get(x, c), v)) >= 0) {
                if (comparison == 0) {
                    BooleanBigArrays.swap(x, c, d--);
                }
                --c;
            }
            if (b > c) break;
            BooleanBigArrays.swap(x, b++, c--);
        } while (true);
        long n = to;
        long s = Math.min(a - from, b - a);
        BooleanBigArrays.vecSwap(x, from, b - s, s);
        s = Math.min(d - c, n - d - 1L);
        BooleanBigArrays.vecSwap(x, b, n - s, s);
        s = b - a;
        if (s > 1L) {
            BooleanBigArrays.quickSort(x, from, from + s, comp);
        }
        if ((s = d - c) > 1L) {
            BooleanBigArrays.quickSort(x, n - s, n, comp);
        }
    }

    private static long med3(boolean[][] x, long a, long b, long c) {
        int ab = Boolean.compare(BooleanBigArrays.get(x, a), BooleanBigArrays.get(x, b));
        int ac = Boolean.compare(BooleanBigArrays.get(x, a), BooleanBigArrays.get(x, c));
        int bc = Boolean.compare(BooleanBigArrays.get(x, b), BooleanBigArrays.get(x, c));
        return ab < 0 ? (bc < 0 ? b : (ac < 0 ? c : a)) : (bc > 0 ? b : (ac > 0 ? c : a));
    }

    private static void selectionSort(boolean[][] a, long from, long to) {
        for (long i = from; i < to - 1L; ++i) {
            long m = i;
            for (long j = i + 1L; j < to; ++j) {
                if (BooleanBigArrays.get(a, j) || !BooleanBigArrays.get(a, m)) continue;
                m = j;
            }
            if (m == i) continue;
            BooleanBigArrays.swap(a, i, m);
        }
    }

    public static void quickSort(boolean[][] x, BooleanComparator comp) {
        BooleanBigArrays.quickSort(x, 0L, BooleanBigArrays.length(x), comp);
    }

    public static void quickSort(boolean[][] x, long from, long to) {
        long a;
        long c;
        long len = to - from;
        if (len < 7L) {
            BooleanBigArrays.selectionSort(x, from, to);
            return;
        }
        long m = from + len / 2L;
        if (len > 7L) {
            long l = from;
            long n = to - 1L;
            if (len > 40L) {
                long s = len / 8L;
                l = BooleanBigArrays.med3(x, l, l + s, l + 2L * s);
                m = BooleanBigArrays.med3(x, m - s, m, m + s);
                n = BooleanBigArrays.med3(x, n - 2L * s, n - s, n);
            }
            m = BooleanBigArrays.med3(x, l, m, n);
        }
        boolean v = BooleanBigArrays.get(x, m);
        long b = a = from;
        long d = c = to - 1L;
        do {
            int comparison;
            if (b <= c && (comparison = Boolean.compare(BooleanBigArrays.get(x, b), v)) <= 0) {
                if (comparison == 0) {
                    BooleanBigArrays.swap(x, a++, b);
                }
                ++b;
                continue;
            }
            while (c >= b && (comparison = Boolean.compare(BooleanBigArrays.get(x, c), v)) >= 0) {
                if (comparison == 0) {
                    BooleanBigArrays.swap(x, c, d--);
                }
                --c;
            }
            if (b > c) break;
            BooleanBigArrays.swap(x, b++, c--);
        } while (true);
        long n = to;
        long s = Math.min(a - from, b - a);
        BooleanBigArrays.vecSwap(x, from, b - s, s);
        s = Math.min(d - c, n - d - 1L);
        BooleanBigArrays.vecSwap(x, b, n - s, s);
        s = b - a;
        if (s > 1L) {
            BooleanBigArrays.quickSort(x, from, from + s);
        }
        if ((s = d - c) > 1L) {
            BooleanBigArrays.quickSort(x, n - s, n);
        }
    }

    public static void quickSort(boolean[][] x) {
        BooleanBigArrays.quickSort(x, 0L, BooleanBigArrays.length(x));
    }

    public static boolean[][] shuffle(boolean[][] a, long from, long to, Random random) {
        long i = to - from;
        while (i-- != 0L) {
            long p = (random.nextLong() & Long.MAX_VALUE) % (i + 1L);
            boolean t = BooleanBigArrays.get(a, from + i);
            BooleanBigArrays.set(a, from + i, BooleanBigArrays.get(a, from + p));
            BooleanBigArrays.set(a, from + p, t);
        }
        return a;
    }

    public static boolean[][] shuffle(boolean[][] a, Random random) {
        long i = BooleanBigArrays.length(a);
        while (i-- != 0L) {
            long p = (random.nextLong() & Long.MAX_VALUE) % (i + 1L);
            boolean t = BooleanBigArrays.get(a, i);
            BooleanBigArrays.set(a, i, BooleanBigArrays.get(a, p));
            BooleanBigArrays.set(a, p, t);
        }
        return a;
    }

    private static final class BigArrayHashStrategy
    implements Hash.Strategy<boolean[][]>,
    Serializable {
        private static final long serialVersionUID = -7046029254386353129L;

        private BigArrayHashStrategy() {
        }

        @Override
        public int hashCode(boolean[][] o) {
            return Arrays.deepHashCode((Object[])o);
        }

        @Override
        public boolean equals(boolean[][] a, boolean[][] b) {
            return BooleanBigArrays.equals(a, b);
        }
    }

}

