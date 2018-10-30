/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.Function;
import it.unimi.dsi.fastutil.floats.AbstractFloat2LongFunction;
import it.unimi.dsi.fastutil.floats.Float2LongFunction;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Objects;
import java.util.function.DoubleToLongFunction;

public final class Float2LongFunctions {
    public static final EmptyFunction EMPTY_FUNCTION = new EmptyFunction();

    private Float2LongFunctions() {
    }

    public static Float2LongFunction singleton(float key, long value) {
        return new Singleton(key, value);
    }

    public static Float2LongFunction singleton(Float key, Long value) {
        return new Singleton(key.floatValue(), value);
    }

    public static Float2LongFunction synchronize(Float2LongFunction f) {
        return new SynchronizedFunction(f);
    }

    public static Float2LongFunction synchronize(Float2LongFunction f, Object sync) {
        return new SynchronizedFunction(f, sync);
    }

    public static Float2LongFunction unmodifiable(Float2LongFunction f) {
        return new UnmodifiableFunction(f);
    }

    public static Float2LongFunction primitive(java.util.function.Function<? super Float, ? extends Long> f) {
        Objects.requireNonNull(f);
        if (f instanceof Float2LongFunction) {
            return (Float2LongFunction)f;
        }
        if (f instanceof DoubleToLongFunction) {
            return ((DoubleToLongFunction)((Object)f))::applyAsLong;
        }
        return new PrimitiveFunction(f);
    }

    public static class PrimitiveFunction
    implements Float2LongFunction {
        protected final java.util.function.Function<? super Float, ? extends Long> function;

        protected PrimitiveFunction(java.util.function.Function<? super Float, ? extends Long> function) {
            this.function = function;
        }

        @Override
        public boolean containsKey(float key) {
            return this.function.apply(Float.valueOf(key)) != null;
        }

        @Deprecated
        @Override
        public boolean containsKey(Object key) {
            if (key == null) {
                return false;
            }
            return this.function.apply((Float)key) != null;
        }

        @Override
        public long get(float key) {
            Long v = this.function.apply(Float.valueOf(key));
            if (v == null) {
                return this.defaultReturnValue();
            }
            return v;
        }

        @Deprecated
        @Override
        public Long get(Object key) {
            if (key == null) {
                return null;
            }
            return this.function.apply((Float)key);
        }

        @Deprecated
        @Override
        public Long put(Float key, Long value) {
            throw new UnsupportedOperationException();
        }
    }

    public static class UnmodifiableFunction
    extends AbstractFloat2LongFunction
    implements Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final Float2LongFunction function;

        protected UnmodifiableFunction(Float2LongFunction f) {
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
        public boolean containsKey(float k) {
            return this.function.containsKey(k);
        }

        @Override
        public long put(float k, long v) {
            throw new UnsupportedOperationException();
        }

        @Override
        public long get(float k) {
            return this.function.get(k);
        }

        @Override
        public long remove(float k) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void clear() {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public Long put(Float k, Long v) {
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

    public static class SynchronizedFunction
    implements Float2LongFunction,
    Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final Float2LongFunction function;
        protected final Object sync;

        protected SynchronizedFunction(Float2LongFunction f, Object sync) {
            if (f == null) {
                throw new NullPointerException();
            }
            this.function = f;
            this.sync = sync;
        }

        protected SynchronizedFunction(Float2LongFunction f) {
            if (f == null) {
                throw new NullPointerException();
            }
            this.function = f;
            this.sync = this;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Deprecated
        @Override
        public long applyAsLong(double operand) {
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
        public Long apply(Float key) {
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
        public boolean containsKey(float k) {
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
        public long put(float k, long v) {
            Object object = this.sync;
            synchronized (object) {
                return this.function.put(k, v);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public long get(float k) {
            Object object = this.sync;
            synchronized (object) {
                return this.function.get(k);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public long remove(float k) {
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
        public Long put(Float k, Long v) {
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

    public static class Singleton
    extends AbstractFloat2LongFunction
    implements Serializable,
    Cloneable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final float key;
        protected final long value;

        protected Singleton(float key, long value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public boolean containsKey(float k) {
            return Float.floatToIntBits(this.key) == Float.floatToIntBits(k);
        }

        @Override
        public long get(float k) {
            return Float.floatToIntBits(this.key) == Float.floatToIntBits(k) ? this.value : this.defRetValue;
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
    extends AbstractFloat2LongFunction
    implements Serializable,
    Cloneable {
        private static final long serialVersionUID = -7046029254386353129L;

        protected EmptyFunction() {
        }

        @Override
        public long get(float k) {
            return 0L;
        }

        @Override
        public boolean containsKey(float k) {
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
            return Float2LongFunctions.EMPTY_FUNCTION;
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
            return Float2LongFunctions.EMPTY_FUNCTION;
        }
    }

}

