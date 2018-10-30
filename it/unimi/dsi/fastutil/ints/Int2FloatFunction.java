/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.ints;

import it.unimi.dsi.fastutil.Function;
import java.util.function.IntToDoubleFunction;

@FunctionalInterface
public interface Int2FloatFunction
extends Function<Integer, Float>,
IntToDoubleFunction {
    @Override
    default public double applyAsDouble(int operand) {
        return this.get(operand);
    }

    @Override
    default public float put(int key, float value) {
        throw new UnsupportedOperationException();
    }

    public float get(int var1);

    default public float remove(int key) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    @Override
    default public Float put(Integer key, Float value) {
        int k = key;
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
        int k = (Integer)key;
        float v = this.get(k);
        return v != this.defaultReturnValue() || this.containsKey(k) ? Float.valueOf(v) : null;
    }

    @Deprecated
    @Override
    default public Float remove(Object key) {
        if (key == null) {
            return null;
        }
        int k = (Integer)key;
        return this.containsKey(k) ? Float.valueOf(this.remove(k)) : null;
    }

    default public boolean containsKey(int key) {
        return true;
    }

    @Deprecated
    @Override
    default public boolean containsKey(Object key) {
        return key == null ? false : this.containsKey((Integer)key);
    }

    default public void defaultReturnValue(float rv) {
        throw new UnsupportedOperationException();
    }

    default public float defaultReturnValue() {
        return 0.0f;
    }
}

