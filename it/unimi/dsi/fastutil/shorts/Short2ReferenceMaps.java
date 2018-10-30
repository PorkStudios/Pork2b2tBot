/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.shorts;

import it.unimi.dsi.fastutil.objects.ObjectIterable;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import it.unimi.dsi.fastutil.objects.ObjectSets;
import it.unimi.dsi.fastutil.objects.ReferenceCollection;
import it.unimi.dsi.fastutil.objects.ReferenceCollections;
import it.unimi.dsi.fastutil.objects.ReferenceSets;
import it.unimi.dsi.fastutil.shorts.AbstractShort2ReferenceMap;
import it.unimi.dsi.fastutil.shorts.Short2ReferenceFunction;
import it.unimi.dsi.fastutil.shorts.Short2ReferenceFunctions;
import it.unimi.dsi.fastutil.shorts.Short2ReferenceMap;
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

public final class Short2ReferenceMaps {
    public static final EmptyMap EMPTY_MAP = new EmptyMap();

    private Short2ReferenceMaps() {
    }

    public static <V> ObjectIterator<Short2ReferenceMap.Entry<V>> fastIterator(Short2ReferenceMap<V> map) {
        ObjectSet<Short2ReferenceMap.Entry<V>> entries = map.short2ReferenceEntrySet();
        return entries instanceof Short2ReferenceMap.FastEntrySet ? ((Short2ReferenceMap.FastEntrySet)entries).fastIterator() : entries.iterator();
    }

    public static <V> void fastForEach(Short2ReferenceMap<V> map, Consumer<? super Short2ReferenceMap.Entry<V>> consumer) {
        ObjectSet<Short2ReferenceMap.Entry<V>> entries = map.short2ReferenceEntrySet();
        if (entries instanceof Short2ReferenceMap.FastEntrySet) {
            ((Short2ReferenceMap.FastEntrySet)entries).fastForEach(consumer);
        } else {
            entries.forEach(consumer);
        }
    }

    public static <V> ObjectIterable<Short2ReferenceMap.Entry<V>> fastIterable(Short2ReferenceMap<V> map) {
        final ObjectSet<Short2ReferenceMap.Entry<V>> entries = map.short2ReferenceEntrySet();
        return entries instanceof Short2ReferenceMap.FastEntrySet ? new ObjectIterable<Short2ReferenceMap.Entry<V>>(){

            @Override
            public ObjectIterator<Short2ReferenceMap.Entry<V>> iterator() {
                return ((Short2ReferenceMap.FastEntrySet)entries).fastIterator();
            }

            @Override
            public void forEach(Consumer<? super Short2ReferenceMap.Entry<V>> consumer) {
                ((Short2ReferenceMap.FastEntrySet)entries).fastForEach(consumer);
            }
        } : entries;
    }

    public static <V> Short2ReferenceMap<V> emptyMap() {
        return EMPTY_MAP;
    }

    public static <V> Short2ReferenceMap<V> singleton(short key, V value) {
        return new Singleton<V>(key, value);
    }

    public static <V> Short2ReferenceMap<V> singleton(Short key, V value) {
        return new Singleton<V>(key, value);
    }

    public static <V> Short2ReferenceMap<V> synchronize(Short2ReferenceMap<V> m) {
        return new SynchronizedMap<V>(m);
    }

    public static <V> Short2ReferenceMap<V> synchronize(Short2ReferenceMap<V> m, Object sync) {
        return new SynchronizedMap<V>(m, sync);
    }

    public static <V> Short2ReferenceMap<V> unmodifiable(Short2ReferenceMap<V> m) {
        return new UnmodifiableMap<V>(m);
    }

    public static class UnmodifiableMap<V>
    extends Short2ReferenceFunctions.UnmodifiableFunction<V>
    implements Short2ReferenceMap<V>,
    Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final Short2ReferenceMap<V> map;
        protected transient ObjectSet<Short2ReferenceMap.Entry<V>> entries;
        protected transient ShortSet keys;
        protected transient ReferenceCollection<V> values;

        protected UnmodifiableMap(Short2ReferenceMap<V> m) {
            super(m);
            this.map = m;
        }

        @Override
        public boolean containsValue(Object v) {
            return this.map.containsValue(v);
        }

