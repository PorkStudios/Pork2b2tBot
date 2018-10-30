/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.SafeMath;
import it.unimi.dsi.fastutil.floats.FloatCollection;
import it.unimi.dsi.fastutil.objects.Object2FloatFunction;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.ToDoubleFunction;

public interface Object2FloatMap<K>
extends Object2FloatFunction<K>,
Map<K, Float> {
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

    public ObjectSet<Entry<K>> object2FloatEntrySet();

    @Deprecated
    @Override
    default public ObjectSet<Map.Entry<K, Float>> entrySet() {
        return this.object2FloatEntrySet();
    }

    @Deprecated
    @Override
    default public Float put(K key, Float value) {
        return Object2FloatFunction.super.put(key, value);
    }

    @Deprecated
    @Override
    default public Float get(Object key) {
        return Object2FloatFunction.super.get(key);
    }

    @Deprecated
    @Override
    default public Float remove(Object key) {
        return Object2FloatFunction.super.remove(key);
    }

    @Override
    public ObjectSet<K> keySet();

    public FloatCollection values();

    @Override
    public boolean containsKey(Object var1);

    public boolean containsValue(float var1);

    @Deprecated
    @Override
    default public boolean containsValue(Object value) {
        return value == null ? false : this.containsValue(((Float)value).floatValue());
    }

    @Override
    default public float getOrDefault(Object key, float defaultValue) {
        float v = this.getFloat(key);
        return v != this.defaultReturnValue() || this.containsKey(key) ? v : defaultValue;
    }

    @Override
    default public float putIfAbsent(K key, float value) {
        float drv;
        float v = this.getFloat(key);
        if (v != (drv = this.defaultReturnValue()) || this.containsKey(key)) {
            return v;
        }
        this.put(key, value);
        return drv;
    }

    default public boolean remove(Object key, float value) {
        float curValue = this.getFloat(key);
        if (Float.floatToIntBits(curValue) != Float.floatToIntBits(value) || curValue == this.defaultReturnValue() && !this.containsKey(key)) {
            return false;
        }
        this.removeFloat(key);
        return true;
    }

    @Override
    default public boolean replace(K key, float oldValue, float newValue) {
        float curValue = this.getFloat(key);
        if (Float.floatToIntBits(curValue) != Float.floatToIntBits(oldValue) || curValue == this.defaultReturnValue() && !this.containsKey(key)) {
            return false;
        }
        this.put(key, newValue);
        return true;
    }

    @Override
    default public float replace(K key, float value) {
        return this.containsKey(key) ? this.put(key, value) : this.defaultReturnValue();
    }

    default public float computeFloatIfAbsent(K key, ToDoubleFunction<? super K> mappingFunction) {
        Objects.requireNonNull(mappingFunction);
        float v = this.getFloat(key);
        if (v != this.defaultReturnValue() || this.containsKey(key)) {
            return v;
        }
        float newValue = SafeMath.safeDoubleToFloat(mappingFunction.applyAsDouble(key));
        this.put(key, newValue);
        return newValue;
    }

    default public float computeFloatIfAbsentPartial(K key, Object2FloatFunction<? super K> mappingFunction) {
        Objects.requireNonNull(mappingFunction);
        float v = this.getFloat(key);
        float drv = this.defaultReturnValue();
        if (v != drv || this.containsKey(key)) {
            return v;
        }
        if (!mappingFunction.containsKey(key)) {
            return drv;
        }
        float newValue = mappingFunction.getFloat(key);
        this.put(key, newValue);
        return newValue;
    }

    default public float computeFloatIfPresent(K key, BiFunction<? super K, ? super Float, ? extends Float> remappingFunction) {
        Objects.requireNonNull(remappingFunction);
        float oldValue = this.getFloat(key);
        float drv = this.defaultReturnValue();
        if (oldValue == drv && !this.containsKey(key)) {
            return drv;
        }
        Float newValue = remappingFunction.apply(key, Float.valueOf(oldValue));
        if (newValue == null) {
            this.removeFloat(key);
            return drv;
        }
        float newVal = newValue.floatValue();
        this.put(key, newVal);
        return newVal;
    }

    default public float computeFloat(K key, BiFunction<? super K, ? super Float, ? extends Float> remappingFunction) {
        Objects.requireNonNull(remappingFunction);
        float oldValue = this.getFloat(key);
        float drv = this.defaultReturnValue();
        boolean contained = oldValue != drv || this.containsKey(key);
        Float newValue = remappingFunction.apply(key, contained ? Float.valueOf(oldValue) : null);
        if (newValue == null) {
            if (contained) {
                this.removeFloat(key);
            }
            return drv;
        }
        float newVal = newValue.floatValue();
        this.put(key, newVal);
        return newVal;
    }

    default public float mergeFloat(K key, float value, BiFunction<? super Float, ? super Float, ? extends Float> remappingFunction) {
        float newValue;
        Objects.requireNonNull(remappingFunction);
        float oldValue = this.getFloat(key);
        float drv = this.defaultReturnValue();
        if (oldValue != drv || this.containsKey(key)) {
            Float mergedValue = remappingFunction.apply(Float.valueOf(oldValue), Float.valueOf(value));
            if (mergedValue == null) {
                this.removeFloat(key);
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
    default public Float putIfAbsent(K key, Float value) {
        return Map.super.putIfAbsent(key, value);
    }

    @Deprecated
    @Override
    default public boolean remove(Object key, Object value) {
        return Map.super.remove(key, value);
    }

    @Deprecated
    @Override
    default public boolean replace(K key, Float oldValue, Float newValue) {
        return Map.super.replace(key, oldValue, newValue);
    }

    @Deprecated
    @Override
    default public Float replace(K key, Float value) {
        return Map.super.replace(key, value);
    }

    @Deprecated
    @Override
    default public Float merge(K key, Float value, BiFunction<? super Float, ? super Float, ? extends Float> remappingFunction) {
        return Map.super.merge(key, value, remappingFunction);
    }

    public static interface Entry<K>
    extends Map.Entry<K, Float> {
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

    public static interface FastEntrySet<K>
    extends ObjectSet<Entry<K>> {
        public ObjectIterator<Entry<K>> fastIterator();

        default public void fastForEach(Consumer<? super Entry<K>> consumer) {
            this.forEach(consumer);
        }
    }

}

