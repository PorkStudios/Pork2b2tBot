/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.doubles;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.bytes.ByteCollection;
import it.unimi.dsi.fastutil.bytes.ByteCollections;
import it.unimi.dsi.fastutil.bytes.ByteSets;
import it.unimi.dsi.fastutil.doubles.AbstractDouble2ByteMap;
import it.unimi.dsi.fastutil.doubles.Double2ByteFunction;
import it.unimi.dsi.fastutil.doubles.Double2ByteFunctions;
import it.unimi.dsi.fastutil.doubles.Double2ByteMap;
import it.unimi.dsi.fastutil.doubles.DoubleSet;
import it.unimi.dsi.fastutil.doubles.DoubleSets;
import it.unimi.dsi.fastutil.objects.ObjectIterable;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import it.unimi.dsi.fastutil.objects.ObjectSets;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.DoubleFunction;
import java.util.function.DoubleToIntFunction;
import java.util.function.Function;

public final class Double2ByteMaps {
    public static final EmptyMap EMPTY_MAP = new EmptyMap();

    private Double2ByteMaps() {
    }

    public static ObjectIterator<Double2ByteMap.Entry> fastIterator(Double2ByteMap map) {
        ObjectSet<Double2ByteMap.Entry> entries = map.double2ByteEntrySet();
        return entries instanceof Double2ByteMap.FastEntrySet ? ((Double2ByteMap.FastEntrySet)entries).fastIterator() : entries.iterator();
    }

    public static void fastForEach(Double2ByteMap map, Consumer<? super Double2ByteMap.Entry> consumer) {
        ObjectSet<Double2ByteMap.Entry> entries = map.double2ByteEntrySet();
        if (entries instanceof Double2ByteMap.FastEntrySet) {
            ((Double2ByteMap.FastEntrySet)entries).fastForEach(consumer);
        } else {
            entries.forEach(consumer);
        }
    }

    public static ObjectIterable<Double2ByteMap.Entry> fastIterable(Double2ByteMap map) {
        final ObjectSet<Double2ByteMap.Entry> entries = map.double2ByteEntrySet();
        return entries instanceof Double2ByteMap.FastEntrySet ? new ObjectIterable<Double2ByteMap.Entry>(){

            @Override
            public ObjectIterator<Double2ByteMap.Entry> iterator() {
                return ((Double2ByteMap.FastEntrySet)entries).fastIterator();
            }

            @Override
            public void forEach(Consumer<? super Double2ByteMap.Entry> consumer) {
                ((Double2ByteMap.FastEntrySet)entries).fastForEach(consumer);
            }
        } : entries;
    }

    public static Double2ByteMap singleton(double key, byte value) {
        return new Singleton(key, value);
    }

    public static Double2ByteMap singleton(Double key, Byte value) {
        return new Singleton(key, value);
    }

    public static Double2ByteMap synchronize(Double2ByteMap m) {
        return new SynchronizedMap(m);
    }

    public static Double2ByteMap synchronize(Double2ByteMap m, Object sync) {
        return new SynchronizedMap(m, sync);
    }

    public static Double2ByteMap unmodifiable(Double2ByteMap m) {
        return new UnmodifiableMap(m);
    }

    public static class UnmodifiableMap
    extends Double2ByteFunctions.UnmodifiableFunction
    implements Double2ByteMap,
    Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final Double2ByteMap map;
        protected transient ObjectSet<Double2ByteMap.Entry> entries;
        protected transient DoubleSet keys;
        protected transient ByteCollection values;

        protected UnmodifiableMap(Double2ByteMap m) {
            super(m);
            this.map = m;
        }

        @Override
        public boolean containsValue(byte v) {
            return this.map.containsValue(v);
        }

        @Deprecated
        @Override
        public boolean containsValue(Object ov) {
            return this.map.containsValue(ov);
        }

        @Override
        public void putAll(Map<? extends Double, ? extends Byte> m) {
            throw new UnsupportedOperationException();
        }

        @Override
        public ObjectSet<Double2ByteMap.Entry> double2ByteEntrySet() {
            if (this.entries == null) {
                this.entries = ObjectSets.unmodifiable(this.map.double2ByteEntrySet());
            }
            return this.entries;
        }

        @Deprecated
        @Override
        public ObjectSet<Map.Entry<Double, Byte>> entrySet() {
            return this.double2ByteEntrySet();
        }

        @Override
        public DoubleSet keySet() {
            if (this.keys == null) {
                this.keys = DoubleSets.unmodifiable(this.map.keySet());
            }
            return this.keys;
        }

        @Override
        public ByteCollection values() {
            if (this.values == null) {
                return ByteCollections.unmodifiable(this.map.values());
            }
            return this.values;
        }

        @Override
        public boolean isEmpty() {
            return this.map.isEmpty();
        }

        @Override
        public int hashCode() {
            return this.map.hashCode();
        }

