/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.booleans.BooleanCollection;
import it.unimi.dsi.fastutil.booleans.BooleanCollections;
import it.unimi.dsi.fastutil.booleans.BooleanSets;
import it.unimi.dsi.fastutil.bytes.AbstractByte2BooleanMap;
import it.unimi.dsi.fastutil.bytes.Byte2BooleanFunction;
import it.unimi.dsi.fastutil.bytes.Byte2BooleanFunctions;
import it.unimi.dsi.fastutil.bytes.Byte2BooleanMap;
import it.unimi.dsi.fastutil.bytes.ByteSet;
import it.unimi.dsi.fastutil.bytes.ByteSets;
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
import java.util.function.IntPredicate;

public final class Byte2BooleanMaps {
    public static final EmptyMap EMPTY_MAP = new EmptyMap();

    private Byte2BooleanMaps() {
    }

    public static ObjectIterator<Byte2BooleanMap.Entry> fastIterator(Byte2BooleanMap map) {
        ObjectSet<Byte2BooleanMap.Entry> entries = map.byte2BooleanEntrySet();
        return entries instanceof Byte2BooleanMap.FastEntrySet ? ((Byte2BooleanMap.FastEntrySet)entries).fastIterator() : entries.iterator();
    }

    public static void fastForEach(Byte2BooleanMap map, Consumer<? super Byte2BooleanMap.Entry> consumer) {
        ObjectSet<Byte2BooleanMap.Entry> entries = map.byte2BooleanEntrySet();
        if (entries instanceof Byte2BooleanMap.FastEntrySet) {
            ((Byte2BooleanMap.FastEntrySet)entries).fastForEach(consumer);
        } else {
            entries.forEach(consumer);
        }
    }

    public static ObjectIterable<Byte2BooleanMap.Entry> fastIterable(Byte2BooleanMap map) {
        final ObjectSet<Byte2BooleanMap.Entry> entries = map.byte2BooleanEntrySet();
        return entries instanceof Byte2BooleanMap.FastEntrySet ? new ObjectIterable<Byte2BooleanMap.Entry>(){

            @Override
            public ObjectIterator<Byte2BooleanMap.Entry> iterator() {
                return ((Byte2BooleanMap.FastEntrySet)entries).fastIterator();
            }

            @Override
            public void forEach(Consumer<? super Byte2BooleanMap.Entry> consumer) {
                ((Byte2BooleanMap.FastEntrySet)entries).fastForEach(consumer);
            }
        } : entries;
    }

    public static Byte2BooleanMap singleton(byte key, boolean value) {
        return new Singleton(key, value);
    }

    public static Byte2BooleanMap singleton(Byte key, Boolean value) {
        return new Singleton(key, value);
    }

    public static Byte2BooleanMap synchronize(Byte2BooleanMap m) {
        return new SynchronizedMap(m);
    }

    public static Byte2BooleanMap synchronize(Byte2BooleanMap m, Object sync) {
        return new SynchronizedMap(m, sync);
    }

    public static Byte2BooleanMap unmodifiable(Byte2BooleanMap m) {
        return new UnmodifiableMap(m);
    }

    public static class UnmodifiableMap
    extends Byte2BooleanFunctions.UnmodifiableFunction
    implements Byte2BooleanMap,
    Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final Byte2BooleanMap map;
        protected transient ObjectSet<Byte2BooleanMap.Entry> entries;
        protected transient ByteSet keys;
        protected transient BooleanCollection values;

