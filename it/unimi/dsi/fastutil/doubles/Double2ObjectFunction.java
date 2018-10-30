/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.doubles;

import it.unimi.dsi.fastutil.Function;
import java.util.function.DoubleFunction;

@FunctionalInterface
public interface Double2ObjectFunction<V>
extends Function<Double, V>,
DoubleFunction<V> {
    @Override
    default public V apply(double operand) {
        return this.get(operand);
    }

    @Override
    default public V put(double key, V value) {
        throw new UnsupportedOperationException();
    }

    public V get(double var1);

    default public V remove(double key) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    @Override
    default public V put(Double key, V value) {
        double k = key;
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
        double k = (Double)key;
        V v = this.get(k);
        return (V)(v != this.defaultReturnValue() || this.containsKey(k) ? v : null);
    }

    @Deprecated
    @Override
    default public V remove(Object key) {
        if (key == null) {
            return null;
        }
        double k = (Double)key;
        return this.containsKey(k) ? (V)this.remove(k) : null;
    }

    default public boolean containsKey(double key) {
        return true;
    }

    @Deprecated
    @Override
    default public boolean containsKey(Object key) {
        return key == null ? false : this.containsKey((Double)key);
    }

    default public void defaultReturnValue(V rv) {
        throw new UnsupportedOperationException();
    }

    default public V defaultReturnValue() {
        return null;
    }
}

