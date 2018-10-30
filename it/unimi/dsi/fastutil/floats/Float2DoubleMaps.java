/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.doubles.DoubleCollection;
import it.unimi.dsi.fastutil.doubles.DoubleCollections;
import it.unimi.dsi.fastutil.doubles.DoubleSets;
import it.unimi.dsi.fastutil.floats.AbstractFloat2DoubleMap;
import it.unimi.dsi.fastutil.floats.Float2DoubleFunction;
import it.unimi.dsi.fastutil.floats.Float2DoubleFunctions;
import it.unimi.dsi.fastutil.floats.Float2DoubleMap;
import it.unimi.dsi.fastutil.floats.FloatSet;
import it.unimi.dsi.fastutil.floats.FloatSets;
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
import java.util.function.DoubleUnaryOperator;
import java.util.function.Function;

public final class Float2DoubleMaps {
    public static final EmptyMap EMPTY_MAP = new EmptyMap();

    private Float2DoubleMaps() {
    }

    public static ObjectIterator<Float2DoubleMap.Entry> fastIterator(Float2DoubleMap map) {
        ObjectSet<Float2DoubleMap.Entry> entries = map.float2DoubleEntrySet();
        return entries instanceof Float2DoubleMap.FastEntrySet ? ((Float2DoubleMap.FastEntrySet)entries).fastIterator() : entries.iterator();
    }

    public static void fastForEach(Float2DoubleMap map, Consumer<? super Float2DoubleMap.Entry> consumer) {
        ObjectSet<Float2DoubleMap.Entry> entries = map.float2DoubleEntrySet();
        if (entries instanceof Float2DoubleMap.FastEntrySet) {
            ((Float2DoubleMap.FastEntrySet)entries).fastForEach(consumer);
        } else {
            entries.forEach(consumer);
        }
    }

    public static ObjectIterable<Float2DoubleMap.Entry> fastIterable(Float2DoubleMap map) {
        final ObjectSet<Float2DoubleMap.Entry> entries = map.float2DoubleEntrySet();
        return entries instanceof Float2DoubleMap.FastEntrySet ? new ObjectIterable<Float2DoubleMap.Entry>(){

            @Override
            public ObjectIterator<Float2DoubleMap.Entry> iterator() {
                return ((Float2DoubleMap.FastEntrySet)entries).fastIterator();
            }

            @Override
            public void forEach(Consumer<? super Float2DoubleMap.Entry> consumer) {
                ((Float2DoubleMap.FastEntrySet)entries).fastForEach(consumer);
            }
        } : entries;
    }

    public static Float2DoubleMap singleton(float key, double value) {
        return new Singleton(key, value);
    }

    public static Float2DoubleMap singleton(Float key, Double value) {
        return new Singleton(key.floatValue(), value);
    }

    public static Float2DoubleMap synchronize(Float2DoubleMap m) {
        return new SynchronizedMap(m);
    }

    public static Float2DoubleMap synchronize(Float2DoubleMap m, Object sync) {
        return new SynchronizedMap(m, sync);
    }

    public static Float2DoubleMap unmodifiable(Float2DoubleMap m) {
        return new UnmodifiableMap(m);
    }

    public static class UnmodifiableMap
    extends Float2DoubleFunctions.UnmodifiableFunction
    implements Float2DoubleMap,
    Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final Float2DoubleMap map;
        protected transient ObjectSet<Float2DoubleMap.Entry> entries;
        protected transient FloatSet keys;
        protected transient DoubleCollection values;

