/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.Function;
import it.unimi.dsi.fastutil.SafeMath;
import it.unimi.dsi.fastutil.floats.AbstractFloat2CharFunction;
import it.unimi.dsi.fastutil.floats.Float2CharFunction;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Objects;
import java.util.function.DoubleToIntFunction;

public final class Float2CharFunctions {
    public static final EmptyFunction EMPTY_FUNCTION = new EmptyFunction();

    private Float2CharFunctions() {
    }

    public static Float2CharFunction singleton(float key, char value) {
        return new Singleton(key, value);
    }

    public static Float2CharFunction singleton(Float key, Character value) {
        return new Singleton(key.floatValue(), value.charValue());
    }

    public static Float2CharFunction synchronize(Float2CharFunction f) {
        return new SynchronizedFunction(f);
    }

    public static Float2CharFunction synchronize(Float2CharFunction f, Object sync) {
        return new SynchronizedFunction(f, sync);
    }

    public static Float2CharFunction unmodifiable(Float2CharFunction f) {
        return new UnmodifiableFunction(f);
    }

    public static Float2CharFunction primitive(java.util.function.Function<? super Float, ? extends Character> f) {
        Objects.requireNonNull(f);
        if (f instanceof Float2CharFunction) {
            return (Float2CharFunction)f;
        }
        if (f instanceof DoubleToIntFunction) {
            return key -> SafeMath.safeIntToChar(((DoubleToIntFunction)((Object)f)).applyAsInt(key));
        }
        return new PrimitiveFunction(f);
    }

    public static class PrimitiveFunction
    implements Float2CharFunction {
        protected final java.util.function.Function<? super Float, ? extends Character> function;

        protected PrimitiveFunction(java.util.function.Function<? super Float, ? extends Character> function) {
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
        public char get(float key) {
            Character v = this.function.apply(Float.valueOf(key));
            if (v == null) {
                return this.defaultReturnValue();
            }
            return v.charValue();
        }

        @Deprecated
        @Override
        public Character get(Object key) {
            if (key == null) {
                return null;
            }
            return this.function.apply((Float)key);
        }

        @Deprecated
        @Override
        public Character put(Float key, Character value) {
            throw new UnsupportedOperationException();
        }
    }

    public static class UnmodifiableFunction
    extends AbstractFloat2CharFunction
    implements Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final Float2CharFunction function;

        protected UnmodifiableFunction(Float2CharFunction f) {
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
        public char defaultReturnValue() {
            return this.function.defaultReturnValue();
        }

        @Override
        public void defaultReturnValue(char defRetValue) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean containsKey(float k) {
            return this.function.containsKey(k);
        }

        @Override
        public char put(float k, char v) {
            throw new UnsupportedOperationException();
        }

        @Override
        public char get(float k) {
            return this.function.get(k);
        }

        @Override
        public char remove(float k) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void clear() {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public Character put(Float k, Character v) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public Character get(Object k) {
            return this.function.get(k);
        }

        @Deprecated
        @Override
        public Character remove(Object k) {
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
    implements Float2CharFunction,
    Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final Float2CharFunction function;
        protected final Object sync;

        protected SynchronizedFunction(Float2CharFunction f, Object sync) {
            if (f == null) {
                throw new NullPointerException();
            }
            this.function = f;
            this.sync = sync;
        }

        protected SynchronizedFunction(Float2CharFunction f) {
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
        public Character apply(Float key) {
            Object object = this.sync;
            synchronized (object) {
                return (Character)this.function.apply(key);
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
        public char defaultReturnValue() {
            Object object = this.sync;
            synchronized (object) {
                return this.function.defaultReturnValue();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void defaultReturnValue(char defRetValue) {
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
        public char put(float k, char v) {
            Object object = this.sync;
            synchronized (object) {
                return this.function.put(k, v);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public char get(float k) {
            Object object = this.sync;
            synchronized (object) {
                return this.function.get(k);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public char remove(float k) {
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
        public Character put(Float k, Character v) {
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
        public Character get(Object k) {
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
        public Character remove(Object k) {
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
    extends AbstractFloat2CharFunction
    implements Serializable,
    Cloneable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final float key;
        protected final char value;

        protected Singleton(float key, char value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public boolean containsKey(float k) {
            return Float.floatToIntBits(this.key) == Float.floatToIntBits(k);
        }

        @Override
        public char get(float k) {
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
    extends AbstractFloat2CharFunction
    implements Serializable,
    Cloneable {
        private static final long serialVersionUID = -7046029254386353129L;

        protected EmptyFunction() {
        }

        @Override
        public char get(float k) {
            return '\u0000';
        }

        @Override
        public boolean containsKey(float k) {
            return false;
        }

        @Override
        public char defaultReturnValue() {
            return '\u0000';
        }

        @Override
        public void defaultReturnValue(char defRetValue) {
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
            return Float2CharFunctions.EMPTY_FUNCTION;
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
            return Float2CharFunctions.EMPTY_FUNCTION;
        }
    }

}

