/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.BigArrays;
import it.unimi.dsi.fastutil.Hash;
import it.unimi.dsi.fastutil.bytes.ByteBigArrays;
import it.unimi.dsi.fastutil.longs.LongArrays;
import it.unimi.dsi.fastutil.longs.LongComparator;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Random;

public final class LongBigArrays {
    public static final long[][] EMPTY_BIG_ARRAY = new long[0][];
    public static final Hash.Strategy HASH_STRATEGY = new BigArrayHashStrategy();
    private static final int SMALL = 7;
    private static final int MEDIUM = 40;
    private static final int DIGIT_BITS = 8;
    private static final int DIGIT_MASK = 255;
    private static final int DIGITS_PER_ELEMENT = 8;

    private LongBigArrays() {
    }

    public static long get(long[][] array, long index) {
        return array[BigArrays.segment(index)][BigArrays.displacement(index)];
    }

    public static void set(long[][] array, long index, long value) {
        array[BigArrays.segment((long)index)][BigArrays.displacement((long)index)] = value;
    }

    public static void swap(long[][] array, long first, long second) {
        long t = array[BigArrays.segment(first)][BigArrays.displacement(first)];
        array[BigArrays.segment((long)first)][BigArrays.displacement((long)first)] = array[BigArrays.segment(second)][BigArrays.displacement(second)];
        array[BigArrays.segment((long)second)][BigArrays.displacement((long)second)] = t;
    }

    public static void add(long[][] array, long index, long incr) {
        long[] arrl = array[BigArrays.segment(index)];
        int n = BigArrays.displacement(index);
        arrl[n] = arrl[n] + incr;
    }

    public static void mul(long[][] array, long index, long factor) {
        long[] arrl = array[BigArrays.segment(index)];
        int n = BigArrays.displacement(index);
        arrl[n] = arrl[n] * factor;
    }

    public static void incr(long[][] array, long index) {
        long[] arrl = array[BigArrays.segment(index)];
        int n = BigArrays.displacement(index);
        arrl[n] = arrl[n] + 1L;
    }

    public static void decr(long[][] array, long index) {
        long[] arrl = array[BigArrays.segment(index)];
        int n = BigArrays.displacement(index);
        arrl[n] = arrl[n] - 1L;
    }

    public static long length(long[][] array) {
        int length = array.length;
        return length == 0 ? 0L : BigArrays.start(length - 1) + (long)array[length - 1].length;
    }

