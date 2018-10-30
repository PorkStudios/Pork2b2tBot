/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.ints;

import it.unimi.dsi.fastutil.ints.Int2ReferenceFunction;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import it.unimi.dsi.fastutil.objects.ReferenceCollection;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntFunction;

public interface Int2ReferenceMap<V>
extends Int2ReferenceFunction<V>,
Map<Integer, V> {
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

    public ObjectSet<Entry<V>> int2ReferenceEntrySet();

    @Deprecated
    @Override
    default public ObjectSet<Map.Entry<Integer, V>> entrySet() {
        return this.int2ReferenceEntrySet();
    }

    @Deprecated
    @Override
    default public V put(Integer key, V value) {
        return Int2ReferenceFunction.super.put(key, value);
    }

    @Deprecated
    @Override
    default public V get(Object key) {
        return Int2ReferenceFunction.super.get(key);
    }

    @Deprecated
    @Override
    default public V remove(Object key) {
        return Int2ReferenceFunction.super.remove(key);
    }

    public IntSet keySet();

    @Override
    public ReferenceCollection<V> values();

    @Override
    public boolean containsKey(int var1);

    @Deprecated
    @Override
    default public boolean containsKey(Object key) {
        return Int2ReferenceFunction.super.containsKey(key);
    }

    default public V getOrDefault(int key, V defaultValue) {
        V v = this.get(key);
        return v != this.defaultReturnValue() || this.containsKey(key) ? v : defaultValue;
    }

    @Override
    default public V putIfAbsent(int key, V value) {
        V drv;
        V v = this.get(key);
        if (v != (drv = this.defaultReturnValue()) || this.containsKey(key)) {
            return v;
        }
        this.put(key, value);
        return drv;
    }

    default public boolean remove(int key, Object value) {
        V curValue = this.get(key);
        if (curValue != value || curValue == this.defaultReturnValue() && !this.containsKey(key)) {
            return false;
        }
        this.remove(key);
        return true;
    }

    @Override
    default public boolean replace(int key, V oldValue, V newValue) {
        V curValue = this.get(key);
        if (curValue != oldValue || curValue == this.defaultReturnValue() && !this.containsKey(key)) {
            return false;
        }
        this.put(key, newValue);
        return true;
    }

    @Override
    default public V replace(int key, V value) {
        return this.containsKey(key) ? this.put(key, value) : this.defaultReturnValue();
    }

    default public V computeIfAbsent(int key, IntFunction<? extends V> mappingFunction) {
        Objects.requireNonNull(mappingFunction);
        V v = this.get(key);
        if (v != this.defaultReturnValue() || this.containsKey(key)) {
            return v;
        }
        V newValue = mappingFunction.apply(key);
        this.put(key, newValue);
        return newValue;
    }

    default public V computeIfAbsentPartial(int key, Int2ReferenceFunction<? extends V> mappingFunction) {
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
    default public V computeIfPresent(int key, BiFunction<? super Integer, ? super V, ? extends V> remappingFunction) {
        Objects.requireNonNull(remappingFunction);
        V oldValue = this.get(key);
        V drv = this.defaultReturnValue();
        if (oldValue == drv && !this.containsKey(key)) {
            return drv;
        }
        V newValue = remappingFunction.apply((Integer)key, oldValue);
        if (newValue == null) {
            this.remove(key);
            return drv;
        }
        this.put(key, newValue);
        return newValue;
    }

    @Override
    default public V compute(int key, BiFunction<? super Integer, ? super V, ? extends V> remappingFunction) {
        Objects.requireNonNull(remappingFunction);
        V oldValue = this.get(key);
        V drv = this.defaultReturnValue();
        boolean contained = oldValue != drv || this.containsKey(key);
        V newValue = remappingFunction.apply((Integer)key, contained ? oldValue : null);
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
    default public V merge(int key, V value, BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
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
    default public V putIfAbsent(Integer key, V value) {
        return Map.super.putIfAbsent(key, value);
    }

    @Deprecated
    @Override
    default public boolean remove(Object key, Object value) {
        return Map.super.remove(key, value);
    }

    @Deprecated
    @Override
    default public boolean replace(Integer key, V oldValue, V newValue) {
        return Map.super.replace(key, oldValue, newValue);
    }

    @Deprecated
    @Override
    default public V replace(Integer key, V value) {
        return Map.super.replace(key, value);
    }

    @Deprecated
    @Override
    default public V computeIfAbsent(Integer key, Function<? super Integer, ? extends V> mappingFunction) {
        return Map.super.computeIfAbsent(key, mappingFunction);
    }

    @Deprecated
    @Override
    default public V computeIfPresent(Integer key, BiFunction<? super Integer, ? super V, ? extends V> remappingFunction) {
        return Map.super.computeIfPresent(key, remappingFunction);
    }

    @Deprecated
    @Override
    default public V compute(Integer key, BiFunction<? super Integer, ? super V, ? extends V> remappingFunction) {
        return Map.super.compute(key, remappingFunction);
    }

    @Deprecated
    @Override
    default public V merge(Integer key, V value, BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
        return Map.super.merge(key, value, remappingFunction);
    }

    public static interface Entry<V>
    extends Map.Entry<Integer, V> {
        public int getIntKey();

        @Deprecated
        @Override
        public Integer getKey();
    }

    public static interface FastEntrySet<V>
    extends ObjectSet<Entry<V>> {
        public ObjectIterator<Entry<V>> fastIterator();

        default public void fastForEach(Consumer<? super Entry<V>> consumer) {
            this.forEach(consumer);
        }
    }

}

