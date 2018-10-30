/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.ints;

import it.unimi.dsi.fastutil.Function;
import java.util.function.IntUnaryOperator;

@FunctionalInterface
public interface Int2CharFunction
extends Function<Integer, Character>,
IntUnaryOperator {
    @Override
    default public int applyAsInt(int operand) {
        return this.get(operand);
    }

    @Override
    default public char put(int key, char value) {
        throw new UnsupportedOperationException();
    }

    public char get(int var1);

    default public char remove(int key) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    @Override
    default public Character put(Integer key, Character value) {
        int k = key;
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
        int k = (Integer)key;
        char v = this.get(k);
        return v != this.defaultReturnValue() || this.containsKey(k) ? Character.valueOf(v) : null;
    }

    @Deprecated
    @Override
    default public Character remove(Object key) {
        if (key == null) {
            return null;
        }
        int k = (Integer)key;
        return this.containsKey(k) ? Character.valueOf(this.remove(k)) : null;
    }

    default public boolean containsKey(int key) {
        return true;
    }

    @Deprecated
    @Override
    default public boolean containsKey(Object key) {
        return key == null ? false : this.containsKey((Integer)key);
    }

    default public void defaultReturnValue(char rv) {
        throw new UnsupportedOperationException();
    }

    default public char defaultReturnValue() {
        return '\u0000';
    }
}

