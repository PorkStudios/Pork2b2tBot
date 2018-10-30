/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.doubles;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.doubles.AbstractDouble2ShortMap;
import it.unimi.dsi.fastutil.doubles.Double2ShortFunction;
import it.unimi.dsi.fastutil.doubles.Double2ShortFunctions;
import it.unimi.dsi.fastutil.doubles.Double2ShortMap;
import it.unimi.dsi.fastutil.doubles.DoubleSet;
import it.unimi.dsi.fastutil.doubles.DoubleSets;
import it.unimi.dsi.fastutil.objects.ObjectIterable;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import it.unimi.dsi.fastutil.objects.ObjectSets;
import it.unimi.dsi.fastutil.shorts.ShortCollection;
import it.unimi.dsi.fastutil.shorts.ShortCollections;
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
import java.util.function.DoubleFunction;
import java.util.function.DoubleToIntFunction;
import java.util.function.Function;

public final class Double2ShortMaps {
    public static final EmptyMap EMPTY_MAP = new EmptyMap();

    private Double2ShortMaps() {
    }

    public static ObjectIterator<Double2ShortMap.Entry> fastIterator(Double2ShortMap map) {
        ObjectSet<Double2ShortMap.Entry> entries = map.double2ShortEntrySet();
        return entries instanceof Double2ShortMap.FastEntrySet ? ((Double2ShortMap.FastEntrySet)entries).fastIterator() : entries.iterator();
    }

    public static void fastForEach(Double2ShortMap map, Consumer<? super Double2ShortMap.Entry> consumer) {
        ObjectSet<Double2ShortMap.Entry> entries = map.double2ShortEntrySet();
        if (entries instanceof Double2ShortMap.FastEntrySet) {
            ((Double2ShortMap.FastEntrySet)entries).fastForEach(consumer);
        } else {
            entries.forEach(consumer);
        }
    }

    public static ObjectIterable<Double2ShortMap.Entry> fastIterable(Double2ShortMap map) {
        final ObjectSet<Double2ShortMap.Entry> entries = map.double2ShortEntrySet();
        return entries instanceof Double2ShortMap.FastEntrySet ? new ObjectIterable<Double2ShortMap.Entry>(){

            @Override
            public ObjectIterator<Double2ShortMap.Entry> iterator() {
                return ((Double2ShortMap.FastEntrySet)entries).fastIterator();
            }

            @Override
            public void forEach(Consumer<? super Double2ShortMap.Entry> consumer) {
                ((Double2ShortMap.FastEntrySet)entries).fastForEach(consumer);
            }
        } : entries;
    }

    public static Double2ShortMap singleton(double key, short value) {
        return new Singleton(key, value);
    }

    public static Double2ShortMap singleton(Double key, Short value) {
        return new Singleton(key, value);
    }

    public static Double2ShortMap synchronize(Double2ShortMap m) {
        return new SynchronizedMap(m);
    }

    public static Double2ShortMap synchronize(Double2ShortMap m, Object sync) {
        return new SynchronizedMap(m, sync);
    }

    public static Double2ShortMap unmodifiable(Double2ShortMap m) {
        return new UnmodifiableMap(m);
    }

    public static class UnmodifiableMap
    extends Double2ShortFunctions.UnmodifiableFunction
    implements Double2ShortMap,
    Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final Double2ShortMap map;
        protected transient ObjectSet<Double2ShortMap.Entry> entries;
        protected transient DoubleSet keys;
        protected transient ShortCollection values;

        protected UnmodifiableMap(Double2ShortMap m) {
            super(m);
            this.map = m;
        }

        @Override
        public boolean containsValue(short v) {
            return this.map.containsValue(v);
        }

        @Deprecated
        @Override
        public boolean containsValue(Object ov) {
            return this.map.containsValue(ov);
        }

        @Override
        public void putAll(Map<? extends Double, ? extends Short> m) {
            throw new UnsupportedOperationException();
        }

        @Override
        public ObjectSet<Double2ShortMap.Entry> double2ShortEntrySet() {
            if (this.entries == null) {
                this.entries = ObjectSets.unmodifiable(this.map.double2ShortEntrySet());
            }
            return this.entries;
        }

        @Deprecated
        @Override
        public ObjectSet<Map.Entry<Double, Short>> entrySet() {
            return this.double2ShortEntrySet();
        }

