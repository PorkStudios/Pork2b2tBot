/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.ints.IntCollection;
import it.unimi.dsi.fastutil.ints.IntCollections;
import it.unimi.dsi.fastutil.ints.IntSets;
import it.unimi.dsi.fastutil.objects.AbstractReference2IntMap;
import it.unimi.dsi.fastutil.objects.ObjectIterable;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import it.unimi.dsi.fastutil.objects.ObjectSets;
import it.unimi.dsi.fastutil.objects.Reference2IntFunction;
import it.unimi.dsi.fastutil.objects.Reference2IntFunctions;
import it.unimi.dsi.fastutil.objects.Reference2IntMap;
import it.unimi.dsi.fastutil.objects.ReferenceSet;
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
import java.util.function.ToIntFunction;

public final class Reference2IntMaps {
    public static final EmptyMap EMPTY_MAP = new EmptyMap();

    private Reference2IntMaps() {
    }

    public static <K> ObjectIterator<Reference2IntMap.Entry<K>> fastIterator(Reference2IntMap<K> map) {
        ObjectSet<Reference2IntMap.Entry<K>> entries = map.reference2IntEntrySet();
        return entries instanceof Reference2IntMap.FastEntrySet ? ((Reference2IntMap.FastEntrySet)entries).fastIterator() : entries.iterator();
    }

    public static <K> void fastForEach(Reference2IntMap<K> map, Consumer<? super Reference2IntMap.Entry<K>> consumer) {
        ObjectSet<Reference2IntMap.Entry<K>> entries = map.reference2IntEntrySet();
        if (entries instanceof Reference2IntMap.FastEntrySet) {
            ((Reference2IntMap.FastEntrySet)entries).fastForEach(consumer);
        } else {
            entries.forEach(consumer);
        }
    }

    public static <K> ObjectIterable<Reference2IntMap.Entry<K>> fastIterable(Reference2IntMap<K> map) {
        final ObjectSet<Reference2IntMap.Entry<K>> entries = map.reference2IntEntrySet();
        return entries instanceof Reference2IntMap.FastEntrySet ? new ObjectIterable<Reference2IntMap.Entry<K>>(){

            @Override
            public ObjectIterator<Reference2IntMap.Entry<K>> iterator() {
                return ((Reference2IntMap.FastEntrySet)entries).fastIterator();
            }

            @Override
            public void forEach(Consumer<? super Reference2IntMap.Entry<K>> consumer) {
                ((Reference2IntMap.FastEntrySet)entries).fastForEach(consumer);
            }
        } : entries;
    }

    public static <K> Reference2IntMap<K> emptyMap() {
        return EMPTY_MAP;
    }

    public static <K> Reference2IntMap<K> singleton(K key, int value) {
        return new Singleton<K>(key, value);
    }

    public static <K> Reference2IntMap<K> singleton(K key, Integer value) {
        return new Singleton<K>(key, value);
    }

    public static <K> Reference2IntMap<K> synchronize(Reference2IntMap<K> m) {
        return new SynchronizedMap<K>(m);
    }

    public static <K> Reference2IntMap<K> synchronize(Reference2IntMap<K> m, Object sync) {
        return new SynchronizedMap<K>(m, sync);
    }

    public static <K> Reference2IntMap<K> unmodifiable(Reference2IntMap<K> m) {
        return new UnmodifiableMap<K>(m);
    }

    public static class UnmodifiableMap<K>
    extends Reference2IntFunctions.UnmodifiableFunction<K>
    implements Reference2IntMap<K>,
    Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final Reference2IntMap<K> map;
        protected transient ObjectSet<Reference2IntMap.Entry<K>> entries;
        protected transient ReferenceSet<K> keys;
        protected transient IntCollection values;

        protected UnmodifiableMap(Reference2IntMap<K> m) {
            super(m);
            this.map = m;
        }

        @Override
        public boolean containsValue(int v) {
            return this.map.containsValue(v);
        }

        @Deprecated
        @Override
        public boolean containsValue(Object ov) {
            return this.map.containsValue(ov);
        }

        @Override
        public void putAll(Map<? extends K, ? extends Integer> m) {
            throw new UnsupportedOperationException();
        }

        @Override
        public ObjectSet<Reference2IntMap.Entry<K>> reference2IntEntrySet() {
            if (this.entries == null) {
                this.entries = ObjectSets.unmodifiable(this.map.reference2IntEntrySet());
            }
            return this.entries;
        }

        @Deprecated
        @Override
        public ObjectSet<Map.Entry<K, Integer>> entrySet() {
            return this.reference2IntEntrySet();
        }

