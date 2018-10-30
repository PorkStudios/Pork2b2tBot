/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.doubles;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.doubles.AbstractDouble2FloatMap;
import it.unimi.dsi.fastutil.doubles.Double2FloatFunction;
import it.unimi.dsi.fastutil.doubles.Double2FloatFunctions;
import it.unimi.dsi.fastutil.doubles.Double2FloatMap;
import it.unimi.dsi.fastutil.doubles.DoubleSet;
import it.unimi.dsi.fastutil.doubles.DoubleSets;
import it.unimi.dsi.fastutil.floats.FloatCollection;
import it.unimi.dsi.fastutil.floats.FloatCollections;
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

public final class Double2FloatMaps {
    public static final EmptyMap EMPTY_MAP = new EmptyMap();

    private Double2FloatMaps() {
    }

    public static ObjectIterator<Double2FloatMap.Entry> fastIterator(Double2FloatMap map) {
        ObjectSet<Double2FloatMap.Entry> entries = map.double2FloatEntrySet();
        return entries instanceof Double2FloatMap.FastEntrySet ? ((Double2FloatMap.FastEntrySet)entries).fastIterator() : entries.iterator();
    }

    public static void fastForEach(Double2FloatMap map, Consumer<? super Double2FloatMap.Entry> consumer) {
        ObjectSet<Double2FloatMap.Entry> entries = map.double2FloatEntrySet();
        if (entries instanceof Double2FloatMap.FastEntrySet) {
            ((Double2FloatMap.FastEntrySet)entries).fastForEach(consumer);
        } else {
            entries.forEach(consumer);
        }
    }

    public static ObjectIterable<Double2FloatMap.Entry> fastIterable(Double2FloatMap map) {
        final ObjectSet<Double2FloatMap.Entry> entries = map.double2FloatEntrySet();
        return entries instanceof Double2FloatMap.FastEntrySet ? new ObjectIterable<Double2FloatMap.Entry>(){

            @Override
            public ObjectIterator<Double2FloatMap.Entry> iterator() {
                return ((Double2FloatMap.FastEntrySet)entries).fastIterator();
            }

            @Override
            public void forEach(Consumer<? super Double2FloatMap.Entry> consumer) {
                ((Double2FloatMap.FastEntrySet)entries).fastForEach(consumer);
            }
        } : entries;
    }

    public static Double2FloatMap singleton(double key, float value) {
        return new Singleton(key, value);
    }

    public static Double2FloatMap singleton(Double key, Float value) {
        return new Singleton(key, value.floatValue());
    }

    public static Double2FloatMap synchronize(Double2FloatMap m) {
        return new SynchronizedMap(m);
    }

    public static Double2FloatMap synchronize(Double2FloatMap m, Object sync) {
        return new SynchronizedMap(m, sync);
    }

    public static Double2FloatMap unmodifiable(Double2FloatMap m) {
        return new UnmodifiableMap(m);
    }

    public static class UnmodifiableMap
    extends Double2FloatFunctions.UnmodifiableFunction
    implements Double2FloatMap,
    Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final Double2FloatMap map;
        protected transient ObjectSet<Double2FloatMap.Entry> entries;
        protected transient DoubleSet keys;
        protected transient FloatCollection values;

