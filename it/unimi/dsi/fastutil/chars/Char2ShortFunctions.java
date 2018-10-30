/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.Function;
import it.unimi.dsi.fastutil.SafeMath;
import it.unimi.dsi.fastutil.chars.AbstractChar2ShortFunction;
import it.unimi.dsi.fastutil.chars.Char2ShortFunction;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Objects;
import java.util.function.IntUnaryOperator;

public final class Char2ShortFunctions {
    public static final EmptyFunction EMPTY_FUNCTION = new EmptyFunction();

    private Char2ShortFunctions() {
    }

    public static Char2ShortFunction singleton(char key, short value) {
        return new Singleton(key, value);
    }

    public static Char2ShortFunction singleton(Character key, Short value) {
        return new Singleton(key.charValue(), value);
    }

    public static Char2ShortFunction synchronize(Char2ShortFunction f) {
        return new SynchronizedFunction(f);
    }

    public static Char2ShortFunction synchronize(Char2ShortFunction f, Object sync) {
        return new SynchronizedFunction(f, sync);
    }

    public static Char2ShortFunction unmodifiable(Char2ShortFunction f) {
        return new UnmodifiableFunction(f);
    }

    public static Char2ShortFunction primitive(java.util.function.Function<? super Character, ? extends Short> f) {
        Objects.requireNonNull(f);
        if (f instanceof Char2ShortFunction) {
            return (Char2ShortFunction)f;
        }
        if (f instanceof IntUnaryOperator) {
            return key -> SafeMath.safeIntToShort(((IntUnaryOperator)((Object)f)).applyAsInt(key));
        }
        return new PrimitiveFunction(f);
    }

    public static class PrimitiveFunction
    implements Char2ShortFunction {
        protected final java.util.function.Function<? super Character, ? extends Short> function;

        protected PrimitiveFunction(java.util.function.Function<? super Character, ? extends Short> function) {
            this.function = function;
        }

        @Override
        public boolean containsKey(char key) {
            return this.function.apply(Character.valueOf(key)) != null;
        }

        @Deprecated
        @Override
        public boolean containsKey(Object key) {
            if (key == null) {
                return false;
            }
            return this.function.apply((Character)key) != null;
        }

        @Override
        public short get(char key) {
            Short v = this.function.apply(Character.valueOf(key));
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
            return this.function.apply((Character)key);
        }

        @Deprecated
        @Override
        public Short put(Character key, Short value) {
            throw new UnsupportedOperationException();
        }
    }

    public static class UnmodifiableFunction
    extends AbstractChar2ShortFunction
    implements Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final Char2ShortFunction function;

        protected UnmodifiableFunction(Char2ShortFunction f) {
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
        public boolean containsKey(char k) {
            return this.function.containsKey(k);
        }

        @Override
        public short put(char k, short v) {
            throw new UnsupportedOperationException();
        }

        @Override
        public short get(char k) {
            return this.function.get(k);
        }

        @Override
        public short remove(char k) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void clear() {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public Short put(Character k, Short v) {
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
    implements Char2ShortFunction,
    Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final Char2ShortFunction function;
        protected final Object sync;

        protected SynchronizedFunction(Char2ShortFunction f, Object sync) {
            if (f == null) {
                throw new NullPointerException();
            }
            this.function = f;
            this.sync = sync;
        }

        protected SynchronizedFunction(Char2ShortFunction f) {
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
        public int applyAsInt(int operand) {
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
        public Short apply(Character key) {
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
        public boolean containsKey(char k) {
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
        public short put(char k, short v) {
            Object object = this.sync;
            synchronized (object) {
                return this.function.put(k, v);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public short get(char k) {
            Object object = this.sync;
            synchronized (object) {
                return this.function.get(k);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public short remove(char k) {
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
        public Short put(Character k, Short v) {
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
    extends AbstractChar2ShortFunction
    implements Serializable,
    Cloneable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final char key;
        protected final short value;

        protected Singleton(char key, short value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public boolean containsKey(char k) {
            return this.key == k;
        }

        @Override
        public short get(char k) {
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
    extends AbstractChar2ShortFunction
    implements Serializable,
    Cloneable {
        private static final long serialVersionUID = -7046029254386353129L;

        protected EmptyFunction() {
        }

        @Override
        public short get(char k) {
            return 0;
        }

        @Override
        public boolean containsKey(char k) {
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
            return Char2ShortFunctions.EMPTY_FUNCTION;
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
            return Char2ShortFunctions.EMPTY_FUNCTION;
        }
    }

}

