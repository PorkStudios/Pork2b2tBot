/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.bytes.ByteBidirectionalIterator;
import it.unimi.dsi.fastutil.bytes.ByteComparator;
import it.unimi.dsi.fastutil.bytes.ByteIterator;
import it.unimi.dsi.fastutil.bytes.ByteIterators;
import it.unimi.dsi.fastutil.bytes.ByteListIterator;
import it.unimi.dsi.fastutil.bytes.ByteSet;
import it.unimi.dsi.fastutil.bytes.ByteSets;
import it.unimi.dsi.fastutil.bytes.ByteSortedSet;
import java.io.Serializable;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.SortedSet;

public final class ByteSortedSets {
    public static final EmptySet EMPTY_SET = new EmptySet();

    private ByteSortedSets() {
    }

    public static ByteSortedSet singleton(byte element) {
        return new Singleton(element);
    }

    public static ByteSortedSet singleton(byte element, ByteComparator comparator) {
        return new Singleton(element, comparator);
    }

    public static ByteSortedSet singleton(Object element) {
        return new Singleton((byte)((Byte)element));
    }

    public static ByteSortedSet singleton(Object element, ByteComparator comparator) {
        return new Singleton((byte)((Byte)element), comparator);
    }

    public static ByteSortedSet synchronize(ByteSortedSet s) {
        return new SynchronizedSortedSet(s);
    }

    public static ByteSortedSet synchronize(ByteSortedSet s, Object sync) {
        return new SynchronizedSortedSet(s, sync);
    }

    public static ByteSortedSet unmodifiable(ByteSortedSet s) {
        return new UnmodifiableSortedSet(s);
    }

    public static class UnmodifiableSortedSet
    extends ByteSets.UnmodifiableSet
    implements ByteSortedSet,
    Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final ByteSortedSet sortedSet;

        protected UnmodifiableSortedSet(ByteSortedSet s) {
            super(s);
            this.sortedSet = s;
        }

        @Override
        public ByteComparator comparator() {
            return this.sortedSet.comparator();
        }

        @Override
        public ByteSortedSet subSet(byte from, byte to) {
            return new UnmodifiableSortedSet(this.sortedSet.subSet(from, to));
        }

        @Override
        public ByteSortedSet headSet(byte to) {
            return new UnmodifiableSortedSet(this.sortedSet.headSet(to));
        }

        @Override
        public ByteSortedSet tailSet(byte from) {
            return new UnmodifiableSortedSet(this.sortedSet.tailSet(from));
        }

        @Override
        public ByteBidirectionalIterator iterator() {
            return ByteIterators.unmodifiable(this.sortedSet.iterator());
        }

        @Override
        public ByteBidirectionalIterator iterator(byte from) {
            return ByteIterators.unmodifiable(this.sortedSet.iterator(from));
        }

        @Override
        public byte firstByte() {
            return this.sortedSet.firstByte();
        }

        @Override
        public byte lastByte() {
            return this.sortedSet.lastByte();
        }

        @Deprecated
        @Override
        public Byte first() {
            return this.sortedSet.first();
        }

        @Deprecated
        @Override
        public Byte last() {
            return this.sortedSet.last();
        }

        @Deprecated
        @Override
        public ByteSortedSet subSet(Byte from, Byte to) {
            return new UnmodifiableSortedSet(this.sortedSet.subSet(from, to));
        }

        @Deprecated
        @Override
        public ByteSortedSet headSet(Byte to) {
            return new UnmodifiableSortedSet(this.sortedSet.headSet(to));
        }

