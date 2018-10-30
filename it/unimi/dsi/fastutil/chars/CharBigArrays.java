/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.BigArrays;
import it.unimi.dsi.fastutil.Hash;
import it.unimi.dsi.fastutil.bytes.ByteBigArrays;
import it.unimi.dsi.fastutil.chars.CharArrays;
import it.unimi.dsi.fastutil.chars.CharComparator;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Random;

public final class CharBigArrays {
    public static final char[][] EMPTY_BIG_ARRAY = new char[0][];
    public static final Hash.Strategy HASH_STRATEGY = new BigArrayHashStrategy();
    private static final int SMALL = 7;
    private static final int MEDIUM = 40;
    private static final int DIGIT_BITS = 8;
    private static final int DIGIT_MASK = 255;
    private static final int DIGITS_PER_ELEMENT = 2;

    private CharBigArrays() {
    }

    public static char get(char[][] array, long index) {
        return array[BigArrays.segment(index)][BigArrays.displacement(index)];
    }

    public static void set(char[][] array, long index, char value) {
        array[BigArrays.segment((long)index)][BigArrays.displacement((long)index)] = value;
    }

    public static void swap(char[][] array, long first, long second) {
        char t = array[BigArrays.segment(first)][BigArrays.displacement(first)];
        array[BigArrays.segment((long)first)][BigArrays.displacement((long)first)] = array[BigArrays.segment(second)][BigArrays.displacement(second)];
        array[BigArrays.segment((long)second)][BigArrays.displacement((long)second)] = t;
    }

    public static void add(char[][] array, long index, char incr) {
        char[] arrc = array[BigArrays.segment(index)];
        int n = BigArrays.displacement(index);
        arrc[n] = (char)(arrc[n] + incr);
    }

    public static void mul(char[][] array, long index, char factor) {
        char[] arrc = array[BigArrays.segment(index)];
        int n = BigArrays.displacement(index);
        arrc[n] = (char)(arrc[n] * factor);
    }

    public static void incr(char[][] array, long index) {
        char[] arrc = array[BigArrays.segment(index)];
        int n = BigArrays.displacement(index);
        arrc[n] = (char)(arrc[n] + '\u0001');
    }

    public static void decr(char[][] array, long index) {
        char[] arrc = array[BigArrays.segment(index)];
        int n = BigArrays.displacement(index);
        arrc[n] = (char)(arrc[n] - '\u0001');
    }

    public static long length(char[][] array) {
        int length = array.length;
        return length == 0 ? 0L : BigArrays.start(length - 1) + (long)array[length - 1].length;
    }

