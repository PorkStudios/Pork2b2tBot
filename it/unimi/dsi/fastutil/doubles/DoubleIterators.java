/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.doubles;

import it.unimi.dsi.fastutil.bytes.ByteIterator;
import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import it.unimi.dsi.fastutil.doubles.DoubleArrays;
import it.unimi.dsi.fastutil.doubles.DoubleBidirectionalIterator;
import it.unimi.dsi.fastutil.doubles.DoubleCollection;
import it.unimi.dsi.fastutil.doubles.DoubleIterator;
import it.unimi.dsi.fastutil.doubles.DoubleList;
import it.unimi.dsi.fastutil.doubles.DoubleListIterator;
import it.unimi.dsi.fastutil.floats.FloatIterator;
import it.unimi.dsi.fastutil.ints.IntIterator;
import it.unimi.dsi.fastutil.shorts.ShortIterator;
import java.io.Serializable;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.DoublePredicate;

public final class DoubleIterators {
    public static final EmptyIterator EMPTY_ITERATOR = new EmptyIterator();

    private DoubleIterators() {
    }

    public static DoubleListIterator singleton(double element) {
        return new SingletonIterator(element);
    }

    public static DoubleListIterator wrap(double[] array, int offset, int length) {
        DoubleArrays.ensureOffsetLength(array, offset, length);
        return new ArrayIterator(array, offset, length);
    }

    public static DoubleListIterator wrap(double[] array) {
        return new ArrayIterator(array, 0, array.length);
    }

    public static int unwrap(DoubleIterator i, double[] array, int offset, int max) {
        if (max < 0) {
            throw new IllegalArgumentException("The maximum number of elements (" + max + ") is negative");
        }
        if (offset < 0 || offset + max > array.length) {
            throw new IllegalArgumentException();
        }
        int j = max;
        while (j-- != 0 && i.hasNext()) {
            array[offset++] = i.nextDouble();
        }
        return max - j - 1;
    }

    public static int unwrap(DoubleIterator i, double[] array) {
        return DoubleIterators.unwrap(i, array, 0, array.length);
    }

    public static double[] unwrap(DoubleIterator i, int max) {
        if (max < 0) {
            throw new IllegalArgumentException("The maximum number of elements (" + max + ") is negative");
        }
        double[] array = new double[16];
        int j = 0;
        while (max-- != 0 && i.hasNext()) {
            if (j == array.length) {
                array = DoubleArrays.grow(array, j + 1);
            }
            array[j++] = i.nextDouble();
        }
        return DoubleArrays.trim(array, j);
    }

    public static double[] unwrap(DoubleIterator i) {
        return DoubleIterators.unwrap(i, Integer.MAX_VALUE);
    }

    public static int unwrap(DoubleIterator i, DoubleCollection c, int max) {
        if (max < 0) {
            throw new IllegalArgumentException("The maximum number of elements (" + max + ") is negative");
        }
        int j = max;
        while (j-- != 0 && i.hasNext()) {
            c.add(i.nextDouble());
        }
        return max - j - 1;
    }

    public static long unwrap(DoubleIterator i, DoubleCollection c) {
        long n = 0L;
        while (i.hasNext()) {
            c.add(i.nextDouble());
            ++n;
        }
        return n;
    }

    public static int pour(DoubleIterator i, DoubleCollection s, int max) {
        if (max < 0) {
            throw new IllegalArgumentException("The maximum number of elements (" + max + ") is negative");
        }
        int j = max;
        while (j-- != 0 && i.hasNext()) {
            s.add(i.nextDouble());
        }
        return max - j - 1;
    }

    public static int pour(DoubleIterator i, DoubleCollection s) {
        return DoubleIterators.pour(i, s, Integer.MAX_VALUE);
    }

    public static DoubleList pour(DoubleIterator i, int max) {
        DoubleArrayList l = new DoubleArrayList();
        DoubleIterators.pour(i, l, max);
        l.trim();
        return l;
    }

    public static DoubleList pour(DoubleIterator i) {
        return DoubleIterators.pour(i, Integer.MAX_VALUE);
    }

