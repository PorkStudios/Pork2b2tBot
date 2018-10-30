/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.Function;
import it.unimi.dsi.fastutil.objects.AbstractReference2ObjectFunction;
import it.unimi.dsi.fastutil.objects.Reference2ObjectFunction;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public final class Reference2ObjectFunctions {
    public static final EmptyFunction EMPTY_FUNCTION = new EmptyFunction();

    private Reference2ObjectFunctions() {
    }

    public static <K, V> Reference2ObjectFunction<K, V> singleton(K key, V value) {
        return new Singleton<K, V>(key, value);
    }

    public static <K, V> Reference2ObjectFunction<K, V> synchronize(Reference2ObjectFunction<K, V> f) {
        return new SynchronizedFunction<K, V>(f);
    }

    public static <K, V> Reference2ObjectFunction<K, V> synchronize(Reference2ObjectFunction<K, V> f, Object sync) {
        return new SynchronizedFunction<K, V>(f, sync);
    }

    public static <K, V> Reference2ObjectFunction<K, V> unmodifiable(Reference2ObjectFunction<K, V> f) {
        return new UnmodifiableFunction<K, V>(f);
    }

    public static class UnmodifiableFunction<K, V>
    extends AbstractReference2ObjectFunction<K, V>
    implements Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final Reference2ObjectFunction<K, V> function;

        protected UnmodifiableFunction(Reference2ObjectFunction<K, V> f) {
            if (f == null) {
                throw new NullPointerException();
            }
            this.function = f;
        }

        @Override
        public int size() {
            return this.function.size();
        }

        @Override
        public V defaultReturnValue() {
            return this.function.defaultReturnValue();
        }

        @Override
        public void defaultReturnValue(V defRetValue) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean containsKey(Object k) {
            return this.function.containsKey(k);
        }

        @Override
        public V put(K k, V v) {
            throw new UnsupportedOperationException();
        }

        @Override
        public V get(Object k) {
            return this.function.get(k);
        }

        @Override
        public V remove(Object k) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void clear() {
            throw new UnsupportedOperationException();
        }

        public int hashCode() {
            return this.function.hashCode();
        }

        public boolean equals(Object o) {
            return o == this || this.function.equals(o);
        }

        public String toString() {
            return this.function.toString();
        }
    }

    public static class SynchronizedFunction<K, V>
    implements Reference2ObjectFunction<K, V>,
    Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final Reference2ObjectFunction<K, V> function;
        protected final Object sync;

        protected SynchronizedFunction(Reference2ObjectFunction<K, V> f, Object sync) {
            if (f == null) {
                throw new NullPointerException();
            }
            this.function = f;
            this.sync = sync;
        }

        protected SynchronizedFunction(Reference2ObjectFunction<K, V> f) {
            if (f == null) {
                throw new NullPointerException();
            }
            this.function = f;
            this.sync = this;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public V apply(K key) {
            Object object = this.sync;
            synchronized (object) {
                return this.function.apply(key);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public int size() {
            Object object = this.sync;
            synchronized (object) {
                return this.function.size();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public V defaultReturnValue() {
            Object object = this.sync;
            synchronized (object) {
                return this.function.defaultReturnValue();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void defaultReturnValue(V defRetValue) {
            Object object = this.sync;
            synchronized (object) {
                this.function.defaultReturnValue(defRetValue);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean containsKey(Object k) {
            Object object = this.sync;
            synchronized (object) {
                return this.function.containsKey(k);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public V put(K k, V v) {
            Object object = this.sync;
            synchronized (object) {
                return this.function.put(k, v);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public V get(Object k) {
            Object object = this.sync;
            synchronized (object) {
                return this.function.get(k);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public V remove(Object k) {
            Object object = this.sync;
            synchronized (object) {
                return this.function.remove(k);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void clear() {
            Object object = this.sync;
            synchronized (object) {
                this.function.clear();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public int hashCode() {
            Object object = this.sync;
            synchronized (object) {
                return this.function.hashCode();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            Object object = this.sync;
            synchronized (object) {
                return this.function.equals(o);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public String toString() {
            Object object = this.sync;
            synchronized (object) {
                return this.function.toString();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        private void writeObject(ObjectOutputStream s) throws IOException {
            Object object = this.sync;
            synchronized (object) {
                s.defaultWriteObject();
            }
        }
    }

    public static class Singleton<K, V>
    extends AbstractReference2ObjectFunction<K, V>
    implements Serializable,
    Cloneable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final K key;
        protected final V value;

        protected Singleton(K key, V value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public boolean containsKey(Object k) {
            return this.key == k;
        }

        @Override
        public V get(Object k) {
            return (V)(this.key == k ? this.value : this.defRetValue);
        }

        @Override
        public int size() {
            return 1;
        }

        public Object clone() {
            return this;
        }
    }

    public static class EmptyFunction<K, V>
    extends AbstractReference2ObjectFunction<K, V>
    implements Serializable,
    Cloneable {
        private static final long serialVersionUID = -7046029254386353129L;

        protected EmptyFunction() {
        }

        @Override
        public V get(Object k) {
            return null;
        }

        @Override
        public boolean containsKey(Object k) {
            return false;
        }

        @Override
        public V defaultReturnValue() {
            return null;
        }

        @Override
        public void defaultReturnValue(V defRetValue) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int size() {
            return 0;
        }

        @Override
        public void clear() {
        }

        public Object clone() {
            return Reference2ObjectFunctions.EMPTY_FUNCTION;
        }

        public int hashCode() {
            return 0;
        }

        public boolean equals(Object o) {
            if (!(o instanceof Function)) {
                return false;
            }
            return ((Function)o).size() == 0;
        }

        public String toString() {
            return "{}";
        }

        private Object readResolve() {
            return Reference2ObjectFunctions.EMPTY_FUNCTION;
        }
    }

}

