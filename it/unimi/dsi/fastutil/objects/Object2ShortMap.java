/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.SafeMath;
import it.unimi.dsi.fastutil.objects.Object2ShortFunction;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import it.unimi.dsi.fastutil.shorts.ShortCollection;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.ToIntFunction;

public interface Object2ShortMap<K>
extends Object2ShortFunction<K>,
Map<K, Short> {
    @Override
    public int size();

    @Override
    default public void clear() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void defaultReturnValue(short var1);

    @Override
    public short defaultReturnValue();

    public ObjectSet<Entry<K>> object2ShortEntrySet();

    @Deprecated
    @Override
    default public ObjectSet<Map.Entry<K, Short>> entrySet() {
        return this.object2ShortEntrySet();
    }

    @Deprecated
    @Override
    default public Short put(K key, Short value) {
        return Object2ShortFunction.super.put(key, value);
    }

    @Deprecated
    @Override
    default public Short get(Object key) {
        return Object2ShortFunction.super.get(key);
    }

    @Deprecated
    @Override
    default public Short remove(Object key) {
        return Object2ShortFunction.super.remove(key);
    }

    @Override
    public ObjectSet<K> keySet();

    public ShortCollection values();

    @Override
    public boolean containsKey(Object var1);

    public boolean containsValue(short var1);

    @Deprecated
    @Override
    default public boolean containsValue(Object value) {
        return value == null ? false : this.containsValue((Short)value);
    }

    @Override
    default public short getOrDefault(Object key, short defaultValue) {
        short v = this.getShort(key);
        return v != this.defaultReturnValue() || this.containsKey(key) ? v : defaultValue;
    }

    @Override
    default public short putIfAbsent(K key, short value) {
        short drv;
        short v = this.getShort(key);
        if (v != (drv = this.defaultReturnValue()) || this.containsKey(key)) {
            return v;
        }
        this.put(key, value);
        return drv;
    }

    default public boolean remove(Object key, short value) {
        short curValue = this.getShort(key);
        if (curValue != value || curValue == this.defaultReturnValue() && !this.containsKey(key)) {
            return false;
        }
        this.removeShort(key);
        return true;
    }

    @Override
    default public boolean replace(K key, short oldValue, short newValue) {
        short curValue = this.getShort(key);
        if (curValue != oldValue || curValue == this.defaultReturnValue() && !this.containsKey(key)) {
            return false;
        }
        this.put(key, newValue);
        return true;
    }

    @Override
    default public short replace(K key, short value) {
        return this.containsKey(key) ? this.put(key, value) : this.defaultReturnValue();
    }

    default public short computeShortIfAbsent(K key, ToIntFunction<? super K> mappingFunction) {
        Objects.requireNonNull(mappingFunction);
        short v = this.getShort(key);
        if (v != this.defaultReturnValue() || this.containsKey(key)) {
            return v;
        }
        short newValue = SafeMath.safeIntToShort(mappingFunction.applyAsInt(key));
        this.put(key, newValue);
        return newValue;
    }

    default public short computeShortIfAbsentPartial(K key, Object2ShortFunction<? super K> mappingFunction) {
        Objects.requireNonNull(mappingFunction);
        short v = this.getShort(key);
        short drv = this.defaultReturnValue();
        if (v != drv || this.containsKey(key)) {
            return v;
        }
        if (!mappingFunction.containsKey(key)) {
            return drv;
        }
        short newValue = mappingFunction.getShort(key);
        this.put(key, newValue);
        return newValue;
    }

    default public short computeShortIfPresent(K key, BiFunction<? super K, ? super Short, ? extends Short> remappingFunction) {
        Objects.requireNonNull(remappingFunction);
        short oldValue = this.getShort(key);
        short drv = this.defaultReturnValue();
        if (oldValue == drv && !this.containsKey(key)) {
            return drv;
        }
        Short newValue = remappingFunction.apply(key, (Short)oldValue);
        if (newValue == null) {
            this.removeShort(key);
            return drv;
        }
        short newVal = newValue;
        this.put(key, newVal);
        return newVal;
    }

    default public short computeShort(K key, BiFunction<? super K, ? super Short, ? extends Short> remappingFunction) {
        Objects.requireNonNull(remappingFunction);
        short oldValue = this.getShort(key);
        short drv = this.defaultReturnValue();
        boolean contained = oldValue != drv || this.containsKey(key);
        Short newValue = remappingFunction.apply(key, contained ? Short.valueOf(oldValue) : null);
        if (newValue == null) {
            if (contained) {
                this.removeShort(key);
            }
            return drv;
        }
        short newVal = newValue;
        this.put(key, newVal);
        return newVal;
    }

    default public short mergeShort(K key, short value, BiFunction<? super Short, ? super Short, ? extends Short> remappingFunction) {
        short newValue;
        Objects.requireNonNull(remappingFunction);
        short oldValue = this.getShort(key);
        short drv = this.defaultReturnValue();
        if (oldValue != drv || this.containsKey(key)) {
            Short mergedValue = remappingFunction.apply((Short)oldValue, (Short)value);
            if (mergedValue == null) {
                this.removeShort(key);
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
    default public Short getOrDefault(Object key, Short defaultValue) {
        return Map.super.getOrDefault(key, defaultValue);
    }

    @Deprecated
    @Override
    default public Short putIfAbsent(K key, Short value) {
        return Map.super.putIfAbsent(key, value);
    }

    @Deprecated
    @Override
    default public boolean remove(Object key, Object value) {
        return Map.super.remove(key, value);
    }

    @Deprecated
    @Override
    default public boolean replace(K key, Short oldValue, Short newValue) {
        return Map.super.replace(key, oldValue, newValue);
    }

    @Deprecated
    @Override
    default public Short replace(K key, Short value) {
        return Map.super.replace(key, value);
    }

    @Deprecated
    @Override
    default public Short merge(K key, Short value, BiFunction<? super Short, ? super Short, ? extends Short> remappingFunction) {
        return Map.super.merge(key, value, remappingFunction);
    }

    public static interface Entry<K>
    extends Map.Entry<K, Short> {
        public short getShortValue();

        @Override
        public short setValue(short var1);

        @Deprecated
        @Override
        public Short getValue();

        @Deprecated
        @Override
        public Short setValue(Short var1);
    }

    public static interface FastEntrySet<K>
    extends ObjectSet<Entry<K>> {
        public ObjectIterator<Entry<K>> fastIterator();

        default public void fastForEach(Consumer<? super Entry<K>> consumer) {
            this.forEach(consumer);
        }
    }

}

