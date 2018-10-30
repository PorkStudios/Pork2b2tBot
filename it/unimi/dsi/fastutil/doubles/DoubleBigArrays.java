/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.doubles;

import it.unimi.dsi.fastutil.BigArrays;
import it.unimi.dsi.fastutil.Hash;
import it.unimi.dsi.fastutil.bytes.ByteBigArrays;
import it.unimi.dsi.fastutil.doubles.DoubleArrays;
import it.unimi.dsi.fastutil.doubles.DoubleComparator;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Random;

public final class DoubleBigArrays {
    public static final double[][] EMPTY_BIG_ARRAY = new double[0][];
    public static final Hash.Strategy HASH_STRATEGY = new BigArrayHashStrategy();
    private static final int SMALL = 7;
    private static final int MEDIUM = 40;
    private static final int DIGIT_BITS = 8;
    private static final int DIGIT_MASK = 255;
    private static final int DIGITS_PER_ELEMENT = 8;

    private DoubleBigArrays() {
    }

    public static double get(double[][] array, long index) {
        return array[BigArrays.segment(index)][BigArrays.displacement(index)];
    }

    public static void set(double[][] array, long index, double value) {
        array[BigArrays.segment((long)index)][BigArrays.displacement((long)index)] = value;
    }

    public static void swap(double[][] array, long first, long second) {
        double t = array[BigArrays.segment(first)][BigArrays.displacement(first)];
        array[BigArrays.segment((long)first)][BigArrays.displacement((long)first)] = array[BigArrays.segment(second)][BigArrays.displacement(second)];
        array[BigArrays.segment((long)second)][BigArrays.displacement((long)second)] = t;
    }

    public static void add(double[][] array, long index, double incr) {
        double[] arrd = array[BigArrays.segment(index)];
        int n = BigArrays.displacement(index);
        arrd[n] = arrd[n] + incr;
    }

    public static void mul(double[][] array, long index, double factor) {
        double[] arrd = array[BigArrays.segment(index)];
        int n = BigArrays.displacement(index);
        arrd[n] = arrd[n] * factor;
    }

    public static void incr(double[][] array, long index) {
        double[] arrd = array[BigArrays.segment(index)];
        int n = BigArrays.displacement(index);
        arrd[n] = arrd[n] + 1.0;
    }

    public static void decr(double[][] array, long index) {
        double[] arrd = array[BigArrays.segment(index)];
        int n = BigArrays.displacement(index);
        arrd[n] = arrd[n] - 1.0;
    }

    public static long length(double[][] array) {
        int length = array.length;
        return length == 0 ? 0L : BigArrays.start(length - 1) + (long)array[length - 1].length;
    }

