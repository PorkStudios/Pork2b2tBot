/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.Function;
import it.unimi.dsi.fastutil.SafeMath;
import java.util.function.IntUnaryOperator;

@FunctionalInterface
public interface Byte2CharFunction
extends Function<Byte, Character>,
IntUnaryOperator {
    @Deprecated
    @Override
    default public int applyAsInt(int operand) {
        return this.get(SafeMath.safeIntToByte(operand));
    }

    @Override
    default public char put(byte key, char value) {
        throw new UnsupportedOperationException();
    }

    public char get(byte var1);

    default public char remove(byte key) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    @Override
    default public Character put(Byte key, Character value) {
        byte k = key;
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
        byte k = (Byte)key;
        char v = this.get(k);
        return v != this.defaultReturnValue() || this.containsKey(k) ? Character.valueOf(v) : null;
    }

    @Deprecated
    @Override
    default public Character remove(Object key) {
        if (key == null) {
            return null;
        }
        byte k = (Byte)key;
        return this.containsKey(k) ? Character.valueOf(this.remove(k)) : null;
    }

    default public boolean containsKey(byte key) {
        return true;
    }

    @Deprecated
    @Override
    default public boolean containsKey(Object key) {
        return key == null ? false : this.containsKey((Byte)key);
    }

    default public void defaultReturnValue(char rv) {
        throw new UnsupportedOperationException();
    }

    default public char defaultReturnValue() {
        return '\u0000';
    }
}

