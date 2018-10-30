/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.Function;
import it.unimi.dsi.fastutil.longs.AbstractLong2DoubleFunction;
import it.unimi.dsi.fastutil.longs.Long2DoubleFunction;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Objects;
import java.util.function.LongToDoubleFunction;

public final class Long2DoubleFunctions {
    public static final EmptyFunction EMPTY_FUNCTION = new EmptyFunction();

    private Long2DoubleFunctions() {
    }

    public static Long2DoubleFunction singleton(long key, double value) {
        return new Singleton(key, value);
    }

    public static Long2DoubleFunction singleton(Long key, Double value) {
        return new Singleton(key, value);
    }

    public static Long2DoubleFunction synchronize(Long2DoubleFunction f) {
        return new SynchronizedFunction(f);
    }

    public static Long2DoubleFunction synchronize(Long2DoubleFunction f, Object sync) {
        return new SynchronizedFunction(f, sync);
    }

    public static Long2DoubleFunction unmodifiable(Long2DoubleFunction f) {
        return new UnmodifiableFunction(f);
    }

    public static Long2DoubleFunction primitive(java.util.function.Function<? super Long, ? extends Double> f) {
        Objects.requireNonNull(f);
        if (f instanceof Long2DoubleFunction) {
            return (Long2DoubleFunction)f;
        }
        if (f instanceof LongToDoubleFunction) {
            return ((LongToDoubleFunction)((Object)f))::applyAsDouble;
        }
        return new PrimitiveFunction(f);
    }

    public static class PrimitiveFunction
    implements Long2DoubleFunction {
        protected final java.util.function.Function<? super Long, ? extends Double> function;

        protected PrimitiveFunction(java.util.function.Function<? super Long, ? extends Double> function) {
            this.function = function;
        }

        @Override
        public boolean containsKey(long key) {
            return this.function.apply((Long)key) != null;
        }

        @Deprecated
        @Override
        public boolean containsKey(Object key) {
            if (key == null) {
                return false;
            }
            return this.function.apply((Long)key) != null;
        }

        @Override
        public double get(long key) {
            Double v = this.function.apply((Long)key);
            if (v == null) {
                return this.defaultReturnValue();
            }
            return v;
        }

        @Deprecated
        @Override
        public Double get(Object key) {
            if (key == null) {
                return null;
            }
            return this.function.apply((Long)key);
        }

        @Deprecated
        @Override
        public Double put(Long key, Double value) {
            throw new UnsupportedOperationException();
        }
    }

    public static class UnmodifiableFunction
    extends AbstractLong2DoubleFunction
    implements Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final Long2DoubleFunction function;

        protected UnmodifiableFunction(Long2DoubleFunction f) {
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
        public boolean containsKey(long k) {
            return this.function.containsKey(k);
        }

        @Override
        public double put(long k, double v) {
            throw new UnsupportedOperationException();
        }

        @Override
        public double get(long k) {
            return this.function.get(k);
        }

        @Override
        public double remove(long k) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void clear() {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public Double put(Long k, Double v) {
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

    public static class SynchronizedFunction
    implements Long2DoubleFunction,
    Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final Long2DoubleFunction function;
        protected final Object sync;

        protected SynchronizedFunction(Long2DoubleFunction f, Object sync) {
            if (f == null) {
                throw new NullPointerException();
            }
            this.function = f;
            this.sync = sync;
        }

        protected SynchronizedFunction(Long2DoubleFunction f) {
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
        public double applyAsDouble(long operand) {
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
        public Double apply(Long key) {
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
        public boolean containsKey(long k) {
            Object object = this.sync;
            synchronized (object) {
                return this.function.containsKey(k);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Deprecated
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
        public double put(long k, double v) {
            Object object = this.sync;
            synchronized (object) {
                return this.function.put(k, v);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public double get(long k) {
            Object object = this.sync;
            synchronized (object) {
                return this.function.get(k);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public double remove(long k) {
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
        @Deprecated
        @Override
        public Double put(Long k, Double v) {
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

    public static class Singleton
    extends AbstractLong2DoubleFunction
    implements Serializable,
    Cloneable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final long key;
        protected final double value;

        protected Singleton(long key, double value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public boolean containsKey(long k) {
            return this.key == k;
        }

        @Override
        public double get(long k) {
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

    public static class EmptyFunction
    extends AbstractLong2DoubleFunction
    implements Serializable,
    Cloneable {
        private static final long serialVersionUID = -7046029254386353129L;

        protected EmptyFunction() {
        }

        @Override
        public double get(long k) {
            return 0.0;
        }

        @Override
        public boolean containsKey(long k) {
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
            return Long2DoubleFunctions.EMPTY_FUNCTION;
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
            return Long2DoubleFunctions.EMPTY_FUNCTION;
        }
    }

}