        protected UnmodifiableMap(Byte2BooleanMap m) {
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
        public void putAll(Map<? extends Byte, ? extends Boolean> m) {
            throw new UnsupportedOperationException();
        }

        @Override
        public ObjectSet<Byte2BooleanMap.Entry> byte2BooleanEntrySet() {
            if (this.entries == null) {
                this.entries = ObjectSets.unmodifiable(this.map.byte2BooleanEntrySet());
            }
            return this.entries;
        }

        @Deprecated
        @Override
        public ObjectSet<Map.Entry<Byte, Boolean>> entrySet() {
            return this.byte2BooleanEntrySet();
        }

        @Override
        public ByteSet keySet() {
            if (this.keys == null) {
                this.keys = ByteSets.unmodifiable(this.map.keySet());
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
        public boolean getOrDefault(byte key, boolean defaultValue) {
            return this.map.getOrDefault(key, defaultValue);
        }

        @Override
        public void forEach(BiConsumer<? super Byte, ? super Boolean> action) {
            this.map.forEach(action);
        }

        @Override
        public void replaceAll(BiFunction<? super Byte, ? super Boolean, ? extends Boolean> function) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean putIfAbsent(byte key, boolean value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean remove(byte key, boolean value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean replace(byte key, boolean value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean replace(byte key, boolean oldValue, boolean newValue) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean computeIfAbsent(byte key, IntPredicate mappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean computeIfAbsentNullable(byte key, IntFunction<? extends Boolean> mappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean computeIfAbsentPartial(byte key, Byte2BooleanFunction mappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean computeIfPresent(byte key, BiFunction<? super Byte, ? super Boolean, ? extends Boolean> remappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean compute(byte key, BiFunction<? super Byte, ? super Boolean, ? extends Boolean> remappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean merge(byte key, boolean value, BiFunction<? super Boolean, ? super Boolean, ? extends Boolean> remappingFunction) {
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
        public Boolean replace(Byte key, Boolean value) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public boolean replace(Byte key, Boolean oldValue, Boolean newValue) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public Boolean putIfAbsent(Byte key, Boolean value) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public Boolean computeIfAbsent(Byte key, Function<? super Byte, ? extends Boolean> mappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public Boolean computeIfPresent(Byte key, BiFunction<? super Byte, ? super Boolean, ? extends Boolean> remappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public Boolean compute(Byte key, BiFunction<? super Byte, ? super Boolean, ? extends Boolean> remappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public Boolean merge(Byte key, Boolean value, BiFunction<? super Boolean, ? super Boolean, ? extends Boolean> remappingFunction) {
            throw new UnsupportedOperationException();
        }
    }

    public static class SynchronizedMap
    extends Byte2BooleanFunctions.SynchronizedFunction
    implements Byte2BooleanMap,
    Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final Byte2BooleanMap map;
        protected transient ObjectSet<Byte2BooleanMap.Entry> entries;
        protected transient ByteSet keys;
        protected transient BooleanCollection values;

        protected SynchronizedMap(Byte2BooleanMap m, Object sync) {
            super(m, sync);
            this.map = m;
        }

        protected SynchronizedMap(Byte2BooleanMap m) {
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
        public void putAll(Map<? extends Byte, ? extends Boolean> m) {
            Object object = this.sync;
            synchronized (object) {
                this.map.putAll(m);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public ObjectSet<Byte2BooleanMap.Entry> byte2BooleanEntrySet() {
            Object object = this.sync;
            synchronized (object) {
                if (this.entries == null) {
                    this.entries = ObjectSets.synchronize(this.map.byte2BooleanEntrySet(), this.sync);
                }
                return this.entries;
            }
        }

        @Deprecated
        @Override
        public ObjectSet<Map.Entry<Byte, Boolean>> entrySet() {
            return this.byte2BooleanEntrySet();
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
        public boolean getOrDefault(byte key, boolean defaultValue) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.getOrDefault(key, defaultValue);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void forEach(BiConsumer<? super Byte, ? super Boolean> action) {
            Object object = this.sync;
            synchronized (object) {
                this.map.forEach(action);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void replaceAll(BiFunction<? super Byte, ? super Boolean, ? extends Boolean> function) {
            Object object = this.sync;
            synchronized (object) {
                this.map.replaceAll(function);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean putIfAbsent(byte key, boolean value) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.putIfAbsent(key, value);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean remove(byte key, boolean value) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.remove(key, value);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean replace(byte key, boolean value) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.replace(key, value);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean replace(byte key, boolean oldValue, boolean newValue) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.replace(key, oldValue, newValue);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean computeIfAbsent(byte key, IntPredicate mappingFunction) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.computeIfAbsent(key, mappingFunction);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean computeIfAbsentNullable(byte key, IntFunction<? extends Boolean> mappingFunction) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.computeIfAbsentNullable(key, mappingFunction);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean computeIfAbsentPartial(byte key, Byte2BooleanFunction mappingFunction) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.computeIfAbsentPartial(key, mappingFunction);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean computeIfPresent(byte key, BiFunction<? super Byte, ? super Boolean, ? extends Boolean> remappingFunction) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.computeIfPresent(key, remappingFunction);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean compute(byte key, BiFunction<? super Byte, ? super Boolean, ? extends Boolean> remappingFunction) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.compute(key, remappingFunction);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean merge(byte key, boolean value, BiFunction<? super Boolean, ? super Boolean, ? extends Boolean> remappingFunction) {
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
        public Boolean replace(Byte key, Boolean value) {
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
        public boolean replace(Byte key, Boolean oldValue, Boolean newValue) {
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
        public Boolean putIfAbsent(Byte key, Boolean value) {
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
        public Boolean computeIfAbsent(Byte key, Function<? super Byte, ? extends Boolean> mappingFunction) {
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
        public Boolean computeIfPresent(Byte key, BiFunction<? super Byte, ? super Boolean, ? extends Boolean> remappingFunction) {
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
        public Boolean compute(Byte key, BiFunction<? super Byte, ? super Boolean, ? extends Boolean> remappingFunction) {
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
        public Boolean merge(Byte key, Boolean value, BiFunction<? super Boolean, ? super Boolean, ? extends Boolean> remappingFunction) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.merge(key, value, remappingFunction);
            }
        }
    }

    public static class Singleton
    extends Byte2BooleanFunctions.Singleton
    implements Byte2BooleanMap,
    Serializable,
    Cloneable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected transient ObjectSet<Byte2BooleanMap.Entry> entries;
        protected transient ByteSet keys;
        protected transient BooleanCollection values;

        protected Singleton(byte key, boolean value) {
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
        public void putAll(Map<? extends Byte, ? extends Boolean> m) {
            throw new UnsupportedOperationException();
        }

        @Override
        public ObjectSet<Byte2BooleanMap.Entry> byte2BooleanEntrySet() {
            if (this.entries == null) {
                this.entries = ObjectSets.singleton(new AbstractByte2BooleanMap.BasicEntry(this.key, this.value));
            }
            return this.entries;
        }

        @Deprecated
        @Override
        public ObjectSet<Map.Entry<Byte, Boolean>> entrySet() {
            return this.byte2BooleanEntrySet();
        }

        @Override
        public ByteSet keySet() {
            if (this.keys == null) {
                this.keys = ByteSets.singleton(this.key);
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
            return this.key ^ (this.value ? 1231 : 1237);
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
    extends Byte2BooleanFunctions.EmptyFunction
    implements Byte2BooleanMap,
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
        public void putAll(Map<? extends Byte, ? extends Boolean> m) {
            throw new UnsupportedOperationException();
        }

        @Override
        public ObjectSet<Byte2BooleanMap.Entry> byte2BooleanEntrySet() {
            return ObjectSets.EMPTY_SET;
        }

        @Override
        public ByteSet keySet() {
            return ByteSets.EMPTY_SET;
        }

        @Override
        public BooleanCollection values() {
            return BooleanSets.EMPTY_SET;
        }

        @Override
        public Object clone() {
            return Byte2BooleanMaps.EMPTY_MAP;
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

