/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.shorts;

import it.unimi.dsi.fastutil.objects.ObjectIterable;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import it.unimi.dsi.fastutil.objects.ObjectSets;
import it.unimi.dsi.fastutil.shorts.AbstractShort2ShortMap;
import it.unimi.dsi.fastutil.shorts.Short2ShortFunction;
import it.unimi.dsi.fastutil.shorts.Short2ShortFunctions;
import it.unimi.dsi.fastutil.shorts.Short2ShortMap;
import it.unimi.dsi.fastutil.shorts.ShortCollection;
import it.unimi.dsi.fastutil.shorts.ShortCollections;
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

public final class Short2ShortMaps {
    public static final EmptyMap EMPTY_MAP = new EmptyMap();

    private Short2ShortMaps() {
    }

    public static ObjectIterator<Short2ShortMap.Entry> fastIterator(Short2ShortMap map) {
        ObjectSet<Short2ShortMap.Entry> entries = map.short2ShortEntrySet();
        return entries instanceof Short2ShortMap.FastEntrySet ? ((Short2ShortMap.FastEntrySet)entries).fastIterator() : entries.iterator();
    }

    public static void fastForEach(Short2ShortMap map, Consumer<? super Short2ShortMap.Entry> consumer) {
        ObjectSet<Short2ShortMap.Entry> entries = map.short2ShortEntrySet();
        if (entries instanceof Short2ShortMap.FastEntrySet) {
            ((Short2ShortMap.FastEntrySet)entries).fastForEach(consumer);
        } else {
            entries.forEach(consumer);
        }
    }

    public static ObjectIterable<Short2ShortMap.Entry> fastIterable(Short2ShortMap map) {
        final ObjectSet<Short2ShortMap.Entry> entries = map.short2ShortEntrySet();
        return entries instanceof Short2ShortMap.FastEntrySet ? new ObjectIterable<Short2ShortMap.Entry>(){

            @Override
            public ObjectIterator<Short2ShortMap.Entry> iterator() {
                return ((Short2ShortMap.FastEntrySet)entries).fastIterator();
            }

            @Override
            public void forEach(Consumer<? super Short2ShortMap.Entry> consumer) {
                ((Short2ShortMap.FastEntrySet)entries).fastForEach(consumer);
            }
        } : entries;
    }

    public static Short2ShortMap singleton(short key, short value) {
        return new Singleton(key, value);
    }

    public static Short2ShortMap singleton(Short key, Short value) {
        return new Singleton(key, value);
    }

    public static Short2ShortMap synchronize(Short2ShortMap m) {
        return new SynchronizedMap(m);
    }

    public static Short2ShortMap synchronize(Short2ShortMap m, Object sync) {
        return new SynchronizedMap(m, sync);
    }

    public static Short2ShortMap unmodifiable(Short2ShortMap m) {
        return new UnmodifiableMap(m);
    }

    public static class UnmodifiableMap
    extends Short2ShortFunctions.UnmodifiableFunction
    implements Short2ShortMap,
    Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final Short2ShortMap map;
        protected transient ObjectSet<Short2ShortMap.Entry> entries;
        protected transient ShortSet keys;
        protected transient ShortCollection values;

        protected UnmodifiableMap(Short2ShortMap m) {
            super(m);
            this.map = m;
        }

        @Override
        public boolean containsValue(short v) {
            return this.map.containsValue(v);
        }

        @Deprecated
        @Override
        public boolean containsValue(Object ov) {
            return this.map.containsValue(ov);
        }

        @Override
        public void putAll(Map<? extends Short, ? extends Short> m) {
            throw new UnsupportedOperationException();
        }

        @Override
        public ObjectSet<Short2ShortMap.Entry> short2ShortEntrySet() {
            if (this.entries == null) {
                this.entries = ObjectSets.unmodifiable(this.map.short2ShortEntrySet());
            }
            return this.entries;
        }

        @Deprecated
        @Override
        public ObjectSet<Map.Entry<Short, Short>> entrySet() {
            return this.short2ShortEntrySet();
        }

        @Override
        public ShortSet keySet() {
            if (this.keys == null) {
                this.keys = ShortSets.unmodifiable(this.map.keySet());
            }
            return this.keys;
        }