    public static void copy(long[][] srcArray, long srcPos, long[][] destArray, long destPos, long length) {
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

    public static void copyFromBig(long[][] srcArray, long srcPos, long[] destArray, int destPos, int length) {
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

    public static void copyToBig(long[] srcArray, int srcPos, long[][] destArray, long destPos, long length) {
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

    public static long[][] newBigArray(long length) {
        if (length == 0L) {
            return EMPTY_BIG_ARRAY;
        }
        BigArrays.ensureLength(length);
        int baseLength = (int)(length + 0x7FFFFFFL >>> 27);
        long[][] base = new long[baseLength][];
        int residual = (int)(length & 0x7FFFFFFL);
        if (residual != 0) {
            for (int i = 0; i < baseLength - 1; ++i) {
                base[i] = new long[134217728];
            }
            base[baseLength - 1] = new long[residual];
        } else {
            for (int i = 0; i < baseLength; ++i) {
                base[i] = new long[134217728];
            }
        }
        return base;
    }

    public static long[][] wrap(long[] array) {
        if (array.length == 0) {
            return EMPTY_BIG_ARRAY;
        }
        if (array.length <= 134217728) {
            return new long[][]{array};
        }
        long[][] bigArray = LongBigArrays.newBigArray(array.length);
        for (int i = 0; i < bigArray.length; ++i) {
            System.arraycopy(array, (int)BigArrays.start(i), bigArray[i], 0, bigArray[i].length);
        }
        return bigArray;
    }

    public static long[][] ensureCapacity(long[][] array, long length) {
        return LongBigArrays.ensureCapacity(array, length, LongBigArrays.length(array));
    }

    public static long[][] ensureCapacity(long[][] array, long length, long preserve) {
        long oldLength = LongBigArrays.length(array);
        if (length > oldLength) {
            BigArrays.ensureLength(length);
            int valid = array.length - (array.length == 0 || array.length > 0 && array[array.length - 1].length == 134217728 ? 0 : 1);
            int baseLength = (int)(length + 0x7FFFFFFL >>> 27);
            long[][] base = (long[][])Arrays.copyOf(array, baseLength);
            int residual = (int)(length & 0x7FFFFFFL);
            if (residual != 0) {
                for (int i = valid; i < baseLength - 1; ++i) {
                    base[i] = new long[134217728];
                }
                base[baseLength - 1] = new long[residual];
            } else {
                for (int i = valid; i < baseLength; ++i) {
                    base[i] = new long[134217728];
                }
            }
            if (preserve - (long)valid * 0x8000000L > 0L) {
                LongBigArrays.copy(array, (long)valid * 0x8000000L, base, (long)valid * 0x8000000L, preserve - (long)valid * 0x8000000L);
            }
            return base;
        }
        return array;
    }

    public static long[][] grow(long[][] array, long length) {
        long oldLength = LongBigArrays.length(array);
        return length > oldLength ? LongBigArrays.grow(array, length, oldLength) : array;
    }

    public static long[][] grow(long[][] array, long length, long preserve) {
        long oldLength = LongBigArrays.length(array);
        return length > oldLength ? LongBigArrays.ensureCapacity(array, Math.max(2L * oldLength, length), preserve) : array;
    }

    public static long[][] trim(long[][] array, long length) {
        BigArrays.ensureLength(length);
        long oldLength = LongBigArrays.length(array);
        if (length >= oldLength) {
            return array;
        }
        int baseLength = (int)(length + 0x7FFFFFFL >>> 27);
        long[][] base = (long[][])Arrays.copyOf(array, baseLength);
        int residual = (int)(length & 0x7FFFFFFL);
        if (residual != 0) {
            base[baseLength - 1] = LongArrays.trim(base[baseLength - 1], residual);
        }
        return base;
    }

    public static long[][] setLength(long[][] array, long length) {
        long oldLength = LongBigArrays.length(array);
        if (length == oldLength) {
            return array;
        }
        if (length < oldLength) {
            return LongBigArrays.trim(array, length);
        }
        return LongBigArrays.ensureCapacity(array, length);
    }

    public static long[][] copy(long[][] array, long offset, long length) {
        LongBigArrays.ensureOffsetLength(array, offset, length);
        long[][] a = LongBigArrays.newBigArray(length);
        LongBigArrays.copy(array, offset, a, 0L, length);
        return a;
    }

    public static long[][] copy(long[][] array) {
        long[][] base = (long[][])array.clone();
        int i = base.length;
        while (i-- != 0) {
            base[i] = (long[])array[i].clone();
        }
        return base;
    }

    public static void fill(long[][] array, long value) {
        int i = array.length;
        while (i-- != 0) {
            Arrays.fill(array[i], value);
        }
    }

    public static void fill(long[][] array, long from, long to, long value) {
        long length = LongBigArrays.length(array);
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

    public static boolean equals(long[][] a1, long[][] a2) {
        if (LongBigArrays.length(a1) != LongBigArrays.length(a2)) {
            return false;
        }
        int i = a1.length;
        while (i-- != 0) {
            long[] t = a1[i];
            long[] u = a2[i];
            int j = t.length;
            while (j-- != 0) {
                if (t[j] == u[j]) continue;
                return false;
            }
        }
        return true;
    }

    public static String toString(long[][] a) {
        if (a == null) {
            return "null";
        }
        long last = LongBigArrays.length(a) - 1L;
        if (last == -1L) {
            return "[]";
        }
        StringBuilder b = new StringBuilder();
        b.append('[');
        long i = 0L;
        do {
            b.append(String.valueOf(LongBigArrays.get(a, i)));
            if (i == last) {
                return b.append(']').toString();
            }
            b.append(", ");
            ++i;
        } while (true);
    }

    public static void ensureFromTo(long[][] a, long from, long to) {
        BigArrays.ensureFromTo(LongBigArrays.length(a), from, to);
    }

    public static void ensureOffsetLength(long[][] a, long offset, long length) {
        BigArrays.ensureOffsetLength(LongBigArrays.length(a), offset, length);
    }

    private static void vecSwap(long[][] x, long a, long b, long n) {
        int i = 0;
        while ((long)i < n) {
            LongBigArrays.swap(x, a, b);
            ++i;
            ++a;
            ++b;
        }
    }

    private static long med3(long[][] x, long a, long b, long c, LongComparator comp) {
        int ab = comp.compare(LongBigArrays.get(x, a), LongBigArrays.get(x, b));
        int ac = comp.compare(LongBigArrays.get(x, a), LongBigArrays.get(x, c));
        int bc = comp.compare(LongBigArrays.get(x, b), LongBigArrays.get(x, c));
        return ab < 0 ? (bc < 0 ? b : (ac < 0 ? c : a)) : (bc > 0 ? b : (ac > 0 ? c : a));
    }

    private static void selectionSort(long[][] a, long from, long to, LongComparator comp) {
        for (long i = from; i < to - 1L; ++i) {
            long m = i;
            for (long j = i + 1L; j < to; ++j) {
                if (comp.compare(LongBigArrays.get(a, j), LongBigArrays.get(a, m)) >= 0) continue;
                m = j;
            }
            if (m == i) continue;
            LongBigArrays.swap(a, i, m);
        }
    }

    public static void quickSort(long[][] x, long from, long to, LongComparator comp) {
        long a;
        long c;
        long len = to - from;
        if (len < 7L) {
            LongBigArrays.selectionSort(x, from, to, comp);
            return;
        }
        long m = from + len / 2L;
        if (len > 7L) {
            long l = from;
            long n = to - 1L;
            if (len > 40L) {
                long s = len / 8L;
                l = LongBigArrays.med3(x, l, l + s, l + 2L * s, comp);
                m = LongBigArrays.med3(x, m - s, m, m + s, comp);
                n = LongBigArrays.med3(x, n - 2L * s, n - s, n, comp);
            }
            m = LongBigArrays.med3(x, l, m, n, comp);
        }
        long v = LongBigArrays.get(x, m);
        long b = a = from;
        long d = c = to - 1L;
        do {
            int comparison;
            if (b <= c && (comparison = comp.compare(LongBigArrays.get(x, b), v)) <= 0) {
                if (comparison == 0) {
                    LongBigArrays.swap(x, a++, b);
                }
                ++b;
                continue;
            }
            while (c >= b && (comparison = comp.compare(LongBigArrays.get(x, c), v)) >= 0) {
                if (comparison == 0) {
                    LongBigArrays.swap(x, c, d--);
                }
                --c;
            }
            if (b > c) break;
            LongBigArrays.swap(x, b++, c--);
        } while (true);
        long n = to;
        long s = Math.min(a - from, b - a);
        LongBigArrays.vecSwap(x, from, b - s, s);
        s = Math.min(d - c, n - d - 1L);
        LongBigArrays.vecSwap(x, b, n - s, s);
        s = b - a;
        if (s > 1L) {
            LongBigArrays.quickSort(x, from, from + s, comp);
        }
        if ((s = d - c) > 1L) {
            LongBigArrays.quickSort(x, n - s, n, comp);
        }
    }

    private static long med3(long[][] x, long a, long b, long c) {
        int ab = Long.compare(LongBigArrays.get(x, a), LongBigArrays.get(x, b));
        int ac = Long.compare(LongBigArrays.get(x, a), LongBigArrays.get(x, c));
        int bc = Long.compare(LongBigArrays.get(x, b), LongBigArrays.get(x, c));
        return ab < 0 ? (bc < 0 ? b : (ac < 0 ? c : a)) : (bc > 0 ? b : (ac > 0 ? c : a));
    }

    private static void selectionSort(long[][] a, long from, long to) {
        for (long i = from; i < to - 1L; ++i) {
            long m = i;
            for (long j = i + 1L; j < to; ++j) {
                if (LongBigArrays.get(a, j) >= LongBigArrays.get(a, m)) continue;
                m = j;
            }
            if (m == i) continue;
            LongBigArrays.swap(a, i, m);
        }
    }

    public static void quickSort(long[][] x, LongComparator comp) {
        LongBigArrays.quickSort(x, 0L, LongBigArrays.length(x), comp);
    }

    public static void quickSort(long[][] x, long from, long to) {
        long c;
        long a;
        long len = to - from;
        if (len < 7L) {
            LongBigArrays.selectionSort(x, from, to);
            return;
        }
        long m = from + len / 2L;
        if (len > 7L) {
            long l = from;
            long n = to - 1L;
            if (len > 40L) {
                long s = len / 8L;
                l = LongBigArrays.med3(x, l, l + s, l + 2L * s);
                m = LongBigArrays.med3(x, m - s, m, m + s);
                n = LongBigArrays.med3(x, n - 2L * s, n - s, n);
            }
            m = LongBigArrays.med3(x, l, m, n);
        }
        long v = LongBigArrays.get(x, m);
        long b = a = from;
        long d = c = to - 1L;
        do {
            int comparison;
            if (b <= c && (comparison = Long.compare(LongBigArrays.get(x, b), v)) <= 0) {
                if (comparison == 0) {
                    LongBigArrays.swap(x, a++, b);
                }
                ++b;
                continue;
            }
            while (c >= b && (comparison = Long.compare(LongBigArrays.get(x, c), v)) >= 0) {
                if (comparison == 0) {
                    LongBigArrays.swap(x, c, d--);
                }
                --c;
            }
            if (b > c) break;
            LongBigArrays.swap(x, b++, c--);
        } while (true);
        long n = to;
        long s = Math.min(a - from, b - a);
        LongBigArrays.vecSwap(x, from, b - s, s);
        s = Math.min(d - c, n - d - 1L);
        LongBigArrays.vecSwap(x, b, n - s, s);
        s = b - a;
        if (s > 1L) {
            LongBigArrays.quickSort(x, from, from + s);
        }
        if ((s = d - c) > 1L) {
            LongBigArrays.quickSort(x, n - s, n);
        }
    }

    public static void quickSort(long[][] x) {
        LongBigArrays.quickSort(x, 0L, LongBigArrays.length(x));
    }

    public static long binarySearch(long[][] a, long from, long to, long key) {
        --to;
        while (from <= to) {
            long mid = from + to >>> 1;
            long midVal = LongBigArrays.get(a, mid);
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

    public static long binarySearch(long[][] a, long key) {
        return LongBigArrays.binarySearch(a, 0L, LongBigArrays.length(a), key);
    }

    public static long binarySearch(long[][] a, long from, long to, long key, LongComparator c) {
        --to;
        while (from <= to) {
            long mid = from + to >>> 1;
            long midVal = LongBigArrays.get(a, mid);
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

    public static long binarySearch(long[][] a, long key, LongComparator c) {
        return LongBigArrays.binarySearch(a, 0L, LongBigArrays.length(a), key, c);
    }

    public static void radixSort(long[][] a) {
        LongBigArrays.radixSort(a, 0L, LongBigArrays.length(a));
    }

    public static void radixSort(long[][] a, long from, long to) {
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
            int signMask;
            int level;
            long first = offsetStack[--offsetPos];
            long length = lengthStack[--lengthPos];
            int n = signMask = (level = levelStack[--levelPos]) % 8 == 0 ? 128 : 0;
            if (length < 40L) {
                LongBigArrays.selectionSort(a, first, first + length);
                continue;
            }
            int shift = (7 - level % 8) * 8;
            long i = length;
            while (i-- != 0L) {
                ByteBigArrays.set(digit, i, (byte)(LongBigArrays.get(a, first + i) >>> shift & 255L ^ (long)signMask));
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
                long t = LongBigArrays.get(a, i3 + first);
                c = ByteBigArrays.get(digit, i3) & 255;
                do {
                    long[] arrl = pos;
                    int n3 = c;
                    long l = arrl[n3] - 1L;
                    arrl[n3] = l;
                    long d = l;
                    if (l <= i3) break;
                    long z = t;
                    int zz = c;
                    t = LongBigArrays.get(a, d + first);
                    c = ByteBigArrays.get(digit, d) & 255;
                    LongBigArrays.set(a, d + first, z);
                    ByteBigArrays.set(digit, d, (byte)zz);
                } while (true);
                LongBigArrays.set(a, i3 + first, t);
                count[c] = 0L;
            }
        }
    }

    private static void selectionSort(long[][] a, long[][] b, long from, long to) {
        for (long i = from; i < to - 1L; ++i) {
            long m = i;
            for (long j = i + 1L; j < to; ++j) {
                if (LongBigArrays.get(a, j) >= LongBigArrays.get(a, m) && (LongBigArrays.get(a, j) != LongBigArrays.get(a, m) || LongBigArrays.get(b, j) >= LongBigArrays.get(b, m))) continue;
                m = j;
            }
            if (m == i) continue;
            long t = LongBigArrays.get(a, i);
            LongBigArrays.set(a, i, LongBigArrays.get(a, m));
            LongBigArrays.set(a, m, t);
            t = LongBigArrays.get(b, i);
            LongBigArrays.set(b, i, LongBigArrays.get(b, m));
            LongBigArrays.set(b, m, t);
        }
    }

    public static void radixSort(long[][] a, long[][] b) {
        LongBigArrays.radixSort(a, b, 0L, LongBigArrays.length(a));
    }

    public static void radixSort(long[][] a, long[][] b, long from, long to) {
        int layers = 2;
        if (LongBigArrays.length(a) != LongBigArrays.length(b)) {
            throw new IllegalArgumentException("Array size mismatch.");
        }
        int maxLevel = 15;
        int stackSize = 3826;
        long[] offsetStack = new long[3826];
        int offsetPos = 0;
        long[] lengthStack = new long[3826];
        int lengthPos = 0;
        int[] levelStack = new int[3826];
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
            int n = signMask = (level = levelStack[--levelPos]) % 8 == 0 ? 128 : 0;
            if (length < 40L) {
                LongBigArrays.selectionSort(a, b, first, first + length);
                continue;
            }
            long[][] k = level < 8 ? a : b;
            int shift = (7 - level % 8) * 8;
            long i = length;
            while (i-- != 0L) {
                ByteBigArrays.set(digit, i, (byte)(LongBigArrays.get(k, first + i) >>> shift & 255L ^ (long)signMask));
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
                    if (level < 15 && count[i2] > 1L) {
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
                long t = LongBigArrays.get(a, i3 + first);
                long u = LongBigArrays.get(b, i3 + first);
                c = ByteBigArrays.get(digit, i3) & 255;
                do {
                    long[] arrl = pos;
                    int n3 = c;
                    long l = arrl[n3] - 1L;
                    arrl[n3] = l;
                    long d = l;
                    if (l <= i3) break;
                    long z = t;
                    int zz = c;
                    t = LongBigArrays.get(a, d + first);
                    LongBigArrays.set(a, d + first, z);
                    z = u;
                    u = LongBigArrays.get(b, d + first);
                    LongBigArrays.set(b, d + first, z);
                    c = ByteBigArrays.get(digit, d) & 255;
                    ByteBigArrays.set(digit, d, (byte)zz);
                } while (true);
                LongBigArrays.set(a, i3 + first, t);
                LongBigArrays.set(b, i3 + first, u);
                count[c] = 0L;
            }
        }
    }

    public static long[][] shuffle(long[][] a, long from, long to, Random random) {
        long i = to - from;
        while (i-- != 0L) {
            long p = (random.nextLong() & Long.MAX_VALUE) % (i + 1L);
            long t = LongBigArrays.get(a, from + i);
            LongBigArrays.set(a, from + i, LongBigArrays.get(a, from + p));
            LongBigArrays.set(a, from + p, t);
        }
        return a;
    }

    public static long[][] shuffle(long[][] a, Random random) {
        long i = LongBigArrays.length(a);
        while (i-- != 0L) {
            long p = (random.nextLong() & Long.MAX_VALUE) % (i + 1L);
            long t = LongBigArrays.get(a, i);
            LongBigArrays.set(a, i, LongBigArrays.get(a, p));
            LongBigArrays.set(a, p, t);
        }
        return a;
    }

    private static final class BigArrayHashStrategy
    implements Hash.Strategy<long[][]>,
    Serializable {
        private static final long serialVersionUID = -7046029254386353129L;

        private BigArrayHashStrategy() {
        }

        @Override
        public int hashCode(long[][] o) {
            return Arrays.deepHashCode((Object[])o);
        }

        @Override
        public boolean equals(long[][] a, long[][] b) {
            return LongBigArrays.equals(a, b);
        }
    }

}

