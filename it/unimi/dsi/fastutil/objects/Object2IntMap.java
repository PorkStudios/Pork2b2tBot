/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.ints.IntCollection;
import it.unimi.dsi.fastutil.objects.Object2IntFunction;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.ToIntFunction;

public interface Object2IntMap<K>
extends Object2IntFunction<K>,
Map<K, Integer> {
    @Override
    public int size();

    @Override
    default public void clear() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void defaultReturnValue(int var1);

    @Override
    public int defaultReturnValue();

    public ObjectSet<Entry<K>> object2IntEntrySet();

    @Deprecated
    @Override
    default public ObjectSet<Map.Entry<K, Integer>> entrySet() {
        return this.object2IntEntrySet();
    }

    @Deprecated
    @Override
    default public Integer put(K key, Integer value) {
        return Object2IntFunction.super.put(key, value);
    }

    @Deprecated
    @Override
    default public Integer get(Object key) {
        return Object2IntFunction.super.get(key);
    }

    @Deprecated
    @Override
    default public Integer remove(Object key) {
        return Object2IntFunction.super.remove(key);
    }

    @Override
    public ObjectSet<K> keySet();

    public IntCollection values();

    @Override
    public boolean containsKey(Object var1);

    public boolean containsValue(int var1);

    @Deprecated
    @Override
    default public boolean containsValue(Object value) {
        return value == null ? false : this.containsValue((Integer)value);
    }

    @Override
    default public int getOrDefault(Object key, int defaultValue) {
        int v = this.getInt(key);
        return v != this.defaultReturnValue() || this.containsKey(key) ? v : defaultValue;
    }

    @Override
    default public int putIfAbsent(K key, int value) {
        int drv;
        int v = this.getInt(key);
        if (v != (drv = this.defaultReturnValue()) || this.containsKey(key)) {
            return v;
        }
        this.put(key, value);
        return drv;
    }

    default public boolean remove(Object key, int value) {
        int curValue = this.getInt(key);
        if (curValue != value || curValue == this.defaultReturnValue() && !this.containsKey(key)) {
            return false;
        }
        this.removeInt(key);
        return true;
    }

    @Override
    default public boolean replace(K key, int oldValue, int newValue) {
        int curValue = this.getInt(key);
        if (curValue != oldValue || curValue == this.defaultReturnValue() && !this.containsKey(key)) {
            return false;
        }
        this.put(key, newValue);
        return true;
    }

    @Override
    default public int replace(K key, int value) {
        return this.containsKey(key) ? this.put(key, value) : this.defaultReturnValue();
    }

    default public int computeIntIfAbsent(K key, ToIntFunction<? super K> mappingFunction) {
        Objects.requireNonNull(mappingFunction);
        int v = this.getInt(key);
        if (v != this.defaultReturnValue() || this.containsKey(key)) {
            return v;
        }
        int newValue = mappingFunction.applyAsInt(key);
        this.put(key, newValue);
        return newValue;
    }

    default public int computeIntIfAbsentPartial(K key, Object2IntFunction<? super K> mappingFunction) {
        Objects.requireNonNull(mappingFunction);
        int v = this.getInt(key);
        int drv = this.defaultReturnValue();
        if (v != drv || this.containsKey(key)) {
            return v;
        }
        if (!mappingFunction.containsKey(key)) {
            return drv;
        }
        int newValue = mappingFunction.getInt(key);
        this.put(key, newValue);
        return newValue;
    }

    default public int computeIntIfPresent(K key, BiFunction<? super K, ? super Integer, ? extends Integer> remappingFunction) {
        Objects.requireNonNull(remappingFunction);
        int oldValue = this.getInt(key);
        int drv = this.defaultReturnValue();
        if (oldValue == drv && !this.containsKey(key)) {
            return drv;
        }
        Integer newValue = remappingFunction.apply(key, (Integer)oldValue);
        if (newValue == null) {
            this.removeInt(key);
            return drv;
        }
        int newVal = newValue;
        this.put(key, newVal);
        return newVal;
    }

    default public int computeInt(K key, BiFunction<? super K, ? super Integer, ? extends Integer> remappingFunction) {
        Objects.requireNonNull(remappingFunction);
        int oldValue = this.getInt(key);
        int drv = this.defaultReturnValue();
        boolean contained = oldValue != drv || this.containsKey(key);
        Integer newValue = remappingFunction.apply(key, contained ? Integer.valueOf(oldValue) : null);
        if (newValue == null) {
            if (contained) {
                this.removeInt(key);
            }
            return drv;
        }
        int newVal = newValue;
        this.put(key, newVal);
        return newVal;
    }

    default public int mergeInt(K key, int value, BiFunction<? super Integer, ? super Integer, ? extends Integer> remappingFunction) {
        int newValue;
        Objects.requireNonNull(remappingFunction);
        int oldValue = this.getInt(key);
        int drv = this.defaultReturnValue();
        if (oldValue != drv || this.containsKey(key)) {
            Integer mergedValue = remappingFunction.apply((Integer)oldValue, (Integer)value);
            if (mergedValue == null) {
                this.removeInt(key);
                return drv;
            }
            newValue = mergedValue;
        } else {
            newValue = value;
        }
        this.put(key, newValue);
        return newValue;
    }

    @Deprecated
    @Override
    default public Integer getOrDefault(Object key, Integer defaultValue) {
        return Map.super.getOrDefault(key, defaultValue);
    }

    @Deprecated
    @Override
    default public Integer putIfAbsent(K key, Integer value) {
        return Map.super.putIfAbsent(key, value);
    }

    @Deprecated
    @Override
    default public boolean remove(Object key, Object value) {
        return Map.super.remove(key, value);
    }

    @Deprecated
    @Override
    default public boolean replace(K key, Integer oldValue, Integer newValue) {
        return Map.super.replace(key, oldValue, newValue);
    }

    @Deprecated
    @Override
    default public Integer replace(K key, Integer value) {
        return Map.super.replace(key, value);
    }

    @Deprecated
    @Override
    default public Integer merge(K key, Integer value, BiFunction<? super Integer, ? super Integer, ? extends Integer> remappingFunction) {
        return Map.super.merge(key, value, remappingFunction);
    }

    public static interface Entry<K>
    extends Map.Entry<K, Integer> {
        public int getIntValue();

        @Override
        public int setValue(int var1);

        @Deprecated
        @Override
        public Integer getValue();

        @Deprecated
        @Override
        public Integer setValue(Integer var1);
    }

    public static interface FastEntrySet<K>
    extends ObjectSet<Entry<K>> {
        public ObjectIterator<Entry<K>> fastIterator();

        default public void fastForEach(Consumer<? super Entry<K>> consumer) {
            this.forEach(consumer);
        }
    }

}