        @Override
        public ReferenceSet<K> keySet() {
            if (this.keys == null) {
                this.keys = ReferenceSets.unmodifiable(this.map.keySet());
            }
            return this.keys;
        }

        @Override
        public IntCollection values() {
            if (this.values == null) {
                return IntCollections.unmodifiable(this.map.values());
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
        public int getOrDefault(Object key, int defaultValue) {
            return this.map.getOrDefault(key, defaultValue);
        }

        @Override
        public void forEach(BiConsumer<? super K, ? super Integer> action) {
            this.map.forEach(action);
        }

        @Override
        public void replaceAll(BiFunction<? super K, ? super Integer, ? extends Integer> function) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int putIfAbsent(K key, int value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean remove(Object key, int value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int replace(K key, int value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean replace(K key, int oldValue, int newValue) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int computeIntIfAbsent(K key, ToIntFunction<? super K> mappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int computeIntIfAbsentPartial(K key, Reference2IntFunction<? super K> mappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int computeIntIfPresent(K key, BiFunction<? super K, ? super Integer, ? extends Integer> remappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int computeInt(K key, BiFunction<? super K, ? super Integer, ? extends Integer> remappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int mergeInt(K key, int value, BiFunction<? super Integer, ? super Integer, ? extends Integer> remappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public Integer getOrDefault(Object key, Integer defaultValue) {
            return this.map.getOrDefault(key, defaultValue);
        }

        @Deprecated
        @Override
        public boolean remove(Object key, Object value) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public Integer replace(K key, Integer value) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public boolean replace(K key, Integer oldValue, Integer newValue) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public Integer putIfAbsent(K key, Integer value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Integer computeIfAbsent(K key, Function<? super K, ? extends Integer> mappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Integer computeIfPresent(K key, BiFunction<? super K, ? super Integer, ? extends Integer> remappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Integer compute(K key, BiFunction<? super K, ? super Integer, ? extends Integer> remappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public Integer merge(K key, Integer value, BiFunction<? super Integer, ? super Integer, ? extends Integer> remappingFunction) {
            throw new UnsupportedOperationException();
        }
    }

    public static class SynchronizedMap<K>
    extends Reference2IntFunctions.SynchronizedFunction<K>
    implements Reference2IntMap<K>,
    Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final Reference2IntMap<K> map;
        protected transient ObjectSet<Reference2IntMap.Entry<K>> entries;
        protected transient ReferenceSet<K> keys;
        protected transient IntCollection values;

        protected SynchronizedMap(Reference2IntMap<K> m, Object sync) {
            super(m, sync);
            this.map = m;
        }

        protected SynchronizedMap(Reference2IntMap<K> m) {
            super(m);
            this.map = m;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean containsValue(int v) {
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
        public void putAll(Map<? extends K, ? extends Integer> m) {
            Object object = this.sync;
            synchronized (object) {
                this.map.putAll(m);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public ObjectSet<Reference2IntMap.Entry<K>> reference2IntEntrySet() {
            Object object = this.sync;
            synchronized (object) {
                if (this.entries == null) {
                    this.entries = ObjectSets.synchronize(this.map.reference2IntEntrySet(), this.sync);
                }
                return this.entries;
            }
        }

        @Deprecated
        @Override
        public ObjectSet<Map.Entry<K, Integer>> entrySet() {
            return this.reference2IntEntrySet();
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public ReferenceSet<K> keySet() {
            Object object = this.sync;
            synchronized (object) {
                if (this.keys == null) {
                    this.keys = ReferenceSets.synchronize(this.map.keySet(), this.sync);
                }
                return this.keys;
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public IntCollection values() {
            Object object = this.sync;
            synchronized (object) {
                if (this.values == null) {
                    return IntCollections.synchronize(this.map.values(), this.sync);
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
        public int getOrDefault(Object key, int defaultValue) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.getOrDefault(key, defaultValue);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void forEach(BiConsumer<? super K, ? super Integer> action) {
            Object object = this.sync;
            synchronized (object) {
                this.map.forEach(action);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void replaceAll(BiFunction<? super K, ? super Integer, ? extends Integer> function) {
            Object object = this.sync;
            synchronized (object) {
                this.map.replaceAll(function);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public int putIfAbsent(K key, int value) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.putIfAbsent(key, value);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean remove(Object key, int value) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.remove(key, value);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public int replace(K key, int value) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.replace(key, value);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean replace(K key, int oldValue, int newValue) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.replace(key, oldValue, newValue);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public int computeIntIfAbsent(K key, ToIntFunction<? super K> mappingFunction) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.computeIntIfAbsent((K)key, mappingFunction);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public int computeIntIfAbsentPartial(K key, Reference2IntFunction<? super K> mappingFunction) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.computeIntIfAbsentPartial((K)key, mappingFunction);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public int computeIntIfPresent(K key, BiFunction<? super K, ? super Integer, ? extends Integer> remappingFunction) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.computeIntIfPresent((K)key, (BiFunction<? super K, Integer, Integer>)remappingFunction);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public int computeInt(K key, BiFunction<? super K, ? super Integer, ? extends Integer> remappingFunction) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.computeInt((K)key, (BiFunction<? super K, Integer, Integer>)remappingFunction);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public int mergeInt(K key, int value, BiFunction<? super Integer, ? super Integer, ? extends Integer> remappingFunction) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.mergeInt(key, value, remappingFunction);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Deprecated
        @Override
        public Integer getOrDefault(Object key, Integer defaultValue) {
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
        public Integer replace(K key, Integer value) {
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
        public boolean replace(K key, Integer oldValue, Integer newValue) {
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
        public Integer putIfAbsent(K key, Integer value) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.putIfAbsent(key, value);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public Integer computeIfAbsent(K key, Function<? super K, ? extends Integer> mappingFunction) {
            Object object = this.sync;
            synchronized (object) {
                return (Integer)this.map.computeIfAbsent(key, mappingFunction);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public Integer computeIfPresent(K key, BiFunction<? super K, ? super Integer, ? extends Integer> remappingFunction) {
            Object object = this.sync;
            synchronized (object) {
                return (Integer)this.map.computeIfPresent(key, remappingFunction);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public Integer compute(K key, BiFunction<? super K, ? super Integer, ? extends Integer> remappingFunction) {
            Object object = this.sync;
            synchronized (object) {
                return (Integer)this.map.compute(key, remappingFunction);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Deprecated
        @Override
        public Integer merge(K key, Integer value, BiFunction<? super Integer, ? super Integer, ? extends Integer> remappingFunction) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.merge(key, value, remappingFunction);
            }
        }
    }

    public static class Singleton<K>
    extends Reference2IntFunctions.Singleton<K>
    implements Reference2IntMap<K>,
    Serializable,
    Cloneable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected transient ObjectSet<Reference2IntMap.Entry<K>> entries;
        protected transient ReferenceSet<K> keys;
        protected transient IntCollection values;

        protected Singleton(K key, int value) {
            super(key, value);
        }

        @Override
        public boolean containsValue(int v) {
            return this.value == v;
        }

        @Deprecated
        @Override
        public boolean containsValue(Object ov) {
            return (Integer)ov == this.value;
        }

        @Override
        public void putAll(Map<? extends K, ? extends Integer> m) {
            throw new UnsupportedOperationException();
        }

        @Override
        public ObjectSet<Reference2IntMap.Entry<K>> reference2IntEntrySet() {
            if (this.entries == null) {
                this.entries = ObjectSets.singleton(new AbstractReference2IntMap.BasicEntry<Object>(this.key, this.value));
            }
            return this.entries;
        }

        @Deprecated
        @Override
        public ObjectSet<Map.Entry<K, Integer>> entrySet() {
            return this.reference2IntEntrySet();
        }

        @Override
        public ReferenceSet<K> keySet() {
            if (this.keys == null) {
                this.keys = ReferenceSets.singleton(this.key);
            }
            return this.keys;
        }

        @Override
        public IntCollection values() {
            if (this.values == null) {
                this.values = IntSets.singleton(this.value);
            }
            return this.values;
        }

        @Override
        public boolean isEmpty() {
            return false;
        }

        @Override
        public int hashCode() {
            return System.identityHashCode(this.key) ^ this.value;
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
    extends Reference2IntFunctions.EmptyFunction<K>
    implements Reference2IntMap<K>,
    Serializable,
    Cloneable {
        private static final long serialVersionUID = -7046029254386353129L;

        protected EmptyMap() {
        }

        @Override
        public boolean containsValue(int v) {
            return false;
        }

        @Deprecated
        @Override
        public boolean containsValue(Object ov) {
            return false;
        }

        @Override
        public void putAll(Map<? extends K, ? extends Integer> m) {
            throw new UnsupportedOperationException();
        }

        @Override
        public ObjectSet<Reference2IntMap.Entry<K>> reference2IntEntrySet() {
            return ObjectSets.EMPTY_SET;
        }

        @Override
        public ReferenceSet<K> keySet() {
            return ReferenceSets.EMPTY_SET;
        }

        @Override
        public IntCollection values() {
            return IntSets.EMPTY_SET;
        }

        @Override
        public Object clone() {
            return Reference2IntMaps.EMPTY_MAP;
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

