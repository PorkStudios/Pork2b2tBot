/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.doubles;

import it.unimi.dsi.fastutil.SafeMath;
import it.unimi.dsi.fastutil.doubles.Double2FloatFunction;
import it.unimi.dsi.fastutil.doubles.DoubleSet;
import it.unimi.dsi.fastutil.floats.FloatCollection;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.DoubleFunction;
import java.util.function.DoubleUnaryOperator;
import java.util.function.Function;

public interface Double2FloatMap
extends Double2FloatFunction,
Map<Double, Float> {
    @Override
    public int size();

    @Override
    default public void clear() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void defaultReturnValue(float var1);

    @Override
    public float defaultReturnValue();

    public ObjectSet<Entry> double2FloatEntrySet();

    @Deprecated
    @Override
    default public ObjectSet<Map.Entry<Double, Float>> entrySet() {
        return this.double2FloatEntrySet();
    }

    @Deprecated
    @Override
    default public Float put(Double key, Float value) {
        return Double2FloatFunction.super.put(key, value);
    }

    @Deprecated
    @Override
    default public Float get(Object key) {
        return Double2FloatFunction.super.get(key);
    }

    @Deprecated
    @Override
    default public Float remove(Object key) {
        return Double2FloatFunction.super.remove(key);
    }

    public DoubleSet keySet();

    public FloatCollection values();

    @Override
    public boolean containsKey(double var1);

    @Deprecated
    @Override
    default public boolean containsKey(Object key) {
        return Double2FloatFunction.super.containsKey(key);
    }

    public boolean containsValue(float var1);

    @Deprecated
    @Override
    default public boolean containsValue(Object value) {
        return value == null ? false : this.containsValue(((Float)value).floatValue());
    }

    default public float getOrDefault(double key, float defaultValue) {
        float v = this.get(key);
        return v != this.defaultReturnValue() || this.containsKey(key) ? v : defaultValue;
    }

    @Override
    default public float putIfAbsent(double key, float value) {
        float drv;
        float v = this.get(key);
        if (v != (drv = this.defaultReturnValue()) || this.containsKey(key)) {
            return v;
        }
        this.put(key, value);
        return drv;
    }

    default public boolean remove(double key, float value) {
        float curValue = this.get(key);
        if (Float.floatToIntBits(curValue) != Float.floatToIntBits(value) || curValue == this.defaultReturnValue() && !this.containsKey(key)) {
            return false;
        }
        this.remove(key);
        return true;
    }

    @Override
    default public boolean replace(double key, float oldValue, float newValue) {
        float curValue = this.get(key);
        if (Float.floatToIntBits(curValue) != Float.floatToIntBits(oldValue) || curValue == this.defaultReturnValue() && !this.containsKey(key)) {
            return false;
        }
        this.put(key, newValue);
        return true;
    }

    @Override
    default public float replace(double key, float value) {
        return this.containsKey(key) ? this.put(key, value) : this.defaultReturnValue();
    }

    default public float computeIfAbsent(double key, DoubleUnaryOperator mappingFunction) {
        Objects.requireNonNull(mappingFunction);
        float v = this.get(key);
        if (v != this.defaultReturnValue() || this.containsKey(key)) {
            return v;
        }
        float newValue = SafeMath.safeDoubleToFloat(mappingFunction.applyAsDouble(key));
        this.put(key, newValue);
        return newValue;
    }

    default public float computeIfAbsentNullable(double key, DoubleFunction<? extends Float> mappingFunction) {
        Objects.requireNonNull(mappingFunction);
        float v = this.get(key);
        float drv = this.defaultReturnValue();
        if (v != drv || this.containsKey(key)) {
            return v;
        }
        Float mappedValue = mappingFunction.apply(key);
        if (mappedValue == null) {
            return drv;
        }
        float newValue = mappedValue.floatValue();
        this.put(key, newValue);
        return newValue;
    }

    default public float computeIfAbsentPartial(double key, Double2FloatFunction mappingFunction) {
        Objects.requireNonNull(mappingFunction);
        float v = this.get(key);
        float drv = this.defaultReturnValue();
        if (v != drv || this.containsKey(key)) {
            return v;
        }
        if (!mappingFunction.containsKey(key)) {
            return drv;
        }
        float newValue = mappingFunction.get(key);
        this.put(key, newValue);
        return newValue;
    }

    @Override
    default public float computeIfPresent(double key, BiFunction<? super Double, ? super Float, ? extends Float> remappingFunction) {
        Objects.requireNonNull(remappingFunction);
        float oldValue = this.get(key);
        float drv = this.defaultReturnValue();
        if (oldValue == drv && !this.containsKey(key)) {
            return drv;
        }
        Float newValue = remappingFunction.apply((Double)key, Float.valueOf(oldValue));
        if (newValue == null) {
            this.remove(key);
            return drv;
        }
        float newVal = newValue.floatValue();
        this.put(key, newVal);
        return newVal;
    }

    @Override
    default public float compute(double key, BiFunction<? super Double, ? super Float, ? extends Float> remappingFunction) {
        Objects.requireNonNull(remappingFunction);
        float oldValue = this.get(key);
        float drv = this.defaultReturnValue();
        boolean contained = oldValue != drv || this.containsKey(key);
        Float newValue = remappingFunction.apply((Double)key, contained ? Float.valueOf(oldValue) : null);
        if (newValue == null) {
            if (contained) {
                this.remove(key);
            }
            return drv;
        }
        float newVal = newValue.floatValue();
        this.put(key, newVal);
        return newVal;
    }

    @Override
    default public float merge(double key, float value, BiFunction<? super Float, ? super Float, ? extends Float> remappingFunction) {
        float newValue;
        Objects.requireNonNull(remappingFunction);
        float oldValue = this.get(key);
        float drv = this.defaultReturnValue();
        if (oldValue != drv || this.containsKey(key)) {
            Float mergedValue = remappingFunction.apply(Float.valueOf(oldValue), Float.valueOf(value));
            if (mergedValue == null) {
                this.remove(key);
                return drv;
            }
            newValue = mergedValue.floatValue();
        } else {
            newValue = value;
        }
        this.put(key, newValue);
        return newValue;
    }

    @Deprecated
    @Override
    default public Float getOrDefault(Object key, Float defaultValue) {
        return Map.super.getOrDefault(key, defaultValue);
    }

    @Deprecated
    @Override
    default public Float putIfAbsent(Double key, Float value) {
        return Map.super.putIfAbsent(key, value);
    }

    @Deprecated
    @Override
    default public boolean remove(Object key, Object value) {
        return Map.super.remove(key, value);
    }

    @Deprecated
    @Override
    default public boolean replace(Double key, Float oldValue, Float newValue) {
        return Map.super.replace(key, oldValue, newValue);
    }

    @Deprecated
    @Override
    default public Float replace(Double key, Float value) {
        return Map.super.replace(key, value);
    }

    @Deprecated
    @Override
    default public Float computeIfAbsent(Double key, Function<? super Double, ? extends Float> mappingFunction) {
        return Map.super.computeIfAbsent(key, mappingFunction);
    }

    @Deprecated
    @Override
    default public Float computeIfPresent(Double key, BiFunction<? super Double, ? super Float, ? extends Float> remappingFunction) {
        return Map.super.computeIfPresent(key, remappingFunction);
    }

    @Deprecated
    @Override
    default public Float compute(Double key, BiFunction<? super Double, ? super Float, ? extends Float> remappingFunction) {
        return Map.super.compute(key, remappingFunction);
    }

    @Deprecated
    @Override
    default public Float merge(Double key, Float value, BiFunction<? super Float, ? super Float, ? extends Float> remappingFunction) {
        return Map.super.merge(key, value, remappingFunction);
    }

    public static interface Entry
    extends Map.Entry<Double, Float> {
        public double getDoubleKey();

        @Deprecated
        @Override
        public Double getKey();

        public float getFloatValue();

        @Override
        public float setValue(float var1);

        @Deprecated
        @Override
        public Float getValue();

        @Deprecated
        @Override
        public Float setValue(Float var1);
    }

    public static interface FastEntrySet
    extends ObjectSet<Entry> {
        public ObjectIterator<Entry> fastIterator();

        default public void fastForEach(Consumer<? super Entry> consumer) {
            this.forEach(consumer);
        }
    }

}

