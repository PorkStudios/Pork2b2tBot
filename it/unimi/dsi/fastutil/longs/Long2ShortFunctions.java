/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.Function;
import it.unimi.dsi.fastutil.SafeMath;
import it.unimi.dsi.fastutil.longs.AbstractLong2ShortFunction;
import it.unimi.dsi.fastutil.longs.Long2ShortFunction;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Objects;
import java.util.function.LongToIntFunction;

public final class Long2ShortFunctions {
    public static final EmptyFunction EMPTY_FUNCTION = new EmptyFunction();

    private Long2ShortFunctions() {
    }

    public static Long2ShortFunction singleton(long key, short value) {
        return new Singleton(key, value);
    }

    public static Long2ShortFunction singleton(Long key, Short value) {
        return new Singleton(key, value);
    }

    public static Long2ShortFunction synchronize(Long2ShortFunction f) {
        return new SynchronizedFunction(f);
    }

    public static Long2ShortFunction synchronize(Long2ShortFunction f, Object sync) {
        return new SynchronizedFunction(f, sync);
    }

    public static Long2ShortFunction unmodifiable(Long2ShortFunction f) {
        return new UnmodifiableFunction(f);
    }

    public static Long2ShortFunction primitive(java.util.function.Function<? super Long, ? extends Short> f) {
        Objects.requireNonNull(f);
        if (f instanceof Long2ShortFunction) {
            return (Long2ShortFunction)f;
        }
        if (f instanceof LongToIntFunction) {
            return key -> SafeMath.safeIntToShort(((LongToIntFunction)((Object)f)).applyAsInt(key));
        }
        return new PrimitiveFunction(f);
    }

    public static class PrimitiveFunction
    implements Long2ShortFunction {
        protected final java.util.function.Function<? super Long, ? extends Short> function;

        protected PrimitiveFunction(java.util.function.Function<? super Long, ? extends Short> function) {
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
        public short get(long key) {
            Short v = this.function.apply((Long)key);
            if (v == null) {
                return this.defaultReturnValue();
            }
            return v;
        }

        @Deprecated
        @Override
        public Short get(Object key) {
            if (key == null) {
                return null;
            }
            return this.function.apply((Long)key);
        }

        @Deprecated
        @Override
        public Short put(Long key, Short value) {
            throw new UnsupportedOperationException();
        }
    }

    public static class UnmodifiableFunction
    extends AbstractLong2ShortFunction
    implements Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final Long2ShortFunction function;

        protected UnmodifiableFunction(Long2ShortFunction f) {
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
        public short defaultReturnValue() {
            return this.function.defaultReturnValue();
        }

        @Override
        public void defaultReturnValue(short defRetValue) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean containsKey(long k) {
            return this.function.containsKey(k);
        }

        @Override
        public short put(long k, short v) {
            throw new UnsupportedOperationException();
        }

        @Override
        public short get(long k) {
            return this.function.get(k);
        }

        @Override
        public short remove(long k) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void clear() {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public Short put(Long k, Short v) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public Short get(Object k) {
            return this.function.get(k);
        }

        @Deprecated
        @Override
        public Short remove(Object k) {
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
    implements Long2ShortFunction,
    Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final Long2ShortFunction function;
        protected final Object sync;

        protected SynchronizedFunction(Long2ShortFunction f, Object sync) {
            if (f == null) {
                throw new NullPointerException();
            }
            this.function = f;
            this.sync = sync;
        }

        protected SynchronizedFunction(Long2ShortFunction f) {
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
        public int applyAsInt(long operand) {
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
        public Short apply(Long key) {
            Object object = this.sync;
            synchronized (object) {
                return (Short)this.function.apply(key);
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
        public short defaultReturnValue() {
            Object object = this.sync;
            synchronized (object) {
                return this.function.defaultReturnValue();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void defaultReturnValue(short defRetValue) {
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
        public short put(long k, short v) {
            Object object = this.sync;
            synchronized (object) {
                return this.function.put(k, v);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public short get(long k) {
            Object object = this.sync;
            synchronized (object) {
                return this.function.get(k);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public short remove(long k) {
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
        public Short put(Long k, Short v) {
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
        public Short get(Object k) {
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
        public Short remove(Object k) {
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
    extends AbstractLong2ShortFunction
    implements Serializable,
    Cloneable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final long key;
        protected final short value;

        protected Singleton(long key, short value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public boolean containsKey(long k) {
            return this.key == k;
        }

        @Override
        public short get(long k) {
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
    extends AbstractLong2ShortFunction
    implements Serializable,
    Cloneable {
        private static final long serialVersionUID = -7046029254386353129L;

        protected EmptyFunction() {
        }

        @Override
        public short get(long k) {
            return 0;
        }

        @Override
        public boolean containsKey(long k) {
            return false;
        }

        @Override
        public short defaultReturnValue() {
            return 0;
        }

        @Override
        public void defaultReturnValue(short defRetValue) {
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
            return Long2ShortFunctions.EMPTY_FUNCTION;
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
            return Long2ShortFunctions.EMPTY_FUNCTION;
        }
    }

}

