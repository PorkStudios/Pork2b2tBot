/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.SafeMath;
import it.unimi.dsi.fastutil.bytes.ByteCollection;
import it.unimi.dsi.fastutil.objects.Object2ByteFunction;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.ToIntFunction;

public interface Object2ByteMap<K>
extends Object2ByteFunction<K>,
Map<K, Byte> {
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

    public ObjectSet<Entry<K>> object2ByteEntrySet();

    @Deprecated
    @Override
    default public ObjectSet<Map.Entry<K, Byte>> entrySet() {
        return this.object2ByteEntrySet();
    }

    @Deprecated
    @Override
    default public Byte put(K key, Byte value) {
        return Object2ByteFunction.super.put(key, value);
    }

    @Deprecated
    @Override
    default public Byte get(Object key) {
        return Object2ByteFunction.super.get(key);
    }

    @Deprecated
    @Override
    default public Byte remove(Object key) {
        return Object2ByteFunction.super.remove(key);
    }

    @Override
    public ObjectSet<K> keySet();

    public ByteCollection values();

    @Override
    public boolean containsKey(Object var1);

    public boolean containsValue(byte var1);

    @Deprecated
    @Override
    default public boolean containsValue(Object value) {
        return value == null ? false : this.containsValue((Byte)value);
    }

    @Override
    default public byte getOrDefault(Object key, byte defaultValue) {
        byte v = this.getByte(key);
        return v != this.defaultReturnValue() || this.containsKey(key) ? v : defaultValue;
    }

    @Override
    default public byte putIfAbsent(K key, byte value) {
        byte drv;
        byte v = this.getByte(key);
        if (v != (drv = this.defaultReturnValue()) || this.containsKey(key)) {
            return v;
        }
        this.put(key, value);
        return drv;
    }

    default public boolean remove(Object key, byte value) {
        byte curValue = this.getByte(key);
        if (curValue != value || curValue == this.defaultReturnValue() && !this.containsKey(key)) {
            return false;
        }
        this.removeByte(key);
        return true;
    }

    @Override
    default public boolean replace(K key, byte oldValue, byte newValue) {
        byte curValue = this.getByte(key);
        if (curValue != oldValue || curValue == this.defaultReturnValue() && !this.containsKey(key)) {
            return false;
        }
        this.put(key, newValue);
        return true;
    }

    @Override
    default public byte replace(K key, byte value) {
        return this.containsKey(key) ? this.put(key, value) : this.defaultReturnValue();
    }

    default public byte computeByteIfAbsent(K key, ToIntFunction<? super K> mappingFunction) {
        Objects.requireNonNull(mappingFunction);
        byte v = this.getByte(key);
        if (v != this.defaultReturnValue() || this.containsKey(key)) {
            return v;
        }
        byte newValue = SafeMath.safeIntToByte(mappingFunction.applyAsInt(key));
        this.put(key, newValue);
        return newValue;
    }

    default public byte computeByteIfAbsentPartial(K key, Object2ByteFunction<? super K> mappingFunction) {
        Objects.requireNonNull(mappingFunction);
        byte v = this.getByte(key);
        byte drv = this.defaultReturnValue();
        if (v != drv || this.containsKey(key)) {
            return v;
        }
        if (!mappingFunction.containsKey(key)) {
            return drv;
        }
        byte newValue = mappingFunction.getByte(key);
        this.put(key, newValue);
        return newValue;
    }

    default public byte computeByteIfPresent(K key, BiFunction<? super K, ? super Byte, ? extends Byte> remappingFunction) {
        Objects.requireNonNull(remappingFunction);
        byte oldValue = this.getByte(key);
        byte drv = this.defaultReturnValue();
        if (oldValue == drv && !this.containsKey(key)) {
            return drv;
        }
        Byte newValue = remappingFunction.apply(key, (Byte)oldValue);
        if (newValue == null) {
            this.removeByte(key);
            return drv;
        }
        byte newVal = newValue;
        this.put(key, newVal);
        return newVal;
    }

    default public byte computeByte(K key, BiFunction<? super K, ? super Byte, ? extends Byte> remappingFunction) {
        Objects.requireNonNull(remappingFunction);
        byte oldValue = this.getByte(key);
        byte drv = this.defaultReturnValue();
        boolean contained = oldValue != drv || this.containsKey(key);
        Byte newValue = remappingFunction.apply(key, contained ? Byte.valueOf(oldValue) : null);
        if (newValue == null) {
            if (contained) {
                this.removeByte(key);
            }
            return drv;
        }
        byte newVal = newValue;
        this.put(key, newVal);
        return newVal;
    }

    default public byte mergeByte(K key, byte value, BiFunction<? super Byte, ? super Byte, ? extends Byte> remappingFunction) {
        byte newValue;
        Objects.requireNonNull(remappingFunction);
        byte oldValue = this.getByte(key);
        byte drv = this.defaultReturnValue();
        if (oldValue != drv || this.containsKey(key)) {
            Byte mergedValue = remappingFunction.apply((Byte)oldValue, (Byte)value);
            if (mergedValue == null) {
                this.removeByte(key);
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
    default public Byte putIfAbsent(K key, Byte value) {
        return Map.super.putIfAbsent(key, value);
    }

    @Deprecated
    @Override
    default public boolean remove(Object key, Object value) {
        return Map.super.remove(key, value);
    }

    @Deprecated
    @Override
    default public boolean replace(K key, Byte oldValue, Byte newValue) {
        return Map.super.replace(key, oldValue, newValue);
    }

    @Deprecated
    @Override
    default public Byte replace(K key, Byte value) {
        return Map.super.replace(key, value);
    }

    @Deprecated
    @Override
    default public Byte merge(K key, Byte value, BiFunction<? super Byte, ? super Byte, ? extends Byte> remappingFunction) {
        return Map.super.merge(key, value, remappingFunction);
    }

    public static interface Entry<K>
    extends Map.Entry<K, Byte> {
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

    public static interface FastEntrySet<K>
    extends ObjectSet<Entry<K>> {
        public ObjectIterator<Entry<K>> fastIterator();

        default public void fastForEach(Consumer<? super Entry<K>> consumer) {
            this.forEach(consumer);
        }
    }

}