    public static void copy(char[][] srcArray, long srcPos, char[][] destArray, long destPos, long length) {
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

    public static void copyFromBig(char[][] srcArray, long srcPos, char[] destArray, int destPos, int length) {
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

    public static void copyToBig(char[] srcArray, int srcPos, char[][] destArray, long destPos, long length) {
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

    public static char[][] newBigArray(long length) {
        if (length == 0L) {
            return EMPTY_BIG_ARRAY;
        }
        BigArrays.ensureLength(length);
        int baseLength = (int)(length + 0x7FFFFFFL >>> 27);
        char[][] base = new char[baseLength][];
        int residual = (int)(length & 0x7FFFFFFL);
        if (residual != 0) {
            for (int i = 0; i < baseLength - 1; ++i) {
                base[i] = new char[134217728];
            }
            base[baseLength - 1] = new char[residual];
        } else {
            for (int i = 0; i < baseLength; ++i) {
                base[i] = new char[134217728];
            }
        }
        return base;
    }

    public static char[][] wrap(char[] array) {
        if (array.length == 0) {
            return EMPTY_BIG_ARRAY;
        }
        if (array.length <= 134217728) {
            return new char[][]{array};
        }
        char[][] bigArray = CharBigArrays.newBigArray(array.length);
        for (int i = 0; i < bigArray.length; ++i) {
            System.arraycopy(array, (int)BigArrays.start(i), bigArray[i], 0, bigArray[i].length);
        }
        return bigArray;
    }

    public static char[][] ensureCapacity(char[][] array, long length) {
        return CharBigArrays.ensureCapacity(array, length, CharBigArrays.length(array));
    }

    public static char[][] ensureCapacity(char[][] array, long length, long preserve) {
        long oldLength = CharBigArrays.length(array);
        if (length > oldLength) {
            BigArrays.ensureLength(length);
            int valid = array.length - (array.length == 0 || array.length > 0 && array[array.length - 1].length == 134217728 ? 0 : 1);
            int baseLength = (int)(length + 0x7FFFFFFL >>> 27);
            char[][] base = (char[][])Arrays.copyOf(array, baseLength);
            int residual = (int)(length & 0x7FFFFFFL);
            if (residual != 0) {
                for (int i = valid; i < baseLength - 1; ++i) {
                    base[i] = new char[134217728];
                }
                base[baseLength - 1] = new char[residual];
            } else {
                for (int i = valid; i < baseLength; ++i) {
                    base[i] = new char[134217728];
                }
            }
            if (preserve - (long)valid * 0x8000000L > 0L) {
                CharBigArrays.copy(array, (long)valid * 0x8000000L, base, (long)valid * 0x8000000L, preserve - (long)valid * 0x8000000L);
            }
            return base;
        }
        return array;
    }

    public static char[][] grow(char[][] array, long length) {
        long oldLength = CharBigArrays.length(array);
        return length > oldLength ? CharBigArrays.grow(array, length, oldLength) : array;
    }

    public static char[][] grow(char[][] array, long length, long preserve) {
        long oldLength = CharBigArrays.length(array);
        return length > oldLength ? CharBigArrays.ensureCapacity(array, Math.max(2L * oldLength, length), preserve) : array;
    }

    public static char[][] trim(char[][] array, long length) {
        BigArrays.ensureLength(length);
        long oldLength = CharBigArrays.length(array);
        if (length >= oldLength) {
            return array;
        }
        int baseLength = (int)(length + 0x7FFFFFFL >>> 27);
        char[][] base = (char[][])Arrays.copyOf(array, baseLength);
        int residual = (int)(length & 0x7FFFFFFL);
        if (residual != 0) {
            base[baseLength - 1] = CharArrays.trim(base[baseLength - 1], residual);
        }
        return base;
    }

    public static char[][] setLength(char[][] array, long length) {
        long oldLength = CharBigArrays.length(array);
        if (length == oldLength) {
            return array;
        }
        if (length < oldLength) {
            return CharBigArrays.trim(array, length);
        }
        return CharBigArrays.ensureCapacity(array, length);
    }

    public static char[][] copy(char[][] array, long offset, long length) {
        CharBigArrays.ensureOffsetLength(array, offset, length);
        char[][] a = CharBigArrays.newBigArray(length);
        CharBigArrays.copy(array, offset, a, 0L, length);
        return a;
    }

    public static char[][] copy(char[][] array) {
        char[][] base = (char[][])array.clone();
        int i = base.length;
        while (i-- != 0) {
            base[i] = (char[])array[i].clone();
        }
        return base;
    }

    public static void fill(char[][] array, char value) {
        int i = array.length;
        while (i-- != 0) {
            Arrays.fill(array[i], value);
        }
    }

    public static void fill(char[][] array, long from, long to, char value) {
        long length = CharBigArrays.length(array);
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

    public static boolean equals(char[][] a1, char[][] a2) {
        if (CharBigArrays.length(a1) != CharBigArrays.length(a2)) {
            return false;
        }
        int i = a1.length;
        while (i-- != 0) {
            char[] t = a1[i];
            char[] u = a2[i];
            int j = t.length;
            while (j-- != 0) {
                if (t[j] == u[j]) continue;
                return false;
            }
        }
        return true;
    }

    public static String toString(char[][] a) {
        if (a == null) {
            return "null";
        }
        long last = CharBigArrays.length(a) - 1L;
        if (last == -1L) {
            return "[]";
        }
        StringBuilder b = new StringBuilder();
        b.append('[');
        long i = 0L;
        do {
            b.append(String.valueOf(CharBigArrays.get(a, i)));
            if (i == last) {
                return b.append(']').toString();
            }
            b.append(", ");
            ++i;
        } while (true);
    }

    public static void ensureFromTo(char[][] a, long from, long to) {
        BigArrays.ensureFromTo(CharBigArrays.length(a), from, to);
    }

    public static void ensureOffsetLength(char[][] a, long offset, long length) {
        BigArrays.ensureOffsetLength(CharBigArrays.length(a), offset, length);
    }

    private static void vecSwap(char[][] x, long a, long b, long n) {
        int i = 0;
        while ((long)i < n) {
            CharBigArrays.swap(x, a, b);
            ++i;
            ++a;
            ++b;
        }
    }

    private static long med3(char[][] x, long a, long b, long c, CharComparator comp) {
        int ab = comp.compare(CharBigArrays.get(x, a), CharBigArrays.get(x, b));
        int ac = comp.compare(CharBigArrays.get(x, a), CharBigArrays.get(x, c));
        int bc = comp.compare(CharBigArrays.get(x, b), CharBigArrays.get(x, c));
        return ab < 0 ? (bc < 0 ? b : (ac < 0 ? c : a)) : (bc > 0 ? b : (ac > 0 ? c : a));
    }

    private static void selectionSort(char[][] a, long from, long to, CharComparator comp) {
        for (long i = from; i < to - 1L; ++i) {
            long m = i;
            for (long j = i + 1L; j < to; ++j) {
                if (comp.compare(CharBigArrays.get(a, j), CharBigArrays.get(a, m)) >= 0) continue;
                m = j;
            }
            if (m == i) continue;
            CharBigArrays.swap(a, i, m);
        }
    }

    public static void quickSort(char[][] x, long from, long to, CharComparator comp) {
        long c;
        long a;
        long len = to - from;
        if (len < 7L) {
            CharBigArrays.selectionSort(x, from, to, comp);
            return;
        }
        long m = from + len / 2L;
        if (len > 7L) {
            long l = from;
            long n = to - 1L;
            if (len > 40L) {
                long s = len / 8L;
                l = CharBigArrays.med3(x, l, l + s, l + 2L * s, comp);
                m = CharBigArrays.med3(x, m - s, m, m + s, comp);
                n = CharBigArrays.med3(x, n - 2L * s, n - s, n, comp);
            }
            m = CharBigArrays.med3(x, l, m, n, comp);
        }
        char v = CharBigArrays.get(x, m);
        long b = a = from;
        long d = c = to - 1L;
        do {
            int comparison;
            if (b <= c && (comparison = comp.compare(CharBigArrays.get(x, b), v)) <= 0) {
                if (comparison == 0) {
                    CharBigArrays.swap(x, a++, b);
                }
                ++b;
                continue;
            }
            while (c >= b && (comparison = comp.compare(CharBigArrays.get(x, c), v)) >= 0) {
                if (comparison == 0) {
                    CharBigArrays.swap(x, c, d--);
                }
                --c;
            }
            if (b > c) break;
            CharBigArrays.swap(x, b++, c--);
        } while (true);
        long n = to;
        long s = Math.min(a - from, b - a);
        CharBigArrays.vecSwap(x, from, b - s, s);
        s = Math.min(d - c, n - d - 1L);
        CharBigArrays.vecSwap(x, b, n - s, s);
        s = b - a;
        if (s > 1L) {
            CharBigArrays.quickSort(x, from, from + s, comp);
        }
        if ((s = d - c) > 1L) {
            CharBigArrays.quickSort(x, n - s, n, comp);
        }
    }

    private static long med3(char[][] x, long a, long b, long c) {
        int ab = Character.compare(CharBigArrays.get(x, a), CharBigArrays.get(x, b));
        int ac = Character.compare(CharBigArrays.get(x, a), CharBigArrays.get(x, c));
        int bc = Character.compare(CharBigArrays.get(x, b), CharBigArrays.get(x, c));
        return ab < 0 ? (bc < 0 ? b : (ac < 0 ? c : a)) : (bc > 0 ? b : (ac > 0 ? c : a));
    }

    private static void selectionSort(char[][] a, long from, long to) {
        for (long i = from; i < to - 1L; ++i) {
            long m = i;
            for (long j = i + 1L; j < to; ++j) {
                if (CharBigArrays.get(a, j) >= CharBigArrays.get(a, m)) continue;
                m = j;
            }
            if (m == i) continue;
            CharBigArrays.swap(a, i, m);
        }
    }

    public static void quickSort(char[][] x, CharComparator comp) {
        CharBigArrays.quickSort(x, 0L, CharBigArrays.length(x), comp);
    }

    public static void quickSort(char[][] x, long from, long to) {
        long a;
        long c;
        long len = to - from;
        if (len < 7L) {
            CharBigArrays.selectionSort(x, from, to);
            return;
        }
        long m = from + len / 2L;
        if (len > 7L) {
            long l = from;
            long n = to - 1L;
            if (len > 40L) {
                long s = len / 8L;
                l = CharBigArrays.med3(x, l, l + s, l + 2L * s);
                m = CharBigArrays.med3(x, m - s, m, m + s);
                n = CharBigArrays.med3(x, n - 2L * s, n - s, n);
            }
            m = CharBigArrays.med3(x, l, m, n);
        }
        char v = CharBigArrays.get(x, m);
        long b = a = from;
        long d = c = to - 1L;
        do {
            int comparison;
            if (b <= c && (comparison = Character.compare(CharBigArrays.get(x, b), v)) <= 0) {
                if (comparison == 0) {
                    CharBigArrays.swap(x, a++, b);
                }
                ++b;
                continue;
            }
            while (c >= b && (comparison = Character.compare(CharBigArrays.get(x, c), v)) >= 0) {
                if (comparison == 0) {
                    CharBigArrays.swap(x, c, d--);
                }
                --c;
            }
            if (b > c) break;
            CharBigArrays.swap(x, b++, c--);
        } while (true);
        long n = to;
        long s = Math.min(a - from, b - a);
        CharBigArrays.vecSwap(x, from, b - s, s);
        s = Math.min(d - c, n - d - 1L);
        CharBigArrays.vecSwap(x, b, n - s, s);
        s = b - a;
        if (s > 1L) {
            CharBigArrays.quickSort(x, from, from + s);
        }
        if ((s = d - c) > 1L) {
            CharBigArrays.quickSort(x, n - s, n);
        }
    }

    public static void quickSort(char[][] x) {
        CharBigArrays.quickSort(x, 0L, CharBigArrays.length(x));
    }

    public static long binarySearch(char[][] a, long from, long to, char key) {
        --to;
        while (from <= to) {
            long mid = from + to >>> 1;
            char midVal = CharBigArrays.get(a, mid);
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

    public static long binarySearch(char[][] a, char key) {
        return CharBigArrays.binarySearch(a, 0L, CharBigArrays.length(a), key);
    }

    public static long binarySearch(char[][] a, long from, long to, char key, CharComparator c) {
        --to;
        while (from <= to) {
            long mid = from + to >>> 1;
            char midVal = CharBigArrays.get(a, mid);
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

    public static long binarySearch(char[][] a, char key, CharComparator c) {
        return CharBigArrays.binarySearch(a, 0L, CharBigArrays.length(a), key, c);
    }

    public static void radixSort(char[][] a) {
        CharBigArrays.radixSort(a, 0L, CharBigArrays.length(a));
    }

    public static void radixSort(char[][] a, long from, long to) {
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
            long first = offsetStack[--offsetPos];
            long length = lengthStack[--lengthPos];
            int level = levelStack[--levelPos];
            boolean signMask = false;
            if (length < 40L) {
                CharBigArrays.selectionSort(a, first, first + length);
                continue;
            }
            int shift = (1 - level % 2) * 8;
            long i = length;
            while (i-- != 0L) {
                ByteBigArrays.set(digit, i, (byte)(CharBigArrays.get(a, first + i) >>> shift & 255 ^ 0));
            }
            i = length;
            while (i-- != 0L) {
                long[] arrl = count;
                int n = ByteBigArrays.get(digit, i) & 255;
                arrl[n] = arrl[n] + 1L;
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
                char t = CharBigArrays.get(a, i3 + first);
                c = ByteBigArrays.get(digit, i3) & 255;
                do {
                    long[] arrl = pos;
                    int n = c;
                    long l = arrl[n] - 1L;
                    arrl[n] = l;
                    long d = l;
                    if (l <= i3) break;
                    char z = t;
                    int zz = c;
                    t = CharBigArrays.get(a, d + first);
                    c = ByteBigArrays.get(digit, d) & 255;
                    CharBigArrays.set(a, d + first, z);
                    ByteBigArrays.set(digit, d, (byte)zz);
                } while (true);
                CharBigArrays.set(a, i3 + first, t);
                count[c] = 0L;
            }
        }
    }

    private static void selectionSort(char[][] a, char[][] b, long from, long to) {
        for (long i = from; i < to - 1L; ++i) {
            long m = i;
            for (long j = i + 1L; j < to; ++j) {
                if (CharBigArrays.get(a, j) >= CharBigArrays.get(a, m) && (CharBigArrays.get(a, j) != CharBigArrays.get(a, m) || CharBigArrays.get(b, j) >= CharBigArrays.get(b, m))) continue;
                m = j;
            }
            if (m == i) continue;
            char t = CharBigArrays.get(a, i);
            CharBigArrays.set(a, i, CharBigArrays.get(a, m));
            CharBigArrays.set(a, m, t);
            t = CharBigArrays.get(b, i);
            CharBigArrays.set(b, i, CharBigArrays.get(b, m));
            CharBigArrays.set(b, m, t);
        }
    }

    public static void radixSort(char[][] a, char[][] b) {
        CharBigArrays.radixSort(a, b, 0L, CharBigArrays.length(a));
    }

    public static void radixSort(char[][] a, char[][] b, long from, long to) {
        int layers = 2;
        if (CharBigArrays.length(a) != CharBigArrays.length(b)) {
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
            long first = offsetStack[--offsetPos];
            long length = lengthStack[--lengthPos];
            int level = levelStack[--levelPos];
            boolean signMask = false;
            if (length < 40L) {
                CharBigArrays.selectionSort(a, b, first, first + length);
                continue;
            }
            char[][] k = level < 2 ? a : b;
            int shift = (1 - level % 2) * 8;
            long i = length;
            while (i-- != 0L) {
                ByteBigArrays.set(digit, i, (byte)(CharBigArrays.get(k, first + i) >>> shift & 255 ^ 0));
            }
            i = length;
            while (i-- != 0L) {
                long[] arrl = count;
                int n = ByteBigArrays.get(digit, i) & 255;
                arrl[n] = arrl[n] + 1L;
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
                char t = CharBigArrays.get(a, i3 + first);
                char u = CharBigArrays.get(b, i3 + first);
                c = ByteBigArrays.get(digit, i3) & 255;
                do {
                    long[] arrl = pos;
                    int n = c;
                    long l = arrl[n] - 1L;
                    arrl[n] = l;
                    long d = l;
                    if (l <= i3) break;
                    char z = t;
                    int zz = c;
                    t = CharBigArrays.get(a, d + first);
                    CharBigArrays.set(a, d + first, z);
                    z = u;
                    u = CharBigArrays.get(b, d + first);
                    CharBigArrays.set(b, d + first, z);
                    c = ByteBigArrays.get(digit, d) & 255;
                    ByteBigArrays.set(digit, d, (byte)zz);
                } while (true);
                CharBigArrays.set(a, i3 + first, t);
                CharBigArrays.set(b, i3 + first, u);
                count[c] = 0L;
            }
        }
    }

    public static char[][] shuffle(char[][] a, long from, long to, Random random) {
        long i = to - from;
        while (i-- != 0L) {
            long p = (random.nextLong() & Long.MAX_VALUE) % (i + 1L);
            char t = CharBigArrays.get(a, from + i);
            CharBigArrays.set(a, from + i, CharBigArrays.get(a, from + p));
            CharBigArrays.set(a, from + p, t);
        }
        return a;
    }

    public static char[][] shuffle(char[][] a, Random random) {
        long i = CharBigArrays.length(a);
        while (i-- != 0L) {
            long p = (random.nextLong() & Long.MAX_VALUE) % (i + 1L);
            char t = CharBigArrays.get(a, i);
            CharBigArrays.set(a, i, CharBigArrays.get(a, p));
            CharBigArrays.set(a, p, t);
        }
        return a;
    }

    private static final class BigArrayHashStrategy
    implements Hash.Strategy<char[][]>,
    Serializable {
        private static final long serialVersionUID = -7046029254386353129L;

        private BigArrayHashStrategy() {
        }

        @Override
        public int hashCode(char[][] o) {
            return Arrays.deepHashCode((Object[])o);
        }

        @Override
        public boolean equals(char[][] a, char[][] b) {
            return CharBigArrays.equals(a, b);
        }
    }

}

