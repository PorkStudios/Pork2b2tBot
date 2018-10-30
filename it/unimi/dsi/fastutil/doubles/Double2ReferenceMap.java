/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.doubles;

import it.unimi.dsi.fastutil.doubles.Double2ReferenceFunction;
import it.unimi.dsi.fastutil.doubles.DoubleSet;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import it.unimi.dsi.fastutil.objects.ReferenceCollection;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.DoubleFunction;
import java.util.function.Function;

public interface Double2ReferenceMap<V>
extends Double2ReferenceFunction<V>,
Map<Double, V> {
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

    public ObjectSet<Entry<V>> double2ReferenceEntrySet();

    @Deprecated
    @Override
    default public ObjectSet<Map.Entry<Double, V>> entrySet() {
        return this.double2ReferenceEntrySet();
    }

    @Deprecated
    @Override
    default public V put(Double key, V value) {
        return Double2ReferenceFunction.super.put(key, value);
    }

    @Deprecated
    @Override
    default public V get(Object key) {
        return Double2ReferenceFunction.super.get(key);
    }

    @Deprecated
    @Override
    default public V remove(Object key) {
        return Double2ReferenceFunction.super.remove(key);
    }

    public DoubleSet keySet();

    @Override
    public ReferenceCollection<V> values();

    @Override
    public boolean containsKey(double var1);

    @Deprecated
    @Override
    default public boolean containsKey(Object key) {
        return Double2ReferenceFunction.super.containsKey(key);
    }

    default public V getOrDefault(double key, V defaultValue) {
        V v = this.get(key);
        return v != this.defaultReturnValue() || this.containsKey(key) ? v : defaultValue;
    }

    @Override
    default public V putIfAbsent(double key, V value) {
        V drv;
        V v = this.get(key);
        if (v != (drv = this.defaultReturnValue()) || this.containsKey(key)) {
            return v;
        }
        this.put(key, value);
        return drv;
    }

    default public boolean remove(double key, Object value) {
        V curValue = this.get(key);
        if (curValue != value || curValue == this.defaultReturnValue() && !this.containsKey(key)) {
            return false;
        }
        this.remove(key);
        return true;
    }

    @Override
    default public boolean replace(double key, V oldValue, V newValue) {
        V curValue = this.get(key);
        if (curValue != oldValue || curValue == this.defaultReturnValue() && !this.containsKey(key)) {
            return false;
        }
        this.put(key, newValue);
        return true;
    }

    @Override
    default public V replace(double key, V value) {
        return this.containsKey(key) ? this.put(key, value) : this.defaultReturnValue();
    }

    default public V computeIfAbsent(double key, DoubleFunction<? extends V> mappingFunction) {
        Objects.requireNonNull(mappingFunction);
        V v = this.get(key);
        if (v != this.defaultReturnValue() || this.containsKey(key)) {
            return v;
        }
        V newValue = mappingFunction.apply(key);
        this.put(key, newValue);
        return newValue;
    }

    default public V computeIfAbsentPartial(double key, Double2ReferenceFunction<? extends V> mappingFunction) {
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
    default public V computeIfPresent(double key, BiFunction<? super Double, ? super V, ? extends V> remappingFunction) {
        Objects.requireNonNull(remappingFunction);
        V oldValue = this.get(key);
        V drv = this.defaultReturnValue();
        if (oldValue == drv && !this.containsKey(key)) {
            return drv;
        }
        V newValue = remappingFunction.apply((Double)key, oldValue);
        if (newValue == null) {
            this.remove(key);
            return drv;
        }
        this.put(key, newValue);
        return newValue;
    }

    @Override
    default public V compute(double key, BiFunction<? super Double, ? super V, ? extends V> remappingFunction) {
        Objects.requireNonNull(remappingFunction);
        V oldValue = this.get(key);
        V drv = this.defaultReturnValue();
        boolean contained = oldValue != drv || this.containsKey(key);
        V newValue = remappingFunction.apply((Double)key, contained ? oldValue : null);
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
    default public V merge(double key, V value, BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
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
    default public V putIfAbsent(Double key, V value) {
        return Map.super.putIfAbsent(key, value);
    }

    @Deprecated
    @Override
    default public boolean remove(Object key, Object value) {
        return Map.super.remove(key, value);
    }

    @Deprecated
    @Override
    default public boolean replace(Double key, V oldValue, V newValue) {
        return Map.super.replace(key, oldValue, newValue);
    }

    @Deprecated
    @Override
    default public V replace(Double key, V value) {
        return Map.super.replace(key, value);
    }

    @Deprecated
    @Override
    default public V computeIfAbsent(Double key, Function<? super Double, ? extends V> mappingFunction) {
        return Map.super.computeIfAbsent(key, mappingFunction);
    }

    @Deprecated
    @Override
    default public V computeIfPresent(Double key, BiFunction<? super Double, ? super V, ? extends V> remappingFunction) {
        return Map.super.computeIfPresent(key, remappingFunction);
    }

    @Deprecated
    @Override
    default public V compute(Double key, BiFunction<? super Double, ? super V, ? extends V> remappingFunction) {
        return Map.super.compute(key, remappingFunction);
    }

    @Deprecated
    @Override
    default public V merge(Double key, V value, BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
        return Map.super.merge(key, value, remappingFunction);
    }

    public static interface Entry<V>
    extends Map.Entry<Double, V> {
        public double getDoubleKey();

        @Deprecated
        @Override
        public Double getKey();
    }

    public static interface FastEntrySet<V>
    extends ObjectSet<Entry<V>> {
        public ObjectIterator<Entry<V>> fastIterator();

        default public void fastForEach(Consumer<? super Entry<V>> consumer) {
            this.forEach(consumer);
        }
    }

}

