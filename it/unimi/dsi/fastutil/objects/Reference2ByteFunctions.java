/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.Function;
import it.unimi.dsi.fastutil.SafeMath;
import it.unimi.dsi.fastutil.objects.AbstractReference2ByteFunction;
import it.unimi.dsi.fastutil.objects.Reference2ByteFunction;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Objects;
import java.util.function.ToIntFunction;

public final class Reference2ByteFunctions {
    public static final EmptyFunction EMPTY_FUNCTION = new EmptyFunction();

    private Reference2ByteFunctions() {
    }

    public static <K> Reference2ByteFunction<K> singleton(K key, byte value) {
        return new Singleton<K>(key, value);
    }

    public static <K> Reference2ByteFunction<K> singleton(K key, Byte value) {
        return new Singleton<K>(key, value);
    }

    public static <K> Reference2ByteFunction<K> synchronize(Reference2ByteFunction<K> f) {
        return new SynchronizedFunction<K>(f);
    }

    public static <K> Reference2ByteFunction<K> synchronize(Reference2ByteFunction<K> f, Object sync) {
        return new SynchronizedFunction<K>(f, sync);
    }

    public static <K> Reference2ByteFunction<K> unmodifiable(Reference2ByteFunction<K> f) {
        return new UnmodifiableFunction<K>(f);
    }

    public static <K> Reference2ByteFunction<K> primitive(java.util.function.Function<? super K, ? extends Byte> f) {
        Objects.requireNonNull(f);
        if (f instanceof Reference2ByteFunction) {
            return (Reference2ByteFunction)f;
        }
        if (f instanceof ToIntFunction) {
            return key -> SafeMath.safeIntToByte(((ToIntFunction)((Object)f)).applyAsInt(key));
        }
        return new PrimitiveFunction<K>(f);
    }

    public static class PrimitiveFunction<K>
    implements Reference2ByteFunction<K> {
        protected final java.util.function.Function<? super K, ? extends Byte> function;

        protected PrimitiveFunction(java.util.function.Function<? super K, ? extends Byte> function) {
            this.function = function;
        }

        @Override
        public boolean containsKey(Object key) {
            return this.function.apply(key) != null;
        }

        @Override
        public byte getByte(Object key) {
            Byte v = this.function.apply(key);
            if (v == null) {
                return this.defaultReturnValue();
            }
            return v;
        }

        @Deprecated
        @Override
        public Byte get(Object key) {
            return this.function.apply(key);
        }

        @Deprecated
        @Override
        public Byte put(K key, Byte value) {
            throw new UnsupportedOperationException();
        }
    }

    public static class UnmodifiableFunction<K>
    extends AbstractReference2ByteFunction<K>
    implements Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final Reference2ByteFunction<K> function;

        protected UnmodifiableFunction(Reference2ByteFunction<K> f) {
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
        public byte defaultReturnValue() {
            return this.function.defaultReturnValue();
        }

        @Override
        public void defaultReturnValue(byte defRetValue) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean containsKey(Object k) {
            return this.function.containsKey(k);
        }

        @Override
        public byte put(K k, byte v) {
            throw new UnsupportedOperationException();
        }

        @Override
        public byte getByte(Object k) {
            return this.function.getByte(k);
        }

        @Override
        public byte removeByte(Object k) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void clear() {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public Byte put(K k, Byte v) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public Byte get(Object k) {
            return this.function.get(k);
        }

        @Deprecated
        @Override
        public Byte remove(Object k) {
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
    implements Reference2ByteFunction<K>,
    Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final Reference2ByteFunction<K> function;
        protected final Object sync;

        protected SynchronizedFunction(Reference2ByteFunction<K> f, Object sync) {
            if (f == null) {
                throw new NullPointerException();
            }
            this.function = f;
            this.sync = sync;
        }

        protected SynchronizedFunction(Reference2ByteFunction<K> f) {
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
        public int applyAsInt(K operand) {
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
        public Byte apply(K key) {
            Object object = this.sync;
            synchronized (object) {
                return (Byte)this.function.apply(key);
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
        public byte defaultReturnValue() {
            Object object = this.sync;
            synchronized (object) {
                return this.function.defaultReturnValue();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void defaultReturnValue(byte defRetValue) {
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
        public byte put(K k, byte v) {
            Object object = this.sync;
            synchronized (object) {
                return this.function.put(k, v);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public byte getByte(Object k) {
            Object object = this.sync;
            synchronized (object) {
                return this.function.getByte(k);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public byte removeByte(Object k) {
            Object object = this.sync;
            synchronized (object) {
                return this.function.removeByte(k);
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
        public Byte put(K k, Byte v) {
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
        public Byte get(Object k) {
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
        public Byte remove(Object k) {
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
    extends AbstractReference2ByteFunction<K>
    implements Serializable,
    Cloneable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final K key;
        protected final byte value;

        protected Singleton(K key, byte value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public boolean containsKey(Object k) {
            return this.key == k;
        }

        @Override
        public byte getByte(Object k) {
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

    public static class EmptyFunction<K>
    extends AbstractReference2ByteFunction<K>
    implements Serializable,
    Cloneable {
        private static final long serialVersionUID = -7046029254386353129L;

        protected EmptyFunction() {
        }

        @Override
        public byte getByte(Object k) {
            return 0;
        }

        @Override
        public boolean containsKey(Object k) {
            return false;
        }

        @Override
        public byte defaultReturnValue() {
            return 0;
        }

        @Override
        public void defaultReturnValue(byte defRetValue) {
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
            return Reference2ByteFunctions.EMPTY_FUNCTION;
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
            return Reference2ByteFunctions.EMPTY_FUNCTION;
        }
    }

}

