/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.Function;
import it.unimi.dsi.fastutil.SafeMath;
import java.util.function.IntUnaryOperator;

@FunctionalInterface
public interface Byte2ByteFunction
extends Function<Byte, Byte>,
IntUnaryOperator {
    @Deprecated
    @Override
    default public int applyAsInt(int operand) {
        return this.get(SafeMath.safeIntToByte(operand));
    }

    @Override
    default public byte put(byte key, byte value) {
        throw new UnsupportedOperationException();
    }

    public byte get(byte var1);

    default public byte remove(byte key) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    @Override
    default public Byte put(Byte key, Byte value) {
        byte k = key;
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
        byte k = (Byte)key;
        byte v = this.get(k);
        return v != this.defaultReturnValue() || this.containsKey(k) ? Byte.valueOf(v) : null;
    }

    @Deprecated
    @Override
    default public Byte remove(Object key) {
        if (key == null) {
            return null;
        }
        byte k = (Byte)key;
        return this.containsKey(k) ? Byte.valueOf(this.remove(k)) : null;
    }

    default public boolean containsKey(byte key) {
        return true;
    }

    @Deprecated
    @Override
    default public boolean containsKey(Object key) {
        return key == null ? false : this.containsKey((Byte)key);
    }

    default public void defaultReturnValue(byte rv) {
        throw new UnsupportedOperationException();
    }

    default public byte defaultReturnValue() {
        return 0;
    }
}

