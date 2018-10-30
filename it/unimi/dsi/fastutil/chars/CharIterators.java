/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.chars.CharArrayList;
import it.unimi.dsi.fastutil.chars.CharArrays;
import it.unimi.dsi.fastutil.chars.CharBidirectionalIterator;
import it.unimi.dsi.fastutil.chars.CharCollection;
import it.unimi.dsi.fastutil.chars.CharIterator;
import it.unimi.dsi.fastutil.chars.CharList;
import it.unimi.dsi.fastutil.chars.CharListIterator;
import java.io.Serializable;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.IntPredicate;

public final class CharIterators {
    public static final EmptyIterator EMPTY_ITERATOR = new EmptyIterator();

    private CharIterators() {
    }

    public static CharListIterator singleton(char element) {
        return new SingletonIterator(element);
    }

    public static CharListIterator wrap(char[] array, int offset, int length) {
        CharArrays.ensureOffsetLength(array, offset, length);
        return new ArrayIterator(array, offset, length);
    }

    public static CharListIterator wrap(char[] array) {
        return new ArrayIterator(array, 0, array.length);
    }

    public static int unwrap(CharIterator i, char[] array, int offset, int max) {
        if (max < 0) {
            throw new IllegalArgumentException("The maximum number of elements (" + max + ") is negative");
        }
        if (offset < 0 || offset + max > array.length) {
            throw new IllegalArgumentException();
        }
        int j = max;
        while (j-- != 0 && i.hasNext()) {
            array[offset++] = i.nextChar();
        }
        return max - j - 1;
    }

    public static int unwrap(CharIterator i, char[] array) {
        return CharIterators.unwrap(i, array, 0, array.length);
    }

    public static char[] unwrap(CharIterator i, int max) {
        if (max < 0) {
            throw new IllegalArgumentException("The maximum number of elements (" + max + ") is negative");
        }
        char[] array = new char[16];
        int j = 0;
        while (max-- != 0 && i.hasNext()) {
            if (j == array.length) {
                array = CharArrays.grow(array, j + 1);
            }
            array[j++] = i.nextChar();
        }
        return CharArrays.trim(array, j);
    }

    public static char[] unwrap(CharIterator i) {
        return CharIterators.unwrap(i, Integer.MAX_VALUE);
    }

    public static int unwrap(CharIterator i, CharCollection c, int max) {
        if (max < 0) {
            throw new IllegalArgumentException("The maximum number of elements (" + max + ") is negative");
        }
        int j = max;
        while (j-- != 0 && i.hasNext()) {
            c.add(i.nextChar());
        }
        return max - j - 1;
    }

    public static long unwrap(CharIterator i, CharCollection c) {
        long n = 0L;
        while (i.hasNext()) {
            c.add(i.nextChar());
            ++n;
        }
        return n;
    }

    public static int pour(CharIterator i, CharCollection s, int max) {
        if (max < 0) {
            throw new IllegalArgumentException("The maximum number of elements (" + max + ") is negative");
        }
        int j = max;
        while (j-- != 0 && i.hasNext()) {
            s.add(i.nextChar());
        }
        return max - j - 1;
    }

    public static int pour(CharIterator i, CharCollection s) {
        return CharIterators.pour(i, s, Integer.MAX_VALUE);
    }

    public static CharList pour(CharIterator i, int max) {
        CharArrayList l = new CharArrayList();
        CharIterators.pour(i, l, max);
        l.trim();
        return l;
    }

    public static CharList pour(CharIterator i) {
        return CharIterators.pour(i, Integer.MAX_VALUE);
    }

    public static CharIterator asCharIterator(Iterator i) {
        if (i instanceof CharIterator) {
            return (CharIterator)i;
        }
        return new IteratorWrapper(i);
    }

    public static CharListIterator asCharIterator(ListIterator i) {
        if (i instanceof CharListIterator) {
            return (CharListIterator)i;
        }
        return new ListIteratorWrapper(i);
    }

    public static boolean any(CharIterator iterator, IntPredicate predicate) {
        return CharIterators.indexOf(iterator, predicate) != -1;
    }

    public static boolean all(CharIterator iterator, IntPredicate predicate) {
        Objects.requireNonNull(predicate);
        do {
            if (iterator.hasNext()) continue;
            return true;
        } while (predicate.test(iterator.nextChar()));
        return false;
    }

    public static int indexOf(CharIterator iterator, IntPredicate predicate) {
        Objects.requireNonNull(predicate);
        int i = 0;
        while (iterator.hasNext()) {
            if (predicate.test(iterator.nextChar())) {
                return i;
            }
            ++i;
        }
        return -1;
    }

    public static CharListIterator fromTo(char from, char to) {
        return new IntervalIterator(from, to);
    }

