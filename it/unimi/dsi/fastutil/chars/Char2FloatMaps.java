/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.chars.AbstractChar2FloatMap;
import it.unimi.dsi.fastutil.chars.Char2FloatFunction;
import it.unimi.dsi.fastutil.chars.Char2FloatFunctions;
import it.unimi.dsi.fastutil.chars.Char2FloatMap;
import it.unimi.dsi.fastutil.chars.CharSet;
import it.unimi.dsi.fastutil.chars.CharSets;
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

public final class Char2FloatMaps {
    public static final EmptyMap EMPTY_MAP = new EmptyMap();

    private Char2FloatMaps() {
    }

    public static ObjectIterator<Char2FloatMap.Entry> fastIterator(Char2FloatMap map) {
        ObjectSet<Char2FloatMap.Entry> entries = map.char2FloatEntrySet();
        return entries instanceof Char2FloatMap.FastEntrySet ? ((Char2FloatMap.FastEntrySet)entries).fastIterator() : entries.iterator();
    }

    public static void fastForEach(Char2FloatMap map, Consumer<? super Char2FloatMap.Entry> consumer) {
        ObjectSet<Char2FloatMap.Entry> entries = map.char2FloatEntrySet();
        if (entries instanceof Char2FloatMap.FastEntrySet) {
            ((Char2FloatMap.FastEntrySet)entries).fastForEach(consumer);
        } else {
            entries.forEach(consumer);
        }
    }

    public static ObjectIterable<Char2FloatMap.Entry> fastIterable(Char2FloatMap map) {
        final ObjectSet<Char2FloatMap.Entry> entries = map.char2FloatEntrySet();
        return entries instanceof Char2FloatMap.FastEntrySet ? new ObjectIterable<Char2FloatMap.Entry>(){

            @Override
            public ObjectIterator<Char2FloatMap.Entry> iterator() {
                return ((Char2FloatMap.FastEntrySet)entries).fastIterator();
            }

            @Override
            public void forEach(Consumer<? super Char2FloatMap.Entry> consumer) {
                ((Char2FloatMap.FastEntrySet)entries).fastForEach(consumer);
            }
        } : entries;
    }

    public static Char2FloatMap singleton(char key, float value) {
        return new Singleton(key, value);
    }

    public static Char2FloatMap singleton(Character key, Float value) {
        return new Singleton(key.charValue(), value.floatValue());
    }

    public static Char2FloatMap synchronize(Char2FloatMap m) {
        return new SynchronizedMap(m);
    }

    public static Char2FloatMap synchronize(Char2FloatMap m, Object sync) {
        return new SynchronizedMap(m, sync);
    }

    public static Char2FloatMap unmodifiable(Char2FloatMap m) {
        return new UnmodifiableMap(m);
    }

    public static class UnmodifiableMap
    extends Char2FloatFunctions.UnmodifiableFunction
    implements Char2FloatMap,
    Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final Char2FloatMap map;
        protected transient ObjectSet<Char2FloatMap.Entry> entries;
        protected transient CharSet keys;
        protected transient FloatCollection values;

