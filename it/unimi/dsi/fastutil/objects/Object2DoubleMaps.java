/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.doubles.DoubleCollection;
import it.unimi.dsi.fastutil.doubles.DoubleCollections;
import it.unimi.dsi.fastutil.doubles.DoubleSets;
import it.unimi.dsi.fastutil.objects.AbstractObject2DoubleMap;
import it.unimi.dsi.fastutil.objects.Object2DoubleFunction;
import it.unimi.dsi.fastutil.objects.Object2DoubleFunctions;
import it.unimi.dsi.fastutil.objects.Object2DoubleMap;
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
import java.util.function.ToDoubleFunction;

public final class Object2DoubleMaps {
    public static final EmptyMap EMPTY_MAP = new EmptyMap();

    private Object2DoubleMaps() {
    }

    public static <K> ObjectIterator<Object2DoubleMap.Entry<K>> fastIterator(Object2DoubleMap<K> map) {
        ObjectSet<Object2DoubleMap.Entry<K>> entries = map.object2DoubleEntrySet();
        return entries instanceof Object2DoubleMap.FastEntrySet ? ((Object2DoubleMap.FastEntrySet)entries).fastIterator() : entries.iterator();
    }

    public static <K> void fastForEach(Object2DoubleMap<K> map, Consumer<? super Object2DoubleMap.Entry<K>> consumer) {
        ObjectSet<Object2DoubleMap.Entry<K>> entries = map.object2DoubleEntrySet();
        if (entries instanceof Object2DoubleMap.FastEntrySet) {
            ((Object2DoubleMap.FastEntrySet)entries).fastForEach(consumer);
        } else {
            entries.forEach(consumer);
        }
    }

    public static <K> ObjectIterable<Object2DoubleMap.Entry<K>> fastIterable(Object2DoubleMap<K> map) {
        final ObjectSet<Object2DoubleMap.Entry<K>> entries = map.object2DoubleEntrySet();
        return entries instanceof Object2DoubleMap.FastEntrySet ? new ObjectIterable<Object2DoubleMap.Entry<K>>(){

            @Override
            public ObjectIterator<Object2DoubleMap.Entry<K>> iterator() {
                return ((Object2DoubleMap.FastEntrySet)entries).fastIterator();
            }

            @Override
            public void forEach(Consumer<? super Object2DoubleMap.Entry<K>> consumer) {
                ((Object2DoubleMap.FastEntrySet)entries).fastForEach(consumer);
            }
        } : entries;
    }

    public static <K> Object2DoubleMap<K> emptyMap() {
        return EMPTY_MAP;
    }

    public static <K> Object2DoubleMap<K> singleton(K key, double value) {
        return new Singleton<K>(key, value);
    }

    public static <K> Object2DoubleMap<K> singleton(K key, Double value) {
        return new Singleton<K>(key, value);
    }

    public static <K> Object2DoubleMap<K> synchronize(Object2DoubleMap<K> m) {
        return new SynchronizedMap<K>(m);
    }

    public static <K> Object2DoubleMap<K> synchronize(Object2DoubleMap<K> m, Object sync) {
        return new SynchronizedMap<K>(m, sync);
    }

    public static <K> Object2DoubleMap<K> unmodifiable(Object2DoubleMap<K> m) {
        return new UnmodifiableMap<K>(m);
    }

    public static class UnmodifiableMap<K>
    extends Object2DoubleFunctions.UnmodifiableFunction<K>
    implements Object2DoubleMap<K>,
    Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final Object2DoubleMap<K> map;
        protected transient ObjectSet<Object2DoubleMap.Entry<K>> entries;
        protected transient ObjectSet<K> keys;
        protected transient DoubleCollection values;

        protected UnmodifiableMap(Object2DoubleMap<K> m) {
            super(m);
            this.map = m;
        }

        @Override
        public boolean containsValue(double v) {
            return this.map.containsValue(v);
        }

        @Deprecated
        @Override
        public boolean containsValue(Object ov) {
            return this.map.containsValue(ov);
        }

        @Override
        public void putAll(Map<? extends K, ? extends Double> m) {
            throw new UnsupportedOperationException();
        }

        @Override
        public ObjectSet<Object2DoubleMap.Entry<K>> object2DoubleEntrySet() {
            if (this.entries == null) {
                this.entries = ObjectSets.unmodifiable(this.map.object2DoubleEntrySet());
            }
            return this.entries;
        }

        @Deprecated
        @Override
        public ObjectSet<Map.Entry<K, Double>> entrySet() {
            return this.object2DoubleEntrySet();
        }

        @Override
        public ObjectSet<K> keySet() {
            if (this.keys == null) {
                this.keys = ObjectSets.unmodifiable(this.map.keySet());
            }
            return this.keys;
        }

