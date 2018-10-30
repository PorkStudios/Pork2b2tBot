/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.ints;

import it.unimi.dsi.fastutil.Function;
import java.util.function.IntUnaryOperator;

@FunctionalInterface
public interface Int2ShortFunction
extends Function<Integer, Short>,
IntUnaryOperator {
    @Override
    default public int applyAsInt(int operand) {
        return this.get(operand);
    }

    @Override
    default public short put(int key, short value) {
        throw new UnsupportedOperationException();
    }

    public short get(int var1);

    default public short remove(int key) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    @Override
    default public Short put(Integer key, Short value) {
        int k = key;
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
        int k = (Integer)key;
        short v = this.get(k);
        return v != this.defaultReturnValue() || this.containsKey(k) ? Short.valueOf(v) : null;
    }

    @Deprecated
    @Override
    default public Short remove(Object key) {
        if (key == null) {
            return null;
        }
        int k = (Integer)key;
        return this.containsKey(k) ? Short.valueOf(this.remove(k)) : null;
    }

    default public boolean containsKey(int key) {
        return true;
    }

    @Deprecated
    @Override
    default public boolean containsKey(Object key) {
        return key == null ? false : this.containsKey((Integer)key);
    }

    default public void defaultReturnValue(short rv) {
        throw new UnsupportedOperationException();
    }

    default public short defaultReturnValue() {
        return 0;
    }
}

