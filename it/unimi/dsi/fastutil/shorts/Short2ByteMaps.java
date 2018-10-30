/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.shorts;

import it.unimi.dsi.fastutil.bytes.ByteCollection;
import it.unimi.dsi.fastutil.bytes.ByteCollections;
import it.unimi.dsi.fastutil.bytes.ByteSets;
import it.unimi.dsi.fastutil.objects.ObjectIterable;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import it.unimi.dsi.fastutil.objects.ObjectSets;
import it.unimi.dsi.fastutil.shorts.AbstractShort2ByteMap;
import it.unimi.dsi.fastutil.shorts.Short2ByteFunction;
import it.unimi.dsi.fastutil.shorts.Short2ByteFunctions;
import it.unimi.dsi.fastutil.shorts.Short2ByteMap;
import it.unimi.dsi.fastutil.shorts.ShortSet;
import it.unimi.dsi.fastutil.shorts.ShortSets;
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
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.IntUnaryOperator;

public final class Short2ByteMaps {
    public static final EmptyMap EMPTY_MAP = new EmptyMap();

    private Short2ByteMaps() {
    }

    public static ObjectIterator<Short2ByteMap.Entry> fastIterator(Short2ByteMap map) {
        ObjectSet<Short2ByteMap.Entry> entries = map.short2ByteEntrySet();
        return entries instanceof Short2ByteMap.FastEntrySet ? ((Short2ByteMap.FastEntrySet)entries).fastIterator() : entries.iterator();
    }

    public static void fastForEach(Short2ByteMap map, Consumer<? super Short2ByteMap.Entry> consumer) {
        ObjectSet<Short2ByteMap.Entry> entries = map.short2ByteEntrySet();
        if (entries instanceof Short2ByteMap.FastEntrySet) {
            ((Short2ByteMap.FastEntrySet)entries).fastForEach(consumer);
        } else {
            entries.forEach(consumer);
        }
    }

    public static ObjectIterable<Short2ByteMap.Entry> fastIterable(Short2ByteMap map) {
        final ObjectSet<Short2ByteMap.Entry> entries = map.short2ByteEntrySet();
        return entries instanceof Short2ByteMap.FastEntrySet ? new ObjectIterable<Short2ByteMap.Entry>(){

            @Override
            public ObjectIterator<Short2ByteMap.Entry> iterator() {
                return ((Short2ByteMap.FastEntrySet)entries).fastIterator();
            }

            @Override
            public void forEach(Consumer<? super Short2ByteMap.Entry> consumer) {
                ((Short2ByteMap.FastEntrySet)entries).fastForEach(consumer);
            }
        } : entries;
    }

    public static Short2ByteMap singleton(short key, byte value) {
        return new Singleton(key, value);
    }

    public static Short2ByteMap singleton(Short key, Byte value) {
        return new Singleton(key, value);
    }

    public static Short2ByteMap synchronize(Short2ByteMap m) {
        return new SynchronizedMap(m);
    }

    public static Short2ByteMap synchronize(Short2ByteMap m, Object sync) {
        return new SynchronizedMap(m, sync);
    }

    public static Short2ByteMap unmodifiable(Short2ByteMap m) {
        return new UnmodifiableMap(m);
    }

    public static class UnmodifiableMap
    extends Short2ByteFunctions.UnmodifiableFunction
    implements Short2ByteMap,
    Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final Short2ByteMap map;
        protected transient ObjectSet<Short2ByteMap.Entry> entries;
        protected transient ShortSet keys;
        protected transient ByteCollection values;

