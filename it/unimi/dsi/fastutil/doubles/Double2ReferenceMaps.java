/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.doubles;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.doubles.AbstractDouble2ReferenceMap;
import it.unimi.dsi.fastutil.doubles.Double2ReferenceFunction;
import it.unimi.dsi.fastutil.doubles.Double2ReferenceFunctions;
import it.unimi.dsi.fastutil.doubles.Double2ReferenceMap;
import it.unimi.dsi.fastutil.doubles.DoubleSet;
import it.unimi.dsi.fastutil.doubles.DoubleSets;
import it.unimi.dsi.fastutil.objects.ObjectIterable;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import it.unimi.dsi.fastutil.objects.ObjectSets;
import it.unimi.dsi.fastutil.objects.ReferenceCollection;
import it.unimi.dsi.fastutil.objects.ReferenceCollections;
import it.unimi.dsi.fastutil.objects.ReferenceSets;
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
import java.util.function.Function;

public final class Double2ReferenceMaps {
    public static final EmptyMap EMPTY_MAP = new EmptyMap();

    private Double2ReferenceMaps() {
    }

    public static <V> ObjectIterator<Double2ReferenceMap.Entry<V>> fastIterator(Double2ReferenceMap<V> map) {
        ObjectSet<Double2ReferenceMap.Entry<V>> entries = map.double2ReferenceEntrySet();
        return entries instanceof Double2ReferenceMap.FastEntrySet ? ((Double2ReferenceMap.FastEntrySet)entries).fastIterator() : entries.iterator();
    }

    public static <V> void fastForEach(Double2ReferenceMap<V> map, Consumer<? super Double2ReferenceMap.Entry<V>> consumer) {
        ObjectSet<Double2ReferenceMap.Entry<V>> entries = map.double2ReferenceEntrySet();
        if (entries instanceof Double2ReferenceMap.FastEntrySet) {
            ((Double2ReferenceMap.FastEntrySet)entries).fastForEach(consumer);
        } else {
            entries.forEach(consumer);
        }
    }

    public static <V> ObjectIterable<Double2ReferenceMap.Entry<V>> fastIterable(Double2ReferenceMap<V> map) {
        final ObjectSet<Double2ReferenceMap.Entry<V>> entries = map.double2ReferenceEntrySet();
        return entries instanceof Double2ReferenceMap.FastEntrySet ? new ObjectIterable<Double2ReferenceMap.Entry<V>>(){

            @Override
            public ObjectIterator<Double2ReferenceMap.Entry<V>> iterator() {
                return ((Double2ReferenceMap.FastEntrySet)entries).fastIterator();
            }

            @Override
            public void forEach(Consumer<? super Double2ReferenceMap.Entry<V>> consumer) {
                ((Double2ReferenceMap.FastEntrySet)entries).fastForEach(consumer);
            }
        } : entries;
    }

    public static <V> Double2ReferenceMap<V> emptyMap() {
        return EMPTY_MAP;
    }

    public static <V> Double2ReferenceMap<V> singleton(double key, V value) {
        return new Singleton<V>(key, value);
    }

    public static <V> Double2ReferenceMap<V> singleton(Double key, V value) {
        return new Singleton<V>(key, value);
    }

    public static <V> Double2ReferenceMap<V> synchronize(Double2ReferenceMap<V> m) {
        return new SynchronizedMap<V>(m);
    }

    public static <V> Double2ReferenceMap<V> synchronize(Double2ReferenceMap<V> m, Object sync) {
        return new SynchronizedMap<V>(m, sync);
    }

    public static <V> Double2ReferenceMap<V> unmodifiable(Double2ReferenceMap<V> m) {
        return new UnmodifiableMap<V>(m);
    }

    public static class UnmodifiableMap<V>
    extends Double2ReferenceFunctions.UnmodifiableFunction<V>
    implements Double2ReferenceMap<V>,
    Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final Double2ReferenceMap<V> map;
        protected transient ObjectSet<Double2ReferenceMap.Entry<V>> entries;
        protected transient DoubleSet keys;
        protected transient ReferenceCollection<V> values;

