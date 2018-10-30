/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.Function;
import it.unimi.dsi.fastutil.SafeMath;
import java.util.function.DoubleToIntFunction;

@FunctionalInterface
public interface Float2IntFunction
extends Function<Float, Integer>,
DoubleToIntFunction {
    @Deprecated
    @Override
    default public int applyAsInt(double operand) {
        return this.get(SafeMath.safeDoubleToFloat(operand));
    }

    @Override
    default public int put(float key, int value) {
        throw new UnsupportedOperationException();
    }

    public int get(float var1);

    default public int remove(float key) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    @Override
    default public Integer put(Float key, Integer value) {
        float k = key.floatValue();
        boolean containsKey = this.containsKey(k);
        int v = this.put(k, (int)value);
        return containsKey ? Integer.valueOf(v) : null;
    }

    @Deprecated
    @Override
    default public Integer get(Object key) {
        if (key == null) {
            return null;
        }
        float k = ((Float)key).floatValue();
        int v = this.get(k);
        return v != this.defaultReturnValue() || this.containsKey(k) ? Integer.valueOf(v) : null;
    }

    @Deprecated
    @Override
    default public Integer remove(Object key) {
        if (key == null) {
            return null;
        }
        float k = ((Float)key).floatValue();
        return this.containsKey(k) ? Integer.valueOf(this.remove(k)) : null;
    }

    default public boolean containsKey(float key) {
        return true;
    }

    @Deprecated
    @Override
    default public boolean containsKey(Object key) {
        return key == null ? false : this.containsKey(((Float)key).floatValue());
    }

    default public void defaultReturnValue(int rv) {
        throw new UnsupportedOperationException();
    }

    default public int defaultReturnValue() {
        return 0;
    }
}

