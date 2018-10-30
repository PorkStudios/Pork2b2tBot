/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.ints;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.floats.FloatCollection;
import it.unimi.dsi.fastutil.floats.FloatCollections;
import it.unimi.dsi.fastutil.floats.FloatSets;
import it.unimi.dsi.fastutil.ints.AbstractInt2FloatMap;
import it.unimi.dsi.fastutil.ints.Int2FloatFunction;
import it.unimi.dsi.fastutil.ints.Int2FloatFunctions;
import it.unimi.dsi.fastutil.ints.Int2FloatMap;
import it.unimi.dsi.fastutil.ints.IntSet;
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
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.IntToDoubleFunction;

public final class Int2FloatMaps {
    public static final EmptyMap EMPTY_MAP = new EmptyMap();

    private Int2FloatMaps() {
    }

    public static ObjectIterator<Int2FloatMap.Entry> fastIterator(Int2FloatMap map) {
        ObjectSet<Int2FloatMap.Entry> entries = map.int2FloatEntrySet();
        return entries instanceof Int2FloatMap.FastEntrySet ? ((Int2FloatMap.FastEntrySet)entries).fastIterator() : entries.iterator();
    }

    public static void fastForEach(Int2FloatMap map, Consumer<? super Int2FloatMap.Entry> consumer) {
        ObjectSet<Int2FloatMap.Entry> entries = map.int2FloatEntrySet();
        if (entries instanceof Int2FloatMap.FastEntrySet) {
            ((Int2FloatMap.FastEntrySet)entries).fastForEach(consumer);
        } else {
            entries.forEach(consumer);
        }
    }

    public static ObjectIterable<Int2FloatMap.Entry> fastIterable(Int2FloatMap map) {
        final ObjectSet<Int2FloatMap.Entry> entries = map.int2FloatEntrySet();
        return entries instanceof Int2FloatMap.FastEntrySet ? new ObjectIterable<Int2FloatMap.Entry>(){

            @Override
            public ObjectIterator<Int2FloatMap.Entry> iterator() {
                return ((Int2FloatMap.FastEntrySet)entries).fastIterator();
            }

            @Override
            public void forEach(Consumer<? super Int2FloatMap.Entry> consumer) {
                ((Int2FloatMap.FastEntrySet)entries).fastForEach(consumer);
            }
        } : entries;
    }

    public static Int2FloatMap singleton(int key, float value) {
        return new Singleton(key, value);
    }

    public static Int2FloatMap singleton(Integer key, Float value) {
        return new Singleton(key, value.floatValue());
    }

    public static Int2FloatMap synchronize(Int2FloatMap m) {
        return new SynchronizedMap(m);
    }

    public static Int2FloatMap synchronize(Int2FloatMap m, Object sync) {
        return new SynchronizedMap(m, sync);
    }

    public static Int2FloatMap unmodifiable(Int2FloatMap m) {
        return new UnmodifiableMap(m);
    }

    public static class UnmodifiableMap
    extends Int2FloatFunctions.UnmodifiableFunction
    implements Int2FloatMap,
    Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final Int2FloatMap map;
        protected transient ObjectSet<Int2FloatMap.Entry> entries;
        protected transient IntSet keys;
        protected transient FloatCollection values;

