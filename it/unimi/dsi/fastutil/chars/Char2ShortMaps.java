/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.chars.AbstractChar2ShortMap;
import it.unimi.dsi.fastutil.chars.Char2ShortFunction;
import it.unimi.dsi.fastutil.chars.Char2ShortFunctions;
import it.unimi.dsi.fastutil.chars.Char2ShortMap;
import it.unimi.dsi.fastutil.chars.CharSet;
import it.unimi.dsi.fastutil.chars.CharSets;
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
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.IntUnaryOperator;

public final class Char2ShortMaps {
    public static final EmptyMap EMPTY_MAP = new EmptyMap();

    private Char2ShortMaps() {
    }

    public static ObjectIterator<Char2ShortMap.Entry> fastIterator(Char2ShortMap map) {
        ObjectSet<Char2ShortMap.Entry> entries = map.char2ShortEntrySet();
        return entries instanceof Char2ShortMap.FastEntrySet ? ((Char2ShortMap.FastEntrySet)entries).fastIterator() : entries.iterator();
    }

    public static void fastForEach(Char2ShortMap map, Consumer<? super Char2ShortMap.Entry> consumer) {
        ObjectSet<Char2ShortMap.Entry> entries = map.char2ShortEntrySet();
        if (entries instanceof Char2ShortMap.FastEntrySet) {
            ((Char2ShortMap.FastEntrySet)entries).fastForEach(consumer);
        } else {
            entries.forEach(consumer);
        }
    }

    public static ObjectIterable<Char2ShortMap.Entry> fastIterable(Char2ShortMap map) {
        final ObjectSet<Char2ShortMap.Entry> entries = map.char2ShortEntrySet();
        return entries instanceof Char2ShortMap.FastEntrySet ? new ObjectIterable<Char2ShortMap.Entry>(){

            @Override
            public ObjectIterator<Char2ShortMap.Entry> iterator() {
                return ((Char2ShortMap.FastEntrySet)entries).fastIterator();
            }

            @Override
            public void forEach(Consumer<? super Char2ShortMap.Entry> consumer) {
                ((Char2ShortMap.FastEntrySet)entries).fastForEach(consumer);
            }
        } : entries;
    }

    public static Char2ShortMap singleton(char key, short value) {
        return new Singleton(key, value);
    }

    public static Char2ShortMap singleton(Character key, Short value) {
        return new Singleton(key.charValue(), value);
    }

    public static Char2ShortMap synchronize(Char2ShortMap m) {
        return new SynchronizedMap(m);
    }

    public static Char2ShortMap synchronize(Char2ShortMap m, Object sync) {
        return new SynchronizedMap(m, sync);
    }

    public static Char2ShortMap unmodifiable(Char2ShortMap m) {
        return new UnmodifiableMap(m);
    }

    public static class UnmodifiableMap
    extends Char2ShortFunctions.UnmodifiableFunction
    implements Char2ShortMap,
    Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final Char2ShortMap map;
        protected transient ObjectSet<Char2ShortMap.Entry> entries;
        protected transient CharSet keys;
        protected transient ShortCollection values;

