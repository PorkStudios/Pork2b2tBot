/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.chars.AbstractChar2LongMap;
import it.unimi.dsi.fastutil.chars.Char2LongFunction;
import it.unimi.dsi.fastutil.chars.Char2LongFunctions;
import it.unimi.dsi.fastutil.chars.Char2LongMap;
import it.unimi.dsi.fastutil.chars.CharSet;
import it.unimi.dsi.fastutil.chars.CharSets;
import it.unimi.dsi.fastutil.longs.LongCollection;
import it.unimi.dsi.fastutil.longs.LongCollections;
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
import java.util.function.IntFunction;
import java.util.function.IntToLongFunction;

public final class Char2LongMaps {
    public static final EmptyMap EMPTY_MAP = new EmptyMap();

    private Char2LongMaps() {
    }

    public static ObjectIterator<Char2LongMap.Entry> fastIterator(Char2LongMap map) {
        ObjectSet<Char2LongMap.Entry> entries = map.char2LongEntrySet();
        return entries instanceof Char2LongMap.FastEntrySet ? ((Char2LongMap.FastEntrySet)entries).fastIterator() : entries.iterator();
    }

    public static void fastForEach(Char2LongMap map, Consumer<? super Char2LongMap.Entry> consumer) {
        ObjectSet<Char2LongMap.Entry> entries = map.char2LongEntrySet();
        if (entries instanceof Char2LongMap.FastEntrySet) {
            ((Char2LongMap.FastEntrySet)entries).fastForEach(consumer);
        } else {
            entries.forEach(consumer);
        }
    }

    public static ObjectIterable<Char2LongMap.Entry> fastIterable(Char2LongMap map) {
        final ObjectSet<Char2LongMap.Entry> entries = map.char2LongEntrySet();
        return entries instanceof Char2LongMap.FastEntrySet ? new ObjectIterable<Char2LongMap.Entry>(){

            @Override
            public ObjectIterator<Char2LongMap.Entry> iterator() {
                return ((Char2LongMap.FastEntrySet)entries).fastIterator();
            }

            @Override
            public void forEach(Consumer<? super Char2LongMap.Entry> consumer) {
                ((Char2LongMap.FastEntrySet)entries).fastForEach(consumer);
            }
        } : entries;
    }

    public static Char2LongMap singleton(char key, long value) {
        return new Singleton(key, value);
    }

    public static Char2LongMap singleton(Character key, Long value) {
        return new Singleton(key.charValue(), value);
    }

    public static Char2LongMap synchronize(Char2LongMap m) {
        return new SynchronizedMap(m);
    }

    public static Char2LongMap synchronize(Char2LongMap m, Object sync) {
        return new SynchronizedMap(m, sync);
    }

    public static Char2LongMap unmodifiable(Char2LongMap m) {
        return new UnmodifiableMap(m);
    }

    public static class UnmodifiableMap
    extends Char2LongFunctions.UnmodifiableFunction
    implements Char2LongMap,
    Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final Char2LongMap map;
        protected transient ObjectSet<Char2LongMap.Entry> entries;
        protected transient CharSet keys;
        protected transient LongCollection values;

        protected UnmodifiableMap(Char2LongMap m) {
            super(m);
            this.map = m;
        }

        @Override
        public boolean containsValue(long v) {
            return this.map.containsValue(v);
        }

        @Deprecated
        @Override
        public boolean containsValue(Object ov) {
            return this.map.containsValue(ov);
        }

        @Override
        public void putAll(Map<? extends Character, ? extends Long> m) {
            throw new UnsupportedOperationException();
        }

        @Override
        public ObjectSet<Char2LongMap.Entry> char2LongEntrySet() {
            if (this.entries == null) {
                this.entries = ObjectSets.unmodifiable(this.map.char2LongEntrySet());
            }
            return this.entries;
        }

        @Deprecated
        @Override
        public ObjectSet<Map.Entry<Character, Long>> entrySet() {
            return this.char2LongEntrySet();
        }

        @Override
        public CharSet keySet() {
            if (this.keys == null) {
                this.keys = CharSets.unmodifiable(this.map.keySet());
            }
            return this.keys;
        }

