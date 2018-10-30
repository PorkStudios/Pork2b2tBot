/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.bytes.ByteIterator;
import it.unimi.dsi.fastutil.ints.IntIterator;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongArrays;
import it.unimi.dsi.fastutil.longs.LongBidirectionalIterator;
import it.unimi.dsi.fastutil.longs.LongCollection;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongList;
import it.unimi.dsi.fastutil.longs.LongListIterator;
import it.unimi.dsi.fastutil.shorts.ShortIterator;
import java.io.Serializable;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.LongPredicate;

public final class LongIterators {
    public static final EmptyIterator EMPTY_ITERATOR = new EmptyIterator();

    private LongIterators() {
    }

    public static LongListIterator singleton(long element) {
        return new SingletonIterator(element);
    }

    public static LongListIterator wrap(long[] array, int offset, int length) {
        LongArrays.ensureOffsetLength(array, offset, length);
        return new ArrayIterator(array, offset, length);
    }

    public static LongListIterator wrap(long[] array) {
        return new ArrayIterator(array, 0, array.length);
    }

    public static int unwrap(LongIterator i, long[] array, int offset, int max) {
        if (max < 0) {
            throw new IllegalArgumentException("The maximum number of elements (" + max + ") is negative");
        }
        if (offset < 0 || offset + max > array.length) {
            throw new IllegalArgumentException();
        }
        int j = max;
        while (j-- != 0 && i.hasNext()) {
            array[offset++] = i.nextLong();
        }
        return max - j - 1;
    }

    public static int unwrap(LongIterator i, long[] array) {
        return LongIterators.unwrap(i, array, 0, array.length);
    }

    public static long[] unwrap(LongIterator i, int max) {
        if (max < 0) {
            throw new IllegalArgumentException("The maximum number of elements (" + max + ") is negative");
        }
        long[] array = new long[16];
        int j = 0;
        while (max-- != 0 && i.hasNext()) {
            if (j == array.length) {
                array = LongArrays.grow(array, j + 1);
            }
            array[j++] = i.nextLong();
        }
        return LongArrays.trim(array, j);
    }

    public static long[] unwrap(LongIterator i) {
        return LongIterators.unwrap(i, Integer.MAX_VALUE);
    }

    public static int unwrap(LongIterator i, LongCollection c, int max) {
        if (max < 0) {
            throw new IllegalArgumentException("The maximum number of elements (" + max + ") is negative");
        }
        int j = max;
        while (j-- != 0 && i.hasNext()) {
            c.add(i.nextLong());
        }
        return max - j - 1;
    }

    public static long unwrap(LongIterator i, LongCollection c) {
        long n = 0L;
        while (i.hasNext()) {
            c.add(i.nextLong());
            ++n;
        }
        return n;
    }

    public static int pour(LongIterator i, LongCollection s, int max) {
        if (max < 0) {
            throw new IllegalArgumentException("The maximum number of elements (" + max + ") is negative");
        }
        int j = max;
        while (j-- != 0 && i.hasNext()) {
            s.add(i.nextLong());
        }
        return max - j - 1;
    }

    public static int pour(LongIterator i, LongCollection s) {
        return LongIterators.pour(i, s, Integer.MAX_VALUE);
    }

    public static LongList pour(LongIterator i, int max) {
        LongArrayList l = new LongArrayList();
        LongIterators.pour(i, l, max);
        l.trim();
        return l;
    }

    public static LongList pour(LongIterator i) {
        return LongIterators.pour(i, Integer.MAX_VALUE);
    }

    public static LongIterator asLongIterator(Iterator i) {
        if (i instanceof LongIterator) {
            return (LongIterator)i;
        }
        return new IteratorWrapper(i);
    }

    public static LongListIterator asLongIterator(ListIterator i) {
        if (i instanceof LongListIterator) {
            return (LongListIterator)i;
        }
        return new ListIteratorWrapper(i);
    }

    public static boolean any(LongIterator iterator, LongPredicate predicate) {
        return LongIterators.indexOf(iterator, predicate) != -1;
    }

    public static boolean all(LongIterator iterator, LongPredicate predicate) {
        Objects.requireNonNull(predicate);
        do {
            if (iterator.hasNext()) continue;
            return true;
        } while (predicate.test(iterator.nextLong()));
        return false;
    }

    public static int indexOf(LongIterator iterator, LongPredicate predicate) {
        Objects.requireNonNull(predicate);
        int i = 0;
        while (iterator.hasNext()) {
            if (predicate.test(iterator.nextLong())) {
                return i;
            }
            ++i;
        }
        return -1;
    }

    public static LongBidirectionalIterator fromTo(long from, long to) {
        return new IntervalIterator(from, to);
    }

    public static LongIterator concat(LongIterator[] a) {
        return LongIterators.concat(a, 0, a.length);
    }

    public static LongIterator concat(LongIterator[] a, int offset, int length) {
        return new IteratorConcatenator(a, offset, length);
    }

    public static LongIterator unmodifiable(LongIterator i) {
        return new UnmodifiableIterator(i);
    }

