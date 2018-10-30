/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.chars.CharCollection;
import it.unimi.dsi.fastutil.chars.CharCollections;
import it.unimi.dsi.fastutil.chars.CharSets;
import it.unimi.dsi.fastutil.objects.AbstractObject2CharMap;
import it.unimi.dsi.fastutil.objects.Object2CharFunction;
import it.unimi.dsi.fastutil.objects.Object2CharFunctions;
import it.unimi.dsi.fastutil.objects.Object2CharMap;
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
import java.util.function.ToIntFunction;

public final class Object2CharMaps {
    public static final EmptyMap EMPTY_MAP = new EmptyMap();

    private Object2CharMaps() {
    }

    public static <K> ObjectIterator<Object2CharMap.Entry<K>> fastIterator(Object2CharMap<K> map) {
        ObjectSet<Object2CharMap.Entry<K>> entries = map.object2CharEntrySet();
        return entries instanceof Object2CharMap.FastEntrySet ? ((Object2CharMap.FastEntrySet)entries).fastIterator() : entries.iterator();
    }

    public static <K> void fastForEach(Object2CharMap<K> map, Consumer<? super Object2CharMap.Entry<K>> consumer) {
        ObjectSet<Object2CharMap.Entry<K>> entries = map.object2CharEntrySet();
        if (entries instanceof Object2CharMap.FastEntrySet) {
            ((Object2CharMap.FastEntrySet)entries).fastForEach(consumer);
        } else {
            entries.forEach(consumer);
        }
    }

    public static <K> ObjectIterable<Object2CharMap.Entry<K>> fastIterable(Object2CharMap<K> map) {
        final ObjectSet<Object2CharMap.Entry<K>> entries = map.object2CharEntrySet();
        return entries instanceof Object2CharMap.FastEntrySet ? new ObjectIterable<Object2CharMap.Entry<K>>(){

            @Override
            public ObjectIterator<Object2CharMap.Entry<K>> iterator() {
                return ((Object2CharMap.FastEntrySet)entries).fastIterator();
            }

            @Override
            public void forEach(Consumer<? super Object2CharMap.Entry<K>> consumer) {
                ((Object2CharMap.FastEntrySet)entries).fastForEach(consumer);
            }
        } : entries;
    }

    public static <K> Object2CharMap<K> emptyMap() {
        return EMPTY_MAP;
    }

    public static <K> Object2CharMap<K> singleton(K key, char value) {
        return new Singleton<K>(key, value);
    }

    public static <K> Object2CharMap<K> singleton(K key, Character value) {
        return new Singleton<K>(key, value.charValue());
    }

    public static <K> Object2CharMap<K> synchronize(Object2CharMap<K> m) {
        return new SynchronizedMap<K>(m);
    }

    public static <K> Object2CharMap<K> synchronize(Object2CharMap<K> m, Object sync) {
        return new SynchronizedMap<K>(m, sync);
    }

    public static <K> Object2CharMap<K> unmodifiable(Object2CharMap<K> m) {
        return new UnmodifiableMap<K>(m);
    }

    public static class UnmodifiableMap<K>
    extends Object2CharFunctions.UnmodifiableFunction<K>
    implements Object2CharMap<K>,
    Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final Object2CharMap<K> map;
        protected transient ObjectSet<Object2CharMap.Entry<K>> entries;
        protected transient ObjectSet<K> keys;
        protected transient CharCollection values;

