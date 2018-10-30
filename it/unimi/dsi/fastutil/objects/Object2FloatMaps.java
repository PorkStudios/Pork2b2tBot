/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.floats.FloatCollection;
import it.unimi.dsi.fastutil.floats.FloatCollections;
import it.unimi.dsi.fastutil.floats.FloatSets;
import it.unimi.dsi.fastutil.objects.AbstractObject2FloatMap;
import it.unimi.dsi.fastutil.objects.Object2FloatFunction;
import it.unimi.dsi.fastutil.objects.Object2FloatFunctions;
import it.unimi.dsi.fastutil.objects.Object2FloatMap;
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

public final class Object2FloatMaps {
    public static final EmptyMap EMPTY_MAP = new EmptyMap();

    private Object2FloatMaps() {
    }

    public static <K> ObjectIterator<Object2FloatMap.Entry<K>> fastIterator(Object2FloatMap<K> map) {
        ObjectSet<Object2FloatMap.Entry<K>> entries = map.object2FloatEntrySet();
        return entries instanceof Object2FloatMap.FastEntrySet ? ((Object2FloatMap.FastEntrySet)entries).fastIterator() : entries.iterator();
    }

    public static <K> void fastForEach(Object2FloatMap<K> map, Consumer<? super Object2FloatMap.Entry<K>> consumer) {
        ObjectSet<Object2FloatMap.Entry<K>> entries = map.object2FloatEntrySet();
        if (entries instanceof Object2FloatMap.FastEntrySet) {
            ((Object2FloatMap.FastEntrySet)entries).fastForEach(consumer);
        } else {
            entries.forEach(consumer);
        }
    }

    public static <K> ObjectIterable<Object2FloatMap.Entry<K>> fastIterable(Object2FloatMap<K> map) {
        final ObjectSet<Object2FloatMap.Entry<K>> entries = map.object2FloatEntrySet();
        return entries instanceof Object2FloatMap.FastEntrySet ? new ObjectIterable<Object2FloatMap.Entry<K>>(){

            @Override
            public ObjectIterator<Object2FloatMap.Entry<K>> iterator() {
                return ((Object2FloatMap.FastEntrySet)entries).fastIterator();
            }

            @Override
            public void forEach(Consumer<? super Object2FloatMap.Entry<K>> consumer) {
                ((Object2FloatMap.FastEntrySet)entries).fastForEach(consumer);
            }
        } : entries;
    }

    public static <K> Object2FloatMap<K> emptyMap() {
        return EMPTY_MAP;
    }

    public static <K> Object2FloatMap<K> singleton(K key, float value) {
        return new Singleton<K>(key, value);
    }

    public static <K> Object2FloatMap<K> singleton(K key, Float value) {
        return new Singleton<K>(key, value.floatValue());
    }

    public static <K> Object2FloatMap<K> synchronize(Object2FloatMap<K> m) {
        return new SynchronizedMap<K>(m);
    }

    public static <K> Object2FloatMap<K> synchronize(Object2FloatMap<K> m, Object sync) {
        return new SynchronizedMap<K>(m, sync);
    }

    public static <K> Object2FloatMap<K> unmodifiable(Object2FloatMap<K> m) {
        return new UnmodifiableMap<K>(m);
    }

    public static class UnmodifiableMap<K>
    extends Object2FloatFunctions.UnmodifiableFunction<K>
    implements Object2FloatMap<K>,
    Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final Object2FloatMap<K> map;
        protected transient ObjectSet<Object2FloatMap.Entry<K>> entries;
        protected transient ObjectSet<K> keys;
        protected transient FloatCollection values;

        protected UnmodifiableMap(Object2FloatMap<K> m) {
            super(m);
            this.map = m;
        }

        @Override
        public boolean containsValue(float v) {
            return this.map.containsValue(v);
        }

        @Deprecated
        @Override
        public boolean containsValue(Object ov) {
            return this.map.containsValue(ov);
        }

        @Override
        public void putAll(Map<? extends K, ? extends Float> m) {
            throw new UnsupportedOperationException();
        }

        @Override
        public ObjectSet<Object2FloatMap.Entry<K>> object2FloatEntrySet() {
            if (this.entries == null) {
                this.entries = ObjectSets.unmodifiable(this.map.object2FloatEntrySet());
            }
            return this.entries;
        }

        @Deprecated
        @Override
        public ObjectSet<Map.Entry<K, Float>> entrySet() {
            return this.object2FloatEntrySet();
        }

        @Override
        public ObjectSet<K> keySet() {
            if (this.keys == null) {
                this.keys = ObjectSets.unmodifiable(this.map.keySet());
            }
            return this.keys;
        }

