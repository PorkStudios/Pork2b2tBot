/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.bytes.ByteCollection;
import it.unimi.dsi.fastutil.bytes.ByteCollections;
import it.unimi.dsi.fastutil.bytes.ByteSets;
import it.unimi.dsi.fastutil.floats.AbstractFloat2ByteMap;
import it.unimi.dsi.fastutil.floats.Float2ByteFunction;
import it.unimi.dsi.fastutil.floats.Float2ByteFunctions;
import it.unimi.dsi.fastutil.floats.Float2ByteMap;
import it.unimi.dsi.fastutil.floats.FloatSet;
import it.unimi.dsi.fastutil.floats.FloatSets;
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

public final class Float2ByteMaps {
    public static final EmptyMap EMPTY_MAP = new EmptyMap();

    private Float2ByteMaps() {
    }

    public static ObjectIterator<Float2ByteMap.Entry> fastIterator(Float2ByteMap map) {
        ObjectSet<Float2ByteMap.Entry> entries = map.float2ByteEntrySet();
        return entries instanceof Float2ByteMap.FastEntrySet ? ((Float2ByteMap.FastEntrySet)entries).fastIterator() : entries.iterator();
    }

    public static void fastForEach(Float2ByteMap map, Consumer<? super Float2ByteMap.Entry> consumer) {
        ObjectSet<Float2ByteMap.Entry> entries = map.float2ByteEntrySet();
        if (entries instanceof Float2ByteMap.FastEntrySet) {
            ((Float2ByteMap.FastEntrySet)entries).fastForEach(consumer);
        } else {
            entries.forEach(consumer);
        }
    }

    public static ObjectIterable<Float2ByteMap.Entry> fastIterable(Float2ByteMap map) {
        final ObjectSet<Float2ByteMap.Entry> entries = map.float2ByteEntrySet();
        return entries instanceof Float2ByteMap.FastEntrySet ? new ObjectIterable<Float2ByteMap.Entry>(){

            @Override
            public ObjectIterator<Float2ByteMap.Entry> iterator() {
                return ((Float2ByteMap.FastEntrySet)entries).fastIterator();
            }

            @Override
            public void forEach(Consumer<? super Float2ByteMap.Entry> consumer) {
                ((Float2ByteMap.FastEntrySet)entries).fastForEach(consumer);
            }
        } : entries;
    }

    public static Float2ByteMap singleton(float key, byte value) {
        return new Singleton(key, value);
    }

    public static Float2ByteMap singleton(Float key, Byte value) {
        return new Singleton(key.floatValue(), value);
    }

    public static Float2ByteMap synchronize(Float2ByteMap m) {
        return new SynchronizedMap(m);
    }

    public static Float2ByteMap synchronize(Float2ByteMap m, Object sync) {
        return new SynchronizedMap(m, sync);
    }

    public static Float2ByteMap unmodifiable(Float2ByteMap m) {
        return new UnmodifiableMap(m);
    }

    public static class UnmodifiableMap
    extends Float2ByteFunctions.UnmodifiableFunction
    implements Float2ByteMap,
    Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final Float2ByteMap map;
        protected transient ObjectSet<Float2ByteMap.Entry> entries;
        protected transient FloatSet keys;
        protected transient ByteCollection values;

