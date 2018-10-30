/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.booleans.BooleanCollection;
import it.unimi.dsi.fastutil.booleans.BooleanCollections;
import it.unimi.dsi.fastutil.booleans.BooleanSets;
import it.unimi.dsi.fastutil.chars.AbstractChar2BooleanMap;
import it.unimi.dsi.fastutil.chars.Char2BooleanFunction;
import it.unimi.dsi.fastutil.chars.Char2BooleanFunctions;
import it.unimi.dsi.fastutil.chars.Char2BooleanMap;
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
import java.util.function.IntPredicate;

public final class Char2BooleanMaps {
    public static final EmptyMap EMPTY_MAP = new EmptyMap();

    private Char2BooleanMaps() {
    }

    public static ObjectIterator<Char2BooleanMap.Entry> fastIterator(Char2BooleanMap map) {
        ObjectSet<Char2BooleanMap.Entry> entries = map.char2BooleanEntrySet();
        return entries instanceof Char2BooleanMap.FastEntrySet ? ((Char2BooleanMap.FastEntrySet)entries).fastIterator() : entries.iterator();
    }

    public static void fastForEach(Char2BooleanMap map, Consumer<? super Char2BooleanMap.Entry> consumer) {
        ObjectSet<Char2BooleanMap.Entry> entries = map.char2BooleanEntrySet();
        if (entries instanceof Char2BooleanMap.FastEntrySet) {
            ((Char2BooleanMap.FastEntrySet)entries).fastForEach(consumer);
        } else {
            entries.forEach(consumer);
        }
    }

    public static ObjectIterable<Char2BooleanMap.Entry> fastIterable(Char2BooleanMap map) {
        final ObjectSet<Char2BooleanMap.Entry> entries = map.char2BooleanEntrySet();
        return entries instanceof Char2BooleanMap.FastEntrySet ? new ObjectIterable<Char2BooleanMap.Entry>(){

            @Override
            public ObjectIterator<Char2BooleanMap.Entry> iterator() {
                return ((Char2BooleanMap.FastEntrySet)entries).fastIterator();
            }

            @Override
            public void forEach(Consumer<? super Char2BooleanMap.Entry> consumer) {
                ((Char2BooleanMap.FastEntrySet)entries).fastForEach(consumer);
            }
        } : entries;
    }

    public static Char2BooleanMap singleton(char key, boolean value) {
        return new Singleton(key, value);
    }

    public static Char2BooleanMap singleton(Character key, Boolean value) {
        return new Singleton(key.charValue(), value);
    }

    public static Char2BooleanMap synchronize(Char2BooleanMap m) {
        return new SynchronizedMap(m);
    }

    public static Char2BooleanMap synchronize(Char2BooleanMap m, Object sync) {
        return new SynchronizedMap(m, sync);
    }

    public static Char2BooleanMap unmodifiable(Char2BooleanMap m) {
        return new UnmodifiableMap(m);
    }

    public static class UnmodifiableMap
    extends Char2BooleanFunctions.UnmodifiableFunction
    implements Char2BooleanMap,
    Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final Char2BooleanMap map;
        protected transient ObjectSet<Char2BooleanMap.Entry> entries;
        protected transient CharSet keys;
        protected transient BooleanCollection values;

