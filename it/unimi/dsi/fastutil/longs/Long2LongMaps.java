/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.longs.AbstractLong2LongMap;
import it.unimi.dsi.fastutil.longs.Long2LongFunction;
import it.unimi.dsi.fastutil.longs.Long2LongFunctions;
import it.unimi.dsi.fastutil.longs.Long2LongMap;
import it.unimi.dsi.fastutil.longs.LongCollection;
import it.unimi.dsi.fastutil.longs.LongCollections;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.longs.LongSets;
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
import java.util.function.Function;
import java.util.function.LongFunction;
import java.util.function.LongUnaryOperator;

public final class Long2LongMaps {
    public static final EmptyMap EMPTY_MAP = new EmptyMap();

    private Long2LongMaps() {
    }

    public static ObjectIterator<Long2LongMap.Entry> fastIterator(Long2LongMap map) {
        ObjectSet<Long2LongMap.Entry> entries = map.long2LongEntrySet();
        return entries instanceof Long2LongMap.FastEntrySet ? ((Long2LongMap.FastEntrySet)entries).fastIterator() : entries.iterator();
    }

    public static void fastForEach(Long2LongMap map, Consumer<? super Long2LongMap.Entry> consumer) {
        ObjectSet<Long2LongMap.Entry> entries = map.long2LongEntrySet();
        if (entries instanceof Long2LongMap.FastEntrySet) {
            ((Long2LongMap.FastEntrySet)entries).fastForEach(consumer);
        } else {
            entries.forEach(consumer);
        }
    }

    public static ObjectIterable<Long2LongMap.Entry> fastIterable(Long2LongMap map) {
        final ObjectSet<Long2LongMap.Entry> entries = map.long2LongEntrySet();
        return entries instanceof Long2LongMap.FastEntrySet ? new ObjectIterable<Long2LongMap.Entry>(){

            @Override
            public ObjectIterator<Long2LongMap.Entry> iterator() {
                return ((Long2LongMap.FastEntrySet)entries).fastIterator();
            }

            @Override
            public void forEach(Consumer<? super Long2LongMap.Entry> consumer) {
                ((Long2LongMap.FastEntrySet)entries).fastForEach(consumer);
            }
        } : entries;
    }

    public static Long2LongMap singleton(long key, long value) {
        return new Singleton(key, value);
    }

    public static Long2LongMap singleton(Long key, Long value) {
        return new Singleton(key, value);
    }

    public static Long2LongMap synchronize(Long2LongMap m) {
        return new SynchronizedMap(m);
    }

    public static Long2LongMap synchronize(Long2LongMap m, Object sync) {
        return new SynchronizedMap(m, sync);
    }

    public static Long2LongMap unmodifiable(Long2LongMap m) {
        return new UnmodifiableMap(m);
    }

    public static class UnmodifiableMap
    extends Long2LongFunctions.UnmodifiableFunction
    implements Long2LongMap,
    Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final Long2LongMap map;
        protected transient ObjectSet<Long2LongMap.Entry> entries;
        protected transient LongSet keys;
        protected transient LongCollection values;

        protected UnmodifiableMap(Long2LongMap m) {
            super(m);
            this.map = m;
        }

        @Override
        public boolean containsValue(long v) {
            return this.map.containsValue(v);
        }

        @Deprecated
        @Override
        public boolean containsValue(Object ov) {
            return this.map.containsValue(ov);
        }

        @Override
        public void putAll(Map<? extends Long, ? extends Long> m) {
            throw new UnsupportedOperationException();
        }

        @Override
        public ObjectSet<Long2LongMap.Entry> long2LongEntrySet() {
            if (this.entries == null) {
                this.entries = ObjectSets.unmodifiable(this.map.long2LongEntrySet());
            }
            return this.entries;
        }

        @Deprecated
        @Override
        public ObjectSet<Map.Entry<Long, Long>> entrySet() {
            return this.long2LongEntrySet();
        }

        @Override
        public LongSet keySet() {
            if (this.keys == null) {
                this.keys = LongSets.unmodifiable(this.map.keySet());
            }
            return this.keys;
        }