        protected UnmodifiableMap(Double2ReferenceMap<V> m) {
            super(m);
            this.map = m;
        }

        @Override
        public boolean containsValue(Object v) {
            return this.map.containsValue(v);
        }

        @Override
        public void putAll(Map<? extends Double, ? extends V> m) {
            throw new UnsupportedOperationException();
        }

        @Override
        public ObjectSet<Double2ReferenceMap.Entry<V>> double2ReferenceEntrySet() {
            if (this.entries == null) {
                this.entries = ObjectSets.unmodifiable(this.map.double2ReferenceEntrySet());
            }
            return this.entries;
        }

        @Deprecated
        @Override
        public ObjectSet<Map.Entry<Double, V>> entrySet() {
            return this.double2ReferenceEntrySet();
        }

        @Override
        public DoubleSet keySet() {
            if (this.keys == null) {
                this.keys = DoubleSets.unmodifiable(this.map.keySet());
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
        public V getOrDefault(double key, V defaultValue) {
            return this.map.getOrDefault(key, defaultValue);
        }

        @Override
        public void forEach(BiConsumer<? super Double, ? super V> action) {
            this.map.forEach(action);
        }

        @Override
        public void replaceAll(BiFunction<? super Double, ? super V, ? extends V> function) {
            throw new UnsupportedOperationException();
        }

        @Override
        public V putIfAbsent(double key, V value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean remove(double key, Object value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public V replace(double key, V value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean replace(double key, V oldValue, V newValue) {
            throw new UnsupportedOperationException();
        }

        @Override
        public V computeIfAbsent(double key, DoubleFunction<? extends V> mappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Override
        public V computeIfAbsentPartial(double key, Double2ReferenceFunction<? extends V> mappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Override
        public V computeIfPresent(double key, BiFunction<? super Double, ? super V, ? extends V> remappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Override
        public V compute(double key, BiFunction<? super Double, ? super V, ? extends V> remappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Override
        public V merge(double key, V value, BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
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
        public V replace(Double key, V value) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public boolean replace(Double key, V oldValue, V newValue) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public V putIfAbsent(Double key, V value) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public V computeIfAbsent(Double key, Function<? super Double, ? extends V> mappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public V computeIfPresent(Double key, BiFunction<? super Double, ? super V, ? extends V> remappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public V compute(Double key, BiFunction<? super Double, ? super V, ? extends V> remappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public V merge(Double key, V value, BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
            throw new UnsupportedOperationException();
        }
    }

    public static class SynchronizedMap<V>
    extends Double2ReferenceFunctions.SynchronizedFunction<V>
    implements Double2ReferenceMap<V>,
    Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final Double2ReferenceMap<V> map;
        protected transient ObjectSet<Double2ReferenceMap.Entry<V>> entries;
        protected transient DoubleSet keys;
        protected transient ReferenceCollection<V> values;

        protected SynchronizedMap(Double2ReferenceMap<V> m, Object sync) {
            super(m, sync);
            this.map = m;
        }

        protected SynchronizedMap(Double2ReferenceMap<V> m) {
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
        public void putAll(Map<? extends Double, ? extends V> m) {
            Object object = this.sync;
            synchronized (object) {
                this.map.putAll(m);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public ObjectSet<Double2ReferenceMap.Entry<V>> double2ReferenceEntrySet() {
            Object object = this.sync;
            synchronized (object) {
                if (this.entries == null) {
                    this.entries = ObjectSets.synchronize(this.map.double2ReferenceEntrySet(), this.sync);
                }
                return this.entries;
            }
        }

        @Deprecated
        @Override
        public ObjectSet<Map.Entry<Double, V>> entrySet() {
            return this.double2ReferenceEntrySet();
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
        public V getOrDefault(double key, V defaultValue) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.getOrDefault(key, defaultValue);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void forEach(BiConsumer<? super Double, ? super V> action) {
            Object object = this.sync;
            synchronized (object) {
                this.map.forEach(action);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void replaceAll(BiFunction<? super Double, ? super V, ? extends V> function) {
            Object object = this.sync;
            synchronized (object) {
                this.map.replaceAll(function);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public V putIfAbsent(double key, V value) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.putIfAbsent(key, value);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean remove(double key, Object value) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.remove(key, value);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public V replace(double key, V value) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.replace(key, value);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean replace(double key, V oldValue, V newValue) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.replace(key, oldValue, newValue);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public V computeIfAbsent(double key, DoubleFunction<? extends V> mappingFunction) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.computeIfAbsent(key, mappingFunction);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public V computeIfAbsentPartial(double key, Double2ReferenceFunction<? extends V> mappingFunction) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.computeIfAbsentPartial(key, mappingFunction);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public V computeIfPresent(double key, BiFunction<? super Double, ? super V, ? extends V> remappingFunction) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.computeIfPresent(key, (BiFunction<Double, ? extends V, ? extends V>)remappingFunction);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public V compute(double key, BiFunction<? super Double, ? super V, ? extends V> remappingFunction) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.compute(key, (BiFunction<Double, ? extends V, ? extends V>)remappingFunction);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public V merge(double key, V value, BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
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
        public V replace(Double key, V value) {
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
        public boolean replace(Double key, V oldValue, V newValue) {
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
        public V putIfAbsent(Double key, V value) {
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
        public V computeIfAbsent(Double key, Function<? super Double, ? extends V> mappingFunction) {
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
        public V computeIfPresent(Double key, BiFunction<? super Double, ? super V, ? extends V> remappingFunction) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.computeIfPresent(key, (BiFunction<Double, ? extends V, ? extends V>)remappingFunction);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Deprecated
        @Override
        public V compute(Double key, BiFunction<? super Double, ? super V, ? extends V> remappingFunction) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.compute(key, (BiFunction<Double, ? extends V, ? extends V>)remappingFunction);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Deprecated
        @Override
        public V merge(Double key, V value, BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.merge(key, (V)value, (BiFunction<? extends V, ? extends V, ? extends V>)remappingFunction);
            }
        }
    }

    public static class Singleton<V>
    extends Double2ReferenceFunctions.Singleton<V>
    implements Double2ReferenceMap<V>,
    Serializable,
    Cloneable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected transient ObjectSet<Double2ReferenceMap.Entry<V>> entries;
        protected transient DoubleSet keys;
        protected transient ReferenceCollection<V> values;

        protected Singleton(double key, V value) {
            super(key, value);
        }

        @Override
        public boolean containsValue(Object v) {
            return this.value == v;
        }

        @Override
        public void putAll(Map<? extends Double, ? extends V> m) {
            throw new UnsupportedOperationException();
        }

        @Override
        public ObjectSet<Double2ReferenceMap.Entry<V>> double2ReferenceEntrySet() {
            if (this.entries == null) {
                this.entries = ObjectSets.singleton(new AbstractDouble2ReferenceMap.BasicEntry<Object>(this.key, this.value));
            }
            return this.entries;
        }

        @Deprecated
        @Override
        public ObjectSet<Map.Entry<Double, V>> entrySet() {
            return this.double2ReferenceEntrySet();
        }

        @Override
        public DoubleSet keySet() {
            if (this.keys == null) {
                this.keys = DoubleSets.singleton(this.key);
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
            return HashCommon.double2int(this.key) ^ (this.value == null ? 0 : System.identityHashCode(this.value));
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
    extends Double2ReferenceFunctions.EmptyFunction<V>
    implements Double2ReferenceMap<V>,
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
        public void putAll(Map<? extends Double, ? extends V> m) {
            throw new UnsupportedOperationException();
        }

        @Override
        public ObjectSet<Double2ReferenceMap.Entry<V>> double2ReferenceEntrySet() {
            return ObjectSets.EMPTY_SET;
        }

        @Override
        public DoubleSet keySet() {
            return DoubleSets.EMPTY_SET;
        }

        @Override
        public ReferenceCollection<V> values() {
            return ReferenceSets.EMPTY_SET;
        }

        @Override
        public Object clone() {
            return Double2ReferenceMaps.EMPTY_MAP;
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

