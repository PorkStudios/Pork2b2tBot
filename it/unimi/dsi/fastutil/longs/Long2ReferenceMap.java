/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.longs.Long2ReferenceFunction;
import it.unimi.dsi.fastutil.longs.LongSet;
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
import java.util.function.LongFunction;

public interface Long2ReferenceMap<V>
extends Long2ReferenceFunction<V>,
Map<Long, V> {
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

    public ObjectSet<Entry<V>> long2ReferenceEntrySet();

    @Deprecated
    @Override
    default public ObjectSet<Map.Entry<Long, V>> entrySet() {
        return this.long2ReferenceEntrySet();
    }

    @Deprecated
    @Override
    default public V put(Long key, V value) {
        return Long2ReferenceFunction.super.put(key, value);
    }

    @Deprecated
    @Override
    default public V get(Object key) {
        return Long2ReferenceFunction.super.get(key);
    }

    @Deprecated
    @Override
    default public V remove(Object key) {
        return Long2ReferenceFunction.super.remove(key);
    }

    public LongSet keySet();

    @Override
    public ReferenceCollection<V> values();

    @Override
    public boolean containsKey(long var1);

    @Deprecated
    @Override
    default public boolean containsKey(Object key) {
        return Long2ReferenceFunction.super.containsKey(key);
    }

    default public V getOrDefault(long key, V defaultValue) {
        V v = this.get(key);
        return v != this.defaultReturnValue() || this.containsKey(key) ? v : defaultValue;
    }

    @Override
    default public V putIfAbsent(long key, V value) {
        V drv;
        V v = this.get(key);
        if (v != (drv = this.defaultReturnValue()) || this.containsKey(key)) {
            return v;
        }
        this.put(key, value);
        return drv;
    }

    default public boolean remove(long key, Object value) {
        V curValue = this.get(key);
        if (curValue != value || curValue == this.defaultReturnValue() && !this.containsKey(key)) {
            return false;
        }
        this.remove(key);
        return true;
    }

    @Override
    default public boolean replace(long key, V oldValue, V newValue) {
        V curValue = this.get(key);
        if (curValue != oldValue || curValue == this.defaultReturnValue() && !this.containsKey(key)) {
            return false;
        }
        this.put(key, newValue);
        return true;
    }

    @Override
    default public V replace(long key, V value) {
        return this.containsKey(key) ? this.put(key, value) : this.defaultReturnValue();
    }

    default public V computeIfAbsent(long key, LongFunction<? extends V> mappingFunction) {
        Objects.requireNonNull(mappingFunction);
        V v = this.get(key);
        if (v != this.defaultReturnValue() || this.containsKey(key)) {
            return v;
        }
        V newValue = mappingFunction.apply(key);
        this.put(key, newValue);
        return newValue;
    }

    default public V computeIfAbsentPartial(long key, Long2ReferenceFunction<? extends V> mappingFunction) {
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
    default public V computeIfPresent(long key, BiFunction<? super Long, ? super V, ? extends V> remappingFunction) {
        Objects.requireNonNull(remappingFunction);
        V oldValue = this.get(key);
        V drv = this.defaultReturnValue();
        if (oldValue == drv && !this.containsKey(key)) {
            return drv;
        }
        V newValue = remappingFunction.apply((Long)key, oldValue);
        if (newValue == null) {
            this.remove(key);
            return drv;
        }
        this.put(key, newValue);
        return newValue;
    }

    @Override
    default public V compute(long key, BiFunction<? super Long, ? super V, ? extends V> remappingFunction) {
        Objects.requireNonNull(remappingFunction);
        V oldValue = this.get(key);
        V drv = this.defaultReturnValue();
        boolean contained = oldValue != drv || this.containsKey(key);
        V newValue = remappingFunction.apply((Long)key, contained ? oldValue : null);
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
    default public V merge(long key, V value, BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
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
    default public V putIfAbsent(Long key, V value) {
        return Map.super.putIfAbsent(key, value);
    }

    @Deprecated
    @Override
    default public boolean remove(Object key, Object value) {
        return Map.super.remove(key, value);
    }

    @Deprecated
    @Override
    default public boolean replace(Long key, V oldValue, V newValue) {
        return Map.super.replace(key, oldValue, newValue);
    }

    @Deprecated
    @Override
    default public V replace(Long key, V value) {
        return Map.super.replace(key, value);
    }

    @Deprecated
    @Override
    default public V computeIfAbsent(Long key, Function<? super Long, ? extends V> mappingFunction) {
        return Map.super.computeIfAbsent(key, mappingFunction);
    }

    @Deprecated
    @Override
    default public V computeIfPresent(Long key, BiFunction<? super Long, ? super V, ? extends V> remappingFunction) {
        return Map.super.computeIfPresent(key, remappingFunction);
    }

    @Deprecated
    @Override
    default public V compute(Long key, BiFunction<? super Long, ? super V, ? extends V> remappingFunction) {
        return Map.super.compute(key, remappingFunction);
    }

    @Deprecated
    @Override
    default public V merge(Long key, V value, BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
        return Map.super.merge(key, value, remappingFunction);
    }

    public static interface Entry<V>
    extends Map.Entry<Long, V> {
        public long getLongKey();

        @Deprecated
        @Override
        public Long getKey();
    }

    public static interface FastEntrySet<V>
    extends ObjectSet<Entry<V>> {
        public ObjectIterator<Entry<V>> fastIterator();

        default public void fastForEach(Consumer<? super Entry<V>> consumer) {
            this.forEach(consumer);
        }
    }

}

