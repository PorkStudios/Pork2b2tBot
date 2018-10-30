/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.BigArrays;
import it.unimi.dsi.fastutil.Hash;
import it.unimi.dsi.fastutil.objects.ObjectArrays;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Objects;
import java.util.Random;

public final class ObjectBigArrays {
    public static final Object[][] EMPTY_BIG_ARRAY = new Object[0][];
    public static final Hash.Strategy HASH_STRATEGY = new BigArrayHashStrategy();
    private static final int SMALL = 7;
    private static final int MEDIUM = 40;

    private ObjectBigArrays() {
    }

    public static <K> K get(K[][] array, long index) {
        return array[BigArrays.segment(index)][BigArrays.displacement(index)];
    }

    public static <K> void set(K[][] array, long index, K value) {
        array[BigArrays.segment((long)index)][BigArrays.displacement((long)index)] = value;
    }

    public static <K> void swap(K[][] array, long first, long second) {
        K t = array[BigArrays.segment(first)][BigArrays.displacement(first)];
        array[BigArrays.segment((long)first)][BigArrays.displacement((long)first)] = array[BigArrays.segment(second)][BigArrays.displacement(second)];
        array[BigArrays.segment((long)second)][BigArrays.displacement((long)second)] = t;
    }

    public static <K> long length(K[][] array) {
        int length = array.length;
        return length == 0 ? 0L : BigArrays.start(length - 1) + (long)array[length - 1].length;
    }