        @Override
        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            return this.map.equals(o);
        }

        @Override
        public byte getOrDefault(double key, byte defaultValue) {
            return this.map.getOrDefault(key, defaultValue);
        }

        @Override
        public void forEach(BiConsumer<? super Double, ? super Byte> action) {
            this.map.forEach(action);
        }

        @Override
        public void replaceAll(BiFunction<? super Double, ? super Byte, ? extends Byte> function) {
            throw new UnsupportedOperationException();
        }

        @Override
        public byte putIfAbsent(double key, byte value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean remove(double key, byte value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public byte replace(double key, byte value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean replace(double key, byte oldValue, byte newValue) {
            throw new UnsupportedOperationException();
        }

        @Override
        public byte computeIfAbsent(double key, DoubleToIntFunction mappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Override
        public byte computeIfAbsentNullable(double key, DoubleFunction<? extends Byte> mappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Override
        public byte computeIfAbsentPartial(double key, Double2ByteFunction mappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Override
        public byte computeIfPresent(double key, BiFunction<? super Double, ? super Byte, ? extends Byte> remappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Override
        public byte compute(double key, BiFunction<? super Double, ? super Byte, ? extends Byte> remappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Override
        public byte merge(double key, byte value, BiFunction<? super Byte, ? super Byte, ? extends Byte> remappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public Byte getOrDefault(Object key, Byte defaultValue) {
            return this.map.getOrDefault(key, defaultValue);
        }

        @Deprecated
        @Override
        public boolean remove(Object key, Object value) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public Byte replace(Double key, Byte value) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public boolean replace(Double key, Byte oldValue, Byte newValue) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public Byte putIfAbsent(Double key, Byte value) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public Byte computeIfAbsent(Double key, Function<? super Double, ? extends Byte> mappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public Byte computeIfPresent(Double key, BiFunction<? super Double, ? super Byte, ? extends Byte> remappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public Byte compute(Double key, BiFunction<? super Double, ? super Byte, ? extends Byte> remappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public Byte merge(Double key, Byte value, BiFunction<? super Byte, ? super Byte, ? extends Byte> remappingFunction) {
            throw new UnsupportedOperationException();
        }
    }

    public static class SynchronizedMap
    extends Double2ByteFunctions.SynchronizedFunction
    implements Double2ByteMap,
    Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final Double2ByteMap map;
        protected transient ObjectSet<Double2ByteMap.Entry> entries;
        protected transient DoubleSet keys;
        protected transient ByteCollection values;

        protected SynchronizedMap(Double2ByteMap m, Object sync) {
            super(m, sync);
            this.map = m;
        }

        protected SynchronizedMap(Double2ByteMap m) {
            super(m);
            this.map = m;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean containsValue(byte v) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.containsValue(v);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Deprecated
        @Override
        public boolean containsValue(Object ov) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.containsValue(ov);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void putAll(Map<? extends Double, ? extends Byte> m) {
            Object object = this.sync;
            synchronized (object) {
                this.map.putAll(m);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public ObjectSet<Double2ByteMap.Entry> double2ByteEntrySet() {
            Object object = this.sync;
            synchronized (object) {
                if (this.entries == null) {
                    this.entries = ObjectSets.synchronize(this.map.double2ByteEntrySet(), this.sync);
                }
                return this.entries;
            }
        }

        @Deprecated
        @Override
        public ObjectSet<Map.Entry<Double, Byte>> entrySet() {
            return this.double2ByteEntrySet();
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public DoubleSet keySet() {
            Object object = this.sync;
            synchronized (object) {
                if (this.keys == null) {
                    this.keys = DoubleSets.synchronize(this.map.keySet(), this.sync);
                }
                return this.keys;
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public ByteCollection values() {
            Object object = this.sync;
            synchronized (object) {
                if (this.values == null) {
                    return ByteCollections.synchronize(this.map.values(), this.sync);
                }
                return this.values;
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean isEmpty() {
            Object object = this.sync;
            synchronized (object) {
                return this.map.isEmpty();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public int hashCode() {
            Object object = this.sync;
            synchronized (object) {
                return this.map.hashCode();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            Object object = this.sync;
            synchronized (object) {
                return this.map.equals(o);
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

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public byte getOrDefault(double key, byte defaultValue) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.getOrDefault(key, defaultValue);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void forEach(BiConsumer<? super Double, ? super Byte> action) {
            Object object = this.sync;
            synchronized (object) {
                this.map.forEach(action);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void replaceAll(BiFunction<? super Double, ? super Byte, ? extends Byte> function) {
            Object object = this.sync;
            synchronized (object) {
                this.map.replaceAll(function);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public byte putIfAbsent(double key, byte value) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.putIfAbsent(key, value);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean remove(double key, byte value) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.remove(key, value);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public byte replace(double key, byte value) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.replace(key, value);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean replace(double key, byte oldValue, byte newValue) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.replace(key, oldValue, newValue);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public byte computeIfAbsent(double key, DoubleToIntFunction mappingFunction) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.computeIfAbsent(key, mappingFunction);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public byte computeIfAbsentNullable(double key, DoubleFunction<? extends Byte> mappingFunction) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.computeIfAbsentNullable(key, mappingFunction);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public byte computeIfAbsentPartial(double key, Double2ByteFunction mappingFunction) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.computeIfAbsentPartial(key, mappingFunction);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public byte computeIfPresent(double key, BiFunction<? super Double, ? super Byte, ? extends Byte> remappingFunction) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.computeIfPresent(key, remappingFunction);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public byte compute(double key, BiFunction<? super Double, ? super Byte, ? extends Byte> remappingFunction) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.compute(key, remappingFunction);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public byte merge(double key, byte value, BiFunction<? super Byte, ? super Byte, ? extends Byte> remappingFunction) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.merge(key, value, remappingFunction);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Deprecated
        @Override
        public Byte getOrDefault(Object key, Byte defaultValue) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.getOrDefault(key, defaultValue);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Deprecated
        @Override
        public boolean remove(Object key, Object value) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.remove(key, value);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Deprecated
        @Override
        public Byte replace(Double key, Byte value) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.replace(key, value);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Deprecated
        @Override
        public boolean replace(Double key, Byte oldValue, Byte newValue) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.replace(key, oldValue, newValue);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Deprecated
        @Override
        public Byte putIfAbsent(Double key, Byte value) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.putIfAbsent(key, value);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Deprecated
        @Override
        public Byte computeIfAbsent(Double key, Function<? super Double, ? extends Byte> mappingFunction) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.computeIfAbsent(key, mappingFunction);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Deprecated
        @Override
        public Byte computeIfPresent(Double key, BiFunction<? super Double, ? super Byte, ? extends Byte> remappingFunction) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.computeIfPresent(key, remappingFunction);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Deprecated
        @Override
        public Byte compute(Double key, BiFunction<? super Double, ? super Byte, ? extends Byte> remappingFunction) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.compute(key, remappingFunction);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Deprecated
        @Override
        public Byte merge(Double key, Byte value, BiFunction<? super Byte, ? super Byte, ? extends Byte> remappingFunction) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.merge(key, value, remappingFunction);
            }
        }
    }

    public static class Singleton
    extends Double2ByteFunctions.Singleton
    implements Double2ByteMap,
    Serializable,
    Cloneable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected transient ObjectSet<Double2ByteMap.Entry> entries;
        protected transient DoubleSet keys;
        protected transient ByteCollection values;

        protected Singleton(double key, byte value) {
            super(key, value);
        }

        @Override
        public boolean containsValue(byte v) {
            return this.value == v;
        }

        @Deprecated
        @Override
        public boolean containsValue(Object ov) {
            return (Byte)ov == this.value;
        }

        @Override
        public void putAll(Map<? extends Double, ? extends Byte> m) {
            throw new UnsupportedOperationException();
        }

        @Override
        public ObjectSet<Double2ByteMap.Entry> double2ByteEntrySet() {
            if (this.entries == null) {
                this.entries = ObjectSets.singleton(new AbstractDouble2ByteMap.BasicEntry(this.key, this.value));
            }
            return this.entries;
        }

        @Deprecated
        @Override
        public ObjectSet<Map.Entry<Double, Byte>> entrySet() {
            return this.double2ByteEntrySet();
        }

        @Override
        public DoubleSet keySet() {
            if (this.keys == null) {
                this.keys = DoubleSets.singleton(this.key);
            }
            return this.keys;
        }

        @Override
        public ByteCollection values() {
            if (this.values == null) {
                this.values = ByteSets.singleton(this.value);
            }
            return this.values;
        }

        @Override
        public boolean isEmpty() {
            return false;
        }

        @Override
        public int hashCode() {
            return HashCommon.double2int(this.key) ^ this.value;
        }

        @Override
        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            if (!(o instanceof Map)) {
                return false;
            }
            Map m = (Map)o;
            if (m.size() != 1) {
                return false;
            }
            return m.entrySet().iterator().next().equals(this.entrySet().iterator().next());
        }

        public String toString() {
            return "{" + this.key + "=>" + this.value + "}";
        }
    }

    public static class EmptyMap
    extends Double2ByteFunctions.EmptyFunction
    implements Double2ByteMap,
    Serializable,
    Cloneable {
        private static final long serialVersionUID = -7046029254386353129L;

        protected EmptyMap() {
        }

        @Override
        public boolean containsValue(byte v) {
            return false;
        }

        @Deprecated
        @Override
        public boolean containsValue(Object ov) {
            return false;
        }

        @Override
        public void putAll(Map<? extends Double, ? extends Byte> m) {
            throw new UnsupportedOperationException();
        }

        @Override
        public ObjectSet<Double2ByteMap.Entry> double2ByteEntrySet() {
            return ObjectSets.EMPTY_SET;
        }

        @Override
        public DoubleSet keySet() {
            return DoubleSets.EMPTY_SET;
        }

        @Override
        public ByteCollection values() {
            return ByteSets.EMPTY_SET;
        }

        @Override
        public Object clone() {
            return Double2ByteMaps.EMPTY_MAP;
        }

        @Override
        public boolean isEmpty() {
            return true;
        }

        @Override
        public int hashCode() {
            return 0;
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof Map)) {
                return false;
            }
            return ((Map)o).isEmpty();
        }

        @Override
        public String toString() {
            return "{}";
        }
    }

}