        @Override
        public ShortCollection values() {
            if (this.values == null) {
                return ShortCollections.unmodifiable(this.map.values());
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
        public short getOrDefault(short key, short defaultValue) {
            return this.map.getOrDefault(key, defaultValue);
        }

        @Override
        public void forEach(BiConsumer<? super Short, ? super Short> action) {
            this.map.forEach(action);
        }

        @Override
        public void replaceAll(BiFunction<? super Short, ? super Short, ? extends Short> function) {
            throw new UnsupportedOperationException();
        }

        @Override
        public short putIfAbsent(short key, short value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean remove(short key, short value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public short replace(short key, short value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean replace(short key, short oldValue, short newValue) {
            throw new UnsupportedOperationException();
        }

        @Override
        public short computeIfAbsent(short key, IntUnaryOperator mappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Override
        public short computeIfAbsentNullable(short key, IntFunction<? extends Short> mappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Override
        public short computeIfAbsentPartial(short key, Short2ShortFunction mappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Override
        public short computeIfPresent(short key, BiFunction<? super Short, ? super Short, ? extends Short> remappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Override
        public short compute(short key, BiFunction<? super Short, ? super Short, ? extends Short> remappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Override
        public short merge(short key, short value, BiFunction<? super Short, ? super Short, ? extends Short> remappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public Short getOrDefault(Object key, Short defaultValue) {
            return this.map.getOrDefault(key, defaultValue);
        }

        @Deprecated
        @Override
        public boolean remove(Object key, Object value) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public Short replace(Short key, Short value) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public boolean replace(Short key, Short oldValue, Short newValue) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public Short putIfAbsent(Short key, Short value) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public Short computeIfAbsent(Short key, Function<? super Short, ? extends Short> mappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public Short computeIfPresent(Short key, BiFunction<? super Short, ? super Short, ? extends Short> remappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public Short compute(Short key, BiFunction<? super Short, ? super Short, ? extends Short> remappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public Short merge(Short key, Short value, BiFunction<? super Short, ? super Short, ? extends Short> remappingFunction) {
            throw new UnsupportedOperationException();
        }
    }

    public static class SynchronizedMap
    extends Short2ShortFunctions.SynchronizedFunction
    implements Short2ShortMap,
    Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final Short2ShortMap map;
        protected transient ObjectSet<Short2ShortMap.Entry> entries;
        protected transient ShortSet keys;
        protected transient ShortCollection values;

        protected SynchronizedMap(Short2ShortMap m, Object sync) {
            super(m, sync);
            this.map = m;
        }

        protected SynchronizedMap(Short2ShortMap m) {
            super(m);
            this.map = m;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean containsValue(short v) {
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
        public void putAll(Map<? extends Short, ? extends Short> m) {
            Object object = this.sync;
            synchronized (object) {
                this.map.putAll(m);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public ObjectSet<Short2ShortMap.Entry> short2ShortEntrySet() {
            Object object = this.sync;
            synchronized (object) {
                if (this.entries == null) {
                    this.entries = ObjectSets.synchronize(this.map.short2ShortEntrySet(), this.sync);
                }
                return this.entries;
            }
        }

        @Deprecated
        @Override
        public ObjectSet<Map.Entry<Short, Short>> entrySet() {
            return this.short2ShortEntrySet();
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
        public ShortCollection values() {
            Object object = this.sync;
            synchronized (object) {
                if (this.values == null) {
                    return ShortCollections.synchronize(this.map.values(), this.sync);
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
        public short getOrDefault(short key, short defaultValue) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.getOrDefault(key, defaultValue);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void forEach(BiConsumer<? super Short, ? super Short> action) {
            Object object = this.sync;
            synchronized (object) {
                this.map.forEach(action);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void replaceAll(BiFunction<? super Short, ? super Short, ? extends Short> function) {
            Object object = this.sync;
            synchronized (object) {
                this.map.replaceAll(function);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public short putIfAbsent(short key, short value) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.putIfAbsent(key, value);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean remove(short key, short value) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.remove(key, value);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public short replace(short key, short value) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.replace(key, value);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean replace(short key, short oldValue, short newValue) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.replace(key, oldValue, newValue);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public short computeIfAbsent(short key, IntUnaryOperator mappingFunction) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.computeIfAbsent(key, mappingFunction);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public short computeIfAbsentNullable(short key, IntFunction<? extends Short> mappingFunction) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.computeIfAbsentNullable(key, mappingFunction);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public short computeIfAbsentPartial(short key, Short2ShortFunction mappingFunction) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.computeIfAbsentPartial(key, mappingFunction);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public short computeIfPresent(short key, BiFunction<? super Short, ? super Short, ? extends Short> remappingFunction) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.computeIfPresent(key, remappingFunction);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public short compute(short key, BiFunction<? super Short, ? super Short, ? extends Short> remappingFunction) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.compute(key, remappingFunction);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public short merge(short key, short value, BiFunction<? super Short, ? super Short, ? extends Short> remappingFunction) {
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
        public Short getOrDefault(Object key, Short defaultValue) {
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
        public Short replace(Short key, Short value) {
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
        public boolean replace(Short key, Short oldValue, Short newValue) {
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
        public Short putIfAbsent(Short key, Short value) {
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
        public Short computeIfAbsent(Short key, Function<? super Short, ? extends Short> mappingFunction) {
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
        public Short computeIfPresent(Short key, BiFunction<? super Short, ? super Short, ? extends Short> remappingFunction) {
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
        public Short compute(Short key, BiFunction<? super Short, ? super Short, ? extends Short> remappingFunction) {
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
        public Short merge(Short key, Short value, BiFunction<? super Short, ? super Short, ? extends Short> remappingFunction) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.merge(key, value, remappingFunction);
            }
        }
    }

    public static class Singleton
    extends Short2ShortFunctions.Singleton
    implements Short2ShortMap,
    Serializable,
    Cloneable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected transient ObjectSet<Short2ShortMap.Entry> entries;
        protected transient ShortSet keys;
        protected transient ShortCollection values;

        protected Singleton(short key, short value) {
            super(key, value);
        }

        @Override
        public boolean containsValue(short v) {
            return this.value == v;
        }

        @Deprecated
        @Override
        public boolean containsValue(Object ov) {
            return (Short)ov == this.value;
        }

        @Override
        public void putAll(Map<? extends Short, ? extends Short> m) {
            throw new UnsupportedOperationException();
        }

        @Override
        public ObjectSet<Short2ShortMap.Entry> short2ShortEntrySet() {
            if (this.entries == null) {
                this.entries = ObjectSets.singleton(new AbstractShort2ShortMap.BasicEntry(this.key, this.value));
            }
            return this.entries;
        }

        @Deprecated
        @Override
        public ObjectSet<Map.Entry<Short, Short>> entrySet() {
            return this.short2ShortEntrySet();
        }

        @Override
        public ShortSet keySet() {
            if (this.keys == null) {
                this.keys = ShortSets.singleton(this.key);
            }
            return this.keys;
        }

        @Override
        public ShortCollection values() {
            if (this.values == null) {
                this.values = ShortSets.singleton(this.value);
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
    extends Short2ShortFunctions.EmptyFunction
    implements Short2ShortMap,
    Serializable,
    Cloneable {
        private static final long serialVersionUID = -7046029254386353129L;

        protected EmptyMap() {
        }

        @Override
        public boolean containsValue(short v) {
            return false;
        }

        @Deprecated
        @Override
        public boolean containsValue(Object ov) {
            return false;
        }

        @Override
        public void putAll(Map<? extends Short, ? extends Short> m) {
            throw new UnsupportedOperationException();
        }

        @Override
        public ObjectSet<Short2ShortMap.Entry> short2ShortEntrySet() {
            return ObjectSets.EMPTY_SET;
        }

        @Override
        public ShortSet keySet() {
            return ShortSets.EMPTY_SET;
        }

        @Override
        public ShortCollection values() {
            return ShortSets.EMPTY_SET;
        }

        @Override
        public Object clone() {
            return Short2ShortMaps.EMPTY_MAP;
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

