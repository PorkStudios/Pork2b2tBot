/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.longs.AbstractLong2ShortMap;
import it.unimi.dsi.fastutil.longs.Long2ShortFunction;
import it.unimi.dsi.fastutil.longs.Long2ShortFunctions;
import it.unimi.dsi.fastutil.longs.Long2ShortMap;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.longs.LongSets;
import it.unimi.dsi.fastutil.objects.ObjectIterable;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import it.unimi.dsi.fastutil.objects.ObjectSets;
import it.unimi.dsi.fastutil.shorts.ShortCollection;
import it.unimi.dsi.fastutil.shorts.ShortCollections;
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
import java.util.function.LongFunction;
import java.util.function.LongToIntFunction;

public final class Long2ShortMaps {
    public static final EmptyMap EMPTY_MAP = new EmptyMap();

    private Long2ShortMaps() {
    }

    public static ObjectIterator<Long2ShortMap.Entry> fastIterator(Long2ShortMap map) {
        ObjectSet<Long2ShortMap.Entry> entries = map.long2ShortEntrySet();
        return entries instanceof Long2ShortMap.FastEntrySet ? ((Long2ShortMap.FastEntrySet)entries).fastIterator() : entries.iterator();
    }

    public static void fastForEach(Long2ShortMap map, Consumer<? super Long2ShortMap.Entry> consumer) {
        ObjectSet<Long2ShortMap.Entry> entries = map.long2ShortEntrySet();
        if (entries instanceof Long2ShortMap.FastEntrySet) {
            ((Long2ShortMap.FastEntrySet)entries).fastForEach(consumer);
        } else {
            entries.forEach(consumer);
        }
    }

    public static ObjectIterable<Long2ShortMap.Entry> fastIterable(Long2ShortMap map) {
        final ObjectSet<Long2ShortMap.Entry> entries = map.long2ShortEntrySet();
        return entries instanceof Long2ShortMap.FastEntrySet ? new ObjectIterable<Long2ShortMap.Entry>(){

            @Override
            public ObjectIterator<Long2ShortMap.Entry> iterator() {
                return ((Long2ShortMap.FastEntrySet)entries).fastIterator();
            }

            @Override
            public void forEach(Consumer<? super Long2ShortMap.Entry> consumer) {
                ((Long2ShortMap.FastEntrySet)entries).fastForEach(consumer);
            }
        } : entries;
    }

    public static Long2ShortMap singleton(long key, short value) {
        return new Singleton(key, value);
    }

    public static Long2ShortMap singleton(Long key, Short value) {
        return new Singleton(key, value);
    }

    public static Long2ShortMap synchronize(Long2ShortMap m) {
        return new SynchronizedMap(m);
    }

    public static Long2ShortMap synchronize(Long2ShortMap m, Object sync) {
        return new SynchronizedMap(m, sync);
    }

    public static Long2ShortMap unmodifiable(Long2ShortMap m) {
        return new UnmodifiableMap(m);
    }

    public static class UnmodifiableMap
    extends Long2ShortFunctions.UnmodifiableFunction
    implements Long2ShortMap,
    Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final Long2ShortMap map;
        protected transient ObjectSet<Long2ShortMap.Entry> entries;
        protected transient LongSet keys;
        protected transient ShortCollection values;

