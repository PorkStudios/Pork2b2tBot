/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.chars.Char2DoubleFunction;
import it.unimi.dsi.fastutil.chars.CharSet;
import it.unimi.dsi.fastutil.doubles.DoubleCollection;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.IntToDoubleFunction;

public interface Char2DoubleMap
extends Char2DoubleFunction,
Map<Character, Double> {
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

    public ObjectSet<Entry> char2DoubleEntrySet();

    @Deprecated
    @Override
    default public ObjectSet<Map.Entry<Character, Double>> entrySet() {
        return this.char2DoubleEntrySet();
    }

    @Deprecated
    @Override
    default public Double put(Character key, Double value) {
        return Char2DoubleFunction.super.put(key, value);
    }

    @Deprecated
    @Override
    default public Double get(Object key) {
        return Char2DoubleFunction.super.get(key);
    }

    @Deprecated
    @Override
    default public Double remove(Object key) {
        return Char2DoubleFunction.super.remove(key);
    }

    public CharSet keySet();

    public DoubleCollection values();

    @Override
    public boolean containsKey(char var1);

    @Deprecated
    @Override
    default public boolean containsKey(Object key) {
        return Char2DoubleFunction.super.containsKey(key);
    }

    public boolean containsValue(double var1);

    @Deprecated
    @Override
    default public boolean containsValue(Object value) {
        return value == null ? false : this.containsValue((Double)value);
    }

    default public double getOrDefault(char key, double defaultValue) {
        double v = this.get(key);
        return v != this.defaultReturnValue() || this.containsKey(key) ? v : defaultValue;
    }

    @Override
    default public double putIfAbsent(char key, double value) {
        double drv;
        double v = this.get(key);
        if (v != (drv = this.defaultReturnValue()) || this.containsKey(key)) {
            return v;
        }
        this.put(key, value);
        return drv;
    }

    default public boolean remove(char key, double value) {
        double curValue = this.get(key);
        if (Double.doubleToLongBits(curValue) != Double.doubleToLongBits(value) || curValue == this.defaultReturnValue() && !this.containsKey(key)) {
            return false;
        }
        this.remove(key);
        return true;
    }

    @Override
    default public boolean replace(char key, double oldValue, double newValue) {
        double curValue = this.get(key);
        if (Double.doubleToLongBits(curValue) != Double.doubleToLongBits(oldValue) || curValue == this.defaultReturnValue() && !this.containsKey(key)) {
            return false;
        }
        this.put(key, newValue);
        return true;
    }

    @Override
    default public double replace(char key, double value) {
        return this.containsKey(key) ? this.put(key, value) : this.defaultReturnValue();
    }

    default public double computeIfAbsent(char key, IntToDoubleFunction mappingFunction) {
        Objects.requireNonNull(mappingFunction);
        double v = this.get(key);
        if (v != this.defaultReturnValue() || this.containsKey(key)) {
            return v;
        }
        double newValue = mappingFunction.applyAsDouble(key);
        this.put(key, newValue);
        return newValue;
    }

    default public double computeIfAbsentNullable(char key, IntFunction<? extends Double> mappingFunction) {
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

    default public double computeIfAbsentPartial(char key, Char2DoubleFunction mappingFunction) {
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
    default public double computeIfPresent(char key, BiFunction<? super Character, ? super Double, ? extends Double> remappingFunction) {
        Objects.requireNonNull(remappingFunction);
        double oldValue = this.get(key);
        double drv = this.defaultReturnValue();
        if (oldValue == drv && !this.containsKey(key)) {
            return drv;
        }
        Double newValue = remappingFunction.apply(Character.valueOf(key), (Double)oldValue);
        if (newValue == null) {
            this.remove(key);
            return drv;
        }
        double newVal = newValue;
        this.put(key, newVal);
        return newVal;
    }

    @Override
    default public double compute(char key, BiFunction<? super Character, ? super Double, ? extends Double> remappingFunction) {
        Objects.requireNonNull(remappingFunction);
        double oldValue = this.get(key);
        double drv = this.defaultReturnValue();
        boolean contained = oldValue != drv || this.containsKey(key);
        Double newValue = remappingFunction.apply(Character.valueOf(key), contained ? Double.valueOf(oldValue) : null);
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
    default public double merge(char key, double value, BiFunction<? super Double, ? super Double, ? extends Double> remappingFunction) {
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
    default public Double putIfAbsent(Character key, Double value) {
        return Map.super.putIfAbsent(key, value);
    }

    @Deprecated
    @Override
    default public boolean remove(Object key, Object value) {
        return Map.super.remove(key, value);
    }

    @Deprecated
    @Override
    default public boolean replace(Character key, Double oldValue, Double newValue) {
        return Map.super.replace(key, oldValue, newValue);
    }

    @Deprecated
    @Override
    default public Double replace(Character key, Double value) {
        return Map.super.replace(key, value);
    }

    @Deprecated
    @Override
    default public Double computeIfAbsent(Character key, Function<? super Character, ? extends Double> mappingFunction) {
        return Map.super.computeIfAbsent(key, mappingFunction);
    }

    @Deprecated
    @Override
    default public Double computeIfPresent(Character key, BiFunction<? super Character, ? super Double, ? extends Double> remappingFunction) {
        return Map.super.computeIfPresent(key, remappingFunction);
    }

    @Deprecated
    @Override
    default public Double compute(Character key, BiFunction<? super Character, ? super Double, ? extends Double> remappingFunction) {
        return Map.super.compute(key, remappingFunction);
    }

    @Deprecated
    @Override
    default public Double merge(Character key, Double value, BiFunction<? super Double, ? super Double, ? extends Double> remappingFunction) {
        return Map.super.merge(key, value, remappingFunction);
    }

    public static interface Entry
    extends Map.Entry<Character, Double> {
        public char getCharKey();

        @Deprecated
        @Override
        public Character getKey();

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

