/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.ints;

import it.unimi.dsi.fastutil.ints.Int2IntFunction;
import it.unimi.dsi.fastutil.ints.IntCollection;
import it.unimi.dsi.fastutil.ints.IntSet;
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
import java.util.function.IntUnaryOperator;

public interface Int2IntMap
extends Int2IntFunction,
Map<Integer, Integer> {
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

    public ObjectSet<Entry> int2IntEntrySet();

    @Deprecated
    @Override
    default public ObjectSet<Map.Entry<Integer, Integer>> entrySet() {
        return this.int2IntEntrySet();
    }

    @Deprecated
    @Override
    default public Integer put(Integer key, Integer value) {
        return Int2IntFunction.super.put(key, value);
    }

    @Deprecated
    @Override
    default public Integer get(Object key) {
        return Int2IntFunction.super.get(key);
    }

    @Deprecated
    @Override
    default public Integer remove(Object key) {
        return Int2IntFunction.super.remove(key);
    }

    public IntSet keySet();

    public IntCollection values();

    @Override
    public boolean containsKey(int var1);

    @Deprecated
    @Override
    default public boolean containsKey(Object key) {
        return Int2IntFunction.super.containsKey(key);
    }

    public boolean containsValue(int var1);

    @Deprecated
    @Override
    default public boolean containsValue(Object value) {
        return value == null ? false : this.containsValue((Integer)value);
    }

    default public int getOrDefault(int key, int defaultValue) {
        int v = this.get(key);
        return v != this.defaultReturnValue() || this.containsKey(key) ? v : defaultValue;
    }

    @Override
    default public int putIfAbsent(int key, int value) {
        int drv;
        int v = this.get(key);
        if (v != (drv = this.defaultReturnValue()) || this.containsKey(key)) {
            return v;
        }
        this.put(key, value);
        return drv;
    }

    default public boolean remove(int key, int value) {
        int curValue = this.get(key);
        if (curValue != value || curValue == this.defaultReturnValue() && !this.containsKey(key)) {
            return false;
        }
        this.remove(key);
        return true;
    }

    @Override
    default public boolean replace(int key, int oldValue, int newValue) {
        int curValue = this.get(key);
        if (curValue != oldValue || curValue == this.defaultReturnValue() && !this.containsKey(key)) {
            return false;
        }
        this.put(key, newValue);
        return true;
    }

    @Override
    default public int replace(int key, int value) {
        return this.containsKey(key) ? this.put(key, value) : this.defaultReturnValue();
    }

    default public int computeIfAbsent(int key, IntUnaryOperator mappingFunction) {
        Objects.requireNonNull(mappingFunction);
        int v = this.get(key);
        if (v != this.defaultReturnValue() || this.containsKey(key)) {
            return v;
        }
        int newValue = mappingFunction.applyAsInt(key);
        this.put(key, newValue);
        return newValue;
    }

    default public int computeIfAbsentNullable(int key, IntFunction<? extends Integer> mappingFunction) {
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

    default public int computeIfAbsentPartial(int key, Int2IntFunction mappingFunction) {
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
    default public int computeIfPresent(int key, BiFunction<? super Integer, ? super Integer, ? extends Integer> remappingFunction) {
        Objects.requireNonNull(remappingFunction);
        int oldValue = this.get(key);
        int drv = this.defaultReturnValue();
        if (oldValue == drv && !this.containsKey(key)) {
            return drv;
        }
        Integer newValue = remappingFunction.apply((Integer)key, (Integer)oldValue);
        if (newValue == null) {
            this.remove(key);
            return drv;
        }
        int newVal = newValue;
        this.put(key, newVal);
        return newVal;
    }

    @Override
    default public int compute(int key, BiFunction<? super Integer, ? super Integer, ? extends Integer> remappingFunction) {
        Objects.requireNonNull(remappingFunction);
        int oldValue = this.get(key);
        int drv = this.defaultReturnValue();
        boolean contained = oldValue != drv || this.containsKey(key);
        Integer newValue = remappingFunction.apply((Integer)key, contained ? Integer.valueOf(oldValue) : null);
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
    default public int merge(int key, int value, BiFunction<? super Integer, ? super Integer, ? extends Integer> remappingFunction) {
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
    default public Integer putIfAbsent(Integer key, Integer value) {
        return Map.super.putIfAbsent(key, value);
    }

    @Deprecated
    @Override
    default public boolean remove(Object key, Object value) {
        return Map.super.remove(key, value);
    }

    @Deprecated
    @Override
    default public boolean replace(Integer key, Integer oldValue, Integer newValue) {
        return Map.super.replace(key, oldValue, newValue);
    }

    @Deprecated
    @Override
    default public Integer replace(Integer key, Integer value) {
        return Map.super.replace(key, value);
    }

    @Deprecated
    @Override
    default public Integer computeIfAbsent(Integer key, Function<? super Integer, ? extends Integer> mappingFunction) {
        return Map.super.computeIfAbsent(key, mappingFunction);
    }

    @Deprecated
    @Override
    default public Integer computeIfPresent(Integer key, BiFunction<? super Integer, ? super Integer, ? extends Integer> remappingFunction) {
        return Map.super.computeIfPresent(key, remappingFunction);
    }

    @Deprecated
    @Override
    default public Integer compute(Integer key, BiFunction<? super Integer, ? super Integer, ? extends Integer> remappingFunction) {
        return Map.super.compute(key, remappingFunction);
    }

    @Deprecated
    @Override
    default public Integer merge(Integer key, Integer value, BiFunction<? super Integer, ? super Integer, ? extends Integer> remappingFunction) {
        return Map.super.merge(key, value, remappingFunction);
    }

    public static interface Entry
    extends Map.Entry<Integer, Integer> {
        public int getIntKey();

        @Deprecated
        @Override
        public Integer getKey();

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

