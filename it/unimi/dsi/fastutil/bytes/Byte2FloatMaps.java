/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.bytes.AbstractByte2FloatMap;
import it.unimi.dsi.fastutil.bytes.Byte2FloatFunction;
import it.unimi.dsi.fastutil.bytes.Byte2FloatFunctions;
import it.unimi.dsi.fastutil.bytes.Byte2FloatMap;
import it.unimi.dsi.fastutil.bytes.ByteSet;
import it.unimi.dsi.fastutil.bytes.ByteSets;
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
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.IntToDoubleFunction;

public final class Byte2FloatMaps {
    public static final EmptyMap EMPTY_MAP = new EmptyMap();

    private Byte2FloatMaps() {
    }

    public static ObjectIterator<Byte2FloatMap.Entry> fastIterator(Byte2FloatMap map) {
        ObjectSet<Byte2FloatMap.Entry> entries = map.byte2FloatEntrySet();
        return entries instanceof Byte2FloatMap.FastEntrySet ? ((Byte2FloatMap.FastEntrySet)entries).fastIterator() : entries.iterator();
    }

    public static void fastForEach(Byte2FloatMap map, Consumer<? super Byte2FloatMap.Entry> consumer) {
        ObjectSet<Byte2FloatMap.Entry> entries = map.byte2FloatEntrySet();
        if (entries instanceof Byte2FloatMap.FastEntrySet) {
            ((Byte2FloatMap.FastEntrySet)entries).fastForEach(consumer);
        } else {
            entries.forEach(consumer);
        }
    }

    public static ObjectIterable<Byte2FloatMap.Entry> fastIterable(Byte2FloatMap map) {
        final ObjectSet<Byte2FloatMap.Entry> entries = map.byte2FloatEntrySet();
        return entries instanceof Byte2FloatMap.FastEntrySet ? new ObjectIterable<Byte2FloatMap.Entry>(){

            @Override
            public ObjectIterator<Byte2FloatMap.Entry> iterator() {
                return ((Byte2FloatMap.FastEntrySet)entries).fastIterator();
            }

            @Override
            public void forEach(Consumer<? super Byte2FloatMap.Entry> consumer) {
                ((Byte2FloatMap.FastEntrySet)entries).fastForEach(consumer);
            }
        } : entries;
    }

    public static Byte2FloatMap singleton(byte key, float value) {
        return new Singleton(key, value);
    }

    public static Byte2FloatMap singleton(Byte key, Float value) {
        return new Singleton(key, value.floatValue());
    }

    public static Byte2FloatMap synchronize(Byte2FloatMap m) {
        return new SynchronizedMap(m);
    }

    public static Byte2FloatMap synchronize(Byte2FloatMap m, Object sync) {
        return new SynchronizedMap(m, sync);
    }

    public static Byte2FloatMap unmodifiable(Byte2FloatMap m) {
        return new UnmodifiableMap(m);
    }

    public static class UnmodifiableMap
    extends Byte2FloatFunctions.UnmodifiableFunction
    implements Byte2FloatMap,
    Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final Byte2FloatMap map;
        protected transient ObjectSet<Byte2FloatMap.Entry> entries;
        protected transient ByteSet keys;
        protected transient FloatCollection values;

