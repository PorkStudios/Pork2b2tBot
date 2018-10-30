/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.booleans.BooleanCollection;
import it.unimi.dsi.fastutil.bytes.Byte2BooleanFunction;
import it.unimi.dsi.fastutil.bytes.ByteSet;
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
import java.util.function.IntPredicate;

public interface Byte2BooleanMap
extends Byte2BooleanFunction,
Map<Byte, Boolean> {
    @Override
    public int size();

    @Override
    default public void clear() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void defaultReturnValue(boolean var1);

    @Override
    public boolean defaultReturnValue();

    public ObjectSet<Entry> byte2BooleanEntrySet();

    @Deprecated
    @Override
    default public ObjectSet<Map.Entry<Byte, Boolean>> entrySet() {
        return this.byte2BooleanEntrySet();
    }

    @Deprecated
    @Override
    default public Boolean put(Byte key, Boolean value) {
        return Byte2BooleanFunction.super.put(key, value);
    }

    @Deprecated
    @Override
    default public Boolean get(Object key) {
        return Byte2BooleanFunction.super.get(key);
    }

    @Deprecated
    @Override
    default public Boolean remove(Object key) {
        return Byte2BooleanFunction.super.remove(key);
    }

    public ByteSet keySet();

    public BooleanCollection values();

    @Override
    public boolean containsKey(byte var1);

    @Deprecated
    @Override
    default public boolean containsKey(Object key) {
        return Byte2BooleanFunction.super.containsKey(key);
    }

    public boolean containsValue(boolean var1);

    @Deprecated
    @Override
    default public boolean containsValue(Object value) {
        return value == null ? false : this.containsValue((Boolean)value);
    }

    default public boolean getOrDefault(byte key, boolean defaultValue) {
        boolean v = this.get(key);
        return v != this.defaultReturnValue() || this.containsKey(key) ? v : defaultValue;
    }

    @Override
    default public boolean putIfAbsent(byte key, boolean value) {
        boolean drv;
        boolean v = this.get(key);
        if (v != (drv = this.defaultReturnValue()) || this.containsKey(key)) {
            return v;
        }
        this.put(key, value);
        return drv;
    }

    default public boolean remove(byte key, boolean value) {
        boolean curValue = this.get(key);
        if (curValue != value || curValue == this.defaultReturnValue() && !this.containsKey(key)) {
            return false;
        }
        this.remove(key);
        return true;
    }

    @Override
    default public boolean replace(byte key, boolean oldValue, boolean newValue) {
        boolean curValue = this.get(key);
        if (curValue != oldValue || curValue == this.defaultReturnValue() && !this.containsKey(key)) {
            return false;
        }
        this.put(key, newValue);
        return true;
    }

    @Override
    default public boolean replace(byte key, boolean value) {
        return this.containsKey(key) ? this.put(key, value) : this.defaultReturnValue();
    }

    default public boolean computeIfAbsent(byte key, IntPredicate mappingFunction) {
        Objects.requireNonNull(mappingFunction);
        boolean v = this.get(key);
        if (v != this.defaultReturnValue() || this.containsKey(key)) {
            return v;
        }
        boolean newValue = mappingFunction.test(key);
        this.put(key, newValue);
        return newValue;
    }

    default public boolean computeIfAbsentNullable(byte key, IntFunction<? extends Boolean> mappingFunction) {
        Objects.requireNonNull(mappingFunction);
        boolean v = this.get(key);
        boolean drv = this.defaultReturnValue();
        if (v != drv || this.containsKey(key)) {
            return v;
        }
        Boolean mappedValue = mappingFunction.apply(key);
        if (mappedValue == null) {
            return drv;
        }
        boolean newValue = mappedValue;
        this.put(key, newValue);
        return newValue;
    }

    default public boolean computeIfAbsentPartial(byte key, Byte2BooleanFunction mappingFunction) {
        Objects.requireNonNull(mappingFunction);
        boolean v = this.get(key);
        boolean drv = this.defaultReturnValue();
        if (v != drv || this.containsKey(key)) {
            return v;
        }
        if (!mappingFunction.containsKey(key)) {
            return drv;
        }
        boolean newValue = mappingFunction.get(key);
        this.put(key, newValue);
        return newValue;
    }

    @Override
    default public boolean computeIfPresent(byte key, BiFunction<? super Byte, ? super Boolean, ? extends Boolean> remappingFunction) {
        Objects.requireNonNull(remappingFunction);
        boolean oldValue = this.get(key);
        boolean drv = this.defaultReturnValue();
        if (oldValue == drv && !this.containsKey(key)) {
            return drv;
        }
        Boolean newValue = remappingFunction.apply((Byte)key, (Boolean)oldValue);
        if (newValue == null) {
            this.remove(key);
            return drv;
        }
        boolean newVal = newValue;
        this.put(key, newVal);
        return newVal;
    }

    @Override
    default public boolean compute(byte key, BiFunction<? super Byte, ? super Boolean, ? extends Boolean> remappingFunction) {
        Objects.requireNonNull(remappingFunction);
        boolean oldValue = this.get(key);
        boolean drv = this.defaultReturnValue();
        boolean contained = oldValue != drv || this.containsKey(key);
        Boolean newValue = remappingFunction.apply((Byte)key, contained ? Boolean.valueOf(oldValue) : null);
        if (newValue == null) {
            if (contained) {
                this.remove(key);
            }
            return drv;
        }
        boolean newVal = newValue;
        this.put(key, newVal);
        return newVal;
    }

    @Override
    default public boolean merge(byte key, boolean value, BiFunction<? super Boolean, ? super Boolean, ? extends Boolean> remappingFunction) {
        boolean newValue;
        Objects.requireNonNull(remappingFunction);
        boolean oldValue = this.get(key);
        boolean drv = this.defaultReturnValue();
        if (oldValue != drv || this.containsKey(key)) {
            Boolean mergedValue = remappingFunction.apply((Boolean)oldValue, (Boolean)value);
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
    default public Boolean getOrDefault(Object key, Boolean defaultValue) {
        return Map.super.getOrDefault(key, defaultValue);
    }

    @Deprecated
    @Override
    default public Boolean putIfAbsent(Byte key, Boolean value) {
        return Map.super.putIfAbsent(key, value);
    }

    @Deprecated
    @Override
    default public boolean remove(Object key, Object value) {
        return Map.super.remove(key, value);
    }

    @Deprecated
    @Override
    default public boolean replace(Byte key, Boolean oldValue, Boolean newValue) {
        return Map.super.replace(key, oldValue, newValue);
    }

    @Deprecated
    @Override
    default public Boolean replace(Byte key, Boolean value) {
        return Map.super.replace(key, value);
    }

    @Deprecated
    @Override
    default public Boolean computeIfAbsent(Byte key, Function<? super Byte, ? extends Boolean> mappingFunction) {
        return Map.super.computeIfAbsent(key, mappingFunction);
    }

    @Deprecated
    @Override
    default public Boolean computeIfPresent(Byte key, BiFunction<? super Byte, ? super Boolean, ? extends Boolean> remappingFunction) {
        return Map.super.computeIfPresent(key, remappingFunction);
    }

    @Deprecated
    @Override
    default public Boolean compute(Byte key, BiFunction<? super Byte, ? super Boolean, ? extends Boolean> remappingFunction) {
        return Map.super.compute(key, remappingFunction);
    }

    @Deprecated
    @Override
    default public Boolean merge(Byte key, Boolean value, BiFunction<? super Boolean, ? super Boolean, ? extends Boolean> remappingFunction) {
        return Map.super.merge(key, value, remappingFunction);
    }

    public static interface Entry
    extends Map.Entry<Byte, Boolean> {
        public byte getByteKey();

        @Deprecated
        @Override
        public Byte getKey();

        public boolean getBooleanValue();

        @Override
        public boolean setValue(boolean var1);

        @Deprecated
        @Override
        public Boolean getValue();

        @Deprecated
        @Override
        public Boolean setValue(Boolean var1);
    }

    public static interface FastEntrySet
    extends ObjectSet<Entry> {
        public ObjectIterator<Entry> fastIterator();

        default public void fastForEach(Consumer<? super Entry> consumer) {
            this.forEach(consumer);
        }
    }

}