        protected UnmodifiableMap(Float2DoubleMap m) {
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
        public void putAll(Map<? extends Float, ? extends Double> m) {
            throw new UnsupportedOperationException();
        }

        @Override
        public ObjectSet<Float2DoubleMap.Entry> float2DoubleEntrySet() {
            if (this.entries == null) {
                this.entries = ObjectSets.unmodifiable(this.map.float2DoubleEntrySet());
            }
            return this.entries;
        }

        @Deprecated
        @Override
        public ObjectSet<Map.Entry<Float, Double>> entrySet() {
            return this.float2DoubleEntrySet();
        }

        @Override
        public FloatSet keySet() {
            if (this.keys == null) {
                this.keys = FloatSets.unmodifiable(this.map.keySet());
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
        public double getOrDefault(float key, double defaultValue) {
            return this.map.getOrDefault(key, defaultValue);
        }

        @Override
        public void forEach(BiConsumer<? super Float, ? super Double> action) {
            this.map.forEach(action);
        }

        @Override
        public void replaceAll(BiFunction<? super Float, ? super Double, ? extends Double> function) {
            throw new UnsupportedOperationException();
        }

        @Override
        public double putIfAbsent(float key, double value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean remove(float key, double value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public double replace(float key, double value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean replace(float key, double oldValue, double newValue) {
            throw new UnsupportedOperationException();
        }

        @Override
        public double computeIfAbsent(float key, DoubleUnaryOperator mappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Override
        public double computeIfAbsentNullable(float key, DoubleFunction<? extends Double> mappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Override
        public double computeIfAbsentPartial(float key, Float2DoubleFunction mappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Override
        public double computeIfPresent(float key, BiFunction<? super Float, ? super Double, ? extends Double> remappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Override
        public double compute(float key, BiFunction<? super Float, ? super Double, ? extends Double> remappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Override
        public double merge(float key, double value, BiFunction<? super Double, ? super Double, ? extends Double> remappingFunction) {
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
        public Double replace(Float key, Double value) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public boolean replace(Float key, Double oldValue, Double newValue) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public Double putIfAbsent(Float key, Double value) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public Double computeIfAbsent(Float key, Function<? super Float, ? extends Double> mappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public Double computeIfPresent(Float key, BiFunction<? super Float, ? super Double, ? extends Double> remappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public Double compute(Float key, BiFunction<? super Float, ? super Double, ? extends Double> remappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public Double merge(Float key, Double value, BiFunction<? super Double, ? super Double, ? extends Double> remappingFunction) {
            throw new UnsupportedOperationException();
        }
    }

    public static class SynchronizedMap
    extends Float2DoubleFunctions.SynchronizedFunction
    implements Float2DoubleMap,
    Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final Float2DoubleMap map;
        protected transient ObjectSet<Float2DoubleMap.Entry> entries;
        protected transient FloatSet keys;
        protected transient DoubleCollection values;

        protected SynchronizedMap(Float2DoubleMap m, Object sync) {
            super(m, sync);
            this.map = m;
        }

        protected SynchronizedMap(Float2DoubleMap m) {
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
        public void putAll(Map<? extends Float, ? extends Double> m) {
            Object object = this.sync;
            synchronized (object) {
                this.map.putAll(m);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public ObjectSet<Float2DoubleMap.Entry> float2DoubleEntrySet() {
            Object object = this.sync;
            synchronized (object) {
                if (this.entries == null) {
                    this.entries = ObjectSets.synchronize(this.map.float2DoubleEntrySet(), this.sync);
                }
                return this.entries;
            }
        }

        @Deprecated
        @Override
        public ObjectSet<Map.Entry<Float, Double>> entrySet() {
            return this.float2DoubleEntrySet();
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
        public double getOrDefault(float key, double defaultValue) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.getOrDefault(key, defaultValue);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void forEach(BiConsumer<? super Float, ? super Double> action) {
            Object object = this.sync;
            synchronized (object) {
                this.map.forEach(action);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void replaceAll(BiFunction<? super Float, ? super Double, ? extends Double> function) {
            Object object = this.sync;
            synchronized (object) {
                this.map.replaceAll(function);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public double putIfAbsent(float key, double value) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.putIfAbsent(key, value);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean remove(float key, double value) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.remove(key, value);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public double replace(float key, double value) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.replace(key, value);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean replace(float key, double oldValue, double newValue) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.replace(key, oldValue, newValue);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public double computeIfAbsent(float key, DoubleUnaryOperator mappingFunction) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.computeIfAbsent(key, mappingFunction);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public double computeIfAbsentNullable(float key, DoubleFunction<? extends Double> mappingFunction) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.computeIfAbsentNullable(key, mappingFunction);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public double computeIfAbsentPartial(float key, Float2DoubleFunction mappingFunction) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.computeIfAbsentPartial(key, mappingFunction);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public double computeIfPresent(float key, BiFunction<? super Float, ? super Double, ? extends Double> remappingFunction) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.computeIfPresent(key, remappingFunction);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public double compute(float key, BiFunction<? super Float, ? super Double, ? extends Double> remappingFunction) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.compute(key, remappingFunction);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public double merge(float key, double value, BiFunction<? super Double, ? super Double, ? extends Double> remappingFunction) {
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
        public Double replace(Float key, Double value) {
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
        public boolean replace(Float key, Double oldValue, Double newValue) {
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
        public Double putIfAbsent(Float key, Double value) {
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
        public Double computeIfAbsent(Float key, Function<? super Float, ? extends Double> mappingFunction) {
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
        public Double computeIfPresent(Float key, BiFunction<? super Float, ? super Double, ? extends Double> remappingFunction) {
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
        public Double compute(Float key, BiFunction<? super Float, ? super Double, ? extends Double> remappingFunction) {
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
        public Double merge(Float key, Double value, BiFunction<? super Double, ? super Double, ? extends Double> remappingFunction) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.merge(key, value, remappingFunction);
            }
        }
    }

    public static class Singleton
    extends Float2DoubleFunctions.Singleton
    implements Float2DoubleMap,
    Serializable,
    Cloneable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected transient ObjectSet<Float2DoubleMap.Entry> entries;
        protected transient FloatSet keys;
        protected transient DoubleCollection values;

        protected Singleton(float key, double value) {
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
        public void putAll(Map<? extends Float, ? extends Double> m) {
            throw new UnsupportedOperationException();
        }

        @Override
        public ObjectSet<Float2DoubleMap.Entry> float2DoubleEntrySet() {
            if (this.entries == null) {
                this.entries = ObjectSets.singleton(new AbstractFloat2DoubleMap.BasicEntry(this.key, this.value));
            }
            return this.entries;
        }

        @Deprecated
        @Override
        public ObjectSet<Map.Entry<Float, Double>> entrySet() {
            return this.float2DoubleEntrySet();
        }

        @Override
        public FloatSet keySet() {
            if (this.keys == null) {
                this.keys = FloatSets.singleton(this.key);
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
            return HashCommon.float2int(this.key) ^ HashCommon.double2int(this.value);
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
    extends Float2DoubleFunctions.EmptyFunction
    implements Float2DoubleMap,
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
        public void putAll(Map<? extends Float, ? extends Double> m) {
            throw new UnsupportedOperationException();
        }

        @Override
        public ObjectSet<Float2DoubleMap.Entry> float2DoubleEntrySet() {
            return ObjectSets.EMPTY_SET;
        }

        @Override
        public FloatSet keySet() {
            return FloatSets.EMPTY_SET;
        }

        @Override
        public DoubleCollection values() {
            return DoubleSets.EMPTY_SET;
        }

        @Override
        public Object clone() {
            return Float2DoubleMaps.EMPTY_MAP;
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

