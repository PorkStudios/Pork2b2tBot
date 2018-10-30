/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.shorts;

import it.unimi.dsi.fastutil.BigArrays;
import it.unimi.dsi.fastutil.Hash;
import it.unimi.dsi.fastutil.bytes.ByteBigArrays;
import it.unimi.dsi.fastutil.shorts.ShortArrays;
import it.unimi.dsi.fastutil.shorts.ShortComparator;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Random;

public final class ShortBigArrays {
    public static final short[][] EMPTY_BIG_ARRAY = new short[0][];
    public static final Hash.Strategy HASH_STRATEGY = new BigArrayHashStrategy();
    private static final int SMALL = 7;
    private static final int MEDIUM = 40;
    private static final int DIGIT_BITS = 8;
    private static final int DIGIT_MASK = 255;
    private static final int DIGITS_PER_ELEMENT = 2;

    private ShortBigArrays() {
    }

    public static short get(short[][] array, long index) {
        return array[BigArrays.segment(index)][BigArrays.displacement(index)];
    }

    public static void set(short[][] array, long index, short value) {
        array[BigArrays.segment((long)index)][BigArrays.displacement((long)index)] = value;
    }

    public static void swap(short[][] array, long first, long second) {
        short t = array[BigArrays.segment(first)][BigArrays.displacement(first)];
        array[BigArrays.segment((long)first)][BigArrays.displacement((long)first)] = array[BigArrays.segment(second)][BigArrays.displacement(second)];
        array[BigArrays.segment((long)second)][BigArrays.displacement((long)second)] = t;
    }

    public static void add(short[][] array, long index, short incr) {
        short[] arrs = array[BigArrays.segment(index)];
        int n = BigArrays.displacement(index);
        arrs[n] = (short)(arrs[n] + incr);
    }

    public static void mul(short[][] array, long index, short factor) {
        short[] arrs = array[BigArrays.segment(index)];
        int n = BigArrays.displacement(index);
        arrs[n] = (short)(arrs[n] * factor);
    }

    public static void incr(short[][] array, long index) {
        short[] arrs = array[BigArrays.segment(index)];
        int n = BigArrays.displacement(index);
        arrs[n] = (short)(arrs[n] + 1);
    }

    public static void decr(short[][] array, long index) {
        short[] arrs = array[BigArrays.segment(index)];
        int n = BigArrays.displacement(index);
        arrs[n] = (short)(arrs[n] - 1);
    }

    public static long length(short[][] array) {
        int length = array.length;
        return length == 0 ? 0L : BigArrays.start(length - 1) + (long)array[length - 1].length;
    }