        @Override
        public LongCollection values() {
            if (this.values == null) {
                return LongCollections.unmodifiable(this.map.values());
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
        public long getOrDefault(long key, long defaultValue) {
            return this.map.getOrDefault(key, defaultValue);
        }

        @Override
        public void forEach(BiConsumer<? super Long, ? super Long> action) {
            this.map.forEach(action);
        }

        @Override
        public void replaceAll(BiFunction<? super Long, ? super Long, ? extends Long> function) {
            throw new UnsupportedOperationException();
        }

        @Override
        public long putIfAbsent(long key, long value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean remove(long key, long value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public long replace(long key, long value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean replace(long key, long oldValue, long newValue) {
            throw new UnsupportedOperationException();
        }

        @Override
        public long computeIfAbsent(long key, LongUnaryOperator mappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Override
        public long computeIfAbsentNullable(long key, LongFunction<? extends Long> mappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Override
        public long computeIfAbsentPartial(long key, Long2LongFunction mappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Override
        public long computeIfPresent(long key, BiFunction<? super Long, ? super Long, ? extends Long> remappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Override
        public long compute(long key, BiFunction<? super Long, ? super Long, ? extends Long> remappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Override
        public long merge(long key, long value, BiFunction<? super Long, ? super Long, ? extends Long> remappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public Long getOrDefault(Object key, Long defaultValue) {
            return this.map.getOrDefault(key, defaultValue);
        }

        @Deprecated
        @Override
        public boolean remove(Object key, Object value) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public Long replace(Long key, Long value) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public boolean replace(Long key, Long oldValue, Long newValue) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public Long putIfAbsent(Long key, Long value) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public Long computeIfAbsent(Long key, Function<? super Long, ? extends Long> mappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public Long computeIfPresent(Long key, BiFunction<? super Long, ? super Long, ? extends Long> remappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public Long compute(Long key, BiFunction<? super Long, ? super Long, ? extends Long> remappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public Long merge(Long key, Long value, BiFunction<? super Long, ? super Long, ? extends Long> remappingFunction) {
            throw new UnsupportedOperationException();
        }
    }

    public static class SynchronizedMap
    extends Long2LongFunctions.SynchronizedFunction
    implements Long2LongMap,
    Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final Long2LongMap map;
        protected transient ObjectSet<Long2LongMap.Entry> entries;
        protected transient LongSet keys;
        protected transient LongCollection values;

        protected SynchronizedMap(Long2LongMap m, Object sync) {
            super(m, sync);
            this.map = m;
        }

        protected SynchronizedMap(Long2LongMap m) {
            super(m);
            this.map = m;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean containsValue(long v) {
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
        public void putAll(Map<? extends Long, ? extends Long> m) {
            Object object = this.sync;
            synchronized (object) {
                this.map.putAll(m);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public ObjectSet<Long2LongMap.Entry> long2LongEntrySet() {
            Object object = this.sync;
            synchronized (object) {
                if (this.entries == null) {
                    this.entries = ObjectSets.synchronize(this.map.long2LongEntrySet(), this.sync);
                }
                return this.entries;
            }
        }

        @Deprecated
        @Override
        public ObjectSet<Map.Entry<Long, Long>> entrySet() {
            return this.long2LongEntrySet();
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
        public LongCollection values() {
            Object object = this.sync;
            synchronized (object) {
                if (this.values == null) {
                    return LongCollections.synchronize(this.map.values(), this.sync);
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
        public long getOrDefault(long key, long defaultValue) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.getOrDefault(key, defaultValue);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void forEach(BiConsumer<? super Long, ? super Long> action) {
            Object object = this.sync;
            synchronized (object) {
                this.map.forEach(action);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void replaceAll(BiFunction<? super Long, ? super Long, ? extends Long> function) {
            Object object = this.sync;
            synchronized (object) {
                this.map.replaceAll(function);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public long putIfAbsent(long key, long value) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.putIfAbsent(key, value);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean remove(long key, long value) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.remove(key, value);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public long replace(long key, long value) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.replace(key, value);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean replace(long key, long oldValue, long newValue) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.replace(key, oldValue, newValue);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public long computeIfAbsent(long key, LongUnaryOperator mappingFunction) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.computeIfAbsent(key, mappingFunction);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public long computeIfAbsentNullable(long key, LongFunction<? extends Long> mappingFunction) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.computeIfAbsentNullable(key, mappingFunction);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public long computeIfAbsentPartial(long key, Long2LongFunction mappingFunction) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.computeIfAbsentPartial(key, mappingFunction);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public long computeIfPresent(long key, BiFunction<? super Long, ? super Long, ? extends Long> remappingFunction) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.computeIfPresent(key, remappingFunction);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public long compute(long key, BiFunction<? super Long, ? super Long, ? extends Long> remappingFunction) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.compute(key, remappingFunction);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public long merge(long key, long value, BiFunction<? super Long, ? super Long, ? extends Long> remappingFunction) {
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
        public Long getOrDefault(Object key, Long defaultValue) {
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
        public Long replace(Long key, Long value) {
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
        public boolean replace(Long key, Long oldValue, Long newValue) {
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
        public Long putIfAbsent(Long key, Long value) {
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
        public Long computeIfAbsent(Long key, Function<? super Long, ? extends Long> mappingFunction) {
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
        public Long computeIfPresent(Long key, BiFunction<? super Long, ? super Long, ? extends Long> remappingFunction) {
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
        public Long compute(Long key, BiFunction<? super Long, ? super Long, ? extends Long> remappingFunction) {
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
        public Long merge(Long key, Long value, BiFunction<? super Long, ? super Long, ? extends Long> remappingFunction) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.merge(key, value, remappingFunction);
            }
        }
    }

    public static class Singleton
    extends Long2LongFunctions.Singleton
    implements Long2LongMap,
    Serializable,
    Cloneable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected transient ObjectSet<Long2LongMap.Entry> entries;
        protected transient LongSet keys;
        protected transient LongCollection values;

        protected Singleton(long key, long value) {
            super(key, value);
        }

        @Override
        public boolean containsValue(long v) {
            return this.value == v;
        }

        @Deprecated
        @Override
        public boolean containsValue(Object ov) {
            return (Long)ov == this.value;
        }

        @Override
        public void putAll(Map<? extends Long, ? extends Long> m) {
            throw new UnsupportedOperationException();
        }

        @Override
        public ObjectSet<Long2LongMap.Entry> long2LongEntrySet() {
            if (this.entries == null) {
                this.entries = ObjectSets.singleton(new AbstractLong2LongMap.BasicEntry(this.key, this.value));
            }
            return this.entries;
        }

        @Deprecated
        @Override
        public ObjectSet<Map.Entry<Long, Long>> entrySet() {
            return this.long2LongEntrySet();
        }

        @Override
        public LongSet keySet() {
            if (this.keys == null) {
                this.keys = LongSets.singleton(this.key);
            }
            return this.keys;
        }

        @Override
        public LongCollection values() {
            if (this.values == null) {
                this.values = LongSets.singleton(this.value);
            }
            return this.values;
        }

        @Override
        public boolean isEmpty() {
            return false;
        }

        @Override
        public int hashCode() {
            return HashCommon.long2int(this.key) ^ HashCommon.long2int(this.value);
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
    extends Long2LongFunctions.EmptyFunction
    implements Long2LongMap,
    Serializable,
    Cloneable {
        private static final long serialVersionUID = -7046029254386353129L;

        protected EmptyMap() {
        }

        @Override
        public boolean containsValue(long v) {
            return false;
        }

        @Deprecated
        @Override
        public boolean containsValue(Object ov) {
            return false;
        }

        @Override
        public void putAll(Map<? extends Long, ? extends Long> m) {
            throw new UnsupportedOperationException();
        }

        @Override
        public ObjectSet<Long2LongMap.Entry> long2LongEntrySet() {
            return ObjectSets.EMPTY_SET;
        }

        @Override
        public LongSet keySet() {
            return LongSets.EMPTY_SET;
        }

        @Override
        public LongCollection values() {
            return LongSets.EMPTY_SET;
        }

        @Override
        public Object clone() {
            return Long2LongMaps.EMPTY_MAP;
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

