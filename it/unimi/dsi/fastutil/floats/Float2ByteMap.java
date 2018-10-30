/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.SafeMath;
import it.unimi.dsi.fastutil.bytes.ByteCollection;
import it.unimi.dsi.fastutil.floats.Float2ByteFunction;
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
import java.util.function.DoubleToIntFunction;
import java.util.function.Function;

public interface Float2ByteMap
extends Float2ByteFunction,
Map<Float, Byte> {
    @Override
    public int size();

    @Override
    default public void clear() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void defaultReturnValue(byte var1);

    @Override
    public byte defaultReturnValue();

    public ObjectSet<Entry> float2ByteEntrySet();

    @Deprecated
    @Override
    default public ObjectSet<Map.Entry<Float, Byte>> entrySet() {
        return this.float2ByteEntrySet();
    }

    @Deprecated
    @Override
    default public Byte put(Float key, Byte value) {
        return Float2ByteFunction.super.put(key, value);
    }

    @Deprecated
    @Override
    default public Byte get(Object key) {
        return Float2ByteFunction.super.get(key);
    }

    @Deprecated
    @Override
    default public Byte remove(Object key) {
        return Float2ByteFunction.super.remove(key);
    }

    public FloatSet keySet();

    public ByteCollection values();

    @Override
    public boolean containsKey(float var1);

    @Deprecated
    @Override
    default public boolean containsKey(Object key) {
        return Float2ByteFunction.super.containsKey(key);
    }

    public boolean containsValue(byte var1);

    @Deprecated
    @Override
    default public boolean containsValue(Object value) {
        return value == null ? false : this.containsValue((Byte)value);
    }

    default public byte getOrDefault(float key, byte defaultValue) {
        byte v = this.get(key);
        return v != this.defaultReturnValue() || this.containsKey(key) ? v : defaultValue;
    }

    @Override
    default public byte putIfAbsent(float key, byte value) {
        byte drv;
        byte v = this.get(key);
        if (v != (drv = this.defaultReturnValue()) || this.containsKey(key)) {
            return v;
        }
        this.put(key, value);
        return drv;
    }

    default public boolean remove(float key, byte value) {
        byte curValue = this.get(key);
        if (curValue != value || curValue == this.defaultReturnValue() && !this.containsKey(key)) {
            return false;
        }
        this.remove(key);
        return true;
    }

    @Override
    default public boolean replace(float key, byte oldValue, byte newValue) {
        byte curValue = this.get(key);
        if (curValue != oldValue || curValue == this.defaultReturnValue() && !this.containsKey(key)) {
            return false;
        }
        this.put(key, newValue);
        return true;
    }

    @Override
    default public byte replace(float key, byte value) {
        return this.containsKey(key) ? this.put(key, value) : this.defaultReturnValue();
    }

    default public byte computeIfAbsent(float key, DoubleToIntFunction mappingFunction) {
        Objects.requireNonNull(mappingFunction);
        byte v = this.get(key);
        if (v != this.defaultReturnValue() || this.containsKey(key)) {
            return v;
        }
        byte newValue = SafeMath.safeIntToByte(mappingFunction.applyAsInt(key));
        this.put(key, newValue);
        return newValue;
    }

    default public byte computeIfAbsentNullable(float key, DoubleFunction<? extends Byte> mappingFunction) {
        Objects.requireNonNull(mappingFunction);
        byte v = this.get(key);
        byte drv = this.defaultReturnValue();
        if (v != drv || this.containsKey(key)) {
            return v;
        }
        Byte mappedValue = mappingFunction.apply(key);
        if (mappedValue == null) {
            return drv;
        }
        byte newValue = mappedValue;
        this.put(key, newValue);
        return newValue;
    }

    default public byte computeIfAbsentPartial(float key, Float2ByteFunction mappingFunction) {
        Objects.requireNonNull(mappingFunction);
        byte v = this.get(key);
        byte drv = this.defaultReturnValue();
        if (v != drv || this.containsKey(key)) {
            return v;
        }
        if (!mappingFunction.containsKey(key)) {
            return drv;
        }
        byte newValue = mappingFunction.get(key);
        this.put(key, newValue);
        return newValue;
    }

    @Override
    default public byte computeIfPresent(float key, BiFunction<? super Float, ? super Byte, ? extends Byte> remappingFunction) {
        Objects.requireNonNull(remappingFunction);
        byte oldValue = this.get(key);
        byte drv = this.defaultReturnValue();
        if (oldValue == drv && !this.containsKey(key)) {
            return drv;
        }
        Byte newValue = remappingFunction.apply(Float.valueOf(key), (Byte)oldValue);
        if (newValue == null) {
            this.remove(key);
            return drv;
        }
        byte newVal = newValue;
        this.put(key, newVal);
        return newVal;
    }

    @Override
    default public byte compute(float key, BiFunction<? super Float, ? super Byte, ? extends Byte> remappingFunction) {
        Objects.requireNonNull(remappingFunction);
        byte oldValue = this.get(key);
        byte drv = this.defaultReturnValue();
        boolean contained = oldValue != drv || this.containsKey(key);
        Byte newValue = remappingFunction.apply(Float.valueOf(key), contained ? Byte.valueOf(oldValue) : null);
        if (newValue == null) {
            if (contained) {
                this.remove(key);
            }
            return drv;
        }
        byte newVal = newValue;
        this.put(key, newVal);
        return newVal;
    }

    @Override
    default public byte merge(float key, byte value, BiFunction<? super Byte, ? super Byte, ? extends Byte> remappingFunction) {
        byte newValue;
        Objects.requireNonNull(remappingFunction);
        byte oldValue = this.get(key);
        byte drv = this.defaultReturnValue();
        if (oldValue != drv || this.containsKey(key)) {
            Byte mergedValue = remappingFunction.apply((Byte)oldValue, (Byte)value);
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
    default public Byte getOrDefault(Object key, Byte defaultValue) {
        return Map.super.getOrDefault(key, defaultValue);
    }

    @Deprecated
    @Override
    default public Byte putIfAbsent(Float key, Byte value) {
        return Map.super.putIfAbsent(key, value);
    }

    @Deprecated
    @Override
    default public boolean remove(Object key, Object value) {
        return Map.super.remove(key, value);
    }

    @Deprecated
    @Override
    default public boolean replace(Float key, Byte oldValue, Byte newValue) {
        return Map.super.replace(key, oldValue, newValue);
    }

    @Deprecated
    @Override
    default public Byte replace(Float key, Byte value) {
        return Map.super.replace(key, value);
    }

    @Deprecated
    @Override
    default public Byte computeIfAbsent(Float key, Function<? super Float, ? extends Byte> mappingFunction) {
        return Map.super.computeIfAbsent(key, mappingFunction);
    }

    @Deprecated
    @Override
    default public Byte computeIfPresent(Float key, BiFunction<? super Float, ? super Byte, ? extends Byte> remappingFunction) {
        return Map.super.computeIfPresent(key, remappingFunction);
    }

    @Deprecated
    @Override
    default public Byte compute(Float key, BiFunction<? super Float, ? super Byte, ? extends Byte> remappingFunction) {
        return Map.super.compute(key, remappingFunction);
    }

    @Deprecated
    @Override
    default public Byte merge(Float key, Byte value, BiFunction<? super Byte, ? super Byte, ? extends Byte> remappingFunction) {
        return Map.super.merge(key, value, remappingFunction);
    }

    public static interface Entry
    extends Map.Entry<Float, Byte> {
        public float getFloatKey();

        @Deprecated
        @Override
        public Float getKey();

        public byte getByteValue();

        @Override
        public byte setValue(byte var1);

        @Deprecated
        @Override
        public Byte getValue();

        @Deprecated
        @Override
        public Byte setValue(Byte var1);
    }

    public static interface FastEntrySet
    extends ObjectSet<Entry> {
        public ObjectIterator<Entry> fastIterator();

        default public void fastForEach(Consumer<? super Entry> consumer) {
            this.forEach(consumer);
        }
    }

}