        protected UnmodifiableMap(Float2ByteMap m) {
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
        public void putAll(Map<? extends Float, ? extends Byte> m) {
            throw new UnsupportedOperationException();
        }

        @Override
        public ObjectSet<Float2ByteMap.Entry> float2ByteEntrySet() {
            if (this.entries == null) {
                this.entries = ObjectSets.unmodifiable(this.map.float2ByteEntrySet());
            }
            return this.entries;
        }

        @Deprecated
        @Override
        public ObjectSet<Map.Entry<Float, Byte>> entrySet() {
            return this.float2ByteEntrySet();
        }

        @Override
        public FloatSet keySet() {
            if (this.keys == null) {
                this.keys = FloatSets.unmodifiable(this.map.keySet());
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
        public byte getOrDefault(float key, byte defaultValue) {
            return this.map.getOrDefault(key, defaultValue);
        }

        @Override
        public void forEach(BiConsumer<? super Float, ? super Byte> action) {
            this.map.forEach(action);
        }

        @Override
        public void replaceAll(BiFunction<? super Float, ? super Byte, ? extends Byte> function) {
            throw new UnsupportedOperationException();
        }

        @Override
        public byte putIfAbsent(float key, byte value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean remove(float key, byte value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public byte replace(float key, byte value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean replace(float key, byte oldValue, byte newValue) {
            throw new UnsupportedOperationException();
        }

        @Override
        public byte computeIfAbsent(float key, DoubleToIntFunction mappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Override
        public byte computeIfAbsentNullable(float key, DoubleFunction<? extends Byte> mappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Override
        public byte computeIfAbsentPartial(float key, Float2ByteFunction mappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Override
        public byte computeIfPresent(float key, BiFunction<? super Float, ? super Byte, ? extends Byte> remappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Override
        public byte compute(float key, BiFunction<? super Float, ? super Byte, ? extends Byte> remappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Override
        public byte merge(float key, byte value, BiFunction<? super Byte, ? super Byte, ? extends Byte> remappingFunction) {
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
        public Byte replace(Float key, Byte value) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public boolean replace(Float key, Byte oldValue, Byte newValue) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public Byte putIfAbsent(Float key, Byte value) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public Byte computeIfAbsent(Float key, Function<? super Float, ? extends Byte> mappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public Byte computeIfPresent(Float key, BiFunction<? super Float, ? super Byte, ? extends Byte> remappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public Byte compute(Float key, BiFunction<? super Float, ? super Byte, ? extends Byte> remappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public Byte merge(Float key, Byte value, BiFunction<? super Byte, ? super Byte, ? extends Byte> remappingFunction) {
            throw new UnsupportedOperationException();
        }
    }

    public static class SynchronizedMap
    extends Float2ByteFunctions.SynchronizedFunction
    implements Float2ByteMap,
    Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final Float2ByteMap map;
        protected transient ObjectSet<Float2ByteMap.Entry> entries;
        protected transient FloatSet keys;
        protected transient ByteCollection values;

        protected SynchronizedMap(Float2ByteMap m, Object sync) {
            super(m, sync);
            this.map = m;
        }

        protected SynchronizedMap(Float2ByteMap m) {
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
        public void putAll(Map<? extends Float, ? extends Byte> m) {
            Object object = this.sync;
            synchronized (object) {
                this.map.putAll(m);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public ObjectSet<Float2ByteMap.Entry> float2ByteEntrySet() {
            Object object = this.sync;
            synchronized (object) {
                if (this.entries == null) {
                    this.entries = ObjectSets.synchronize(this.map.float2ByteEntrySet(), this.sync);
                }
                return this.entries;
            }
        }

        @Deprecated
        @Override
        public ObjectSet<Map.Entry<Float, Byte>> entrySet() {
            return this.float2ByteEntrySet();
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public FloatSet keySet() {
            Object object = this.sync;
            synchronized (object) {
                if (this.keys == null) {
                    this.keys = FloatSets.synchronize(this.map.keySet(), this.sync);
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
        public byte getOrDefault(float key, byte defaultValue) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.getOrDefault(key, defaultValue);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void forEach(BiConsumer<? super Float, ? super Byte> action) {
            Object object = this.sync;
            synchronized (object) {
                this.map.forEach(action);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void replaceAll(BiFunction<? super Float, ? super Byte, ? extends Byte> function) {
            Object object = this.sync;
            synchronized (object) {
                this.map.replaceAll(function);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public byte putIfAbsent(float key, byte value) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.putIfAbsent(key, value);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean remove(float key, byte value) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.remove(key, value);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public byte replace(float key, byte value) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.replace(key, value);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean replace(float key, byte oldValue, byte newValue) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.replace(key, oldValue, newValue);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public byte computeIfAbsent(float key, DoubleToIntFunction mappingFunction) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.computeIfAbsent(key, mappingFunction);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public byte computeIfAbsentNullable(float key, DoubleFunction<? extends Byte> mappingFunction) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.computeIfAbsentNullable(key, mappingFunction);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public byte computeIfAbsentPartial(float key, Float2ByteFunction mappingFunction) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.computeIfAbsentPartial(key, mappingFunction);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public byte computeIfPresent(float key, BiFunction<? super Float, ? super Byte, ? extends Byte> remappingFunction) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.computeIfPresent(key, remappingFunction);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public byte compute(float key, BiFunction<? super Float, ? super Byte, ? extends Byte> remappingFunction) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.compute(key, remappingFunction);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public byte merge(float key, byte value, BiFunction<? super Byte, ? super Byte, ? extends Byte> remappingFunction) {
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
        public Byte replace(Float key, Byte value) {
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
        public boolean replace(Float key, Byte oldValue, Byte newValue) {
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
        public Byte putIfAbsent(Float key, Byte value) {
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
        public Byte computeIfAbsent(Float key, Function<? super Float, ? extends Byte> mappingFunction) {
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
        public Byte computeIfPresent(Float key, BiFunction<? super Float, ? super Byte, ? extends Byte> remappingFunction) {
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
        public Byte compute(Float key, BiFunction<? super Float, ? super Byte, ? extends Byte> remappingFunction) {
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
        public Byte merge(Float key, Byte value, BiFunction<? super Byte, ? super Byte, ? extends Byte> remappingFunction) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.merge(key, value, remappingFunction);
            }
        }
    }

    public static class Singleton
    extends Float2ByteFunctions.Singleton
    implements Float2ByteMap,
    Serializable,
    Cloneable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected transient ObjectSet<Float2ByteMap.Entry> entries;
        protected transient FloatSet keys;
        protected transient ByteCollection values;

        protected Singleton(float key, byte value) {
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
        public void putAll(Map<? extends Float, ? extends Byte> m) {
            throw new UnsupportedOperationException();
        }

        @Override
        public ObjectSet<Float2ByteMap.Entry> float2ByteEntrySet() {
            if (this.entries == null) {
                this.entries = ObjectSets.singleton(new AbstractFloat2ByteMap.BasicEntry(this.key, this.value));
            }
            return this.entries;
        }

        @Deprecated
        @Override
        public ObjectSet<Map.Entry<Float, Byte>> entrySet() {
            return this.float2ByteEntrySet();
        }

        @Override
        public FloatSet keySet() {
            if (this.keys == null) {
                this.keys = FloatSets.singleton(this.key);
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
            return HashCommon.float2int(this.key) ^ this.value;
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
    extends Float2ByteFunctions.EmptyFunction
    implements Float2ByteMap,
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
        public void putAll(Map<? extends Float, ? extends Byte> m) {
            throw new UnsupportedOperationException();
        }

        @Override
        public ObjectSet<Float2ByteMap.Entry> float2ByteEntrySet() {
            return ObjectSets.EMPTY_SET;
        }

        @Override
        public FloatSet keySet() {
            return FloatSets.EMPTY_SET;
        }

        @Override
        public ByteCollection values() {
            return ByteSets.EMPTY_SET;
        }

        @Override
        public Object clone() {
            return Float2ByteMaps.EMPTY_MAP;
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

