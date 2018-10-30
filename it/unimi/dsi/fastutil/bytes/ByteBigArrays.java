/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.BigArrays;
import it.unimi.dsi.fastutil.Hash;
import it.unimi.dsi.fastutil.bytes.ByteArrays;
import it.unimi.dsi.fastutil.bytes.ByteComparator;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Random;

public final class ByteBigArrays {
    public static final byte[][] EMPTY_BIG_ARRAY = new byte[0][];
    public static final Hash.Strategy HASH_STRATEGY = new BigArrayHashStrategy();
    private static final int SMALL = 7;
    private static final int MEDIUM = 40;
    private static final int DIGIT_BITS = 8;
    private static final int DIGIT_MASK = 255;
    private static final int DIGITS_PER_ELEMENT = 1;

    private ByteBigArrays() {
    }

    public static byte get(byte[][] array, long index) {
        return array[BigArrays.segment(index)][BigArrays.displacement(index)];
    }

    public static void set(byte[][] array, long index, byte value) {
        array[BigArrays.segment((long)index)][BigArrays.displacement((long)index)] = value;
    }

    public static void swap(byte[][] array, long first, long second) {
        byte t = array[BigArrays.segment(first)][BigArrays.displacement(first)];
        array[BigArrays.segment((long)first)][BigArrays.displacement((long)first)] = array[BigArrays.segment(second)][BigArrays.displacement(second)];
        array[BigArrays.segment((long)second)][BigArrays.displacement((long)second)] = t;
    }

    public static void add(byte[][] array, long index, byte incr) {
        byte[] arrby = array[BigArrays.segment(index)];
        int n = BigArrays.displacement(index);
        arrby[n] = (byte)(arrby[n] + incr);
    }

    public static void mul(byte[][] array, long index, byte factor) {
        byte[] arrby = array[BigArrays.segment(index)];
        int n = BigArrays.displacement(index);
        arrby[n] = (byte)(arrby[n] * factor);
    }

    public static void incr(byte[][] array, long index) {
        byte[] arrby = array[BigArrays.segment(index)];
        int n = BigArrays.displacement(index);
        arrby[n] = (byte)(arrby[n] + 1);
    }

    public static void decr(byte[][] array, long index) {
        byte[] arrby = array[BigArrays.segment(index)];
        int n = BigArrays.displacement(index);
        arrby[n] = (byte)(arrby[n] - 1);
    }

    public static long length(byte[][] array) {
        int length = array.length;
        return length == 0 ? 0L : BigArrays.start(length - 1) + (long)array[length - 1].length;
    }

