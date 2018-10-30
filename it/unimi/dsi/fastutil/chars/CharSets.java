/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.chars.AbstractCharSet;
import it.unimi.dsi.fastutil.chars.CharCollection;
import it.unimi.dsi.fastutil.chars.CharCollections;
import it.unimi.dsi.fastutil.chars.CharIterator;
import it.unimi.dsi.fastutil.chars.CharIterators;
import it.unimi.dsi.fastutil.chars.CharListIterator;
import it.unimi.dsi.fastutil.chars.CharSet;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

public final class CharSets {
    public static final EmptySet EMPTY_SET = new EmptySet();

    private CharSets() {
    }

    public static CharSet singleton(char element) {
        return new Singleton(element);
    }

    public static CharSet singleton(Character element) {
        return new Singleton(element.charValue());
    }

    public static CharSet synchronize(CharSet s) {
        return new SynchronizedSet(s);
    }

    public static CharSet synchronize(CharSet s, Object sync) {
        return new SynchronizedSet(s, sync);
    }

    public static CharSet unmodifiable(CharSet s) {
        return new UnmodifiableSet(s);
    }

    public static class UnmodifiableSet
    extends CharCollections.UnmodifiableCollection
    implements CharSet,
    Serializable {
        private static final long serialVersionUID = -7046029254386353129L;

        protected UnmodifiableSet(CharSet s) {
            super(s);
        }

        @Override
        public boolean remove(char k) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            return this.collection.equals(o);
        }

        @Override
        public int hashCode() {
            return this.collection.hashCode();
        }

        @Deprecated
        @Override
        public boolean rem(char k) {
            return super.rem(k);
        }
    }

    public static class SynchronizedSet
    extends CharCollections.SynchronizedCollection
    implements CharSet,
    Serializable {
        private static final long serialVersionUID = -7046029254386353129L;

        protected SynchronizedSet(CharSet s, Object sync) {
            super(s, sync);
        }

        protected SynchronizedSet(CharSet s) {
            super(s);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean remove(char k) {
            Object object = this.sync;
            synchronized (object) {
                return this.collection.rem(k);
            }
        }

        @Deprecated
        @Override
        public boolean rem(char k) {
            return super.rem(k);
        }
    }

    public static class Singleton
    extends AbstractCharSet
    implements Serializable,
    Cloneable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final char element;

        protected Singleton(char element) {
            this.element = element;
        }

        @Override
        public boolean contains(char k) {
            return k == this.element;
        }

        @Override
        public boolean remove(char k) {
            throw new UnsupportedOperationException();
        }

        @Override
        public CharListIterator iterator() {
            return CharIterators.singleton(this.element);
        }

        @Override
        public int size() {
            return 1;
        }

        @Override
        public boolean addAll(Collection<? extends Character> c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean removeAll(Collection<?> c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean retainAll(Collection<?> c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(CharCollection c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean removeAll(CharCollection c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean retainAll(CharCollection c) {
            throw new UnsupportedOperationException();
        }

        public Object clone() {
            return this;
        }
    }

    public static class EmptySet
    extends CharCollections.EmptyCollection
    implements CharSet,
    Serializable,
    Cloneable {
        private static final long serialVersionUID = -7046029254386353129L;

        protected EmptySet() {
        }

        @Override
        public boolean remove(char ok) {
            throw new UnsupportedOperationException();
        }

        public Object clone() {
            return CharSets.EMPTY_SET;
        }

        @Override
        public boolean equals(Object o) {
            return o instanceof Set && ((Set)o).isEmpty();
        }

        @Deprecated
        @Override
        public boolean rem(char k) {
            return super.rem(k);
        }

        private Object readResolve() {
            return CharSets.EMPTY_SET;
        }
    }

}

