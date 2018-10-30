/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.bytes.AbstractByte2CharMap;
import it.unimi.dsi.fastutil.bytes.Byte2CharFunction;
import it.unimi.dsi.fastutil.bytes.Byte2CharFunctions;
import it.unimi.dsi.fastutil.bytes.Byte2CharMap;
import it.unimi.dsi.fastutil.bytes.ByteSet;
import it.unimi.dsi.fastutil.bytes.ByteSets;
import it.unimi.dsi.fastutil.chars.CharCollection;
import it.unimi.dsi.fastutil.chars.CharCollections;
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

public final class Byte2CharMaps {
    public static final EmptyMap EMPTY_MAP = new EmptyMap();

    private Byte2CharMaps() {
    }

    public static ObjectIterator<Byte2CharMap.Entry> fastIterator(Byte2CharMap map) {
        ObjectSet<Byte2CharMap.Entry> entries = map.byte2CharEntrySet();
        return entries instanceof Byte2CharMap.FastEntrySet ? ((Byte2CharMap.FastEntrySet)entries).fastIterator() : entries.iterator();
    }

    public static void fastForEach(Byte2CharMap map, Consumer<? super Byte2CharMap.Entry> consumer) {
        ObjectSet<Byte2CharMap.Entry> entries = map.byte2CharEntrySet();
        if (entries instanceof Byte2CharMap.FastEntrySet) {
            ((Byte2CharMap.FastEntrySet)entries).fastForEach(consumer);
        } else {
            entries.forEach(consumer);
        }
    }

    public static ObjectIterable<Byte2CharMap.Entry> fastIterable(Byte2CharMap map) {
        final ObjectSet<Byte2CharMap.Entry> entries = map.byte2CharEntrySet();
        return entries instanceof Byte2CharMap.FastEntrySet ? new ObjectIterable<Byte2CharMap.Entry>(){

            @Override
            public ObjectIterator<Byte2CharMap.Entry> iterator() {
                return ((Byte2CharMap.FastEntrySet)entries).fastIterator();
            }

            @Override
            public void forEach(Consumer<? super Byte2CharMap.Entry> consumer) {
                ((Byte2CharMap.FastEntrySet)entries).fastForEach(consumer);
            }
        } : entries;
    }

    public static Byte2CharMap singleton(byte key, char value) {
        return new Singleton(key, value);
    }

    public static Byte2CharMap singleton(Byte key, Character value) {
        return new Singleton(key, value.charValue());
    }

    public static Byte2CharMap synchronize(Byte2CharMap m) {
        return new SynchronizedMap(m);
    }

    public static Byte2CharMap synchronize(Byte2CharMap m, Object sync) {
        return new SynchronizedMap(m, sync);
    }

    public static Byte2CharMap unmodifiable(Byte2CharMap m) {
        return new UnmodifiableMap(m);
    }

    public static class UnmodifiableMap
    extends Byte2CharFunctions.UnmodifiableFunction
    implements Byte2CharMap,
    Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final Byte2CharMap map;
        protected transient ObjectSet<Byte2CharMap.Entry> entries;
        protected transient ByteSet keys;
        protected transient CharCollection values;

