/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.doubles;

import it.unimi.dsi.fastutil.Function;
import java.util.function.DoubleUnaryOperator;

@FunctionalInterface
public interface Double2FloatFunction
extends Function<Double, Float>,
DoubleUnaryOperator {
    @Override
    default public double applyAsDouble(double operand) {
        return this.get(operand);
    }

    @Override
    default public float put(double key, float value) {
        throw new UnsupportedOperationException();
    }

    public float get(double var1);

    default public float remove(double key) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    @Override
    default public Float put(Double key, Float value) {
        double k = key;
        boolean containsKey = this.containsKey(k);
        float v = this.put(k, value.floatValue());
        return containsKey ? Float.valueOf(v) : null;
    }

    @Deprecated
    @Override
    default public Float get(Object key) {
        if (key == null) {
            return null;
        }
        double k = (Double)key;
        float v = this.get(k);
        return v != this.defaultReturnValue() || this.containsKey(k) ? Float.valueOf(v) : null;
    }

    @Deprecated
    @Override
    default public Float remove(Object key) {
        if (key == null) {
            return null;
        }
        double k = (Double)key;
        return this.containsKey(k) ? Float.valueOf(this.remove(k)) : null;
    }

    default public boolean containsKey(double key) {
        return true;
    }

    @Deprecated
    @Override
    default public boolean containsKey(Object key) {
        return key == null ? false : this.containsKey((Double)key);
    }

    default public void defaultReturnValue(float rv) {
        throw new UnsupportedOperationException();
    }

    default public float defaultReturnValue() {
        return 0.0f;
    }
}

