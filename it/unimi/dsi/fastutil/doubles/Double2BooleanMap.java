/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.doubles;

import it.unimi.dsi.fastutil.booleans.BooleanCollection;
import it.unimi.dsi.fastutil.doubles.Double2BooleanFunction;
import it.unimi.dsi.fastutil.doubles.DoubleSet;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.DoubleFunction;
import java.util.function.DoublePredicate;
import java.util.function.Function;

public interface Double2BooleanMap
extends Double2BooleanFunction,
Map<Double, Boolean> {
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

    public ObjectSet<Entry> double2BooleanEntrySet();

    @Deprecated
    @Override
    default public ObjectSet<Map.Entry<Double, Boolean>> entrySet() {
        return this.double2BooleanEntrySet();
    }

    @Deprecated
    @Override
    default public Boolean put(Double key, Boolean value) {
        return Double2BooleanFunction.super.put(key, value);
    }

    @Deprecated
    @Override
    default public Boolean get(Object key) {
        return Double2BooleanFunction.super.get(key);
    }

    @Deprecated
    @Override
    default public Boolean remove(Object key) {
        return Double2BooleanFunction.super.remove(key);
    }

    public DoubleSet keySet();

    public BooleanCollection values();

    @Override
    public boolean containsKey(double var1);

    @Deprecated
    @Override
    default public boolean containsKey(Object key) {
        return Double2BooleanFunction.super.containsKey(key);
    }

    public boolean containsValue(boolean var1);

    @Deprecated
    @Override
    default public boolean containsValue(Object value) {
        return value == null ? false : this.containsValue((Boolean)value);
    }

    default public boolean getOrDefault(double key, boolean defaultValue) {
        boolean v = this.get(key);
        return v != this.defaultReturnValue() || this.containsKey(key) ? v : defaultValue;
    }

    @Override
    default public boolean putIfAbsent(double key, boolean value) {
        boolean drv;
        boolean v = this.get(key);
        if (v != (drv = this.defaultReturnValue()) || this.containsKey(key)) {
            return v;
        }
        this.put(key, value);
        return drv;
    }

    default public boolean remove(double key, boolean value) {
        boolean curValue = this.get(key);
        if (curValue != value || curValue == this.defaultReturnValue() && !this.containsKey(key)) {
            return false;
        }
        this.remove(key);
        return true;
    }

    @Override
    default public boolean replace(double key, boolean oldValue, boolean newValue) {
        boolean curValue = this.get(key);
        if (curValue != oldValue || curValue == this.defaultReturnValue() && !this.containsKey(key)) {
            return false;
        }
        this.put(key, newValue);
        return true;
    }

    @Override
    default public boolean replace(double key, boolean value) {
        return this.containsKey(key) ? this.put(key, value) : this.defaultReturnValue();
    }

    default public boolean computeIfAbsent(double key, DoublePredicate mappingFunction) {
        Objects.requireNonNull(mappingFunction);
        boolean v = this.get(key);
        if (v != this.defaultReturnValue() || this.containsKey(key)) {
            return v;
        }
        boolean newValue = mappingFunction.test(key);
        this.put(key, newValue);
        return newValue;
    }

    default public boolean computeIfAbsentNullable(double key, DoubleFunction<? extends Boolean> mappingFunction) {
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

    default public boolean computeIfAbsentPartial(double key, Double2BooleanFunction mappingFunction) {
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
    default public boolean computeIfPresent(double key, BiFunction<? super Double, ? super Boolean, ? extends Boolean> remappingFunction) {
        Objects.requireNonNull(remappingFunction);
        boolean oldValue = this.get(key);
        boolean drv = this.defaultReturnValue();
        if (oldValue == drv && !this.containsKey(key)) {
            return drv;
        }
        Boolean newValue = remappingFunction.apply((Double)key, (Boolean)oldValue);
        if (newValue == null) {
            this.remove(key);
            return drv;
        }
        boolean newVal = newValue;
        this.put(key, newVal);
        return newVal;
    }

    @Override
    default public boolean compute(double key, BiFunction<? super Double, ? super Boolean, ? extends Boolean> remappingFunction) {
        Objects.requireNonNull(remappingFunction);
        boolean oldValue = this.get(key);
        boolean drv = this.defaultReturnValue();
        boolean contained = oldValue != drv || this.containsKey(key);
        Boolean newValue = remappingFunction.apply((Double)key, contained ? Boolean.valueOf(oldValue) : null);
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
    default public boolean merge(double key, boolean value, BiFunction<? super Boolean, ? super Boolean, ? extends Boolean> remappingFunction) {
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
    default public Boolean putIfAbsent(Double key, Boolean value) {
        return Map.super.putIfAbsent(key, value);
    }

    @Deprecated
    @Override
    default public boolean remove(Object key, Object value) {
        return Map.super.remove(key, value);
    }

    @Deprecated
    @Override
    default public boolean replace(Double key, Boolean oldValue, Boolean newValue) {
        return Map.super.replace(key, oldValue, newValue);
    }

    @Deprecated
    @Override
    default public Boolean replace(Double key, Boolean value) {
        return Map.super.replace(key, value);
    }

    @Deprecated
    @Override
    default public Boolean computeIfAbsent(Double key, Function<? super Double, ? extends Boolean> mappingFunction) {
        return Map.super.computeIfAbsent(key, mappingFunction);
    }

    @Deprecated
    @Override
    default public Boolean computeIfPresent(Double key, BiFunction<? super Double, ? super Boolean, ? extends Boolean> remappingFunction) {
        return Map.super.computeIfPresent(key, remappingFunction);
    }

    @Deprecated
    @Override
    default public Boolean compute(Double key, BiFunction<? super Double, ? super Boolean, ? extends Boolean> remappingFunction) {
        return Map.super.compute(key, remappingFunction);
    }

    @Deprecated
    @Override
    default public Boolean merge(Double key, Boolean value, BiFunction<? super Boolean, ? super Boolean, ? extends Boolean> remappingFunction) {
        return Map.super.merge(key, value, remappingFunction);
    }

    public static interface Entry
    extends Map.Entry<Double, Boolean> {
        public double getDoubleKey();

        @Deprecated
        @Override
        public Double getKey();

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

