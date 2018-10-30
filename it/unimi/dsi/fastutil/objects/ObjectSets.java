/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.objects.AbstractObjectSet;
import it.unimi.dsi.fastutil.objects.ObjectCollection;
import it.unimi.dsi.fastutil.objects.ObjectCollections;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectIterators;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;

public final class ObjectSets {
    public static final EmptySet EMPTY_SET = new EmptySet();

    private ObjectSets() {
    }

    public static <K> ObjectSet<K> emptySet() {
        return EMPTY_SET;
    }

    public static <K> ObjectSet<K> singleton(K element) {
        return new Singleton<K>(element);
    }

    public static <K> ObjectSet<K> synchronize(ObjectSet<K> s) {
        return new SynchronizedSet<K>(s);
    }

    public static <K> ObjectSet<K> synchronize(ObjectSet<K> s, Object sync) {
        return new SynchronizedSet<K>(s, sync);
    }

    public static <K> ObjectSet<K> unmodifiable(ObjectSet<K> s) {
        return new UnmodifiableSet<K>(s);
    }

    public static class UnmodifiableSet<K>
    extends ObjectCollections.UnmodifiableCollection<K>
    implements ObjectSet<K>,
    Serializable {
        private static final long serialVersionUID = -7046029254386353129L;

        protected UnmodifiableSet(ObjectSet<K> s) {
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
    extends ObjectCollections.SynchronizedCollection<K>
    implements ObjectSet<K>,
    Serializable {
        private static final long serialVersionUID = -7046029254386353129L;

        protected SynchronizedSet(ObjectSet<K> s, Object sync) {
            super(s, sync);
        }

        protected SynchronizedSet(ObjectSet<K> s) {
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
    extends AbstractObjectSet<K>
    implements Serializable,
    Cloneable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final K element;

        protected Singleton(K element) {
            this.element = element;
        }

        @Override
        public boolean contains(Object k) {
            return Objects.equals(k, this.element);
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
    extends ObjectCollections.EmptyCollection<K>
    implements ObjectSet<K>,
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
            return ObjectSets.EMPTY_SET;
        }

        @Override
        public boolean equals(Object o) {
            return o instanceof Set && ((Set)o).isEmpty();
        }

        private Object readResolve() {
            return ObjectSets.EMPTY_SET;
        }
    }

}

