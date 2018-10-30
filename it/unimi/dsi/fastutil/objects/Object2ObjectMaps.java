/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.objects.AbstractObject2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectFunction;
import it.unimi.dsi.fastutil.objects.Object2ObjectFunctions;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
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

public final class Object2ObjectMaps {
    public static final EmptyMap EMPTY_MAP = new EmptyMap();

    private Object2ObjectMaps() {
    }

    public static <K, V> ObjectIterator<Object2ObjectMap.Entry<K, V>> fastIterator(Object2ObjectMap<K, V> map) {
        ObjectSet<Object2ObjectMap.Entry<K, V>> entries = map.object2ObjectEntrySet();
        return entries instanceof Object2ObjectMap.FastEntrySet ? ((Object2ObjectMap.FastEntrySet)entries).fastIterator() : entries.iterator();
    }

    public static <K, V> void fastForEach(Object2ObjectMap<K, V> map, Consumer<? super Object2ObjectMap.Entry<K, V>> consumer) {
        ObjectSet<Object2ObjectMap.Entry<K, V>> entries = map.object2ObjectEntrySet();
        if (entries instanceof Object2ObjectMap.FastEntrySet) {
            ((Object2ObjectMap.FastEntrySet)entries).fastForEach(consumer);
        } else {
            entries.forEach(consumer);
        }
    }

    public static <K, V> ObjectIterable<Object2ObjectMap.Entry<K, V>> fastIterable(Object2ObjectMap<K, V> map) {
        final ObjectSet<Object2ObjectMap.Entry<K, V>> entries = map.object2ObjectEntrySet();
        return entries instanceof Object2ObjectMap.FastEntrySet ? new ObjectIterable<Object2ObjectMap.Entry<K, V>>(){

            @Override
            public ObjectIterator<Object2ObjectMap.Entry<K, V>> iterator() {
                return ((Object2ObjectMap.FastEntrySet)entries).fastIterator();
            }

            @Override
            public void forEach(Consumer<? super Object2ObjectMap.Entry<K, V>> consumer) {
                ((Object2ObjectMap.FastEntrySet)entries).fastForEach(consumer);
            }
        } : entries;
    }

    public static <K, V> Object2ObjectMap<K, V> emptyMap() {
        return EMPTY_MAP;
    }

    public static <K, V> Object2ObjectMap<K, V> singleton(K key, V value) {
        return new Singleton<K, V>(key, value);
    }

    public static <K, V> Object2ObjectMap<K, V> synchronize(Object2ObjectMap<K, V> m) {
        return new SynchronizedMap<K, V>(m);
    }

    public static <K, V> Object2ObjectMap<K, V> synchronize(Object2ObjectMap<K, V> m, Object sync) {
        return new SynchronizedMap<K, V>(m, sync);
    }

    public static <K, V> Object2ObjectMap<K, V> unmodifiable(Object2ObjectMap<K, V> m) {
        return new UnmodifiableMap<K, V>(m);
    }

    public static class UnmodifiableMap<K, V>
    extends Object2ObjectFunctions.UnmodifiableFunction<K, V>
    implements Object2ObjectMap<K, V>,
    Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final Object2ObjectMap<K, V> map;
        protected transient ObjectSet<Object2ObjectMap.Entry<K, V>> entries;
        protected transient ObjectSet<K> keys;
        protected transient ObjectCollection<V> values;

        protected UnmodifiableMap(Object2ObjectMap<K, V> m) {
            super(m);
            this.map = m;
        }

        @Override
        public boolean containsValue(Object v) {
            return this.map.containsValue(v);
        }

        @Override
        public void putAll(Map<? extends K, ? extends V> m) {
            throw new UnsupportedOperationException();
        }

        @Override
        public ObjectSet<Object2ObjectMap.Entry<K, V>> object2ObjectEntrySet() {
            if (this.entries == null) {
                this.entries = ObjectSets.unmodifiable(this.map.object2ObjectEntrySet());
            }
            return this.entries;
        }

        @Override
        public ObjectSet<Map.Entry<K, V>> entrySet() {
            return this.object2ObjectEntrySet();
        }

