/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.shorts;

import it.unimi.dsi.fastutil.Function;
import it.unimi.dsi.fastutil.SafeMath;
import java.util.function.IntUnaryOperator;

@FunctionalInterface
public interface Short2ByteFunction
extends Function<Short, Byte>,
IntUnaryOperator {
    @Deprecated
    @Override
    default public int applyAsInt(int operand) {
        return this.get(SafeMath.safeIntToShort(operand));
    }

    @Override
    default public byte put(short key, byte value) {
        throw new UnsupportedOperationException();
    }

    public byte get(short var1);

    default public byte remove(short key) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    @Override
    default public Byte put(Short key, Byte value) {
        short k = key;
        boolean containsKey = this.containsKey(k);
        byte v = this.put(k, (byte)value);
        return containsKey ? Byte.valueOf(v) : null;
    }

    @Deprecated
    @Override
    default public Byte get(Object key) {
        if (key == null) {
            return null;
        }
        short k = (Short)key;
        byte v = this.get(k);
        return v != this.defaultReturnValue() || this.containsKey(k) ? Byte.valueOf(v) : null;
    }

    @Deprecated
    @Override
    default public Byte remove(Object key) {
        if (key == null) {
            return null;
        }
        short k = (Short)key;
        return this.containsKey(k) ? Byte.valueOf(this.remove(k)) : null;
    }

    default public boolean containsKey(short key) {
        return true;
    }

    @Deprecated
    @Override
    default public boolean containsKey(Object key) {
        return key == null ? false : this.containsKey((Short)key);
    }

    default public void defaultReturnValue(byte rv) {
        throw new UnsupportedOperationException();
    }

    default public byte defaultReturnValue() {
        return 0;
    }
}

