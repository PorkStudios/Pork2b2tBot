/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.BigArrays;
import it.unimi.dsi.fastutil.Hash;
import it.unimi.dsi.fastutil.bytes.ByteBigArrays;
import it.unimi.dsi.fastutil.floats.FloatArrays;
import it.unimi.dsi.fastutil.floats.FloatComparator;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Random;

public final class FloatBigArrays {
    public static final float[][] EMPTY_BIG_ARRAY = new float[0][];
    public static final Hash.Strategy HASH_STRATEGY = new BigArrayHashStrategy();
    private static final int SMALL = 7;
    private static final int MEDIUM = 40;
    private static final int DIGIT_BITS = 8;
    private static final int DIGIT_MASK = 255;
    private static final int DIGITS_PER_ELEMENT = 4;

    private FloatBigArrays() {
    }

    public static float get(float[][] array, long index) {
        return array[BigArrays.segment(index)][BigArrays.displacement(index)];
    }

    public static void set(float[][] array, long index, float value) {
        array[BigArrays.segment((long)index)][BigArrays.displacement((long)index)] = value;
    }

    public static void swap(float[][] array, long first, long second) {
        float t = array[BigArrays.segment(first)][BigArrays.displacement(first)];
        array[BigArrays.segment((long)first)][BigArrays.displacement((long)first)] = array[BigArrays.segment(second)][BigArrays.displacement(second)];
        array[BigArrays.segment((long)second)][BigArrays.displacement((long)second)] = t;
    }

    public static void add(float[][] array, long index, float incr) {
        float[] arrf = array[BigArrays.segment(index)];
        int n = BigArrays.displacement(index);
        arrf[n] = arrf[n] + incr;
    }

    public static void mul(float[][] array, long index, float factor) {
        float[] arrf = array[BigArrays.segment(index)];
        int n = BigArrays.displacement(index);
        arrf[n] = arrf[n] * factor;
    }

    public static void incr(float[][] array, long index) {
        float[] arrf = array[BigArrays.segment(index)];
        int n = BigArrays.displacement(index);
        arrf[n] = arrf[n] + 1.0f;
    }

    public static void decr(float[][] array, long index) {
        float[] arrf = array[BigArrays.segment(index)];
        int n = BigArrays.displacement(index);
        arrf[n] = arrf[n] - 1.0f;
    }

    public static long length(float[][] array) {
        int length = array.length;
        return length == 0 ? 0L : BigArrays.start(length - 1) + (long)array[length - 1].length;
    }

