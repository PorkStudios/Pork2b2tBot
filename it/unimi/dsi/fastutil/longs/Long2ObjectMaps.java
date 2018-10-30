/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.longs.AbstractLong2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectFunction;
import it.unimi.dsi.fastutil.longs.Long2ObjectFunctions;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.longs.LongSets;
import it.unimi.dsi.fastutil.objects.ObjectCollection;
import it.unimi.dsi.fastutil.objects.ObjectCollections;
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
import java.util.Objects;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.LongFunction;

public final class Long2ObjectMaps {
    public static final EmptyMap EMPTY_MAP = new EmptyMap();

    private Long2ObjectMaps() {
    }

    public static <V> ObjectIterator<Long2ObjectMap.Entry<V>> fastIterator(Long2ObjectMap<V> map) {
        ObjectSet<Long2ObjectMap.Entry<V>> entries = map.long2ObjectEntrySet();
        return entries instanceof Long2ObjectMap.FastEntrySet ? ((Long2ObjectMap.FastEntrySet)entries).fastIterator() : entries.iterator();
    }

    public static <V> void fastForEach(Long2ObjectMap<V> map, Consumer<? super Long2ObjectMap.Entry<V>> consumer) {
        ObjectSet<Long2ObjectMap.Entry<V>> entries = map.long2ObjectEntrySet();
        if (entries instanceof Long2ObjectMap.FastEntrySet) {
            ((Long2ObjectMap.FastEntrySet)entries).fastForEach(consumer);
        } else {
            entries.forEach(consumer);
        }
    }

    public static <V> ObjectIterable<Long2ObjectMap.Entry<V>> fastIterable(Long2ObjectMap<V> map) {
        final ObjectSet<Long2ObjectMap.Entry<V>> entries = map.long2ObjectEntrySet();
        return entries instanceof Long2ObjectMap.FastEntrySet ? new ObjectIterable<Long2ObjectMap.Entry<V>>(){

            @Override
            public ObjectIterator<Long2ObjectMap.Entry<V>> iterator() {
                return ((Long2ObjectMap.FastEntrySet)entries).fastIterator();
            }

            @Override
            public void forEach(Consumer<? super Long2ObjectMap.Entry<V>> consumer) {
                ((Long2ObjectMap.FastEntrySet)entries).fastForEach(consumer);
            }
        } : entries;
    }

    public static <V> Long2ObjectMap<V> emptyMap() {
        return EMPTY_MAP;
    }

    public static <V> Long2ObjectMap<V> singleton(long key, V value) {
        return new Singleton<V>(key, value);
    }

    public static <V> Long2ObjectMap<V> singleton(Long key, V value) {
        return new Singleton<V>(key, value);
    }

    public static <V> Long2ObjectMap<V> synchronize(Long2ObjectMap<V> m) {
        return new SynchronizedMap<V>(m);
    }

    public static <V> Long2ObjectMap<V> synchronize(Long2ObjectMap<V> m, Object sync) {
        return new SynchronizedMap<V>(m, sync);
    }

    public static <V> Long2ObjectMap<V> unmodifiable(Long2ObjectMap<V> m) {
        return new UnmodifiableMap<V>(m);
    }

    public static class UnmodifiableMap<V>
    extends Long2ObjectFunctions.UnmodifiableFunction<V>
    implements Long2ObjectMap<V>,
    Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final Long2ObjectMap<V> map;
        protected transient ObjectSet<Long2ObjectMap.Entry<V>> entries;
        protected transient LongSet keys;
        protected transient ObjectCollection<V> values;

        protected UnmodifiableMap(Long2ObjectMap<V> m) {
            super(m);
            this.map = m;
        }

        @Override
        public boolean containsValue(Object v) {
            return this.map.containsValue(v);
        }

        @Override
        public void putAll(Map<? extends Long, ? extends V> m) {
            throw new UnsupportedOperationException();
        }

        @Override
        public ObjectSet<Long2ObjectMap.Entry<V>> long2ObjectEntrySet() {
            if (this.entries == null) {
                this.entries = ObjectSets.unmodifiable(this.map.long2ObjectEntrySet());
            }
            return this.entries;
        }

        @Deprecated
        @Override
        public ObjectSet<Map.Entry<Long, V>> entrySet() {
            return this.long2ObjectEntrySet();
        }

