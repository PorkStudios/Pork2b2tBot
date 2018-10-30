/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.booleans.BooleanCollection;
import it.unimi.dsi.fastutil.booleans.BooleanCollections;
import it.unimi.dsi.fastutil.booleans.BooleanSets;
import it.unimi.dsi.fastutil.longs.AbstractLong2BooleanMap;
import it.unimi.dsi.fastutil.longs.Long2BooleanFunction;
import it.unimi.dsi.fastutil.longs.Long2BooleanFunctions;
import it.unimi.dsi.fastutil.longs.Long2BooleanMap;
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
import java.util.function.LongPredicate;

public final class Long2BooleanMaps {
    public static final EmptyMap EMPTY_MAP = new EmptyMap();

    private Long2BooleanMaps() {
    }

    public static ObjectIterator<Long2BooleanMap.Entry> fastIterator(Long2BooleanMap map) {
        ObjectSet<Long2BooleanMap.Entry> entries = map.long2BooleanEntrySet();
        return entries instanceof Long2BooleanMap.FastEntrySet ? ((Long2BooleanMap.FastEntrySet)entries).fastIterator() : entries.iterator();
    }

    public static void fastForEach(Long2BooleanMap map, Consumer<? super Long2BooleanMap.Entry> consumer) {
        ObjectSet<Long2BooleanMap.Entry> entries = map.long2BooleanEntrySet();
        if (entries instanceof Long2BooleanMap.FastEntrySet) {
            ((Long2BooleanMap.FastEntrySet)entries).fastForEach(consumer);
        } else {
            entries.forEach(consumer);
        }
    }

    public static ObjectIterable<Long2BooleanMap.Entry> fastIterable(Long2BooleanMap map) {
        final ObjectSet<Long2BooleanMap.Entry> entries = map.long2BooleanEntrySet();
        return entries instanceof Long2BooleanMap.FastEntrySet ? new ObjectIterable<Long2BooleanMap.Entry>(){

            @Override
            public ObjectIterator<Long2BooleanMap.Entry> iterator() {
                return ((Long2BooleanMap.FastEntrySet)entries).fastIterator();
            }

            @Override
            public void forEach(Consumer<? super Long2BooleanMap.Entry> consumer) {
                ((Long2BooleanMap.FastEntrySet)entries).fastForEach(consumer);
            }
        } : entries;
    }

    public static Long2BooleanMap singleton(long key, boolean value) {
        return new Singleton(key, value);
    }

    public static Long2BooleanMap singleton(Long key, Boolean value) {
        return new Singleton(key, value);
    }

    public static Long2BooleanMap synchronize(Long2BooleanMap m) {
        return new SynchronizedMap(m);
    }

    public static Long2BooleanMap synchronize(Long2BooleanMap m, Object sync) {
        return new SynchronizedMap(m, sync);
    }

    public static Long2BooleanMap unmodifiable(Long2BooleanMap m) {
        return new UnmodifiableMap(m);
    }

    public static class UnmodifiableMap
    extends Long2BooleanFunctions.UnmodifiableFunction
    implements Long2BooleanMap,
    Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final Long2BooleanMap map;
        protected transient ObjectSet<Long2BooleanMap.Entry> entries;
        protected transient LongSet keys;
        protected transient BooleanCollection values;

        protected UnmodifiableMap(Long2BooleanMap m) {
            super(m);
            this.map = m;
        }

        @Override
        public boolean containsValue(boolean v) {
            return this.map.containsValue(v);
        }

        @Deprecated
        @Override
        public boolean containsValue(Object ov) {
            return this.map.containsValue(ov);
        }

        @Override
        public void putAll(Map<? extends Long, ? extends Boolean> m) {
            throw new UnsupportedOperationException();
        }

        @Override
        public ObjectSet<Long2BooleanMap.Entry> long2BooleanEntrySet() {
            if (this.entries == null) {
                this.entries = ObjectSets.unmodifiable(this.map.long2BooleanEntrySet());
            }
            return this.entries;
        }

        @Deprecated
        @Override
        public ObjectSet<Map.Entry<Long, Boolean>> entrySet() {
            return this.long2BooleanEntrySet();
        }

        @Override
        public LongSet keySet() {
            if (this.keys == null) {
                this.keys = LongSets.unmodifiable(this.map.keySet());
            }
            return this.keys;
        }

