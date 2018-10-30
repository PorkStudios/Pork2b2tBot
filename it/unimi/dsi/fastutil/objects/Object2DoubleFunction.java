/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.Function;
import java.util.function.ToDoubleFunction;

@FunctionalInterface
public interface Object2DoubleFunction<K>
extends Function<K, Double>,
ToDoubleFunction<K> {
    @Override
    default public double applyAsDouble(K operand) {
        return this.getDouble(operand);
    }

    @Override
    default public double put(K key, double value) {
        throw new UnsupportedOperationException();
    }

    public double getDouble(Object var1);

    default public double removeDouble(Object key) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    @Override
    default public Double put(K key, Double value) {
        K k = key;
        boolean containsKey = this.containsKey(k);
        double v = this.put(k, (double)value);
        return containsKey ? Double.valueOf(v) : null;
    }

    @Deprecated
    @Override
    default public Double get(Object key) {
        Object k = key;
        double v = this.getDouble(k);
        return v != this.defaultReturnValue() || this.containsKey(k) ? Double.valueOf(v) : null;
    }

    @Deprecated
    @Override
    default public Double remove(Object key) {
        Object k = key;
        return this.containsKey(k) ? Double.valueOf(this.removeDouble(k)) : null;
    }

    default public void defaultReturnValue(double rv) {
        throw new UnsupportedOperationException();
    }

    default public double defaultReturnValue() {
        return 0.0;
    }
}

