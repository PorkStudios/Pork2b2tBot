/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.booleans.BooleanCollection;
import it.unimi.dsi.fastutil.booleans.BooleanCollections;
import it.unimi.dsi.fastutil.booleans.BooleanSets;
import it.unimi.dsi.fastutil.objects.AbstractObject2BooleanMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanFunction;
import it.unimi.dsi.fastutil.objects.Object2BooleanFunctions;
import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
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
import java.util.function.Predicate;

public final class Object2BooleanMaps {
    public static final EmptyMap EMPTY_MAP = new EmptyMap();

    private Object2BooleanMaps() {
    }

    public static <K> ObjectIterator<Object2BooleanMap.Entry<K>> fastIterator(Object2BooleanMap<K> map) {
        ObjectSet<Object2BooleanMap.Entry<K>> entries = map.object2BooleanEntrySet();
        return entries instanceof Object2BooleanMap.FastEntrySet ? ((Object2BooleanMap.FastEntrySet)entries).fastIterator() : entries.iterator();
    }

    public static <K> void fastForEach(Object2BooleanMap<K> map, Consumer<? super Object2BooleanMap.Entry<K>> consumer) {
        ObjectSet<Object2BooleanMap.Entry<K>> entries = map.object2BooleanEntrySet();
        if (entries instanceof Object2BooleanMap.FastEntrySet) {
            ((Object2BooleanMap.FastEntrySet)entries).fastForEach(consumer);
        } else {
            entries.forEach(consumer);
        }
    }

    public static <K> ObjectIterable<Object2BooleanMap.Entry<K>> fastIterable(Object2BooleanMap<K> map) {
        final ObjectSet<Object2BooleanMap.Entry<K>> entries = map.object2BooleanEntrySet();
        return entries instanceof Object2BooleanMap.FastEntrySet ? new ObjectIterable<Object2BooleanMap.Entry<K>>(){

            @Override
            public ObjectIterator<Object2BooleanMap.Entry<K>> iterator() {
                return ((Object2BooleanMap.FastEntrySet)entries).fastIterator();
            }

            @Override
            public void forEach(Consumer<? super Object2BooleanMap.Entry<K>> consumer) {
                ((Object2BooleanMap.FastEntrySet)entries).fastForEach(consumer);
            }
        } : entries;
    }

    public static <K> Object2BooleanMap<K> emptyMap() {
        return EMPTY_MAP;
    }

    public static <K> Object2BooleanMap<K> singleton(K key, boolean value) {
        return new Singleton<K>(key, value);
    }

    public static <K> Object2BooleanMap<K> singleton(K key, Boolean value) {
        return new Singleton<K>(key, value);
    }

    public static <K> Object2BooleanMap<K> synchronize(Object2BooleanMap<K> m) {
        return new SynchronizedMap<K>(m);
    }

    public static <K> Object2BooleanMap<K> synchronize(Object2BooleanMap<K> m, Object sync) {
        return new SynchronizedMap<K>(m, sync);
    }

    public static <K> Object2BooleanMap<K> unmodifiable(Object2BooleanMap<K> m) {
        return new UnmodifiableMap<K>(m);
    }

    public static class UnmodifiableMap<K>
    extends Object2BooleanFunctions.UnmodifiableFunction<K>
    implements Object2BooleanMap<K>,
    Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final Object2BooleanMap<K> map;
        protected transient ObjectSet<Object2BooleanMap.Entry<K>> entries;
        protected transient ObjectSet<K> keys;
        protected transient BooleanCollection values;

        protected UnmodifiableMap(Object2BooleanMap<K> m) {
            super(m);
            this.map = m;
        }

        @Override
        public boolean containsValue(boolean v) {
            return this.map.containsValue(v);
        }

        @Deprecated
        @Override
        public boolean containsValue(Object ov) {
            return this.map.containsValue(ov);
        }

        @Override
        public void putAll(Map<? extends K, ? extends Boolean> m) {
            throw new UnsupportedOperationException();
        }

        @Override
        public ObjectSet<Object2BooleanMap.Entry<K>> object2BooleanEntrySet() {
            if (this.entries == null) {
                this.entries = ObjectSets.unmodifiable(this.map.object2BooleanEntrySet());
            }
            return this.entries;
        }

        @Deprecated
        @Override
        public ObjectSet<Map.Entry<K, Boolean>> entrySet() {
            return this.object2BooleanEntrySet();
        }

        @Override
        public ObjectSet<K> keySet() {
            if (this.keys == null) {
                this.keys = ObjectSets.unmodifiable(this.map.keySet());
            }
            return this.keys;
        }

