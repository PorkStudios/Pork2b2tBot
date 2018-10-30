/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.shorts;

import it.unimi.dsi.fastutil.Function;
import it.unimi.dsi.fastutil.SafeMath;
import java.util.function.IntToDoubleFunction;

@FunctionalInterface
public interface Short2FloatFunction
extends Function<Short, Float>,
IntToDoubleFunction {
    @Deprecated
    @Override
    default public double applyAsDouble(int operand) {
        return this.get(SafeMath.safeIntToShort(operand));
    }

    @Override
    default public float put(short key, float value) {
        throw new UnsupportedOperationException();
    }

    public float get(short var1);

    default public float remove(short key) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    @Override
    default public Float put(Short key, Float value) {
        short k = key;
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
        short k = (Short)key;
        float v = this.get(k);
        return v != this.defaultReturnValue() || this.containsKey(k) ? Float.valueOf(v) : null;
    }

    @Deprecated
    @Override
    default public Float remove(Object key) {
        if (key == null) {
            return null;
        }
        short k = (Short)key;
        return this.containsKey(k) ? Float.valueOf(this.remove(k)) : null;
    }

    default public boolean containsKey(short key) {
        return true;
    }

    @Deprecated
    @Override
    default public boolean containsKey(Object key) {
        return key == null ? false : this.containsKey((Short)key);
    }

    default public void defaultReturnValue(float rv) {
        throw new UnsupportedOperationException();
    }

    default public float defaultReturnValue() {
        return 0.0f;
    }
}

