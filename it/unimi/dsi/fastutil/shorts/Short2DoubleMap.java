/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.shorts;

import it.unimi.dsi.fastutil.doubles.DoubleCollection;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import it.unimi.dsi.fastutil.shorts.Short2DoubleFunction;
import it.unimi.dsi.fastutil.shorts.ShortSet;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.IntToDoubleFunction;

public interface Short2DoubleMap
extends Short2DoubleFunction,
Map<Short, Double> {
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

    public ObjectSet<Entry> short2DoubleEntrySet();

    @Deprecated
    @Override
    default public ObjectSet<Map.Entry<Short, Double>> entrySet() {
        return this.short2DoubleEntrySet();
    }

    @Deprecated
    @Override
    default public Double put(Short key, Double value) {
        return Short2DoubleFunction.super.put(key, value);
    }

    @Deprecated
    @Override
    default public Double get(Object key) {
        return Short2DoubleFunction.super.get(key);
    }

    @Deprecated
    @Override
    default public Double remove(Object key) {
        return Short2DoubleFunction.super.remove(key);
    }

    public ShortSet keySet();

    public DoubleCollection values();

    @Override
    public boolean containsKey(short var1);

    @Deprecated
    @Override
    default public boolean containsKey(Object key) {
        return Short2DoubleFunction.super.containsKey(key);
    }

    public boolean containsValue(double var1);

    @Deprecated
    @Override
    default public boolean containsValue(Object value) {
        return value == null ? false : this.containsValue((Double)value);
    }

    default public double getOrDefault(short key, double defaultValue) {
        double v = this.get(key);
        return v != this.defaultReturnValue() || this.containsKey(key) ? v : defaultValue;
    }

    @Override
    default public double putIfAbsent(short key, double value) {
        double drv;
        double v = this.get(key);
        if (v != (drv = this.defaultReturnValue()) || this.containsKey(key)) {
            return v;
        }
        this.put(key, value);
        return drv;
    }

    default public boolean remove(short key, double value) {
        double curValue = this.get(key);
        if (Double.doubleToLongBits(curValue) != Double.doubleToLongBits(value) || curValue == this.defaultReturnValue() && !this.containsKey(key)) {
            return false;
        }
        this.remove(key);
        return true;
    }

    @Override
    default public boolean replace(short key, double oldValue, double newValue) {
        double curValue = this.get(key);
        if (Double.doubleToLongBits(curValue) != Double.doubleToLongBits(oldValue) || curValue == this.defaultReturnValue() && !this.containsKey(key)) {
            return false;
        }
        this.put(key, newValue);
        return true;
    }

    @Override
    default public double replace(short key, double value) {
        return this.containsKey(key) ? this.put(key, value) : this.defaultReturnValue();
    }

    default public double computeIfAbsent(short key, IntToDoubleFunction mappingFunction) {
        Objects.requireNonNull(mappingFunction);
        double v = this.get(key);
        if (v != this.defaultReturnValue() || this.containsKey(key)) {
            return v;
        }
        double newValue = mappingFunction.applyAsDouble(key);
        this.put(key, newValue);
        return newValue;
    }

    default public double computeIfAbsentNullable(short key, IntFunction<? extends Double> mappingFunction) {
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

    default public double computeIfAbsentPartial(short key, Short2DoubleFunction mappingFunction) {
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
    default public double computeIfPresent(short key, BiFunction<? super Short, ? super Double, ? extends Double> remappingFunction) {
        Objects.requireNonNull(remappingFunction);
        double oldValue = this.get(key);
        double drv = this.defaultReturnValue();
        if (oldValue == drv && !this.containsKey(key)) {
            return drv;
        }
        Double newValue = remappingFunction.apply((Short)key, (Double)oldValue);
        if (newValue == null) {
            this.remove(key);
            return drv;
        }
        double newVal = newValue;
        this.put(key, newVal);
        return newVal;
    }

    @Override
    default public double compute(short key, BiFunction<? super Short, ? super Double, ? extends Double> remappingFunction) {
        Objects.requireNonNull(remappingFunction);
        double oldValue = this.get(key);
        double drv = this.defaultReturnValue();
        boolean contained = oldValue != drv || this.containsKey(key);
        Double newValue = remappingFunction.apply((Short)key, contained ? Double.valueOf(oldValue) : null);
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
    default public double merge(short key, double value, BiFunction<? super Double, ? super Double, ? extends Double> remappingFunction) {
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
    default public Double putIfAbsent(Short key, Double value) {
        return Map.super.putIfAbsent(key, value);
    }

    @Deprecated
    @Override
    default public boolean remove(Object key, Object value) {
        return Map.super.remove(key, value);
    }

    @Deprecated
    @Override
    default public boolean replace(Short key, Double oldValue, Double newValue) {
        return Map.super.replace(key, oldValue, newValue);
    }

    @Deprecated
    @Override
    default public Double replace(Short key, Double value) {
        return Map.super.replace(key, value);
    }

    @Deprecated
    @Override
    default public Double computeIfAbsent(Short key, Function<? super Short, ? extends Double> mappingFunction) {
        return Map.super.computeIfAbsent(key, mappingFunction);
    }

    @Deprecated
    @Override
    default public Double computeIfPresent(Short key, BiFunction<? super Short, ? super Double, ? extends Double> remappingFunction) {
        return Map.super.computeIfPresent(key, remappingFunction);
    }

    @Deprecated
    @Override
    default public Double compute(Short key, BiFunction<? super Short, ? super Double, ? extends Double> remappingFunction) {
        return Map.super.compute(key, remappingFunction);
    }

    @Deprecated
    @Override
    default public Double merge(Short key, Double value, BiFunction<? super Double, ? super Double, ? extends Double> remappingFunction) {
        return Map.super.merge(key, value, remappingFunction);
    }

    public static interface Entry
    extends Map.Entry<Short, Double> {
        public short getShortKey();

        @Deprecated
        @Override
        public Short getKey();

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