    public static LongBidirectionalIterator unmodifiable(LongBidirectionalIterator i) {
        return new UnmodifiableBidirectionalIterator(i);
    }

    public static LongListIterator unmodifiable(LongListIterator i) {
        return new UnmodifiableListIterator(i);
    }

    public static LongIterator wrap(ByteIterator iterator) {
        return new ByteIteratorWrapper(iterator);
    }

    public static LongIterator wrap(ShortIterator iterator) {
        return new ShortIteratorWrapper(iterator);
    }

    public static LongIterator wrap(IntIterator iterator) {
        return new IntIteratorWrapper(iterator);
    }

    protected static class IntIteratorWrapper
    implements LongIterator {
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
        public Long next() {
            return this.iterator.nextInt();
        }

        @Override
        public long nextLong() {
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
    implements LongIterator {
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
        public Long next() {
            return this.iterator.nextShort();
        }

        @Override
        public long nextLong() {
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
    implements LongIterator {
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
        public Long next() {
            return this.iterator.nextByte();
        }

        @Override
        public long nextLong() {
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
    implements LongListIterator {
        protected final LongListIterator i;

        public UnmodifiableListIterator(LongListIterator i) {
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
        public long nextLong() {
            return this.i.nextLong();
        }

        @Override
        public long previousLong() {
            return this.i.previousLong();
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
    implements LongBidirectionalIterator {
        protected final LongBidirectionalIterator i;

        public UnmodifiableBidirectionalIterator(LongBidirectionalIterator i) {
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
        public long nextLong() {
            return this.i.nextLong();
        }

        @Override
        public long previousLong() {
            return this.i.previousLong();
        }
    }

    public static class UnmodifiableIterator
    implements LongIterator {
        protected final LongIterator i;

        public UnmodifiableIterator(LongIterator i) {
            this.i = i;
        }

        @Override
        public boolean hasNext() {
            return this.i.hasNext();
        }

        @Override
        public long nextLong() {
            return this.i.nextLong();
        }
    }

    private static class IteratorConcatenator
    implements LongIterator {
        final LongIterator[] a;
        int offset;
        int length;
        int lastOffset = -1;

        public IteratorConcatenator(LongIterator[] a, int offset, int length) {
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
        public long nextLong() {
            if (!this.hasNext()) {
                throw new NoSuchElementException();
            }
            this.lastOffset = this.offset;
            long next = this.a[this.lastOffset].nextLong();
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
    implements LongBidirectionalIterator {
        private final long from;
        private final long to;
        long curr;

        public IntervalIterator(long from, long to) {
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
        public long nextLong() {
            if (!this.hasNext()) {
                throw new NoSuchElementException();
            }
            return this.curr++;
        }

        @Override
        public long previousLong() {
            if (!this.hasPrevious()) {
                throw new NoSuchElementException();
            }
            return --this.curr;
        }

        @Override
        public int skip(int n) {
            if (this.curr + (long)n <= this.to) {
                this.curr += (long)n;
                return n;
            }
            n = (int)(this.to - this.curr);
            this.curr = this.to;
            return n;
        }

        @Override
        public int back(int n) {
            if (this.curr - (long)n >= this.from) {
                this.curr -= (long)n;
                return n;
            }
            n = (int)(this.curr - this.from);
            this.curr = this.from;
            return n;
        }
    }

    private static class ListIteratorWrapper
    implements LongListIterator {
        final ListIterator<Long> i;

        public ListIteratorWrapper(ListIterator<Long> i) {
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
        public void set(long k) {
            this.i.set(k);
        }

        @Override
        public void add(long k) {
            this.i.add(k);
        }

        @Override
        public void remove() {
            this.i.remove();
        }

        @Override
        public long nextLong() {
            return this.i.next();
        }

        @Override
        public long previousLong() {
            return this.i.previous();
        }
    }

    private static class IteratorWrapper
    implements LongIterator {
        final Iterator<Long> i;

        public IteratorWrapper(Iterator<Long> i) {
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
        public long nextLong() {
            return this.i.next();
        }
    }

    private static class ArrayIterator
    implements LongListIterator {
        private final long[] array;
        private final int offset;
        private final int length;
        private int curr;

        public ArrayIterator(long[] array, int offset, int length) {
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
        public long nextLong() {
            if (!this.hasNext()) {
                throw new NoSuchElementException();
            }
            return this.array[this.offset + this.curr++];
        }

        @Override
        public long previousLong() {
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
    implements LongListIterator {
        private final long element;
        private int curr;

        public SingletonIterator(long element) {
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
        public long nextLong() {
            if (!this.hasNext()) {
                throw new NoSuchElementException();
            }
            this.curr = 1;
            return this.element;
        }

        @Override
        public long previousLong() {
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
    implements LongListIterator,
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
        public long nextLong() {
            throw new NoSuchElementException();
        }

        @Override
        public long previousLong() {
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
            return LongIterators.EMPTY_ITERATOR;
        }

        private Object readResolve() {
            return LongIterators.EMPTY_ITERATOR;
        }
    }

}

