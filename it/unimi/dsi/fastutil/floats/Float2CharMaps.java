/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.chars.CharCollection;
import it.unimi.dsi.fastutil.chars.CharCollections;
import it.unimi.dsi.fastutil.chars.CharSets;
import it.unimi.dsi.fastutil.floats.AbstractFloat2CharMap;
import it.unimi.dsi.fastutil.floats.Float2CharFunction;
import it.unimi.dsi.fastutil.floats.Float2CharFunctions;
import it.unimi.dsi.fastutil.floats.Float2CharMap;
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
import java.util.function.DoubleToIntFunction;
import java.util.function.Function;

public final class Float2CharMaps {
    public static final EmptyMap EMPTY_MAP = new EmptyMap();

    private Float2CharMaps() {
    }

    public static ObjectIterator<Float2CharMap.Entry> fastIterator(Float2CharMap map) {
        ObjectSet<Float2CharMap.Entry> entries = map.float2CharEntrySet();
        return entries instanceof Float2CharMap.FastEntrySet ? ((Float2CharMap.FastEntrySet)entries).fastIterator() : entries.iterator();
    }

    public static void fastForEach(Float2CharMap map, Consumer<? super Float2CharMap.Entry> consumer) {
        ObjectSet<Float2CharMap.Entry> entries = map.float2CharEntrySet();
        if (entries instanceof Float2CharMap.FastEntrySet) {
            ((Float2CharMap.FastEntrySet)entries).fastForEach(consumer);
        } else {
            entries.forEach(consumer);
        }
    }

    public static ObjectIterable<Float2CharMap.Entry> fastIterable(Float2CharMap map) {
        final ObjectSet<Float2CharMap.Entry> entries = map.float2CharEntrySet();
        return entries instanceof Float2CharMap.FastEntrySet ? new ObjectIterable<Float2CharMap.Entry>(){

            @Override
            public ObjectIterator<Float2CharMap.Entry> iterator() {
                return ((Float2CharMap.FastEntrySet)entries).fastIterator();
            }

            @Override
            public void forEach(Consumer<? super Float2CharMap.Entry> consumer) {
                ((Float2CharMap.FastEntrySet)entries).fastForEach(consumer);
            }
        } : entries;
    }

    public static Float2CharMap singleton(float key, char value) {
        return new Singleton(key, value);
    }

    public static Float2CharMap singleton(Float key, Character value) {
        return new Singleton(key.floatValue(), value.charValue());
    }

    public static Float2CharMap synchronize(Float2CharMap m) {
        return new SynchronizedMap(m);
    }

    public static Float2CharMap synchronize(Float2CharMap m, Object sync) {
        return new SynchronizedMap(m, sync);
    }

    public static Float2CharMap unmodifiable(Float2CharMap m) {
        return new UnmodifiableMap(m);
    }

    public static class UnmodifiableMap
    extends Float2CharFunctions.UnmodifiableFunction
    implements Float2CharMap,
    Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final Float2CharMap map;
        protected transient ObjectSet<Float2CharMap.Entry> entries;
        protected transient FloatSet keys;
        protected transient CharCollection values;

        protected UnmodifiableMap(Float2CharMap m) {
            super(m);
            this.map = m;
        }

        @Override
        public boolean containsValue(char v) {
            return this.map.containsValue(v);
        }

        @Deprecated
        @Override
        public boolean containsValue(Object ov) {
            return this.map.containsValue(ov);
        }

        @Override
        public void putAll(Map<? extends Float, ? extends Character> m) {
            throw new UnsupportedOperationException();
        }

        @Override
        public ObjectSet<Float2CharMap.Entry> float2CharEntrySet() {
            if (this.entries == null) {
                this.entries = ObjectSets.unmodifiable(this.map.float2CharEntrySet());
            }
            return this.entries;
        }

        @Deprecated
        @Override
        public ObjectSet<Map.Entry<Float, Character>> entrySet() {
            return this.float2CharEntrySet();
        }

        @Override
        public FloatSet keySet() {
            if (this.keys == null) {
                this.keys = FloatSets.unmodifiable(this.map.keySet());
            }
            return this.keys;
        }