        @Override
        public ObjectSet<K> keySet() {
            if (this.keys == null) {
                this.keys = ObjectSets.unmodifiable(this.map.keySet());
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
        public V getOrDefault(Object key, V defaultValue) {
            return this.map.getOrDefault(key, defaultValue);
        }

        @Override
        public void forEach(BiConsumer<? super K, ? super V> action) {
            this.map.forEach(action);
        }

        @Override
        public void replaceAll(BiFunction<? super K, ? super V, ? extends V> function) {
            throw new UnsupportedOperationException();
        }

        @Override
        public V putIfAbsent(K key, V value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean remove(Object key, Object value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public V replace(K key, V value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean replace(K key, V oldValue, V newValue) {
            throw new UnsupportedOperationException();
        }

        @Override
        public V computeIfPresent(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Override
        public V compute(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Override
        public V merge(K key, V value, BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
            throw new UnsupportedOperationException();
        }
    }

    public static class SynchronizedMap<K, V>
    extends Object2ObjectFunctions.SynchronizedFunction<K, V>
    implements Object2ObjectMap<K, V>,
    Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final Object2ObjectMap<K, V> map;
        protected transient ObjectSet<Object2ObjectMap.Entry<K, V>> entries;
        protected transient ObjectSet<K> keys;
        protected transient ObjectCollection<V> values;

        protected SynchronizedMap(Object2ObjectMap<K, V> m, Object sync) {
            super(m, sync);
            this.map = m;
        }

        protected SynchronizedMap(Object2ObjectMap<K, V> m) {
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
        public void putAll(Map<? extends K, ? extends V> m) {
            Object object = this.sync;
            synchronized (object) {
                this.map.putAll(m);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public ObjectSet<Object2ObjectMap.Entry<K, V>> object2ObjectEntrySet() {
            Object object = this.sync;
            synchronized (object) {
                if (this.entries == null) {
                    this.entries = ObjectSets.synchronize(this.map.object2ObjectEntrySet(), this.sync);
                }
                return this.entries;
            }
        }

        @Override
        public ObjectSet<Map.Entry<K, V>> entrySet() {
            return this.object2ObjectEntrySet();
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public ObjectSet<K> keySet() {
            Object object = this.sync;
            synchronized (object) {
                if (this.keys == null) {
                    this.keys = ObjectSets.synchronize(this.map.keySet(), this.sync);
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
        public V getOrDefault(Object key, V defaultValue) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.getOrDefault(key, defaultValue);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void forEach(BiConsumer<? super K, ? super V> action) {
            Object object = this.sync;
            synchronized (object) {
                this.map.forEach(action);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void replaceAll(BiFunction<? super K, ? super V, ? extends V> function) {
            Object object = this.sync;
            synchronized (object) {
                this.map.replaceAll(function);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public V putIfAbsent(K key, V value) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.putIfAbsent(key, value);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
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
        @Override
        public V replace(K key, V value) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.replace(key, value);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean replace(K key, V oldValue, V newValue) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.replace(key, oldValue, newValue);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public V computeIfPresent(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.computeIfPresent((K)key, (BiFunction<? super K, ? extends V, ? extends V>)remappingFunction);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public V compute(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.compute((K)key, (BiFunction<? super K, ? extends V, ? extends V>)remappingFunction);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public V merge(K key, V value, BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.merge(key, (V)value, (BiFunction<? extends V, ? extends V, ? extends V>)remappingFunction);
            }
        }
    }

    public static class Singleton<K, V>
    extends Object2ObjectFunctions.Singleton<K, V>
    implements Object2ObjectMap<K, V>,
    Serializable,
    Cloneable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected transient ObjectSet<Object2ObjectMap.Entry<K, V>> entries;
        protected transient ObjectSet<K> keys;
        protected transient ObjectCollection<V> values;

        protected Singleton(K key, V value) {
            super(key, value);
        }

        @Override
        public boolean containsValue(Object v) {
            return Objects.equals(this.value, v);
        }

        @Override
        public void putAll(Map<? extends K, ? extends V> m) {
            throw new UnsupportedOperationException();
        }

        @Override
        public ObjectSet<Object2ObjectMap.Entry<K, V>> object2ObjectEntrySet() {
            if (this.entries == null) {
                this.entries = ObjectSets.singleton(new AbstractObject2ObjectMap.BasicEntry<Object, Object>(this.key, this.value));
            }
            return this.entries;
        }

        @Override
        public ObjectSet<Map.Entry<K, V>> entrySet() {
            return this.object2ObjectEntrySet();
        }

        @Override
        public ObjectSet<K> keySet() {
            if (this.keys == null) {
                this.keys = ObjectSets.singleton(this.key);
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
            return (this.key == null ? 0 : this.key.hashCode()) ^ (this.value == null ? 0 : this.value.hashCode());
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

    public static class EmptyMap<K, V>
    extends Object2ObjectFunctions.EmptyFunction<K, V>
    implements Object2ObjectMap<K, V>,
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
        public void putAll(Map<? extends K, ? extends V> m) {
            throw new UnsupportedOperationException();
        }

        @Override
        public ObjectSet<Object2ObjectMap.Entry<K, V>> object2ObjectEntrySet() {
            return ObjectSets.EMPTY_SET;
        }

        @Override
        public ObjectSet<K> keySet() {
            return ObjectSets.EMPTY_SET;
        }

        @Override
        public ObjectCollection<V> values() {
            return ObjectSets.EMPTY_SET;
        }

        @Override
        public Object clone() {
            return Object2ObjectMaps.EMPTY_MAP;
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

