/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.chars.AbstractChar2DoubleMap;
import it.unimi.dsi.fastutil.chars.Char2DoubleFunction;
import it.unimi.dsi.fastutil.chars.Char2DoubleFunctions;
import it.unimi.dsi.fastutil.chars.Char2DoubleMap;
import it.unimi.dsi.fastutil.chars.CharSet;
import it.unimi.dsi.fastutil.chars.CharSets;
import it.unimi.dsi.fastutil.doubles.DoubleCollection;
import it.unimi.dsi.fastutil.doubles.DoubleCollections;
import it.unimi.dsi.fastutil.doubles.DoubleSets;
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
import java.util.function.IntToDoubleFunction;

public final class Char2DoubleMaps {
    public static final EmptyMap EMPTY_MAP = new EmptyMap();

    private Char2DoubleMaps() {
    }

    public static ObjectIterator<Char2DoubleMap.Entry> fastIterator(Char2DoubleMap map) {
        ObjectSet<Char2DoubleMap.Entry> entries = map.char2DoubleEntrySet();
        return entries instanceof Char2DoubleMap.FastEntrySet ? ((Char2DoubleMap.FastEntrySet)entries).fastIterator() : entries.iterator();
    }

    public static void fastForEach(Char2DoubleMap map, Consumer<? super Char2DoubleMap.Entry> consumer) {
        ObjectSet<Char2DoubleMap.Entry> entries = map.char2DoubleEntrySet();
        if (entries instanceof Char2DoubleMap.FastEntrySet) {
            ((Char2DoubleMap.FastEntrySet)entries).fastForEach(consumer);
        } else {
            entries.forEach(consumer);
        }
    }

    public static ObjectIterable<Char2DoubleMap.Entry> fastIterable(Char2DoubleMap map) {
        final ObjectSet<Char2DoubleMap.Entry> entries = map.char2DoubleEntrySet();
        return entries instanceof Char2DoubleMap.FastEntrySet ? new ObjectIterable<Char2DoubleMap.Entry>(){

            @Override
            public ObjectIterator<Char2DoubleMap.Entry> iterator() {
                return ((Char2DoubleMap.FastEntrySet)entries).fastIterator();
            }

            @Override
            public void forEach(Consumer<? super Char2DoubleMap.Entry> consumer) {
                ((Char2DoubleMap.FastEntrySet)entries).fastForEach(consumer);
            }
        } : entries;
    }

    public static Char2DoubleMap singleton(char key, double value) {
        return new Singleton(key, value);
    }

    public static Char2DoubleMap singleton(Character key, Double value) {
        return new Singleton(key.charValue(), value);
    }

    public static Char2DoubleMap synchronize(Char2DoubleMap m) {
        return new SynchronizedMap(m);
    }

    public static Char2DoubleMap synchronize(Char2DoubleMap m, Object sync) {
        return new SynchronizedMap(m, sync);
    }

    public static Char2DoubleMap unmodifiable(Char2DoubleMap m) {
        return new UnmodifiableMap(m);
    }

    public static class UnmodifiableMap
    extends Char2DoubleFunctions.UnmodifiableFunction
    implements Char2DoubleMap,
    Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final Char2DoubleMap map;
        protected transient ObjectSet<Char2DoubleMap.Entry> entries;
        protected transient CharSet keys;
        protected transient DoubleCollection values;

        protected UnmodifiableMap(Char2DoubleMap m) {
            super(m);
            this.map = m;
        }

        @Override
        public boolean containsValue(double v) {
            return this.map.containsValue(v);
        }

        @Deprecated
        @Override
        public boolean containsValue(Object ov) {
            return this.map.containsValue(ov);
        }

        @Override
        public void putAll(Map<? extends Character, ? extends Double> m) {
            throw new UnsupportedOperationException();
        }

        @Override
        public ObjectSet<Char2DoubleMap.Entry> char2DoubleEntrySet() {
            if (this.entries == null) {
                this.entries = ObjectSets.unmodifiable(this.map.char2DoubleEntrySet());
            }
            return this.entries;
        }

        @Deprecated
        @Override
        public ObjectSet<Map.Entry<Character, Double>> entrySet() {
            return this.char2DoubleEntrySet();
        }

        @Override
        public CharSet keySet() {
            if (this.keys == null) {
                this.keys = CharSets.unmodifiable(this.map.keySet());
            }
            return this.keys;
        }

