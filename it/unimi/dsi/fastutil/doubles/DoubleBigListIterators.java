/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.doubles;

import it.unimi.dsi.fastutil.doubles.DoubleBigListIterator;
import it.unimi.dsi.fastutil.doubles.DoubleListIterator;
import java.io.Serializable;
import java.util.NoSuchElementException;

public final class DoubleBigListIterators {
    public static final EmptyBigListIterator EMPTY_BIG_LIST_ITERATOR = new EmptyBigListIterator();

    private DoubleBigListIterators() {
    }

    public static DoubleBigListIterator singleton(double element) {
        return new SingletonBigListIterator(element);
    }

    public static DoubleBigListIterator unmodifiable(DoubleBigListIterator i) {
        return new UnmodifiableBigListIterator(i);
    }

    public static DoubleBigListIterator asBigListIterator(DoubleListIterator i) {
        return new BigListIteratorListIterator(i);
    }

    public static class BigListIteratorListIterator
    implements DoubleBigListIterator {
        protected final DoubleListIterator i;

        protected BigListIteratorListIterator(DoubleListIterator i) {
            this.i = i;
        }

        private int intDisplacement(long n) {
            if (n < Integer.MIN_VALUE || n > Integer.MAX_VALUE) {
                throw new IndexOutOfBoundsException("This big iterator is restricted to 32-bit displacements");
            }
            return (int)n;
        }

        @Override
        public void set(double ok) {
            this.i.set(ok);
        }

        @Override
        public void add(double ok) {
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
        public double nextDouble() {
            return this.i.nextDouble();
        }

        @Override
        public double previousDouble() {
            return this.i.previousDouble();
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
    implements DoubleBigListIterator {
        protected final DoubleBigListIterator i;

        public UnmodifiableBigListIterator(DoubleBigListIterator i) {
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
        public long nextIndex() {
            return this.i.nextIndex();
        }

        @Override
        public long previousIndex() {
            return this.i.previousIndex();
        }
    }

    private static class SingletonBigListIterator
    implements DoubleBigListIterator {
        private final double element;
        private int curr;

        public SingletonBigListIterator(double element) {
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
        public long nextIndex() {
            return this.curr;
        }

        @Override
        public long previousIndex() {
            return this.curr - 1;
        }
    }

    public static class EmptyBigListIterator
    implements DoubleBigListIterator,
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
        public double nextDouble() {
            throw new NoSuchElementException();
        }

        @Override
        public double previousDouble() {
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
            return DoubleBigListIterators.EMPTY_BIG_LIST_ITERATOR;
        }

        private Object readResolve() {
            return DoubleBigListIterators.EMPTY_BIG_LIST_ITERATOR;
        }
    }

}

