/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.shorts;

import it.unimi.dsi.fastutil.Function;
import it.unimi.dsi.fastutil.shorts.AbstractShort2LongFunction;
import it.unimi.dsi.fastutil.shorts.Short2LongFunction;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Objects;
import java.util.function.IntToLongFunction;

public final class Short2LongFunctions {
    public static final EmptyFunction EMPTY_FUNCTION = new EmptyFunction();

    private Short2LongFunctions() {
    }

    public static Short2LongFunction singleton(short key, long value) {
        return new Singleton(key, value);
    }

    public static Short2LongFunction singleton(Short key, Long value) {
        return new Singleton(key, value);
    }

    public static Short2LongFunction synchronize(Short2LongFunction f) {
        return new SynchronizedFunction(f);
    }

    public static Short2LongFunction synchronize(Short2LongFunction f, Object sync) {
        return new SynchronizedFunction(f, sync);
    }

    public static Short2LongFunction unmodifiable(Short2LongFunction f) {
        return new UnmodifiableFunction(f);
    }

    public static Short2LongFunction primitive(java.util.function.Function<? super Short, ? extends Long> f) {
        Objects.requireNonNull(f);
        if (f instanceof Short2LongFunction) {
            return (Short2LongFunction)f;
        }
        if (f instanceof IntToLongFunction) {
            return ((IntToLongFunction)((Object)f))::applyAsLong;
        }
        return new PrimitiveFunction(f);
    }

    public static class PrimitiveFunction
    implements Short2LongFunction {
        protected final java.util.function.Function<? super Short, ? extends Long> function;

        protected PrimitiveFunction(java.util.function.Function<? super Short, ? extends Long> function) {
            this.function = function;
        }

        @Override
        public boolean containsKey(short key) {
            return this.function.apply((Short)key) != null;
        }

        @Deprecated
        @Override
        public boolean containsKey(Object key) {
            if (key == null) {
                return false;
            }
            return this.function.apply((Short)key) != null;
        }

        @Override
        public long get(short key) {
            Long v = this.function.apply((Short)key);
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
            return this.function.apply((Short)key);
        }

        @Deprecated
        @Override
        public Long put(Short key, Long value) {
            throw new UnsupportedOperationException();
        }
    }

    public static class UnmodifiableFunction
    extends AbstractShort2LongFunction
    implements Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final Short2LongFunction function;

        protected UnmodifiableFunction(Short2LongFunction f) {
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
        public boolean containsKey(short k) {
            return this.function.containsKey(k);
        }

        @Override
        public long put(short k, long v) {
            throw new UnsupportedOperationException();
        }

        @Override
        public long get(short k) {
            return this.function.get(k);
        }

        @Override
        public long remove(short k) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void clear() {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public Long put(Short k, Long v) {
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
    implements Short2LongFunction,
    Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final Short2LongFunction function;
        protected final Object sync;

        protected SynchronizedFunction(Short2LongFunction f, Object sync) {
            if (f == null) {
                throw new NullPointerException();
            }
            this.function = f;
            this.sync = sync;
        }

        protected SynchronizedFunction(Short2LongFunction f) {
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
        public long applyAsLong(int operand) {
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
        public Long apply(Short key) {
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
        public boolean containsKey(short k) {
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
        public long put(short k, long v) {
            Object object = this.sync;
            synchronized (object) {
                return this.function.put(k, v);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public long get(short k) {
            Object object = this.sync;
            synchronized (object) {
                return this.function.get(k);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public long remove(short k) {
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
        public Long put(Short k, Long v) {
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
    extends AbstractShort2LongFunction
    implements Serializable,
    Cloneable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final short key;
        protected final long value;

        protected Singleton(short key, long value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public boolean containsKey(short k) {
            return this.key == k;
        }

        @Override
        public long get(short k) {
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
    extends AbstractShort2LongFunction
    implements Serializable,
    Cloneable {
        private static final long serialVersionUID = -7046029254386353129L;

        protected EmptyFunction() {
        }

        @Override
        public long get(short k) {
            return 0L;
        }

        @Override
        public boolean containsKey(short k) {
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
            return Short2LongFunctions.EMPTY_FUNCTION;
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
            return Short2LongFunctions.EMPTY_FUNCTION;
        }
    }

}