        @Override
        public void putAll(Map<? extends Short, ? extends V> m) {
            throw new UnsupportedOperationException();
        }

        @Override
        public ObjectSet<Short2ReferenceMap.Entry<V>> short2ReferenceEntrySet() {
            if (this.entries == null) {
                this.entries = ObjectSets.unmodifiable(this.map.short2ReferenceEntrySet());
            }
            return this.entries;
        }

        @Deprecated
        @Override
        public ObjectSet<Map.Entry<Short, V>> entrySet() {
            return this.short2ReferenceEntrySet();
        }

        @Override
        public ShortSet keySet() {
            if (this.keys == null) {
                this.keys = ShortSets.unmodifiable(this.map.keySet());
            }
            return this.keys;
        }

        @Override
        public ReferenceCollection<V> values() {
            if (this.values == null) {
                return ReferenceCollections.unmodifiable(this.map.values());
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
        public V getOrDefault(short key, V defaultValue) {
            return this.map.getOrDefault(key, defaultValue);
        }

        @Override
        public void forEach(BiConsumer<? super Short, ? super V> action) {
            this.map.forEach(action);
        }

        @Override
        public void replaceAll(BiFunction<? super Short, ? super V, ? extends V> function) {
            throw new UnsupportedOperationException();
        }

        @Override
        public V putIfAbsent(short key, V value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean remove(short key, Object value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public V replace(short key, V value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean replace(short key, V oldValue, V newValue) {
            throw new UnsupportedOperationException();
        }

        @Override
        public V computeIfAbsent(short key, IntFunction<? extends V> mappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Override
        public V computeIfAbsentPartial(short key, Short2ReferenceFunction<? extends V> mappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Override
        public V computeIfPresent(short key, BiFunction<? super Short, ? super V, ? extends V> remappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Override
        public V compute(short key, BiFunction<? super Short, ? super V, ? extends V> remappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Override
        public V merge(short key, V value, BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
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
        public V replace(Short key, V value) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public boolean replace(Short key, V oldValue, V newValue) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public V putIfAbsent(Short key, V value) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public V computeIfAbsent(Short key, Function<? super Short, ? extends V> mappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public V computeIfPresent(Short key, BiFunction<? super Short, ? super V, ? extends V> remappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public V compute(Short key, BiFunction<? super Short, ? super V, ? extends V> remappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public V merge(Short key, V value, BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
            throw new UnsupportedOperationException();
        }
    }

    public static class SynchronizedMap<V>
    extends Short2ReferenceFunctions.SynchronizedFunction<V>
    implements Short2ReferenceMap<V>,
    Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final Short2ReferenceMap<V> map;
        protected transient ObjectSet<Short2ReferenceMap.Entry<V>> entries;
        protected transient ShortSet keys;
        protected transient ReferenceCollection<V> values;

        protected SynchronizedMap(Short2ReferenceMap<V> m, Object sync) {
            super(m, sync);
            this.map = m;
        }

        protected SynchronizedMap(Short2ReferenceMap<V> m) {
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
        public void putAll(Map<? extends Short, ? extends V> m) {
            Object object = this.sync;
            synchronized (object) {
                this.map.putAll(m);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public ObjectSet<Short2ReferenceMap.Entry<V>> short2ReferenceEntrySet() {
            Object object = this.sync;
            synchronized (object) {
                if (this.entries == null) {
                    this.entries = ObjectSets.synchronize(this.map.short2ReferenceEntrySet(), this.sync);
                }
                return this.entries;
            }
        }

        @Deprecated
        @Override
        public ObjectSet<Map.Entry<Short, V>> entrySet() {
            return this.short2ReferenceEntrySet();
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
        public ReferenceCollection<V> values() {
            Object object = this.sync;
            synchronized (object) {
                if (this.values == null) {
                    return ReferenceCollections.synchronize(this.map.values(), this.sync);
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
        public V getOrDefault(short key, V defaultValue) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.getOrDefault(key, defaultValue);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void forEach(BiConsumer<? super Short, ? super V> action) {
            Object object = this.sync;
            synchronized (object) {
                this.map.forEach(action);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void replaceAll(BiFunction<? super Short, ? super V, ? extends V> function) {
            Object object = this.sync;
            synchronized (object) {
                this.map.replaceAll(function);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public V putIfAbsent(short key, V value) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.putIfAbsent(key, value);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean remove(short key, Object value) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.remove(key, value);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public V replace(short key, V value) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.replace(key, value);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean replace(short key, V oldValue, V newValue) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.replace(key, oldValue, newValue);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public V computeIfAbsent(short key, IntFunction<? extends V> mappingFunction) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.computeIfAbsent(key, mappingFunction);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public V computeIfAbsentPartial(short key, Short2ReferenceFunction<? extends V> mappingFunction) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.computeIfAbsentPartial(key, mappingFunction);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public V computeIfPresent(short key, BiFunction<? super Short, ? super V, ? extends V> remappingFunction) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.computeIfPresent(key, (BiFunction<Short, ? extends V, ? extends V>)remappingFunction);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public V compute(short key, BiFunction<? super Short, ? super V, ? extends V> remappingFunction) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.compute(key, (BiFunction<Short, ? extends V, ? extends V>)remappingFunction);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public V merge(short key, V value, BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
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
        public V replace(Short key, V value) {
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
        public boolean replace(Short key, V oldValue, V newValue) {
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
        public V putIfAbsent(Short key, V value) {
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
        public V computeIfAbsent(Short key, Function<? super Short, ? extends V> mappingFunction) {
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
        public V computeIfPresent(Short key, BiFunction<? super Short, ? super V, ? extends V> remappingFunction) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.computeIfPresent(key, (BiFunction<Short, ? extends V, ? extends V>)remappingFunction);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Deprecated
        @Override
        public V compute(Short key, BiFunction<? super Short, ? super V, ? extends V> remappingFunction) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.compute(key, (BiFunction<Short, ? extends V, ? extends V>)remappingFunction);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Deprecated
        @Override
        public V merge(Short key, V value, BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.merge(key, (V)value, (BiFunction<? extends V, ? extends V, ? extends V>)remappingFunction);
            }
        }
    }

    public static class Singleton<V>
    extends Short2ReferenceFunctions.Singleton<V>
    implements Short2ReferenceMap<V>,
    Serializable,
    Cloneable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected transient ObjectSet<Short2ReferenceMap.Entry<V>> entries;
        protected transient ShortSet keys;
        protected transient ReferenceCollection<V> values;

        protected Singleton(short key, V value) {
            super(key, value);
        }

        @Override
        public boolean containsValue(Object v) {
            return this.value == v;
        }

        @Override
        public void putAll(Map<? extends Short, ? extends V> m) {
            throw new UnsupportedOperationException();
        }

        @Override
        public ObjectSet<Short2ReferenceMap.Entry<V>> short2ReferenceEntrySet() {
            if (this.entries == null) {
                this.entries = ObjectSets.singleton(new AbstractShort2ReferenceMap.BasicEntry<Object>(this.key, this.value));
            }
            return this.entries;
        }

        @Deprecated
        @Override
        public ObjectSet<Map.Entry<Short, V>> entrySet() {
            return this.short2ReferenceEntrySet();
        }

        @Override
        public ShortSet keySet() {
            if (this.keys == null) {
                this.keys = ShortSets.singleton(this.key);
            }
            return this.keys;
        }

        @Override
        public ReferenceCollection<V> values() {
            if (this.values == null) {
                this.values = ReferenceSets.singleton(this.value);
            }
            return this.values;
        }

        @Override
        public boolean isEmpty() {
            return false;
        }

        @Override
        public int hashCode() {
            return this.key ^ (this.value == null ? 0 : System.identityHashCode(this.value));
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
    extends Short2ReferenceFunctions.EmptyFunction<V>
    implements Short2ReferenceMap<V>,
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
        public void putAll(Map<? extends Short, ? extends V> m) {
            throw new UnsupportedOperationException();
        }

        @Override
        public ObjectSet<Short2ReferenceMap.Entry<V>> short2ReferenceEntrySet() {
            return ObjectSets.EMPTY_SET;
        }

        @Override
        public ShortSet keySet() {
            return ShortSets.EMPTY_SET;
        }

        @Override
        public ReferenceCollection<V> values() {
            return ReferenceSets.EMPTY_SET;
        }

        @Override
        public Object clone() {
            return Short2ReferenceMaps.EMPTY_MAP;
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

