/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.floats.Float2ObjectFunction;
import it.unimi.dsi.fastutil.floats.FloatSet;
import it.unimi.dsi.fastutil.objects.ObjectCollection;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.DoubleFunction;
import java.util.function.Function;

public interface Float2ObjectMap<V>
extends Float2ObjectFunction<V>,
Map<Float, V> {
    @Override
    public int size();

    @Override
    default public void clear() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void defaultReturnValue(V var1);

    @Override
    public V defaultReturnValue();

    public ObjectSet<Entry<V>> float2ObjectEntrySet();

    @Deprecated
    @Override
    default public ObjectSet<Map.Entry<Float, V>> entrySet() {
        return this.float2ObjectEntrySet();
    }

    @Deprecated
    @Override
    default public V put(Float key, V value) {
        return Float2ObjectFunction.super.put(key, value);
    }

    @Deprecated
    @Override
    default public V get(Object key) {
        return Float2ObjectFunction.super.get(key);
    }

    @Deprecated
    @Override
    default public V remove(Object key) {
        return Float2ObjectFunction.super.remove(key);
    }

    public FloatSet keySet();

    @Override
    public ObjectCollection<V> values();

    @Override
    public boolean containsKey(float var1);

    @Deprecated
    @Override
    default public boolean containsKey(Object key) {
        return Float2ObjectFunction.super.containsKey(key);
    }

    default public V getOrDefault(float key, V defaultValue) {
        V v = this.get(key);
        return v != this.defaultReturnValue() || this.containsKey(key) ? v : defaultValue;
    }

    @Override
    default public V putIfAbsent(float key, V value) {
        V drv;
        V v = this.get(key);
        if (v != (drv = this.defaultReturnValue()) || this.containsKey(key)) {
            return v;
        }
        this.put(key, value);
        return drv;
    }

    default public boolean remove(float key, Object value) {
        V curValue = this.get(key);
        if (!Objects.equals(curValue, value) || curValue == this.defaultReturnValue() && !this.containsKey(key)) {
            return false;
        }
        this.remove(key);
        return true;
    }

    @Override
    default public boolean replace(float key, V oldValue, V newValue) {
        V curValue = this.get(key);
        if (!Objects.equals(curValue, oldValue) || curValue == this.defaultReturnValue() && !this.containsKey(key)) {
            return false;
        }
        this.put(key, newValue);
        return true;
    }

    @Override
    default public V replace(float key, V value) {
        return this.containsKey(key) ? this.put(key, value) : this.defaultReturnValue();
    }

    default public V computeIfAbsent(float key, DoubleFunction<? extends V> mappingFunction) {
        Objects.requireNonNull(mappingFunction);
        V v = this.get(key);
        if (v != this.defaultReturnValue() || this.containsKey(key)) {
            return v;
        }
        V newValue = mappingFunction.apply(key);
        this.put(key, newValue);
        return newValue;
    }

    default public V computeIfAbsentPartial(float key, Float2ObjectFunction<? extends V> mappingFunction) {
        Objects.requireNonNull(mappingFunction);
        V v = this.get(key);
        V drv = this.defaultReturnValue();
        if (v != drv || this.containsKey(key)) {
            return v;
        }
        if (!mappingFunction.containsKey(key)) {
            return drv;
        }
        V newValue = mappingFunction.get(key);
        this.put(key, newValue);
        return newValue;
    }

    @Override
    default public V computeIfPresent(float key, BiFunction<? super Float, ? super V, ? extends V> remappingFunction) {
        Objects.requireNonNull(remappingFunction);
        V oldValue = this.get(key);
        V drv = this.defaultReturnValue();
        if (oldValue == drv && !this.containsKey(key)) {
            return drv;
        }
        V newValue = remappingFunction.apply(Float.valueOf(key), oldValue);
        if (newValue == null) {
            this.remove(key);
            return drv;
        }
        this.put(key, newValue);
        return newValue;
    }

    @Override
    default public V compute(float key, BiFunction<? super Float, ? super V, ? extends V> remappingFunction) {
        Objects.requireNonNull(remappingFunction);
        V oldValue = this.get(key);
        V drv = this.defaultReturnValue();
        boolean contained = oldValue != drv || this.containsKey(key);
        V newValue = remappingFunction.apply(Float.valueOf(key), contained ? oldValue : null);
        if (newValue == null) {
            if (contained) {
                this.remove(key);
            }
            return drv;
        }
        this.put(key, newValue);
        return newValue;
    }

    @Override
    default public V merge(float key, V value, BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
        V newValue;
        Objects.requireNonNull(remappingFunction);
        Objects.requireNonNull(value);
        V oldValue = this.get(key);
        V drv = this.defaultReturnValue();
        if (oldValue != drv || this.containsKey(key)) {
            V mergedValue = remappingFunction.apply(oldValue, value);
            if (mergedValue == null) {
                this.remove(key);
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
    default public V getOrDefault(Object key, V defaultValue) {
        return Map.super.getOrDefault(key, defaultValue);
    }

    @Deprecated
    @Override
    default public V putIfAbsent(Float key, V value) {
        return Map.super.putIfAbsent(key, value);
    }

    @Deprecated
    @Override
    default public boolean remove(Object key, Object value) {
        return Map.super.remove(key, value);
    }

    @Deprecated
    @Override
    default public boolean replace(Float key, V oldValue, V newValue) {
        return Map.super.replace(key, oldValue, newValue);
    }

    @Deprecated
    @Override
    default public V replace(Float key, V value) {
        return Map.super.replace(key, value);
    }

    @Deprecated
    @Override
    default public V computeIfAbsent(Float key, Function<? super Float, ? extends V> mappingFunction) {
        return Map.super.computeIfAbsent(key, mappingFunction);
    }

    @Deprecated
    @Override
    default public V computeIfPresent(Float key, BiFunction<? super Float, ? super V, ? extends V> remappingFunction) {
        return Map.super.computeIfPresent(key, remappingFunction);
    }

    @Deprecated
    @Override
    default public V compute(Float key, BiFunction<? super Float, ? super V, ? extends V> remappingFunction) {
        return Map.super.compute(key, remappingFunction);
    }

    @Deprecated
    @Override
    default public V merge(Float key, V value, BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
        return Map.super.merge(key, value, remappingFunction);
    }

    public static interface Entry<V>
    extends Map.Entry<Float, V> {
        public float getFloatKey();

        @Deprecated
        @Override
        public Float getKey();
    }

    public static interface FastEntrySet<V>
    extends ObjectSet<Entry<V>> {
        public ObjectIterator<Entry<V>> fastIterator();

        default public void fastForEach(Consumer<? super Entry<V>> consumer) {
            this.forEach(consumer);
        }
    }

}