        protected UnmodifiableMap(Char2BooleanMap m) {
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
        public void putAll(Map<? extends Character, ? extends Boolean> m) {
            throw new UnsupportedOperationException();
        }

        @Override
        public ObjectSet<Char2BooleanMap.Entry> char2BooleanEntrySet() {
            if (this.entries == null) {
                this.entries = ObjectSets.unmodifiable(this.map.char2BooleanEntrySet());
            }
            return this.entries;
        }

        @Deprecated
        @Override
        public ObjectSet<Map.Entry<Character, Boolean>> entrySet() {
            return this.char2BooleanEntrySet();
        }

        @Override
        public CharSet keySet() {
            if (this.keys == null) {
                this.keys = CharSets.unmodifiable(this.map.keySet());
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
        public boolean getOrDefault(char key, boolean defaultValue) {
            return this.map.getOrDefault(key, defaultValue);
        }

        @Override
        public void forEach(BiConsumer<? super Character, ? super Boolean> action) {
            this.map.forEach(action);
        }

        @Override
        public void replaceAll(BiFunction<? super Character, ? super Boolean, ? extends Boolean> function) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean putIfAbsent(char key, boolean value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean remove(char key, boolean value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean replace(char key, boolean value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean replace(char key, boolean oldValue, boolean newValue) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean computeIfAbsent(char key, IntPredicate mappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean computeIfAbsentNullable(char key, IntFunction<? extends Boolean> mappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean computeIfAbsentPartial(char key, Char2BooleanFunction mappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean computeIfPresent(char key, BiFunction<? super Character, ? super Boolean, ? extends Boolean> remappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean compute(char key, BiFunction<? super Character, ? super Boolean, ? extends Boolean> remappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean merge(char key, boolean value, BiFunction<? super Boolean, ? super Boolean, ? extends Boolean> remappingFunction) {
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
        public Boolean replace(Character key, Boolean value) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public boolean replace(Character key, Boolean oldValue, Boolean newValue) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public Boolean putIfAbsent(Character key, Boolean value) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public Boolean computeIfAbsent(Character key, Function<? super Character, ? extends Boolean> mappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public Boolean computeIfPresent(Character key, BiFunction<? super Character, ? super Boolean, ? extends Boolean> remappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public Boolean compute(Character key, BiFunction<? super Character, ? super Boolean, ? extends Boolean> remappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public Boolean merge(Character key, Boolean value, BiFunction<? super Boolean, ? super Boolean, ? extends Boolean> remappingFunction) {
            throw new UnsupportedOperationException();
        }
    }

    public static class SynchronizedMap
    extends Char2BooleanFunctions.SynchronizedFunction
    implements Char2BooleanMap,
    Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final Char2BooleanMap map;
        protected transient ObjectSet<Char2BooleanMap.Entry> entries;
        protected transient CharSet keys;
        protected transient BooleanCollection values;

        protected SynchronizedMap(Char2BooleanMap m, Object sync) {
            super(m, sync);
            this.map = m;
        }

        protected SynchronizedMap(Char2BooleanMap m) {
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
        public void putAll(Map<? extends Character, ? extends Boolean> m) {
            Object object = this.sync;
            synchronized (object) {
                this.map.putAll(m);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public ObjectSet<Char2BooleanMap.Entry> char2BooleanEntrySet() {
            Object object = this.sync;
            synchronized (object) {
                if (this.entries == null) {
                    this.entries = ObjectSets.synchronize(this.map.char2BooleanEntrySet(), this.sync);
                }
                return this.entries;
            }
        }

        @Deprecated
        @Override
        public ObjectSet<Map.Entry<Character, Boolean>> entrySet() {
            return this.char2BooleanEntrySet();
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
        public boolean getOrDefault(char key, boolean defaultValue) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.getOrDefault(key, defaultValue);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void forEach(BiConsumer<? super Character, ? super Boolean> action) {
            Object object = this.sync;
            synchronized (object) {
                this.map.forEach(action);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void replaceAll(BiFunction<? super Character, ? super Boolean, ? extends Boolean> function) {
            Object object = this.sync;
            synchronized (object) {
                this.map.replaceAll(function);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean putIfAbsent(char key, boolean value) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.putIfAbsent(key, value);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean remove(char key, boolean value) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.remove(key, value);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean replace(char key, boolean value) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.replace(key, value);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean replace(char key, boolean oldValue, boolean newValue) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.replace(key, oldValue, newValue);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean computeIfAbsent(char key, IntPredicate mappingFunction) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.computeIfAbsent(key, mappingFunction);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean computeIfAbsentNullable(char key, IntFunction<? extends Boolean> mappingFunction) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.computeIfAbsentNullable(key, mappingFunction);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean computeIfAbsentPartial(char key, Char2BooleanFunction mappingFunction) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.computeIfAbsentPartial(key, mappingFunction);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean computeIfPresent(char key, BiFunction<? super Character, ? super Boolean, ? extends Boolean> remappingFunction) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.computeIfPresent(key, remappingFunction);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean compute(char key, BiFunction<? super Character, ? super Boolean, ? extends Boolean> remappingFunction) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.compute(key, remappingFunction);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean merge(char key, boolean value, BiFunction<? super Boolean, ? super Boolean, ? extends Boolean> remappingFunction) {
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
        public Boolean replace(Character key, Boolean value) {
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
        public boolean replace(Character key, Boolean oldValue, Boolean newValue) {
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
        public Boolean putIfAbsent(Character key, Boolean value) {
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
        public Boolean computeIfAbsent(Character key, Function<? super Character, ? extends Boolean> mappingFunction) {
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
        public Boolean computeIfPresent(Character key, BiFunction<? super Character, ? super Boolean, ? extends Boolean> remappingFunction) {
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
        public Boolean compute(Character key, BiFunction<? super Character, ? super Boolean, ? extends Boolean> remappingFunction) {
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
        public Boolean merge(Character key, Boolean value, BiFunction<? super Boolean, ? super Boolean, ? extends Boolean> remappingFunction) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.merge(key, value, remappingFunction);
            }
        }
    }

    public static class Singleton
    extends Char2BooleanFunctions.Singleton
    implements Char2BooleanMap,
    Serializable,
    Cloneable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected transient ObjectSet<Char2BooleanMap.Entry> entries;
        protected transient CharSet keys;
        protected transient BooleanCollection values;

        protected Singleton(char key, boolean value) {
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
        public void putAll(Map<? extends Character, ? extends Boolean> m) {
            throw new UnsupportedOperationException();
        }

        @Override
        public ObjectSet<Char2BooleanMap.Entry> char2BooleanEntrySet() {
            if (this.entries == null) {
                this.entries = ObjectSets.singleton(new AbstractChar2BooleanMap.BasicEntry(this.key, this.value));
            }
            return this.entries;
        }

        @Deprecated
        @Override
        public ObjectSet<Map.Entry<Character, Boolean>> entrySet() {
            return this.char2BooleanEntrySet();
        }

        @Override
        public CharSet keySet() {
            if (this.keys == null) {
                this.keys = CharSets.singleton(this.key);
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
    extends Char2BooleanFunctions.EmptyFunction
    implements Char2BooleanMap,
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
        public void putAll(Map<? extends Character, ? extends Boolean> m) {
            throw new UnsupportedOperationException();
        }

        @Override
        public ObjectSet<Char2BooleanMap.Entry> char2BooleanEntrySet() {
            return ObjectSets.EMPTY_SET;
        }

        @Override
        public CharSet keySet() {
            return CharSets.EMPTY_SET;
        }

        @Override
        public BooleanCollection values() {
            return BooleanSets.EMPTY_SET;
        }

        @Override
        public Object clone() {
            return Char2BooleanMaps.EMPTY_MAP;
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

