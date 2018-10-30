/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.bytes.ByteCollection;
import it.unimi.dsi.fastutil.bytes.ByteCollections;
import it.unimi.dsi.fastutil.bytes.ByteSets;
import it.unimi.dsi.fastutil.chars.AbstractChar2ByteMap;
import it.unimi.dsi.fastutil.chars.Char2ByteFunction;
import it.unimi.dsi.fastutil.chars.Char2ByteFunctions;
import it.unimi.dsi.fastutil.chars.Char2ByteMap;
import it.unimi.dsi.fastutil.chars.CharSet;
import it.unimi.dsi.fastutil.chars.CharSets;
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
import java.util.function.IntUnaryOperator;

public final class Char2ByteMaps {
    public static final EmptyMap EMPTY_MAP = new EmptyMap();

    private Char2ByteMaps() {
    }

    public static ObjectIterator<Char2ByteMap.Entry> fastIterator(Char2ByteMap map) {
        ObjectSet<Char2ByteMap.Entry> entries = map.char2ByteEntrySet();
        return entries instanceof Char2ByteMap.FastEntrySet ? ((Char2ByteMap.FastEntrySet)entries).fastIterator() : entries.iterator();
    }

    public static void fastForEach(Char2ByteMap map, Consumer<? super Char2ByteMap.Entry> consumer) {
        ObjectSet<Char2ByteMap.Entry> entries = map.char2ByteEntrySet();
        if (entries instanceof Char2ByteMap.FastEntrySet) {
            ((Char2ByteMap.FastEntrySet)entries).fastForEach(consumer);
        } else {
            entries.forEach(consumer);
        }
    }

    public static ObjectIterable<Char2ByteMap.Entry> fastIterable(Char2ByteMap map) {
        final ObjectSet<Char2ByteMap.Entry> entries = map.char2ByteEntrySet();
        return entries instanceof Char2ByteMap.FastEntrySet ? new ObjectIterable<Char2ByteMap.Entry>(){

            @Override
            public ObjectIterator<Char2ByteMap.Entry> iterator() {
                return ((Char2ByteMap.FastEntrySet)entries).fastIterator();
            }

            @Override
            public void forEach(Consumer<? super Char2ByteMap.Entry> consumer) {
                ((Char2ByteMap.FastEntrySet)entries).fastForEach(consumer);
            }
        } : entries;
    }

    public static Char2ByteMap singleton(char key, byte value) {
        return new Singleton(key, value);
    }

    public static Char2ByteMap singleton(Character key, Byte value) {
        return new Singleton(key.charValue(), value);
    }

    public static Char2ByteMap synchronize(Char2ByteMap m) {
        return new SynchronizedMap(m);
    }

    public static Char2ByteMap synchronize(Char2ByteMap m, Object sync) {
        return new SynchronizedMap(m, sync);
    }

    public static Char2ByteMap unmodifiable(Char2ByteMap m) {
        return new UnmodifiableMap(m);
    }

    public static class UnmodifiableMap
    extends Char2ByteFunctions.UnmodifiableFunction
    implements Char2ByteMap,
    Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final Char2ByteMap map;
        protected transient ObjectSet<Char2ByteMap.Entry> entries;
        protected transient CharSet keys;
        protected transient ByteCollection values;

        protected UnmodifiableMap(Char2ByteMap m) {
            super(m);
            this.map = m;
        }

        @Override
        public boolean containsValue(byte v) {
            return this.map.containsValue(v);
        }

        @Deprecated
        @Override
        public boolean containsValue(Object ov) {
            return this.map.containsValue(ov);
        }

        @Override
        public void putAll(Map<? extends Character, ? extends Byte> m) {
            throw new UnsupportedOperationException();
        }

        @Override
        public ObjectSet<Char2ByteMap.Entry> char2ByteEntrySet() {
            if (this.entries == null) {
                this.entries = ObjectSets.unmodifiable(this.map.char2ByteEntrySet());
            }
            return this.entries;
        }

        @Deprecated
        @Override
        public ObjectSet<Map.Entry<Character, Byte>> entrySet() {
            return this.char2ByteEntrySet();
        }

        @Override
        public CharSet keySet() {
            if (this.keys == null) {
                this.keys = CharSets.unmodifiable(this.map.keySet());
            }
            return this.keys;
        }

