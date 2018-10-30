/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.doubles;

import it.unimi.dsi.fastutil.doubles.Double2IntFunction;
import it.unimi.dsi.fastutil.doubles.DoubleSet;
import it.unimi.dsi.fastutil.ints.IntCollection;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.DoubleFunction;
import java.util.function.DoubleToIntFunction;
import java.util.function.Function;

public interface Double2IntMap
extends Double2IntFunction,
Map<Double, Integer> {
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

    public ObjectSet<Entry> double2IntEntrySet();

    @Deprecated
    @Override
    default public ObjectSet<Map.Entry<Double, Integer>> entrySet() {
        return this.double2IntEntrySet();
    }

    @Deprecated
    @Override
    default public Integer put(Double key, Integer value) {
        return Double2IntFunction.super.put(key, value);
    }

    @Deprecated
    @Override
    default public Integer get(Object key) {
        return Double2IntFunction.super.get(key);
    }

    @Deprecated
    @Override
    default public Integer remove(Object key) {
        return Double2IntFunction.super.remove(key);
    }

    public DoubleSet keySet();

    public IntCollection values();

    @Override
    public boolean containsKey(double var1);

    @Deprecated
    @Override
    default public boolean containsKey(Object key) {
        return Double2IntFunction.super.containsKey(key);
    }

    public boolean containsValue(int var1);

    @Deprecated
    @Override
    default public boolean containsValue(Object value) {
        return value == null ? false : this.containsValue((Integer)value);
    }

    default public int getOrDefault(double key, int defaultValue) {
        int v = this.get(key);
        return v != this.defaultReturnValue() || this.containsKey(key) ? v : defaultValue;
    }

    @Override
    default public int putIfAbsent(double key, int value) {
        int drv;
        int v = this.get(key);
        if (v != (drv = this.defaultReturnValue()) || this.containsKey(key)) {
            return v;
        }
        this.put(key, value);
        return drv;
    }

    default public boolean remove(double key, int value) {
        int curValue = this.get(key);
        if (curValue != value || curValue == this.defaultReturnValue() && !this.containsKey(key)) {
            return false;
        }
        this.remove(key);
        return true;
    }

    @Override
    default public boolean replace(double key, int oldValue, int newValue) {
        int curValue = this.get(key);
        if (curValue != oldValue || curValue == this.defaultReturnValue() && !this.containsKey(key)) {
            return false;
        }
        this.put(key, newValue);
        return true;
    }

    @Override
    default public int replace(double key, int value) {
        return this.containsKey(key) ? this.put(key, value) : this.defaultReturnValue();
    }

    default public int computeIfAbsent(double key, DoubleToIntFunction mappingFunction) {
        Objects.requireNonNull(mappingFunction);
        int v = this.get(key);
        if (v != this.defaultReturnValue() || this.containsKey(key)) {
            return v;
        }
        int newValue = mappingFunction.applyAsInt(key);
        this.put(key, newValue);
        return newValue;
    }

    default public int computeIfAbsentNullable(double key, DoubleFunction<? extends Integer> mappingFunction) {
        Objects.requireNonNull(mappingFunction);
        int v = this.get(key);
        int drv = this.defaultReturnValue();
        if (v != drv || this.containsKey(key)) {
            return v;
        }
        Integer mappedValue = mappingFunction.apply(key);
        if (mappedValue == null) {
            return drv;
        }
        int newValue = mappedValue;
        this.put(key, newValue);
        return newValue;
    }

    default public int computeIfAbsentPartial(double key, Double2IntFunction mappingFunction) {
        Objects.requireNonNull(mappingFunction);
        int v = this.get(key);
        int drv = this.defaultReturnValue();
        if (v != drv || this.containsKey(key)) {
            return v;
        }
        if (!mappingFunction.containsKey(key)) {
            return drv;
        }
        int newValue = mappingFunction.get(key);
        this.put(key, newValue);
        return newValue;
    }

    @Override
    default public int computeIfPresent(double key, BiFunction<? super Double, ? super Integer, ? extends Integer> remappingFunction) {
        Objects.requireNonNull(remappingFunction);
        int oldValue = this.get(key);
        int drv = this.defaultReturnValue();
        if (oldValue == drv && !this.containsKey(key)) {
            return drv;
        }
        Integer newValue = remappingFunction.apply((Double)key, (Integer)oldValue);
        if (newValue == null) {
            this.remove(key);
            return drv;
        }
        int newVal = newValue;
        this.put(key, newVal);
        return newVal;
    }

    @Override
    default public int compute(double key, BiFunction<? super Double, ? super Integer, ? extends Integer> remappingFunction) {
        Objects.requireNonNull(remappingFunction);
        int oldValue = this.get(key);
        int drv = this.defaultReturnValue();
        boolean contained = oldValue != drv || this.containsKey(key);
        Integer newValue = remappingFunction.apply((Double)key, contained ? Integer.valueOf(oldValue) : null);
        if (newValue == null) {
            if (contained) {
                this.remove(key);
            }
            return drv;
        }
        int newVal = newValue;
        this.put(key, newVal);
        return newVal;
    }

    @Override
    default public int merge(double key, int value, BiFunction<? super Integer, ? super Integer, ? extends Integer> remappingFunction) {
        int newValue;
        Objects.requireNonNull(remappingFunction);
        int oldValue = this.get(key);
        int drv = this.defaultReturnValue();
        if (oldValue != drv || this.containsKey(key)) {
            Integer mergedValue = remappingFunction.apply((Integer)oldValue, (Integer)value);
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
    default public Integer getOrDefault(Object key, Integer defaultValue) {
        return Map.super.getOrDefault(key, defaultValue);
    }

    @Deprecated
    @Override
    default public Integer putIfAbsent(Double key, Integer value) {
        return Map.super.putIfAbsent(key, value);
    }

    @Deprecated
    @Override
    default public boolean remove(Object key, Object value) {
        return Map.super.remove(key, value);
    }

    @Deprecated
    @Override
    default public boolean replace(Double key, Integer oldValue, Integer newValue) {
        return Map.super.replace(key, oldValue, newValue);
    }

    @Deprecated
    @Override
    default public Integer replace(Double key, Integer value) {
        return Map.super.replace(key, value);
    }

    @Deprecated
    @Override
    default public Integer computeIfAbsent(Double key, Function<? super Double, ? extends Integer> mappingFunction) {
        return Map.super.computeIfAbsent(key, mappingFunction);
    }

    @Deprecated
    @Override
    default public Integer computeIfPresent(Double key, BiFunction<? super Double, ? super Integer, ? extends Integer> remappingFunction) {
        return Map.super.computeIfPresent(key, remappingFunction);
    }

    @Deprecated
    @Override
    default public Integer compute(Double key, BiFunction<? super Double, ? super Integer, ? extends Integer> remappingFunction) {
        return Map.super.compute(key, remappingFunction);
    }

    @Deprecated
    @Override
    default public Integer merge(Double key, Integer value, BiFunction<? super Integer, ? super Integer, ? extends Integer> remappingFunction) {
        return Map.super.merge(key, value, remappingFunction);
    }

    public static interface Entry
    extends Map.Entry<Double, Integer> {
        public double getDoubleKey();

        @Deprecated
        @Override
        public Double getKey();

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

    public static interface FastEntrySet
    extends ObjectSet<Entry> {
        public ObjectIterator<Entry> fastIterator();

        default public void fastForEach(Consumer<? super Entry> consumer) {
            this.forEach(consumer);
        }
    }

}