    public static CharIterator concat(CharIterator[] a) {
        return CharIterators.concat(a, 0, a.length);
    }

    public static CharIterator concat(CharIterator[] a, int offset, int length) {
        return new IteratorConcatenator(a, offset, length);
    }

    public static CharIterator unmodifiable(CharIterator i) {
        return new UnmodifiableIterator(i);
    }

    public static CharBidirectionalIterator unmodifiable(CharBidirectionalIterator i) {
        return new UnmodifiableBidirectionalIterator(i);
    }

    public static CharListIterator unmodifiable(CharListIterator i) {
        return new UnmodifiableListIterator(i);
    }

    public static class UnmodifiableListIterator
    implements CharListIterator {
        protected final CharListIterator i;

        public UnmodifiableListIterator(CharListIterator i) {
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
        public char nextChar() {
            return this.i.nextChar();
        }

        @Override
        public char previousChar() {
            return this.i.previousChar();
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
    implements CharBidirectionalIterator {
        protected final CharBidirectionalIterator i;

        public UnmodifiableBidirectionalIterator(CharBidirectionalIterator i) {
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
        public char nextChar() {
            return this.i.nextChar();
        }

        @Override
        public char previousChar() {
            return this.i.previousChar();
        }
    }

    public static class UnmodifiableIterator
    implements CharIterator {
        protected final CharIterator i;

        public UnmodifiableIterator(CharIterator i) {
            this.i = i;
        }

        @Override
        public boolean hasNext() {
            return this.i.hasNext();
        }

        @Override
        public char nextChar() {
            return this.i.nextChar();
        }
    }

    private static class IteratorConcatenator
    implements CharIterator {
        final CharIterator[] a;
        int offset;
        int length;
        int lastOffset = -1;

        public IteratorConcatenator(CharIterator[] a, int offset, int length) {
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
        public char nextChar() {
            if (!this.hasNext()) {
                throw new NoSuchElementException();
            }
            this.lastOffset = this.offset;
            char next = this.a[this.lastOffset].nextChar();
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
    implements CharListIterator {
        private final char from;
        private final char to;
        char curr;

        public IntervalIterator(char from, char to) {
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
        public char nextChar() {
            if (!this.hasNext()) {
                throw new NoSuchElementException();
            }
            char c = this.curr;
            this.curr = (char)(c + '\u0001');
            return c;
        }

        @Override
        public char previousChar() {
            if (!this.hasPrevious()) {
                throw new NoSuchElementException();
            }
            this.curr = (char)(this.curr - '\u0001');
            return this.curr;
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
                this.curr = (char)(this.curr + n);
                return n;
            }
            n = this.to - this.curr;
            this.curr = this.to;
            return n;
        }

        @Override
        public int back(int n) {
            if (this.curr - n >= this.from) {
                this.curr = (char)(this.curr - n);
                return n;
            }
            n = this.curr - this.from;
            this.curr = this.from;
            return n;
        }
    }

    private static class ListIteratorWrapper
    implements CharListIterator {
        final ListIterator<Character> i;

        public ListIteratorWrapper(ListIterator<Character> i) {
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
        public void set(char k) {
            this.i.set(Character.valueOf(k));
        }

        @Override
        public void add(char k) {
            this.i.add(Character.valueOf(k));
        }

        @Override
        public void remove() {
            this.i.remove();
        }

        @Override
        public char nextChar() {
            return this.i.next().charValue();
        }

        @Override
        public char previousChar() {
            return this.i.previous().charValue();
        }
    }

    private static class IteratorWrapper
    implements CharIterator {
        final Iterator<Character> i;

        public IteratorWrapper(Iterator<Character> i) {
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
        public char nextChar() {
            return this.i.next().charValue();
        }
    }

    private static class ArrayIterator
    implements CharListIterator {
        private final char[] array;
        private final int offset;
        private final int length;
        private int curr;

        public ArrayIterator(char[] array, int offset, int length) {
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
        public char nextChar() {
            if (!this.hasNext()) {
                throw new NoSuchElementException();
            }
            return this.array[this.offset + this.curr++];
        }

        @Override
        public char previousChar() {
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
    implements CharListIterator {
        private final char element;
        private int curr;

        public SingletonIterator(char element) {
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
        public char nextChar() {
            if (!this.hasNext()) {
                throw new NoSuchElementException();
            }
            this.curr = 1;
            return this.element;
        }

        @Override
        public char previousChar() {
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
    implements CharListIterator,
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
        public char nextChar() {
            throw new NoSuchElementException();
        }

        @Override
        public char previousChar() {
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
            return CharIterators.EMPTY_ITERATOR;
        }

        private Object readResolve() {
            return CharIterators.EMPTY_ITERATOR;
        }
    }

}