        protected UnmodifiableMap(Byte2CharMap m) {
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
        public void putAll(Map<? extends Byte, ? extends Character> m) {
            throw new UnsupportedOperationException();
        }

        @Override
        public ObjectSet<Byte2CharMap.Entry> byte2CharEntrySet() {
            if (this.entries == null) {
                this.entries = ObjectSets.unmodifiable(this.map.byte2CharEntrySet());
            }
            return this.entries;
        }

        @Deprecated
        @Override
        public ObjectSet<Map.Entry<Byte, Character>> entrySet() {
            return this.byte2CharEntrySet();
        }

        @Override
        public ByteSet keySet() {
            if (this.keys == null) {
                this.keys = ByteSets.unmodifiable(this.map.keySet());
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
        public char getOrDefault(byte key, char defaultValue) {
            return this.map.getOrDefault(key, defaultValue);
        }

        @Override
        public void forEach(BiConsumer<? super Byte, ? super Character> action) {
            this.map.forEach(action);
        }

        @Override
        public void replaceAll(BiFunction<? super Byte, ? super Character, ? extends Character> function) {
            throw new UnsupportedOperationException();
        }

        @Override
        public char putIfAbsent(byte key, char value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean remove(byte key, char value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public char replace(byte key, char value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean replace(byte key, char oldValue, char newValue) {
            throw new UnsupportedOperationException();
        }

        @Override
        public char computeIfAbsent(byte key, IntUnaryOperator mappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Override
        public char computeIfAbsentNullable(byte key, IntFunction<? extends Character> mappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Override
        public char computeIfAbsentPartial(byte key, Byte2CharFunction mappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Override
        public char computeIfPresent(byte key, BiFunction<? super Byte, ? super Character, ? extends Character> remappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Override
        public char compute(byte key, BiFunction<? super Byte, ? super Character, ? extends Character> remappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Override
        public char merge(byte key, char value, BiFunction<? super Character, ? super Character, ? extends Character> remappingFunction) {
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
        public Character replace(Byte key, Character value) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public boolean replace(Byte key, Character oldValue, Character newValue) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public Character putIfAbsent(Byte key, Character value) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public Character computeIfAbsent(Byte key, Function<? super Byte, ? extends Character> mappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public Character computeIfPresent(Byte key, BiFunction<? super Byte, ? super Character, ? extends Character> remappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public Character compute(Byte key, BiFunction<? super Byte, ? super Character, ? extends Character> remappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public Character merge(Byte key, Character value, BiFunction<? super Character, ? super Character, ? extends Character> remappingFunction) {
            throw new UnsupportedOperationException();
        }
    }

    public static class SynchronizedMap
    extends Byte2CharFunctions.SynchronizedFunction
    implements Byte2CharMap,
    Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final Byte2CharMap map;
        protected transient ObjectSet<Byte2CharMap.Entry> entries;
        protected transient ByteSet keys;
        protected transient CharCollection values;

        protected SynchronizedMap(Byte2CharMap m, Object sync) {
            super(m, sync);
            this.map = m;
        }

        protected SynchronizedMap(Byte2CharMap m) {
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
        public void putAll(Map<? extends Byte, ? extends Character> m) {
            Object object = this.sync;
            synchronized (object) {
                this.map.putAll(m);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public ObjectSet<Byte2CharMap.Entry> byte2CharEntrySet() {
            Object object = this.sync;
            synchronized (object) {
                if (this.entries == null) {
                    this.entries = ObjectSets.synchronize(this.map.byte2CharEntrySet(), this.sync);
                }
                return this.entries;
            }
        }

        @Deprecated
        @Override
        public ObjectSet<Map.Entry<Byte, Character>> entrySet() {
            return this.byte2CharEntrySet();
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
        public char getOrDefault(byte key, char defaultValue) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.getOrDefault(key, defaultValue);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void forEach(BiConsumer<? super Byte, ? super Character> action) {
            Object object = this.sync;
            synchronized (object) {
                this.map.forEach(action);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void replaceAll(BiFunction<? super Byte, ? super Character, ? extends Character> function) {
            Object object = this.sync;
            synchronized (object) {
                this.map.replaceAll(function);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public char putIfAbsent(byte key, char value) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.putIfAbsent(key, value);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean remove(byte key, char value) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.remove(key, value);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public char replace(byte key, char value) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.replace(key, value);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean replace(byte key, char oldValue, char newValue) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.replace(key, oldValue, newValue);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public char computeIfAbsent(byte key, IntUnaryOperator mappingFunction) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.computeIfAbsent(key, mappingFunction);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public char computeIfAbsentNullable(byte key, IntFunction<? extends Character> mappingFunction) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.computeIfAbsentNullable(key, mappingFunction);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public char computeIfAbsentPartial(byte key, Byte2CharFunction mappingFunction) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.computeIfAbsentPartial(key, mappingFunction);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public char computeIfPresent(byte key, BiFunction<? super Byte, ? super Character, ? extends Character> remappingFunction) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.computeIfPresent(key, remappingFunction);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public char compute(byte key, BiFunction<? super Byte, ? super Character, ? extends Character> remappingFunction) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.compute(key, remappingFunction);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public char merge(byte key, char value, BiFunction<? super Character, ? super Character, ? extends Character> remappingFunction) {
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
        public Character replace(Byte key, Character value) {
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
        public boolean replace(Byte key, Character oldValue, Character newValue) {
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
        public Character putIfAbsent(Byte key, Character value) {
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
        public Character computeIfAbsent(Byte key, Function<? super Byte, ? extends Character> mappingFunction) {
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
        public Character computeIfPresent(Byte key, BiFunction<? super Byte, ? super Character, ? extends Character> remappingFunction) {
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
        public Character compute(Byte key, BiFunction<? super Byte, ? super Character, ? extends Character> remappingFunction) {
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
        public Character merge(Byte key, Character value, BiFunction<? super Character, ? super Character, ? extends Character> remappingFunction) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.merge(key, value, remappingFunction);
            }
        }
    }

    public static class Singleton
    extends Byte2CharFunctions.Singleton
    implements Byte2CharMap,
    Serializable,
    Cloneable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected transient ObjectSet<Byte2CharMap.Entry> entries;
        protected transient ByteSet keys;
        protected transient CharCollection values;

        protected Singleton(byte key, char value) {
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
        public void putAll(Map<? extends Byte, ? extends Character> m) {
            throw new UnsupportedOperationException();
        }

        @Override
        public ObjectSet<Byte2CharMap.Entry> byte2CharEntrySet() {
            if (this.entries == null) {
                this.entries = ObjectSets.singleton(new AbstractByte2CharMap.BasicEntry(this.key, this.value));
            }
            return this.entries;
        }

        @Deprecated
        @Override
        public ObjectSet<Map.Entry<Byte, Character>> entrySet() {
            return this.byte2CharEntrySet();
        }

        @Override
        public ByteSet keySet() {
            if (this.keys == null) {
                this.keys = ByteSets.singleton(this.key);
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
    extends Byte2CharFunctions.EmptyFunction
    implements Byte2CharMap,
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
        public void putAll(Map<? extends Byte, ? extends Character> m) {
            throw new UnsupportedOperationException();
        }

        @Override
        public ObjectSet<Byte2CharMap.Entry> byte2CharEntrySet() {
            return ObjectSets.EMPTY_SET;
        }

        @Override
        public ByteSet keySet() {
            return ByteSets.EMPTY_SET;
        }

        @Override
        public CharCollection values() {
            return CharSets.EMPTY_SET;
        }

        @Override
        public Object clone() {
            return Byte2CharMaps.EMPTY_MAP;
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