    public static void copy(double[][] srcArray, long srcPos, double[][] destArray, long destPos, long length) {
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

    public static void copyFromBig(double[][] srcArray, long srcPos, double[] destArray, int destPos, int length) {
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

    public static void copyToBig(double[] srcArray, int srcPos, double[][] destArray, long destPos, long length) {
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

    public static double[][] newBigArray(long length) {
        if (length == 0L) {
            return EMPTY_BIG_ARRAY;
        }
        BigArrays.ensureLength(length);
        int baseLength = (int)(length + 0x7FFFFFFL >>> 27);
        double[][] base = new double[baseLength][];
        int residual = (int)(length & 0x7FFFFFFL);
        if (residual != 0) {
            for (int i = 0; i < baseLength - 1; ++i) {
                base[i] = new double[134217728];
            }
            base[baseLength - 1] = new double[residual];
        } else {
            for (int i = 0; i < baseLength; ++i) {
                base[i] = new double[134217728];
            }
        }
        return base;
    }

    public static double[][] wrap(double[] array) {
        if (array.length == 0) {
            return EMPTY_BIG_ARRAY;
        }
        if (array.length <= 134217728) {
            return new double[][]{array};
        }
        double[][] bigArray = DoubleBigArrays.newBigArray(array.length);
        for (int i = 0; i < bigArray.length; ++i) {
            System.arraycopy(array, (int)BigArrays.start(i), bigArray[i], 0, bigArray[i].length);
        }
        return bigArray;
    }

    public static double[][] ensureCapacity(double[][] array, long length) {
        return DoubleBigArrays.ensureCapacity(array, length, DoubleBigArrays.length(array));
    }

    public static double[][] ensureCapacity(double[][] array, long length, long preserve) {
        long oldLength = DoubleBigArrays.length(array);
        if (length > oldLength) {
            BigArrays.ensureLength(length);
            int valid = array.length - (array.length == 0 || array.length > 0 && array[array.length - 1].length == 134217728 ? 0 : 1);
            int baseLength = (int)(length + 0x7FFFFFFL >>> 27);
            double[][] base = (double[][])Arrays.copyOf(array, baseLength);
            int residual = (int)(length & 0x7FFFFFFL);
            if (residual != 0) {
                for (int i = valid; i < baseLength - 1; ++i) {
                    base[i] = new double[134217728];
                }
                base[baseLength - 1] = new double[residual];
            } else {
                for (int i = valid; i < baseLength; ++i) {
                    base[i] = new double[134217728];
                }
            }
            if (preserve - (long)valid * 0x8000000L > 0L) {
                DoubleBigArrays.copy(array, (long)valid * 0x8000000L, base, (long)valid * 0x8000000L, preserve - (long)valid * 0x8000000L);
            }
            return base;
        }
        return array;
    }

    public static double[][] grow(double[][] array, long length) {
        long oldLength = DoubleBigArrays.length(array);
        return length > oldLength ? DoubleBigArrays.grow(array, length, oldLength) : array;
    }

    public static double[][] grow(double[][] array, long length, long preserve) {
        long oldLength = DoubleBigArrays.length(array);
        return length > oldLength ? DoubleBigArrays.ensureCapacity(array, Math.max(2L * oldLength, length), preserve) : array;
    }

    public static double[][] trim(double[][] array, long length) {
        BigArrays.ensureLength(length);
        long oldLength = DoubleBigArrays.length(array);
        if (length >= oldLength) {
            return array;
        }
        int baseLength = (int)(length + 0x7FFFFFFL >>> 27);
        double[][] base = (double[][])Arrays.copyOf(array, baseLength);
        int residual = (int)(length & 0x7FFFFFFL);
        if (residual != 0) {
            base[baseLength - 1] = DoubleArrays.trim(base[baseLength - 1], residual);
        }
        return base;
    }

    public static double[][] setLength(double[][] array, long length) {
        long oldLength = DoubleBigArrays.length(array);
        if (length == oldLength) {
            return array;
        }
        if (length < oldLength) {
            return DoubleBigArrays.trim(array, length);
        }
        return DoubleBigArrays.ensureCapacity(array, length);
    }

    public static double[][] copy(double[][] array, long offset, long length) {
        DoubleBigArrays.ensureOffsetLength(array, offset, length);
        double[][] a = DoubleBigArrays.newBigArray(length);
        DoubleBigArrays.copy(array, offset, a, 0L, length);
        return a;
    }

    public static double[][] copy(double[][] array) {
        double[][] base = (double[][])array.clone();
        int i = base.length;
        while (i-- != 0) {
            base[i] = (double[])array[i].clone();
        }
        return base;
    }

    public static void fill(double[][] array, double value) {
        int i = array.length;
        while (i-- != 0) {
            Arrays.fill(array[i], value);
        }
    }

    public static void fill(double[][] array, long from, long to, double value) {
        long length = DoubleBigArrays.length(array);
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

    public static boolean equals(double[][] a1, double[][] a2) {
        if (DoubleBigArrays.length(a1) != DoubleBigArrays.length(a2)) {
            return false;
        }
        int i = a1.length;
        while (i-- != 0) {
            double[] t = a1[i];
            double[] u = a2[i];
            int j = t.length;
            while (j-- != 0) {
                if (Double.doubleToLongBits(t[j]) == Double.doubleToLongBits(u[j])) continue;
                return false;
            }
        }
        return true;
    }

    public static String toString(double[][] a) {
        if (a == null) {
            return "null";
        }
        long last = DoubleBigArrays.length(a) - 1L;
        if (last == -1L) {
            return "[]";
        }
        StringBuilder b = new StringBuilder();
        b.append('[');
        long i = 0L;
        do {
            b.append(String.valueOf(DoubleBigArrays.get(a, i)));
            if (i == last) {
                return b.append(']').toString();
            }
            b.append(", ");
            ++i;
        } while (true);
    }

    public static void ensureFromTo(double[][] a, long from, long to) {
        BigArrays.ensureFromTo(DoubleBigArrays.length(a), from, to);
    }

    public static void ensureOffsetLength(double[][] a, long offset, long length) {
        BigArrays.ensureOffsetLength(DoubleBigArrays.length(a), offset, length);
    }

    private static void vecSwap(double[][] x, long a, long b, long n) {
        int i = 0;
        while ((long)i < n) {
            DoubleBigArrays.swap(x, a, b);
            ++i;
            ++a;
            ++b;
        }
    }

    private static long med3(double[][] x, long a, long b, long c, DoubleComparator comp) {
        int ab = comp.compare(DoubleBigArrays.get(x, a), DoubleBigArrays.get(x, b));
        int ac = comp.compare(DoubleBigArrays.get(x, a), DoubleBigArrays.get(x, c));
        int bc = comp.compare(DoubleBigArrays.get(x, b), DoubleBigArrays.get(x, c));
        return ab < 0 ? (bc < 0 ? b : (ac < 0 ? c : a)) : (bc > 0 ? b : (ac > 0 ? c : a));
    }

    private static void selectionSort(double[][] a, long from, long to, DoubleComparator comp) {
        for (long i = from; i < to - 1L; ++i) {
            long m = i;
            for (long j = i + 1L; j < to; ++j) {
                if (comp.compare(DoubleBigArrays.get(a, j), DoubleBigArrays.get(a, m)) >= 0) continue;
                m = j;
            }
            if (m == i) continue;
            DoubleBigArrays.swap(a, i, m);
        }
    }

    public static void quickSort(double[][] x, long from, long to, DoubleComparator comp) {
        long a;
        long c;
        long len = to - from;
        if (len < 7L) {
            DoubleBigArrays.selectionSort(x, from, to, comp);
            return;
        }
        long m = from + len / 2L;
        if (len > 7L) {
            long l = from;
            long n = to - 1L;
            if (len > 40L) {
                long s = len / 8L;
                l = DoubleBigArrays.med3(x, l, l + s, l + 2L * s, comp);
                m = DoubleBigArrays.med3(x, m - s, m, m + s, comp);
                n = DoubleBigArrays.med3(x, n - 2L * s, n - s, n, comp);
            }
            m = DoubleBigArrays.med3(x, l, m, n, comp);
        }
        double v = DoubleBigArrays.get(x, m);
        long b = a = from;
        long d = c = to - 1L;
        do {
            int comparison;
            if (b <= c && (comparison = comp.compare(DoubleBigArrays.get(x, b), v)) <= 0) {
                if (comparison == 0) {
                    DoubleBigArrays.swap(x, a++, b);
                }
                ++b;
                continue;
            }
            while (c >= b && (comparison = comp.compare(DoubleBigArrays.get(x, c), v)) >= 0) {
                if (comparison == 0) {
                    DoubleBigArrays.swap(x, c, d--);
                }
                --c;
            }
            if (b > c) break;
            DoubleBigArrays.swap(x, b++, c--);
        } while (true);
        long n = to;
        long s = Math.min(a - from, b - a);
        DoubleBigArrays.vecSwap(x, from, b - s, s);
        s = Math.min(d - c, n - d - 1L);
        DoubleBigArrays.vecSwap(x, b, n - s, s);
        s = b - a;
        if (s > 1L) {
            DoubleBigArrays.quickSort(x, from, from + s, comp);
        }
        if ((s = d - c) > 1L) {
            DoubleBigArrays.quickSort(x, n - s, n, comp);
        }
    }

    private static long med3(double[][] x, long a, long b, long c) {
        int ab = Double.compare(DoubleBigArrays.get(x, a), DoubleBigArrays.get(x, b));
        int ac = Double.compare(DoubleBigArrays.get(x, a), DoubleBigArrays.get(x, c));
        int bc = Double.compare(DoubleBigArrays.get(x, b), DoubleBigArrays.get(x, c));
        return ab < 0 ? (bc < 0 ? b : (ac < 0 ? c : a)) : (bc > 0 ? b : (ac > 0 ? c : a));
    }

    private static void selectionSort(double[][] a, long from, long to) {
        for (long i = from; i < to - 1L; ++i) {
            long m = i;
            for (long j = i + 1L; j < to; ++j) {
                if (Double.compare(DoubleBigArrays.get(a, j), DoubleBigArrays.get(a, m)) >= 0) continue;
                m = j;
            }
            if (m == i) continue;
            DoubleBigArrays.swap(a, i, m);
        }
    }

    public static void quickSort(double[][] x, DoubleComparator comp) {
        DoubleBigArrays.quickSort(x, 0L, DoubleBigArrays.length(x), comp);
    }

    public static void quickSort(double[][] x, long from, long to) {
        long c;
        long a;
        long len = to - from;
        if (len < 7L) {
            DoubleBigArrays.selectionSort(x, from, to);
            return;
        }
        long m = from + len / 2L;
        if (len > 7L) {
            long l = from;
            long n = to - 1L;
            if (len > 40L) {
                long s = len / 8L;
                l = DoubleBigArrays.med3(x, l, l + s, l + 2L * s);
                m = DoubleBigArrays.med3(x, m - s, m, m + s);
                n = DoubleBigArrays.med3(x, n - 2L * s, n - s, n);
            }
            m = DoubleBigArrays.med3(x, l, m, n);
        }
        double v = DoubleBigArrays.get(x, m);
        long b = a = from;
        long d = c = to - 1L;
        do {
            int comparison;
            if (b <= c && (comparison = Double.compare(DoubleBigArrays.get(x, b), v)) <= 0) {
                if (comparison == 0) {
                    DoubleBigArrays.swap(x, a++, b);
                }
                ++b;
                continue;
            }
            while (c >= b && (comparison = Double.compare(DoubleBigArrays.get(x, c), v)) >= 0) {
                if (comparison == 0) {
                    DoubleBigArrays.swap(x, c, d--);
                }
                --c;
            }
            if (b > c) break;
            DoubleBigArrays.swap(x, b++, c--);
        } while (true);
        long n = to;
        long s = Math.min(a - from, b - a);
        DoubleBigArrays.vecSwap(x, from, b - s, s);
        s = Math.min(d - c, n - d - 1L);
        DoubleBigArrays.vecSwap(x, b, n - s, s);
        s = b - a;
        if (s > 1L) {
            DoubleBigArrays.quickSort(x, from, from + s);
        }
        if ((s = d - c) > 1L) {
            DoubleBigArrays.quickSort(x, n - s, n);
        }
    }

    public static void quickSort(double[][] x) {
        DoubleBigArrays.quickSort(x, 0L, DoubleBigArrays.length(x));
    }

    public static long binarySearch(double[][] a, long from, long to, double key) {
        --to;
        while (from <= to) {
            long mid = from + to >>> 1;
            double midVal = DoubleBigArrays.get(a, mid);
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

    public static long binarySearch(double[][] a, double key) {
        return DoubleBigArrays.binarySearch(a, 0L, DoubleBigArrays.length(a), key);
    }

    public static long binarySearch(double[][] a, long from, long to, double key, DoubleComparator c) {
        --to;
        while (from <= to) {
            long mid = from + to >>> 1;
            double midVal = DoubleBigArrays.get(a, mid);
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

    public static long binarySearch(double[][] a, double key, DoubleComparator c) {
        return DoubleBigArrays.binarySearch(a, 0L, DoubleBigArrays.length(a), key, c);
    }

    private static final long fixDouble(double d) {
        long l = Double.doubleToRawLongBits(d);
        return l >= 0L ? l : l ^ Long.MAX_VALUE;
    }

    public static void radixSort(double[][] a) {
        DoubleBigArrays.radixSort(a, 0L, DoubleBigArrays.length(a));
    }

    public static void radixSort(double[][] a, long from, long to) {
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
                DoubleBigArrays.selectionSort(a, first, first + length);
                continue;
            }
            int shift = (7 - level % 8) * 8;
            long i = length;
            while (i-- != 0L) {
                ByteBigArrays.set(digit, i, (byte)(DoubleBigArrays.fixDouble(DoubleBigArrays.get(a, first + i)) >>> shift & 255L ^ (long)signMask));
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
                double t = DoubleBigArrays.get(a, i3 + first);
                c = ByteBigArrays.get(digit, i3) & 255;
                do {
                    long[] arrl = pos;
                    int n3 = c;
                    long l = arrl[n3] - 1L;
                    arrl[n3] = l;
                    long d = l;
                    if (l <= i3) break;
                    double z = t;
                    int zz = c;
                    t = DoubleBigArrays.get(a, d + first);
                    c = ByteBigArrays.get(digit, d) & 255;
                    DoubleBigArrays.set(a, d + first, z);
                    ByteBigArrays.set(digit, d, (byte)zz);
                } while (true);
                DoubleBigArrays.set(a, i3 + first, t);
                count[c] = 0L;
            }
        }
    }

    private static void selectionSort(double[][] a, double[][] b, long from, long to) {
        for (long i = from; i < to - 1L; ++i) {
            long m = i;
            for (long j = i + 1L; j < to; ++j) {
                if (Double.compare(DoubleBigArrays.get(a, j), DoubleBigArrays.get(a, m)) >= 0 && (Double.compare(DoubleBigArrays.get(a, j), DoubleBigArrays.get(a, m)) != 0 || Double.compare(DoubleBigArrays.get(b, j), DoubleBigArrays.get(b, m)) >= 0)) continue;
                m = j;
            }
            if (m == i) continue;
            double t = DoubleBigArrays.get(a, i);
            DoubleBigArrays.set(a, i, DoubleBigArrays.get(a, m));
            DoubleBigArrays.set(a, m, t);
            t = DoubleBigArrays.get(b, i);
            DoubleBigArrays.set(b, i, DoubleBigArrays.get(b, m));
            DoubleBigArrays.set(b, m, t);
        }
    }

    public static void radixSort(double[][] a, double[][] b) {
        DoubleBigArrays.radixSort(a, b, 0L, DoubleBigArrays.length(a));
    }

    public static void radixSort(double[][] a, double[][] b, long from, long to) {
        int layers = 2;
        if (DoubleBigArrays.length(a) != DoubleBigArrays.length(b)) {
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
                DoubleBigArrays.selectionSort(a, b, first, first + length);
                continue;
            }
            double[][] k = level < 8 ? a : b;
            int shift = (7 - level % 8) * 8;
            long i = length;
            while (i-- != 0L) {
                ByteBigArrays.set(digit, i, (byte)(DoubleBigArrays.fixDouble(DoubleBigArrays.get(k, first + i)) >>> shift & 255L ^ (long)signMask));
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
                double t = DoubleBigArrays.get(a, i3 + first);
                double u = DoubleBigArrays.get(b, i3 + first);
                c = ByteBigArrays.get(digit, i3) & 255;
                do {
                    long[] arrl = pos;
                    int n3 = c;
                    long l = arrl[n3] - 1L;
                    arrl[n3] = l;
                    long d = l;
                    if (l <= i3) break;
                    double z = t;
                    int zz = c;
                    t = DoubleBigArrays.get(a, d + first);
                    DoubleBigArrays.set(a, d + first, z);
                    z = u;
                    u = DoubleBigArrays.get(b, d + first);
                    DoubleBigArrays.set(b, d + first, z);
                    c = ByteBigArrays.get(digit, d) & 255;
                    ByteBigArrays.set(digit, d, (byte)zz);
                } while (true);
                DoubleBigArrays.set(a, i3 + first, t);
                DoubleBigArrays.set(b, i3 + first, u);
                count[c] = 0L;
            }
        }
    }

    public static double[][] shuffle(double[][] a, long from, long to, Random random) {
        long i = to - from;
        while (i-- != 0L) {
            long p = (random.nextLong() & Long.MAX_VALUE) % (i + 1L);
            double t = DoubleBigArrays.get(a, from + i);
            DoubleBigArrays.set(a, from + i, DoubleBigArrays.get(a, from + p));
            DoubleBigArrays.set(a, from + p, t);
        }
        return a;
    }

    public static double[][] shuffle(double[][] a, Random random) {
        long i = DoubleBigArrays.length(a);
        while (i-- != 0L) {
            long p = (random.nextLong() & Long.MAX_VALUE) % (i + 1L);
            double t = DoubleBigArrays.get(a, i);
            DoubleBigArrays.set(a, i, DoubleBigArrays.get(a, p));
            DoubleBigArrays.set(a, p, t);
        }
        return a;
    }

    private static final class BigArrayHashStrategy
    implements Hash.Strategy<double[][]>,
    Serializable {
        private static final long serialVersionUID = -7046029254386353129L;

        private BigArrayHashStrategy() {
        }

        @Override
        public int hashCode(double[][] o) {
            return Arrays.deepHashCode((Object[])o);
        }

        @Override
        public boolean equals(double[][] a, double[][] b) {
            return DoubleBigArrays.equals(a, b);
        }
    }

}

