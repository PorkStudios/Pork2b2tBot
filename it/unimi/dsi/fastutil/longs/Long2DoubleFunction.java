/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.Function;
import java.util.function.LongToDoubleFunction;

@FunctionalInterface
public interface Long2DoubleFunction
extends Function<Long, Double>,
LongToDoubleFunction {
    @Override
    default public double applyAsDouble(long operand) {
        return this.get(operand);
    }

    @Override
    default public double put(long key, double value) {
        throw new UnsupportedOperationException();
    }

    public double get(long var1);

    default public double remove(long key) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    @Override
    default public Double put(Long key, Double value) {
        long k = key;
        boolean containsKey = this.containsKey(k);
        double v = this.put(k, (double)value);
        return containsKey ? Double.valueOf(v) : null;
    }

    @Deprecated
    @Override
    default public Double get(Object key) {
        if (key == null) {
            return null;
        }
        long k = (Long)key;
        double v = this.get(k);
        return v != this.defaultReturnValue() || this.containsKey(k) ? Double.valueOf(v) : null;
    }

    @Deprecated
    @Override
    default public Double remove(Object key) {
        if (key == null) {
            return null;
        }
        long k = (Long)key;
        return this.containsKey(k) ? Double.valueOf(this.remove(k)) : null;
    }

    default public boolean containsKey(long key) {
        return true;
    }

    @Deprecated
    @Override
    default public boolean containsKey(Object key) {
        return key == null ? false : this.containsKey((Long)key);
    }

    default public void defaultReturnValue(double rv) {
        throw new UnsupportedOperationException();
    }

    default public double defaultReturnValue() {
        return 0.0;
    }
}