    public static void copy(byte[][] srcArray, long srcPos, byte[][] destArray, long destPos, long length) {
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

    public static void copyFromBig(byte[][] srcArray, long srcPos, byte[] destArray, int destPos, int length) {
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

    public static void copyToBig(byte[] srcArray, int srcPos, byte[][] destArray, long destPos, long length) {
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

    public static byte[][] newBigArray(long length) {
        if (length == 0L) {
            return EMPTY_BIG_ARRAY;
        }
        BigArrays.ensureLength(length);
        int baseLength = (int)(length + 0x7FFFFFFL >>> 27);
        byte[][] base = new byte[baseLength][];
        int residual = (int)(length & 0x7FFFFFFL);
        if (residual != 0) {
            for (int i = 0; i < baseLength - 1; ++i) {
                base[i] = new byte[134217728];
            }
            base[baseLength - 1] = new byte[residual];
        } else {
            for (int i = 0; i < baseLength; ++i) {
                base[i] = new byte[134217728];
            }
        }
        return base;
    }

    public static byte[][] wrap(byte[] array) {
        if (array.length == 0) {
            return EMPTY_BIG_ARRAY;
        }
        if (array.length <= 134217728) {
            return new byte[][]{array};
        }
        byte[][] bigArray = ByteBigArrays.newBigArray(array.length);
        for (int i = 0; i < bigArray.length; ++i) {
            System.arraycopy(array, (int)BigArrays.start(i), bigArray[i], 0, bigArray[i].length);
        }
        return bigArray;
    }

    public static byte[][] ensureCapacity(byte[][] array, long length) {
        return ByteBigArrays.ensureCapacity(array, length, ByteBigArrays.length(array));
    }

    public static byte[][] ensureCapacity(byte[][] array, long length, long preserve) {
        long oldLength = ByteBigArrays.length(array);
        if (length > oldLength) {
            BigArrays.ensureLength(length);
            int valid = array.length - (array.length == 0 || array.length > 0 && array[array.length - 1].length == 134217728 ? 0 : 1);
            int baseLength = (int)(length + 0x7FFFFFFL >>> 27);
            byte[][] base = (byte[][])Arrays.copyOf(array, baseLength);
            int residual = (int)(length & 0x7FFFFFFL);
            if (residual != 0) {
                for (int i = valid; i < baseLength - 1; ++i) {
                    base[i] = new byte[134217728];
                }
                base[baseLength - 1] = new byte[residual];
            } else {
                for (int i = valid; i < baseLength; ++i) {
                    base[i] = new byte[134217728];
                }
            }
            if (preserve - (long)valid * 0x8000000L > 0L) {
                ByteBigArrays.copy(array, (long)valid * 0x8000000L, base, (long)valid * 0x8000000L, preserve - (long)valid * 0x8000000L);
            }
            return base;
        }
        return array;
    }

    public static byte[][] grow(byte[][] array, long length) {
        long oldLength = ByteBigArrays.length(array);
        return length > oldLength ? ByteBigArrays.grow(array, length, oldLength) : array;
    }

    public static byte[][] grow(byte[][] array, long length, long preserve) {
        long oldLength = ByteBigArrays.length(array);
        return length > oldLength ? ByteBigArrays.ensureCapacity(array, Math.max(2L * oldLength, length), preserve) : array;
    }

    public static byte[][] trim(byte[][] array, long length) {
        BigArrays.ensureLength(length);
        long oldLength = ByteBigArrays.length(array);
        if (length >= oldLength) {
            return array;
        }
        int baseLength = (int)(length + 0x7FFFFFFL >>> 27);
        byte[][] base = (byte[][])Arrays.copyOf(array, baseLength);
        int residual = (int)(length & 0x7FFFFFFL);
        if (residual != 0) {
            base[baseLength - 1] = ByteArrays.trim(base[baseLength - 1], residual);
        }
        return base;
    }

    public static byte[][] setLength(byte[][] array, long length) {
        long oldLength = ByteBigArrays.length(array);
        if (length == oldLength) {
            return array;
        }
        if (length < oldLength) {
            return ByteBigArrays.trim(array, length);
        }
        return ByteBigArrays.ensureCapacity(array, length);
    }

    public static byte[][] copy(byte[][] array, long offset, long length) {
        ByteBigArrays.ensureOffsetLength(array, offset, length);
        byte[][] a = ByteBigArrays.newBigArray(length);
        ByteBigArrays.copy(array, offset, a, 0L, length);
        return a;
    }

    public static byte[][] copy(byte[][] array) {
        byte[][] base = (byte[][])array.clone();
        int i = base.length;
        while (i-- != 0) {
            base[i] = (byte[])array[i].clone();
        }
        return base;
    }

    public static void fill(byte[][] array, byte value) {
        int i = array.length;
        while (i-- != 0) {
            Arrays.fill(array[i], value);
        }
    }

    public static void fill(byte[][] array, long from, long to, byte value) {
        long length = ByteBigArrays.length(array);
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

    public static boolean equals(byte[][] a1, byte[][] a2) {
        if (ByteBigArrays.length(a1) != ByteBigArrays.length(a2)) {
            return false;
        }
        int i = a1.length;
        while (i-- != 0) {
            byte[] t = a1[i];
            byte[] u = a2[i];
            int j = t.length;
            while (j-- != 0) {
                if (t[j] == u[j]) continue;
                return false;
            }
        }
        return true;
    }

    public static String toString(byte[][] a) {
        if (a == null) {
            return "null";
        }
        long last = ByteBigArrays.length(a) - 1L;
        if (last == -1L) {
            return "[]";
        }
        StringBuilder b = new StringBuilder();
        b.append('[');
        long i = 0L;
        do {
            b.append(String.valueOf(ByteBigArrays.get(a, i)));
            if (i == last) {
                return b.append(']').toString();
            }
            b.append(", ");
            ++i;
        } while (true);
    }

    public static void ensureFromTo(byte[][] a, long from, long to) {
        BigArrays.ensureFromTo(ByteBigArrays.length(a), from, to);
    }

    public static void ensureOffsetLength(byte[][] a, long offset, long length) {
        BigArrays.ensureOffsetLength(ByteBigArrays.length(a), offset, length);
    }

    private static void vecSwap(byte[][] x, long a, long b, long n) {
        int i = 0;
        while ((long)i < n) {
            ByteBigArrays.swap(x, a, b);
            ++i;
            ++a;
            ++b;
        }
    }

    private static long med3(byte[][] x, long a, long b, long c, ByteComparator comp) {
        int ab = comp.compare(ByteBigArrays.get(x, a), ByteBigArrays.get(x, b));
        int ac = comp.compare(ByteBigArrays.get(x, a), ByteBigArrays.get(x, c));
        int bc = comp.compare(ByteBigArrays.get(x, b), ByteBigArrays.get(x, c));
        return ab < 0 ? (bc < 0 ? b : (ac < 0 ? c : a)) : (bc > 0 ? b : (ac > 0 ? c : a));
    }

    private static void selectionSort(byte[][] a, long from, long to, ByteComparator comp) {
        for (long i = from; i < to - 1L; ++i) {
            long m = i;
            for (long j = i + 1L; j < to; ++j) {
                if (comp.compare(ByteBigArrays.get(a, j), ByteBigArrays.get(a, m)) >= 0) continue;
                m = j;
            }
            if (m == i) continue;
            ByteBigArrays.swap(a, i, m);
        }
    }

    public static void quickSort(byte[][] x, long from, long to, ByteComparator comp) {
        long c;
        long a;
        long len = to - from;
        if (len < 7L) {
            ByteBigArrays.selectionSort(x, from, to, comp);
            return;
        }
        long m = from + len / 2L;
        if (len > 7L) {
            long l = from;
            long n = to - 1L;
            if (len > 40L) {
                long s = len / 8L;
                l = ByteBigArrays.med3(x, l, l + s, l + 2L * s, comp);
                m = ByteBigArrays.med3(x, m - s, m, m + s, comp);
                n = ByteBigArrays.med3(x, n - 2L * s, n - s, n, comp);
            }
            m = ByteBigArrays.med3(x, l, m, n, comp);
        }
        byte v = ByteBigArrays.get(x, m);
        long b = a = from;
        long d = c = to - 1L;
        do {
            int comparison;
            if (b <= c && (comparison = comp.compare(ByteBigArrays.get(x, b), v)) <= 0) {
                if (comparison == 0) {
                    ByteBigArrays.swap(x, a++, b);
                }
                ++b;
                continue;
            }
            while (c >= b && (comparison = comp.compare(ByteBigArrays.get(x, c), v)) >= 0) {
                if (comparison == 0) {
                    ByteBigArrays.swap(x, c, d--);
                }
                --c;
            }
            if (b > c) break;
            ByteBigArrays.swap(x, b++, c--);
        } while (true);
        long n = to;
        long s = Math.min(a - from, b - a);
        ByteBigArrays.vecSwap(x, from, b - s, s);
        s = Math.min(d - c, n - d - 1L);
        ByteBigArrays.vecSwap(x, b, n - s, s);
        s = b - a;
        if (s > 1L) {
            ByteBigArrays.quickSort(x, from, from + s, comp);
        }
        if ((s = d - c) > 1L) {
            ByteBigArrays.quickSort(x, n - s, n, comp);
        }
    }

    private static long med3(byte[][] x, long a, long b, long c) {
        int ab = Byte.compare(ByteBigArrays.get(x, a), ByteBigArrays.get(x, b));
        int ac = Byte.compare(ByteBigArrays.get(x, a), ByteBigArrays.get(x, c));
        int bc = Byte.compare(ByteBigArrays.get(x, b), ByteBigArrays.get(x, c));
        return ab < 0 ? (bc < 0 ? b : (ac < 0 ? c : a)) : (bc > 0 ? b : (ac > 0 ? c : a));
    }

    private static void selectionSort(byte[][] a, long from, long to) {
        for (long i = from; i < to - 1L; ++i) {
            long m = i;
            for (long j = i + 1L; j < to; ++j) {
                if (ByteBigArrays.get(a, j) >= ByteBigArrays.get(a, m)) continue;
                m = j;
            }
            if (m == i) continue;
            ByteBigArrays.swap(a, i, m);
        }
    }

    public static void quickSort(byte[][] x, ByteComparator comp) {
        ByteBigArrays.quickSort(x, 0L, ByteBigArrays.length(x), comp);
    }

    public static void quickSort(byte[][] x, long from, long to) {
        long a;
        long c;
        long len = to - from;
        if (len < 7L) {
            ByteBigArrays.selectionSort(x, from, to);
            return;
        }
        long m = from + len / 2L;
        if (len > 7L) {
            long l = from;
            long n = to - 1L;
            if (len > 40L) {
                long s = len / 8L;
                l = ByteBigArrays.med3(x, l, l + s, l + 2L * s);
                m = ByteBigArrays.med3(x, m - s, m, m + s);
                n = ByteBigArrays.med3(x, n - 2L * s, n - s, n);
            }
            m = ByteBigArrays.med3(x, l, m, n);
        }
        byte v = ByteBigArrays.get(x, m);
        long b = a = from;
        long d = c = to - 1L;
        do {
            int comparison;
            if (b <= c && (comparison = Byte.compare(ByteBigArrays.get(x, b), v)) <= 0) {
                if (comparison == 0) {
                    ByteBigArrays.swap(x, a++, b);
                }
                ++b;
                continue;
            }
            while (c >= b && (comparison = Byte.compare(ByteBigArrays.get(x, c), v)) >= 0) {
                if (comparison == 0) {
                    ByteBigArrays.swap(x, c, d--);
                }
                --c;
            }
            if (b > c) break;
            ByteBigArrays.swap(x, b++, c--);
        } while (true);
        long n = to;
        long s = Math.min(a - from, b - a);
        ByteBigArrays.vecSwap(x, from, b - s, s);
        s = Math.min(d - c, n - d - 1L);
        ByteBigArrays.vecSwap(x, b, n - s, s);
        s = b - a;
        if (s > 1L) {
            ByteBigArrays.quickSort(x, from, from + s);
        }
        if ((s = d - c) > 1L) {
            ByteBigArrays.quickSort(x, n - s, n);
        }
    }

    public static void quickSort(byte[][] x) {
        ByteBigArrays.quickSort(x, 0L, ByteBigArrays.length(x));
    }

    public static long binarySearch(byte[][] a, long from, long to, byte key) {
        --to;
        while (from <= to) {
            long mid = from + to >>> 1;
            byte midVal = ByteBigArrays.get(a, mid);
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

    public static long binarySearch(byte[][] a, byte key) {
        return ByteBigArrays.binarySearch(a, 0L, ByteBigArrays.length(a), key);
    }

    public static long binarySearch(byte[][] a, long from, long to, byte key, ByteComparator c) {
        --to;
        while (from <= to) {
            long mid = from + to >>> 1;
            byte midVal = ByteBigArrays.get(a, mid);
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

    public static long binarySearch(byte[][] a, byte key, ByteComparator c) {
        return ByteBigArrays.binarySearch(a, 0L, ByteBigArrays.length(a), key, c);
    }

    public static void radixSort(byte[][] a) {
        ByteBigArrays.radixSort(a, 0L, ByteBigArrays.length(a));
    }

    public static void radixSort(byte[][] a, long from, long to) {
        boolean maxLevel = false;
        boolean stackSize = true;
        long[] offsetStack = new long[1];
        int offsetPos = 0;
        long[] lengthStack = new long[1];
        int lengthPos = 0;
        int[] levelStack = new int[1];
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
            int n = signMask = (level = levelStack[--levelPos]) % 1 == 0 ? 128 : 0;
            if (length < 40L) {
                ByteBigArrays.selectionSort(a, first, first + length);
                continue;
            }
            int shift = (0 - level % 1) * 8;
            long i = length;
            while (i-- != 0L) {
                ByteBigArrays.set(digit, i, (byte)(ByteBigArrays.get(a, first + i) >>> shift & 255 ^ signMask));
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
                    if (level < 0 && count[i2] > 1L) {
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
                byte t = ByteBigArrays.get(a, i3 + first);
                c = ByteBigArrays.get(digit, i3) & 255;
                do {
                    long[] arrl = pos;
                    int n3 = c;
                    long l = arrl[n3] - 1L;
                    arrl[n3] = l;
                    long d = l;
                    if (l <= i3) break;
                    byte z = t;
                    int zz = c;
                    t = ByteBigArrays.get(a, d + first);
                    c = ByteBigArrays.get(digit, d) & 255;
                    ByteBigArrays.set(a, d + first, z);
                    ByteBigArrays.set(digit, d, (byte)zz);
                } while (true);
                ByteBigArrays.set(a, i3 + first, t);
                count[c] = 0L;
            }
        }
    }

    private static void selectionSort(byte[][] a, byte[][] b, long from, long to) {
        for (long i = from; i < to - 1L; ++i) {
            long m = i;
            for (long j = i + 1L; j < to; ++j) {
                if (ByteBigArrays.get(a, j) >= ByteBigArrays.get(a, m) && (ByteBigArrays.get(a, j) != ByteBigArrays.get(a, m) || ByteBigArrays.get(b, j) >= ByteBigArrays.get(b, m))) continue;
                m = j;
            }
            if (m == i) continue;
            byte t = ByteBigArrays.get(a, i);
            ByteBigArrays.set(a, i, ByteBigArrays.get(a, m));
            ByteBigArrays.set(a, m, t);
            t = ByteBigArrays.get(b, i);
            ByteBigArrays.set(b, i, ByteBigArrays.get(b, m));
            ByteBigArrays.set(b, m, t);
        }
    }

    public static void radixSort(byte[][] a, byte[][] b) {
        ByteBigArrays.radixSort(a, b, 0L, ByteBigArrays.length(a));
    }

    public static void radixSort(byte[][] a, byte[][] b, long from, long to) {
        int layers = 2;
        if (ByteBigArrays.length(a) != ByteBigArrays.length(b)) {
            throw new IllegalArgumentException("Array size mismatch.");
        }
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
            int level;
            int signMask;
            long first = offsetStack[--offsetPos];
            long length = lengthStack[--lengthPos];
            int n = signMask = (level = levelStack[--levelPos]) % 1 == 0 ? 128 : 0;
            if (length < 40L) {
                ByteBigArrays.selectionSort(a, b, first, first + length);
                continue;
            }
            byte[][] k = level < 1 ? a : b;
            int shift = (0 - level % 1) * 8;
            long i = length;
            while (i-- != 0L) {
                ByteBigArrays.set(digit, i, (byte)(ByteBigArrays.get(k, first + i) >>> shift & 255 ^ signMask));
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
                byte t = ByteBigArrays.get(a, i3 + first);
                byte u = ByteBigArrays.get(b, i3 + first);
                c = ByteBigArrays.get(digit, i3) & 255;
                do {
                    long[] arrl = pos;
                    int n3 = c;
                    long l = arrl[n3] - 1L;
                    arrl[n3] = l;
                    long d = l;
                    if (l <= i3) break;
                    byte z = t;
                    int zz = c;
                    t = ByteBigArrays.get(a, d + first);
                    ByteBigArrays.set(a, d + first, z);
                    z = u;
                    u = ByteBigArrays.get(b, d + first);
                    ByteBigArrays.set(b, d + first, z);
                    c = ByteBigArrays.get(digit, d) & 255;
                    ByteBigArrays.set(digit, d, (byte)zz);
                } while (true);
                ByteBigArrays.set(a, i3 + first, t);
                ByteBigArrays.set(b, i3 + first, u);
                count[c] = 0L;
            }
        }
    }

    public static byte[][] shuffle(byte[][] a, long from, long to, Random random) {
        long i = to - from;
        while (i-- != 0L) {
            long p = (random.nextLong() & Long.MAX_VALUE) % (i + 1L);
            byte t = ByteBigArrays.get(a, from + i);
            ByteBigArrays.set(a, from + i, ByteBigArrays.get(a, from + p));
            ByteBigArrays.set(a, from + p, t);
        }
        return a;
    }

    public static byte[][] shuffle(byte[][] a, Random random) {
        long i = ByteBigArrays.length(a);
        while (i-- != 0L) {
            long p = (random.nextLong() & Long.MAX_VALUE) % (i + 1L);
            byte t = ByteBigArrays.get(a, i);
            ByteBigArrays.set(a, i, ByteBigArrays.get(a, p));
            ByteBigArrays.set(a, p, t);
        }
        return a;
    }

    private static final class BigArrayHashStrategy
    implements Hash.Strategy<byte[][]>,
    Serializable {
        private static final long serialVersionUID = -7046029254386353129L;

        private BigArrayHashStrategy() {
        }

        @Override
        public int hashCode(byte[][] o) {
            return Arrays.deepHashCode((Object[])o);
        }

        @Override
        public boolean equals(byte[][] a, byte[][] b) {
            return ByteBigArrays.equals(a, b);
        }
    }

}

