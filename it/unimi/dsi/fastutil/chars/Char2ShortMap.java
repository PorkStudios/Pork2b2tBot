/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.SafeMath;
import it.unimi.dsi.fastutil.chars.Char2ShortFunction;
import it.unimi.dsi.fastutil.chars.CharSet;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import it.unimi.dsi.fastutil.shorts.ShortCollection;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.IntUnaryOperator;

public interface Char2ShortMap
extends Char2ShortFunction,
Map<Character, Short> {
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

    public ObjectSet<Entry> char2ShortEntrySet();

    @Deprecated
    @Override
    default public ObjectSet<Map.Entry<Character, Short>> entrySet() {
        return this.char2ShortEntrySet();
    }

    @Deprecated
    @Override
    default public Short put(Character key, Short value) {
        return Char2ShortFunction.super.put(key, value);
    }

    @Deprecated
    @Override
    default public Short get(Object key) {
        return Char2ShortFunction.super.get(key);
    }

    @Deprecated
    @Override
    default public Short remove(Object key) {
        return Char2ShortFunction.super.remove(key);
    }

    public CharSet keySet();

    public ShortCollection values();

    @Override
    public boolean containsKey(char var1);

    @Deprecated
    @Override
    default public boolean containsKey(Object key) {
        return Char2ShortFunction.super.containsKey(key);
    }

    public boolean containsValue(short var1);

    @Deprecated
    @Override
    default public boolean containsValue(Object value) {
        return value == null ? false : this.containsValue((Short)value);
    }

    default public short getOrDefault(char key, short defaultValue) {
        short v = this.get(key);
        return v != this.defaultReturnValue() || this.containsKey(key) ? v : defaultValue;
    }

    @Override
    default public short putIfAbsent(char key, short value) {
        short drv;
        short v = this.get(key);
        if (v != (drv = this.defaultReturnValue()) || this.containsKey(key)) {
            return v;
        }
        this.put(key, value);
        return drv;
    }

    default public boolean remove(char key, short value) {
        short curValue = this.get(key);
        if (curValue != value || curValue == this.defaultReturnValue() && !this.containsKey(key)) {
            return false;
        }
        this.remove(key);
        return true;
    }

    @Override
    default public boolean replace(char key, short oldValue, short newValue) {
        short curValue = this.get(key);
        if (curValue != oldValue || curValue == this.defaultReturnValue() && !this.containsKey(key)) {
            return false;
        }
        this.put(key, newValue);
        return true;
    }

    @Override
    default public short replace(char key, short value) {
        return this.containsKey(key) ? this.put(key, value) : this.defaultReturnValue();
    }

    default public short computeIfAbsent(char key, IntUnaryOperator mappingFunction) {
        Objects.requireNonNull(mappingFunction);
        short v = this.get(key);
        if (v != this.defaultReturnValue() || this.containsKey(key)) {
            return v;
        }
        short newValue = SafeMath.safeIntToShort(mappingFunction.applyAsInt(key));
        this.put(key, newValue);
        return newValue;
    }

    default public short computeIfAbsentNullable(char key, IntFunction<? extends Short> mappingFunction) {
        Objects.requireNonNull(mappingFunction);
        short v = this.get(key);
        short drv = this.defaultReturnValue();
        if (v != drv || this.containsKey(key)) {
            return v;
        }
        Short mappedValue = mappingFunction.apply(key);
        if (mappedValue == null) {
            return drv;
        }
        short newValue = mappedValue;
        this.put(key, newValue);
        return newValue;
    }

    default public short computeIfAbsentPartial(char key, Char2ShortFunction mappingFunction) {
        Objects.requireNonNull(mappingFunction);
        short v = this.get(key);
        short drv = this.defaultReturnValue();
        if (v != drv || this.containsKey(key)) {
            return v;
        }
        if (!mappingFunction.containsKey(key)) {
            return drv;
        }
        short newValue = mappingFunction.get(key);
        this.put(key, newValue);
        return newValue;
    }

    @Override
    default public short computeIfPresent(char key, BiFunction<? super Character, ? super Short, ? extends Short> remappingFunction) {
        Objects.requireNonNull(remappingFunction);
        short oldValue = this.get(key);
        short drv = this.defaultReturnValue();
        if (oldValue == drv && !this.containsKey(key)) {
            return drv;
        }
        Short newValue = remappingFunction.apply(Character.valueOf(key), (Short)oldValue);
        if (newValue == null) {
            this.remove(key);
            return drv;
        }
        short newVal = newValue;
        this.put(key, newVal);
        return newVal;
    }

    @Override
    default public short compute(char key, BiFunction<? super Character, ? super Short, ? extends Short> remappingFunction) {
        Objects.requireNonNull(remappingFunction);
        short oldValue = this.get(key);
        short drv = this.defaultReturnValue();
        boolean contained = oldValue != drv || this.containsKey(key);
        Short newValue = remappingFunction.apply(Character.valueOf(key), contained ? Short.valueOf(oldValue) : null);
        if (newValue == null) {
            if (contained) {
                this.remove(key);
            }
            return drv;
        }
        short newVal = newValue;
        this.put(key, newVal);
        return newVal;
    }

    @Override
    default public short merge(char key, short value, BiFunction<? super Short, ? super Short, ? extends Short> remappingFunction) {
        short newValue;
        Objects.requireNonNull(remappingFunction);
        short oldValue = this.get(key);
        short drv = this.defaultReturnValue();
        if (oldValue != drv || this.containsKey(key)) {
            Short mergedValue = remappingFunction.apply((Short)oldValue, (Short)value);
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
    default public Short getOrDefault(Object key, Short defaultValue) {
        return Map.super.getOrDefault(key, defaultValue);
    }

    @Deprecated
    @Override
    default public Short putIfAbsent(Character key, Short value) {
        return Map.super.putIfAbsent(key, value);
    }

    @Deprecated
    @Override
    default public boolean remove(Object key, Object value) {
        return Map.super.remove(key, value);
    }

    @Deprecated
    @Override
    default public boolean replace(Character key, Short oldValue, Short newValue) {
        return Map.super.replace(key, oldValue, newValue);
    }

    @Deprecated
    @Override
    default public Short replace(Character key, Short value) {
        return Map.super.replace(key, value);
    }

    @Deprecated
    @Override
    default public Short computeIfAbsent(Character key, Function<? super Character, ? extends Short> mappingFunction) {
        return Map.super.computeIfAbsent(key, mappingFunction);
    }

    @Deprecated
    @Override
    default public Short computeIfPresent(Character key, BiFunction<? super Character, ? super Short, ? extends Short> remappingFunction) {
        return Map.super.computeIfPresent(key, remappingFunction);
    }

    @Deprecated
    @Override
    default public Short compute(Character key, BiFunction<? super Character, ? super Short, ? extends Short> remappingFunction) {
        return Map.super.compute(key, remappingFunction);
    }

    @Deprecated
    @Override
    default public Short merge(Character key, Short value, BiFunction<? super Short, ? super Short, ? extends Short> remappingFunction) {
        return Map.super.merge(key, value, remappingFunction);
    }

    public static interface Entry
    extends Map.Entry<Character, Short> {
        public char getCharKey();

        @Deprecated
        @Override
        public Character getKey();

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

    public static interface FastEntrySet
    extends ObjectSet<Entry> {
        public ObjectIterator<Entry> fastIterator();

        default public void fastForEach(Consumer<? super Entry> consumer) {
            this.forEach(consumer);
        }
    }

}

