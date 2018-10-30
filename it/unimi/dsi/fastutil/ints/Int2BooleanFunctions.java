/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.ints;

import it.unimi.dsi.fastutil.Function;
import it.unimi.dsi.fastutil.ints.AbstractInt2BooleanFunction;
import it.unimi.dsi.fastutil.ints.Int2BooleanFunction;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Objects;
import java.util.function.IntPredicate;

public final class Int2BooleanFunctions {
    public static final EmptyFunction EMPTY_FUNCTION = new EmptyFunction();

    private Int2BooleanFunctions() {
    }

    public static Int2BooleanFunction singleton(int key, boolean value) {
        return new Singleton(key, value);
    }

    public static Int2BooleanFunction singleton(Integer key, Boolean value) {
        return new Singleton(key, value);
    }

    public static Int2BooleanFunction synchronize(Int2BooleanFunction f) {
        return new SynchronizedFunction(f);
    }

    public static Int2BooleanFunction synchronize(Int2BooleanFunction f, Object sync) {
        return new SynchronizedFunction(f, sync);
    }

    public static Int2BooleanFunction unmodifiable(Int2BooleanFunction f) {
        return new UnmodifiableFunction(f);
    }

    public static Int2BooleanFunction primitive(java.util.function.Function<? super Integer, ? extends Boolean> f) {
        Objects.requireNonNull(f);
        if (f instanceof Int2BooleanFunction) {
            return (Int2BooleanFunction)f;
        }
        if (f instanceof IntPredicate) {
            return ((IntPredicate)((Object)f))::test;
        }
        return new PrimitiveFunction(f);
    }

    public static class PrimitiveFunction
    implements Int2BooleanFunction {
        protected final java.util.function.Function<? super Integer, ? extends Boolean> function;

        protected PrimitiveFunction(java.util.function.Function<? super Integer, ? extends Boolean> function) {
            this.function = function;
        }

        @Override
        public boolean containsKey(int key) {
            return this.function.apply((Integer)key) != null;
        }

        @Deprecated
        @Override
        public boolean containsKey(Object key) {
            if (key == null) {
                return false;
            }
            return this.function.apply((Integer)key) != null;
        }

        @Override
        public boolean get(int key) {
            Boolean v = this.function.apply((Integer)key);
            if (v == null) {
                return this.defaultReturnValue();
            }
            return v;
        }

        @Deprecated
        @Override
        public Boolean get(Object key) {
            if (key == null) {
                return null;
            }
            return this.function.apply((Integer)key);
        }

        @Deprecated
        @Override
        public Boolean put(Integer key, Boolean value) {
            throw new UnsupportedOperationException();
        }
    }

    public static class UnmodifiableFunction
    extends AbstractInt2BooleanFunction
    implements Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final Int2BooleanFunction function;

        protected UnmodifiableFunction(Int2BooleanFunction f) {
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
        public boolean defaultReturnValue() {
            return this.function.defaultReturnValue();
        }

        @Override
        public void defaultReturnValue(boolean defRetValue) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean containsKey(int k) {
            return this.function.containsKey(k);
        }

        @Override
        public boolean put(int k, boolean v) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean get(int k) {
            return this.function.get(k);
        }

        @Override
        public boolean remove(int k) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void clear() {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public Boolean put(Integer k, Boolean v) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public Boolean get(Object k) {
            return this.function.get(k);
        }

        @Deprecated
        @Override
        public Boolean remove(Object k) {
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
    implements Int2BooleanFunction,
    Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final Int2BooleanFunction function;
        protected final Object sync;

        protected SynchronizedFunction(Int2BooleanFunction f, Object sync) {
            if (f == null) {
                throw new NullPointerException();
            }
            this.function = f;
            this.sync = sync;
        }

        protected SynchronizedFunction(Int2BooleanFunction f) {
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
        public boolean test(int operand) {
            Object object = this.sync;
            synchronized (object) {
                return this.function.test(operand);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Deprecated
        @Override
        public Boolean apply(Integer key) {
            Object object = this.sync;
            synchronized (object) {
                return (Boolean)this.function.apply(key);
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
        public boolean defaultReturnValue() {
            Object object = this.sync;
            synchronized (object) {
                return this.function.defaultReturnValue();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void defaultReturnValue(boolean defRetValue) {
            Object object = this.sync;
            synchronized (object) {
                this.function.defaultReturnValue(defRetValue);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean containsKey(int k) {
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
        public boolean put(int k, boolean v) {
            Object object = this.sync;
            synchronized (object) {
                return this.function.put(k, v);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean get(int k) {
            Object object = this.sync;
            synchronized (object) {
                return this.function.get(k);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean remove(int k) {
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
        public Boolean put(Integer k, Boolean v) {
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
        public Boolean get(Object k) {
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
        public Boolean remove(Object k) {
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
    extends AbstractInt2BooleanFunction
    implements Serializable,
    Cloneable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final int key;
        protected final boolean value;

        protected Singleton(int key, boolean value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public boolean containsKey(int k) {
            return this.key == k;
        }

        @Override
        public boolean get(int k) {
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
    extends AbstractInt2BooleanFunction
    implements Serializable,
    Cloneable {
        private static final long serialVersionUID = -7046029254386353129L;

        protected EmptyFunction() {
        }

        @Override
        public boolean get(int k) {
            return false;
        }

        @Override
        public boolean containsKey(int k) {
            return false;
        }

        @Override
        public boolean defaultReturnValue() {
            return false;
        }

        @Override
        public void defaultReturnValue(boolean defRetValue) {
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
            return Int2BooleanFunctions.EMPTY_FUNCTION;
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
            return Int2BooleanFunctions.EMPTY_FUNCTION;
        }
    }

}

