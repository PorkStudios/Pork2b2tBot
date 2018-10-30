/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.Function;
import java.util.function.LongToIntFunction;

@FunctionalInterface
public interface Long2CharFunction
extends Function<Long, Character>,
LongToIntFunction {
    @Override
    default public int applyAsInt(long operand) {
        return this.get(operand);
    }

    @Override
    default public char put(long key, char value) {
        throw new UnsupportedOperationException();
    }

    public char get(long var1);

    default public char remove(long key) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    @Override
    default public Character put(Long key, Character value) {
        long k = key;
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
        long k = (Long)key;
        char v = this.get(k);
        return v != this.defaultReturnValue() || this.containsKey(k) ? Character.valueOf(v) : null;
    }

    @Deprecated
    @Override
    default public Character remove(Object key) {
        if (key == null) {
            return null;
        }
        long k = (Long)key;
        return this.containsKey(k) ? Character.valueOf(this.remove(k)) : null;
    }

    default public boolean containsKey(long key) {
        return true;
    }

    @Deprecated
    @Override
    default public boolean containsKey(Object key) {
        return key == null ? false : this.containsKey((Long)key);
    }

    default public void defaultReturnValue(char rv) {
        throw new UnsupportedOperationException();
    }

    default public char defaultReturnValue() {
        return '\u0000';
    }
}

