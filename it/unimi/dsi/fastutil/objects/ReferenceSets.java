/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.objects.AbstractReferenceSet;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectIterators;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import it.unimi.dsi.fastutil.objects.ReferenceCollection;
import it.unimi.dsi.fastutil.objects.ReferenceCollections;
import it.unimi.dsi.fastutil.objects.ReferenceSet;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

public final class ReferenceSets {
    public static final EmptySet EMPTY_SET = new EmptySet();

    private ReferenceSets() {
    }

    public static <K> ReferenceSet<K> emptySet() {
        return EMPTY_SET;
    }

    public static <K> ReferenceSet<K> singleton(K element) {
        return new Singleton<K>(element);
    }

    public static <K> ReferenceSet<K> synchronize(ReferenceSet<K> s) {
        return new SynchronizedSet<K>(s);
    }

    public static <K> ReferenceSet<K> synchronize(ReferenceSet<K> s, Object sync) {
        return new SynchronizedSet<K>(s, sync);
    }

    public static <K> ReferenceSet<K> unmodifiable(ReferenceSet<K> s) {
        return new UnmodifiableSet<K>(s);
    }

    public static class UnmodifiableSet<K>
    extends ReferenceCollections.UnmodifiableCollection<K>
    implements ReferenceSet<K>,
    Serializable {
        private static final long serialVersionUID = -7046029254386353129L;

        protected UnmodifiableSet(ReferenceSet<K> s) {
            super(s);
        }

        @Override
        public boolean remove(Object k) {
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
    }

    public static class SynchronizedSet<K>
    extends ReferenceCollections.SynchronizedCollection<K>
    implements ReferenceSet<K>,
    Serializable {
        private static final long serialVersionUID = -7046029254386353129L;

        protected SynchronizedSet(ReferenceSet<K> s, Object sync) {
            super(s, sync);
        }

        protected SynchronizedSet(ReferenceSet<K> s) {
            super(s);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean remove(Object k) {
            Object object = this.sync;
            synchronized (object) {
                return this.collection.remove(k);
            }
        }
    }

    public static class Singleton<K>
    extends AbstractReferenceSet<K>
    implements Serializable,
    Cloneable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final K element;

        protected Singleton(K element) {
            this.element = element;
        }

        @Override
        public boolean contains(Object k) {
            return k == this.element;
        }

        @Override
        public boolean remove(Object k) {
            throw new UnsupportedOperationException();
        }

        @Override
        public ObjectListIterator<K> iterator() {
            return ObjectIterators.singleton(this.element);
        }

        @Override
        public int size() {
            return 1;
        }

        @Override
        public boolean addAll(Collection<? extends K> c) {
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

        public Object clone() {
            return this;
        }
    }

    public static class EmptySet<K>
    extends ReferenceCollections.EmptyCollection<K>
    implements ReferenceSet<K>,
    Serializable,
    Cloneable {
        private static final long serialVersionUID = -7046029254386353129L;

        protected EmptySet() {
        }

        @Override
        public boolean remove(Object ok) {
            throw new UnsupportedOperationException();
        }

        public Object clone() {
            return ReferenceSets.EMPTY_SET;
        }

        @Override
        public boolean equals(Object o) {
            return o instanceof Set && ((Set)o).isEmpty();
        }

        private Object readResolve() {
            return ReferenceSets.EMPTY_SET;
        }
    }

}