        @Override
        public BooleanCollection values() {
            if (this.values == null) {
                return BooleanCollections.unmodifiable(this.map.values());
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
        public boolean getOrDefault(Object key, boolean defaultValue) {
            return this.map.getOrDefault(key, defaultValue);
        }

        @Override
        public void forEach(BiConsumer<? super K, ? super Boolean> action) {
            this.map.forEach(action);
        }

        @Override
        public void replaceAll(BiFunction<? super K, ? super Boolean, ? extends Boolean> function) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean putIfAbsent(K key, boolean value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean remove(Object key, boolean value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean replace(K key, boolean value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean replace(K key, boolean oldValue, boolean newValue) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean computeBooleanIfAbsent(K key, Predicate<? super K> mappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean computeBooleanIfAbsentPartial(K key, Object2BooleanFunction<? super K> mappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean computeBooleanIfPresent(K key, BiFunction<? super K, ? super Boolean, ? extends Boolean> remappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean computeBoolean(K key, BiFunction<? super K, ? super Boolean, ? extends Boolean> remappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean mergeBoolean(K key, boolean value, BiFunction<? super Boolean, ? super Boolean, ? extends Boolean> remappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public Boolean getOrDefault(Object key, Boolean defaultValue) {
            return this.map.getOrDefault(key, defaultValue);
        }

        @Deprecated
        @Override
        public boolean remove(Object key, Object value) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public Boolean replace(K key, Boolean value) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public boolean replace(K key, Boolean oldValue, Boolean newValue) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public Boolean putIfAbsent(K key, Boolean value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Boolean computeIfAbsent(K key, Function<? super K, ? extends Boolean> mappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Boolean computeIfPresent(K key, BiFunction<? super K, ? super Boolean, ? extends Boolean> remappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Boolean compute(K key, BiFunction<? super K, ? super Boolean, ? extends Boolean> remappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public Boolean merge(K key, Boolean value, BiFunction<? super Boolean, ? super Boolean, ? extends Boolean> remappingFunction) {
            throw new UnsupportedOperationException();
        }
    }

    public static class SynchronizedMap<K>
    extends Object2BooleanFunctions.SynchronizedFunction<K>
    implements Object2BooleanMap<K>,
    Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final Object2BooleanMap<K> map;
        protected transient ObjectSet<Object2BooleanMap.Entry<K>> entries;
        protected transient ObjectSet<K> keys;
        protected transient BooleanCollection values;

        protected SynchronizedMap(Object2BooleanMap<K> m, Object sync) {
            super(m, sync);
            this.map = m;
        }

        protected SynchronizedMap(Object2BooleanMap<K> m) {
            super(m);
            this.map = m;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean containsValue(boolean v) {
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
        public void putAll(Map<? extends K, ? extends Boolean> m) {
            Object object = this.sync;
            synchronized (object) {
                this.map.putAll(m);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public ObjectSet<Object2BooleanMap.Entry<K>> object2BooleanEntrySet() {
            Object object = this.sync;
            synchronized (object) {
                if (this.entries == null) {
                    this.entries = ObjectSets.synchronize(this.map.object2BooleanEntrySet(), this.sync);
                }
                return this.entries;
            }
        }

        @Deprecated
        @Override
        public ObjectSet<Map.Entry<K, Boolean>> entrySet() {
            return this.object2BooleanEntrySet();
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
        public BooleanCollection values() {
            Object object = this.sync;
            synchronized (object) {
                if (this.values == null) {
                    return BooleanCollections.synchronize(this.map.values(), this.sync);
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
        public boolean getOrDefault(Object key, boolean defaultValue) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.getOrDefault(key, defaultValue);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void forEach(BiConsumer<? super K, ? super Boolean> action) {
            Object object = this.sync;
            synchronized (object) {
                this.map.forEach(action);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void replaceAll(BiFunction<? super K, ? super Boolean, ? extends Boolean> function) {
            Object object = this.sync;
            synchronized (object) {
                this.map.replaceAll(function);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean putIfAbsent(K key, boolean value) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.putIfAbsent(key, value);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean remove(Object key, boolean value) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.remove(key, value);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean replace(K key, boolean value) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.replace(key, value);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean replace(K key, boolean oldValue, boolean newValue) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.replace(key, oldValue, newValue);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean computeBooleanIfAbsent(K key, Predicate<? super K> mappingFunction) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.computeBooleanIfAbsent((K)key, mappingFunction);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean computeBooleanIfAbsentPartial(K key, Object2BooleanFunction<? super K> mappingFunction) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.computeBooleanIfAbsentPartial((K)key, mappingFunction);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean computeBooleanIfPresent(K key, BiFunction<? super K, ? super Boolean, ? extends Boolean> remappingFunction) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.computeBooleanIfPresent((K)key, (BiFunction<? super K, Boolean, Boolean>)remappingFunction);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean computeBoolean(K key, BiFunction<? super K, ? super Boolean, ? extends Boolean> remappingFunction) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.computeBoolean((K)key, (BiFunction<? super K, Boolean, Boolean>)remappingFunction);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean mergeBoolean(K key, boolean value, BiFunction<? super Boolean, ? super Boolean, ? extends Boolean> remappingFunction) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.mergeBoolean(key, value, remappingFunction);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Deprecated
        @Override
        public Boolean getOrDefault(Object key, Boolean defaultValue) {
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
        public Boolean replace(K key, Boolean value) {
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
        public boolean replace(K key, Boolean oldValue, Boolean newValue) {
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
        public Boolean putIfAbsent(K key, Boolean value) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.putIfAbsent(key, value);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public Boolean computeIfAbsent(K key, Function<? super K, ? extends Boolean> mappingFunction) {
            Object object = this.sync;
            synchronized (object) {
                return (Boolean)this.map.computeIfAbsent(key, mappingFunction);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public Boolean computeIfPresent(K key, BiFunction<? super K, ? super Boolean, ? extends Boolean> remappingFunction) {
            Object object = this.sync;
            synchronized (object) {
                return (Boolean)this.map.computeIfPresent(key, remappingFunction);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public Boolean compute(K key, BiFunction<? super K, ? super Boolean, ? extends Boolean> remappingFunction) {
            Object object = this.sync;
            synchronized (object) {
                return (Boolean)this.map.compute(key, remappingFunction);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Deprecated
        @Override
        public Boolean merge(K key, Boolean value, BiFunction<? super Boolean, ? super Boolean, ? extends Boolean> remappingFunction) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.merge(key, value, remappingFunction);
            }
        }
    }

    public static class Singleton<K>
    extends Object2BooleanFunctions.Singleton<K>
    implements Object2BooleanMap<K>,
    Serializable,
    Cloneable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected transient ObjectSet<Object2BooleanMap.Entry<K>> entries;
        protected transient ObjectSet<K> keys;
        protected transient BooleanCollection values;

        protected Singleton(K key, boolean value) {
            super(key, value);
        }

        @Override
        public boolean containsValue(boolean v) {
            return this.value == v;
        }

        @Deprecated
        @Override
        public boolean containsValue(Object ov) {
            return (Boolean)ov == this.value;
        }

        @Override
        public void putAll(Map<? extends K, ? extends Boolean> m) {
            throw new UnsupportedOperationException();
        }

        @Override
        public ObjectSet<Object2BooleanMap.Entry<K>> object2BooleanEntrySet() {
            if (this.entries == null) {
                this.entries = ObjectSets.singleton(new AbstractObject2BooleanMap.BasicEntry<Object>(this.key, this.value));
            }
            return this.entries;
        }

        @Deprecated
        @Override
        public ObjectSet<Map.Entry<K, Boolean>> entrySet() {
            return this.object2BooleanEntrySet();
        }

        @Override
        public ObjectSet<K> keySet() {
            if (this.keys == null) {
                this.keys = ObjectSets.singleton(this.key);
            }
            return this.keys;
        }

        @Override
        public BooleanCollection values() {
            if (this.values == null) {
                this.values = BooleanSets.singleton(this.value);
            }
            return this.values;
        }

        @Override
        public boolean isEmpty() {
            return false;
        }

        @Override
        public int hashCode() {
            return (this.key == null ? 0 : this.key.hashCode()) ^ (this.value ? 1231 : 1237);
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
    extends Object2BooleanFunctions.EmptyFunction<K>
    implements Object2BooleanMap<K>,
    Serializable,
    Cloneable {
        private static final long serialVersionUID = -7046029254386353129L;

        protected EmptyMap() {
        }

        @Override
        public boolean containsValue(boolean v) {
            return false;
        }

        @Deprecated
        @Override
        public boolean containsValue(Object ov) {
            return false;
        }

        @Override
        public void putAll(Map<? extends K, ? extends Boolean> m) {
            throw new UnsupportedOperationException();
        }

        @Override
        public ObjectSet<Object2BooleanMap.Entry<K>> object2BooleanEntrySet() {
            return ObjectSets.EMPTY_SET;
        }

        @Override
        public ObjectSet<K> keySet() {
            return ObjectSets.EMPTY_SET;
        }

        @Override
        public BooleanCollection values() {
            return BooleanSets.EMPTY_SET;
        }

        @Override
        public Object clone() {
            return Object2BooleanMaps.EMPTY_MAP;
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