        @Override
        public ByteCollection values() {
            if (this.values == null) {
                return ByteCollections.unmodifiable(this.map.values());
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
        public byte getOrDefault(char key, byte defaultValue) {
            return this.map.getOrDefault(key, defaultValue);
        }

        @Override
        public void forEach(BiConsumer<? super Character, ? super Byte> action) {
            this.map.forEach(action);
        }

        @Override
        public void replaceAll(BiFunction<? super Character, ? super Byte, ? extends Byte> function) {
            throw new UnsupportedOperationException();
        }

        @Override
        public byte putIfAbsent(char key, byte value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean remove(char key, byte value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public byte replace(char key, byte value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean replace(char key, byte oldValue, byte newValue) {
            throw new UnsupportedOperationException();
        }

        @Override
        public byte computeIfAbsent(char key, IntUnaryOperator mappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Override
        public byte computeIfAbsentNullable(char key, IntFunction<? extends Byte> mappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Override
        public byte computeIfAbsentPartial(char key, Char2ByteFunction mappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Override
        public byte computeIfPresent(char key, BiFunction<? super Character, ? super Byte, ? extends Byte> remappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Override
        public byte compute(char key, BiFunction<? super Character, ? super Byte, ? extends Byte> remappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Override
        public byte merge(char key, byte value, BiFunction<? super Byte, ? super Byte, ? extends Byte> remappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public Byte getOrDefault(Object key, Byte defaultValue) {
            return this.map.getOrDefault(key, defaultValue);
        }

        @Deprecated
        @Override
        public boolean remove(Object key, Object value) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public Byte replace(Character key, Byte value) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public boolean replace(Character key, Byte oldValue, Byte newValue) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public Byte putIfAbsent(Character key, Byte value) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public Byte computeIfAbsent(Character key, Function<? super Character, ? extends Byte> mappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public Byte computeIfPresent(Character key, BiFunction<? super Character, ? super Byte, ? extends Byte> remappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public Byte compute(Character key, BiFunction<? super Character, ? super Byte, ? extends Byte> remappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public Byte merge(Character key, Byte value, BiFunction<? super Byte, ? super Byte, ? extends Byte> remappingFunction) {
            throw new UnsupportedOperationException();
        }
    }

    public static class SynchronizedMap
    extends Char2ByteFunctions.SynchronizedFunction
    implements Char2ByteMap,
    Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final Char2ByteMap map;
        protected transient ObjectSet<Char2ByteMap.Entry> entries;
        protected transient CharSet keys;
        protected transient ByteCollection values;

        protected SynchronizedMap(Char2ByteMap m, Object sync) {
            super(m, sync);
            this.map = m;
        }

        protected SynchronizedMap(Char2ByteMap m) {
            super(m);
            this.map = m;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean containsValue(byte v) {
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
        public void putAll(Map<? extends Character, ? extends Byte> m) {
            Object object = this.sync;
            synchronized (object) {
                this.map.putAll(m);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public ObjectSet<Char2ByteMap.Entry> char2ByteEntrySet() {
            Object object = this.sync;
            synchronized (object) {
                if (this.entries == null) {
                    this.entries = ObjectSets.synchronize(this.map.char2ByteEntrySet(), this.sync);
                }
                return this.entries;
            }
        }

        @Deprecated
        @Override
        public ObjectSet<Map.Entry<Character, Byte>> entrySet() {
            return this.char2ByteEntrySet();
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
        public ByteCollection values() {
            Object object = this.sync;
            synchronized (object) {
                if (this.values == null) {
                    return ByteCollections.synchronize(this.map.values(), this.sync);
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
        public byte getOrDefault(char key, byte defaultValue) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.getOrDefault(key, defaultValue);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void forEach(BiConsumer<? super Character, ? super Byte> action) {
            Object object = this.sync;
            synchronized (object) {
                this.map.forEach(action);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void replaceAll(BiFunction<? super Character, ? super Byte, ? extends Byte> function) {
            Object object = this.sync;
            synchronized (object) {
                this.map.replaceAll(function);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public byte putIfAbsent(char key, byte value) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.putIfAbsent(key, value);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean remove(char key, byte value) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.remove(key, value);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public byte replace(char key, byte value) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.replace(key, value);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean replace(char key, byte oldValue, byte newValue) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.replace(key, oldValue, newValue);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public byte computeIfAbsent(char key, IntUnaryOperator mappingFunction) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.computeIfAbsent(key, mappingFunction);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public byte computeIfAbsentNullable(char key, IntFunction<? extends Byte> mappingFunction) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.computeIfAbsentNullable(key, mappingFunction);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public byte computeIfAbsentPartial(char key, Char2ByteFunction mappingFunction) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.computeIfAbsentPartial(key, mappingFunction);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public byte computeIfPresent(char key, BiFunction<? super Character, ? super Byte, ? extends Byte> remappingFunction) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.computeIfPresent(key, remappingFunction);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public byte compute(char key, BiFunction<? super Character, ? super Byte, ? extends Byte> remappingFunction) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.compute(key, remappingFunction);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public byte merge(char key, byte value, BiFunction<? super Byte, ? super Byte, ? extends Byte> remappingFunction) {
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
        public Byte getOrDefault(Object key, Byte defaultValue) {
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
        public Byte replace(Character key, Byte value) {
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
        public boolean replace(Character key, Byte oldValue, Byte newValue) {
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
        public Byte putIfAbsent(Character key, Byte value) {
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
        public Byte computeIfAbsent(Character key, Function<? super Character, ? extends Byte> mappingFunction) {
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
        public Byte computeIfPresent(Character key, BiFunction<? super Character, ? super Byte, ? extends Byte> remappingFunction) {
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
        public Byte compute(Character key, BiFunction<? super Character, ? super Byte, ? extends Byte> remappingFunction) {
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
        public Byte merge(Character key, Byte value, BiFunction<? super Byte, ? super Byte, ? extends Byte> remappingFunction) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.merge(key, value, remappingFunction);
            }
        }
    }

    public static class Singleton
    extends Char2ByteFunctions.Singleton
    implements Char2ByteMap,
    Serializable,
    Cloneable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected transient ObjectSet<Char2ByteMap.Entry> entries;
        protected transient CharSet keys;
        protected transient ByteCollection values;

        protected Singleton(char key, byte value) {
            super(key, value);
        }

        @Override
        public boolean containsValue(byte v) {
            return this.value == v;
        }

        @Deprecated
        @Override
        public boolean containsValue(Object ov) {
            return (Byte)ov == this.value;
        }

        @Override
        public void putAll(Map<? extends Character, ? extends Byte> m) {
            throw new UnsupportedOperationException();
        }

        @Override
        public ObjectSet<Char2ByteMap.Entry> char2ByteEntrySet() {
            if (this.entries == null) {
                this.entries = ObjectSets.singleton(new AbstractChar2ByteMap.BasicEntry(this.key, this.value));
            }
            return this.entries;
        }

        @Deprecated
        @Override
        public ObjectSet<Map.Entry<Character, Byte>> entrySet() {
            return this.char2ByteEntrySet();
        }

        @Override
        public CharSet keySet() {
            if (this.keys == null) {
                this.keys = CharSets.singleton(this.key);
            }
            return this.keys;
        }

        @Override
        public ByteCollection values() {
            if (this.values == null) {
                this.values = ByteSets.singleton(this.value);
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
    extends Char2ByteFunctions.EmptyFunction
    implements Char2ByteMap,
    Serializable,
    Cloneable {
        private static final long serialVersionUID = -7046029254386353129L;

        protected EmptyMap() {
        }

        @Override
        public boolean containsValue(byte v) {
            return false;
        }

        @Deprecated
        @Override
        public boolean containsValue(Object ov) {
            return false;
        }

        @Override
        public void putAll(Map<? extends Character, ? extends Byte> m) {
            throw new UnsupportedOperationException();
        }

        @Override
        public ObjectSet<Char2ByteMap.Entry> char2ByteEntrySet() {
            return ObjectSets.EMPTY_SET;
        }

        @Override
        public CharSet keySet() {
            return CharSets.EMPTY_SET;
        }

        @Override
        public ByteCollection values() {
            return ByteSets.EMPTY_SET;
        }

        @Override
        public Object clone() {
            return Char2ByteMaps.EMPTY_MAP;
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

