/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.bytes.ByteIterator;
import it.unimi.dsi.fastutil.floats.FloatArrayList;
import it.unimi.dsi.fastutil.floats.FloatArrays;
import it.unimi.dsi.fastutil.floats.FloatBidirectionalIterator;
import it.unimi.dsi.fastutil.floats.FloatCollection;
import it.unimi.dsi.fastutil.floats.FloatIterator;
import it.unimi.dsi.fastutil.floats.FloatList;
import it.unimi.dsi.fastutil.floats.FloatListIterator;
import it.unimi.dsi.fastutil.shorts.ShortIterator;
import java.io.Serializable;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.DoublePredicate;

public final class FloatIterators {
    public static final EmptyIterator EMPTY_ITERATOR = new EmptyIterator();

    private FloatIterators() {
    }

    public static FloatListIterator singleton(float element) {
        return new SingletonIterator(element);
    }

    public static FloatListIterator wrap(float[] array, int offset, int length) {
        FloatArrays.ensureOffsetLength(array, offset, length);
        return new ArrayIterator(array, offset, length);
    }

    public static FloatListIterator wrap(float[] array) {
        return new ArrayIterator(array, 0, array.length);
    }

    public static int unwrap(FloatIterator i, float[] array, int offset, int max) {
        if (max < 0) {
            throw new IllegalArgumentException("The maximum number of elements (" + max + ") is negative");
        }
        if (offset < 0 || offset + max > array.length) {
            throw new IllegalArgumentException();
        }
        int j = max;
        while (j-- != 0 && i.hasNext()) {
            array[offset++] = i.nextFloat();
        }
        return max - j - 1;
    }

    public static int unwrap(FloatIterator i, float[] array) {
        return FloatIterators.unwrap(i, array, 0, array.length);
    }

    public static float[] unwrap(FloatIterator i, int max) {
        if (max < 0) {
            throw new IllegalArgumentException("The maximum number of elements (" + max + ") is negative");
        }
        float[] array = new float[16];
        int j = 0;
        while (max-- != 0 && i.hasNext()) {
            if (j == array.length) {
                array = FloatArrays.grow(array, j + 1);
            }
            array[j++] = i.nextFloat();
        }
        return FloatArrays.trim(array, j);
    }

    public static float[] unwrap(FloatIterator i) {
        return FloatIterators.unwrap(i, Integer.MAX_VALUE);
    }

    public static int unwrap(FloatIterator i, FloatCollection c, int max) {
        if (max < 0) {
            throw new IllegalArgumentException("The maximum number of elements (" + max + ") is negative");
        }
        int j = max;
        while (j-- != 0 && i.hasNext()) {
            c.add(i.nextFloat());
        }
        return max - j - 1;
    }

    public static long unwrap(FloatIterator i, FloatCollection c) {
        long n = 0L;
        while (i.hasNext()) {
            c.add(i.nextFloat());
            ++n;
        }
        return n;
    }

    public static int pour(FloatIterator i, FloatCollection s, int max) {
        if (max < 0) {
            throw new IllegalArgumentException("The maximum number of elements (" + max + ") is negative");
        }
        int j = max;
        while (j-- != 0 && i.hasNext()) {
            s.add(i.nextFloat());
        }
        return max - j - 1;
    }

    public static int pour(FloatIterator i, FloatCollection s) {
        return FloatIterators.pour(i, s, Integer.MAX_VALUE);
    }

    public static FloatList pour(FloatIterator i, int max) {
        FloatArrayList l = new FloatArrayList();
        FloatIterators.pour(i, l, max);
        l.trim();
        return l;
    }

    public static FloatList pour(FloatIterator i) {
        return FloatIterators.pour(i, Integer.MAX_VALUE);
    }

    public static FloatIterator asFloatIterator(Iterator i) {
        if (i instanceof FloatIterator) {
            return (FloatIterator)i;
        }
        return new IteratorWrapper(i);
    }

    public static FloatListIterator asFloatIterator(ListIterator i) {
        if (i instanceof FloatListIterator) {
            return (FloatListIterator)i;
        }
        return new ListIteratorWrapper(i);
    }

    public static boolean any(FloatIterator iterator, DoublePredicate predicate) {
        return FloatIterators.indexOf(iterator, predicate) != -1;
    }

    public static boolean all(FloatIterator iterator, DoublePredicate predicate) {
        Objects.requireNonNull(predicate);
        do {
            if (iterator.hasNext()) continue;
            return true;
        } while (predicate.test(iterator.nextFloat()));
        return false;
    }

