/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.longs.LongCollection;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import it.unimi.dsi.fastutil.objects.Reference2LongFunction;
import it.unimi.dsi.fastutil.objects.ReferenceSet;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.ToLongFunction;

public interface Reference2LongMap<K>
extends Reference2LongFunction<K>,
Map<K, Long> {
    @Override
    public int size();

    @Override
    default public void clear() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void defaultReturnValue(long var1);

    @Override
    public long defaultReturnValue();

    public ObjectSet<Entry<K>> reference2LongEntrySet();

    @Deprecated
    @Override
    default public ObjectSet<Map.Entry<K, Long>> entrySet() {
        return this.reference2LongEntrySet();
    }

    @Deprecated
    @Override
    default public Long put(K key, Long value) {
        return Reference2LongFunction.super.put(key, value);
    }

    @Deprecated
    @Override
    default public Long get(Object key) {
        return Reference2LongFunction.super.get(key);
    }

    @Deprecated
    @Override
    default public Long remove(Object key) {
        return Reference2LongFunction.super.remove(key);
    }

    @Override
    public ReferenceSet<K> keySet();

    public LongCollection values();

    @Override
    public boolean containsKey(Object var1);

    public boolean containsValue(long var1);

    @Deprecated
    @Override
    default public boolean containsValue(Object value) {
        return value == null ? false : this.containsValue((Long)value);
    }

    @Override
    default public long getOrDefault(Object key, long defaultValue) {
        long v = this.getLong(key);
        return v != this.defaultReturnValue() || this.containsKey(key) ? v : defaultValue;
    }

    @Override
    default public long putIfAbsent(K key, long value) {
        long drv;
        long v = this.getLong(key);
        if (v != (drv = this.defaultReturnValue()) || this.containsKey(key)) {
            return v;
        }
        this.put(key, value);
        return drv;
    }

    default public boolean remove(Object key, long value) {
        long curValue = this.getLong(key);
        if (curValue != value || curValue == this.defaultReturnValue() && !this.containsKey(key)) {
            return false;
        }
        this.removeLong(key);
        return true;
    }

    @Override
    default public boolean replace(K key, long oldValue, long newValue) {
        long curValue = this.getLong(key);
        if (curValue != oldValue || curValue == this.defaultReturnValue() && !this.containsKey(key)) {
            return false;
        }
        this.put(key, newValue);
        return true;
    }

    @Override
    default public long replace(K key, long value) {
        return this.containsKey(key) ? this.put(key, value) : this.defaultReturnValue();
    }

    default public long computeLongIfAbsent(K key, ToLongFunction<? super K> mappingFunction) {
        Objects.requireNonNull(mappingFunction);
        long v = this.getLong(key);
        if (v != this.defaultReturnValue() || this.containsKey(key)) {
            return v;
        }
        long newValue = mappingFunction.applyAsLong(key);
        this.put(key, newValue);
        return newValue;
    }

    default public long computeLongIfAbsentPartial(K key, Reference2LongFunction<? super K> mappingFunction) {
        Objects.requireNonNull(mappingFunction);
        long v = this.getLong(key);
        long drv = this.defaultReturnValue();
        if (v != drv || this.containsKey(key)) {
            return v;
        }
        if (!mappingFunction.containsKey(key)) {
            return drv;
        }
        long newValue = mappingFunction.getLong(key);
        this.put(key, newValue);
        return newValue;
    }

    default public long computeLongIfPresent(K key, BiFunction<? super K, ? super Long, ? extends Long> remappingFunction) {
        Objects.requireNonNull(remappingFunction);
        long oldValue = this.getLong(key);
        long drv = this.defaultReturnValue();
        if (oldValue == drv && !this.containsKey(key)) {
            return drv;
        }
        Long newValue = remappingFunction.apply(key, (Long)oldValue);
        if (newValue == null) {
            this.removeLong(key);
            return drv;
        }
        long newVal = newValue;
        this.put(key, newVal);
        return newVal;
    }

    default public long computeLong(K key, BiFunction<? super K, ? super Long, ? extends Long> remappingFunction) {
        Objects.requireNonNull(remappingFunction);
        long oldValue = this.getLong(key);
        long drv = this.defaultReturnValue();
        boolean contained = oldValue != drv || this.containsKey(key);
        Long newValue = remappingFunction.apply(key, contained ? Long.valueOf(oldValue) : null);
        if (newValue == null) {
            if (contained) {
                this.removeLong(key);
            }
            return drv;
        }
        long newVal = newValue;
        this.put(key, newVal);
        return newVal;
    }

    default public long mergeLong(K key, long value, BiFunction<? super Long, ? super Long, ? extends Long> remappingFunction) {
        long newValue;
        Objects.requireNonNull(remappingFunction);
        long oldValue = this.getLong(key);
        long drv = this.defaultReturnValue();
        if (oldValue != drv || this.containsKey(key)) {
            Long mergedValue = remappingFunction.apply((Long)oldValue, (Long)value);
            if (mergedValue == null) {
                this.removeLong(key);
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
    default public Long getOrDefault(Object key, Long defaultValue) {
        return Map.super.getOrDefault(key, defaultValue);
    }

    @Deprecated
    @Override
    default public Long putIfAbsent(K key, Long value) {
        return Map.super.putIfAbsent(key, value);
    }

    @Deprecated
    @Override
    default public boolean remove(Object key, Object value) {
        return Map.super.remove(key, value);
    }

    @Deprecated
    @Override
    default public boolean replace(K key, Long oldValue, Long newValue) {
        return Map.super.replace(key, oldValue, newValue);
    }

    @Deprecated
    @Override
    default public Long replace(K key, Long value) {
        return Map.super.replace(key, value);
    }

    @Deprecated
    @Override
    default public Long merge(K key, Long value, BiFunction<? super Long, ? super Long, ? extends Long> remappingFunction) {
        return Map.super.merge(key, value, remappingFunction);
    }

    public static interface Entry<K>
    extends Map.Entry<K, Long> {
        public long getLongValue();

        @Override
        public long setValue(long var1);

        @Deprecated
        @Override
        public Long getValue();

        @Deprecated
        @Override
        public Long setValue(Long var1);
    }

    public static interface FastEntrySet<K>
    extends ObjectSet<Entry<K>> {
        public ObjectIterator<Entry<K>> fastIterator();

        default public void fastForEach(Consumer<? super Entry<K>> consumer) {
            this.forEach(consumer);
        }
    }

}

