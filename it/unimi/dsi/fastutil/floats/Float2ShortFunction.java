/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.Function;
import it.unimi.dsi.fastutil.SafeMath;
import java.util.function.DoubleToIntFunction;

@FunctionalInterface
public interface Float2ShortFunction
extends Function<Float, Short>,
DoubleToIntFunction {
    @Deprecated
    @Override
    default public int applyAsInt(double operand) {
        return this.get(SafeMath.safeDoubleToFloat(operand));
    }

    @Override
    default public short put(float key, short value) {
        throw new UnsupportedOperationException();
    }

    public short get(float var1);

    default public short remove(float key) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    @Override
    default public Short put(Float key, Short value) {
        float k = key.floatValue();
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
        float k = ((Float)key).floatValue();
        short v = this.get(k);
        return v != this.defaultReturnValue() || this.containsKey(k) ? Short.valueOf(v) : null;
    }

    @Deprecated
    @Override
    default public Short remove(Object key) {
        if (key == null) {
            return null;
        }
        float k = ((Float)key).floatValue();
        return this.containsKey(k) ? Short.valueOf(this.remove(k)) : null;
    }

    default public boolean containsKey(float key) {
        return true;
    }

    @Deprecated
    @Override
    default public boolean containsKey(Object key) {
        return key == null ? false : this.containsKey(((Float)key).floatValue());
    }

    default public void defaultReturnValue(short rv) {
        throw new UnsupportedOperationException();
    }

    default public short defaultReturnValue() {
        return 0;
    }
}

