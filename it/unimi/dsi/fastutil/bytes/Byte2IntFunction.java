/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.Function;
import it.unimi.dsi.fastutil.SafeMath;
import java.util.function.IntUnaryOperator;

@FunctionalInterface
public interface Byte2IntFunction
extends Function<Byte, Integer>,
IntUnaryOperator {
    @Deprecated
    @Override
    default public int applyAsInt(int operand) {
        return this.get(SafeMath.safeIntToByte(operand));
    }

    @Override
    default public int put(byte key, int value) {
        throw new UnsupportedOperationException();
    }

    public int get(byte var1);

    default public int remove(byte key) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    @Override
    default public Integer put(Byte key, Integer value) {
        byte k = key;
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
        byte k = (Byte)key;
        int v = this.get(k);
        return v != this.defaultReturnValue() || this.containsKey(k) ? Integer.valueOf(v) : null;
    }

    @Deprecated
    @Override
    default public Integer remove(Object key) {
        if (key == null) {
            return null;
        }
        byte k = (Byte)key;
        return this.containsKey(k) ? Integer.valueOf(this.remove(k)) : null;
    }

    default public boolean containsKey(byte key) {
        return true;
    }

    @Deprecated
    @Override
    default public boolean containsKey(Object key) {
        return key == null ? false : this.containsKey((Byte)key);
    }

    default public void defaultReturnValue(int rv) {
        throw new UnsupportedOperationException();
    }

    default public int defaultReturnValue() {
        return 0;
    }
}

