/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.doubles.DoubleCollection;
import it.unimi.dsi.fastutil.longs.Long2DoubleFunction;
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
import java.util.function.LongToDoubleFunction;

public interface Long2DoubleMap
extends Long2DoubleFunction,
Map<Long, Double> {
    @Override
    public int size();

    @Override
    default public void clear() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void defaultReturnValue(double var1);

    @Override
    public double defaultReturnValue();

    public ObjectSet<Entry> long2DoubleEntrySet();

    @Deprecated
    @Override
    default public ObjectSet<Map.Entry<Long, Double>> entrySet() {
        return this.long2DoubleEntrySet();
    }

    @Deprecated
    @Override
    default public Double put(Long key, Double value) {
        return Long2DoubleFunction.super.put(key, value);
    }

    @Deprecated
    @Override
    default public Double get(Object key) {
        return Long2DoubleFunction.super.get(key);
    }

    @Deprecated
    @Override
    default public Double remove(Object key) {
        return Long2DoubleFunction.super.remove(key);
    }

    public LongSet keySet();

    public DoubleCollection values();

    @Override
    public boolean containsKey(long var1);

    @Deprecated
    @Override
    default public boolean containsKey(Object key) {
        return Long2DoubleFunction.super.containsKey(key);
    }

    public boolean containsValue(double var1);

    @Deprecated
    @Override
    default public boolean containsValue(Object value) {
        return value == null ? false : this.containsValue((Double)value);
    }

    default public double getOrDefault(long key, double defaultValue) {
        double v = this.get(key);
        return v != this.defaultReturnValue() || this.containsKey(key) ? v : defaultValue;
    }

    @Override
    default public double putIfAbsent(long key, double value) {
        double drv;
        double v = this.get(key);
        if (v != (drv = this.defaultReturnValue()) || this.containsKey(key)) {
            return v;
        }
        this.put(key, value);
        return drv;
    }

    default public boolean remove(long key, double value) {
        double curValue = this.get(key);
        if (Double.doubleToLongBits(curValue) != Double.doubleToLongBits(value) || curValue == this.defaultReturnValue() && !this.containsKey(key)) {
            return false;
        }
        this.remove(key);
        return true;
    }

    @Override
    default public boolean replace(long key, double oldValue, double newValue) {
        double curValue = this.get(key);
        if (Double.doubleToLongBits(curValue) != Double.doubleToLongBits(oldValue) || curValue == this.defaultReturnValue() && !this.containsKey(key)) {
            return false;
        }
        this.put(key, newValue);
        return true;
    }

    @Override
    default public double replace(long key, double value) {
        return this.containsKey(key) ? this.put(key, value) : this.defaultReturnValue();
    }

    default public double computeIfAbsent(long key, LongToDoubleFunction mappingFunction) {
        Objects.requireNonNull(mappingFunction);
        double v = this.get(key);
        if (v != this.defaultReturnValue() || this.containsKey(key)) {
            return v;
        }
        double newValue = mappingFunction.applyAsDouble(key);
        this.put(key, newValue);
        return newValue;
    }

    default public double computeIfAbsentNullable(long key, LongFunction<? extends Double> mappingFunction) {
        Objects.requireNonNull(mappingFunction);
        double v = this.get(key);
        double drv = this.defaultReturnValue();
        if (v != drv || this.containsKey(key)) {
            return v;
        }
        Double mappedValue = mappingFunction.apply(key);
        if (mappedValue == null) {
            return drv;
        }
        double newValue = mappedValue;
        this.put(key, newValue);
        return newValue;
    }

    default public double computeIfAbsentPartial(long key, Long2DoubleFunction mappingFunction) {
        Objects.requireNonNull(mappingFunction);
        double v = this.get(key);
        double drv = this.defaultReturnValue();
        if (v != drv || this.containsKey(key)) {
            return v;
        }
        if (!mappingFunction.containsKey(key)) {
            return drv;
        }
        double newValue = mappingFunction.get(key);
        this.put(key, newValue);
        return newValue;
    }

    @Override
    default public double computeIfPresent(long key, BiFunction<? super Long, ? super Double, ? extends Double> remappingFunction) {
        Objects.requireNonNull(remappingFunction);
        double oldValue = this.get(key);
        double drv = this.defaultReturnValue();
        if (oldValue == drv && !this.containsKey(key)) {
            return drv;
        }
        Double newValue = remappingFunction.apply((Long)key, (Double)oldValue);
        if (newValue == null) {
            this.remove(key);
            return drv;
        }
        double newVal = newValue;
        this.put(key, newVal);
        return newVal;
    }

    @Override
    default public double compute(long key, BiFunction<? super Long, ? super Double, ? extends Double> remappingFunction) {
        Objects.requireNonNull(remappingFunction);
        double oldValue = this.get(key);
        double drv = this.defaultReturnValue();
        boolean contained = oldValue != drv || this.containsKey(key);
        Double newValue = remappingFunction.apply((Long)key, contained ? Double.valueOf(oldValue) : null);
        if (newValue == null) {
            if (contained) {
                this.remove(key);
            }
            return drv;
        }
        double newVal = newValue;
        this.put(key, newVal);
        return newVal;
    }

    @Override
    default public double merge(long key, double value, BiFunction<? super Double, ? super Double, ? extends Double> remappingFunction) {
        double newValue;
        Objects.requireNonNull(remappingFunction);
        double oldValue = this.get(key);
        double drv = this.defaultReturnValue();
        if (oldValue != drv || this.containsKey(key)) {
            Double mergedValue = remappingFunction.apply((Double)oldValue, (Double)value);
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
    default public Double getOrDefault(Object key, Double defaultValue) {
        return Map.super.getOrDefault(key, defaultValue);
    }

    @Deprecated
    @Override
    default public Double putIfAbsent(Long key, Double value) {
        return Map.super.putIfAbsent(key, value);
    }

    @Deprecated
    @Override
    default public boolean remove(Object key, Object value) {
        return Map.super.remove(key, value);
    }

    @Deprecated
    @Override
    default public boolean replace(Long key, Double oldValue, Double newValue) {
        return Map.super.replace(key, oldValue, newValue);
    }

    @Deprecated
    @Override
    default public Double replace(Long key, Double value) {
        return Map.super.replace(key, value);
    }

    @Deprecated
    @Override
    default public Double computeIfAbsent(Long key, Function<? super Long, ? extends Double> mappingFunction) {
        return Map.super.computeIfAbsent(key, mappingFunction);
    }

    @Deprecated
    @Override
    default public Double computeIfPresent(Long key, BiFunction<? super Long, ? super Double, ? extends Double> remappingFunction) {
        return Map.super.computeIfPresent(key, remappingFunction);
    }

    @Deprecated
    @Override
    default public Double compute(Long key, BiFunction<? super Long, ? super Double, ? extends Double> remappingFunction) {
        return Map.super.compute(key, remappingFunction);
    }

    @Deprecated
    @Override
    default public Double merge(Long key, Double value, BiFunction<? super Double, ? super Double, ? extends Double> remappingFunction) {
        return Map.super.merge(key, value, remappingFunction);
    }

    public static interface Entry
    extends Map.Entry<Long, Double> {
        public long getLongKey();

        @Deprecated
        @Override
        public Long getKey();

        public double getDoubleValue();

        @Override
        public double setValue(double var1);

        @Deprecated
        @Override
        public Double getValue();

        @Deprecated
        @Override
        public Double setValue(Double var1);
    }

    public static interface FastEntrySet
    extends ObjectSet<Entry> {
        public ObjectIterator<Entry> fastIterator();

        default public void fastForEach(Consumer<? super Entry> consumer) {
            this.forEach(consumer);
        }
    }

}

