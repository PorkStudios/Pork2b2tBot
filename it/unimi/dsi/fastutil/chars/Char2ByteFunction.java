/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.Function;
import it.unimi.dsi.fastutil.SafeMath;
import java.util.function.IntUnaryOperator;

@FunctionalInterface
public interface Char2ByteFunction
extends Function<Character, Byte>,
IntUnaryOperator {
    @Deprecated
    @Override
    default public int applyAsInt(int operand) {
        return this.get(SafeMath.safeIntToChar(operand));
    }

    @Override
    default public byte put(char key, byte value) {
        throw new UnsupportedOperationException();
    }

    public byte get(char var1);

    default public byte remove(char key) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    @Override
    default public Byte put(Character key, Byte value) {
        char k = key.charValue();
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
        char k = ((Character)key).charValue();
        byte v = this.get(k);
        return v != this.defaultReturnValue() || this.containsKey(k) ? Byte.valueOf(v) : null;
    }

    @Deprecated
    @Override
    default public Byte remove(Object key) {
        if (key == null) {
            return null;
        }
        char k = ((Character)key).charValue();
        return this.containsKey(k) ? Byte.valueOf(this.remove(k)) : null;
    }

    default public boolean containsKey(char key) {
        return true;
    }

    @Deprecated
    @Override
    default public boolean containsKey(Object key) {
        return key == null ? false : this.containsKey(((Character)key).charValue());
    }

    default public void defaultReturnValue(byte rv) {
        throw new UnsupportedOperationException();
    }

    default public byte defaultReturnValue() {
        return 0;
    }
}