    public static DoubleIterator asDoubleIterator(Iterator i) {
        if (i instanceof DoubleIterator) {
            return (DoubleIterator)i;
        }
        return new IteratorWrapper(i);
    }

    public static DoubleListIterator asDoubleIterator(ListIterator i) {
        if (i instanceof DoubleListIterator) {
            return (DoubleListIterator)i;
        }
        return new ListIteratorWrapper(i);
    }

    public static boolean any(DoubleIterator iterator, DoublePredicate predicate) {
        return DoubleIterators.indexOf(iterator, predicate) != -1;
    }

    public static boolean all(DoubleIterator iterator, DoublePredicate predicate) {
        Objects.requireNonNull(predicate);
        do {
            if (iterator.hasNext()) continue;
            return true;
        } while (predicate.test(iterator.nextDouble()));
        return false;
    }

    public static int indexOf(DoubleIterator iterator, DoublePredicate predicate) {
        Objects.requireNonNull(predicate);
        int i = 0;
        while (iterator.hasNext()) {
            if (predicate.test(iterator.nextDouble())) {
                return i;
            }
            ++i;
        }
        return -1;
    }

    public static DoubleIterator concat(DoubleIterator[] a) {
        return DoubleIterators.concat(a, 0, a.length);
    }

    public static DoubleIterator concat(DoubleIterator[] a, int offset, int length) {
        return new IteratorConcatenator(a, offset, length);
    }

    public static DoubleIterator unmodifiable(DoubleIterator i) {
        return new UnmodifiableIterator(i);
    }

    public static DoubleBidirectionalIterator unmodifiable(DoubleBidirectionalIterator i) {
        return new UnmodifiableBidirectionalIterator(i);
    }

    public static DoubleListIterator unmodifiable(DoubleListIterator i) {
        return new UnmodifiableListIterator(i);
    }

    public static DoubleIterator wrap(ByteIterator iterator) {
        return new ByteIteratorWrapper(iterator);
    }

    public static DoubleIterator wrap(ShortIterator iterator) {
        return new ShortIteratorWrapper(iterator);
    }

    public static DoubleIterator wrap(IntIterator iterator) {
        return new IntIteratorWrapper(iterator);
    }

    public static DoubleIterator wrap(FloatIterator iterator) {
        return new FloatIteratorWrapper(iterator);
    }

    protected static class FloatIteratorWrapper
    implements DoubleIterator {
        final FloatIterator iterator;

        public FloatIteratorWrapper(FloatIterator iterator) {
            this.iterator = iterator;
        }

        @Override
        public boolean hasNext() {
            return this.iterator.hasNext();
        }

        @Deprecated
        @Override
        public Double next() {
            return this.iterator.nextFloat();
        }

        @Override
        public double nextDouble() {
            return this.iterator.nextFloat();
        }

        @Override
        public void remove() {
            this.iterator.remove();
        }

        @Override
        public int skip(int n) {
            return this.iterator.skip(n);
        }
    }

    protected static class IntIteratorWrapper
    implements DoubleIterator {
        final IntIterator iterator;

        public IntIteratorWrapper(IntIterator iterator) {
            this.iterator = iterator;
        }

        @Override
        public boolean hasNext() {
            return this.iterator.hasNext();
        }

        @Deprecated
        @Override
        public Double next() {
            return this.iterator.nextInt();
        }

        @Override
        public double nextDouble() {
            return this.iterator.nextInt();
        }

        @Override
        public void remove() {
            this.iterator.remove();
        }

        @Override
        public int skip(int n) {
            return this.iterator.skip(n);
        }
    }

    protected static class ShortIteratorWrapper
    implements DoubleIterator {
        final ShortIterator iterator;

        public ShortIteratorWrapper(ShortIterator iterator) {
            this.iterator = iterator;
        }

        @Override
        public boolean hasNext() {
            return this.iterator.hasNext();
        }

        @Deprecated
        @Override
        public Double next() {
            return this.iterator.nextShort();
        }

        @Override
        public double nextDouble() {
            return this.iterator.nextShort();
        }

        @Override
        public void remove() {
            this.iterator.remove();
        }

        @Override
        public int skip(int n) {
            return this.iterator.skip(n);
        }
    }

