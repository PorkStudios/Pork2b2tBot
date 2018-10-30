/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.Function;
import it.unimi.dsi.fastutil.SafeMath;
import java.util.function.IntToDoubleFunction;

@FunctionalInterface
public interface Char2DoubleFunction
extends Function<Character, Double>,
IntToDoubleFunction {
    @Deprecated
    @Override
    default public double applyAsDouble(int operand) {
        return this.get(SafeMath.safeIntToChar(operand));
    }

    @Override
    default public double put(char key, double value) {
        throw new UnsupportedOperationException();
    }

    public double get(char var1);

    default public double remove(char key) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    @Override
    default public Double put(Character key, Double value) {
        char k = key.charValue();
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
        char k = ((Character)key).charValue();
        double v = this.get(k);
        return v != this.defaultReturnValue() || this.containsKey(k) ? Double.valueOf(v) : null;
    }

    @Deprecated
    @Override
    default public Double remove(Object key) {
        if (key == null) {
            return null;
        }
        char k = ((Character)key).charValue();
        return this.containsKey(k) ? Double.valueOf(this.remove(k)) : null;
    }

    default public boolean containsKey(char key) {
        return true;
    }

    @Deprecated
    @Override
    default public boolean containsKey(Object key) {
        return key == null ? false : this.containsKey(((Character)key).charValue());
    }

    default public void defaultReturnValue(double rv) {
        throw new UnsupportedOperationException();
    }

    default public double defaultReturnValue() {
        return 0.0;
    }
}

