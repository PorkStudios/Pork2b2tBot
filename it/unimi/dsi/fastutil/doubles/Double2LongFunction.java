/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.doubles;

import it.unimi.dsi.fastutil.Function;
import java.util.function.DoubleToLongFunction;

@FunctionalInterface
public interface Double2LongFunction
extends Function<Double, Long>,
DoubleToLongFunction {
    @Override
    default public long applyAsLong(double operand) {
        return this.get(operand);
    }

    @Override
    default public long put(double key, long value) {
        throw new UnsupportedOperationException();
    }

    public long get(double var1);

    default public long remove(double key) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    @Override
    default public Long put(Double key, Long value) {
        double k = key;
        boolean containsKey = this.containsKey(k);
        long v = this.put(k, (long)value);
        return containsKey ? Long.valueOf(v) : null;
    }

    @Deprecated
    @Override
    default public Long get(Object key) {
        if (key == null) {
            return null;
        }
        double k = (Double)key;
        long v = this.get(k);
        return v != this.defaultReturnValue() || this.containsKey(k) ? Long.valueOf(v) : null;
    }

    @Deprecated
    @Override
    default public Long remove(Object key) {
        if (key == null) {
            return null;
        }
        double k = (Double)key;
        return this.containsKey(k) ? Long.valueOf(this.remove(k)) : null;
    }

    default public boolean containsKey(double key) {
        return true;
    }

    @Deprecated
    @Override
    default public boolean containsKey(Object key) {
        return key == null ? false : this.containsKey((Double)key);
    }

    default public void defaultReturnValue(long rv) {
        throw new UnsupportedOperationException();
    }

    default public long defaultReturnValue() {
        return 0L;
    }
}