    public static void copy(short[][] srcArray, long srcPos, short[][] destArray, long destPos, long length) {
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

    public static void copyFromBig(short[][] srcArray, long srcPos, short[] destArray, int destPos, int length) {
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

    public static void copyToBig(short[] srcArray, int srcPos, short[][] destArray, long destPos, long length) {
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

    public static short[][] newBigArray(long length) {
        if (length == 0L) {
            return EMPTY_BIG_ARRAY;
        }
        BigArrays.ensureLength(length);
        int baseLength = (int)(length + 0x7FFFFFFL >>> 27);
        short[][] base = new short[baseLength][];
        int residual = (int)(length & 0x7FFFFFFL);
        if (residual != 0) {
            for (int i = 0; i < baseLength - 1; ++i) {
                base[i] = new short[134217728];
            }
            base[baseLength - 1] = new short[residual];
        } else {
            for (int i = 0; i < baseLength; ++i) {
                base[i] = new short[134217728];
            }
        }
        return base;
    }

    public static short[][] wrap(short[] array) {
        if (array.length == 0) {
            return EMPTY_BIG_ARRAY;
        }
        if (array.length <= 134217728) {
            return new short[][]{array};
        }
        short[][] bigArray = ShortBigArrays.newBigArray(array.length);
        for (int i = 0; i < bigArray.length; ++i) {
            System.arraycopy(array, (int)BigArrays.start(i), bigArray[i], 0, bigArray[i].length);
        }
        return bigArray;
    }

    public static short[][] ensureCapacity(short[][] array, long length) {
        return ShortBigArrays.ensureCapacity(array, length, ShortBigArrays.length(array));
    }

    public static short[][] ensureCapacity(short[][] array, long length, long preserve) {
        long oldLength = ShortBigArrays.length(array);
        if (length > oldLength) {
            BigArrays.ensureLength(length);
            int valid = array.length - (array.length == 0 || array.length > 0 && array[array.length - 1].length == 134217728 ? 0 : 1);
            int baseLength = (int)(length + 0x7FFFFFFL >>> 27);
            short[][] base = (short[][])Arrays.copyOf(array, baseLength);
            int residual = (int)(length & 0x7FFFFFFL);
            if (residual != 0) {
                for (int i = valid; i < baseLength - 1; ++i) {
                    base[i] = new short[134217728];
                }
                base[baseLength - 1] = new short[residual];
            } else {
                for (int i = valid; i < baseLength; ++i) {
                    base[i] = new short[134217728];
                }
            }
            if (preserve - (long)valid * 0x8000000L > 0L) {
                ShortBigArrays.copy(array, (long)valid * 0x8000000L, base, (long)valid * 0x8000000L, preserve - (long)valid * 0x8000000L);
            }
            return base;
        }
        return array;
    }

    public static short[][] grow(short[][] array, long length) {
        long oldLength = ShortBigArrays.length(array);
        return length > oldLength ? ShortBigArrays.grow(array, length, oldLength) : array;
    }

    public static short[][] grow(short[][] array, long length, long preserve) {
        long oldLength = ShortBigArrays.length(array);
        return length > oldLength ? ShortBigArrays.ensureCapacity(array, Math.max(2L * oldLength, length), preserve) : array;
    }

    public static short[][] trim(short[][] array, long length) {
        BigArrays.ensureLength(length);
        long oldLength = ShortBigArrays.length(array);
        if (length >= oldLength) {
            return array;
        }
        int baseLength = (int)(length + 0x7FFFFFFL >>> 27);
        short[][] base = (short[][])Arrays.copyOf(array, baseLength);
        int residual = (int)(length & 0x7FFFFFFL);
        if (residual != 0) {
            base[baseLength - 1] = ShortArrays.trim(base[baseLength - 1], residual);
        }
        return base;
    }

    public static short[][] setLength(short[][] array, long length) {
        long oldLength = ShortBigArrays.length(array);
        if (length == oldLength) {
            return array;
        }
        if (length < oldLength) {
            return ShortBigArrays.trim(array, length);
        }
        return ShortBigArrays.ensureCapacity(array, length);
    }

    public static short[][] copy(short[][] array, long offset, long length) {
        ShortBigArrays.ensureOffsetLength(array, offset, length);
        short[][] a = ShortBigArrays.newBigArray(length);
        ShortBigArrays.copy(array, offset, a, 0L, length);
        return a;
    }

    public static short[][] copy(short[][] array) {
        short[][] base = (short[][])array.clone();
        int i = base.length;
        while (i-- != 0) {
            base[i] = (short[])array[i].clone();
        }
        return base;
    }

    public static void fill(short[][] array, short value) {
        int i = array.length;
        while (i-- != 0) {
            Arrays.fill(array[i], value);
        }
    }

    public static void fill(short[][] array, long from, long to, short value) {
        long length = ShortBigArrays.length(array);
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

    public static boolean equals(short[][] a1, short[][] a2) {
        if (ShortBigArrays.length(a1) != ShortBigArrays.length(a2)) {
            return false;
        }
        int i = a1.length;
        while (i-- != 0) {
            short[] t = a1[i];
            short[] u = a2[i];
            int j = t.length;
            while (j-- != 0) {
                if (t[j] == u[j]) continue;
                return false;
            }
        }
        return true;
    }

    public static String toString(short[][] a) {
        if (a == null) {
            return "null";
        }
        long last = ShortBigArrays.length(a) - 1L;
        if (last == -1L) {
            return "[]";
        }
        StringBuilder b = new StringBuilder();
        b.append('[');
        long i = 0L;
        do {
            b.append(String.valueOf(ShortBigArrays.get(a, i)));
            if (i == last) {
                return b.append(']').toString();
            }
            b.append(", ");
            ++i;
        } while (true);
    }

    public static void ensureFromTo(short[][] a, long from, long to) {
        BigArrays.ensureFromTo(ShortBigArrays.length(a), from, to);
    }

    public static void ensureOffsetLength(short[][] a, long offset, long length) {
        BigArrays.ensureOffsetLength(ShortBigArrays.length(a), offset, length);
    }

    private static void vecSwap(short[][] x, long a, long b, long n) {
        int i = 0;
        while ((long)i < n) {
            ShortBigArrays.swap(x, a, b);
            ++i;
            ++a;
            ++b;
        }
    }

    private static long med3(short[][] x, long a, long b, long c, ShortComparator comp) {
        int ab = comp.compare(ShortBigArrays.get(x, a), ShortBigArrays.get(x, b));
        int ac = comp.compare(ShortBigArrays.get(x, a), ShortBigArrays.get(x, c));
        int bc = comp.compare(ShortBigArrays.get(x, b), ShortBigArrays.get(x, c));
        return ab < 0 ? (bc < 0 ? b : (ac < 0 ? c : a)) : (bc > 0 ? b : (ac > 0 ? c : a));
    }

    private static void selectionSort(short[][] a, long from, long to, ShortComparator comp) {
        for (long i = from; i < to - 1L; ++i) {
            long m = i;
            for (long j = i + 1L; j < to; ++j) {
                if (comp.compare(ShortBigArrays.get(a, j), ShortBigArrays.get(a, m)) >= 0) continue;
                m = j;
            }
            if (m == i) continue;
            ShortBigArrays.swap(a, i, m);
        }
    }

    public static void quickSort(short[][] x, long from, long to, ShortComparator comp) {
        long c;
        long a;
        long len = to - from;
        if (len < 7L) {
            ShortBigArrays.selectionSort(x, from, to, comp);
            return;
        }
        long m = from + len / 2L;
        if (len > 7L) {
            long l = from;
            long n = to - 1L;
            if (len > 40L) {
                long s = len / 8L;
                l = ShortBigArrays.med3(x, l, l + s, l + 2L * s, comp);
                m = ShortBigArrays.med3(x, m - s, m, m + s, comp);
                n = ShortBigArrays.med3(x, n - 2L * s, n - s, n, comp);
            }
            m = ShortBigArrays.med3(x, l, m, n, comp);
        }
        short v = ShortBigArrays.get(x, m);
        long b = a = from;
        long d = c = to - 1L;
        do {
            int comparison;
            if (b <= c && (comparison = comp.compare(ShortBigArrays.get(x, b), v)) <= 0) {
                if (comparison == 0) {
                    ShortBigArrays.swap(x, a++, b);
                }
                ++b;
                continue;
            }
            while (c >= b && (comparison = comp.compare(ShortBigArrays.get(x, c), v)) >= 0) {
                if (comparison == 0) {
                    ShortBigArrays.swap(x, c, d--);
                }
                --c;
            }
            if (b > c) break;
            ShortBigArrays.swap(x, b++, c--);
        } while (true);
        long n = to;
        long s = Math.min(a - from, b - a);
        ShortBigArrays.vecSwap(x, from, b - s, s);
        s = Math.min(d - c, n - d - 1L);
        ShortBigArrays.vecSwap(x, b, n - s, s);
        s = b - a;
        if (s > 1L) {
            ShortBigArrays.quickSort(x, from, from + s, comp);
        }
        if ((s = d - c) > 1L) {
            ShortBigArrays.quickSort(x, n - s, n, comp);
        }
    }

    private static long med3(short[][] x, long a, long b, long c) {
        int ab = Short.compare(ShortBigArrays.get(x, a), ShortBigArrays.get(x, b));
        int ac = Short.compare(ShortBigArrays.get(x, a), ShortBigArrays.get(x, c));
        int bc = Short.compare(ShortBigArrays.get(x, b), ShortBigArrays.get(x, c));
        return ab < 0 ? (bc < 0 ? b : (ac < 0 ? c : a)) : (bc > 0 ? b : (ac > 0 ? c : a));
    }

    private static void selectionSort(short[][] a, long from, long to) {
        for (long i = from; i < to - 1L; ++i) {
            long m = i;
            for (long j = i + 1L; j < to; ++j) {
                if (ShortBigArrays.get(a, j) >= ShortBigArrays.get(a, m)) continue;
                m = j;
            }
            if (m == i) continue;
            ShortBigArrays.swap(a, i, m);
        }
    }

    public static void quickSort(short[][] x, ShortComparator comp) {
        ShortBigArrays.quickSort(x, 0L, ShortBigArrays.length(x), comp);
    }

    public static void quickSort(short[][] x, long from, long to) {
        long a;
        long c;
        long len = to - from;
        if (len < 7L) {
            ShortBigArrays.selectionSort(x, from, to);
            return;
        }
        long m = from + len / 2L;
        if (len > 7L) {
            long l = from;
            long n = to - 1L;
            if (len > 40L) {
                long s = len / 8L;
                l = ShortBigArrays.med3(x, l, l + s, l + 2L * s);
                m = ShortBigArrays.med3(x, m - s, m, m + s);
                n = ShortBigArrays.med3(x, n - 2L * s, n - s, n);
            }
            m = ShortBigArrays.med3(x, l, m, n);
        }
        short v = ShortBigArrays.get(x, m);
        long b = a = from;
        long d = c = to - 1L;
        do {
            int comparison;
            if (b <= c && (comparison = Short.compare(ShortBigArrays.get(x, b), v)) <= 0) {
                if (comparison == 0) {
                    ShortBigArrays.swap(x, a++, b);
                }
                ++b;
                continue;
            }
            while (c >= b && (comparison = Short.compare(ShortBigArrays.get(x, c), v)) >= 0) {
                if (comparison == 0) {
                    ShortBigArrays.swap(x, c, d--);
                }
                --c;
            }
            if (b > c) break;
            ShortBigArrays.swap(x, b++, c--);
        } while (true);
        long n = to;
        long s = Math.min(a - from, b - a);
        ShortBigArrays.vecSwap(x, from, b - s, s);
        s = Math.min(d - c, n - d - 1L);
        ShortBigArrays.vecSwap(x, b, n - s, s);
        s = b - a;
        if (s > 1L) {
            ShortBigArrays.quickSort(x, from, from + s);
        }
        if ((s = d - c) > 1L) {
            ShortBigArrays.quickSort(x, n - s, n);
        }
    }

    public static void quickSort(short[][] x) {
        ShortBigArrays.quickSort(x, 0L, ShortBigArrays.length(x));
    }

    public static long binarySearch(short[][] a, long from, long to, short key) {
        --to;
        while (from <= to) {
            long mid = from + to >>> 1;
            short midVal = ShortBigArrays.get(a, mid);
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

    public static long binarySearch(short[][] a, short key) {
        return ShortBigArrays.binarySearch(a, 0L, ShortBigArrays.length(a), key);
    }

    public static long binarySearch(short[][] a, long from, long to, short key, ShortComparator c) {
        --to;
        while (from <= to) {
            long mid = from + to >>> 1;
            short midVal = ShortBigArrays.get(a, mid);
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

    public static long binarySearch(short[][] a, short key, ShortComparator c) {
        return ShortBigArrays.binarySearch(a, 0L, ShortBigArrays.length(a), key, c);
    }

    public static void radixSort(short[][] a) {
        ShortBigArrays.radixSort(a, 0L, ShortBigArrays.length(a));
    }

    public static void radixSort(short[][] a, long from, long to) {
        boolean maxLevel = true;
        int stackSize = 256;
        long[] offsetStack = new long[256];
        int offsetPos = 0;
        long[] lengthStack = new long[256];
        int lengthPos = 0;
        int[] levelStack = new int[256];
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
            int n = signMask = (level = levelStack[--levelPos]) % 2 == 0 ? 128 : 0;
            if (length < 40L) {
                ShortBigArrays.selectionSort(a, first, first + length);
                continue;
            }
            int shift = (1 - level % 2) * 8;
            long i = length;
            while (i-- != 0L) {
                ByteBigArrays.set(digit, i, (byte)(ShortBigArrays.get(a, first + i) >>> shift & 255 ^ signMask));
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
                    if (level < 1 && count[i2] > 1L) {
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
                short t = ShortBigArrays.get(a, i3 + first);
                c = ByteBigArrays.get(digit, i3) & 255;
                do {
                    long[] arrl = pos;
                    int n3 = c;
                    long l = arrl[n3] - 1L;
                    arrl[n3] = l;
                    long d = l;
                    if (l <= i3) break;
                    short z = t;
                    int zz = c;
                    t = ShortBigArrays.get(a, d + first);
                    c = ByteBigArrays.get(digit, d) & 255;
                    ShortBigArrays.set(a, d + first, z);
                    ByteBigArrays.set(digit, d, (byte)zz);
                } while (true);
                ShortBigArrays.set(a, i3 + first, t);
                count[c] = 0L;
            }
        }
    }

    private static void selectionSort(short[][] a, short[][] b, long from, long to) {
        for (long i = from; i < to - 1L; ++i) {
            long m = i;
            for (long j = i + 1L; j < to; ++j) {
                if (ShortBigArrays.get(a, j) >= ShortBigArrays.get(a, m) && (ShortBigArrays.get(a, j) != ShortBigArrays.get(a, m) || ShortBigArrays.get(b, j) >= ShortBigArrays.get(b, m))) continue;
                m = j;
            }
            if (m == i) continue;
            short t = ShortBigArrays.get(a, i);
            ShortBigArrays.set(a, i, ShortBigArrays.get(a, m));
            ShortBigArrays.set(a, m, t);
            t = ShortBigArrays.get(b, i);
            ShortBigArrays.set(b, i, ShortBigArrays.get(b, m));
            ShortBigArrays.set(b, m, t);
        }
    }

    public static void radixSort(short[][] a, short[][] b) {
        ShortBigArrays.radixSort(a, b, 0L, ShortBigArrays.length(a));
    }

    public static void radixSort(short[][] a, short[][] b, long from, long to) {
        int layers = 2;
        if (ShortBigArrays.length(a) != ShortBigArrays.length(b)) {
            throw new IllegalArgumentException("Array size mismatch.");
        }
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
            int level;
            int signMask;
            long first = offsetStack[--offsetPos];
            long length = lengthStack[--lengthPos];
            int n = signMask = (level = levelStack[--levelPos]) % 2 == 0 ? 128 : 0;
            if (length < 40L) {
                ShortBigArrays.selectionSort(a, b, first, first + length);
                continue;
            }
            short[][] k = level < 2 ? a : b;
            int shift = (1 - level % 2) * 8;
            long i = length;
            while (i-- != 0L) {
                ByteBigArrays.set(digit, i, (byte)(ShortBigArrays.get(k, first + i) >>> shift & 255 ^ signMask));
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
                short t = ShortBigArrays.get(a, i3 + first);
                short u = ShortBigArrays.get(b, i3 + first);
                c = ByteBigArrays.get(digit, i3) & 255;
                do {
                    long[] arrl = pos;
                    int n3 = c;
                    long l = arrl[n3] - 1L;
                    arrl[n3] = l;
                    long d = l;
                    if (l <= i3) break;
                    short z = t;
                    int zz = c;
                    t = ShortBigArrays.get(a, d + first);
                    ShortBigArrays.set(a, d + first, z);
                    z = u;
                    u = ShortBigArrays.get(b, d + first);
                    ShortBigArrays.set(b, d + first, z);
                    c = ByteBigArrays.get(digit, d) & 255;
                    ByteBigArrays.set(digit, d, (byte)zz);
                } while (true);
                ShortBigArrays.set(a, i3 + first, t);
                ShortBigArrays.set(b, i3 + first, u);
                count[c] = 0L;
            }
        }
    }

    public static short[][] shuffle(short[][] a, long from, long to, Random random) {
        long i = to - from;
        while (i-- != 0L) {
            long p = (random.nextLong() & Long.MAX_VALUE) % (i + 1L);
            short t = ShortBigArrays.get(a, from + i);
            ShortBigArrays.set(a, from + i, ShortBigArrays.get(a, from + p));
            ShortBigArrays.set(a, from + p, t);
        }
        return a;
    }

    public static short[][] shuffle(short[][] a, Random random) {
        long i = ShortBigArrays.length(a);
        while (i-- != 0L) {
            long p = (random.nextLong() & Long.MAX_VALUE) % (i + 1L);
            short t = ShortBigArrays.get(a, i);
            ShortBigArrays.set(a, i, ShortBigArrays.get(a, p));
            ShortBigArrays.set(a, p, t);
        }
        return a;
    }

    private static final class BigArrayHashStrategy
    implements Hash.Strategy<short[][]>,
    Serializable {
        private static final long serialVersionUID = -7046029254386353129L;

        private BigArrayHashStrategy() {
        }

        @Override
        public int hashCode(short[][] o) {
            return Arrays.deepHashCode((Object[])o);
        }

        @Override
        public boolean equals(short[][] a, short[][] b) {
            return ShortBigArrays.equals(a, b);
        }
    }

}