    public static <K> void copy(K[][] srcArray, long srcPos, K[][] destArray, long destPos, long length) {
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

    public static <K> void copyFromBig(K[][] srcArray, long srcPos, K[] destArray, int destPos, int length) {
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

    public static <K> void copyToBig(K[] srcArray, int srcPos, K[][] destArray, long destPos, long length) {
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

    public static <K> K[][] newBigArray(K[][] prototype, long length) {
        return ObjectBigArrays.newBigArray(prototype.getClass().getComponentType(), length);
    }

    private static Object[][] newBigArray(Class<?> componentType, long length) {
        if (length == 0L && componentType == Object[].class) {
            return EMPTY_BIG_ARRAY;
        }
        BigArrays.ensureLength(length);
        int baseLength = (int)(length + 0x7FFFFFFL >>> 27);
        Object[][] base = (Object[][])Array.newInstance(componentType, baseLength);
        int residual = (int)(length & 0x7FFFFFFL);
        if (residual != 0) {
            for (int i = 0; i < baseLength - 1; ++i) {
                base[i] = (Object[])Array.newInstance(componentType.getComponentType(), 134217728);
            }
            base[baseLength - 1] = (Object[])Array.newInstance(componentType.getComponentType(), residual);
        } else {
            for (int i = 0; i < baseLength; ++i) {
                base[i] = (Object[])Array.newInstance(componentType.getComponentType(), 134217728);
            }
        }
        return base;
    }

    public static Object[][] newBigArray(long length) {
        if (length == 0L) {
            return EMPTY_BIG_ARRAY;
        }
        BigArrays.ensureLength(length);
        int baseLength = (int)(length + 0x7FFFFFFL >>> 27);
        Object[][] base = new Object[baseLength][];
        int residual = (int)(length & 0x7FFFFFFL);
        if (residual != 0) {
            for (int i = 0; i < baseLength - 1; ++i) {
                base[i] = new Object[134217728];
            }
            base[baseLength - 1] = new Object[residual];
        } else {
            for (int i = 0; i < baseLength; ++i) {
                base[i] = new Object[134217728];
            }
        }
        return base;
    }

    public static <K> K[][] wrap(K[] array) {
        if (array.length == 0 && array.getClass() == Object[].class) {
            return EMPTY_BIG_ARRAY;
        }
        if (array.length <= 134217728) {
            Object[][] bigArray = (Object[][])Array.newInstance(array.getClass(), 1);
            bigArray[0] = array;
            return bigArray;
        }
        Object[][] bigArray = ObjectBigArrays.newBigArray(array.getClass(), (long)array.length);
        for (int i = 0; i < bigArray.length; ++i) {
            System.arraycopy(array, (int)BigArrays.start(i), bigArray[i], 0, bigArray[i].length);
        }
        return bigArray;
    }

    public static <K> K[][] ensureCapacity(K[][] array, long length) {
        return ObjectBigArrays.ensureCapacity(array, length, ObjectBigArrays.length(array));
    }

    public static <K> K[][] ensureCapacity(K[][] array, long length, long preserve) {
        long oldLength = ObjectBigArrays.length(array);
        if (length > oldLength) {
            BigArrays.ensureLength(length);
            int valid = array.length - (array.length == 0 || array.length > 0 && array[array.length - 1].length == 134217728 ? 0 : 1);
            int baseLength = (int)(length + 0x7FFFFFFL >>> 27);
            Object[][] base = (Object[][])Arrays.copyOf(array, baseLength);
            Class<?> componentType = array.getClass().getComponentType();
            int residual = (int)(length & 0x7FFFFFFL);
            if (residual != 0) {
                for (int i = valid; i < baseLength - 1; ++i) {
                    base[i] = (Object[])Array.newInstance(componentType.getComponentType(), 134217728);
                }
                base[baseLength - 1] = (Object[])Array.newInstance(componentType.getComponentType(), residual);
            } else {
                for (int i = valid; i < baseLength; ++i) {
                    base[i] = (Object[])Array.newInstance(componentType.getComponentType(), 134217728);
                }
            }
            if (preserve - (long)valid * 0x8000000L > 0L) {
                ObjectBigArrays.copy(array, (long)valid * 0x8000000L, base, (long)valid * 0x8000000L, preserve - (long)valid * 0x8000000L);
            }
            return base;
        }
        return array;
    }

    public static <K> K[][] grow(K[][] array, long length) {
        long oldLength = ObjectBigArrays.length(array);
        return length > oldLength ? ObjectBigArrays.grow(array, length, oldLength) : array;
    }

    public static <K> K[][] grow(K[][] array, long length, long preserve) {
        long oldLength = ObjectBigArrays.length(array);
        return length > oldLength ? ObjectBigArrays.ensureCapacity(array, Math.max(2L * oldLength, length), preserve) : array;
    }

    public static <K> K[][] trim(K[][] array, long length) {
        BigArrays.ensureLength(length);
        long oldLength = ObjectBigArrays.length(array);
        if (length >= oldLength) {
            return array;
        }
        int baseLength = (int)(length + 0x7FFFFFFL >>> 27);
        Object[][] base = (Object[][])Arrays.copyOf(array, baseLength);
        int residual = (int)(length & 0x7FFFFFFL);
        if (residual != 0) {
            base[baseLength - 1] = ObjectArrays.trim(base[baseLength - 1], residual);
        }
        return base;
    }

    public static <K> K[][] setLength(K[][] array, long length) {
        long oldLength = ObjectBigArrays.length(array);
        if (length == oldLength) {
            return array;
        }
        if (length < oldLength) {
            return ObjectBigArrays.trim(array, length);
        }
        return ObjectBigArrays.ensureCapacity(array, length);
    }

    public static <K> K[][] copy(K[][] array, long offset, long length) {
        ObjectBigArrays.ensureOffsetLength(array, offset, length);
        K[][] a = ObjectBigArrays.newBigArray(array, length);
        ObjectBigArrays.copy(array, offset, a, 0L, length);
        return a;
    }

    public static <K> K[][] copy(K[][] array) {
        Object[][] base = (Object[][])array.clone();
        int i = base.length;
        while (i-- != 0) {
            base[i] = (Object[])array[i].clone();
        }
        return base;
    }

    public static <K> void fill(K[][] array, K value) {
        int i = array.length;
        while (i-- != 0) {
            Arrays.fill(array[i], value);
        }
    }

    public static <K> void fill(K[][] array, long from, long to, K value) {
        long length = ObjectBigArrays.length(array);
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

    public static <K> boolean equals(K[][] a1, K[][] a2) {
        if (ObjectBigArrays.length(a1) != ObjectBigArrays.length(a2)) {
            return false;
        }
        int i = a1.length;
        while (i-- != 0) {
            K[] t = a1[i];
            K[] u = a2[i];
            int j = t.length;
            while (j-- != 0) {
                if (Objects.equals(t[j], u[j])) continue;
                return false;
            }
        }
        return true;
    }

    public static <K> String toString(K[][] a) {
        if (a == null) {
            return "null";
        }
        long last = ObjectBigArrays.length(a) - 1L;
        if (last == -1L) {
            return "[]";
        }
        StringBuilder b = new StringBuilder();
        b.append('[');
        long i = 0L;
        do {
            b.append(String.valueOf(ObjectBigArrays.get(a, i)));
            if (i == last) {
                return b.append(']').toString();
            }
            b.append(", ");
            ++i;
        } while (true);
    }

    public static <K> void ensureFromTo(K[][] a, long from, long to) {
        BigArrays.ensureFromTo(ObjectBigArrays.length(a), from, to);
    }

    public static <K> void ensureOffsetLength(K[][] a, long offset, long length) {
        BigArrays.ensureOffsetLength(ObjectBigArrays.length(a), offset, length);
    }

    private static <K> void vecSwap(K[][] x, long a, long b, long n) {
        int i = 0;
        while ((long)i < n) {
            ObjectBigArrays.swap(x, a, b);
            ++i;
            ++a;
            ++b;
        }
    }

    private static <K> long med3(K[][] x, long a, long b, long c, Comparator<K> comp) {
        int ab = comp.compare(ObjectBigArrays.get(x, a), ObjectBigArrays.get(x, b));
        int ac = comp.compare(ObjectBigArrays.get(x, a), ObjectBigArrays.get(x, c));
        int bc = comp.compare(ObjectBigArrays.get(x, b), ObjectBigArrays.get(x, c));
        return ab < 0 ? (bc < 0 ? b : (ac < 0 ? c : a)) : (bc > 0 ? b : (ac > 0 ? c : a));
    }

    private static <K> void selectionSort(K[][] a, long from, long to, Comparator<K> comp) {
        for (long i = from; i < to - 1L; ++i) {
            long m = i;
            for (long j = i + 1L; j < to; ++j) {
                if (comp.compare(ObjectBigArrays.get(a, j), ObjectBigArrays.get(a, m)) >= 0) continue;
                m = j;
            }
            if (m == i) continue;
            ObjectBigArrays.swap(a, i, m);
        }
    }

    public static <K> void quickSort(K[][] x, long from, long to, Comparator<K> comp) {
        long c;
        long a;
        long len = to - from;
        if (len < 7L) {
            ObjectBigArrays.selectionSort(x, from, to, comp);
            return;
        }
        long m = from + len / 2L;
        if (len > 7L) {
            long l = from;
            long n = to - 1L;
            if (len > 40L) {
                long s = len / 8L;
                l = ObjectBigArrays.med3(x, l, l + s, l + 2L * s, comp);
                m = ObjectBigArrays.med3(x, m - s, m, m + s, comp);
                n = ObjectBigArrays.med3(x, n - 2L * s, n - s, n, comp);
            }
            m = ObjectBigArrays.med3(x, l, m, n, comp);
        }
        K v = ObjectBigArrays.get(x, m);
        long b = a = from;
        long d = c = to - 1L;
        do {
            int comparison;
            if (b <= c && (comparison = comp.compare(ObjectBigArrays.get(x, b), v)) <= 0) {
                if (comparison == 0) {
                    ObjectBigArrays.swap(x, a++, b);
                }
                ++b;
                continue;
            }
            while (c >= b && (comparison = comp.compare(ObjectBigArrays.get(x, c), v)) >= 0) {
                if (comparison == 0) {
                    ObjectBigArrays.swap(x, c, d--);
                }
                --c;
            }
            if (b > c) break;
            ObjectBigArrays.swap(x, b++, c--);
        } while (true);
        long n = to;
        long s = Math.min(a - from, b - a);
        ObjectBigArrays.vecSwap(x, from, b - s, s);
        s = Math.min(d - c, n - d - 1L);
        ObjectBigArrays.vecSwap(x, b, n - s, s);
        s = b - a;
        if (s > 1L) {
            ObjectBigArrays.quickSort(x, from, from + s, comp);
        }
        if ((s = d - c) > 1L) {
            ObjectBigArrays.quickSort(x, n - s, n, comp);
        }
    }

    private static <K> long med3(K[][] x, long a, long b, long c) {
        int ab = ((Comparable)ObjectBigArrays.get(x, a)).compareTo(ObjectBigArrays.get(x, b));
        int ac = ((Comparable)ObjectBigArrays.get(x, a)).compareTo(ObjectBigArrays.get(x, c));
        int bc = ((Comparable)ObjectBigArrays.get(x, b)).compareTo(ObjectBigArrays.get(x, c));
        return ab < 0 ? (bc < 0 ? b : (ac < 0 ? c : a)) : (bc > 0 ? b : (ac > 0 ? c : a));
    }

    private static <K> void selectionSort(K[][] a, long from, long to) {
        for (long i = from; i < to - 1L; ++i) {
            long m = i;
            for (long j = i + 1L; j < to; ++j) {
                if (((Comparable)ObjectBigArrays.get(a, j)).compareTo(ObjectBigArrays.get(a, m)) >= 0) continue;
                m = j;
            }
            if (m == i) continue;
            ObjectBigArrays.swap(a, i, m);
        }
    }

    public static <K> void quickSort(K[][] x, Comparator<K> comp) {
        ObjectBigArrays.quickSort(x, 0L, ObjectBigArrays.length(x), comp);
    }

    public static <K> void quickSort(K[][] x, long from, long to) {
        long a;
        long c;
        long len = to - from;
        if (len < 7L) {
            ObjectBigArrays.selectionSort(x, from, to);
            return;
        }
        long m = from + len / 2L;
        if (len > 7L) {
            long l = from;
            long n = to - 1L;
            if (len > 40L) {
                long s = len / 8L;
                l = ObjectBigArrays.med3(x, l, l + s, l + 2L * s);
                m = ObjectBigArrays.med3(x, m - s, m, m + s);
                n = ObjectBigArrays.med3(x, n - 2L * s, n - s, n);
            }
            m = ObjectBigArrays.med3(x, l, m, n);
        }
        K v = ObjectBigArrays.get(x, m);
        long b = a = from;
        long d = c = to - 1L;
        do {
            int comparison;
            if (b <= c && (comparison = ((Comparable)ObjectBigArrays.get(x, b)).compareTo(v)) <= 0) {
                if (comparison == 0) {
                    ObjectBigArrays.swap(x, a++, b);
                }
                ++b;
                continue;
            }
            while (c >= b && (comparison = ((Comparable)ObjectBigArrays.get(x, c)).compareTo(v)) >= 0) {
                if (comparison == 0) {
                    ObjectBigArrays.swap(x, c, d--);
                }
                --c;
            }
            if (b > c) break;
            ObjectBigArrays.swap(x, b++, c--);
        } while (true);
        long n = to;
        long s = Math.min(a - from, b - a);
        ObjectBigArrays.vecSwap(x, from, b - s, s);
        s = Math.min(d - c, n - d - 1L);
        ObjectBigArrays.vecSwap(x, b, n - s, s);
        s = b - a;
        if (s > 1L) {
            ObjectBigArrays.quickSort(x, from, from + s);
        }
        if ((s = d - c) > 1L) {
            ObjectBigArrays.quickSort(x, n - s, n);
        }
    }

    public static <K> void quickSort(K[][] x) {
        ObjectBigArrays.quickSort(x, 0L, ObjectBigArrays.length(x));
    }

    public static <K> long binarySearch(K[][] a, long from, long to, K key) {
        --to;
        while (from <= to) {
            long mid = from + to >>> 1;
            K midVal = ObjectBigArrays.get(a, mid);
            int cmp = ((Comparable)midVal).compareTo(key);
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

    public static <K> long binarySearch(K[][] a, Object key) {
        return ObjectBigArrays.binarySearch(a, 0L, ObjectBigArrays.length(a), key);
    }

    public static <K> long binarySearch(K[][] a, long from, long to, K key, Comparator<K> c) {
        --to;
        while (from <= to) {
            long mid = from + to >>> 1;
            K midVal = ObjectBigArrays.get(a, mid);
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

    public static <K> long binarySearch(K[][] a, K key, Comparator<K> c) {
        return ObjectBigArrays.binarySearch(a, 0L, ObjectBigArrays.length(a), key, c);
    }

    public static <K> K[][] shuffle(K[][] a, long from, long to, Random random) {
        long i = to - from;
        while (i-- != 0L) {
            long p = (random.nextLong() & Long.MAX_VALUE) % (i + 1L);
            K t = ObjectBigArrays.get(a, from + i);
            ObjectBigArrays.set(a, from + i, ObjectBigArrays.get(a, from + p));
            ObjectBigArrays.set(a, from + p, t);
        }
        return a;
    }

    public static <K> K[][] shuffle(K[][] a, Random random) {
        long i = ObjectBigArrays.length(a);
        while (i-- != 0L) {
            long p = (random.nextLong() & Long.MAX_VALUE) % (i + 1L);
            K t = ObjectBigArrays.get(a, i);
            ObjectBigArrays.set(a, i, ObjectBigArrays.get(a, p));
            ObjectBigArrays.set(a, p, t);
        }
        return a;
    }

    private static final class BigArrayHashStrategy<K>
    implements Hash.Strategy<K[][]>,
    Serializable {
        private static final long serialVersionUID = -7046029254386353129L;

        private BigArrayHashStrategy() {
        }

        @Override
        public int hashCode(K[][] o) {
            return Arrays.deepHashCode(o);
        }

        @Override
        public boolean equals(K[][] a, K[][] b) {
            return ObjectBigArrays.equals(a, b);
        }
    }

}

