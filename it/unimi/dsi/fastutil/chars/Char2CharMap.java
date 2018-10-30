/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.SafeMath;
import it.unimi.dsi.fastutil.chars.Char2CharFunction;
import it.unimi.dsi.fastutil.chars.CharCollection;
import it.unimi.dsi.fastutil.chars.CharSet;
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

public interface Char2CharMap
extends Char2CharFunction,
Map<Character, Character> {
    @Override
    public int size();

    @Override
    default public void clear() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void defaultReturnValue(char var1);

    @Override
    public char defaultReturnValue();

    public ObjectSet<Entry> char2CharEntrySet();

    @Deprecated
    @Override
    default public ObjectSet<Map.Entry<Character, Character>> entrySet() {
        return this.char2CharEntrySet();
    }

    @Deprecated
    @Override
    default public Character put(Character key, Character value) {
        return Char2CharFunction.super.put(key, value);
    }

    @Deprecated
    @Override
    default public Character get(Object key) {
        return Char2CharFunction.super.get(key);
    }

    @Deprecated
    @Override
    default public Character remove(Object key) {
        return Char2CharFunction.super.remove(key);
    }

    public CharSet keySet();

    public CharCollection values();

    @Override
    public boolean containsKey(char var1);

    @Deprecated
    @Override
    default public boolean containsKey(Object key) {
        return Char2CharFunction.super.containsKey(key);
    }

    public boolean containsValue(char var1);

    @Deprecated
    @Override
    default public boolean containsValue(Object value) {
        return value == null ? false : this.containsValue(((Character)value).charValue());
    }

    default public char getOrDefault(char key, char defaultValue) {
        char v = this.get(key);
        return v != this.defaultReturnValue() || this.containsKey(key) ? v : defaultValue;
    }

    @Override
    default public char putIfAbsent(char key, char value) {
        char drv;
        char v = this.get(key);
        if (v != (drv = this.defaultReturnValue()) || this.containsKey(key)) {
            return v;
        }
        this.put(key, value);
        return drv;
    }

    default public boolean remove(char key, char value) {
        char curValue = this.get(key);
        if (curValue != value || curValue == this.defaultReturnValue() && !this.containsKey(key)) {
            return false;
        }
        this.remove(key);
        return true;
    }

    @Override
    default public boolean replace(char key, char oldValue, char newValue) {
        char curValue = this.get(key);
        if (curValue != oldValue || curValue == this.defaultReturnValue() && !this.containsKey(key)) {
            return false;
        }
        this.put(key, newValue);
        return true;
    }

    @Override
    default public char replace(char key, char value) {
        return this.containsKey(key) ? this.put(key, value) : this.defaultReturnValue();
    }

    default public char computeIfAbsent(char key, IntUnaryOperator mappingFunction) {
        Objects.requireNonNull(mappingFunction);
        char v = this.get(key);
        if (v != this.defaultReturnValue() || this.containsKey(key)) {
            return v;
        }
        char newValue = SafeMath.safeIntToChar(mappingFunction.applyAsInt(key));
        this.put(key, newValue);
        return newValue;
    }

    default public char computeIfAbsentNullable(char key, IntFunction<? extends Character> mappingFunction) {
        Objects.requireNonNull(mappingFunction);
        char v = this.get(key);
        char drv = this.defaultReturnValue();
        if (v != drv || this.containsKey(key)) {
            return v;
        }
        Character mappedValue = mappingFunction.apply(key);
        if (mappedValue == null) {
            return drv;
        }
        char newValue = mappedValue.charValue();
        this.put(key, newValue);
        return newValue;
    }

    default public char computeIfAbsentPartial(char key, Char2CharFunction mappingFunction) {
        Objects.requireNonNull(mappingFunction);
        char v = this.get(key);
        char drv = this.defaultReturnValue();
        if (v != drv || this.containsKey(key)) {
            return v;
        }
        if (!mappingFunction.containsKey(key)) {
            return drv;
        }
        char newValue = mappingFunction.get(key);
        this.put(key, newValue);
        return newValue;
    }

    @Override
    default public char computeIfPresent(char key, BiFunction<? super Character, ? super Character, ? extends Character> remappingFunction) {
        Objects.requireNonNull(remappingFunction);
        char oldValue = this.get(key);
        char drv = this.defaultReturnValue();
        if (oldValue == drv && !this.containsKey(key)) {
            return drv;
        }
        Character newValue = remappingFunction.apply(Character.valueOf(key), Character.valueOf(oldValue));
        if (newValue == null) {
            this.remove(key);
            return drv;
        }
        char newVal = newValue.charValue();
        this.put(key, newVal);
        return newVal;
    }

    @Override
    default public char compute(char key, BiFunction<? super Character, ? super Character, ? extends Character> remappingFunction) {
        Objects.requireNonNull(remappingFunction);
        char oldValue = this.get(key);
        char drv = this.defaultReturnValue();
        boolean contained = oldValue != drv || this.containsKey(key);
        Character newValue = remappingFunction.apply(Character.valueOf(key), contained ? Character.valueOf(oldValue) : null);
        if (newValue == null) {
            if (contained) {
                this.remove(key);
            }
            return drv;
        }
        char newVal = newValue.charValue();
        this.put(key, newVal);
        return newVal;
    }

    @Override
    default public char merge(char key, char value, BiFunction<? super Character, ? super Character, ? extends Character> remappingFunction) {
        char newValue;
        Objects.requireNonNull(remappingFunction);
        char oldValue = this.get(key);
        char drv = this.defaultReturnValue();
        if (oldValue != drv || this.containsKey(key)) {
            Character mergedValue = remappingFunction.apply(Character.valueOf(oldValue), Character.valueOf(value));
            if (mergedValue == null) {
                this.remove(key);
                return drv;
            }
            newValue = mergedValue.charValue();
        } else {
            newValue = value;
        }
        this.put(key, newValue);
        return newValue;
    }

    @Deprecated
    @Override
    default public Character getOrDefault(Object key, Character defaultValue) {
        return Map.super.getOrDefault(key, defaultValue);
    }

    @Deprecated
    @Override
    default public Character putIfAbsent(Character key, Character value) {
        return Map.super.putIfAbsent(key, value);
    }

    @Deprecated
    @Override
    default public boolean remove(Object key, Object value) {
        return Map.super.remove(key, value);
    }

    @Deprecated
    @Override
    default public boolean replace(Character key, Character oldValue, Character newValue) {
        return Map.super.replace(key, oldValue, newValue);
    }

    @Deprecated
    @Override
    default public Character replace(Character key, Character value) {
        return Map.super.replace(key, value);
    }

    @Deprecated
    @Override
    default public Character computeIfAbsent(Character key, Function<? super Character, ? extends Character> mappingFunction) {
        return Map.super.computeIfAbsent(key, mappingFunction);
    }

    @Deprecated
    @Override
    default public Character computeIfPresent(Character key, BiFunction<? super Character, ? super Character, ? extends Character> remappingFunction) {
        return Map.super.computeIfPresent(key, remappingFunction);
    }

    @Deprecated
    @Override
    default public Character compute(Character key, BiFunction<? super Character, ? super Character, ? extends Character> remappingFunction) {
        return Map.super.compute(key, remappingFunction);
    }

    @Deprecated
    @Override
    default public Character merge(Character key, Character value, BiFunction<? super Character, ? super Character, ? extends Character> remappingFunction) {
        return Map.super.merge(key, value, remappingFunction);
    }

    public static interface Entry
    extends Map.Entry<Character, Character> {
        public char getCharKey();

        @Deprecated
        @Override
        public Character getKey();

        public char getCharValue();

        @Override
        public char setValue(char var1);

        @Deprecated
        @Override
        public Character getValue();

        @Deprecated
        @Override
        public Character setValue(Character var1);
    }

    public static interface FastEntrySet
    extends ObjectSet<Entry> {
        public ObjectIterator<Entry> fastIterator();

        default public void fastForEach(Consumer<? super Entry> consumer) {
            this.forEach(consumer);
        }
    }

}

