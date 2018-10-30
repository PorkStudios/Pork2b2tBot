/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.Function;
import it.unimi.dsi.fastutil.SafeMath;
import java.util.function.IntUnaryOperator;

@FunctionalInterface
public interface Char2ShortFunction
extends Function<Character, Short>,
IntUnaryOperator {
    @Deprecated
    @Override
    default public int applyAsInt(int operand) {
        return this.get(SafeMath.safeIntToChar(operand));
    }

    @Override
    default public short put(char key, short value) {
        throw new UnsupportedOperationException();
    }

    public short get(char var1);

    default public short remove(char key) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    @Override
    default public Short put(Character key, Short value) {
        char k = key.charValue();
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
        char k = ((Character)key).charValue();
        short v = this.get(k);
        return v != this.defaultReturnValue() || this.containsKey(k) ? Short.valueOf(v) : null;
    }

    @Deprecated
    @Override
    default public Short remove(Object key) {
        if (key == null) {
            return null;
        }
        char k = ((Character)key).charValue();
        return this.containsKey(k) ? Short.valueOf(this.remove(k)) : null;
    }

    default public boolean containsKey(char key) {
        return true;
    }

    @Deprecated
    @Override
    default public boolean containsKey(Object key) {
        return key == null ? false : this.containsKey(((Character)key).charValue());
    }

    default public void defaultReturnValue(short rv) {
        throw new UnsupportedOperationException();
    }

    default public short defaultReturnValue() {
        return 0;
    }
}

