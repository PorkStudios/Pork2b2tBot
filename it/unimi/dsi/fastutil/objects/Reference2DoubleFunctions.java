/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.Function;
import it.unimi.dsi.fastutil.objects.AbstractReference2DoubleFunction;
import it.unimi.dsi.fastutil.objects.Reference2DoubleFunction;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Objects;
import java.util.function.ToDoubleFunction;

public final class Reference2DoubleFunctions {
    public static final EmptyFunction EMPTY_FUNCTION = new EmptyFunction();

    private Reference2DoubleFunctions() {
    }

    public static <K> Reference2DoubleFunction<K> singleton(K key, double value) {
        return new Singleton<K>(key, value);
    }

    public static <K> Reference2DoubleFunction<K> singleton(K key, Double value) {
        return new Singleton<K>(key, value);
    }

    public static <K> Reference2DoubleFunction<K> synchronize(Reference2DoubleFunction<K> f) {
        return new SynchronizedFunction<K>(f);
    }

    public static <K> Reference2DoubleFunction<K> synchronize(Reference2DoubleFunction<K> f, Object sync) {
        return new SynchronizedFunction<K>(f, sync);
    }

    public static <K> Reference2DoubleFunction<K> unmodifiable(Reference2DoubleFunction<K> f) {
        return new UnmodifiableFunction<K>(f);
    }

    public static <K> Reference2DoubleFunction<K> primitive(java.util.function.Function<? super K, ? extends Double> f) {
        Objects.requireNonNull(f);
        if (f instanceof Reference2DoubleFunction) {
            return (Reference2DoubleFunction)f;
        }
        if (f instanceof ToDoubleFunction) {
            return key -> ((ToDoubleFunction)((Object)f)).applyAsDouble(key);
        }
        return new PrimitiveFunction<K>(f);
    }

    public static class PrimitiveFunction<K>
    implements Reference2DoubleFunction<K> {
        protected final java.util.function.Function<? super K, ? extends Double> function;

        protected PrimitiveFunction(java.util.function.Function<? super K, ? extends Double> function) {
            this.function = function;
        }

        @Override
        public boolean containsKey(Object key) {
            return this.function.apply(key) != null;
        }

        @Override
        public double getDouble(Object key) {
            Double v = this.function.apply(key);
            if (v == null) {
                return this.defaultReturnValue();
            }
            return v;
        }

        @Deprecated
        @Override
        public Double get(Object key) {
            return this.function.apply(key);
        }

        @Deprecated
        @Override
        public Double put(K key, Double value) {
            throw new UnsupportedOperationException();
        }
    }

    public static class UnmodifiableFunction<K>
    extends AbstractReference2DoubleFunction<K>
    implements Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final Reference2DoubleFunction<K> function;

        protected UnmodifiableFunction(Reference2DoubleFunction<K> f) {
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
        public double defaultReturnValue() {
            return this.function.defaultReturnValue();
        }

        @Override
        public void defaultReturnValue(double defRetValue) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean containsKey(Object k) {
            return this.function.containsKey(k);
        }

        @Override
        public double put(K k, double v) {
            throw new UnsupportedOperationException();
        }

        @Override
        public double getDouble(Object k) {
            return this.function.getDouble(k);
        }

        @Override
        public double removeDouble(Object k) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void clear() {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public Double put(K k, Double v) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public Double get(Object k) {
            return this.function.get(k);
        }

        @Deprecated
        @Override
        public Double remove(Object k) {
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
    implements Reference2DoubleFunction<K>,
    Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final Reference2DoubleFunction<K> function;
        protected final Object sync;

        protected SynchronizedFunction(Reference2DoubleFunction<K> f, Object sync) {
            if (f == null) {
                throw new NullPointerException();
            }
            this.function = f;
            this.sync = sync;
        }

        protected SynchronizedFunction(Reference2DoubleFunction<K> f) {
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
        public double applyAsDouble(K operand) {
            Object object = this.sync;
            synchronized (object) {
                return this.function.applyAsDouble(operand);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Deprecated
        @Override
        public Double apply(K key) {
            Object object = this.sync;
            synchronized (object) {
                return (Double)this.function.apply(key);
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
        public double defaultReturnValue() {
            Object object = this.sync;
            synchronized (object) {
                return this.function.defaultReturnValue();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void defaultReturnValue(double defRetValue) {
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
        public double put(K k, double v) {
            Object object = this.sync;
            synchronized (object) {
                return this.function.put(k, v);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public double getDouble(Object k) {
            Object object = this.sync;
            synchronized (object) {
                return this.function.getDouble(k);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public double removeDouble(Object k) {
            Object object = this.sync;
            synchronized (object) {
                return this.function.removeDouble(k);
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
        public Double put(K k, Double v) {
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
        public Double get(Object k) {
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
        public Double remove(Object k) {
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
    extends AbstractReference2DoubleFunction<K>
    implements Serializable,
    Cloneable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final K key;
        protected final double value;

        protected Singleton(K key, double value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public boolean containsKey(Object k) {
            return this.key == k;
        }

        @Override
        public double getDouble(Object k) {
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
    extends AbstractReference2DoubleFunction<K>
    implements Serializable,
    Cloneable {
        private static final long serialVersionUID = -7046029254386353129L;

        protected EmptyFunction() {
        }

        @Override
        public double getDouble(Object k) {
            return 0.0;
        }

        @Override
        public boolean containsKey(Object k) {
            return false;
        }

        @Override
        public double defaultReturnValue() {
            return 0.0;
        }

        @Override
        public void defaultReturnValue(double defRetValue) {
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
            return Reference2DoubleFunctions.EMPTY_FUNCTION;
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
            return Reference2DoubleFunctions.EMPTY_FUNCTION;
        }
    }

}

