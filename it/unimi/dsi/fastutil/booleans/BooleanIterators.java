/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.booleans;

import it.unimi.dsi.fastutil.booleans.BooleanArrayList;
import it.unimi.dsi.fastutil.booleans.BooleanArrays;
import it.unimi.dsi.fastutil.booleans.BooleanBidirectionalIterator;
import it.unimi.dsi.fastutil.booleans.BooleanCollection;
import it.unimi.dsi.fastutil.booleans.BooleanIterator;
import it.unimi.dsi.fastutil.booleans.BooleanList;
import it.unimi.dsi.fastutil.booleans.BooleanListIterator;
import java.io.Serializable;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.Predicate;

public final class BooleanIterators {
    public static final EmptyIterator EMPTY_ITERATOR = new EmptyIterator();

    private BooleanIterators() {
    }

    public static BooleanListIterator singleton(boolean element) {
        return new SingletonIterator(element);
    }

    public static BooleanListIterator wrap(boolean[] array, int offset, int length) {
        BooleanArrays.ensureOffsetLength(array, offset, length);
        return new ArrayIterator(array, offset, length);
    }

    public static BooleanListIterator wrap(boolean[] array) {
        return new ArrayIterator(array, 0, array.length);
    }

    public static int unwrap(BooleanIterator i, boolean[] array, int offset, int max) {
        if (max < 0) {
            throw new IllegalArgumentException("The maximum number of elements (" + max + ") is negative");
        }
        if (offset < 0 || offset + max > array.length) {
            throw new IllegalArgumentException();
        }
        int j = max;
        while (j-- != 0 && i.hasNext()) {
            array[offset++] = i.nextBoolean();
        }
        return max - j - 1;
    }

    public static int unwrap(BooleanIterator i, boolean[] array) {
        return BooleanIterators.unwrap(i, array, 0, array.length);
    }

    public static boolean[] unwrap(BooleanIterator i, int max) {
        if (max < 0) {
            throw new IllegalArgumentException("The maximum number of elements (" + max + ") is negative");
        }
        boolean[] array = new boolean[16];
        int j = 0;
        while (max-- != 0 && i.hasNext()) {
            if (j == array.length) {
                array = BooleanArrays.grow(array, j + 1);
            }
            array[j++] = i.nextBoolean();
        }
        return BooleanArrays.trim(array, j);
    }

    public static boolean[] unwrap(BooleanIterator i) {
        return BooleanIterators.unwrap(i, Integer.MAX_VALUE);
    }

    public static int unwrap(BooleanIterator i, BooleanCollection c, int max) {
        if (max < 0) {
            throw new IllegalArgumentException("The maximum number of elements (" + max + ") is negative");
        }
        int j = max;
        while (j-- != 0 && i.hasNext()) {
            c.add(i.nextBoolean());
        }
        return max - j - 1;
    }

    public static long unwrap(BooleanIterator i, BooleanCollection c) {
        long n = 0L;
        while (i.hasNext()) {
            c.add(i.nextBoolean());
            ++n;
        }
        return n;
    }

    public static int pour(BooleanIterator i, BooleanCollection s, int max) {
        if (max < 0) {
            throw new IllegalArgumentException("The maximum number of elements (" + max + ") is negative");
        }
        int j = max;
        while (j-- != 0 && i.hasNext()) {
            s.add(i.nextBoolean());
        }
        return max - j - 1;
    }

    public static int pour(BooleanIterator i, BooleanCollection s) {
        return BooleanIterators.pour(i, s, Integer.MAX_VALUE);
    }

    public static BooleanList pour(BooleanIterator i, int max) {
        BooleanArrayList l = new BooleanArrayList();
        BooleanIterators.pour(i, l, max);
        l.trim();
        return l;
    }

    public static BooleanList pour(BooleanIterator i) {
        return BooleanIterators.pour(i, Integer.MAX_VALUE);
    }

    public static BooleanIterator asBooleanIterator(Iterator i) {
        if (i instanceof BooleanIterator) {
            return (BooleanIterator)i;
        }
        return new IteratorWrapper(i);
    }

    public static BooleanListIterator asBooleanIterator(ListIterator i) {
        if (i instanceof BooleanListIterator) {
            return (BooleanListIterator)i;
        }
        return new ListIteratorWrapper(i);
    }

    public static boolean any(BooleanIterator iterator, Predicate<? super Boolean> predicate) {
        return BooleanIterators.indexOf(iterator, predicate) != -1;
    }