        protected UnmodifiableMap(Int2FloatMap m) {
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
        public void putAll(Map<? extends Integer, ? extends Float> m) {
            throw new UnsupportedOperationException();
        }

        @Override
        public ObjectSet<Int2FloatMap.Entry> int2FloatEntrySet() {
            if (this.entries == null) {
                this.entries = ObjectSets.unmodifiable(this.map.int2FloatEntrySet());
            }
            return this.entries;
        }

        @Deprecated
        @Override
        public ObjectSet<Map.Entry<Integer, Float>> entrySet() {
            return this.int2FloatEntrySet();
        }

        @Override
        public IntSet keySet() {
            if (this.keys == null) {
                this.keys = IntSets.unmodifiable(this.map.keySet());
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
        public float getOrDefault(int key, float defaultValue) {
            return this.map.getOrDefault(key, defaultValue);
        }

        @Override
        public void forEach(BiConsumer<? super Integer, ? super Float> action) {
            this.map.forEach(action);
        }

        @Override
        public void replaceAll(BiFunction<? super Integer, ? super Float, ? extends Float> function) {
            throw new UnsupportedOperationException();
        }

        @Override
        public float putIfAbsent(int key, float value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean remove(int key, float value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public float replace(int key, float value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean replace(int key, float oldValue, float newValue) {
            throw new UnsupportedOperationException();
        }

        @Override
        public float computeIfAbsent(int key, IntToDoubleFunction mappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Override
        public float computeIfAbsentNullable(int key, IntFunction<? extends Float> mappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Override
        public float computeIfAbsentPartial(int key, Int2FloatFunction mappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Override
        public float computeIfPresent(int key, BiFunction<? super Integer, ? super Float, ? extends Float> remappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Override
        public float compute(int key, BiFunction<? super Integer, ? super Float, ? extends Float> remappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Override
        public float merge(int key, float value, BiFunction<? super Float, ? super Float, ? extends Float> remappingFunction) {
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
        public Float replace(Integer key, Float value) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public boolean replace(Integer key, Float oldValue, Float newValue) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public Float putIfAbsent(Integer key, Float value) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public Float computeIfAbsent(Integer key, Function<? super Integer, ? extends Float> mappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public Float computeIfPresent(Integer key, BiFunction<? super Integer, ? super Float, ? extends Float> remappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public Float compute(Integer key, BiFunction<? super Integer, ? super Float, ? extends Float> remappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public Float merge(Integer key, Float value, BiFunction<? super Float, ? super Float, ? extends Float> remappingFunction) {
            throw new UnsupportedOperationException();
        }
    }

    public static class SynchronizedMap
    extends Int2FloatFunctions.SynchronizedFunction
    implements Int2FloatMap,
    Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final Int2FloatMap map;
        protected transient ObjectSet<Int2FloatMap.Entry> entries;
        protected transient IntSet keys;
        protected transient FloatCollection values;

        protected SynchronizedMap(Int2FloatMap m, Object sync) {
            super(m, sync);
            this.map = m;
        }

        protected SynchronizedMap(Int2FloatMap m) {
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
        public void putAll(Map<? extends Integer, ? extends Float> m) {
            Object object = this.sync;
            synchronized (object) {
                this.map.putAll(m);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public ObjectSet<Int2FloatMap.Entry> int2FloatEntrySet() {
            Object object = this.sync;
            synchronized (object) {
                if (this.entries == null) {
                    this.entries = ObjectSets.synchronize(this.map.int2FloatEntrySet(), this.sync);
                }
                return this.entries;
            }
        }

        @Deprecated
        @Override
        public ObjectSet<Map.Entry<Integer, Float>> entrySet() {
            return this.int2FloatEntrySet();
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public IntSet keySet() {
            Object object = this.sync;
            synchronized (object) {
                if (this.keys == null) {
                    this.keys = IntSets.synchronize(this.map.keySet(), this.sync);
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
        public float getOrDefault(int key, float defaultValue) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.getOrDefault(key, defaultValue);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void forEach(BiConsumer<? super Integer, ? super Float> action) {
            Object object = this.sync;
            synchronized (object) {
                this.map.forEach(action);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void replaceAll(BiFunction<? super Integer, ? super Float, ? extends Float> function) {
            Object object = this.sync;
            synchronized (object) {
                this.map.replaceAll(function);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public float putIfAbsent(int key, float value) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.putIfAbsent(key, value);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean remove(int key, float value) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.remove(key, value);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public float replace(int key, float value) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.replace(key, value);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean replace(int key, float oldValue, float newValue) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.replace(key, oldValue, newValue);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public float computeIfAbsent(int key, IntToDoubleFunction mappingFunction) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.computeIfAbsent(key, mappingFunction);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public float computeIfAbsentNullable(int key, IntFunction<? extends Float> mappingFunction) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.computeIfAbsentNullable(key, mappingFunction);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public float computeIfAbsentPartial(int key, Int2FloatFunction mappingFunction) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.computeIfAbsentPartial(key, mappingFunction);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public float computeIfPresent(int key, BiFunction<? super Integer, ? super Float, ? extends Float> remappingFunction) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.computeIfPresent(key, remappingFunction);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public float compute(int key, BiFunction<? super Integer, ? super Float, ? extends Float> remappingFunction) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.compute(key, remappingFunction);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public float merge(int key, float value, BiFunction<? super Float, ? super Float, ? extends Float> remappingFunction) {
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
        public Float replace(Integer key, Float value) {
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
        public boolean replace(Integer key, Float oldValue, Float newValue) {
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
        public Float putIfAbsent(Integer key, Float value) {
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
        public Float computeIfAbsent(Integer key, Function<? super Integer, ? extends Float> mappingFunction) {
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
        public Float computeIfPresent(Integer key, BiFunction<? super Integer, ? super Float, ? extends Float> remappingFunction) {
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
        public Float compute(Integer key, BiFunction<? super Integer, ? super Float, ? extends Float> remappingFunction) {
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
        public Float merge(Integer key, Float value, BiFunction<? super Float, ? super Float, ? extends Float> remappingFunction) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.merge(key, value, remappingFunction);
            }
        }
    }

    public static class Singleton
    extends Int2FloatFunctions.Singleton
    implements Int2FloatMap,
    Serializable,
    Cloneable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected transient ObjectSet<Int2FloatMap.Entry> entries;
        protected transient IntSet keys;
        protected transient FloatCollection values;

        protected Singleton(int key, float value) {
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
        public void putAll(Map<? extends Integer, ? extends Float> m) {
            throw new UnsupportedOperationException();
        }

        @Override
        public ObjectSet<Int2FloatMap.Entry> int2FloatEntrySet() {
            if (this.entries == null) {
                this.entries = ObjectSets.singleton(new AbstractInt2FloatMap.BasicEntry(this.key, this.value));
            }
            return this.entries;
        }

        @Deprecated
        @Override
        public ObjectSet<Map.Entry<Integer, Float>> entrySet() {
            return this.int2FloatEntrySet();
        }

        @Override
        public IntSet keySet() {
            if (this.keys == null) {
                this.keys = IntSets.singleton(this.key);
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
            return this.key ^ HashCommon.float2int(this.value);
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
    extends Int2FloatFunctions.EmptyFunction
    implements Int2FloatMap,
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
        public void putAll(Map<? extends Integer, ? extends Float> m) {
            throw new UnsupportedOperationException();
        }

        @Override
        public ObjectSet<Int2FloatMap.Entry> int2FloatEntrySet() {
            return ObjectSets.EMPTY_SET;
        }

        @Override
        public IntSet keySet() {
            return IntSets.EMPTY_SET;
        }

        @Override
        public FloatCollection values() {
            return FloatSets.EMPTY_SET;
        }

        @Override
        public Object clone() {
            return Int2FloatMaps.EMPTY_MAP;
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

