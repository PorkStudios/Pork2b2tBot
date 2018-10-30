/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.shorts;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.doubles.DoubleCollection;
import it.unimi.dsi.fastutil.doubles.DoubleCollections;
import it.unimi.dsi.fastutil.doubles.DoubleSets;
import it.unimi.dsi.fastutil.objects.ObjectIterable;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import it.unimi.dsi.fastutil.objects.ObjectSets;
import it.unimi.dsi.fastutil.shorts.AbstractShort2DoubleMap;
import it.unimi.dsi.fastutil.shorts.Short2DoubleFunction;
import it.unimi.dsi.fastutil.shorts.Short2DoubleFunctions;
import it.unimi.dsi.fastutil.shorts.Short2DoubleMap;
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
import java.util.function.IntToDoubleFunction;

public final class Short2DoubleMaps {
    public static final EmptyMap EMPTY_MAP = new EmptyMap();

    private Short2DoubleMaps() {
    }

    public static ObjectIterator<Short2DoubleMap.Entry> fastIterator(Short2DoubleMap map) {
        ObjectSet<Short2DoubleMap.Entry> entries = map.short2DoubleEntrySet();
        return entries instanceof Short2DoubleMap.FastEntrySet ? ((Short2DoubleMap.FastEntrySet)entries).fastIterator() : entries.iterator();
    }

    public static void fastForEach(Short2DoubleMap map, Consumer<? super Short2DoubleMap.Entry> consumer) {
        ObjectSet<Short2DoubleMap.Entry> entries = map.short2DoubleEntrySet();
        if (entries instanceof Short2DoubleMap.FastEntrySet) {
            ((Short2DoubleMap.FastEntrySet)entries).fastForEach(consumer);
        } else {
            entries.forEach(consumer);
        }
    }

    public static ObjectIterable<Short2DoubleMap.Entry> fastIterable(Short2DoubleMap map) {
        final ObjectSet<Short2DoubleMap.Entry> entries = map.short2DoubleEntrySet();
        return entries instanceof Short2DoubleMap.FastEntrySet ? new ObjectIterable<Short2DoubleMap.Entry>(){

            @Override
            public ObjectIterator<Short2DoubleMap.Entry> iterator() {
                return ((Short2DoubleMap.FastEntrySet)entries).fastIterator();
            }

            @Override
            public void forEach(Consumer<? super Short2DoubleMap.Entry> consumer) {
                ((Short2DoubleMap.FastEntrySet)entries).fastForEach(consumer);
            }
        } : entries;
    }

    public static Short2DoubleMap singleton(short key, double value) {
        return new Singleton(key, value);
    }

    public static Short2DoubleMap singleton(Short key, Double value) {
        return new Singleton(key, value);
    }

    public static Short2DoubleMap synchronize(Short2DoubleMap m) {
        return new SynchronizedMap(m);
    }

    public static Short2DoubleMap synchronize(Short2DoubleMap m, Object sync) {
        return new SynchronizedMap(m, sync);
    }

    public static Short2DoubleMap unmodifiable(Short2DoubleMap m) {
        return new UnmodifiableMap(m);
    }

    public static class UnmodifiableMap
    extends Short2DoubleFunctions.UnmodifiableFunction
    implements Short2DoubleMap,
    Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final Short2DoubleMap map;
        protected transient ObjectSet<Short2DoubleMap.Entry> entries;
        protected transient ShortSet keys;
        protected transient DoubleCollection values;