        @Override
        public DoubleCollection values() {
            if (this.values == null) {
                return DoubleCollections.unmodifiable(this.map.values());
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
        public double getOrDefault(Object key, double defaultValue) {
            return this.map.getOrDefault(key, defaultValue);
        }

        @Override
        public void forEach(BiConsumer<? super K, ? super Double> action) {
            this.map.forEach(action);
        }

        @Override
        public void replaceAll(BiFunction<? super K, ? super Double, ? extends Double> function) {
            throw new UnsupportedOperationException();
        }

        @Override
        public double putIfAbsent(K key, double value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean remove(Object key, double value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public double replace(K key, double value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean replace(K key, double oldValue, double newValue) {
            throw new UnsupportedOperationException();
        }

        @Override
        public double computeDoubleIfAbsent(K key, ToDoubleFunction<? super K> mappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Override
        public double computeDoubleIfAbsentPartial(K key, Object2DoubleFunction<? super K> mappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Override
        public double computeDoubleIfPresent(K key, BiFunction<? super K, ? super Double, ? extends Double> remappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Override
        public double computeDouble(K key, BiFunction<? super K, ? super Double, ? extends Double> remappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Override
        public double mergeDouble(K key, double value, BiFunction<? super Double, ? super Double, ? extends Double> remappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public Double getOrDefault(Object key, Double defaultValue) {
            return this.map.getOrDefault(key, defaultValue);
        }

        @Deprecated
        @Override
        public boolean remove(Object key, Object value) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public Double replace(K key, Double value) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public boolean replace(K key, Double oldValue, Double newValue) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public Double putIfAbsent(K key, Double value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Double computeIfAbsent(K key, Function<? super K, ? extends Double> mappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Double computeIfPresent(K key, BiFunction<? super K, ? super Double, ? extends Double> remappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Double compute(K key, BiFunction<? super K, ? super Double, ? extends Double> remappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public Double merge(K key, Double value, BiFunction<? super Double, ? super Double, ? extends Double> remappingFunction) {
            throw new UnsupportedOperationException();
        }
    }

    public static class SynchronizedMap<K>
    extends Object2DoubleFunctions.SynchronizedFunction<K>
    implements Object2DoubleMap<K>,
    Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final Object2DoubleMap<K> map;
        protected transient ObjectSet<Object2DoubleMap.Entry<K>> entries;
        protected transient ObjectSet<K> keys;
        protected transient DoubleCollection values;

        protected SynchronizedMap(Object2DoubleMap<K> m, Object sync) {
            super(m, sync);
            this.map = m;
        }

        protected SynchronizedMap(Object2DoubleMap<K> m) {
            super(m);
            this.map = m;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean containsValue(double v) {
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
        public void putAll(Map<? extends K, ? extends Double> m) {
            Object object = this.sync;
            synchronized (object) {
                this.map.putAll(m);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public ObjectSet<Object2DoubleMap.Entry<K>> object2DoubleEntrySet() {
            Object object = this.sync;
            synchronized (object) {
                if (this.entries == null) {
                    this.entries = ObjectSets.synchronize(this.map.object2DoubleEntrySet(), this.sync);
                }
                return this.entries;
            }
        }

        @Deprecated
        @Override
        public ObjectSet<Map.Entry<K, Double>> entrySet() {
            return this.object2DoubleEntrySet();
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
        public DoubleCollection values() {
            Object object = this.sync;
            synchronized (object) {
                if (this.values == null) {
                    return DoubleCollections.synchronize(this.map.values(), this.sync);
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
        public double getOrDefault(Object key, double defaultValue) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.getOrDefault(key, defaultValue);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void forEach(BiConsumer<? super K, ? super Double> action) {
            Object object = this.sync;
            synchronized (object) {
                this.map.forEach(action);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void replaceAll(BiFunction<? super K, ? super Double, ? extends Double> function) {
            Object object = this.sync;
            synchronized (object) {
                this.map.replaceAll(function);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public double putIfAbsent(K key, double value) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.putIfAbsent(key, value);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean remove(Object key, double value) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.remove(key, value);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public double replace(K key, double value) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.replace(key, value);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean replace(K key, double oldValue, double newValue) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.replace(key, oldValue, newValue);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public double computeDoubleIfAbsent(K key, ToDoubleFunction<? super K> mappingFunction) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.computeDoubleIfAbsent((K)key, mappingFunction);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public double computeDoubleIfAbsentPartial(K key, Object2DoubleFunction<? super K> mappingFunction) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.computeDoubleIfAbsentPartial((K)key, mappingFunction);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public double computeDoubleIfPresent(K key, BiFunction<? super K, ? super Double, ? extends Double> remappingFunction) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.computeDoubleIfPresent((K)key, (BiFunction<? super K, Double, Double>)remappingFunction);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public double computeDouble(K key, BiFunction<? super K, ? super Double, ? extends Double> remappingFunction) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.computeDouble((K)key, (BiFunction<? super K, Double, Double>)remappingFunction);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public double mergeDouble(K key, double value, BiFunction<? super Double, ? super Double, ? extends Double> remappingFunction) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.mergeDouble(key, value, remappingFunction);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Deprecated
        @Override
        public Double getOrDefault(Object key, Double defaultValue) {
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
        public Double replace(K key, Double value) {
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
        public boolean replace(K key, Double oldValue, Double newValue) {
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
        public Double putIfAbsent(K key, Double value) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.putIfAbsent(key, value);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public Double computeIfAbsent(K key, Function<? super K, ? extends Double> mappingFunction) {
            Object object = this.sync;
            synchronized (object) {
                return (Double)this.map.computeIfAbsent(key, mappingFunction);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public Double computeIfPresent(K key, BiFunction<? super K, ? super Double, ? extends Double> remappingFunction) {
            Object object = this.sync;
            synchronized (object) {
                return (Double)this.map.computeIfPresent(key, remappingFunction);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public Double compute(K key, BiFunction<? super K, ? super Double, ? extends Double> remappingFunction) {
            Object object = this.sync;
            synchronized (object) {
                return (Double)this.map.compute(key, remappingFunction);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Deprecated
        @Override
        public Double merge(K key, Double value, BiFunction<? super Double, ? super Double, ? extends Double> remappingFunction) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.merge(key, value, remappingFunction);
            }
        }
    }

    public static class Singleton<K>
    extends Object2DoubleFunctions.Singleton<K>
    implements Object2DoubleMap<K>,
    Serializable,
    Cloneable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected transient ObjectSet<Object2DoubleMap.Entry<K>> entries;
        protected transient ObjectSet<K> keys;
        protected transient DoubleCollection values;

        protected Singleton(K key, double value) {
            super(key, value);
        }

        @Override
        public boolean containsValue(double v) {
            return Double.doubleToLongBits(this.value) == Double.doubleToLongBits(v);
        }

        @Deprecated
        @Override
        public boolean containsValue(Object ov) {
            return Double.doubleToLongBits((Double)ov) == Double.doubleToLongBits(this.value);
        }

        @Override
        public void putAll(Map<? extends K, ? extends Double> m) {
            throw new UnsupportedOperationException();
        }

        @Override
        public ObjectSet<Object2DoubleMap.Entry<K>> object2DoubleEntrySet() {
            if (this.entries == null) {
                this.entries = ObjectSets.singleton(new AbstractObject2DoubleMap.BasicEntry<Object>(this.key, this.value));
            }
            return this.entries;
        }

        @Deprecated
        @Override
        public ObjectSet<Map.Entry<K, Double>> entrySet() {
            return this.object2DoubleEntrySet();
        }

        @Override
        public ObjectSet<K> keySet() {
            if (this.keys == null) {
                this.keys = ObjectSets.singleton(this.key);
            }
            return this.keys;
        }

        @Override
        public DoubleCollection values() {
            if (this.values == null) {
                this.values = DoubleSets.singleton(this.value);
            }
            return this.values;
        }

        @Override
        public boolean isEmpty() {
            return false;
        }

        @Override
        public int hashCode() {
            return (this.key == null ? 0 : this.key.hashCode()) ^ HashCommon.double2int(this.value);
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

    public static class EmptyMap<K>
    extends Object2DoubleFunctions.EmptyFunction<K>
    implements Object2DoubleMap<K>,
    Serializable,
    Cloneable {
        private static final long serialVersionUID = -7046029254386353129L;

        protected EmptyMap() {
        }

        @Override
        public boolean containsValue(double v) {
            return false;
        }

        @Deprecated
        @Override
        public boolean containsValue(Object ov) {
            return false;
        }

        @Override
        public void putAll(Map<? extends K, ? extends Double> m) {
            throw new UnsupportedOperationException();
        }

        @Override
        public ObjectSet<Object2DoubleMap.Entry<K>> object2DoubleEntrySet() {
            return ObjectSets.EMPTY_SET;
        }

        @Override
        public ObjectSet<K> keySet() {
            return ObjectSets.EMPTY_SET;
        }

        @Override
        public DoubleCollection values() {
            return DoubleSets.EMPTY_SET;
        }

        @Override
        public Object clone() {
            return Object2DoubleMaps.EMPTY_MAP;
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

