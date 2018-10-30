/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.doubles;

import it.unimi.dsi.fastutil.Function;
import it.unimi.dsi.fastutil.doubles.AbstractDouble2IntFunction;
import it.unimi.dsi.fastutil.doubles.Double2IntFunction;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Objects;
import java.util.function.DoubleToIntFunction;

public final class Double2IntFunctions {
    public static final EmptyFunction EMPTY_FUNCTION = new EmptyFunction();

    private Double2IntFunctions() {
    }

    public static Double2IntFunction singleton(double key, int value) {
        return new Singleton(key, value);
    }

    public static Double2IntFunction singleton(Double key, Integer value) {
        return new Singleton(key, value);
    }

    public static Double2IntFunction synchronize(Double2IntFunction f) {
        return new SynchronizedFunction(f);
    }

    public static Double2IntFunction synchronize(Double2IntFunction f, Object sync) {
        return new SynchronizedFunction(f, sync);
    }

    public static Double2IntFunction unmodifiable(Double2IntFunction f) {
        return new UnmodifiableFunction(f);
    }

    public static Double2IntFunction primitive(java.util.function.Function<? super Double, ? extends Integer> f) {
        Objects.requireNonNull(f);
        if (f instanceof Double2IntFunction) {
            return (Double2IntFunction)f;
        }
        if (f instanceof DoubleToIntFunction) {
            return ((DoubleToIntFunction)((Object)f))::applyAsInt;
        }
        return new PrimitiveFunction(f);
    }

    public static class PrimitiveFunction
    implements Double2IntFunction {
        protected final java.util.function.Function<? super Double, ? extends Integer> function;

        protected PrimitiveFunction(java.util.function.Function<? super Double, ? extends Integer> function) {
            this.function = function;
        }

        @Override
        public boolean containsKey(double key) {
            return this.function.apply((Double)key) != null;
        }

        @Deprecated
        @Override
        public boolean containsKey(Object key) {
            if (key == null) {
                return false;
            }
            return this.function.apply((Double)key) != null;
        }

        @Override
        public int get(double key) {
            Integer v = this.function.apply((Double)key);
            if (v == null) {
                return this.defaultReturnValue();
            }
            return v;
        }

        @Deprecated
        @Override
        public Integer get(Object key) {
            if (key == null) {
                return null;
            }
            return this.function.apply((Double)key);
        }

        @Deprecated
        @Override
        public Integer put(Double key, Integer value) {
            throw new UnsupportedOperationException();
        }
    }

    public static class UnmodifiableFunction
    extends AbstractDouble2IntFunction
    implements Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final Double2IntFunction function;

        protected UnmodifiableFunction(Double2IntFunction f) {
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
        public int defaultReturnValue() {
            return this.function.defaultReturnValue();
        }

        @Override
        public void defaultReturnValue(int defRetValue) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean containsKey(double k) {
            return this.function.containsKey(k);
        }

        @Override
        public int put(double k, int v) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int get(double k) {
            return this.function.get(k);
        }

        @Override
        public int remove(double k) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void clear() {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public Integer put(Double k, Integer v) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public Integer get(Object k) {
            return this.function.get(k);
        }

        @Deprecated
        @Override
        public Integer remove(Object k) {
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
    implements Double2IntFunction,
    Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final Double2IntFunction function;
        protected final Object sync;

        protected SynchronizedFunction(Double2IntFunction f, Object sync) {
            if (f == null) {
                throw new NullPointerException();
            }
            this.function = f;
            this.sync = sync;
        }

        protected SynchronizedFunction(Double2IntFunction f) {
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
        public int applyAsInt(double operand) {
            Object object = this.sync;
            synchronized (object) {
                return this.function.applyAsInt(operand);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Deprecated
        @Override
        public Integer apply(Double key) {
            Object object = this.sync;
            synchronized (object) {
                return (Integer)this.function.apply(key);
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
        public int defaultReturnValue() {
            Object object = this.sync;
            synchronized (object) {
                return this.function.defaultReturnValue();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void defaultReturnValue(int defRetValue) {
            Object object = this.sync;
            synchronized (object) {
                this.function.defaultReturnValue(defRetValue);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean containsKey(double k) {
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
        public int put(double k, int v) {
            Object object = this.sync;
            synchronized (object) {
                return this.function.put(k, v);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public int get(double k) {
            Object object = this.sync;
            synchronized (object) {
                return this.function.get(k);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public int remove(double k) {
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
        public Integer put(Double k, Integer v) {
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
        public Integer get(Object k) {
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
        public Integer remove(Object k) {
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
    extends AbstractDouble2IntFunction
    implements Serializable,
    Cloneable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final double key;
        protected final int value;

        protected Singleton(double key, int value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public boolean containsKey(double k) {
            return Double.doubleToLongBits(this.key) == Double.doubleToLongBits(k);
        }

        @Override
        public int get(double k) {
            return Double.doubleToLongBits(this.key) == Double.doubleToLongBits(k) ? this.value : this.defRetValue;
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
    extends AbstractDouble2IntFunction
    implements Serializable,
    Cloneable {
        private static final long serialVersionUID = -7046029254386353129L;

        protected EmptyFunction() {
        }

        @Override
        public int get(double k) {
            return 0;
        }

        @Override
        public boolean containsKey(double k) {
            return false;
        }

        @Override
        public int defaultReturnValue() {
            return 0;
        }

        @Override
        public void defaultReturnValue(int defRetValue) {
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
            return Double2IntFunctions.EMPTY_FUNCTION;
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
            return Double2IntFunctions.EMPTY_FUNCTION;
        }
    }

}

