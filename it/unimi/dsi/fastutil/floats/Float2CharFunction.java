/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.Function;
import it.unimi.dsi.fastutil.SafeMath;
import java.util.function.DoubleToIntFunction;

@FunctionalInterface
public interface Float2CharFunction
extends Function<Float, Character>,
DoubleToIntFunction {
    @Deprecated
    @Override
    default public int applyAsInt(double operand) {
        return this.get(SafeMath.safeDoubleToFloat(operand));
    }

    @Override
    default public char put(float key, char value) {
        throw new UnsupportedOperationException();
    }

    public char get(float var1);

    default public char remove(float key) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    @Override
    default public Character put(Float key, Character value) {
        float k = key.floatValue();
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
        float k = ((Float)key).floatValue();
        char v = this.get(k);
        return v != this.defaultReturnValue() || this.containsKey(k) ? Character.valueOf(v) : null;
    }

    @Deprecated
    @Override
    default public Character remove(Object key) {
        if (key == null) {
            return null;
        }
        float k = ((Float)key).floatValue();
        return this.containsKey(k) ? Character.valueOf(this.remove(k)) : null;
    }

    default public boolean containsKey(float key) {
        return true;
    }

    @Deprecated
    @Override
    default public boolean containsKey(Object key) {
        return key == null ? false : this.containsKey(((Float)key).floatValue());
    }

    default public void defaultReturnValue(char rv) {
        throw new UnsupportedOperationException();
    }

    default public char defaultReturnValue() {
        return '\u0000';
    }
}

