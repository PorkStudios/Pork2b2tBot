/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.Function;
import it.unimi.dsi.fastutil.SafeMath;
import java.util.function.IntUnaryOperator;

@FunctionalInterface
public interface Char2IntFunction
extends Function<Character, Integer>,
IntUnaryOperator {
    @Deprecated
    @Override
    default public int applyAsInt(int operand) {
        return this.get(SafeMath.safeIntToChar(operand));
    }

    @Override
    default public int put(char key, int value) {
        throw new UnsupportedOperationException();
    }

    public int get(char var1);

    default public int remove(char key) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    @Override
    default public Integer put(Character key, Integer value) {
        char k = key.charValue();
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
        char k = ((Character)key).charValue();
        int v = this.get(k);
        return v != this.defaultReturnValue() || this.containsKey(k) ? Integer.valueOf(v) : null;
    }

    @Deprecated
    @Override
    default public Integer remove(Object key) {
        if (key == null) {
            return null;
        }
        char k = ((Character)key).charValue();
        return this.containsKey(k) ? Integer.valueOf(this.remove(k)) : null;
    }

    default public boolean containsKey(char key) {
        return true;
    }

    @Deprecated
    @Override
    default public boolean containsKey(Object key) {
        return key == null ? false : this.containsKey(((Character)key).charValue());
    }

    default public void defaultReturnValue(int rv) {
        throw new UnsupportedOperationException();
    }

    default public int defaultReturnValue() {
        return 0;
    }
}

