/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.Function;
import it.unimi.dsi.fastutil.SafeMath;
import java.util.function.DoubleUnaryOperator;

@FunctionalInterface
public interface Float2FloatFunction
extends Function<Float, Float>,
DoubleUnaryOperator {
    @Deprecated
    @Override
    default public double applyAsDouble(double operand) {
        return this.get(SafeMath.safeDoubleToFloat(operand));
    }

    @Override
    default public float put(float key, float value) {
        throw new UnsupportedOperationException();
    }

    public float get(float var1);

    default public float remove(float key) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    @Override
    default public Float put(Float key, Float value) {
        float k = key.floatValue();
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
        float k = ((Float)key).floatValue();
        float v = this.get(k);
        return v != this.defaultReturnValue() || this.containsKey(k) ? Float.valueOf(v) : null;
    }

    @Deprecated
    @Override
    default public Float remove(Object key) {
        if (key == null) {
            return null;
        }
        float k = ((Float)key).floatValue();
        return this.containsKey(k) ? Float.valueOf(this.remove(k)) : null;
    }

    default public boolean containsKey(float key) {
        return true;
    }

    @Deprecated
    @Override
    default public boolean containsKey(Object key) {
        return key == null ? false : this.containsKey(((Float)key).floatValue());
    }

    default public void defaultReturnValue(float rv) {
        throw new UnsupportedOperationException();
    }

    default public float defaultReturnValue() {
        return 0.0f;
    }
}