        @Override
        public CharCollection values() {
            if (this.values == null) {
                return CharCollections.unmodifiable(this.map.values());
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
        public char getOrDefault(float key, char defaultValue) {
            return this.map.getOrDefault(key, defaultValue);
        }

        @Override
        public void forEach(BiConsumer<? super Float, ? super Character> action) {
            this.map.forEach(action);
        }

        @Override
        public void replaceAll(BiFunction<? super Float, ? super Character, ? extends Character> function) {
            throw new UnsupportedOperationException();
        }

        @Override
        public char putIfAbsent(float key, char value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean remove(float key, char value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public char replace(float key, char value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean replace(float key, char oldValue, char newValue) {
            throw new UnsupportedOperationException();
        }

        @Override
        public char computeIfAbsent(float key, DoubleToIntFunction mappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Override
        public char computeIfAbsentNullable(float key, DoubleFunction<? extends Character> mappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Override
        public char computeIfAbsentPartial(float key, Float2CharFunction mappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Override
        public char computeIfPresent(float key, BiFunction<? super Float, ? super Character, ? extends Character> remappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Override
        public char compute(float key, BiFunction<? super Float, ? super Character, ? extends Character> remappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Override
        public char merge(float key, char value, BiFunction<? super Character, ? super Character, ? extends Character> remappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public Character getOrDefault(Object key, Character defaultValue) {
            return this.map.getOrDefault(key, defaultValue);
        }

        @Deprecated
        @Override
        public boolean remove(Object key, Object value) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public Character replace(Float key, Character value) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public boolean replace(Float key, Character oldValue, Character newValue) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public Character putIfAbsent(Float key, Character value) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public Character computeIfAbsent(Float key, Function<? super Float, ? extends Character> mappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public Character computeIfPresent(Float key, BiFunction<? super Float, ? super Character, ? extends Character> remappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public Character compute(Float key, BiFunction<? super Float, ? super Character, ? extends Character> remappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public Character merge(Float key, Character value, BiFunction<? super Character, ? super Character, ? extends Character> remappingFunction) {
            throw new UnsupportedOperationException();
        }
    }

    public static class SynchronizedMap
    extends Float2CharFunctions.SynchronizedFunction
    implements Float2CharMap,
    Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final Float2CharMap map;
        protected transient ObjectSet<Float2CharMap.Entry> entries;
        protected transient FloatSet keys;
        protected transient CharCollection values;

        protected SynchronizedMap(Float2CharMap m, Object sync) {
            super(m, sync);
            this.map = m;
        }

        protected SynchronizedMap(Float2CharMap m) {
            super(m);
            this.map = m;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean containsValue(char v) {
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
        public void putAll(Map<? extends Float, ? extends Character> m) {
            Object object = this.sync;
            synchronized (object) {
                this.map.putAll(m);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public ObjectSet<Float2CharMap.Entry> float2CharEntrySet() {
            Object object = this.sync;
            synchronized (object) {
                if (this.entries == null) {
                    this.entries = ObjectSets.synchronize(this.map.float2CharEntrySet(), this.sync);
                }
                return this.entries;
            }
        }

        @Deprecated
        @Override
        public ObjectSet<Map.Entry<Float, Character>> entrySet() {
            return this.float2CharEntrySet();
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
        public CharCollection values() {
            Object object = this.sync;
            synchronized (object) {
                if (this.values == null) {
                    return CharCollections.synchronize(this.map.values(), this.sync);
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
        public char getOrDefault(float key, char defaultValue) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.getOrDefault(key, defaultValue);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void forEach(BiConsumer<? super Float, ? super Character> action) {
            Object object = this.sync;
            synchronized (object) {
                this.map.forEach(action);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void replaceAll(BiFunction<? super Float, ? super Character, ? extends Character> function) {
            Object object = this.sync;
            synchronized (object) {
                this.map.replaceAll(function);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public char putIfAbsent(float key, char value) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.putIfAbsent(key, value);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean remove(float key, char value) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.remove(key, value);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public char replace(float key, char value) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.replace(key, value);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean replace(float key, char oldValue, char newValue) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.replace(key, oldValue, newValue);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public char computeIfAbsent(float key, DoubleToIntFunction mappingFunction) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.computeIfAbsent(key, mappingFunction);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public char computeIfAbsentNullable(float key, DoubleFunction<? extends Character> mappingFunction) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.computeIfAbsentNullable(key, mappingFunction);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public char computeIfAbsentPartial(float key, Float2CharFunction mappingFunction) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.computeIfAbsentPartial(key, mappingFunction);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public char computeIfPresent(float key, BiFunction<? super Float, ? super Character, ? extends Character> remappingFunction) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.computeIfPresent(key, remappingFunction);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public char compute(float key, BiFunction<? super Float, ? super Character, ? extends Character> remappingFunction) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.compute(key, remappingFunction);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public char merge(float key, char value, BiFunction<? super Character, ? super Character, ? extends Character> remappingFunction) {
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
        public Character getOrDefault(Object key, Character defaultValue) {
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
        public Character replace(Float key, Character value) {
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
        public boolean replace(Float key, Character oldValue, Character newValue) {
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
        public Character putIfAbsent(Float key, Character value) {
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
        public Character computeIfAbsent(Float key, Function<? super Float, ? extends Character> mappingFunction) {
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
        public Character computeIfPresent(Float key, BiFunction<? super Float, ? super Character, ? extends Character> remappingFunction) {
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
        public Character compute(Float key, BiFunction<? super Float, ? super Character, ? extends Character> remappingFunction) {
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
        public Character merge(Float key, Character value, BiFunction<? super Character, ? super Character, ? extends Character> remappingFunction) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.merge(key, value, remappingFunction);
            }
        }
    }

    public static class Singleton
    extends Float2CharFunctions.Singleton
    implements Float2CharMap,
    Serializable,
    Cloneable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected transient ObjectSet<Float2CharMap.Entry> entries;
        protected transient FloatSet keys;
        protected transient CharCollection values;

        protected Singleton(float key, char value) {
            super(key, value);
        }

        @Override
        public boolean containsValue(char v) {
            return this.value == v;
        }

        @Deprecated
        @Override
        public boolean containsValue(Object ov) {
            return ((Character)ov).charValue() == this.value;
        }

        @Override
        public void putAll(Map<? extends Float, ? extends Character> m) {
            throw new UnsupportedOperationException();
        }

        @Override
        public ObjectSet<Float2CharMap.Entry> float2CharEntrySet() {
            if (this.entries == null) {
                this.entries = ObjectSets.singleton(new AbstractFloat2CharMap.BasicEntry(this.key, this.value));
            }
            return this.entries;
        }

        @Deprecated
        @Override
        public ObjectSet<Map.Entry<Float, Character>> entrySet() {
            return this.float2CharEntrySet();
        }

        @Override
        public FloatSet keySet() {
            if (this.keys == null) {
                this.keys = FloatSets.singleton(this.key);
            }
            return this.keys;
        }

        @Override
        public CharCollection values() {
            if (this.values == null) {
                this.values = CharSets.singleton(this.value);
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
    extends Float2CharFunctions.EmptyFunction
    implements Float2CharMap,
    Serializable,
    Cloneable {
        private static final long serialVersionUID = -7046029254386353129L;

        protected EmptyMap() {
        }

        @Override
        public boolean containsValue(char v) {
            return false;
        }

        @Deprecated
        @Override
        public boolean containsValue(Object ov) {
            return false;
        }

        @Override
        public void putAll(Map<? extends Float, ? extends Character> m) {
            throw new UnsupportedOperationException();
        }

        @Override
        public ObjectSet<Float2CharMap.Entry> float2CharEntrySet() {
            return ObjectSets.EMPTY_SET;
        }

        @Override
        public FloatSet keySet() {
            return FloatSets.EMPTY_SET;
        }

        @Override
        public CharCollection values() {
            return CharSets.EMPTY_SET;
        }

        @Override
        public Object clone() {
            return Float2CharMaps.EMPTY_MAP;
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

