/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.Function;
import java.util.function.LongToDoubleFunction;

@FunctionalInterface
public interface Long2FloatFunction
extends Function<Long, Float>,
LongToDoubleFunction {
    @Override
    default public double applyAsDouble(long operand) {
        return this.get(operand);
    }

    @Override
    default public float put(long key, float value) {
        throw new UnsupportedOperationException();
    }

    public float get(long var1);

    default public float remove(long key) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    @Override
    default public Float put(Long key, Float value) {
        long k = key;
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
        long k = (Long)key;
        float v = this.get(k);
        return v != this.defaultReturnValue() || this.containsKey(k) ? Float.valueOf(v) : null;
    }

    @Deprecated
    @Override
    default public Float remove(Object key) {
        if (key == null) {
            return null;
        }
        long k = (Long)key;
        return this.containsKey(k) ? Float.valueOf(this.remove(k)) : null;
    }

    default public boolean containsKey(long key) {
        return true;
    }

    @Deprecated
    @Override
    default public boolean containsKey(Object key) {
        return key == null ? false : this.containsKey((Long)key);
    }

    default public void defaultReturnValue(float rv) {
        throw new UnsupportedOperationException();
    }

    default public float defaultReturnValue() {
        return 0.0f;
    }
}

