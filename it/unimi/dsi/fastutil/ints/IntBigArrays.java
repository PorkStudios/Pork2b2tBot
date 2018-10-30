/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.ints;

import it.unimi.dsi.fastutil.BigArrays;
import it.unimi.dsi.fastutil.Hash;
import it.unimi.dsi.fastutil.bytes.ByteBigArrays;
import it.unimi.dsi.fastutil.ints.IntArrays;
import it.unimi.dsi.fastutil.ints.IntComparator;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Random;

public final class IntBigArrays {
    public static final int[][] EMPTY_BIG_ARRAY = new int[0][];
    public static final Hash.Strategy HASH_STRATEGY = new BigArrayHashStrategy();
    private static final int SMALL = 7;
    private static final int MEDIUM = 40;
    private static final int DIGIT_BITS = 8;
    private static final int DIGIT_MASK = 255;
    private static final int DIGITS_PER_ELEMENT = 4;

    private IntBigArrays() {
    }

    public static int get(int[][] array, long index) {
        return array[BigArrays.segment(index)][BigArrays.displacement(index)];
    }

    public static void set(int[][] array, long index, int value) {
        array[BigArrays.segment((long)index)][BigArrays.displacement((long)index)] = value;
    }

    public static void swap(int[][] array, long first, long second) {
        int t = array[BigArrays.segment(first)][BigArrays.displacement(first)];
        array[BigArrays.segment((long)first)][BigArrays.displacement((long)first)] = array[BigArrays.segment(second)][BigArrays.displacement(second)];
        array[BigArrays.segment((long)second)][BigArrays.displacement((long)second)] = t;
    }

    public static void add(int[][] array, long index, int incr) {
        int[] arrn = array[BigArrays.segment(index)];
        int n = BigArrays.displacement(index);
        arrn[n] = arrn[n] + incr;
    }

    public static void mul(int[][] array, long index, int factor) {
        int[] arrn = array[BigArrays.segment(index)];
        int n = BigArrays.displacement(index);
        arrn[n] = arrn[n] * factor;
    }

    public static void incr(int[][] array, long index) {
        int[] arrn = array[BigArrays.segment(index)];
        int n = BigArrays.displacement(index);
        arrn[n] = arrn[n] + 1;
    }

    public static void decr(int[][] array, long index) {
        int[] arrn = array[BigArrays.segment(index)];
        int n = BigArrays.displacement(index);
        arrn[n] = arrn[n] - 1;
    }

    public static long length(int[][] array) {
        int length = array.length;
        return length == 0 ? 0L : BigArrays.start(length - 1) + (long)array[length - 1].length;
    }

