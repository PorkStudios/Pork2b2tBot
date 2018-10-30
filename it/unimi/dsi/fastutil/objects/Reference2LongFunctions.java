/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.Function;
import it.unimi.dsi.fastutil.objects.AbstractReference2LongFunction;
import it.unimi.dsi.fastutil.objects.Reference2LongFunction;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Objects;
import java.util.function.ToLongFunction;

public final class Reference2LongFunctions {
    public static final EmptyFunction EMPTY_FUNCTION = new EmptyFunction();

    private Reference2LongFunctions() {
    }

    public static <K> Reference2LongFunction<K> singleton(K key, long value) {
        return new Singleton<K>(key, value);
    }

    public static <K> Reference2LongFunction<K> singleton(K key, Long value) {
        return new Singleton<K>(key, value);
    }

    public static <K> Reference2LongFunction<K> synchronize(Reference2LongFunction<K> f) {
        return new SynchronizedFunction<K>(f);
    }

    public static <K> Reference2LongFunction<K> synchronize(Reference2LongFunction<K> f, Object sync) {
        return new SynchronizedFunction<K>(f, sync);
    }

    public static <K> Reference2LongFunction<K> unmodifiable(Reference2LongFunction<K> f) {
        return new UnmodifiableFunction<K>(f);
    }

    public static <K> Reference2LongFunction<K> primitive(java.util.function.Function<? super K, ? extends Long> f) {
        Objects.requireNonNull(f);
        if (f instanceof Reference2LongFunction) {
            return (Reference2LongFunction)f;
        }
        if (f instanceof ToLongFunction) {
            return key -> ((ToLongFunction)((Object)f)).applyAsLong(key);
        }
        return new PrimitiveFunction<K>(f);
    }

    public static class PrimitiveFunction<K>
    implements Reference2LongFunction<K> {
        protected final java.util.function.Function<? super K, ? extends Long> function;

        protected PrimitiveFunction(java.util.function.Function<? super K, ? extends Long> function) {
            this.function = function;
        }

        @Override
        public boolean containsKey(Object key) {
            return this.function.apply(key) != null;
        }

        @Override
        public long getLong(Object key) {
            Long v = this.function.apply(key);
            if (v == null) {
                return this.defaultReturnValue();
            }
            return v;
        }

        @Deprecated
        @Override
        public Long get(Object key) {
            return this.function.apply(key);
        }

        @Deprecated
        @Override
        public Long put(K key, Long value) {
            throw new UnsupportedOperationException();
        }
    }

    public static class UnmodifiableFunction<K>
    extends AbstractReference2LongFunction<K>
    implements Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final Reference2LongFunction<K> function;

        protected UnmodifiableFunction(Reference2LongFunction<K> f) {
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
        public long defaultReturnValue() {
            return this.function.defaultReturnValue();
        }

        @Override
        public void defaultReturnValue(long defRetValue) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean containsKey(Object k) {
            return this.function.containsKey(k);
        }

        @Override
        public long put(K k, long v) {
            throw new UnsupportedOperationException();
        }

        @Override
        public long getLong(Object k) {
            return this.function.getLong(k);
        }

        @Override
        public long removeLong(Object k) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void clear() {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public Long put(K k, Long v) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public Long get(Object k) {
            return this.function.get(k);
        }

        @Deprecated
        @Override
        public Long remove(Object k) {
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

    public static class SynchronizedFunction<K>
    implements Reference2LongFunction<K>,
    Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final Reference2LongFunction<K> function;
        protected final Object sync;

        protected SynchronizedFunction(Reference2LongFunction<K> f, Object sync) {
            if (f == null) {
                throw new NullPointerException();
            }
            this.function = f;
            this.sync = sync;
        }

        protected SynchronizedFunction(Reference2LongFunction<K> f) {
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
        public long applyAsLong(K operand) {
            Object object = this.sync;
            synchronized (object) {
                return this.function.applyAsLong(operand);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Deprecated
        @Override
        public Long apply(K key) {
            Object object = this.sync;
            synchronized (object) {
                return (Long)this.function.apply(key);
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
        public long defaultReturnValue() {
            Object object = this.sync;
            synchronized (object) {
                return this.function.defaultReturnValue();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void defaultReturnValue(long defRetValue) {
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
        public long put(K k, long v) {
            Object object = this.sync;
            synchronized (object) {
                return this.function.put(k, v);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public long getLong(Object k) {
            Object object = this.sync;
            synchronized (object) {
                return this.function.getLong(k);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public long removeLong(Object k) {
            Object object = this.sync;
            synchronized (object) {
                return this.function.removeLong(k);
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
        @Deprecated
        @Override
        public Long put(K k, Long v) {
            Object object = this.sync;
            synchronized (object) {
                return this.function.put(k, v);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Deprecated
        @Override
        public Long get(Object k) {
            Object object = this.sync;
            synchronized (object) {
                return this.function.get(k);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Deprecated
        @Override
        public Long remove(Object k) {
            Object object = this.sync;
            synchronized (object) {
                return this.function.remove(k);
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

    public static class Singleton<K>
    extends AbstractReference2LongFunction<K>
    implements Serializable,
    Cloneable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final K key;
        protected final long value;

        protected Singleton(K key, long value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public boolean containsKey(Object k) {
            return this.key == k;
        }

        @Override
        public long getLong(Object k) {
            return this.key == k ? this.value : this.defRetValue;
        }

        @Override
        public int size() {
            return 1;
        }

        public Object clone() {
            return this;
        }
    }

    public static class EmptyFunction<K>
    extends AbstractReference2LongFunction<K>
    implements Serializable,
    Cloneable {
        private static final long serialVersionUID = -7046029254386353129L;

        protected EmptyFunction() {
        }

        @Override
        public long getLong(Object k) {
            return 0L;
        }

        @Override
        public boolean containsKey(Object k) {
            return false;
        }

        @Override
        public long defaultReturnValue() {
            return 0L;
        }

        @Override
        public void defaultReturnValue(long defRetValue) {
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
            return Reference2LongFunctions.EMPTY_FUNCTION;
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
            return Reference2LongFunctions.EMPTY_FUNCTION;
        }
    }

}