    public static boolean all(BooleanIterator iterator, Predicate<? super Boolean> predicate) {
        Objects.requireNonNull(predicate);
        do {
            if (iterator.hasNext()) continue;
            return true;
        } while (predicate.test((Boolean)iterator.nextBoolean()));
        return false;
    }

    public static int indexOf(BooleanIterator iterator, Predicate<? super Boolean> predicate) {
        Objects.requireNonNull(predicate);
        int i = 0;
        while (iterator.hasNext()) {
            if (predicate.test((Boolean)iterator.nextBoolean())) {
                return i;
            }
            ++i;
        }
        return -1;
    }

    public static BooleanIterator concat(BooleanIterator[] a) {
        return BooleanIterators.concat(a, 0, a.length);
    }

    public static BooleanIterator concat(BooleanIterator[] a, int offset, int length) {
        return new IteratorConcatenator(a, offset, length);
    }

    public static BooleanIterator unmodifiable(BooleanIterator i) {
        return new UnmodifiableIterator(i);
    }

    public static BooleanBidirectionalIterator unmodifiable(BooleanBidirectionalIterator i) {
        return new UnmodifiableBidirectionalIterator(i);
    }

    public static BooleanListIterator unmodifiable(BooleanListIterator i) {
        return new UnmodifiableListIterator(i);
    }

    public static class UnmodifiableListIterator
    implements BooleanListIterator {
        protected final BooleanListIterator i;

        public UnmodifiableListIterator(BooleanListIterator i) {
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
        public boolean nextBoolean() {
            return this.i.nextBoolean();
        }

        @Override
        public boolean previousBoolean() {
            return this.i.previousBoolean();
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
    implements BooleanBidirectionalIterator {
        protected final BooleanBidirectionalIterator i;

        public UnmodifiableBidirectionalIterator(BooleanBidirectionalIterator i) {
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
        public boolean nextBoolean() {
            return this.i.nextBoolean();
        }

        @Override
        public boolean previousBoolean() {
            return this.i.previousBoolean();
        }
    }

    public static class UnmodifiableIterator
    implements BooleanIterator {
        protected final BooleanIterator i;

        public UnmodifiableIterator(BooleanIterator i) {
            this.i = i;
        }

        @Override
        public boolean hasNext() {
            return this.i.hasNext();
        }

        @Override
        public boolean nextBoolean() {
            return this.i.nextBoolean();
        }
    }

    private static class IteratorConcatenator
    implements BooleanIterator {
        final BooleanIterator[] a;
        int offset;
        int length;
        int lastOffset = -1;

        public IteratorConcatenator(BooleanIterator[] a, int offset, int length) {
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
        public boolean nextBoolean() {
            if (!this.hasNext()) {
                throw new NoSuchElementException();
            }
            this.lastOffset = this.offset;
            boolean next = this.a[this.lastOffset].nextBoolean();
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
    implements BooleanListIterator {
        final ListIterator<Boolean> i;

        public ListIteratorWrapper(ListIterator<Boolean> i) {
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
        public void set(boolean k) {
            this.i.set(k);
        }

        @Override
        public void add(boolean k) {
            this.i.add(k);
        }

        @Override
        public void remove() {
            this.i.remove();
        }

        @Override
        public boolean nextBoolean() {
            return this.i.next();
        }

        @Override
        public boolean previousBoolean() {
            return this.i.previous();
        }
    }

    private static class IteratorWrapper
    implements BooleanIterator {
        final Iterator<Boolean> i;

        public IteratorWrapper(Iterator<Boolean> i) {
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
        public boolean nextBoolean() {
            return this.i.next();
        }
    }

    private static class ArrayIterator
    implements BooleanListIterator {
        private final boolean[] array;
        private final int offset;
        private final int length;
        private int curr;

        public ArrayIterator(boolean[] array, int offset, int length) {
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
        public boolean nextBoolean() {
            if (!this.hasNext()) {
                throw new NoSuchElementException();
            }
            return this.array[this.offset + this.curr++];
        }

        @Override
        public boolean previousBoolean() {
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
    implements BooleanListIterator {
        private final boolean element;
        private int curr;

        public SingletonIterator(boolean element) {
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
        public boolean nextBoolean() {
            if (!this.hasNext()) {
                throw new NoSuchElementException();
            }
            this.curr = 1;
            return this.element;
        }

        @Override
        public boolean previousBoolean() {
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
    implements BooleanListIterator,
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
        public boolean nextBoolean() {
            throw new NoSuchElementException();
        }

        @Override
        public boolean previousBoolean() {
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
            return BooleanIterators.EMPTY_ITERATOR;
        }

        private Object readResolve() {
            return BooleanIterators.EMPTY_ITERATOR;
        }
    }

}

