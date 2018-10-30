/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.doubles;

import it.unimi.dsi.fastutil.Function;
import java.util.function.DoubleToIntFunction;

@FunctionalInterface
public interface Double2ShortFunction
extends Function<Double, Short>,
DoubleToIntFunction {
    @Override
    default public int applyAsInt(double operand) {
        return this.get(operand);
    }

    @Override
    default public short put(double key, short value) {
        throw new UnsupportedOperationException();
    }

    public short get(double var1);

    default public short remove(double key) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    @Override
    default public Short put(Double key, Short value) {
        double k = key;
        boolean containsKey = this.containsKey(k);
        short v = this.put(k, (short)value);
        return containsKey ? Short.valueOf(v) : null;
    }

    @Deprecated
    @Override
    default public Short get(Object key) {
        if (key == null) {
            return null;
        }
        double k = (Double)key;
        short v = this.get(k);
        return v != this.defaultReturnValue() || this.containsKey(k) ? Short.valueOf(v) : null;
    }

    @Deprecated
    @Override
    default public Short remove(Object key) {
        if (key == null) {
            return null;
        }
        double k = (Double)key;
        return this.containsKey(k) ? Short.valueOf(this.remove(k)) : null;
    }

    default public boolean containsKey(double key) {
        return true;
    }

    @Deprecated
    @Override
    default public boolean containsKey(Object key) {
        return key == null ? false : this.containsKey((Double)key);
    }

    default public void defaultReturnValue(short rv) {
        throw new UnsupportedOperationException();
    }

    default public short defaultReturnValue() {
        return 0;
    }
}