        protected UnmodifiableMap(Byte2FloatMap m) {
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
        public void putAll(Map<? extends Byte, ? extends Float> m) {
            throw new UnsupportedOperationException();
        }

        @Override
        public ObjectSet<Byte2FloatMap.Entry> byte2FloatEntrySet() {
            if (this.entries == null) {
                this.entries = ObjectSets.unmodifiable(this.map.byte2FloatEntrySet());
            }
            return this.entries;
        }

        @Deprecated
        @Override
        public ObjectSet<Map.Entry<Byte, Float>> entrySet() {
            return this.byte2FloatEntrySet();
        }

        @Override
        public ByteSet keySet() {
            if (this.keys == null) {
                this.keys = ByteSets.unmodifiable(this.map.keySet());
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
        public float getOrDefault(byte key, float defaultValue) {
            return this.map.getOrDefault(key, defaultValue);
        }

        @Override
        public void forEach(BiConsumer<? super Byte, ? super Float> action) {
            this.map.forEach(action);
        }

        @Override
        public void replaceAll(BiFunction<? super Byte, ? super Float, ? extends Float> function) {
            throw new UnsupportedOperationException();
        }

        @Override
        public float putIfAbsent(byte key, float value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean remove(byte key, float value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public float replace(byte key, float value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean replace(byte key, float oldValue, float newValue) {
            throw new UnsupportedOperationException();
        }

        @Override
        public float computeIfAbsent(byte key, IntToDoubleFunction mappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Override
        public float computeIfAbsentNullable(byte key, IntFunction<? extends Float> mappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Override
        public float computeIfAbsentPartial(byte key, Byte2FloatFunction mappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Override
        public float computeIfPresent(byte key, BiFunction<? super Byte, ? super Float, ? extends Float> remappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Override
        public float compute(byte key, BiFunction<? super Byte, ? super Float, ? extends Float> remappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Override
        public float merge(byte key, float value, BiFunction<? super Float, ? super Float, ? extends Float> remappingFunction) {
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
        public Float replace(Byte key, Float value) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public boolean replace(Byte key, Float oldValue, Float newValue) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public Float putIfAbsent(Byte key, Float value) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public Float computeIfAbsent(Byte key, Function<? super Byte, ? extends Float> mappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public Float computeIfPresent(Byte key, BiFunction<? super Byte, ? super Float, ? extends Float> remappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public Float compute(Byte key, BiFunction<? super Byte, ? super Float, ? extends Float> remappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public Float merge(Byte key, Float value, BiFunction<? super Float, ? super Float, ? extends Float> remappingFunction) {
            throw new UnsupportedOperationException();
        }
    }

    public static class SynchronizedMap
    extends Byte2FloatFunctions.SynchronizedFunction
    implements Byte2FloatMap,
    Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final Byte2FloatMap map;
        protected transient ObjectSet<Byte2FloatMap.Entry> entries;
        protected transient ByteSet keys;
        protected transient FloatCollection values;

        protected SynchronizedMap(Byte2FloatMap m, Object sync) {
            super(m, sync);
            this.map = m;
        }

        protected SynchronizedMap(Byte2FloatMap m) {
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
        public void putAll(Map<? extends Byte, ? extends Float> m) {
            Object object = this.sync;
            synchronized (object) {
                this.map.putAll(m);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public ObjectSet<Byte2FloatMap.Entry> byte2FloatEntrySet() {
            Object object = this.sync;
            synchronized (object) {
                if (this.entries == null) {
                    this.entries = ObjectSets.synchronize(this.map.byte2FloatEntrySet(), this.sync);
                }
                return this.entries;
            }
        }

        @Deprecated
        @Override
        public ObjectSet<Map.Entry<Byte, Float>> entrySet() {
            return this.byte2FloatEntrySet();
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public ByteSet keySet() {
            Object object = this.sync;
            synchronized (object) {
                if (this.keys == null) {
                    this.keys = ByteSets.synchronize(this.map.keySet(), this.sync);
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
        public float getOrDefault(byte key, float defaultValue) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.getOrDefault(key, defaultValue);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void forEach(BiConsumer<? super Byte, ? super Float> action) {
            Object object = this.sync;
            synchronized (object) {
                this.map.forEach(action);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void replaceAll(BiFunction<? super Byte, ? super Float, ? extends Float> function) {
            Object object = this.sync;
            synchronized (object) {
                this.map.replaceAll(function);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public float putIfAbsent(byte key, float value) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.putIfAbsent(key, value);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean remove(byte key, float value) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.remove(key, value);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public float replace(byte key, float value) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.replace(key, value);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean replace(byte key, float oldValue, float newValue) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.replace(key, oldValue, newValue);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public float computeIfAbsent(byte key, IntToDoubleFunction mappingFunction) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.computeIfAbsent(key, mappingFunction);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public float computeIfAbsentNullable(byte key, IntFunction<? extends Float> mappingFunction) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.computeIfAbsentNullable(key, mappingFunction);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public float computeIfAbsentPartial(byte key, Byte2FloatFunction mappingFunction) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.computeIfAbsentPartial(key, mappingFunction);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public float computeIfPresent(byte key, BiFunction<? super Byte, ? super Float, ? extends Float> remappingFunction) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.computeIfPresent(key, remappingFunction);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public float compute(byte key, BiFunction<? super Byte, ? super Float, ? extends Float> remappingFunction) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.compute(key, remappingFunction);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public float merge(byte key, float value, BiFunction<? super Float, ? super Float, ? extends Float> remappingFunction) {
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
        public Float replace(Byte key, Float value) {
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
        public boolean replace(Byte key, Float oldValue, Float newValue) {
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
        public Float putIfAbsent(Byte key, Float value) {
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
        public Float computeIfAbsent(Byte key, Function<? super Byte, ? extends Float> mappingFunction) {
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
        public Float computeIfPresent(Byte key, BiFunction<? super Byte, ? super Float, ? extends Float> remappingFunction) {
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
        public Float compute(Byte key, BiFunction<? super Byte, ? super Float, ? extends Float> remappingFunction) {
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
        public Float merge(Byte key, Float value, BiFunction<? super Float, ? super Float, ? extends Float> remappingFunction) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.merge(key, value, remappingFunction);
            }
        }
    }

    public static class Singleton
    extends Byte2FloatFunctions.Singleton
    implements Byte2FloatMap,
    Serializable,
    Cloneable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected transient ObjectSet<Byte2FloatMap.Entry> entries;
        protected transient ByteSet keys;
        protected transient FloatCollection values;

        protected Singleton(byte key, float value) {
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
        public void putAll(Map<? extends Byte, ? extends Float> m) {
            throw new UnsupportedOperationException();
        }

        @Override
        public ObjectSet<Byte2FloatMap.Entry> byte2FloatEntrySet() {
            if (this.entries == null) {
                this.entries = ObjectSets.singleton(new AbstractByte2FloatMap.BasicEntry(this.key, this.value));
            }
            return this.entries;
        }

        @Deprecated
        @Override
        public ObjectSet<Map.Entry<Byte, Float>> entrySet() {
            return this.byte2FloatEntrySet();
        }

        @Override
        public ByteSet keySet() {
            if (this.keys == null) {
                this.keys = ByteSets.singleton(this.key);
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
    extends Byte2FloatFunctions.EmptyFunction
    implements Byte2FloatMap,
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
        public void putAll(Map<? extends Byte, ? extends Float> m) {
            throw new UnsupportedOperationException();
        }

        @Override
        public ObjectSet<Byte2FloatMap.Entry> byte2FloatEntrySet() {
            return ObjectSets.EMPTY_SET;
        }

        @Override
        public ByteSet keySet() {
            return ByteSets.EMPTY_SET;
        }

        @Override
        public FloatCollection values() {
            return FloatSets.EMPTY_SET;
        }

        @Override
        public Object clone() {
            return Byte2FloatMaps.EMPTY_MAP;
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

