/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.ints;

import it.unimi.dsi.fastutil.Function;
import java.util.function.IntFunction;

@FunctionalInterface
public interface Int2ObjectFunction<V>
extends Function<Integer, V>,
IntFunction<V> {
    @Override
    default public V apply(int operand) {
        return this.get(operand);
    }

    @Override
    default public V put(int key, V value) {
        throw new UnsupportedOperationException();
    }

    public V get(int var1);

    default public V remove(int key) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    @Override
    default public V put(Integer key, V value) {
        int k = key;
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
        int k = (Integer)key;
        V v = this.get(k);
        return (V)(v != this.defaultReturnValue() || this.containsKey(k) ? v : null);
    }

    @Deprecated
    @Override
    default public V remove(Object key) {
        if (key == null) {
            return null;
        }
        int k = (Integer)key;
        return this.containsKey(k) ? (V)this.remove(k) : null;
    }

    default public boolean containsKey(int key) {
        return true;
    }

    @Deprecated
    @Override
    default public boolean containsKey(Object key) {
        return key == null ? false : this.containsKey((Integer)key);
    }

    default public void defaultReturnValue(V rv) {
        throw new UnsupportedOperationException();
    }

    default public V defaultReturnValue() {
        return null;
    }
}

