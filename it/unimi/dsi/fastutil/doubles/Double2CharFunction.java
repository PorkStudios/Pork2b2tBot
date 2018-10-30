/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.doubles;

import it.unimi.dsi.fastutil.Function;
import java.util.function.DoubleToIntFunction;

@FunctionalInterface
public interface Double2CharFunction
extends Function<Double, Character>,
DoubleToIntFunction {
    @Override
    default public int applyAsInt(double operand) {
        return this.get(operand);
    }

    @Override
    default public char put(double key, char value) {
        throw new UnsupportedOperationException();
    }

    public char get(double var1);

    default public char remove(double key) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    @Override
    default public Character put(Double key, Character value) {
        double k = key;
        boolean containsKey = this.containsKey(k);
        char v = this.put(k, value.charValue());
        return containsKey ? Character.valueOf(v) : null;
    }

    @Deprecated
    @Override
    default public Character get(Object key) {
        if (key == null) {
            return null;
        }
        double k = (Double)key;
        char v = this.get(k);
        return v != this.defaultReturnValue() || this.containsKey(k) ? Character.valueOf(v) : null;
    }

    @Deprecated
    @Override
    default public Character remove(Object key) {
        if (key == null) {
            return null;
        }
        double k = (Double)key;
        return this.containsKey(k) ? Character.valueOf(this.remove(k)) : null;
    }

    default public boolean containsKey(double key) {
        return true;
    }

    @Deprecated
    @Override
    default public boolean containsKey(Object key) {
        return key == null ? false : this.containsKey((Double)key);
    }

    default public void defaultReturnValue(char rv) {
        throw new UnsupportedOperationException();
    }

    default public char defaultReturnValue() {
        return '\u0000';
    }
}