        @Override
        public LongSet keySet() {
            if (this.keys == null) {
                this.keys = LongSets.unmodifiable(this.map.keySet());
            }
            return this.keys;
        }

        @Override
        public ObjectCollection<V> values() {
            if (this.values == null) {
                return ObjectCollections.unmodifiable(this.map.values());
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
        public V getOrDefault(long key, V defaultValue) {
            return this.map.getOrDefault(key, defaultValue);
        }

        @Override
        public void forEach(BiConsumer<? super Long, ? super V> action) {
            this.map.forEach(action);
        }

        @Override
        public void replaceAll(BiFunction<? super Long, ? super V, ? extends V> function) {
            throw new UnsupportedOperationException();
        }

        @Override
        public V putIfAbsent(long key, V value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean remove(long key, Object value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public V replace(long key, V value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean replace(long key, V oldValue, V newValue) {
            throw new UnsupportedOperationException();
        }

        @Override
        public V computeIfAbsent(long key, LongFunction<? extends V> mappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Override
        public V computeIfAbsentPartial(long key, Long2ObjectFunction<? extends V> mappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Override
        public V computeIfPresent(long key, BiFunction<? super Long, ? super V, ? extends V> remappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Override
        public V compute(long key, BiFunction<? super Long, ? super V, ? extends V> remappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Override
        public V merge(long key, V value, BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public V getOrDefault(Object key, V defaultValue) {
            return this.map.getOrDefault(key, defaultValue);
        }

        @Deprecated
        @Override
        public boolean remove(Object key, Object value) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public V replace(Long key, V value) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public boolean replace(Long key, V oldValue, V newValue) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public V putIfAbsent(Long key, V value) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public V computeIfAbsent(Long key, Function<? super Long, ? extends V> mappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public V computeIfPresent(Long key, BiFunction<? super Long, ? super V, ? extends V> remappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public V compute(Long key, BiFunction<? super Long, ? super V, ? extends V> remappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public V merge(Long key, V value, BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
            throw new UnsupportedOperationException();
        }
    }

    public static class SynchronizedMap<V>
    extends Long2ObjectFunctions.SynchronizedFunction<V>
    implements Long2ObjectMap<V>,
    Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final Long2ObjectMap<V> map;
        protected transient ObjectSet<Long2ObjectMap.Entry<V>> entries;
        protected transient LongSet keys;
        protected transient ObjectCollection<V> values;

        protected SynchronizedMap(Long2ObjectMap<V> m, Object sync) {
            super(m, sync);
            this.map = m;
        }

        protected SynchronizedMap(Long2ObjectMap<V> m) {
            super(m);
            this.map = m;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean containsValue(Object v) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.containsValue(v);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void putAll(Map<? extends Long, ? extends V> m) {
            Object object = this.sync;
            synchronized (object) {
                this.map.putAll(m);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public ObjectSet<Long2ObjectMap.Entry<V>> long2ObjectEntrySet() {
            Object object = this.sync;
            synchronized (object) {
                if (this.entries == null) {
                    this.entries = ObjectSets.synchronize(this.map.long2ObjectEntrySet(), this.sync);
                }
                return this.entries;
            }
        }

        @Deprecated
        @Override
        public ObjectSet<Map.Entry<Long, V>> entrySet() {
            return this.long2ObjectEntrySet();
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
        public ObjectCollection<V> values() {
            Object object = this.sync;
            synchronized (object) {
                if (this.values == null) {
                    return ObjectCollections.synchronize(this.map.values(), this.sync);
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
        public V getOrDefault(long key, V defaultValue) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.getOrDefault(key, defaultValue);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void forEach(BiConsumer<? super Long, ? super V> action) {
            Object object = this.sync;
            synchronized (object) {
                this.map.forEach(action);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void replaceAll(BiFunction<? super Long, ? super V, ? extends V> function) {
            Object object = this.sync;
            synchronized (object) {
                this.map.replaceAll(function);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public V putIfAbsent(long key, V value) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.putIfAbsent(key, value);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean remove(long key, Object value) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.remove(key, value);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public V replace(long key, V value) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.replace(key, value);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean replace(long key, V oldValue, V newValue) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.replace(key, oldValue, newValue);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public V computeIfAbsent(long key, LongFunction<? extends V> mappingFunction) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.computeIfAbsent(key, mappingFunction);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public V computeIfAbsentPartial(long key, Long2ObjectFunction<? extends V> mappingFunction) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.computeIfAbsentPartial(key, mappingFunction);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public V computeIfPresent(long key, BiFunction<? super Long, ? super V, ? extends V> remappingFunction) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.computeIfPresent(key, (BiFunction<Long, ? extends V, ? extends V>)remappingFunction);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public V compute(long key, BiFunction<? super Long, ? super V, ? extends V> remappingFunction) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.compute(key, (BiFunction<Long, ? extends V, ? extends V>)remappingFunction);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public V merge(long key, V value, BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.merge(key, (V)value, (BiFunction<? extends V, ? extends V, ? extends V>)remappingFunction);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Deprecated
        @Override
        public V getOrDefault(Object key, V defaultValue) {
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
        public V replace(Long key, V value) {
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
        public boolean replace(Long key, V oldValue, V newValue) {
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
        public V putIfAbsent(Long key, V value) {
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
        public V computeIfAbsent(Long key, Function<? super Long, ? extends V> mappingFunction) {
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
        public V computeIfPresent(Long key, BiFunction<? super Long, ? super V, ? extends V> remappingFunction) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.computeIfPresent(key, (BiFunction<Long, ? extends V, ? extends V>)remappingFunction);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Deprecated
        @Override
        public V compute(Long key, BiFunction<? super Long, ? super V, ? extends V> remappingFunction) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.compute(key, (BiFunction<Long, ? extends V, ? extends V>)remappingFunction);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Deprecated
        @Override
        public V merge(Long key, V value, BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.merge(key, (V)value, (BiFunction<? extends V, ? extends V, ? extends V>)remappingFunction);
            }
        }
    }

    public static class Singleton<V>
    extends Long2ObjectFunctions.Singleton<V>
    implements Long2ObjectMap<V>,
    Serializable,
    Cloneable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected transient ObjectSet<Long2ObjectMap.Entry<V>> entries;
        protected transient LongSet keys;
        protected transient ObjectCollection<V> values;

        protected Singleton(long key, V value) {
            super(key, value);
        }

        @Override
        public boolean containsValue(Object v) {
            return Objects.equals(this.value, v);
        }

        @Override
        public void putAll(Map<? extends Long, ? extends V> m) {
            throw new UnsupportedOperationException();
        }

        @Override
        public ObjectSet<Long2ObjectMap.Entry<V>> long2ObjectEntrySet() {
            if (this.entries == null) {
                this.entries = ObjectSets.singleton(new AbstractLong2ObjectMap.BasicEntry<Object>(this.key, this.value));
            }
            return this.entries;
        }

        @Deprecated
        @Override
        public ObjectSet<Map.Entry<Long, V>> entrySet() {
            return this.long2ObjectEntrySet();
        }

        @Override
        public LongSet keySet() {
            if (this.keys == null) {
                this.keys = LongSets.singleton(this.key);
            }
            return this.keys;
        }

        @Override
        public ObjectCollection<V> values() {
            if (this.values == null) {
                this.values = ObjectSets.singleton(this.value);
            }
            return this.values;
        }

        @Override
        public boolean isEmpty() {
            return false;
        }

        @Override
        public int hashCode() {
            return HashCommon.long2int(this.key) ^ (this.value == null ? 0 : this.value.hashCode());
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

    public static class EmptyMap<V>
    extends Long2ObjectFunctions.EmptyFunction<V>
    implements Long2ObjectMap<V>,
    Serializable,
    Cloneable {
        private static final long serialVersionUID = -7046029254386353129L;

        protected EmptyMap() {
        }

        @Override
        public boolean containsValue(Object v) {
            return false;
        }

        @Override
        public void putAll(Map<? extends Long, ? extends V> m) {
            throw new UnsupportedOperationException();
        }

        @Override
        public ObjectSet<Long2ObjectMap.Entry<V>> long2ObjectEntrySet() {
            return ObjectSets.EMPTY_SET;
        }

        @Override
        public LongSet keySet() {
            return LongSets.EMPTY_SET;
        }

        @Override
        public ObjectCollection<V> values() {
            return ObjectSets.EMPTY_SET;
        }

        @Override
        public Object clone() {
            return Long2ObjectMaps.EMPTY_MAP;
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

