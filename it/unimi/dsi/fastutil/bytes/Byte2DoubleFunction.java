/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.Function;
import it.unimi.dsi.fastutil.SafeMath;
import java.util.function.IntToDoubleFunction;

@FunctionalInterface
public interface Byte2DoubleFunction
extends Function<Byte, Double>,
IntToDoubleFunction {
    @Deprecated
    @Override
    default public double applyAsDouble(int operand) {
        return this.get(SafeMath.safeIntToByte(operand));
    }

    @Override
    default public double put(byte key, double value) {
        throw new UnsupportedOperationException();
    }

    public double get(byte var1);

    default public double remove(byte key) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    @Override
    default public Double put(Byte key, Double value) {
        byte k = key;
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
        byte k = (Byte)key;
        double v = this.get(k);
        return v != this.defaultReturnValue() || this.containsKey(k) ? Double.valueOf(v) : null;
    }

    @Deprecated
    @Override
    default public Double remove(Object key) {
        if (key == null) {
            return null;
        }
        byte k = (Byte)key;
        return this.containsKey(k) ? Double.valueOf(this.remove(k)) : null;
    }

    default public boolean containsKey(byte key) {
        return true;
    }

    @Deprecated
    @Override
    default public boolean containsKey(Object key) {
        return key == null ? false : this.containsKey((Byte)key);
    }

    default public void defaultReturnValue(double rv) {
        throw new UnsupportedOperationException();
    }

    default public double defaultReturnValue() {
        return 0.0;
    }
}

