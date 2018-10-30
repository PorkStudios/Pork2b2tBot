/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.doubles;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.doubles.AbstractDouble2LongMap;
import it.unimi.dsi.fastutil.doubles.Double2LongFunction;
import it.unimi.dsi.fastutil.doubles.Double2LongFunctions;
import it.unimi.dsi.fastutil.doubles.Double2LongMap;
import it.unimi.dsi.fastutil.doubles.DoubleSet;
import it.unimi.dsi.fastutil.doubles.DoubleSets;
import it.unimi.dsi.fastutil.longs.LongCollection;
import it.unimi.dsi.fastutil.longs.LongCollections;
import it.unimi.dsi.fastutil.longs.LongSets;
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
import java.util.function.DoubleToLongFunction;
import java.util.function.Function;

public final class Double2LongMaps {
    public static final EmptyMap EMPTY_MAP = new EmptyMap();

    private Double2LongMaps() {
    }

    public static ObjectIterator<Double2LongMap.Entry> fastIterator(Double2LongMap map) {
        ObjectSet<Double2LongMap.Entry> entries = map.double2LongEntrySet();
        return entries instanceof Double2LongMap.FastEntrySet ? ((Double2LongMap.FastEntrySet)entries).fastIterator() : entries.iterator();
    }

    public static void fastForEach(Double2LongMap map, Consumer<? super Double2LongMap.Entry> consumer) {
        ObjectSet<Double2LongMap.Entry> entries = map.double2LongEntrySet();
        if (entries instanceof Double2LongMap.FastEntrySet) {
            ((Double2LongMap.FastEntrySet)entries).fastForEach(consumer);
        } else {
            entries.forEach(consumer);
        }
    }

    public static ObjectIterable<Double2LongMap.Entry> fastIterable(Double2LongMap map) {
        final ObjectSet<Double2LongMap.Entry> entries = map.double2LongEntrySet();
        return entries instanceof Double2LongMap.FastEntrySet ? new ObjectIterable<Double2LongMap.Entry>(){

            @Override
            public ObjectIterator<Double2LongMap.Entry> iterator() {
                return ((Double2LongMap.FastEntrySet)entries).fastIterator();
            }

            @Override
            public void forEach(Consumer<? super Double2LongMap.Entry> consumer) {
                ((Double2LongMap.FastEntrySet)entries).fastForEach(consumer);
            }
        } : entries;
    }

    public static Double2LongMap singleton(double key, long value) {
        return new Singleton(key, value);
    }

    public static Double2LongMap singleton(Double key, Long value) {
        return new Singleton(key, value);
    }

    public static Double2LongMap synchronize(Double2LongMap m) {
        return new SynchronizedMap(m);
    }

    public static Double2LongMap synchronize(Double2LongMap m, Object sync) {
        return new SynchronizedMap(m, sync);
    }

    public static Double2LongMap unmodifiable(Double2LongMap m) {
        return new UnmodifiableMap(m);
    }

    public static class UnmodifiableMap
    extends Double2LongFunctions.UnmodifiableFunction
    implements Double2LongMap,
    Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final Double2LongMap map;
        protected transient ObjectSet<Double2LongMap.Entry> entries;
        protected transient DoubleSet keys;
        protected transient LongCollection values;

        protected UnmodifiableMap(Double2LongMap m) {
            super(m);
            this.map = m;
        }

        @Override
        public boolean containsValue(long v) {
            return this.map.containsValue(v);
        }

        @Deprecated
        @Override
        public boolean containsValue(Object ov) {
            return this.map.containsValue(ov);
        }

        @Override
        public void putAll(Map<? extends Double, ? extends Long> m) {
            throw new UnsupportedOperationException();
        }

        @Override
        public ObjectSet<Double2LongMap.Entry> double2LongEntrySet() {
            if (this.entries == null) {
                this.entries = ObjectSets.unmodifiable(this.map.double2LongEntrySet());
            }
            return this.entries;
        }

        @Deprecated
        @Override
        public ObjectSet<Map.Entry<Double, Long>> entrySet() {
            return this.double2LongEntrySet();
        }

        @Override
        public DoubleSet keySet() {
            if (this.keys == null) {
                this.keys = DoubleSets.unmodifiable(this.map.keySet());
            }
            return this.keys;
        }