    public static int indexOf(FloatIterator iterator, DoublePredicate predicate) {
        Objects.requireNonNull(predicate);
        int i = 0;
        while (iterator.hasNext()) {
            if (predicate.test(iterator.nextFloat())) {
                return i;
            }
            ++i;
        }
        return -1;
    }

    public static FloatIterator concat(FloatIterator[] a) {
        return FloatIterators.concat(a, 0, a.length);
    }

    public static FloatIterator concat(FloatIterator[] a, int offset, int length) {
        return new IteratorConcatenator(a, offset, length);
    }

    public static FloatIterator unmodifiable(FloatIterator i) {
        return new UnmodifiableIterator(i);
    }

    public static FloatBidirectionalIterator unmodifiable(FloatBidirectionalIterator i) {
        return new UnmodifiableBidirectionalIterator(i);
    }

    public static FloatListIterator unmodifiable(FloatListIterator i) {
        return new UnmodifiableListIterator(i);
    }

    public static FloatIterator wrap(ByteIterator iterator) {
        return new ByteIteratorWrapper(iterator);
    }

    public static FloatIterator wrap(ShortIterator iterator) {
        return new ShortIteratorWrapper(iterator);
    }

    protected static class ShortIteratorWrapper
    implements FloatIterator {
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
        public Float next() {
            return Float.valueOf(this.iterator.nextShort());
        }

        @Override
        public float nextFloat() {
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
    implements FloatIterator {
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
        public Float next() {
            return Float.valueOf(this.iterator.nextByte());
        }

        @Override
        public float nextFloat() {
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
    implements FloatListIterator {
        protected final FloatListIterator i;

        public UnmodifiableListIterator(FloatListIterator i) {
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
        public float nextFloat() {
            return this.i.nextFloat();
        }

        @Override
        public float previousFloat() {
            return this.i.previousFloat();
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
    implements FloatBidirectionalIterator {
        protected final FloatBidirectionalIterator i;

        public UnmodifiableBidirectionalIterator(FloatBidirectionalIterator i) {
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
        public float nextFloat() {
            return this.i.nextFloat();
        }

        @Override
        public float previousFloat() {
            return this.i.previousFloat();
        }
    }

    public static class UnmodifiableIterator
    implements FloatIterator {
        protected final FloatIterator i;

        public UnmodifiableIterator(FloatIterator i) {
            this.i = i;
        }

        @Override
        public boolean hasNext() {
            return this.i.hasNext();
        }

        @Override
        public float nextFloat() {
            return this.i.nextFloat();
        }
    }

    private static class IteratorConcatenator
    implements FloatIterator {
        final FloatIterator[] a;
        int offset;
        int length;
        int lastOffset = -1;

        public IteratorConcatenator(FloatIterator[] a, int offset, int length) {
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
        public float nextFloat() {
            if (!this.hasNext()) {
                throw new NoSuchElementException();
            }
            this.lastOffset = this.offset;
            float next = this.a[this.lastOffset].nextFloat();
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
    implements FloatListIterator {
        final ListIterator<Float> i;

        public ListIteratorWrapper(ListIterator<Float> i) {
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
        public void set(float k) {
            this.i.set(Float.valueOf(k));
        }

        @Override
        public void add(float k) {
            this.i.add(Float.valueOf(k));
        }

        @Override
        public void remove() {
            this.i.remove();
        }

        @Override
        public float nextFloat() {
            return this.i.next().floatValue();
        }

        @Override
        public float previousFloat() {
            return this.i.previous().floatValue();
        }
    }

    private static class IteratorWrapper
    implements FloatIterator {
        final Iterator<Float> i;

        public IteratorWrapper(Iterator<Float> i) {
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
        public float nextFloat() {
            return this.i.next().floatValue();
        }
    }

    private static class ArrayIterator
    implements FloatListIterator {
        private final float[] array;
        private final int offset;
        private final int length;
        private int curr;

        public ArrayIterator(float[] array, int offset, int length) {
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
        public float nextFloat() {
            if (!this.hasNext()) {
                throw new NoSuchElementException();
            }
            return this.array[this.offset + this.curr++];
        }

        @Override
        public float previousFloat() {
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
    implements FloatListIterator {
        private final float element;
        private int curr;

        public SingletonIterator(float element) {
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
        public float nextFloat() {
            if (!this.hasNext()) {
                throw new NoSuchElementException();
            }
            this.curr = 1;
            return this.element;
        }

        @Override
        public float previousFloat() {
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
    implements FloatListIterator,
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
        public float nextFloat() {
            throw new NoSuchElementException();
        }

        @Override
        public float previousFloat() {
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
            return FloatIterators.EMPTY_ITERATOR;
        }

        private Object readResolve() {
            return FloatIterators.EMPTY_ITERATOR;
        }
    }

}