        protected UnmodifiableMap(Short2DoubleMap m) {
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
        public void putAll(Map<? extends Short, ? extends Double> m) {
            throw new UnsupportedOperationException();
        }

        @Override
        public ObjectSet<Short2DoubleMap.Entry> short2DoubleEntrySet() {
            if (this.entries == null) {
                this.entries = ObjectSets.unmodifiable(this.map.short2DoubleEntrySet());
            }
            return this.entries;
        }

        @Deprecated
        @Override
        public ObjectSet<Map.Entry<Short, Double>> entrySet() {
            return this.short2DoubleEntrySet();
        }

        @Override
        public ShortSet keySet() {
            if (this.keys == null) {
                this.keys = ShortSets.unmodifiable(this.map.keySet());
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
        public double getOrDefault(short key, double defaultValue) {
            return this.map.getOrDefault(key, defaultValue);
        }

        @Override
        public void forEach(BiConsumer<? super Short, ? super Double> action) {
            this.map.forEach(action);
        }

        @Override
        public void replaceAll(BiFunction<? super Short, ? super Double, ? extends Double> function) {
            throw new UnsupportedOperationException();
        }

        @Override
        public double putIfAbsent(short key, double value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean remove(short key, double value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public double replace(short key, double value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean replace(short key, double oldValue, double newValue) {
            throw new UnsupportedOperationException();
        }

        @Override
        public double computeIfAbsent(short key, IntToDoubleFunction mappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Override
        public double computeIfAbsentNullable(short key, IntFunction<? extends Double> mappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Override
        public double computeIfAbsentPartial(short key, Short2DoubleFunction mappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Override
        public double computeIfPresent(short key, BiFunction<? super Short, ? super Double, ? extends Double> remappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Override
        public double compute(short key, BiFunction<? super Short, ? super Double, ? extends Double> remappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Override
        public double merge(short key, double value, BiFunction<? super Double, ? super Double, ? extends Double> remappingFunction) {
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
        public Double replace(Short key, Double value) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public boolean replace(Short key, Double oldValue, Double newValue) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public Double putIfAbsent(Short key, Double value) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public Double computeIfAbsent(Short key, Function<? super Short, ? extends Double> mappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public Double computeIfPresent(Short key, BiFunction<? super Short, ? super Double, ? extends Double> remappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public Double compute(Short key, BiFunction<? super Short, ? super Double, ? extends Double> remappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public Double merge(Short key, Double value, BiFunction<? super Double, ? super Double, ? extends Double> remappingFunction) {
            throw new UnsupportedOperationException();
        }
    }

    public static class SynchronizedMap
    extends Short2DoubleFunctions.SynchronizedFunction
    implements Short2DoubleMap,
    Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final Short2DoubleMap map;
        protected transient ObjectSet<Short2DoubleMap.Entry> entries;
        protected transient ShortSet keys;
        protected transient DoubleCollection values;

        protected SynchronizedMap(Short2DoubleMap m, Object sync) {
            super(m, sync);
            this.map = m;
        }

        protected SynchronizedMap(Short2DoubleMap m) {
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
        public void putAll(Map<? extends Short, ? extends Double> m) {
            Object object = this.sync;
            synchronized (object) {
                this.map.putAll(m);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public ObjectSet<Short2DoubleMap.Entry> short2DoubleEntrySet() {
            Object object = this.sync;
            synchronized (object) {
                if (this.entries == null) {
                    this.entries = ObjectSets.synchronize(this.map.short2DoubleEntrySet(), this.sync);
                }
                return this.entries;
            }
        }

        @Deprecated
        @Override
        public ObjectSet<Map.Entry<Short, Double>> entrySet() {
            return this.short2DoubleEntrySet();
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
        public double getOrDefault(short key, double defaultValue) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.getOrDefault(key, defaultValue);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void forEach(BiConsumer<? super Short, ? super Double> action) {
            Object object = this.sync;
            synchronized (object) {
                this.map.forEach(action);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void replaceAll(BiFunction<? super Short, ? super Double, ? extends Double> function) {
            Object object = this.sync;
            synchronized (object) {
                this.map.replaceAll(function);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public double putIfAbsent(short key, double value) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.putIfAbsent(key, value);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean remove(short key, double value) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.remove(key, value);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public double replace(short key, double value) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.replace(key, value);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean replace(short key, double oldValue, double newValue) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.replace(key, oldValue, newValue);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public double computeIfAbsent(short key, IntToDoubleFunction mappingFunction) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.computeIfAbsent(key, mappingFunction);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public double computeIfAbsentNullable(short key, IntFunction<? extends Double> mappingFunction) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.computeIfAbsentNullable(key, mappingFunction);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public double computeIfAbsentPartial(short key, Short2DoubleFunction mappingFunction) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.computeIfAbsentPartial(key, mappingFunction);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public double computeIfPresent(short key, BiFunction<? super Short, ? super Double, ? extends Double> remappingFunction) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.computeIfPresent(key, remappingFunction);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public double compute(short key, BiFunction<? super Short, ? super Double, ? extends Double> remappingFunction) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.compute(key, remappingFunction);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public double merge(short key, double value, BiFunction<? super Double, ? super Double, ? extends Double> remappingFunction) {
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
        public Double replace(Short key, Double value) {
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
        public boolean replace(Short key, Double oldValue, Double newValue) {
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
        public Double putIfAbsent(Short key, Double value) {
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
        public Double computeIfAbsent(Short key, Function<? super Short, ? extends Double> mappingFunction) {
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
        public Double computeIfPresent(Short key, BiFunction<? super Short, ? super Double, ? extends Double> remappingFunction) {
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
        public Double compute(Short key, BiFunction<? super Short, ? super Double, ? extends Double> remappingFunction) {
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
        public Double merge(Short key, Double value, BiFunction<? super Double, ? super Double, ? extends Double> remappingFunction) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.merge(key, value, remappingFunction);
            }
        }
    }

    public static class Singleton
    extends Short2DoubleFunctions.Singleton
    implements Short2DoubleMap,
    Serializable,
    Cloneable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected transient ObjectSet<Short2DoubleMap.Entry> entries;
        protected transient ShortSet keys;
        protected transient DoubleCollection values;

        protected Singleton(short key, double value) {
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
        public void putAll(Map<? extends Short, ? extends Double> m) {
            throw new UnsupportedOperationException();
        }

        @Override
        public ObjectSet<Short2DoubleMap.Entry> short2DoubleEntrySet() {
            if (this.entries == null) {
                this.entries = ObjectSets.singleton(new AbstractShort2DoubleMap.BasicEntry(this.key, this.value));
            }
            return this.entries;
        }

        @Deprecated
        @Override
        public ObjectSet<Map.Entry<Short, Double>> entrySet() {
            return this.short2DoubleEntrySet();
        }

        @Override
        public ShortSet keySet() {
            if (this.keys == null) {
                this.keys = ShortSets.singleton(this.key);
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
            return this.key ^ HashCommon.double2int(this.value);
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
    extends Short2DoubleFunctions.EmptyFunction
    implements Short2DoubleMap,
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
        public void putAll(Map<? extends Short, ? extends Double> m) {
            throw new UnsupportedOperationException();
        }

        @Override
        public ObjectSet<Short2DoubleMap.Entry> short2DoubleEntrySet() {
            return ObjectSets.EMPTY_SET;
        }

        @Override
        public ShortSet keySet() {
            return ShortSets.EMPTY_SET;
        }

        @Override
        public DoubleCollection values() {
            return DoubleSets.EMPTY_SET;
        }

        @Override
        public Object clone() {
            return Short2DoubleMaps.EMPTY_MAP;
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

