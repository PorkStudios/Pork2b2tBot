/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.doubles;

import it.unimi.dsi.fastutil.Function;
import java.util.function.DoublePredicate;

@FunctionalInterface
public interface Double2BooleanFunction
extends Function<Double, Boolean>,
DoublePredicate {
    @Override
    default public boolean test(double operand) {
        return this.get(operand);
    }

    @Override
    default public boolean put(double key, boolean value) {
        throw new UnsupportedOperationException();
    }

    public boolean get(double var1);

    default public boolean remove(double key) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    @Override
    default public Boolean put(Double key, Boolean value) {
        double k = key;
        boolean containsKey = this.containsKey(k);
        boolean v = this.put(k, (boolean)value);
        return containsKey ? Boolean.valueOf(v) : null;
    }

    @Deprecated
    @Override
    default public Boolean get(Object key) {
        if (key == null) {
            return null;
        }
        double k = (Double)key;
        boolean v = this.get(k);
        return v != this.defaultReturnValue() || this.containsKey(k) ? Boolean.valueOf(v) : null;
    }

    @Deprecated
    @Override
    default public Boolean remove(Object key) {
        if (key == null) {
            return null;
        }
        double k = (Double)key;
        return this.containsKey(k) ? Boolean.valueOf(this.remove(k)) : null;
    }

    default public boolean containsKey(double key) {
        return true;
    }

    @Deprecated
    @Override
    default public boolean containsKey(Object key) {
        return key == null ? false : this.containsKey((Double)key);
    }

    default public void defaultReturnValue(boolean rv) {
        throw new UnsupportedOperationException();
    }

    default public boolean defaultReturnValue() {
        return false;
    }
}

