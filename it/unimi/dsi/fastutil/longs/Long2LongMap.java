/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.longs.Long2LongFunction;
import it.unimi.dsi.fastutil.longs.LongCollection;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.LongFunction;
import java.util.function.LongUnaryOperator;

public interface Long2LongMap
extends Long2LongFunction,
Map<Long, Long> {
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

    public ObjectSet<Entry> long2LongEntrySet();

    @Deprecated
    @Override
    default public ObjectSet<Map.Entry<Long, Long>> entrySet() {
        return this.long2LongEntrySet();
    }

    @Deprecated
    @Override
    default public Long put(Long key, Long value) {
        return Long2LongFunction.super.put(key, value);
    }

    @Deprecated
    @Override
    default public Long get(Object key) {
        return Long2LongFunction.super.get(key);
    }

    @Deprecated
    @Override
    default public Long remove(Object key) {
        return Long2LongFunction.super.remove(key);
    }

    public LongSet keySet();

    public LongCollection values();

    @Override
    public boolean containsKey(long var1);

    @Deprecated
    @Override
    default public boolean containsKey(Object key) {
        return Long2LongFunction.super.containsKey(key);
    }

    public boolean containsValue(long var1);

    @Deprecated
    @Override
    default public boolean containsValue(Object value) {
        return value == null ? false : this.containsValue((Long)value);
    }

    default public long getOrDefault(long key, long defaultValue) {
        long v = this.get(key);
        return v != this.defaultReturnValue() || this.containsKey(key) ? v : defaultValue;
    }

    @Override
    default public long putIfAbsent(long key, long value) {
        long drv;
        long v = this.get(key);
        if (v != (drv = this.defaultReturnValue()) || this.containsKey(key)) {
            return v;
        }
        this.put(key, value);
        return drv;
    }

    default public boolean remove(long key, long value) {
        long curValue = this.get(key);
        if (curValue != value || curValue == this.defaultReturnValue() && !this.containsKey(key)) {
            return false;
        }
        this.remove(key);
        return true;
    }

    @Override
    default public boolean replace(long key, long oldValue, long newValue) {
        long curValue = this.get(key);
        if (curValue != oldValue || curValue == this.defaultReturnValue() && !this.containsKey(key)) {
            return false;
        }
        this.put(key, newValue);
        return true;
    }

    @Override
    default public long replace(long key, long value) {
        return this.containsKey(key) ? this.put(key, value) : this.defaultReturnValue();
    }

    default public long computeIfAbsent(long key, LongUnaryOperator mappingFunction) {
        Objects.requireNonNull(mappingFunction);
        long v = this.get(key);
        if (v != this.defaultReturnValue() || this.containsKey(key)) {
            return v;
        }
        long newValue = mappingFunction.applyAsLong(key);
        this.put(key, newValue);
        return newValue;
    }

    default public long computeIfAbsentNullable(long key, LongFunction<? extends Long> mappingFunction) {
        Objects.requireNonNull(mappingFunction);
        long v = this.get(key);
        long drv = this.defaultReturnValue();
        if (v != drv || this.containsKey(key)) {
            return v;
        }
        Long mappedValue = mappingFunction.apply(key);
        if (mappedValue == null) {
            return drv;
        }
        long newValue = mappedValue;
        this.put(key, newValue);
        return newValue;
    }

    default public long computeIfAbsentPartial(long key, Long2LongFunction mappingFunction) {
        Objects.requireNonNull(mappingFunction);
        long v = this.get(key);
        long drv = this.defaultReturnValue();
        if (v != drv || this.containsKey(key)) {
            return v;
        }
        if (!mappingFunction.containsKey(key)) {
            return drv;
        }
        long newValue = mappingFunction.get(key);
        this.put(key, newValue);
        return newValue;
    }

    @Override
    default public long computeIfPresent(long key, BiFunction<? super Long, ? super Long, ? extends Long> remappingFunction) {
        Objects.requireNonNull(remappingFunction);
        long oldValue = this.get(key);
        long drv = this.defaultReturnValue();
        if (oldValue == drv && !this.containsKey(key)) {
            return drv;
        }
        Long newValue = remappingFunction.apply((Long)key, (Long)oldValue);
        if (newValue == null) {
            this.remove(key);
            return drv;
        }
        long newVal = newValue;
        this.put(key, newVal);
        return newVal;
    }

    @Override
    default public long compute(long key, BiFunction<? super Long, ? super Long, ? extends Long> remappingFunction) {
        Objects.requireNonNull(remappingFunction);
        long oldValue = this.get(key);
        long drv = this.defaultReturnValue();
        boolean contained = oldValue != drv || this.containsKey(key);
        Long newValue = remappingFunction.apply((Long)key, contained ? Long.valueOf(oldValue) : null);
        if (newValue == null) {
            if (contained) {
                this.remove(key);
            }
            return drv;
        }
        long newVal = newValue;
        this.put(key, newVal);
        return newVal;
    }

    @Override
    default public long merge(long key, long value, BiFunction<? super Long, ? super Long, ? extends Long> remappingFunction) {
        long newValue;
        Objects.requireNonNull(remappingFunction);
        long oldValue = this.get(key);
        long drv = this.defaultReturnValue();
        if (oldValue != drv || this.containsKey(key)) {
            Long mergedValue = remappingFunction.apply((Long)oldValue, (Long)value);
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
    default public Long getOrDefault(Object key, Long defaultValue) {
        return Map.super.getOrDefault(key, defaultValue);
    }

    @Deprecated
    @Override
    default public Long putIfAbsent(Long key, Long value) {
        return Map.super.putIfAbsent(key, value);
    }

    @Deprecated
    @Override
    default public boolean remove(Object key, Object value) {
        return Map.super.remove(key, value);
    }

    @Deprecated
    @Override
    default public boolean replace(Long key, Long oldValue, Long newValue) {
        return Map.super.replace(key, oldValue, newValue);
    }

    @Deprecated
    @Override
    default public Long replace(Long key, Long value) {
        return Map.super.replace(key, value);
    }

    @Deprecated
    @Override
    default public Long computeIfAbsent(Long key, Function<? super Long, ? extends Long> mappingFunction) {
        return Map.super.computeIfAbsent(key, mappingFunction);
    }

    @Deprecated
    @Override
    default public Long computeIfPresent(Long key, BiFunction<? super Long, ? super Long, ? extends Long> remappingFunction) {
        return Map.super.computeIfPresent(key, remappingFunction);
    }

    @Deprecated
    @Override
    default public Long compute(Long key, BiFunction<? super Long, ? super Long, ? extends Long> remappingFunction) {
        return Map.super.compute(key, remappingFunction);
    }

    @Deprecated
    @Override
    default public Long merge(Long key, Long value, BiFunction<? super Long, ? super Long, ? extends Long> remappingFunction) {
        return Map.super.merge(key, value, remappingFunction);
    }

    public static interface Entry
    extends Map.Entry<Long, Long> {
        public long getLongKey();

        @Deprecated
        @Override
        public Long getKey();

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

    public static interface FastEntrySet
    extends ObjectSet<Entry> {
        public ObjectIterator<Entry> fastIterator();

        default public void fastForEach(Consumer<? super Entry> consumer) {
            this.forEach(consumer);
        }
    }

}