    protected static class ByteIteratorWrapper
    implements DoubleIterator {
        final ByteIterator iterator;

        public ByteIteratorWrapper(ByteIterator iterator) {
            this.iterator = iterator;
        }

        @Override
        public boolean hasNext() {
            return this.iterator.hasNext();
        }

        @Deprecated
        @Override
        public Double next() {
            return this.iterator.nextByte();
        }

        @Override
        public double nextDouble() {
            return this.iterator.nextByte();
        }

        @Override
        public void remove() {
            this.iterator.remove();
        }

        @Override
        public int skip(int n) {
            return this.iterator.skip(n);
        }
    }

    public static class UnmodifiableListIterator
    implements DoubleListIterator {
        protected final DoubleListIterator i;

        public UnmodifiableListIterator(DoubleListIterator i) {
            this.i = i;
        }

        @Override
        public boolean hasNext() {
            return this.i.hasNext();
        }

        @Override
        public boolean hasPrevious() {
            return this.i.hasPrevious();
        }

        @Override
        public double nextDouble() {
            return this.i.nextDouble();
        }

        @Override
        public double previousDouble() {
            return this.i.previousDouble();
        }

        @Override
        public int nextIndex() {
            return this.i.nextIndex();
        }

        @Override
        public int previousIndex() {
            return this.i.previousIndex();
        }
    }

    public static class UnmodifiableBidirectionalIterator
    implements DoubleBidirectionalIterator {
        protected final DoubleBidirectionalIterator i;

        public UnmodifiableBidirectionalIterator(DoubleBidirectionalIterator i) {
            this.i = i;
        }

        @Override
        public boolean hasNext() {
            return this.i.hasNext();
        }

        @Override
        public boolean hasPrevious() {
            return this.i.hasPrevious();
        }

        @Override
        public double nextDouble() {
            return this.i.nextDouble();
        }

        @Override
        public double previousDouble() {
            return this.i.previousDouble();
        }
    }

    public static class UnmodifiableIterator
    implements DoubleIterator {
        protected final DoubleIterator i;

        public UnmodifiableIterator(DoubleIterator i) {
            this.i = i;
        }

        @Override
        public boolean hasNext() {
            return this.i.hasNext();
        }

        @Override
        public double nextDouble() {
            return this.i.nextDouble();
        }
    }

    private static class IteratorConcatenator
    implements DoubleIterator {
        final DoubleIterator[] a;
        int offset;
        int length;
        int lastOffset = -1;

        public IteratorConcatenator(DoubleIterator[] a, int offset, int length) {
            this.a = a;
            this.offset = offset;
            this.length = length;
            this.advance();
        }

        private void advance() {
            while (this.length != 0 && !this.a[this.offset].hasNext()) {
                --this.length;
                ++this.offset;
            }
        }

        @Override
        public boolean hasNext() {
            return this.length > 0;
        }

        @Override
        public double nextDouble() {
            if (!this.hasNext()) {
                throw new NoSuchElementException();
            }
            this.lastOffset = this.offset;
            double next = this.a[this.lastOffset].nextDouble();
            this.advance();
            return next;
        }

        @Override
        public void remove() {
            if (this.lastOffset == -1) {
                throw new IllegalStateException();
            }
            this.a[this.lastOffset].remove();
        }

        @Override
        public int skip(int n) {
            this.lastOffset = -1;
            int skipped = 0;
            while (skipped < n && this.length != 0) {
                skipped += this.a[this.offset].skip(n - skipped);
                if (this.a[this.offset].hasNext()) break;
                --this.length;
                ++this.offset;
            }
            return skipped;
        }
    }

