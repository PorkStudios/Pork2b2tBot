/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.bytes.AbstractByte2ReferenceMap;
import it.unimi.dsi.fastutil.bytes.Byte2ReferenceFunction;
import it.unimi.dsi.fastutil.bytes.Byte2ReferenceFunctions;
import it.unimi.dsi.fastutil.bytes.Byte2ReferenceMap;
import it.unimi.dsi.fastutil.bytes.ByteSet;
import it.unimi.dsi.fastutil.bytes.ByteSets;
import it.unimi.dsi.fastutil.objects.ObjectIterable;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import it.unimi.dsi.fastutil.objects.ObjectSets;
import it.unimi.dsi.fastutil.objects.ReferenceCollection;
import it.unimi.dsi.fastutil.objects.ReferenceCollections;
import it.unimi.dsi.fastutil.objects.ReferenceSets;
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

public final class Byte2ReferenceMaps {
    public static final EmptyMap EMPTY_MAP = new EmptyMap();

    private Byte2ReferenceMaps() {
    }

    public static <V> ObjectIterator<Byte2ReferenceMap.Entry<V>> fastIterator(Byte2ReferenceMap<V> map) {
        ObjectSet<Byte2ReferenceMap.Entry<V>> entries = map.byte2ReferenceEntrySet();
        return entries instanceof Byte2ReferenceMap.FastEntrySet ? ((Byte2ReferenceMap.FastEntrySet)entries).fastIterator() : entries.iterator();
    }

    public static <V> void fastForEach(Byte2ReferenceMap<V> map, Consumer<? super Byte2ReferenceMap.Entry<V>> consumer) {
        ObjectSet<Byte2ReferenceMap.Entry<V>> entries = map.byte2ReferenceEntrySet();
        if (entries instanceof Byte2ReferenceMap.FastEntrySet) {
            ((Byte2ReferenceMap.FastEntrySet)entries).fastForEach(consumer);
        } else {
            entries.forEach(consumer);
        }
    }

    public static <V> ObjectIterable<Byte2ReferenceMap.Entry<V>> fastIterable(Byte2ReferenceMap<V> map) {
        final ObjectSet<Byte2ReferenceMap.Entry<V>> entries = map.byte2ReferenceEntrySet();
        return entries instanceof Byte2ReferenceMap.FastEntrySet ? new ObjectIterable<Byte2ReferenceMap.Entry<V>>(){

            @Override
            public ObjectIterator<Byte2ReferenceMap.Entry<V>> iterator() {
                return ((Byte2ReferenceMap.FastEntrySet)entries).fastIterator();
            }

            @Override
            public void forEach(Consumer<? super Byte2ReferenceMap.Entry<V>> consumer) {
                ((Byte2ReferenceMap.FastEntrySet)entries).fastForEach(consumer);
            }
        } : entries;
    }

    public static <V> Byte2ReferenceMap<V> emptyMap() {
        return EMPTY_MAP;
    }

    public static <V> Byte2ReferenceMap<V> singleton(byte key, V value) {
        return new Singleton<V>(key, value);
    }

    public static <V> Byte2ReferenceMap<V> singleton(Byte key, V value) {
        return new Singleton<V>(key, value);
    }

    public static <V> Byte2ReferenceMap<V> synchronize(Byte2ReferenceMap<V> m) {
        return new SynchronizedMap<V>(m);
    }

    public static <V> Byte2ReferenceMap<V> synchronize(Byte2ReferenceMap<V> m, Object sync) {
        return new SynchronizedMap<V>(m, sync);
    }

    public static <V> Byte2ReferenceMap<V> unmodifiable(Byte2ReferenceMap<V> m) {
        return new UnmodifiableMap<V>(m);
    }

    public static class UnmodifiableMap<V>
    extends Byte2ReferenceFunctions.UnmodifiableFunction<V>
    implements Byte2ReferenceMap<V>,
    Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final Byte2ReferenceMap<V> map;
        protected transient ObjectSet<Byte2ReferenceMap.Entry<V>> entries;
        protected transient ByteSet keys;
        protected transient ReferenceCollection<V> values;

        protected UnmodifiableMap(Byte2ReferenceMap<V> m) {
            super(m);
            this.map = m;
        }

        @Override
        public boolean containsValue(Object v) {
            return this.map.containsValue(v);
        }

        @Override
        public void putAll(Map<? extends Byte, ? extends V> m) {
            throw new UnsupportedOperationException();
        }

        @Override
        public ObjectSet<Byte2ReferenceMap.Entry<V>> byte2ReferenceEntrySet() {
            if (this.entries == null) {
                this.entries = ObjectSets.unmodifiable(this.map.byte2ReferenceEntrySet());
            }
            return this.entries;
        }