    public static void copy(int[][] srcArray, long srcPos, int[][] destArray, long destPos, long length) {
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

    public static void copyFromBig(int[][] srcArray, long srcPos, int[] destArray, int destPos, int length) {
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

    public static void copyToBig(int[] srcArray, int srcPos, int[][] destArray, long destPos, long length) {
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

    public static int[][] newBigArray(long length) {
        if (length == 0L) {
            return EMPTY_BIG_ARRAY;
        }
        BigArrays.ensureLength(length);
        int baseLength = (int)(length + 0x7FFFFFFL >>> 27);
        int[][] base = new int[baseLength][];
        int residual = (int)(length & 0x7FFFFFFL);
        if (residual != 0) {
            for (int i = 0; i < baseLength - 1; ++i) {
                base[i] = new int[134217728];
            }
            base[baseLength - 1] = new int[residual];
        } else {
            for (int i = 0; i < baseLength; ++i) {
                base[i] = new int[134217728];
            }
        }
        return base;
    }

    public static int[][] wrap(int[] array) {
        if (array.length == 0) {
            return EMPTY_BIG_ARRAY;
        }
        if (array.length <= 134217728) {
            return new int[][]{array};
        }
        int[][] bigArray = IntBigArrays.newBigArray(array.length);
        for (int i = 0; i < bigArray.length; ++i) {
            System.arraycopy(array, (int)BigArrays.start(i), bigArray[i], 0, bigArray[i].length);
        }
        return bigArray;
    }

    public static int[][] ensureCapacity(int[][] array, long length) {
        return IntBigArrays.ensureCapacity(array, length, IntBigArrays.length(array));
    }

    public static int[][] ensureCapacity(int[][] array, long length, long preserve) {
        long oldLength = IntBigArrays.length(array);
        if (length > oldLength) {
            BigArrays.ensureLength(length);
            int valid = array.length - (array.length == 0 || array.length > 0 && array[array.length - 1].length == 134217728 ? 0 : 1);
            int baseLength = (int)(length + 0x7FFFFFFL >>> 27);
            int[][] base = (int[][])Arrays.copyOf(array, baseLength);
            int residual = (int)(length & 0x7FFFFFFL);
            if (residual != 0) {
                for (int i = valid; i < baseLength - 1; ++i) {
                    base[i] = new int[134217728];
                }
                base[baseLength - 1] = new int[residual];
            } else {
                for (int i = valid; i < baseLength; ++i) {
                    base[i] = new int[134217728];
                }
            }
            if (preserve - (long)valid * 0x8000000L > 0L) {
                IntBigArrays.copy(array, (long)valid * 0x8000000L, base, (long)valid * 0x8000000L, preserve - (long)valid * 0x8000000L);
            }
            return base;
        }
        return array;
    }

    public static int[][] grow(int[][] array, long length) {
        long oldLength = IntBigArrays.length(array);
        return length > oldLength ? IntBigArrays.grow(array, length, oldLength) : array;
    }

    public static int[][] grow(int[][] array, long length, long preserve) {
        long oldLength = IntBigArrays.length(array);
        return length > oldLength ? IntBigArrays.ensureCapacity(array, Math.max(2L * oldLength, length), preserve) : array;
    }

    public static int[][] trim(int[][] array, long length) {
        BigArrays.ensureLength(length);
        long oldLength = IntBigArrays.length(array);
        if (length >= oldLength) {
            return array;
        }
        int baseLength = (int)(length + 0x7FFFFFFL >>> 27);
        int[][] base = (int[][])Arrays.copyOf(array, baseLength);
        int residual = (int)(length & 0x7FFFFFFL);
        if (residual != 0) {
            base[baseLength - 1] = IntArrays.trim(base[baseLength - 1], residual);
        }
        return base;
    }

    public static int[][] setLength(int[][] array, long length) {
        long oldLength = IntBigArrays.length(array);
        if (length == oldLength) {
            return array;
        }
        if (length < oldLength) {
            return IntBigArrays.trim(array, length);
        }
        return IntBigArrays.ensureCapacity(array, length);
    }

    public static int[][] copy(int[][] array, long offset, long length) {
        IntBigArrays.ensureOffsetLength(array, offset, length);
        int[][] a = IntBigArrays.newBigArray(length);
        IntBigArrays.copy(array, offset, a, 0L, length);
        return a;
    }

    public static int[][] copy(int[][] array) {
        int[][] base = (int[][])array.clone();
        int i = base.length;
        while (i-- != 0) {
            base[i] = (int[])array[i].clone();
        }
        return base;
    }

    public static void fill(int[][] array, int value) {
        int i = array.length;
        while (i-- != 0) {
            Arrays.fill(array[i], value);
        }
    }

    public static void fill(int[][] array, long from, long to, int value) {
        long length = IntBigArrays.length(array);
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

    public static boolean equals(int[][] a1, int[][] a2) {
        if (IntBigArrays.length(a1) != IntBigArrays.length(a2)) {
            return false;
        }
        int i = a1.length;
        while (i-- != 0) {
            int[] t = a1[i];
            int[] u = a2[i];
            int j = t.length;
            while (j-- != 0) {
                if (t[j] == u[j]) continue;
                return false;
            }
        }
        return true;
    }

    public static String toString(int[][] a) {
        if (a == null) {
            return "null";
        }
        long last = IntBigArrays.length(a) - 1L;
        if (last == -1L) {
            return "[]";
        }
        StringBuilder b = new StringBuilder();
        b.append('[');
        long i = 0L;
        do {
            b.append(String.valueOf(IntBigArrays.get(a, i)));
            if (i == last) {
                return b.append(']').toString();
            }
            b.append(", ");
            ++i;
        } while (true);
    }

    public static void ensureFromTo(int[][] a, long from, long to) {
        BigArrays.ensureFromTo(IntBigArrays.length(a), from, to);
    }

    public static void ensureOffsetLength(int[][] a, long offset, long length) {
        BigArrays.ensureOffsetLength(IntBigArrays.length(a), offset, length);
    }

    private static void vecSwap(int[][] x, long a, long b, long n) {
        int i = 0;
        while ((long)i < n) {
            IntBigArrays.swap(x, a, b);
            ++i;
            ++a;
            ++b;
        }
    }

    private static long med3(int[][] x, long a, long b, long c, IntComparator comp) {
        int ab = comp.compare(IntBigArrays.get(x, a), IntBigArrays.get(x, b));
        int ac = comp.compare(IntBigArrays.get(x, a), IntBigArrays.get(x, c));
        int bc = comp.compare(IntBigArrays.get(x, b), IntBigArrays.get(x, c));
        return ab < 0 ? (bc < 0 ? b : (ac < 0 ? c : a)) : (bc > 0 ? b : (ac > 0 ? c : a));
    }

    private static void selectionSort(int[][] a, long from, long to, IntComparator comp) {
        for (long i = from; i < to - 1L; ++i) {
            long m = i;
            for (long j = i + 1L; j < to; ++j) {
                if (comp.compare(IntBigArrays.get(a, j), IntBigArrays.get(a, m)) >= 0) continue;
                m = j;
            }
            if (m == i) continue;
            IntBigArrays.swap(a, i, m);
        }
    }

    public static void quickSort(int[][] x, long from, long to, IntComparator comp) {
        long c;
        long a;
        long len = to - from;
        if (len < 7L) {
            IntBigArrays.selectionSort(x, from, to, comp);
            return;
        }
        long m = from + len / 2L;
        if (len > 7L) {
            long l = from;
            long n = to - 1L;
            if (len > 40L) {
                long s = len / 8L;
                l = IntBigArrays.med3(x, l, l + s, l + 2L * s, comp);
                m = IntBigArrays.med3(x, m - s, m, m + s, comp);
                n = IntBigArrays.med3(x, n - 2L * s, n - s, n, comp);
            }
            m = IntBigArrays.med3(x, l, m, n, comp);
        }
        int v = IntBigArrays.get(x, m);
        long b = a = from;
        long d = c = to - 1L;
        do {
            int comparison;
            if (b <= c && (comparison = comp.compare(IntBigArrays.get(x, b), v)) <= 0) {
                if (comparison == 0) {
                    IntBigArrays.swap(x, a++, b);
                }
                ++b;
                continue;
            }
            while (c >= b && (comparison = comp.compare(IntBigArrays.get(x, c), v)) >= 0) {
                if (comparison == 0) {
                    IntBigArrays.swap(x, c, d--);
                }
                --c;
            }
            if (b > c) break;
            IntBigArrays.swap(x, b++, c--);
        } while (true);
        long n = to;
        long s = Math.min(a - from, b - a);
        IntBigArrays.vecSwap(x, from, b - s, s);
        s = Math.min(d - c, n - d - 1L);
        IntBigArrays.vecSwap(x, b, n - s, s);
        s = b - a;
        if (s > 1L) {
            IntBigArrays.quickSort(x, from, from + s, comp);
        }
        if ((s = d - c) > 1L) {
            IntBigArrays.quickSort(x, n - s, n, comp);
        }
    }

    private static long med3(int[][] x, long a, long b, long c) {
        int ab = Integer.compare(IntBigArrays.get(x, a), IntBigArrays.get(x, b));
        int ac = Integer.compare(IntBigArrays.get(x, a), IntBigArrays.get(x, c));
        int bc = Integer.compare(IntBigArrays.get(x, b), IntBigArrays.get(x, c));
        return ab < 0 ? (bc < 0 ? b : (ac < 0 ? c : a)) : (bc > 0 ? b : (ac > 0 ? c : a));
    }

    private static void selectionSort(int[][] a, long from, long to) {
        for (long i = from; i < to - 1L; ++i) {
            long m = i;
            for (long j = i + 1L; j < to; ++j) {
                if (IntBigArrays.get(a, j) >= IntBigArrays.get(a, m)) continue;
                m = j;
            }
            if (m == i) continue;
            IntBigArrays.swap(a, i, m);
        }
    }

    public static void quickSort(int[][] x, IntComparator comp) {
        IntBigArrays.quickSort(x, 0L, IntBigArrays.length(x), comp);
    }

    public static void quickSort(int[][] x, long from, long to) {
        long a;
        long c;
        long len = to - from;
        if (len < 7L) {
            IntBigArrays.selectionSort(x, from, to);
            return;
        }
        long m = from + len / 2L;
        if (len > 7L) {
            long l = from;
            long n = to - 1L;
            if (len > 40L) {
                long s = len / 8L;
                l = IntBigArrays.med3(x, l, l + s, l + 2L * s);
                m = IntBigArrays.med3(x, m - s, m, m + s);
                n = IntBigArrays.med3(x, n - 2L * s, n - s, n);
            }
            m = IntBigArrays.med3(x, l, m, n);
        }
        int v = IntBigArrays.get(x, m);
        long b = a = from;
        long d = c = to - 1L;
        do {
            int comparison;
            if (b <= c && (comparison = Integer.compare(IntBigArrays.get(x, b), v)) <= 0) {
                if (comparison == 0) {
                    IntBigArrays.swap(x, a++, b);
                }
                ++b;
                continue;
            }
            while (c >= b && (comparison = Integer.compare(IntBigArrays.get(x, c), v)) >= 0) {
                if (comparison == 0) {
                    IntBigArrays.swap(x, c, d--);
                }
                --c;
            }
            if (b > c) break;
            IntBigArrays.swap(x, b++, c--);
        } while (true);
        long n = to;
        long s = Math.min(a - from, b - a);
        IntBigArrays.vecSwap(x, from, b - s, s);
        s = Math.min(d - c, n - d - 1L);
        IntBigArrays.vecSwap(x, b, n - s, s);
        s = b - a;
        if (s > 1L) {
            IntBigArrays.quickSort(x, from, from + s);
        }
        if ((s = d - c) > 1L) {
            IntBigArrays.quickSort(x, n - s, n);
        }
    }

    public static void quickSort(int[][] x) {
        IntBigArrays.quickSort(x, 0L, IntBigArrays.length(x));
    }

    public static long binarySearch(int[][] a, long from, long to, int key) {
        --to;
        while (from <= to) {
            long mid = from + to >>> 1;
            int midVal = IntBigArrays.get(a, mid);
            if (midVal < key) {
                from = mid + 1L;
                continue;
            }
            if (midVal > key) {
                to = mid - 1L;
                continue;
            }
            return mid;
        }
        return - from + 1L;
    }

    public static long binarySearch(int[][] a, int key) {
        return IntBigArrays.binarySearch(a, 0L, IntBigArrays.length(a), key);
    }

    public static long binarySearch(int[][] a, long from, long to, int key, IntComparator c) {
        --to;
        while (from <= to) {
            long mid = from + to >>> 1;
            int midVal = IntBigArrays.get(a, mid);
            int cmp = c.compare(midVal, key);
            if (cmp < 0) {
                from = mid + 1L;
                continue;
            }
            if (cmp > 0) {
                to = mid - 1L;
                continue;
            }
            return mid;
        }
        return - from + 1L;
    }

    public static long binarySearch(int[][] a, int key, IntComparator c) {
        return IntBigArrays.binarySearch(a, 0L, IntBigArrays.length(a), key, c);
    }

    public static void radixSort(int[][] a) {
        IntBigArrays.radixSort(a, 0L, IntBigArrays.length(a));
    }

    public static void radixSort(int[][] a, long from, long to) {
        int maxLevel = 3;
        int stackSize = 766;
        long[] offsetStack = new long[766];
        int offsetPos = 0;
        long[] lengthStack = new long[766];
        int lengthPos = 0;
        int[] levelStack = new int[766];
        int levelPos = 0;
        offsetStack[offsetPos++] = from;
        lengthStack[lengthPos++] = to - from;
        levelStack[levelPos++] = 0;
        long[] count = new long[256];
        long[] pos = new long[256];
        byte[][] digit = ByteBigArrays.newBigArray(to - from);
        while (offsetPos > 0) {
            int signMask;
            int level;
            long first = offsetStack[--offsetPos];
            long length = lengthStack[--lengthPos];
            int n = signMask = (level = levelStack[--levelPos]) % 4 == 0 ? 128 : 0;
            if (length < 40L) {
                IntBigArrays.selectionSort(a, first, first + length);
                continue;
            }
            int shift = (3 - level % 4) * 8;
            long i = length;
            while (i-- != 0L) {
                ByteBigArrays.set(digit, i, (byte)(IntBigArrays.get(a, first + i) >>> shift & 255 ^ signMask));
            }
            i = length;
            while (i-- != 0L) {
                long[] arrl = count;
                int n2 = ByteBigArrays.get(digit, i) & 255;
                arrl[n2] = arrl[n2] + 1L;
            }
            int lastUsed = -1;
            long p = 0L;
            for (int i2 = 0; i2 < 256; ++i2) {
                if (count[i2] != 0L) {
                    lastUsed = i2;
                    if (level < 3 && count[i2] > 1L) {
                        offsetStack[offsetPos++] = p + first;
                        lengthStack[lengthPos++] = count[i2];
                        levelStack[levelPos++] = level + 1;
                    }
                }
                pos[i2] = p += count[i2];
            }
            long end = length - count[lastUsed];
            count[lastUsed] = 0L;
            int c = -1;
            for (long i3 = 0L; i3 < end; i3 += count[c]) {
                int t = IntBigArrays.get(a, i3 + first);
                c = ByteBigArrays.get(digit, i3) & 255;
                do {
                    long[] arrl = pos;
                    int n3 = c;
                    long l = arrl[n3] - 1L;
                    arrl[n3] = l;
                    long d = l;
                    if (l <= i3) break;
                    int z = t;
                    int zz = c;
                    t = IntBigArrays.get(a, d + first);
                    c = ByteBigArrays.get(digit, d) & 255;
                    IntBigArrays.set(a, d + first, z);
                    ByteBigArrays.set(digit, d, (byte)zz);
                } while (true);
                IntBigArrays.set(a, i3 + first, t);
                count[c] = 0L;
            }
        }
    }

    private static void selectionSort(int[][] a, int[][] b, long from, long to) {
        for (long i = from; i < to - 1L; ++i) {
            long m = i;
            for (long j = i + 1L; j < to; ++j) {
                if (IntBigArrays.get(a, j) >= IntBigArrays.get(a, m) && (IntBigArrays.get(a, j) != IntBigArrays.get(a, m) || IntBigArrays.get(b, j) >= IntBigArrays.get(b, m))) continue;
                m = j;
            }
            if (m == i) continue;
            int t = IntBigArrays.get(a, i);
            IntBigArrays.set(a, i, IntBigArrays.get(a, m));
            IntBigArrays.set(a, m, t);
            t = IntBigArrays.get(b, i);
            IntBigArrays.set(b, i, IntBigArrays.get(b, m));
            IntBigArrays.set(b, m, t);
        }
    }

    public static void radixSort(int[][] a, int[][] b) {
        IntBigArrays.radixSort(a, b, 0L, IntBigArrays.length(a));
    }

    public static void radixSort(int[][] a, int[][] b, long from, long to) {
        int layers = 2;
        if (IntBigArrays.length(a) != IntBigArrays.length(b)) {
            throw new IllegalArgumentException("Array size mismatch.");
        }
        int maxLevel = 7;
        int stackSize = 1786;
        long[] offsetStack = new long[1786];
        int offsetPos = 0;
        long[] lengthStack = new long[1786];
        int lengthPos = 0;
        int[] levelStack = new int[1786];
        int levelPos = 0;
        offsetStack[offsetPos++] = from;
        lengthStack[lengthPos++] = to - from;
        levelStack[levelPos++] = 0;
        long[] count = new long[256];
        long[] pos = new long[256];
        byte[][] digit = ByteBigArrays.newBigArray(to - from);
        while (offsetPos > 0) {
            int level;
            int signMask;
            long first = offsetStack[--offsetPos];
            long length = lengthStack[--lengthPos];
            int n = signMask = (level = levelStack[--levelPos]) % 4 == 0 ? 128 : 0;
            if (length < 40L) {
                IntBigArrays.selectionSort(a, b, first, first + length);
                continue;
            }
            int[][] k = level < 4 ? a : b;
            int shift = (3 - level % 4) * 8;
            long i = length;
            while (i-- != 0L) {
                ByteBigArrays.set(digit, i, (byte)(IntBigArrays.get(k, first + i) >>> shift & 255 ^ signMask));
            }
            i = length;
            while (i-- != 0L) {
                long[] arrl = count;
                int n2 = ByteBigArrays.get(digit, i) & 255;
                arrl[n2] = arrl[n2] + 1L;
            }
            int lastUsed = -1;
            long p = 0L;
            for (int i2 = 0; i2 < 256; ++i2) {
                if (count[i2] != 0L) {
                    lastUsed = i2;
                    if (level < 7 && count[i2] > 1L) {
                        offsetStack[offsetPos++] = p + first;
                        lengthStack[lengthPos++] = count[i2];
                        levelStack[levelPos++] = level + 1;
                    }
                }
                pos[i2] = p += count[i2];
            }
            long end = length - count[lastUsed];
            count[lastUsed] = 0L;
            int c = -1;
            for (long i3 = 0L; i3 < end; i3 += count[c]) {
                int t = IntBigArrays.get(a, i3 + first);
                int u = IntBigArrays.get(b, i3 + first);
                c = ByteBigArrays.get(digit, i3) & 255;
                do {
                    long[] arrl = pos;
                    int n3 = c;
                    long l = arrl[n3] - 1L;
                    arrl[n3] = l;
                    long d = l;
                    if (l <= i3) break;
                    int z = t;
                    int zz = c;
                    t = IntBigArrays.get(a, d + first);
                    IntBigArrays.set(a, d + first, z);
                    z = u;
                    u = IntBigArrays.get(b, d + first);
                    IntBigArrays.set(b, d + first, z);
                    c = ByteBigArrays.get(digit, d) & 255;
                    ByteBigArrays.set(digit, d, (byte)zz);
                } while (true);
                IntBigArrays.set(a, i3 + first, t);
                IntBigArrays.set(b, i3 + first, u);
                count[c] = 0L;
            }
        }
    }

    public static int[][] shuffle(int[][] a, long from, long to, Random random) {
        long i = to - from;
        while (i-- != 0L) {
            long p = (random.nextLong() & Long.MAX_VALUE) % (i + 1L);
            int t = IntBigArrays.get(a, from + i);
            IntBigArrays.set(a, from + i, IntBigArrays.get(a, from + p));
            IntBigArrays.set(a, from + p, t);
        }
        return a;
    }

    public static int[][] shuffle(int[][] a, Random random) {
        long i = IntBigArrays.length(a);
        while (i-- != 0L) {
            long p = (random.nextLong() & Long.MAX_VALUE) % (i + 1L);
            int t = IntBigArrays.get(a, i);
            IntBigArrays.set(a, i, IntBigArrays.get(a, p));
            IntBigArrays.set(a, p, t);
        }
        return a;
    }

    private static final class BigArrayHashStrategy
    implements Hash.Strategy<int[][]>,
    Serializable {
        private static final long serialVersionUID = -7046029254386353129L;

        private BigArrayHashStrategy() {
        }

        @Override
        public int hashCode(int[][] o) {
            return Arrays.deepHashCode((Object[])o);
        }

        @Override
        public boolean equals(int[][] a, int[][] b) {
            return IntBigArrays.equals(a, b);
        }
    }

}