        @Deprecated
        @Override
        public ByteSortedSet tailSet(Byte from) {
            return new UnmodifiableSortedSet(this.sortedSet.tailSet(from));
        }
    }

    public static class SynchronizedSortedSet
    extends ByteSets.SynchronizedSet
    implements ByteSortedSet,
    Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final ByteSortedSet sortedSet;

        protected SynchronizedSortedSet(ByteSortedSet s, Object sync) {
            super(s, sync);
            this.sortedSet = s;
        }

        protected SynchronizedSortedSet(ByteSortedSet s) {
            super(s);
            this.sortedSet = s;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public ByteComparator comparator() {
            Object object = this.sync;
            synchronized (object) {
                return this.sortedSet.comparator();
            }
        }

        @Override
        public ByteSortedSet subSet(byte from, byte to) {
            return new SynchronizedSortedSet(this.sortedSet.subSet(from, to), this.sync);
        }

        @Override
        public ByteSortedSet headSet(byte to) {
            return new SynchronizedSortedSet(this.sortedSet.headSet(to), this.sync);
        }

        @Override
        public ByteSortedSet tailSet(byte from) {
            return new SynchronizedSortedSet(this.sortedSet.tailSet(from), this.sync);
        }

        @Override
        public ByteBidirectionalIterator iterator() {
            return this.sortedSet.iterator();
        }

        @Override
        public ByteBidirectionalIterator iterator(byte from) {
            return this.sortedSet.iterator(from);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public byte firstByte() {
            Object object = this.sync;
            synchronized (object) {
                return this.sortedSet.firstByte();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public byte lastByte() {
            Object object = this.sync;
            synchronized (object) {
                return this.sortedSet.lastByte();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Deprecated
        @Override
        public Byte first() {
            Object object = this.sync;
            synchronized (object) {
                return this.sortedSet.first();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Deprecated
        @Override
        public Byte last() {
            Object object = this.sync;
            synchronized (object) {
                return this.sortedSet.last();
            }
        }

        @Deprecated
        @Override
        public ByteSortedSet subSet(Byte from, Byte to) {
            return new SynchronizedSortedSet(this.sortedSet.subSet(from, to), this.sync);
        }

        @Deprecated
        @Override
        public ByteSortedSet headSet(Byte to) {
            return new SynchronizedSortedSet(this.sortedSet.headSet(to), this.sync);
        }

        @Deprecated
        @Override
        public ByteSortedSet tailSet(Byte from) {
            return new SynchronizedSortedSet(this.sortedSet.tailSet(from), this.sync);
        }
    }

    public static class Singleton
    extends ByteSets.Singleton
    implements ByteSortedSet,
    Serializable,
    Cloneable {
        private static final long serialVersionUID = -7046029254386353129L;
        final ByteComparator comparator;

        protected Singleton(byte element, ByteComparator comparator) {
            super(element);
            this.comparator = comparator;
        }

        private Singleton(byte element) {
            this(element, (ByteComparator)null);
        }

        final int compare(byte k1, byte k2) {
            return this.comparator == null ? Byte.compare(k1, k2) : this.comparator.compare(k1, k2);
        }

        @Override
        public ByteBidirectionalIterator iterator(byte from) {
            ByteBidirectionalIterator i = this.iterator();
            if (this.compare(this.element, from) <= 0) {
                i.nextByte();
            }
            return i;
        }

        @Override
        public ByteComparator comparator() {
            return this.comparator;
        }

        @Override
        public ByteSortedSet subSet(byte from, byte to) {
            if (this.compare(from, this.element) <= 0 && this.compare(this.element, to) < 0) {
                return this;
            }
            return ByteSortedSets.EMPTY_SET;
        }

        @Override
        public ByteSortedSet headSet(byte to) {
            if (this.compare(this.element, to) < 0) {
                return this;
            }
            return ByteSortedSets.EMPTY_SET;
        }

        @Override
        public ByteSortedSet tailSet(byte from) {
            if (this.compare(from, this.element) <= 0) {
                return this;
            }
            return ByteSortedSets.EMPTY_SET;
        }

        @Override
        public byte firstByte() {
            return this.element;
        }

        @Override
        public byte lastByte() {
            return this.element;
        }

        @Deprecated
        @Override
        public ByteSortedSet subSet(Byte from, Byte to) {
            return this.subSet((byte)from, (byte)to);
        }

        @Deprecated
        @Override
        public ByteSortedSet headSet(Byte to) {
            return this.headSet((byte)to);
        }

        @Deprecated
        @Override
        public ByteSortedSet tailSet(Byte from) {
            return this.tailSet((byte)from);
        }

        @Deprecated
        @Override
        public Byte first() {
            return this.element;
        }

        @Deprecated
        @Override
        public Byte last() {
            return this.element;
        }
    }

    public static class EmptySet
    extends ByteSets.EmptySet
    implements ByteSortedSet,
    Serializable,
    Cloneable {
        private static final long serialVersionUID = -7046029254386353129L;

        protected EmptySet() {
        }

        @Override
        public ByteBidirectionalIterator iterator(byte from) {
            return ByteIterators.EMPTY_ITERATOR;
        }

        @Override
        public ByteSortedSet subSet(byte from, byte to) {
            return ByteSortedSets.EMPTY_SET;
        }

        @Override
        public ByteSortedSet headSet(byte from) {
            return ByteSortedSets.EMPTY_SET;
        }

        @Override
        public ByteSortedSet tailSet(byte to) {
            return ByteSortedSets.EMPTY_SET;
        }

        @Override
        public byte firstByte() {
            throw new NoSuchElementException();
        }

        @Override
        public byte lastByte() {
            throw new NoSuchElementException();
        }

        @Override
        public ByteComparator comparator() {
            return null;
        }

        @Deprecated
        @Override
        public ByteSortedSet subSet(Byte from, Byte to) {
            return ByteSortedSets.EMPTY_SET;
        }

        @Deprecated
        @Override
        public ByteSortedSet headSet(Byte from) {
            return ByteSortedSets.EMPTY_SET;
        }

        @Deprecated
        @Override
        public ByteSortedSet tailSet(Byte to) {
            return ByteSortedSets.EMPTY_SET;
        }

        @Deprecated
        @Override
        public Byte first() {
            throw new NoSuchElementException();
        }

        @Deprecated
        @Override
        public Byte last() {
            throw new NoSuchElementException();
        }

        @Override
        public Object clone() {
            return ByteSortedSets.EMPTY_SET;
        }

        private Object readResolve() {
            return ByteSortedSets.EMPTY_SET;
        }
    }

}

