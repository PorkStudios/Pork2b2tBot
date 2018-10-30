/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.longs.LongBigListIterator;
import it.unimi.dsi.fastutil.longs.LongListIterator;
import java.io.Serializable;
import java.util.NoSuchElementException;

public final class LongBigListIterators {
    public static final EmptyBigListIterator EMPTY_BIG_LIST_ITERATOR = new EmptyBigListIterator();

    private LongBigListIterators() {
    }

    public static LongBigListIterator singleton(long element) {
        return new SingletonBigListIterator(element);
    }

    public static LongBigListIterator unmodifiable(LongBigListIterator i) {
        return new UnmodifiableBigListIterator(i);
    }

    public static LongBigListIterator asBigListIterator(LongListIterator i) {
        return new BigListIteratorListIterator(i);
    }

    public static class BigListIteratorListIterator
    implements LongBigListIterator {
        protected final LongListIterator i;

        protected BigListIteratorListIterator(LongListIterator i) {
            this.i = i;
        }

        private int intDisplacement(long n) {
            if (n < Integer.MIN_VALUE || n > Integer.MAX_VALUE) {
                throw new IndexOutOfBoundsException("This big iterator is restricted to 32-bit displacements");
            }
            return (int)n;
        }

        @Override
        public void set(long ok) {
            this.i.set(ok);
        }

        @Override
        public void add(long ok) {
            this.i.add(ok);
        }

        @Override
        public int back(int n) {
            return this.i.back(n);
        }

        @Override
        public long back(long n) {
            return this.i.back(this.intDisplacement(n));
        }

        @Override
        public void remove() {
            this.i.remove();
        }

        @Override
        public int skip(int n) {
            return this.i.skip(n);
        }

        @Override
        public long skip(long n) {
            return this.i.skip(this.intDisplacement(n));
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
        public long nextIndex() {
            return this.i.nextIndex();
        }

        @Override
        public long previousIndex() {
            return this.i.previousIndex();
        }
    }

    public static class UnmodifiableBigListIterator
    implements LongBigListIterator {
        protected final LongBigListIterator i;

        public UnmodifiableBigListIterator(LongBigListIterator i) {
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
        public long nextIndex() {
            return this.i.nextIndex();
        }

        @Override
        public long previousIndex() {
            return this.i.previousIndex();
        }
    }

    private static class SingletonBigListIterator
    implements LongBigListIterator {
        private final long element;
        private int curr;

        public SingletonBigListIterator(long element) {
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
        public long nextIndex() {
            return this.curr;
        }

        @Override
        public long previousIndex() {
            return this.curr - 1;
        }
    }

    public static class EmptyBigListIterator
    implements LongBigListIterator,
    Serializable,
    Cloneable {
        private static final long serialVersionUID = -7046029254386353129L;

        protected EmptyBigListIterator() {
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
        public long nextIndex() {
            return 0L;
        }

        @Override
        public long previousIndex() {
            return -1L;
        }

        @Override
        public long skip(long n) {
            return 0L;
        }

        @Override
        public long back(long n) {
            return 0L;
        }

        public Object clone() {
            return LongBigListIterators.EMPTY_BIG_LIST_ITERATOR;
        }

        private Object readResolve() {
            return LongBigListIterators.EMPTY_BIG_LIST_ITERATOR;
        }
    }

}

