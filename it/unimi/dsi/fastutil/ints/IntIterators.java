/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.ints;

import it.unimi.dsi.fastutil.bytes.ByteIterator;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntArrays;
import it.unimi.dsi.fastutil.ints.IntBidirectionalIterator;
import it.unimi.dsi.fastutil.ints.IntCollection;
import it.unimi.dsi.fastutil.ints.IntIterator;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.ints.IntListIterator;
import it.unimi.dsi.fastutil.shorts.ShortIterator;
import java.io.Serializable;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.IntPredicate;

public final class IntIterators {
    public static final EmptyIterator EMPTY_ITERATOR = new EmptyIterator();

    private IntIterators() {
    }

    public static IntListIterator singleton(int element) {
        return new SingletonIterator(element);
    }

    public static IntListIterator wrap(int[] array, int offset, int length) {
        IntArrays.ensureOffsetLength(array, offset, length);
        return new ArrayIterator(array, offset, length);
    }

    public static IntListIterator wrap(int[] array) {
        return new ArrayIterator(array, 0, array.length);
    }

    public static int unwrap(IntIterator i, int[] array, int offset, int max) {
        if (max < 0) {
            throw new IllegalArgumentException("The maximum number of elements (" + max + ") is negative");
        }
        if (offset < 0 || offset + max > array.length) {
            throw new IllegalArgumentException();
        }
        int j = max;
        while (j-- != 0 && i.hasNext()) {
            array[offset++] = i.nextInt();
        }
        return max - j - 1;
    }

    public static int unwrap(IntIterator i, int[] array) {
        return IntIterators.unwrap(i, array, 0, array.length);
    }

    public static int[] unwrap(IntIterator i, int max) {
        if (max < 0) {
            throw new IllegalArgumentException("The maximum number of elements (" + max + ") is negative");
        }
        int[] array = new int[16];
        int j = 0;
        while (max-- != 0 && i.hasNext()) {
            if (j == array.length) {
                array = IntArrays.grow(array, j + 1);
            }
            array[j++] = i.nextInt();
        }
        return IntArrays.trim(array, j);
    }

    public static int[] unwrap(IntIterator i) {
        return IntIterators.unwrap(i, Integer.MAX_VALUE);
    }

    public static int unwrap(IntIterator i, IntCollection c, int max) {
        if (max < 0) {
            throw new IllegalArgumentException("The maximum number of elements (" + max + ") is negative");
        }
        int j = max;
        while (j-- != 0 && i.hasNext()) {
            c.add(i.nextInt());
        }
        return max - j - 1;
    }

    public static long unwrap(IntIterator i, IntCollection c) {
        long n = 0L;
        while (i.hasNext()) {
            c.add(i.nextInt());
            ++n;
        }
        return n;
    }

    public static int pour(IntIterator i, IntCollection s, int max) {
        if (max < 0) {
            throw new IllegalArgumentException("The maximum number of elements (" + max + ") is negative");
        }
        int j = max;
        while (j-- != 0 && i.hasNext()) {
            s.add(i.nextInt());
        }
        return max - j - 1;
    }

    public static int pour(IntIterator i, IntCollection s) {
        return IntIterators.pour(i, s, Integer.MAX_VALUE);
    }

    public static IntList pour(IntIterator i, int max) {
        IntArrayList l = new IntArrayList();
        IntIterators.pour(i, l, max);
        l.trim();
        return l;
    }

    public static IntList pour(IntIterator i) {
        return IntIterators.pour(i, Integer.MAX_VALUE);
    }

    public static IntIterator asIntIterator(Iterator i) {
        if (i instanceof IntIterator) {
            return (IntIterator)i;
        }
        return new IteratorWrapper(i);
    }

    public static IntListIterator asIntIterator(ListIterator i) {
        if (i instanceof IntListIterator) {
            return (IntListIterator)i;
        }
        return new ListIteratorWrapper(i);
    }

    public static boolean any(IntIterator iterator, IntPredicate predicate) {
        return IntIterators.indexOf(iterator, predicate) != -1;
    }

    public static boolean all(IntIterator iterator, IntPredicate predicate) {
        Objects.requireNonNull(predicate);
        do {
            if (iterator.hasNext()) continue;
            return true;
        } while (predicate.test(iterator.nextInt()));
        return false;
    }

    public static int indexOf(IntIterator iterator, IntPredicate predicate) {
        Objects.requireNonNull(predicate);
        int i = 0;
        while (iterator.hasNext()) {
            if (predicate.test(iterator.nextInt())) {
                return i;
            }
            ++i;
        }
        return -1;
    }

    public static IntListIterator fromTo(int from, int to) {
        return new IntervalIterator(from, to);
    }

    public static IntIterator concat(IntIterator[] a) {
        return IntIterators.concat(a, 0, a.length);
    }

    public static IntIterator concat(IntIterator[] a, int offset, int length) {
        return new IteratorConcatenator(a, offset, length);
    }

    public static IntIterator unmodifiable(IntIterator i) {
        return new UnmodifiableIterator(i);
    }