    public static void copy(float[][] srcArray, long srcPos, float[][] destArray, long destPos, long length) {
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

    public static void copyFromBig(float[][] srcArray, long srcPos, float[] destArray, int destPos, int length) {
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

    public static void copyToBig(float[] srcArray, int srcPos, float[][] destArray, long destPos, long length) {
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

    public static float[][] newBigArray(long length) {
        if (length == 0L) {
            return EMPTY_BIG_ARRAY;
        }
        BigArrays.ensureLength(length);
        int baseLength = (int)(length + 0x7FFFFFFL >>> 27);
        float[][] base = new float[baseLength][];
        int residual = (int)(length & 0x7FFFFFFL);
        if (residual != 0) {
            for (int i = 0; i < baseLength - 1; ++i) {
                base[i] = new float[134217728];
            }
            base[baseLength - 1] = new float[residual];
        } else {
            for (int i = 0; i < baseLength; ++i) {
                base[i] = new float[134217728];
            }
        }
        return base;
    }

    public static float[][] wrap(float[] array) {
        if (array.length == 0) {
            return EMPTY_BIG_ARRAY;
        }
        if (array.length <= 134217728) {
            return new float[][]{array};
        }
        float[][] bigArray = FloatBigArrays.newBigArray(array.length);
        for (int i = 0; i < bigArray.length; ++i) {
            System.arraycopy(array, (int)BigArrays.start(i), bigArray[i], 0, bigArray[i].length);
        }
        return bigArray;
    }

    public static float[][] ensureCapacity(float[][] array, long length) {
        return FloatBigArrays.ensureCapacity(array, length, FloatBigArrays.length(array));
    }

    public static float[][] ensureCapacity(float[][] array, long length, long preserve) {
        long oldLength = FloatBigArrays.length(array);
        if (length > oldLength) {
            BigArrays.ensureLength(length);
            int valid = array.length - (array.length == 0 || array.length > 0 && array[array.length - 1].length == 134217728 ? 0 : 1);
            int baseLength = (int)(length + 0x7FFFFFFL >>> 27);
            float[][] base = (float[][])Arrays.copyOf(array, baseLength);
            int residual = (int)(length & 0x7FFFFFFL);
            if (residual != 0) {
                for (int i = valid; i < baseLength - 1; ++i) {
                    base[i] = new float[134217728];
                }
                base[baseLength - 1] = new float[residual];
            } else {
                for (int i = valid; i < baseLength; ++i) {
                    base[i] = new float[134217728];
                }
            }
            if (preserve - (long)valid * 0x8000000L > 0L) {
                FloatBigArrays.copy(array, (long)valid * 0x8000000L, base, (long)valid * 0x8000000L, preserve - (long)valid * 0x8000000L);
            }
            return base;
        }
        return array;
    }

    public static float[][] grow(float[][] array, long length) {
        long oldLength = FloatBigArrays.length(array);
        return length > oldLength ? FloatBigArrays.grow(array, length, oldLength) : array;
    }

    public static float[][] grow(float[][] array, long length, long preserve) {
        long oldLength = FloatBigArrays.length(array);
        return length > oldLength ? FloatBigArrays.ensureCapacity(array, Math.max(2L * oldLength, length), preserve) : array;
    }

    public static float[][] trim(float[][] array, long length) {
        BigArrays.ensureLength(length);
        long oldLength = FloatBigArrays.length(array);
        if (length >= oldLength) {
            return array;
        }
        int baseLength = (int)(length + 0x7FFFFFFL >>> 27);
        float[][] base = (float[][])Arrays.copyOf(array, baseLength);
        int residual = (int)(length & 0x7FFFFFFL);
        if (residual != 0) {
            base[baseLength - 1] = FloatArrays.trim(base[baseLength - 1], residual);
        }
        return base;
    }

    public static float[][] setLength(float[][] array, long length) {
        long oldLength = FloatBigArrays.length(array);
        if (length == oldLength) {
            return array;
        }
        if (length < oldLength) {
            return FloatBigArrays.trim(array, length);
        }
        return FloatBigArrays.ensureCapacity(array, length);
    }

    public static float[][] copy(float[][] array, long offset, long length) {
        FloatBigArrays.ensureOffsetLength(array, offset, length);
        float[][] a = FloatBigArrays.newBigArray(length);
        FloatBigArrays.copy(array, offset, a, 0L, length);
        return a;
    }

    public static float[][] copy(float[][] array) {
        float[][] base = (float[][])array.clone();
        int i = base.length;
        while (i-- != 0) {
            base[i] = (float[])array[i].clone();
        }
        return base;
    }

    public static void fill(float[][] array, float value) {
        int i = array.length;
        while (i-- != 0) {
            Arrays.fill(array[i], value);
        }
    }

    public static void fill(float[][] array, long from, long to, float value) {
        long length = FloatBigArrays.length(array);
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

    public static boolean equals(float[][] a1, float[][] a2) {
        if (FloatBigArrays.length(a1) != FloatBigArrays.length(a2)) {
            return false;
        }
        int i = a1.length;
        while (i-- != 0) {
            float[] t = a1[i];
            float[] u = a2[i];
            int j = t.length;
            while (j-- != 0) {
                if (Float.floatToIntBits(t[j]) == Float.floatToIntBits(u[j])) continue;
                return false;
            }
        }
        return true;
    }

    public static String toString(float[][] a) {
        if (a == null) {
            return "null";
        }
        long last = FloatBigArrays.length(a) - 1L;
        if (last == -1L) {
            return "[]";
        }
        StringBuilder b = new StringBuilder();
        b.append('[');
        long i = 0L;
        do {
            b.append(String.valueOf(FloatBigArrays.get(a, i)));
            if (i == last) {
                return b.append(']').toString();
            }
            b.append(", ");
            ++i;
        } while (true);
    }

    public static void ensureFromTo(float[][] a, long from, long to) {
        BigArrays.ensureFromTo(FloatBigArrays.length(a), from, to);
    }

    public static void ensureOffsetLength(float[][] a, long offset, long length) {
        BigArrays.ensureOffsetLength(FloatBigArrays.length(a), offset, length);
    }

    private static void vecSwap(float[][] x, long a, long b, long n) {
        int i = 0;
        while ((long)i < n) {
            FloatBigArrays.swap(x, a, b);
            ++i;
            ++a;
            ++b;
        }
    }

    private static long med3(float[][] x, long a, long b, long c, FloatComparator comp) {
        int ab = comp.compare(FloatBigArrays.get(x, a), FloatBigArrays.get(x, b));
        int ac = comp.compare(FloatBigArrays.get(x, a), FloatBigArrays.get(x, c));
        int bc = comp.compare(FloatBigArrays.get(x, b), FloatBigArrays.get(x, c));
        return ab < 0 ? (bc < 0 ? b : (ac < 0 ? c : a)) : (bc > 0 ? b : (ac > 0 ? c : a));
    }

    private static void selectionSort(float[][] a, long from, long to, FloatComparator comp) {
        for (long i = from; i < to - 1L; ++i) {
            long m = i;
            for (long j = i + 1L; j < to; ++j) {
                if (comp.compare(FloatBigArrays.get(a, j), FloatBigArrays.get(a, m)) >= 0) continue;
                m = j;
            }
            if (m == i) continue;
            FloatBigArrays.swap(a, i, m);
        }
    }

    public static void quickSort(float[][] x, long from, long to, FloatComparator comp) {
        long c;
        long a;
        long len = to - from;
        if (len < 7L) {
            FloatBigArrays.selectionSort(x, from, to, comp);
            return;
        }
        long m = from + len / 2L;
        if (len > 7L) {
            long l = from;
            long n = to - 1L;
            if (len > 40L) {
                long s = len / 8L;
                l = FloatBigArrays.med3(x, l, l + s, l + 2L * s, comp);
                m = FloatBigArrays.med3(x, m - s, m, m + s, comp);
                n = FloatBigArrays.med3(x, n - 2L * s, n - s, n, comp);
            }
            m = FloatBigArrays.med3(x, l, m, n, comp);
        }
        float v = FloatBigArrays.get(x, m);
        long b = a = from;
        long d = c = to - 1L;
        do {
            int comparison;
            if (b <= c && (comparison = comp.compare(FloatBigArrays.get(x, b), v)) <= 0) {
                if (comparison == 0) {
                    FloatBigArrays.swap(x, a++, b);
                }
                ++b;
                continue;
            }
            while (c >= b && (comparison = comp.compare(FloatBigArrays.get(x, c), v)) >= 0) {
                if (comparison == 0) {
                    FloatBigArrays.swap(x, c, d--);
                }
                --c;
            }
            if (b > c) break;
            FloatBigArrays.swap(x, b++, c--);
        } while (true);
        long n = to;
        long s = Math.min(a - from, b - a);
        FloatBigArrays.vecSwap(x, from, b - s, s);
        s = Math.min(d - c, n - d - 1L);
        FloatBigArrays.vecSwap(x, b, n - s, s);
        s = b - a;
        if (s > 1L) {
            FloatBigArrays.quickSort(x, from, from + s, comp);
        }
        if ((s = d - c) > 1L) {
            FloatBigArrays.quickSort(x, n - s, n, comp);
        }
    }

    private static long med3(float[][] x, long a, long b, long c) {
        int ab = Float.compare(FloatBigArrays.get(x, a), FloatBigArrays.get(x, b));
        int ac = Float.compare(FloatBigArrays.get(x, a), FloatBigArrays.get(x, c));
        int bc = Float.compare(FloatBigArrays.get(x, b), FloatBigArrays.get(x, c));
        return ab < 0 ? (bc < 0 ? b : (ac < 0 ? c : a)) : (bc > 0 ? b : (ac > 0 ? c : a));
    }

    private static void selectionSort(float[][] a, long from, long to) {
        for (long i = from; i < to - 1L; ++i) {
            long m = i;
            for (long j = i + 1L; j < to; ++j) {
                if (Float.compare(FloatBigArrays.get(a, j), FloatBigArrays.get(a, m)) >= 0) continue;
                m = j;
            }
            if (m == i) continue;
            FloatBigArrays.swap(a, i, m);
        }
    }

    public static void quickSort(float[][] x, FloatComparator comp) {
        FloatBigArrays.quickSort(x, 0L, FloatBigArrays.length(x), comp);
    }

    public static void quickSort(float[][] x, long from, long to) {
        long a;
        long c;
        long len = to - from;
        if (len < 7L) {
            FloatBigArrays.selectionSort(x, from, to);
            return;
        }
        long m = from + len / 2L;
        if (len > 7L) {
            long l = from;
            long n = to - 1L;
            if (len > 40L) {
                long s = len / 8L;
                l = FloatBigArrays.med3(x, l, l + s, l + 2L * s);
                m = FloatBigArrays.med3(x, m - s, m, m + s);
                n = FloatBigArrays.med3(x, n - 2L * s, n - s, n);
            }
            m = FloatBigArrays.med3(x, l, m, n);
        }
        float v = FloatBigArrays.get(x, m);
        long b = a = from;
        long d = c = to - 1L;
        do {
            int comparison;
            if (b <= c && (comparison = Float.compare(FloatBigArrays.get(x, b), v)) <= 0) {
                if (comparison == 0) {
                    FloatBigArrays.swap(x, a++, b);
                }
                ++b;
                continue;
            }
            while (c >= b && (comparison = Float.compare(FloatBigArrays.get(x, c), v)) >= 0) {
                if (comparison == 0) {
                    FloatBigArrays.swap(x, c, d--);
                }
                --c;
            }
            if (b > c) break;
            FloatBigArrays.swap(x, b++, c--);
        } while (true);
        long n = to;
        long s = Math.min(a - from, b - a);
        FloatBigArrays.vecSwap(x, from, b - s, s);
        s = Math.min(d - c, n - d - 1L);
        FloatBigArrays.vecSwap(x, b, n - s, s);
        s = b - a;
        if (s > 1L) {
            FloatBigArrays.quickSort(x, from, from + s);
        }
        if ((s = d - c) > 1L) {
            FloatBigArrays.quickSort(x, n - s, n);
        }
    }

    public static void quickSort(float[][] x) {
        FloatBigArrays.quickSort(x, 0L, FloatBigArrays.length(x));
    }

    public static long binarySearch(float[][] a, long from, long to, float key) {
        --to;
        while (from <= to) {
            long mid = from + to >>> 1;
            float midVal = FloatBigArrays.get(a, mid);
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

    public static long binarySearch(float[][] a, float key) {
        return FloatBigArrays.binarySearch(a, 0L, FloatBigArrays.length(a), key);
    }

    public static long binarySearch(float[][] a, long from, long to, float key, FloatComparator c) {
        --to;
        while (from <= to) {
            long mid = from + to >>> 1;
            float midVal = FloatBigArrays.get(a, mid);
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

    public static long binarySearch(float[][] a, float key, FloatComparator c) {
        return FloatBigArrays.binarySearch(a, 0L, FloatBigArrays.length(a), key, c);
    }

    private static final long fixFloat(float f) {
        long i = Float.floatToRawIntBits(f);
        return i >= 0L ? i : i ^ Integer.MAX_VALUE;
    }

    public static void radixSort(float[][] a) {
        FloatBigArrays.radixSort(a, 0L, FloatBigArrays.length(a));
    }

    public static void radixSort(float[][] a, long from, long to) {
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
                FloatBigArrays.selectionSort(a, first, first + length);
                continue;
            }
            int shift = (3 - level % 4) * 8;
            long i = length;
            while (i-- != 0L) {
                ByteBigArrays.set(digit, i, (byte)(FloatBigArrays.fixFloat(FloatBigArrays.get(a, first + i)) >>> shift & 255L ^ (long)signMask));
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
                float t = FloatBigArrays.get(a, i3 + first);
                c = ByteBigArrays.get(digit, i3) & 255;
                do {
                    long[] arrl = pos;
                    int n3 = c;
                    long l = arrl[n3] - 1L;
                    arrl[n3] = l;
                    long d = l;
                    if (l <= i3) break;
                    float z = t;
                    int zz = c;
                    t = FloatBigArrays.get(a, d + first);
                    c = ByteBigArrays.get(digit, d) & 255;
                    FloatBigArrays.set(a, d + first, z);
                    ByteBigArrays.set(digit, d, (byte)zz);
                } while (true);
                FloatBigArrays.set(a, i3 + first, t);
                count[c] = 0L;
            }
        }
    }

    private static void selectionSort(float[][] a, float[][] b, long from, long to) {
        for (long i = from; i < to - 1L; ++i) {
            long m = i;
            for (long j = i + 1L; j < to; ++j) {
                if (Float.compare(FloatBigArrays.get(a, j), FloatBigArrays.get(a, m)) >= 0 && (Float.compare(FloatBigArrays.get(a, j), FloatBigArrays.get(a, m)) != 0 || Float.compare(FloatBigArrays.get(b, j), FloatBigArrays.get(b, m)) >= 0)) continue;
                m = j;
            }
            if (m == i) continue;
            float t = FloatBigArrays.get(a, i);
            FloatBigArrays.set(a, i, FloatBigArrays.get(a, m));
            FloatBigArrays.set(a, m, t);
            t = FloatBigArrays.get(b, i);
            FloatBigArrays.set(b, i, FloatBigArrays.get(b, m));
            FloatBigArrays.set(b, m, t);
        }
    }

    public static void radixSort(float[][] a, float[][] b) {
        FloatBigArrays.radixSort(a, b, 0L, FloatBigArrays.length(a));
    }

    public static void radixSort(float[][] a, float[][] b, long from, long to) {
        int layers = 2;
        if (FloatBigArrays.length(a) != FloatBigArrays.length(b)) {
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
                FloatBigArrays.selectionSort(a, b, first, first + length);
                continue;
            }
            float[][] k = level < 4 ? a : b;
            int shift = (3 - level % 4) * 8;
            long i = length;
            while (i-- != 0L) {
                ByteBigArrays.set(digit, i, (byte)(FloatBigArrays.fixFloat(FloatBigArrays.get(k, first + i)) >>> shift & 255L ^ (long)signMask));
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
                float t = FloatBigArrays.get(a, i3 + first);
                float u = FloatBigArrays.get(b, i3 + first);
                c = ByteBigArrays.get(digit, i3) & 255;
                do {
                    long[] arrl = pos;
                    int n3 = c;
                    long l = arrl[n3] - 1L;
                    arrl[n3] = l;
                    long d = l;
                    if (l <= i3) break;
                    float z = t;
                    int zz = c;
                    t = FloatBigArrays.get(a, d + first);
                    FloatBigArrays.set(a, d + first, z);
                    z = u;
                    u = FloatBigArrays.get(b, d + first);
                    FloatBigArrays.set(b, d + first, z);
                    c = ByteBigArrays.get(digit, d) & 255;
                    ByteBigArrays.set(digit, d, (byte)zz);
                } while (true);
                FloatBigArrays.set(a, i3 + first, t);
                FloatBigArrays.set(b, i3 + first, u);
                count[c] = 0L;
            }
        }
    }

    public static float[][] shuffle(float[][] a, long from, long to, Random random) {
        long i = to - from;
        while (i-- != 0L) {
            long p = (random.nextLong() & Long.MAX_VALUE) % (i + 1L);
            float t = FloatBigArrays.get(a, from + i);
            FloatBigArrays.set(a, from + i, FloatBigArrays.get(a, from + p));
            FloatBigArrays.set(a, from + p, t);
        }
        return a;
    }

    public static float[][] shuffle(float[][] a, Random random) {
        long i = FloatBigArrays.length(a);
        while (i-- != 0L) {
            long p = (random.nextLong() & Long.MAX_VALUE) % (i + 1L);
            float t = FloatBigArrays.get(a, i);
            FloatBigArrays.set(a, i, FloatBigArrays.get(a, p));
            FloatBigArrays.set(a, p, t);
        }
        return a;
    }

    private static final class BigArrayHashStrategy
    implements Hash.Strategy<float[][]>,
    Serializable {
        private static final long serialVersionUID = -7046029254386353129L;

        private BigArrayHashStrategy() {
        }

        @Override
        public int hashCode(float[][] o) {
            return Arrays.deepHashCode((Object[])o);
        }

        @Override
        public boolean equals(float[][] a, float[][] b) {
            return FloatBigArrays.equals(a, b);
        }
    }

}