        @Override
        public BooleanCollection values() {
            if (this.values == null) {
                return BooleanCollections.unmodifiable(this.map.values());
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
        public boolean getOrDefault(long key, boolean defaultValue) {
            return this.map.getOrDefault(key, defaultValue);
        }

        @Override
        public void forEach(BiConsumer<? super Long, ? super Boolean> action) {
            this.map.forEach(action);
        }

        @Override
        public void replaceAll(BiFunction<? super Long, ? super Boolean, ? extends Boolean> function) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean putIfAbsent(long key, boolean value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean remove(long key, boolean value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean replace(long key, boolean value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean replace(long key, boolean oldValue, boolean newValue) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean computeIfAbsent(long key, LongPredicate mappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean computeIfAbsentNullable(long key, LongFunction<? extends Boolean> mappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean computeIfAbsentPartial(long key, Long2BooleanFunction mappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean computeIfPresent(long key, BiFunction<? super Long, ? super Boolean, ? extends Boolean> remappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean compute(long key, BiFunction<? super Long, ? super Boolean, ? extends Boolean> remappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean merge(long key, boolean value, BiFunction<? super Boolean, ? super Boolean, ? extends Boolean> remappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public Boolean getOrDefault(Object key, Boolean defaultValue) {
            return this.map.getOrDefault(key, defaultValue);
        }

        @Deprecated
        @Override
        public boolean remove(Object key, Object value) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public Boolean replace(Long key, Boolean value) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public boolean replace(Long key, Boolean oldValue, Boolean newValue) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public Boolean putIfAbsent(Long key, Boolean value) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public Boolean computeIfAbsent(Long key, Function<? super Long, ? extends Boolean> mappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public Boolean computeIfPresent(Long key, BiFunction<? super Long, ? super Boolean, ? extends Boolean> remappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public Boolean compute(Long key, BiFunction<? super Long, ? super Boolean, ? extends Boolean> remappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public Boolean merge(Long key, Boolean value, BiFunction<? super Boolean, ? super Boolean, ? extends Boolean> remappingFunction) {
            throw new UnsupportedOperationException();
        }
    }

    public static class SynchronizedMap
    extends Long2BooleanFunctions.SynchronizedFunction
    implements Long2BooleanMap,
    Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final Long2BooleanMap map;
        protected transient ObjectSet<Long2BooleanMap.Entry> entries;
        protected transient LongSet keys;
        protected transient BooleanCollection values;

        protected SynchronizedMap(Long2BooleanMap m, Object sync) {
            super(m, sync);
            this.map = m;
        }

        protected SynchronizedMap(Long2BooleanMap m) {
            super(m);
            this.map = m;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean containsValue(boolean v) {
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
        public void putAll(Map<? extends Long, ? extends Boolean> m) {
            Object object = this.sync;
            synchronized (object) {
                this.map.putAll(m);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public ObjectSet<Long2BooleanMap.Entry> long2BooleanEntrySet() {
            Object object = this.sync;
            synchronized (object) {
                if (this.entries == null) {
                    this.entries = ObjectSets.synchronize(this.map.long2BooleanEntrySet(), this.sync);
                }
                return this.entries;
            }
        }

        @Deprecated
        @Override
        public ObjectSet<Map.Entry<Long, Boolean>> entrySet() {
            return this.long2BooleanEntrySet();
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
        public BooleanCollection values() {
            Object object = this.sync;
            synchronized (object) {
                if (this.values == null) {
                    return BooleanCollections.synchronize(this.map.values(), this.sync);
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
        public boolean getOrDefault(long key, boolean defaultValue) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.getOrDefault(key, defaultValue);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void forEach(BiConsumer<? super Long, ? super Boolean> action) {
            Object object = this.sync;
            synchronized (object) {
                this.map.forEach(action);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void replaceAll(BiFunction<? super Long, ? super Boolean, ? extends Boolean> function) {
            Object object = this.sync;
            synchronized (object) {
                this.map.replaceAll(function);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean putIfAbsent(long key, boolean value) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.putIfAbsent(key, value);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean remove(long key, boolean value) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.remove(key, value);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean replace(long key, boolean value) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.replace(key, value);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean replace(long key, boolean oldValue, boolean newValue) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.replace(key, oldValue, newValue);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean computeIfAbsent(long key, LongPredicate mappingFunction) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.computeIfAbsent(key, mappingFunction);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean computeIfAbsentNullable(long key, LongFunction<? extends Boolean> mappingFunction) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.computeIfAbsentNullable(key, mappingFunction);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean computeIfAbsentPartial(long key, Long2BooleanFunction mappingFunction) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.computeIfAbsentPartial(key, mappingFunction);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean computeIfPresent(long key, BiFunction<? super Long, ? super Boolean, ? extends Boolean> remappingFunction) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.computeIfPresent(key, remappingFunction);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean compute(long key, BiFunction<? super Long, ? super Boolean, ? extends Boolean> remappingFunction) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.compute(key, remappingFunction);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean merge(long key, boolean value, BiFunction<? super Boolean, ? super Boolean, ? extends Boolean> remappingFunction) {
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
        public Boolean getOrDefault(Object key, Boolean defaultValue) {
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
        public Boolean replace(Long key, Boolean value) {
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
        public boolean replace(Long key, Boolean oldValue, Boolean newValue) {
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
        public Boolean putIfAbsent(Long key, Boolean value) {
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
        public Boolean computeIfAbsent(Long key, Function<? super Long, ? extends Boolean> mappingFunction) {
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
        public Boolean computeIfPresent(Long key, BiFunction<? super Long, ? super Boolean, ? extends Boolean> remappingFunction) {
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
        public Boolean compute(Long key, BiFunction<? super Long, ? super Boolean, ? extends Boolean> remappingFunction) {
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
        public Boolean merge(Long key, Boolean value, BiFunction<? super Boolean, ? super Boolean, ? extends Boolean> remappingFunction) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.merge(key, value, remappingFunction);
            }
        }
    }

    public static class Singleton
    extends Long2BooleanFunctions.Singleton
    implements Long2BooleanMap,
    Serializable,
    Cloneable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected transient ObjectSet<Long2BooleanMap.Entry> entries;
        protected transient LongSet keys;
        protected transient BooleanCollection values;

        protected Singleton(long key, boolean value) {
            super(key, value);
        }

        @Override
        public boolean containsValue(boolean v) {
            return this.value == v;
        }

        @Deprecated
        @Override
        public boolean containsValue(Object ov) {
            return (Boolean)ov == this.value;
        }

        @Override
        public void putAll(Map<? extends Long, ? extends Boolean> m) {
            throw new UnsupportedOperationException();
        }

        @Override
        public ObjectSet<Long2BooleanMap.Entry> long2BooleanEntrySet() {
            if (this.entries == null) {
                this.entries = ObjectSets.singleton(new AbstractLong2BooleanMap.BasicEntry(this.key, this.value));
            }
            return this.entries;
        }

        @Deprecated
        @Override
        public ObjectSet<Map.Entry<Long, Boolean>> entrySet() {
            return this.long2BooleanEntrySet();
        }

        @Override
        public LongSet keySet() {
            if (this.keys == null) {
                this.keys = LongSets.singleton(this.key);
            }
            return this.keys;
        }

        @Override
        public BooleanCollection values() {
            if (this.values == null) {
                this.values = BooleanSets.singleton(this.value);
            }
            return this.values;
        }

        @Override
        public boolean isEmpty() {
            return false;
        }

        @Override
        public int hashCode() {
            return HashCommon.long2int(this.key) ^ (this.value ? 1231 : 1237);
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
    extends Long2BooleanFunctions.EmptyFunction
    implements Long2BooleanMap,
    Serializable,
    Cloneable {
        private static final long serialVersionUID = -7046029254386353129L;

        protected EmptyMap() {
        }

        @Override
        public boolean containsValue(boolean v) {
            return false;
        }

        @Deprecated
        @Override
        public boolean containsValue(Object ov) {
            return false;
        }

        @Override
        public void putAll(Map<? extends Long, ? extends Boolean> m) {
            throw new UnsupportedOperationException();
        }

        @Override
        public ObjectSet<Long2BooleanMap.Entry> long2BooleanEntrySet() {
            return ObjectSets.EMPTY_SET;
        }

        @Override
        public LongSet keySet() {
            return LongSets.EMPTY_SET;
        }

        @Override
        public BooleanCollection values() {
            return BooleanSets.EMPTY_SET;
        }

        @Override
        public Object clone() {
            return Long2BooleanMaps.EMPTY_MAP;
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