        @Override
        public FloatCollection values() {
            if (this.values == null) {
                return FloatCollections.unmodifiable(this.map.values());
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
        public float getOrDefault(Object key, float defaultValue) {
            return this.map.getOrDefault(key, defaultValue);
        }

        @Override
        public void forEach(BiConsumer<? super K, ? super Float> action) {
            this.map.forEach(action);
        }

        @Override
        public void replaceAll(BiFunction<? super K, ? super Float, ? extends Float> function) {
            throw new UnsupportedOperationException();
        }

        @Override
        public float putIfAbsent(K key, float value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean remove(Object key, float value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public float replace(K key, float value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean replace(K key, float oldValue, float newValue) {
            throw new UnsupportedOperationException();
        }

        @Override
        public float computeFloatIfAbsent(K key, ToDoubleFunction<? super K> mappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Override
        public float computeFloatIfAbsentPartial(K key, Object2FloatFunction<? super K> mappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Override
        public float computeFloatIfPresent(K key, BiFunction<? super K, ? super Float, ? extends Float> remappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Override
        public float computeFloat(K key, BiFunction<? super K, ? super Float, ? extends Float> remappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Override
        public float mergeFloat(K key, float value, BiFunction<? super Float, ? super Float, ? extends Float> remappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public Float getOrDefault(Object key, Float defaultValue) {
            return this.map.getOrDefault(key, defaultValue);
        }

        @Deprecated
        @Override
        public boolean remove(Object key, Object value) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public Float replace(K key, Float value) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public boolean replace(K key, Float oldValue, Float newValue) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public Float putIfAbsent(K key, Float value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Float computeIfAbsent(K key, Function<? super K, ? extends Float> mappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Float computeIfPresent(K key, BiFunction<? super K, ? super Float, ? extends Float> remappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Float compute(K key, BiFunction<? super K, ? super Float, ? extends Float> remappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public Float merge(K key, Float value, BiFunction<? super Float, ? super Float, ? extends Float> remappingFunction) {
            throw new UnsupportedOperationException();
        }
    }

    public static class SynchronizedMap<K>
    extends Object2FloatFunctions.SynchronizedFunction<K>
    implements Object2FloatMap<K>,
    Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final Object2FloatMap<K> map;
        protected transient ObjectSet<Object2FloatMap.Entry<K>> entries;
        protected transient ObjectSet<K> keys;
        protected transient FloatCollection values;

        protected SynchronizedMap(Object2FloatMap<K> m, Object sync) {
            super(m, sync);
            this.map = m;
        }

        protected SynchronizedMap(Object2FloatMap<K> m) {
            super(m);
            this.map = m;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean containsValue(float v) {
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
        public void putAll(Map<? extends K, ? extends Float> m) {
            Object object = this.sync;
            synchronized (object) {
                this.map.putAll(m);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public ObjectSet<Object2FloatMap.Entry<K>> object2FloatEntrySet() {
            Object object = this.sync;
            synchronized (object) {
                if (this.entries == null) {
                    this.entries = ObjectSets.synchronize(this.map.object2FloatEntrySet(), this.sync);
                }
                return this.entries;
            }
        }

        @Deprecated
        @Override
        public ObjectSet<Map.Entry<K, Float>> entrySet() {
            return this.object2FloatEntrySet();
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
        public FloatCollection values() {
            Object object = this.sync;
            synchronized (object) {
                if (this.values == null) {
                    return FloatCollections.synchronize(this.map.values(), this.sync);
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
        public float getOrDefault(Object key, float defaultValue) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.getOrDefault(key, defaultValue);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void forEach(BiConsumer<? super K, ? super Float> action) {
            Object object = this.sync;
            synchronized (object) {
                this.map.forEach(action);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void replaceAll(BiFunction<? super K, ? super Float, ? extends Float> function) {
            Object object = this.sync;
            synchronized (object) {
                this.map.replaceAll(function);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public float putIfAbsent(K key, float value) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.putIfAbsent(key, value);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean remove(Object key, float value) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.remove(key, value);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public float replace(K key, float value) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.replace(key, value);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean replace(K key, float oldValue, float newValue) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.replace(key, oldValue, newValue);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public float computeFloatIfAbsent(K key, ToDoubleFunction<? super K> mappingFunction) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.computeFloatIfAbsent((K)key, mappingFunction);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public float computeFloatIfAbsentPartial(K key, Object2FloatFunction<? super K> mappingFunction) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.computeFloatIfAbsentPartial((K)key, mappingFunction);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public float computeFloatIfPresent(K key, BiFunction<? super K, ? super Float, ? extends Float> remappingFunction) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.computeFloatIfPresent((K)key, (BiFunction<? super K, Float, Float>)remappingFunction);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public float computeFloat(K key, BiFunction<? super K, ? super Float, ? extends Float> remappingFunction) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.computeFloat((K)key, (BiFunction<? super K, Float, Float>)remappingFunction);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public float mergeFloat(K key, float value, BiFunction<? super Float, ? super Float, ? extends Float> remappingFunction) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.mergeFloat(key, value, remappingFunction);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Deprecated
        @Override
        public Float getOrDefault(Object key, Float defaultValue) {
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
        public Float replace(K key, Float value) {
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
        public boolean replace(K key, Float oldValue, Float newValue) {
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
        public Float putIfAbsent(K key, Float value) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.putIfAbsent(key, value);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public Float computeIfAbsent(K key, Function<? super K, ? extends Float> mappingFunction) {
            Object object = this.sync;
            synchronized (object) {
                return (Float)this.map.computeIfAbsent(key, mappingFunction);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public Float computeIfPresent(K key, BiFunction<? super K, ? super Float, ? extends Float> remappingFunction) {
            Object object = this.sync;
            synchronized (object) {
                return (Float)this.map.computeIfPresent(key, remappingFunction);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public Float compute(K key, BiFunction<? super K, ? super Float, ? extends Float> remappingFunction) {
            Object object = this.sync;
            synchronized (object) {
                return (Float)this.map.compute(key, remappingFunction);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Deprecated
        @Override
        public Float merge(K key, Float value, BiFunction<? super Float, ? super Float, ? extends Float> remappingFunction) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.merge(key, value, remappingFunction);
            }
        }
    }

    public static class Singleton<K>
    extends Object2FloatFunctions.Singleton<K>
    implements Object2FloatMap<K>,
    Serializable,
    Cloneable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected transient ObjectSet<Object2FloatMap.Entry<K>> entries;
        protected transient ObjectSet<K> keys;
        protected transient FloatCollection values;

        protected Singleton(K key, float value) {
            super(key, value);
        }

        @Override
        public boolean containsValue(float v) {
            return Float.floatToIntBits(this.value) == Float.floatToIntBits(v);
        }

        @Deprecated
        @Override
        public boolean containsValue(Object ov) {
            return Float.floatToIntBits(((Float)ov).floatValue()) == Float.floatToIntBits(this.value);
        }

        @Override
        public void putAll(Map<? extends K, ? extends Float> m) {
            throw new UnsupportedOperationException();
        }

        @Override
        public ObjectSet<Object2FloatMap.Entry<K>> object2FloatEntrySet() {
            if (this.entries == null) {
                this.entries = ObjectSets.singleton(new AbstractObject2FloatMap.BasicEntry<Object>(this.key, this.value));
            }
            return this.entries;
        }

        @Deprecated
        @Override
        public ObjectSet<Map.Entry<K, Float>> entrySet() {
            return this.object2FloatEntrySet();
        }

        @Override
        public ObjectSet<K> keySet() {
            if (this.keys == null) {
                this.keys = ObjectSets.singleton(this.key);
            }
            return this.keys;
        }

        @Override
        public FloatCollection values() {
            if (this.values == null) {
                this.values = FloatSets.singleton(this.value);
            }
            return this.values;
        }

        @Override
        public boolean isEmpty() {
            return false;
        }

        @Override
        public int hashCode() {
            return (this.key == null ? 0 : this.key.hashCode()) ^ HashCommon.float2int(this.value);
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
    extends Object2FloatFunctions.EmptyFunction<K>
    implements Object2FloatMap<K>,
    Serializable,
    Cloneable {
        private static final long serialVersionUID = -7046029254386353129L;

        protected EmptyMap() {
        }

        @Override
        public boolean containsValue(float v) {
            return false;
        }

        @Deprecated
        @Override
        public boolean containsValue(Object ov) {
            return false;
        }

        @Override
        public void putAll(Map<? extends K, ? extends Float> m) {
            throw new UnsupportedOperationException();
        }

        @Override
        public ObjectSet<Object2FloatMap.Entry<K>> object2FloatEntrySet() {
            return ObjectSets.EMPTY_SET;
        }

        @Override
        public ObjectSet<K> keySet() {
            return ObjectSets.EMPTY_SET;
        }

        @Override
        public FloatCollection values() {
            return FloatSets.EMPTY_SET;
        }

        @Override
        public Object clone() {
            return Object2FloatMaps.EMPTY_MAP;
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

