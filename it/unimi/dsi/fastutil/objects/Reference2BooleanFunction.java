/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.Function;
import java.util.function.Predicate;

@FunctionalInterface
public interface Reference2BooleanFunction<K>
extends Function<K, Boolean>,
Predicate<K> {
    @Override
    default public boolean test(K operand) {
        return this.getBoolean(operand);
    }

    @Override
    default public boolean put(K key, boolean value) {
        throw new UnsupportedOperationException();
    }

    public boolean getBoolean(Object var1);

    default public boolean removeBoolean(Object key) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    @Override
    default public Boolean put(K key, Boolean value) {
        K k = key;
        boolean containsKey = this.containsKey(k);
        boolean v = this.put(k, (boolean)value);
        return containsKey ? Boolean.valueOf(v) : null;
    }

    @Deprecated
    @Override
    default public Boolean get(Object key) {
        Object k = key;
        boolean v = this.getBoolean(k);
        return v != this.defaultReturnValue() || this.containsKey(k) ? Boolean.valueOf(v) : null;
    }

    @Deprecated
    @Override
    default public Boolean remove(Object key) {
        Object k = key;
        return this.containsKey(k) ? Boolean.valueOf(this.removeBoolean(k)) : null;
    }

    default public void defaultReturnValue(boolean rv) {
        throw new UnsupportedOperationException();
    }

    default public boolean defaultReturnValue() {
        return false;
    }
}

