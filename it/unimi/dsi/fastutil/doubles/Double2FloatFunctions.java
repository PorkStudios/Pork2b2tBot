/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.doubles;

import it.unimi.dsi.fastutil.Function;
import it.unimi.dsi.fastutil.SafeMath;
import it.unimi.dsi.fastutil.doubles.AbstractDouble2FloatFunction;
import it.unimi.dsi.fastutil.doubles.Double2FloatFunction;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Objects;
import java.util.function.DoubleUnaryOperator;

public final class Double2FloatFunctions {
    public static final EmptyFunction EMPTY_FUNCTION = new EmptyFunction();

    private Double2FloatFunctions() {
    }

    public static Double2FloatFunction singleton(double key, float value) {
        return new Singleton(key, value);
    }

    public static Double2FloatFunction singleton(Double key, Float value) {
        return new Singleton(key, value.floatValue());
    }

    public static Double2FloatFunction synchronize(Double2FloatFunction f) {
        return new SynchronizedFunction(f);
    }

    public static Double2FloatFunction synchronize(Double2FloatFunction f, Object sync) {
        return new SynchronizedFunction(f, sync);
    }

    public static Double2FloatFunction unmodifiable(Double2FloatFunction f) {
        return new UnmodifiableFunction(f);
    }

    public static Double2FloatFunction primitive(java.util.function.Function<? super Double, ? extends Float> f) {
        Objects.requireNonNull(f);
        if (f instanceof Double2FloatFunction) {
            return (Double2FloatFunction)f;
        }
        if (f instanceof DoubleUnaryOperator) {
            return key -> SafeMath.safeDoubleToFloat(((DoubleUnaryOperator)((Object)f)).applyAsDouble(key));
        }
        return new PrimitiveFunction(f);
    }

    public static class PrimitiveFunction
    implements Double2FloatFunction {
        protected final java.util.function.Function<? super Double, ? extends Float> function;

        protected PrimitiveFunction(java.util.function.Function<? super Double, ? extends Float> function) {
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
        public float get(double key) {
            Float v = this.function.apply((Double)key);
            if (v == null) {
                return this.defaultReturnValue();
            }
            return v.floatValue();
        }

        @Deprecated
        @Override
        public Float get(Object key) {
            if (key == null) {
                return null;
            }
            return this.function.apply((Double)key);
        }

        @Deprecated
        @Override
        public Float put(Double key, Float value) {
            throw new UnsupportedOperationException();
        }
    }

    public static class UnmodifiableFunction
    extends AbstractDouble2FloatFunction
    implements Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final Double2FloatFunction function;

        protected UnmodifiableFunction(Double2FloatFunction f) {
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
        public float defaultReturnValue() {
            return this.function.defaultReturnValue();
        }

        @Override
        public void defaultReturnValue(float defRetValue) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean containsKey(double k) {
            return this.function.containsKey(k);
        }

        @Override
        public float put(double k, float v) {
            throw new UnsupportedOperationException();
        }

        @Override
        public float get(double k) {
            return this.function.get(k);
        }

        @Override
        public float remove(double k) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void clear() {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public Float put(Double k, Float v) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public Float get(Object k) {
            return this.function.get(k);
        }

        @Deprecated
        @Override
        public Float remove(Object k) {
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
    implements Double2FloatFunction,
    Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final Double2FloatFunction function;
        protected final Object sync;

        protected SynchronizedFunction(Double2FloatFunction f, Object sync) {
            if (f == null) {
                throw new NullPointerException();
            }
            this.function = f;
            this.sync = sync;
        }

        protected SynchronizedFunction(Double2FloatFunction f) {
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
        public double applyAsDouble(double operand) {
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
        public Float apply(Double key) {
            Object object = this.sync;
            synchronized (object) {
                return (Float)this.function.apply(key);
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
        public float defaultReturnValue() {
            Object object = this.sync;
            synchronized (object) {
                return this.function.defaultReturnValue();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void defaultReturnValue(float defRetValue) {
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
        public float put(double k, float v) {
            Object object = this.sync;
            synchronized (object) {
                return this.function.put(k, v);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public float get(double k) {
            Object object = this.sync;
            synchronized (object) {
                return this.function.get(k);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public float remove(double k) {
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
        public Float put(Double k, Float v) {
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
        public Float get(Object k) {
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
        public Float remove(Object k) {
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
    extends AbstractDouble2FloatFunction
    implements Serializable,
    Cloneable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final double key;
        protected final float value;

        protected Singleton(double key, float value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public boolean containsKey(double k) {
            return Double.doubleToLongBits(this.key) == Double.doubleToLongBits(k);
        }

        @Override
        public float get(double k) {
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
    extends AbstractDouble2FloatFunction
    implements Serializable,
    Cloneable {
        private static final long serialVersionUID = -7046029254386353129L;

        protected EmptyFunction() {
        }

        @Override
        public float get(double k) {
            return 0.0f;
        }

        @Override
        public boolean containsKey(double k) {
            return false;
        }

        @Override
        public float defaultReturnValue() {
            return 0.0f;
        }

        @Override
        public void defaultReturnValue(float defRetValue) {
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
            return Double2FloatFunctions.EMPTY_FUNCTION;
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
            return Double2FloatFunctions.EMPTY_FUNCTION;
        }
    }

}

