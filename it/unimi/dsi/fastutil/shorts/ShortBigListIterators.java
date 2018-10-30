/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.shorts;

import it.unimi.dsi.fastutil.shorts.ShortBigListIterator;
import it.unimi.dsi.fastutil.shorts.ShortListIterator;
import java.io.Serializable;
import java.util.NoSuchElementException;

public final class ShortBigListIterators {
    public static final EmptyBigListIterator EMPTY_BIG_LIST_ITERATOR = new EmptyBigListIterator();

    private ShortBigListIterators() {
    }

    public static ShortBigListIterator singleton(short element) {
        return new SingletonBigListIterator(element);
    }

    public static ShortBigListIterator unmodifiable(ShortBigListIterator i) {
        return new UnmodifiableBigListIterator(i);
    }

    public static ShortBigListIterator asBigListIterator(ShortListIterator i) {
        return new BigListIteratorListIterator(i);
    }

    public static class BigListIteratorListIterator
    implements ShortBigListIterator {
        protected final ShortListIterator i;

        protected BigListIteratorListIterator(ShortListIterator i) {
            this.i = i;
        }

        private int intDisplacement(long n) {
            if (n < Integer.MIN_VALUE || n > Integer.MAX_VALUE) {
                throw new IndexOutOfBoundsException("This big iterator is restricted to 32-bit displacements");
            }
            return (int)n;
        }

        @Override
        public void set(short ok) {
            this.i.set(ok);
        }

        @Override
        public void add(short ok) {
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
        public short nextShort() {
            return this.i.nextShort();
        }

        @Override
        public short previousShort() {
            return this.i.previousShort();
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
    implements ShortBigListIterator {
        protected final ShortBigListIterator i;

        public UnmodifiableBigListIterator(ShortBigListIterator i) {
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
        public short nextShort() {
            return this.i.nextShort();
        }

        @Override
        public short previousShort() {
            return this.i.previousShort();
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
    implements ShortBigListIterator {
        private final short element;
        private int curr;

        public SingletonBigListIterator(short element) {
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
        public short nextShort() {
            if (!this.hasNext()) {
                throw new NoSuchElementException();
            }
            this.curr = 1;
            return this.element;
        }

        @Override
        public short previousShort() {
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
    implements ShortBigListIterator,
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
        public short nextShort() {
            throw new NoSuchElementException();
        }

        @Override
        public short previousShort() {
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
            return ShortBigListIterators.EMPTY_BIG_LIST_ITERATOR;
        }

        private Object readResolve() {
            return ShortBigListIterators.EMPTY_BIG_LIST_ITERATOR;
        }
    }

}