        protected UnmodifiableMap(Char2FloatMap m) {
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
        public void putAll(Map<? extends Character, ? extends Float> m) {
            throw new UnsupportedOperationException();
        }

        @Override
        public ObjectSet<Char2FloatMap.Entry> char2FloatEntrySet() {
            if (this.entries == null) {
                this.entries = ObjectSets.unmodifiable(this.map.char2FloatEntrySet());
            }
            return this.entries;
        }

        @Deprecated
        @Override
        public ObjectSet<Map.Entry<Character, Float>> entrySet() {
            return this.char2FloatEntrySet();
        }

        @Override
        public CharSet keySet() {
            if (this.keys == null) {
                this.keys = CharSets.unmodifiable(this.map.keySet());
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
        public float getOrDefault(char key, float defaultValue) {
            return this.map.getOrDefault(key, defaultValue);
        }

        @Override
        public void forEach(BiConsumer<? super Character, ? super Float> action) {
            this.map.forEach(action);
        }

        @Override
        public void replaceAll(BiFunction<? super Character, ? super Float, ? extends Float> function) {
            throw new UnsupportedOperationException();
        }

        @Override
        public float putIfAbsent(char key, float value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean remove(char key, float value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public float replace(char key, float value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean replace(char key, float oldValue, float newValue) {
            throw new UnsupportedOperationException();
        }

        @Override
        public float computeIfAbsent(char key, IntToDoubleFunction mappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Override
        public float computeIfAbsentNullable(char key, IntFunction<? extends Float> mappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Override
        public float computeIfAbsentPartial(char key, Char2FloatFunction mappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Override
        public float computeIfPresent(char key, BiFunction<? super Character, ? super Float, ? extends Float> remappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Override
        public float compute(char key, BiFunction<? super Character, ? super Float, ? extends Float> remappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Override
        public float merge(char key, float value, BiFunction<? super Float, ? super Float, ? extends Float> remappingFunction) {
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
        public Float replace(Character key, Float value) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public boolean replace(Character key, Float oldValue, Float newValue) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public Float putIfAbsent(Character key, Float value) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public Float computeIfAbsent(Character key, Function<? super Character, ? extends Float> mappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public Float computeIfPresent(Character key, BiFunction<? super Character, ? super Float, ? extends Float> remappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public Float compute(Character key, BiFunction<? super Character, ? super Float, ? extends Float> remappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public Float merge(Character key, Float value, BiFunction<? super Float, ? super Float, ? extends Float> remappingFunction) {
            throw new UnsupportedOperationException();
        }
    }

    public static class SynchronizedMap
    extends Char2FloatFunctions.SynchronizedFunction
    implements Char2FloatMap,
    Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final Char2FloatMap map;
        protected transient ObjectSet<Char2FloatMap.Entry> entries;
        protected transient CharSet keys;
        protected transient FloatCollection values;

        protected SynchronizedMap(Char2FloatMap m, Object sync) {
            super(m, sync);
            this.map = m;
        }

        protected SynchronizedMap(Char2FloatMap m) {
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
        public void putAll(Map<? extends Character, ? extends Float> m) {
            Object object = this.sync;
            synchronized (object) {
                this.map.putAll(m);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public ObjectSet<Char2FloatMap.Entry> char2FloatEntrySet() {
            Object object = this.sync;
            synchronized (object) {
                if (this.entries == null) {
                    this.entries = ObjectSets.synchronize(this.map.char2FloatEntrySet(), this.sync);
                }
                return this.entries;
            }
        }

        @Deprecated
        @Override
        public ObjectSet<Map.Entry<Character, Float>> entrySet() {
            return this.char2FloatEntrySet();
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public CharSet keySet() {
            Object object = this.sync;
            synchronized (object) {
                if (this.keys == null) {
                    this.keys = CharSets.synchronize(this.map.keySet(), this.sync);
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
        public float getOrDefault(char key, float defaultValue) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.getOrDefault(key, defaultValue);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void forEach(BiConsumer<? super Character, ? super Float> action) {
            Object object = this.sync;
            synchronized (object) {
                this.map.forEach(action);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void replaceAll(BiFunction<? super Character, ? super Float, ? extends Float> function) {
            Object object = this.sync;
            synchronized (object) {
                this.map.replaceAll(function);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public float putIfAbsent(char key, float value) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.putIfAbsent(key, value);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean remove(char key, float value) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.remove(key, value);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public float replace(char key, float value) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.replace(key, value);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean replace(char key, float oldValue, float newValue) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.replace(key, oldValue, newValue);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public float computeIfAbsent(char key, IntToDoubleFunction mappingFunction) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.computeIfAbsent(key, mappingFunction);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public float computeIfAbsentNullable(char key, IntFunction<? extends Float> mappingFunction) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.computeIfAbsentNullable(key, mappingFunction);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public float computeIfAbsentPartial(char key, Char2FloatFunction mappingFunction) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.computeIfAbsentPartial(key, mappingFunction);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public float computeIfPresent(char key, BiFunction<? super Character, ? super Float, ? extends Float> remappingFunction) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.computeIfPresent(key, remappingFunction);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public float compute(char key, BiFunction<? super Character, ? super Float, ? extends Float> remappingFunction) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.compute(key, remappingFunction);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public float merge(char key, float value, BiFunction<? super Float, ? super Float, ? extends Float> remappingFunction) {
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
        public Float replace(Character key, Float value) {
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
        public boolean replace(Character key, Float oldValue, Float newValue) {
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
        public Float putIfAbsent(Character key, Float value) {
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
        public Float computeIfAbsent(Character key, Function<? super Character, ? extends Float> mappingFunction) {
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
        public Float computeIfPresent(Character key, BiFunction<? super Character, ? super Float, ? extends Float> remappingFunction) {
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
        public Float compute(Character key, BiFunction<? super Character, ? super Float, ? extends Float> remappingFunction) {
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
        public Float merge(Character key, Float value, BiFunction<? super Float, ? super Float, ? extends Float> remappingFunction) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.merge(key, value, remappingFunction);
            }
        }
    }

    public static class Singleton
    extends Char2FloatFunctions.Singleton
    implements Char2FloatMap,
    Serializable,
    Cloneable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected transient ObjectSet<Char2FloatMap.Entry> entries;
        protected transient CharSet keys;
        protected transient FloatCollection values;

        protected Singleton(char key, float value) {
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
        public void putAll(Map<? extends Character, ? extends Float> m) {
            throw new UnsupportedOperationException();
        }

        @Override
        public ObjectSet<Char2FloatMap.Entry> char2FloatEntrySet() {
            if (this.entries == null) {
                this.entries = ObjectSets.singleton(new AbstractChar2FloatMap.BasicEntry(this.key, this.value));
            }
            return this.entries;
        }

        @Deprecated
        @Override
        public ObjectSet<Map.Entry<Character, Float>> entrySet() {
            return this.char2FloatEntrySet();
        }

        @Override
        public CharSet keySet() {
            if (this.keys == null) {
                this.keys = CharSets.singleton(this.key);
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
    extends Char2FloatFunctions.EmptyFunction
    implements Char2FloatMap,
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
        public void putAll(Map<? extends Character, ? extends Float> m) {
            throw new UnsupportedOperationException();
        }

        @Override
        public ObjectSet<Char2FloatMap.Entry> char2FloatEntrySet() {
            return ObjectSets.EMPTY_SET;
        }

        @Override
        public CharSet keySet() {
            return CharSets.EMPTY_SET;
        }

        @Override
        public FloatCollection values() {
            return FloatSets.EMPTY_SET;
        }

        @Override
        public Object clone() {
            return Char2FloatMaps.EMPTY_MAP;
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

