/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.Function;
import java.util.function.ToIntFunction;

@FunctionalInterface
public interface Object2IntFunction<K>
extends Function<K, Integer>,
ToIntFunction<K> {
    @Override
    default public int applyAsInt(K operand) {
        return this.getInt(operand);
    }

    @Override
    default public int put(K key, int value) {
        throw new UnsupportedOperationException();
    }

    public int getInt(Object var1);

    default public int removeInt(Object key) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    @Override
    default public Integer put(K key, Integer value) {
        K k = key;
        boolean containsKey = this.containsKey(k);
        int v = this.put(k, (int)value);
        return containsKey ? Integer.valueOf(v) : null;
    }

    @Deprecated
    @Override
    default public Integer get(Object key) {
        Object k = key;
        int v = this.getInt(k);
        return v != this.defaultReturnValue() || this.containsKey(k) ? Integer.valueOf(v) : null;
    }

    @Deprecated
    @Override
    default public Integer remove(Object key) {
        Object k = key;
        return this.containsKey(k) ? Integer.valueOf(this.removeInt(k)) : null;
    }

    default public void defaultReturnValue(int rv) {
        throw new UnsupportedOperationException();
    }

    default public int defaultReturnValue() {
        return 0;
    }
}