        protected UnmodifiableMap(Long2ShortMap m) {
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
        public void putAll(Map<? extends Long, ? extends Short> m) {
            throw new UnsupportedOperationException();
        }

        @Override
        public ObjectSet<Long2ShortMap.Entry> long2ShortEntrySet() {
            if (this.entries == null) {
                this.entries = ObjectSets.unmodifiable(this.map.long2ShortEntrySet());
            }
            return this.entries;
        }

        @Deprecated
        @Override
        public ObjectSet<Map.Entry<Long, Short>> entrySet() {
            return this.long2ShortEntrySet();
        }

        @Override
        public LongSet keySet() {
            if (this.keys == null) {
                this.keys = LongSets.unmodifiable(this.map.keySet());
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
        public short getOrDefault(long key, short defaultValue) {
            return this.map.getOrDefault(key, defaultValue);
        }

        @Override
        public void forEach(BiConsumer<? super Long, ? super Short> action) {
            this.map.forEach(action);
        }

        @Override
        public void replaceAll(BiFunction<? super Long, ? super Short, ? extends Short> function) {
            throw new UnsupportedOperationException();
        }

        @Override
        public short putIfAbsent(long key, short value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean remove(long key, short value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public short replace(long key, short value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean replace(long key, short oldValue, short newValue) {
            throw new UnsupportedOperationException();
        }

        @Override
        public short computeIfAbsent(long key, LongToIntFunction mappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Override
        public short computeIfAbsentNullable(long key, LongFunction<? extends Short> mappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Override
        public short computeIfAbsentPartial(long key, Long2ShortFunction mappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Override
        public short computeIfPresent(long key, BiFunction<? super Long, ? super Short, ? extends Short> remappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Override
        public short compute(long key, BiFunction<? super Long, ? super Short, ? extends Short> remappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Override
        public short merge(long key, short value, BiFunction<? super Short, ? super Short, ? extends Short> remappingFunction) {
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
        public Short replace(Long key, Short value) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public boolean replace(Long key, Short oldValue, Short newValue) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public Short putIfAbsent(Long key, Short value) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public Short computeIfAbsent(Long key, Function<? super Long, ? extends Short> mappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public Short computeIfPresent(Long key, BiFunction<? super Long, ? super Short, ? extends Short> remappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public Short compute(Long key, BiFunction<? super Long, ? super Short, ? extends Short> remappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public Short merge(Long key, Short value, BiFunction<? super Short, ? super Short, ? extends Short> remappingFunction) {
            throw new UnsupportedOperationException();
        }
    }

    public static class SynchronizedMap
    extends Long2ShortFunctions.SynchronizedFunction
    implements Long2ShortMap,
    Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final Long2ShortMap map;
        protected transient ObjectSet<Long2ShortMap.Entry> entries;
        protected transient LongSet keys;
        protected transient ShortCollection values;

        protected SynchronizedMap(Long2ShortMap m, Object sync) {
            super(m, sync);
            this.map = m;
        }

        protected SynchronizedMap(Long2ShortMap m) {
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
        public void putAll(Map<? extends Long, ? extends Short> m) {
            Object object = this.sync;
            synchronized (object) {
                this.map.putAll(m);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public ObjectSet<Long2ShortMap.Entry> long2ShortEntrySet() {
            Object object = this.sync;
            synchronized (object) {
                if (this.entries == null) {
                    this.entries = ObjectSets.synchronize(this.map.long2ShortEntrySet(), this.sync);
                }
                return this.entries;
            }
        }

        @Deprecated
        @Override
        public ObjectSet<Map.Entry<Long, Short>> entrySet() {
            return this.long2ShortEntrySet();
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public LongSet keySet() {
            Object object = this.sync;
            synchronized (object) {
                if (this.keys == null) {
                    this.keys = LongSets.synchronize(this.map.keySet(), this.sync);
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
        public short getOrDefault(long key, short defaultValue) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.getOrDefault(key, defaultValue);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void forEach(BiConsumer<? super Long, ? super Short> action) {
            Object object = this.sync;
            synchronized (object) {
                this.map.forEach(action);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void replaceAll(BiFunction<? super Long, ? super Short, ? extends Short> function) {
            Object object = this.sync;
            synchronized (object) {
                this.map.replaceAll(function);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public short putIfAbsent(long key, short value) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.putIfAbsent(key, value);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean remove(long key, short value) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.remove(key, value);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public short replace(long key, short value) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.replace(key, value);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean replace(long key, short oldValue, short newValue) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.replace(key, oldValue, newValue);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public short computeIfAbsent(long key, LongToIntFunction mappingFunction) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.computeIfAbsent(key, mappingFunction);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public short computeIfAbsentNullable(long key, LongFunction<? extends Short> mappingFunction) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.computeIfAbsentNullable(key, mappingFunction);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public short computeIfAbsentPartial(long key, Long2ShortFunction mappingFunction) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.computeIfAbsentPartial(key, mappingFunction);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public short computeIfPresent(long key, BiFunction<? super Long, ? super Short, ? extends Short> remappingFunction) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.computeIfPresent(key, remappingFunction);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public short compute(long key, BiFunction<? super Long, ? super Short, ? extends Short> remappingFunction) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.compute(key, remappingFunction);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public short merge(long key, short value, BiFunction<? super Short, ? super Short, ? extends Short> remappingFunction) {
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
        public Short replace(Long key, Short value) {
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
        public boolean replace(Long key, Short oldValue, Short newValue) {
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
        public Short putIfAbsent(Long key, Short value) {
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
        public Short computeIfAbsent(Long key, Function<? super Long, ? extends Short> mappingFunction) {
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
        public Short computeIfPresent(Long key, BiFunction<? super Long, ? super Short, ? extends Short> remappingFunction) {
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
        public Short compute(Long key, BiFunction<? super Long, ? super Short, ? extends Short> remappingFunction) {
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
        public Short merge(Long key, Short value, BiFunction<? super Short, ? super Short, ? extends Short> remappingFunction) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.merge(key, value, remappingFunction);
            }
        }
    }

    public static class Singleton
    extends Long2ShortFunctions.Singleton
    implements Long2ShortMap,
    Serializable,
    Cloneable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected transient ObjectSet<Long2ShortMap.Entry> entries;
        protected transient LongSet keys;
        protected transient ShortCollection values;

        protected Singleton(long key, short value) {
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
        public void putAll(Map<? extends Long, ? extends Short> m) {
            throw new UnsupportedOperationException();
        }

        @Override
        public ObjectSet<Long2ShortMap.Entry> long2ShortEntrySet() {
            if (this.entries == null) {
                this.entries = ObjectSets.singleton(new AbstractLong2ShortMap.BasicEntry(this.key, this.value));
            }
            return this.entries;
        }

        @Deprecated
        @Override
        public ObjectSet<Map.Entry<Long, Short>> entrySet() {
            return this.long2ShortEntrySet();
        }

        @Override
        public LongSet keySet() {
            if (this.keys == null) {
                this.keys = LongSets.singleton(this.key);
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
            return HashCommon.long2int(this.key) ^ this.value;
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
    extends Long2ShortFunctions.EmptyFunction
    implements Long2ShortMap,
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
        public void putAll(Map<? extends Long, ? extends Short> m) {
            throw new UnsupportedOperationException();
        }

        @Override
        public ObjectSet<Long2ShortMap.Entry> long2ShortEntrySet() {
            return ObjectSets.EMPTY_SET;
        }

        @Override
        public LongSet keySet() {
            return LongSets.EMPTY_SET;
        }

        @Override
        public ShortCollection values() {
            return ShortSets.EMPTY_SET;
        }

        @Override
        public Object clone() {
            return Long2ShortMaps.EMPTY_MAP;
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

