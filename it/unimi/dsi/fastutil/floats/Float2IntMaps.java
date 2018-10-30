/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.floats.AbstractFloat2IntMap;
import it.unimi.dsi.fastutil.floats.Float2IntFunction;
import it.unimi.dsi.fastutil.floats.Float2IntFunctions;
import it.unimi.dsi.fastutil.floats.Float2IntMap;
import it.unimi.dsi.fastutil.floats.FloatSet;
import it.unimi.dsi.fastutil.floats.FloatSets;
import it.unimi.dsi.fastutil.ints.IntCollection;
import it.unimi.dsi.fastutil.ints.IntCollections;
import it.unimi.dsi.fastutil.ints.IntSets;
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
import java.util.function.DoubleFunction;
import java.util.function.DoubleToIntFunction;
import java.util.function.Function;

public final class Float2IntMaps {
    public static final EmptyMap EMPTY_MAP = new EmptyMap();

    private Float2IntMaps() {
    }

    public static ObjectIterator<Float2IntMap.Entry> fastIterator(Float2IntMap map) {
        ObjectSet<Float2IntMap.Entry> entries = map.float2IntEntrySet();
        return entries instanceof Float2IntMap.FastEntrySet ? ((Float2IntMap.FastEntrySet)entries).fastIterator() : entries.iterator();
    }

    public static void fastForEach(Float2IntMap map, Consumer<? super Float2IntMap.Entry> consumer) {
        ObjectSet<Float2IntMap.Entry> entries = map.float2IntEntrySet();
        if (entries instanceof Float2IntMap.FastEntrySet) {
            ((Float2IntMap.FastEntrySet)entries).fastForEach(consumer);
        } else {
            entries.forEach(consumer);
        }
    }

    public static ObjectIterable<Float2IntMap.Entry> fastIterable(Float2IntMap map) {
        final ObjectSet<Float2IntMap.Entry> entries = map.float2IntEntrySet();
        return entries instanceof Float2IntMap.FastEntrySet ? new ObjectIterable<Float2IntMap.Entry>(){

            @Override
            public ObjectIterator<Float2IntMap.Entry> iterator() {
                return ((Float2IntMap.FastEntrySet)entries).fastIterator();
            }

            @Override
            public void forEach(Consumer<? super Float2IntMap.Entry> consumer) {
                ((Float2IntMap.FastEntrySet)entries).fastForEach(consumer);
            }
        } : entries;
    }

    public static Float2IntMap singleton(float key, int value) {
        return new Singleton(key, value);
    }

    public static Float2IntMap singleton(Float key, Integer value) {
        return new Singleton(key.floatValue(), value);
    }

    public static Float2IntMap synchronize(Float2IntMap m) {
        return new SynchronizedMap(m);
    }

    public static Float2IntMap synchronize(Float2IntMap m, Object sync) {
        return new SynchronizedMap(m, sync);
    }

    public static Float2IntMap unmodifiable(Float2IntMap m) {
        return new UnmodifiableMap(m);
    }

    public static class UnmodifiableMap
    extends Float2IntFunctions.UnmodifiableFunction
    implements Float2IntMap,
    Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final Float2IntMap map;
        protected transient ObjectSet<Float2IntMap.Entry> entries;
        protected transient FloatSet keys;
        protected transient IntCollection values;

        protected UnmodifiableMap(Float2IntMap m) {
            super(m);
            this.map = m;
        }

        @Override
        public boolean containsValue(int v) {
            return this.map.containsValue(v);
        }

        @Deprecated
        @Override
        public boolean containsValue(Object ov) {
            return this.map.containsValue(ov);
        }

        @Override
        public void putAll(Map<? extends Float, ? extends Integer> m) {
            throw new UnsupportedOperationException();
        }

        @Override
        public ObjectSet<Float2IntMap.Entry> float2IntEntrySet() {
            if (this.entries == null) {
                this.entries = ObjectSets.unmodifiable(this.map.float2IntEntrySet());
            }
            return this.entries;
        }

        @Deprecated
        @Override
        public ObjectSet<Map.Entry<Float, Integer>> entrySet() {
            return this.float2IntEntrySet();
        }

        @Override
        public FloatSet keySet() {
            if (this.keys == null) {
                this.keys = FloatSets.unmodifiable(this.map.keySet());
            }
            return this.keys;
        }

