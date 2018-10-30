/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.Function;
import java.util.function.LongFunction;

@FunctionalInterface
public interface Long2ReferenceFunction<V>
extends Function<Long, V>,
LongFunction<V> {
    @Override
    default public V apply(long operand) {
        return this.get(operand);
    }

    @Override
    default public V put(long key, V value) {
        throw new UnsupportedOperationException();
    }

    public V get(long var1);

    default public V remove(long key) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    @Override
    default public V put(Long key, V value) {
        long k = key;
        boolean containsKey = this.containsKey(k);
        V v = this.put(k, value);
        return (V)(containsKey ? v : null);
    }

    @Deprecated
    @Override
    default public V get(Object key) {
        if (key == null) {
            return null;
        }
        long k = (Long)key;
        V v = this.get(k);
        return (V)(v != this.defaultReturnValue() || this.containsKey(k) ? v : null);
    }

    @Deprecated
    @Override
    default public V remove(Object key) {
        if (key == null) {
            return null;
        }
        long k = (Long)key;
        return this.containsKey(k) ? (V)this.remove(k) : null;
    }

    default public boolean containsKey(long key) {
        return true;
    }

    @Deprecated
    @Override
    default public boolean containsKey(Object key) {
        return key == null ? false : this.containsKey((Long)key);
    }

    default public void defaultReturnValue(V rv) {
        throw new UnsupportedOperationException();
    }

    default public V defaultReturnValue() {
        return null;
    }
}