        protected UnmodifiableMap(Double2FloatMap m) {
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
        public void putAll(Map<? extends Double, ? extends Float> m) {
            throw new UnsupportedOperationException();
        }

        @Override
        public ObjectSet<Double2FloatMap.Entry> double2FloatEntrySet() {
            if (this.entries == null) {
                this.entries = ObjectSets.unmodifiable(this.map.double2FloatEntrySet());
            }
            return this.entries;
        }

        @Deprecated
        @Override
        public ObjectSet<Map.Entry<Double, Float>> entrySet() {
            return this.double2FloatEntrySet();
        }

        @Override
        public DoubleSet keySet() {
            if (this.keys == null) {
                this.keys = DoubleSets.unmodifiable(this.map.keySet());
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
        public float getOrDefault(double key, float defaultValue) {
            return this.map.getOrDefault(key, defaultValue);
        }

        @Override
        public void forEach(BiConsumer<? super Double, ? super Float> action) {
            this.map.forEach(action);
        }

        @Override
        public void replaceAll(BiFunction<? super Double, ? super Float, ? extends Float> function) {
            throw new UnsupportedOperationException();
        }

        @Override
        public float putIfAbsent(double key, float value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean remove(double key, float value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public float replace(double key, float value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean replace(double key, float oldValue, float newValue) {
            throw new UnsupportedOperationException();
        }

        @Override
        public float computeIfAbsent(double key, DoubleUnaryOperator mappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Override
        public float computeIfAbsentNullable(double key, DoubleFunction<? extends Float> mappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Override
        public float computeIfAbsentPartial(double key, Double2FloatFunction mappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Override
        public float computeIfPresent(double key, BiFunction<? super Double, ? super Float, ? extends Float> remappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Override
        public float compute(double key, BiFunction<? super Double, ? super Float, ? extends Float> remappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Override
        public float merge(double key, float value, BiFunction<? super Float, ? super Float, ? extends Float> remappingFunction) {
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
        public Float replace(Double key, Float value) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public boolean replace(Double key, Float oldValue, Float newValue) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public Float putIfAbsent(Double key, Float value) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public Float computeIfAbsent(Double key, Function<? super Double, ? extends Float> mappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public Float computeIfPresent(Double key, BiFunction<? super Double, ? super Float, ? extends Float> remappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public Float compute(Double key, BiFunction<? super Double, ? super Float, ? extends Float> remappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public Float merge(Double key, Float value, BiFunction<? super Float, ? super Float, ? extends Float> remappingFunction) {
            throw new UnsupportedOperationException();
        }
    }

    public static class SynchronizedMap
    extends Double2FloatFunctions.SynchronizedFunction
    implements Double2FloatMap,
    Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final Double2FloatMap map;
        protected transient ObjectSet<Double2FloatMap.Entry> entries;
        protected transient DoubleSet keys;
        protected transient FloatCollection values;

        protected SynchronizedMap(Double2FloatMap m, Object sync) {
            super(m, sync);
            this.map = m;
        }

        protected SynchronizedMap(Double2FloatMap m) {
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
        public void putAll(Map<? extends Double, ? extends Float> m) {
            Object object = this.sync;
            synchronized (object) {
                this.map.putAll(m);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public ObjectSet<Double2FloatMap.Entry> double2FloatEntrySet() {
            Object object = this.sync;
            synchronized (object) {
                if (this.entries == null) {
                    this.entries = ObjectSets.synchronize(this.map.double2FloatEntrySet(), this.sync);
                }
                return this.entries;
            }
        }

        @Deprecated
        @Override
        public ObjectSet<Map.Entry<Double, Float>> entrySet() {
            return this.double2FloatEntrySet();
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
        public float getOrDefault(double key, float defaultValue) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.getOrDefault(key, defaultValue);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void forEach(BiConsumer<? super Double, ? super Float> action) {
            Object object = this.sync;
            synchronized (object) {
                this.map.forEach(action);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void replaceAll(BiFunction<? super Double, ? super Float, ? extends Float> function) {
            Object object = this.sync;
            synchronized (object) {
                this.map.replaceAll(function);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public float putIfAbsent(double key, float value) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.putIfAbsent(key, value);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean remove(double key, float value) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.remove(key, value);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public float replace(double key, float value) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.replace(key, value);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean replace(double key, float oldValue, float newValue) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.replace(key, oldValue, newValue);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public float computeIfAbsent(double key, DoubleUnaryOperator mappingFunction) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.computeIfAbsent(key, mappingFunction);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public float computeIfAbsentNullable(double key, DoubleFunction<? extends Float> mappingFunction) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.computeIfAbsentNullable(key, mappingFunction);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public float computeIfAbsentPartial(double key, Double2FloatFunction mappingFunction) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.computeIfAbsentPartial(key, mappingFunction);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public float computeIfPresent(double key, BiFunction<? super Double, ? super Float, ? extends Float> remappingFunction) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.computeIfPresent(key, remappingFunction);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public float compute(double key, BiFunction<? super Double, ? super Float, ? extends Float> remappingFunction) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.compute(key, remappingFunction);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public float merge(double key, float value, BiFunction<? super Float, ? super Float, ? extends Float> remappingFunction) {
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
        public Float replace(Double key, Float value) {
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
        public boolean replace(Double key, Float oldValue, Float newValue) {
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
        public Float putIfAbsent(Double key, Float value) {
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
        public Float computeIfAbsent(Double key, Function<? super Double, ? extends Float> mappingFunction) {
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
        public Float computeIfPresent(Double key, BiFunction<? super Double, ? super Float, ? extends Float> remappingFunction) {
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
        public Float compute(Double key, BiFunction<? super Double, ? super Float, ? extends Float> remappingFunction) {
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
        public Float merge(Double key, Float value, BiFunction<? super Float, ? super Float, ? extends Float> remappingFunction) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.merge(key, value, remappingFunction);
            }
        }
    }

    public static class Singleton
    extends Double2FloatFunctions.Singleton
    implements Double2FloatMap,
    Serializable,
    Cloneable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected transient ObjectSet<Double2FloatMap.Entry> entries;
        protected transient DoubleSet keys;
        protected transient FloatCollection values;

        protected Singleton(double key, float value) {
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
        public void putAll(Map<? extends Double, ? extends Float> m) {
            throw new UnsupportedOperationException();
        }

        @Override
        public ObjectSet<Double2FloatMap.Entry> double2FloatEntrySet() {
            if (this.entries == null) {
                this.entries = ObjectSets.singleton(new AbstractDouble2FloatMap.BasicEntry(this.key, this.value));
            }
            return this.entries;
        }

        @Deprecated
        @Override
        public ObjectSet<Map.Entry<Double, Float>> entrySet() {
            return this.double2FloatEntrySet();
        }

        @Override
        public DoubleSet keySet() {
            if (this.keys == null) {
                this.keys = DoubleSets.singleton(this.key);
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
            return HashCommon.double2int(this.key) ^ HashCommon.float2int(this.value);
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
    extends Double2FloatFunctions.EmptyFunction
    implements Double2FloatMap,
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
        public void putAll(Map<? extends Double, ? extends Float> m) {
            throw new UnsupportedOperationException();
        }

        @Override
        public ObjectSet<Double2FloatMap.Entry> double2FloatEntrySet() {
            return ObjectSets.EMPTY_SET;
        }

        @Override
        public DoubleSet keySet() {
            return DoubleSets.EMPTY_SET;
        }

        @Override
        public FloatCollection values() {
            return FloatSets.EMPTY_SET;
        }

        @Override
        public Object clone() {
            return Double2FloatMaps.EMPTY_MAP;
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

