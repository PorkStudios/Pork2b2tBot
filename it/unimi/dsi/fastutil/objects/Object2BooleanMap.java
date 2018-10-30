/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.booleans.BooleanCollection;
import it.unimi.dsi.fastutil.objects.Object2BooleanFunction;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Predicate;

public interface Object2BooleanMap<K>
extends Object2BooleanFunction<K>,
Map<K, Boolean> {
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

    public ObjectSet<Entry<K>> object2BooleanEntrySet();

    @Deprecated
    @Override
    default public ObjectSet<Map.Entry<K, Boolean>> entrySet() {
        return this.object2BooleanEntrySet();
    }

    @Deprecated
    @Override
    default public Boolean put(K key, Boolean value) {
        return Object2BooleanFunction.super.put(key, value);
    }

    @Deprecated
    @Override
    default public Boolean get(Object key) {
        return Object2BooleanFunction.super.get(key);
    }

    @Deprecated
    @Override
    default public Boolean remove(Object key) {
        return Object2BooleanFunction.super.remove(key);
    }

    @Override
    public ObjectSet<K> keySet();

    public BooleanCollection values();

    @Override
    public boolean containsKey(Object var1);

    public boolean containsValue(boolean var1);

    @Deprecated
    @Override
    default public boolean containsValue(Object value) {
        return value == null ? false : this.containsValue((Boolean)value);
    }

    @Override
    default public boolean getOrDefault(Object key, boolean defaultValue) {
        boolean v = this.getBoolean(key);
        return v != this.defaultReturnValue() || this.containsKey(key) ? v : defaultValue;
    }

    @Override
    default public boolean putIfAbsent(K key, boolean value) {
        boolean drv;
        boolean v = this.getBoolean(key);
        if (v != (drv = this.defaultReturnValue()) || this.containsKey(key)) {
            return v;
        }
        this.put(key, value);
        return drv;
    }

    default public boolean remove(Object key, boolean value) {
        boolean curValue = this.getBoolean(key);
        if (curValue != value || curValue == this.defaultReturnValue() && !this.containsKey(key)) {
            return false;
        }
        this.removeBoolean(key);
        return true;
    }

    @Override
    default public boolean replace(K key, boolean oldValue, boolean newValue) {
        boolean curValue = this.getBoolean(key);
        if (curValue != oldValue || curValue == this.defaultReturnValue() && !this.containsKey(key)) {
            return false;
        }
        this.put(key, newValue);
        return true;
    }

    @Override
    default public boolean replace(K key, boolean value) {
        return this.containsKey(key) ? this.put(key, value) : this.defaultReturnValue();
    }

    default public boolean computeBooleanIfAbsent(K key, Predicate<? super K> mappingFunction) {
        Objects.requireNonNull(mappingFunction);
        boolean v = this.getBoolean(key);
        if (v != this.defaultReturnValue() || this.containsKey(key)) {
            return v;
        }
        boolean newValue = mappingFunction.test(key);
        this.put(key, newValue);
        return newValue;
    }

    default public boolean computeBooleanIfAbsentPartial(K key, Object2BooleanFunction<? super K> mappingFunction) {
        Objects.requireNonNull(mappingFunction);
        boolean v = this.getBoolean(key);
        boolean drv = this.defaultReturnValue();
        if (v != drv || this.containsKey(key)) {
            return v;
        }
        if (!mappingFunction.containsKey(key)) {
            return drv;
        }
        boolean newValue = mappingFunction.getBoolean(key);
        this.put(key, newValue);
        return newValue;
    }

    default public boolean computeBooleanIfPresent(K key, BiFunction<? super K, ? super Boolean, ? extends Boolean> remappingFunction) {
        Objects.requireNonNull(remappingFunction);
        boolean oldValue = this.getBoolean(key);
        boolean drv = this.defaultReturnValue();
        if (oldValue == drv && !this.containsKey(key)) {
            return drv;
        }
        Boolean newValue = remappingFunction.apply(key, (Boolean)oldValue);
        if (newValue == null) {
            this.removeBoolean(key);
            return drv;
        }
        boolean newVal = newValue;
        this.put(key, newVal);
        return newVal;
    }

    default public boolean computeBoolean(K key, BiFunction<? super K, ? super Boolean, ? extends Boolean> remappingFunction) {
        Objects.requireNonNull(remappingFunction);
        boolean oldValue = this.getBoolean(key);
        boolean drv = this.defaultReturnValue();
        boolean contained = oldValue != drv || this.containsKey(key);
        Boolean newValue = remappingFunction.apply(key, contained ? Boolean.valueOf(oldValue) : null);
        if (newValue == null) {
            if (contained) {
                this.removeBoolean(key);
            }
            return drv;
        }
        boolean newVal = newValue;
        this.put(key, newVal);
        return newVal;
    }

    default public boolean mergeBoolean(K key, boolean value, BiFunction<? super Boolean, ? super Boolean, ? extends Boolean> remappingFunction) {
        boolean newValue;
        Objects.requireNonNull(remappingFunction);
        boolean oldValue = this.getBoolean(key);
        boolean drv = this.defaultReturnValue();
        if (oldValue != drv || this.containsKey(key)) {
            Boolean mergedValue = remappingFunction.apply((Boolean)oldValue, (Boolean)value);
            if (mergedValue == null) {
                this.removeBoolean(key);
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
    default public Boolean putIfAbsent(K key, Boolean value) {
        return Map.super.putIfAbsent(key, value);
    }

    @Deprecated
    @Override
    default public boolean remove(Object key, Object value) {
        return Map.super.remove(key, value);
    }

    @Deprecated
    @Override
    default public boolean replace(K key, Boolean oldValue, Boolean newValue) {
        return Map.super.replace(key, oldValue, newValue);
    }

    @Deprecated
    @Override
    default public Boolean replace(K key, Boolean value) {
        return Map.super.replace(key, value);
    }

    @Deprecated
    @Override
    default public Boolean merge(K key, Boolean value, BiFunction<? super Boolean, ? super Boolean, ? extends Boolean> remappingFunction) {
        return Map.super.merge(key, value, remappingFunction);
    }

    public static interface Entry<K>
    extends Map.Entry<K, Boolean> {
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

    public static interface FastEntrySet<K>
    extends ObjectSet<Entry<K>> {
        public ObjectIterator<Entry<K>> fastIterator();

        default public void fastForEach(Consumer<? super Entry<K>> consumer) {
            this.forEach(consumer);
        }
    }

}