        @Deprecated
        @Override
        public ObjectSet<Map.Entry<Byte, V>> entrySet() {
            return this.byte2ReferenceEntrySet();
        }

        @Override
        public ByteSet keySet() {
            if (this.keys == null) {
                this.keys = ByteSets.unmodifiable(this.map.keySet());
            }
            return this.keys;
        }

        @Override
        public ReferenceCollection<V> values() {
            if (this.values == null) {
                return ReferenceCollections.unmodifiable(this.map.values());
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
        public V getOrDefault(byte key, V defaultValue) {
            return this.map.getOrDefault(key, defaultValue);
        }

        @Override
        public void forEach(BiConsumer<? super Byte, ? super V> action) {
            this.map.forEach(action);
        }

        @Override
        public void replaceAll(BiFunction<? super Byte, ? super V, ? extends V> function) {
            throw new UnsupportedOperationException();
        }

        @Override
        public V putIfAbsent(byte key, V value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean remove(byte key, Object value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public V replace(byte key, V value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean replace(byte key, V oldValue, V newValue) {
            throw new UnsupportedOperationException();
        }

        @Override
        public V computeIfAbsent(byte key, IntFunction<? extends V> mappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Override
        public V computeIfAbsentPartial(byte key, Byte2ReferenceFunction<? extends V> mappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Override
        public V computeIfPresent(byte key, BiFunction<? super Byte, ? super V, ? extends V> remappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Override
        public V compute(byte key, BiFunction<? super Byte, ? super V, ? extends V> remappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Override
        public V merge(byte key, V value, BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public V getOrDefault(Object key, V defaultValue) {
            return this.map.getOrDefault(key, defaultValue);
        }

        @Deprecated
        @Override
        public boolean remove(Object key, Object value) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public V replace(Byte key, V value) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public boolean replace(Byte key, V oldValue, V newValue) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public V putIfAbsent(Byte key, V value) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public V computeIfAbsent(Byte key, Function<? super Byte, ? extends V> mappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public V computeIfPresent(Byte key, BiFunction<? super Byte, ? super V, ? extends V> remappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public V compute(Byte key, BiFunction<? super Byte, ? super V, ? extends V> remappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public V merge(Byte key, V value, BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
            throw new UnsupportedOperationException();
        }
    }

    public static class SynchronizedMap<V>
    extends Byte2ReferenceFunctions.SynchronizedFunction<V>
    implements Byte2ReferenceMap<V>,
    Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final Byte2ReferenceMap<V> map;
        protected transient ObjectSet<Byte2ReferenceMap.Entry<V>> entries;
        protected transient ByteSet keys;
        protected transient ReferenceCollection<V> values;

        protected SynchronizedMap(Byte2ReferenceMap<V> m, Object sync) {
            super(m, sync);
            this.map = m;
        }

        protected SynchronizedMap(Byte2ReferenceMap<V> m) {
            super(m);
            this.map = m;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean containsValue(Object v) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.containsValue(v);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void putAll(Map<? extends Byte, ? extends V> m) {
            Object object = this.sync;
            synchronized (object) {
                this.map.putAll(m);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public ObjectSet<Byte2ReferenceMap.Entry<V>> byte2ReferenceEntrySet() {
            Object object = this.sync;
            synchronized (object) {
                if (this.entries == null) {
                    this.entries = ObjectSets.synchronize(this.map.byte2ReferenceEntrySet(), this.sync);
                }
                return this.entries;
            }
        }

        @Deprecated
        @Override
        public ObjectSet<Map.Entry<Byte, V>> entrySet() {
            return this.byte2ReferenceEntrySet();
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
        public ReferenceCollection<V> values() {
            Object object = this.sync;
            synchronized (object) {
                if (this.values == null) {
                    return ReferenceCollections.synchronize(this.map.values(), this.sync);
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
        public V getOrDefault(byte key, V defaultValue) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.getOrDefault(key, defaultValue);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void forEach(BiConsumer<? super Byte, ? super V> action) {
            Object object = this.sync;
            synchronized (object) {
                this.map.forEach(action);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void replaceAll(BiFunction<? super Byte, ? super V, ? extends V> function) {
            Object object = this.sync;
            synchronized (object) {
                this.map.replaceAll(function);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public V putIfAbsent(byte key, V value) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.putIfAbsent(key, value);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean remove(byte key, Object value) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.remove(key, value);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public V replace(byte key, V value) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.replace(key, value);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean replace(byte key, V oldValue, V newValue) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.replace(key, oldValue, newValue);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public V computeIfAbsent(byte key, IntFunction<? extends V> mappingFunction) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.computeIfAbsent(key, mappingFunction);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public V computeIfAbsentPartial(byte key, Byte2ReferenceFunction<? extends V> mappingFunction) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.computeIfAbsentPartial(key, mappingFunction);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public V computeIfPresent(byte key, BiFunction<? super Byte, ? super V, ? extends V> remappingFunction) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.computeIfPresent(key, (BiFunction<Byte, ? extends V, ? extends V>)remappingFunction);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public V compute(byte key, BiFunction<? super Byte, ? super V, ? extends V> remappingFunction) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.compute(key, (BiFunction<Byte, ? extends V, ? extends V>)remappingFunction);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public V merge(byte key, V value, BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.merge(key, (V)value, (BiFunction<? extends V, ? extends V, ? extends V>)remappingFunction);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Deprecated
        @Override
        public V getOrDefault(Object key, V defaultValue) {
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
        public V replace(Byte key, V value) {
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
        public boolean replace(Byte key, V oldValue, V newValue) {
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
        public V putIfAbsent(Byte key, V value) {
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
        public V computeIfAbsent(Byte key, Function<? super Byte, ? extends V> mappingFunction) {
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
        public V computeIfPresent(Byte key, BiFunction<? super Byte, ? super V, ? extends V> remappingFunction) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.computeIfPresent(key, (BiFunction<Byte, ? extends V, ? extends V>)remappingFunction);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Deprecated
        @Override
        public V compute(Byte key, BiFunction<? super Byte, ? super V, ? extends V> remappingFunction) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.compute(key, (BiFunction<Byte, ? extends V, ? extends V>)remappingFunction);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Deprecated
        @Override
        public V merge(Byte key, V value, BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.merge(key, (V)value, (BiFunction<? extends V, ? extends V, ? extends V>)remappingFunction);
            }
        }
    }

    public static class Singleton<V>
    extends Byte2ReferenceFunctions.Singleton<V>
    implements Byte2ReferenceMap<V>,
    Serializable,
    Cloneable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected transient ObjectSet<Byte2ReferenceMap.Entry<V>> entries;
        protected transient ByteSet keys;
        protected transient ReferenceCollection<V> values;

        protected Singleton(byte key, V value) {
            super(key, value);
        }

        @Override
        public boolean containsValue(Object v) {
            return this.value == v;
        }

        @Override
        public void putAll(Map<? extends Byte, ? extends V> m) {
            throw new UnsupportedOperationException();
        }

        @Override
        public ObjectSet<Byte2ReferenceMap.Entry<V>> byte2ReferenceEntrySet() {
            if (this.entries == null) {
                this.entries = ObjectSets.singleton(new AbstractByte2ReferenceMap.BasicEntry<Object>(this.key, this.value));
            }
            return this.entries;
        }

        @Deprecated
        @Override
        public ObjectSet<Map.Entry<Byte, V>> entrySet() {
            return this.byte2ReferenceEntrySet();
        }

        @Override
        public ByteSet keySet() {
            if (this.keys == null) {
                this.keys = ByteSets.singleton(this.key);
            }
            return this.keys;
        }

        @Override
        public ReferenceCollection<V> values() {
            if (this.values == null) {
                this.values = ReferenceSets.singleton(this.value);
            }
            return this.values;
        }

        @Override
        public boolean isEmpty() {
            return false;
        }

        @Override
        public int hashCode() {
            return this.key ^ (this.value == null ? 0 : System.identityHashCode(this.value));
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

    public static class EmptyMap<V>
    extends Byte2ReferenceFunctions.EmptyFunction<V>
    implements Byte2ReferenceMap<V>,
    Serializable,
    Cloneable {
        private static final long serialVersionUID = -7046029254386353129L;

        protected EmptyMap() {
        }

        @Override
        public boolean containsValue(Object v) {
            return false;
        }

        @Override
        public void putAll(Map<? extends Byte, ? extends V> m) {
            throw new UnsupportedOperationException();
        }

        @Override
        public ObjectSet<Byte2ReferenceMap.Entry<V>> byte2ReferenceEntrySet() {
            return ObjectSets.EMPTY_SET;
        }

        @Override
        public ByteSet keySet() {
            return ByteSets.EMPTY_SET;
        }

        @Override
        public ReferenceCollection<V> values() {
            return ReferenceSets.EMPTY_SET;
        }

        @Override
        public Object clone() {
            return Byte2ReferenceMaps.EMPTY_MAP;
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

