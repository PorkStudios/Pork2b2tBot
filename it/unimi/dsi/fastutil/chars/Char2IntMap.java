/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.chars.Char2IntFunction;
import it.unimi.dsi.fastutil.chars.CharSet;
import it.unimi.dsi.fastutil.ints.IntCollection;
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

public interface Char2IntMap
extends Char2IntFunction,
Map<Character, Integer> {
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

    public ObjectSet<Entry> char2IntEntrySet();

    @Deprecated
    @Override
    default public ObjectSet<Map.Entry<Character, Integer>> entrySet() {
        return this.char2IntEntrySet();
    }

    @Deprecated
    @Override
    default public Integer put(Character key, Integer value) {
        return Char2IntFunction.super.put(key, value);
    }

    @Deprecated
    @Override
    default public Integer get(Object key) {
        return Char2IntFunction.super.get(key);
    }

    @Deprecated
    @Override
    default public Integer remove(Object key) {
        return Char2IntFunction.super.remove(key);
    }

    public CharSet keySet();

    public IntCollection values();

    @Override
    public boolean containsKey(char var1);

    @Deprecated
    @Override
    default public boolean containsKey(Object key) {
        return Char2IntFunction.super.containsKey(key);
    }

    public boolean containsValue(int var1);

    @Deprecated
    @Override
    default public boolean containsValue(Object value) {
        return value == null ? false : this.containsValue((Integer)value);
    }

    default public int getOrDefault(char key, int defaultValue) {
        int v = this.get(key);
        return v != this.defaultReturnValue() || this.containsKey(key) ? v : defaultValue;
    }

    @Override
    default public int putIfAbsent(char key, int value) {
        int drv;
        int v = this.get(key);
        if (v != (drv = this.defaultReturnValue()) || this.containsKey(key)) {
            return v;
        }
        this.put(key, value);
        return drv;
    }

    default public boolean remove(char key, int value) {
        int curValue = this.get(key);
        if (curValue != value || curValue == this.defaultReturnValue() && !this.containsKey(key)) {
            return false;
        }
        this.remove(key);
        return true;
    }

    @Override
    default public boolean replace(char key, int oldValue, int newValue) {
        int curValue = this.get(key);
        if (curValue != oldValue || curValue == this.defaultReturnValue() && !this.containsKey(key)) {
            return false;
        }
        this.put(key, newValue);
        return true;
    }

    @Override
    default public int replace(char key, int value) {
        return this.containsKey(key) ? this.put(key, value) : this.defaultReturnValue();
    }

    default public int computeIfAbsent(char key, IntUnaryOperator mappingFunction) {
        Objects.requireNonNull(mappingFunction);
        int v = this.get(key);
        if (v != this.defaultReturnValue() || this.containsKey(key)) {
            return v;
        }
        int newValue = mappingFunction.applyAsInt(key);
        this.put(key, newValue);
        return newValue;
    }

    default public int computeIfAbsentNullable(char key, IntFunction<? extends Integer> mappingFunction) {
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

    default public int computeIfAbsentPartial(char key, Char2IntFunction mappingFunction) {
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
    default public int computeIfPresent(char key, BiFunction<? super Character, ? super Integer, ? extends Integer> remappingFunction) {
        Objects.requireNonNull(remappingFunction);
        int oldValue = this.get(key);
        int drv = this.defaultReturnValue();
        if (oldValue == drv && !this.containsKey(key)) {
            return drv;
        }
        Integer newValue = remappingFunction.apply(Character.valueOf(key), (Integer)oldValue);
        if (newValue == null) {
            this.remove(key);
            return drv;
        }
        int newVal = newValue;
        this.put(key, newVal);
        return newVal;
    }

    @Override
    default public int compute(char key, BiFunction<? super Character, ? super Integer, ? extends Integer> remappingFunction) {
        Objects.requireNonNull(remappingFunction);
        int oldValue = this.get(key);
        int drv = this.defaultReturnValue();
        boolean contained = oldValue != drv || this.containsKey(key);
        Integer newValue = remappingFunction.apply(Character.valueOf(key), contained ? Integer.valueOf(oldValue) : null);
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
    default public int merge(char key, int value, BiFunction<? super Integer, ? super Integer, ? extends Integer> remappingFunction) {
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
    default public Integer putIfAbsent(Character key, Integer value) {
        return Map.super.putIfAbsent(key, value);
    }

    @Deprecated
    @Override
    default public boolean remove(Object key, Object value) {
        return Map.super.remove(key, value);
    }

    @Deprecated
    @Override
    default public boolean replace(Character key, Integer oldValue, Integer newValue) {
        return Map.super.replace(key, oldValue, newValue);
    }

    @Deprecated
    @Override
    default public Integer replace(Character key, Integer value) {
        return Map.super.replace(key, value);
    }

    @Deprecated
    @Override
    default public Integer computeIfAbsent(Character key, Function<? super Character, ? extends Integer> mappingFunction) {
        return Map.super.computeIfAbsent(key, mappingFunction);
    }

    @Deprecated
    @Override
    default public Integer computeIfPresent(Character key, BiFunction<? super Character, ? super Integer, ? extends Integer> remappingFunction) {
        return Map.super.computeIfPresent(key, remappingFunction);
    }

    @Deprecated
    @Override
    default public Integer compute(Character key, BiFunction<? super Character, ? super Integer, ? extends Integer> remappingFunction) {
        return Map.super.compute(key, remappingFunction);
    }

    @Deprecated
    @Override
    default public Integer merge(Character key, Integer value, BiFunction<? super Integer, ? super Integer, ? extends Integer> remappingFunction) {
        return Map.super.merge(key, value, remappingFunction);
    }

    public static interface Entry
    extends Map.Entry<Character, Integer> {
        public char getCharKey();

        @Deprecated
        @Override
        public Character getKey();

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