        @Override
        public LongCollection values() {
            if (this.values == null) {
                return LongCollections.unmodifiable(this.map.values());
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
        public long getOrDefault(char key, long defaultValue) {
            return this.map.getOrDefault(key, defaultValue);
        }

        @Override
        public void forEach(BiConsumer<? super Character, ? super Long> action) {
            this.map.forEach(action);
        }

        @Override
        public void replaceAll(BiFunction<? super Character, ? super Long, ? extends Long> function) {
            throw new UnsupportedOperationException();
        }

        @Override
        public long putIfAbsent(char key, long value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean remove(char key, long value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public long replace(char key, long value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean replace(char key, long oldValue, long newValue) {
            throw new UnsupportedOperationException();
        }

        @Override
        public long computeIfAbsent(char key, IntToLongFunction mappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Override
        public long computeIfAbsentNullable(char key, IntFunction<? extends Long> mappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Override
        public long computeIfAbsentPartial(char key, Char2LongFunction mappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Override
        public long computeIfPresent(char key, BiFunction<? super Character, ? super Long, ? extends Long> remappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Override
        public long compute(char key, BiFunction<? super Character, ? super Long, ? extends Long> remappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Override
        public long merge(char key, long value, BiFunction<? super Long, ? super Long, ? extends Long> remappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public Long getOrDefault(Object key, Long defaultValue) {
            return this.map.getOrDefault(key, defaultValue);
        }

        @Deprecated
        @Override
        public boolean remove(Object key, Object value) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public Long replace(Character key, Long value) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public boolean replace(Character key, Long oldValue, Long newValue) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public Long putIfAbsent(Character key, Long value) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public Long computeIfAbsent(Character key, Function<? super Character, ? extends Long> mappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public Long computeIfPresent(Character key, BiFunction<? super Character, ? super Long, ? extends Long> remappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public Long compute(Character key, BiFunction<? super Character, ? super Long, ? extends Long> remappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public Long merge(Character key, Long value, BiFunction<? super Long, ? super Long, ? extends Long> remappingFunction) {
            throw new UnsupportedOperationException();
        }
    }

    public static class SynchronizedMap
    extends Char2LongFunctions.SynchronizedFunction
    implements Char2LongMap,
    Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final Char2LongMap map;
        protected transient ObjectSet<Char2LongMap.Entry> entries;
        protected transient CharSet keys;
        protected transient LongCollection values;

        protected SynchronizedMap(Char2LongMap m, Object sync) {
            super(m, sync);
            this.map = m;
        }

        protected SynchronizedMap(Char2LongMap m) {
            super(m);
            this.map = m;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean containsValue(long v) {
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
        public void putAll(Map<? extends Character, ? extends Long> m) {
            Object object = this.sync;
            synchronized (object) {
                this.map.putAll(m);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public ObjectSet<Char2LongMap.Entry> char2LongEntrySet() {
            Object object = this.sync;
            synchronized (object) {
                if (this.entries == null) {
                    this.entries = ObjectSets.synchronize(this.map.char2LongEntrySet(), this.sync);
                }
                return this.entries;
            }
        }

        @Deprecated
        @Override
        public ObjectSet<Map.Entry<Character, Long>> entrySet() {
            return this.char2LongEntrySet();
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
        public LongCollection values() {
            Object object = this.sync;
            synchronized (object) {
                if (this.values == null) {
                    return LongCollections.synchronize(this.map.values(), this.sync);
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
        public long getOrDefault(char key, long defaultValue) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.getOrDefault(key, defaultValue);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void forEach(BiConsumer<? super Character, ? super Long> action) {
            Object object = this.sync;
            synchronized (object) {
                this.map.forEach(action);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void replaceAll(BiFunction<? super Character, ? super Long, ? extends Long> function) {
            Object object = this.sync;
            synchronized (object) {
                this.map.replaceAll(function);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public long putIfAbsent(char key, long value) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.putIfAbsent(key, value);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean remove(char key, long value) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.remove(key, value);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public long replace(char key, long value) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.replace(key, value);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean replace(char key, long oldValue, long newValue) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.replace(key, oldValue, newValue);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public long computeIfAbsent(char key, IntToLongFunction mappingFunction) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.computeIfAbsent(key, mappingFunction);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public long computeIfAbsentNullable(char key, IntFunction<? extends Long> mappingFunction) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.computeIfAbsentNullable(key, mappingFunction);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public long computeIfAbsentPartial(char key, Char2LongFunction mappingFunction) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.computeIfAbsentPartial(key, mappingFunction);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public long computeIfPresent(char key, BiFunction<? super Character, ? super Long, ? extends Long> remappingFunction) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.computeIfPresent(key, remappingFunction);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public long compute(char key, BiFunction<? super Character, ? super Long, ? extends Long> remappingFunction) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.compute(key, remappingFunction);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public long merge(char key, long value, BiFunction<? super Long, ? super Long, ? extends Long> remappingFunction) {
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
        public Long getOrDefault(Object key, Long defaultValue) {
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
        public Long replace(Character key, Long value) {
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
        public boolean replace(Character key, Long oldValue, Long newValue) {
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
        public Long putIfAbsent(Character key, Long value) {
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
        public Long computeIfAbsent(Character key, Function<? super Character, ? extends Long> mappingFunction) {
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
        public Long computeIfPresent(Character key, BiFunction<? super Character, ? super Long, ? extends Long> remappingFunction) {
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
        public Long compute(Character key, BiFunction<? super Character, ? super Long, ? extends Long> remappingFunction) {
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
        public Long merge(Character key, Long value, BiFunction<? super Long, ? super Long, ? extends Long> remappingFunction) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.merge(key, value, remappingFunction);
            }
        }
    }

    public static class Singleton
    extends Char2LongFunctions.Singleton
    implements Char2LongMap,
    Serializable,
    Cloneable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected transient ObjectSet<Char2LongMap.Entry> entries;
        protected transient CharSet keys;
        protected transient LongCollection values;

        protected Singleton(char key, long value) {
            super(key, value);
        }

        @Override
        public boolean containsValue(long v) {
            return this.value == v;
        }

        @Deprecated
        @Override
        public boolean containsValue(Object ov) {
            return (Long)ov == this.value;
        }

        @Override
        public void putAll(Map<? extends Character, ? extends Long> m) {
            throw new UnsupportedOperationException();
        }

        @Override
        public ObjectSet<Char2LongMap.Entry> char2LongEntrySet() {
            if (this.entries == null) {
                this.entries = ObjectSets.singleton(new AbstractChar2LongMap.BasicEntry(this.key, this.value));
            }
            return this.entries;
        }

        @Deprecated
        @Override
        public ObjectSet<Map.Entry<Character, Long>> entrySet() {
            return this.char2LongEntrySet();
        }

        @Override
        public CharSet keySet() {
            if (this.keys == null) {
                this.keys = CharSets.singleton(this.key);
            }
            return this.keys;
        }

        @Override
        public LongCollection values() {
            if (this.values == null) {
                this.values = LongSets.singleton(this.value);
            }
            return this.values;
        }

        @Override
        public boolean isEmpty() {
            return false;
        }

        @Override
        public int hashCode() {
            return this.key ^ HashCommon.long2int(this.value);
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
    extends Char2LongFunctions.EmptyFunction
    implements Char2LongMap,
    Serializable,
    Cloneable {
        private static final long serialVersionUID = -7046029254386353129L;

        protected EmptyMap() {
        }

        @Override
        public boolean containsValue(long v) {
            return false;
        }

        @Deprecated
        @Override
        public boolean containsValue(Object ov) {
            return false;
        }

        @Override
        public void putAll(Map<? extends Character, ? extends Long> m) {
            throw new UnsupportedOperationException();
        }

        @Override
        public ObjectSet<Char2LongMap.Entry> char2LongEntrySet() {
            return ObjectSets.EMPTY_SET;
        }

        @Override
        public CharSet keySet() {
            return CharSets.EMPTY_SET;
        }

        @Override
        public LongCollection values() {
            return LongSets.EMPTY_SET;
        }

        @Override
        public Object clone() {
            return Char2LongMaps.EMPTY_MAP;
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

