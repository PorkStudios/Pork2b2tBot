/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.shorts;

import it.unimi.dsi.fastutil.Function;
import it.unimi.dsi.fastutil.SafeMath;
import java.util.function.IntUnaryOperator;

@FunctionalInterface
public interface Short2IntFunction
extends Function<Short, Integer>,
IntUnaryOperator {
    @Deprecated
    @Override
    default public int applyAsInt(int operand) {
        return this.get(SafeMath.safeIntToShort(operand));
    }

    @Override
    default public int put(short key, int value) {
        throw new UnsupportedOperationException();
    }

    public int get(short var1);

    default public int remove(short key) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    @Override
    default public Integer put(Short key, Integer value) {
        short k = key;
        boolean containsKey = this.containsKey(k);
        int v = this.put(k, (int)value);
        return containsKey ? Integer.valueOf(v) : null;
    }

    @Deprecated
    @Override
    default public Integer get(Object key) {
        if (key == null) {
            return null;
        }
        short k = (Short)key;
        int v = this.get(k);
        return v != this.defaultReturnValue() || this.containsKey(k) ? Integer.valueOf(v) : null;
    }

    @Deprecated
    @Override
    default public Integer remove(Object key) {
        if (key == null) {
            return null;
        }
        short k = (Short)key;
        return this.containsKey(k) ? Integer.valueOf(this.remove(k)) : null;
    }

    default public boolean containsKey(short key) {
        return true;
    }

    @Deprecated
    @Override
    default public boolean containsKey(Object key) {
        return key == null ? false : this.containsKey((Short)key);
    }

    default public void defaultReturnValue(int rv) {
        throw new UnsupportedOperationException();
    }

    default public int defaultReturnValue() {
        return 0;
    }
}

