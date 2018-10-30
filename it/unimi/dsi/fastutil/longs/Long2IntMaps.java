/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.ints.IntCollection;
import it.unimi.dsi.fastutil.ints.IntCollections;
import it.unimi.dsi.fastutil.ints.IntSets;
import it.unimi.dsi.fastutil.longs.AbstractLong2IntMap;
import it.unimi.dsi.fastutil.longs.Long2IntFunction;
import it.unimi.dsi.fastutil.longs.Long2IntFunctions;
import it.unimi.dsi.fastutil.longs.Long2IntMap;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.longs.LongSets;
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
import java.util.function.LongFunction;
import java.util.function.LongToIntFunction;

public final class Long2IntMaps {
    public static final EmptyMap EMPTY_MAP = new EmptyMap();

    private Long2IntMaps() {
    }

    public static ObjectIterator<Long2IntMap.Entry> fastIterator(Long2IntMap map) {
        ObjectSet<Long2IntMap.Entry> entries = map.long2IntEntrySet();
        return entries instanceof Long2IntMap.FastEntrySet ? ((Long2IntMap.FastEntrySet)entries).fastIterator() : entries.iterator();
    }

    public static void fastForEach(Long2IntMap map, Consumer<? super Long2IntMap.Entry> consumer) {
        ObjectSet<Long2IntMap.Entry> entries = map.long2IntEntrySet();
        if (entries instanceof Long2IntMap.FastEntrySet) {
            ((Long2IntMap.FastEntrySet)entries).fastForEach(consumer);
        } else {
            entries.forEach(consumer);
        }
    }

    public static ObjectIterable<Long2IntMap.Entry> fastIterable(Long2IntMap map) {
        final ObjectSet<Long2IntMap.Entry> entries = map.long2IntEntrySet();
        return entries instanceof Long2IntMap.FastEntrySet ? new ObjectIterable<Long2IntMap.Entry>(){

            @Override
            public ObjectIterator<Long2IntMap.Entry> iterator() {
                return ((Long2IntMap.FastEntrySet)entries).fastIterator();
            }

            @Override
            public void forEach(Consumer<? super Long2IntMap.Entry> consumer) {
                ((Long2IntMap.FastEntrySet)entries).fastForEach(consumer);
            }
        } : entries;
    }

    public static Long2IntMap singleton(long key, int value) {
        return new Singleton(key, value);
    }

    public static Long2IntMap singleton(Long key, Integer value) {
        return new Singleton(key, value);
    }

    public static Long2IntMap synchronize(Long2IntMap m) {
        return new SynchronizedMap(m);
    }

    public static Long2IntMap synchronize(Long2IntMap m, Object sync) {
        return new SynchronizedMap(m, sync);
    }

    public static Long2IntMap unmodifiable(Long2IntMap m) {
        return new UnmodifiableMap(m);
    }

    public static class UnmodifiableMap
    extends Long2IntFunctions.UnmodifiableFunction
    implements Long2IntMap,
    Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final Long2IntMap map;
        protected transient ObjectSet<Long2IntMap.Entry> entries;
        protected transient LongSet keys;
        protected transient IntCollection values;

