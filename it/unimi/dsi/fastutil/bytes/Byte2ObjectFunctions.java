/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.Function;
import it.unimi.dsi.fastutil.bytes.AbstractByte2ObjectFunction;
import it.unimi.dsi.fastutil.bytes.Byte2ObjectFunction;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Objects;
import java.util.function.IntFunction;

public final class Byte2ObjectFunctions {
    public static final EmptyFunction EMPTY_FUNCTION = new EmptyFunction();

    private Byte2ObjectFunctions() {
    }

    public static <V> Byte2ObjectFunction<V> singleton(byte key, V value) {
        return new Singleton<V>(key, value);
    }

    public static <V> Byte2ObjectFunction<V> singleton(Byte key, V value) {
        return new Singleton<V>(key, value);
    }

    public static <V> Byte2ObjectFunction<V> synchronize(Byte2ObjectFunction<V> f) {
        return new SynchronizedFunction<V>(f);
    }

    public static <V> Byte2ObjectFunction<V> synchronize(Byte2ObjectFunction<V> f, Object sync) {
        return new SynchronizedFunction<V>(f, sync);
    }

    public static <V> Byte2ObjectFunction<V> unmodifiable(Byte2ObjectFunction<V> f) {
        return new UnmodifiableFunction<V>(f);
    }

    public static <V> Byte2ObjectFunction<V> primitive(java.util.function.Function<? super Byte, ? extends V> f) {
        Objects.requireNonNull(f);
        if (f instanceof Byte2ObjectFunction) {
            return (Byte2ObjectFunction)f;
        }
        if (f instanceof IntFunction) {
            return ((IntFunction)((Object)f))::apply;
        }
        return new PrimitiveFunction<V>(f);
    }

    public static class PrimitiveFunction<V>
    implements Byte2ObjectFunction<V> {
        protected final java.util.function.Function<? super Byte, ? extends V> function;

        protected PrimitiveFunction(java.util.function.Function<? super Byte, ? extends V> function) {
            this.function = function;
        }

        @Override
        public boolean containsKey(byte key) {
            return this.function.apply((Byte)key) != null;
        }

        @Deprecated
        @Override
        public boolean containsKey(Object key) {
            if (key == null) {
                return false;
            }
            return this.function.apply((Byte)key) != null;
        }

        @Override
        public V get(byte key) {
            V v = this.function.apply((Byte)key);
            if (v == null) {
                return null;
            }
            return v;
        }

        @Deprecated
        @Override
        public V get(Object key) {
            if (key == null) {
                return null;
            }
            return this.function.apply((Byte)key);
        }

        @Deprecated
        @Override
        public V put(Byte key, V value) {
            throw new UnsupportedOperationException();
        }
    }

    public static class UnmodifiableFunction<V>
    extends AbstractByte2ObjectFunction<V>
    implements Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final Byte2ObjectFunction<V> function;

        protected UnmodifiableFunction(Byte2ObjectFunction<V> f) {
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
        public boolean containsKey(byte k) {
            return this.function.containsKey(k);
        }

        @Override
        public V put(byte k, V v) {
            throw new UnsupportedOperationException();
        }

        @Override
        public V get(byte k) {
            return this.function.get(k);
        }

        @Override
        public V remove(byte k) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void clear() {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public V put(Byte k, V v) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public V get(Object k) {
            return this.function.get(k);
        }

        @Deprecated
        @Override
        public V remove(Object k) {
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

    public static class SynchronizedFunction<V>
    implements Byte2ObjectFunction<V>,
    Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final Byte2ObjectFunction<V> function;
        protected final Object sync;

        protected SynchronizedFunction(Byte2ObjectFunction<V> f, Object sync) {
            if (f == null) {
                throw new NullPointerException();
            }
            this.function = f;
            this.sync = sync;
        }

        protected SynchronizedFunction(Byte2ObjectFunction<V> f) {
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
        public V apply(int operand) {
            Object object = this.sync;
            synchronized (object) {
                return this.function.apply(operand);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Deprecated
        @Override
        public V apply(Byte key) {
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
        public boolean containsKey(byte k) {
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
        public V put(byte k, V v) {
            Object object = this.sync;
            synchronized (object) {
                return this.function.put(k, v);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public V get(byte k) {
            Object object = this.sync;
            synchronized (object) {
                return this.function.get(k);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public V remove(byte k) {
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
        public V put(Byte k, V v) {
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
        public V get(Object k) {
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
        public V remove(Object k) {
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

    public static class Singleton<V>
    extends AbstractByte2ObjectFunction<V>
    implements Serializable,
    Cloneable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final byte key;
        protected final V value;

        protected Singleton(byte key, V value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public boolean containsKey(byte k) {
            return this.key == k;
        }

        @Override
        public V get(byte k) {
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

    public static class EmptyFunction<V>
    extends AbstractByte2ObjectFunction<V>
    implements Serializable,
    Cloneable {
        private static final long serialVersionUID = -7046029254386353129L;

        protected EmptyFunction() {
        }

        @Override
        public V get(byte k) {
            return null;
        }

        @Override
        public boolean containsKey(byte k) {
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
            return Byte2ObjectFunctions.EMPTY_FUNCTION;
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
            return Byte2ObjectFunctions.EMPTY_FUNCTION;
        }
    }

}