    public static IntBidirectionalIterator unmodifiable(IntBidirectionalIterator i) {
        return new UnmodifiableBidirectionalIterator(i);
    }

    public static IntListIterator unmodifiable(IntListIterator i) {
        return new UnmodifiableListIterator(i);
    }

    public static IntIterator wrap(ByteIterator iterator) {
        return new ByteIteratorWrapper(iterator);
    }

    public static IntIterator wrap(ShortIterator iterator) {
        return new ShortIteratorWrapper(iterator);
    }

    protected static class ShortIteratorWrapper
    implements IntIterator {
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
        public Integer next() {
            return this.iterator.nextShort();
        }

        @Override
        public int nextInt() {
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
    implements IntIterator {
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
        public Integer next() {
            return this.iterator.nextByte();
        }

        @Override
        public int nextInt() {
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
    implements IntListIterator {
        protected final IntListIterator i;

        public UnmodifiableListIterator(IntListIterator i) {
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
        public int nextInt() {
            return this.i.nextInt();
        }

        @Override
        public int previousInt() {
            return this.i.previousInt();
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
    implements IntBidirectionalIterator {
        protected final IntBidirectionalIterator i;

        public UnmodifiableBidirectionalIterator(IntBidirectionalIterator i) {
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
        public int nextInt() {
            return this.i.nextInt();
        }

        @Override
        public int previousInt() {
            return this.i.previousInt();
        }
    }

    public static class UnmodifiableIterator
    implements IntIterator {
        protected final IntIterator i;

        public UnmodifiableIterator(IntIterator i) {
            this.i = i;
        }

        @Override
        public boolean hasNext() {
            return this.i.hasNext();
        }

        @Override
        public int nextInt() {
            return this.i.nextInt();
        }
    }

    private static class IteratorConcatenator
    implements IntIterator {
        final IntIterator[] a;
        int offset;
        int length;
        int lastOffset = -1;

        public IteratorConcatenator(IntIterator[] a, int offset, int length) {
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
        public int nextInt() {
            if (!this.hasNext()) {
                throw new NoSuchElementException();
            }
            this.lastOffset = this.offset;
            int next = this.a[this.lastOffset].nextInt();
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

    private static class IntervalIterator
    implements IntListIterator {
        private final int from;
        private final int to;
        int curr;

        public IntervalIterator(int from, int to) {
            this.from = this.curr = from;
            this.to = to;
        }

        @Override
        public boolean hasNext() {
            return this.curr < this.to;
        }

        @Override
        public boolean hasPrevious() {
            return this.curr > this.from;
        }

        @Override
        public int nextInt() {
            if (!this.hasNext()) {
                throw new NoSuchElementException();
            }
            return this.curr++;
        }

        @Override
        public int previousInt() {
            if (!this.hasPrevious()) {
                throw new NoSuchElementException();
            }
            return --this.curr;
        }

        @Override
        public int nextIndex() {
            return this.curr - this.from;
        }

        @Override
        public int previousIndex() {
            return this.curr - this.from - 1;
        }

        @Override
        public int skip(int n) {
            if (this.curr + n <= this.to) {
                this.curr += n;
                return n;
            }
            n = this.to - this.curr;
            this.curr = this.to;
            return n;
        }

        @Override
        public int back(int n) {
            if (this.curr - n >= this.from) {
                this.curr -= n;
                return n;
            }
            n = this.curr - this.from;
            this.curr = this.from;
            return n;
        }
    }

    private static class ListIteratorWrapper
    implements IntListIterator {
        final ListIterator<Integer> i;

        public ListIteratorWrapper(ListIterator<Integer> i) {
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
        public void set(int k) {
            this.i.set(k);
        }

        @Override
        public void add(int k) {
            this.i.add(k);
        }

        @Override
        public void remove() {
            this.i.remove();
        }

        @Override
        public int nextInt() {
            return this.i.next();
        }

        @Override
        public int previousInt() {
            return this.i.previous();
        }
    }

    private static class IteratorWrapper
    implements IntIterator {
        final Iterator<Integer> i;

        public IteratorWrapper(Iterator<Integer> i) {
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
        public int nextInt() {
            return this.i.next();
        }
    }

    private static class ArrayIterator
    implements IntListIterator {
        private final int[] array;
        private final int offset;
        private final int length;
        private int curr;

        public ArrayIterator(int[] array, int offset, int length) {
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
        public int nextInt() {
            if (!this.hasNext()) {
                throw new NoSuchElementException();
            }
            return this.array[this.offset + this.curr++];
        }

        @Override
        public int previousInt() {
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
    implements IntListIterator {
        private final int element;
        private int curr;

        public SingletonIterator(int element) {
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
        public int nextInt() {
            if (!this.hasNext()) {
                throw new NoSuchElementException();
            }
            this.curr = 1;
            return this.element;
        }

        @Override
        public int previousInt() {
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
    implements IntListIterator,
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
        public int nextInt() {
            throw new NoSuchElementException();
        }

        @Override
        public int previousInt() {
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
            return IntIterators.EMPTY_ITERATOR;
        }

        private Object readResolve() {
            return IntIterators.EMPTY_ITERATOR;
        }
    }

}