    private static class ListIteratorWrapper
    implements DoubleListIterator {
        final ListIterator<Double> i;

        public ListIteratorWrapper(ListIterator<Double> i) {
            this.i = i;
        }

        @Override
        public boolean hasNext() {
            return this.i.hasNext();
        }

        @Override
        public boolean hasPrevious() {
            return this.i.hasPrevious();
        }

        @Override
        public int nextIndex() {
            return this.i.nextIndex();
        }

        @Override
        public int previousIndex() {
            return this.i.previousIndex();
        }

        @Override
        public void set(double k) {
            this.i.set(k);
        }

        @Override
        public void add(double k) {
            this.i.add(k);
        }

        @Override
        public void remove() {
            this.i.remove();
        }

        @Override
        public double nextDouble() {
            return this.i.next();
        }

        @Override
        public double previousDouble() {
            return this.i.previous();
        }
    }

    private static class IteratorWrapper
    implements DoubleIterator {
        final Iterator<Double> i;

        public IteratorWrapper(Iterator<Double> i) {
            this.i = i;
        }

        @Override
        public boolean hasNext() {
            return this.i.hasNext();
        }

        @Override
        public void remove() {
            this.i.remove();
        }

        @Override
        public double nextDouble() {
            return this.i.next();
        }
    }

    private static class ArrayIterator
    implements DoubleListIterator {
        private final double[] array;
        private final int offset;
        private final int length;
        private int curr;

        public ArrayIterator(double[] array, int offset, int length) {
            this.array = array;
            this.offset = offset;
            this.length = length;
        }

        @Override
        public boolean hasNext() {
            return this.curr < this.length;
        }

        @Override
        public boolean hasPrevious() {
            return this.curr > 0;
        }

        @Override
        public double nextDouble() {
            if (!this.hasNext()) {
                throw new NoSuchElementException();
            }
            return this.array[this.offset + this.curr++];
        }

        @Override
        public double previousDouble() {
            if (!this.hasPrevious()) {
                throw new NoSuchElementException();
            }
            return this.array[this.offset + --this.curr];
        }

        @Override
        public int skip(int n) {
            if (n <= this.length - this.curr) {
                this.curr += n;
                return n;
            }
            n = this.length - this.curr;
            this.curr = this.length;
            return n;
        }

        @Override
        public int back(int n) {
            if (n <= this.curr) {
                this.curr -= n;
                return n;
            }
            n = this.curr;
            this.curr = 0;
            return n;
        }

        @Override
        public int nextIndex() {
            return this.curr;
        }

        @Override
        public int previousIndex() {
            return this.curr - 1;
        }
    }

    private static class SingletonIterator
    implements DoubleListIterator {
        private final double element;
        private int curr;

        public SingletonIterator(double element) {
            this.element = element;
        }

        @Override
        public boolean hasNext() {
            return this.curr == 0;
        }

        @Override
        public boolean hasPrevious() {
            return this.curr == 1;
        }

        @Override
        public double nextDouble() {
            if (!this.hasNext()) {
                throw new NoSuchElementException();
            }
            this.curr = 1;
            return this.element;
        }

        @Override
        public double previousDouble() {
            if (!this.hasPrevious()) {
                throw new NoSuchElementException();
            }
            this.curr = 0;
            return this.element;
        }

        @Override
        public int nextIndex() {
            return this.curr;
        }

        @Override
        public int previousIndex() {
            return this.curr - 1;
        }
    }

    public static class EmptyIterator
    implements DoubleListIterator,
    Serializable,
    Cloneable {
        private static final long serialVersionUID = -7046029254386353129L;

        protected EmptyIterator() {
        }

        @Override
        public boolean hasNext() {
            return false;
        }

        @Override
        public boolean hasPrevious() {
            return false;
        }

        @Override
        public double nextDouble() {
            throw new NoSuchElementException();
        }

        @Override
        public double previousDouble() {
            throw new NoSuchElementException();
        }

        @Override
        public int nextIndex() {
            return 0;
        }

        @Override
        public int previousIndex() {
            return -1;
        }

        @Override
        public int skip(int n) {
            return 0;
        }

        @Override
        public int back(int n) {
            return 0;
        }

        public Object clone() {
            return DoubleIterators.EMPTY_ITERATOR;
        }

        private Object readResolve() {
            return DoubleIterators.EMPTY_ITERATOR;
        }
    }

}