        @Override
        public DoubleCollection values() {
            if (this.values == null) {
                return DoubleCollections.unmodifiable(this.map.values());
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
        public double getOrDefault(char key, double defaultValue) {
            return this.map.getOrDefault(key, defaultValue);
        }

        @Override
        public void forEach(BiConsumer<? super Character, ? super Double> action) {
            this.map.forEach(action);
        }

        @Override
        public void replaceAll(BiFunction<? super Character, ? super Double, ? extends Double> function) {
            throw new UnsupportedOperationException();
        }

        @Override
        public double putIfAbsent(char key, double value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean remove(char key, double value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public double replace(char key, double value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean replace(char key, double oldValue, double newValue) {
            throw new UnsupportedOperationException();
        }

        @Override
        public double computeIfAbsent(char key, IntToDoubleFunction mappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Override
        public double computeIfAbsentNullable(char key, IntFunction<? extends Double> mappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Override
        public double computeIfAbsentPartial(char key, Char2DoubleFunction mappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Override
        public double computeIfPresent(char key, BiFunction<? super Character, ? super Double, ? extends Double> remappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Override
        public double compute(char key, BiFunction<? super Character, ? super Double, ? extends Double> remappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Override
        public double merge(char key, double value, BiFunction<? super Double, ? super Double, ? extends Double> remappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public Double getOrDefault(Object key, Double defaultValue) {
            return this.map.getOrDefault(key, defaultValue);
        }

        @Deprecated
        @Override
        public boolean remove(Object key, Object value) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public Double replace(Character key, Double value) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public boolean replace(Character key, Double oldValue, Double newValue) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public Double putIfAbsent(Character key, Double value) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public Double computeIfAbsent(Character key, Function<? super Character, ? extends Double> mappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public Double computeIfPresent(Character key, BiFunction<? super Character, ? super Double, ? extends Double> remappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public Double compute(Character key, BiFunction<? super Character, ? super Double, ? extends Double> remappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public Double merge(Character key, Double value, BiFunction<? super Double, ? super Double, ? extends Double> remappingFunction) {
            throw new UnsupportedOperationException();
        }
    }

    public static class SynchronizedMap
    extends Char2DoubleFunctions.SynchronizedFunction
    implements Char2DoubleMap,
    Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected final Char2DoubleMap map;
        protected transient ObjectSet<Char2DoubleMap.Entry> entries;
        protected transient CharSet keys;
        protected transient DoubleCollection values;

        protected SynchronizedMap(Char2DoubleMap m, Object sync) {
            super(m, sync);
            this.map = m;
        }

        protected SynchronizedMap(Char2DoubleMap m) {
            super(m);
            this.map = m;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean containsValue(double v) {
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
        public void putAll(Map<? extends Character, ? extends Double> m) {
            Object object = this.sync;
            synchronized (object) {
                this.map.putAll(m);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public ObjectSet<Char2DoubleMap.Entry> char2DoubleEntrySet() {
            Object object = this.sync;
            synchronized (object) {
                if (this.entries == null) {
                    this.entries = ObjectSets.synchronize(this.map.char2DoubleEntrySet(), this.sync);
                }
                return this.entries;
            }
        }

        @Deprecated
        @Override
        public ObjectSet<Map.Entry<Character, Double>> entrySet() {
            return this.char2DoubleEntrySet();
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
        public DoubleCollection values() {
            Object object = this.sync;
            synchronized (object) {
                if (this.values == null) {
                    return DoubleCollections.synchronize(this.map.values(), this.sync);
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
        public double getOrDefault(char key, double defaultValue) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.getOrDefault(key, defaultValue);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void forEach(BiConsumer<? super Character, ? super Double> action) {
            Object object = this.sync;
            synchronized (object) {
                this.map.forEach(action);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void replaceAll(BiFunction<? super Character, ? super Double, ? extends Double> function) {
            Object object = this.sync;
            synchronized (object) {
                this.map.replaceAll(function);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public double putIfAbsent(char key, double value) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.putIfAbsent(key, value);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean remove(char key, double value) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.remove(key, value);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public double replace(char key, double value) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.replace(key, value);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean replace(char key, double oldValue, double newValue) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.replace(key, oldValue, newValue);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public double computeIfAbsent(char key, IntToDoubleFunction mappingFunction) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.computeIfAbsent(key, mappingFunction);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public double computeIfAbsentNullable(char key, IntFunction<? extends Double> mappingFunction) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.computeIfAbsentNullable(key, mappingFunction);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public double computeIfAbsentPartial(char key, Char2DoubleFunction mappingFunction) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.computeIfAbsentPartial(key, mappingFunction);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public double computeIfPresent(char key, BiFunction<? super Character, ? super Double, ? extends Double> remappingFunction) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.computeIfPresent(key, remappingFunction);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public double compute(char key, BiFunction<? super Character, ? super Double, ? extends Double> remappingFunction) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.compute(key, remappingFunction);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public double merge(char key, double value, BiFunction<? super Double, ? super Double, ? extends Double> remappingFunction) {
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
        public Double getOrDefault(Object key, Double defaultValue) {
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
        public Double replace(Character key, Double value) {
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
        public boolean replace(Character key, Double oldValue, Double newValue) {
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
        public Double putIfAbsent(Character key, Double value) {
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
        public Double computeIfAbsent(Character key, Function<? super Character, ? extends Double> mappingFunction) {
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
        public Double computeIfPresent(Character key, BiFunction<? super Character, ? super Double, ? extends Double> remappingFunction) {
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
        public Double compute(Character key, BiFunction<? super Character, ? super Double, ? extends Double> remappingFunction) {
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
        public Double merge(Character key, Double value, BiFunction<? super Double, ? super Double, ? extends Double> remappingFunction) {
            Object object = this.sync;
            synchronized (object) {
                return this.map.merge(key, value, remappingFunction);
            }
        }
    }

    public static class Singleton
    extends Char2DoubleFunctions.Singleton
    implements Char2DoubleMap,
    Serializable,
    Cloneable {
        private static final long serialVersionUID = -7046029254386353129L;
        protected transient ObjectSet<Char2DoubleMap.Entry> entries;
        protected transient CharSet keys;
        protected transient DoubleCollection values;

        protected Singleton(char key, double value) {
            super(key, value);
        }

        @Override
        public boolean containsValue(double v) {
            return Double.doubleToLongBits(this.value) == Double.doubleToLongBits(v);
        }

        @Deprecated
        @Override
        public boolean containsValue(Object ov) {
            return Double.doubleToLongBits((Double)ov) == Double.doubleToLongBits(this.value);
        }

        @Override
        public void putAll(Map<? extends Character, ? extends Double> m) {
            throw new UnsupportedOperationException();
        }

        @Override
        public ObjectSet<Char2DoubleMap.Entry> char2DoubleEntrySet() {
            if (this.entries == null) {
                this.entries = ObjectSets.singleton(new AbstractChar2DoubleMap.BasicEntry(this.key, this.value));
            }
            return this.entries;
        }

        @Deprecated
        @Override
        public ObjectSet<Map.Entry<Character, Double>> entrySet() {
            return this.char2DoubleEntrySet();
        }

        @Override
        public CharSet keySet() {
            if (this.keys == null) {
                this.keys = CharSets.singleton(this.key);
            }
            return this.keys;
        }

        @Override
        public DoubleCollection values() {
            if (this.values == null) {
                this.values = DoubleSets.singleton(this.value);
            }
            return this.values;
        }

        @Override
        public boolean isEmpty() {
            return false;
        }

        @Override
        public int hashCode() {
            return this.key ^ HashCommon.double2int(this.value);
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
    extends Char2DoubleFunctions.EmptyFunction
    implements Char2DoubleMap,
    Serializable,
    Cloneable {
        private static final long serialVersionUID = -7046029254386353129L;

        protected EmptyMap() {
        }

        @Override
        public boolean containsValue(double v) {
            return false;
        }

        @Deprecated
        @Override
        public boolean containsValue(Object ov) {
            return false;
        }

        @Override
        public void putAll(Map<? extends Character, ? extends Double> m) {
            throw new UnsupportedOperationException();
        }

        @Override
        public ObjectSet<Char2DoubleMap.Entry> char2DoubleEntrySet() {
            return ObjectSets.EMPTY_SET;
        }

        @Override
        public CharSet keySet() {
            return CharSets.EMPTY_SET;
        }

        @Override
        public DoubleCollection values() {
            return DoubleSets.EMPTY_SET;
        }

        @Override
        public Object clone() {
            return Char2DoubleMaps.EMPTY_MAP;
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