        @Override
        public LongCollection values() {
            if (this.values == null) {
                return LongCollections.unmodifiable(this.map.values());
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
        public long getOrDefault(double key, long defaultValue) {
            return this.map.getOrDefault(key, defaultValue);
        }

        @Override
        public void forEach(BiConsumer<? super Double, ? super Long> action) {
            this.map.forEach(action);
        }

        @Override
        public void replaceAll(BiFunction<? super Double, ? super Long, ? extends Long> function) {
            throw new UnsupportedOperationException();
        }

        @Override
        public long putIfAbsent(double key, long value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean remove(double key, long value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public long replace(double key, long value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean replace(double key, long oldValue, long newValue) {
            throw new UnsupportedOperationException();
        }

        @Override
        public long computeIfAbsent(double key, DoubleToLongFunction mappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Override
        public long computeIfAbsentNullable(double key, DoubleFunction<? extends Long> mappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Override
        public long computeIfAbsentPartial(double key, Double2LongFunction mappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Override
        public long computeIfPresent(double key, BiFunction<? super Double, ? super Long, ? extends Long> remappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Override
        public long compute(double key, BiFunction<? super Double, ? super Long, ? extends Long> remappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Override
        public long merge(double key, long value, BiFunction<? super Long, ? super Long, ? extends Long> remappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public Long getOrDefault(Object key, Long defaultValue) {
            return this.map.getOrDefault(key, defaultValue);
        }

        @Deprecated
        @Override
        public boolean remove(Object key, Object value) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public Long replace(Double key, Long value) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public boolean replace(Double key, Long oldValue, Long newValue) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public Long putIfAbsent(Double key, Long value) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public Long computeIfAbsent(Double key, Function<? super Double, ? extends Long> mappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public Long computeIfPresent(Double key, BiFunction<? super Double, ? super Long, ? extends Long> remappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public Long compute(Double key, BiFunction<? super Double, ? super Long, ? extends Long> remappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public Long merge(Double key, Long value, BiFunction<? super Long, ? super Long, ? extends Long> remappingFunction) {
            throw new UnsupportedOperationException();
        }
    }

    public static class SynchronizedMap
    extends Double2LongFunctions.SynchronizedFunction
    implements Double2LongMap,
    Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final Double2LongMap map;
        protected transient ObjectSet<Double2LongMap.Entry> entries;
        protected transient DoubleSet keys;
        protected transient LongCollection values;

        protected SynchronizedMap(Double2LongMap m, Object sync) {
            super(m, sync);
            this.map = m;
        }

        protected SynchronizedMap(Double2LongMap m) {
            super(m);
            this.map = m;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean containsValue(long v) {
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
        public void putAll(Map<? extends Double, ? extends Long> m) {
            Object object = this.sync;
            synchronized (object) {
                this.map.putAll(m);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public ObjectSet<Double2LongMap.Entry> double2LongEntrySet() {
            Object object = this.sync;
            synchronized (object) {
                if (this.entries == null) {
                    this.entries = ObjectSets.synchronize(this.map.double2LongEntrySet(), this.sync);
                }
                return this.entries;
            }
        }

        @Deprecated
        @Override
        public ObjectSet<Map.Entry<Double, Long>> entrySet() {
            return this.double2LongEntrySet();
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
        public LongCollection values() {
            Object object = this.sync;
            synchronized (object) {
                if (this.values == null) {
                    return LongCollections.synchronize(this.map.values(), this.sync);
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
        public long getOrDefault(double key, long defaultValue) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.getOrDefault(key, defaultValue);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void forEach(BiConsumer<? super Double, ? super Long> action) {
            Object object = this.sync;
            synchronized (object) {
                this.map.forEach(action);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void replaceAll(BiFunction<? super Double, ? super Long, ? extends Long> function) {
            Object object = this.sync;
            synchronized (object) {
                this.map.replaceAll(function);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public long putIfAbsent(double key, long value) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.putIfAbsent(key, value);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean remove(double key, long value) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.remove(key, value);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public long replace(double key, long value) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.replace(key, value);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean replace(double key, long oldValue, long newValue) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.replace(key, oldValue, newValue);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public long computeIfAbsent(double key, DoubleToLongFunction mappingFunction) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.computeIfAbsent(key, mappingFunction);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public long computeIfAbsentNullable(double key, DoubleFunction<? extends Long> mappingFunction) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.computeIfAbsentNullable(key, mappingFunction);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public long computeIfAbsentPartial(double key, Double2LongFunction mappingFunction) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.computeIfAbsentPartial(key, mappingFunction);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public long computeIfPresent(double key, BiFunction<? super Double, ? super Long, ? extends Long> remappingFunction) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.computeIfPresent(key, remappingFunction);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public long compute(double key, BiFunction<? super Double, ? super Long, ? extends Long> remappingFunction) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.compute(key, remappingFunction);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public long merge(double key, long value, BiFunction<? super Long, ? super Long, ? extends Long> remappingFunction) {
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
        public Long getOrDefault(Object key, Long defaultValue) {
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
        public Long replace(Double key, Long value) {
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
        public boolean replace(Double key, Long oldValue, Long newValue) {
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
        public Long putIfAbsent(Double key, Long value) {
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
        public Long computeIfAbsent(Double key, Function<? super Double, ? extends Long> mappingFunction) {
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
        public Long computeIfPresent(Double key, BiFunction<? super Double, ? super Long, ? extends Long> remappingFunction) {
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
        public Long compute(Double key, BiFunction<? super Double, ? super Long, ? extends Long> remappingFunction) {
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
        public Long merge(Double key, Long value, BiFunction<? super Long, ? super Long, ? extends Long> remappingFunction) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.merge(key, value, remappingFunction);
            }
        }
    }

    public static class Singleton
    extends Double2LongFunctions.Singleton
    implements Double2LongMap,
    Serializable,
    Cloneable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected transient ObjectSet<Double2LongMap.Entry> entries;
        protected transient DoubleSet keys;
        protected transient LongCollection values;

        protected Singleton(double key, long value) {
            super(key, value);
        }

        @Override
        public boolean containsValue(long v) {
            return this.value == v;
        }

        @Deprecated
        @Override
        public boolean containsValue(Object ov) {
            return (Long)ov == this.value;
        }

        @Override
        public void putAll(Map<? extends Double, ? extends Long> m) {
            throw new UnsupportedOperationException();
        }

        @Override
        public ObjectSet<Double2LongMap.Entry> double2LongEntrySet() {
            if (this.entries == null) {
                this.entries = ObjectSets.singleton(new AbstractDouble2LongMap.BasicEntry(this.key, this.value));
            }
            return this.entries;
        }

        @Deprecated
        @Override
        public ObjectSet<Map.Entry<Double, Long>> entrySet() {
            return this.double2LongEntrySet();
        }

        @Override
        public DoubleSet keySet() {
            if (this.keys == null) {
                this.keys = DoubleSets.singleton(this.key);
            }
            return this.keys;
        }

        @Override
        public LongCollection values() {
            if (this.values == null) {
                this.values = LongSets.singleton(this.value);
            }
            return this.values;
        }

        @Override
        public boolean isEmpty() {
            return false;
        }

        @Override
        public int hashCode() {
            return HashCommon.double2int(this.key) ^ HashCommon.long2int(this.value);
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
    extends Double2LongFunctions.EmptyFunction
    implements Double2LongMap,
    Serializable,
    Cloneable {
        private static final long serialVersionUID = -7046029254386353129L;

        protected EmptyMap() {
        }

        @Override
        public boolean containsValue(long v) {
            return false;
        }

        @Deprecated
        @Override
        public boolean containsValue(Object ov) {
            return false;
        }

        @Override
        public void putAll(Map<? extends Double, ? extends Long> m) {
            throw new UnsupportedOperationException();
        }

        @Override
        public ObjectSet<Double2LongMap.Entry> double2LongEntrySet() {
            return ObjectSets.EMPTY_SET;
        }

        @Override
        public DoubleSet keySet() {
            return DoubleSets.EMPTY_SET;
        }

        @Override
        public LongCollection values() {
            return LongSets.EMPTY_SET;
        }

        @Override
        public Object clone() {
            return Double2LongMaps.EMPTY_MAP;
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

