/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.doubles;

import it.unimi.dsi.fastutil.Function;
import java.util.function.DoubleUnaryOperator;

@FunctionalInterface
public interface Double2DoubleFunction
extends Function<Double, Double>,
DoubleUnaryOperator {
    @Override
    default public double applyAsDouble(double operand) {
        return this.get(operand);
    }

    @Override
    default public double put(double key, double value) {
        throw new UnsupportedOperationException();
    }

    public double get(double var1);

    default public double remove(double key) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    @Override
    default public Double put(Double key, Double value) {
        double k = key;
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
        double k = (Double)key;
        double v = this.get(k);
        return v != this.defaultReturnValue() || this.containsKey(k) ? Double.valueOf(v) : null;
    }

    @Deprecated
    @Override
    default public Double remove(Object key) {
        if (key == null) {
            return null;
        }
        double k = (Double)key;
        return this.containsKey(k) ? Double.valueOf(this.remove(k)) : null;
    }

    default public boolean containsKey(double key) {
        return true;
    }

    @Deprecated
    @Override
    default public boolean containsKey(Object key) {
        return key == null ? false : this.containsKey((Double)key);
    }

    default public void defaultReturnValue(double rv) {
        throw new UnsupportedOperationException();
    }

    default public double defaultReturnValue() {
        return 0.0;
    }
}

