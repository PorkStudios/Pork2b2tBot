/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.SafeMath;
import it.unimi.dsi.fastutil.floats.Float2FloatFunction;
import it.unimi.dsi.fastutil.floats.FloatCollection;
import it.unimi.dsi.fastutil.floats.FloatSet;
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

public interface Float2FloatMap
extends Float2FloatFunction,
Map<Float, Float> {
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

    public ObjectSet<Entry> float2FloatEntrySet();

    @Deprecated
    @Override
    default public ObjectSet<Map.Entry<Float, Float>> entrySet() {
        return this.float2FloatEntrySet();
    }

    @Deprecated
    @Override
    default public Float put(Float key, Float value) {
        return Float2FloatFunction.super.put(key, value);
    }

    @Deprecated
    @Override
    default public Float get(Object key) {
        return Float2FloatFunction.super.get(key);
    }

    @Deprecated
    @Override
    default public Float remove(Object key) {
        return Float2FloatFunction.super.remove(key);
    }

    public FloatSet keySet();

    public FloatCollection values();

    @Override
    public boolean containsKey(float var1);

    @Deprecated
    @Override
    default public boolean containsKey(Object key) {
        return Float2FloatFunction.super.containsKey(key);
    }

    public boolean containsValue(float var1);

    @Deprecated
    @Override
    default public boolean containsValue(Object value) {
        return value == null ? false : this.containsValue(((Float)value).floatValue());
    }

    default public float getOrDefault(float key, float defaultValue) {
        float v = this.get(key);
        return v != this.defaultReturnValue() || this.containsKey(key) ? v : defaultValue;
    }

    @Override
    default public float putIfAbsent(float key, float value) {
        float drv;
        float v = this.get(key);
        if (v != (drv = this.defaultReturnValue()) || this.containsKey(key)) {
            return v;
        }
        this.put(key, value);
        return drv;
    }

    default public boolean remove(float key, float value) {
        float curValue = this.get(key);
        if (Float.floatToIntBits(curValue) != Float.floatToIntBits(value) || curValue == this.defaultReturnValue() && !this.containsKey(key)) {
            return false;
        }
        this.remove(key);
        return true;
    }

    @Override
    default public boolean replace(float key, float oldValue, float newValue) {
        float curValue = this.get(key);
        if (Float.floatToIntBits(curValue) != Float.floatToIntBits(oldValue) || curValue == this.defaultReturnValue() && !this.containsKey(key)) {
            return false;
        }
        this.put(key, newValue);
        return true;
    }

    @Override
    default public float replace(float key, float value) {
        return this.containsKey(key) ? this.put(key, value) : this.defaultReturnValue();
    }

    default public float computeIfAbsent(float key, DoubleUnaryOperator mappingFunction) {
        Objects.requireNonNull(mappingFunction);
        float v = this.get(key);
        if (v != this.defaultReturnValue() || this.containsKey(key)) {
            return v;
        }
        float newValue = SafeMath.safeDoubleToFloat(mappingFunction.applyAsDouble(key));
        this.put(key, newValue);
        return newValue;
    }

    default public float computeIfAbsentNullable(float key, DoubleFunction<? extends Float> mappingFunction) {
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

    default public float computeIfAbsentPartial(float key, Float2FloatFunction mappingFunction) {
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
    default public float computeIfPresent(float key, BiFunction<? super Float, ? super Float, ? extends Float> remappingFunction) {
        Objects.requireNonNull(remappingFunction);
        float oldValue = this.get(key);
        float drv = this.defaultReturnValue();
        if (oldValue == drv && !this.containsKey(key)) {
            return drv;
        }
        Float newValue = remappingFunction.apply(Float.valueOf(key), Float.valueOf(oldValue));
        if (newValue == null) {
            this.remove(key);
            return drv;
        }
        float newVal = newValue.floatValue();
        this.put(key, newVal);
        return newVal;
    }

    @Override
    default public float compute(float key, BiFunction<? super Float, ? super Float, ? extends Float> remappingFunction) {
        Objects.requireNonNull(remappingFunction);
        float oldValue = this.get(key);
        float drv = this.defaultReturnValue();
        boolean contained = oldValue != drv || this.containsKey(key);
        Float newValue = remappingFunction.apply(Float.valueOf(key), contained ? Float.valueOf(oldValue) : null);
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
    default public float merge(float key, float value, BiFunction<? super Float, ? super Float, ? extends Float> remappingFunction) {
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
    default public Float putIfAbsent(Float key, Float value) {
        return Map.super.putIfAbsent(key, value);
    }

    @Deprecated
    @Override
    default public boolean remove(Object key, Object value) {
        return Map.super.remove(key, value);
    }

    @Deprecated
    @Override
    default public boolean replace(Float key, Float oldValue, Float newValue) {
        return Map.super.replace(key, oldValue, newValue);
    }

    @Deprecated
    @Override
    default public Float replace(Float key, Float value) {
        return Map.super.replace(key, value);
    }

    @Deprecated
    @Override
    default public Float computeIfAbsent(Float key, Function<? super Float, ? extends Float> mappingFunction) {
        return Map.super.computeIfAbsent(key, mappingFunction);
    }

    @Deprecated
    @Override
    default public Float computeIfPresent(Float key, BiFunction<? super Float, ? super Float, ? extends Float> remappingFunction) {
        return Map.super.computeIfPresent(key, remappingFunction);
    }

    @Deprecated
    @Override
    default public Float compute(Float key, BiFunction<? super Float, ? super Float, ? extends Float> remappingFunction) {
        return Map.super.compute(key, remappingFunction);
    }

    @Deprecated
    @Override
    default public Float merge(Float key, Float value, BiFunction<? super Float, ? super Float, ? extends Float> remappingFunction) {
        return Map.super.merge(key, value, remappingFunction);
    }

    public static interface Entry
    extends Map.Entry<Float, Float> {
        public float getFloatKey();

        @Deprecated
        @Override
        public Float getKey();

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