        protected UnmodifiableMap(Short2ByteMap m) {
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
        public void putAll(Map<? extends Short, ? extends Byte> m) {
            throw new UnsupportedOperationException();
        }

        @Override
        public ObjectSet<Short2ByteMap.Entry> short2ByteEntrySet() {
            if (this.entries == null) {
                this.entries = ObjectSets.unmodifiable(this.map.short2ByteEntrySet());
            }
            return this.entries;
        }

        @Deprecated
        @Override
        public ObjectSet<Map.Entry<Short, Byte>> entrySet() {
            return this.short2ByteEntrySet();
        }

        @Override
        public ShortSet keySet() {
            if (this.keys == null) {
                this.keys = ShortSets.unmodifiable(this.map.keySet());
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
        public byte getOrDefault(short key, byte defaultValue) {
            return this.map.getOrDefault(key, defaultValue);
        }

        @Override
        public void forEach(BiConsumer<? super Short, ? super Byte> action) {
            this.map.forEach(action);
        }

        @Override
        public void replaceAll(BiFunction<? super Short, ? super Byte, ? extends Byte> function) {
            throw new UnsupportedOperationException();
        }

        @Override
        public byte putIfAbsent(short key, byte value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean remove(short key, byte value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public byte replace(short key, byte value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean replace(short key, byte oldValue, byte newValue) {
            throw new UnsupportedOperationException();
        }

        @Override
        public byte computeIfAbsent(short key, IntUnaryOperator mappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Override
        public byte computeIfAbsentNullable(short key, IntFunction<? extends Byte> mappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Override
        public byte computeIfAbsentPartial(short key, Short2ByteFunction mappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Override
        public byte computeIfPresent(short key, BiFunction<? super Short, ? super Byte, ? extends Byte> remappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Override
        public byte compute(short key, BiFunction<? super Short, ? super Byte, ? extends Byte> remappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Override
        public byte merge(short key, byte value, BiFunction<? super Byte, ? super Byte, ? extends Byte> remappingFunction) {
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
        public Byte replace(Short key, Byte value) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public boolean replace(Short key, Byte oldValue, Byte newValue) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public Byte putIfAbsent(Short key, Byte value) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public Byte computeIfAbsent(Short key, Function<? super Short, ? extends Byte> mappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public Byte computeIfPresent(Short key, BiFunction<? super Short, ? super Byte, ? extends Byte> remappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public Byte compute(Short key, BiFunction<? super Short, ? super Byte, ? extends Byte> remappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public Byte merge(Short key, Byte value, BiFunction<? super Byte, ? super Byte, ? extends Byte> remappingFunction) {
            throw new UnsupportedOperationException();
        }
    }

    public static class SynchronizedMap
    extends Short2ByteFunctions.SynchronizedFunction
    implements Short2ByteMap,
    Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final Short2ByteMap map;
        protected transient ObjectSet<Short2ByteMap.Entry> entries;
        protected transient ShortSet keys;
        protected transient ByteCollection values;

        protected SynchronizedMap(Short2ByteMap m, Object sync) {
            super(m, sync);
            this.map = m;
        }

        protected SynchronizedMap(Short2ByteMap m) {
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
        public void putAll(Map<? extends Short, ? extends Byte> m) {
            Object object = this.sync;
            synchronized (object) {
                this.map.putAll(m);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public ObjectSet<Short2ByteMap.Entry> short2ByteEntrySet() {
            Object object = this.sync;
            synchronized (object) {
                if (this.entries == null) {
                    this.entries = ObjectSets.synchronize(this.map.short2ByteEntrySet(), this.sync);
                }
                return this.entries;
            }
        }

        @Deprecated
        @Override
        public ObjectSet<Map.Entry<Short, Byte>> entrySet() {
            return this.short2ByteEntrySet();
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public ShortSet keySet() {
            Object object = this.sync;
            synchronized (object) {
                if (this.keys == null) {
                    this.keys = ShortSets.synchronize(this.map.keySet(), this.sync);
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
        public byte getOrDefault(short key, byte defaultValue) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.getOrDefault(key, defaultValue);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void forEach(BiConsumer<? super Short, ? super Byte> action) {
            Object object = this.sync;
            synchronized (object) {
                this.map.forEach(action);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void replaceAll(BiFunction<? super Short, ? super Byte, ? extends Byte> function) {
            Object object = this.sync;
            synchronized (object) {
                this.map.replaceAll(function);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public byte putIfAbsent(short key, byte value) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.putIfAbsent(key, value);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean remove(short key, byte value) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.remove(key, value);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public byte replace(short key, byte value) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.replace(key, value);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean replace(short key, byte oldValue, byte newValue) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.replace(key, oldValue, newValue);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public byte computeIfAbsent(short key, IntUnaryOperator mappingFunction) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.computeIfAbsent(key, mappingFunction);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public byte computeIfAbsentNullable(short key, IntFunction<? extends Byte> mappingFunction) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.computeIfAbsentNullable(key, mappingFunction);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public byte computeIfAbsentPartial(short key, Short2ByteFunction mappingFunction) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.computeIfAbsentPartial(key, mappingFunction);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public byte computeIfPresent(short key, BiFunction<? super Short, ? super Byte, ? extends Byte> remappingFunction) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.computeIfPresent(key, remappingFunction);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public byte compute(short key, BiFunction<? super Short, ? super Byte, ? extends Byte> remappingFunction) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.compute(key, remappingFunction);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public byte merge(short key, byte value, BiFunction<? super Byte, ? super Byte, ? extends Byte> remappingFunction) {
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
        public Byte replace(Short key, Byte value) {
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
        public boolean replace(Short key, Byte oldValue, Byte newValue) {
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
        public Byte putIfAbsent(Short key, Byte value) {
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
        public Byte computeIfAbsent(Short key, Function<? super Short, ? extends Byte> mappingFunction) {
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
        public Byte computeIfPresent(Short key, BiFunction<? super Short, ? super Byte, ? extends Byte> remappingFunction) {
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
        public Byte compute(Short key, BiFunction<? super Short, ? super Byte, ? extends Byte> remappingFunction) {
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
        public Byte merge(Short key, Byte value, BiFunction<? super Byte, ? super Byte, ? extends Byte> remappingFunction) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.merge(key, value, remappingFunction);
            }
        }
    }

    public static class Singleton
    extends Short2ByteFunctions.Singleton
    implements Short2ByteMap,
    Serializable,
    Cloneable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected transient ObjectSet<Short2ByteMap.Entry> entries;
        protected transient ShortSet keys;
        protected transient ByteCollection values;

        protected Singleton(short key, byte value) {
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
        public void putAll(Map<? extends Short, ? extends Byte> m) {
            throw new UnsupportedOperationException();
        }

        @Override
        public ObjectSet<Short2ByteMap.Entry> short2ByteEntrySet() {
            if (this.entries == null) {
                this.entries = ObjectSets.singleton(new AbstractShort2ByteMap.BasicEntry(this.key, this.value));
            }
            return this.entries;
        }

        @Deprecated
        @Override
        public ObjectSet<Map.Entry<Short, Byte>> entrySet() {
            return this.short2ByteEntrySet();
        }

        @Override
        public ShortSet keySet() {
            if (this.keys == null) {
                this.keys = ShortSets.singleton(this.key);
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
            return this.key ^ this.value;
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
    extends Short2ByteFunctions.EmptyFunction
    implements Short2ByteMap,
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
        public void putAll(Map<? extends Short, ? extends Byte> m) {
            throw new UnsupportedOperationException();
        }

        @Override
        public ObjectSet<Short2ByteMap.Entry> short2ByteEntrySet() {
            return ObjectSets.EMPTY_SET;
        }

        @Override
        public ShortSet keySet() {
            return ShortSets.EMPTY_SET;
        }

        @Override
        public ByteCollection values() {
            return ByteSets.EMPTY_SET;
        }

        @Override
        public Object clone() {
            return Short2ByteMaps.EMPTY_MAP;
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

