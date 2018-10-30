/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.ints;

import it.unimi.dsi.fastutil.ints.Int2LongFunction;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.longs.LongCollection;
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
import java.util.function.IntToLongFunction;

public interface Int2LongMap
extends Int2LongFunction,
Map<Integer, Long> {
    @Override
    public int size();

    @Override
    default public void clear() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void defaultReturnValue(long var1);

    @Override
    public long defaultReturnValue();

    public ObjectSet<Entry> int2LongEntrySet();

    @Deprecated
    @Override
    default public ObjectSet<Map.Entry<Integer, Long>> entrySet() {
        return this.int2LongEntrySet();
    }

    @Deprecated
    @Override
    default public Long put(Integer key, Long value) {
        return Int2LongFunction.super.put(key, value);
    }

    @Deprecated
    @Override
    default public Long get(Object key) {
        return Int2LongFunction.super.get(key);
    }

    @Deprecated
    @Override
    default public Long remove(Object key) {
        return Int2LongFunction.super.remove(key);
    }

    public IntSet keySet();

    public LongCollection values();

    @Override
    public boolean containsKey(int var1);

    @Deprecated
    @Override
    default public boolean containsKey(Object key) {
        return Int2LongFunction.super.containsKey(key);
    }

    public boolean containsValue(long var1);

    @Deprecated
    @Override
    default public boolean containsValue(Object value) {
        return value == null ? false : this.containsValue((Long)value);
    }

    default public long getOrDefault(int key, long defaultValue) {
        long v = this.get(key);
        return v != this.defaultReturnValue() || this.containsKey(key) ? v : defaultValue;
    }

    @Override
    default public long putIfAbsent(int key, long value) {
        long drv;
        long v = this.get(key);
        if (v != (drv = this.defaultReturnValue()) || this.containsKey(key)) {
            return v;
        }
        this.put(key, value);
        return drv;
    }

    default public boolean remove(int key, long value) {
        long curValue = this.get(key);
        if (curValue != value || curValue == this.defaultReturnValue() && !this.containsKey(key)) {
            return false;
        }
        this.remove(key);
        return true;
    }

    @Override
    default public boolean replace(int key, long oldValue, long newValue) {
        long curValue = this.get(key);
        if (curValue != oldValue || curValue == this.defaultReturnValue() && !this.containsKey(key)) {
            return false;
        }
        this.put(key, newValue);
        return true;
    }

    @Override
    default public long replace(int key, long value) {
        return this.containsKey(key) ? this.put(key, value) : this.defaultReturnValue();
    }

    default public long computeIfAbsent(int key, IntToLongFunction mappingFunction) {
        Objects.requireNonNull(mappingFunction);
        long v = this.get(key);
        if (v != this.defaultReturnValue() || this.containsKey(key)) {
            return v;
        }
        long newValue = mappingFunction.applyAsLong(key);
        this.put(key, newValue);
        return newValue;
    }

    default public long computeIfAbsentNullable(int key, IntFunction<? extends Long> mappingFunction) {
        Objects.requireNonNull(mappingFunction);
        long v = this.get(key);
        long drv = this.defaultReturnValue();
        if (v != drv || this.containsKey(key)) {
            return v;
        }
        Long mappedValue = mappingFunction.apply(key);
        if (mappedValue == null) {
            return drv;
        }
        long newValue = mappedValue;
        this.put(key, newValue);
        return newValue;
    }

    default public long computeIfAbsentPartial(int key, Int2LongFunction mappingFunction) {
        Objects.requireNonNull(mappingFunction);
        long v = this.get(key);
        long drv = this.defaultReturnValue();
        if (v != drv || this.containsKey(key)) {
            return v;
        }
        if (!mappingFunction.containsKey(key)) {
            return drv;
        }
        long newValue = mappingFunction.get(key);
        this.put(key, newValue);
        return newValue;
    }

    @Override
    default public long computeIfPresent(int key, BiFunction<? super Integer, ? super Long, ? extends Long> remappingFunction) {
        Objects.requireNonNull(remappingFunction);
        long oldValue = this.get(key);
        long drv = this.defaultReturnValue();
        if (oldValue == drv && !this.containsKey(key)) {
            return drv;
        }
        Long newValue = remappingFunction.apply((Integer)key, (Long)oldValue);
        if (newValue == null) {
            this.remove(key);
            return drv;
        }
        long newVal = newValue;
        this.put(key, newVal);
        return newVal;
    }

    @Override
    default public long compute(int key, BiFunction<? super Integer, ? super Long, ? extends Long> remappingFunction) {
        Objects.requireNonNull(remappingFunction);
        long oldValue = this.get(key);
        long drv = this.defaultReturnValue();
        boolean contained = oldValue != drv || this.containsKey(key);
        Long newValue = remappingFunction.apply((Integer)key, contained ? Long.valueOf(oldValue) : null);
        if (newValue == null) {
            if (contained) {
                this.remove(key);
            }
            return drv;
        }
        long newVal = newValue;
        this.put(key, newVal);
        return newVal;
    }

    @Override
    default public long merge(int key, long value, BiFunction<? super Long, ? super Long, ? extends Long> remappingFunction) {
        long newValue;
        Objects.requireNonNull(remappingFunction);
        long oldValue = this.get(key);
        long drv = this.defaultReturnValue();
        if (oldValue != drv || this.containsKey(key)) {
            Long mergedValue = remappingFunction.apply((Long)oldValue, (Long)value);
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
    default public Long getOrDefault(Object key, Long defaultValue) {
        return Map.super.getOrDefault(key, defaultValue);
    }

    @Deprecated
    @Override
    default public Long putIfAbsent(Integer key, Long value) {
        return Map.super.putIfAbsent(key, value);
    }

    @Deprecated
    @Override
    default public boolean remove(Object key, Object value) {
        return Map.super.remove(key, value);
    }

    @Deprecated
    @Override
    default public boolean replace(Integer key, Long oldValue, Long newValue) {
        return Map.super.replace(key, oldValue, newValue);
    }

    @Deprecated
    @Override
    default public Long replace(Integer key, Long value) {
        return Map.super.replace(key, value);
    }

    @Deprecated
    @Override
    default public Long computeIfAbsent(Integer key, Function<? super Integer, ? extends Long> mappingFunction) {
        return Map.super.computeIfAbsent(key, mappingFunction);
    }

    @Deprecated
    @Override
    default public Long computeIfPresent(Integer key, BiFunction<? super Integer, ? super Long, ? extends Long> remappingFunction) {
        return Map.super.computeIfPresent(key, remappingFunction);
    }

    @Deprecated
    @Override
    default public Long compute(Integer key, BiFunction<? super Integer, ? super Long, ? extends Long> remappingFunction) {
        return Map.super.compute(key, remappingFunction);
    }

    @Deprecated
    @Override
    default public Long merge(Integer key, Long value, BiFunction<? super Long, ? super Long, ? extends Long> remappingFunction) {
        return Map.super.merge(key, value, remappingFunction);
    }

    public static interface Entry
    extends Map.Entry<Integer, Long> {
        public int getIntKey();

        @Deprecated
        @Override
        public Integer getKey();

        public long getLongValue();

        @Override
        public long setValue(long var1);

        @Deprecated
        @Override
        public Long getValue();

        @Deprecated
        @Override
        public Long setValue(Long var1);
    }

    public static interface FastEntrySet
    extends ObjectSet<Entry> {
        public ObjectIterator<Entry> fastIterator();

        default public void fastForEach(Consumer<? super Entry> consumer) {
            this.forEach(consumer);
        }
    }

}

