/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.doubles;

import it.unimi.dsi.fastutil.doubles.Double2LongFunction;
import it.unimi.dsi.fastutil.doubles.DoubleSet;
import it.unimi.dsi.fastutil.longs.LongCollection;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.DoubleFunction;
import java.util.function.DoubleToLongFunction;
import java.util.function.Function;

public interface Double2LongMap
extends Double2LongFunction,
Map<Double, Long> {
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

    public ObjectSet<Entry> double2LongEntrySet();

    @Deprecated
    @Override
    default public ObjectSet<Map.Entry<Double, Long>> entrySet() {
        return this.double2LongEntrySet();
    }

    @Deprecated
    @Override
    default public Long put(Double key, Long value) {
        return Double2LongFunction.super.put(key, value);
    }

    @Deprecated
    @Override
    default public Long get(Object key) {
        return Double2LongFunction.super.get(key);
    }

    @Deprecated
    @Override
    default public Long remove(Object key) {
        return Double2LongFunction.super.remove(key);
    }

    public DoubleSet keySet();

    public LongCollection values();

    @Override
    public boolean containsKey(double var1);

    @Deprecated
    @Override
    default public boolean containsKey(Object key) {
        return Double2LongFunction.super.containsKey(key);
    }

    public boolean containsValue(long var1);

    @Deprecated
    @Override
    default public boolean containsValue(Object value) {
        return value == null ? false : this.containsValue((Long)value);
    }

    default public long getOrDefault(double key, long defaultValue) {
        long v = this.get(key);
        return v != this.defaultReturnValue() || this.containsKey(key) ? v : defaultValue;
    }

    @Override
    default public long putIfAbsent(double key, long value) {
        long drv;
        long v = this.get(key);
        if (v != (drv = this.defaultReturnValue()) || this.containsKey(key)) {
            return v;
        }
        this.put(key, value);
        return drv;
    }

    default public boolean remove(double key, long value) {
        long curValue = this.get(key);
        if (curValue != value || curValue == this.defaultReturnValue() && !this.containsKey(key)) {
            return false;
        }
        this.remove(key);
        return true;
    }

    @Override
    default public boolean replace(double key, long oldValue, long newValue) {
        long curValue = this.get(key);
        if (curValue != oldValue || curValue == this.defaultReturnValue() && !this.containsKey(key)) {
            return false;
        }
        this.put(key, newValue);
        return true;
    }

    @Override
    default public long replace(double key, long value) {
        return this.containsKey(key) ? this.put(key, value) : this.defaultReturnValue();
    }

    default public long computeIfAbsent(double key, DoubleToLongFunction mappingFunction) {
        Objects.requireNonNull(mappingFunction);
        long v = this.get(key);
        if (v != this.defaultReturnValue() || this.containsKey(key)) {
            return v;
        }
        long newValue = mappingFunction.applyAsLong(key);
        this.put(key, newValue);
        return newValue;
    }

    default public long computeIfAbsentNullable(double key, DoubleFunction<? extends Long> mappingFunction) {
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

    default public long computeIfAbsentPartial(double key, Double2LongFunction mappingFunction) {
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
    default public long computeIfPresent(double key, BiFunction<? super Double, ? super Long, ? extends Long> remappingFunction) {
        Objects.requireNonNull(remappingFunction);
        long oldValue = this.get(key);
        long drv = this.defaultReturnValue();
        if (oldValue == drv && !this.containsKey(key)) {
            return drv;
        }
        Long newValue = remappingFunction.apply((Double)key, (Long)oldValue);
        if (newValue == null) {
            this.remove(key);
            return drv;
        }
        long newVal = newValue;
        this.put(key, newVal);
        return newVal;
    }

    @Override
    default public long compute(double key, BiFunction<? super Double, ? super Long, ? extends Long> remappingFunction) {
        Objects.requireNonNull(remappingFunction);
        long oldValue = this.get(key);
        long drv = this.defaultReturnValue();
        boolean contained = oldValue != drv || this.containsKey(key);
        Long newValue = remappingFunction.apply((Double)key, contained ? Long.valueOf(oldValue) : null);
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
    default public long merge(double key, long value, BiFunction<? super Long, ? super Long, ? extends Long> remappingFunction) {
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
    default public Long putIfAbsent(Double key, Long value) {
        return Map.super.putIfAbsent(key, value);
    }

    @Deprecated
    @Override
    default public boolean remove(Object key, Object value) {
        return Map.super.remove(key, value);
    }

    @Deprecated
    @Override
    default public boolean replace(Double key, Long oldValue, Long newValue) {
        return Map.super.replace(key, oldValue, newValue);
    }

    @Deprecated
    @Override
    default public Long replace(Double key, Long value) {
        return Map.super.replace(key, value);
    }

    @Deprecated
    @Override
    default public Long computeIfAbsent(Double key, Function<? super Double, ? extends Long> mappingFunction) {
        return Map.super.computeIfAbsent(key, mappingFunction);
    }

    @Deprecated
    @Override
    default public Long computeIfPresent(Double key, BiFunction<? super Double, ? super Long, ? extends Long> remappingFunction) {
        return Map.super.computeIfPresent(key, remappingFunction);
    }

    @Deprecated
    @Override
    default public Long compute(Double key, BiFunction<? super Double, ? super Long, ? extends Long> remappingFunction) {
        return Map.super.compute(key, remappingFunction);
    }

    @Deprecated
    @Override
    default public Long merge(Double key, Long value, BiFunction<? super Long, ? super Long, ? extends Long> remappingFunction) {
        return Map.super.merge(key, value, remappingFunction);
    }

    public static interface Entry
    extends Map.Entry<Double, Long> {
        public double getDoubleKey();

        @Deprecated
        @Override
        public Double getKey();

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

