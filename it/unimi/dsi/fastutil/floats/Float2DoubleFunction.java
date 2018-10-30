/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.Function;
import it.unimi.dsi.fastutil.SafeMath;
import java.util.function.DoubleUnaryOperator;

@FunctionalInterface
public interface Float2DoubleFunction
extends Function<Float, Double>,
DoubleUnaryOperator {
    @Deprecated
    @Override
    default public double applyAsDouble(double operand) {
        return this.get(SafeMath.safeDoubleToFloat(operand));
    }

    @Override
    default public double put(float key, double value) {
        throw new UnsupportedOperationException();
    }

    public double get(float var1);

    default public double remove(float key) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    @Override
    default public Double put(Float key, Double value) {
        float k = key.floatValue();
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
        float k = ((Float)key).floatValue();
        double v = this.get(k);
        return v != this.defaultReturnValue() || this.containsKey(k) ? Double.valueOf(v) : null;
    }

    @Deprecated
    @Override
    default public Double remove(Object key) {
        if (key == null) {
            return null;
        }
        float k = ((Float)key).floatValue();
        return this.containsKey(k) ? Double.valueOf(this.remove(k)) : null;
    }

    default public boolean containsKey(float key) {
        return true;
    }

    @Deprecated
    @Override
    default public boolean containsKey(Object key) {
        return key == null ? false : this.containsKey(((Float)key).floatValue());
    }

    default public void defaultReturnValue(double rv) {
        throw new UnsupportedOperationException();
    }

    default public double defaultReturnValue() {
        return 0.0;
    }
}

