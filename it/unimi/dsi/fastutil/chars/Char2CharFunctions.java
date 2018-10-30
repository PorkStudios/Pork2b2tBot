/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.Function;
import it.unimi.dsi.fastutil.SafeMath;
import it.unimi.dsi.fastutil.chars.AbstractChar2CharFunction;
import it.unimi.dsi.fastutil.chars.Char2CharFunction;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Objects;
import java.util.function.IntUnaryOperator;

public final class Char2CharFunctions {
    public static final EmptyFunction EMPTY_FUNCTION = new EmptyFunction();

    private Char2CharFunctions() {
    }

    public static Char2CharFunction singleton(char key, char value) {
        return new Singleton(key, value);
    }

    public static Char2CharFunction singleton(Character key, Character value) {
        return new Singleton(key.charValue(), value.charValue());
    }

    public static Char2CharFunction synchronize(Char2CharFunction f) {
        return new SynchronizedFunction(f);
    }

    public static Char2CharFunction synchronize(Char2CharFunction f, Object sync) {
        return new SynchronizedFunction(f, sync);
    }

    public static Char2CharFunction unmodifiable(Char2CharFunction f) {
        return new UnmodifiableFunction(f);
    }

    public static Char2CharFunction primitive(java.util.function.Function<? super Character, ? extends Character> f) {
        Objects.requireNonNull(f);
        if (f instanceof Char2CharFunction) {
            return (Char2CharFunction)f;
        }
        if (f instanceof IntUnaryOperator) {
            return key -> SafeMath.safeIntToChar(((IntUnaryOperator)((Object)f)).applyAsInt(key));
        }
        return new PrimitiveFunction(f);
    }

    public static class PrimitiveFunction
    implements Char2CharFunction {
        protected final java.util.function.Function<? super Character, ? extends Character> function;

        protected PrimitiveFunction(java.util.function.Function<? super Character, ? extends Character> function) {
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
        public char get(char key) {
            Character v = this.function.apply(Character.valueOf(key));
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
            return this.function.apply((Character)key);
        }

        @Deprecated
        @Override
        public Character put(Character key, Character value) {
            throw new UnsupportedOperationException();
        }
    }

    public static class UnmodifiableFunction
    extends AbstractChar2CharFunction
    implements Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final Char2CharFunction function;

        protected UnmodifiableFunction(Char2CharFunction f) {
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
        public boolean containsKey(char k) {
            return this.function.containsKey(k);
        }

        @Override
        public char put(char k, char v) {
            throw new UnsupportedOperationException();
        }

        @Override
        public char get(char k) {
            return this.function.get(k);
        }

        @Override
        public char remove(char k) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void clear() {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public Character put(Character k, Character v) {
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
    implements Char2CharFunction,
    Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final Char2CharFunction function;
        protected final Object sync;

        protected SynchronizedFunction(Char2CharFunction f, Object sync) {
            if (f == null) {
                throw new NullPointerException();
            }
            this.function = f;
            this.sync = sync;
        }

        protected SynchronizedFunction(Char2CharFunction f) {
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
        public Character apply(Character key) {
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
        public char put(char k, char v) {
            Object object = this.sync;
            synchronized (object) {
                return this.function.put(k, v);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public char get(char k) {
            Object object = this.sync;
            synchronized (object) {
                return this.function.get(k);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public char remove(char k) {
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
        public Character put(Character k, Character v) {
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
    extends AbstractChar2CharFunction
    implements Serializable,
    Cloneable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final char key;
        protected final char value;

        protected Singleton(char key, char value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public boolean containsKey(char k) {
            return this.key == k;
        }

        @Override
        public char get(char k) {
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
    extends AbstractChar2CharFunction
    implements Serializable,
    Cloneable {
        private static final long serialVersionUID = -7046029254386353129L;

        protected EmptyFunction() {
        }

        @Override
        public char get(char k) {
            return '\u0000';
        }

        @Override
        public boolean containsKey(char k) {
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
            return Char2CharFunctions.EMPTY_FUNCTION;
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
            return Char2CharFunctions.EMPTY_FUNCTION;
        }
    }

}