        protected UnmodifiableMap(Char2ShortMap m) {
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
        public void putAll(Map<? extends Character, ? extends Short> m) {
            throw new UnsupportedOperationException();
        }

        @Override
        public ObjectSet<Char2ShortMap.Entry> char2ShortEntrySet() {
            if (this.entries == null) {
                this.entries = ObjectSets.unmodifiable(this.map.char2ShortEntrySet());
            }
            return this.entries;
        }

        @Deprecated
        @Override
        public ObjectSet<Map.Entry<Character, Short>> entrySet() {
            return this.char2ShortEntrySet();
        }

        @Override
        public CharSet keySet() {
            if (this.keys == null) {
                this.keys = CharSets.unmodifiable(this.map.keySet());
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
        public short getOrDefault(char key, short defaultValue) {
            return this.map.getOrDefault(key, defaultValue);
        }

        @Override
        public void forEach(BiConsumer<? super Character, ? super Short> action) {
            this.map.forEach(action);
        }

        @Override
        public void replaceAll(BiFunction<? super Character, ? super Short, ? extends Short> function) {
            throw new UnsupportedOperationException();
        }

        @Override
        public short putIfAbsent(char key, short value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean remove(char key, short value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public short replace(char key, short value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean replace(char key, short oldValue, short newValue) {
            throw new UnsupportedOperationException();
        }

        @Override
        public short computeIfAbsent(char key, IntUnaryOperator mappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Override
        public short computeIfAbsentNullable(char key, IntFunction<? extends Short> mappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Override
        public short computeIfAbsentPartial(char key, Char2ShortFunction mappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Override
        public short computeIfPresent(char key, BiFunction<? super Character, ? super Short, ? extends Short> remappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Override
        public short compute(char key, BiFunction<? super Character, ? super Short, ? extends Short> remappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Override
        public short merge(char key, short value, BiFunction<? super Short, ? super Short, ? extends Short> remappingFunction) {
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
        public Short replace(Character key, Short value) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public boolean replace(Character key, Short oldValue, Short newValue) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public Short putIfAbsent(Character key, Short value) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public Short computeIfAbsent(Character key, Function<? super Character, ? extends Short> mappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public Short computeIfPresent(Character key, BiFunction<? super Character, ? super Short, ? extends Short> remappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public Short compute(Character key, BiFunction<? super Character, ? super Short, ? extends Short> remappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public Short merge(Character key, Short value, BiFunction<? super Short, ? super Short, ? extends Short> remappingFunction) {
            throw new UnsupportedOperationException();
        }
    }

    public static class SynchronizedMap
    extends Char2ShortFunctions.SynchronizedFunction
    implements Char2ShortMap,
    Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final Char2ShortMap map;
        protected transient ObjectSet<Char2ShortMap.Entry> entries;
        protected transient CharSet keys;
        protected transient ShortCollection values;

        protected SynchronizedMap(Char2ShortMap m, Object sync) {
            super(m, sync);
            this.map = m;
        }

        protected SynchronizedMap(Char2ShortMap m) {
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
        public void putAll(Map<? extends Character, ? extends Short> m) {
            Object object = this.sync;
            synchronized (object) {
                this.map.putAll(m);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public ObjectSet<Char2ShortMap.Entry> char2ShortEntrySet() {
            Object object = this.sync;
            synchronized (object) {
                if (this.entries == null) {
                    this.entries = ObjectSets.synchronize(this.map.char2ShortEntrySet(), this.sync);
                }
                return this.entries;
            }
        }

        @Deprecated
        @Override
        public ObjectSet<Map.Entry<Character, Short>> entrySet() {
            return this.char2ShortEntrySet();
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
        public short getOrDefault(char key, short defaultValue) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.getOrDefault(key, defaultValue);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void forEach(BiConsumer<? super Character, ? super Short> action) {
            Object object = this.sync;
            synchronized (object) {
                this.map.forEach(action);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void replaceAll(BiFunction<? super Character, ? super Short, ? extends Short> function) {
            Object object = this.sync;
            synchronized (object) {
                this.map.replaceAll(function);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public short putIfAbsent(char key, short value) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.putIfAbsent(key, value);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean remove(char key, short value) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.remove(key, value);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public short replace(char key, short value) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.replace(key, value);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean replace(char key, short oldValue, short newValue) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.replace(key, oldValue, newValue);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public short computeIfAbsent(char key, IntUnaryOperator mappingFunction) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.computeIfAbsent(key, mappingFunction);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public short computeIfAbsentNullable(char key, IntFunction<? extends Short> mappingFunction) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.computeIfAbsentNullable(key, mappingFunction);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public short computeIfAbsentPartial(char key, Char2ShortFunction mappingFunction) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.computeIfAbsentPartial(key, mappingFunction);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public short computeIfPresent(char key, BiFunction<? super Character, ? super Short, ? extends Short> remappingFunction) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.computeIfPresent(key, remappingFunction);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public short compute(char key, BiFunction<? super Character, ? super Short, ? extends Short> remappingFunction) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.compute(key, remappingFunction);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public short merge(char key, short value, BiFunction<? super Short, ? super Short, ? extends Short> remappingFunction) {
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
        public Short replace(Character key, Short value) {
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
        public boolean replace(Character key, Short oldValue, Short newValue) {
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
        public Short putIfAbsent(Character key, Short value) {
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
        public Short computeIfAbsent(Character key, Function<? super Character, ? extends Short> mappingFunction) {
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
        public Short computeIfPresent(Character key, BiFunction<? super Character, ? super Short, ? extends Short> remappingFunction) {
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
        public Short compute(Character key, BiFunction<? super Character, ? super Short, ? extends Short> remappingFunction) {
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
        public Short merge(Character key, Short value, BiFunction<? super Short, ? super Short, ? extends Short> remappingFunction) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.merge(key, value, remappingFunction);
            }
        }
    }

    public static class Singleton
    extends Char2ShortFunctions.Singleton
    implements Char2ShortMap,
    Serializable,
    Cloneable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected transient ObjectSet<Char2ShortMap.Entry> entries;
        protected transient CharSet keys;
        protected transient ShortCollection values;

        protected Singleton(char key, short value) {
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
        public void putAll(Map<? extends Character, ? extends Short> m) {
            throw new UnsupportedOperationException();
        }

        @Override
        public ObjectSet<Char2ShortMap.Entry> char2ShortEntrySet() {
            if (this.entries == null) {
                this.entries = ObjectSets.singleton(new AbstractChar2ShortMap.BasicEntry(this.key, this.value));
            }
            return this.entries;
        }

        @Deprecated
        @Override
        public ObjectSet<Map.Entry<Character, Short>> entrySet() {
            return this.char2ShortEntrySet();
        }

        @Override
        public CharSet keySet() {
            if (this.keys == null) {
                this.keys = CharSets.singleton(this.key);
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
            return this.key ^ this.value;
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
    extends Char2ShortFunctions.EmptyFunction
    implements Char2ShortMap,
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
        public void putAll(Map<? extends Character, ? extends Short> m) {
            throw new UnsupportedOperationException();
        }

        @Override
        public ObjectSet<Char2ShortMap.Entry> char2ShortEntrySet() {
            return ObjectSets.EMPTY_SET;
        }

        @Override
        public CharSet keySet() {
            return CharSets.EMPTY_SET;
        }

        @Override
        public ShortCollection values() {
            return ShortSets.EMPTY_SET;
        }

        @Override
        public Object clone() {
            return Char2ShortMaps.EMPTY_MAP;
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