        @Override
        public DoubleSet keySet() {
            if (this.keys == null) {
                this.keys = DoubleSets.unmodifiable(this.map.keySet());
            }
            return this.keys;
        }

        @Override
        public ShortCollection values() {
            if (this.values == null) {
                return ShortCollections.unmodifiable(this.map.values());
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
        public short getOrDefault(double key, short defaultValue) {
            return this.map.getOrDefault(key, defaultValue);
        }

        @Override
        public void forEach(BiConsumer<? super Double, ? super Short> action) {
            this.map.forEach(action);
        }

        @Override
        public void replaceAll(BiFunction<? super Double, ? super Short, ? extends Short> function) {
            throw new UnsupportedOperationException();
        }

        @Override
        public short putIfAbsent(double key, short value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean remove(double key, short value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public short replace(double key, short value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean replace(double key, short oldValue, short newValue) {
            throw new UnsupportedOperationException();
        }

        @Override
        public short computeIfAbsent(double key, DoubleToIntFunction mappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Override
        public short computeIfAbsentNullable(double key, DoubleFunction<? extends Short> mappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Override
        public short computeIfAbsentPartial(double key, Double2ShortFunction mappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Override
        public short computeIfPresent(double key, BiFunction<? super Double, ? super Short, ? extends Short> remappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Override
        public short compute(double key, BiFunction<? super Double, ? super Short, ? extends Short> remappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Override
        public short merge(double key, short value, BiFunction<? super Short, ? super Short, ? extends Short> remappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public Short getOrDefault(Object key, Short defaultValue) {
            return this.map.getOrDefault(key, defaultValue);
        }

        @Deprecated
        @Override
        public boolean remove(Object key, Object value) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public Short replace(Double key, Short value) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public boolean replace(Double key, Short oldValue, Short newValue) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public Short putIfAbsent(Double key, Short value) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public Short computeIfAbsent(Double key, Function<? super Double, ? extends Short> mappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public Short computeIfPresent(Double key, BiFunction<? super Double, ? super Short, ? extends Short> remappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public Short compute(Double key, BiFunction<? super Double, ? super Short, ? extends Short> remappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public Short merge(Double key, Short value, BiFunction<? super Short, ? super Short, ? extends Short> remappingFunction) {
            throw new UnsupportedOperationException();
        }
    }

    public static class SynchronizedMap
    extends Double2ShortFunctions.SynchronizedFunction
    implements Double2ShortMap,
    Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final Double2ShortMap map;
        protected transient ObjectSet<Double2ShortMap.Entry> entries;
        protected transient DoubleSet keys;
        protected transient ShortCollection values;

        protected SynchronizedMap(Double2ShortMap m, Object sync) {
            super(m, sync);
            this.map = m;
        }

        protected SynchronizedMap(Double2ShortMap m) {
            super(m);
            this.map = m;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean containsValue(short v) {
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
        public void putAll(Map<? extends Double, ? extends Short> m) {
            Object object = this.sync;
            synchronized (object) {
                this.map.putAll(m);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public ObjectSet<Double2ShortMap.Entry> double2ShortEntrySet() {
            Object object = this.sync;
            synchronized (object) {
                if (this.entries == null) {
                    this.entries = ObjectSets.synchronize(this.map.double2ShortEntrySet(), this.sync);
                }
                return this.entries;
            }
        }

        @Deprecated
        @Override
        public ObjectSet<Map.Entry<Double, Short>> entrySet() {
            return this.double2ShortEntrySet();
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
        public ShortCollection values() {
            Object object = this.sync;
            synchronized (object) {
                if (this.values == null) {
                    return ShortCollections.synchronize(this.map.values(), this.sync);
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
        public short getOrDefault(double key, short defaultValue) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.getOrDefault(key, defaultValue);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void forEach(BiConsumer<? super Double, ? super Short> action) {
            Object object = this.sync;
            synchronized (object) {
                this.map.forEach(action);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void replaceAll(BiFunction<? super Double, ? super Short, ? extends Short> function) {
            Object object = this.sync;
            synchronized (object) {
                this.map.replaceAll(function);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public short putIfAbsent(double key, short value) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.putIfAbsent(key, value);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean remove(double key, short value) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.remove(key, value);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public short replace(double key, short value) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.replace(key, value);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean replace(double key, short oldValue, short newValue) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.replace(key, oldValue, newValue);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public short computeIfAbsent(double key, DoubleToIntFunction mappingFunction) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.computeIfAbsent(key, mappingFunction);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public short computeIfAbsentNullable(double key, DoubleFunction<? extends Short> mappingFunction) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.computeIfAbsentNullable(key, mappingFunction);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public short computeIfAbsentPartial(double key, Double2ShortFunction mappingFunction) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.computeIfAbsentPartial(key, mappingFunction);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public short computeIfPresent(double key, BiFunction<? super Double, ? super Short, ? extends Short> remappingFunction) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.computeIfPresent(key, remappingFunction);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public short compute(double key, BiFunction<? super Double, ? super Short, ? extends Short> remappingFunction) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.compute(key, remappingFunction);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public short merge(double key, short value, BiFunction<? super Short, ? super Short, ? extends Short> remappingFunction) {
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
        public Short getOrDefault(Object key, Short defaultValue) {
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
        public Short replace(Double key, Short value) {
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
        public boolean replace(Double key, Short oldValue, Short newValue) {
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
        public Short putIfAbsent(Double key, Short value) {
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
        public Short computeIfAbsent(Double key, Function<? super Double, ? extends Short> mappingFunction) {
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
        public Short computeIfPresent(Double key, BiFunction<? super Double, ? super Short, ? extends Short> remappingFunction) {
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
        public Short compute(Double key, BiFunction<? super Double, ? super Short, ? extends Short> remappingFunction) {
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
        public Short merge(Double key, Short value, BiFunction<? super Short, ? super Short, ? extends Short> remappingFunction) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.merge(key, value, remappingFunction);
            }
        }
    }

    public static class Singleton
    extends Double2ShortFunctions.Singleton
    implements Double2ShortMap,
    Serializable,
    Cloneable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected transient ObjectSet<Double2ShortMap.Entry> entries;
        protected transient DoubleSet keys;
        protected transient ShortCollection values;

        protected Singleton(double key, short value) {
            super(key, value);
        }

        @Override
        public boolean containsValue(short v) {
            return this.value == v;
        }

        @Deprecated
        @Override
        public boolean containsValue(Object ov) {
            return (Short)ov == this.value;
        }

        @Override
        public void putAll(Map<? extends Double, ? extends Short> m) {
            throw new UnsupportedOperationException();
        }

        @Override
        public ObjectSet<Double2ShortMap.Entry> double2ShortEntrySet() {
            if (this.entries == null) {
                this.entries = ObjectSets.singleton(new AbstractDouble2ShortMap.BasicEntry(this.key, this.value));
            }
            return this.entries;
        }

        @Deprecated
        @Override
        public ObjectSet<Map.Entry<Double, Short>> entrySet() {
            return this.double2ShortEntrySet();
        }

        @Override
        public DoubleSet keySet() {
            if (this.keys == null) {
                this.keys = DoubleSets.singleton(this.key);
            }
            return this.keys;
        }

        @Override
        public ShortCollection values() {
            if (this.values == null) {
                this.values = ShortSets.singleton(this.value);
            }
            return this.values;
        }

        @Override
        public boolean isEmpty() {
            return false;
        }

        @Override
        public int hashCode() {
            return HashCommon.double2int(this.key) ^ this.value;
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
    extends Double2ShortFunctions.EmptyFunction
    implements Double2ShortMap,
    Serializable,
    Cloneable {
        private static final long serialVersionUID = -7046029254386353129L;

        protected EmptyMap() {
        }

        @Override
        public boolean containsValue(short v) {
            return false;
        }

        @Deprecated
        @Override
        public boolean containsValue(Object ov) {
            return false;
        }

        @Override
        public void putAll(Map<? extends Double, ? extends Short> m) {
            throw new UnsupportedOperationException();
        }

        @Override
        public ObjectSet<Double2ShortMap.Entry> double2ShortEntrySet() {
            return ObjectSets.EMPTY_SET;
        }

        @Override
        public DoubleSet keySet() {
            return DoubleSets.EMPTY_SET;
        }

        @Override
        public ShortCollection values() {
            return ShortSets.EMPTY_SET;
        }

        @Override
        public Object clone() {
            return Double2ShortMaps.EMPTY_MAP;
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

