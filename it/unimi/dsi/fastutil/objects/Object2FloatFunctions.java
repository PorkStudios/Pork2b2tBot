/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.Function;
import it.unimi.dsi.fastutil.SafeMath;
import it.unimi.dsi.fastutil.objects.AbstractObject2FloatFunction;
import it.unimi.dsi.fastutil.objects.Object2FloatFunction;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Objects;
import java.util.function.ToDoubleFunction;

public final class Object2FloatFunctions {
    public static final EmptyFunction EMPTY_FUNCTION = new EmptyFunction();

    private Object2FloatFunctions() {
    }

    public static <K> Object2FloatFunction<K> singleton(K key, float value) {
        return new Singleton<K>(key, value);
    }

    public static <K> Object2FloatFunction<K> singleton(K key, Float value) {
        return new Singleton<K>(key, value.floatValue());
    }

    public static <K> Object2FloatFunction<K> synchronize(Object2FloatFunction<K> f) {
        return new SynchronizedFunction<K>(f);
    }

    public static <K> Object2FloatFunction<K> synchronize(Object2FloatFunction<K> f, Object sync) {
        return new SynchronizedFunction<K>(f, sync);
    }

    public static <K> Object2FloatFunction<K> unmodifiable(Object2FloatFunction<K> f) {
        return new UnmodifiableFunction<K>(f);
    }

    public static <K> Object2FloatFunction<K> primitive(java.util.function.Function<? super K, ? extends Float> f) {
        Objects.requireNonNull(f);
        if (f instanceof Object2FloatFunction) {
            return (Object2FloatFunction)f;
        }
        if (f instanceof ToDoubleFunction) {
            return key -> SafeMath.safeDoubleToFloat(((ToDoubleFunction)((Object)f)).applyAsDouble(key));
        }
        return new PrimitiveFunction<K>(f);
    }

    public static class PrimitiveFunction<K>
    implements Object2FloatFunction<K> {
        protected final java.util.function.Function<? super K, ? extends Float> function;

        protected PrimitiveFunction(java.util.function.Function<? super K, ? extends Float> function) {
            this.function = function;
        }

        @Override
        public boolean containsKey(Object key) {
            return this.function.apply(key) != null;
        }

        @Override
        public float getFloat(Object key) {
            Float v = this.function.apply(key);
            if (v == null) {
                return this.defaultReturnValue();
            }
            return v.floatValue();
        }

        @Deprecated
        @Override
        public Float get(Object key) {
            return this.function.apply(key);
        }

        @Deprecated
        @Override
        public Float put(K key, Float value) {
            throw new UnsupportedOperationException();
        }
    }

    public static class UnmodifiableFunction<K>
    extends AbstractObject2FloatFunction<K>
    implements Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final Object2FloatFunction<K> function;

        protected UnmodifiableFunction(Object2FloatFunction<K> f) {
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
        public boolean containsKey(Object k) {
            return this.function.containsKey(k);
        }

        @Override
        public float put(K k, float v) {
            throw new UnsupportedOperationException();
        }

        @Override
        public float getFloat(Object k) {
            return this.function.getFloat(k);
        }

        @Override
        public float removeFloat(Object k) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void clear() {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public Float put(K k, Float v) {
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

    public static class SynchronizedFunction<K>
    implements Object2FloatFunction<K>,
    Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final Object2FloatFunction<K> function;
        protected final Object sync;

        protected SynchronizedFunction(Object2FloatFunction<K> f, Object sync) {
            if (f == null) {
                throw new NullPointerException();
            }
            this.function = f;
            this.sync = sync;
        }

        protected SynchronizedFunction(Object2FloatFunction<K> f) {
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
        public Float apply(K key) {
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
        public float put(K k, float v) {
            Object object = this.sync;
            synchronized (object) {
                return this.function.put(k, v);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public float getFloat(Object k) {
            Object object = this.sync;
            synchronized (object) {
                return this.function.getFloat(k);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public float removeFloat(Object k) {
            Object object = this.sync;
            synchronized (object) {
                return this.function.removeFloat(k);
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
        public Float put(K k, Float v) {
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

    public static class Singleton<K>
    extends AbstractObject2FloatFunction<K>
    implements Serializable,
    Cloneable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final K key;
        protected final float value;

        protected Singleton(K key, float value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public boolean containsKey(Object k) {
            return Objects.equals(this.key, k);
        }

        @Override
        public float getFloat(Object k) {
            return Objects.equals(this.key, k) ? this.value : this.defRetValue;
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
    extends AbstractObject2FloatFunction<K>
    implements Serializable,
    Cloneable {
        private static final long serialVersionUID = -7046029254386353129L;

        protected EmptyFunction() {
        }

        @Override
        public float getFloat(Object k) {
            return 0.0f;
        }

        @Override
        public boolean containsKey(Object k) {
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
            return Object2FloatFunctions.EMPTY_FUNCTION;
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
            return Object2FloatFunctions.EMPTY_FUNCTION;
        }
    }

}