        @Override
        public IntCollection values() {
            if (this.values == null) {
                return IntCollections.unmodifiable(this.map.values());
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
        public int getOrDefault(float key, int defaultValue) {
            return this.map.getOrDefault(key, defaultValue);
        }

        @Override
        public void forEach(BiConsumer<? super Float, ? super Integer> action) {
            this.map.forEach(action);
        }

        @Override
        public void replaceAll(BiFunction<? super Float, ? super Integer, ? extends Integer> function) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int putIfAbsent(float key, int value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean remove(float key, int value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int replace(float key, int value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean replace(float key, int oldValue, int newValue) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int computeIfAbsent(float key, DoubleToIntFunction mappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int computeIfAbsentNullable(float key, DoubleFunction<? extends Integer> mappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int computeIfAbsentPartial(float key, Float2IntFunction mappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int computeIfPresent(float key, BiFunction<? super Float, ? super Integer, ? extends Integer> remappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int compute(float key, BiFunction<? super Float, ? super Integer, ? extends Integer> remappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int merge(float key, int value, BiFunction<? super Integer, ? super Integer, ? extends Integer> remappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public Integer getOrDefault(Object key, Integer defaultValue) {
            return this.map.getOrDefault(key, defaultValue);
        }

        @Deprecated
        @Override
        public boolean remove(Object key, Object value) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public Integer replace(Float key, Integer value) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public boolean replace(Float key, Integer oldValue, Integer newValue) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public Integer putIfAbsent(Float key, Integer value) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public Integer computeIfAbsent(Float key, Function<? super Float, ? extends Integer> mappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public Integer computeIfPresent(Float key, BiFunction<? super Float, ? super Integer, ? extends Integer> remappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public Integer compute(Float key, BiFunction<? super Float, ? super Integer, ? extends Integer> remappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public Integer merge(Float key, Integer value, BiFunction<? super Integer, ? super Integer, ? extends Integer> remappingFunction) {
            throw new UnsupportedOperationException();
        }
    }

    public static class SynchronizedMap
    extends Float2IntFunctions.SynchronizedFunction
    implements Float2IntMap,
    Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final Float2IntMap map;
        protected transient ObjectSet<Float2IntMap.Entry> entries;
        protected transient FloatSet keys;
        protected transient IntCollection values;

        protected SynchronizedMap(Float2IntMap m, Object sync) {
            super(m, sync);
            this.map = m;
        }

        protected SynchronizedMap(Float2IntMap m) {
            super(m);
            this.map = m;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean containsValue(int v) {
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
        public void putAll(Map<? extends Float, ? extends Integer> m) {
            Object object = this.sync;
            synchronized (object) {
                this.map.putAll(m);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public ObjectSet<Float2IntMap.Entry> float2IntEntrySet() {
            Object object = this.sync;
            synchronized (object) {
                if (this.entries == null) {
                    this.entries = ObjectSets.synchronize(this.map.float2IntEntrySet(), this.sync);
                }
                return this.entries;
            }
        }

        @Deprecated
        @Override
        public ObjectSet<Map.Entry<Float, Integer>> entrySet() {
            return this.float2IntEntrySet();
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public FloatSet keySet() {
            Object object = this.sync;
            synchronized (object) {
                if (this.keys == null) {
                    this.keys = FloatSets.synchronize(this.map.keySet(), this.sync);
                }
                return this.keys;
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public IntCollection values() {
            Object object = this.sync;
            synchronized (object) {
                if (this.values == null) {
                    return IntCollections.synchronize(this.map.values(), this.sync);
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
        public int getOrDefault(float key, int defaultValue) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.getOrDefault(key, defaultValue);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void forEach(BiConsumer<? super Float, ? super Integer> action) {
            Object object = this.sync;
            synchronized (object) {
                this.map.forEach(action);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void replaceAll(BiFunction<? super Float, ? super Integer, ? extends Integer> function) {
            Object object = this.sync;
            synchronized (object) {
                this.map.replaceAll(function);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public int putIfAbsent(float key, int value) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.putIfAbsent(key, value);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean remove(float key, int value) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.remove(key, value);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public int replace(float key, int value) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.replace(key, value);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean replace(float key, int oldValue, int newValue) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.replace(key, oldValue, newValue);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public int computeIfAbsent(float key, DoubleToIntFunction mappingFunction) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.computeIfAbsent(key, mappingFunction);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public int computeIfAbsentNullable(float key, DoubleFunction<? extends Integer> mappingFunction) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.computeIfAbsentNullable(key, mappingFunction);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public int computeIfAbsentPartial(float key, Float2IntFunction mappingFunction) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.computeIfAbsentPartial(key, mappingFunction);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public int computeIfPresent(float key, BiFunction<? super Float, ? super Integer, ? extends Integer> remappingFunction) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.computeIfPresent(key, remappingFunction);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public int compute(float key, BiFunction<? super Float, ? super Integer, ? extends Integer> remappingFunction) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.compute(key, remappingFunction);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public int merge(float key, int value, BiFunction<? super Integer, ? super Integer, ? extends Integer> remappingFunction) {
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
        public Integer getOrDefault(Object key, Integer defaultValue) {
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
        public Integer replace(Float key, Integer value) {
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
        public boolean replace(Float key, Integer oldValue, Integer newValue) {
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
        public Integer putIfAbsent(Float key, Integer value) {
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
        public Integer computeIfAbsent(Float key, Function<? super Float, ? extends Integer> mappingFunction) {
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
        public Integer computeIfPresent(Float key, BiFunction<? super Float, ? super Integer, ? extends Integer> remappingFunction) {
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
        public Integer compute(Float key, BiFunction<? super Float, ? super Integer, ? extends Integer> remappingFunction) {
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
        public Integer merge(Float key, Integer value, BiFunction<? super Integer, ? super Integer, ? extends Integer> remappingFunction) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.merge(key, value, remappingFunction);
            }
        }
    }

    public static class Singleton
    extends Float2IntFunctions.Singleton
    implements Float2IntMap,
    Serializable,
    Cloneable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected transient ObjectSet<Float2IntMap.Entry> entries;
        protected transient FloatSet keys;
        protected transient IntCollection values;

        protected Singleton(float key, int value) {
            super(key, value);
        }

        @Override
        public boolean containsValue(int v) {
            return this.value == v;
        }

        @Deprecated
        @Override
        public boolean containsValue(Object ov) {
            return (Integer)ov == this.value;
        }

        @Override
        public void putAll(Map<? extends Float, ? extends Integer> m) {
            throw new UnsupportedOperationException();
        }

        @Override
        public ObjectSet<Float2IntMap.Entry> float2IntEntrySet() {
            if (this.entries == null) {
                this.entries = ObjectSets.singleton(new AbstractFloat2IntMap.BasicEntry(this.key, this.value));
            }
            return this.entries;
        }

        @Deprecated
        @Override
        public ObjectSet<Map.Entry<Float, Integer>> entrySet() {
            return this.float2IntEntrySet();
        }

        @Override
        public FloatSet keySet() {
            if (this.keys == null) {
                this.keys = FloatSets.singleton(this.key);
            }
            return this.keys;
        }

        @Override
        public IntCollection values() {
            if (this.values == null) {
                this.values = IntSets.singleton(this.value);
            }
            return this.values;
        }

        @Override
        public boolean isEmpty() {
            return false;
        }

        @Override
        public int hashCode() {
            return HashCommon.float2int(this.key) ^ this.value;
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
    extends Float2IntFunctions.EmptyFunction
    implements Float2IntMap,
    Serializable,
    Cloneable {
        private static final long serialVersionUID = -7046029254386353129L;

        protected EmptyMap() {
        }

        @Override
        public boolean containsValue(int v) {
            return false;
        }

        @Deprecated
        @Override
        public boolean containsValue(Object ov) {
            return false;
        }

        @Override
        public void putAll(Map<? extends Float, ? extends Integer> m) {
            throw new UnsupportedOperationException();
        }

        @Override
        public ObjectSet<Float2IntMap.Entry> float2IntEntrySet() {
            return ObjectSets.EMPTY_SET;
        }

        @Override
        public FloatSet keySet() {
            return FloatSets.EMPTY_SET;
        }

        @Override
        public IntCollection values() {
            return IntSets.EMPTY_SET;
        }

        @Override
        public Object clone() {
            return Float2IntMaps.EMPTY_MAP;
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