        protected UnmodifiableMap(Object2CharMap<K> m) {
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
        public void putAll(Map<? extends K, ? extends Character> m) {
            throw new UnsupportedOperationException();
        }

        @Override
        public ObjectSet<Object2CharMap.Entry<K>> object2CharEntrySet() {
            if (this.entries == null) {
                this.entries = ObjectSets.unmodifiable(this.map.object2CharEntrySet());
            }
            return this.entries;
        }

        @Deprecated
        @Override
        public ObjectSet<Map.Entry<K, Character>> entrySet() {
            return this.object2CharEntrySet();
        }

        @Override
        public ObjectSet<K> keySet() {
            if (this.keys == null) {
                this.keys = ObjectSets.unmodifiable(this.map.keySet());
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
        public char getOrDefault(Object key, char defaultValue) {
            return this.map.getOrDefault(key, defaultValue);
        }

        @Override
        public void forEach(BiConsumer<? super K, ? super Character> action) {
            this.map.forEach(action);
        }

        @Override
        public void replaceAll(BiFunction<? super K, ? super Character, ? extends Character> function) {
            throw new UnsupportedOperationException();
        }

        @Override
        public char putIfAbsent(K key, char value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean remove(Object key, char value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public char replace(K key, char value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean replace(K key, char oldValue, char newValue) {
            throw new UnsupportedOperationException();
        }

        @Override
        public char computeCharIfAbsent(K key, ToIntFunction<? super K> mappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Override
        public char computeCharIfAbsentPartial(K key, Object2CharFunction<? super K> mappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Override
        public char computeCharIfPresent(K key, BiFunction<? super K, ? super Character, ? extends Character> remappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Override
        public char computeChar(K key, BiFunction<? super K, ? super Character, ? extends Character> remappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Override
        public char mergeChar(K key, char value, BiFunction<? super Character, ? super Character, ? extends Character> remappingFunction) {
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
        public Character replace(K key, Character value) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public boolean replace(K key, Character oldValue, Character newValue) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public Character putIfAbsent(K key, Character value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Character computeIfAbsent(K key, Function<? super K, ? extends Character> mappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Character computeIfPresent(K key, BiFunction<? super K, ? super Character, ? extends Character> remappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Character compute(K key, BiFunction<? super K, ? super Character, ? extends Character> remappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public Character merge(K key, Character value, BiFunction<? super Character, ? super Character, ? extends Character> remappingFunction) {
            throw new UnsupportedOperationException();
        }
    }

    public static class SynchronizedMap<K>
    extends Object2CharFunctions.SynchronizedFunction<K>
    implements Object2CharMap<K>,
    Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final Object2CharMap<K> map;
        protected transient ObjectSet<Object2CharMap.Entry<K>> entries;
        protected transient ObjectSet<K> keys;
        protected transient CharCollection values;

        protected SynchronizedMap(Object2CharMap<K> m, Object sync) {
            super(m, sync);
            this.map = m;
        }

        protected SynchronizedMap(Object2CharMap<K> m) {
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
        public void putAll(Map<? extends K, ? extends Character> m) {
            Object object = this.sync;
            synchronized (object) {
                this.map.putAll(m);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public ObjectSet<Object2CharMap.Entry<K>> object2CharEntrySet() {
            Object object = this.sync;
            synchronized (object) {
                if (this.entries == null) {
                    this.entries = ObjectSets.synchronize(this.map.object2CharEntrySet(), this.sync);
                }
                return this.entries;
            }
        }

        @Deprecated
        @Override
        public ObjectSet<Map.Entry<K, Character>> entrySet() {
            return this.object2CharEntrySet();
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public ObjectSet<K> keySet() {
            Object object = this.sync;
            synchronized (object) {
                if (this.keys == null) {
                    this.keys = ObjectSets.synchronize(this.map.keySet(), this.sync);
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
        public char getOrDefault(Object key, char defaultValue) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.getOrDefault(key, defaultValue);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void forEach(BiConsumer<? super K, ? super Character> action) {
            Object object = this.sync;
            synchronized (object) {
                this.map.forEach(action);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void replaceAll(BiFunction<? super K, ? super Character, ? extends Character> function) {
            Object object = this.sync;
            synchronized (object) {
                this.map.replaceAll(function);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public char putIfAbsent(K key, char value) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.putIfAbsent(key, value);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean remove(Object key, char value) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.remove(key, value);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public char replace(K key, char value) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.replace(key, value);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean replace(K key, char oldValue, char newValue) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.replace(key, oldValue, newValue);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public char computeCharIfAbsent(K key, ToIntFunction<? super K> mappingFunction) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.computeCharIfAbsent((K)key, mappingFunction);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public char computeCharIfAbsentPartial(K key, Object2CharFunction<? super K> mappingFunction) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.computeCharIfAbsentPartial((K)key, mappingFunction);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public char computeCharIfPresent(K key, BiFunction<? super K, ? super Character, ? extends Character> remappingFunction) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.computeCharIfPresent((K)key, (BiFunction<? super K, Character, Character>)remappingFunction);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public char computeChar(K key, BiFunction<? super K, ? super Character, ? extends Character> remappingFunction) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.computeChar((K)key, (BiFunction<? super K, Character, Character>)remappingFunction);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public char mergeChar(K key, char value, BiFunction<? super Character, ? super Character, ? extends Character> remappingFunction) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.mergeChar(key, value, remappingFunction);
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
        public Character replace(K key, Character value) {
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
        public boolean replace(K key, Character oldValue, Character newValue) {
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
        public Character putIfAbsent(K key, Character value) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.putIfAbsent(key, value);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public Character computeIfAbsent(K key, Function<? super K, ? extends Character> mappingFunction) {
            Object object = this.sync;
            synchronized (object) {
                return (Character)this.map.computeIfAbsent(key, mappingFunction);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public Character computeIfPresent(K key, BiFunction<? super K, ? super Character, ? extends Character> remappingFunction) {
            Object object = this.sync;
            synchronized (object) {
                return (Character)this.map.computeIfPresent(key, remappingFunction);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public Character compute(K key, BiFunction<? super K, ? super Character, ? extends Character> remappingFunction) {
            Object object = this.sync;
            synchronized (object) {
                return (Character)this.map.compute(key, remappingFunction);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Deprecated
        @Override
        public Character merge(K key, Character value, BiFunction<? super Character, ? super Character, ? extends Character> remappingFunction) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.merge(key, value, remappingFunction);
            }
        }
    }

    public static class Singleton<K>
    extends Object2CharFunctions.Singleton<K>
    implements Object2CharMap<K>,
    Serializable,
    Cloneable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected transient ObjectSet<Object2CharMap.Entry<K>> entries;
        protected transient ObjectSet<K> keys;
        protected transient CharCollection values;

        protected Singleton(K key, char value) {
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
        public void putAll(Map<? extends K, ? extends Character> m) {
            throw new UnsupportedOperationException();
        }

        @Override
        public ObjectSet<Object2CharMap.Entry<K>> object2CharEntrySet() {
            if (this.entries == null) {
                this.entries = ObjectSets.singleton(new AbstractObject2CharMap.BasicEntry<Object>(this.key, this.value));
            }
            return this.entries;
        }

        @Deprecated
        @Override
        public ObjectSet<Map.Entry<K, Character>> entrySet() {
            return this.object2CharEntrySet();
        }

        @Override
        public ObjectSet<K> keySet() {
            if (this.keys == null) {
                this.keys = ObjectSets.singleton(this.key);
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
            return (this.key == null ? 0 : this.key.hashCode()) ^ this.value;
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

    public static class EmptyMap<K>
    extends Object2CharFunctions.EmptyFunction<K>
    implements Object2CharMap<K>,
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
        public void putAll(Map<? extends K, ? extends Character> m) {
            throw new UnsupportedOperationException();
        }

        @Override
        public ObjectSet<Object2CharMap.Entry<K>> object2CharEntrySet() {
            return ObjectSets.EMPTY_SET;
        }

        @Override
        public ObjectSet<K> keySet() {
            return ObjectSets.EMPTY_SET;
        }

        @Override
        public CharCollection values() {
            return CharSets.EMPTY_SET;
        }

        @Override
        public Object clone() {
            return Object2CharMaps.EMPTY_MAP;
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