        protected UnmodifiableMap(Long2IntMap m) {
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
        public void putAll(Map<? extends Long, ? extends Integer> m) {
            throw new UnsupportedOperationException();
        }

        @Override
        public ObjectSet<Long2IntMap.Entry> long2IntEntrySet() {
            if (this.entries == null) {
                this.entries = ObjectSets.unmodifiable(this.map.long2IntEntrySet());
            }
            return this.entries;
        }

        @Deprecated
        @Override
        public ObjectSet<Map.Entry<Long, Integer>> entrySet() {
            return this.long2IntEntrySet();
        }

        @Override
        public LongSet keySet() {
            if (this.keys == null) {
                this.keys = LongSets.unmodifiable(this.map.keySet());
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
        public int getOrDefault(long key, int defaultValue) {
            return this.map.getOrDefault(key, defaultValue);
        }

        @Override
        public void forEach(BiConsumer<? super Long, ? super Integer> action) {
            this.map.forEach(action);
        }

        @Override
        public void replaceAll(BiFunction<? super Long, ? super Integer, ? extends Integer> function) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int putIfAbsent(long key, int value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean remove(long key, int value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int replace(long key, int value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean replace(long key, int oldValue, int newValue) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int computeIfAbsent(long key, LongToIntFunction mappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int computeIfAbsentNullable(long key, LongFunction<? extends Integer> mappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int computeIfAbsentPartial(long key, Long2IntFunction mappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int computeIfPresent(long key, BiFunction<? super Long, ? super Integer, ? extends Integer> remappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int compute(long key, BiFunction<? super Long, ? super Integer, ? extends Integer> remappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int merge(long key, int value, BiFunction<? super Integer, ? super Integer, ? extends Integer> remappingFunction) {
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
        public Integer replace(Long key, Integer value) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public boolean replace(Long key, Integer oldValue, Integer newValue) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public Integer putIfAbsent(Long key, Integer value) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public Integer computeIfAbsent(Long key, Function<? super Long, ? extends Integer> mappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public Integer computeIfPresent(Long key, BiFunction<? super Long, ? super Integer, ? extends Integer> remappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public Integer compute(Long key, BiFunction<? super Long, ? super Integer, ? extends Integer> remappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public Integer merge(Long key, Integer value, BiFunction<? super Integer, ? super Integer, ? extends Integer> remappingFunction) {
            throw new UnsupportedOperationException();
        }
    }

    public static class SynchronizedMap
    extends Long2IntFunctions.SynchronizedFunction
    implements Long2IntMap,
    Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final Long2IntMap map;
        protected transient ObjectSet<Long2IntMap.Entry> entries;
        protected transient LongSet keys;
        protected transient IntCollection values;

        protected SynchronizedMap(Long2IntMap m, Object sync) {
            super(m, sync);
            this.map = m;
        }

        protected SynchronizedMap(Long2IntMap m) {
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
        public void putAll(Map<? extends Long, ? extends Integer> m) {
            Object object = this.sync;
            synchronized (object) {
                this.map.putAll(m);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public ObjectSet<Long2IntMap.Entry> long2IntEntrySet() {
            Object object = this.sync;
            synchronized (object) {
                if (this.entries == null) {
                    this.entries = ObjectSets.synchronize(this.map.long2IntEntrySet(), this.sync);
                }
                return this.entries;
            }
        }

        @Deprecated
        @Override
        public ObjectSet<Map.Entry<Long, Integer>> entrySet() {
            return this.long2IntEntrySet();
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public LongSet keySet() {
            Object object = this.sync;
            synchronized (object) {
                if (this.keys == null) {
                    this.keys = LongSets.synchronize(this.map.keySet(), this.sync);
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
        public int getOrDefault(long key, int defaultValue) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.getOrDefault(key, defaultValue);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void forEach(BiConsumer<? super Long, ? super Integer> action) {
            Object object = this.sync;
            synchronized (object) {
                this.map.forEach(action);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void replaceAll(BiFunction<? super Long, ? super Integer, ? extends Integer> function) {
            Object object = this.sync;
            synchronized (object) {
                this.map.replaceAll(function);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public int putIfAbsent(long key, int value) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.putIfAbsent(key, value);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean remove(long key, int value) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.remove(key, value);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public int replace(long key, int value) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.replace(key, value);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean replace(long key, int oldValue, int newValue) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.replace(key, oldValue, newValue);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public int computeIfAbsent(long key, LongToIntFunction mappingFunction) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.computeIfAbsent(key, mappingFunction);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public int computeIfAbsentNullable(long key, LongFunction<? extends Integer> mappingFunction) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.computeIfAbsentNullable(key, mappingFunction);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public int computeIfAbsentPartial(long key, Long2IntFunction mappingFunction) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.computeIfAbsentPartial(key, mappingFunction);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public int computeIfPresent(long key, BiFunction<? super Long, ? super Integer, ? extends Integer> remappingFunction) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.computeIfPresent(key, remappingFunction);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public int compute(long key, BiFunction<? super Long, ? super Integer, ? extends Integer> remappingFunction) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.compute(key, remappingFunction);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public int merge(long key, int value, BiFunction<? super Integer, ? super Integer, ? extends Integer> remappingFunction) {
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
        public Integer replace(Long key, Integer value) {
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
        public boolean replace(Long key, Integer oldValue, Integer newValue) {
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
        public Integer putIfAbsent(Long key, Integer value) {
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
        public Integer computeIfAbsent(Long key, Function<? super Long, ? extends Integer> mappingFunction) {
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
        public Integer computeIfPresent(Long key, BiFunction<? super Long, ? super Integer, ? extends Integer> remappingFunction) {
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
        public Integer compute(Long key, BiFunction<? super Long, ? super Integer, ? extends Integer> remappingFunction) {
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
        public Integer merge(Long key, Integer value, BiFunction<? super Integer, ? super Integer, ? extends Integer> remappingFunction) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.merge(key, value, remappingFunction);
            }
        }
    }

    public static class Singleton
    extends Long2IntFunctions.Singleton
    implements Long2IntMap,
    Serializable,
    Cloneable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected transient ObjectSet<Long2IntMap.Entry> entries;
        protected transient LongSet keys;
        protected transient IntCollection values;

        protected Singleton(long key, int value) {
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
        public void putAll(Map<? extends Long, ? extends Integer> m) {
            throw new UnsupportedOperationException();
        }

        @Override
        public ObjectSet<Long2IntMap.Entry> long2IntEntrySet() {
            if (this.entries == null) {
                this.entries = ObjectSets.singleton(new AbstractLong2IntMap.BasicEntry(this.key, this.value));
            }
            return this.entries;
        }

        @Deprecated
        @Override
        public ObjectSet<Map.Entry<Long, Integer>> entrySet() {
            return this.long2IntEntrySet();
        }

        @Override
        public LongSet keySet() {
            if (this.keys == null) {
                this.keys = LongSets.singleton(this.key);
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
            return HashCommon.long2int(this.key) ^ this.value;
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
    extends Long2IntFunctions.EmptyFunction
    implements Long2IntMap,
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
        public void putAll(Map<? extends Long, ? extends Integer> m) {
            throw new UnsupportedOperationException();
        }

        @Override
        public ObjectSet<Long2IntMap.Entry> long2IntEntrySet() {
            return ObjectSets.EMPTY_SET;
        }

        @Override
        public LongSet keySet() {
            return LongSets.EMPTY_SET;
        }

        @Override
        public IntCollection values() {
            return IntSets.EMPTY_SET;
        }

        @Override
        public Object clone() {
            return Long2IntMaps.EMPTY_MAP;
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

